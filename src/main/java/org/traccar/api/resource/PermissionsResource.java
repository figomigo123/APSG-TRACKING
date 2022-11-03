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
package org.traccar.api.resource;

import org.traccar.Context;
import org.traccar.api.BaseResource;
import org.traccar.database.GroupTree;
import org.traccar.helper.LogAction;
import org.traccar.model.Device;
import org.traccar.model.Permission;
import org.traccar.model.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.*;

@Path("permissions")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class PermissionsResource extends BaseResource {

    private void checkPermission(Permission permission, boolean link) {
        if (!link && permission.getOwnerClass().equals(User.class)
                && permission.getPropertyClass().equals(Device.class)) {
            if (getUserId() != permission.getOwnerId()) {
                Context.getPermissionsManager().checkUser(getUserId(), permission.getOwnerId());
            } else {
                Context.getPermissionsManager().checkAdmin(getUserId());
            }
        } else {
            Context.getPermissionsManager().checkPermission(
                    permission.getOwnerClass(), getUserId(), permission.getOwnerId());
        }
        Context.getPermissionsManager().checkPermission(
                permission.getPropertyClass(), getUserId(), permission.getPropertyId());
    }

    private void checkPermissionTypes(List<LinkedHashMap<String, Long>> entities) {
        Set<String> keys = null;
        for (LinkedHashMap<String, Long> entity : entities) {
            if (keys != null & !entity.keySet().equals(keys)) {
                throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).build());
            }
            keys = entity.keySet();
        }
    }

    @Path("bulk")
    @POST
    public Response add(List<LinkedHashMap<String, Long>> entities) throws SQLException, ClassNotFoundException {

        Context.getPermissionsManager().checkReadonly(getUserId());
        checkPermissionTypes(entities);
        for (LinkedHashMap<String, Long> entity : entities) {
            Permission permission = new Permission(entity);
            checkPermission(permission, true);
            Context.getDataManager().linkObject(permission.getOwnerClass(), permission.getOwnerId(),
                    permission.getPropertyClass(), permission.getPropertyId(), true);
            LogAction.link(getUserId(), permission.getOwnerClass(), permission.getOwnerId(),
                    permission.getPropertyClass(), permission.getPropertyId());
        }
        if (!entities.isEmpty()) {
            Context.getPermissionsManager().refreshPermissions(new Permission(entities.get(0)));
        }
        return Response.noContent().build();
    }

    @POST
    public Response add(LinkedHashMap<String, Long> entity) throws SQLException, ClassNotFoundException {
        if ( entity.keySet().contains("groupId") &&   entity.keySet().contains("userId"))
            addOrRemoveGroupDevices(entity.get("groupId"),entity.get("userId"),"add");
        return add(Collections.singletonList(entity));
    }

    @DELETE
    @Path("bulk")
    public Response remove(List<LinkedHashMap<String, Long>> entities) throws SQLException, ClassNotFoundException {
        Context.getPermissionsManager().checkReadonly(getUserId());
        checkPermissionTypes(entities);
        for (LinkedHashMap<String, Long> entity : entities) {
            Permission permission = new Permission(entity);
            checkPermission(permission, false);
            Context.getDataManager().linkObject(permission.getOwnerClass(), permission.getOwnerId(),
                    permission.getPropertyClass(), permission.getPropertyId(), false);
            LogAction.unlink(getUserId(), permission.getOwnerClass(), permission.getOwnerId(),
                    permission.getPropertyClass(), permission.getPropertyId());
        }
        if (!entities.isEmpty()) {
            Context.getPermissionsManager().refreshPermissions(new Permission(entities.get(0)));
        }
        return Response.noContent().build();
    }

    @DELETE
    public Response remove(LinkedHashMap<String, Long> entity) throws SQLException, ClassNotFoundException {
        if ( entity.keySet().contains("groupId") &&   entity.keySet().contains("userId"))
            addOrRemoveGroupDevices(entity.get("groupId"),entity.get("userId"),"");
        return remove(Collections.singletonList(entity));
    }



    public void addOrRemoveGroupDevices( long groupId,long userId,String key) throws SQLException, ClassNotFoundException {
        Collection<Device> devices=  new GroupTree(Context.getGroupsManager().getItems(
                Context.getGroupsManager().getAllItems()),
                Context.getDeviceManager().getAllDevices()).getDevices(groupId);

        for(Device d:devices){
            LinkedHashMap<String, Long> en=new LinkedHashMap<>();
            en.put("userId",userId);
            en.put("deviceId",d.getId());
            if(key.equals("add"))
                add(Collections.singletonList(en));
            else
            remove(Collections.singletonList(en));
        }

    }


}
