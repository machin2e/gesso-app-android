package camp.computer.clay.engine.entity;

import java.util.UUID;

import camp.computer.clay.engine.Groupable;
import camp.computer.clay.engine.component.ActionListenerComponent;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Component;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Label;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.Image;

public final class Entity extends Groupable {

    // <ENTITY_MANAGEMENT>
    // TODO: public static Group<Entity> Manager = new Group<>();
    public static Group<Entity> Manager = new Group<>();

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
        Entity entity = getEntity(uuid);
        if (entity != null) {
            Entity.Manager.remove(entity);
        }
        return entity;
    }
    // </ENTITY_MANAGEMENT>

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

        // Add Entity to Manager
        Entity.addEntity(this);

        // Create list of Components
        components = new Group<>();
    }



    protected Entity parent; // TODO: Delete!

    // TODO: DELETE
    public void setParent(Entity parent) {
        this.parent = parent;
    }

    public Entity getParent() {
        return this.parent;
    }



    // <TEMPORARY_COMPONENTS_REFERENCES>
    // TODO: Eventually, put this in the list of components.
    // TODO: i.e., Store these in the Entity.components Group.
    protected Transform transform = null;
    protected Image image = null;
    protected ActionListenerComponent actionListener = null;
    protected Portable portable = null;
    protected Extension extension = null;
    protected Host host = null;
    protected Port port = null; // Only used by Ports (DUH)
    protected Path path = null;
    protected Camera camera = null;
    protected Label label = null;
    // </TEMPORARY_COMPONENTS_REFERENCES>


    // <TEMPORARY_COMPONENT_INTERFACE>
    public <C extends Component> void addComponent(C component) {

        // Add to Entity
        if (component instanceof Transform) {
            this.transform = (Transform) component;
        } else if (component instanceof Image) {
            this.image = (Image) component;
        } else if (component instanceof ActionListenerComponent) {
            this.actionListener = (ActionListenerComponent) component;
        } else if (component instanceof Portable) {
            this.portable = (Portable) component;
        } else if (component instanceof Extension) {
            this.extension = (Extension) component;
        } else if (component instanceof Host) {
            this.host = (Host) component;
        } else if (component instanceof Port) {
            this.port = (Port) component;
        } else if (component instanceof Path) {
            this.path = (Path) component;
        } else if (component instanceof Camera) {
            this.camera = (Camera) component;
        } else if (component instanceof Label) {
            this.label = (Label) component;
        }

        // Associate with Entity
        component.setEntity(this);
    }

    public boolean hasComponent(Class<? extends Component> type) {
        return getComponent(type) != null;
    }

    public <C extends Component> C getComponent(Class<C> type) {
        if (type == Transform.class) {
            return type.cast(this.transform);
        } else if (type == Image.class) {
            return type.cast(this.image);
        } else if (type == ActionListenerComponent.class) {
            return type.cast(this.actionListener);
        } else if (type == Portable.class) {
            return type.cast(this.portable);
        } else if (type == Extension.class) {
            return type.cast(this.extension);
        } else if (type == Host.class) {
            return type.cast(this.host);
        } else if (type == Port.class) {
            return type.cast(this.port);
        } else if (type == Path.class) {
            return type.cast(this.path);
        } else if (type == Camera.class) {
            return type.cast(this.camera);
        } else if (type == Label.class) {
            return type.cast(this.label);
        } else {
            return null;
        }
    }

    public <C extends Component> C removeComponent(Class<C> type) {
        C component = getComponent(type);
        if (component != null) {
            components.remove(component);
        }
        return component;
    }
    // </TEMPORARY_COMPONENT_INTERFACE>


    // <GENERIC_COMPONENT_INTERFACE>
    /*
    public boolean addComponent(Component component) {
        this.components.add(component);
        return true;
    }

    public Component removeComponent(UUID uuid) {
        return components.remove(uuid);
    }

    public Group<Component> getComponents() {
        return components;
    }

    public Component getComponent(UUID uuid) {
        return components.get(uuid);
    }
    */

    /*
    // TODO?
    public Component getComponent(UUID entityUuid, UUID componentUuid) {
        return null;
    }
    */

    public boolean hasComponent(UUID uuid) {
        return components.contains(uuid);
    }
    // </GENERIC_COMPONENT_INTERFACE>
}
