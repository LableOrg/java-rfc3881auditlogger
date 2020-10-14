/*
 * Copyright © 2015 Lable (info@lable.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lable.rfc3881.auditlogger.serialization;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;

import java.io.IOException;

public class CompactReferenceableSerializer extends JsonSerializer<Referenceable> {
    @Override
    public void serialize(Referenceable value, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        // Serialize CodeReference instead of whatever is backing Referenceable, to prevent the default serializer from
        // using the name of an enum implementing Referenceable.
        if (value instanceof CodeReference) {
            generator.writeStartObject();
            generator.writeStringField("cs", ((CodeReference) value).getCodeSystem());
            generator.writeStringField("code", ((CodeReference) value).getCode());
            generator.writeEndObject();
        } else {
            provider.defaultSerializeValue(value.toCodeReference(), generator);
        }
    }
}
