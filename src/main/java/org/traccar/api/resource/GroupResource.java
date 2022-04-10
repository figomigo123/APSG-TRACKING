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
import org.traccar.api.resource.new_models.TreeGroup;
import org.traccar.database.BaseObjectManager;
import org.traccar.model.Device;
import org.traccar.model.Group;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
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
    public Collection<Group> get(
            @QueryParam("all") boolean all, @QueryParam("userId") long userId) throws SQLException {
        BaseObjectManager<Group> manager = Context.getManager(getBaseClass());
        return manager.getItems(getSimpleManagerItems(manager, all, userId));
    }

    @Path("view")
    @GET
    public List<NewBaseModel> getView(@QueryParam("all") boolean all, @QueryParam("userId") long userId) throws SQLException {
        BaseObjectManager<Group> manager = Context.getManager(getBaseClass());
        Collection<Group> items = manager.getItems(getSimpleManagerItems(manager, all, userId));
        List<NewBaseModel> newBaseModels = new ArrayList<>();
        items.forEach(u -> {
            NewBaseModel newUser = new NewBaseModel(u.getName(), u.getId());
            newBaseModels.add(newUser);
        });
        return newBaseModels;
    }



    public List<TreeGroup> getTree2( String search,
                                    String status) throws SQLException, CloneNotSupportedException {
        BaseObjectManager<Group> manager = Context.getManager(getBaseClass());
        Collection<Group> items = manager.getItems(getSimpleManagerItems(manager, true, 1L));

        List<TreeGroup> treeGroups = new ArrayList<>();
        List<TreeGroup> treeGroups2 = new ArrayList<>();
        if (items.size() > 0)
            items.forEach(t -> {
                treeGroups.add(new TreeGroup(t.getName(), t.getId(), t.getGroupId()));
            });
        TreeGroup base = new TreeGroup("base", 0, 0);
        DeviceResource deviceResource = new DeviceResource();
        Collection<Device> all = deviceResource.getAllDevices();
        for (Device d : all)
            if (search == null || d.getName().toLowerCase().contains(search.toLowerCase())) {
                if (status == null || status.equals("all") || d.getStatus().equals(status)) {
                    if (d.getGroupId() == 0)
                        base.getDevices().add(d);
                    else
                        for (TreeGroup t : treeGroups)
                            if (t.getId() == d.getGroupId()) {
                                t.getDevices().add(d);
                                t.setHaveDevices(true);
                            }
                }
            }


        List<StringBuilder> pathes = new ArrayList<>();
        for (int i = 0; i < treeGroups.size(); i++)
            if (treeGroups.get(i).getGroupId() == 0) {
                treeGroups.get(i).setLevel(1);
                base.getTreeGroups().add(treeGroups.get(i).clone());
                pathes.add(new StringBuilder(Long.toString(treeGroups.get(i).getId())));
                treeGroups2.add(treeGroups.get(i).clone());
                treeGroups.remove(treeGroups.get(i));
                i--;
            }
        while (treeGroups.size() > 0) {
            for (StringBuilder s : pathes) {
                if (treeGroups.size() == 0) break;
                String[] ss = s.toString().split("-");
                for (String item : ss)
                    if (item.equals(Long.toString(treeGroups.get(0).getGroupId()))) {
                        List<Long> pp = new ArrayList<>();
                        for (String value : ss) {
                            long l = Long.parseLong(value);
                            pp.add(l);
                            if (l == treeGroups.get(0).getGroupId()) break;
                        }
                        treeGroups.get(0).setLevel(pp.size() + 1);
                        List<TreeGroup> leaf = base.getLeaf(pp);
                        if (leaf == null) return getTree2(search, status);
                        leaf.add(treeGroups.get(0));
                        s.append("-").append(treeGroups.get(0).getId());
                        treeGroups2.add(treeGroups.get(0).clone());
                        treeGroups.remove(0);
                        break;
                    }
            }
            Collections.shuffle(treeGroups);
        }
        for (TreeGroup t : treeGroups2) t.setTreeGroups(new ArrayList<>());
        base.setTreeGroups(new ArrayList<>());
        treeGroups2.add(base);
        return treeGroups2;
    }

    @Path("tree")
    @GET
    public TreeGroup getTree3(
            @QueryParam(value = "search") String search,
            @QueryParam(value = "status") String status
    ) throws SQLException, CloneNotSupportedException {
        List<TreeGroup> treeGroups = getTree2(search, status);
        List<TreeGroup> treeGroups2 = new ArrayList<>();
        for (TreeGroup t : treeGroups) treeGroups2.add(t.clone());
        TreeGroup base = treeGroups.get(treeGroups.size() - 1);


        TreeGroup t;

        while (treeGroups.size() > 0 && (t = getHaveDevices(treeGroups)) != null) {
            if (t.getGroupId() == 0) base.getTreeGroups().add(t);
            else {
                TreeGroup t2 = getRoot(treeGroups, t.getGroupId());
                t2.getTreeGroups().add(t);
                t2.setHaveDevices(true);
            }

        }

        for (TreeGroup treeGroup : treeGroups)
            if (treeGroup.isHaveDevices() && !treeGroup.isBuild()) base.getTreeGroups().add(treeGroup.clone());





        return base;
    }


    TreeGroup getHaveDevices(List<TreeGroup> treeGroups) throws CloneNotSupportedException {
        //System.out.println("treeGroups size: "+treeGroups.size());
        for (TreeGroup treeGroup : treeGroups)
            if (treeGroup.isHaveDevices() && !treeGroup.isBuild()) {
                treeGroup.setBuild(true);
                return treeGroup.clone();

            }


        return null;

    }

    TreeGroup getRoot(List<TreeGroup> treeGroups, long treeGroupId) {
        for (TreeGroup t : treeGroups)
            if (t.getId() == treeGroupId) return t;

        return null;

    }


}
