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
package org.lable.rfc3881.auditlogger.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.lable.codesystem.codereference.CodeReference;
import org.lable.codesystem.codereference.Referenceable;

import java.io.Serializable;
import java.util.Objects;

/**
 * A type/value pair that describes additional details about a {@link ParticipantObject}.
 * <p>
 * The byte value of this class may be interpreted by audit log analyzers at a later stage.
 */
public class Detail implements Serializable {
    final CodeReference type;

    final String value;

    @JsonCreator
    private Detail(@JsonProperty("type") CodeReference type,
                   @JsonProperty("value") String value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Define a detail.
     *
     * @param type  Code reference.
     * @param value Value.
     */
    public Detail(Referenceable type, String value) {
        this.type = type.toCodeReference();
        this.value = value;
    }

    public CodeReference getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;

        Detail that = (Detail) other;
        return Objects.equals(this.type, that.type) &&
                Objects.equals(this.value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, value);
    }

    @Override
    public String toString() {
        return getType().toString() + ":" + getValue();
    }
}
