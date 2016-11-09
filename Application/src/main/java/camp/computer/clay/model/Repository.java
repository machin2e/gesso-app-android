package camp.computer.clay.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Repository {

    // TODO: 11/8/2016 Consider renaming to Process, Program, Behavior, or Calendar

    private UUID uuid;

    private List<UUID> processUuids;

    private List<UUID> actionUuids;

    private List<UUID> scriptUuids;

    public Repository() {
        uuid = UUID.randomUUID();
        processUuids = new ArrayList<>();
        actionUuids = new ArrayList<>();
        scriptUuids = new ArrayList<>();
    }

    private void populateTestData() {
        // TODO: 11/8/2016 Create Scripts
        // TODO: 11/8/2016 Create Actions
        // TODO: 11/8/2016 Create Processes
        // TODO: 11/8/2016 Add them to Repository 
        // TODO: 11/8/2016 Load from Repository to populate Extension Controller UI with Actions/Scripts
    }

    public void addAction(Action action) {
        if (!actionUuids.contains(action.getUuid())) {
            actionUuids.add(action.getUuid());
        }
    }

    public void addScript(Script script) {
        if (!scriptUuids.contains(script.getUuid())) {
            scriptUuids.add(script.getUuid());
        }
    }
}
