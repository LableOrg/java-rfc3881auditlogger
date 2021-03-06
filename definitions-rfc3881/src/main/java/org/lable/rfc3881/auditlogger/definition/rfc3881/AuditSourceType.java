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
package org.lable.rfc3881.auditlogger.definition.rfc3881;

import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;

import java.util.Optional;

/**
 * Default codes for audit source types.
 * <p>
 * Defined by IETF/RFC 3881 §5.4.3. Audit Source Type Code.
 */
public enum AuditSourceType implements Referenceable {
    /**
     * End-user interface.
     */
    END_USER_INTERFACE("1", "End-user interface"),
    /**
     * Data acquisition device or instrument.
     */
    DATA_ACQUISITION_DEVICE("2", "Data acquisition device or instrument"),
    /**
     * Web server process tier in a multi-tier system.
     */
    WEB_SERVER_PROCESS("3", "Web server process tier in a multi-tier system"),
    /**
     * Application server process tier in a multi-tier system.
     */
    APPLICATION_SERVER_PROCESS("4", "Application server process tier in a multi-tier system"),
    /**
     * Database server process tier in a multi-tier system.
     */
    DATABASE_SERVER_PROCESS("5", "Database server process tier in a multi-tier system"),
    /**
     * Security server, e.g., a domain controller.
     */
    SECURITY_SERVER("6", "Security server, e.g., a domain controller"),
    /**
     * OSI model/ISO level 1-3 network component.
     */
    OSI_LAYER_1_2_3("7", "OSI model/ISO level 1-3 network component"),
    /**
     * OSI model/ISO level 4-6 operating software.
     */
    OSI_LAYER_4_5_6("8", "OSI model/ISO level 4-6 operating software"),
    /**
     * External source, other or unknown type.
     */
    EXTERNAL_UNKNOWN_OR_OTHER("9", "External source, other or unknown type");

    static final String CODE_SYSTEM = "IETF/RFC3881.5.4.3";

    private final String code;
    private final String displayName;

    AuditSourceType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Optional<AuditSourceType> fromReferenceable(Referenceable referenceable) {
        CodeReference cs = referenceable.toCodeReference();
        if (!cs.getCodeSystem().equals(CODE_SYSTEM)) return Optional.empty();

        String code = cs.getCode();
        if (code == null) return Optional.empty();

        for (AuditSourceType value : values()) {
            if (value.getCode().equals(code)) return Optional.of(value);
        }

        return Optional.empty();
    }

    @Override
    public CodeReference toCodeReference() {
        return new CodeReference(
                CODE_SYSTEM,
                "IETF/RFC 3881, §5.4.3., Default Audit Source Type Codes",
                getCode(),
                getDisplayName(),
                name());
    }

    @Override
    public String toString() {
        return toCodeReference().toString();
    }
}
