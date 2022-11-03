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
import org.traccar.api.BaseResource;
import org.traccar.model.Landmark;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.Collection;

@Path("landmarks")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class LandmarkResource extends BaseResource {


    @GET
    public Collection<Landmark> get() throws SQLException {
        return Context.getDataManager().getUserLandmarks(getUserId());
    }
    @Path("view")
    @GET
    public Collection<Landmark> getview() throws SQLException {
        return Context.getDataManager().getUserLandmarks(getUserId());
    }

    @Path("{id}")
    @GET
    public Landmark getSingle(@PathParam("id") long id) throws SQLException {
        return (Landmark) Context.getDataManager().getUserLandmarks(getUserId()).stream().toArray()[0];
    }

    @PermitAll
    @POST
    public long add(Landmark entity) throws SQLException {
System.out.println(entity.toString());
        if (entity != null)
            entity.setUserid(getUserId());
        if(entity.getDescription()==null)entity.setDescription("");
            Context.getDataManager().addObject(entity);
      //  return Response.ok().build();
        return getUserId();
    }

    @Path("{id}")
    @PUT
    public Response update(@PathParam("id") long id,Landmark entity) throws SQLException {
        entity.setId(id);
        entity.setUserid(getUserId());
        if(entity.getLatitude()==0&&entity.getLongitude()==0){
            Landmark single = getSingle(id);
            entity.setLongitude(single.getLongitude());
            entity.setLatitude(single.getLatitude());

        }

        Context.getDataManager().updateObject(entity);
        return Response.noContent().build();
    }

    @Path("{id}")
    @DELETE
    public Response del(@PathParam("id") long id) throws SQLException {
        Context.getDataManager().removeObject(Landmark.class, id);
        return Response.noContent().build();
    }
}
