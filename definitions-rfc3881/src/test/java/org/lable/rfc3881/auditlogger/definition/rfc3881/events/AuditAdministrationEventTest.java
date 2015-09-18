package org.lable.rfc3881.auditlogger.definition.rfc3881.events;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class AuditAdministrationEventTest {
    @Test
    public void uniquenessTest() {
        // Verify that all codes and display names defined are unique within this enumerator.
        Set<String> codes = new HashSet<>();
        Set<String> displayNames = new HashSet<>();

        for (AuditAdministrationEvent event : AuditAdministrationEvent.values()) {
            codes.add(event.getCode());
            displayNames.add(event.getDisplayName());
        }

        assertThat(codes.size(), is(AuditAdministrationEvent.values().length));
        assertThat(displayNames.size(), is(AuditAdministrationEvent.values().length));
    }

    @Test
    public void toStringTest() {
        // Defer to CodeReference for toString.
        for (AuditAdministrationEvent event : AuditAdministrationEvent.values()) {
            assertThat(event.toString(), is(event.toCodeReference().toString()));
        }
    }

    @Test
    public void valueOfTest() {
        // Test equality and valueOf.
        for (AuditAdministrationEvent event : AuditAdministrationEvent.values()) {
            assertThat(AuditAdministrationEvent.valueOf(event.name()), is(event));
        }
    }
}