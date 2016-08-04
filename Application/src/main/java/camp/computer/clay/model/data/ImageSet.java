package camp.computer.clay.model.data;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.model.sim.Model;
import camp.computer.clay.viz.arch.Image;
import camp.computer.clay.viz.arch.Visibility;
import camp.computer.clay.viz.util.Geometry;
import camp.computer.clay.viz.util.Point;
import camp.computer.clay.viz.util.Rectangle;

/**
 * ImageSet is an interface for managing and manaipulating sets of images.
 */
public class ImageSet {

    private List<Image> images = new ArrayList<>();

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
     * @param type
     * @return
     */
    public ImageSet old_filterType(String type) {
        ImageSet imageSet = new ImageSet();
        for (int i = 0; i < this.images.size(); i++) {
            if (this.images.get(i).isType(type)) {
                imageSet.add(this.images.get(i));
            }
        }
        return imageSet;
    }

    public <T extends Model> ImageSet filterType(Class<T> type) {
        ImageSet imageSet = new ImageSet();
        for (int i = 0; i < this.images.size(); i++) {
            if (this.images.get(i).getModel().getClass() == type) {
                imageSet.add(this.images.get(i));
            }
        }
        return imageSet;
    }

    public <T extends Model> List<T> getModels(Class<T> type) {
        List models = new ArrayList<T>();
        for (int i = 0; i < this.images.size(); i++) {
            if (this.images.get(i).getModel().getClass() == type) {
                models.add(this.images.get(i).getModel());
            }
        }
        return models;
    }

//    /**
//     * References:
//     * - http://stackoverflow.com/questions/450807/how-do-i-make-the-method-return-type-generic?rq=1
//     *
//     * @param type
//     * @param classType
//     * @param <T>
//     * @return
//     */
//    public <T extends Image> List<T> filterType (String type, Class<T> classType) {
//        List<T> imageGroup = new ArrayList<>();
//        for (int i = 0; i < images.size(); i++) {
//            if (images.get(i).getType().equals(type)) {
//                imageGroup.add((T) images.get(i));
//            }
//        }
//        return imageGroup;
//    }

    /**
     * Filters images to those that are within the specified distance from the specified point.
     *
     * @param position
     * @param distance
     * @return
     */
    public ImageSet filterDistance(Point position, double distance) {
        ImageSet imageSet = new ImageSet();
        for (int i = 0; i < images.size(); i++) {

            double distanceToImage = Geometry.calculateDistance(
                    position,
                    images.get(i).getPosition()
            );

            if (distanceToImage < distance) {
                imageSet.add(images.get(i));
            }

        }
        return imageSet;
    }

    public ImageSet filterVisibility(Visibility visibility) {

        ImageSet imageSet = new ImageSet();
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i).getVisibility() == visibility) {
                imageSet.add(images.get(i));
            }
        }
        return imageSet;
    }

    public List<Image> getList() {
        return images;
    }

    public List<Point> getPositions() {
        List<Point> positions = new ArrayList<>();
        for (Image image : images) {
            positions.add(new Point(image.getPosition().getX(), image.getPosition().getY()));
        }
        return positions;
    }

    public Point calculateCenter() {
        return Geometry.calculateCenter(getPositions());
    }

    public Point calculateCentroid() {
        return Geometry.calculateCentroid(getPositions());
    }

    public Rectangle calculateBoundingBox() {
        return Geometry.calculateBoundingBox(getPositions());
    }

    public List<Point> computeConvexHull() {
        return Geometry.computeConvexHull(getPositions());
    }

    /**
     * Finds and returns the nearest <em>visible</em> <code>Image</code>.
     *
     * @param point
     * @return
     */
    public Image getNearest(Point point) {

        double shortestDistance = Float.MAX_VALUE;
        Image nearestImage = null;

        for (Image image : images) {

            double currentDistance = Geometry.calculateDistance(point, image.getPosition());

            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                nearestImage = image;
            }
        }

        return nearestImage;
    }

    public Image getAt(Point point) {
        for (Image image : filterVisibility(Visibility.VISIBLE).getList()) {
            if (image.isTouching(point)) {
                return image;
            }
        }
        return null;
    }

    public void setVisibility(Visibility visibility) {
        for (Image image : images) {
            image.setVisibility(visibility);
        }
    }

    // TODO: setVisibility()

    // TODO: getPoints(set)

    // TODO: Geometry.getBoundingBox(set)

    // TODO: Geometry.getConvexHull(set)
}
