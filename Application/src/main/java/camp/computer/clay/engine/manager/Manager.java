package camp.computer.clay.engine.manager;

import java.util.UUID;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.entity.Entity;

public class Manager {

    // NOTE: This should be the only language reference to each Entity.
    private Group<Entity> entities;

    public Manager() {
        setup();
    }

    private void setup() {
        entities = new Group<>();
    }

    public Group<Entity> getEntities() {
        return entities;
    }

    public UUID add(Entity entity) {
        if (entity != null && !entities.contains(entity)) {
            entities.add(entity);
            return entity.getUuid();
        }
        return null;
    }

    public Entity get(UUID uuid) {
        return entities.get(uuid);
    }

    // TODO: Return true or false depending on success or failure of removal
    public void remove(Entity entity) {
        entities.remove(entity);
    }
}
