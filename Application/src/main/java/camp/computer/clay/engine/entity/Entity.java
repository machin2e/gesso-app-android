package camp.computer.clay.engine.entity;

import java.util.UUID;

import camp.computer.clay.engine.Groupable;
import camp.computer.clay.engine.component.Component;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.Image;

public abstract class Entity extends Groupable {

    // TODO: public static Group<Entity> Manager = new Group<>();

    // TODO: Add support for Components (as in the ECS architecture)

    protected Entity parent; // TODO: Delete!

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
        components = new Group<>();
    }

    // TODO: DELETE
    public void setParent(Entity parent) {
        this.parent = parent;
    }

    public Entity getParent() {
        return this.parent;
    }


    // <TAG_COMPONENT>
    protected String label = "";

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
    // </TAG_COMPONENT>


    // <COMPONENT_PLACEHOLDERS>
    protected Transform transform = new Transform(0, 0); // TODO: Eventually, put this in the list of components.
    protected Image image = null; // TODO: Eventually, put this in the list of components.
    // </COMPONENT_PLACEHOLDERS>


    // <TEMPORARY_COMPONENT_INTERFACE>
    public <C extends Component> void setComponent(C component) {
        if (component instanceof Transform) {
            this.transform = (Transform) component;
        } else if (component instanceof Image) {
            this.image = (Image) component;
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
