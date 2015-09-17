package org.lable.rfc3881.auditlogger.adapter.hbase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Identifiable;
import org.lable.codesystem.codereference.Referenceable;
import org.lable.oss.bitsandbytes.ByteMangler;
import org.lable.rfc3881.auditlogger.api.AuditLogAdapter;
import org.lable.rfc3881.auditlogger.api.Event;
import org.lable.rfc3881.auditlogger.api.LogEntry;
import org.lable.rfc3881.auditlogger.serialization.ObjectMapperFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

import static org.apache.hadoop.hbase.util.Bytes.toBytes;
import static org.lable.oss.bitsandbytes.ByteMangler.flipTheFirstBit;

/**
 * Persist audit log messages in a HBase table.
 */
public class HBaseAdapter implements AuditLogAdapter {
    static final byte[] NULL_BYTE = new byte[]{0x00};
    static final ObjectMapper objectMapper = ObjectMapperFactory.getObjectMapper();

    final HConnection hConnection;
    final String table;
    final byte[] columnFamily;

    @Inject
    public HBaseAdapter(HConnection hConnection,
                        @Named("audit-table") String table,
                        @Named("audit-column-family") String columnFamily) {
        this.hConnection = hConnection;
        this.table = table;
        this.columnFamily = toBytes(columnFamily);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void record(LogEntry logEntry) throws IOException {
        if (logEntry == null) return;

        // Very simple and naive approach for this first iteration.
        try (HTableInterface auditTable = hConnection.getTable(table)) {
            Put put = new Put(rowKeyFor(logEntry));
            addIfNotNull(put, "event", logEntry.getEvent());
            addIfNotNull(put, "requestor", logEntry.getRequestor());
            addIfNotNull(put, "delegator", logEntry.getDelegator());
            addIfNotNull(put, "access_point", logEntry.getNetworkAccessPoint());
            addIfNotNull(put, "principal", logEntry.getParticipatingPrincipals());
            addIfNotNull(put, "source", logEntry.getAuditSources());
            addIfNotNull(put, "object", logEntry.getParticipantObjects());
            auditTable.put(put);
        }
    }

    void addIfNotNull(Put put, String qualifier, Object value) throws JsonProcessingException {
        addIfNotNull(put, toBytes(qualifier), value);
    }

    void addIfNotNull(Put put, String qualifier, Collection<? extends Identifiable> collection)
            throws JsonProcessingException {
        if (collection == null) return;
        for (Object value : collection) {
            addIfNotNull(put, toBytes(qualifier), value);
        }
    }

    void addIfNotNull(Put put, byte[] qualifier, Object value) throws JsonProcessingException {
        if (value == null) return;
        if (value instanceof Identifiable) {
            // Add the identifiers to the column qualifier.
            qualifier = Bytes.add(qualifier, NULL_BYTE, columnQualifierSuffixFor((Identifiable) value));
        }
        put.add(columnFamily, qualifier, objectMapper.writeValueAsBytes(value));
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

    static byte[] rowKeyFor(LogEntry logEntry) {
        Event event = logEntry.getEvent();
        return Bytes.add(
                // Flip the bytes in the data to order descending; latest event first.
                ByteMangler.flip(flipTheFirstBit(toBytes(event.getHappenedAt().getMillis()))),
                referenceableToBytes(event.getId())
        );
    }

    static byte[] referenceableToBytes(Referenceable referenceable) {
        CodeReference codeReference = referenceable.toCodeReference();
        return Bytes.add(toBytes(codeReference.getCodeSystem()), NULL_BYTE, toBytes(codeReference.getCode()));
    }
}
