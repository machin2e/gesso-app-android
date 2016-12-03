package camp.computer.clay.engine.manager;

import java.util.HashMap;

import camp.computer.clay.engine.entity.Entity;

public class EntityManager {

    public static long INVALID_UUID = -1L;
    public static long count = 0L;
    public long uuid = 0L;

    // NOTE: This should be the only language reference to each Entity object.
    private HashMap<Long, Entity> entities;

    public EntityManager() {
        setup();
    }

    private void setup() {
        entities = new HashMap<>();
    }

    synchronized public long add(Entity entity) {
        entities.put(entity.uuid, entity);
        return entity.uuid;
    }

    synchronized public Group<Entity> get() {
        Group<Entity> entityGroup = new Group<>();
        entityGroup.addAll(entities.values());
        return entityGroup;
    }

    synchronized public Entity get(long uuid) {
        return entities.get(uuid);
    }

    // TODO: Return true or false depending on success or failure of removal
    synchronized public void remove(Entity entity) {
        // TODO: 11/18/2016 Queue the removal operation and perform it at after the current render update completes.
        entities.remove(entity.uuid);
    }
}
