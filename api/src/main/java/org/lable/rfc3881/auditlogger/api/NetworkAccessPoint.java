package org.lable.rfc3881.auditlogger.api;

import org.lable.codesystem.codereference.Identifiable;
import org.lable.rfc3881.auditlogger.definition.rfc3881.NetworkAccessPointType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lable.rfc3881.auditlogger.api.util.ParameterValidation.parameterMayNotBeNull;

/**
 * Identifies the logical network location for application activity.
 * <p>
 * Defined in IETF/RFC 3881 §5.3. Network Access Point Identification.
 */
public class NetworkAccessPoint implements Identifiable {
    /**
     * An identifier for the type of network access point that originated the audit event.
     * <p>
     * IETF/RFC 3881 §5.3.1. Network Access Point Type Code.
     */
    private final NetworkAccessPointType type;

    /**
     * An identifier for the network access point of the user device for the audit event.  This could be a device id, IP
     * address, or some other identifier associated with a device.
     * <p>
     * IETF/RFC 3881 §5.3.2. Network Access Point ID.
     */
    private final String id;

    NetworkAccessPoint(NetworkAccessPointType type, String id) {
        this.type = type;
        this.id = id;
    }

    /**
     * Define a network access point by its hostname.
     *
     * @param hostName Hostname.
     * @return Network access point definition.
     */
    public static NetworkAccessPoint byHostName(String hostName) {
        parameterMayNotBeNull("hostName", hostName);
        return new NetworkAccessPoint(NetworkAccessPointType.MACHINE_NAME, hostName);
    }

    /**
     * Define a network access point by its IP address.
     *
     * @param ipAddress IP address.
     * @return Network access point definition.
     */
    public static NetworkAccessPoint byIPAddress(String ipAddress) {
        parameterMayNotBeNull("ipAddress", ipAddress);
        return new NetworkAccessPoint(NetworkAccessPointType.IP_ADDRESS, ipAddress);
    }

    /**
     * Define a network access point by its corresponding telephone number.
     *
     * @param telephoneNumber Telephone number.
     * @return Network access point definition.
     */
    public static NetworkAccessPoint byTelephoneNumber(String telephoneNumber) {
        parameterMayNotBeNull("telephoneNumber", telephoneNumber);
        return new NetworkAccessPoint(NetworkAccessPointType.TELEPHONE_NUMBER, telephoneNumber);
    }

    public NetworkAccessPointType getType() {
        return type;
    }

    public String getId() {
        return id;
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
    public String toString() {
        return "ID:          " + getId() +
                "\nType:        " + getType();
    }
}
