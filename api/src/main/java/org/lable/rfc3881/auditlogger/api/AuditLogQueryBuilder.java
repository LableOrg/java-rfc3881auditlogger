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

import org.lable.rfc3881.auditlogger.api.AuditLogReader.QueryLogger;
import org.lable.rfc3881.auditlogger.api.Event.EventId;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

public class AuditLogQueryBuilder {
    private final AuditLogQuery query;
    private final AuditLogReader reader;
    private final QueryLogger queryLogger;

    private AuditLogQueryBuilder(AuditLogQuery query, AuditLogReader reader, QueryLogger queryLogger) {
        this.query = query;
        this.reader = reader;
        this.queryLogger = queryLogger;
    }

    /**
     * @param reader      The {@link AuditLogReader}.
     * @param queryLogger Target for a log line describing the query performed.
     * @return A builder for chaining.
     */
    public static AuditLogQueryBuilder define(AuditLogReader reader, QueryLogger queryLogger) {
        return new AuditLogQueryBuilder(new AuditLogQuery(), reader, queryLogger);
    }

    /**
     * Set {@link Instant} to start querying from (inclusive).
     *
     * @param from Instant to start from.
     */
    public AuditLogQueryBuilder withFrom(Instant from) {
        query.setFrom(from, true);
        return this;
    }

    /**
     * Set {@link Instant} to start querying from.
     *
     * @param from      Instant to start from.
     * @param inclusive Whether this limit is inclusive or exclusive.
     */
    public AuditLogQueryBuilder withFrom(Instant from, boolean inclusive) {
        query.setFrom(from, inclusive);
        return this;
    }

    /**
     * Set {@link Instant} to start querying from (inclusive).
     *
     * @param from Event to start from.
     */
    public AuditLogQueryBuilder withFrom(EventId from) {
        query.setFrom(from, true);
        return this;
    }

    /**
     * Set {@link Instant} to start querying from (inclusive).
     *
     * @param from      Event to start from.
     * @param inclusive Whether this limit is inclusive or exclusive.
     */
    public AuditLogQueryBuilder withFrom(EventId from, boolean inclusive) {
        query.setFrom(from, inclusive);
        return this;
    }

    /**
     * Set {@link EventId} to stop querying at (exclusive).
     *
     * @param to Instant to end at.
     */
    public AuditLogQueryBuilder withTo(Instant to) {
        query.setTo(to, false);
        return this;
    }

    /**
     * Set {@link EventId} to stop querying at.
     *
     * @param to        Instant to end at.
     * @param inclusive Whether this limit is inclusive or exclusive.
     */
    public AuditLogQueryBuilder withTo(Instant to, boolean inclusive) {
        query.setTo(to, inclusive);
        return this;
    }

    /**
     * Set {@link EventId} to stop querying at (exclusive).
     *
     * @param to Event to end at.
     */
    public AuditLogQueryBuilder withTo(EventId to) {
        query.setTo(to, false);
        return this;
    }

    /**
     * Set {@link EventId} to stop querying at.
     *
     * @param to        Event to end at.
     * @param inclusive Whether this limit is inclusive or exclusive.
     */
    public AuditLogQueryBuilder withTo(EventId to, boolean inclusive) {
        query.setTo(to, inclusive);
        return this;
    }

    /**
     * Set limit. Stop querying once we are at or past this limit.
     *
     * @param limit Limit.
     */
    public AuditLogQueryBuilder withLimit(Long limit) {
        query.setLimit(limit);
        return this;
    }

    /**
     * Set {@link LogFilter}.
     *
     * @param filter Filter.
     */
    public AuditLogQueryBuilder withFilter(LogFilter filter) {
        query.setFilter(filter);
        return this;
    }

    /**
     * Get the {@link AuditLogQuery}.
     */
    public AuditLogQuery getQuery() {
        return query;
    }

    /**
     * Execute the query.
     */
    public List<LogEntry> execute() throws IOException {
        return reader.read(query, queryLogger);
    }
}
