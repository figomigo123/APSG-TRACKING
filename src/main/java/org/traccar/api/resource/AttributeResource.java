/*
 * Copyright 2017 - 2019 Anton Tananaev (anton@traccar.org)
 * Copyright 2017 - 2018 Andrey Kunitsyn (andrey@traccar.org)
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
import org.traccar.api.resource.new_models.NewAttribute;
import org.traccar.database.BaseObjectManager;
import org.traccar.handler.ComputedAttributesHandler;
import org.traccar.model.Attribute;
import org.traccar.model.Position;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Path("attributes/computed")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AttributeResource extends ExtendedObjectResource<Attribute> {

    public AttributeResource() {
        super(Attribute.class);
    }


    @Path("view")
    @GET
    public List<NewAttribute> getView(@QueryParam("all") boolean all, @QueryParam("userId") long userId) throws SQLException {
        BaseObjectManager<Attribute> manager = Context.getManager(getBaseClass());
        Collection<Attribute> items = manager.getItems(getSimpleManagerItems(manager, all, userId));
        List<NewAttribute> newAttributes = new ArrayList<>();
        items.forEach(u -> {
            NewAttribute newAttribute = new NewAttribute(u);
            newAttributes.add(newAttribute);
        });
        return newAttributes;
    }


    @POST
    @Path("test")
    public Response test(@QueryParam("deviceId") long deviceId, Attribute entity) {
        Context.getPermissionsManager().checkAdmin(getUserId());
        Context.getPermissionsManager().checkDevice(getUserId(), deviceId);
        Position last = Context.getIdentityManager().getLastPosition(deviceId);
        if (last != null) {
            Object result = new ComputedAttributesHandler(
                    Context.getConfig(),
                    Context.getIdentityManager(),
                    Context.getAttributesManager()).computeAttribute(entity, last);
            if (result != null) {
                switch (entity.getType()) {
                    case "number":
                        Number numberValue = (Number) result;
                        return Response.ok(numberValue).build();
                    case "boolean":
                        Boolean booleanValue = (Boolean) result;
                        return Response.ok(booleanValue).build();
                    default:
                        return Response.ok(result.toString()).build();
                }
            } else {
                return Response.noContent().build();
            }
        } else {
            throw new IllegalArgumentException("Device has no last position");
        }
    }

    @POST
    public Response add(Attribute entity) throws SQLException {
        Context.getPermissionsManager().checkAdmin(getUserId());
        return super.add(entity);
    }

    @Path("{id}")
    @PUT
    public Response update(Attribute entity) throws SQLException {
        Context.getPermissionsManager().checkAdmin(getUserId());
        return super.update(entity);
    }

    @Path("{id}")
    @DELETE
    public Response remove(@PathParam("id") long id) throws SQLException {
        Context.getPermissionsManager().checkAdmin(getUserId());
        return super.remove(id);
    }

}