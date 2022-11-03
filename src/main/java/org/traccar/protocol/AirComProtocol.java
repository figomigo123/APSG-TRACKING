package org.traccar.protocol;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.traccar.BaseProtocol;
import org.traccar.PipelineBuilder;
import org.traccar.TrackerServer;
import org.traccar.model.Command;

public class AirComProtocol extends BaseProtocol {

    public AirComProtocol() {
        setSupportedDataCommands(
                Command.TYPE_ENGINE_STOP,
                Command.TYPE_ENGINE_RESUME,
                Command.TYPE_GET_VERSION,
                Command.TYPE_FACTORY_RESET,
                Command.TYPE_SET_SPEED_LIMIT,
                Command.TYPE_SET_ODOMETER,
                Command.TYPE_POSITION_SINGLE);

        addServer(new TrackerServer(false, getName()) {
            @Override
            protected void addProtocolHandlers(PipelineBuilder pipeline) {
                pipeline.addLast(new LengthFieldBasedFrameDecoder(512, 3, 2));
                pipeline.addLast(new AirComProtocolEncoder(AirComProtocol.this));
                pipeline.addLast(new AirComProtocolDecoder(AirComProtocol.this));
            }
        });
    }

}
