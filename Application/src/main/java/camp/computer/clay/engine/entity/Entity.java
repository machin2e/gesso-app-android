package camp.computer.clay.engine.entity;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Component;
import camp.computer.clay.engine.manager.Manager;

public final class Entity {

    // <HACK>
    public long uuid = Manager.count++; // Manager.INVALID_UUID;
    // </HACK>

    public long getUuid() {
        return uuid;
    }

    public boolean isActive = true;

    // TODO?: Move into World. Allows World-specific Entities.
//    public static Group<Entity> Manager = new Group<>();

    private Group<Component> components = null;

    public Entity() {
        super();
        setup();
    }

    private void setup() {
        components = new Group<>(); // Create list of Components
    }

    public <C extends Component> void addComponent(C component) {
        component.setEntity(this); // Associate Component with Entity
        components.add(component); // Add to Entity
    }

    public <C extends Component> C getComponent(Class<C> type) {
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i).getClass() == type) {
                return type.cast(components.get(i));
            }
        }
        return null;
    }

    public boolean hasComponent(Class<? extends Component> type) {
        return getComponent(type) != null;
    }

    public boolean hasComponents(Class<? extends Component>... types) {
        for (int i = 0; i < types.length; i++) {
            if (!hasComponent(types[i])) {
                return false;
            }
        }
        return true;
    }

    public <C extends Component> C removeComponent(Class<C> type) {
        C component = getComponent(type);
        if (component != null) {
            components.remove(component);
        }
        return component;
    }


    // TODO: <DELETE>
    private Entity parent;

    public void setParent(Entity parent) {
        this.parent = parent;
    }

    public Entity getParent() {
        return this.parent;
    }
    // TODO: </DELETE>
}
