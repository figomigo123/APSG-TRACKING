package org.traccar.api.resource.new_models;

import org.traccar.model.Geofence;

public class NewGeofence extends NewBaseModel {
    String description;

    public NewGeofence(Geofence geofence) {
        this.name = geofence.getName();
        this.description = geofence.getDescription();
        setId(geofence.getId());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
