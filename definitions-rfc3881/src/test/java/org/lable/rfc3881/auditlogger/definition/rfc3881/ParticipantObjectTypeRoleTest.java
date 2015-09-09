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