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
    /**
     * Name of the marker passed to the logger. If the logging implementation chosen supports markers, the audit
     * messages can be filtered with it.
     */
    public static final String LOG_MARKER = "AUDIT";

    final Logger logger = LoggerFactory.getLogger(SLF4JAdapter.class);

    public SLF4JAdapter() {
        logger.info("SLF4JAdapter created. Audit events will be logged with marker '" + LOG_MARKER + "'.");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void record(LogEntry logEntry) {
        logger.info(MarkerFactory.getMarker(LOG_MARKER), logEntry.toString());
    }
}
