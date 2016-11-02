package camp.computer.clay.engine.entity;

import java.util.UUID;

import camp.computer.clay.engine.Groupable;
import camp.computer.clay.engine.component.Boundary;
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
import camp.computer.clay.engine.component.Visibility;

public final class Entity extends Groupable {

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


    // TODO: <DELETE>
    private Entity parent;

    public void setParent(Entity parent) {
        this.parent = parent;
    }

    public Entity getParent() {
        return this.parent;
    }
    // TODO: </DELETE>



    // <COMPONENTS>
    // TODO: Eventually, put this in the list of components.
    // TODO: i.e., Store these in the Entity.components Group.
//    private Transform transform = null;
//    private Image image = null;
//    private Portable portable = null;
//    private Extension extension = null;
//    private Host host = null;
//    private Port port = null; // Only used by Ports (DUH)
//    private Path path = null;
//    private Camera camera = null;
//    private Label label = null;
//    private Visibility visibility = null;
//    private Boundary boundary = null;
    // </COMPONENTS>

    public <C extends Component> void addComponent(C component) {

        // Associate with Entity
        component.setEntity(this);

        // Add to Entity
        this.components.add(component);
//        if (component instanceof Transform) {
//            this.transform = (Transform) component;
//        } else if (component instanceof Image) {
//            this.image = (Image) component;
//        } else if (component instanceof Portable) {
//            this.portable = (Portable) component;
//        } else if (component instanceof Extension) {
//            this.extension = (Extension) component;
//        } else if (component instanceof Host) {
//            this.host = (Host) component;
//        } else if (component instanceof Port) {
//            this.port = (Port) component;
//        } else if (component instanceof Path) {
//            this.path = (Path) component;
//        } else if (component instanceof Camera) {
//            this.camera = (Camera) component;
//        } else if (component instanceof Label) {
//            this.label = (Label) component;
//        } else if (component instanceof Visibility) {
//            this.visibility = (Visibility) component;
//        } else if (component instanceof Boundary) {
//            this.boundary = (Boundary) component;
//        }
    }

    public <C extends Component> C getComponent(Class<C> type) {
//        if (type == Transform.class) {
//            return type.cast(this.transform);
//        } else if (type == Image.class) {
//            return type.cast(this.image);
//        } else if (type == Portable.class) {
//            return type.cast(this.portable);
//        } else if (type == Extension.class) {
//            return type.cast(this.extension);
//        } else if (type == Host.class) {
//            return type.cast(this.host);
//        } else if (type == Port.class) {
//            return type.cast(this.port);
//        } else if (type == Path.class) {
//            return type.cast(this.path);
//        } else if (type == Camera.class) {
//            return type.cast(this.camera);
//        } else if (type == Label.class) {
//            return type.cast(this.label);
//        } else if (type == Visibility.class) {
//            return type.cast(this.visibility);
//        } else if (type == Boundary.class) {
//            return type.cast(this.boundary);
//        } else {
//            return null;
//        }

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

    public <C extends Component> C removeComponent(Class<C> type) {
        C component = getComponent(type);
        if (component != null) {
            components.remove(component); // TODO: FIX THIS BUG! This doesn't actually remove the component in place. It returns a new Group without the element!
        }
        return component;
    }



    /*
    // <ENTITY_MANAGEMENT>
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
