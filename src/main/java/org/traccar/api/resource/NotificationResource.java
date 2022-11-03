/*
 * Copyright 2016 - 2018 Anton Tananaev (anton@traccar.org)
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
package org.traccar.api.resource;

import org.traccar.Context;
import org.traccar.api.ExtendedObjectResource;
import org.traccar.api.resource.new_models.NewNotification;
import org.traccar.database.BaseObjectManager;
import org.traccar.model.Event;
import org.traccar.model.Notification;
import org.traccar.model.Typed;
import org.traccar.notification.MessageException;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.*;


@Path("notifications")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class NotificationResource extends ExtendedObjectResource<Notification> {

    public NotificationResource() {
        super(Notification.class);
    }

    @GET
    @Path("types")
    public Collection<Typed> get() {
        return Context.getNotificationManager().getAllNotificationTypes();
    }

    @GET
    @Path("notificators")
    public Collection<Typed> getNotificators() {
        Set<Typed> allNotificatorTypes = Context.getNotificatorManager().getAllNotificatorTypes();
        for (Typed t : allNotificatorTypes) System.out.println(t.getType());
        return allNotificatorTypes;
    }

    @POST
    @Path("test")
    public Response testMessage() throws MessageException, InterruptedException {
        for (Typed method : Context.getNotificatorManager().getAllNotificatorTypes()) {
            Context.getNotificatorManager()
                    .getNotificator(method.getType()).sendSync(getUserId(), new Event("test", 0), null);
        }
        return Response.noContent().build();
    }

    @POST
    @Path("test/{notificator}")
    public Response testMessage(@PathParam("notificator") String notificator)
            throws MessageException, InterruptedException {
        Context.getNotificatorManager().getNotificator(notificator).sendSync(getUserId(), new Event("test", 0), null);
        return Response.noContent().build();
    }


    @Path("view")
    @GET
    public List<NewNotification> getView(@QueryParam("all") boolean all, @QueryParam("userId") long userId) throws SQLException {
        BaseObjectManager<Notification> manager = Context.getManager(getBaseClass());
        Collection<Notification> items = manager.getItems(getSimpleManagerItems(manager, all, userId));
        List<NewNotification> newNotifications = new ArrayList<>();
        items.forEach(u -> {
            NewNotification newNotification = new NewNotification(u);
            newNotifications.add(newNotification);
        });
        return newNotifications;
    }

    @Path("alarms")
    @GET
    public List<String> getAlarms() {
        return Arrays.asList(
                "GENERAL"
                , "SOS"
                , "VIBRATION"
                , "MOVEMENT"
                , "LOW SPEED"
                , "OVER SPEED"
                , "FALL DOWN"
                , "LOW POWER"
                , "LOW BATTERY"
                , "FAULT"
                , "POWER OFF"
                , "POWER ON"
                , "DOOR"
                , "LOCK"
                , "UNLOCK"
                , "GEOFENCE"
                , "GEOFENCE ENTER"
                , "GEOFENCE EXIT"
                , "GPS ANTENNA CUT"
                , "ACCIDENT"
                , "TOW"
                , "IDLE"
                , "HIGH RPM"
                , "ACCELERATION"
                , "BRAKING"
                , "CORNERING"
                , "LANE CHANGE"
                , "FATIGUE DRIVING"
                , "POWER CUT"
                , "POWER RESTORED"
                , "JAMMING"
                , "TEMPERATURE"
                , "PARKING"
                , "SHOCK"
                , "BONNET"
                , "FOOT BRAKE"
                , "FUEL LEAK"
                , "TAMPERING"
                , "REMOVING"
        );
    }

}
