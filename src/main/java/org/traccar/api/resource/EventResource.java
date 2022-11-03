package org.traccar.api.resource;

import org.traccar.Context;
import org.traccar.Main;
import org.traccar.api.BaseResource;
import org.traccar.api.resource.new_models.NewEvent;
import org.traccar.api.resource.new_models.Pageable;
import org.traccar.model.Event;
import org.traccar.model.Geofence;
import org.traccar.model.Maintenance;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

@Path("events")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)

public class EventResource extends BaseResource {


    @Path("{id}")
    @GET
    public Event get(@PathParam("id") long id) throws SQLException {
        Event event = Context.getDataManager().getObject(Event.class, id);
        Context.getPermissionsManager().checkDevice(getUserId(), event.getDeviceId());
        if (event.getGeofenceId() != 0) {
            Context.getPermissionsManager().checkPermission(Geofence.class, getUserId(), event.getGeofenceId());
        }
        if (event.getMaintenanceId() != 0) {
            Context.getPermissionsManager().checkPermission(Maintenance.class, getUserId(), event.getMaintenanceId());
        }
        return event;
    }


    @Path("t")
    @GET
   public Map<String, Long> getEventsCountByType() throws SQLException {
        return Context.getDataManager().getEventsCountByType();
    }

    @Path("all")
    @GET
    public Pageable getAll(@QueryParam("page") int page, @QueryParam("size") int limit) throws SQLException {
        Long eventsCount = Context.getDataManager().getEventsCount();
        if (page < 1) page = 1;
        if (limit < 1 || limit > 50) limit = 50;
        long limit2 = eventsCount - ((page - 1) * limit);
        long limit1 = eventsCount - (page * limit) + 1;
        long pages = eventsCount / limit;
        if (eventsCount % limit > 0) pages++;
        if (limit1 < 1) limit1 = 1;
        if (limit2 < 1) limit2 = 1;
        Collection<NewEvent> events = Context.getDataManager().getEvents(limit1, limit2);
        for (NewEvent ev : events) {
            String type = ev.getType();
            type = type.replace("device", "");
            if (type.equals("Unknown")) type = "Idle";
            ev.setType(type);
        }
        Pageable pageable = new Pageable(page, pages, events);
        return pageable;
    }

    @Path("all2")
    @GET
    public Collection<NewEvent> getAll2() throws SQLException, ParseException {
        Collection<Long> ids = new ArrayList<>();
        ids.add(1l);
        ids.add(2l);
        String sDate1 = "31/12/2021";
        Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
        Collection<NewEvent> events = Context.getDataManager().getEvents2(ids, date1, new Date(), null);
        return events;
    }
}
