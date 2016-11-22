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

//        Script script = new Script();
//        script.setCode("console.log('Hello');");
//
//        Action action = new Action();
//        action.setTitle("smooth();");
//        action.setScript(script);
//
//        Process process = new Process();
//        process.addAction(action);
//
//        // Add Script, Action, Process Data to Repository
//        addScript(script);
//        addAction(action);
//        addProcess(process);

//        createTestAction("set brightness", "var setBrightness = function(data) { print(\"setBrightness\"); return data; }");
//        createTestAction("set color", "var setColor = function(data) { print(\"setColor\"); return data; }");
//        createTestAction("set rotation direction", "var setRotationDirection = function(data) { print(\"setRotationDirection\"); return data; }");
//        createTestAction("set rotation speed", "var setRotationSpeed = function(data) { print(\"setRotationSpeed\"); return data; }");
//        createTestAction("send message", "var sendMessage = function(data) { print(\"sendMessage\"); return data; }");
//        createTestAction("get sensor data", "var getSensorData = function(data) { print(\"getSernsorData\"); return data; }");

//        createTestAction("set brightness", "var setBrightness = function(data) { print('setBrightness'); return data; }");
//        createTestAction("set color", "var setColor = function(data) { print('setColor'); return data; }");
//        createTestAction("set rotation direction", "var setRotationDirection = function(data) { print('setRotationDirection'); return data; }");
//        createTestAction("set rotation speed", "var setRotationSpeed = function(data) { print('setRotationSpeed'); return data; }");
//        createTestAction("send message", "var sendMessage = function(data) { print('sendMessage'); return data; }");
//        createTestAction("get sensor data", "var getSensorData = function(data) { print('getSernsorData'); return data; }");

        // Retrieve Actions and Scripts from Remote Server
//        createTestAction("get ports", "var action = function(data) { clay.getPorts(); return data; }");
//        createTestAction("set brightness", "var action = function(data) { print('setBrightness'); print(data); return data; }");
//        createTestAction("set color", "var action = function(data) { print('setColor');  print(data); return data; }");
//        createTestAction("set rotation direction", "var action = function(data) { print('setRotationDirection'); print(data); return data; }");
//        createTestAction("set rotation speed", "var action = function(data) { print('setRotationSpeed'); print(data); return data; }");
//        createTestAction("send message", "var action = function(data) { print('sendMessage'); print(data); return data; }");
//        createTestAction("get sensor data", "var action = function(data) { print('getSernsorData'); print(data); return data; }");

//        createTestAction("set brightness", "setBrightness(< slider >);");
//        createTestAction("set color", "setColor(<selector>);");
//        createTestAction("set rotation direction", "setRotationDirection();");
//        createTestAction("set rotation speed", "setRotationSpeed();");
//        createTestAction("send message", "sendMessage()");
//        createTestAction("get sensor data", "getSensorData(<sensor>)");
    }

    public void createTestAction(String title, String code) {

        Script script = new Script();
        script.setCode(code);
        addScript(script);

        Action action = new Action();
        action.setTitle(title);
        action.setScript(script);
        addAction(action);
    }

    public void addScript(Script script) {
        scripts.add(script);
    }

    public List<Script> getScripts() {
        return scripts;
    }

    public Script getScript(long uuid) {
        for (int i = 0; i < scripts.size(); i++) {
            if (Repository.scripts.get(i).getUuid() == uuid) {
                return Repository.scripts.get(i);
            }
        }
        return null;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void addAction(Action action) {
        actions.add(action);
    }

    public Action getAction(long uuid) {
        for (int i = 0; i < actions.size(); i++) {
            if (Repository.actions.get(i).getUuid() == uuid) {
                return Repository.actions.get(i);
            }
        }
        return null;
    }

    public void addProcess(Process process) {
        processes.add(process);
    }

    public List<Process> getProcesses() {
        return processes;
    }

    public Process getProcess(long uuid) {
        for (int i = 0; i < processes.size(); i++) {
            if (Repository.processes.get(i).getUuid() == uuid) {
                return Repository.processes.get(i);
            }
        }
        return null;
    }
}
