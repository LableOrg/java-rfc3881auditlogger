package org.lable.rfc3881.auditlogger.serialization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.Test;
import org.lable.codesystem.codereference.CodeReference;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class CodeReferenceDeserializerTest {
    @Test
    public void basicTest() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new CodeReferenceDeserializerModule());

        String json = "{" +
                "\"cs\":\"CS\"," +
                "\"code\":\"001\"," +
                "\"csn\":\"CodeSystem\"," +
                "\"dn\":\"One\"," +
                "\"ot\":\"ONE\"" +
                "}";

        CodeReference expected = new CodeReference("CS", "CodeSystem", "001", "One", "ONE");
        CodeReference output = objectMapper.readValue(json, CodeReference.class);

        assertThat(output, is(expected));
        assertThat(output.getCodeSystemName(), is (expected.getCodeSystemName()));
        assertThat(output.getOriginalText(), is (expected.getOriginalText()));
        assertThat(output.getDisplayName(), is (expected.getDisplayName()));
    }

    @Test
    public void nullFieldTest() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new CodeReferenceDeserializerModule());

        String json = "{" +
                "\"cs\":\"CS\"," +
                "\"code\":\"001\"," +
                "\"csn\":null," +
                "\"dn\":\"One\"" +
                "}";

        CodeReference expected = new CodeReference("CS", null, "001", "One", null);
        CodeReference output = objectMapper.readValue(json, CodeReference.class);

        assertThat(output, is(expected));
        assertThat(output.getCodeSystemName(), is (expected.getCodeSystemName()));
        assertThat(output.getOriginalText(), is (expected.getOriginalText()));
        assertThat(output.getDisplayName(), is(expected.getDisplayName()));
    }

    public static class CodeReferenceDeserializerModule extends SimpleModule {
        private static final long serialVersionUID = 1L;

        public CodeReferenceDeserializerModule() {
            addDeserializer(CodeReference.class, new CodeReferenceDeserializer());
        }
    }
}