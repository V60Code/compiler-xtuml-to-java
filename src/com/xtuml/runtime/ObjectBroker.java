package com.xtuml.runtime;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Singleton service that acts as an in-memory database for all active model
 * instances.
 * Usage: The Compiler will generate code that registers new instances here upon
 * creation.
 */
public class ObjectBroker {

    private static ObjectBroker instance;

    // Outer map key: The class of the model (e.g., Dog.class)
    // Inner map key: The UUID of the instance
    private final Map<Class<?>, Map<UUID, BaseModel>> instanceExtent;

    // Global map for O(1) lookup by UUID without knowing the type
    private final Map<UUID, BaseModel> globalExtent;

    private ObjectBroker() {
        // Using ConcurrentHashMap for thread safety if the runtime becomes
        // multi-threaded
        this.instanceExtent = new ConcurrentHashMap<>();
        this.globalExtent = new ConcurrentHashMap<>();
    }

    /**
     * Gets the singleton instance of ObjectBroker.
     * 
     * @return the ObjectBroker instance.
     */
    public static synchronized ObjectBroker getInstance() {
        if (instance == null) {
            instance = new ObjectBroker();
        }
        return instance;
    }

    /**
     * Adds a model instance to the broker.
     * 
     * @param modelInstance The instance to add.
     */
    public void add(BaseModel modelInstance) {
        if (modelInstance == null)
            return;

        instanceExtent.computeIfAbsent(modelInstance.getClass(), k -> new ConcurrentHashMap<>())
                .put(modelInstance.getInstanceId(), modelInstance);
        globalExtent.put(modelInstance.getInstanceId(), modelInstance);
    }

    /**
     * Removes a model instance from the broker.
     * 
     * @param modelInstance The instance to remove.
     */
    public void remove(BaseModel modelInstance) {
        if (modelInstance == null)
            return;

        Map<UUID, BaseModel> extent = instanceExtent.get(modelInstance.getClass());
        if (extent != null) {
            extent.remove(modelInstance.getInstanceId());
        }
        globalExtent.remove(modelInstance.getInstanceId());
    }

    /**
     * Selects any instance by its ID, regardless of type.
     * 
     * @param id The UUID of the instance.
     * @return The instance if found, or null.
     */
    public BaseModel selectAny(UUID id) {
        return globalExtent.get(id);
    }

    /**
     * Selects a specific instance by its ID.
     * 
     * @param type The class type of the instance.
     * @param id   The UUID of the instance.
     * @param <T>  The type parameter.
     * @return The instance if found, or null.
     */
    @SuppressWarnings("unchecked")
    public <T> T selectById(Class<T> type, UUID id) {
        Map<UUID, BaseModel> extent = instanceExtent.get(type);
        if (extent != null) {
            return (T) extent.get(id);
        }
        return null;
    }

    /**
     * Selects all instances of a specific type.
     * 
     * @param type The class type to select.
     * @param <T>  The type parameter.
     * @return A list of all instances of the given type.
     */
    @SuppressWarnings("unchecked")
    public <T> List<T> selectAll(Class<T> type) {
        Map<UUID, BaseModel> extent = instanceExtent.get(type);
        if (extent != null) {
            return new ArrayList<>((Collection<T>) extent.values());
        }
        return new ArrayList<>();
    }
}
