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
package org.lable.rfc3881.auditlogger.api;

import org.junit.Test;
import org.lable.codesystem.codereference.Categorizable;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;
import org.lable.rfc3881.auditlogger.definition.rfc3881.EventAction;
import org.lable.rfc3881.auditlogger.definition.rfc3881.EventOutcome;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EventTest {
    @Test
    public void toStringTest() {
        Event event = new Event(
                new CodeReference("CS", "01", "test"),
                EventAction.CREATE,
                Instant.parse("2015-09-01T14:00:00.001Z").toEpochMilli(),
                EventOutcome.SUCCESS);

        String expected = "ID:          CS: 01 (test)\n" +
                "Action:      Create\n" +
                "At:          2015-09-01T14:00:00.001Z\n" +
                "Outcome:     Success\n" +
                "Type:        []";

        assertThat(event.toString(), is(expected));
    }

    @Test
    public void toStringWithTypesTest() {
        Event event = new Event(
                new CodeReference("CS", "01", "test"),
                EventAction.CREATE,
                Instant.parse("2015-09-01T14:00:00.001Z").toEpochMilli(),
                EventOutcome.SUCCESS,
                new CodeReference("A", "1", "xx"),
                new CodeReference("B", "2", "yy"));

        String expected = "ID:          CS: 01 (test)\n" +
                "Action:      Create\n" +
                "At:          2015-09-01T14:00:00.001Z\n" +
                "Outcome:     Success\n" +
                "Type:        [A: 1 (xx), B: 2 (yy)]";

        assertThat(event.toString(), is(expected));
    }

    @Test
    public void categorizableConstructorTest() {
        Event event = new Event(new CategorizableTest(), EventAction.EXECUTE, EventOutcome.SUCCESS);

        assertThat(event.getTypes().size(), is(1));
        assertThat(event.getTypes().get(0), is((Referenceable) new CodeReference("cat", "ZZZ")));
    }

    public static class CategorizableTest implements Categorizable {
        @Override
        public List<Referenceable> categorizedUnder() {
            return Collections.singletonList((Referenceable) new CodeReference("cat", "ZZZ"));
        }

        @Override
        public CodeReference toCodeReference() {
            return new CodeReference("test", "XXX");
        }
    }
}