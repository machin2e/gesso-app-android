package camp.computer.clay.model;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.entity.Actor;

/**
 * {@code Model} represents the build state of available and online Clay Hosts and Entities of the
 * discovered physical environment sensed or computed based on data collected from Clay hosts.
 */
public class Model extends Entity {

    private List<Actor> actors = new ArrayList<>();

    private Group<Entity> entities = new Group<>();

    public void addActor(Actor actor) {
        this.actors.add(actor);
    }

    public Actor getActor(int index) {
        if (index < this.actors.size()) {
            return this.actors.get(index);
        } else {
            return null;
        }
    }

    public List<Actor> getActors() {
        return this.actors;
    }

    public void addEntity(Entity entity) {
        this.entities.add(entity);
    }

    public Group<Entity> getEntities() {
        return this.entities;
    }
}
