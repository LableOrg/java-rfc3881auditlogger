package org.lable.rfc3881.auditlogger.api;

import java.io.IOException;

/**
 * Implementing classes handle audit events and persist them to database or file, or show them in logging output.
 */
public interface AuditLogAdapter {
    /**
     * Handle (e.g. persist or display) an audit event.
     *
     * @param logEntry Audit log entry to process.
     * @throws IOException Thrown when persisting the audit message failed.
     */
    void record(LogEntry logEntry) throws IOException;
}
