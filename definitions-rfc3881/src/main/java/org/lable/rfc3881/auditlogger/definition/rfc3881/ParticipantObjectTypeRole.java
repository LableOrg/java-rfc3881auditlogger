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

import static org.lable.rfc3881.auditlogger.definition.rfc3881.ParticipantObjectType.*;

/**
 * Participant object type role.
 * <p>
 * Defined in RFC 3881 §5.5.2 Participant Object Type Code Role.
 */
public enum ParticipantObjectTypeRole implements Applicable, Referenceable {
    /**
     * Patient.
     */
    PATIENT("1", "Patient", PERSON),
    /**
     * Location.
     */
    LOCATION("2", "Location", ORGANIZATION),
    /**
     * Report.
     */
    REPORT("3", "Report", SYSTEM_OBJECT),
    /**
     * Resource.
     */
    RESOURCE("4", "Resource", PERSON, ORGANIZATION),
    /**
     * Master file.
     */
    MASTER_FILE("5", "Master file", SYSTEM_OBJECT),
    /**
     * User.
     */
    USER("6", "User", PERSON, SYSTEM_OBJECT),
    /**
     * List.
     */
    LIST("7", "List", SYSTEM_OBJECT),
    /**
     * Doctor.
     */
    DOCTOR("8", "Doctor", PERSON),
    /**
     * Subscriber.
     */
    SUBSCRIBER("9", "Subscriber", ORGANIZATION),
    /**
     * Guarantor.
     */
    GUARANTOR("10", "Guarantor", PERSON, ORGANIZATION),
    /**
     * Security User Entity.
     */
    SECURITY_USER_ENTITY("11", "Security User Entity", PERSON, SYSTEM_OBJECT),
    /**
     * Security User Group.
     */
    SECURITY_USER_GROUP("12", "Security User Group", SYSTEM_OBJECT),
    /**
     * Security Resource. An abstract securable object, e.g., a screen, interface, document, program, etc. -- or even
     * an audit data set or repository.
     */
    SECURITY_RESOURCE("13", "Security Resource", SYSTEM_OBJECT),
    /**
     * Security Granularity Definition.
     */
    SECURITY_GRANULARITY_DEFINITION("14", "Security Granularity Definition", SYSTEM_OBJECT),
    /**
     * Provider.
     */
    PROVIDER("15", "Provider", PERSON, ORGANIZATION),
    /**
     * Data Destination.
     */
    DATA_DESTINATION("16", "Data Destination", SYSTEM_OBJECT),
    /**
     * Data Repository.
     */
    DATA_REPOSITORY("17", "Data Repository", SYSTEM_OBJECT),
    /**
     * Schedule.
     */
    SCHEDULE("18", "Schedule", SYSTEM_OBJECT),
    /**
     * Customer.
     */
    CUSTOMER("19", "Customer", ORGANIZATION),
    /**
     * Job.
     */
    JOB("20", "Job", SYSTEM_OBJECT),
    /**
     * Job Stream.
     */
    JOB_STREAM("21", "Job Stream", SYSTEM_OBJECT),
    /**
     * Table.
     */
    TABLE("22", "Table", SYSTEM_OBJECT),
    /**
     * Routing Criteria.
     */
    ROUTING_CRITERIA("23", "Routing Criteria", SYSTEM_OBJECT),
    /**
     * Query.
     */
    QUERY("24", "Query", SYSTEM_OBJECT);

    final static String CODE_SYSTEM = "IETF/RFC3881.5.5.2";

    private final String code;
    private final String displayName;
    private final ParticipantObjectType[] applicableTypes;

    ParticipantObjectTypeRole(String code, String displayName, ParticipantObjectType... applicableTypes) {
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

    public static Optional<ParticipantObjectTypeRole> fromReferenceable(Referenceable referenceable) {
        CodeReference cs = referenceable.toCodeReference();
        if (!cs.getCodeSystem().equals(CODE_SYSTEM)) return Optional.empty();

        String code = cs.getCode();
        if (code == null) return Optional.empty();

        for (ParticipantObjectTypeRole value : values()) {
            if (value.getCode().equals(code)) return Optional.of(value);
        }

        return Optional.empty();
    }

    @Override
    public Referenceable[] applicableTypes() {
        return applicableTypes;
    }

    @Override
    public CodeReference toCodeReference() {
        return new CodeReference(
                CODE_SYSTEM,
                "IETF/RFC 3881, §5.5.2., Participant Object Type Code Role",
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
