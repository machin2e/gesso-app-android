package camp.computer.clay.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Process {

    // TODO: 11/8/2016 Consider renaming to Process, Program, Behavior, or Calendar

    private UUID uuid;

    private List<UUID> actionUuids;

    public Process() {
        uuid = UUID.randomUUID();
        actionUuids = new ArrayList<>();
    }
}
