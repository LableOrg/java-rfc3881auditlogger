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

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.junit.Test;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.rfc3881.auditlogger.api.*;
import org.lable.rfc3881.auditlogger.definition.rfc3881.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class RFC3881ModuleTest {
    static ObjectMapper testingObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new RFC3881Module(true));
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        SimpleFilterProvider filterProvider = new SimpleFilterProvider()
                .addFilter("logFilter", new LogFilter());
        objectMapper.setFilterProvider(filterProvider);

        return objectMapper;
    }

    @Test
    public void moduleTest() throws JsonProcessingException {
        ObjectMapper objectMapper = testingObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        LogEntry logEntry = new LogEntry(
                new Event(new CodeReference("events", "logon", "log-on"), EventAction.EXECUTE, EventOutcome.SUCCESS),
                new Principal("bob", (String) null, "Bob Jones", new CodeReference("roles", "user", "authenticated user")),
                null,
                null,
                NetworkAccessPoint.byIPAddress("127.0.0.1"),
                Collections.singletonList(
                        new AuditSource("servercluster1", "tomcat1", AuditSourceType.WEB_SERVER_PROCESS)),
                Collections.singletonList(
                        new ParticipantObject("bob",
                                ParticipantObjectType.PERSON,
                                ParticipantObjectIDType.USER_IDENTIFIER,
                                ParticipantObjectTypeRole.USER,
                                DataLifeCycle.ACCESS_OR_USE,
                                new CodeReference("sensitivity", "TOPSECRET", "Quite secret"),
                                "Bob Jones",
                                "TEST",
                                new Detail(
                                        new CodeReference("detail", "DT1", "Detail 1"),
                                        "DETAIL"
                                ))
                ),
                null,
                new CodeReference("version", "1", "1")
        );

        System.out.println(objectMapper.writeValueAsString(logEntry));
    }

    @Test
    public void eventTest() throws IOException {
        Event event = new Event(
                new CodeReference("id", "1"),
                EventAction.READ,
                12L,
                EventOutcome.SUCCESS,
                Arrays.asList(
                        new CodeReference("t", "A"),
                        new CodeReference("t", "B")
                )
        );

        ObjectMapper objectMapper = testingObjectMapper();

        String json = objectMapper.writeValueAsString(event);

        System.out.println(json);

        Event eventOut = objectMapper.readValue(json, Event.class);

        assertThat(event, is(eventOut));

        System.out.println(event);
        System.out.println(eventOut);
    }

    @Test
    public void principalTest() throws IOException {
        Principal principal = new Principal(
                "id",
                Arrays.asList("a", "b"),
                "name",
                new CodeReference("role", "R")
        );

        ObjectMapper objectMapper = testingObjectMapper();

        String json = objectMapper.writeValueAsString(principal);

        Principal principalOut = objectMapper.readValue(json, Principal.class);

        assertThat(principal, is(principalOut));
    }

    @Test
    public void networkAccessPointTest() throws IOException {
        NetworkAccessPoint accessPoint = NetworkAccessPoint.byIPAddress("10.0.0.1");

        ObjectMapper objectMapper = testingObjectMapper();

        String json = objectMapper.writeValueAsString(accessPoint);

        NetworkAccessPoint accessPointOut = objectMapper.readValue(json, NetworkAccessPoint.class);

        assertThat(accessPoint, is(accessPointOut));
    }

    @Test
    public void auditSourceTest() throws IOException {
        AuditSource auditSource = new AuditSource("site-id", "id");

        ObjectMapper objectMapper = testingObjectMapper();

        String json = objectMapper.writeValueAsString(auditSource);

        AuditSource auditSourceOut = objectMapper.readValue(json, AuditSource.class);

        assertThat(auditSource, is(auditSourceOut));
    }

    @Test
    public void participantObjectTest() throws IOException {
        ParticipantObject object = new ParticipantObject(
                "id",
                ParticipantObjectType.SYSTEM_OBJECT,
                new CodeReference("cr", "id"),
                ParticipantObjectTypeRole.JOB,
                DataLifeCycle.ACCESS_OR_USE,
                new CodeReference("sens", "very"),
                "name",
                "GET",
                new Detail(new CodeReference("dt", "d"), "XXX")
        );

        ObjectMapper objectMapper = testingObjectMapper();

        String json = objectMapper.writeValueAsString(object);

        ParticipantObject objectOut = objectMapper.readValue(json, ParticipantObject.class);

        assertThat(objectOut, is(object));
    }

    @Test
    public void participantObjectIncompleteTest() throws IOException {
        ParticipantObject object = new ParticipantObject(
                "id",
                ParticipantObjectType.SYSTEM_OBJECT,
                new CodeReference("cr", "id"),
                ParticipantObjectTypeRole.JOB,
                DataLifeCycle.ACCESS_OR_USE,
                new CodeReference("sens", "very"),
                "name",
                "GET",
                false,
                new Detail(new CodeReference("dt", "d"), "XXX")
        );

        ObjectMapper objectMapper = testingObjectMapper();

        String json = objectMapper.writeValueAsString(object);

        ParticipantObject objectOut = objectMapper.readValue(json, ParticipantObject.class);

        assertThat(objectOut, is(object));
    }

    @Test
    public void participantObjectExplicitCompleteTest() throws IOException {
        ParticipantObject object = new ParticipantObject(
                "id",
                ParticipantObjectType.SYSTEM_OBJECT,
                new CodeReference("cr", "id"),
                ParticipantObjectTypeRole.JOB,
                DataLifeCycle.ACCESS_OR_USE,
                null,
                null,
                null
        );

        String json = "{\n" +
                "  \"id\" : \"id\",\n" +
                "  \"type\" : {\n" +
                "    \"cs\" : \"IETF/RFC3881.5.5.1\",\n" +
                "    \"code\" : \"2\"\n" +
                "  },\n" +
                "  \"idType\" : {\n" +
                "    \"cs\" : \"cr\",\n" +
                "    \"code\" : \"id\"\n" +
                "  },\n" +
                "  \"typeRole\" : {\n" +
                "    \"cs\" : \"IETF/RFC3881.5.5.2\",\n" +
                "    \"code\" : \"20\"\n" +
                "  },\n" +
                "  \"dataLifeCycle\" : {\n" +
                "    \"cs\" : \"IETF/RFC3881.5.5.3\",\n" +
                "    \"code\" : \"6\"\n" +
                "  },\n" +
                "  \"sensitivity\" : null,\n" +
                "  \"name\" : null,\n" +
                "  \"query\" : null,\n" +
                "  \"complete\" : true,\n" +
                "  \"details\" : [ ]\n" +
                "}";

        ObjectMapper objectMapper = testingObjectMapper();
        ParticipantObject objectOut = objectMapper.readValue(json, ParticipantObject.class);

        assertThat(objectOut, is(object));
    }
}