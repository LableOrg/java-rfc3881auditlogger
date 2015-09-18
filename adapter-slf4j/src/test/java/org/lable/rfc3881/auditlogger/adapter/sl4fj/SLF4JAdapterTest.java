package org.lable.rfc3881.auditlogger.adapter.sl4fj;

import org.junit.Test;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.rfc3881.auditlogger.api.*;
import org.lable.rfc3881.auditlogger.definition.rfc3881.*;
import org.slf4j.Logger;
import org.slf4j.MarkerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collections;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SLF4JAdapterTest {
    @Test
    public void recordTest() throws NoSuchFieldException, IllegalAccessException, IOException {
        AuditLogAdapter auditLogAdapter = new SLF4JAdapter();
        Logger mockLogger = setMockLogger((SLF4JAdapter) auditLogAdapter);

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
                ),
                new CodeReference("version", "1", "1")
        );

        auditLogAdapter.record(logEntry);

        verify(mockLogger).info(eq(MarkerFactory.getMarker("AUDIT")), anyString());
    }

    // Set up a mock logger in the 'final' logger field.
    private Logger setMockLogger(SLF4JAdapter slf4JAdapter) throws NoSuchFieldException, IllegalAccessException {
        Logger mockLogger = mock(Logger.class);
        Field field = slf4JAdapter.getClass().getDeclaredField("logger");
        field.setAccessible(true);
        // Remove final modifier from field.
        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(slf4JAdapter, mockLogger);
        return mockLogger;
    }
}