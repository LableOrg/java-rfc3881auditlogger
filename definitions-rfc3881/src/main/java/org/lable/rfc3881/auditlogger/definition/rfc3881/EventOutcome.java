/*
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
 * Event outcome.
 * <p>
 * Defined in RFC 3881 §5.1.4. Event Outcome Indicator.
 */
public enum EventOutcome implements Referenceable {
    /**
     * Success.
     */
    SUCCESS(0, "Success"),
    /**
     * Minor failure; action restarted, e.g., invalid password with first retry.
     */
    MINOR_FAILURE(4, "Minor failure"),
    /**
     * Serious failure; action terminated, e.g., invalid password with excess retries.
     */
    SERIOUS_FAILURE(8, "Serious failure"),
    /**
     * Major failure; action made unavailable, e.g., user account disabled due to excessive invalid log-on
     * attempts.
     */
    MAJOR_FAILURE(12, "Major failure");

    /**
     * Kept as int, because they can be sorted in order of severity that way.
     */
    private final int code;
    private final String displayName;

    EventOutcome(int code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    /**
     * @return Zero for success, or if greater than zero; the severity of the failure.
     */
    public int getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public CodeReference toCodeReference() {
        return new CodeReference(
                "IETF/RFC3881.5.1.4",
                "IETF/RFC 3881, §5.1.4., Event Outcome Indicator",
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
