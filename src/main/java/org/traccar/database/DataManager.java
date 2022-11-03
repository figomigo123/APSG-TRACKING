/*
 * Copyright 2012 - 2021 Anton Tananaev (anton@traccar.org)
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
package org.traccar.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.exception.LiquibaseException;
import liquibase.resource.FileSystemResourceAccessor;
import liquibase.resource.ResourceAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.traccar.Context;
import org.traccar.api.resource.new_models.NewEvent;
import org.traccar.config.Config;
import org.traccar.config.Keys;
import org.traccar.model.Calendar;
import org.traccar.model.*;

import javax.sql.DataSource;
import java.beans.Introspector;
import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.*;

public class DataManager {

    public static final String ACTION_SELECT_ALL = "selectAll";
    public static final String ACTION_SELECT = "select";
    public static final String ACTION_INSERT = "insert";
    public static final String ACTION_UPDATE = "update";
    public static final String ACTION_DELETE = "delete";
    private static final Logger LOGGER = LoggerFactory.getLogger(DataManager.class);
    private final Config config;
    private final boolean forceLdap;
    private DataSource dataSource;
    private boolean generateQueries;

    public DataManager(Config config) throws Exception {
        this.config = config;

        forceLdap = config.getBoolean(Keys.LDAP_FORCE);

        initDatabase();
        initDatabaseSchema();
    }

    public static String constructObjectQuery(String action, Class<?> clazz, boolean extended) {
        switch (action) {
            case ACTION_INSERT:
            case ACTION_UPDATE:
                StringBuilder result = new StringBuilder();
                StringBuilder fields = new StringBuilder();
                StringBuilder values = new StringBuilder();

                Set<Method> methods = new HashSet<>(Arrays.asList(clazz.getMethods()));
                methods.removeAll(Arrays.asList(Object.class.getMethods()));
                methods.removeAll(Arrays.asList(BaseModel.class.getMethods()));
                for (Method method : methods) {
                    boolean skip;
                    if (extended) {
                        skip = !method.isAnnotationPresent(QueryExtended.class);
                    } else {
                        skip = method.isAnnotationPresent(QueryIgnore.class)
                                || method.isAnnotationPresent(QueryExtended.class) && !action.equals(ACTION_INSERT);
                    }
                    if (!skip && method.getName().startsWith("get") && method.getParameterTypes().length == 0) {
                        String name = Introspector.decapitalize(method.getName().substring(3));
                        if (action.equals(ACTION_INSERT)) {
                            fields.append(name).append(", ");
                            values.append(":").append(name).append(", ");
                        } else {
                            fields.append(name).append(" = :").append(name).append(", ");
                        }
                    }
                }
                fields.setLength(fields.length() - 2);
                if (action.equals(ACTION_INSERT)) {
                    values.setLength(values.length() - 2);
                    result.append("INSERT INTO ").append(getObjectsTableName(clazz)).append(" (");
                    result.append(fields).append(") ");
                    result.append("VALUES (").append(values).append(")");
                } else {
                    result.append("UPDATE ").append(getObjectsTableName(clazz)).append(" SET ");
                    result.append(fields);
                    result.append(" WHERE id = :id");
                }
                return result.toString();
            case ACTION_SELECT_ALL:
                return "SELECT * FROM " + getObjectsTableName(clazz);
            case ACTION_SELECT:
                return "SELECT * FROM " + getObjectsTableName(clazz) + " WHERE id = :id";
            case ACTION_DELETE:
                return "DELETE FROM " + getObjectsTableName(clazz) + " WHERE id = :id";
            default:
                throw new IllegalArgumentException("Unknown action");
        }
    }

    public static String constructPermissionQuery(String action, Class<?> owner, Class<?> property) {
        switch (action) {
            case ACTION_SELECT_ALL:
                return "SELECT " + makeNameId(owner) + ", " + makeNameId(property) + " FROM "
                        + getPermissionsTableName(owner, property);
            case ACTION_INSERT:
                return "INSERT INTO " + getPermissionsTableName(owner, property)
                        + " (" + makeNameId(owner) + ", " + makeNameId(property) + ") VALUES (:"
                        + makeNameId(owner) + ", :" + makeNameId(property) + ")";
            case ACTION_DELETE:
                return "DELETE FROM " + getPermissionsTableName(owner, property)
                        + " WHERE " + makeNameId(owner) + " = :" + makeNameId(owner)
                        + " AND " + makeNameId(property) + " = :" + makeNameId(property);
            default:
                throw new IllegalArgumentException("Unknown action");
        }
    }

    private static String getPermissionsTableName(Class<?> owner, Class<?> property) {
        String propertyName = property.getSimpleName();
        if (propertyName.equals("ManagedUser")) {
            propertyName = "User";
        }
        return "tc_" + Introspector.decapitalize(owner.getSimpleName())
                + "_" + Introspector.decapitalize(propertyName);
    }

    private static String getObjectsTableName(Class<?> clazz) {
        String result = "tc_" + Introspector.decapitalize(clazz.getSimpleName());
        // Add "s" ending if object name is not plural already
        if (!result.endsWith("s")) {
            result += "s";
        }
        return result;
    }

    public static Class<?> getClassByName(String name) throws ClassNotFoundException {
        switch (name.toLowerCase().replace("id", "")) {
            case "device":
                return Device.class;
            case "group":
                return Group.class;
            case "user":
                return User.class;
            case "manageduser":
                return ManagedUser.class;
            case "geofence":
                return Geofence.class;
            case "driver":
                return Driver.class;
            case "attribute":
                return Attribute.class;
            case "calendar":
                return Calendar.class;
            case "command":
                return Command.class;
            case "maintenance":
                return Maintenance.class;
            case "notification":
                return Notification.class;
            default:
                throw new ClassNotFoundException();
        }
    }

    private static String makeNameId(Class<?> clazz) {
        String name = clazz.getSimpleName();
        return Introspector.decapitalize(name) + (!name.contains("Id") ? "Id" : "");
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    private void initDatabase() throws Exception {

        String driverFile = config.getString(Keys.DATABASE_DRIVER_FILE);
        if (driverFile != null) {
            ClassLoader classLoader = ClassLoader.getSystemClassLoader();
            try {
                Method method = classLoader.getClass().getDeclaredMethod("addURL", URL.class);
                method.setAccessible(true);
                method.invoke(classLoader, new File(driverFile).toURI().toURL());
            } catch (NoSuchMethodException e) {
                Method method = classLoader.getClass()
                        .getDeclaredMethod("appendToClassPathForInstrumentation", String.class);
                method.setAccessible(true);
                method.invoke(classLoader, driverFile);
            }
        }

        String driver = config.getString(Keys.DATABASE_DRIVER);
        if (driver != null) {
            Class.forName(driver);
        }

        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setDriverClassName(driver);
        hikariConfig.setJdbcUrl(config.getString(Keys.DATABASE_URL));
        hikariConfig.setUsername(config.getString(Keys.DATABASE_USER));
        hikariConfig.setPassword(config.getString(Keys.DATABASE_PASSWORD));
        hikariConfig.setConnectionInitSql(config.getString(Keys.DATABASE_CHECK_CONNECTION));
        hikariConfig.setIdleTimeout(600000);

        int maxPoolSize = config.getInteger(Keys.DATABASE_MAX_POOL_SIZE);
        if (maxPoolSize != 0) {
            hikariConfig.setMaximumPoolSize(maxPoolSize);
        }

        generateQueries = config.getBoolean(Keys.DATABASE_GENERATE_QUERIES);

        dataSource = new HikariDataSource(hikariConfig);
    }

    private String getQuery(String key) {
        String query = config.getString(key);
        if (query == null) {
            LOGGER.info("Query not provided: " + key);
        }
        return query;
    }

    public String getQuery(String action, Class<?> clazz) {
        return getQuery(action, clazz, false);
    }

    public String getQuery(String action, Class<?> clazz, boolean extended) {
        String queryName;
        if (action.equals(ACTION_SELECT_ALL)) {
            queryName = "database.select" + clazz.getSimpleName() + "s";
        } else {
            queryName = "database." + action.toLowerCase() + clazz.getSimpleName();
            if (extended) {
                queryName += "Extended";
            }
        }
        String query = config.getString(queryName);
        if (query == null) {
            if (generateQueries) {
                query = constructObjectQuery(action, clazz, extended);
            } else {
                LOGGER.info("Query not provided: " + queryName);
            }
        }
        return query;
    }

    public String getQuery(String action, Class<?> owner, Class<?> property) {
        String queryName;
        switch (action) {
            case ACTION_SELECT_ALL:
                queryName = "database.select" + owner.getSimpleName() + property.getSimpleName() + "s";
                break;
            case ACTION_INSERT:
                queryName = "database.link" + owner.getSimpleName() + property.getSimpleName();
                break;
            default:
                queryName = "database.unlink" + owner.getSimpleName() + property.getSimpleName();
                break;
        }
        String query = config.getString(queryName);
        if (query == null) {
            if (generateQueries) {
                query = constructPermissionQuery(
                        action, owner, property.equals(User.class) ? ManagedUser.class : property);
            } else {
                LOGGER.info("Query not provided: " + queryName);
            }
        }
        return query;
    }

    private void initDatabaseSchema() throws LiquibaseException {

        if (config.hasKey(Keys.DATABASE_CHANGELOG)) {

            ResourceAccessor resourceAccessor = new FileSystemResourceAccessor(new File("."));

            Database database = DatabaseFactory.getInstance().openDatabase(
                    config.getString(Keys.DATABASE_URL),
                    config.getString(Keys.DATABASE_USER),
                    config.getString(Keys.DATABASE_PASSWORD),
                    config.getString(Keys.DATABASE_DRIVER),
                    null, null, null, resourceAccessor);

            Liquibase liquibase = new Liquibase(
                    config.getString(Keys.DATABASE_CHANGELOG), resourceAccessor, database);

            liquibase.clearCheckSums();

            liquibase.update(new Contexts());
        }
    }

    public User login(String email, String password) throws SQLException {
        User user = QueryBuilder.create(dataSource, getQuery("database.loginUser"))
                .setString("email", email.trim())
                .executeQuerySingle(User.class);
        LdapProvider ldapProvider = Context.getLdapProvider();
        if (user != null) {
            if (ldapProvider != null && user.getLogin() != null && ldapProvider.login(user.getLogin(), password)
                    || !forceLdap && user.isPasswordValid(password)) {
                return user;
            }
        } else {
            if (ldapProvider != null && ldapProvider.login(email, password)) {
                user = ldapProvider.getUser(email);
                Context.getUsersManager().addItem(user);
                return user;
            }
        }
        return null;
    }

    public void updateDeviceStatus(Device device) throws SQLException {
        QueryBuilder.create(dataSource, getQuery(ACTION_UPDATE, Device.class, true))
                .setObject(device)
                .executeUpdate();
    }

    public Collection<Position> getPositions(long deviceId, Date from, Date to) throws SQLException {
        return QueryBuilder.create(dataSource, getQuery("database.selectPositions"))
                .setLong("deviceId", deviceId)
                .setDate("from", from)
                .setDate("to", to)
                .executeQuery(Position.class);
    }

    public void updateLatestPosition(Position position) throws SQLException {
        QueryBuilder.create(dataSource, getQuery("database.updateLatestPosition"))
                .setDate("now", new Date())
                .setObject(position)
                .executeUpdate();
    }

    public Collection<Position> getLatestPositions() throws SQLException {
        return QueryBuilder.create(dataSource, getQuery("database.selectLatestPositions"))
                .executeQuery(Position.class);
    }

    public Server getServer() throws SQLException {
        return QueryBuilder.create(dataSource, getQuery(ACTION_SELECT_ALL, Server.class))
                .executeQuerySingle(Server.class);
    }

    public Collection<Event> getEvents(long deviceId, Date from, Date to) throws SQLException {
        return QueryBuilder.create(dataSource, getQuery("database.selectEvents"))
                .setLong("deviceId", deviceId)
                .setDate("from", from)
                .setDate("to", to)
                .executeQuery(Event.class);
    }

    public Collection<NewEvent> getEvents2(Collection<Long> deviceId, Date from, Date to,Collection<String> types) throws SQLException {
        Collection<NewEvent> events = null;
        String ids[] = {""};
        String ty[] = {""};
        deviceId.forEach(x -> {
            ids[0] += Long.toString(x);
            ids[0] += ",";
        });
        String all = ids[0].substring(0, ids[0].length() - 1);
        types.forEach(x -> {
            if(x.equals("deviceIdle"))
            ty[0] += "\""+("deviceUnknown")+"\"";
           else ty[0] += "\""+(x)+"\"";
            ty[0] += ",";
        });
        String alltyeps = ty[0].substring(0, ty[0].length() - 1);

        String statment = "select tc_events.*,tc_devices.name,tc_devices.groupid FROM tc_events INNER JOIN tc_devices on tc_events.deviceid=tc_devices.id  " +
                "where  tc_events.deviceid in (" + all + ") and tc_events.eventtime  BETWEEN '" + new Timestamp(from.getTime()) + "' and  '" +
                new Timestamp(to.getTime())+ "'  ";//ORDER BY tc_events.eventtime ";

        String statment2 = "select tc_events.*,tc_devices.name,tc_devices.groupid FROM tc_events INNER JOIN tc_devices on tc_events.deviceid=tc_devices.id  " +
                "where  tc_events.deviceid in (" + all + ") and tc_events.type in (" + alltyeps + ") and tc_events.eventtime  BETWEEN '" + new Timestamp(from.getTime()) + "' and  '" +
                new Timestamp(to.getTime())+ "' ";// ORDER BY tc_events.eventtime ";


        if(types==null||types.size()==0||types.contains("allEvents")||types.contains("%"))
            events = QueryBuilder.create(dataSource, statment)
                    .executeQuery(NewEvent.class);
        else
            events = QueryBuilder.create(dataSource, statment2)
                    .executeQuery(NewEvent.class);

        return events;
    }

    public Collection<Landmark> getUserLandmarks(long userid) throws SQLException {
        return QueryBuilder.create(dataSource, getQuery("database.selectLandmarks"))
                .setLong("userid", userid)
                .executeQuery(Landmark.class);
    }

    public Map<String, Long> getEventsCountByType() throws SQLException {


        return QueryBuilder.create(dataSource, getQuery("database.selectEventsByType"))
                .getEventsCountByType();
    }

    public Long getEventsCount() throws SQLException {
        String ss="SELECT  MAX(id)  FROM tc_events ";
        Long l= QueryBuilder.create(dataSource, ss)
                .getEventsCount();
return l;


    }

    public Collection<Statistics> getStatistics(Date from, Date to) throws SQLException {
        return QueryBuilder.create(dataSource, getQuery("database.selectStatistics"))
                .setDate("from", from)
                .setDate("to", to)
                .executeQuery(Statistics.class);
    }

    public Collection<Permission> getPermissions(Class<? extends BaseModel> owner, Class<? extends BaseModel> property)
            throws SQLException, ClassNotFoundException {
        return QueryBuilder.create(dataSource, getQuery(ACTION_SELECT_ALL, owner, property))
                .executePermissionsQuery();
    }

    public void linkObject(Class<?> owner, long ownerId, Class<?> property, long propertyId, boolean link)
            throws SQLException {
        QueryBuilder.create(dataSource, getQuery(link ? ACTION_INSERT : ACTION_DELETE, owner, property))
                .setLong(makeNameId(owner), ownerId)
                .setLong(makeNameId(property), propertyId)
                .executeUpdate();
    }

    public <T extends BaseModel> T getObject(Class<T> clazz, long entityId) throws SQLException {
        return QueryBuilder.create(dataSource, getQuery(ACTION_SELECT, clazz))
                .setLong("id", entityId)
                .executeQuerySingle(clazz);
    }

    public <T extends BaseModel> Collection<T> getObjects(Class<T> clazz) throws SQLException {
        return QueryBuilder.create(dataSource, getQuery(ACTION_SELECT_ALL, clazz))
                .executeQuery(clazz);
    }

    public Collection<NewEvent> getEvents(long limit1, long limit2) throws SQLException {
        Collection<NewEvent> events = null;
        String statment = "select tc_events.*,tc_devices.name FROM tc_events INNER JOIN tc_devices on tc_events.deviceid=tc_devices.id  " +
                "where tc_events.id >= " + limit1 + " and tc_events.id <= " + limit2 ;//+ "  ORDER BY eventtime DESC";
        try {
            events = QueryBuilder.create(dataSource, statment)
                    .executeQuery(NewEvent.class);
        } catch (Exception e) {
            events = QueryBuilder.create(dataSource, statment)
                    .executeQuery(NewEvent.class);
        }
        return events;
    }

    public Collection<Event> getEvents7(long limit1, long limit2) throws SQLException {
        Collection<Event> events = null;
        String statment = "select tc_events.*,tc_devices.name FROM tc_events INNER JOIN tc_devices on tc_events.deviceid=tc_devices.id  " +
                "where tc_events.id >= " + limit1 + " and tc_events.id <= " + limit2 + "  ORDER BY eventtime DESC";
        try {
            events = QueryBuilder.create(dataSource, statment)
                    .executeQuery(Event.class);
        } catch (Exception e) {
            events = QueryBuilder.create(dataSource, statment)
                    .executeQuery(Event.class);
        }
        return events;
    }

    public void addObject(BaseModel entity) throws SQLException {
        entity.setId(QueryBuilder.create(dataSource, getQuery(ACTION_INSERT, entity.getClass()), true)
                .setObject(entity)
                .executeUpdate());
    }

    public void updateObject(BaseModel entity) throws SQLException {
        QueryBuilder.create(dataSource, getQuery(ACTION_UPDATE, entity.getClass()))
                .setObject(entity)
                .executeUpdate();
        if (entity instanceof User && ((User) entity).getHashedPassword() != null) {
            QueryBuilder.create(dataSource, getQuery(ACTION_UPDATE, User.class, true))
                    .setObject(entity)
                    .executeUpdate();
        }
    }

    public void removeObject(Class<? extends BaseModel> clazz, long entityId) throws SQLException {
        QueryBuilder.create(dataSource, getQuery(ACTION_DELETE, clazz))
                .setLong("id", entityId)
                .executeUpdate();
    }

}
