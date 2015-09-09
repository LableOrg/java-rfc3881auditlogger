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
