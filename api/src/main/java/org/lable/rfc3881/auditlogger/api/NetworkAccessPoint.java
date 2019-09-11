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
import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Identifiable;
import org.lable.codesystem.codereference.Referenceable;
import org.lable.rfc3881.auditlogger.definition.rfc3881.NetworkAccessPointType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lable.rfc3881.auditlogger.api.util.ParameterValidation.parameterMayNotBeNull;

/**
 * Identifies the logical network location for application activity.
 * <p>
 * Defined in IETF/RFC 3881 §5.3. Network Access Point Identification.
 */
@JsonFilter("logFilter")
public class NetworkAccessPoint implements EntryPart, Identifiable, Serializable {
    private static final long serialVersionUID = 5288045572514508815L;

    /**
     * An identifier for the type of network access point that originated the audit event.
     * <p>
     * IETF/RFC 3881 §5.3.1. Network Access Point Type Code.
     */
    private final CodeReference type;

    /**
     * An identifier for the network access point of the user device for the audit event.  This could be a device id, IP
     * address, or some other identifier associated with a device.
     * <p>
     * IETF/RFC 3881 §5.3.2. Network Access Point ID.
     */
    private final String id;

    /**
     * Mark this log entry part as complete or in need of further refinement further down the processing chain.
     */
    private final boolean complete;

    @JsonCreator
    NetworkAccessPoint(@JsonProperty("type") CodeReference type,
                       @JsonProperty("id") String id,
                       @JsonProperty("complete") Boolean complete) {
        this.type = type;
        this.id = id;
        this.complete = complete == null || complete;
    }

    NetworkAccessPoint(Referenceable type,
                       String id,
                       Boolean complete) {
        this(type.toCodeReference(), id, complete);
    }

    /**
     * Define a network access point by its hostname.
     *
     * @param hostName Hostname.
     * @return Network access point definition.
     */
    public static NetworkAccessPoint byHostName(String hostName) {
        parameterMayNotBeNull("hostName", hostName);
        return new NetworkAccessPoint(NetworkAccessPointType.MACHINE_NAME, hostName, true);
    }

    /**
     * Define a network access point by its hostname.
     *
     * @param hostName Hostname.
     * @param complete Mark this data as complete, or in need of further refinement.
     * @return Network access point definition.
     */
    public static NetworkAccessPoint byHostName(String hostName, boolean complete) {
        parameterMayNotBeNull("hostName", hostName);
        return new NetworkAccessPoint(NetworkAccessPointType.MACHINE_NAME, hostName, complete);
    }

    /**
     * Define a network access point by its IP address.
     *
     * @param ipAddress IP address.
     * @return Network access point definition.
     */
    public static NetworkAccessPoint byIPAddress(String ipAddress) {
        parameterMayNotBeNull("ipAddress", ipAddress);
        return new NetworkAccessPoint(NetworkAccessPointType.IP_ADDRESS, ipAddress, true);
    }

    /**
     * Define a network access point by its IP address.
     *
     * @param ipAddress IP address.
     * @param complete  Mark this data as complete, or in need of further refinement.
     * @return Network access point definition.
     */
    public static NetworkAccessPoint byIPAddress(String ipAddress, boolean complete) {
        parameterMayNotBeNull("ipAddress", ipAddress);
        return new NetworkAccessPoint(NetworkAccessPointType.IP_ADDRESS, ipAddress, complete);
    }

    /**
     * Define a network access point by its corresponding telephone number.
     *
     * @param telephoneNumber Telephone number.
     * @return Network access point definition.
     */
    public static NetworkAccessPoint byTelephoneNumber(String telephoneNumber) {
        parameterMayNotBeNull("telephoneNumber", telephoneNumber);
        return new NetworkAccessPoint(NetworkAccessPointType.TELEPHONE_NUMBER, telephoneNumber, true);
    }

    /**
     * Define a network access point by its corresponding telephone number.
     *
     * @param telephoneNumber Telephone number.
     * @param complete        Mark this data as complete, or in need of further refinement.
     * @return Network access point definition.
     */
    public static NetworkAccessPoint byTelephoneNumber(String telephoneNumber, boolean complete) {
        parameterMayNotBeNull("telephoneNumber", telephoneNumber);
        return new NetworkAccessPoint(NetworkAccessPointType.TELEPHONE_NUMBER, telephoneNumber, complete);
    }

    public CodeReference getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isComplete() {
        return complete;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> identifyingStack() {
        List<String> parts = new ArrayList<>(getType().toCodeReference().identifyingStack());
        parts.add(getId());
        return parts;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        NetworkAccessPoint that = (NetworkAccessPoint) other;
        return Objects.equals(this.type, that.type) &&
                this.complete == that.complete &&
                Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, complete, id);
    }

    @Override
    public String toString() {
        return "ID:          " + getId() +
                "\nType:        " + getType() +
                (complete ? "" : "\nINCOMPLETE");
    }
}
