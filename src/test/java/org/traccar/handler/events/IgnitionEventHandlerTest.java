package org.traccar.handler.events;

import org.junit.Test;
import org.traccar.BaseTest;
import org.traccar.TestIdentityManager;
import org.traccar.model.Event;
import org.traccar.model.Position;

import java.util.Map;

import static org.junit.Assert.assertNull;

public class IgnitionEventHandlerTest extends BaseTest {

    @Test
    public void testIgnitionEventHandler() {

        IgnitionEventHandler ignitionEventHandler = new IgnitionEventHandler(new TestIdentityManager());

        Position position = new Position();
        position.set(Position.KEY_IGNITION, true);
        position.setValid(true);
        Map<Event, Position> events = ignitionEventHandler.analyzePosition(position);
        assertNull(events);
    }

}
