package org.lable.rfc3881.auditlogger.definition.rfc3881;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class DataLifeCycleTest {
    @Test
    public void uniquenessTest() {
        // Verify that all codes and display names defined are unique within this enumerator.
        Set<String> codes = new HashSet<>();
        Set<String> displayNames = new HashSet<>();

        for (DataLifeCycle dataLifeCycle : DataLifeCycle.values()) {
            codes.add(dataLifeCycle.getCode());
            displayNames.add(dataLifeCycle.getDisplayName());
        }

        assertThat(codes.size(), is(DataLifeCycle.values().length));
        assertThat(displayNames.size(), is(DataLifeCycle.values().length));
    }

    @Test
    public void toStringTest() {
        // Defer to CodeReference for toString.
        for (DataLifeCycle dataLifeCycle : DataLifeCycle.values()) {
            assertThat(dataLifeCycle.toString(), is(dataLifeCycle.toCodeReference().toString()));
        }
    }

    @Test
    public void valueOfTest() {
        // Test equality and valueOf.
        for (DataLifeCycle dataLifeCycle : DataLifeCycle.values()) {
            assertThat(DataLifeCycle.valueOf(dataLifeCycle.name()), is(dataLifeCycle));
        }
    }
}