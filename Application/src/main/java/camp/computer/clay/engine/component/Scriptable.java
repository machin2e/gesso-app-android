package camp.computer.clay.engine.component;

import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.structure.Process;

public class Scriptable extends Component {

    // TODO: 11/8/2016 Consider renaming to something like ControllerComponent

    // <COMPONENT_DATA>
    public Process process;
    // </COMPONENT_DATA>


    // <CONSTRUCTOR>
    public Scriptable() {
        // <REFACTOR>
        // TODO: Should a process be created by default? How to tell if a process should be stored in the database?
        process = new Process();
        // </REFACTOR>
    }
    // </CONSTRUCTOR>


    // <ABSTRACT_ENTITY_INTERFACE>
    public static void setProcess(Entity entity, Process process) {
        entity.getComponent(Scriptable.class).process = process;
    }

    public static Process getProcess(Entity entity) {
        return entity.getComponent(Scriptable.class).process;
    }
    // </ABSTRACT_ENTITY_INTERFACE>
}
