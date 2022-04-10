/*
 * Copyright 2016 - 2017 Anton Tananaev (anton@traccar.org)
 * Copyright 2016 - 2017 Andrey Kunitsyn (andrey@traccar.org)
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
import org.traccar.api.SimpleObjectResource;
import org.traccar.api.resource.new_models.NewBaseModel;
import org.traccar.database.BaseObjectManager;
import org.traccar.model.Calendar;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Path("calendars")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CalendarResource extends SimpleObjectResource<Calendar> {

    public CalendarResource() {
        super(Calendar.class);
    }

    @Path("view")
    @GET
    public List<NewBaseModel> getView(@QueryParam("all") boolean all, @QueryParam("userId") long userId) throws SQLException {
        BaseObjectManager<Calendar> manager = Context.getManager(getBaseClass());
        Collection<Calendar> items = manager.getItems(getSimpleManagerItems(manager, all, userId));
        List<NewBaseModel> newBaseModels = new ArrayList<>();
        items.forEach(u -> {
            NewBaseModel newUser = new NewBaseModel(u.getName(),u.getId());
            newBaseModels.add(newUser);
        });
        return newBaseModels;
    }


}
