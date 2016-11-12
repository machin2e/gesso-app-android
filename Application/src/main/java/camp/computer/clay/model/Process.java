package camp.computer.clay.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import camp.computer.clay.engine.Groupable;

public class Process extends Groupable {

    // TODO: 11/8/2016 Consider renaming to Process, Program, Behavior, or Calendar

    private List<Long> actionUuids;

    public Process() {
        actionUuids = new ArrayList<>();

        // <TEMPORARY>
//        Repository.processes.add(this);
        // </TEMPORARY>
    }

    public void addAction(Action action) {
        actionUuids.add(action.getUuid());
    }

    public Action getAction(int index) {
        long actionUuid = actionUuids.get(index);
        return Repository.actions.get(actionUuid);
    }

    public void removeAction(int index) {
        actionUuids.remove(index);
    }
}
