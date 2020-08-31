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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.rfc3881.auditlogger.api.Principal;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.lable.rfc3881.auditlogger.serialization.ObjectMapperFactory.getObjectMapper;

public class ObjectMapperFactoryTest {
    @Test
    public void objectMapperTest() throws JsonProcessingException {
        ObjectMapper objectMapper = getObjectMapper();

        TestObject testObject = new TestObject("a", null);

        System.out.println(objectMapper.writeValueAsString(testObject));
    }

    @Test
    public void singleValueAsListTest() throws IOException {
        ObjectMapper objectMapper = getObjectMapper();

        Principal principal = objectMapper.readValue(
                "{\"userId\":\"USER\", \"alternateUserId\": \"A\"}",
                Principal.class
        );

        assertThat(principal.getUserId(), is("USER"));
        assertThat(principal.getAlternateUserId(), containsInAnyOrder("A"));
    }

    @Test
    public void multiValueAsListTest() throws IOException {
        ObjectMapper objectMapper = getObjectMapper();

        Principal principal = objectMapper.readValue(
                "{\"userId\":\"USER\", " +
                "\"alternateUserId\": [\"A\", \"B\"]," +
                "\"name\": \"NAME\"," +
                "\"relevantRoles\": [{\"cs\":\"CS\",\"code\":\"CODE\"}]" +
                "}",
                Principal.class
        );

        assertThat(principal.getUserId(), is("USER"));
        assertThat(principal.getName(), is("NAME"));
        assertThat(principal.getRelevantRoles(), containsInAnyOrder(new CodeReference("CS", "CODE")));
        assertThat(principal.getAlternateUserId(), containsInAnyOrder("A", "B"));
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