package camp.computer.clay.engine.manager;

import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
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

    public long add(Entity entity) {
        if (entity != null && !entities.contains(entity)) {
            entities.add(entity);

            /*
            Collections.sort(entities, new Comparator<Entity>() {
                @Override
                public int compare(Entity entity1, Entity entity2) {
                    return entity1.getUuid().compareTo(entity2.getUuid());
                }
            });
            */

            return entity.getUuid();
        }
        return -1;
    }

    public Entity get(long uuid) {

//        Collections.binarySearch(entities, uuid, new Comparator<Entity>() {
//            @Override
//            public int compare(Entity entity1, Entity entity2) {
//                return entity1.getUuid().compareTo(entity2.getUuid());
//            }
//        });

        return entities.get(uuid);
    }

    // TODO: Return true or false depending on success or failure of removal
    public void remove(Entity entity) {
        entities.remove(entity);
    }

    /*
    // <ENTITY_MANAGEMENT>
    // TODO: Create non-static Manager in World?
    public static void addEntity(Entity entity) {
        Manager.add(entity);
    }

    public static boolean hasEntity(UUID uuid) {
        for (int i = 0; i < Entity.Manager.size(); i++) {
            if (Entity.Manager.get(i).getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public static Entity getEntity(UUID uuid) {
        for (int i = 0; i < Entity.Manager.size(); i++) {
            if (Entity.Manager.get(i).getUuid().equals(uuid)) {
                return Entity.Manager.get(i);
            }
        }
        return null;
    }

    public static Entity removeEntity(UUID uuid) {
        Entity entity = Manager.get(uuid);
        if (entity != null) {
            Entity.Manager.remove(entity);
        }
        return entity;
    }
    // </ENTITY_MANAGEMENT>
    */
}
