package com.xtuml.runtime;

import java.util.*;

/**
 * Singleton service for managing relationships (links) between model instances.
 * Checks for R_ID + Phrase matches.
 */
public class RelationshipManager {

    private static RelationshipManager instance;

    // A simple container for a link
    private static class Link {
        UUID srcId;
        UUID targetId;
        String relationId; // e.g., "R1"
        String phrase; // e.g., "owns"

        Link(UUID srcId, UUID targetId, String relationId, String phrase) {
            this.srcId = srcId;
            this.targetId = targetId;
            this.relationId = relationId;
            this.phrase = phrase == null ? "" : phrase;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o)
                return true;
            if (o == null || getClass() != o.getClass())
                return false;
            Link link = (Link) o;
            return Objects.equals(srcId, link.srcId) &&
                    Objects.equals(targetId, link.targetId) &&
                    Objects.equals(relationId, link.relationId) &&
                    Objects.equals(phrase, link.phrase);
        }

        @Override
        public int hashCode() {
            return Objects.hash(srcId, targetId, relationId, phrase);
        }
    }

    // Storage for links. Using a synchronized Set for simplicity.
    // In a high-perf scenario, a MultiMap or specific index would be better.
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
     * 
     * @param src            Source instance
     * @param target         Target instance
     * @param relationshipId The relationship identifier (e.g., "R1")
     * @param phrase         The phrase describing the link direction (optional)
     */
    public void relate(BaseModel src, BaseModel target, String relationshipId, String phrase) {
        if (src == null || target == null)
            return;
        links.add(new Link(src.getInstanceId(), target.getInstanceId(), relationshipId, phrase));
    }

    /**
     * Removes a link between two instances.
     * 
     * @param src            Source instance
     * @param target         Target instance
     * @param relationshipId The relationship identifier
     * @param phrase         The phrase describing the link direction
     */
    public void unrelate(BaseModel src, BaseModel target, String relationshipId, String phrase) {
        if (src == null || target == null)
            return;
        links.remove(new Link(src.getInstanceId(), target.getInstanceId(), relationshipId, phrase));
    }

    /**
     * Finds related instances.
     * This method scans the links. For bi-directional navigation, the compiler must
     * allow logic
     * to swap src/target or call this with the correct phrase.
     * 
     * @param src            The source instance we are navigating FROM.
     * @param relationshipId The relationship ID.
     * @param phrase         The phrase associated with the navigation direction.
     * @return List of related BaseModel instances.
     */
    public List<BaseModel> getRelated(BaseModel src, String relationshipId, String phrase) {
        if (src == null)
            return Collections.emptyList();

        String safePhrase = phrase == null ? "" : phrase;
        List<UUID> relatedIds = new ArrayList<>();

        // Synchronized block or copy for iteration safety
        synchronized (links) {
            for (Link link : links) {
                if (link.srcId.equals(src.getInstanceId()) &&
                        link.relationId.equals(relationshipId) &&
                        link.phrase.equals(safePhrase)) {
                    relatedIds.add(link.targetId);
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
