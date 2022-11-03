package org.traccar.api.resource.new_models;

import org.traccar.model.Statistics;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class NewStatistics {
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
    private String captureTime;
    private int activeUsers;
    private int activeDevices;
    private int requests;
    private int messagesReceived;
    private int messagesStored;
    private int mailSent;
    private int smsSent;
    private int geocoderRequests;
    private int geolocationRequests;

    public NewStatistics(Statistics statistics) {
        this.captureTime = formatter.format(statistics.getCaptureTime());
        this.activeUsers = statistics.getActiveUsers();
        this.activeDevices = statistics.getActiveDevices();
        this.requests = statistics.getRequests();
        this.messagesReceived = statistics.getMessagesReceived();
        this.messagesStored = statistics.getMessagesStored();
        this.mailSent = statistics.getMailSent();
        this.smsSent = statistics.getSmsSent();
        this.geocoderRequests = statistics.getGeocoderRequests();
        this.geolocationRequests = statistics.getGeolocationRequests();
    }

    public String getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(String captureTime) {
        this.captureTime = captureTime;
    }

    public int getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(int activeUsers) {
        this.activeUsers = activeUsers;
    }

    public int getActiveDevices() {
        return activeDevices;
    }

    public void setActiveDevices(int activeDevices) {
        this.activeDevices = activeDevices;
    }

    public int getRequests() {
        return requests;
    }

    public void setRequests(int requests) {
        this.requests = requests;
    }

    public int getMessagesReceived() {
        return messagesReceived;
    }

    public void setMessagesReceived(int messagesReceived) {
        this.messagesReceived = messagesReceived;
    }

    public int getMessagesStored() {
        return messagesStored;
    }

    public void setMessagesStored(int messagesStored) {
        this.messagesStored = messagesStored;
    }

    public int getMailSent() {
        return mailSent;
    }

    public void setMailSent(int mailSent) {
        this.mailSent = mailSent;
    }

    public int getSmsSent() {
        return smsSent;
    }

    public void setSmsSent(int smsSent) {
        this.smsSent = smsSent;
    }

    public int getGeocoderRequests() {
        return geocoderRequests;
    }

    public void setGeocoderRequests(int geocoderRequests) {
        this.geocoderRequests = geocoderRequests;
    }

    public int getGeolocationRequests() {
        return geolocationRequests;
    }

    public void setGeolocationRequests(int geolocationRequests) {
        this.geolocationRequests = geolocationRequests;
    }
}
