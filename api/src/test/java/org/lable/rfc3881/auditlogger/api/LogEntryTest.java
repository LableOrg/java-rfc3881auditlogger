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
package org.lable.rfc3881.auditlogger.api;

import org.junit.Test;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.rfc3881.auditlogger.definition.rfc3881.*;

import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class LogEntryTest {
    @Test
    public void toStringTest() {
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
                                new ParticipantObject.Detail(
                                        new CodeReference("detail", "DT1", "Detail 1"),
                                        "Detail"
                                ))
                ),
                new CodeReference("version", "1", "1")
        );

        // Test that there are no NPEs and such.
        assertThat(logEntry.toString(), is(not("")));

        System.out.println(logEntry);
    }
}