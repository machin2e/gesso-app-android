package camp.computer.clay.engine.manager;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Model;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.TransformConstraint;
import camp.computer.clay.engine.entity.Entity;

public class EntityManager {

    public static long INVALID_UUID = -1L;
    public static long count = 0L;
    public long uuid = 0L;

    // NOTE: This should be the only language reference to each Entity object.
    private HashMap<Long, Entity> entities;

    private List<Group> subscribers = new ArrayList<>();

    public EntityManager() {
        setup();
    }

    private void setup() {
        entities = new HashMap<>();
    }

    synchronized public long add(Entity entity) {
        entities.put(entity.uuid, entity);

        entities.get(entity.uuid).isActive = true;

        // Update subscribers
        for (int i = 0; i < subscribers.size(); i++) {
            Log.v("SUBGROUP_ADD", "Adding to subscriber...");
            subscribers.get(i).add(entity);
        }

        return entity.uuid;
    }

    synchronized public Group<Entity> get() {
        Group<Entity> entityGroup = new Group<>();
        List<Entity> hashEntities = new ArrayList<>(entities.values());
        for (int i = 0; i < hashEntities.size(); i++) {
            if (hashEntities.get(i).isActive && !hashEntities.get(i).isDestroyable) {
                entityGroup.add(hashEntities.get(i));
            }
        }
//        entityGroup.addAll(entities.values());
        return entityGroup;
    }

    synchronized public Entity get(long uuid) {
        return entities.get(uuid);
    }

    // TODO: Return true or false depending on success or failure of removal
    synchronized public void remove(Entity entity) {
        entity.isActive = false;
        entity.isDestroyable = true;
    }

    // <REFACTOR>
    public synchronized void destroyEntities() {
        Group<Entity> entities2 = new Group<>(entities.values());
        for (int i = 0; i < entities2.size(); i++) {

            if (entities2.get(i).isDestroyable) {

                Entity destroyableEntity = entities2.get(i);

                // TODO: 11/18/2016 Queue the removal operation and perform it at after the current render update completes.

                // Remove TransformConstraint components referencing the Entity being deleted
                for (int j = 0; j < entities2.size(); j++) {
                    if (entities2.get(j).hasComponent(TransformConstraint.class)) {
                        Entity referenceEntity = entities2.get(j).getComponent(TransformConstraint.class).getReferenceEntity();
                        if (referenceEntity == destroyableEntity) {
                            entities2.get(j).removeComponent(TransformConstraint.class);
                        }
                    }
                }

                // Destroy Portable component
                if (destroyableEntity.hasComponent(Host.class) || destroyableEntity.hasComponent(Extension.class)) {
                    Portable portable = destroyableEntity.getComponent(Portable.class);
                    for (int j = 0; j < portable.ports.size(); j++) {
                        Entity port = portable.ports.get(j);
                        port.isDestroyable = true;
                    }
                }

                // Destroy Model component
                if (destroyableEntity.hasComponent(Model.class)) {
                    Model model = destroyableEntity.getComponent(Model.class);
                    for (int j = 0; j < model.primitives.size(); j++) {
                        long primitiveUid = model.primitives.get(j);
                        Entity primitiveEntity = get(primitiveUid);
                        primitiveEntity.isDestroyable = true;
                    }
                }

                // Remove the Entity's components
                destroyableEntity.removeComponents();

                // Delete the Entity
                entities.remove(destroyableEntity.uuid);

                // Remove the Entity from the subscribers
                for (int j = 0; j < subscribers.size(); j++) {
                    subscribers.get(j).remove(destroyableEntity);
                }
            }
        }
    }
    // </REFACTOR>

    /*
    class SubscribeStrategy {
        private Group.Filter filter;
        private Object data;

        public <D> SubscribeStrategy(Group.Filter filter, D... data) {
            this.filter = filter;
            this.data = data;
        }
    }

    public Group<Entity> subscribe(SubscribeStrategy... subscribeStrategies) {
        return null;
    }
    */

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
}
