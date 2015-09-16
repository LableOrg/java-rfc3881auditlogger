package org.lable.rfc3881.auditlogger.api;

import javax.inject.Inject;
import java.io.IOException;

/**
 * Audit logger.
 */
public class AuditLogger {
    private final AuditLogAdapter auditLogAdapter;

    @Inject
    public AuditLogger(AuditLogAdapter auditLogAdapter) {
        this.auditLogAdapter = auditLogAdapter;
    }

    /**
     * Log an audit event.
     *
     * @param logEntry Audit log entry to log.
     * @throws IOException Thrown when persisting the audit message failed.
     */
    public void log(LogEntry logEntry) throws IOException {
        auditLogAdapter.record(logEntry);
    }
}
