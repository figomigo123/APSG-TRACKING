package org.traccar.api.resource.new_models;

public class NewBaseModel {

    String name;

    public NewBaseModel() {
    }
    private long id;

    public final long getId() {
        return id;
    }

    public final void setId(long id) {
        this.id = id;
    }

    public NewBaseModel(String name,long id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
