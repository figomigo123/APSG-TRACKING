/*
 * Copyright 2015 - 2019 Anton Tananaev (anton@traccar.org)
 * Copyright 2016 Gabor Somogyi (gabor.g.somogyi@gmail.com)
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
package org.traccar.api.resource;

import org.traccar.Context;
import org.traccar.api.ExtendedObjectResource;
import org.traccar.api.resource.new_models.NewCommand;
import org.traccar.database.CommandsManager;
import org.traccar.model.Command;
import org.traccar.model.Device;
import org.traccar.model.Typed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("commands")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CommandResource extends ExtendedObjectResource<Command> {

    public CommandResource() {
        super(Command.class);
    }

    @GET
    @Path("send")
    public Collection<Command> get(@QueryParam("deviceId") long deviceId) {
        Context.getPermissionsManager().checkDevice(getUserId(), deviceId);
        CommandsManager commandsManager = Context.getCommandsManager();
        Set<Long> result = new HashSet<>(commandsManager.getUserItems(getUserId()));
        result.retainAll(commandsManager.getSupportedCommands(deviceId));
        return commandsManager.getItems(result);
    }

    @GET
    @Path("view")
    public Collection<NewCommand> getView(@QueryParam("deviceId") long deviceId) {
        Context.getPermissionsManager().checkDevice(getUserId(), deviceId);
        CommandsManager commandsManager = Context.getCommandsManager();
        Set<Long> result = new HashSet<>(commandsManager.getUserItems(getUserId()));
        Collection<Command> items = commandsManager.getItems(result);
        List<NewCommand> newCommands = new ArrayList<>();
        items.forEach(u -> {
            NewCommand newCommand = new NewCommand(u);
            newCommands.add(newCommand);
        });
        return newCommands;
    }


    @POST
    @Path("send")
    public Response send(Command entity) throws Exception {
        Context.getPermissionsManager().checkReadonly(getUserId());
        long deviceId = entity.getDeviceId();
        long id = entity.getId();
        Context.getPermissionsManager().checkDevice(getUserId(), deviceId);
        if (id != 0) {
            Context.getPermissionsManager().checkPermission(Command.class, getUserId(), id);
            Context.getPermissionsManager().checkUserDeviceCommand(getUserId(), deviceId, id);
        } else {
            Context.getPermissionsManager().checkLimitCommands(getUserId());
        }
        if (!Context.getCommandsManager().sendCommand(entity)) {
            return Response.accepted(entity).build();
        }
        return Response.ok(entity).build();
    }

    @GET
    @Path("refreshall")
    public String refreshAll() throws Exception {
        Command command = new Command();
        command.setTextChannel(true);
        command.setType("custom");
        command.getAttributes().put("data","singleReport");

        DeviceResource deviceResource = new DeviceResource();
        Collection<Device> all = deviceResource.getAllDevices();
        for (Device device : all) {
            command.setDeviceId(device.getId());
            try{
            Context.getCommandsManager().sendCommand(command);}
            catch (Exception e){
              // if ( e.getMessage().contains("Bad credentials"))return "{\"type\":\"SMS GateWay Bad credentials\"}";
              // return e.getMessage();
            }
        }
        return  "{\"type\":\" Command is sending to " + all.size() + " Devices ...\"}";
    }

    @GET
    @Path("types")
    public Collection<Typed> get(
            @QueryParam("deviceId") long deviceId,
            @QueryParam("protocol") String protocol,
            @QueryParam("textChannel") boolean textChannel) {
        System.out.println("types get");
        if (deviceId != 0) {
            Context.getPermissionsManager().checkDevice(getUserId(), deviceId);
            return Context.getCommandsManager().getCommandTypes(deviceId, textChannel);
        } else if (protocol != null) {
            return Context.getCommandsManager().getCommandTypes(protocol, textChannel);
        } else {
            return Context.getCommandsManager().getAllCommandTypes();
        }
    }


}
