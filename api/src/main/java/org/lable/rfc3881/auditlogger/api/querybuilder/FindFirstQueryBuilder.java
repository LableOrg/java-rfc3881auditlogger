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
package org.lable.rfc3881.auditlogger.api.querybuilder;

import org.lable.rfc3881.auditlogger.api.AuditLogReader;
import org.lable.rfc3881.auditlogger.api.AuditLogReader.QueryLogger;
import org.lable.rfc3881.auditlogger.api.LogEntry;
import org.lable.rfc3881.auditlogger.api.LogFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.Optional;

public class FindFirstQueryBuilder {
    private final FindFirstQuery query;
    private final AuditLogReader reader;
    private final QueryLogger queryLogger;

    private FindFirstQueryBuilder(FindFirstQuery query, AuditLogReader reader, QueryLogger queryLogger) {
        this.query = query;
        this.reader = reader;
        this.queryLogger = queryLogger;
    }

    /**
     * @param reader      The {@link AuditLogReader}.
     * @param queryLogger Target for a log line describing the query performed.
     * @return A builder for chaining.
     */
    public static FindFirstQueryBuilder define(AuditLogReader reader, QueryLogger queryLogger) {
        return new FindFirstQueryBuilder(new FindFirstQuery(), reader, queryLogger);
    }

    /**
     * Set {@link Instant} to start querying from (inclusive).
     *
     * @param from Instant to start from.
     */
    public FindFirstQueryBuilder withFrom(Instant from) {
        query.setFrom(from);
        return this;
    }

    /**
     * Set {@link LogFilter}.
     *
     * @param filter Filter.
     */
    public FindFirstQueryBuilder withFilter(LogFilter filter) {
        query.setFilter(filter);
        return this;
    }

    /**
     * Get the {@link FindFirstQuery}.
     */
    public FindFirstQuery getQuery() {
        return query;
    }

    /**
     * Execute the query.
     */
    public Optional<LogEntry> execute() throws IOException {
        return reader.findFirst(query, queryLogger);
    }
}
