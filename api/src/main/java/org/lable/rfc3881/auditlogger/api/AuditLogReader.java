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
import java.time.Instant;
import java.util.List;

/**
 * Read log entries from the audit log.
 */
public interface AuditLogReader {
    /**
     * Read log entries from the audit log.
     *
     * @param from  Start date. May be {@code null} to stop at the first entry recorded, or if the limit (if set) is
     *              reached.
     * @param to    End date. May be {@code null} to return entries starting from the present.
     * @param limit Limit the number of entries returned. May be {@code null} to set no limit.
     * @return Log entries.
     */
    List<LogEntry> read(Instant from, Instant to, Long limit, LogFilter filter) throws IOException;

    /**
     * Read log entries from the audit log.
     *
     * @param from  Start date. May be {@code null} to stop at the first entry recorded, or if the limit (if set) is
     *              reached.
     * @param to    End date. May be {@code null} to return entries starting from the present.
     * @param limit Limit the number of entries returned. May be {@code null} to set no limit.
     * @return Log entries.
     */
    default List<LogEntry> read(Instant from, Instant to, Long limit) throws IOException {
        return read(from, to, limit, null);
    }

    /**
     * Read log entries from the audit log.
     *
     * @param from   Start date. May be {@code null} to start at the first entry recorded.
     * @param to     End date. May be {@code null} to return entries until the present.
     * @param filter Filter for log entries.
     * @return Log entries.
     */
    default List<LogEntry> read(Instant from, Instant to, LogFilter filter) throws IOException {
        return read(from, to, null, filter);
    }

    /**
     * Read log entries from the audit log.
     *
     * @param from Start date. May be {@code null} to start at the first entry recorded.
     * @param to   End date. May be {@code null} to return entries until the present.
     * @return Log entries.
     */
    default List<LogEntry> read(Instant from, Instant to) throws IOException {
        return read(from, to, null, null);
    }

    /**
     * Read log entries from the audit log from the present until a set date.
     *
     * @param from Start date.
     * @return Log entries.
     */
    default List<LogEntry> read(Instant from) throws IOException {
        return read(from, null, null, null);
    }

    /**
     * Read log entries from the audit log from the present until a set date.
     *
     * @param from   Start date.
     * @param filter Filter for log entries.
     * @return Log entries.
     */
    default List<LogEntry> read(Instant from, LogFilter filter) throws IOException {
        return read(from, null, null, filter);
    }

    /**
     * Read log entries from the audit log from the present until a set date.
     *
     * @param from  Start date.
     * @param limit Limit the number of entries returned. May be {@code null} to set no limit.
     * @return Log entries.
     */
    default List<LogEntry> read(Instant from, long limit) throws IOException {
        return read(from, null, limit, null);
    }

    /**
     * Read log entries from the audit log from the present until a set date.
     *
     * @param from   Start date.
     * @param limit  Limit the number of entries returned. May be {@code null} to set no limit.
     * @param filter Filter for log entries.
     * @return Log entries.
     */
    default List<LogEntry> read(Instant from, long limit, LogFilter filter) throws IOException {
        return read(from, null, limit, filter);
    }

    /**
     * Read log entries from the audit log from the present, stopping when the limit is reached.
     *
     * @param limit Limit the number of entries returned. May be {@code null} to set no limit.
     * @return Log entries.
     */
    default List<LogEntry> read(long limit) throws IOException {
        return read(null, null, limit, null);
    }


    /**
     * Read log entries from the audit log from the present, stopping when the limit is reached.
     *
     * @param limit  Limit the number of entries returned. May be {@code null} to set no limit.
     * @param filter Filter for log entries.
     * @return Log entries.
     */
    default List<LogEntry> read(long limit, LogFilter filter) throws IOException {
        return read(null, null, limit, filter);
    }
}
