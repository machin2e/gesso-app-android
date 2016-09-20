package camp.computer.clay.space.architecture;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.model.architecture.Entity;
import camp.computer.clay.space.util.Visibility;
import camp.computer.clay.space.util.geometry.Geometry;
import camp.computer.clay.space.util.geometry.Point;
import camp.computer.clay.space.util.geometry.Rectangle;

/**
 * ImageGroup is an interface for managing and manipulating sets of images.
 */
public class ImageGroup {

    private List<Image> images = new LinkedList<>();

    public ImageGroup() {
    }

    public void add(Image image) {
        this.images.add(image);
    }

    public void add(List<Image> images) {
        this.images.addAll(images);
    }

    public boolean contains(Image image) {
        return images.contains(image);
    }

    public ImageGroup remove(Image image) {
        images.remove(image);
        return this;
    }

    public Image get(int index) {
        return images.get(index);
    }

    public Image getFirst() {
        if (images.size() > 0) {
            return images.get(0);
        }
        return null;
    }

    public Image getLast() {
        if (images.size() > 0) {
            return images.get(images.size() - 1);
        }
        return null;
    }

    public int size() {
        return this.images.size();
    }

    /**
     * Removes all elements except those with the specified type.
     *
     * @param types
     * @return
     */
    public <T extends Entity> ImageGroup filterType(Class<?>... types) {

        ImageGroup imageGroup = new ImageGroup();

        for (int i = 0; i < this.images.size(); i++) {
            for (int j = 0; j < types.length; j++) {
                Class<?> type = types[j];
                if (this.images.get(i).getEntity().getClass() == type) {
                    imageGroup.add(this.images.get(i));
                }
            }
        }

        return imageGroup;
    }

    public <T extends Entity> ImageGroup filterEntity(List<T> entities) {

        ImageGroup imageGroup = new ImageGroup();

        for (int i = 0; i < this.images.size(); i++) {
            for (int j = 0; j < entities.size(); j++) {
                if (this.images.get(i).getEntity() != null && this.images.get(i).getEntity() == entities.get(j)) {
                    imageGroup.add(this.images.get(i));
                }
            }
        }

        return imageGroup;
    }

    /**
     * Filters images to those that are within the specified distance from the specified point.
     *
     * @param point
     * @param distance
     * @return
     */
    public ImageGroup filterArea(Point point, double distance) {

        ImageGroup imageGroup = new ImageGroup();

        for (int i = 0; i < images.size(); i++) {

            Image image = images.get(i);

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
     * Filters images that fall within the area defined by {@code shape}.
     *
     * @param shape The {@code Shape} covering the area to filter.
     * @return The {@code ImageGroup} containing the area covered by {@code shape}.
     */
    public ImageGroup filterArea(Shape shape) {

        ImageGroup imageGroup = new ImageGroup();

        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            if (shape.contains(image.getPosition())) {
                imageGroup.add(image);
            }
        }

        return imageGroup;
    }

    public ImageGroup filterVisibility(Visibility visibility) {

        ImageGroup imageGroup = new ImageGroup();

        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            if (image.getVisibility() == visibility) {
                imageGroup.add(image);
            }

        }

        return imageGroup;
    }

    public List<Image> getList() {
        return images;
    }

    public List<Point> getPositions() {
        List<Point> positions = new LinkedList<>();
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            positions.add(new Point(image.getPosition().getX(), image.getPosition().getY()));
        }
        return positions;
    }

    public List<Point> getVertices() {
        List<Point> positions = new LinkedList<>();
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            positions.addAll(image.getAbsoluteVertices());
        }
        return positions;
    }

    public ShapeGroup getShapes() {
        ShapeGroup shapeGroup = new ShapeGroup();

        for (int i = 0; i < this.images.size(); i++) {
            shapeGroup.add(this.images.get(i).getShapes());
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

        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);

            double currentDistance = Geometry.calculateDistance(position, image.getPosition());

            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                nearestImage = image;
            }
        }

        return nearestImage;
    }

    public void setTransparency(double transparency) {
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            image.setTransparency(transparency);
        }
    }

    public void setVisibility(Visibility visibility) {
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            image.setVisibility(visibility);
        }
    }
}
