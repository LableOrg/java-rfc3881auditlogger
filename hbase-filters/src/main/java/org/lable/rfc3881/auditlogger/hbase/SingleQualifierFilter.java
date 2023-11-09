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
package org.lable.rfc3881.auditlogger.hbase;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CompareOperator;
import org.apache.hadoop.hbase.PrivateCellUtil;
import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.filter.ByteArrayComparable;
import org.apache.hadoop.hbase.filter.FilterBase;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class SingleQualifierFilter extends FilterBase {
    protected byte[] columnFamily;
    protected CompareOperator op;
    protected ByteArrayComparable comparator;

    protected boolean matchedQualifier = false;

    public SingleQualifierFilter(final byte[] family,
                                 final CompareOperator op,
                                 final ByteArrayComparable comparator) {
        this.columnFamily = family;
        this.op = op;
        this.comparator = comparator;
    }

    public CompareOperator getCompareOperator() {
        return op;
    }

    public ByteArrayComparable getComparator() {
        return comparator;
    }

    public byte[] getFamily() {
        return columnFamily;
    }

    @Override
    public ReturnCode filterCell(final Cell c) {
        if (this.matchedQualifier) {
            // We already found a matching qualifier, all remaining keys now pass.
            return ReturnCode.INCLUDE;
        }

        if (filterQualifier(c)) {
            return ReturnCode.INCLUDE;
        }

        // Matched a qualifier.
        this.matchedQualifier = true;
        return ReturnCode.INCLUDE;
    }

    private boolean filterQualifier(final Cell cell) {
        int compareResult = PrivateCellUtil.compareQualifier(cell, this.comparator);
        return compare(this.op, compareResult);
    }

    /*
        Copied from Hbase's own CompareFilter#compare, which is annoyingly package private.
     */
    static boolean compare(final CompareOperator op, int compareResult) {
        switch (op) {
            case LESS:
                return compareResult <= 0;
            case LESS_OR_EQUAL:
                return compareResult < 0;
            case EQUAL:
                return compareResult != 0;
            case NOT_EQUAL:
                return compareResult == 0;
            case GREATER_OR_EQUAL:
                return compareResult > 0;
            case GREATER:
                return compareResult >= 0;
            default:
                throw new RuntimeException("Unknown Compare op " + op.name());
        }
    }

    @Override
    public boolean filterRowKey(Cell cell) throws IOException {
        return false;
    }

    @Override
    public boolean filterRow() {
        return !this.matchedQualifier;
    }

    @Override
    public boolean hasFilterRow() {
        return true;
    }

    @Override
    public void reset() {
        matchedQualifier = false;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(columnFamily.length);
        dos.write(columnFamily);

        String opName = op.name();
        dos.writeInt(opName.length());
        dos.writeBytes(opName);

        String compClzName = comparator.getClass().getCanonicalName();
        dos.writeInt(compClzName.length());
        dos.writeBytes(compClzName);
        dos.write(comparator.toByteArray());

        dos.flush();

        return baos.toByteArray();
    }

    public static SingleQualifierFilter parseFrom(final byte[] bytes) throws DeserializationException {
        ByteBuffer bb = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);

        byte[] family = new byte[bb.getInt()];
        bb.get(family);

        byte[] opBytes = new byte[bb.getInt()];
        bb.get(opBytes);
        CompareOperator op = CompareOperator.valueOf(Bytes.toString(opBytes));

        byte[] compClzBytes = new byte[bb.getInt()];
        bb.get(compClzBytes);
        String compClzName = Bytes.toString(compClzBytes);
        ByteArrayComparable comparator;
        try {
            Class<?> compClz = Class.forName(compClzName);
            Method method = compClz.getMethod("parseFrom", byte[].class);

            byte[] compBytes = new byte[bb.remaining()];
            bb.get(compBytes);

            comparator = (ByteArrayComparable) method.invoke(null, compBytes);

        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException |
                 InvocationTargetException e) {
            throw new DeserializationException(e);
        }

        return new SingleQualifierFilter(
                family,
                op,
                comparator
        );
    }
}
