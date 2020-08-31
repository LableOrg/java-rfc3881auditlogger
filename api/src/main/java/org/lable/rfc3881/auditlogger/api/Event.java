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
package org.lable.rfc3881.auditlogger.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.lable.codesystem.codereference.Categorizable;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;
import org.lable.rfc3881.auditlogger.definition.rfc3881.EventAction;
import org.lable.rfc3881.auditlogger.definition.rfc3881.EventOutcome;
import org.lable.rfc3881.auditlogger.definition.rfc3881.eventtypes.AuditAdministrationEventType;
import org.lable.rfc3881.auditlogger.definition.rfc3881.eventtypes.SecurityAdministrationEventType;
import org.lable.rfc3881.auditlogger.definition.rfc3881.eventtypes.UserAccessEventType;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Audit event.
 * <p>
 * Defined in IETF/RFC 3881 §5.1. Event Identification.
 */
public class Event implements Serializable, Comparable<Event> {
    private static final long serialVersionUID = 3890563908827120988L;

    /* Required fields. */

    /**
     * Identifier for a specific audited event, e.g., a menu item, program, rule, policy, function code, application
     * name, or URL. It identifies the performed function.
     * <p>
     * IETF/RFC 3881 §5.1.1. Event ID.
     */
    final CodeReference id;

    /**
     * Timestamp for when the audit event took place in milliseconds since the Unix epoch.
     * <p>
     * IETF/RFC 3881 §5.1.3. Event Date/Time.
     */
    final long happenedAt;

    /**
     * Indicates whether the event succeeded or failed.
     * <p>
     * IETF/RFC 3881 §5.1.4. Event Outcome Indicator.
     */
    final CodeReference eventOutcome;

    /* Optional fields. */

    /**
     * Indicator for type of action performed during the event that generated the audit.
     * <p>
     * IETF/RFC 3881 §5.1.2. Event Action Code.
     */
    final CodeReference eventAction;

    /**
     * Identifier for the category of event.
     * <p>
     * IETF/RFC 3881 §5.1.5. Event Type Code.
     */
    final List<CodeReference> types;

    /**
     * Define an audit event.
     * <p>
     * The event-id may be domain-specific. A set of common generic events is provided with this library, but for a
     * lot of event types the event-id will be something defined within your projects.
     * <p>
     * RFC 3881 lists three categories of event types; these are defined in {@link AuditAdministrationEventType},
     * {@link SecurityAdministrationEventType}, and {@link UserAccessEventType}. Add any number of relevant event
     * types to an event to help categorize it.
     *
     * @param id           Identifier.
     * @param eventAction  Audit action.
     * @param happenedAt   When this event took place.
     * @param eventOutcome Outcome of the event.
     * @param types        Event classification.
     * @see AuditAdministrationEventType
     * @see SecurityAdministrationEventType
     * @see UserAccessEventType
     */
    public Event(Referenceable id,
                 Referenceable eventAction,
                 long happenedAt,
                 Referenceable eventOutcome,
                 List<? extends Referenceable> types) {
        this.id = id.toCodeReference();
        this.eventAction = eventAction.toCodeReference();
        this.happenedAt = happenedAt;
        this.eventOutcome = eventOutcome.toCodeReference();
        this.types = types == null ? Collections.emptyList() : types.stream()
                .map(Referenceable::toCodeReference)
                .collect(Collectors.toList());
    }

    @JsonCreator
    private static Event json(@JsonProperty("id") CodeReference id,
                              @JsonProperty("happenedAt") long happenedAt,
                              @JsonProperty("outcome") CodeReference eventOutcome,
                              @JsonProperty("action") CodeReference eventAction,
                              @JsonProperty("types") List<CodeReference> types) {
        return new Event(id, eventAction, happenedAt, eventOutcome, types);
    }

    /**
     * Define an audit event that took place just now.
     * <p>
     * The event-id may be domain-specific. A set of common generic events is provided with this library, but for a
     * lot of event types the event-id will be something defined within your projects.
     * <p>
     * RFC 3881 lists three categories of event types; these are defined in {@link AuditAdministrationEventType},
     * {@link SecurityAdministrationEventType}, and {@link UserAccessEventType}. Add any number of relevant event
     * types to an event to help categorize it.
     *
     * @param id           Event identifier.
     * @param eventAction  Audit action.
     * @param eventOutcome Outcome of the event.
     * @param types        Event classification.
     * @see AuditAdministrationEventType
     * @see SecurityAdministrationEventType
     * @see UserAccessEventType
     */
    public Event(Referenceable id, EventAction eventAction, EventOutcome eventOutcome, Referenceable... types) {
        this(id, eventAction, System.currentTimeMillis(), eventOutcome, types);
    }

