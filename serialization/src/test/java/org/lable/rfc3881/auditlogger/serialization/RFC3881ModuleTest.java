package org.lable.rfc3881.auditlogger.serialization;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.junit.Test;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.rfc3881.auditlogger.api.*;
import org.lable.rfc3881.auditlogger.definition.rfc3881.*;

import java.util.Collections;

public class RFC3881ModuleTest {
    @Test
    public void moduleTest() throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new RFC3881Module());
        objectMapper.registerModule(new JodaModule());
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        LogEntry logEntry = new LogEntry(
                new Event(new CodeReference("events", "logon", "log-on"), EventAction.EXECUTE, EventOutcome.SUCCESS),
                new Principal("bob", null, "Bob Jones", new CodeReference("roles", "user", "authenticated user")),
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
                                "TEST".getBytes(),
                                new ParticipantObject.Detail(
                                        new CodeReference("detail", "DT1", "Detail 1"),
                                        new byte[0]
                                ))
                )
        );

        System.out.println(objectMapper.writeValueAsString(logEntry));
    }
}