package org.lable.rfc3881.auditlogger.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import static org.lable.rfc3881.auditlogger.serialization.ObjectMapperFactory.getObjectMapper;

public class ObjectMapperFactoryTest {
    @Test
    public void objectMapperTest() throws JsonProcessingException {
        ObjectMapper objectMapper = getObjectMapper();

        TestObject testObject = new TestObject("a", null);

        System.out.println(objectMapper.writeValueAsString(testObject));
    }

    static class TestObject {
        String fieldA;
        String fieldB;

        public TestObject(String fieldA, String fieldB) {
            this.fieldA = fieldA;
            this.fieldB = fieldB;
        }

        public String getFieldA() {
            return fieldA;
        }

        public String getFieldB() {
            return fieldB;
        }
    }
}