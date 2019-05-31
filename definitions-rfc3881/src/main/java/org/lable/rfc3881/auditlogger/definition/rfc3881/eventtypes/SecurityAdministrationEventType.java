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
package org.lable.rfc3881.auditlogger.definition.rfc3881.eventtypes;

import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;

import java.util.Optional;

/**
 * A set of audit event types related to security administration.
 * <p>
 * Defined by IETF/RFC 3881 §4.1. Security Administration.
 */
public enum SecurityAdministrationEventType implements Referenceable {
    /**
     * All operations performed on security attributes of data.
     * <p>
     * IETF/RFC 3881 §4.1.1. Data Definition.
     */
    DATA_DEFINITION("Data Definition"),
    /**
     * All operations performed on function definitions.
     * <p>
     * IETF/RFC 3881 §4.1.2. Function Definition.
     */
    FUNCTION_DEFINITION("Function Definition"),
    /**
     * All operations performed on security domains.
     * <p>
     * IETF/RFC 3881 §4.1.3. Domain Definition.
     */
    DOMAIN_DEFINITION("Domain Definition"),
    /**
     * All operations performed on security categories.
     * <p>
     * IETF/RFC 3881 §4.1.4. Classification Definition.
     */
    CLASSIFICATION_DEFINITION("Classification Definition"),
    /**
     * All operations performed on security permissions.
     * <p>
     * IETF/RFC 3881 §4.1.5. Permission Definition.
     */
    PERMISSION_DEFINITION("Permission Definition"),
    /**
     * All operations performed on security roles.
     * <p>
     * This includes associating permissions with roles for role-based access control.
     * <p>
     * IETF/RFC 3881 §4.1.6. Role Definition.
     */
    ROLE_DEFINITION("Role Definition"),
    /**
     * All operations performed on user accounts. This includes password or other authentication data.
     * <p>
     * This includes associating roles with users for role-based access control, and associating permissions with users
     * for user-based access control.
     * <p>
     * IETF/RFC 3881 §4.1.7. User Definition.
     */
    USER_DEFINITION("User Definition");

    static final String CODE_SYSTEM = "IETF/RFC3881.4.1";

    private final String displayName;

    SecurityAdministrationEventType(String displayName) {
        this.displayName = displayName;
    }

    public String getCode() {
        return name();
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Optional<SecurityAdministrationEventType> fromReferenceable(Referenceable referenceable) {
        CodeReference cs = referenceable.toCodeReference();
        if (!cs.getCodeSystem().equals(CODE_SYSTEM)) return Optional.empty();

        String code = cs.getCode();
        if (code == null) return Optional.empty();

        for (SecurityAdministrationEventType value : values()) {
            if (value.getCode().equals(code)) return Optional.of(value);
        }

        return Optional.empty();
    }

    @Override
    public CodeReference toCodeReference() {
        return new CodeReference(
                CODE_SYSTEM,
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
