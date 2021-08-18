package org.traccar.reports.model;

import org.traccar.model.Message;
import org.traccar.model.Position;

import java.util.Date;

public class EventReportBuilder extends Message {

    private String deviceName;

    public String getDeviceName() {
        return deviceName;
    }

    public EventReportBuilder setDeviceName(String deviceName) {
        this.deviceName = deviceName;
        return  this;

    }

    public EventReportBuilder(String type, Position position) {
        setType(type);
        setPositionId(position.getId());
        setDeviceId(position.getDeviceId());
        eventTime = position.getDeviceTime();
    }

    public EventReportBuilder(String type, long deviceId) {
        setType(type);
        setDeviceId(deviceId);
        eventTime = new Date();
    }

    public EventReportBuilder() {
    }

    public static final String ALL_EVENTS = "allEvents";

    public static final String TYPE_COMMAND_RESULT = "commandResult";

    public static final String TYPE_DEVICE_ONLINE = "deviceOnline";
    public static final String TYPE_DEVICE_UNKNOWN = "deviceUnknown";
    public static final String TYPE_DEVICE_OFFLINE = "deviceOffline";
    public static final String TYPE_DEVICE_INACTIVE = "deviceInactive";

    public static final String TYPE_DEVICE_MOVING = "deviceMoving";
    public static final String TYPE_DEVICE_STOPPED = "deviceStopped";

    public static final String TYPE_DEVICE_OVERSPEED = "deviceOverspeed";
    public static final String TYPE_DEVICE_FUEL_DROP = "deviceFuelDrop";

    public static final String TYPE_GEOFENCE_ENTER = "geofenceEnter";
    public static final String TYPE_GEOFENCE_EXIT = "geofenceExit";

    public static final String TYPE_ALARM = "alarm";

    public static final String TYPE_IGNITION_ON = "ignitionOn";
    public static final String TYPE_IGNITION_OFF = "ignitionOff";

    public static final String TYPE_MAINTENANCE = "maintenance";

    public static final String TYPE_TEXT_MESSAGE = "textMessage";

    public static final String TYPE_DRIVER_CHANGED = "driverChanged";

    private Date eventTime;

    public Date getEventTime() {
        return eventTime;
    }

    public EventReportBuilder setEventTime(Date eventTime) {
        this.eventTime = eventTime;
        return this;
    }

    private long positionId;

    public long getPositionId() {
        return positionId;
    }

    public EventReportBuilder setPositionId(long positionId) {
        this.positionId = positionId;
        return this;
    }

    public EventReportBuilder DeviceId(long id)
    {
        this.setDeviceId(id);
        return this;
    }

    private long geofenceId = 0;

    /*public long getGeofenceId() {
        return geofenceId;
    }*/

    public EventReportBuilder setGeofenceId(long geofenceId) {
        this.geofenceId = geofenceId;
        return this;
    }

    private long maintenanceId = 0;

    /*public long getMaintenanceId() {
        return maintenanceId;
    }*/

    public EventReportBuilder setMaintenanceId(long maintenanceId) {
        this.maintenanceId = maintenanceId;
        return this;
    }

    public  EventReport Build()
    {
        return  new EventReport(this.deviceName, this.eventTime, this.positionId, this.geofenceId, this.maintenanceId);
    }
}
