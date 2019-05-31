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
        if (!node.has("cs") || !node.has("code")) {
            return null;
        }
        String codeSystem = node.get("cs").asText(null);
        String code = node.get("code").asText(null);

        // Optional fields, can be null.
        String codeSystemName = node.has("csn") ? node.get("csn").asText(null) : null;
        String originalText = node.has("ot") ? node.get("ot").asText(null) : null;
        String displayName = node.has("dn") ? node.get("dn").asText(null) : null;

        return new CodeReference(codeSystem, codeSystemName, code, displayName, originalText);
    }
}
