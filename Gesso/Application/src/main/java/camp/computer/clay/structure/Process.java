package camp.computer.clay.structure;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.manager.Handle;

public class Process extends Handle {

    // TODO: 11/8/2016 Consider renaming to Process, Program, Behavior, or Calendar

    private List<Long> actionUuids;

    public Process() {
        super();
        actionUuids = new ArrayList<>();

        // <TEMPORARY>
//        Repository.processes.add(this);
        // </TEMPORARY>
    }

    public void addAction(Action action) {
        actionUuids.add(action.getUid());

//        Log.v("ActionEditor", "---");
//        for (int i = 0; i < actionUuids.size(); i++) {
//            Log.v("ActionEditor", "" + i + ": " + getAction(i).getTitle());
//        }
//        Log.v("ActionEditor", "---");

        Log.v("ActionEditor", "added action (" + actionUuids.size() + "): " + action.getTitle());
    }

    public List<Action> getActions() {
        List<Action> actions = new ArrayList<>();
        for (int i = 0; i < actionUuids.size(); i++) {
            actions.add(getAction(i));
        }
        return actions;
    }

    public void clear() {
        actionUuids.clear();
    }

    public Action getAction(int index) {
        long actionUuid = actionUuids.get(index);
//        return Repository.actions.get(actionUuid);

//        for (int i = 0; i < Repository.actions.size(); i++) {
//            if (Repository.actions.get(i).getUid() == actionUuid) {
//                return Repository.actions.get(i);
//            }
//        }
        return null;
    }

    public void removeAction(int index) {
        actionUuids.remove(index);
    }
}
