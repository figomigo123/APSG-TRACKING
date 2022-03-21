/*
 * Copyright 2015 - 2017 Anton Tananaev (anton@traccar.org)
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
package org.traccar.api.resource;

import org.traccar.Context;
import org.traccar.api.BaseObjectResource;
import org.traccar.database.DataManager;
import org.traccar.database.DeviceManager;
import org.traccar.model.Device;
import org.traccar.model.Event;
import org.traccar.model.User;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Path("dashboard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DashBoardResource extends BaseObjectResource<User> {

    public DashBoardResource() {
        super(User.class);
    }

    @Path("/devices")
    @GET
    public Map<String, Integer> getDevicesSatistics() throws SQLException {
        Map<String, Integer> devicesStatusCount = new HashMap<>();
        DeviceManager deviceManager = Context.getDeviceManager();
        Set<Long> result;
        result = deviceManager.getAllItems();
        Collection<Device> devices = deviceManager.getItems(result);
        int offLine = 0, onLine = 0, unKnown = 0;
        for (Device device : devices) {
            switch (device.getStatus()) {
                case "unknown":
                    unKnown++;
                    break;
                case "online":
                    onLine++;
                    break;
                case "offline":
                    offLine++;
            }
        }
        devicesStatusCount.put("unknown", unKnown);
        devicesStatusCount.put("online", onLine);
        devicesStatusCount.put("offline", offLine);

        return devicesStatusCount;
    }

    /**
     * Alarm
     * Command Result
     * Geofence
     * Ignition
     * Maintenance
     * Motion
     * Overspeed
     * Status
     * Text Message
     *
     * @return
     * @throws SQLException
     */
    @Path("/events")
    @GET
    public Map<String, Long> getEventsSatistics() throws SQLException {

        DataManager db = Context.getDataManager();
        Map<String, Long> eventsCount = new HashMap<>();
        Map<String, Long> dbeventsCount = db.getEventsCountByType();
        eventsCount.put(Event.TYPE_ALARM, 0l);
        eventsCount.put(Event.TYPE_COMMAND_RESULT, 0l);
        eventsCount.put(Event.TYPE_DEVICE_FUEL_DROP, 0l);
        eventsCount.put(Event.TYPE_DEVICE_INACTIVE, 0l);
        eventsCount.put(Event.TYPE_DEVICE_MOVING, 0l);
        eventsCount.put(Event.TYPE_DEVICE_OFFLINE, 0l);
        eventsCount.put(Event.TYPE_DEVICE_ONLINE, 0l);
        eventsCount.put(Event.TYPE_DEVICE_OVERSPEED, 0l);
        eventsCount.put(Event.TYPE_DEVICE_STOPPED, 0l);
        eventsCount.put(Event.TYPE_DEVICE_UNKNOWN, 0l);
        eventsCount.put(Event.TYPE_DRIVER_CHANGED, 0l);
        eventsCount.put(Event.TYPE_GEOFENCE_ENTER, 0l);
        eventsCount.put(Event.TYPE_GEOFENCE_EXIT, 0l);
        eventsCount.put(Event.TYPE_IGNITION_OFF, 0l);
        eventsCount.put(Event.TYPE_IGNITION_ON, 0l);
        eventsCount.put(Event.TYPE_MAINTENANCE, 0l);
        eventsCount.put(Event.TYPE_TEXT_MESSAGE, 0l);
        dbeventsCount.forEach((k, v) -> {
            eventsCount.put(k, v);
        });
        return eventsCount;


    }


}
