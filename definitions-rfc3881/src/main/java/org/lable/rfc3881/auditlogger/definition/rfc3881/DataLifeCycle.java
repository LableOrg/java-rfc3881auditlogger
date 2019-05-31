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
package org.lable.rfc3881.auditlogger.definition.rfc3881;

import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;

import java.util.Optional;

/**
 * Identifier for the data life-cycle stage.
 * <p>
 * Defined by IETF/RFC 3881 §5.5.3. Participant Object Data Life Cycle.
 */
public enum DataLifeCycle implements Referenceable {
    /**
     * Origination / Creation.
     */
    ORIGINATION_OR_CREATION("1", "Origination / Creation"),
    /**
     * Import / Copy from original.
     */
    IMPORT_OR_COPY_FROM_ORIGINAL("2", "Import / Copy from original"),
    /**
     * Amendment.
     */
    AMENDMENT("3", "Amendment"),
    /**
     * Verification.
     */
    VERIFICATION("4", "Verification"),
    /**
     * Translation.
     */
    TRANSLATION("5", "Translation"),
    /**
     * Access / Use.
     */
    ACCESS_OR_USE("6", "Access / Use"),
    /**
     * De-identification.
     */
    DEIDENTIFICATION("7", "De-identification"),
    /**
     * Aggregation, summarization, derivation.
     */
    AGGREGATION_SUMMARIZATION_DERIVATION("8", "Aggregation, summarization, derivation"),
    /**
     * Report.
     */
    REPORT("9", "Report"),
    /**
     * Export / Copy to target.
     */
    EXPORT_OR_COPY_TO_TARGET("10", "Export / Copy to target"),
    /**
     * Disclosure.
     */
    DISCLOSURE("11", "Disclosure"),
    /**
     * Receipt of disclosure.
     */
    RECEIPT_OF_DISCLOSURE("12", "Receipt of disclosure"),
    /**
     * Archiving.
     */
    ARCHIVING("13", "Archiving"),
    /**
     * Logical deletion.
     */
    LOGICAL_DELETION("14", "Logical deletion"),
    /**
     * Permanent erasure / Physical destruction.
     */
    PERMANENT_ERASURE_OR_PHYSICAL_DESTRUCTION("15", "Permanent erasure / Physical destruction");

    static final String CODE_SYSTEM = "IETF/RFC3881.5.5.3";

    private final String code;
    private final String displayName;

    DataLifeCycle(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Optional<DataLifeCycle> fromReferenceable(Referenceable referenceable) {
        CodeReference cs = referenceable.toCodeReference();
        if (!cs.getCodeSystem().equals(CODE_SYSTEM)) return Optional.empty();

        String code = cs.getCode();
        if (code == null) return Optional.empty();

        for (DataLifeCycle value : values()) {
            if (value.getCode().equals(code)) return Optional.of(value);
        }

        return Optional.empty();
    }

    @Override
    public CodeReference toCodeReference() {
        return new CodeReference(
                CODE_SYSTEM,
                "IETF/RFC 3881, §5.5.3. Participant Object Data Life Cycle",
                getCode(),
                getDisplayName(),
                name());
    }

    @Override
    public String toString() {
        return toCodeReference().toString();
    }
}
