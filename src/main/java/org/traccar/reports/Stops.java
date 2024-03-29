/*
 * Copyright 2017 Anton Tananaev (anton@traccar.org)
 * Copyright 2017 Andrey Kunitsyn (andrey@traccar.org)
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

import org.traccar.Context;
import org.traccar.Main;
import org.traccar.database.DeviceManager;
import org.traccar.database.IdentityManager;
import org.traccar.model.Device;
import org.traccar.model.Group;
import org.traccar.reports.model.DeviceReport;
import org.traccar.reports.model.StopReport;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.*;

public final class Stops {

    private Stops() {
    }

    private static Collection<StopReport> detectStops(long deviceId, Date from, Date to) throws SQLException {
        boolean ignoreOdometer = Context.getDeviceManager()
                .lookupAttributeBoolean(deviceId, "report.ignoreOdometer", false, false, true);

        IdentityManager identityManager = Main.getInjector().getInstance(IdentityManager.class);
        DeviceManager deviceManager = Main.getInjector().getInstance(DeviceManager.class);

        return ReportUtils.detectTripsAndStops(
                identityManager, deviceManager, Context.getDataManager().getPositions(deviceId, from, to),
                Context.getTripsConfig(), ignoreOdometer, StopReport.class);
    }

    public static Collection<StopReport> getObjects(
            long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Date from, Date to) throws SQLException {
        ReportUtils.checkPeriodLimit(from, to);
        ArrayList<StopReport> result = new ArrayList<>();
        for (long deviceId : ReportUtils.getDeviceList(deviceIds, groupIds)) {
            Context.getPermissionsManager().checkDevice(userId, deviceId);
            result.addAll(detectStops(deviceId, from, to));
        }
        return result;
    }

    public static void getExcel(
            OutputStream outputStream, long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Date from, Date to) throws SQLException, IOException {
        ReportUtils.checkPeriodLimit(from, to);
        ArrayList<DeviceReport> devicesStops = new ArrayList<>();
        ArrayList<String> sheetNames = new ArrayList<>();
        String devicesNames = "";
        String groupsNames = "";
        List<StopReport> stopReportList = new LinkedList<>();
        for (long deviceId : ReportUtils.getDeviceList(deviceIds, groupIds)) {
            Context.getPermissionsManager().checkDevice(userId, deviceId);
            Collection<StopReport> stops = detectStops(deviceId, from, to);

            Device device = Context.getIdentityManager().getById(deviceId);
            //deviceStops.setDeviceName(device.getName());
            //sheetNames.add(WorkbookUtil.createSafeSheetName(deviceStops.getDeviceName()));
            devicesNames = devicesNames + "," + device.getName();
            if (device.getGroupId() != 0) {
                Group group = Context.getGroupsManager().getById(device.getGroupId());
                if (group != null) {
                    // deviceStops.setGroupName(group.getName());
                    groupsNames = groupsNames + ',' + group.getName();
                }
            }
            for (StopReport s : stops) {
                s.setDeviceName(device.getName());
            }
            stopReportList.addAll(stops);
        }

        if (devicesNames.length() > 1) {
            devicesNames = devicesNames.substring(1, devicesNames.length());
        }
        if (groupsNames.length() > 1) {
            groupsNames = groupsNames.substring(1, groupsNames.length());
        }
        DeviceReport deviceStops = new DeviceReport();
        deviceStops.setObjects(stopReportList);
        devicesStops.add(deviceStops);
        deviceStops.setDeviceName(devicesNames);
        deviceStops.setGroupName(devicesNames);
        /*sheetNames.add(devicesNames);*/
        sheetNames.add("Stops");
        String templatePath = Context.getConfig().getString("report.templatesPath",
                "templates/export/");
        try (InputStream inputStream = new FileInputStream(templatePath + "/stops.xlsx")) {
            org.jxls.common.Context jxlsContext = ReportUtils.initializeContext(userId);
            jxlsContext.putVar("devices", devicesStops);
            jxlsContext.putVar("sheetNames", sheetNames);
            jxlsContext.putVar("from", from);
            jxlsContext.putVar("to", to);
            ReportUtils.processTemplateWithSheets(inputStream, outputStream, jxlsContext);
        }
    }

}
