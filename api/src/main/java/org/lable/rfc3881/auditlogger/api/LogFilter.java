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

import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;

import java.util.ArrayList;
import java.util.List;

public class LogFilter {
    Referenceable eventId;
    String principal;
    List<ObjectId> participantObjectIds;

    private LogFilter() {
        this.participantObjectIds = new ArrayList<>();
    }

    public static FilterBuilder define() {
        return new FilterBuilder();
    }

    public static LogFilter empty() {
        return new LogFilter();
    }

    public Referenceable getEventId() {
        return eventId;
    }

    public String getPrincipal() {
        return principal;
    }

    public List<ObjectId> getParticipantObjectIds() {
        return participantObjectIds;
    }

    public static class FilterBuilder {
        LogFilter logFilter;

        FilterBuilder() {
            this.logFilter = new LogFilter();
        }

        public FilterBuilder filterOnEventId(Referenceable eventId) {
            logFilter.eventId = eventId;
            return this;
        }

        public FilterBuilder filterOnEventId(String codeSystem, String code) {
            logFilter.eventId = new CodeReference(codeSystem, code);
            return this;
        }

        public FilterBuilder filterOnPrincipalInvolved(String principal) {
            logFilter.principal = principal;
            return this;
        }

        public FilterBuilder addFilterOnParticipantObject(Referenceable type, String id) {
            ObjectId oid = new ObjectId(type, id);
            logFilter.participantObjectIds.add(oid);
            return this;
        }


        public FilterBuilder addFilterOnParticipantObject(String codeSystem, String code, String id) {
            ObjectId oid = new ObjectId(new CodeReference(codeSystem, code), id);
            logFilter.participantObjectIds.add(oid);
            return this;
        }

        public LogFilter build() {
            return logFilter;
        }
    }

    public static class ObjectId {
        Referenceable typeId;
        String id;

        public ObjectId(Referenceable typeId, String id) {
            this.typeId = typeId;
            this.id = id;
        }

        public Referenceable getTypeId() {
            return typeId;
        }

        public String getId() {
            return id;
        }
    }
}
