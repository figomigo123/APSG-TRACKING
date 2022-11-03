package org.traccar.api.resource.new_models;


import org.traccar.model.Event;


public class NewEvent extends Event {
String name,dviceName;
long groupId;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.dviceName = name;
    }

    public String getDviceName() {
        return dviceName;
    }

    public void setDviceName(String dviceName) {
        this.dviceName = dviceName;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }
}
