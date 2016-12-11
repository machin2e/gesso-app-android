package camp.computer.clay.engine.manager;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import camp.computer.clay.engine.component.Boundary;
import camp.computer.clay.engine.component.Component;
import camp.computer.clay.engine.component.Label;
import camp.computer.clay.engine.component.Model;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.Visibility;
import camp.computer.clay.engine.component.util.Visible;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.lib.Geometry.Rectangle;
import camp.computer.clay.lib.Geometry.Shape;

public class Group<E> implements List<E> {

    // TODO: Impelement a generic filter(...) interface so custom filters can be used. They should
    // TODO: (cont'd) be associated with a Entity type ID, so they only operate on the right entityManager.
    // TODO: (cont'd) Place custom filters in Entity classes (e.g., Entity.Filter.getPosition(...)).

    // <GROUP>
    private List<E> elements = new ArrayList<>();

    // Used by subscribe to filter elements that will be added to it.
    public Filter filter = null;
    public Object data = null;

    public Sorter sorter = null;

    public Group() {
    }

    public Group(Collection<? extends E> collection) {
        this.elements.addAll(collection);
    }

    public E get(long uuid) {
        Group<E> result = filterUuid(uuid);
        if (result.size() > 0) {
            return result.get(0);
        } else {
            return null;
        }
    }

    public E remove(long uuid) {
        E element = get(uuid);
        if (element != null) {
            remove(element);
        }
        return element;
    }

//    public Group<E> remove(E entity) {
//        int elementIndex = indexOf(entity);
//        remove(elementIndex);
//        return this;
//    }

//    private List<Group> subscribers = new ArrayList<>();
//
//    /**
//     * Creates subscribe that is automatically updated using the specified {@code filter} when
//     * elements are added or removed from the parent {@code Group}.
//     *
//     * @param filter
//     * @return
//     */
//    public <D> Group<E> subscribe(Filter filter, D... data) {
//        Group<E> subgroup = filter(filter, data);
//        subgroup.filter = filter;
//        subgroup.data = data;
//
//        subscribers.add(subgroup);
//        return subgroup;
//    }
//
//    // TODO: unsubscribe(...)
    // </GROUP>


    public interface Sorter<V, D> {
        int sort(V entity, D data);
    }


    // <GROUP_LOOKUP>
    public interface Filter<V, D> {
        boolean filter(V entity, D data);
    }

    public <D> Group filter(Filter filter, D data) {
        Group<E> result = new Group<>();
        for (int i = 0; i < elements.size(); i++) {
            if (filter.filter(elements.get(i), data) == true) {
                result.add(elements.get(i));
            }
        }
        return result;
    }

    public <D> Group<E> transform(Mapper mapper, D data) {
        // Group<V> result = new Group<>();
        for (int i = 0; i < elements.size(); i++) {
            mapper.map(elements.get(i), data);
        }
        // return result;
        return this;
    }

    // "Filters" namespace
    public static class Filters {

