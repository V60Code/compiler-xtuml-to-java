package com.xtuml.runtime;

import java.util.Objects;
import java.util.UUID;

/**
 * Base class for all generated model classes in the xtUML to Java compiler.
 * This class handles unique identity and logical deletion.
 */
public abstract class BaseModel {

    /**
     * Unique identifier for this instance.
     */
    protected UUID instanceId;

    /**
     * Flag indicating if this instance has been logically deleted.
     */
    protected boolean isDeleted;

    /**
     * Default constructor. Generates a new random UUID.
     */
    public BaseModel() {
        this.instanceId = UUID.randomUUID();
        this.isDeleted = false;
    }

    /**
     * Gets the unique instance ID.
     * @return the UUID of this instance.
     */
    public UUID getInstanceId() {
        return instanceId;
    }

    /**
     * Checks if the instance is marked as deleted.
     * @return true if deleted, false otherwise.
     */
    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * Marks the instance as deleted.
     * The compiler generated code should call ObjectBroker.remove(this) 
     * in addition to calling this method if complete removal is desired.
     */
    public void delete() {
        this.isDeleted = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseModel baseModel = (BaseModel) o;
        return Objects.equals(instanceId, baseModel.instanceId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(instanceId);
    }

    /**
     * Processes an event targeted at this instance.
     * To be implemented by generated classes with State Machines.
     * @param e The event to process.
     */
    public void processEvent(XtUmlEvent e) {
        // Default implementation does nothing (ignoring event).
        // Generated classes will override this.
    }
}
