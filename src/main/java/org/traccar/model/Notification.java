/*
 * Copyright 2016 - 2018 Anton Tananaev (anton@traccar.org)
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
package org.traccar.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.traccar.database.QueryIgnore;

import java.util.HashSet;
import java.util.Set;

public class Notification extends ScheduledModel {

    private boolean always;
    private String type;
    private String notificators;

    public boolean getAlways() {
        return always;
    }

    public void setAlways(boolean always) {
        this.always = always;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNotificators() {
        return notificators;
    }

    public void setNotificators(String transports) {
        this.notificators = transports;
    }


    @JsonIgnore
    @QueryIgnore
    public Set<String> getNotificatorsTypes() {
        final Set<String> result = new HashSet<>();
        if (notificators != null) {
            final String[] transportsList = notificators.split(",");
            for (String transport : transportsList) {
                result.add(transport.trim());
            }
        }
        return result;
    }

    @Override
    public String toString() {
        return "Notification{" +
                "always=" + always +
                ", type='" + type + '\'' +
                ", notificators='" + notificators + '\'' +
                "} " + super.toString();
    }
}
