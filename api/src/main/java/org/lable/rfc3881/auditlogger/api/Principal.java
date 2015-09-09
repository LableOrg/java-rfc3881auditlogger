package org.lable.rfc3881.auditlogger.api;


import org.lable.codesystem.codereference.Referenceable;

import java.util.Arrays;
import java.util.List;

import static org.lable.rfc3881.auditlogger.api.util.ParameterValidation.collectionMayNotBeNullOrEmpty;
import static org.lable.rfc3881.auditlogger.api.util.ParameterValidation.parameterMayNotBeNull;

/**
 * Security identity of a user or automated process. This includes the relevant security roles for the action that
 * was performed.
 */
public class Principal {
    /* Required fields. */

    /**
     * Unique identifier for the user actively participating in the event.
     * <p>
     * IETF/RFC 3881 §5.2.1. User ID.
     */
    final String userId;

    /* Optional fields. */

    /**
     * Alternative unique identifiers for the user.
     * <p>
     * IETF/RFC 3881 §5.2.2.  Alternative User ID.
     */
    final String alternateUserId;

    /**
     * The human-meaningful name for the user.
     * <p>
     * IETF/RFC 3881 §5.2.3. User Name.
     */
    final String name;

    /**
     * Specification of the role(s) the user plays when performing the event, as assigned in role-based access
     * control security.
     * <p>
     * IETF/RFC 3881 §5.2.5. Role ID Code.
     */
    final List<Referenceable> relevantRoles;

    /**
     * Define a principal.
     *
     * @param userId        Unique user ID.
     * @param relevantRoles List of roles relevant for the action performed.
     */
    public Principal(String userId, Referenceable... relevantRoles) {
        this(userId, null, null, relevantRoles);
    }

    /**
     * Define a principal.
     *
     * @param userId          Unique user ID.
     * @param alternateUserId Synonymous user ID.
     * @param name            Human readable name.
     * @param relevantRoles   List of roles relevant for the action performed.
     */
    public Principal(String userId, String alternateUserId, String name, Referenceable... relevantRoles) {
        parameterMayNotBeNull("userId", userId);

        this.userId = userId;
        this.alternateUserId = alternateUserId;
        this.name = name;
        this.relevantRoles = Arrays.asList(relevantRoles);
    }

    public String getUserId() {
        return userId;
    }

    public List<Referenceable> getRelevantRoles() {
        return relevantRoles;
    }

    public String getName() {
        return name;
    }

    public String getAlternateUserId() {
        return alternateUserId;
    }

    @Override
    public String toString() {
        return "ID:          " + getUserId() +
                (getName() == null || getName().isEmpty() ? "" : "\nName:        " + getName()) +
                (getAlternateUserId() == null ? "" : "\nAlt ID:      " + getAlternateUserId()) +
                "\nRoles:       " + (getRelevantRoles() == null ? "[]" : getRelevantRoles());
    }
}
