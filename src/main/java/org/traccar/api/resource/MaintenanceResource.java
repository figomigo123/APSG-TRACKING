/*
 * Copyright 2018 Anton Tananaev (anton@traccar.org)
 * Copyright 2018 Andrey Kunitsyn (andrey@traccar.org)
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
import org.traccar.api.ExtendedObjectResource;
import org.traccar.api.resource.new_models.NewMaintenance;
import org.traccar.database.BaseObjectManager;
import org.traccar.model.Maintenance;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Path("maintenance")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class MaintenanceResource extends ExtendedObjectResource<Maintenance> {

    public MaintenanceResource() {
        super(Maintenance.class);
    }

    @Path("view")
    @GET
    public List<NewMaintenance> getView(@QueryParam("all") boolean all, @QueryParam("userId") long userId) throws SQLException {
        BaseObjectManager<Maintenance> manager = Context.getManager(getBaseClass());
        Collection<Maintenance> items = manager.getItems(getSimpleManagerItems(manager, all, userId));
        List<NewMaintenance> newMaintenances = new ArrayList<>();
        items.forEach(u -> {
            NewMaintenance newMaintenance = new NewMaintenance(u);
            newMaintenances.add(newMaintenance);
        });
        return newMaintenances;
    }

    @Path("types")
    @GET
    public List<String> maplayer() {
        return Arrays.asList(
                "Index",
                "HDOP",
                "VDOP",
                "PDOP",
                "Satellite",
                "Visible Satellite",
                "RSSI",
                "GPS",
                "Odometer",
                "service Odometer",
                "Trip Odometer",
                "Hours",
                "Steps",
                "Power",
                "Battery",
                "Battery Level",
                "Fuel",
                "Fuel Consumption",
                "Distance",
                "Total Distance",
                "RPM",
                "Throttle",
                "Armed",
                "Acceleration",
                "Device Temperature",
                "OBD Speed",
                "OBD Odometer"
        );
    }


}
