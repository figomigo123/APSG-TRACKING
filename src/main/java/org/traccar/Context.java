/*
 * Copyright 2015 - 2020 Anton Tananaev (anton@traccar.org)
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
package org.traccar;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr353.JSR353Module;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.util.URIUtil;
import org.traccar.config.Config;
import org.traccar.config.Keys;
import org.traccar.database.*;
import org.traccar.geocoder.Geocoder;
import org.traccar.helper.Log;
import org.traccar.helper.SanitizerModule;
import org.traccar.model.*;
import org.traccar.notification.EventForwarder;
import org.traccar.notification.NotificatorManager;
import org.traccar.reports.model.TripsConfig;
import org.traccar.schedule.ScheduleManager;
import org.traccar.sms.HttpSmsClient;
import org.traccar.sms.SmsManager;
import org.traccar.sms.SnsSmsClient;
import org.traccar.web.WebServer;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.ext.ContextResolver;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public final class Context {

    private static Config config;
    private static ObjectMapper objectMapper;
    private static IdentityManager identityManager;
    private static DataManager dataManager;
    private static LdapProvider ldapProvider;
    private static MailManager mailManager;
    private static MediaManager mediaManager;
    private static UsersManager usersManager;
    private static GroupsManager groupsManager;
    private static DeviceManager deviceManager;
    private static ConnectionManager connectionManager;
    private static PermissionsManager permissionsManager;
    private static WebServer webServer;
    private static ServerManager serverManager;
    private static ScheduleManager scheduleManager;
    private static GeofenceManager geofenceManager;
    private static CalendarManager calendarManager;
    private static NotificationManager notificationManager;
    private static NotificatorManager notificatorManager;
    private static VelocityEngine velocityEngine;
    private static Client client = ClientBuilder.newClient();
    private static EventForwarder eventForwarder;
    private static AttributesManager attributesManager;
    private static DriversManager driversManager;
    private static CommandsManager commandsManager;
    private static MaintenancesManager maintenancesManager;
    private static SmsManager smsManager;
    private static TripsConfig tripsConfig;

    private Context() {
    }

    public static Config getConfig() {
        return config;
    }

    public static ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    public static IdentityManager getIdentityManager() {
        return identityManager;
    }

    public static DataManager getDataManager() {
        return dataManager;
    }

    public static LdapProvider getLdapProvider() {
        return ldapProvider;
    }

    public static MailManager getMailManager() {
        return mailManager;
    }

    public static MediaManager getMediaManager() {
        return mediaManager;
    }

    public static UsersManager getUsersManager() {
        return usersManager;
    }

    public static GroupsManager getGroupsManager() {
        return groupsManager;
    }

    public static DeviceManager getDeviceManager() {
        return deviceManager;
    }

    public static ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    public static PermissionsManager getPermissionsManager() {
        return permissionsManager;
    }

    public static Geocoder getGeocoder() {
        return Main.getInjector() != null ? Main.getInjector().getInstance(Geocoder.class) : null;
    }

    public static WebServer getWebServer() {
        return webServer;
    }

    public static ServerManager getServerManager() {
        return serverManager;
    }

    public static ScheduleManager getScheduleManager() {
        return scheduleManager;
    }

    public static GeofenceManager getGeofenceManager() {
        return geofenceManager;
    }

    public static CalendarManager getCalendarManager() {
        return calendarManager;
    }

    public static NotificationManager getNotificationManager() {
        return notificationManager;
    }

    public static NotificatorManager getNotificatorManager() {
        return notificatorManager;
    }

    public static VelocityEngine getVelocityEngine() {
        return velocityEngine;
    }

    public static Client getClient() {
        return client;
    }

    public static EventForwarder getEventForwarder() {
        return eventForwarder;
    }

    public static AttributesManager getAttributesManager() {
        return attributesManager;
    }

    public static DriversManager getDriversManager() {
        return driversManager;
    }

    public static CommandsManager getCommandsManager() {
        return commandsManager;
    }

    public static MaintenancesManager getMaintenancesManager() {
        return maintenancesManager;
    }

    public static SmsManager getSmsManager() {
        return smsManager;
    }

    public static TripsConfig getTripsConfig() {
        return tripsConfig;
    }

    public static TripsConfig initTripsConfig() {
        return new TripsConfig(
                config.getLong(Keys.REPORT_TRIP_MINIMAL_TRIP_DISTANCE),
                config.getLong(Keys.REPORT_TRIP_MINIMAL_TRIP_DURATION) * 1000,
                config.getLong(Keys.REPORT_TRIP_MINIMAL_PARKING_DURATION) * 1000,
                config.getLong(Keys.REPORT_TRIP_MINIMAL_NO_DATA_DURATION) * 1000,
                config.getBoolean(Keys.REPORT_TRIP_USE_IGNITION),
                config.getBoolean(Keys.EVENT_MOTION_PROCESS_INVALID_POSITIONS),
                config.getDouble(Keys.EVENT_MOTION_SPEED_THRESHOLD));
    }

    public static void init(String configFile) throws Exception {

        try {
            config = new Config(configFile);
            Log.setupLogger(config);
        } catch (Exception e) {
            config = new Config();
            Log.setupDefaultLogger();
            throw e;
        }

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new SanitizerModule());
        objectMapper.registerModule(new JSR353Module());
        objectMapper.setConfig(
                objectMapper.getSerializationConfig().without(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS));

        client = ClientBuilder.newClient().register(new ObjectMapperContextResolver());

        if (config.hasKey(Keys.DATABASE_URL)) {
            dataManager = new DataManager(config);
        }

        if (config.hasKey(Keys.LDAP_URL)) {
            ldapProvider = new LdapProvider(config);
        }

        mailManager = new MailManager();

        mediaManager = new MediaManager(config.getString(Keys.MEDIA_PATH));

        if (dataManager != null) {
            usersManager = new UsersManager(dataManager);
            groupsManager = new GroupsManager(dataManager);
            deviceManager = new DeviceManager(dataManager);
        }

        identityManager = deviceManager;

        if (config.hasKey(Keys.WEB_PORT)) {
            webServer = new WebServer(config);
        }

        permissionsManager = new PermissionsManager(dataManager, usersManager);

        connectionManager = new ConnectionManager();

        tripsConfig = initTripsConfig();

        if (config.hasKey(Keys.SMS_HTTP_URL)) {
            smsManager = new HttpSmsClient();
        } else if (config.hasKey(Keys.SMS_AWS_REGION)) {
            smsManager = new SnsSmsClient();
        }

        initEventsModule();

        serverManager = new ServerManager();
        scheduleManager = new ScheduleManager();

        if (config.hasKey(Keys.EVENT_FORWARD_URL)) {
            eventForwarder = new EventForwarder();
        }

        attributesManager = new AttributesManager(dataManager);

        driversManager = new DriversManager(dataManager);

        commandsManager = new CommandsManager(dataManager, config.getBoolean(Keys.COMMANDS_QUEUEING));

    }

    private static void initEventsModule() {

        geofenceManager = new GeofenceManager(dataManager);
        calendarManager = new CalendarManager(dataManager);
        maintenancesManager = new MaintenancesManager(dataManager);
        notificationManager = new NotificationManager(dataManager);
        notificatorManager = new NotificatorManager();
        Properties velocityProperties = new Properties();
        velocityProperties.setProperty("file.resource.loader.path",
                Context.getConfig().getString("templates.rootPath", "templates") + "/");
        velocityProperties.setProperty("runtime.log.logsystem.class",
                "org.apache.velocity.runtime.log.NullLogChute");

        String address;
        try {
            address = config.getString(Keys.WEB_ADDRESS, InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException e) {
            address = "localhost";
        }

        String webUrl = URIUtil.newURI("http", address, config.getInteger(Keys.WEB_PORT), "", "");
        webUrl = Context.getConfig().getString("web.url", webUrl);
        velocityProperties.setProperty("web.url", webUrl);

        velocityEngine = new VelocityEngine();
        velocityEngine.init(velocityProperties);
    }

    public static void init(IdentityManager testIdentityManager, MediaManager testMediaManager) {
        config = new Config();
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JSR353Module());
        client = ClientBuilder.newClient().register(new ObjectMapperContextResolver());
        identityManager = testIdentityManager;
        mediaManager = testMediaManager;
    }

    public static <T extends BaseModel> BaseObjectManager<T> getManager(Class<T> clazz) {
        if (clazz.equals(Device.class)) {
            return (BaseObjectManager<T>) deviceManager;
        } else if (clazz.equals(Group.class)) {
            return (BaseObjectManager<T>) groupsManager;
        } else if (clazz.equals(User.class)) {
            return (BaseObjectManager<T>) usersManager;
        } else if (clazz.equals(Calendar.class)) {
            return (BaseObjectManager<T>) calendarManager;
        } else if (clazz.equals(Attribute.class)) {
            return (BaseObjectManager<T>) attributesManager;
        } else if (clazz.equals(Geofence.class)) {
            return (BaseObjectManager<T>) geofenceManager;
        } else if (clazz.equals(Driver.class)) {
            return (BaseObjectManager<T>) driversManager;
        } else if (clazz.equals(Command.class)) {
            return (BaseObjectManager<T>) commandsManager;
        } else if (clazz.equals(Maintenance.class)) {
            return (BaseObjectManager<T>) maintenancesManager;
        } else if (clazz.equals(Notification.class)) {
            return (BaseObjectManager<T>) notificationManager;
        }
        return null;
    }

    private static class ObjectMapperContextResolver implements ContextResolver<ObjectMapper> {

        @Override
        public ObjectMapper getContext(Class<?> clazz) {
            return objectMapper;
        }

    }

}
