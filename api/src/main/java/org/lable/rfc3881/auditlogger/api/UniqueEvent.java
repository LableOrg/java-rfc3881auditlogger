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

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UniqueEvent extends Event {
    /**
     * Unique 8 byte identifier for this event.
     */
    final long uid;

    private UniqueEvent(Event event, long uid) {
        super(
                event.getId(),
                event.getAction(),
                event.getHappenedAt(),
                event.getOutcome(),
                event.getTypes()
        );
        this.uid = uid;
    }

    public static UniqueEvent fromEvent(Event event, long uid) {
        return new UniqueEvent(event, uid);
    }

    /**
     * @return Unique 8 byte identifier.
     */
    @JsonIgnore
    public long getUid() {
        return uid;
    }

    public EventId toId() {
        return new EventId(getId(), getHappenedAt(), uid);
    }
}