    /**
     * Define an audit event with a {@link Categorizable} event-id. This type of event-id knows which event types it is
     * classified under.
     * <p>
     * The event-id may be domain-specific. A set of common generic events is provided with this library, but for a
     * lot of event types the event-id will be something defined within your projects.
     *
     * @param id           Identifier with event type association.
     * @param eventAction  Audit action.
     * @param eventOutcome Outcome of the event.
     */
    public Event(Categorizable id, EventAction eventAction, EventOutcome eventOutcome) {
        this(id, eventAction, System.currentTimeMillis(), eventOutcome);
    }

    /**
     * Define an audit event.
     * <p>
     * The event-id may be domain-specific. A set of common generic events is provided with this library, but for a
     * lot of event types the event-id will be something defined within your projects.
     * <p>
     * RFC 3881 lists three categories of event types; these are defined in {@link AuditAdministrationEventType},
     * {@link SecurityAdministrationEventType}, and {@link UserAccessEventType}. Add any number of relevant event
     * types to an event to help categorize it.
     *
     * @param id           Identifier.
     * @param eventAction  Audit action.
     * @param happenedAt   When this event took place.
     * @param eventOutcome Outcome of the event.
     * @param types        Event classification.
     * @see AuditAdministrationEventType
     * @see SecurityAdministrationEventType
     * @see UserAccessEventType
     */
    public Event(Referenceable id,
                 EventAction eventAction,
                 long happenedAt,
                 EventOutcome eventOutcome,
                 Referenceable... types) {
        this(id, eventAction, happenedAt, eventOutcome, Arrays.asList(types));
    }

    /**
     * Define an audit event with a {@link Categorizable} event-id. This type of event-id knows which event types it is
     * classified under.
     * <p>
     * The event-id may be domain-specific. A set of common generic events is provided with this library, but for a
     * lot of event types the event-id will be something defined within your projects.
     *
     * @param id           Identifier with event type association.
     * @param eventAction  Audit action.
     * @param happenedAt   When this event took place.
     * @param eventOutcome Outcome of the event.
     */
    public Event(Categorizable id, EventAction eventAction, long happenedAt, EventOutcome eventOutcome) {
        this(id, eventAction, happenedAt, eventOutcome, id.categorizedUnder());
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
    public CodeReference getAction() {
        return eventAction;
    }

    /**
     * @return When the event happened.
     */
    public long getHappenedAt() {
        return happenedAt;
    }

    /**
     * @return Outcome of the event (success or failure).
     */
    public CodeReference getOutcome() {
        return eventOutcome;
    }

    public List<CodeReference> getTypes() {
        return types;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Event that = (Event) other;
        return this.happenedAt == that.happenedAt &&
                Objects.equals(this.id, that.id) &&
                Objects.equals(this.eventOutcome, that.eventOutcome) &&
                Objects.equals(this.eventAction, that.eventAction) &&
                Objects.equals(this.types, that.types);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, happenedAt, eventOutcome, eventAction, types);
    }

    @Override
    public int compareTo(Event other) {
        if (other == null) return -1;
        if (this == other) return 0;

        return Long.compare(this.getHappenedAt(), other.getHappenedAt());
    }

    @Override
    public String toString() {
        TimeZone timeZone = TimeZone.getTimeZone(ZoneId.systemDefault());
        Instant at = Instant.ofEpochMilli(happenedAt);
        boolean dst = ZoneId.systemDefault().getRules().isDaylightSavings(at);

        return "ID:          " + getId() +
                "\nAction:      " + EventAction.fromReferenceable(getAction())
                        .map(EventAction::getDisplayName)
                        .orElse(getAction().getCode()) +
                "\nAt:          " + DateTimeFormatter.ISO_INSTANT.format(at) +
                " (" + DateTimeFormatter.ofPattern("d MMM YYYY, HH:mm:ss")
                .withZone(ZoneId.systemDefault())
                .format(at) + ", " + timeZone.getDisplayName(dst, TimeZone.SHORT) + ")" +
                "\nOutcome:     " + EventOutcome.fromReferenceable(getOutcome())
                        .map(EventOutcome::getDisplayName)
                        .orElse(getOutcome().getCode()) +
                "\nType:        " + getTypes();
    }
}
