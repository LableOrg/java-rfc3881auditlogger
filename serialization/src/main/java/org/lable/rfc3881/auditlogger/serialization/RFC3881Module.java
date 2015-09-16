package org.lable.rfc3881.auditlogger.serialization;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;

/**
 * JSON serialization and deserialization for types related to this project.
 */
public class RFC3881Module extends SimpleModule {
    private static final long serialVersionUID = 1844370161677549243L;

    public RFC3881Module() {
        super("IETF/RFC 3881 JSON module", new Version(1, 0, 0, null, null, null));

        addSerializer(Referenceable.class, new ReferenceableSerializer());
        addDeserializer(CodeReference.class, new CodeReferenceDeserializer());
    }
}
