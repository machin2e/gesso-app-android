package camp.computer.clay.engine.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Model;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.TransformConstraint;
import camp.computer.clay.engine.component.util.FilterStrategy;
import camp.computer.clay.engine.component.util.SorterStrategy;
import camp.computer.clay.engine.entity.Entity;

public class EntityManager {

    public static long INVALID_UUID = -1L;
    public static long entityCounter = 0L;
    public long uuid = 0L;

    // NOTE: This should be the only language reference to each Entity object, aside from
    // NOTE: references in some components (e.g., Structure, TransformConstraint).
    private HashMap<Long, Entity> entities;

    private List<Group> subscribers = new ArrayList<>();

    public EntityManager() {
        setup();
    }

    private void setup() {
        entities = new HashMap<>();
    }

    synchronized public long add(Entity entity) {

        // Assign UID to Entity and store it
        if (entity.uid == INVALID_UUID) {
            entity.uid = EntityManager.entityCounter++;
        }
        entities.put(entity.uid, entity);

        // Activate the Entity
        entities.get(entity.uid).isActive = true;

        // Update subscriber Entity groups
        for (int i = 0; i < subscribers.size(); i++) {
            subscribers.get(i).add(entity);
        }

        return entity.uid;
    }

    /**
     * Flags the specified {@code Entity} for removal after completion of the current cycle.
     *
     * @param entity
     */
    synchronized public void remove(Entity entity) {
        // TODO: Return true or false depending on success or failure of removal
        entity.isActive = false;
        entity.isDestroyable = true;
    }

    synchronized public Group<Entity> get() {
        Group<Entity> entityGroup = new Group<>();
        List<Entity> hashEntities = new ArrayList<>(entities.values());
        for (int i = 0; i < hashEntities.size(); i++) {
            if (hashEntities.get(i).isActive && !hashEntities.get(i).isDestroyable) {
                entityGroup.add(hashEntities.get(i));
            }
        }
        return entityGroup;
    }

    /**
     * Returns the {@code Entity} with the specified {@code uid}. Returns {@code null} if there no
     * {@code Entity} with the {@code uid}.
     *
     * @param uid
     * @return
     */
    synchronized public Entity get(long uid) {
        return entities.get(uid);
    }

    public Group<Entity> subscribe(FilterStrategy filterStrategy, SorterStrategy sorterStrategy) {
        Group<Entity> subgroup = new Group<>();

        if (filterStrategy != null) {
            subgroup.filter = filterStrategy.getFilter();
            subgroup.data = filterStrategy.getData();
        }

        if (sorterStrategy != null) {
            subgroup.sorter = sorterStrategy.getSorter();
        }

        List<Entity> elements = new ArrayList<>(entities.values());
        for (int i = 0; i < elements.size(); i++) {
            subgroup.add(elements.get(i));
        }

        subscribers.add(subgroup);

        return subgroup;
    }


    /**
     * Creates subscribe that is automatically updated using the specified {@code filter} when
     * elements are added or removed from the parent {@code Group}.
     *
     * @param filter
     * @return
     */
//    public <D> Group<Entity> subscribe(Filter filter, D... data) {
//        Group<Entity> group = new Group<>();
//        group.filter = filter;
//        group.data = data;
//
//        List<Entity> elements = new ArrayList<>(entities.values());
//        for (int i = 0; i < elements.size(); i++) {
//            if (filter == null || filter.filter(elements.get(i), data) == true) {
//                group.add(elements.get(i));
//            }
//        }
//
//        subscribers.add(group);
//
//        return group;
//    }
    public boolean unsubscribe(Group group) {
        return subscribers.remove(group);
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
                entities.remove(destroyableEntity.uid);

                // Remove the Entity from the subscribers
                for (int j = 0; j < subscribers.size(); j++) {
                    subscribers.get(j).remove(destroyableEntity);
                }
            }
        }
    }
    // </REFACTOR>
}
