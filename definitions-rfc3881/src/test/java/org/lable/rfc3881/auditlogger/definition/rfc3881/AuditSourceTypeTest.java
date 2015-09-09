package org.lable.rfc3881.auditlogger.definition.rfc3881;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class AuditSourceTypeTest {
    @Test
    public void uniquenessTest() {
        // Verify that all codes and display names defined are unique within this enumerator.
        Set<String> codes = new HashSet<>();
        Set<String> displayNames = new HashSet<>();

        for (AuditSourceType auditSourceType : AuditSourceType.values()) {
            codes.add(auditSourceType.getCode());
            displayNames.add(auditSourceType.getDisplayName());
        }

        assertThat(codes.size(), is(AuditSourceType.values().length));
        assertThat(displayNames.size(), is(AuditSourceType.values().length));
    }

    @Test
    public void toStringTest() {
        // Defer to CodeReference for toString.
        for (AuditSourceType auditSourceType : AuditSourceType.values()) {
            assertThat(auditSourceType.toString(), is(auditSourceType.toCodeReference().toString()));
        }
    }

    @Test
    public void valueOfTest() {
        // Test equality and valueOf.
        for (AuditSourceType auditSourceType : AuditSourceType.values()) {
            assertThat(AuditSourceType.valueOf(auditSourceType.name()), is(auditSourceType));
        }
    }
}