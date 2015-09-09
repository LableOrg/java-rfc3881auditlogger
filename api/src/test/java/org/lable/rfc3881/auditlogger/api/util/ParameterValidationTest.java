package org.lable.rfc3881.auditlogger.api.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.lable.rfc3881.auditlogger.api.util.ParameterValidation.collectionMayNotBeNullOrEmpty;
import static org.lable.rfc3881.auditlogger.api.util.ParameterValidation.parameterMayNotBeNull;

public class ParameterValidationTest {
    @Test
    public void parameterMayNotBeNullTest() {
        parameterMayNotBeNull("name", "parameter");
    }

    @Test(expected = IllegalArgumentException.class)
    public void parameterMayNotBeNullWithNullTest() {
        parameterMayNotBeNull("name", null);
    }

    @Test
    public void collectionMayNotBeNullOrEmptyTest() {
        collectionMayNotBeNullOrEmpty("name", 1, 2, 3);
    }

    @Test(expected = IllegalArgumentException.class)
    public void collectionMayNotBeNullOrEmptyWithNullTest() {
        collectionMayNotBeNullOrEmpty("name", (Object[]) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void collectionMayNotBeNullOrEmptyWithEmptyTest() {
        collectionMayNotBeNullOrEmpty("name");
    }

    @Test
    public void collectionMayNotBeNullOrEmptyCollectionTest() {
        collectionMayNotBeNullOrEmpty("name", Arrays.asList(1, 2, 3));
    }

    @Test(expected = IllegalArgumentException.class)
    public void collectionMayNotBeNullOrEmptyCollectionWithNullTest() {
        collectionMayNotBeNullOrEmpty("name", (List<Character>) null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void collectionMayNotBeNullOrEmptyCollectionWithEmptyTest() {
        collectionMayNotBeNullOrEmpty("name", new ArrayList<String>());
    }

    // Code coverage.
    @Test
    public void instanceTest() throws NoSuchMethodException {
        new ParameterValidation();
    }

    @Test(expected = NoSuchMethodException.class)
    public void constructorNotAccessible() throws NoSuchMethodException {
        ParameterValidation.class.getConstructor();
    }
}