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
            generator.writeStringField("cs", ((CodeReference) value).getCodeSystem());
            generator.writeStringField("code", ((CodeReference) value).getCode());
            generator.writeStringField("csn", ((CodeReference) value).getCodeSystemName());
            generator.writeStringField("dn", ((CodeReference) value).getDisplayName());
            generator.writeStringField("ot", ((CodeReference) value).getOriginalText());
            generator.writeEndObject();
        } else {
            provider.defaultSerializeValue(value.toCodeReference(), generator);
        }
    }
}
