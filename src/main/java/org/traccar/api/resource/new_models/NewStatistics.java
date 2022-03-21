package org.traccar.api.resource.new_models;

import org.traccar.model.Statistics;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class NewStatistics {
    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");

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

    private String captureTime;

    public String getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(String captureTime) {
        this.captureTime = captureTime;
    }

    private int activeUsers;

    public int getActiveUsers() {
        return activeUsers;
    }

    public void setActiveUsers(int activeUsers) {
        this.activeUsers = activeUsers;
    }

    private int activeDevices;

    public int getActiveDevices() {
        return activeDevices;
    }

    public void setActiveDevices(int activeDevices) {
        this.activeDevices = activeDevices;
    }

    private int requests;

    public int getRequests() {
        return requests;
    }

    public void setRequests(int requests) {
        this.requests = requests;
    }

    private int messagesReceived;

    public int getMessagesReceived() {
        return messagesReceived;
    }

    public void setMessagesReceived(int messagesReceived) {
        this.messagesReceived = messagesReceived;
    }

    private int messagesStored;

    public int getMessagesStored() {
        return messagesStored;
    }

    public void setMessagesStored(int messagesStored) {
        this.messagesStored = messagesStored;
    }

    private int mailSent;

    public int getMailSent() {
        return mailSent;
    }

    public void setMailSent(int mailSent) {
        this.mailSent = mailSent;
    }

    private int smsSent;

    public int getSmsSent() {
        return smsSent;
    }

    public void setSmsSent(int smsSent) {
        this.smsSent = smsSent;
    }

    private int geocoderRequests;

    public int getGeocoderRequests() {
        return geocoderRequests;
    }

    public void setGeocoderRequests(int geocoderRequests) {
        this.geocoderRequests = geocoderRequests;
    }

    private int geolocationRequests;

    public int getGeolocationRequests() {
        return geolocationRequests;
    }

    public void setGeolocationRequests(int geolocationRequests) {
        this.geolocationRequests = geolocationRequests;
    }
}
