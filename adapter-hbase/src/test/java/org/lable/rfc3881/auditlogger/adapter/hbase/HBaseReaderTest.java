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
import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Ignore;
import org.junit.Test;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.rfc3881.auditlogger.api.*;
import org.lable.rfc3881.auditlogger.api.LogEntry.ToStringOptions;
import org.lable.rfc3881.auditlogger.hbase.AuditLogPrincipalFilter;
import org.lable.rfc3881.auditlogger.hbase.AuditLogPrincipalFilter.FilterMode;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.EnumSet;
import java.util.List;

public class HBaseReaderTest {

    @Test
    @Ignore
    public void readSome() throws IOException {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "ntzka,ntzkb,ntzkc");
        conf.set("zookeeper.znode.parent", "/hbase-unsecure");

        try (Connection hConnection = ConnectionFactory.createConnection(conf)) {

            AuditLogReader logReader = new HBaseReader(
                    () -> hConnection,
                    () -> TableName.valueOf("audit", "care_loki"),
                    () -> "a"
            );

            List<LogEntry> logs;
            EnumSet<ToStringOptions> options;

            Instant now = Instant.now();

            // Een uur geleden.
            Instant then = now.minus(1, ChronoUnit.HOURS);

            // Specifiek tijdstip.
            Instant at = LocalDateTime.parse("2020-03-11T10:37:46")
                    .atZone(ZoneId.of("Europe/Amsterdam"))
                    .toInstant();

            at = Instant.parse("2020-03-24T09:57:34.470Z");

            LogFilter filter =

                    LogFilter.define()
//                            .filterOnAccountDomain("perf", "loki")
//                            .filterOnEventId("lable/auditevents/resource", "/api/v1/clients")
//                            .filterOnPrincipalInvolved("domain-master-stable-local//8-mftqfmwtxmqxxxxz")
//                            .addFilterOnParticipantObject("lable/data-owner/1.0", "client", "8-szmrxptfzmqxxxxz")
//                            .addFilterOnParticipantObject("lable/data-owner/1.0", "Client", "8-szmrxptfzmqxxxxz")
//                            .addFilterOnParticipantObject("lable/data-owner/1.0", "Client", "Client_0_abddcf9f50f34aa08b9f25ddde046d0e")
                            .build();

            // Logs ophalen:
            logs =

                    // Laatste n logs.
                    logReader.defineQuery()
//                            .withFrom(Instant.parse("2023-09-14T11:56:39.361Z"))
//                            .withFrom(new Event.EventId(
//                                    new CodeReference("org.lable.auditevents", "SIGN_ON@USERNAME+PASSWORD"),
//                                    1694691348597L,
//                                    7108050702198380864L
//                            ), true)
                            .withTo(Instant.parse("2023-09-20T08:44:18.969Z"), true)
                            .withFrom(Instant.parse("2023-09-14T11:35:48.597Z"), false)
//                            .withLimit(2L)
//                            .withTo(new Event.EventId(
//                                    new CodeReference("org.lable.auditevents", "APP_INIT"),
//                                    1695199458969L,
//                                    7110181871572488590L
//                            ), true)
                            .withFilter(filter)
                            .execute();

            // Toon n logs vanaf een tijdstip.
//                    logReader.read(at, null, 2L, filter);
//                    logReader.read(then, 1);


            // Weergave-instellingen:
            options =

                    // Laat de betrokken objecten beknopt zien.
//                    EnumSet.of(TRUNCATE_PARTICIPANT_OBJECTS);

                    // Laat alles zien.
                    EnumSet.noneOf(ToStringOptions.class);


            for (LogEntry log : logs) {
//                System.out.println(((UniqueEvent) log.getEvent()).toId());
//                System.out.println("  " + (log.getRequestor() == null ? "-" : log.getRequestor().getUserId()));
                System.out.println(log.toString(options));
            }
        }
    }

    @Test
    @Ignore
    public void readSomeMore() throws IOException {
        long start = System.nanoTime();

        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", "ntzka,ntzkb,ntzkc");
        conf.set("zookeeper.znode.parent", "/hbase-unsecure");

        Scan scan = new Scan()
                .setFilter(new AuditLogPrincipalFilter("a".getBytes(), FilterMode.DOMAIN_SUBSTRING, "perf"))
                .setLimit(100);

        try (
                Connection hConnection = ConnectionFactory.createConnection(conf);
                Table table = hConnection.getTable(TableName.valueOf("jeroen", "audit"));
                ResultScanner scanner = table.getScanner(scan)
        ) {
            for (Result result : scanner) {
                CellScanner cellScanner = result.cellScanner();
                while (cellScanner.advance()) {
                    Cell cell = cellScanner.current();
                    String cq = Bytes.toStringBinary(CellUtil.cloneQualifier(cell));
                    if (cq.startsWith("requestor")) {
                        System.out.print(Bytes.toStringBinary(result.getRow()));
                        System.out.println("  " + cq);
                    }
                    if (cq.startsWith("access")) {
                        System.out.print(cq + "  ");
                        System.out.println(Bytes.toStringBinary(CellUtil.cloneValue(cell)));
                    }
                }
            }
        }

        long stop = System.nanoTime();
        long took = (stop - start) / 1_000_000;
        System.out.println("Took: " + took);
    }
}