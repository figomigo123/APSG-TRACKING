package org.traccar.api.resource.new_models;

import org.traccar.model.Driver;

public class NewDriver extends NewBaseModel{
    String identifier;

    public NewDriver(Driver driver) {
        this.identifier=driver.getUniqueId();
        this.name=driver.getName();
        setId(driver.getId());
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }
}
