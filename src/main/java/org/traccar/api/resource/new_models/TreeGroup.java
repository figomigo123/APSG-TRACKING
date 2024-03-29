package org.traccar.api.resource.new_models;

import org.traccar.model.Device;

import java.util.ArrayList;
import java.util.List;

public class TreeGroup implements Cloneable {
    String label;
    long id, groupId;
    List<TreeGroup> children = new ArrayList<>();
    List<Device> devices = new ArrayList<>();
    int level, online, offline, unknown;
    boolean haveDevices = false, build = false;

    public TreeGroup() {
    }

    public TreeGroup(String label, long id, long groupId) {
        this.label = label;
        this.id = id;
        this.groupId = groupId;
    }

    public List<TreeGroup> getLeaf(List<Long> path) {
        for (TreeGroup t : children)
            if (t.getId() == path.get(0))
                if (path.size() == 1)
                    return t.getChildren();
                else {
                    path.remove(0);
                    return t.getLeaf(path);
                }


        return null;
    }

    public void setNumbers(String status) {
        switch (status) {
            case "online":
                this.online++;
                break;
            case "offline":
                this.offline++;
                break;
            case "unknown":
                this.unknown++;
                break;
        }
    }

    public boolean isBuild() {
        return build;
    }

    public void setBuild(boolean build) {
        this.build = build;
    }

    public int getOnline() {
        return online;
    }

    public void setOnline(int online) {
        this.online = online;
    }

    public int getOffline() {
        return offline;
    }

    public void setOffline(int offline) {
        this.offline = offline;
    }

    public int getUnknown() {
        return unknown;
    }

    public void setUnknown(int unknown) {
        this.unknown = unknown;
    }

    public boolean isHaveDevices() {
        return haveDevices;
    }

    public void setHaveDevices(boolean haveDevices) {
        this.haveDevices = haveDevices;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
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

    public List<TreeGroup> getChildren() {
        return children;
    }

    public void setChildren(List<TreeGroup> children) {
        this.children = children;
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
                "name='" + label + '\'' +
                ", treeGroups=" + children +
                ", devices=" + devices +
                '}';
    }
}
