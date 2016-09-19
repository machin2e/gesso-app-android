package camp.computer.clay.model.architecture;

public abstract class Feature { // was Feature
    // TODO: UUID.
    // TODO: Tag.
    // TODO: Physical dimensions (of actual physical object). Add it as a label-like property.

    private Feature parent;

    public Feature() {
    }

    // TODO: Get serialized feature (e.g., from redis database)
    // TODO: Alternavively, pass in a JavaScript method that simulates the feature. This would be
    // TODO: (cont'd) after loading it from redis.
    public Feature(String serializedConstruct) {
    }

    public void setParent(Feature parent) {
        this.parent = parent;
    }

    public boolean hasParent() {
        return (this.parent != null);
    }

    public Feature getParent() {
        return this.parent;
    }
}
