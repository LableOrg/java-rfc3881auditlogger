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
import org.lable.codesystem.codereference.Applicable;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Identifiable;
import org.lable.codesystem.codereference.Referenceable;
import org.lable.rfc3881.auditlogger.definition.rfc3881.DataLifeCycle;
import org.lable.rfc3881.auditlogger.definition.rfc3881.ParticipantObjectIDType;
import org.lable.rfc3881.auditlogger.definition.rfc3881.ParticipantObjectType;
import org.lable.rfc3881.auditlogger.definition.rfc3881.ParticipantObjectTypeRole;

import java.io.Serializable;
import java.util.*;

import static org.lable.rfc3881.auditlogger.api.util.ParameterValidation.parameterMayNotBeNull;

/**
 * Participant object.
 * <p>
 * Defined in RFC 3881 §5.5  Participant Object Identification.
 */
@JsonFilter("logFilter")
public class ParticipantObject implements EntryPart, Identifiable, Serializable {
    private static final long serialVersionUID = 2395902234146300877L;

    /* Required fields. */

    /**
     * Identifies a specific instance of the participant object.
     * <p>
     * IETF/RFC 3881 §5.5.6. Participant Object ID.
     */
    final String id;

    /**
     * Describes the identifier that is contained in Participant Object ID ({@link #id}).
     * <p>
     * RFC 3881 defines a default set of values for this field ({@link ParticipantObjectIDType}), but codes taken from
     * other standards may be used as well.
     * <p>
     * IETF/RFC 3881 §5.5.4. Participant Object ID Type Code.
     */
    final CodeReference idType;

    /* Optional fields. */

    /**
     * Code for the participant object type being audited. This value is distinct from the user's role or any user
     * relationship to the participant object.
     * <p>
     * IETF/RFC 3881 §5.5.1. Participant Object Type Code.
     */
    final CodeReference type;

    /**
     * Code representing the functional application role of Participant Object being audited.
     * <p>
     * IETF/RFC 3881 §5.5.2 Participant Object Type Code Role.
     */
    final CodeReference typeRole;

    /**
     * Identifier for the data life-cycle stage for the participant object. This can be used to provide an audit trail
     * for data, over time, as it passes through the system
     * <p>
     * IETF/RFC 3881 §5.5.3. Participant Object Data Life Cycle.
     */
    final CodeReference dataLifeCycle;

    /**
     * Denotes policy-defined sensitivity for the Participant Object ID such as VIP, HIV status, mental health status,
     * or similar topics.
     * <p>
     * RFC 3881 defines no default set of values for this field.
     * <p>
     * IETF/RFC 3881 §5.5.5. Participant Object Sensitivity.
     */
    final CodeReference sensitivity;

    /**
     * An instance-specific descriptor of the Participant Object ID audited, such as a person's name.
     * <p>
     * IETF/RFC 3881 §5.5.7. Participant Object Name.
     */
    final String name;

    /**
     * The actual query for a query-type participant object.
     * <p>
     * This field can be used for further analysis of audit log messages. The contents of this field can be anything,
     * and it assumed that the tools that analyze it know how to interpret it.
     * <p>
     * IETF/RFC 3881 §5.5.8. Participant Object Query.
     */
    final String query;

    /**
     * Implementation-defined data about specific details of the object accessed or used.
     * <p>
     * IETF/RFC 3881 §5.5.9. Participant Object Detail.
     */
    final List<Detail> details;

    /**
     * Mark this log entry part as complete or in need of further refinement further down the processing chain.
     */
    final boolean complete;

    @JsonCreator
    private ParticipantObject(@JsonProperty("id") String id,
                              @JsonProperty("type") CodeReference type,
                              @JsonProperty("idType") CodeReference idType,
                              @JsonProperty("typeRole") CodeReference typeRole,
                              @JsonProperty("dataLifeCycle") CodeReference dataLifeCycle,
                              @JsonProperty("sensitivity") CodeReference sensitivity,
                              @JsonProperty("name") String name,
                              @JsonProperty("query") String query,
                              @JsonProperty("complete") Boolean complete,
                              @JsonProperty("details") List<Detail> details) {
        parameterMayNotBeNull("id", id);
        parameterMayNotBeNull("idType", idType);

        this.id = id;
        this.type = type;
        this.typeRole = typeRole;
        this.idType = idType;
        this.dataLifeCycle = dataLifeCycle;
        this.name = name;
        this.sensitivity = sensitivity;
        this.query = query;
        this.complete = complete == null || complete;
        this.details = details == null ? Collections.emptyList() : details;
    }

