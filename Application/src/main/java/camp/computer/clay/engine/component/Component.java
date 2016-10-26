package camp.computer.clay.engine.component;

import camp.computer.clay.engine.Groupable;
import camp.computer.clay.engine.entity.Entity;

public abstract class Component extends Groupable {

    private Entity entity = null;

    public Component(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
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
