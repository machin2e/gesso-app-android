package camp.computer.clay.space.architecture;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import camp.computer.clay.model.architecture.Entity;
import camp.computer.clay.space.util.Visibility;
import camp.computer.clay.space.util.geometry.Geometry;
import camp.computer.clay.space.util.geometry.Point;
import camp.computer.clay.space.util.geometry.Rectangle;
import camp.computer.clay.space.util.geometry.Shape;

/**
 * ShapeGroup is an interface for managing and manipulating sets of shapes.
 */
public class ShapeGroup {

    private List<Shape> shapes = new LinkedList<>();

    public ShapeGroup() {
    }

    public void add(Shape shape) {
        this.shapes.add(shape);
    }

    public void add(List<Shape> shapes) {
        this.shapes.addAll(shapes);
    }

    public void add(ShapeGroup shapeGroup) {
        List<Shape> shapeList = shapeGroup.getList();
        this.shapes.addAll(shapeList);
    }

    public boolean contains(Shape shape) {
        return shapes.contains(shape);
    }

    public ShapeGroup remove(Shape shape) {
        shapes.remove(shape);
        return this;
    }

    public Shape get(int index) {
        if (index < this.shapes.size()) {
            return shapes.get(index);
        } else {
            return null;
        }
    }

    public Shape getFirst() {
        if (shapes.size() > 0) {
            return shapes.get(0);
        }
        return null;
    }

    public Shape getLast() {
        if (shapes.size() > 0) {
            return shapes.get(shapes.size() - 1);
        }
        return null;
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

        for (int i = 0; i < this.shapes.size(); i++) {
            for (int j = 0; j < labels.length; j++) {

                Pattern pattern = Pattern.compile(labels[j]);
                Matcher matcher = pattern.matcher(this.shapes.get(i).getLabel());

                boolean isMatch = matcher.matches();

//                if (this.shapes.get(i).getLabel().equals(labels[j])) {
                if (isMatch) {
                    shapeGroup.add(this.shapes.get(i));
                }
            }
        }

        return shapeGroup;
    }

    /**
     * Removes all elements except those with the specified type.
     *
     * @param types
     * @return
     */
    public <T extends Entity> ShapeGroup filterType(Class<?>... types) {

        ShapeGroup shapeGroup = new ShapeGroup();

        for (int i = 0; i < this.shapes.size(); i++) {
            for (int j = 0; j < types.length; j++) {
                Class<?> type = types[j];
                if (this.shapes.get(i).getEntity() != null && this.shapes.get(i).getEntity().getClass() == type) {
                    shapeGroup.add(this.shapes.get(i));
                }
            }
        }

        return shapeGroup;
    }

    public <T extends Entity> ShapeGroup filterEntity(List<T> entities) {

        ShapeGroup shapeGroup = new ShapeGroup();

        for (int i = 0; i < this.shapes.size(); i++) {
            for (int j = 0; j < entities.size(); j++) {
                if (this.shapes.get(i).getEntity() != null && this.shapes.get(i).getEntity() == entities.get(j)) {
                    shapeGroup.add(this.shapes.get(i));
                }
            }
        }

        return shapeGroup;
    }

    /**
     * Filters shapes to those that are within the specified distance from the specified point.
     *
     * @param point
     * @param distance
     * @return
     */
    public ShapeGroup filterArea(Point point, double distance) {

        ShapeGroup shapeGroup = new ShapeGroup();

        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);

            double distanceToShape = Geometry.calculateDistance(
                    point,
                    shape.getPosition()
            );

            if (distanceToShape < distance) {
                shapeGroup.add(shape);
            }

        }

        return shapeGroup;

    }

    public ShapeGroup filterContains(Point point) {

        ShapeGroup shapeGroup = new ShapeGroup();

        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);

            if (shape.contains(point)) {
                shapeGroup.add(shape);
            }

        }

        return shapeGroup;

    }

    /**
     * Filters shapes that fall within the area defined by {@code shape}.
     *
     * @param shape The {@code Shape} covering the area to filter.
     * @return The {@code ShapeGroup} containing the area covered by {@code shape}.
     */
    public ShapeGroup filterArea(Shape shape) {

        ShapeGroup shapeGroup = new ShapeGroup();

        for (int i = 0; i < shapes.size(); i++) {
            Shape otherShape = shapes.get(i);
            if (shape.contains(otherShape.getPosition())) {
                shapeGroup.add(otherShape);
            }
        }

        return shapeGroup;
    }

    public ShapeGroup filterVisibility(Visibility visibility) {

        ShapeGroup shapeGroup = new ShapeGroup();

        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            if (shape.getVisibility() == visibility) {
                shapeGroup.add(shape);
            }

        }

        return shapeGroup;
    }

    public List<Shape> getList() {
        return shapes;
    }

    public List<Point> getPositions() {
        List<Point> positions = new LinkedList<>();
        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            positions.add(new Point(shape.getPosition().getX(), shape.getPosition().getY()));
        }
        return positions;
    }

    public List<Point> getVertices() {
        List<Point> positions = new LinkedList<>();
        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            positions.addAll(shape.getVertices());
        }
        return positions;
    }

    public Point getCenterPosition() {
        return Geometry.calculateCenterPosition(getPositions());
    }


    public Point getCentroidPosition() {
        return Geometry.calculateCentroidCoordinate(getPositions());
    }

    public Rectangle getBoundingBox() {
        return Geometry.calculateBoundingBox(getVertices());
    }

    public List<Point> getBoundingShape() {
        return Geometry.computeConvexHull(getPositions());
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

        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);

            double currentDistance = Geometry.calculateDistance(position, shape.getPosition());

            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                nearestShape = shape;
            }
        }

        return nearestShape;
    }

    public void setTransparency(double transparency) {
        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            shape.setTransparency(transparency);
        }
    }

    public void setVisibility(Visibility visibility) {
        for (int i = 0; i < shapes.size(); i++) {
            Shape shape = shapes.get(i);
            shape.setVisibility(visibility);
        }
    }

    public int size() {
        return this.shapes.size();
    }
}
