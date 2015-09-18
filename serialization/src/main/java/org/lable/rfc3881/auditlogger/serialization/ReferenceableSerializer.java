package org.lable.rfc3881.auditlogger.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;

import java.io.IOException;

/**
 * Serialize {@link Referenceable} implementations to JSON.
 */
public class ReferenceableSerializer extends JsonSerializer<Referenceable> {
    @Override
    public void serialize(Referenceable value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        // Serialize CodeReference instead of whatever is backing Referenceable, to prevent the default serializer from
        // using the name of an enum implementing Referenceable.
        if (value instanceof CodeReference) {
            generator.writeStartObject();
            writeField(generator, provider, "cs", ((CodeReference) value).getCodeSystem());
            writeField(generator, provider, "code", ((CodeReference) value).getCode());
            writeField(generator, provider, "csn", ((CodeReference) value).getCodeSystemName());
            writeField(generator, provider, "dn", ((CodeReference) value).getDisplayName());
            writeField(generator, provider, "ot", ((CodeReference) value).getOriginalText());
            generator.writeEndObject();
        } else {
            provider.defaultSerializeValue(value.toCodeReference(), generator);
        }
    }

    void writeField(JsonGenerator generator, SerializerProvider provider, String name, String value)
            throws IOException {
        switch (provider.getConfig().getSerializationInclusion()) {
            case NON_NULL:
            case NON_ABSENT:
                if (value == null) return;
                break;
            case NON_EMPTY:
                if (value == null || value.isEmpty()) return;
                break;
            default:
            case ALWAYS:
                // Allow.
                break;
        }

        generator.writeStringField(name, value);
    }
}
