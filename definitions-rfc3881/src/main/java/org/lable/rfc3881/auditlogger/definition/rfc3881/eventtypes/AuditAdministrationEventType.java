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
import org.lable.rfc3881.auditlogger.definition.rfc3881.EventAction;

/**
 * A set of audit event types related to access and administration of the audit log itself.
 * <p/>
 * Defined by IETF/RFC 3881 §4.2. Audit Administration and Data Access.
 */
public enum AuditAdministrationEventType implements Referenceable {
    /**
     * Changes in the configuration of which events are or are not logged to the audit log.
     * <p>
     * For integrity, this event should always be audited.
     * <p>
     * IETF/RFC 3881 §4.2.1. Auditable Event Enable or Disable.
     */
    CONFIGURE_AUDITABLE_EVENT("Auditable Event Enable or Disable"),
    /**
     * All operations performed on audit data itself.
     * <p>
     * This event should always be audited.
     * <p>
     * This event type combines IETF/RFC 3881 §4.2.2 (viewing) and §4.2.3 (modification or deletion); distinction is
     * made by supplying {@link EventAction} to the event instance.
     */
    AUDIT_DATA_ACCESS("Audit Data Access");

    private final String displayName;

    AuditAdministrationEventType(String displayName) {
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
