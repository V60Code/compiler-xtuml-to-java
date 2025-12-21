package com.xtuml.runtime;

import java.util.UUID;

/**
 * Represents a signal or event in the xtUML system.
 * Used for inter-component messaging and state machine triggers.
 */
public class XtUmlEvent {

    private UUID targetInstanceId;
    private int eventId;
    private java.util.Map<String, Object> data;

    /**
     * Constructs a new event.
     * 
     * @param targetInstanceId The UUID of the target instance.
     * @param eventId          The integer ID of the event/signal.
     * @param data             Optional named data parameters carried by the event.
     */
    public XtUmlEvent(UUID targetInstanceId, int eventId, java.util.Map<String, Object> data) {
        this.targetInstanceId = targetInstanceId;
        this.eventId = eventId;
        this.data = data;
    }

    public UUID getTargetInstanceId() {
        return targetInstanceId;
    }

    public int getEventId() {
        return eventId;
    }

    public java.util.Map<String, Object> getData() {
        return data;
    }

    public Object getData(String key) {
        if (data == null)
            return null;
        return data.get(key);
    }
}
