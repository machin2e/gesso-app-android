package camp.computer.clay.scene.architecture;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.model.architecture.Feature;
import camp.computer.clay.scene.util.Visibility;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Rectangle;
import camp.computer.clay.scene.util.geometry.Shape;

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

    /**
     * Removes all elements except those with the specified type.
     *
     * @param types
     * @return
     */
    public <T extends Feature> ImageGroup filterType(Class<?>... types) {

        ImageGroup imageGroup = new ImageGroup();

        for (int i = 0; i < this.images.size(); i++) {
            for (int j = 0; j < types.length; j++) {
                Class<?> type = types[j];
                //for (Class<?> type : types) {
                //if (this.images.getEvent(i).getClass() == type) {
                if (this.images.get(i).getFeature().getClass() == type) {
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

    public List<Point> getCoordinates() {
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

    public Point getCenterPoint() {
        return Geometry.calculateCenterCoordinate(getCoordinates());
    }

    public Point getCentroidPoint() {
        return Geometry.calculateCentroidCoordinate(getCoordinates());
    }

    public Rectangle getBoundingBox() {
        return Geometry.calculateBoundingBox(getVertices());
    }

    public List<Point> getBoundingShape() {
        return Geometry.computeConvexHull(getCoordinates());
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
