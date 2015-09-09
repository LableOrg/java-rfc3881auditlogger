package org.lable.rfc3881.auditlogger.definition.rfc3881;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class EventOutcomeTest {
    @Test
    public void uniquenessTest() {
        // Verify that all codes and display names defined are unique within this enumerator.
        Set<Integer> codes = new HashSet<>();
        Set<String> displayNames = new HashSet<>();

        for (EventOutcome eventOutcome : EventOutcome.values()) {
            codes.add(eventOutcome.getCode());
            displayNames.add(eventOutcome.getDisplayName());
        }

        assertThat(codes.size(), is(EventOutcome.values().length));
        assertThat(displayNames.size(), is(EventOutcome.values().length));
    }

    @Test
    public void toStringTest() {
        // Defer to CodeReference for toString.
        for (EventOutcome eventOutcome : EventOutcome.values()) {
            assertThat(eventOutcome.toString(), is(eventOutcome.toCodeReference().toString()));
        }
    }

    @Test
    public void valueOfTest() {
        // Test equality and valueOf.
        for (EventOutcome eventOutcome : EventOutcome.values()) {
            assertThat(EventOutcome.valueOf(eventOutcome.name()), is(eventOutcome));
        }
    }
}