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
package org.lable.rfc3881.auditlogger.definition.rfc3881;

import org.junit.Test;
import org.lable.codesystem.codereference.CodeReference;

import java.util.HashSet;
import java.util.Set;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.lable.rfc3881.auditlogger.definition.rfc3881.EventOutcome.*;
import static org.lable.rfc3881.auditlogger.definition.rfc3881.EventOutcome.fromReferenceable;

public class EventOutcomeTest {
    @Test
    public void uniquenessTest() {
        // Verify that all codes and display names defined are unique within this enumerator.
        Set<Integer> codes = new HashSet<>();
        Set<String> displayNames = new HashSet<>();

        for (EventOutcome eventOutcome : values()) {
            codes.add(eventOutcome.getCode());
            displayNames.add(eventOutcome.getDisplayName());
        }

        assertThat(codes.size(), is(values().length));
        assertThat(displayNames.size(), is(values().length));
    }

    @Test
    public void fromReferenceableTest() {
        assertThat(fromReferenceable(new CodeReference(CODE_SYSTEM, "0")), hasValue(SUCCESS));
        assertThat(fromReferenceable(new CodeReference(CODE_SYSTEM, "4")), hasValue(MINOR_FAILURE));
        assertThat(fromReferenceable(new CodeReference(CODE_SYSTEM, "8")), hasValue(SERIOUS_FAILURE));
        assertThat(fromReferenceable(new CodeReference(CODE_SYSTEM, "12")), hasValue(MAJOR_FAILURE));

        assertThat(fromReferenceable(new CodeReference(EventAction.CODE_SYSTEM, "6")), isEmpty());
        assertThat(fromReferenceable(new CodeReference(EventAction.CODE_SYSTEM, "?")), isEmpty());
        assertThat(fromReferenceable(new CodeReference("?", "0")), isEmpty());
    }

    @Test
    public void toStringTest() {
        // Defer to CodeReference for toString.
        for (EventOutcome eventOutcome : values()) {
            assertThat(eventOutcome.toString(), is(eventOutcome.toCodeReference().toString()));
        }
    }

    @Test
    public void valueOfTest() {
        // Test equality and valueOf.
        for (EventOutcome eventOutcome : values()) {
            assertThat(valueOf(eventOutcome.name()), is(eventOutcome));
        }
    }
}