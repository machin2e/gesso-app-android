package camp.computer.clay.model;

import android.support.annotation.NonNull;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;

import camp.computer.clay.engine.Component;
import camp.computer.clay.engine.Groupable;
import camp.computer.clay.engine.Entity;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.image.Image;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Visibility;
import camp.computer.clay.util.image.util.ShapeGroup;

public class Group<E extends Groupable> implements List<E> {

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
    // TODO: (cont'd) Place custom filters in Entity classes (e.g., Entity.Filter.getImagePosition(...)).

    public interface Filter<V extends Groupable, D> {
        boolean filter(V entity, D... data);
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

    public <D> Group<E> transform(Mapper mapper, D data) {
        // Group<V> result = new Group<>();
        for (int i = 0; i < elements.size(); i++) {
            mapper.map(elements.get(i), data);
        }
        // return result;
        return this;
    }

    public <R, D> List<R> collect(Mapper collector, D data) {
        List<R> result = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            R resultGroup = (R) collector.map(elements.get(i), data);
            result.add(resultGroup);
        }
        return result;
    }

    public static class Filters {

        public static Filter filterType = new Filter<Entity, Class<?>>() {
            @Override
            public boolean filter(Entity entity, Class<?>... entityTypes) {
                if (Arrays.asList(entityTypes).contains(entity.getClass())) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        public static Filter filterVisibility = new Filter<Image, Visibility>() {
            @Override
            public boolean filter(Image entity, Visibility... visibilities) {
                //if (entity.getImage().getVisibility() == visibilities[0]) {
                if (entity.getVisibility() == visibilities[0]) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        public static Filter filterContains = new Filter<Image, Point>() {
            @Override
            public boolean filter(Image entity, Point... points) {
                if (entity.contains(points[0])) {
                    return true;
                } else {
                    return false;
                }
            }
        };

        // TODO: hasComponent
    }

    /**
     * Interface for custom map functions.
     *
     * @param <E> "Input" group element type.
     * @param <M> "Result" group element type.
     * @param <D> Type of data to pass to the {@code Mapper}. Set to {@code Void} if there's no
     *            data.
     */
    public interface Mapper<E extends Groupable, M extends Groupable, D> {
        M map(E value, D data);
    }

    public <V extends Groupable, M extends Groupable, D> Group<M> map(Mapper mapper, D data) {
        Group<M> group = new Group<>();
        for (int i = 0; i < elements.size(); i++) {
            M result = (M) mapper.map(elements.get(i), data);
            if (result != null) {
                group.add(result);
            }
        }
        return group;
    }

    public static class Mappers { // was Operations

//        public static Mapper setImageVisibility = new Mapper<Entity, Entity, Visibility>() {
//            @Override
//            public Entity map(Entity entity, Visibility visibility) {
//                entity.getImage().setImageVisibility(visibility);
//                return entity;
//            }
//        };

        // HACK:
        public static Mapper setVisibilityGeneric = new Mapper<Groupable, Groupable, Visibility>() {
            @Override
            public Image map(Groupable entity, Visibility visibility) {
                if (entity.getClass() == Shape.class) { // HACK
                    ((Shape) entity).setVisibility(visibility);
                } else if (entity.getClass() == Image.class) { // HACK
                    ((Shape) entity).setVisibility(visibility);
                }
                return null;
            }
        };

//        public static Mapper setImageVisibility = new Mapper<Image, Image, Visibility>() {
//            @Override
//            public Image map(Image entity, Visibility visibility) {
//                entity.setVisibility(visibility);
//                return entity;
//            }
//        };
//
//        public static Mapper setShapeVisibility = new Mapper<Shape, Shape, Visibility>() {
//            @Override
//            public Shape map(Shape entity, Visibility visibility) {
//                entity.setVisibility(visibility);
//                return entity;
//            }
//        };

        public static Mapper setTransparency = new Mapper<Entity, Entity, Double>() {
            @Override
            public Entity map(Entity entity, Double transparency) {
//                if (entity instanceof Host) { // TODO: Replace with hasComponent(Transparency) -OR- entity.typeUuid == Host.getTypeUuid()
                if (entity.getImage() != null) {
                    entity.getImage().setTransparency(transparency);
                }
                return entity;
            }
        };

        public static Mapper getImagePosition = new Mapper<Image, Point, Void>() {
            @Override
            public Point map(Image entity, Void data) {
                if (entity != null) {
                    return entity.getPosition();
                } else {
                    return null;
                }
            }
        };

        // HACK?
        public static Mapper getShapePosition = new Mapper<Shape, Point, Void>() {
            @Override
            public Point map(Shape entity, Void data) {
                if (entity != null) {
                    return entity.getPosition();
                } else {
                    return null;
                }
            }
        };

//        public static Mapper getPosition = new Mapper<Entity, Point, Void>() {
//            @Override
//            public Point map(Entity entity, Void data) {
//                if (entity.getImage() != null) {
//                    return entity.getImage().getPosition();
//                } else {
//                    return null;
//                }
//            }
//        };

        public static Mapper getImage = new Mapper<Entity, Image, Void>() {
            @Override
            public Image map(Entity entity, Void data) {
                if (entity.getImage() != null) {
                    return entity.getImage();
                } else {
                    return null;
                }
            }
        };
    }

    // TODO: Convert to Filter and restrict to Image types
    public Group<E> filterVisibility(Visibility visibility) {
        return filter(Filters.filterVisibility, visibility); // OR: Mappers.setImageVisibility.filter(this);
    }

    public Group<E> filterContains(Point point) {
        return filter(Filters.filterContains, point);
    }

    public void setTransparency(double transparency) {

//        List<Point> positions = getPositionCollector();
//        Log.v("Collector", "positions.size: " + positions.size());
//        for (int i = 0; i < positions.size(); i++) {
//            Log.v("Collector", "position.x: " + positions.get(i).x + ", y: " + positions.get(i).y);
//        }

        map(Mappers.setTransparency, transparency); // OR: Mappers.setImageVisibility.filter(this);

//        map(new Mapper<Entity, Entity, Double>() {
//            @Override
//            public Entity map(Entity entity, Double transparency) {
////                if (entity instanceof Host) { // TODO: Replace with hasComponent(Transparency) -OR- entity.typeUuid == Host.getTypeUuid()
//                if (entity.getImages() != null) {
//                    entity.getImages().setTransparency(transparency);
//                }
//                return entity;
//            }
//        }, null);
    }

    public void setVisibility(Visibility visibility) {
        Log.v("Reflect", "E: " + this.getClass());
        map(Mappers.setVisibilityGeneric, visibility);
    }

    public Group<Image> getImages() {
        return map(Mappers.getImage, null);
    }

    public Group<Point> getPositions() {
        Log.v("Reflect", "E: " + this.getClass());
        if (this.getClass() == ShapeGroup.class) { // HACK
            return map(Mappers.getShapePosition, null);
        } else {
            return map(Mappers.getImagePosition, null);
        }
    }

    /**
     * Removes all elements except those with the specified type.
     *
     * @param entityTypes
     * @return
     */
    public <E extends Groupable> Group<E> filterType2(Class<? extends Groupable>... entityTypes) {

        Group<E> imageGroup = new Group<>();

        for (int i = 0; i < this.elements.size(); i++) {
            for (int j = 0; j < entityTypes.length; j++) {
                Class<?> type = entityTypes[j];
                if (this.elements.get(i).getClass() == type) {
                    imageGroup.add((E) this.elements.get(i));
                }
            }
        }

        return imageGroup;
    }

    /**
     * Filters elements that fall within the area defined by {@code shape}.
     *
     * @param shape The {@code Shape} covering the area to filter.
     * @return The {@code ImageGroup} containing the area covered by {@code shape}.
     */
    // HACK: Expects Group<Image>
    public Group<Image> filterArea(Point point, double distance) {

        Group<Image> imageGroup = new Group<>();

        for (int i = 0; i < elements.size(); i++) {

            Image image = (Image) elements.get(i);

            double distanceToImage = Geometry.distance(point, image.getPosition());

            if (distanceToImage < distance) {
                imageGroup.add(image);
            }

        }

        return imageGroup;

    }

    /**
     * Finds and returns the nearest <em>visible</em> <code>Image</code>.
     *
     * @param position
     * @return
     */
    // HACK: Expects Group<Image>
    public Image getNearestImage(Point position) {

        double shortestDistance = Float.MAX_VALUE;
        Image nearestImage = null;

        for (int i = 0; i < elements.size(); i++) {
            Image image = (Image) elements.get(i);

            double currentDistance = Geometry.distance(position, image.getPosition());

            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                nearestImage = image;
            }
        }

        return nearestImage;
    }

    // HACK: Expects Group<Image>
    // TODO: Restrict it to Group<Point> and use reduce(Reducers.getCenterPoint)
    public Point getCenterPoint() {
        return Geometry.getCenterPoint(getPositions());
    }

    // HACK: Expects Group<Image>
    // TODO: Restrict it to Group<Point> and use reduce(Reducers.getCenterPoint)
    public Point getCentroidPosition() {
        return Geometry.getCentroidPoint(getPositions());
    }

    // HACK: Expects Group<Image>
    public Rectangle getBoundingBox() {

        List<Point> imageBoundaries = new LinkedList<>();
        for (int i = 0; i < elements.size(); i++) {
            Image image = (Image) elements.get(i);
            imageBoundaries.addAll(image.getBoundingBox().getBoundary());
        }

        return Geometry.getBoundingBox(imageBoundaries);
    }

//    public Rectangle getBoundingBox() {
//
//        List<Point> imageBoundaries = new LinkedList<>();
//        for (int i = 0; i < elements.size(); i++) {
//            imageBoundaries.addAll(elements.get(i).getBoundingBox().getBoundary());
//        }
//
//        return Geometry.getBoundingBox(imageBoundaries);
//    }

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