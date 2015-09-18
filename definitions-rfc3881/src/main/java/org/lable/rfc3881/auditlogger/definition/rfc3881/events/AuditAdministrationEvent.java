package org.lable.rfc3881.auditlogger.definition.rfc3881.events;

import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;
import org.lable.rfc3881.auditlogger.definition.rfc3881.EventAction;

/**
 * A set of audit event actions related to access and administration of the audit log itself.
 * <p/>
 * Defined by IETF/RFC 3881 §4.2. Audit Administration and Data Access.
 */
public enum AuditAdministrationEvent implements Referenceable {
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
     * This event combines IETF/RFC 3881 §4.2.2 (viewing) and §4.2.3 (modification or deletion); distinction is made by
     * supplying {@link EventAction} to the event instance.
     */
    AUDIT_DATA_ACCESS("Audit Data Access");

    private final String displayName;

    AuditAdministrationEvent(String displayName) {
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
