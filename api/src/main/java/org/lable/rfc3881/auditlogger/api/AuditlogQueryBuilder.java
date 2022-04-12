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

    public static AuditlogQueryBuilder define(AuditLogReader reader) {
        return new AuditlogQueryBuilder(new AuditlogQuery(), reader);
    }

    public AuditlogQueryBuilder withFrom(Instant from) {
        query.setFrom(from);
        return this;
    }

    public AuditlogQueryBuilder withTo(Instant to) {
        query.setTo(to);
        return this;
    }

    public AuditlogQueryBuilder withLimit(Long limit) {
        query.setLimit(limit);
        return this;
    }

    public AuditlogQueryBuilder withFilter(LogFilter filter) {
        query.setFilter(filter);
        return this;
    }

    public AuditlogQueryBuilder withRowId(byte[] startRow8id) {
        query.setStartRowId(startRow8id);
        return this;
    }

    public AuditlogQuery getQuery() {
        return query;
    }

    public List<LogEntry> execute() throws IOException {
        return reader.read(query);
    }
}
