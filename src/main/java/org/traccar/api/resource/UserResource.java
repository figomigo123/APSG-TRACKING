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
import org.traccar.api.resource.new_models.NewUser;
import org.traccar.config.Keys;
import org.traccar.database.UsersManager;
import org.traccar.helper.LogAction;
import org.traccar.model.ManagedUser;
import org.traccar.model.User;

import javax.annotation.security.PermitAll;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

@Path("users")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource extends BaseObjectResource<User> {

    public UserResource() {
        super(User.class);
    }

    @GET
    public Collection<User> get(@QueryParam("userId") long userId) throws SQLException {
        UsersManager usersManager = Context.getUsersManager();
        Set<Long> result;
        if (Context.getPermissionsManager().getUserAdmin(getUserId())) {
            if (userId != 0) {
                result = usersManager.getUserItems(userId);
            } else {
                result = usersManager.getAllItems();
            }
        } else if (Context.getPermissionsManager().getUserManager(getUserId())) {
            result = usersManager.getManagedItems(getUserId());
        } else {
            throw new SecurityException("Admin or manager access required");
        }
        return usersManager.getItems(result);
    }


    @Path("view")
    @GET
    public Collection<NewUser> getview(@QueryParam("userId") long userId) throws SQLException {
        UsersManager usersManager = Context.getUsersManager();
        Set<Long> result;
        if (Context.getPermissionsManager().getUserAdmin(getUserId())) {
            if (userId != 0) {
                result = usersManager.getUserItems(userId);
            } else {
                result = usersManager.getAllItems();
            }
        } else if (Context.getPermissionsManager().getUserManager(getUserId())) {
            result = usersManager.getManagedItems(getUserId());
        } else {
            throw new SecurityException("Admin or manager access required");
        }
        Collection<User> items = usersManager.getItems(result);
        Collection<NewUser> items2 = new ArrayList<>();
        items.forEach(u -> {
            NewUser newUser = new NewUser(u);
            items2.add(newUser);
        });
        return items2;
    }

    String yesORno(boolean v) {
        if (v) return "Yes";
        else return "No";
    }


    @Path("all")
    @GET
    public Collection<User> getAll() throws SQLException {
        UsersManager usersManager = Context.getUsersManager();
        Set<Long> result;
        if (Context.getPermissionsManager().getUserAdmin(getUserId())) {

            result = usersManager.getAllItems();

        } else if (Context.getPermissionsManager().getUserManager(getUserId())) {
            result = usersManager.getManagedItems(getUserId());
        } else {
            throw new SecurityException("Admin or manager access required");
        }
        return usersManager.getItems(result);
    }

    @Path("/count")
    @GET
    public int getUsersCount(@QueryParam("map") String map) throws SQLException {
        return 0;
    }

    @Override
    @PermitAll
    @POST
    public Response add(User entity) throws SQLException {
        Response response = addUser(entity, true);
        Context.getUsersManager().refreshUserItems();
        return response;
    }

    @Path("/all")
    @PermitAll
    @POST
    public Response addAll(Collection<User> entitys) throws SQLException {
        UsersManager usersManager = Context.getUsersManager();
        Collection<User> items = usersManager.getItems(usersManager.getAllItems());
        for (User entity : entitys) {
            boolean isNewUser = true;
            if (entity.getId() > 0)
                for (User item : items) if (item.getId() == entity.getId()) isNewUser = false;
            addUser(entity, isNewUser);
        }
        Context.getUsersManager().refreshUserItems();
        return Response.ok(entitys).build();
    }

    public Response addUser(User entity, boolean isNewUser) throws SQLException {

        if (!Context.getPermissionsManager().getUserAdmin(getUserId())) {
            Context.getPermissionsManager().checkUserUpdate(getUserId(), new User(), entity);
            if (Context.getPermissionsManager().getUserManager(getUserId())) {
                Context.getPermissionsManager().checkUserLimit(getUserId());
            } else {
                Context.getPermissionsManager().checkRegistration(getUserId());
                entity.setDeviceLimit(Context.getConfig().getInteger(Keys.USERS_DEFAULT_DEVICE_LIMIT));
                int expirationDays = Context.getConfig().getInteger(Keys.USERS_DEFAULT_EXPIRATION_DAYS);
                if (expirationDays > 0) {
                    entity.setExpirationTime(
                            new Date(System.currentTimeMillis() + (long) expirationDays * 24 * 3600 * 1000));
                }
            }
        }
        if (isNewUser) {
            Context.getUsersManager().addItem(entity);
            LogAction.create(getUserId(), entity);
        } else {
            User before = Context.getPermissionsManager().getUser(entity.getId());
            Context.getPermissionsManager().checkUserUpdate(getUserId(), before, entity);
            Context.getPermissionsManager().checkPermission(User.class, getUserId(), entity.getId());

            Context.getManager(User.class).updateItem(entity);
            LogAction.edit(getUserId(), entity);
        }

        if (Context.getPermissionsManager().getUserManager(getUserId())) {
            Context.getDataManager().linkObject(User.class, getUserId(), ManagedUser.class, entity.getId(), true);
            LogAction.link(getUserId(), User.class, getUserId(), ManagedUser.class, entity.getId());
        }

        return Response.ok(entity).build();
    }



}
