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
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.lable.rfc3881.auditlogger.api.AuditLogReader;
import org.lable.rfc3881.auditlogger.api.LogEntry;
import org.lable.rfc3881.auditlogger.api.LogEntry.ToStringOptions;
import org.lable.rfc3881.auditlogger.api.LogFilter;

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
        conf.set("hbase.zookeeper.quorum", "tzka,tzkb,tzkc");
        try (Connection hConnection = ConnectionFactory.createConnection(conf)) {

            AuditLogReader logReader = new HBaseReader(
                    tableName -> {
                        try {
                            return hConnection.getTable(tableName);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    },
                    () -> TableName.valueOf("audit", "care_master_stable"),
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
//                            .filterOnEventId("lable/auditevents/resource", "/api/v1/clients")
//                            .filterOnPrincipalInvolved("domain-master-stable-local//8-mftqfmwtxmqxxxxz")
//                            .addFilterOnParticipantObject("lable/data-owner/1.0", "client", "8-szmrxptfzmqxxxxz")
//                            .addFilterOnParticipantObject("lable/data-owner/1.0", "Client", "8-szmrxptfzmqxxxxz")
//                            .addFilterOnParticipantObject("lable/data-owner/1.0", "Client", "Client_0_abddcf9f50f34aa08b9f25ddde046d0e")
                            .build();

            // Logs ophalen:
            logs =

                    // Laatste n logs.
                    logReader.defineQuery().withLimit(10L).withFilter(filter).execute();

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
                System.out.println(log.toString(options));
            }
        }
    }
}