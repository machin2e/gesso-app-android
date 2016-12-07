package camp.computer.clay.engine.manager;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

        // Update subscribers
        for (int i = 0; i < subscribers.size(); i++) {
            Log.v("SUBGROUP_ADD", "Adding to subscriber...");
            subscribers.get(i).add(entity);
        }

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

        // Update subscribers
        for (int i = 0; i < subscribers.size(); i++) {
            Log.v("SUBGROUP_ADD", "Adding to subscriber...");
            subscribers.get(i).remove(entity);
        }
    }


    private List<Group> subscribers = new ArrayList<>();

    // <TODO>
    public interface SubscriptionStrategy {
        void onAdd(Entity entity);
    }

    public <D> Group<Entity> subscribe(SubscriptionStrategy subscriptionStrategy) {
        return null;
    }

    public void EXAMPLE() {

        subscribe(new SubscriptionStrategy() {
            @Override
            public void onAdd(Entity entity) {
                // TODO: apply filters
                // TODO: apply sort algorithm
            }
        });

    }
    // </TODO>

    /**
     * Creates subscribe that is automatically updated using the specified {@code filter} when
     * elements are added or removed from the parent {@code Group}.
     *
     * @param filter
     * @return
     */
    public <D> Group<Entity> subscribe(Group.Filter filter, D... data) {
        Group<Entity> subgroup = new Group<>();
        subgroup.filter = filter;
        subgroup.data = data;

        List<Entity> elements = new ArrayList<>(entities.values());
        for (int i = 0; i < elements.size(); i++) {
            if (filter == null || filter.filter(elements.get(i), data) == true) {
                subgroup.add(elements.get(i));
            }
        }

        subscribers.add(subgroup);

        return subgroup;
    }

    // TODO: unsubscribe(...)

//    public <D> Group filter(Group.Filter filter, D data) {
//        Group<Entity> result = new Group<>();
//        entityManager.
//        for (int i = 0; i < entityManager.size(); i++) {
//            if (filter.filter(entityManager.get(i), data) == true) {
//                result.add(elements.get(i));
//            }
//        }
//        return result;
//    }
}
