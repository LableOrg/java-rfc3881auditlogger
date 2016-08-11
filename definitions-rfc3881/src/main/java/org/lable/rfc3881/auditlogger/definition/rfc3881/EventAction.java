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
 * Event action.
 * <p>
 * Defined in RFC 3881 §5.1.2. Event Action Code.
 */
public enum EventAction implements Referenceable {
    /**
     * Create.
     */
    CREATE('C', "Create"),
    /**
     * Read/View/Print/Query.
     */
    READ('R', "Read"),
    /**
     * Update.
     */
    UPDATE('U', "Update"),
    /**
     * Delete.
     */
    DELETE('D', "Delete"),
    /**
     * Execute.
     * <p>
     * This category includes actions such a log-on and log-off action.
     */
    EXECUTE('E', "Execute");

    private final char code;
    private final String displayName;

    EventAction(char code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    /**
     * @return The single-character abbreviation for this action.
     */
    public char getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public CodeReference toCodeReference() {
        return new CodeReference(
                "IETF/RFC3881.5.1.2",
                "IETF/RFC 3881, §5.1.2., Event Action Code",
                String.valueOf(getCode()),
                getDisplayName(),
                name()
        );
    }

    @Override
    public String toString() {
        return toCodeReference().toString();
    }
}
