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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;
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
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.lable.oss.bitsandbytes.ByteMangler.flipTheFirstBit;
import static org.lable.rfc3881.auditlogger.adapter.hbase.HBaseAdapter.INCOMPLETE_MARKER;

/**
 * Retrieves {@link LogEntry} written to HBase by {@link HBaseAdapter}.
 */
public class HBaseReader implements AuditLogReader {
    private static final Logger logger = LoggerFactory.getLogger(HBaseReader.class);

    static ObjectMapper objectMapper;

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
     * {@inheritDoc}
     */
    @Override
    public List<LogEntry> read(
            AuditlogQuery query
    ) throws IOException {
        LogFilter filter = query.getFilter();

        if (filter == null) filter = LogFilter.empty();
        byte[] cf = columnFamilySetting.get().getBytes(StandardCharsets.UTF_8);

        Scan scan = new Scan();
        scan.addFamily(cf);

        FilterList filters = new FilterList();

        Referenceable eventId = filter.getEventId();
        if (eventId != null) {
            // Filter on event ID.
            CodeReference cr = eventId.toCodeReference();
            String codeSystem = Pattern.quote(cr.getCodeSystem());
            String code = Pattern.quote(cr.getCode());
            filters.addFilter(new RowFilter(
                    CompareOperator.EQUAL,
                    new RegexStringComparator(codeSystem + "\0" + code + "$")
            ));
        }

        String principal = filter.getPrincipal();
        if (principal != null) {
            System.out.println(principal);
            // Filter on principal involved.
            filters.addFilter(new FilterList(
                    FilterList.Operator.MUST_PASS_ONE,
                    mustInclude(cf, "requestor", principal),
                    mustInclude(cf, "delegator", principal),
                    mustInclude(cf, "principal", principal)
            ));
        }

        List<LogFilter.ObjectId> objectIds = filter.getParticipantObjectIds();
        if (!objectIds.isEmpty()) {
            FilterList objectFilterList = new FilterList(FilterList.Operator.MUST_PASS_ONE);

            for (LogFilter.ObjectId objectId : objectIds) {
                objectFilterList.addFilter(
                        mustIncludeParticipantObject(cf, "object", objectId.getTypeId(), objectId.getId())
                );
                objectFilterList.addFilter(
                        mustIncludeParticipantObject(cf, "X-object", objectId.getTypeId(), objectId.getId())
                );
            }

            filters.addFilter(objectFilterList);

        }

        Instant from = query.getFrom();
        Instant to = query.getTo();
        byte[] startRowId = query.getStartRowId();
        if (from != null) {
            if (to == null) {
                // Reverse scan.
                scan.setReversed(true);
                byte[] start = ByteMangler.flip(flipTheFirstBit(Bytes.toBytes(from.toEpochMilli() - 1)));
                if (startRowId != null) {
                    start = ByteMangler.add(start, startRowId);
                }
                scan.withStartRow(start);
            } else {
                byte[] stop = ByteMangler.plusOne(ByteMangler.flip(flipTheFirstBit(Bytes.toBytes(from.toEpochMilli()))));
                scan.withStopRow(stop);
                byte[] start = ByteMangler.flip(flipTheFirstBit(Bytes.toBytes(to.toEpochMilli())));
                if (startRowId != null) {
                    start = ByteMangler.add(start, startRowId);
                }
                scan.withStartRow(start);
            }
        } else if (to != null) {
            byte[] start = ByteMangler.flip(flipTheFirstBit(Bytes.toBytes(to.toEpochMilli())));
            if (startRowId != null) {
                start = ByteMangler.add(start, startRowId);
            }
            scan.withStartRow(start);
        }

        Long limit = query.getLimit();
        if (limit != null && limit > 0) {
            PageFilter pageFilter = new PageFilter(limit);
            filters.addFilter(pageFilter);
        }

        if (!filters.getFilters().isEmpty()) {
            scan.setFilter(filters);
        }

        if (objectMapper == null) objectMapper = ObjectMapperFactory.getObjectMapper();

        try (
                Table table = hbaseConnection.apply(tableNameSetting.get());
                ResultScanner scanner = table.getScanner(scan)
        ) {
            Stream<LogEntry> stream = StreamSupport.stream(scanner.spliterator(), false)
                    .map(result -> parseEntry(objectMapper, result, cf))
                    .filter(Optional::isPresent)
                    .map(Optional::get);

            if (limit != null && limit > 0) {
                // Always apply the limit on returned results too, because the PageFilter doesn't guarantee
                // that no more than `limit` results will be returned.
                stream = stream.limit(limit);
            }

            return stream.collect(Collectors.toList());
        }
    }

