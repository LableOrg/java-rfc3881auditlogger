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
import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class LocalHbase {
    private static final Logger logger = LoggerFactory.getLogger(LocalHbase.class);

    private HBaseTestingUtility htu;
    private Connection hConnection;

    public LocalHbase() throws Exception {
        startLocalHbaseInstance();
    }

    public Table getTable(TableName tableName) {
        try {
            return hConnection.getTable(tableName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void createNamespace(String name) throws IOException {
        htu.getAdmin().createNamespace(NamespaceDescriptor.create(name).build());
    }

    public HBaseTestingUtility getHBaseTestingUtility() {
        return htu;
    }

    void startLocalHbaseInstance() throws Exception {
        logger.info("Starting local HBase instance…");

        // Disable commons logging to shut up the HDFS block log. This is a crude approach to
        // clearing the output to the console during tests, but shouldn't cause any problems for
        // logging during tests as long as commons logging isn't used.
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        htu = new HBaseTestingUtility();
        htu.startMiniCluster();
        Configuration conf = htu.getConfiguration();

        hConnection = ConnectionFactory.createConnection(conf);

        logger.info("Local HBase instance up and running.");
    }

    public void close() throws IOException {
        logger.info("Shutting down local HBase instance.");

        try {
            hConnection.close();
        } catch (IOException e) {
            // Ignore.
        }
        try {
            htu.shutdownMiniCluster();
        } catch (Throwable e) {
            // Ignore.
        }

        logger.info("Local HBase instance shut down.");
    }
}
