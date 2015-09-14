package org.lable.rfc3881.auditlogger.adapter.sl4fj;

import org.lable.rfc3881.auditlogger.api.AuditLogAdapter;
import org.lable.rfc3881.auditlogger.api.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MarkerFactory;

/**
 * Simply log the output of {@link LogEntry#toString()} to the logger. This class is meant for debugging and testing.
 */
public class SLF4JAdapter implements AuditLogAdapter {
    final Logger logger = LoggerFactory.getLogger(SLF4JAdapter.class);

    public SLF4JAdapter() {
        logger.info("SLF4JAdapter created.");
    }

    @Override
    public void record(LogEntry logEntry) {
        logger.info(MarkerFactory.getMarker("AUDIT"), logEntry.toString());
    }
}
