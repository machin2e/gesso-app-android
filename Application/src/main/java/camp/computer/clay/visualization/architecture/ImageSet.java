package camp.computer.clay.visualization.architecture;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.visualization.util.Visibility;
import camp.computer.clay.visualization.util.geometry.Geometry;
import camp.computer.clay.visualization.util.geometry.Point;
import camp.computer.clay.visualization.util.geometry.Rectangle;

/**
 * ImageSet is an interface for managing and manipulating sets of images.
 */
public class ImageSet {

    private List<Image> images = new LinkedList<>();

    public ImageSet() {
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

    public ImageSet remove(Image image) {
        images.remove(image);
        return this;
    }

    public Image get(int index) {
        return images.get(index);
    }

    /**
     * Removes all elements except those with the specified type.
     *
     * @param types
     * @return
     */
    public <T extends Image> ImageSet filterType(Class<?>... types) {

        ImageSet imageSet = new ImageSet();

        for (int i = 0; i < this.images.size(); i++) {
            for (Class<?> type : types) {
                if (this.images.get(i).getClass() == type) {
                    imageSet.add(this.images.get(i));
                }
            }
        }

        return imageSet;
    }

    /**
     * Filters images to those that are within the specified distance from the specified point.
     *
     * @param point
     * @param distance
     * @return
     */
    public ImageSet filterProximity(Point point, double distance) {

        ImageSet imageSet = new ImageSet();

        for (int i = 0; i < images.size(); i++) {

            Image image = images.get(i);

            double distanceToImage = Geometry.calculateDistance(
                    point,
                    image.getPosition()
            );

            if (distanceToImage < distance) {
                imageSet.add(image);
            }

        }

        return imageSet;

    }

    public ImageSet filterVisibility(Visibility visibility) {

        ImageSet imageSet = new ImageSet();
        for (int i = 0; i < images.size(); i++) {

            Image image = images.get(i);

            if (image.getVisibility() == visibility) {
                imageSet.add(image);
            }

        }

        return imageSet;
    }

    public List<Image> getList() {
        return images;
    }

    public List<Point> getPositions() {
        List<Point> positions = new LinkedList<>();
        for (Image image : images) {
            positions.add(new Point(image.getPosition().getX(), image.getPosition().getY()));
        }
        return positions;
    }

    public Point getCenterPoint() {
        return Geometry.calculateCenterPosition(getPositions());
    }

    public Point getCentroidPoint() {
        return Geometry.calculateCentroidPosition(getPositions());
    }

    public Rectangle getBoundingBox() {
        return Geometry.calculateBoundingBox(getPositions());
    }

    public List<Point> getBoundingPolygon() {
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

        for (Image image : images) {

            double currentDistance = Geometry.calculateDistance(position, image.getPosition());

            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                nearestImage = image;
            }
        }

        return nearestImage;
    }
}
