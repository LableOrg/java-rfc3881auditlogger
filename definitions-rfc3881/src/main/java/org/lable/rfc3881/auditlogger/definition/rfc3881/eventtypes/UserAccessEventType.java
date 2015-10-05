/**
 * Copyright (C) ${project.inceptionYear} Lable (info@lable.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lable.rfc3881.auditlogger.definition.rfc3881.eventtypes;

import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;

/**
 * A set of audit event types related to accessing data.
 * <p/>
 * Defined by IETF/RFC 3881 §4.3. User Access.
 */
public enum UserAccessEventType implements Referenceable {
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

    UserAccessEventType(String displayName) {
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
