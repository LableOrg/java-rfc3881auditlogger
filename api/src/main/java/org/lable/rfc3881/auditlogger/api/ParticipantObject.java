package org.lable.rfc3881.auditlogger.api;

import org.lable.codesystem.codereference.Applicable;
import org.lable.codesystem.codereference.Identifiable;
import org.lable.codesystem.codereference.Referenceable;
import org.lable.rfc3881.auditlogger.definition.rfc3881.DataLifeCycle;
import org.lable.rfc3881.auditlogger.definition.rfc3881.ParticipantObjectIDType;
import org.lable.rfc3881.auditlogger.definition.rfc3881.ParticipantObjectType;
import org.lable.rfc3881.auditlogger.definition.rfc3881.ParticipantObjectTypeRole;

import java.util.*;

import static org.lable.rfc3881.auditlogger.api.util.ParameterValidation.parameterMayNotBeNull;

/**
 * Participant object.
 * <p>
 * Defined in RFC 3881 §5.5  Participant Object Identification.
 */
public class ParticipantObject implements Identifiable {
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
    final Referenceable idType;

    /* Optional fields. */

    /**
     * Code for the participant object type being audited. This value is distinct from the user's role or any user
     * relationship to the participant object.
     * <p>
     * IETF/RFC 3881 §5.5.1. Participant Object Type Code.
     */
    final ParticipantObjectType type;

    /**
     * Code representing the functional application role of Participant Object being audited.
     * <p>
     * IETF/RFC 3881 §5.5.2 Participant Object Type Code Role.
     */
    final ParticipantObjectTypeRole typeRole;

    /**
     * Identifier for the data life-cycle stage for the participant object. This can be used to provide an audit trail
     * for data, over time, as it passes through the system
     * <p/>
     * IETF/RFC 3881 §5.5.3. Participant Object Data Life Cycle.
     */
    final DataLifeCycle dataLifeCycle;

    /**
     * Denotes policy-defined sensitivity for the Participant Object ID such as VIP, HIV status, mental health status,
     * or similar topics.
     * <p>
     * RFC 3881 defines no default set of values for this field.
     * <p>
     * IETF/RFC 3881 §5.5.5. Participant Object Sensitivity.
     */
    final Referenceable sensitivity;

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
    final byte[] query;

    /**
     * Implementation-defined data about specific details of the object accessed or used.
     * <p>
     * IETF/RFC 3881 §5.5.9. Participant Object Detail.
     */
    final Map<Referenceable, byte[]> details;

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
                             byte[] query,
                             Detail... details) {

        parameterMayNotBeNull("id", id);
        parameterMayNotBeNull("idType", idType);

        this.id = id;
        if (type != null) {
            // Verify that the values chosen for these fields can actually be applied to the object type passed.
            verifyApplicability("idType", idType, type);
            verifyApplicability("typeRole", typeRole, type);
        }
        this.type = type;
        this.typeRole = typeRole;
        this.idType = idType;
        this.dataLifeCycle = dataLifeCycle;
        this.name = name;
        this.sensitivity = sensitivity;
        this.query = query;

        this.details = new HashMap<>();
        for (Detail detail : details) {
            this.details.put(detail.getType(), detail.getValue());
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

    public ParticipantObjectType getType() {
        return type;
    }

    public ParticipantObjectTypeRole getTypeRole() {
        return typeRole;
    }

    public String getId() {
        return id;
    }

    public Referenceable getIdType() {
        return idType;
    }

    public DataLifeCycle getDataLifeCycle() {
        return dataLifeCycle;
    }

    public Referenceable getSensitivity() {
        return sensitivity;
    }

    public String getName() {
        return name;
    }

    public byte[] getQuery() {
        return query;
    }

    public Map<Referenceable, byte[]> getDetails() {
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

    @Override
    public String toString() {
        return "ID:          " + getId() +
                "\nID type:     " + getIdType() +
                (getType() == null ? "" : "\nType:        " + getType()) +
                (getTypeRole() == null ? "" : "\nType role:   " + getTypeRole()) +
                (getDataLifeCycle() == null ? "" : "\nLife cycle:  " + getDataLifeCycle()) +
                (getSensitivity() == null ? "" : "\nSensitivity: " + getSensitivity()) +
                (getName() == null ? "" : "\nName:        " + getName()) +
                (getQuery() == null || getQuery().length == 0
                        ? "" : "\nQuery:       " + getQuery().length + " bytes set") +
                (getDetails() == null ? "" : "\nDetails:     " + getDetails().keySet());
    }

    /**
     * A type/value pair that describes additional details about a {@link ParticipantObject}.
     * <p>
     * The byte value of this class may be interpreted by audit log analyzers at a later stage.
     */
    public static class Detail {
        final Referenceable type;

        final byte[] value;

        /**
         * Define a detail.
         *
         * @param type  Code reference.
         * @param value Value.
         */
        public Detail(Referenceable type, byte[] value) {
            this.type = type;
            this.value = value;
        }

        public Referenceable getType() {
            return type;
        }

        public byte[] getValue() {
            return value;
        }
        @Override
        public String toString() {
            return getType().toCodeReference().toString();
        }
    }
}
