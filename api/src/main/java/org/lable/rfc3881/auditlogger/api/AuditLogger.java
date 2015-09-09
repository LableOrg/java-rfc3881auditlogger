package org.lable.rfc3881.auditlogger.api;

import javax.inject.Inject;

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
     */
    public void log(LogEntry logEntry) {
        auditLogAdapter.record(logEntry);
    }
}
