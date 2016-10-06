package camp.computer.clay.model;

public abstract class Entity {

    // TODO: UUID.
    // TODO: Tag.
    // TODO: Physical dimensions/geometry (of actual physical object). Add it as a label-like property.

    protected Entity parent;

    protected String label = "";

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

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean hasLabel() {
        return this.label != null && this.label.length() > 0;
    }

    public String getLabel() {
        return this.label;
    }
}
