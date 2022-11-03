/*
 * Copyright 2017 Anton Tananaev (anton@traccar.org)
 * Copyright 2017 Andrey Kunitsyn (andrey@traccar.org)
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
package org.traccar.reports.model;

public class TripsConfig {

    private double minimalTripDistance;
    private long minimalTripDuration;
    private long minimalParkingDuration;
    private long minimalNoDataDuration;
    private boolean useIgnition;
    private boolean processInvalidPositions;
    private double speedThreshold;

    public TripsConfig() {
    }

    public TripsConfig(double minimalTripDistance, long minimalTripDuration, long minimalParkingDuration,
                       long minimalNoDataDuration, boolean useIgnition, boolean processInvalidPositions, double speedThreshold) {
        this.minimalTripDistance = minimalTripDistance;
        this.minimalTripDuration = minimalTripDuration;
        this.minimalParkingDuration = minimalParkingDuration;
        this.minimalNoDataDuration = minimalNoDataDuration;
        this.useIgnition = useIgnition;
        this.processInvalidPositions = processInvalidPositions;
        this.speedThreshold = speedThreshold;
    }

    public double getMinimalTripDistance() {
        return minimalTripDistance;
    }

    public void setMinimalTripDistance(double minimalTripDistance) {
        this.minimalTripDistance = minimalTripDistance;
    }

    public long getMinimalTripDuration() {
        return minimalTripDuration;
    }

    public void setMinimalTripDuration(long minimalTripDuration) {
        this.minimalTripDuration = minimalTripDuration;
    }

    public long getMinimalParkingDuration() {
        return minimalParkingDuration;
    }

    public void setMinimalParkingDuration(long minimalParkingDuration) {
        this.minimalParkingDuration = minimalParkingDuration;
    }

    public long getMinimalNoDataDuration() {
        return minimalNoDataDuration;
    }

    public void setMinimalNoDataDuration(long minimalNoDataDuration) {
        this.minimalNoDataDuration = minimalNoDataDuration;
    }

    public boolean getUseIgnition() {
        return useIgnition;
    }

    public void setUseIgnition(boolean useIgnition) {
        this.useIgnition = useIgnition;
    }

    public boolean getProcessInvalidPositions() {
        return processInvalidPositions;
    }

    public void setProcessInvalidPositions(boolean processInvalidPositions) {
        this.processInvalidPositions = processInvalidPositions;
    }

    public double getSpeedThreshold() {
        return speedThreshold;
    }

    public void setSpeedThreshold(double speedThreshold) {
        this.speedThreshold = speedThreshold;
    }

}
