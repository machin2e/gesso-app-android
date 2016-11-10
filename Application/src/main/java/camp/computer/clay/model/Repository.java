package camp.computer.clay.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Repository {

    // TODO: 11/8/2016 Consider renaming to Process, Program, Behavior, or Calendar

    // <TEMPORARY>
    public static List<Process> processes = new ArrayList<>();
    public static List<Action> actions = new ArrayList<>();
    public static List<Script> scripts = new ArrayList<>();
    // </TEMPORARY>

    private UUID uuid;

    private List<UUID> scriptUuids;

    private List<UUID> actionUuids;

    private List<UUID> processUuids;

    public Repository() {
        uuid = UUID.randomUUID();
        scriptUuids = new ArrayList<>();
        actionUuids = new ArrayList<>();
        processUuids = new ArrayList<>();
    }

    public void populateTestData() {
        // TODO: 11/8/2016 Create Scripts
        // TODO: 11/8/2016 Create Actions
        // TODO: 11/8/2016 Create Processes
        // TODO: 11/8/2016 Add them to Repository 
        // TODO: 11/8/2016 Load from Repository to populate Extension Controller UI with Actions/Scripts

        Script script = new Script();
        script.setCode("console.log('Hello');");

        Action action = new Action();
        action.setScript(script);

        Process process = new Process();
        process.addAction(action);

        // Add Script, Action, Process Data to Repository
        addScript(script);
        addAction(action);
        addProcess(process);
    }

    public void addScript(Script script) {
        if (!scriptUuids.contains(script.getUuid())) {
            scriptUuids.add(script.getUuid());
        }
    }

    // TODO: getScript(UUID uuid)

    public void addAction(Action action) {
        if (!actionUuids.contains(action.getUuid())) {
            actionUuids.add(action.getUuid());
        }
    }

    // TODO: getAction(UUID uuid)

    public void addProcess(Process process) {
        if (!processUuids.contains(process.getUuid())) {
            processUuids.add(process.getUuid());
        }
    }

    // TODO: getProcess(UUID uuid)
}
