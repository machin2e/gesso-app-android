package camp.computer.clay.engine.component;

import camp.computer.clay.engine.component.Component;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.model.Process;

public class Processor extends Component {

    // TODO: 11/8/2016 Consider renaming to something like ControllerComponent

    // <COMPONENT_DATA>
    public Process process;
    // </COMPONENT_DATA>



    // <CONSTRUCTOR>
    public Processor() {
    }
    // </CONSTRUCTOR>



    // <ABSTRACT_ENTITY_INTERFACE>
    public static void setProcess(Entity entity, Process process) {
        entity.getComponent(Processor.class).process = process;
    }

    public static Process getProcess(Entity entity) {
        return entity.getComponent(Processor.class).process;
    }
    // </ABSTRACT_ENTITY_INTERFACE>
}
