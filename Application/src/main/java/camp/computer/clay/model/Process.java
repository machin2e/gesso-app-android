package camp.computer.clay.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import camp.computer.clay.engine.Group;

public class Process {

    // TODO: 11/8/2016 Consider renaming to Process, Program, Behavior, or Calendar

    private UUID uuid;

    private List<UUID> actionUuids;

    public Process() {
        uuid = UUID.randomUUID();
        actionUuids = new ArrayList<>();

        // <TEMPORARY>
        Repository.processes.add(this);
        // </TEMPORARY>
    }

    public UUID getUuid() {
        return uuid;
    }

    public void addAction(Action action) {
        actionUuids.add(action.getUuid());
    }

    public Action getAction(int index) {
        UUID actionUuid = actionUuids.get(index);
        return null; // TODO: Return the actual Action
    }

    public void removeAction(int index) {
        actionUuids.remove(index);
    }
}
