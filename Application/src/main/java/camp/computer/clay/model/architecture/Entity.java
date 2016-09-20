package camp.computer.clay.model.architecture;

public abstract class Entity {
    // TODO: UUID.
    // TODO: Tag.
    // TODO: Physical dimensions (of actual physical object). Add it as a label-like property.

    private Entity parent;

    public Entity() {
    }

    // TODO: Get serialized entity (e.g., from redis database)
    // TODO: Alternavively, pass in a JavaScript method that simulates the entity. This would be
    // TODO: (cont'd) after loading it from redis.
    public Entity(String serializedProfile) {
    }

    public void setParent(Entity parent) {
        this.parent = parent;
    }

    public boolean hasParent() {
        return (this.parent != null);
    }

    public Entity getParent() {
        return this.parent;
    }
}
