package camp.computer.clay.model;

import java.util.UUID;

import camp.computer.clay.util.image.Image;

public abstract class Entity {

    // TODO: Add support for Components (as in the ECS architecture)

    protected UUID uuid = null;

    protected Entity parent;

    public Entity() {
        this.uuid = UUID.randomUUID();
    }

    public Entity(UUID uuid) {
        this.uuid = uuid;
    }

    public UUID getUuid() {
        return this.uuid;
    }

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
}
