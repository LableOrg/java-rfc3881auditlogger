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
package org.lable.rfc3881.auditlogger.definition.rfc3881;

import org.junit.Test;
import org.lable.codesystem.codereference.CodeReference;

import java.util.HashSet;
import java.util.Set;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.lable.rfc3881.auditlogger.definition.rfc3881.AuditSourceType.*;

public class AuditSourceTypeTest {
    @Test
    public void uniquenessTest() {
        // Verify that all codes and display names defined are unique within this enumerator.
        Set<String> codes = new HashSet<>();
        Set<String> displayNames = new HashSet<>();

        for (AuditSourceType auditSourceType : AuditSourceType.values()) {
            codes.add(auditSourceType.getCode());
            displayNames.add(auditSourceType.getDisplayName());
        }

        assertThat(codes.size(), is(AuditSourceType.values().length));
        assertThat(displayNames.size(), is(AuditSourceType.values().length));
    }

    @Test
    public void fromReferenceableTest() {
        assertThat(fromReferenceable(new CodeReference(CODE_SYSTEM, "1")), hasValue(END_USER_INTERFACE));
        assertThat(fromReferenceable(new CodeReference(CODE_SYSTEM, "9")), hasValue(EXTERNAL_UNKNOWN_OR_OTHER));

        assertThat(fromReferenceable(new CodeReference(CODE_SYSTEM, "?")), isEmpty());
        assertThat(fromReferenceable(new CodeReference("?", "1")), isEmpty());
    }

    @Test
    public void toStringTest() {
        // Defer to CodeReference for toString.
        for (AuditSourceType auditSourceType : AuditSourceType.values()) {
            assertThat(auditSourceType.toString(), is(auditSourceType.toCodeReference().toString()));
        }
    }

    @Test
    public void valueOfTest() {
        // Test equality and valueOf.
        for (AuditSourceType auditSourceType : AuditSourceType.values()) {
            assertThat(AuditSourceType.valueOf(auditSourceType.name()), is(auditSourceType));
        }
    }
}