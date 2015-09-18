package org.lable.rfc3881.auditlogger.definition.rfc3881.events;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class UserAccessEventTest {
    @Test
    public void uniquenessTest() {
        // Verify that all codes and display names defined are unique within this enumerator.
        Set<String> codes = new HashSet<>();
        Set<String> displayNames = new HashSet<>();

        for (UserAccessEvent event : UserAccessEvent.values()) {
            codes.add(event.getCode());
            displayNames.add(event.getDisplayName());
        }

        assertThat(codes.size(), is(UserAccessEvent.values().length));
        assertThat(displayNames.size(), is(UserAccessEvent.values().length));
    }

    @Test
    public void toStringTest() {
        // Defer to CodeReference for toString.
        for (UserAccessEvent event : UserAccessEvent.values()) {
            assertThat(event.toString(), is(event.toCodeReference().toString()));
        }
    }

    @Test
    public void valueOfTest() {
        // Test equality and valueOf.
        for (UserAccessEvent event : UserAccessEvent.values()) {
            assertThat(UserAccessEvent.valueOf(event.name()), is(event));
        }
    }
}