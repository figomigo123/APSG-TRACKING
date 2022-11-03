package org.traccar.api.resource.new_models;

import org.traccar.model.Device;

import java.util.ArrayList;
import java.util.List;



public class NewChild {

    String label,data="",key,status, type;
   boolean  leaf= false;
   long  id,groupId;
    List<NewChild> Children = new ArrayList<>();
   Device devices ;

    public NewChild( String label, String status, String type, boolean leaf, long id, long groupId) {
        this.label = label;
        this.key = Long.toString(id);
        this.status = status;
        this.type = type;
        this.leaf = leaf;
        this.id = id;
        this.groupId = groupId;
    }

    public NewChild() {
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isLeaf() {
        return leaf;
    }

    public void setLeaf(boolean leaf) {
        this.leaf = leaf;
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

    public List<NewChild> getChildren() {
        return Children;
    }

    public void setChildren(List<NewChild> children) {
        Children = children;
    }

    public Device getDevices() {
        return devices;
    }

    public void setDevices(Device devices) {
        this.devices = devices;
    }
}
