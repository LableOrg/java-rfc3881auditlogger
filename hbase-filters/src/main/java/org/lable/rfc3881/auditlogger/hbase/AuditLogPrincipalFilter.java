/*
 * Copyright © 2015 Lable (info@lable.nl)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.lable.rfc3881.auditlogger.hbase;

import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.exceptions.DeserializationException;
import org.apache.hadoop.hbase.filter.FilterBase;
import org.apache.hadoop.hbase.util.Bytes;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Filter out rows which do not include a principal matching this filter. This filter can match on (part of the)
 * account-domain or the full user identifier, and looks in the requestor, delegator, and principal fields of the log
 * record.
 */
public class AuditLogPrincipalFilter extends FilterBase {
    // 'delegator' followed by a zero-byte.
    static byte[] DELEGATOR_PREFIX = new byte[]{100, 101, 108, 101, 103, 97, 116, 111, 114, 0};
    // 'principal' followed by a zero-byte.
    static byte[] PRINCIPAL_PREFIX = new byte[]{112, 114, 105, 110, 99, 105, 112, 97, 108, 0};
    // 'requestor' followed by a zero-byte.
    static byte[] REQUESTOR_PREFIX = new byte[]{114, 101, 113, 117, 101, 115, 116, 111, 114, 0};

    protected byte[] columnFamily;
    protected FilterMode filterMode;
    protected String match;

    protected boolean matchedQualifier = false;

    /**
     * Standard constructor.
     *
     * @param family     Column family to use.
     * @param filterMode Filter mode to apply filter string to.
     * @param match      Filter string.
     */
    public AuditLogPrincipalFilter(final byte[] family,
                                   final FilterMode filterMode,
                                   final String match) {
        this.columnFamily = family;
        this.filterMode = filterMode;
        this.match = match;
    }

    /**
     * Easy string-only constructor for Hbase shell.
     *
     * @param family     Column family to use.
     * @param filterMode Filter mode to apply filter string to. Can be provided as its two-letter abbreviation or the
     *                   full enum name (values like {@code EXACT_DOMAIN} and {@code exact-domain} work).
     * @param match      Filter string.
     */
    public AuditLogPrincipalFilter(final String family,
                                   final String filterMode,
                                   final String match) throws IllegalArgumentException {
        if (family == null) throw new IllegalArgumentException("Column family cannot be null.");
        if (match == null) throw new IllegalArgumentException("Match string cannot be null.");

        this.columnFamily = family.getBytes(StandardCharsets.UTF_8);
        this.filterMode = FilterMode.fromString(filterMode).orElseThrow(() -> new IllegalArgumentException("Unknown FilterMode: " + filterMode));
        this.match = match;
    }

    public FilterMode getFilterMode() {
        return filterMode;
    }

    public String getMatch() {
        return match;
    }

    public byte[] getFamily() {
        return columnFamily;
    }

    @Override
    public ReturnCode filterCell(final Cell c) {
        if (this.matchedQualifier) {
            // We already found a matching qualifier, all remaining keys now pass.
            return ReturnCode.INCLUDE;
        }

        if (filterQualifier(c)) {
            return ReturnCode.INCLUDE;
        }

        // Matched a qualifier.
        this.matchedQualifier = true;
        return ReturnCode.INCLUDE;
    }

    boolean filterQualifier(final Cell cell) {
        int qLen = cell.getQualifierLength();

        // Ignore column qualifiers too short to represent a principal.
        if (qLen < 11) return true;

        byte[] qBytes = cell.getQualifierArray();
        int qOffset = cell.getQualifierOffset();
        // Ignore cells not starting with one of the principal-prefixes.
        if (!matchesPrincipalPrefix(qBytes, qOffset)) {
            return true;
        }

        String principal = Bytes.toString(qBytes, qOffset + 10, qLen - 10);
        return !filterMode.matches(principal, match);
    }

    static boolean matchesPrincipalPrefix(byte[] qualifierArray, int qualifierOffset) {
        return matchesPrincipalPrefix(qualifierArray, qualifierOffset, 0, true, true, true);
    }

