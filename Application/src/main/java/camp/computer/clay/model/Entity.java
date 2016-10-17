package camp.computer.clay.model;

import java.util.UUID;

import camp.computer.clay.engine.Addressable;
import camp.computer.clay.engine.components.Component;
import camp.computer.clay.util.image.ImageComponent;

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
    protected ImageComponent imageComponent = null;

    public ImageComponent getImage() {
        return this.imageComponent;
    }

    public void setImage(ImageComponent imageComponent) {
        this.imageComponent = imageComponent;
    }
    // </IMAGE_COMPONENT>

    // <COMPONENT_INTERFACE>
    public boolean addComponent(Component component) {
        return true;
    }
    // </COMPONENT_INTERFACE>
}
