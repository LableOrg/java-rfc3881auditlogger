/*
 * Copyright © 2015 Lable (info@lable.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lable.rfc3881.auditlogger.adapter.hbase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Identifiable;
import org.lable.codesystem.codereference.Referenceable;
import org.lable.oss.bitsandbytes.ByteConversion;
import org.lable.oss.bitsandbytes.ByteMangler;
import org.lable.rfc3881.auditlogger.api.*;
import org.lable.rfc3881.auditlogger.serialization.ObjectMapperFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.apache.hadoop.hbase.util.Bytes.toBytes;
import static org.lable.oss.bitsandbytes.ByteMangler.flipTheFirstBit;

/**
 * Persist audit log messages in a HBase table.
 */
public class HBaseAdapter implements AuditLogAdapter {
    static final byte[] INCOMPLETE_MARKER = "X-".getBytes();
    static final byte[] NULL_BYTE = new byte[]{0x00};

    static ObjectMapper objectMapper;

    private final BiConsumer<TableName, Put> putConsumer;
    private final Function<LogEntry, TableName> tableDecider;
    private final Supplier<String> columnFamilySetting;
    private final Supplier<Long> uniqueIDGenerator;

    /**
     * Create a new {@link HBaseAdapter}.
     *
     * @param putConsumer         A consumer that will handle persisting the generated {@link Put}.
     * @param tableDecider        Provides the {@link TableName} for any given {@link LogEntry}.
     * @param columnFamilySetting A supplier that returns the column family that should be used for the logs.
     * @param uniqueIDGenerator   A supplier that returns a unique identifier on each call.
     */
    @Inject
    public HBaseAdapter(@Named("hbase-put-consumer") BiConsumer<TableName, Put> putConsumer,
                        @Named("hbase-table-decider") Function<LogEntry, TableName> tableDecider,
                        @Named("audit-column-family") Supplier<String> columnFamilySetting,
                        @Named("uid-generator") Supplier<Long> uniqueIDGenerator) {
        this.putConsumer = putConsumer;
        this.tableDecider = tableDecider;
        this.columnFamilySetting = columnFamilySetting;
        this.uniqueIDGenerator = uniqueIDGenerator;
    }

    /**
     * Override the default {@link ObjectMapper}. This is only needed in rare cases where the default
     * {@link ObjectMapper} created by this library clashes with the data-bind library on the classpath, or if you want
     * some radically different serialization.
     *
     * @param objectMapper Custom object-mapper.
     */
    public static void setObjectMapper(ObjectMapper objectMapper) {
        HBaseAdapter.objectMapper = objectMapper;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void record(LogEntry logEntry) throws IOException {
        if (logEntry == null) return;
        Event event = logEntry.getEvent();
        UniqueEvent uEvent;
        if (event instanceof UniqueEvent) {
            uEvent = (UniqueEvent) event;
        } else {
            uEvent = UniqueEvent.fromEvent(logEntry.getEvent(), uniqueIDGenerator.get());
            logEntry.setEvent(uEvent);
        }


        Put put = new Put(rowKeyFor(uEvent.toId()));
        addIfNotNull(put, "event", uEvent);
        addIfNotNull(put, "requestor", logEntry.getRequestor());
        addIfNotNull(put, "delegator", logEntry.getDelegator());
        addIfNotNull(put, "access_point", logEntry.getNetworkAccessPoint());
        addIfNotNull(put, "principal", logEntry.getParticipatingPrincipals());
        addIfNotNull(put, "source", logEntry.getAuditSources());
        addIfNotNull(put, "object", logEntry.getParticipantObjects());
        addIfNotNull(put, "details", logEntry.getDetails());
        addIfNotNull(put, "version", logEntry.getVersion());

        TableName tableName = tableDecider.apply(logEntry);

        putConsumer.accept(tableName, put);
    }

    void addIfNotNull(Put put, String qualifier, Object value) throws JsonProcessingException {
        if (value instanceof EntryPart) {
            addIfNotNull(put, toBytes(qualifier), ((EntryPart) value).isComplete(), value);
        } else {
            addIfNotNull(put, toBytes(qualifier), true, value);
        }
    }

    void addIfNotNull(Put put, String qualifier, Collection<? extends EntryPart> collection)
            throws JsonProcessingException {
        if (collection == null) return;
        for (EntryPart value : collection) {
            addIfNotNull(put, toBytes(qualifier), value.isComplete(), value);
        }
    }

    void addIfNotNull(Put put, byte[] qualifier, boolean complete, Object value) throws JsonProcessingException {
        if (value == null) return;
        if (value instanceof Identifiable) {
            byte[] suffix = columnQualifierSuffixFor((Identifiable) value);

            // Add the identifiers to the column qualifier.
            qualifier = complete
                    ? ByteMangler.add(qualifier, NULL_BYTE, suffix)
                    : ByteMangler.add(INCOMPLETE_MARKER, qualifier, NULL_BYTE, suffix);
        }

        if (objectMapper == null) objectMapper = ObjectMapperFactory.getObjectMapper();
        put.addColumn(toBytes(columnFamilySetting.get()), qualifier, objectMapper.writeValueAsBytes(value));
    }

    static byte[] columnQualifierSuffixFor(Identifiable identifiable) {
        List<String> parts = identifiable.identifyingStack();
        List<byte[]> byteParts = new ArrayList<>();

        // Account for the separator bytes.
        int targetLength = parts.size() - 1;
        for (String part : parts) {
            if (part != null) {
                byte[] asBytes = toBytes(part);
                targetLength += asBytes.length;
                byteParts.add(asBytes);
            }
        }

        ByteBuffer buffer = ByteBuffer.allocate(targetLength);
        boolean first = true;

        for (byte[] part : byteParts) {
            if (!first) {
                buffer.put(NULL_BYTE);
            } else {
                first = false;
            }

            if (part != null) {
                buffer.put(part);
            }
        }

        return buffer.array();
    }

    static byte[] rowKeyFor(Event.EventId eventId) {
        return Bytes.add(
                // Flip the bytes in the data to order descending; latest event first.
                ByteMangler.flip(flipTheFirstBit(toBytes(eventId.getHappenedAt()))),
                ByteConversion.fromLong(eventId.getUid()),
                referenceableToBytes(eventId.getId())
        );
    }

    static byte[] referenceableToBytes(Referenceable referenceable) {
        CodeReference codeReference = referenceable.toCodeReference();
        return Bytes.add(toBytes(codeReference.getCodeSystem()), NULL_BYTE, toBytes(codeReference.getCode()));
    }
}
