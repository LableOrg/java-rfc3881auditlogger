/**
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
package org.lable.rfc3881.auditlogger.adapter.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HConnection;
import org.apache.hadoop.hbase.client.HConnectionManager;
import org.joda.time.Instant;
import org.junit.Ignore;
import org.junit.Test;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;
import org.lable.rfc3881.auditlogger.api.*;
import org.lable.rfc3881.auditlogger.definition.rfc3881.*;

import java.io.IOException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.lable.rfc3881.auditlogger.adapter.hbase.HBaseAdapter.referenceableToBytes;

public class HBaseAdapterTest {
    @Test
    @Ignore
    public void recordTest() throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "tzka,tzkb,tzkc");
        try (HConnection hConnection = HConnectionManager.createConnection(conf)) {

            AuditLogAdapter auditLogAdapter = new HBaseAdapter(hConnection, "audit:audit_trail_by_event", "a");

            Instant instant = Instant.now();
            for (int i = 0; i < 1; i++) {
                instant = instant.plus(1);
                LogEntry logEntry = new LogEntry(
                        new Event(new CodeReference("events", "logon", "log-on"),
                                EventAction.EXECUTE, instant, EventOutcome.SUCCESS),
                        new Principal("bob", null, "Bob Jones", new CodeReference("roles", "user", "authenticated user")),
                        new Principal("john", null, "John Jones", new CodeReference("roles", "user", "authenticated user")),
                        Arrays.asList(
                                new Principal("alice", null, "Alice Jones",
                                        new CodeReference("roles", "user", "authenticated user")),
                                new Principal("claire", null, "Claire Jones",
                                        new CodeReference("roles", "user", "authenticated user"))
                        ),
                        NetworkAccessPoint.byIPAddress("127.0.0.1"),
                        Arrays.asList(
                                new AuditSource("servercluster1", "tomcat1", AuditSourceType.WEB_SERVER_PROCESS),
                                new AuditSource("servercluster1", "authserver", AuditSourceType.SECURITY_SERVER)
                        ),
                        Arrays.asList(
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
                                        )),
                                new ParticipantObject("test",
                                        ParticipantObjectType.SYSTEM_OBJECT,
                                        ParticipantObjectIDType.REPORT_NAME,
                                        ParticipantObjectTypeRole.DATA_REPOSITORY,
                                        DataLifeCycle.ACCESS_OR_USE,
                                        null,
                                        "Test",
                                        null)
                        ),
                        new CodeReference("version", "1", "1")
                );

                auditLogAdapter.record(logEntry);
            }
        }
    }

    @Test
    public void referenceableToBytesTest() {
        final Referenceable codeReference = new CodeReference("CS", "00", "Zeroes");
        final byte[] template = "CS00".getBytes();
        final byte[] expected = new byte[]{template[0], template[1], 0x0, template[2], template[3]};

        assertThat(referenceableToBytes(codeReference), is(expected));
    }
}