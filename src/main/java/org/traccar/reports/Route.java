/*
 * Copyright 2016 Anton Tananaev (anton@traccar.org)
 * Copyright 2016 Andrey Kunitsyn (andrey@traccar.org)
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
import org.traccar.model.Device;
import org.traccar.model.Group;
import org.traccar.model.Position;
import org.traccar.reports.model.DeviceReport;
import org.traccar.reports.model.RouteReport;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.*;

public final class Route {

    private Route() {
    }

    public static Collection<Position> getObjects(long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Date from, Date to) throws SQLException {
        ReportUtils.checkPeriodLimit(from, to);
        ArrayList<Position> result = new ArrayList<>();
        for (long deviceId: ReportUtils.getDeviceList(deviceIds, groupIds)) {
            Context.getPermissionsManager().checkDevice(userId, deviceId);
            result.addAll(Context.getDataManager().getPositions(deviceId, from, to));
        }
        return result;
    }

    public static void getExcel(OutputStream outputStream,
            long userId, Collection<Long> deviceIds, Collection<Long> groupIds,
            Date from, Date to) throws SQLException, IOException {
        ReportUtils.checkPeriodLimit(from, to);
        ArrayList<DeviceReport> devicesRoutes = new ArrayList<>();
        ArrayList<String> sheetNames = new ArrayList<>();
        String devicesNames = "";
        String groupsNames = "";
        List<RouteReport> positionList = new LinkedList<>();
        for (long deviceId: ReportUtils.getDeviceList(deviceIds, groupIds)) {
            Context.getPermissionsManager().checkDevice(userId, deviceId);
            Collection<Position> positions = Context.getDataManager()
                    .getPositions(deviceId, from, to);
            List<RouteReport> routes = new LinkedList<>();
            Device device = Context.getIdentityManager().getById(deviceId);
            for(Position p :positions)
            {
                routes.add( RouteReport.Builder()
                        .setProtocol(p.getProtocol())
                        .setAccuracy(p.getAccuracy())
                        .setAddress(p.getAddress())
                        .setAltitude(p.getAltitude())
                        .setTime(p.getServerTime())
                        .setCourse(p.getCourse())
                        .setDeviceTime(p.getDeviceTime())
                        .setFixTime(p.getFixTime())
                        .setLatitude(p.getLatitude())
                        .setLongitude(p.getLongitude())
                        .setNetwork(p.getNetwork())
                        .setOutdated(p.getOutdated())
                        .setServerTime(p.getServerTime())
                        .setSpeed(p.getSpeed())
                        .setValid(p.getValid())
                        .setDeviceName(device.getName())
                        .Build()
                );
            }
            positionList.addAll(routes);


            devicesNames = devicesNames+" , "+device.getName();
            //deviceRoutes.setDeviceName(device.getName());
          /*  if(sheetNames.stream().count() == 0)
            {
                sheetNames.add(WorkbookUtil.createSafeSheetName(deviceRoutes.getDeviceName()));
            }*/

            if (device.getGroupId() != 0) {
                Group group = Context.getGroupsManager().getById(device.getGroupId());
                if (group != null) {
                    groupsNames = groupsNames + ","+group.getName();
                    //deviceRoutes.setGroupName(group.getName());
                }
            }


            //devicesRoutes.add(deviceRoutes);
        }
        if(devicesNames.length() > 1)
        {
            devicesNames = devicesNames.substring(1, devicesNames.length());
        }
        if(groupsNames.length() > 1)
        {
            groupsNames = groupsNames.substring(1, groupsNames.length());
        }
        DeviceReport deviceRoutes = new DeviceReport();
        deviceRoutes.setGroupName(groupsNames);
        deviceRoutes.setDeviceName(devicesNames);
        deviceRoutes.setObjects(positionList);
        devicesRoutes.add(deviceRoutes);

        /*sheetNames.add(WorkbookUtil.createSafeSheetName(devicesNames));*/
        sheetNames.add(WorkbookUtil.createSafeSheetName("Routes"));
        String templatePath = Context.getConfig().getString("report.templatesPath",
                "templates/export/");
        try (InputStream inputStream = new FileInputStream(templatePath + "/route.xlsx")) {
            org.jxls.common.Context jxlsContext = ReportUtils.initializeContext(userId);
            jxlsContext.putVar("devices", devicesRoutes);
            jxlsContext.putVar("sheetNames", sheetNames);
            jxlsContext.putVar("from", from);
            jxlsContext.putVar("to", to);
            ReportUtils.processTemplateWithSheets(inputStream, outputStream, jxlsContext);
        }
    }
}
