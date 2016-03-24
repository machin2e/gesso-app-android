package camp.computer.clay.system;

import java.util.ArrayList;
import java.util.UUID;

public class CacheManager {

    private Clay clay;

    private ArrayList<Action> actions = new ArrayList<Action>();

    private ArrayList<Script> scripts = new ArrayList<Script>();

    CacheManager(Clay clay) {
        this.clay = clay;
    }

    public Clay getClay () {
        return this.clay;
    }

    /**
     * Caches the specified action.
     * @param action The action to cache.
     */
    public void cache (Action action) {
        this.actions.add(action);
    }

    public void cache (Script script) {
        this.scripts.add(script);
    }

    public boolean hasBehavior (String tag) {
        for (Action cachedAction : this.actions) {
            if (cachedAction.getTag().equals(tag)) {
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
        for (Action cachedAction : this.actions) {
            if (cachedAction.getUuid().equals(behaviorUuid)) {
                return true;
            }
        }
        return false;
    }

    public Action getBehavior (UUID behaviorUuid) {
        for (Action cachedAction : this.actions) {
            if (cachedAction.getUuid().equals(behaviorUuid)) {
                return cachedAction;
            }
        }
        return null;
    }

    public ArrayList<Action> getActions() {
        return this.actions;
    }

    public boolean hasBehaviorScript (String tag) {
        for (Script cachedScript : this.scripts) {
            if (cachedScript.getTag().equals(tag)) {
                return true;
            }
        }
        return false;
    }

    public Script getBehaviorScript (UUID behaviorScriptUuid) {
        for (Script cachedScript : this.scripts) {
            if (cachedScript.getUuid().equals(behaviorScriptUuid)) {
                return cachedScript;
            }
        }
        return null;
    }

    public ArrayList<Script> getScripts() {
        return this.scripts;
    }
}
