package org.traccar.api.resource;

import org.traccar.Context;
import org.traccar.api.ExtendedObjectResource;
import org.traccar.api.resource.new_models.NewAttribute;
import org.traccar.database.BaseObjectManager;
import org.traccar.model.Attribute;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Path("attributes")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class Attribute2Resource extends ExtendedObjectResource<Attribute> {

    public Attribute2Resource() {
        super(Attribute.class);
    }


    @Path("view")
    @GET
    public List<NewAttribute> getView(@QueryParam("all") boolean all, @QueryParam("userId") long userId) throws SQLException {
        BaseObjectManager<Attribute> manager = Context.getManager(getBaseClass());
        Collection<Attribute> items = manager.getItems(getSimpleManagerItems(manager, all, userId));
        List<NewAttribute> newAttributes = new ArrayList<>();
        items.forEach(u -> {
            NewAttribute newAttribute = new NewAttribute(u);
            newAttributes.add(newAttribute);
        });
        return newAttributes;
    }

}