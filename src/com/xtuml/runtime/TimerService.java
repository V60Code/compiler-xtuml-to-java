package com.xtuml.runtime;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Singleton service for handling timed events.
 * Wraps a ScheduledExecutorService.
 */
public class TimerService {

    private static TimerService instance;
    private final ScheduledExecutorService scheduler;

    private TimerService() {
        // Core pool size 1 is usually sufficient for sequential model execution
        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public static synchronized TimerService getInstance() {
        if (instance == null) {
            instance = new TimerService();
        }
        return instance;
    }

    /**
     * Schedules an event to be fired after a specified delay.
     * Note: the actual 'firing' (dispatching) logic is not here;
     * this normally would push to an event queue.
     * Since the requirements asked specifically for `void startTimer(int delayMs,
     * XtUmlEvent eventToFire)`,
     * we will assume there is a mechanism to handle the event when the timer
     * expires.
     * For now, we print to stdout or we need an EventManager listener.
     * 
     * As per prompt "Wraps ScheduledExecutorService", "Methods: ... startTimer
     * ...".
     * 
     * @param delayMs     Delay in milliseconds.
     * @param eventToFire The event to trigger.
     */
    public void startTimer(int delayMs, XtUmlEvent eventToFire) {
        scheduler.schedule(() -> {
            // In a real implementation, this would push eventToFire to the main event
            // queue.
            // For Phase 1, we haven't defined the EventDispatcher/Manager yet.
            // We will just log it or leave a placeholder.
            // Push to Event Queue
            System.out.println("Timer fired! Event ID: " + eventToFire.getEventId() + " for Target: "
                    + eventToFire.getTargetInstanceId());
            com.xtuml.runtime.EventDispatcher.getInstance().enqueue(eventToFire);
        }, delayMs, TimeUnit.MILLISECONDS);
    }

    public void shutdown() {
        scheduler.shutdown();
    }
}
