package org.lable.rfc3881.auditlogger.definition.rfc3881.events;

import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;
import org.lable.rfc3881.auditlogger.definition.rfc3881.EventAction;

/**
 * A set of audit event actions related to accessing data.
 * <p/>
 * Defined by IETF/RFC 3881 §4.3. User Access.
 */
public enum UserAccessEvent implements Referenceable {
    /**
     * All authentication attempts, successful and unsuccessful. Includes automatically reissued tickets and tokens.
     * <p>
     * IETF/RFC 3881 §4.3.1. Sign-On.
     */
    SIGN_ON("Sign-On"),
    /**
     * Explicit sign-out attempts, as well as automated expiration of sessions.
     * <p>
     * IETF/RFC 3881 §4.3.2. Sign-Off.
     */
    SIGN_OFF("Sign-Off"),
    /**
     * All operations on basic patient data.
     * <p>
     * IETF/RFC 3881 §4.3.3.1. Subject of Care Record Access.
     */
    SUBJECT_OF_CARE_RECORD_ACCESS("Subject of Care Record Access"),
    /**
     * All functions which associate instances of care to a subject of care.
     * <p>
     * IETF/RFC 3881 §4.3.3.2. Encounter or Visit.
     */
    ENCOUNTER_OR_VISIT("Encounter or Visit"),
    /**
     * All functions which associate care plans with an instance or subject of care.
     * <p>
     * IETF/RFC 3881 §4.3.3.3. Care Protocols.
     */
    CARE_PROTOCOLS("Care Protocols"),
    /**
     * All operations on specific clinical episodes within an instance of care.
     * <p>
     * IETF/RFC 3881 §4.3.3.4. Episodes or Problems.
     */
    EPISODES_OR_PROBLEMS("Episodes or Problems"),
    /**
     * All operations on clinical or supplies orders within an instance or episode of care.
     * <p>
     * IETF/RFC 3881 §4.3.3.5. Orders and Order Sets.
     */
    ORDERS_AND_ORDER_SETS("Orders and Order Sets"),
    /**
     * All operations on various health services scheduled and performed within an instance or episode of care.
     * <p>
     * IETF/RFC 3881 §4.3.3.6. Health Service Event or Act.
     */
    HEALTH_SERVICE_EVENT("Health Service Event or Act"),
    /**
     * All operations on medication orders and administration within an instance or episode of care.
     * <p>
     * IETF/RFC 3881 §4.3.3.7. Medications.
     */
    MEDICATIONS("Medications"),
    /**
     * All operations on staffing or participant assignment actions relevant to an instance or episode of care.
     * <p>
     * IETF/RFC 3881 §4.3.3.8. Staff/Participant Assignment.
     */
    STAFF_OR_PARTICIPANT_ASSIGNMENT("Staff/Participant Assignment");

    private final String displayName;

    UserAccessEvent(String displayName) {
        this.displayName = displayName;
    }

    public String getCode() {
        return name();
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public CodeReference toCodeReference() {
        return new CodeReference(
                "IETF/RFC3881.4.1",
                "IETF/RFC 3881, §4.1., Security Administration",
                getCode(),
                getDisplayName(),
                name());
    }

    @Override
    public String toString() {
        return toCodeReference().toString();
    }
}