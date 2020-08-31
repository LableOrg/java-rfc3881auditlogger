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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Identifiable;
import org.lable.codesystem.codereference.Referenceable;
import org.lable.rfc3881.auditlogger.definition.rfc3881.AuditSourceType;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.lable.rfc3881.auditlogger.api.util.ParameterValidation.parameterMayNotBeNull;

/**
 * An audit source is a distinct participating process, service, or server in an audit event. For distributed services
 * these may be used to identify relevant participants in an event. These include, for example, the application server
 * handling a request from a webapp, and an authentication server or some remote service performing tasks related to
 * the audit event.
 * <p>
 * Defined in RFC 3881 §5.4. Audit Source Identification.
 */
@JsonFilter("logFilter")
public class AuditSource implements EntryPart, Identifiable, Serializable {
    private static final long serialVersionUID = 1287102005812178285L;

    /* Required fields. */

    /**
     * Identifier of the source where the event originated.
     * <p>
     * IETF/RFC 3881 §5.4.2. Audit Source ID.
     */
    final String id;

    /* Optional fields. */

    /**
     * Logical source location within the healthcare enterprise network, e.g., a hospital or other provider location
     * within a multi-entity provider group.
     * <p>
     * IETF/RFC 3881 §5.4.1. Audit Enterprise Site ID.
     */
    final String enterpriseSiteId;

    /**
     * Code specifying the type of source where event originated.
     * <p>
     * IETF/RFC 3881 §5.4.3. Audit Source Type Code
     */
    final List<CodeReference> typeCodes;

    /**
     * Mark this log entry part as complete or in need of further refinement further down the processing chain.
     */
    final boolean complete;

    @JsonCreator
    private AuditSource(@JsonProperty("enterpriseSiteId") String enterpriseSiteId,
                        @JsonProperty("id") String id,
                        @JsonProperty("complete") Boolean complete,
                        @JsonProperty("typeCodes") List<CodeReference> typeCodes) {
        parameterMayNotBeNull("id", id);

        this.typeCodes = defaultIfEmpty(typeCodes);
        this.enterpriseSiteId = enterpriseSiteId;
        this.id = id;
        this.complete = complete == null || complete;
    }

    /**
     * Define an audit source. The type codes defined in {@link AuditSourceType} can be used here to specify the type
     * of audit source, but custom code references may be used as well.
     *
     * @param enterpriseSiteId Logical identifier of your server cluster or network.
     * @param id               Identifier.
     * @param complete         Mark this data as complete, or in need of further refinement.
     * @param typeCodes        Type of audit source.
     */
    public AuditSource(String enterpriseSiteId, String id, boolean complete, Referenceable... typeCodes) {
        parameterMayNotBeNull("id", id);

        List<CodeReference> typeCodesCR = (typeCodes == null)
                ? typeCodesCR = Collections.emptyList()
                : Arrays.stream(typeCodes)
                .map(Referenceable::toCodeReference)
                .collect(Collectors.toList());

        this.typeCodes = defaultIfEmpty(typeCodesCR);
        this.enterpriseSiteId = enterpriseSiteId;
        this.complete = complete;
        this.id = id;
    }

    /**
     * Define an audit source. The type codes defined in {@link AuditSourceType} can be used here to specify the type
     * of audit source, but custom code references may be used as well.
     *
     * @param enterpriseSiteId Logical identifier of your server cluster or network.
     * @param id               Identifier.
     * @param typeCodes        Type of audit source.
     */
    public AuditSource(String enterpriseSiteId, String id, Referenceable... typeCodes) {
        this(enterpriseSiteId, id, true, typeCodes);
    }

    private List<CodeReference> defaultIfEmpty(List<CodeReference> typeCodes) {
        // Default to predefined "unknown" type if not set.
        return typeCodes == null || typeCodes.size() == 0
                ? Collections.singletonList(AuditSourceType.EXTERNAL_UNKNOWN_OR_OTHER.toCodeReference())
                : typeCodes;
    }

    public String getId() {
        return id;
    }

    public String getEnterpriseSiteId() {
        return enterpriseSiteId;
    }

    public List<CodeReference> getTypeCodes() {
        return typeCodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> identifyingStack() {
        return Arrays.asList(getEnterpriseSiteId(), getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isComplete() {
        return complete;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        AuditSource that = (AuditSource) other;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.enterpriseSiteId, that.enterpriseSiteId) &&
                this.complete == that.complete &&
                Objects.equals(this.typeCodes, that.typeCodes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, enterpriseSiteId, complete, typeCodes);
    }

    @Override
    public String toString() {
        return "ID:          " + getId() +
                (getEnterpriseSiteId() == null ? "" : "\nSite ID:     " + getEnterpriseSiteId()) +
                "\nType:        " + (getTypeCodes() == null ? "[]" : getTypeCodes()) +
                (complete ? "" : "\nINCOMPLETE");
    }
}
