/*
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
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Ignore;
import org.junit.Test;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;
import org.lable.oss.bitsandbytes.ByteConversion;
import org.lable.oss.bitsandbytes.ByteMangler;
import org.lable.rfc3881.auditlogger.api.*;
import org.lable.rfc3881.auditlogger.definition.rfc3881.*;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.lable.rfc3881.auditlogger.adapter.hbase.HBaseAdapter.referenceableToBytes;

public class HBaseAdapterTest {
    @Test
    @Ignore
    public void recordTest() throws IOException {
        AtomicLong uid = new AtomicLong();

        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "tzka,tzkb,tzkc");
        try (Connection hConnection = ConnectionFactory.createConnection(conf)) {

            AuditLogAdapter auditLogAdapter = new HBaseAdapter(
                    tableName -> {
                        try {
                            return hConnection.getTable(tableName);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    () -> TableName.valueOf("jeroen", "audit_test6"),
                    () -> "a",
                    () -> Bytes.toBytes(uid.getAndIncrement())
            );

            long instant = System.currentTimeMillis() - 1_000_000;
            for (int i = 0; i < 100_000; i++) {
                instant += 1;
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
    @Ignore
    public void rawTest() throws IOException {
        byte[] bEvents = ByteConversion.fromString("events");
        byte[] bLogon = ByteConversion.fromString("logon");
        byte[] cf = ByteConversion.fromString("a");
        byte[] bNet = ByteConversion.fromString("net");
        byte[] bNetVal = ByteConversion.fromString("127.0.0.1");
        byte[] bEvent = ByteConversion.fromString("e");
        byte[] bEventVal = ByteConversion.fromString("le:logon:E:0");
        byte[] bVersion = ByteConversion.fromString("v");
        byte[] bVersionVal = ByteConversion.fromString("le:1");
        byte[] bPrin1 = ByteConversion.fromString("p");
        byte[] bPrin1Val = ByteConversion.fromString("8-mkmdmgmmmkmpxxmc");
        byte[] bPrin2 = ByteConversion.fromString("d");
        byte[] bPrin2Val = ByteConversion.fromString("8-zzzzzzmmmkmpxxmc");
        byte[] as1 = ByteConversion.fromString("as:0");
        byte[] as1Val = ByteConversion.fromString("ws:tomcat1");
        byte[] as2 = ByteConversion.fromString("as:1");
        byte[] as2Val = ByteConversion.fromString("ss:authserver");
        byte[] po1 = ByteConversion.fromString("po:0");
        byte[] po1Val = ByteConversion.fromString("8-xxxxxzmmmkmpxxmc:P:le1:user:A:TOPSECRET:details1");
        byte[] po2 = ByteConversion.fromString("po:1");
        byte[] po2Val = ByteConversion.fromString("8-ccccczmmmkmpxxmc:S:le1:data:A");

        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "tzka,tzkb,tzkc");
        try (Connection hConnection = ConnectionFactory.createConnection(conf)) {
            Table table = hConnection.getTable(TableName.valueOf("jeroen", "audit_test4"));
            long instant = System.currentTimeMillis() - 1_000;
            for (int i = 0; i < 100_000; i++) {
                instant += 1;
                byte[] row = ByteMangler.add(
                        ByteMangler.reverse(ByteConversion.fromLong(instant)),
                        bEvents,
                        new byte[0],
                        bLogon
                );
                Put put = new Put(row);
                put.addColumn(cf, bNet, bNetVal);
                put.addColumn(cf, bEvent, bEventVal);
                put.addColumn(cf, bEvent, bEventVal);
                put.addColumn(cf, bVersion, bVersionVal);
                put.addColumn(cf, bPrin1, bPrin1Val);
                put.addColumn(cf, bPrin2, bPrin2Val);
                put.addColumn(cf, as1, as1Val);
                put.addColumn(cf, as2, as2Val);
                put.addColumn(cf, po1, po1Val);
                put.addColumn(cf, po2, po2Val);

                table.put(put);
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