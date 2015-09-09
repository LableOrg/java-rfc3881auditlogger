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