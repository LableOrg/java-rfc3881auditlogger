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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.oss.bitsandbytes.ByteComparison;
import org.lable.oss.bitsandbytes.ByteConversion;
import org.lable.oss.bitsandbytes.ByteMangler;
import org.lable.oss.bitsandbytes.BytePrinter;
import org.lable.rfc3881.auditlogger.api.*;
import org.lable.rfc3881.auditlogger.serialization.ObjectMapperFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.lable.oss.bitsandbytes.ByteMangler.flipTheFirstBit;

/**
 * Retrieves {@link LogEntry} written to HBase by {@link HBaseAdapter}.
 */
public class HBaseReader implements AuditLogReader {
    private static final Logger logger = LoggerFactory.getLogger(HBaseReader.class);

    static final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    private final Function<TableName, Table> hbaseConnection;
    private final Supplier<TableName> tableNameSetting;
    private final Supplier<String> columnFamilySetting;

    /**
     * Create a new {@link HBaseReader}.
     *
     * @param hbaseConnection     A function that returns a HBase {@link Table}.
     * @param tableNameSetting    A supplier that returns the table logs should be read from.
     * @param columnFamilySetting A supplier that returns the column family logs are stored in.
     */
    @Inject
    public HBaseReader(@Named("hbase-connection") Function<TableName, Table> hbaseConnection,
                       @Named("audit-table") Supplier<TableName> tableNameSetting,
                       @Named("audit-column-family") Supplier<String> columnFamilySetting) {
        this.hbaseConnection = hbaseConnection;
        this.tableNameSetting = tableNameSetting;
        this.columnFamilySetting = columnFamilySetting;
    }

    /**
     * Read {@link LogEntry}'s from HBase.
     *
     * @param from Instant in time to start reading from.
     * @param to   Instant in time to stop reading after.
     * @return A list of {@link LogEntry}, in reverse chronological order (newest first).
     */
    @Override
    public List<LogEntry> read(Instant from, Instant to) throws IOException {
        if (from == null) throw new IllegalArgumentException("Parameter `from` is required.");
        if (to == null) throw new IllegalArgumentException("Parameter `to` is required.");

        byte[] cf = columnFamilySetting.get().getBytes(StandardCharsets.UTF_8);
        byte[] start = ByteMangler.flip(flipTheFirstBit(Bytes.toBytes(to.toEpochMilli())));
        byte[] stop = ByteMangler.flip(flipTheFirstBit(Bytes.toBytes(from.toEpochMilli())));

        Scan scan = new Scan(start, stop);
        scan.addFamily(cf);

        try (
                Table table = hbaseConnection.apply(tableNameSetting.get());
                ResultScanner scanner = table.getScanner(scan)
        ) {
            return StreamSupport.stream(scanner.spliterator(), false)
                    .map(result -> parseEntry(result, cf))
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .collect(Collectors.toList());
        }
    }

    Optional<LogEntry> parseEntry(Result result, byte[] cf) {
        if (result == null || result.isEmpty()) return Optional.empty();

        byte[] row = result.getRow();
        NavigableMap<byte[], byte[]> familyValues = result.getFamilyMap(cf);

        Event event = readObjectFromResult(Event.class, familyValues, row, "event");
        if (event == null) return Optional.empty();

        Principal requestor = readObjectFromResult(Principal.class, familyValues, row, "requestor");
        Principal delegator = readObjectFromResult(Principal.class, familyValues, row, "delegator");
        NetworkAccessPoint accessPoint =
                readObjectFromResult(NetworkAccessPoint.class, familyValues, row, "access_point");

        List<Principal> principals = readObjectsFromResult(Principal.class, familyValues, row, "principal");
        List<AuditSource> auditSources = readObjectsFromResult(AuditSource.class, familyValues, row, "source");
        List<ParticipantObject> participantObjects =
                readObjectsFromResult(ParticipantObject.class, familyValues, row, "object");
        CodeReference version = readObjectFromResult(CodeReference.class, familyValues, row, "version");

        return Optional.of(new LogEntry(
                event,
                requestor,
                delegator,
                principals,
                accessPoint,
                auditSources,
                participantObjects,
                version
        ));
    }

    <T> List<T> readObjectsFromResult(Class<T> objectType,
                                      NavigableMap<byte[], byte[]> columns,
                                      byte[] row,
                                      String columnPrefix) {
        List<T> list = new ArrayList<>();

        byte[] prefixBytes = ByteConversion.fromString(columnPrefix);
        for (Map.Entry<byte[], byte[]> entry : columns.entrySet()) {
            if (ByteComparison.startsWith(entry.getKey(), prefixBytes)) {
                byte[] value = entry.getValue();
                if (value == null) return null;

                try {
                    T v = objectMapper.readValue(value, objectType);
                    if (v != null) {
                        list.add(v);
                    }
                } catch (IOException e) {
                    logger.error(
                            "Failed to parse byte value found in column {} as {}. Row: {}.",
                            columnPrefix,
                            objectType.getName(),
                            BytePrinter.utf8Escaped(row)
                    );
                }
            }
        }

        return list;
    }

    <T> T readObjectFromResult(Class<T> objectType,
                               NavigableMap<byte[], byte[]> columns,
                               byte[] row,
                               String columnPrefix) {

        byte[] prefixBytes = ByteConversion.fromString(columnPrefix);
        for (Map.Entry<byte[], byte[]> entry : columns.entrySet()) {
            if (ByteComparison.startsWith(entry.getKey(), prefixBytes)) {
                byte[] value = entry.getValue();
                if (value == null) return null;

                try {
                    return objectMapper.readValue(value, objectType);
                } catch (IOException e) {
                    logger.error(
                            "Failed to parse byte value found in column {} as {}. Row: {}.",
                            columnPrefix,
                            objectType.getName(),
                            BytePrinter.utf8Escaped(row)
                    );
                    return null;
                }
            }
        }

        return null;
    }
}
