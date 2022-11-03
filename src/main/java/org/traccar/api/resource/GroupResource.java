/*
 * Copyright 2016 - 2017 Anton Tananaev (anton@traccar.org)
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
import org.traccar.api.SimpleObjectResource;
import org.traccar.api.resource.new_models.NewBaseModel;
import org.traccar.api.resource.new_models.NewChild;
import org.traccar.database.BaseObjectManager;
import org.traccar.database.GroupTree;
import org.traccar.model.Device;
import org.traccar.model.Group;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Path("groups")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class GroupResource extends SimpleObjectResource<Group> {

    public GroupResource() {
        super(Group.class);
    }

    @Path("all")
    @GET
    public Collection<Group> get() throws SQLException {
        BaseObjectManager<Group> manager = Context.getManager(getBaseClass());
        return manager.getItems(manager.getAllItems());
    }

    @Path("view")
    @GET
    public List<NewBaseModel> getView() throws SQLException {
        BaseObjectManager<Group> manager = Context.getManager(getBaseClass());
        Collection<Group> items = manager.getItems(manager.getAllItems());
        List<NewBaseModel> newBaseModels = new ArrayList<>();
        items.forEach(u -> {
            NewBaseModel newUser = new NewBaseModel(u.getName(), u.getId());
            newBaseModels.add(newUser);
        });
        return newBaseModels;
    }


    @Path("test")
    @GET
    public Collection<Group> test(@QueryParam("e") long e) {
        return new GroupTree(Context.getGroupsManager().getItems(
                Context.getGroupsManager().getAllItems()),
                Context.getDeviceManager().getAllDevices()).getGroups(e);
    }

    @Path("nodess2")
    @GET
    public List<NewChild> getNodessTree2(@QueryParam("status") String status,
                                         @QueryParam("search") String search) throws SQLException {
        List<NewChild> level1 = new ArrayList<>();
        BaseObjectManager<Group> manager = Context.getManager(getBaseClass());
        Collection<Group> groups = manager.getItems(manager.getAllItems());

        DeviceResource deviceResource = new DeviceResource();
        long userId = getUserId();
        List<Device> devices  = new ArrayList(deviceResource.getAllDevices2(userId));
        List<NewChild> level2 = new ArrayList<>();


        for (int i=0;i< devices.size();i++){
            Device d = devices.get(i);
            if ((!status.equals("all") && !status.equals(d.getStatus()))||
                    (!search.equals("") && !d.getName().contains(search))){ devices.remove(d);i--;}
            else if(d.getGroupId() == 0) {
            NewChild n2 = new NewChild(d.getName(), d.getStatus(), "device", true, d.getId(), d.getGroupId());
            n2.setDevices(d);
            level1.add(n2);
        }}
        for (Group g : groups) {
            if (g.getGroupId() == 0) continue;
            NewChild n1 = new NewChild(g.getName(), "", "", false, g.getId(), g.getGroupId());
            for (Device d : devices)
                if (d.getGroupId() == g.getId()) {
                    NewChild n2 = new NewChild(d.getName(), d.getStatus(), "device", true, d.getId(), d.getGroupId());
                    n2.setDevices(d);
                    n1.getChildren().add(n2);
                }
            if (n1.getChildren().size() > 0)
                level2.add(n1);
        }
        for (Group g : groups) {
            if (g.getGroupId() > 0) continue;
            NewChild n1 = new NewChild(g.getName(), "", "", false, g.getId(), g.getGroupId());
            for (Device d : devices) {
                if (d.getGroupId() == g.getId()) {
                    NewChild n2 = new NewChild(d.getName(), d.getStatus(), "device", true, d.getId(), d.getGroupId());
                    n2.setDevices(d);
                    n1.getChildren().add(n2);
                }
            }
            for (NewChild n : level2)
                if (n.getGroupId() == g.getId() && n.getChildren().size() > 0)
                    n1.getChildren().add(n);
            if (n1.getChildren().size() > 0)
                level1.add(n1);
        }
        return level1;
    }

}
