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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Creates new instances of {@link ObjectMapper}.
 */
public class ObjectMapperFactory {
    /**
     * Configures an {@link ObjectMapper} instance with the serialization strategies needed for this project.
     *
     * @return An object-mapper.
     */
    public static ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new RFC3881Module(true));
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        return objectMapper;
    }
}
