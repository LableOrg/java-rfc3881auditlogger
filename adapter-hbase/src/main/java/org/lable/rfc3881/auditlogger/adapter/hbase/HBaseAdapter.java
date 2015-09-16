package org.lable.rfc3881.auditlogger.adapter.hbase;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.util.Bytes;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;
import org.lable.rfc3881.auditlogger.api.*;
import org.lable.rfc3881.auditlogger.serialization.ObjectMapperFactory;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.util.Collection;

import static org.apache.hadoop.hbase.util.Bytes.toBytes;

/**
 * Persist audit log messages in a HBase table.
 */
public class HBaseAdapter implements AuditLogAdapter {
    static final byte[] NULL_BYTE = new byte[]{0x00};
    static final byte[] PRINCIPAL_PREFIX = toBytes("principal");
    static final byte[] AUDIT_SOURCE_PREFIX = toBytes("source");
    static final byte[] OBJECT_PREFIX = toBytes("object");

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

            // Java 8: clean up with anonymous methods.
            if (logEntry.getParticipatingPrincipals() != null) {
                for (Principal principal : logEntry.getParticipatingPrincipals()) {
                    byte[] qualifier = Bytes.add(PRINCIPAL_PREFIX, NULL_BYTE, toBytes(principal.getUserId()));
                    addIfNotNull(put, qualifier, principal);
                }
            }

            if (logEntry.getAuditSources() != null) {
                for (AuditSource source : logEntry.getAuditSources()) {
                    byte[] qualifier = Bytes.add(AUDIT_SOURCE_PREFIX, NULL_BYTE, columnQualifierSuffixFor(source));
                    addIfNotNull(put, qualifier, source);
                }
            }

            if (logEntry.getParticipantObjects() != null) {
                for (ParticipantObject object : logEntry.getParticipantObjects()) {
                    byte[] qualifier = Bytes.add(OBJECT_PREFIX, NULL_BYTE, columnQualifierSuffixFor(object));
                    addIfNotNull(put, qualifier, object);

                }
            }
            auditTable.put(put);
        }
    }

    void addIfNotNull(Put put, String qualifier, Object value) throws JsonProcessingException {
        addIfNotNull(put, toBytes(qualifier), value);
    }

    void addIfNotNull(Put put, byte[] qualifier, Object value) throws JsonProcessingException {
        if (value == null) return;
        put.add(columnFamily, qualifier, objectMapper.writeValueAsBytes(value));
    }

    static byte[] columnQualifierSuffixFor(ParticipantObject object) {
        return Bytes.add(referenceableToBytes(object.getIdType()), NULL_BYTE, toBytes(object.getId()));
    }

    static byte[] columnQualifierSuffixFor(AuditSource auditSource) {
        return auditSource.getEnterpriseSiteId() == null ?
                toBytes(auditSource.getId()) :
                Bytes.add(toBytes(auditSource.getEnterpriseSiteId()), NULL_BYTE, toBytes(auditSource.getId()));
    }

    static byte[] rowKeyFor(LogEntry logEntry) {
        Event event = logEntry.getEvent();
        return Bytes.add(
                toBytes(event.getHappenedAt().getMillis()),
                referenceableToBytes(event.getId())
        );
    }

    static byte[] referenceableToBytes(Referenceable referenceable) {
        CodeReference codeReference = referenceable.toCodeReference();
        return Bytes.add(toBytes(codeReference.getCodeSystem()), NULL_BYTE, toBytes(codeReference.getCode()));
    }
}
