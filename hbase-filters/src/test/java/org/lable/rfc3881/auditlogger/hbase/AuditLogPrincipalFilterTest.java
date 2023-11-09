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

import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;
import org.lable.rfc3881.auditlogger.hbase.AuditLogPrincipalFilter.FilterMode;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.lable.rfc3881.auditlogger.hbase.AuditLogPrincipalFilter.matchesPrincipalPrefix;


public class AuditLogPrincipalFilterTest {
    @Test
    public void serializationTest() throws IOException, DeserializationException {
        AuditLogPrincipalFilter in = new AuditLogPrincipalFilter(
                "a".getBytes(),
                FilterMode.EXACT_DOMAIN,
                "domain"
        );

        byte[] bytes = in.toByteArray();

        AuditLogPrincipalFilter out = AuditLogPrincipalFilter.parseFrom(bytes);

        assertThat(out.getFamily(), is(in.getFamily()));
        assertThat(out.getFilterMode(), is(in.getFilterMode()));
        assertThat(out.getMatch(), is(in.getMatch()));
    }

    @Test
    public void testBytePrefixes() {
        assertThat(AuditLogPrincipalFilter.REQUESTOR_PREFIX, is(Bytes.add("requestor".getBytes(), new byte[]{0})));
        assertThat(AuditLogPrincipalFilter.DELEGATOR_PREFIX, is(Bytes.add("delegator".getBytes(), new byte[]{0})));
        assertThat(AuditLogPrincipalFilter.PRINCIPAL_PREFIX, is(Bytes.add("principal".getBytes(), new byte[]{0})));
    }

    @Test
    public void matchesPrincipalPrefixTest() {
        assertThat(matchesPrincipalPrefix(Bytes.add(Bytes.toBytes("requestor"), new byte[]{0, 1}), 0), is(true));
        assertThat(matchesPrincipalPrefix(Bytes.add(Bytes.toBytes("delegator"), new byte[]{0, 1}), 0), is(true));
        assertThat(matchesPrincipalPrefix(Bytes.add(Bytes.toBytes("principal"), new byte[]{0, 1}), 0), is(true));

        assertThat(matchesPrincipalPrefix(Bytes.add(Bytes.toBytes("----requestor"), new byte[]{0, 1}), 4), is(true));
        assertThat(matchesPrincipalPrefix(Bytes.add(Bytes.toBytes("----delegator"), new byte[]{0, 1}), 4), is(true));
        assertThat(matchesPrincipalPrefix(Bytes.add(Bytes.toBytes("----principal"), new byte[]{0, 1}), 4), is(true));

        assertThat(matchesPrincipalPrefix(new byte[]{-1, -2, -3, -4, 0, 1, 2, 3, 4, 5, 6}, 0), is(false));
        assertThat(matchesPrincipalPrefix(Bytes.add(Bytes.toBytes("cow-or-chicken"), new byte[]{0, 1}), 0), is(false));
        assertThat(matchesPrincipalPrefix(Bytes.add(Bytes.toBytes("cow"), new byte[]{0, 1}, Bytes.toBytes("chicken")), 0), is(false));
    }

    @Test
    public void filterModeMatchTest() {
        assertThat(FilterMode.EXACT_PRINCIPAL.matches("domain//user", "domain//user"), is(true));
        assertThat(FilterMode.EXACT_PRINCIPAL.matches("domain//user", "domain//other"), is(false));

        assertThat(FilterMode.EXACT_DOMAIN.matches("domain//user", "domain"), is(true));
        assertThat(FilterMode.EXACT_DOMAIN.matches("domain//user", "domain2"), is(false));
        assertThat(FilterMode.EXACT_DOMAIN.matches("domain2//user", "domain"), is(false));
        assertThat(FilterMode.EXACT_DOMAIN.matches("domain", "domain"), is(false));

        assertThat(FilterMode.DOMAIN_PREFIX.matches("cust-local//user", "cust"), is(true));
        assertThat(FilterMode.DOMAIN_PREFIX.matches("domain//user", "cust"), is(false));
        assertThat(FilterMode.DOMAIN_PREFIX.matches("dd-cust//user", "cust"), is(false));

        assertThat(FilterMode.DOMAIN_SUBSTRING.matches("a-domain-here//user", "domain"), is(true));
        assertThat(FilterMode.DOMAIN_SUBSTRING.matches("domain//user", "domain"), is(true));
        assertThat(FilterMode.DOMAIN_SUBSTRING.matches("a-domain-here//user", "nope"), is(false));

        assertThat(FilterMode.DOMAIN_REGEX.matches("domain_12//user", "^(domain)?_[0-9]{2}$"), is(true));
        assertThat(FilterMode.DOMAIN_REGEX.matches("domain_123//user", "^(domain)?_[0-9]{2}$"), is(false));
    }
}