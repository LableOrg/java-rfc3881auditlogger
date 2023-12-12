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

import org.lable.rfc3881.auditlogger.api.LogFilter;

import java.time.Instant;

public class FindFirstQuery {
    private Instant from;
    private LogFilter filter;

    public Instant getFrom() {
        return from;
    }

    public void setFrom(Instant from) {
        this.from = from;
    }

    public LogFilter getFilter() {
        return filter;
    }

    public void setFilter(LogFilter filter) {
        this.filter = filter;
    }

    @Override
    public String toString() {
        return "Find-first:\n" +
                "    from: " + (from == null ? "-" : from) + "\n" +
                "  filter: " + (filter == null ? "-" : filter);
    }
}
