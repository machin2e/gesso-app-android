package camp.computer.clay.engine.component;

import camp.computer.clay.engine.entity.Entity;

public abstract class Component {

    private Entity entity = null;

    public enum State {
        VIEW,
        EDIT
    }

    public State state = State.VIEW;

    public Component() {
        this.entity = null;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public static State getState(Entity path) {
        return path.getComponent(Path.class).state;
    }

    public static void setState(Entity path, State state) {
        path.getComponent(Path.class).state = state;
    }

    // TODO: Phone
    // TODO: Workspace?
    // TODO: Widget
    // TODO: Player
    //
    // TODO: PortableComponent
    // TODO: TouchableComponent (enables touch interaction)
    // TODO: PositionComponent/TransformComponent
    // TODO: DrawableComponent/GraphicComponent
    // TODO: HostCommunicationComponent
    // TODO: PhysicsComponent
    // TODO: InputComponent/ControllerComponent
    // TODO: StateComponent
}
