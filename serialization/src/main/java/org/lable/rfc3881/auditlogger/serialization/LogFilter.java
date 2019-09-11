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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import org.lable.rfc3881.auditlogger.api.EntryPart;

import java.io.Serializable;

/**
 * Perform filtering on the properties written to JSON. In particular, leave out the {@code complete} field if set to
 * true (default value).
 */
public class LogFilter extends SimpleBeanPropertyFilter implements Serializable {
    @Override
    public void serializeAsField(Object pojo,
                                 JsonGenerator jgen,
                                 SerializerProvider provider,
                                 PropertyWriter writer) throws Exception {

        if (include(writer)) {
            if (writer.getName().equals("complete") && pojo instanceof EntryPart) {
                EntryPart entryPart = (EntryPart) pojo;
                if (!entryPart.isComplete()) {
                    writer.serializeAsField(pojo, jgen, provider);
                }
            } else {
                writer.serializeAsField(pojo, jgen, provider);
            }
        } else if (!jgen.canOmitFields()) {
            writer.serializeAsOmittedField(pojo, jgen, provider);
        }
    }
}
