package camp.computer.clay.space.architecture;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.model.architecture.Entity;
import camp.computer.clay.model.architecture.Group;
import camp.computer.clay.space.util.Visibility;
import camp.computer.clay.space.util.geometry.Geometry;
import camp.computer.clay.space.util.geometry.Point;
import camp.computer.clay.space.util.geometry.Rectangle;

/**
 * ImageGroup is an interface for managing and manipulating sets of elements.
 */
public class ImageGroup extends Group<Image> {

    public ImageGroup() {
    }

    // TODO: Move this into the Group base class
    public ImageGroup remove(Image image) {
        elements.remove(image);
        return this;
    }

    /**
     * Removes all elements except those with the specified type.
     *
     * @param types
     * @return
     */
    public <T extends Entity> ImageGroup filterType(Class<?>... types) {

        ImageGroup imageGroup = new ImageGroup();

        for (int i = 0; i < this.elements.size(); i++) {
            for (int j = 0; j < types.length; j++) {
                Class<?> type = types[j];
                if (this.elements.get(i).getEntity().getClass() == type) {
                    imageGroup.add(this.elements.get(i));
                }
            }
        }

        return imageGroup;
    }

    public <T extends Entity> ImageGroup filterEntity(List<T> entities) {

        ImageGroup imageGroup = new ImageGroup();

        for (int i = 0; i < this.elements.size(); i++) {
            for (int j = 0; j < entities.size(); j++) {
                if (this.elements.get(i).getEntity() != null && this.elements.get(i).getEntity() == entities.get(j)) {
                    imageGroup.add(this.elements.get(i));
                }
            }
        }

        return imageGroup;
    }

    /**
     * Filters elements to those that are within the specified distance from the specified point.
     *
     * @param point
     * @param distance
     * @return
     */
    public ImageGroup filterArea(Point point, double distance) {

        ImageGroup imageGroup = new ImageGroup();

        for (int i = 0; i < elements.size(); i++) {

            Image image = elements.get(i);

            double distanceToImage = Geometry.calculateDistance(
                    point,
                    image.getPosition()
            );

            if (distanceToImage < distance) {
                imageGroup.add(image);
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
    public ImageGroup filterArea(Shape shape) {

        ImageGroup imageGroup = new ImageGroup();

        for (int i = 0; i < elements.size(); i++) {
            Image image = elements.get(i);
            if (shape.contains(image.getPosition())) {
                imageGroup.add(image);
            }
        }

        return imageGroup;
    }

    public ImageGroup filterVisibility(Visibility visibility) {

        ImageGroup imageGroup = new ImageGroup();

        for (int i = 0; i < elements.size(); i++) {
            Image image = elements.get(i);
            if (image.getVisibility() == visibility) {
                imageGroup.add(image);
            }

        }

        return imageGroup;
    }

    public List<Image> getList() {
        return elements;
    }

    public List<Point> getPositions() {
        List<Point> positions = new LinkedList<>();
        for (int i = 0; i < elements.size(); i++) {
            Image image = elements.get(i);
            positions.add(new Point(image.getPosition().getX(), image.getPosition().getY()));
        }
        return positions;
    }

    public List<Point> getVertices() {
        List<Point> positions = new LinkedList<>();
        for (int i = 0; i < elements.size(); i++) {
            Image image = elements.get(i);
            positions.addAll(image.getAbsoluteVertices());
        }
        return positions;
    }

    public ShapeGroup getShapes() {
        ShapeGroup shapeGroup = new ShapeGroup();

        for (int i = 0; i < this.elements.size(); i++) {
            shapeGroup.add(this.elements.get(i).getShapes());
        }

        return shapeGroup;
    }

    public Point getCenterPoint() {
        return Geometry.calculateCenterPosition(getPositions());
    }

    public Point getCentroidPoint() {
        return Geometry.calculateCentroidCoordinate(getPositions());
    }

    public Rectangle getBoundingBox() {
        return Geometry.calculateBoundingBox(getVertices());
    }

    public List<Point> getBoundingShape() {
        return Geometry.computeConvexHull(getPositions());
    }

    /**
     * Finds and returns the nearest <em>visible</em> <code>Image</code>.
     *
     * @param position
     * @return
     */
    public Image getNearest(Point position) {

        double shortestDistance = Float.MAX_VALUE;
        Image nearestImage = null;

        for (int i = 0; i < elements.size(); i++) {
            Image image = elements.get(i);

            double currentDistance = Geometry.calculateDistance(position, image.getPosition());

            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                nearestImage = image;
            }
        }

        return nearestImage;
    }

    public void setTransparency(double transparency) {
        for (int i = 0; i < elements.size(); i++) {
            Image image = elements.get(i);
            image.setTransparency(transparency);
        }
    }

    public void setVisibility(Visibility visibility) {
        for (int i = 0; i < elements.size(); i++) {
            Image image = elements.get(i);
            image.setVisibility(visibility);
        }
    }
}