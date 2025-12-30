package com.xtuml.runtime;

import java.util.*;

/**
 * Singleton service for managing relationships (links) between model instances.
 * Supports simple links and associative links.
 */
public class RelationshipManager {

    private static RelationshipManager instance;

    // A container for a link
    private static class Link {
        UUID srcId;
        UUID targetId;
        String relationId; // e.g., "R1"
        UUID linkObjId;    // For associative relationships (optional)

        Link(UUID srcId, UUID targetId, String relationId, UUID linkObjId) {
            this.srcId = srcId;
            this.targetId = targetId;
            this.relationId = relationId;
            this.linkObjId = linkObjId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Link link = (Link) o;
            return Objects.equals(srcId, link.srcId) &&
                    Objects.equals(targetId, link.targetId) &&
                    Objects.equals(relationId, link.relationId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(srcId, targetId, relationId);
        }
    }

    private final Set<Link> links;

    private RelationshipManager() {
        this.links = Collections.synchronizedSet(new HashSet<>());
    }

    public static synchronized RelationshipManager getInstance() {
        if (instance == null) {
            instance = new RelationshipManager();
        }
        return instance;
    }

    /**
     * Creates a link between two instances.
     * [CRITERIA 2] Relation (Simple 1-1, 1-N)
     * [CRITERIA 3] Asosiasi (M-N with Link Object support via 'linkObj' param)
     * 
     * @param src            Source instance
     * @param target         Target instance
     * @param relationshipId The relationship identifier (e.g., "R1")
     * @param linkObj        Optional associative object instance (can be null)
     */
    public void relate(BaseModel src, BaseModel target, String relationshipId, BaseModel linkObj) {
        if (src == null || target == null)
            return;
        
        UUID linkId = (linkObj != null) ? linkObj.getInstanceId() : null;
        links.add(new Link(src.getInstanceId(), target.getInstanceId(), relationshipId, linkId));
        // System.out.println("DEBUG: Linked " + src.getClass().getSimpleName() + " to " + target.getClass().getSimpleName() + " across " + relationshipId);
    }

    /**
     * Removes a link between two instances.
     */
    public void unrelate(BaseModel src, BaseModel target, String relationshipId, BaseModel linkObj) {
        if (src == null || target == null)
            return;
        
        synchronized (links) {
            links.removeIf(link -> 
                link.relationId.equals(relationshipId) && 
                link.srcId.equals(src.getInstanceId()) && 
                link.targetId.equals(target.getInstanceId())
            );
        }
    }

    /**
     * Finds related instances with Bidirectional support.
     * 
     * @param src            The source instance we are navigating FROM.
     * @param relationshipId The relationship ID.
     * @param phrase         Ignored now (legacy support).
     * @return List of related BaseModel instances.
     */
    public List<BaseModel> getRelated(BaseModel src, String relationshipId, String phrase) {
        if (src == null)
            return Collections.emptyList();

        List<UUID> relatedIds = new ArrayList<>();
        UUID myId = src.getInstanceId();

        synchronized (links) {
            for (Link link : links) {
                if (!link.relationId.equals(relationshipId)) continue;

                // Forward: src -> target
                if (link.srcId.equals(myId)) {
                    relatedIds.add(link.targetId);
                } 
                // Backward: target -> src
                else if (link.targetId.equals(myId)) {
                    relatedIds.add(link.srcId);
                }
            }
        }

        List<BaseModel> results = new ArrayList<>();
        ObjectBroker broker = ObjectBroker.getInstance();

        for (UUID id : relatedIds) {
            BaseModel found = broker.selectAny(id);
            if (found != null) {
                results.add(found);
            }
        }

        return results;
    }
}
