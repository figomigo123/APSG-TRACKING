package org.traccar.api.resource.new_models;

import org.traccar.model.Device;

import java.util.ArrayList;
import java.util.List;

public class TreeGroup implements Cloneable {
    String name;
    long id, groupId;
    List<TreeGroup> treeGroups = new ArrayList<>();
    List<Device> devices = new ArrayList<>();
int level;
boolean haveDevices=false,build=false;

    public TreeGroup() {
    }

    public List<TreeGroup> getLeaf(List<Long> path) {
        for (TreeGroup t : treeGroups)
            if (t.getId() == path.get(0))
                if (path.size() == 1)
                    return t.getTreeGroups();
                else {
                    path.remove(0);
                    return t.getLeaf(path);
                }


        return null;
    }


    public TreeGroup(String name, long id, long groupId) {
        this.name = name;
        this.id = id;
        this.groupId = groupId;
    }

    public boolean isBuild() {
        return build;
    }

    public void setBuild(boolean build) {
        this.build = build;
    }

    public boolean isHaveDevices() {
        return haveDevices;
    }

    public void setHaveDevices(boolean haveDevices) {
        this.haveDevices = haveDevices;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public List<TreeGroup> getTreeGroups() {
        return treeGroups;
    }

    public void setTreeGroups(List<TreeGroup> treeGroups) {
        this.treeGroups = treeGroups;
    }

    public List<Device> getDevices() {
        return devices;
    }

    public void setDevices(List<Device> devices) {
        this.devices = devices;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public TreeGroup clone() throws CloneNotSupportedException {
        return (TreeGroup) super.clone();
    }

    @Override
    public String toString() {
        return "TreeGroup{" +
                "name='" + name + '\'' +
                ", treeGroups=" + treeGroups +
                ", devices=" + devices +
                '}';
    }
}
