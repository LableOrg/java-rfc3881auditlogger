package org.lable.rfc3881.auditlogger.definition.rfc3881;

import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;

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

    @Override
    public CodeReference toCodeReference() {
        return new CodeReference(
                "IETF/RFC3881.5.5.3",
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
