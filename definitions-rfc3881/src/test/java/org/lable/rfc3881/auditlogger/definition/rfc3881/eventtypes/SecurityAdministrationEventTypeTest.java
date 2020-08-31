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
package org.lable.rfc3881.auditlogger.definition.rfc3881.eventtypes;

import org.junit.Test;
import org.lable.codesystem.codereference.CodeReference;

import java.util.HashSet;
import java.util.Set;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.lable.rfc3881.auditlogger.definition.rfc3881.eventtypes.SecurityAdministrationEventType.*;

public class SecurityAdministrationEventTypeTest {
    @Test
    public void uniquenessTest() {
        // Verify that all codes and display names defined are unique within this enumerator.
        Set<String> codes = new HashSet<>();
        Set<String> displayNames = new HashSet<>();

        for (SecurityAdministrationEventType event : SecurityAdministrationEventType.values()) {
            codes.add(event.getCode());
            displayNames.add(event.getDisplayName());
        }

        assertThat(codes.size(), is(SecurityAdministrationEventType.values().length));
        assertThat(displayNames.size(), is(SecurityAdministrationEventType.values().length));
    }

    @Test
    public void fromReferenceableTest() {
        assertThat(fromReferenceable(new CodeReference(CODE_SYSTEM, "DATA_DEFINITION")), hasValue(DATA_DEFINITION));
        assertThat(fromReferenceable(new CodeReference(CODE_SYSTEM, "USER_DEFINITION")), hasValue(USER_DEFINITION));

        assertThat(fromReferenceable(new CodeReference(CODE_SYSTEM, "?")), isEmpty());
        assertThat(fromReferenceable(new CodeReference("?", "USER_DEFINITION")), isEmpty());
    }

    @Test
    public void toStringTest() {
        // Defer to CodeReference for toString.
        for (SecurityAdministrationEventType event : SecurityAdministrationEventType.values()) {
            assertThat(event.toString(), is(event.toCodeReference().toString()));
        }
    }

    @Test
    public void valueOfTest() {
        // Test equality and valueOf.
        for (SecurityAdministrationEventType event : SecurityAdministrationEventType.values()) {
            assertThat(SecurityAdministrationEventType.valueOf(event.name()), is(event));
        }
    }
}