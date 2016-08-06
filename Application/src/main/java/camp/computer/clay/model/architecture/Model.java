package camp.computer.clay.model.architecture;

public abstract class Model {
    // TODO: UUID.
    // TODO: Tag.
    // TODO: Physical dimensions (of actual physical object). Add it as a tag-like property.

    private Model parent;

    public Model() {
    }

    // TODO: Get serialized model (e.g., from redis database)
    // TODO: Alternavively, pass in a JavaScript method that simulates the model. This would be
    // TODO: (cont'd) after loading it from redis.
    public Model(String serializedModel) {
    }

    public void setParent(Model parent) {
        this.parent = parent;
    }

    public boolean hasParent() {
        return (this.parent != null);
    }

    public Model getParent() {
        return this.parent;
    }
}
