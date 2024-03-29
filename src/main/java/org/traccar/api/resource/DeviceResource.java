/*
 * Copyright 2015 - 2018 Anton Tananaev (anton@traccar.org)
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
import org.traccar.database.DeviceManager;
import org.traccar.helper.LogAction;
import org.traccar.model.Device;
import org.traccar.model.DeviceAccumulators;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.*;

@Path("devices")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class DeviceResource extends BaseObjectResource<Device> {
    public DeviceResource() {
        super(Device.class);
    }




    @GET
    public Collection<Device> get(
            @QueryParam("all") boolean all, @QueryParam("userId") long userId,
            @QueryParam("uniqueId") List<String> uniqueIds,
            @QueryParam("id") List<Long> deviceIds) throws SQLException {


        DeviceManager deviceManager = Context.getDeviceManager();
        Set<Long> result;
        if (all) {
            if (Context.getPermissionsManager().getUserAdmin(getUserId())) {
                result = deviceManager.getAllItems();
            } else {
                Context.getPermissionsManager().checkManager(getUserId());
                result = deviceManager.getManagedItems(getUserId());
            }
        } else if (uniqueIds.isEmpty() && deviceIds.isEmpty()) {
            if (userId == 0)   userId = getUserId();

            Context.getPermissionsManager().checkUser(getUserId(), userId);
            if (Context.getPermissionsManager().getUserAdmin(getUserId())) {
                result = deviceManager.getAllUserItems(userId);
            } else {
               // result = deviceManager.getUserItems(userId);
                result = deviceManager.getAllUserItems(userId);

            }
        } else {
            result = new HashSet<>();
            for (String uniqueId : uniqueIds) {
                Device device = deviceManager.getByUniqueId(uniqueId);
                Context.getPermissionsManager().checkDevice(getUserId(), device.getId());
                result.add(device.getId());
            }
            for (Long deviceId : deviceIds) {
                Context.getPermissionsManager().checkDevice(getUserId(), deviceId);
                result.add(deviceId);
            }
        }
        Collection<Device> d= deviceManager.getItems(result);
//System.out.println("devices : userId : "+getUserId()+ "    d: "+d.size());
        return d;
    }

    @Path("counters")
    @GET()
    public Map<String, Long> getcounters() throws SQLException {

        Collection<Device> devices = getAllDevices2(0);

        Map<String, Long> counters = new HashMap<>();
        long onLine = 0l, offLine = 0l, idle = 0l, allD = 0l;
        for (Device d : devices) {
            if (d.getStatus().equalsIgnoreCase("online")) onLine++;
            if (d.getStatus().equalsIgnoreCase("offline")) offLine++;
            if (d.getStatus().equalsIgnoreCase("Unknown")) idle++;
            System.out.println(d.getStatus());

            allD++;

        }
        counters.put("allDevices", allD);
        counters.put("onLine", onLine);
        counters.put("offLine", offLine);
        counters.put("idle", idle);


        return counters;

    }

    @Path("view")
    @GET
    public Collection<Device> getView() throws SQLException {
       return getAllDevices2(0);
    }

    @Path("all")
    @GET
    public Collection<Device> getAll() throws SQLException {
        DeviceManager deviceManager = Context.getDeviceManager();
        Set<Long> result;

        if (Context.getPermissionsManager().getUserAdmin(getUserId())) {
            result = deviceManager.getAllItems();
        } else {
            Context.getPermissionsManager().checkManager(getUserId());
            result = deviceManager.getManagedItems(getUserId());
        }

        return deviceManager.getItems(result);
    }

    public Collection<Device> getAllDevices() throws SQLException {
        DeviceManager deviceManager = Context.getDeviceManager();
        Set<Long> result = deviceManager.getAllItems();
        return deviceManager.getItems(result);
    }

    @Path("devices2")
    @GET
    public Collection<Device> getAllDevices2(@QueryParam("userId") long userId) throws SQLException {
        if(userId==0)userId=getUserId();
        DeviceManager deviceManager = Context.getDeviceManager();
        Set<Long> result;
        Context.getPermissionsManager().checkUser(userId, userId);
        if (Context.getPermissionsManager().getUserAdmin(userId)) {
            result = deviceManager.getAllItems();
        } else {
            result = deviceManager.getUserItems(userId);
        }
        return deviceManager.getItems(result);
    }
    @Path("{id}/accumulators")
    @PUT
    public Response updateAccumulators(DeviceAccumulators entity) throws SQLException {
        if (!Context.getPermissionsManager().getUserAdmin(getUserId())) {
            Context.getPermissionsManager().checkManager(getUserId());
            Context.getPermissionsManager().checkPermission(Device.class, getUserId(), entity.getDeviceId());
        }
        Context.getDeviceManager().resetDeviceAccumulators(entity);
        LogAction.resetDeviceAccumulators(getUserId(), entity.getDeviceId());
        return Response.noContent().build();
    }

}
