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

import org.apache.hadoop.hbase.*;
import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class SingleQualifierFilterTest {
    @Test
    public void serializationTest() throws IOException, DeserializationException {
        SingleQualifierFilter in = new SingleQualifierFilter(
                "a".getBytes(),
                CompareOperator.EQUAL,
                new RegexStringComparator("requestor.*")
        );

        byte[] bytes = in.toByteArray();

        SingleQualifierFilter out = SingleQualifierFilter.parseFrom(bytes);

        assertThat(out.getFamily(), is(in.getFamily()));
        assertThat(out.getCompareOperator(), is(in.getCompareOperator()));
        // RegexStringComparator doesn't implement #equals.
        assertThat(out.getComparator().getValue(), is(in.getComparator().getValue()));
    }
}