package org.traccar.api.resource.new_models;

import org.traccar.model.Maintenance;

public class NewMaintenance extends NewBaseModel{


    public NewMaintenance(Maintenance maintenance) {
        this.name=maintenance.getName();
        this.type = maintenance.getType();
        this.start = maintenance.getStart();
        this.period = maintenance.getPeriod();
    }

    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    private double start;

    public double getStart() {
        return start;
    }

    public void setStart(double start) {
        this.start = start;
    }

    private double period;

    public double getPeriod() {
        return period;
    }

    public void setPeriod(double period) {
        this.period = period;
    }

}
