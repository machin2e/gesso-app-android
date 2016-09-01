package camp.computer.clay.model.architecture;

public abstract class Construct {
    // TODO: UUID.
    // TODO: Tag.
    // TODO: Physical dimensions (of actual physical object). Add it as a tag-like property.

    private Construct parent;

    public Construct() {
    }

    // TODO: Get serialized construct (e.g., from redis database)
    // TODO: Alternavively, pass in a JavaScript method that simulates the construct. This would be
    // TODO: (cont'd) after loading it from redis.
    public Construct(String serializedConstruct) {
    }

    public void setParent(Construct parent) {
        this.parent = parent;
    }

    public boolean hasParent() {
        return (this.parent != null);
    }

    public Construct getParent() {
        return this.parent;
    }
}
