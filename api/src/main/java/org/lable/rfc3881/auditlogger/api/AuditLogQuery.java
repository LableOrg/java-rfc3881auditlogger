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

import org.lable.rfc3881.auditlogger.api.util.BytePrinter;

import java.time.Instant;

public class AuditLogQuery {
    private Instant from;
    private Instant to;
    private Long limit;
    private LogFilter filter;
    private byte[] startRowId;

    public void setFrom(Instant from) {
        this.from = from;
    }

    public void setTo(Instant to) {
        this.to = to;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public void setFilter(LogFilter filter) {
        this.filter = filter;
    }

    public void setStartRowId(byte[] startRowId) {
        this.startRowId = startRowId;
    }

    public Instant getFrom() {
        return from;
    }

    public Instant getTo() {
        return to;
    }

    public Long getLimit() {
        return limit;
    }

    public LogFilter getFilter() {
        return filter;
    }

    public byte[] getStartRowId() {
        return startRowId;
    }

    @Override
    public String toString() {
        return "Query:\n" +
                "    from: " + (from == null ? "-" : from) + "\n" +
                "      to: " + (to == null ? "-" : to) + "\n" +
                "   start: " + (startRowId == null ? "-" : BytePrinter.toStringBinary(startRowId)) + "\n" +
                "   limit: " + (limit == null ? "-" : limit) + "\n" +
                "  filter: " + (filter == null ? "-" : filter);
    }
}
