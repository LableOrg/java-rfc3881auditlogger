package org.lable.rfc3881.auditlogger.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.Test;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;

import java.io.IOException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class ReferenceableSerializerTest {
    @Test
    public void basicTest() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new ReferenceableSerializerModule());

        CodeReference codeReference = new CodeReference("CS", "CodeSystem", "001", "One", "ONE");

        String result = objectMapper.writeValueAsString(codeReference);

        assertThat(result, is("{" +
                "\"cs\":\"CS\"," +
                "\"code\":\"001\"," +
                "\"csn\":\"CodeSystem\"," +
                "\"dn\":\"One\"," +
                "\"ot\":\"ONE\"" +
                "}"));
    }

    public static class ReferenceableSerializerModule extends SimpleModule {
        private static final long serialVersionUID = 1L;

        public ReferenceableSerializerModule() {
            addSerializer(Referenceable.class, new ReferenceableSerializer());
        }
    }
}