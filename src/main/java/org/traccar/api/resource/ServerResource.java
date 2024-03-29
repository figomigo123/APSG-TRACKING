/*
 * Copyright 2015 - 2020 Anton Tananaev (anton@traccar.org)
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
import org.traccar.api.BaseResource;
import org.traccar.helper.LogAction;
import org.traccar.model.Server;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

@Path("server")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ServerResource extends BaseResource {
    @PermitAll
    @GET
    public Server get(@QueryParam("force") boolean force) throws SQLException {
        if (force) {
            return Context.getDataManager().getServer();
        } else {
            return Context.getPermissionsManager().getServer();
        }
    }

    @PermitAll
    @Path("array")
    @GET
    public List<Server> getAsArray(@QueryParam("force") boolean force) throws SQLException {
        if (force) {
            return Arrays.asList(Context.getDataManager().getServer());
        } else {
            return Arrays.asList(Context.getPermissionsManager().getServer());
        }
    }

    @PUT
    public Response update(Server entity) throws SQLException {
        Context.getPermissionsManager().checkAdmin(getUserId());
        Context.getPermissionsManager().updateServer(entity);
        LogAction.edit(getUserId(), entity);
        return Response.ok(entity).build();
    }

    @Path("geocode")
    @GET
    public String geocode(@QueryParam("latitude") double latitude, @QueryParam("longitude") double longitude) {

        if (Context.getGeocoder() != null) {
            return Context.getGeocoder().getAddress(latitude, longitude, null);
        } else {
            throw new RuntimeException("Reverse geocoding is not enabled");
        }
    }

    @Path("maplayers")
    @GET
    public List<String> maplayer() {
        return Arrays.asList(
                "Open Street Map",
                "Carto Basemaps",
                "Auto Navi",
                "Bing Maps Road",
                "Bing Maps Aerial",
                "Bing Maps Hybrid",
                "Yandex Map",
                "Yandex Satellite",
                "Custom (XYZ)",
                "Custom (ArcGIS)"
        );
    }

    @Path("coordinates")
    @GET
    public List<String> coordinates() {
        return Arrays.asList(
                "Decimal Degrees",
                "Degrees Decimal Minutes",
                "Degrees  Minutes Seconds"
        );
    }

}
