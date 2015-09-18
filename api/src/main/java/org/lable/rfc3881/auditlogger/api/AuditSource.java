package org.lable.rfc3881.auditlogger.api;

import org.lable.codesystem.codereference.Identifiable;
import org.lable.codesystem.codereference.Referenceable;
import org.lable.rfc3881.auditlogger.definition.rfc3881.AuditSourceType;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.lable.rfc3881.auditlogger.api.util.ParameterValidation.parameterMayNotBeNull;

/**
 * An audit source is a distinct participating process, service, or server in an audit event. For distributed services
 * these may be used to identify relevant participants in an event. These include, for example, the application server
 * handling a request from a webapp, and an authentication server or some remote service performing tasks related to
 * the audit event.
 * <p/>
 * Defined in RFC 3881 §5.4. Audit Source Identification.
 */
public class AuditSource implements Identifiable, Serializable {
    private static final long serialVersionUID = 1287102005812178285L;

    /* Required fields. */

    /**
     * Identifier of the source where the event originated.
     * <p>
     * IETF/RFC 3881 §5.4.2. Audit Source ID.
     */
    final String id;

    /* Optional fields. */

    /**
     * Logical source location within the healthcare enterprise network, e.g., a hospital or other provider location
     * within a multi-entity provider group.
     * <p>
     * IETF/RFC 3881 §5.4.1. Audit Enterprise Site ID.
     */
    final String enterpriseSiteId;

    /**
     * Code specifying the type of source where event originated.
     * <p>
     * IETF/RFC 3881 §5.4.3. Audit Source Type Code
     */
    final List<Referenceable> typeCodes;

    /**
     * Define an audit source. The type codes defined in {@link AuditSourceType} can be used here to specify the type
     * of audit source, but custom code references may be used as well.
     *
     * @param enterpriseSiteId Logical identifier of your server cluster or network.
     * @param id               Identifier.
     * @param typeCodes        Type of audit source.
     */
    public AuditSource(String enterpriseSiteId, String id, Referenceable... typeCodes) {
        parameterMayNotBeNull("id", id);

        if (typeCodes == null || typeCodes.length == 0) {
            // Default to predefined "unknown" type if not set.
            this.typeCodes = Collections.singletonList((Referenceable) AuditSourceType.EXTERNAL_UNKNOWN_OR_OTHER);
        } else {
            this.typeCodes = Arrays.asList(typeCodes);
        }

        this.enterpriseSiteId = enterpriseSiteId;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getEnterpriseSiteId() {
        return enterpriseSiteId;
    }

    public List<Referenceable> getTypeCodes() {
        return typeCodes;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> identifyingStack() {
        return Arrays.asList(getEnterpriseSiteId(), getId());
    }

    @Override
    public String toString() {
        return "ID:          " + getId() +
                (getEnterpriseSiteId() == null ? "" : "\nSite ID:     " + getEnterpriseSiteId()) +
                "\nType:        " + (getTypeCodes() == null ? "[]" : getTypeCodes());
    }
}
