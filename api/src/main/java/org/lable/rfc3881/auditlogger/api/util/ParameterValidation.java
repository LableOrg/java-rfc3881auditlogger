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

import java.util.Collection;

/**
 * Static utility methods for simple argument validation in methods.
 */
public class ParameterValidation {
    ParameterValidation() { /* Static helper class. */}

    public static <T> void parameterMayNotBeNull(String name, T parameter) {
        if (parameter == null) {
            throw new IllegalArgumentException("Parameter " + name + " is a required attribute (null passed).");
        }
    }

    @SafeVarargs
    public static <T> void collectionMayNotBeNullOrEmpty(String name, T... parameter) {
        if (parameter == null) {
            throw new IllegalArgumentException("Parameter " + name + " is a required attribute (null passed).");
        }

        if (parameter.length == 0) {
            throw new IllegalArgumentException("Parameter " + name + " is a required attribute (empty set passed).");
        }
    }

    public static <T> void collectionMayNotBeNullOrEmpty(String name, Collection<T> parameter) {
        if (parameter == null) {
            throw new IllegalArgumentException("Parameter " + name + " is a required attribute (null passed).");
        }

        if (parameter.isEmpty()) {
            throw new IllegalArgumentException("Parameter " + name + " is a required attribute " +
                    "(empty collection passed).");
        }
    }
}
