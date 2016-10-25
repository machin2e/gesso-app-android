package camp.computer.clay.engine.entity;

import java.util.UUID;

import camp.computer.clay.engine.Groupable;
import camp.computer.clay.engine.component.ActionListenerComponent;
import camp.computer.clay.engine.component.Component;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.Image;

public abstract class Entity extends Groupable {

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

    protected Group<Component> components = null;

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



    public abstract void update();



    // <TAG_COMPONENT>
    protected String label = "";

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
    // </TAG_COMPONENT>


    // <TEMPORARY_COMPONENTS_REFERENCES>
    // TODO: Eventually, put this in the list of components.
    // TODO: i.e., Store these in the Entity.components Group.
    protected Transform transform = null;
    protected Image image = null;
    protected ActionListenerComponent actionListener = null;
    // </TEMPORARY_COMPONENTS_REFERENCES>


    // <TEMPORARY_COMPONENT_INTERFACE>
    public <C extends Component> void setComponent(C component) {
        if (component instanceof Transform) {
            this.transform = (Transform) component;
        } else if (component instanceof Image) {
            this.image = (Image) component;
        } else if (component instanceof ActionListenerComponent) {
            this.actionListener = (ActionListenerComponent) component;
        }
    }

    public <C extends Component> boolean hasComponent(Class<C> type) {
        return getComponent(type) != null;
    }

    public <C extends Component> C getComponent(Class<C> type) {
        if (type == Transform.class) {
            return type.cast(this.transform);
        } else if (type == Image.class) {
            return type.cast(this.image);
        } else if (type == ActionListenerComponent.class) {
            return type.cast(this.actionListener);
        } else {
            return null;
        }
    }
    // </TEMPORARY_COMPONENT_INTERFACE>


    // <GENERIC_COMPONENT_INTERFACE>
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

    public Component getComponent(UUID entityUuid, UUID componentUuid) {
        return null;
    }

    public boolean hasComponent(UUID uuid) {
        return components.contains(uuid);
    }
    // </GENERIC_COMPONENT_INTERFACE>



    // <TRANSFORM_COMPONENT>
//    public Transform getPosition() {
//        return transform;
//    }

//    public boolean hasTransform() {
//        return (this.transform != null);
//    }

//    public void setPosition(Transform transform) {
//        this.transform = transform;
//    }
    // </TRANSFORM_COMPONENT>


    // <IMAGE_COMPONENT>
//    public boolean hasImage() {
//        return hasComponent(Image.class);
//    }

//    public Image getImage() {
//        return getComponent(Image.class);
//    }

//    public void setImage(Image image) {
//        setComponent(image);
//    }
    // </IMAGE_COMPONENT>
}
