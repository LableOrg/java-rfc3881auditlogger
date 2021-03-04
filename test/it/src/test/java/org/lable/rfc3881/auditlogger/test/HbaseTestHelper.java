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
package org.lable.rfc3881.auditlogger.test;

import org.apache.hadoop.hbase.NamespaceDescriptor;
import org.apache.hadoop.hbase.NamespaceNotFoundException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.IOException;

/**
 * Collection of Hbase operations commonly used in integration tests.
 */
public class HbaseTestHelper {
    private HbaseTestHelper() {
        // No-op.
    }

    public static void createNamespaceIfMissing(Admin admin, String name) throws IOException {
        try {
            admin.getNamespaceDescriptor(name);
        } catch (NamespaceNotFoundException e) {
            admin.createNamespace(NamespaceDescriptor.create(name).build());
        }
    }

    public static TableDescriptor buildSimpleDescriptor(TableName tableName, String columnFamily) {
        return buildSimpleDescriptor(tableName, Bytes.toBytes(columnFamily), 1);
    }

    public static TableDescriptor buildSimpleDescriptor(TableName tableName, String columnFamily, int versions) {
        return buildSimpleDescriptor(tableName, Bytes.toBytes(columnFamily), versions);
    }

    public static TableDescriptor buildSimpleDescriptor(TableName tableName, byte[] columnFamily) {
        return buildSimpleDescriptor(tableName, columnFamily, 1);
    }

    public static TableDescriptor buildSimpleDescriptor(TableName tableName, byte[] columnFamily, int versions) {
        return TableDescriptorBuilder
                .newBuilder(tableName)
                .setColumnFamily(ColumnFamilyDescriptorBuilder
                        .newBuilder(columnFamily)
                        .setMaxVersions(versions)
                        .build()
                )
                .build();
    }

    public static void createOrTruncateTable(Admin admin, TableDescriptor descriptor) throws IOException {
        TableName tableName = descriptor.getTableName();
        if (admin.tableExists(tableName)) {
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
        }

        admin.createTable(descriptor);
    }

    public static void disableAndDeleteTable(Admin admin, TableName tableName) throws IOException {
        if (admin.tableExists(tableName)) {
            admin.disableTable(tableName);
            admin.deleteTable(tableName);
        }
    }

    public static int count(Connection connection, TableName tableName) throws IOException {
        Scan scan = new Scan()
                .setFilter(new FilterList(
                        FilterList.Operator.MUST_PASS_ALL,
                        new FirstKeyOnlyFilter(),
                        new KeyOnlyFilter()
                ));

        int rows = 0;
        try (
                Table table = connection.getTable(tableName);
                ResultScanner scanner = table.getScanner(scan)
        ) {
            for (Result ignored : scanner) {
                rows += 1;
            }
        }

        return rows;
    }
}
