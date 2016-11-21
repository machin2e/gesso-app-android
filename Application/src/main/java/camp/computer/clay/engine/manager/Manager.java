package camp.computer.clay.engine.manager;

import java.util.HashMap;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.entity.Entity;

public class Manager {

    public static long count = 0L;
    public long uuid = 0L;
    public static long INVALID_UUID = -1L;

    // NOTE: This should be the only language reference to each Entity.
    private HashMap<Long, Entity> entities;

    public Manager() {
        setup();
    }

    private void setup() {
        entities = new HashMap<>();
    }

    synchronized public Group<Entity> getEntities() {
        Group<Entity> entityGroup = new Group<>();
        entityGroup.addAll(entities.values());
        return entityGroup;
    }

    synchronized public long add(Entity entity) {
//        entity.uuid = count++;
        entities.put(entity.uuid, entity);
        return entity.uuid;
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
