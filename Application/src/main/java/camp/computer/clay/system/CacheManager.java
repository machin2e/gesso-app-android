package camp.computer.clay.system;

import java.util.ArrayList;
import java.util.UUID;

public class CacheManager {

    private Clay clay;

    private ArrayList<Behavior> behaviors = new ArrayList<Behavior>();

    private ArrayList<BehaviorScript> behaviorScripts = new ArrayList<BehaviorScript>();

    CacheManager(Clay clay) {
        this.clay = clay;
    }

    public Clay getClay () {
        return this.clay;
    }

    /**
     * Caches the specified behavior.
     * @param behavior The behavior to cache.
     */
    public void cache (Behavior behavior) {
        this.behaviors.add(behavior);
    }

    public void cache (BehaviorScript behaviorScript) {
        this.behaviorScripts.add(behaviorScript);
    }

    public boolean hasBehavior (String tag) {
        for (Behavior cachedBehavior : this.behaviors) {
            if (cachedBehavior.getTag().equals(tag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Search the cache for the behavior with the specified UUID and return whether or not it was
     * found.
     * @param behaviorUuid The UUID of the behavior to search for in the cache.
     * @return True if a behavior with the specified UUID was found. Otherwise, false.
     */
    public boolean hasBehavior (UUID behaviorUuid) {
        for (Behavior cachedBehavior : this.behaviors) {
            if (cachedBehavior.getUuid().equals(behaviorUuid)) {
                return true;
            }
        }
        return false;
    }

    public Behavior getBehavior (UUID behaviorUuid) {
        for (Behavior cachedBehavior : this.behaviors) {
            if (cachedBehavior.getUuid().equals(behaviorUuid)) {
                return cachedBehavior;
            }
        }
        return null;
    }

    public ArrayList<Behavior> getBehaviors() {
        return this.behaviors;
    }

    public boolean hasBehaviorScript (String tag) {
        for (BehaviorScript cachedBehaviorScript : this.behaviorScripts) {
            if (cachedBehaviorScript.getTag().equals(tag)) {
                return true;
            }
        }
        return false;
    }

    public BehaviorScript getBehaviorScript (UUID behaviorScriptUuid) {
        for (BehaviorScript cachedBehaviorScript : this.behaviorScripts) {
            if (cachedBehaviorScript.getUuid().equals(behaviorScriptUuid)) {
                return cachedBehaviorScript;
            }
        }
        return null;
    }

    public ArrayList<BehaviorScript> getBehaviorScripts() {
        return this.behaviorScripts;
    }
}
