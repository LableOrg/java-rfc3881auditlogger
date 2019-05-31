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
import org.lable.codesystem.codereference.Referenceable;

import java.util.HashSet;
import java.util.Set;

import static com.github.npathai.hamcrestopt.OptionalMatchers.hasValue;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.*;
import static org.lable.rfc3881.auditlogger.definition.rfc3881.ParticipantObjectIDType.*;

public class ParticipantObjectIDTypeTest {
    @Test
    public void uniquenessTest() {
        // Verify that all codes and display names defined are unique within this enumerator.
        Set<String> codes = new HashSet<>();
        Set<String> displayNames = new HashSet<>();

        for (ParticipantObjectIDType idType : ParticipantObjectIDType.values()) {
            codes.add(idType.getCode());
            displayNames.add(idType.getDisplayName());
        }

        assertThat(codes.size(), is(ParticipantObjectIDType.values().length));
        assertThat(displayNames.size(), is(ParticipantObjectIDType.values().length));
    }

    @Test
    public void fromReferenceableTest() {
        assertThat(fromReferenceable(new CodeReference(CODE_SYSTEM, "1")), hasValue(MEDICAL_RECORD_NUMBER));
        assertThat(fromReferenceable(new CodeReference(CODE_SYSTEM, "12")), hasValue(URI));

        assertThat(fromReferenceable(new CodeReference(CODE_SYSTEM, "?")), isEmpty());
        assertThat(fromReferenceable(new CodeReference("?", "1")), isEmpty());
    }

    @Test
    public void applicableTest() {
        for (ParticipantObjectIDType idType : ParticipantObjectIDType.values()) {
            // Must have at least one applicable type.
            assertThat(idType.applicableTypes().length, is(greaterThanOrEqualTo(1)));
            for (Referenceable type : idType.applicableTypes()) {
                assertThat(idType.appliesTo(type), is(true));
            }
            // "Other" is never used in the predefined enums.
            assertThat(idType.appliesTo(ParticipantObjectType.OTHER), is(false));

            // Any other kind of Referenceable should not work.
            assertThat(idType.appliesTo(new CodeReference("fake", "fake", "fake")), is(false));
        }
    }

    @Test
    public void toStringTest() {
        // Defer to CodeReference for toString.
        for (ParticipantObjectIDType idType : ParticipantObjectIDType.values()) {
            assertThat(idType.toString(), is(idType.toCodeReference().toString()));
        }
    }

    @Test
    public void valueOfTest() {
        // Test equality and valueOf.
        for (ParticipantObjectIDType idType : ParticipantObjectIDType.values()) {
            assertThat(ParticipantObjectIDType.valueOf(idType.name()), is(idType));
        }
    }
}