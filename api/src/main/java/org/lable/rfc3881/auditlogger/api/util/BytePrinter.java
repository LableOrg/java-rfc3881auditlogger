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
package org.lable.rfc3881.auditlogger.api.util;

/*
    Copied from Hadoop's Bytes class to prevent having to depend on it here in the generic module.
 */
public class BytePrinter {
    private static final char[] HEX_CHARS_UPPER = {
            '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'
    };

    public static String toStringBinary(final byte [] b) {
        return toStringBinary(b, 0, b.length);
    }

    /**
     * Write a printable representation of a byte array. Non-printable
     * characters are hex escaped in the format \\x%02X, eg:
     * \x00 \x05 etc
     *
     * @param b array to write out
     * @param off offset to start at
     * @param len length to write
     * @return string output
     */
    public static String toStringBinary(final byte [] b, int off, int len) {
        StringBuilder result = new StringBuilder();
        // Just in case we are passed a 'len' that is > buffer length...
        if (off >= b.length) return result.toString();
        if (off + len > b.length) len = b.length - off;
        for (int i = off; i < off + len ; ++i) {
            int ch = b[i] & 0xFF;
            if (ch >= ' ' && ch <= '~' && ch != '\\') {
                result.append((char)ch);
            } else {
                result.append("\\x");
                result.append(HEX_CHARS_UPPER[ch / 0x10]);
                result.append(HEX_CHARS_UPPER[ch % 0x10]);
            }
        }
        return result.toString();
    }
}
