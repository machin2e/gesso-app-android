package camp.computer.clay.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import camp.computer.clay.engine.Group;

public class Repository {

    // TODO: 11/8/2016 Consider renaming to Process, Program, Behavior, or Calendar

    // <TEMPORARY>
    public static Group<Process> processes = new Group<>();
    public static Group<Action> actions = new Group<>();
    public static Group<Script> scripts = new Group<>();
    // </TEMPORARY>

    private UUID uuid;

//    // <DELETE>
//    private Group<Script> scriptUuids;
//    private Group<Action> actionUuids;
//    private Group<Process> processUuids;
//    // </DELETE>

    public Repository() {
        uuid = UUID.randomUUID();
//        scriptUuids = new Group<>();
//        actionUuids = new Group<>();
//        processUuids = new Group<>();
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
        action.setTitle("smooth();");
        action.setScript(script);

        Process process = new Process();
        process.addAction(action);

        // Add Script, Action, Process Data to Repository
        addScript(script);
        addAction(action);
        addProcess(process);
    }

    public void addScript(Script script) {
        scripts.add(script);
    }

    public Group<Script> getScripts() {
        return scripts;
    }

    public Script getScript(long uuid) {
        return scripts.get(uuid);
    }

    public Group<Action> getActions() {
        return actions;
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public Action getAction(long uuid) {
        return actions.get(uuid);
    }

    public void addProcess(Process process) {
        processes.add(process);
    }

    public Group<Process> getProcesses() {
        return processes;
    }

    public Process getProcess(long uuid) {
        return processes.get(uuid);
    }
}
