package org.lable.rfc3881.auditlogger.definition.rfc3881;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class NetworkAccessPointTypeTest {
    @Test
    public void uniquenessTest() {
        // Verify that all codes and display names defined are unique within this enumerator.
        Set<String> codes = new HashSet<>();
        Set<String> displayNames = new HashSet<>();

        for (NetworkAccessPointType networkAccessPointType : NetworkAccessPointType.values()) {
            codes.add(networkAccessPointType.getCode());
            displayNames.add(networkAccessPointType.getDisplayName());
        }

        assertThat(codes.size(), is(NetworkAccessPointType.values().length));
        assertThat(displayNames.size(), is(NetworkAccessPointType.values().length));
    }

    @Test
    public void toStringTest() {
        // Defer to CodeReference for toString.
        for (NetworkAccessPointType networkAccessPointType : NetworkAccessPointType.values()) {
            assertThat(networkAccessPointType.toString(), is(networkAccessPointType.toCodeReference().toString()));
        }
    }

    @Test
    public void valueOfTest() {
        // Test equality and valueOf.
        for (NetworkAccessPointType networkAccessPointType : NetworkAccessPointType.values()) {
            assertThat(NetworkAccessPointType.valueOf(networkAccessPointType.name()), is(networkAccessPointType));
        }
    }
}