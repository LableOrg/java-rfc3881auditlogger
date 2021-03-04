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
package org.lable.lapin.maven;

import org.apache.hadoop.hbase.HBaseTestingUtility;
import org.apache.hadoop.hbase.NamespaceDescriptor;

public class LocalHbaseManager {
    private volatile HbaseRunner hbaseThread;

    public void start() throws Exception {
        // Disable commons logging to shut up the HDFS block log. This is a crude approach to
        // clearing the output to the console during tests, but shouldn't cause any problems for
        // logging during tests as long as commons logging isn't used.
        System.setProperty("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            // Only required for using the HbaseTestingUtility on Windows.
            System.setProperty("test.build.data.basedirectory", "C:/Temp/hbase");
        }

        HBaseTestingUtility htu = new HBaseTestingUtility();
        htu.getConfiguration().setInt("test.hbase.zookeeper.property.clientPort", 33533);
        htu.startMiniCluster();

        htu.getAdmin().createNamespace(NamespaceDescriptor.create("test").build());

        hbaseThread = new HbaseRunner(htu);
        hbaseThread.setDaemon(true);
        hbaseThread.start();
    }

    public void stop() {
        hbaseThread.terminate();
    }


    static class HbaseRunner extends Thread {
        private final HBaseTestingUtility htu;
        private volatile boolean shouldRun = true;

        public HbaseRunner(HBaseTestingUtility htu) {
            this.htu = htu;
        }

        @Override
        public void run() {
            while (shouldRun) {
                Thread.onSpinWait();
            }
        }

        public void terminate() {
            try {
                htu.shutdownMiniCluster();
            } catch (Exception e) {
                // Ignore.
            }
            System.out.println("STOPPING");
            shouldRun = false;
        }
    }
}
