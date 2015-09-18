package org.lable.rfc3881.auditlogger.definition.rfc3881.events;

import org.junit.Test;
import org.lable.rfc3881.auditlogger.definition.rfc3881.AuditSourceType;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class SecurityAdministrationEventTest {
    @Test
    public void uniquenessTest() {
        // Verify that all codes and display names defined are unique within this enumerator.
        Set<String> codes = new HashSet<>();
        Set<String> displayNames = new HashSet<>();

        for (SecurityAdministrationEvent event : SecurityAdministrationEvent.values()) {
            codes.add(event.getCode());
            displayNames.add(event.getDisplayName());
        }

        assertThat(codes.size(), is(SecurityAdministrationEvent.values().length));
        assertThat(displayNames.size(), is(SecurityAdministrationEvent.values().length));
    }

    @Test
    public void toStringTest() {
        // Defer to CodeReference for toString.
        for (SecurityAdministrationEvent event : SecurityAdministrationEvent.values()) {
            assertThat(event.toString(), is(event.toCodeReference().toString()));
        }
    }

    @Test
    public void valueOfTest() {
        // Test equality and valueOf.
        for (SecurityAdministrationEvent event : SecurityAdministrationEvent.values()) {
            assertThat(SecurityAdministrationEvent.valueOf(event.name()), is(event));
        }
    }
}