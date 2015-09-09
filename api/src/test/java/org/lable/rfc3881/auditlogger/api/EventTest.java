package org.lable.rfc3881.auditlogger.api;

import org.joda.time.Instant;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.rfc3881.auditlogger.definition.rfc3881.EventAction;
import org.lable.rfc3881.auditlogger.definition.rfc3881.EventOutcome;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class EventTest {
    @Test
    public void toStringTest() {
        Event event = new Event(
                new CodeReference("CS", "01", "test"),
                EventAction.CREATE,
                Instant.parse("2015-09-01T14:00:00.000", ISODateTimeFormat.dateHourMinuteSecondMillis().withZoneUTC()),
                EventOutcome.SUCCESS);

        String expected = "ID:          CS: 01 (test)\n" +
                "Action:      Create\n" +
                "At:          2015-09-01T14:00:00.000 (UTC)\n" +
                "Outcome:     Success\n" +
                "Type:        []";

        assertThat(event.toString(), is(expected));
    }

    @Test
    public void toStringWithTypesTest() {
        Event event = new Event(
                new CodeReference("CS", "01", "test"),
                EventAction.CREATE,
                Instant.parse("2015-09-01T14:00:00.000", ISODateTimeFormat.dateHourMinuteSecondMillis().withZoneUTC()),
                EventOutcome.SUCCESS,
                new CodeReference("A", "1", "xx"),
                new CodeReference("B", "2", "yy"));

        String expected = "ID:          CS: 01 (test)\n" +
                "Action:      Create\n" +
                "At:          2015-09-01T14:00:00.000 (UTC)\n" +
                "Outcome:     Success\n" +
                "Type:        [A: 1 (xx), B: 2 (yy)]";

        assertThat(event.toString(), is(expected));
    }
}