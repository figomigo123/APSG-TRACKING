/*
 * Copyright 2016 - 2020 Anton Tananaev (anton@traccar.org)
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
import org.traccar.api.BaseResource;
import org.traccar.api.resource.new_models.NewStatistics;
import org.traccar.model.Statistics;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.SQLException;
import java.util.*;

@Path("statistics")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StatisticsResource extends BaseResource {

    @GET
    public Collection<Statistics> get(
            @QueryParam("from") Date from, @QueryParam("to") Date to) throws SQLException {
        to = getDate(to);
        Context.getPermissionsManager().checkAdmin(getUserId());
        return Context.getDataManager().getStatistics(from, to);
    }

    @Path("view")
    @GET
    public Collection<NewStatistics> getView(
            @QueryParam("from") Date from, @QueryParam("to") Date to) throws SQLException {
        to = getDate(to);
        Context.getPermissionsManager().checkAdmin(getUserId());
        Collection<Statistics> statistics = Context.getDataManager().getStatistics(from, to);
        List<NewStatistics> newStatistics = new ArrayList<>();
        statistics.forEach(u -> {
            NewStatistics newStatistics1 = new NewStatistics(u);
            newStatistics.add(newStatistics1);
        });
        return newStatistics;
    }

    private Date getDate(@QueryParam("to") Date to) {
        Calendar c = Calendar.getInstance();
        c.setTime(to);
      //  c.add(Calendar.DATE, 1);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 0);
        to = c.getTime();
        return to;
    }
}
