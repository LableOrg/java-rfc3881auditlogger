/*
 * Copyright (C) ${project.inceptionYear} Lable (info@lable.nl)
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

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;

/**
 * JSON serialization and deserialization for types related to this project.
 */
public class RFC3881Module extends SimpleModule {
    private static final long serialVersionUID = 1844370161677549243L;

    public RFC3881Module(boolean compact) {
        super("IETF/RFC 3881 JSON module", new Version(1, 0, 0, null, null, null));

        if (compact) {
            addSerializer(Referenceable.class, new CompactReferenceableSerializer());
        } else {
            addSerializer(Referenceable.class, new ReferenceableSerializer());
        }

        addDeserializer(CodeReference.class, new CodeReferenceDeserializer());
    }
}
