/*
 * Copyright 2016 - 2018 Anton Tananaev (anton@traccar.org)
 * Copyright 2016 - 2018 Andrey Kunitsyn (andrey@traccar.org)
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
package org.traccar.reports;

import org.apache.poi.ss.util.WorkbookUtil;
import org.traccar.Context;
import org.traccar.api.resource.new_models.NewEvent;
import org.traccar.model.Device;
import org.traccar.model.Event;
import org.traccar.model.Group;
import org.traccar.reports.model.DeviceReport;
import org.traccar.reports.model.EventReport;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.*;

public final class Events {

    private Events() {
    }

    public static Collection<Event> getObjects(long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
                                               Collection<String> types, Date from, Date to) throws SQLException {
        ReportUtils.checkPeriodLimit(from, to);
        ArrayList<Event> result = new ArrayList<>();

        for (long deviceId : ReportUtils.getDeviceList(deviceIds, groupIds)) {
            Context.getPermissionsManager().checkDevice(userId, deviceId);
            Collection<Event> events = Context.getDataManager().getEvents(deviceId, from, to);
            boolean all = types.isEmpty() || types.contains(Event.ALL_EVENTS);
            for (Event event : events) {
                if (all || types.contains(event.getType())) {
                    long geofenceId = event.getGeofenceId();
                    long maintenanceId = event.getMaintenanceId();
                    if ((geofenceId == 0 || Context.getGeofenceManager().checkItemPermission(userId, geofenceId))
                            && (maintenanceId == 0
                            || Context.getMaintenancesManager().checkItemPermission(userId, maintenanceId))) {
                        result.add(event);
                    }
                }
            }
        }
        return result;
    }

    public static void getExcel(OutputStream outputStream,
                                long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
                                Collection<String> types, Date from, Date to) throws SQLException, IOException {
        ReportUtils.checkPeriodLimit(from, to);
        List<EventReport> eventList = new LinkedList<>();
        ArrayList<DeviceReport> devicesEvents = new ArrayList<>();
        ArrayList<String> sheetNames = new ArrayList<>();
        HashMap<Long, String> geofenceNames = new HashMap<>();
        HashMap<Long, String> maintenanceNames = new HashMap<>();
        String devicesNames = ",";
        String groupsNames = "";
        Collection<Long> deviceList= ReportUtils.getDeviceList(deviceIds, groupIds);
        for (long deviceId : deviceList) {
            Context.getPermissionsManager().checkDevice(userId, deviceId);
        }


        Collection<NewEvent> events = Context.getDataManager().getEvents2(deviceList,from,to,types);



            List<EventReport> eventReports = new LinkedList<>();
            for (NewEvent ev : events) {
                String type = ev.getType();
                type = type.replace("device", "");
                if (type.equals("Unknown")) type = "Idle";
                ev.setType(type);

                EventReport eventReport=EventReport.Builder()
                        .setDeviceName(ev.getDviceName())
                        .setEventTime(ev.getEventTime())
                        .setGeofenceId(ev.getGeofenceId())
                        .setMaintenanceId(ev.getMaintenanceId())
                        .setPositionId(ev.getPositionId())
                        .DeviceId(ev.getDeviceId())
                        .Type(ev.getType())
                        .Build();
                eventReport.setAttributes(ev.getAttributes());
                eventReports.add(eventReport   );
                if ( !devicesNames.contains(","+ev.getDviceName()+","))
                    devicesNames = devicesNames + ev.getDviceName()+ "," ;
                if (ev.getGroupId() != 0) {
                    Group group = Context.getGroupsManager().getById(ev.getGroupId());
                    if (group != null && !groupsNames.contains(group.getName())) {
                        //deviceEvents.setGroupName(group.getName());
                        groupsNames = groupsNames + "," + group.getName();
                    }
                }

            }



            eventList.addAll(eventReports);




        if (devicesNames.length() > 1) {
            devicesNames = devicesNames.substring(1, devicesNames.length());
        }
        if (groupsNames.length() > 1) {
            groupsNames = groupsNames.substring(1, groupsNames.length());
        }
        DeviceReport deviceEvents = new DeviceReport();
        deviceEvents.setDeviceName(devicesNames);
        deviceEvents.setObjects(eventList);
        deviceEvents.setGroupName(groupsNames);
        devicesEvents.add(deviceEvents);

        sheetNames.add(WorkbookUtil.createSafeSheetName("Events"));


        String templatePath = Context.getConfig().getString("report.templatesPath",
                "templates/export/");
        try (InputStream inputStream = new FileInputStream(templatePath + "/events.xlsx")) {
            org.jxls.common.Context jxlsContext = ReportUtils.initializeContext(userId);
            jxlsContext.putVar("devices", devicesEvents);
            jxlsContext.putVar("sheetNames", sheetNames);
            jxlsContext.putVar("geofenceNames", geofenceNames);
            jxlsContext.putVar("maintenanceNames", maintenanceNames);
            jxlsContext.putVar("from", from);
            jxlsContext.putVar("to", to);
            ReportUtils.processTemplateWithSheets(inputStream, outputStream, jxlsContext);
        }
    }
    public static void getExcel2(OutputStream outputStream,
                                long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
                                Collection<String> types, Date from, Date to) throws SQLException, IOException {
        ReportUtils.checkPeriodLimit(from, to);
        List<EventReport> eventList = new LinkedList<>();
        ArrayList<DeviceReport> devicesEvents = new ArrayList<>();
        ArrayList<String> sheetNames = new ArrayList<>();
        HashMap<Long, String> geofenceNames = new HashMap<>();
        HashMap<Long, String> maintenanceNames = new HashMap<>();
        String devicesNames = "";
        String groupsNames = "";
        for (long deviceId : ReportUtils.getDeviceList(deviceIds, groupIds)) {
            Context.getPermissionsManager().checkDevice(userId, deviceId);
            Collection<Event> events = Context.getDataManager().getEvents(deviceId, from, to);

            boolean all = types.isEmpty() || types.contains(Event.ALL_EVENTS);
          /*  for (Iterator<Event> iterator = events.iterator(); iterator.hasNext(); ) {
                Event event = iterator.next();
                if (all || types.contains(event.getType())) {
                    long geofenceId = event.getGeofenceId();
                    long maintenanceId = event.getMaintenanceId();
                    if (geofenceId != 0) {
                        if (Context.getGeofenceManager().checkItemPermission(userId, geofenceId)) {
                            Geofence geofence = Context.getGeofenceManager().getById(geofenceId);
                            if (geofence != null) {
                                geofenceNames.put(geofenceId, geofence.getName());
                            }
                        } else {
                            iterator.remove();
                        }
                    } else if (maintenanceId != 0) {
                        if (Context.getMaintenancesManager().checkItemPermission(userId, maintenanceId)) {
                            Maintenance maintenance = Context.getMaintenancesManager().getById(maintenanceId);
                            if (maintenance != null) {
                                maintenanceNames.put(maintenanceId, maintenance.getName());
                            }
                        } else {
                            iterator.remove();
                        }
                    }
                } else {
                    iterator.remove();
                }
            }*/
            /* DeviceReport deviceEvents = new DeviceReport();*/
            Device device = Context.getIdentityManager().getById(deviceId);
            List<EventReport> eventReports = new LinkedList<>();
            for (Event ev : events) {
                String type = ev.getType();
                type = type.replace("device", "");
                if (type.equals("Unknown")) type = "Idle";
                ev.setType(type);
                Map<String, Object> attributes = new LinkedHashMap<>();
                attributes.put("alarm", "Sos");
                attributes.put("alarm", "true");
                attributes.put("alarm", "good");
                ev.setAttributes(attributes);
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                System.out.println(device.getName());
                System.out.println(ev.getAttributes().toString());
                System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
                EventReport eventReport=EventReport.Builder()
                        .setDeviceName(device.getName())
                        .setEventTime(ev.getEventTime())
                        .setGeofenceId(ev.getGeofenceId())
                        .setMaintenanceId(ev.getMaintenanceId())
                        .setPositionId(ev.getPositionId())
                        .DeviceId(ev.getDeviceId())
                        .Type(ev.getType())
                        .Build();
                eventReport.setAttributes(ev.getAttributes());
                eventReports.add(eventReport   );

            }
            //deviceEvents.setDeviceName(device.getName());
            //sheetNames.add(WorkbookUtil.createSafeSheetName(deviceEvents.getDeviceName()));
            devicesNames = devicesNames + "," + device.getName();
            if (device.getGroupId() != 0) {
                Group group = Context.getGroupsManager().getById(device.getGroupId());
                if (group != null && !groupsNames.contains(group.getName())) {
                    //deviceEvents.setGroupName(group.getName());
                    groupsNames = groupsNames + "," + group.getName();
                }
            }
            /*deviceEvents.setObjects(events);*/
            //devicesEvents.add(deviceEvents);
            eventList.addAll(eventReports);

        }


        if (devicesNames.length() > 1) {
            devicesNames = devicesNames.substring(1, devicesNames.length());
        }
        if (groupsNames.length() > 1) {
            groupsNames = groupsNames.substring(1, groupsNames.length());
        }
        DeviceReport deviceEvents = new DeviceReport();
        deviceEvents.setDeviceName(devicesNames);
        deviceEvents.setObjects(eventList);
        deviceEvents.setGroupName(groupsNames);
        devicesEvents.add(deviceEvents);
        System.out.println("======================================");
        System.out.println("devicesNames: " + devicesNames);
        System.out.println("eventList: " + eventList);
        System.out.println("groupsNames: " + groupsNames);
        System.out.println("deviceEvents: " + deviceEvents);
        System.out.println("======================================");



        /*sheetNames.add(WorkbookUtil.createSafeSheetName(devicesNames));*/
        sheetNames.add(WorkbookUtil.createSafeSheetName("Events"));


        String templatePath = Context.getConfig().getString("report.templatesPath",
                "templates/export/");
        try (InputStream inputStream = new FileInputStream(templatePath + "/events.xlsx")) {
            org.jxls.common.Context jxlsContext = ReportUtils.initializeContext(userId);
            jxlsContext.putVar("devices", devicesEvents);
            jxlsContext.putVar("sheetNames", sheetNames);
            jxlsContext.putVar("geofenceNames", geofenceNames);
            jxlsContext.putVar("maintenanceNames", maintenanceNames);
            jxlsContext.putVar("from", from);
            jxlsContext.putVar("to", to);
            ReportUtils.processTemplateWithSheets(inputStream, outputStream, jxlsContext);
        }
    }
}
