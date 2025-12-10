package com.xtuml.runtime;

import java.util.UUID;

/**
 * Represents a signal or event in the xtUML system.
 * Used for inter-component messaging and state machine triggers.
 */
public class XtUmlEvent {

    private UUID targetInstanceId;
    private int eventId;
    private Object[] payload;

    /**
     * Constructs a new event.
     * @param targetInstanceId The UUID of the target instance.
     * @param eventId The integer ID of the event/signal.
     * @param payload Optional data parameters carried by the event.
     */
    public XtUmlEvent(UUID targetInstanceId, int eventId, Object[] payload) {
        this.targetInstanceId = targetInstanceId;
        this.eventId = eventId;
        this.payload = payload;
    }

    public UUID getTargetInstanceId() {
        return targetInstanceId;
    }

    public int getEventId() {
        return eventId;
    }

    public Object[] getPayload() {
        return payload;
    }
}
