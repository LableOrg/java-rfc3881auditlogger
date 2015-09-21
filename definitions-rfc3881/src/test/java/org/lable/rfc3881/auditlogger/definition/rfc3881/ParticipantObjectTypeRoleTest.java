/**
 * Copyright (C) ${project.inceptionYear} Lable (info@lable.nl)
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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.junit.Assert.*;

public class ParticipantObjectTypeRoleTest {
    @Test
    public void uniquenessTest() {
        // Verify that all codes and display names defined are unique within this enumerator.
        Set<String> codes = new HashSet<>();
        Set<String> displayNames = new HashSet<>();

        for (ParticipantObjectTypeRole typeRole : ParticipantObjectTypeRole.values()) {
            codes.add(typeRole.getCode());
            displayNames.add(typeRole.getDisplayName());
        }

        assertThat(codes.size(), is(ParticipantObjectTypeRole.values().length));
        assertThat(displayNames.size(), is(ParticipantObjectTypeRole.values().length));
    }

    @Test
    public void applicableTest() {
        for (ParticipantObjectTypeRole typeRole : ParticipantObjectTypeRole.values()) {
            // Must have at least one applicable type.
            assertThat(typeRole.applicableTypes().length, is(greaterThanOrEqualTo(1)));
            for (Referenceable type : typeRole.applicableTypes()) {
                assertThat(typeRole.appliesTo(type), is(true));
            }
            // "Other" is never used in the predefined enums.
            assertThat(typeRole.appliesTo(ParticipantObjectType.OTHER), is(false));

            // Any other kind of Referenceable should not work.
            assertThat(typeRole.appliesTo(new CodeReference("fake", "fake", "fake")), is(false));
        }
    }

    @Test
    public void toStringTest() {
        // Defer to CodeReference for toString.
        for (ParticipantObjectTypeRole typeRole : ParticipantObjectTypeRole.values()) {
            assertThat(typeRole.toString(), is(typeRole.toCodeReference().toString()));
        }
    }

    @Test
    public void valueOfTest() {
        // Test equality and valueOf.
        for (ParticipantObjectTypeRole typeRole : ParticipantObjectTypeRole.values()) {
            assertThat(ParticipantObjectTypeRole.valueOf(typeRole.name()), is(typeRole));
        }
    }
}