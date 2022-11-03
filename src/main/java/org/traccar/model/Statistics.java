/*
 * Copyright 2016 - 2017 Anton Tananaev (anton@traccar.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.traccar.model;

import java.util.Date;
import java.util.Map;

public class Statistics extends ExtendedModel {

    private Date captureTime;
    private int activeUsers;
    private int activeDevices;
    private int requests;
    private int messagesReceived;
    private int messagesStored;
    private int mailSent;
    private int smsSent;
    private int geocoderRequests;
    private int geolocationRequests;
    private Map<String, Integer> protocols;

    public Date getCaptureTime() {
        return captureTime;
    }

    public void setCaptureTime(Date captureTime) {
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

    public Map<String, Integer> getProtocols() {
        return protocols;
    }

    public void setProtocols(Map<String, Integer> protocols) {
        this.protocols = protocols;
    }

}
