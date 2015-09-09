package org.lable.rfc3881.auditlogger.api;

/**
 * Implementing classes handle audit events and persist them to database or file, or show them in logging output.
 */
public interface AuditLogAdapter {
    /**
     * Handle (e.g. persist or display) an audit event.
     *
     * @param logEntry Audit log entry to process.
     */
    void record(LogEntry logEntry);
}