    /**
     * Define a participant object.
     *
     * @param id            Identifier (required).
     * @param type          Type of participant object (person, system object, or organization).
     * @param idType        Type of the identifier used (required).
     * @param typeRole      Functional application role of participant object.
     * @param dataLifeCycle Stage in the data life-cycle of this object.
     * @param sensitivity   Sensitivity.
     * @param name          Human readable name for this object.
     * @param query         Query used to locate this object.
     * @param complete         Mark this data as complete, or in need of further refinement.
     * @param details       Additional details relevant to the audit trail.
     * @throws IllegalArgumentException Thrown when required fields are missing, or if {@link #idType} or
     *                                  {@link #typeRole} are not applicable to the
     *                                  {@link ParticipantObjectType} specified in {@link #type}.
     */
    public ParticipantObject(String id,
                             ParticipantObjectType type,
                             Referenceable idType,
                             ParticipantObjectTypeRole typeRole,
                             DataLifeCycle dataLifeCycle,
                             Referenceable sensitivity,
                             String name,
                             String query,
                             boolean complete,
                             Detail... details) {
        this(id, (Referenceable) type, idType, typeRole, dataLifeCycle, sensitivity, name, query, complete, details);
    }

    /**
     * Define a participant object.
     *
     * @param id            Identifier (required).
     * @param type          Type of participant object (person, system object, or organization).
     * @param idType        Type of the identifier used (required).
     * @param typeRole      Functional application role of participant object.
     * @param dataLifeCycle Stage in the data life-cycle of this object.
     * @param sensitivity   Sensitivity.
     * @param name          Human readable name for this object.
     * @param query         Query used to locate this object.
     * @param details       Additional details relevant to the audit trail.
     * @throws IllegalArgumentException Thrown when required fields are missing, or if {@link #idType} or
     *                                  {@link #typeRole} are not applicable to the
     *                                  {@link ParticipantObjectType} specified in {@link #type}.
     */
    public ParticipantObject(String id,
                             ParticipantObjectType type,
                             Referenceable idType,
                             ParticipantObjectTypeRole typeRole,
                             DataLifeCycle dataLifeCycle,
                             Referenceable sensitivity,
                             String name,
                             String query,
                             Detail... details) {
        this(id, (Referenceable) type, idType, typeRole, dataLifeCycle, sensitivity, name, query, true, details);
    }

    /**
     * Define a participant object.
     *
     * @param id            Identifier (required).
     * @param type          Type of participant object (e.g., person, system object, or organization).
     * @param idType        Type of the identifier used (required).
     * @param typeRole      Functional application role of participant object.
     * @param dataLifeCycle Stage in the data life-cycle of this object.
     * @param sensitivity   Sensitivity.
     * @param name          Human readable name for this object.
     * @param query         Query used to locate this object.
     * @param details       Additional details relevant to the audit trail.
     * @throws IllegalArgumentException Thrown when required fields are missing, or if {@link #idType} or
     *                                  {@link #typeRole} are not applicable to the
     *                                  {@link ParticipantObjectType} specified in {@link #type}.
     */
    public ParticipantObject(String id,
                             Referenceable type,
                             Referenceable idType,
                             Referenceable typeRole,
                             Referenceable dataLifeCycle,
                             Referenceable sensitivity,
                             String name,
                             String query,
                             Detail... details) {
        this(id, type, idType, typeRole, dataLifeCycle, sensitivity, name, query, true, details);
    }

    /**
     * Define a participant object.
     *
     * @param id            Identifier (required).
     * @param type          Type of participant object (e.g., person, system object, or organization).
     * @param idType        Type of the identifier used (required).
     * @param typeRole      Functional application role of participant object.
     * @param dataLifeCycle Stage in the data life-cycle of this object.
     * @param sensitivity   Sensitivity.
     * @param name          Human readable name for this object.
     * @param query         Query used to locate this object.
     * @param complete         Mark this data as complete, or in need of further refinement.
     * @param details       Additional details relevant to the audit trail.
     * @throws IllegalArgumentException Thrown when required fields are missing, or if {@link #idType} or
     *                                  {@link #typeRole} are not applicable to the
     *                                  {@link ParticipantObjectType} specified in {@link #type}.
     */
    public ParticipantObject(String id,
                             Referenceable type,
                             Referenceable idType,
                             Referenceable typeRole,
                             Referenceable dataLifeCycle,
                             Referenceable sensitivity,
                             String name,
                             String query,
                             boolean complete,
                             Detail... details) {

        parameterMayNotBeNull("id", id);
        parameterMayNotBeNull("idType", idType);

        this.id = id;
        if (type != null) {
            // Verify that the values chosen for these fields can actually be applied to the object type passed.
            verifyApplicability("idType", idType, type);
            verifyApplicability("typeRole", typeRole, type);
        }

        this.type = type == null ? null : type.toCodeReference();
        this.typeRole = typeRole == null ? null : typeRole.toCodeReference();
        this.idType = idType.toCodeReference();
        this.dataLifeCycle = dataLifeCycle == null ? null : dataLifeCycle.toCodeReference();
        this.name = name;
        this.sensitivity = sensitivity == null ? null : sensitivity.toCodeReference();
        this.query = query;
        this.complete = complete;

        if (details != null) {
            this.details = Arrays.asList(details);
        } else {
            this.details = Collections.emptyList();
        }
    }