        public static Filter filterUuid = new Filter<Entity, Long>() {
            @Override
            public boolean filter(Entity entity, Long uuid) {
                if (entity.uuid == uuid) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        public static Filter filterVisibility = new Filter<Entity, Boolean>() {
            @Override
            public boolean filter(Entity entity, Boolean data) {
                Visibility visibility = entity.getComponent(Visibility.class);
                if (visibility != null && visibility.getVisibile() == Visible.VISIBLE) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        /**
         * Returns {@code true} if the {@code Entity} contains <em>any</em> of {@code Component}s
         * in {@code componentTypes}.
         */
        public static Filter filterWithComponent = new Filter<Entity, Class<? extends Component>[]>() {
            @Override
            public boolean filter(Entity entity, Class<? extends Component>[] componentTypes) {
                for (int j = 0; j < componentTypes.length; j++) {
                    if (entity.hasComponent(componentTypes[j])) {
                        return true;
                    }
                }
                return false;
            }
        };

        /**
         * Returns {@code true} if the {@code Entity} contains <em>all</em> of {@code Component}s
         * in {@code componentTypes}.
         */
        public static Filter filterWithComponents = new Filter<Entity, Class<? extends Component>[]>() {
            @Override
            public boolean filter(Entity entity, Class<? extends Component>[] componentTypes) {
                for (int j = 0; j < componentTypes.length; j++) {
                    if (entity.hasComponents(componentTypes)) {
                        return true;
                    }
                }
                return false;
            }
        };

        public static Filter filterActive = new Filter<Entity, Boolean>() {
            @Override
            public boolean filter(Entity entity, Boolean data) {
                if (entity.isActive) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        public static Filter filterContains = new Filter<Entity, Transform>() {
            @Override
            public boolean filter(Entity entity, Transform point) {
                if (Boundary.contains(entity, point)) {
                    return true;
                } else {
                    return false;
                }
            }
        };
    }

    /**
     * Interface for custom map functions.
     *
     * @param <E> "Input" group element type.
     * @param <M> "Result" group element type.
     * @param <D> Type of data to pass to the {@code Mapper}. Set to {@code Void} if there's no
     *            data.
     */
    public interface Mapper<E, M, D> {
        M map(E value, D data);
    }

    public <V, M, D> Group<M> map(Mapper mapper, D data) {
        Group<M> group = new Group<>();
        for (int i = 0; i < elements.size(); i++) {
            M result = (M) mapper.map(elements.get(i), data);
            if (result != null) {
                group.add(result);
            }
        }
        return group;
    }

    // "Mappers" namespace
    public static class Mappers {

        // Expects Group<Entity>
        // Requires components: Visible
        public static Mapper setVisibility = new Mapper<Entity, Entity, Visible>() {
            @Override
            public Entity map(Entity entity, Visible visible) {
                Visibility visibility = entity.getComponent(Visibility.class);
                if (visibility != null) {
                    visibility.setVisible(visible);
                }
                return null;
            }
        };

        public static Mapper setTransparency = new Mapper<Entity, Entity, Double>() {
            @Override
            public Entity map(Entity entity, Double transparency) {
//                if (entity instanceof HostEntity) { // TODO: Replace with hasComponent(Transparency) -OR- entity.typeUuid == HostEntity.getTypeUuid()
                if (entity.getComponent(Model.class) != null) {
                    // TODO: Set transparency
                    // e.g., entity.getComponent(Style.class).setTransparency(entity, transparency);
                }
                return entity;
            }
        };

        // Assumes Group<Entity>. Returns the Positions of the contained Entities.
        public static Mapper getPosition = new Mapper<Entity, Transform, Void>() {
            @Override
            public Transform map(Entity entity, Void data) {
                if (entity != null) {
                    return entity.getComponent(Transform.class);
                } else {
                    return null;
                }
            }
        };

        // Assumes Group<Entity>. Returns the Positions of the contained Entities.
        public static Mapper getComponent = new Mapper<Entity, Component, Class<? extends Component>>() {
            @Override
            public Component map(Entity entity, Class<? extends Component> componentType) {
                if (entity != null && entity.hasComponent(componentType)) {
                    return entity.getComponent(componentType);
                } else {
                    return null;
                }
            }
        };

        // Assumes Group<Entity>
        public static Mapper getModel = new Mapper<Entity, Model, Void>() {
            @Override
            public Model map(Entity entity, Void data) {
                if (entity.getComponent(Model.class) != null) {
                    return entity.getComponent(Model.class);
                } else {
                    return null;
                }
            }
        };
    }

    // Expects Group<Entity>
    public Group<Entity> filterWithComponent(Class<? extends Component>... componentTypes) {

//        Group<Entity> group = new Group<>();
//
//        for (int i = 0; i < this.elements.size(); i++) {
//            for (int j = 0; j < componentTypes.length; j++) {
//                Class<? extends Component> type = componentTypes[j];
//                Entity entity = (Entity) this.elements.get(i); // HACK: Forcing typecast to Entity
//                if (entity.hasComponent(type)) {
//                    group.add(entity);
//                }
//            }
//        }
//
//        return group;

        return filter(Filters.filterWithComponent, componentTypes);

    }

    // Expects Group<Entity>
    public Group<Entity> filterWithComponents(Class<? extends Component>... componentTypes) {
//        Group<Entity> group = new Group<>();
//        for (int i = 0; i < this.elements.size(); i++) {
//            Entity entity = (Entity) this.elements.get(i); // HACK: Forcing typecast to Entity
//            if (entity.hasComponents(componentTypes)) {
//                group.add(entity);
//            }
//        }
//        return group;

        return filter(Filters.filterWithComponents, componentTypes);
    }

    // Exepcts Group<Entity>
    // Requires components: ModelBuilder
    public Group<Entity> sortByLayer() {
        Group<Entity> group = new Group<>();
        for (int i = 0; i < this.elements.size(); i++) {
            Entity entity = (Entity) this.elements.get(i); // HACK: Forcing typecast to Entity
//            if (entity.hasComponents(componentTypes)) {
//                group.add(entity);
//            }
            // Insertion sort by layers
            boolean isEntityInserted = false;
            for (int j = 0; j < group.size(); j++) {
                Entity sortedEntity = (Entity) group.get(j); // HACK: Forcing typecast to Entity
                if (entity.getComponent(Model.class).layerIndex < sortedEntity.getComponent(Model.class).layerIndex) {
                    group.add(j, entity);
                    isEntityInserted = true;
                    break;
                }
            }

            if (!isEntityInserted) {
                group.add(entity);
            }
        }
        return group;
    }

    // Expects Group<Entity>
    public Group<E> filterUuid(long uuid) {
        return filter(Filters.filterUuid, uuid); // OR: Mappers.setImageVisibility.filter(this);
    }

    // Expects Group<Entity>
    public Group<E> filterActive(boolean isActive) {
        return filter(Filters.filterActive, isActive); // OR: Mappers.setImageVisibility.filter(this);
    }

    // Expects Group<Entity>
    public Group<E> filterVisibility(boolean isVisible) {
        return filter(Filters.filterVisibility, isVisible); // OR: Mappers.setImageVisibility.filter(this);
    }

    // Expects Group<Entity>
    public Group<E> filterContains(Transform point) {
        return filter(Filters.filterContains, point);
    }

    // Expects Group<Entity>
    // Requires components: ModelBuilder
    public void setTransparency(double transparency) {
        map(Mappers.setTransparency, transparency);
    }

    // Assumes Group<Entity>
    public void setVisibility(Visible visible) {
        map(Mappers.setVisibility, visible);
    }

    // <REFACTOR>
    // Assumes Group<Entity>
    public Group<Model> getModels() {
        return map(Mappers.getModel, null);
    }

    // Assumes Group<ModelBuilder>
    public Group<Entity> getPrimitives() {
        Group<Entity> primitives = new Group<>();
        for (int i = 0; i < elements.size(); i++) {
            Model model = (Model) elements.get(i);
            Group<Entity> modelPrimitives = Model.getPrimitives(model.getEntity());
            for (int j = 0; j < modelPrimitives.size(); j++) {
                primitives.add(modelPrimitives.get(j));
            }
        }
        return primitives;
    }
    // <REFACTOR>

    // TODO: <REPLACE_WITH_GET_TRANSFORM>
    // Expects Group<Entity>
    // Requires components: Transform
    public Group<Transform> getPositions() {
        return map(Mappers.getPosition, null);
    }
    // TODO: </REPLACE_WITH_GET_TRANSFORM>

    // Expects Group<Entity>
    // Requires components: Passed in as argument
    public <C extends Component> Group<C> getComponent(Class<C> componentType) {
        return map(Mappers.getComponent, componentType);
    }

    /**
     * Removes elements <em>that do not match</em> the regular expressions defined in
     * {@code labels}.
     *
     * @param labels The list of {@code Shape} objects matching the regular expressions list.
     * @return A list of {@code Shape} objects.
     */
    // Expects Group<Entity>
    // Requires components: Label
    public Group<Entity> filterLabel(String... labels) {

        // HACK: Assumes Group<Entity>

        Group<Entity> shapeGroup = new Group<>();

        for (int i = 0; i < this.elements.size(); i++) {
            for (int j = 0; j < labels.length; j++) {

                Pattern pattern = Pattern.compile(labels[j]);
                Entity entity = (Entity) this.elements.get(i); // HACK: Forcing typecast to Entity. TODO: Make this safe...
                Matcher matcher = pattern.matcher(Label.getLabel(entity));

                boolean isMatch = matcher.matches();

                if (isMatch) {
                    shapeGroup.add((Entity) this.elements.get(i)); // HACK: Forced type to be Entity
                }
            }
        }

        return shapeGroup;
    }
    // </TODO: REMOVE. REPLACE WITH CALLS TO SHAPES IN IMAGE API.>

    /**
     * Filters elements that fall within the area defined by {@code shape}.
     *
     * @param point The {@code Shape} covering the area to filter.
     * @return {@code Group} of {@code Entity} objects within {@code distance} from {@code point}.
     */
    // Expects Group<Entity>
    public Group<Entity> filterArea(Transform point, double distance) {
        Group<Entity> entities = new Group<>();
        for (int i = 0; i < elements.size(); i++) {
            Entity entity = (Entity) elements.get(i);
            double distanceToEntity = camp.computer.clay.util.Geometry.distance(point, entity.getComponent(Transform.class));
            if (distanceToEntity < distance) {
                entities.add(entity);
            }
        }
        return entities;
    }

    /**
     * Finds and returns the nearest <em>visible</em> <code>ModelBuilder</code>.
     *
     * @return
     */
//    // HACK: Expects Group<ModelBuilder>
//    public ModelBuilder getNearestImage(Transform position) {
//
//        double shortestDistance = Float.MAX_VALUE;
//        ModelBuilder nearestImage = null;
//
//        for (int i = 0; i < elements.size(); i++) {
//            ModelBuilder image = (ModelBuilder) elements.get(i);
//
//            double currentDistance = Primitive.distance(position, image.getPosition());
//
//            if (currentDistance < shortestDistance) {
//                shortestDistance = currentDistance;
//                nearestImage = image;
//            }
//        }
//
//        return nearestImage;
//    }

    // HACK: Expects Group<ModelBuilder>
    // TODO: Restrict it to Group<Transform> and use reduce(Reducers.getCenterPoint)
    public Transform getCenterPoint() {
        return camp.computer.clay.util.Geometry.getCenterPoint(getPositions());
    }

    // TODO: <MERGE_WITH_GET_BOUNDARY_VERTICES>
    // Expects Group<Shape>
    public Group<Transform> getVertices() {
        Group<Transform> positions = new Group<>();
        for (int i = 0; i < elements.size(); i++) {
            Shape shape = (Shape) elements.get(i);
            positions.addAll(shape.getVertices()); // HACK: (shouldn't get Boundary if asking for Vertices. DISTINGUISH THESE SOMEHOW!)
            // TODO: Fix this and replace previous line: //positions.addAll(BoundarySystem.get(shape));
        }
        return positions;
    }
    // TODO: </MERGE_WITH_GET_BOUNDARY_VERTICES>

    // Expects Group<Entity>
    public Group<Transform> getBoundaryVertices() {
        Group<Transform> boundaryVertices = new Group<>();
        for (int i = 0; i < elements.size(); i++) {
            Entity entity = (Entity) elements.get(i);
            boundaryVertices.addAll(entity.getComponent(Boundary.class).boundary); // HACK: (shouldn't get Boundary if asking for Vertices. DISTINGUISH THESE SOMEHOW!)
            // TODO: Fix this and replace previous line: //positions.addAll(BoundarySystem.get(shape));
        }
        return boundaryVertices;
    }

    // Expects Group<Entity>
    public Rectangle getBoundingBox() {

        List<Transform> imageBoundaries = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            Entity entity = (Entity) elements.get(i); // HACK: Force cast to Entity. TODO: Add safety!
            // TODO: Fix: imageBoundaries.addAll(BoundarySystem.get(Boundary.getBoundingBox(entity)));
//            imageBoundaries.addAll(Boundary.getBoundingBox(entity).getVertices());
            //imageBoundaries.addAll(entity.getComponent(Boundary.class).get());
//            imageBoundaries.addAll(entity.getComponent(Boundary.class).boundary);

            imageBoundaries.add(entity.getComponent(Transform.class));
        }

        return camp.computer.clay.util.Geometry.getBoundingBox(imageBoundaries);
    }
    // </GROUP_LOOKUP>


    // <LIST_INTERFACE>

    /**
     * Inserts the specified object into this {@code List} at the specified location.
     * The object is inserted before the current element at the specified
     * location. If the location is equal to the size of this {@code List}, the object
     * is added at the end. If the location is smaller than the size of this
     * {@code List}, then all elements beyond the specified location are moved by one
     * position towards the end of the {@code List}.
     *
     * @param location the index at which to insert.
     * @param object   the object to add.
     * @throws UnsupportedOperationException if adding to this {@code List} is not supported.
     * @throws ClassCastException            if the class of the object is inappropriate for this
     *                                       {@code List}.
     * @throws IllegalArgumentException      if the object cannot be added to this {@code List}.
     * @throws IndexOutOfBoundsException     if {@code location < 0 || location > size()}
     */
    @Override
    public void add(int location, E object) {
        this.elements.add(location, object);
    }

    /**
     * Adds the specified object at the end of this {@code List}.
     *
     * @param object the object to add.
     * @return always true.
     * @throws UnsupportedOperationException if adding to this {@code List} is not supported.
     * @throws ClassCastException            if the class of the object is inappropriate for this
     *                                       {@code List}.
     * @throws IllegalArgumentException      if the object cannot be added to this {@code List}.
     */
    @Override
    public boolean add(E object) {
//        Log.v("SUBGROUP_ADD", "Adding to Group");
//        result.add(elements.get(i));
        if (this.filter == null || this.filter.filter(object, this.data) == true) {
            this.elements.add(object);
        }

//        // Update subscribers
//        for (int i = 0; i < subscribers.size(); i++) {
//            Log.v("SUBGROUP_ADD", "Adding to subscriber...");
//            subscribers.get(i).add(object);
//        }

        return true;
    }

    /**
     * Inserts the objects in the specified collection at the specified location
     * in this {@code List}. The objects are added in the order they are returned from
     * the collection's iterator.
     *
     * @param location   the index at which to insert.
     * @param collection the collection of objects to be inserted.
     * @return true if this {@code List} has been modified through the insertion, false
     * otherwise (i.e. if the passed collection was empty).
     * @throws UnsupportedOperationException if adding to this {@code List} is not supported.
     * @throws ClassCastException            if the class of an object is inappropriate for this
     *                                       {@code List}.
     * @throws IllegalArgumentException      if an object cannot be added to this {@code List}.
     * @throws IndexOutOfBoundsException     if {@code location < 0 || location > size()}.
     * @throws NullPointerException          if {@code collection} is {@code null}.
     */
    @Override
    public boolean addAll(int location, Collection<? extends E> collection) {
        return this.elements.addAll(location, collection);
    }

    /**
     * Adds the objects in the specified collection to the end of this {@code List}. The
     * objects are added in the order in which they are returned from the
     * collection's iterator.
     *
     * @param collection the collection of objects.
     * @return {@code true} if this {@code List} is modified, {@code false} otherwise
     * (i.e. if the passed collection was empty).
     * @throws UnsupportedOperationException if adding to this {@code List} is not supported.
     * @throws ClassCastException            if the class of an object is inappropriate for this
     *                                       {@code List}.
     * @throws IllegalArgumentException      if an object cannot be added to this {@code List}.
     * @throws NullPointerException          if {@code collection} is {@code null}.
     */
    @Override
    public boolean addAll(Collection<? extends E> collection) {
        return this.elements.addAll(collection);
    }

    /**
     * Removes all elements from this {@code List}, leaving it empty.
     *
     * @throws UnsupportedOperationException if removing from this {@code List} is not supported.
     * @see #isEmpty
     * @see #size
     */
    @Override
    public void clear() {
        this.elements.clear();
    }

    /**
     * Tests whether this {@code List} contains the specified object.
     *
     * @param object the object to search for.
     * @return {@code true} if object is an element of this {@code List}, {@code false}
     * otherwise
     */
    @Override
    public boolean contains(Object object) {
        return elements.contains(object);
    }

    /**
     * Tests whether this {@code List} contains all objects contained in the
     * specified collection.
     *
     * @param collection the collection of objects
     * @return {@code true} if all objects in the specified collection are
     * elements of this {@code List}, {@code false} otherwise.
     * @throws NullPointerException if {@code collection} is {@code null}.
     */
    @Override
    public boolean containsAll(Collection<?> collection) {
        return this.elements.containsAll(collection);
    }

    /**
     * Returns the element at the specified location in this {@code List}.
     *
     * @param location the index of the element to return.
     * @return the element at the specified location.
     * @throws IndexOutOfBoundsException if {@code location < 0 || location >= size()}
     */
    @Override
    public E get(int location) {
        return this.elements.get(location);
    }

    /**
     * Searches this {@code List} for the specified object and returns the index of the
     * first occurrence.
     *
     * @param object the object to search for.
     * @return the index of the first occurrence of the object or -1 if the
     * object was not found.
     */
    @Override
    public int indexOf(Object object) {
        return this.elements.indexOf(object);
    }

    /**
     * Returns whether this {@code List} contains no elements.
     *
     * @return {@code true} if this {@code List} has no elements, {@code false}
     * otherwise.
     * @see #size
     */
    @Override
    public boolean isEmpty() {
        return this.elements.isEmpty();
    }

    /**
     * Returns an iterator on the elements of this {@code List}. The elements are
     * iterated in the same order as they occur in the {@code List}.
     *
     * @return an iterator on the elements of this {@code List}.
     * @see Iterator
     */
    @NonNull
    @Override
    public Iterator<E> iterator() {
        return this.elements.iterator();
    }

    /**
     * Searches this {@code List} for the specified object and returns the index of the
     * last occurrence.
     *
     * @param object the object to search for.
     * @return the index of the last occurrence of the object, or -1 if the
     * object was not found.
     */
    @Override
    public int lastIndexOf(Object object) {
        return this.elements.lastIndexOf(object);
    }

    /**
     * Returns a {@code List} iterator on the elements of this {@code List}. The elements are
     * iterated in the same order that they occur in the {@code List}.
     *
     * @return a {@code List} iterator on the elements of this {@code List}
     * @see ListIterator
     */
    @Override
    public ListIterator<E> listIterator() {
        return this.elements.listIterator();
    }

    /**
     * Returns a list iterator on the elements of this {@code List}. The elements are
     * iterated in the same order as they occur in the {@code List}. The iteration
     * starts at the specified location.
     *
     * @param location the index at which to start the iteration.
     * @return a list iterator on the elements of this {@code List}.
     * @throws IndexOutOfBoundsException if {@code location < 0 || location > size()}
     * @see ListIterator
     */
    @NonNull
    @Override
    public ListIterator<E> listIterator(int location) {
        return this.elements.listIterator(location);
    }

    /**
     * Removes the object at the specified location from this {@code List}.
     *
     * @param location the index of the object to remove.
     * @return the removed object.
     * @throws UnsupportedOperationException if removing from this {@code List} is not supported.
     * @throws IndexOutOfBoundsException     if {@code location < 0 || location >= size()}
     */
    @Override
    public E remove(int location) {
        return this.elements.remove(location);
    }

    /**
     * Removes the first occurrence of the specified object from this {@code List}.
     *
     * @param object the object to remove.
     * @return true if this {@code List} was modified by this operation, false
     * otherwise.
     * @throws UnsupportedOperationException if removing from this {@code List} is not supported.
     */
    @Override
    public boolean remove(Object object) {
        return this.elements.remove(object);
    }

    /**
     * Removes all occurrences in this {@code List} of each object in the specified
     * collection.
     *
     * @param collection the collection of objects to remove.
     * @return {@code true} if this {@code List} is modified, {@code false} otherwise.
     * @throws UnsupportedOperationException if removing from this {@code List} is not supported.
     * @throws NullPointerException          if {@code collection} is {@code null}.
     */
    @Override
    public boolean removeAll(Collection<?> collection) {
        return this.elements.removeAll(collection);
    }

    /**
     * Removes all objects from this {@code List} that are not contained in the
     * specified collection.
     *
     * @param collection the collection of objects to retain.
     * @return {@code true} if this {@code List} is modified, {@code false} otherwise.
     * @throws UnsupportedOperationException if removing from this {@code List} is not supported.
     * @throws NullPointerException          if {@code collection} is {@code null}.
     */
    @Override
    public boolean retainAll(Collection<?> collection) {
        return this.elements.retainAll(collection);
    }

    /**
     * Replaces the element at the specified location in this {@code List} with the
     * specified object. This operation does not change the size of the {@code List}.
     *
     * @param location the index at which to put the specified object.
     * @param object   the object to insert.
     * @return the previous element at the index.
     * @throws UnsupportedOperationException if replacing elements in this {@code List} is not supported.
     * @throws ClassCastException            if the class of an object is inappropriate for this
     *                                       {@code List}.
     * @throws IllegalArgumentException      if an object cannot be added to this {@code List}.
     * @throws IndexOutOfBoundsException     if {@code location < 0 || location >= size()}
     */
    @Override
    public E set(int location, E object) {
        return this.elements.set(location, object);
    }

    /**
     * Returns the number of elements in this {@code List}.
     *
     * @return the number of elements in this {@code List}.
     */
    @Override
    public int size() {
        return this.elements.size();
    }

    /**
     * Returns a {@code List} of the specified portion of this {@code List} from the given start
     * index to the end index minus one. The returned {@code List} is backed by this
     * {@code List} so changes to it are reflected by the other.
     *
     * @param start the index at which to start the sublist.
     * @param end   the index one past the end of the sublist.
     * @return a list of a portion of this {@code List}.
     * @throws IndexOutOfBoundsException if {@code start < 0, start > end} or {@code end >
     *                                   size()}
     */
    @NonNull
    @Override
    public List<E> subList(int start, int end) {
        return this.subList(start, end);
    }

    /**
     * Returns an array containing all elements contained in this {@code List}.
     *
     * @return an array of the elements from this {@code List}.
     */
    @NonNull
    @Override
    public Object[] toArray() {
        return this.elements.toArray();
    }

    /**
     * Returns an array containing all elements contained in this {@code List}. If the
     * specified array is large enough to hold the elements, the specified array
     * is used, otherwise an array of the same type is created. If the specified
     * array is used and is larger than this {@code List}, the array element following
     * the collection elements is set to null.
     *
     * @param array the array.
     * @return an array of the elements from this {@code List}.
     * @throws ArrayStoreException if the type of an element in this {@code List} cannot be stored
     *                             in the type of the specified array.
     */
    @NonNull
    @Override
    public <T1> T1[] toArray(T1[] array) {
        return this.elements.toArray(array);
    }

    // </LIST_INTERFACE>
}