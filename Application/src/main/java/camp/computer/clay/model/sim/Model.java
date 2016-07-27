package camp.computer.clay.model.sim;

import java.util.UUID;

public abstract class Model {
    
    private UUID uuid = UUID.randomUUID();

    // TODO: UUID.
    // TODO: Tag.
    // TODO: Physical dimensions (of actual physical object). Add it as a tag-like property.

    private Model parent = null;

    public Model() {
    }

    public Model(UUID uuid) {
        this.uuid = uuid;
    }

    // TODO: Get serialized model (e.g., from redis database)
    // TODO: Alternavively, pass in a JavaScript method that simulates the model. This would be
    // TODO: (cont'd) after loading it from redis.
    public Model(String description) {
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public boolean hasParent() {
        return (this.parent != null);
    }

    public Model getParent() {
        return this.parent;
    }

    public void setParent(Model parent) {
        this.parent = parent;
    }
}
