/*
 * Copyright (C) 2015 Lable (info@lable.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
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
import org.lable.oss.bitsandbytes.ByteMangler;
import org.lable.rfc3881.auditlogger.api.AuditLogAdapter;
import org.lable.rfc3881.auditlogger.api.EntryPart;
import org.lable.rfc3881.auditlogger.api.Event;
import org.lable.rfc3881.auditlogger.api.LogEntry;
import org.lable.rfc3881.auditlogger.serialization.ObjectMapperFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;
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

    private final Function<TableName, Table> hbaseConnection;
    private final Supplier<TableName> tableNameSetting;
    private final Supplier<String> columnFamilySetting;
    private final Supplier<byte[]> uniqueIDGenerator;

    /**
     * Create a new {@link HBaseAdapter}.
     *
     * @param hbaseConnection     A function that returns a HBase {@link Table}.
     * @param tableNameSetting    A supplier that returns the table logs should be persisted to.
     * @param columnFamilySetting A supplier that returns the column family that should be used for the logs.
     * @param uniqueIDGenerator   A supplier that returns a unique identifier on each call.
     */
    @Inject
    public HBaseAdapter(@Named("hbase-connection") Function<TableName, Table> hbaseConnection,
                        @Named("audit-table") Supplier<TableName> tableNameSetting,
                        @Named("audit-column-family") Supplier<String> columnFamilySetting,
                        @Named("uid-generator") Supplier<byte[]> uniqueIDGenerator) {
        this.hbaseConnection = hbaseConnection;
        this.tableNameSetting = tableNameSetting;
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

        try (Table auditTable = hbaseConnection.apply(tableNameSetting.get())) {
            Put put = new Put(rowKeyFor(logEntry, uniqueIDGenerator.get()));
            addIfNotNull(put, "event", logEntry.getEvent());
            addIfNotNull(put, "requestor", logEntry.getRequestor());
            addIfNotNull(put, "delegator", logEntry.getDelegator());
            addIfNotNull(put, "access_point", logEntry.getNetworkAccessPoint());
            addIfNotNull(put, "principal", logEntry.getParticipatingPrincipals());
            addIfNotNull(put, "source", logEntry.getAuditSources());
            addIfNotNull(put, "object", logEntry.getParticipantObjects());
            addIfNotNull(put, "version", logEntry.getVersion());
            auditTable.put(put);
        }
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
        // Account for the separator bytes.
        int targetLength = parts.size() - 1;
        for (String part : parts) {
            if (part != null) {
                targetLength += part.length();
            }
        }

        ByteBuffer buffer = ByteBuffer.allocate(targetLength);
        boolean first = true;
        for (String part : parts) {
            if (!first) {
                buffer.put(NULL_BYTE);
            } else {
                first = false;
            }

            if (part != null) {
                buffer.put(toBytes(part));
            }
        }

        return buffer.array();
    }

    static byte[] rowKeyFor(LogEntry logEntry, byte[] uid) {
        Event event = logEntry.getEvent();
        return Bytes.add(
                // Flip the bytes in the data to order descending; latest event first.
                ByteMangler.flip(flipTheFirstBit(toBytes(event.getHappenedAt()))),
                uid,
                referenceableToBytes(event.getId())
        );
    }

    static byte[] referenceableToBytes(Referenceable referenceable) {
        CodeReference codeReference = referenceable.toCodeReference();
        return Bytes.add(toBytes(codeReference.getCodeSystem()), NULL_BYTE, toBytes(codeReference.getCode()));
    }
}
