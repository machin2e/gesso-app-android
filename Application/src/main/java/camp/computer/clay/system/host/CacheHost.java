package camp.computer.clay.system.host;

import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

import camp.computer.clay.system.Clay;
import camp.computer.clay.system.old_model.Action;
import camp.computer.clay.system.old_model.Script;

public class CacheHost {

    private Clay clay;

    private ArrayList<Action> actions = new ArrayList<Action>();

    private ArrayList<Script> scripts = new ArrayList<Script>();

    public CacheHost(Clay clay) {
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

    public boolean hasAction(String tag) {
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
     * @param uuid The UUID of the behavior to search for in the cache.
     * @return True if a behavior with the specified UUID was found. Otherwise, false.
     */
    public boolean hasAction(UUID uuid) {
        for (Action cachedAction : this.actions) {
            if (cachedAction.getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public Action getAction(UUID uuid) {
        for (Action cachedAction : this.actions) {
            if (cachedAction.getUuid().equals(uuid)) {
                return cachedAction;
            }
        }
        return null;
    }

    public ArrayList<Action> getActions() {
        return this.actions;
    }

    public boolean hasScript(String tag) {
        for (Script cachedScript : this.scripts) {
            if (cachedScript.getTag().equals(tag)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasScript(UUID uuid) {
        for (Script cachedScript : this.scripts) {
            if (cachedScript.getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public Script getScript(UUID uuid) {
        for (Script cachedScript : this.scripts) {
            if (cachedScript.getUuid().equals(uuid)) {
                return cachedScript;
            }
        }
        return null;
    }

    public ArrayList<Script> getScripts() {
        return this.scripts;
    }



    /**
     * Populates cache with all behavior scripts and actions from the available content managers.
     */
    public void populate () {
        Log.v("Content_Manager", "populateCache");


        // if (useClay().canStore()) {
        if (getClay().hasStore()) {
            Log.v("Content_Manager", "populateCache");

            // Restore behavior scripts and addPatch them to the cache
            getClay().getStore().restoreScripts();
            Log.v("Content_Manager", "Restored behavior scripts:");
            for (Script script : getClay().getCache().getScripts()) {
                Log.v("Content_Manager", "\t" + script.getUuid());
            }

            // Restore actions and addPatch them to the cache
            getClay().getStore().restoreActions();
            Log.v("Content_Manager", "Restored actions:");
            for (Action action : getClay().getCache().getActions()) {
                //Log.v("Content_Manager", "\t" + action.getUuid());
                printRestoredBehavior(action, 1);
            }
        }
    }

    private void printRestoredBehavior (Action action, int tabCount) {
        String tabString = "";
        for (int i = 0; i < tabCount; i++) {
            tabString += "\t";
        }
        Log.v ("Content_Manager", tabString + "Action (UUID: " + action.getUuid() + ")");
        if (!action.hasScript()) {
            for (Action childAction : action.getActions()) {
                printBehavior(childAction, tabCount + 1);
            }
        } else {
//            Log.v("Content_Manager", tabString + "\tScript (UUID: " + action.getScript().getUuid() + ")");
//            Log.v("Content_Manager", tabString + "\tState (UUID: " + action.getState().getUuid() + ")");
        }
    }

    private void printBehavior (Action action, int tabCount) {
        String tabString = "";
        for (int i = 0; i < tabCount; i++) {
            tabString += "\t";
        }
        Log.v ("Content_Manager", tabString + "Action (UUID: " + action.getUuid() + ")");
        if (!action.hasScript()) {
            for (Action childAction : action.getActions()) {
                printBehavior(childAction, tabCount + 1);
            }
        } else {
            Log.v("Content_Manager", tabString + "\tScript (UUID: " + action.getScript().getUuid() + ")");
//            Log.v("Content_Manager", tabString + "\tState (UUID: " + action.getState().getUuid() + ")");
        }
    }
}
