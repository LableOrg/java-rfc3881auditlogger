package org.lable.rfc3881.auditlogger.definition.rfc3881;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class ParticipantObjectTypeTest {
    @Test
    public void uniquenessTest() {
        // Verify that all codes and display names defined are unique within this enumerator.
        Set<String> codes = new HashSet<>();
        Set<String> displayNames = new HashSet<>();

        for (ParticipantObjectType type : ParticipantObjectType.values()) {
            codes.add(type.getCode());
            displayNames.add(type.getDisplayName());
        }

        assertThat(codes.size(), is(ParticipantObjectType.values().length));
        assertThat(displayNames.size(), is(ParticipantObjectType.values().length));
    }

    @Test
    public void toStringTest() {
        // Defer to CodeReference for toString.
        for (ParticipantObjectType type : ParticipantObjectType.values()) {
            assertThat(type.toString(), is(type.toCodeReference().toString()));
        }
    }

    @Test
    public void valueOfTest() {
        // Test equality and valueOf.
        for (ParticipantObjectType type : ParticipantObjectType.values()) {
            assertThat(ParticipantObjectType.valueOf(type.name()), is(type));
        }
    }
}