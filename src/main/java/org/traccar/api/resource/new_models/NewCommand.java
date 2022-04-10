package org.traccar.api.resource.new_models;

import org.traccar.model.Command;

public class NewCommand {

    private String description;
    private String type;
    private String sendSMS;

    public NewCommand(Command command) {
        this.description = command.getDescription();
        this.type = command.getType();
        this.id=command.getId();
        if(command.getTextChannel()) this.sendSMS="Yes";else this.sendSMS="No";
    }
    private long id;

    public final long getId() {
        return id;
    }

    public final void setId(long id) {
        this.id = id;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSendSMS() {
        return sendSMS;
    }

    public void setSendSMS(String sendSMS) {
        this.sendSMS = sendSMS;
    }
}
