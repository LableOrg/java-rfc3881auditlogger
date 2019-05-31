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

import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;

import java.util.Optional;

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

    static final String CODE_SYSTEM = "IETF/RFC3881.5.1.2";

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

    public static Optional<EventAction> fromReferenceable(Referenceable referenceable) {
        CodeReference cs = referenceable.toCodeReference();
        if (!cs.getCodeSystem().equals(CODE_SYSTEM)) return Optional.empty();

        String code = cs.getCode();
        if (code == null || code.length() != 1) return Optional.empty();
        char c = code.charAt(0);

        for (EventAction value : values()) {
            if (value.getCode() == c) return Optional.of(value);
        }

        return Optional.empty();
    }

    @Override
    public CodeReference toCodeReference() {
        return new CodeReference(
                CODE_SYSTEM,
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
