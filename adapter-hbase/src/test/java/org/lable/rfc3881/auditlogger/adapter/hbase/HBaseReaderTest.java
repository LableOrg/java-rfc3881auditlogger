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
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Ignore;
import org.junit.Test;
import org.lable.rfc3881.auditlogger.api.AuditLogAdapter;
import org.lable.rfc3881.auditlogger.api.AuditLogReader;
import org.lable.rfc3881.auditlogger.api.LogEntry;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

public class HBaseReaderTest {

    @Test
    @Ignore
    public void readOne() throws IOException {
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
                    () -> TableName.valueOf("jeroen", "audit_test2"),
                    () -> "a"
            );

            List<LogEntry> logs = logReader.read(Instant.now().minus(1, ChronoUnit.HOURS), Instant.now());

            for (LogEntry log : logs) {
                System.out.println(log);
            }
        }
    }
}