package org.lable.rfc3881.auditlogger.api;

import org.junit.Test;
import org.lable.rfc3881.auditlogger.definition.rfc3881.ParticipantObjectType;
import org.lable.rfc3881.auditlogger.definition.rfc3881.ParticipantObjectTypeRole;

import static org.lable.rfc3881.auditlogger.api.ParticipantObject.verifyApplicability;

public class ParticipantObjectTest {
    @Test
    public void verifyApplicabilityTestA() {
        verifyApplicability("test", ParticipantObjectTypeRole.DOCTOR, ParticipantObjectType.PERSON);
    }

    @Test(expected = IllegalArgumentException.class)
    public void verifyApplicabilityTestB() {
        verifyApplicability("test", ParticipantObjectTypeRole.DOCTOR, ParticipantObjectType.SYSTEM_OBJECT);
    }
}