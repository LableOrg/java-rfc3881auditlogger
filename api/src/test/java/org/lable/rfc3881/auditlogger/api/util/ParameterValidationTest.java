/*
 * Copyright (C) 2015 Lable (info@lable.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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