    public static Optional<LogEntry> parseEntry(ObjectMapper objectMapper, Result result, byte[] cf) {
        if (result == null || result.isEmpty()) return Optional.empty();

        byte[] row = result.getRow();
        NavigableMap<byte[], byte[]> familyValues = result.getFamilyMap(cf);

        Event event = readObjectFromResult(objectMapper, Event.class, familyValues, row, "event");
        if (event == null) return Optional.empty();

        byte[] timestampAndId = ByteMangler.shrink(16, row);
        byte[] id8 = ByteMangler.chomp(8, timestampAndId);
        event.setRaw8id(id8);

        Principal requestor = readObjectFromResult(objectMapper, Principal.class, familyValues, row, "requestor");
        Principal delegator = readObjectFromResult(objectMapper, Principal.class, familyValues, row, "delegator");
        NetworkAccessPoint accessPoint =
                readObjectFromResult(objectMapper, NetworkAccessPoint.class, familyValues, row, "access_point");

        List<Principal> principals = readObjectsFromResult(
                objectMapper, Principal.class, familyValues, row, "principal"
        );
        List<AuditSource> auditSources = readObjectsFromResult(
                objectMapper, AuditSource.class, familyValues, row, "source"
        );
        List<ParticipantObject> participantObjects = readObjectsFromResult(
                objectMapper, ParticipantObject.class, familyValues, row, "object"
        );
        CodeReference version = readObjectFromResult(objectMapper, CodeReference.class, familyValues, row, "version");

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

    static <T> List<T> readObjectsFromResult(ObjectMapper objectMapper,
                                             Class<T> objectType,
                                             NavigableMap<byte[], byte[]> columns,
                                             byte[] row,
                                             String columnPrefix) {
        List<T> list = new ArrayList<>();

        byte[] prefixBytes = ByteConversion.fromString(columnPrefix);
        byte[] incompletePrefixBytes = ByteMangler.add(INCOMPLETE_MARKER, prefixBytes);
        for (Map.Entry<byte[], byte[]> entry : columns.entrySet()) {
            byte[] key = entry.getKey();
            if (ByteComparison.startsWith(key, prefixBytes) || ByteComparison.startsWith(key, incompletePrefixBytes)) {
                byte[] value = entry.getValue();
                if (value == null) continue;

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

    static <T> T readObjectFromResult(ObjectMapper objectMapper,
                                      Class<T> objectType,
                                      NavigableMap<byte[], byte[]> columns,
                                      byte[] row,
                                      String columnPrefix) {

        byte[] prefixBytes = ByteConversion.fromString(columnPrefix);
        byte[] incompletePrefixBytes = ByteMangler.add(INCOMPLETE_MARKER, prefixBytes);
        for (Map.Entry<byte[], byte[]> entry : columns.entrySet()) {
            byte[] key = entry.getKey();
            if (ByteComparison.startsWith(key, prefixBytes) || ByteComparison.startsWith(key, incompletePrefixBytes)) {
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

    Filter mustInclude(byte[] cf, String prefix, String suffix) {
        byte[] cq = ByteMangler.add(
                Bytes.toBytes(prefix),
                new byte[]{0},
                Bytes.toBytes(suffix)
        );
        return mustInclude(cf, cq);
    }

    Filter mustIncludeParticipantObject(byte[] cf, String prefix, Referenceable typeId, String id) {
        CodeReference cr = typeId.toCodeReference();
        byte[] cq = ByteMangler.add(
                Bytes.toBytes(prefix),
                new byte[]{0},
                Bytes.toBytes(cr.getCodeSystem()),
                new byte[]{0},
                Bytes.toBytes(cr.getCode()),
                new byte[]{0},
                Bytes.toBytes(id)
        );
        return mustInclude(cf, cq);
    }

    Filter mustInclude(byte[] cf, byte[] cq) {
        SingleColumnValueFilter filter = new SingleColumnValueFilter(
                cf,
                cq,
                CompareOperator.NOT_EQUAL,
                new BinaryComparator(new byte[0])
        );
        filter.setFilterIfMissing(true);
        return filter;
    }
}
