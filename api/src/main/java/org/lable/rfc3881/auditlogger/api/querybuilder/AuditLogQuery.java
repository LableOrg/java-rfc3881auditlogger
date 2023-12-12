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

import org.lable.rfc3881.auditlogger.api.Event.EventId;
import org.lable.rfc3881.auditlogger.api.LogFilter;

import java.time.Instant;

public class AuditLogQuery {
    private Object from;
    private Object to;
    private boolean toInclusive = false;
    private boolean fromInclusive = true;
    private Long limit;
    private LogFilter filter;

    public void setFrom(Instant from, boolean inclusive) {
        this.from = from;
        this.fromInclusive = inclusive;
    }

    public void setFrom(EventId from, boolean inclusive) {
        this.from = from;
        this.fromInclusive = inclusive;
    }

    public void setTo(Instant to, boolean inclusive) {
        this.to = to;
        this.toInclusive = inclusive;
    }

    public void setTo(EventId to, boolean inclusive) {
        this.to = to;
        this.toInclusive = inclusive;
    }

    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public void setFilter(LogFilter filter) {
        this.filter = filter;
    }

    public boolean hasFrom() {
        return from != null;
    }

    public boolean hasTo() {
        return to != null;
    }

    public Instant getFromAsInstant() {
        return from instanceof Instant ? (Instant) from : null;
    }

    public EventId getFromAsEventId() {
        return from instanceof EventId ? (EventId) from : null;
    }

    public Instant getToAsInstant() {
        return to instanceof Instant ? (Instant) to : null;
    }

    public EventId getToAsEventId() {
        return to instanceof EventId ? (EventId) to : null;
    }

    public boolean isToInclusive() {
        return toInclusive;
    }

    public boolean isFromInclusive() {
        return fromInclusive;
    }

    public Long getLimit() {
        return limit;
    }

    public LogFilter getFilter() {
        return filter;
    }

    @Override
    public String toString() {
        return "Query:\n" +
                "    from: " + (from == null ? "-" : from) + "\n" +
                "      to: " + (to == null ? "-" : to) + "\n" +
                "   limit: " + (limit == null ? "-" : limit) + "\n" +
                "  filter: " + (filter == null ? "-" : filter);
    }
}
