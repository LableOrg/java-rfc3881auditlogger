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

public class AuditlogQueryBuilder {
    private final AuditlogQuery query;
    private final AuditLogReader reader;

    private AuditlogQueryBuilder(AuditlogQuery query, AuditLogReader reader) {
        this.query = query;
        this.reader = reader;
    }

    /**
     * @param reader The {@link AuditLogReader}.
     * @return A builder for chaining.
     */
    public static AuditlogQueryBuilder define(AuditLogReader reader) {
        return new AuditlogQueryBuilder(new AuditlogQuery(), reader);
    }

    /**
     * Set {@link Instant} to start querying from (inclusive).
     */
    public AuditlogQueryBuilder withFrom(Instant from) {
        query.setFrom(from);
        return this;
    }

    /**
     * Set {@link Instant} to stop querying at (exclusive).
     */
    public AuditlogQueryBuilder withTo(Instant to) {
        query.setTo(to);
        return this;
    }

    /**
     * Set limit. Stop querying once we are at or past this limit.
     */
    public AuditlogQueryBuilder withLimit(Long limit) {
        query.setLimit(limit);
        return this;
    }

    /**
     * Set {@link LogFilter}.
     */
    public AuditlogQueryBuilder withFilter(LogFilter filter) {
        query.setFilter(filter);
        return this;
    }

    /**
     * Set unique eight byte id part of the start row.
     */
    public AuditlogQueryBuilder withStartRowId(byte[] startRawUid) {
        query.setStartRowId(startRawUid);
        return this;
    }

    /**
     * Get the {@link AuditlogQuery}.
     */
    public AuditlogQuery getQuery() {
        return query;
    }

    /**
     * Execute the query.
     */
    public List<LogEntry> execute() throws IOException {
        return reader.read(query);
    }
}
