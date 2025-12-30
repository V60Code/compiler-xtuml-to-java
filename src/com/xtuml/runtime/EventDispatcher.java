package com.xtuml.runtime;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Singleton service to manage and dispatch events.
 */
public class EventDispatcher {

    private static EventDispatcher instance;
    private final Queue<XtUmlEvent> eventQueue;

    private EventDispatcher() {
        this.eventQueue = new ConcurrentLinkedQueue<>();
    }

    public static synchronized EventDispatcher getInstance() {
        if (instance == null) {
            instance = new EventDispatcher();
        }
        return instance;
    }

    /**
     * Adds an event to the queue.
     * @param e The event to enqueue.
     */
    public void enqueue(XtUmlEvent e) {
        if (e != null) {
            eventQueue.add(e);
        }
    }

    /**
     * Processes all events currently in the queue.
     * [CRITERIA 6] Handle Event (Dispatch Loop)
     * Dequeues them one by one and calls processEvent on the target instance.
     */
    public void processQueue() {
        ObjectBroker broker = ObjectBroker.getInstance();
        while (!eventQueue.isEmpty()) {
            XtUmlEvent event = eventQueue.poll();
            if (event != null) {
                BaseModel target = broker.selectAny(event.getTargetInstanceId());
                if (target != null) {
                    try {
                        target.processEvent(event);
                    } catch (Exception ex) {
                        System.err.println("Error processing event " + event.getEventId() + " for instance " + event.getTargetInstanceId() + ": " + ex.getMessage());
                        ex.printStackTrace();
                    }
                } else {
                    System.err.println("Target instance not found for event: " + event.getEventId() + " TargetID: " + event.getTargetInstanceId());
                }
            }
        }
    }
}
