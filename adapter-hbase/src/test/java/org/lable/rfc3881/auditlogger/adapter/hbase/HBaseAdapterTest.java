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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HConstants;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Ignore;
import org.junit.Test;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.oss.bitsandbytes.ByteMangler;
import org.lable.rfc3881.auditlogger.api.*;
import org.lable.rfc3881.auditlogger.definition.rfc3881.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import static org.apache.hadoop.hbase.util.Bytes.toBytes;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.lable.rfc3881.auditlogger.adapter.hbase.HBaseAdapter.NULL_BYTE;
import static org.lable.rfc3881.auditlogger.adapter.hbase.HBaseAdapter.columnQualifierSuffixFor;

public class HBaseAdapterTest {
    Random random = new Random();
    char[] eightIdIshPool = {
            'x', 'c', 'd', 'f', 'g', 'k', 'm', 'p',
            'q', 'r', 's', 't', 'v', 'w', 'b', 'z'
    };

    @Test
    public void columnQualifierSuffixForTest() {
        AuditSource source = new AuditSource("ñ", "ñ", true);
        byte[] suffix = columnQualifierSuffixFor(source);
        byte[] asBytes = toBytes("ñ");
        assertThat(suffix, is(ByteMangler.add(asBytes, NULL_BYTE, asBytes)));
    }

    @Test
    @Ignore
    public void cursorTest() throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "ntzka,ntzkb,ntzkc");
        conf.set("zookeeper.znode.parent", "/hbase-unsecure");

        conf.set(HConstants.HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD, "1000");

        TableName tableName = TableName.valueOf("audit", "care_loki");

        long start = System.currentTimeMillis();
        System.out.println("Scan");
        try (
                Connection hConnection = ConnectionFactory.createConnection(conf);
                Table table = hConnection.getTable(tableName)
        ) {
            Scan scan = new Scan();
            scan.setLimit(1)
                    .setNeedCursorResult(true)
                    .setFilter(new SingleColumnValueFilter(
                            "a".getBytes(),
                            "event".getBytes(),
                            CompareOperator.EQUAL,
                            "{\"id\":{\"cs\":\"org.lable.audit.api-call\",\"code\":\"/api/v1/ou\"},\"happenedAt\":1630008292944,\"outcome\":{\"cs\":\"IETF/RFC3881.5.1.4\",\"code\":\"0\"},\"action\":{\"cs\":\"IETF/RFC3881.5.1.2\",\"code\":\"R\"}}".getBytes()
                    ));

            try (ResultScanner resultScanner = table.getScanner(scan)) {
                for (Result result : resultScanner) {
                    if (result.isCursor()) {
                        System.out.println("C: " + Bytes.toStringBinary(result.getCursor().getRow()));
                    } else {
                        System.out.println("R: " + Bytes.toStringBinary(result.getRow()));
                    }
                }
            }
        }

        System.out.println("Done");
        long stop = System.currentTimeMillis();
        long took = stop - start;
        System.out.println("Took " + took);
    }

    @Test
    @Ignore
    public void cursorResumeTest() throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "ntzka,ntzkb,ntzkc");
        conf.set("zookeeper.znode.parent", "/hbase-unsecure");

        conf.set(HConstants.HBASE_CLIENT_SCANNER_TIMEOUT_PERIOD, "1000");

        TableName tableName = TableName.valueOf("audit", "care_loki");

        SingleColumnValueFilter filter = new SingleColumnValueFilter(
                "a".getBytes(),
                "event".getBytes(),
                CompareOperator.EQUAL,
                "{\"id\":{\"cs\":\"org.lable.audit.api-call\",\"code\":\"/api/v1/ou\"},\"happenedAt\":1630008292944,\"outcome\":{\"cs\":\"IETF/RFC3881.5.1.4\",\"code\":\"0\"},\"action\":{\"cs\":\"IETF/RFC3881.5.1.2\",\"code\":\"R\"}}".getBytes()
        );

        long start = System.currentTimeMillis();
        System.out.println("Scan");
        try (
                Connection hConnection = ConnectionFactory.createConnection(conf);
                Table table = hConnection.getTable(tableName)
        ) {
            Scan scan = new Scan()
                    .setLimit(1)
                    .setNeedCursorResult(true)
                    .setFilter(filter);

            scan:
            {
                while (true) {
                    singleScanner:
                    {
                        try (ResultScanner resultScanner = table.getScanner(scan)) {
                            for (Result result : resultScanner) {
                                if (result.isCursor()) {
                                    Cursor cursor = result.getCursor();
                                    System.out.println("C: " + Bytes.toStringBinary(cursor.getRow()));
                                    scan = Scan
                                            .createScanFromCursor(cursor)
                                            .setNeedCursorResult(true)
                                            .setFilter(filter)
                                            .setLimit(1);
                                    break singleScanner;
                                } else {
                                    System.out.println("R: " + Bytes.toStringBinary(result.getRow()));
                                    break scan;
                                }
                            }
                            System.out.println("Not found.");
                            break scan;
                        }
                    }
                }
            }
        }

        System.out.println("Done");
        long stop = System.currentTimeMillis();
        long took = stop - start;
        System.out.println("Took " + took);
    }


    @Test
    @Ignore
    public void doubleIdentifierTest() throws IOException {
        AtomicLong uid = new AtomicLong();

        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "ntzka,ntzkb,ntzkc");
        conf.set("zookeeper.znode.parent", "/hbase-unsecure");

        TableName tableName = TableName.valueOf("jeroen", "audit2");

        try (
                Connection hConnection = ConnectionFactory.createConnection(conf);
                Table table = hConnection.getTable(tableName)
        ) {
            AuditLogAdapter auditLogAdapter = new HBaseAdapter(
                    (ignored, put) -> {
                        try {
                            table.put(put);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    logEntry -> tableName,
                    () -> "a",
                    uid::getAndIncrement
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
                    null,
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
        conf.set("hbase.zookeeper.quorum", "ntzka,ntzkb,ntzkc");
        conf.set("zookeeper.znode.parent", "/hbase-unsecure");

        try (
                Connection hConnection = ConnectionFactory.createConnection(conf)
        ) {

            AuditLogAdapter auditLogAdapter = new HBaseAdapter(
                    (tableName, put) -> {
                        try (Table table = hConnection.getTable(tableName)) {
                            table.put(put);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    logEntry -> TableName.valueOf("jeroen", "audit2"),
                    () -> "a",
                    uid::getAndIncrement
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
                        null,
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
                new Detail(
                        new CodeReference("org.lable.audit.ids", "legacy-key"),
                        "xxx-xxx-prod//k/" + randomLegacyKeyIsh("Account")
                ),
                new Detail(
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
                new Detail(
                        new CodeReference("org.lable.audit.ids", "user"),
                        "xxx-xxx-prod//k/Account_0_a8855327e7f5414dbf8e480d2df88b0b"
                ),
                new Detail(
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