package camp.computer.clay.model.simulation;

public abstract class Model {
<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/simulation/Model.java
=======
    
    private UUID uuid = UUID.randomUUID();

>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Model.java
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
