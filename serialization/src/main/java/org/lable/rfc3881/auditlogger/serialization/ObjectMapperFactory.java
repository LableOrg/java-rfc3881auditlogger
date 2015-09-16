package org.lable.rfc3881.auditlogger.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.joda.JodaModule;

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
        objectMapper.registerModule(new RFC3881Module());
        objectMapper.registerModule(new JodaModule());
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

        return objectMapper;
    }
}
