package camp.computer.clay.engine;

import java.util.UUID;

import camp.computer.clay.model.Group;
import camp.computer.clay.util.image.Image;

public abstract class Entity extends Addressable {

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

    // <IMAGE_COMPONENT>
    protected Image image = null;

    public Image getImage() {
        return this.image;
    }

    public void setImage(Image image) {
        this.image = image;
    }
    // </IMAGE_COMPONENT>

    // <COMPONENT_INTERFACE>
    public boolean addComponent(Component component) {
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

    public boolean hasComponent(UUID uuid) {
        return components.contains(uuid);
    }
    // </COMPONENT_INTERFACE>
}
