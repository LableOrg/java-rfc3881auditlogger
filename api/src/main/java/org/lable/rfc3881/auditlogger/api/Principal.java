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
package org.lable.rfc3881.auditlogger.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Identifiable;
import org.lable.codesystem.codereference.Referenceable;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.lable.rfc3881.auditlogger.api.util.ParameterValidation.parameterMayNotBeNull;

/**
 * Security identity of a user or automated process. This includes the relevant security roles for the action that
 * was performed.
 */
public class Principal implements Identifiable, Serializable {
    private static final long serialVersionUID = -7367595173448586271L;

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
    final List<String> alternateUserId;

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
    final List<CodeReference> relevantRoles;

    /**
     * Define a principal.
     *
     * @param userId          Unique user ID.
     * @param alternateUserId Synonymous user IDs.
     * @param name            Human readable name.
     * @param relevantRoles   List of roles relevant for the action performed.
     */
    public Principal(String userId,
                     List<String> alternateUserId,
                     String name,
                     List<? extends Referenceable> relevantRoles) {
        parameterMayNotBeNull("userId", userId);

        this.userId = userId;
        this.alternateUserId = alternateUserId == null ? Collections.emptyList() : alternateUserId;
        this.name = name;
        this.relevantRoles = relevantRoles == null ? Collections.emptyList() : relevantRoles.stream()
                .map(Referenceable::toCodeReference)
                .collect(Collectors.toList());
    }

    @JsonCreator
    private static Principal json(@JsonProperty("userId") String userId,
                                  @JsonProperty("alternateUserId") List<String> alternateUserId,
                                  @JsonProperty("name") String name,
                                  @JsonProperty("relevantRoles") List<CodeReference> relevantRoles) {
        return new Principal(userId, alternateUserId, name, relevantRoles);
    }

    /**
     * Define a principal.
     *
     * @param userId        Unique user ID.
     * @param relevantRoles List of roles relevant for the action performed.
     */
    public Principal(String userId, Referenceable... relevantRoles) {
        this(userId, Collections.emptyList(), null, relevantRoles);
    }

    /**
     * Define a principal.
     *
     * @param userId          Unique user ID.
     * @param alternateUserId Synonymous user IDs.
     * @param name            Human readable name.
     * @param relevantRoles   List of roles relevant for the action performed.
     */
    public Principal(String userId, List<String> alternateUserId, String name, Referenceable... relevantRoles) {
        this(userId, alternateUserId, name, Arrays.asList(relevantRoles));
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
        this(userId, Collections.singletonList(alternateUserId), name, relevantRoles);
    }

    public String getUserId() {
        return userId;
    }

    public List<CodeReference> getRelevantRoles() {
        return relevantRoles;
    }

    public String getName() {
        return name;
    }

    public List<String> getAlternateUserId() {
        return alternateUserId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> identifyingStack() {
        return Collections.singletonList(getUserId());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Principal that = (Principal) other;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(alternateUserId, that.alternateUserId) &&
                Objects.equals(name, that.name) &&
                Objects.equals(relevantRoles, that.relevantRoles);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, alternateUserId, name, relevantRoles);
    }

    @Override
    public String toString() {
        return "ID:          " + getUserId() +
                (getName() == null || getName().isEmpty() ? "" : "\nName:        " + getName()) +
                (getAlternateUserId().isEmpty()
                        ? ""
                        : "\nAlt ID:      " + String.join("; ", getAlternateUserId())) +
                "\nRoles:       " + (getRelevantRoles() == null ? "[]" : getRelevantRoles());
    }
}
