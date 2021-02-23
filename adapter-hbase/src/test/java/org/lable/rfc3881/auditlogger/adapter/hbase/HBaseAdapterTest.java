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
package org.lable.rfc3881.auditlogger.adapter.hbase;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Ignore;
import org.junit.Test;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.rfc3881.auditlogger.api.*;
import org.lable.rfc3881.auditlogger.definition.rfc3881.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

public class HBaseAdapterTest {
    Random random = new Random();
    char[] eightIdIshPool = {
            'x', 'c', 'd', 'f', 'g', 'k', 'm', 'p',
            'q', 'r', 's', 't', 'v', 'w', 'b', 'z'
    };

    @Test
    @Ignore
    public void doubleIdentifierTest() throws IOException {
        AtomicLong uid = new AtomicLong();

        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "tzka,tzkb,tzkc");
        try (
                Connection hConnection = ConnectionFactory.createConnection(conf);
                Table table = hConnection.getTable(TableName.valueOf("jeroen", "audit_test2"))
        ) {
            AuditLogAdapter auditLogAdapter = new HBaseAdapter(
                    put -> {
                        try {
                            table.put(put);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    () -> "a",
                    () -> Bytes.toBytes(uid.getAndIncrement())
            );

            long instant = System.currentTimeMillis() - 1_000_000;

            LogEntry logEntry = new LogEntry(
                    randomEvent(instant),
                    randomPrincipal(),
                    randomPrincipal(),
                    Arrays.asList(randomPrincipal(), randomPrincipal()),
                    NetworkAccessPoint.byIPAddress("127.0.0.1", true),
                    Collections.singletonList(
                            new AuditSource("servercluster1", "authserver", true, AuditSourceType.SECURITY_SERVER)
                    ),
                    Arrays.asList(
                            new ParticipantObject(
                                    "XXX",
                                    ParticipantObjectType.SYSTEM_OBJECT,
                                    new CodeReference("org.lable.test", "test"),
                                    null,
                                    DataLifeCycle.ACCESS_OR_USE,
                                    null,
                                    null,
                                    null,
                                    true
                            ),
                            new ParticipantObject(
                                    "XXX",
                                    ParticipantObjectType.SYSTEM_OBJECT,
                                    new CodeReference("org.lable.test", "test"),
                                    null,
                                    DataLifeCycle.ACCESS_OR_USE,
                                    null,
                                    null,
                                    null
                            )
                    ),
                    new CodeReference("version", "1", "1")
            );

            auditLogAdapter.record(logEntry);
        }
    }

    @Test
    @Ignore
    public void recordTest() throws IOException {
        AtomicLong uid = new AtomicLong();

        List<Principal> principals = new ArrayList<>();
        for (int i = 0; i < 200; i++) {
            principals.add(randomPrincipal());
        }
        List<ParticipantObject> clients = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            clients.add(randomClient());
        }

        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "tzka,tzkb,tzkc");
        try (
                Connection hConnection = ConnectionFactory.createConnection(conf);
                Table table = hConnection.getTable(TableName.valueOf("jeroen", "audit_test5"))
        ) {

            AuditLogAdapter auditLogAdapter = new HBaseAdapter(
                    put -> {
                        try {
                            table.put(put);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    () -> "a",
                    () -> Bytes.toBytes(uid.getAndIncrement())
            );

            long instant = System.currentTimeMillis() - 1_000_000;
            for (int i = 0; i < 100_000; i++) {
                if (i % 5000 == 0) {
                    System.out.println(">> " + i);
                }
                instant += 1;
                LogEntry logEntry = new LogEntry(
                        randomEvent(instant),
                        principals.get(random.nextInt(principals.size())),
                        null,
                        null,
                        NetworkAccessPoint.byIPAddress("127.0.0.1", false),
                        Arrays.asList(
                                new AuditSource("servercluster1", "tomcat1", false, AuditSourceType.WEB_SERVER_PROCESS),
                                new AuditSource("servercluster1", "authserver", AuditSourceType.SECURITY_SERVER)
                        ),
                        Arrays.asList(
                                clients.get(random.nextInt(clients.size())),
                                new ParticipantObject(
                                        random8idIsh(),
                                        ParticipantObjectType.SYSTEM_OBJECT,
                                        new CodeReference("org.lable.model", "report"),
                                        ParticipantObjectTypeRole.DATA_REPOSITORY,
                                        DataLifeCycle.ACCESS_OR_USE,
                                        null,
                                        "20190527T1030",
                                        null,
                                        false
                                )
                        ),
                        new CodeReference("version", "1", "1")
                );

                auditLogAdapter.record(logEntry);
            }
        }
    }

    private ParticipantObject randomClient() {
        return new ParticipantObject(
                "xxx-xxx-prod//" + random8idIsh(),
                ParticipantObjectType.PERSON,
                ParticipantObjectIDType.ACCOUNT_NUMBER,
                ParticipantObjectTypeRole.USER,
                DataLifeCycle.ACCESS_OR_USE,
                null,
                randomName(),
                null,
                new ParticipantObject.Detail(
                        new CodeReference("org.lable.audit.ids", "legacy-key"),
                        "xxx-xxx-prod//k/" + randomLegacyKeyIsh("Account")
                ),
                new ParticipantObject.Detail(
                        new CodeReference("org.lable.audit.ids", "bsn"),
                        "926346347"
                )
        );
    }

    private Principal randomPrincipal() {
        String id = "xxx-xxx-prod//" + random8idIsh();
        String name = randomName();

        return new Principal(
                id,
                Arrays.asList(
                        "xxx-xxx-prod//k/" + randomLegacyKeyIsh("Account"),
                        "xxx-xxx-prod//u/" + name.toLowerCase().replace(' ', '.')
                ),
                name,
                true,
                new CodeReference("roles", "is-employee")
        );
    }

    ParticipantObject plain() {
        return new ParticipantObject(
                "xxx-xxx-prod//8-mkmdmgmmmkmpxxmc",
                ParticipantObjectType.PERSON,
                ParticipantObjectIDType.ACCOUNT_NUMBER,
                ParticipantObjectTypeRole.USER,
                DataLifeCycle.ACCESS_OR_USE,
                null,
                null,
                null,
                false
        );
    }

    ParticipantObject fleshedOut() {
        return new ParticipantObject(
                "xxx-xxx-prod//8-mkmdmgmmmkmpxxmc",
                ParticipantObjectType.PERSON,
                ParticipantObjectIDType.ACCOUNT_NUMBER,
                ParticipantObjectTypeRole.USER,
                DataLifeCycle.ACCESS_OR_USE,
                null,
                "Bob van der Testdäta",
                null,
                new ParticipantObject.Detail(
                        new CodeReference("org.lable.audit.ids", "user"),
                        "xxx-xxx-prod//k/Account_0_a8855327e7f5414dbf8e480d2df88b0b"
                ),
                new ParticipantObject.Detail(
                        new CodeReference("org.lable.audit.ids", "user"),
                        "xxx-xxx-prod//u/bob.vandertestdata"
                )
        );
    }

    Event randomEvent(long happenedAt) {
        boolean login = random.nextInt(100) < 5;
        if (login) {
            boolean success = random.nextInt(100) < 95;
            return new Event(
                    new CodeReference(
                            "org.lable.auditevents",
                            "SIGN_ON@USERNAME+PASSWORD"
                    ),
                    EventAction.EXECUTE,
                    happenedAt,
                    success ? EventOutcome.SUCCESS : EventOutcome.MAJOR_FAILURE
            );
        } else {
            int actInt = random.nextInt(100);
            EventAction action = EventAction.READ;
            if (actInt > 90) {
                action = EventAction.UPDATE;
            } else if (actInt > 70) {
                action = EventAction.CREATE;
            } else if (actInt > 65) {
                action = EventAction.DELETE;
            }

            actInt = random.nextInt(100);
            String resource = "client";
            if (actInt > 60) {
                resource = "journal";
            } else if (actInt > 40) {
                resource = "risk";
            }

            return new Event(
                    new CodeReference(
                            "org.lable.auditevents.resource",
                            "/api/v1/" + resource + "/" + random8idIsh()
                    ),
                    action,
                    happenedAt,
                    EventOutcome.SUCCESS
            );
        }
    }

    public String random8idIsh() {
        StringBuilder id = new StringBuilder("8-");
        for (int i = 0; i < 16; i++) {
            id.append(eightIdIshPool[random.nextInt(16)]);
        }

        return id.toString();
    }

    public String randomLegacyKeyIsh(String type) {
        StringBuilder key = new StringBuilder(type).append("_0_");
        for (int i = 0; i < 32; i++) {
            if (i < 10) {
                key.append(i);
            } else {
                key.append((char) ('a' + i - 10));
            }
        }

        return key.toString();
    }

    public String randomName() {
        StringBuilder name = new StringBuilder();
        for (int i = 2; i < 3 + random.nextInt(5); i++) {
            name.append((char) ('A' + random.nextInt(26)));
            for (int j = 6; j < 8 + random.nextInt(25); j++) {
                name.append((char) ('a' + random.nextInt(26)));
            }
            name.append(' ');
        }
        return name.toString().trim();
    }
}