/*
 * Copyright (C) 2015 Lable (info@lable.nl)
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
package org.lable.rfc3881.auditlogger.definition.rfc3881;

import org.lable.codesystem.codereference.Applicable;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;

import java.util.Optional;

import static org.lable.rfc3881.auditlogger.definition.rfc3881.ParticipantObjectType.ORGANIZATION;
import static org.lable.rfc3881.auditlogger.definition.rfc3881.ParticipantObjectType.PERSON;
import static org.lable.rfc3881.auditlogger.definition.rfc3881.ParticipantObjectType.SYSTEM_OBJECT;

/**
 * Participant object ID type.
 * <p>
 * Defined in RFC 3881 §5.5.4. Participant Object ID Type Code.
 */
public enum ParticipantObjectIDType implements Applicable, Referenceable {
    /**
     * Medical Record Number.
     */
    MEDICAL_RECORD_NUMBER("1", "Medical Record Number", PERSON),
    /**
     * Patient Number.
     */
    PATIENT_NUMBER("2", "Patient Number", PERSON),
    /**
     * Encounter Number.
     */
    ENCOUNTER_NUMBER("3", "Encounter Number", PERSON),
    /**
     * Enrollee Number.
     */
    ENROLLEE_NUMBER("4", "Enrollee Number", PERSON),
    /**
     * Social Security Number.
     */
    SOCIAL_SECURITY_NUMBER("5", "Social Security Number", PERSON),
    /**
     * Account Number.
     */
    ACCOUNT_NUMBER("6", "Account Number", PERSON, ORGANIZATION),
    /**
     * Guarantor Number.
     */
    GUARANTOR_NUMBER("7", "Guarantor Number", PERSON, ORGANIZATION),
    /**
     * Report Name.
     */
    REPORT_NAME("8", "Report Name", SYSTEM_OBJECT),
    /**
     * Report Number.
     */
    REPORT_NUMBER("9", "Report Number", SYSTEM_OBJECT),
    /**
     * Search Criteria.
     */
    SEARCH_CRITERIA("10", "Search Criteria", SYSTEM_OBJECT),
    /**
     * User Identifier.
     */
    USER_IDENTIFIER("11", "User Identifier", PERSON, SYSTEM_OBJECT),
    /**
     * URI.
     */
    URI("12", "URI", SYSTEM_OBJECT);

    final static String CODE_SYSTEM = "IETF/RFC3881.5.5.4";

    private final String code;
    private final String displayName;
    private final ParticipantObjectType[] applicableTypes;

    ParticipantObjectIDType(String code, String displayName, ParticipantObjectType... applicableTypes) {
        this.code = code;
        this.displayName = displayName;
        this.applicableTypes = applicableTypes;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Optional<ParticipantObjectIDType> fromReferenceable(Referenceable referenceable) {
        CodeReference cs = referenceable.toCodeReference();
        if (!cs.getCodeSystem().equals(CODE_SYSTEM)) return Optional.empty();

        String code = cs.getCode();
        if (code == null) return Optional.empty();

        for (ParticipantObjectIDType value : values()) {
            if (value.getCode().equals(code)) return Optional.of(value);
        }

        return Optional.empty();
    }

    @Override
    public boolean appliesTo(Referenceable referenceable) {
        for (ParticipantObjectType applicableType : applicableTypes) {
            if (applicableType == referenceable) {
                return true;
            }
        }
        return false;
    }

    @Override
    public Referenceable[] applicableTypes() {
        return applicableTypes;
    }

    @Override
    public CodeReference toCodeReference() {
        return new CodeReference(
                "IETF/RFC3881.5.5.4",
                "IETF/RFC 3881, §5.5.4. Participant Object ID Type Code",
                getCode(),
                getDisplayName(),
                name()
        );
    }

    @Override
    public String toString() {
        return toCodeReference().toString();
    }
}
