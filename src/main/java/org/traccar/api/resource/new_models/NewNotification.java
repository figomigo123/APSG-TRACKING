package org.traccar.api.resource.new_models;

import org.traccar.model.Notification;

public class NewNotification {
    String type_of_notification;
    String all_devices;
    String alarms;
    String channels;

    public NewNotification(Notification notification) {
        this.type_of_notification=notification.getType();
       if(notification.getAlways()) this.all_devices="Yes";else this.all_devices="No";
       this.alarms=notification.getString("alarms");
       if(this.alarms==null)this.alarms="";
       this.channels=notification.getNotificators();

    }

    public String getType_of_notification() {
        return type_of_notification;
    }

    public void setType_of_notification(String type_of_notification) {
        this.type_of_notification = type_of_notification;
    }

    public String getAll_devices() {
        return all_devices;
    }

    public void setAll_devices(String all_devices) {
        this.all_devices = all_devices;
    }

    public String getAlarms() {
        return alarms;
    }

    public void setAlarms(String alarms) {
        this.alarms = alarms;
    }

    public String getChannels() {
        return channels;
    }

    public void setChannels(String channels) {
        this.channels = channels;
    }
}
