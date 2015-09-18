package org.lable.rfc3881.auditlogger.api;

import org.lable.codesystem.codereference.Referenceable;

import java.util.List;

/**
 * Log entry for an audit event.
 */
public class LogEntry {
    /**
     * Identifies the name, action types, time, and disposition of the audited event.
     * <p>
     * IETF/RFC 3881 §5.1. Event Identification.
     */
    Event event;

    /**
     * The initiator of this event.
     * <p>
     * IETF/RFC 3881 §5.2.4. User Is Requestor.
     */
    Principal requestor;

    /**
     * The user accountable for this event. This field is optional, and can be used in case an action is
     * delegated to another user (the {@link #requestor}), but responsibility remains with the delegator.
     * <p>
     * This optional field is not part of IETF/RFC 3881; this is an extension defined in the related Dutch standard
     * NEN 7513:2010 §7.3.6 ID van verantwoordelijke.
     */
    Principal delegator;

    /**
     * Other principals involved in this event — e.g., as the recipient of a generated report or a grantee of security
     * permissions — but not responsible for initiating it.
     * <p>
     * IETF/RFC 3881 §5.2. Active Participant Identification.
     */
    List<Principal> participatingPrincipals;

    /**
     * Network node where the event was initiated.
     * <p>
     * IETF/RFC 3881 §5.3. Network Access Point Identification.
     */
    NetworkAccessPoint networkAccessPoint;

    /**
     * List of participating processes, services, or servers.
     * <p>
     * IETF/RFC 3881 §5.4. Audit Source Identification.
     */
    List<AuditSource> auditSources;

    /**
     * Participant objects. These indicate the data or objects that have been accessed.
     * <p>
     * IETF/RFC 3881 §5.5. Participant Object Identification.
     */
    List<ParticipantObject> participantObjects;

    /**
     * Version of the audit logging implementation. Can be used to track the current implementation of
     * your audit logging. This may be useful when audit logging is gradually implemented or periodically reviewed,
     * and the breadth and scope of what is logged changes. Versioning the current status of implementation may aid
     * in the analysis of audit logs at a later date.
     * <p>
     * This optional field is not part of RFC 3881.
     */
    Referenceable version;

    public LogEntry(Event event,
                    Principal requestor,
                    Principal delegator,
                    List<Principal> participatingPrincipals,
                    NetworkAccessPoint networkAccessPoint,
                    List<AuditSource> auditSources,
                    List<ParticipantObject> participantObjects,
                    Referenceable version) {
        this.event = event;
        this.requestor = requestor;
        this.delegator = delegator;
        this.participatingPrincipals = participatingPrincipals;
        this.networkAccessPoint = networkAccessPoint;
        this.auditSources = auditSources;
        this.participantObjects = participantObjects;
        this.version = version;
    }

    /**
     * @return The audit event ({@link #event}).
     */
    public Event getEvent() {
        return event;
    }

    /**
     * @return The requestor ({@link #requestor}).
     */
    public Principal getRequestor() {
        return requestor;
    }

    /**
     * @return The delegator ({@link #delegator}).
     */
    public Principal getDelegator() {
        return delegator;
    }

    /**
     * @return The list of active participants ({@link #participatingPrincipals}).
     */
    public List<Principal> getParticipatingPrincipals() {
        return participatingPrincipals;
    }

    /**
     * @return The network access point ({@link #networkAccessPoint}).
     */
    public NetworkAccessPoint getNetworkAccessPoint() {
        return networkAccessPoint;
    }

    /**
     * @return The list of audit sources ({@link #auditSources}).
     */
    public List<AuditSource> getAuditSources() {
        return auditSources;
    }

    /**
     * @return The list of participant objects ({@link #participantObjects}).
     */
    public List<ParticipantObject> getParticipantObjects() {
        return participantObjects;
    }

    /**
     * @return The current version of the audit log implementation ({@link #version}).
     */
    public Referenceable getVersion() {
        return version;
    }

    @Override
    public String toString() {
        return "AUDIT EVENT\n" +
                "--------------------------------------------\n" +
                "[[   event   ]]:\n" +
                getEvent() + "\n" +
                "[[   active participants   ]]:\n" +
                (getRequestor() == null ? "" : "requestor:\n" + getRequestor() + "\n") +
                (getDelegator() == null ? "" : "delegator:\n" + getDelegator() + "\n") +
                (getParticipatingPrincipals() == null || getParticipatingPrincipals().isEmpty()
                        ? "" : "participants:\n" + join(getParticipatingPrincipals()) + "\n") +
                "[[   network access point   ]]:\n" +
                getNetworkAccessPoint() + "\n" +
                "[[   audit sources   ]]:\n" +
                join(getAuditSources()) + "\n" +
                "[[   participant objects   ]]:\n" +
                join(getParticipantObjects()) + "\n" +
                "[[   version   ]]:\n" +
                (getVersion() == null ? "unknown" : getVersion() + "\n") +
                "--------------------------------------------";
    }

    static String join(List<?> objects) {
        String out = "";
        boolean first = true;
        for (Object object : objects) {
            out += object.toString();
            if (!first) {
                out += "\n-\n";
            }
            first = false;
        }
        return out;
    }
}
