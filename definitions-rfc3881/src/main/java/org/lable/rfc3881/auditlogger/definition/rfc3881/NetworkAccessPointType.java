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
package org.lable.rfc3881.auditlogger.definition.rfc3881;

import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;

/**
 * Network access point type.
 * <p>
 * Defined in RFC 3881 §5.3.1. Network Access Point Type Code.
 */
public enum NetworkAccessPointType implements Referenceable {
    /**
     * Machine Name, including DNS name.
     */
    MACHINE_NAME("1", "Machine name"),
    /**
     * IP Address
     */
    IP_ADDRESS("2", "IP address"),
    /**
     * Telephone Number.
     */
    TELEPHONE_NUMBER("3", "Telephone number");

    private final String code;
    private final String displayName;

    NetworkAccessPointType(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public CodeReference toCodeReference() {
        return new CodeReference(
                "IETF/RFC3881.5.3.1",
                "IETF/RFC 3881, §5.3.1., Network Access Point Type Code",
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
