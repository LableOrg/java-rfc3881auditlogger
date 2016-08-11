/*
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

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class EventActionTest {
    @Test
    public void uniquenessTest() {
        // Verify that all codes and display names defined are unique within this enumerator.
        Set<Character> codes = new HashSet<>();
        Set<String> displayNames = new HashSet<>();

        for (EventAction eventAction : EventAction.values()) {
            codes.add(eventAction.getCode());
            displayNames.add(eventAction.getDisplayName());
        }

        assertThat(codes.size(), is(EventAction.values().length));
        assertThat(displayNames.size(), is(EventAction.values().length));
    }

    @Test
    public void toStringTest() {
        // Defer to CodeReference for toString.
        for (EventAction eventAction : EventAction.values()) {
            assertThat(eventAction.toString(), is(eventAction.toCodeReference().toString()));
        }
    }

    @Test
    public void valueOfTest() {
        // Test equality and valueOf.
        for (EventAction eventAction : EventAction.values()) {
            assertThat(EventAction.valueOf(eventAction.name()), is(eventAction));
        }
    }
}