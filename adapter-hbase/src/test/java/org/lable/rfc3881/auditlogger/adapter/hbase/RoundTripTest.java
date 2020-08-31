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
package org.lable.rfc3881.auditlogger.adapter.hbase;

import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.rfc3881.auditlogger.api.*;
import org.lable.rfc3881.auditlogger.definition.rfc3881.*;

import java.io.IOException;
import java.time.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


public class RoundTripTest {
    private LocalHbase hbase;
    private AuditLogAdapter logAdapter;
    private AuditLogReader logReader;

    TableName AUDIT_TABLE = TableName.valueOf("ns", "audit");

    @Before
    public void before() throws Exception {
        AtomicLong uid = new AtomicLong();

        hbase = new LocalHbase();
        hbase.createNamespace("ns");
        hbase.getHBaseTestingUtility().createTable(AUDIT_TABLE, "a");

        logAdapter = new HBaseAdapter(
                hbase::getTable,
                () -> AUDIT_TABLE,
                () -> "a",
                () -> Bytes.toBytes(uid.getAndIncrement())
        );

        logReader = new HBaseReader(
                hbase::getTable,
                () -> AUDIT_TABLE,
                () -> "a"
        );
    }

    @After
    public void after() throws IOException {
        hbase.close();
    }

    @Test
    public void roundTripTest() throws IOException {
        Instant at = ZonedDateTime.of(
                LocalDateTime.of(2015, Month.APRIL, 3, 12, 0),
                ZoneId.of("Europe/Amsterdam")
        ).toInstant();

        LogEntry entryNullish = new LogEntry(
                new Event(
                        new CodeReference("system", "code"),
                        EventAction.READ,
                        at.toEpochMilli(),
                        EventOutcome.SUCCESS
                ),
                null,
                null,
                null,
                null,
                null,
                null,
                new CodeReference("version", "1.0")
        );

        LogEntry entryFullish = new LogEntry(
                new Event(
                        new CodeReference("system", "code"),
                        EventAction.READ,
                        at.toEpochMilli(),
                        EventOutcome.SUCCESS
                ),
                new Principal(
                        "user-id",
                        Arrays.asList("id-a", "id-b"),
                        "User",
                        Arrays.asList(
                                new CodeReference("r", "1"),
                                new CodeReference("r", "2")
                        )
                ),
                new Principal(
                        "user-id-del",
                        Arrays.asList("del-id-a", "del-id-b"),
                        "User Del",
                        new CodeReference("r", "1")
                ),
                Arrays.asList(
                        new Principal(
                                "user-id-part1",
                                Arrays.asList("part1-id-a", "part1-id-b"),
                                "User Participant 1",
                                new CodeReference("r", "1")
                        ),
                        new Principal(
                                "user-id-part2",
                                Arrays.asList("part2-id-a", "part2-id-b"),
                                "User Participant 2",
                                new CodeReference("r", "2")
                        )
                ),
                NetworkAccessPoint.byIPAddress("10.0.0.1"),
                Arrays.asList(
                        new AuditSource("site", "id1"),
                        new AuditSource("site", "id2", new CodeReference("t", "1")),
                        new AuditSource("site", "id3", new CodeReference("t", "3"), new CodeReference("t", "5"))
                ),
                Arrays.asList(
                        new ParticipantObject(
                                "id1",
                                ParticipantObjectType.SYSTEM_OBJECT,
                                new CodeReference("idtype", "t"),
                                ParticipantObjectTypeRole.JOB,
                                DataLifeCycle.ACCESS_OR_USE,
                                new CodeReference("sens", "very"),
                                "Some object 1",
                                "GET",
                                new ParticipantObject.Detail(new CodeReference("dt", "t"), "1"),
                                new ParticipantObject.Detail(new CodeReference("dt", "t"), "2")
                        ),
                        new ParticipantObject(
                                "id2",
                                ParticipantObjectType.SYSTEM_OBJECT,
                                new CodeReference("idtype", "t"),
                                ParticipantObjectTypeRole.JOB,
                                DataLifeCycle.ACCESS_OR_USE,
                                new CodeReference("sens", "not so"),
                                "Some object 2",
                                "GET",
                                new ParticipantObject.Detail(new CodeReference("dt", "t"), "1"),
                                new ParticipantObject.Detail(new CodeReference("dt", "t"), "2")
                        )
                ),
                new CodeReference("version", "1.0")
        );

        logAdapter.record(entryNullish);
        logAdapter.record(entryFullish);

        List<LogEntry> entries = logReader.read(2);

        assertThat(entries.size(), is(2));

        LogEntry entryNullishOut = entries.get(0);
        LogEntry entryFullishOut = entries.get(1);

        assertThat(entryNullishOut, is(entryNullish));
        assertThat(entryFullishOut, is(entryFullish));

        assertThat(entryFullish.getParticipatingPrincipals().size(), is(2));
        assertThat(entryFullishOut.getParticipatingPrincipals().size(), is(2));
        assertThat(entryFullishOut.getParticipatingPrincipals(), is(entryFullish.getParticipatingPrincipals()));

        assertThat(entryFullish.getAuditSources().size(), is(3));


        System.out.println(entryFullish);
    }
}
