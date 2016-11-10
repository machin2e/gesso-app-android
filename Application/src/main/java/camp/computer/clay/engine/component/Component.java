package camp.computer.clay.engine.component;

import camp.computer.clay.engine.Groupable;
import camp.computer.clay.engine.entity.Entity;

public abstract class Component extends Groupable {

    public enum State {
        NONE,
        EDITING
    }

    private Entity entity = null;

    public Component() {
        this.entity = null;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public State state = State.NONE;

    public static State getState(Entity path) {
        return path.getComponent(Path.class).state;
    }

    public static void setState(Entity path, State state) {
        path.getComponent(Path.class).state = state;
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