    static boolean matchesPrincipalPrefix(byte[] qualifierArray,
                                          int qualifierOffset,
                                          int pos,
                                          boolean couldBeRequestor,
                                          boolean couldBeDelegator,
                                          boolean couldBePrincipal) {
        // There are three prefixes which mark a principal of a record; requestor, delegator, and principal,
        // followed by a zero-byte. Here we check if the first ten bytes of the column qualifier match one of these
        // three. They are all the same length, so we can limit the check to exactly the first 10 bytes.
        if (!couldBeRequestor && !couldBeDelegator && !couldBePrincipal) return false;
        if (pos == 10) return true;

        byte b = qualifierArray[qualifierOffset + pos];
        if (couldBeRequestor) {
            couldBeRequestor = REQUESTOR_PREFIX[pos] == b;
        }
        if (couldBeDelegator) {
            couldBeDelegator = DELEGATOR_PREFIX[pos] == b;
        }
        if (couldBePrincipal) {
            couldBePrincipal = PRINCIPAL_PREFIX[pos] == b;
        }

        return matchesPrincipalPrefix(qualifierArray, qualifierOffset, pos + 1, couldBeRequestor, couldBeDelegator, couldBePrincipal);
    }

    @Override
    public boolean filterRowKey(Cell cell) throws IOException {
        return false;
    }

    @Override
    public boolean filterRow() {
        return !this.matchedQualifier;
    }

    @Override
    public boolean hasFilterRow() {
        return true;
    }

    @Override
    public void reset() {
        matchedQualifier = false;
    }

    @Override
    public byte[] toByteArray() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        dos.writeInt(columnFamily.length);
        dos.write(columnFamily);

        dos.writeBytes(filterMode.getAbbr());

        dos.writeBytes(match);

        dos.flush();

        return baos.toByteArray();
    }

    public static AuditLogPrincipalFilter parseFrom(final byte[] bytes) throws DeserializationException {
        ByteBuffer bb = ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN);

        byte[] family = new byte[bb.getInt()];
        bb.get(family);

        // Fixed length abbreviation.
        byte[] modeBytes = new byte[2];
        bb.get(modeBytes);
        String filterAbbr = Bytes.toString(modeBytes);
        FilterMode filterMode = FilterMode.fromAbbr(filterAbbr)
                .orElseThrow(() -> new DeserializationException("Unknown FilterMode: " + filterAbbr));

        byte[] matchBytes = new byte[bb.remaining()];
        bb.get(matchBytes);
        String match = Bytes.toString(matchBytes);

        return new AuditLogPrincipalFilter(
                family,
                filterMode,
                match
        );
    }

    public enum FilterMode {
        EXACT_PRINCIPAL("EP") {
            @Override
            public boolean matches(String principal, String match) {
                return principal.equals(match);
            }
        },
        EXACT_DOMAIN("ED") {
            @Override
            public boolean matches(String principal, String match) {
                String[] parts = principal.split("//");
                if (parts.length < 2) return false;

                return parts[0].equals(match);
            }
        },
        DOMAIN_PREFIX("PD") {
            @Override
            public boolean matches(String principal, String match) {
                String[] parts = principal.split("//");
                if (parts.length < 2) return false;

                return parts[0].startsWith(match);
            }
        },
        DOMAIN_SUBSTRING("SD") {
            @Override
            public boolean matches(String principal, String match) {
                String[] parts = principal.split("//");
                if (parts.length < 2) return false;

                return parts[0].contains(match);
            }
        },
        DOMAIN_REGEX("RD") {
            @Override
            public boolean matches(String principal, String match) {
                String[] parts = principal.split("//");
                if (parts.length < 2) return false;

                return parts[0].matches(match);
            }
        },
        ;

        private final String abbr;

        FilterMode(String abbr) {
            this.abbr = abbr;
        }

        public static Optional<FilterMode> fromString(String name) {
            Optional<FilterMode> fromAbbr = fromAbbr(name);
            if (fromAbbr.isPresent()) return fromAbbr;

            name = name.toUpperCase()
                    .replace(' ', '_')
                    .replace('-', '_');
            for (FilterMode filterMode : values()) {
                if (filterMode.name().equals(name)) return Optional.of(filterMode);
            }

            return Optional.empty();
        }

        public String getAbbr() {
            return this.abbr;
        }

        public abstract boolean matches(String principal, String match);

        public static Optional<FilterMode> fromAbbr(String abbr) {
            if (abbr == null) return Optional.empty();
            switch (abbr.toUpperCase()) {
                case "ED":
                    return Optional.of(FilterMode.EXACT_DOMAIN);
                case "PD":
                    return Optional.of(FilterMode.DOMAIN_PREFIX);
                case "SD":
                    return Optional.of(FilterMode.DOMAIN_SUBSTRING);
                case "RD":
                    return Optional.of(FilterMode.DOMAIN_REGEX);
                case "EP":
                    return Optional.of(FilterMode.EXACT_PRINCIPAL);
                default:
                    return Optional.empty();
            }
        }
    }
}
