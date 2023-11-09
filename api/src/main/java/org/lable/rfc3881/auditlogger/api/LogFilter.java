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

import java.util.*;

public class LogFilter {
    Referenceable eventId;
    Set<String> principalFilter;
    PrincipalFilterType principalFilterType = PrincipalFilterType.EXACT;
    List<ObjectId> participantObjectIds;
    ParticipantObjectFilterType participantObjectFilterType = ParticipantObjectFilterType.OR;

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

    public Set<String> getPrincipalFilter() {
        return principalFilter;
    }

    public PrincipalFilterType getPrincipalFilterType() {
        return principalFilterType;
    }

    public List<ObjectId> getParticipantObjectIds() {
        return participantObjectIds;
    }

    public ParticipantObjectFilterType getParticipantObjectFilterType() {
        return participantObjectFilterType;
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
            return principal(PrincipalFilterType.EXACT, principal);
        }

        public FilterBuilder filterOnPrincipalsInvolved(String... principals) {
            return principals(PrincipalFilterType.EXACT, principals);
        }

        public FilterBuilder filterOnPrincipalsInvolved(Collection<String> principals) {
            return principals(PrincipalFilterType.EXACT, principals);
        }


        public FilterBuilder filterOnAccountDomain(String domain) {
            return principal(PrincipalFilterType.DOMAIN, domain);
        }

        public FilterBuilder filterOnAccountDomain(String... domains) {
            return principals(PrincipalFilterType.DOMAIN, domains);
        }

        public FilterBuilder filterOnAccountDomain(Collection<String> domains) {
            return principals(PrincipalFilterType.DOMAIN, domains);
        }


        public FilterBuilder filterOnAccountDomainsStartingWith(String domainPrefix) {
            return principal(PrincipalFilterType.DOMAIN_STARTS_WITH, domainPrefix);
        }

        public FilterBuilder filterOnAccountDomainsStartingWith(String... domainPrefixes) {
            return principals(PrincipalFilterType.DOMAIN_STARTS_WITH, domainPrefixes);
        }

        public FilterBuilder filterOnAccountDomainsStartingWith(Collection<String> domainPrefixes) {
            return principals(PrincipalFilterType.DOMAIN_STARTS_WITH, domainPrefixes);
        }


        public FilterBuilder filterOnAccountDomainsContaining(String fragment) {
            return principal(PrincipalFilterType.DOMAIN_CONTAINS, fragment);
        }

        public FilterBuilder filterOnAccountDomainsContaining(String... fragments) {
            return principals(PrincipalFilterType.DOMAIN_CONTAINS, fragments);
        }

        public FilterBuilder filterOnAccountDomainsContaining(Collection<String> fragments) {
            return principals(PrincipalFilterType.DOMAIN_CONTAINS, fragments);
        }


        public FilterBuilder filterOnAccountDomainsMatchingRegex(String regex) {
            return principal(PrincipalFilterType.DOMAIN_REGEX, regex);
        }

        public FilterBuilder filterOnAccountDomainsMatchingRegex(String... regexes) {
            return principals(PrincipalFilterType.DOMAIN_REGEX, regexes);
        }

        public FilterBuilder filterOnAccountDomainsMatchingRegex(Collection<String> regexes) {
            return principals(PrincipalFilterType.DOMAIN_REGEX, regexes);
        }


        private FilterBuilder principal(PrincipalFilterType principalFilterType, String item) {
            logFilter.principalFilter = Collections.singleton(item);
            logFilter.principalFilterType = principalFilterType;
            return this;
        }

        private FilterBuilder principals(PrincipalFilterType principalFilterType, String... items) {
            logFilter.principalFilter = new HashSet<>(Arrays.asList(items));
            logFilter.principalFilterType = principalFilterType;
            return this;
        }

        private FilterBuilder principals(PrincipalFilterType principalFilterType, Collection<String> items) {
            logFilter.principalFilter = new HashSet<>(items);
            logFilter.principalFilterType = principalFilterType;
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

        public FilterBuilder allParticipantObjectFiltersMustMatch() {
            logFilter.participantObjectFilterType = ParticipantObjectFilterType.AND;
            return this;
        }

        public FilterBuilder atLeastOneParticipantObjectFiltersMustMatch() {
            logFilter.participantObjectFilterType = ParticipantObjectFilterType.OR;
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

    public enum PrincipalFilterType {
        EXACT,
        DOMAIN,
        DOMAIN_REGEX,
        DOMAIN_CONTAINS,
        DOMAIN_STARTS_WITH,
    }

    public enum ParticipantObjectFilterType {
        AND,
        OR,
    }
}
