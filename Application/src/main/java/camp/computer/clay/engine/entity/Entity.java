package camp.computer.clay.engine.entity;

import java.util.UUID;

import camp.computer.clay.engine.Groupable;
import camp.computer.clay.engine.component.Component;
import camp.computer.clay.engine.Group;

public final class Entity extends Groupable {

    public boolean isActive = true;

    // TODO?: Move into World. Allows World-specific Entities.
    public static Group<Entity> Manager = new Group<>();

    private Group<Component> components = null;

    public Entity() {
        super();
        setup();
    }

    public Entity(UUID uuid) {
        super(uuid);
        setup();
    }

    private void setup() {
        components = new Group<>(); // Create list of Components
        Entity.Manager.add(this); // Add Entity to Manager
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
        for(int i = 0; i < types.length; i++) {
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



    /*
    // <ENTITY_MANAGEMENT>
    // TODO: Create non-static EntityManager in World?
    public static void addEntity(Entity entity) {
        Manager.add(entity);
    }

    public static boolean hasEntity(UUID uuid) {
        for (int i = 0; i < Entity.Manager.size(); i++) {
            if (Entity.Manager.get(i).getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public static Entity getEntity(UUID uuid) {
        for (int i = 0; i < Entity.Manager.size(); i++) {
            if (Entity.Manager.get(i).getUuid().equals(uuid)) {
                return Entity.Manager.get(i);
            }
        }
        return null;
    }

    public static Entity removeEntity(UUID uuid) {
        Entity entity = Manager.get(uuid);
        if (entity != null) {
            Entity.Manager.remove(entity);
        }
        return entity;
    }
    // </ENTITY_MANAGEMENT>
    */
}
