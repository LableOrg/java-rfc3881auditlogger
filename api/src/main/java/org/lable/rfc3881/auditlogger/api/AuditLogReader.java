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
package org.lable.rfc3881.auditlogger.api;

import java.io.IOException;
import java.util.List;

/**
 * Read log entries from the audit log.
 */
public interface AuditLogReader {

    /**
     * Read log entries from the audit log.
     *
     * @param query The {@link AuditLogQuery}.
     * @return Log entries.
     */
    default List<LogEntry> read(AuditLogQuery query) throws IOException {
        return read(query, null);
    }

    /**
     * Read log entries from the audit log.
     *
     * @param query The {@link AuditLogQuery}.
     * @param queryLogger Target for a log line describing the query performed.
     * @return Log entries.
     */
    List<LogEntry> read(AuditLogQuery query, QueryLogger queryLogger) throws IOException;

    /**
     * Start defining the query.
     */
    default AuditLogQueryBuilder defineQuery() {
        return AuditLogQueryBuilder.define(this, null);
    }

    /**
     * Start defining the query.
     */
    default AuditLogQueryBuilder defineQuery(QueryLogger queryLogger) {
        return AuditLogQueryBuilder.define(this, queryLogger);
    }

    @FunctionalInterface
    interface QueryLogger {
        void log(String line);
    }
}
