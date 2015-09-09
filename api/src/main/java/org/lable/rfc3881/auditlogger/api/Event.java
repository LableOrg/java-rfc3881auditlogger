package org.lable.rfc3881.auditlogger.api;

import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;
import org.lable.rfc3881.auditlogger.definition.rfc3881.EventAction;
import org.lable.rfc3881.auditlogger.definition.rfc3881.EventOutcome;

import java.util.Arrays;
import java.util.List;

/**
 * Audit event.
 * <p>
 * Defined in IETF/RFC 3881 §5.1. Event Identification.
 */
public class Event {
    /* Required fields. */

    /**
     * Identifier for a specific audited event, e.g., a menu item, program, rule, policy, function code, application
     * name, or URL. It identifies the performed function.
     * <p>
     * IETF/RFC 3881 §5.1.1. Event ID.
     */
    final Referenceable id;

    /**
     * Timestamp for when the audit event took place.
     * <p>
     * IETF/RFC 3881 §5.1.3. Event Date/Time.
     */
    final Instant happenedAt;

    /**
     * Indicates whether the event succeeded or failed.
     * <p>
     * IETF/RFC 3881 §5.1.4. Event Outcome Indicator.
     */
    final EventOutcome eventOutcome;

    /* Optional fields. */

    /**
     * Indicator for type of action performed during the event that generated the audit.
     * <p>
     * IETF/RFC 3881 §5.1.2. Event Action Code.
     */
    final EventAction eventAction;

    /**
     * Identifier for the category of event.
     * <p>
     * IETF/RFC 3881 §5.1.5. Event Type Code.
     */
    final List<Referenceable> types;

    /**
     * Define an audit event that took place just now.
     *
     * @param id           Identifier.
     * @param eventAction  Audit action.
     * @param eventOutcome Outcome of the event.
     */
    public Event(Referenceable id, EventAction eventAction, EventOutcome eventOutcome) {
        this(id, eventAction, Instant.now(), eventOutcome, new CodeReference[0]);
    }

    /**
     * Define an audit event that took place just now.
     *
     * @param id           Identifier.
     * @param eventAction  Audit action.
     * @param eventOutcome Outcome of the event.
     * @param types        Event classification.
     */
    public Event(Referenceable id, EventAction eventAction, EventOutcome eventOutcome, Referenceable... types) {
        this(id, eventAction, Instant.now(), eventOutcome, types);
    }

    /**
     * Define an audit event.
     *
     * @param id           Identifier.
     * @param eventAction  Audit action.
     * @param happenedAt   When this event took place.
     * @param eventOutcome Outcome of the event.
     * @param types        Event classification.
     */
    public Event(Referenceable id, EventAction eventAction, Instant happenedAt, EventOutcome eventOutcome,
                 Referenceable... types) {
        this.id = id;
        this.eventAction = eventAction;
        this.happenedAt = happenedAt;
        this.eventOutcome = eventOutcome;
        this.types = Arrays.asList(types);
    }

    /**
     * @return Action ID.
     */
    public Referenceable getId() {
        return id;
    }

    /**
     * @return Sort of action performed (create, read, update, delete, or execute).
     */
    public EventAction getAction() {
        return eventAction;
    }

    /**
     * @return When the event happened.
     */
    public Instant getHappenedAt() {
        return happenedAt;
    }

    /**
     * @return Outcome of the event (success or failure).
     */
    public EventOutcome getOutcome() {
        return eventOutcome;
    }

    public List<Referenceable> getTypes() {
        return types;
    }

    @Override
    public String toString() {
        return "ID:          " + getId() +
                "\nAction:      " + getAction().getDisplayName() +
                "\nAt:          " + getHappenedAt().toString(
                ISODateTimeFormat.dateHourMinuteSecondMillis().withZoneUTC()) + " (UTC)" +
                "\nOutcome:     " + getOutcome().getDisplayName() +
                "\nType:        " + getTypes();
    }
}
