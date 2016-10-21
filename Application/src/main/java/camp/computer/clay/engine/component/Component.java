package camp.computer.clay.engine.component;

import camp.computer.clay.engine.Groupable;

public abstract class Component extends Groupable {

    public enum Type {
        Transform, // i.e., Position
        Geometry // i.e., Image
    }

    // TODO: PortableComponent

    // TODO: TouchableComponent (enables touch interaction)
    // TODO: PositionComponent/TransformComponent
    // TODO: DrawableComponent/GraphicComponent
    // TODO: HostCommunicationComponent
    // TODO: PhysicsComponent
    // TODO: InputComponent/ControllerComponent
    // TODO: StateComponent
}