    static void verifyApplicability(String parameter,
                                    Referenceable referenceable,
                                    Referenceable type) {
        if (referenceable == null) return;

        if (referenceable instanceof Applicable) {
            Applicable applicable = (Applicable) referenceable;
            if (!applicable.appliesTo(type)) {
                String theseApply = Arrays.toString(applicable.applicableTypes());
                theseApply += applicable.applicableTypes().length == 1 ? " applies" : " apply";
                throw new IllegalArgumentException(
                        "Parameter " + parameter + " can not be used together with an object type of `" +
                                type.toCodeReference().getDisplayName() + "`. Only " + theseApply);
            }
        }
    }

    public CodeReference getType() {
        return type;
    }

    public CodeReference getTypeRole() {
        return typeRole;
    }

    public String getId() {
        return id;
    }

    public Referenceable getIdType() {
        return idType;
    }

    public CodeReference getDataLifeCycle() {
        return dataLifeCycle;
    }

    public Referenceable getSensitivity() {
        return sensitivity;
    }

    public String getName() {
        return name;
    }

    public String getQuery() {
        return query;
    }

    public List<Detail> getDetails() {
        return details;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> identifyingStack() {
        List<String> parts = new ArrayList<>(getIdType().toCodeReference().identifyingStack());
        parts.add(getId());
        return parts;
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

        ParticipantObject that = (ParticipantObject) other;
        return Objects.equals(this.id, that.id) &&
                Objects.equals(this.idType, that.idType) &&
                Objects.equals(this.type, that.type) &&
                Objects.equals(this.typeRole, that.typeRole) &&
                Objects.equals(this.dataLifeCycle, that.dataLifeCycle) &&
                Objects.equals(this.sensitivity, that.sensitivity) &&
                Objects.equals(this.name, that.name) &&
                Objects.equals(this.query, that.query) &&
                this.complete == that.complete &&
                Objects.equals(this.details, that.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idType, type, typeRole, dataLifeCycle, sensitivity, name, query, complete, details);
    }

    @Override
    public String toString() {
        return "ID:          " + getId() +
                "\nID type:     " + getIdType() +
                (getType() == null ? "" : "\nType:        " + getType()) +
                (getTypeRole() == null ? "" : "\nType role:   " + getTypeRole()) +
                (getDataLifeCycle() == null ? "" : "\nLife cycle:  " + getDataLifeCycle()) +
                (getSensitivity() == null ? "" : "\nSensitivity: " + getSensitivity()) +
                (getName() == null ? "" : "\nName:        " + getName()) +
                (getQuery() == null ? "" : "\nQuery:       " + getQuery()) +
                (getDetails() == null ? "" : "\nDetails:     " + getDetails().size()) +
                (complete ? "" : "\nINCOMPLETE");
    }

    /**
     * A type/value pair that describes additional details about a {@link ParticipantObject}.
     * <p>
     * The byte value of this class may be interpreted by audit log analyzers at a later stage.
     */
    public static class Detail {
        final CodeReference type;

        final String value;

        @JsonCreator
        private Detail(@JsonProperty("type") CodeReference type,
                       @JsonProperty("value") String value) {
            this.type = type;
            this.value = value;
        }

        /**
         * Define a detail.
         *
         * @param type  Code reference.
         * @param value Value.
         */
        public Detail(Referenceable type, String value) {
            this.type = type.toCodeReference();
            this.value = value;
        }

        public CodeReference getType() {
            return type;
        }

        public String getValue() {
            return value;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) return true;
            if (other == null || getClass() != other.getClass()) return false;

            Detail that = (Detail) other;
            return Objects.equals(this.type, that.type) &&
                    Objects.equals(this.value, that.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, value);
        }

        @Override
        public String toString() {
            return getType().toString() + ":" + getValue();
        }
    }
}
