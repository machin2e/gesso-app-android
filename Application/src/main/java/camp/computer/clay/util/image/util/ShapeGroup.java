package camp.computer.clay.util.image.util;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import camp.computer.clay.model.Entity;
import camp.computer.clay.model.Group;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Visibility;

/**
 * ShapeGroup is an interface for managing and manipulating sets of elements.
 */
public class ShapeGroup extends Group<Shape> {

    public ShapeGroup() {
    }

    public ShapeGroup remove(Shape shape) {
        elements.remove(shape);
        return this;
    }

    /**
     * Removes elements <em>that do not match</em> the regular expressions defined in
     * {@code labels}.
     *
     * @param labels The list of {@code Shape} objects matching the regular expressions list.
     * @return A list of {@code Shape} objects.
     */
    public ShapeGroup filterLabel(String... labels) {

        ShapeGroup shapeGroup = new ShapeGroup();

        for (int i = 0; i < this.elements.size(); i++) {
            for (int j = 0; j < labels.length; j++) {

                Pattern pattern = Pattern.compile(labels[j]);
                Matcher matcher = pattern.matcher(this.elements.get(i).getLabel());

                boolean isMatch = matcher.matches();

//                if (this.elements.get(i).getLabel().equals(labels[j])) {
                if (isMatch) {
                    shapeGroup.add(this.elements.get(i));
                }
            }
        }

        return shapeGroup;
    }

    /**
     * Removes all elements except those with the specified type.
     *
     * @param entityTypes
     * @return
     */
    public <T extends Entity> ShapeGroup filterType(Class<?>... entityTypes) {

        ShapeGroup shapeGroup = new ShapeGroup();

        for (int i = 0; i < this.elements.size(); i++) {
            for (int j = 0; j < entityTypes.length; j++) {
                Class<?> type = entityTypes[j];
                if (this.elements.get(i).getEntity() != null && this.elements.get(i).getEntity().getClass() == type) {
                    shapeGroup.add(this.elements.get(i));
                }
            }
        }

        return shapeGroup;
    }

    public <T extends Entity> ShapeGroup filterEntity(Group<T> entities) {
        ShapeGroup shapeGroup = new ShapeGroup();

        for (int i = 0; i < this.elements.size(); i++) {
            for (int j = 0; j < entities.size(); j++) {
                if (this.elements.get(i).getEntity() != null && this.elements.get(i).getEntity() == entities.get(j)) {
                    shapeGroup.add(this.elements.get(i));
                }
            }
        }

        return shapeGroup;
    }

    public <T extends Entity> ShapeGroup filterEntity(T... entities) {
        ShapeGroup shapeGroup = new ShapeGroup();

        for (int i = 0; i < this.elements.size(); i++) {
            for (int j = 0; j < entities.length; j++) {
                if (this.elements.get(i).getEntity() != null && this.elements.get(i).getEntity() == entities[j]) {
                    shapeGroup.add(this.elements.get(i));
                }
            }
        }

        return shapeGroup;
    }

    /**
     * Filters elements to those that are within the specified distance from the specified point.
     *
     * @param point
     * @param distance
     * @return
     */
    public ShapeGroup filterArea(Point point, double distance) {

        ShapeGroup shapeGroup = new ShapeGroup();

        for (int i = 0; i < elements.size(); i++) {
            Shape shape = elements.get(i);

            double distanceToShape = Geometry.distance(point, shape.getPosition());

            if (distanceToShape < distance) {
                shapeGroup.add(shape);
            }

        }

        return shapeGroup;

    }

    public ShapeGroup filterContains(Point point) {

        ShapeGroup shapeGroup = new ShapeGroup();

        for (int i = 0; i < elements.size(); i++) {
            Shape shape = elements.get(i);

            if (shape.contains(point)) {
                shapeGroup.add(shape);
            }

        }

        return shapeGroup;
    }

    /**
     * Filters elements that fall within the area defined by {@code shape}.
     *
     * @param shape The {@code Shape} covering the area to filter.
     * @return The {@code ShapeGroup} containing the area covered by {@code shape}.
     */
    public ShapeGroup filterArea(Shape shape) {

        ShapeGroup shapeGroup = new ShapeGroup();

        for (int i = 0; i < elements.size(); i++) {
            Shape otherShape = elements.get(i);
            if (shape.contains(otherShape.getPosition())) {
                shapeGroup.add(otherShape);
            }
        }

        return shapeGroup;
    }

    public ShapeGroup filterVisibility(Visibility visibility) {

        ShapeGroup shapeGroup = new ShapeGroup();

        for (int i = 0; i < elements.size(); i++) {
            Shape shape = elements.get(i);
            if (shape.getVisibility() == visibility) {
                shapeGroup.add(shape);
            }

        }

        return shapeGroup;
    }

    public List<Shape> getList() {
        return elements;
    }

    public List<Point> getPositions() {
        List<Point> positions = new LinkedList<>();
        for (int i = 0; i < elements.size(); i++) {
            Shape shape = elements.get(i);
            positions.add(new Point(shape.getPosition().x, shape.getPosition().y));
        }
        return positions;
    }

    public List<Point> getVertices() {
        List<Point> positions = new LinkedList<>();
        for (int i = 0; i < elements.size(); i++) {
            Shape shape = elements.get(i);
            positions.addAll(shape.getBoundary());
        }
        return positions;
    }

    public Point getCenterPosition() {
        return Geometry.getCenterPoint(getPositions());
    }


    public Point getCentroidPosition() {
        return Geometry.getCentroidPoint(getPositions());
    }

    public Rectangle getBoundingBox() {
        return Geometry.getBoundingBox(getVertices());
    }

    /**
     * Finds and returns the nearest <em>visible</em> <code>Shape</code>.
     *
     * @param position
     * @return
     */
    public Shape getNearest(Point position) {

        double shortestDistance = Float.MAX_VALUE;
        Shape nearestShape = null;

        for (int i = 0; i < elements.size(); i++) {
            Shape shape = elements.get(i);

            double currentDistance = Geometry.distance(position, shape.getPosition());

            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                nearestShape = shape;
            }
        }

        return nearestShape;
    }

    public void setTransparency(double transparency) {
        for (int i = 0; i < elements.size(); i++) {
            Shape shape = elements.get(i);
            shape.setTransparency(transparency);
        }
    }

    public void setVisibility(Visibility.Value visibility) {
        for (int i = 0; i < elements.size(); i++) {
            Shape shape = elements.get(i);
            shape.setVisibility(visibility);
        }
    }
}
