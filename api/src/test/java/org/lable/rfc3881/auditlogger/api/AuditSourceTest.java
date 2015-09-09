package org.lable.rfc3881.auditlogger.api;

import org.junit.Test;
import org.lable.rfc3881.auditlogger.definition.rfc3881.AuditSourceType;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class AuditSourceTest {
    @Test
    public void auditSourceTypeToStringTest() {
        // Use CodeReference's toString.
        assertThat(
                AuditSourceType.END_USER_INTERFACE.toString(),
                is("IETF/RFC3881.5.4.3: 1 (End-user interface)")
        );
    }
}