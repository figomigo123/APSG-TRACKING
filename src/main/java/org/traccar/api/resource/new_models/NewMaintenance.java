package org.traccar.api.resource.new_models;

import org.traccar.model.Maintenance;

public class NewMaintenance extends NewBaseModel {


    private String type;
    private double start;
    private double period;

    public NewMaintenance(Maintenance maintenance) {
        this.name = maintenance.getName();
        this.type = maintenance.getType();
        this.start = maintenance.getStart();
        this.period = maintenance.getPeriod();
        setId(maintenance.getId());
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    public double getPeriod() {
        return period;
    }

    public void setPeriod(double period) {
        this.period = period;
    }

}
