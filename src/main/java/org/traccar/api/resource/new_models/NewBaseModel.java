package org.traccar.api.resource.new_models;

public class NewBaseModel {
    String name;

    public NewBaseModel() {
    }

    public NewBaseModel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
