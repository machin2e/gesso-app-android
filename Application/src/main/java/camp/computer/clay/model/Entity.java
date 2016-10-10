package camp.computer.clay.model;

import java.util.UUID;

public abstract class Entity {

    // TODO: Add support for Components (as in the ECS architecture)

    protected UUID uuid = null;

    protected Entity parent;

    protected String label = "";

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

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
}
