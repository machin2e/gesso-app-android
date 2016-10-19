package camp.computer.clay.model;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import camp.computer.clay.engine.Addressable;
import camp.computer.clay.engine.Entity;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.image.Visibility;

public class Group<E extends Addressable> implements List<E> {
// TODO: public class Group<E> implements List<E> {

    // TODO: private List<E> elements = new LinkedList<>();
    protected List<E> elements = new LinkedList<>();

    public E get(UUID uuid) {
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).getUuid().equals(uuid)) {
                return elements.get(i);
            }
        }
        return null;
    }

    public boolean contains(UUID uuid) {
        for (int i = 0; i < elements.size(); i++) {
            if (elements.get(i).getUuid().equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    public E remove(UUID uuid) {
        E element = get(uuid);
        remove(element);
        return element;
    }

    // TODO: Impelement a generic filter(...) interface so custom filters can be used. They should
    // TODO: (cont'd) be associated with a Entity type ID, so they only operate on the right entities.
    // TODO: (cont'd) Place custom filters in Entity classes (e.g., Entity.Filter.getPosition(...)).
    public interface Filter<E extends Addressable, D> {
        boolean filter(E entity, D... data); // TODO: boolean test(E element)
    }

    public interface Mapper<E extends Addressable, D> {
        void map(E entity, D data);
    }

    // R : result type
    // D : data
    public interface Collector<E extends Addressable, R, D> {
        R collect(E entity, D data);
    }

    public <D> Group filter(Filter filter, D... data) {
        Group<E> result = new Group<>();
        for (int i = 0; i < elements.size(); i++) {
            if (filter.filter(elements.get(i), data) == true) {
                result.add(elements.get(i));
            }
        }
        return result;
    }

    public <D> Group<E> map(Mapper mapper, D data) {
        // Group<E> result = new Group<>();
        for (int i = 0; i < elements.size(); i++) {
            mapper.map(elements.get(i), data);
        }
        // return result;
        return this;
    }

    public <R, D> List<R> collect(Collector collector, D data) {
        List<R> result = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            R resultGroup = (R) collector.collect(elements.get(i), data);
            result.add(resultGroup);
        }
        return result;
    }

    public static class Filters {

        public static Filter retainType = new Filter<Entity, Class<?>>() {
            @Override
            public boolean filter(Entity entity, Class<?>... entityTypes) {
                if (Arrays.asList(entityTypes).contains(entity.getClass())) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        // TODO: hasComponent
    }

    public static class Collectors {

        public static Collector getPosition = new Collector<Entity, Point, Void>() {
            @Override
            public Point collect(Entity entity, Void data) {
                if (entity.getImage() != null) {
                    return entity.getImage().getPosition();
                } else {
                    return null;
                }
            }
        };
    }

    public static class Mappers { // was Operations

        public static Mapper setVisibility = new Mapper<Entity, Visibility>() {
            @Override
            public void map(Entity entity, Visibility visibility) {
                entity.getImage().setVisibility(visibility);
            }
        };

        public static Mapper setTransparency = new Mapper<Entity, Double>() {
            @Override
            public void map(Entity entity, Double transparency) {
//                if (entity instanceof Host) { // TODO: Replace with hasComponent(Transparency) -OR- entity.typeUuid == Host.getTypeUuid()
                if (entity.getImage() != null) {
                    entity.getImage().setTransparency(transparency);
                }
            }
        };

//        public static Mapper setVisibility = new Mapper<Host, Visibility>() {
//            @Override
//            public void map(Host entity, Visibility data) {
//                entity.getImage().setVisibility(data);
//            }
//        };
//
//        public static Mapper setTransparency = new Mapper<Host, Double>() {
//            @Override
//            public void map(Host entity, Double transparency) {
//                if (entity instanceof Host) { // TODO: Replace with hasComponent(Transparency) -OR- entity.typeUuid == Host.getTypeUuid()
//                    entity.getImage().setTransparency(transparency);
//                }
//            }
//        };

    }

    public Group<E> setVisibilityMapper(Visibility visibility) {
        return map(Mappers.setVisibility, visibility); // OR: Mappers.setVisibility.filter(this);
    }

    public List<Point> getPositionCollector() {
        return collect(Collectors.getPosition, null);
    }

    public void setTransparency(double transparency) {

//        List<Point> positions = getPositionCollector();
//        Log.v("Collector", "positions.size: " + positions.size());
//        for (int i = 0; i < positions.size(); i++) {
//            Log.v("Collector", "position.x: " + positions.get(i).x + ", y: " + positions.get(i).y);
//        }

        map(Mappers.setTransparency, transparency); // OR: Mappers.setVisibility.filter(this);
    }

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
        this.elements.add(object);
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