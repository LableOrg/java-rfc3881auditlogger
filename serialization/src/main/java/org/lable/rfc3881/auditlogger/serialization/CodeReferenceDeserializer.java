package org.lable.rfc3881.auditlogger.serialization;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import org.lable.codesystem.codereference.CodeReference;

import java.io.IOException;

/**
 * Deserialize CodeReference representations from JSON.
 */
public class CodeReferenceDeserializer extends JsonDeserializer<CodeReference> {
    @Override
    public CodeReference deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        ObjectCodec oc = parser.getCodec();
        JsonNode node = oc.readTree(parser);

        // Required fields.
        if (!node.has("cs") || !node.has("code") || !node.has("dn")) {
            return null;
        }
        String codeSystem = node.get("cs").asText(null);
        String code = node.get("code").asText(null);
        String displayName = node.get("dn").asText(null);

        // Optional fields, can be null.
        String codeSystemName = node.has("csn") ? node.get("csn").asText(null) : null;
        String originalText = node.has("ot") ? node.get("ot").asText(null) : null;

        return new CodeReference(codeSystem, codeSystemName, code, displayName, originalText);
    }
}
