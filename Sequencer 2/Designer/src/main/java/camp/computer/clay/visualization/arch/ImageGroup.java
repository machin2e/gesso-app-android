package camp.computer.clay.visualization.arch;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.PointHolder;
import camp.computer.clay.visualization.util.Rectangle;

/**
 * ImageGroup is an interface for managing and manaipulating sets of images.
 */
public class ImageGroup {

    private ArrayList<Image> images = new ArrayList<>();

    public ImageGroup() {
    }

    public void add (Image image) {
        this.images.add(image);
    }

    public void add (List<Image> images) {
        this.images.addAll(images);
    }

    public boolean contains (Image image) {
        return images.contains(image);
    }

    public ImageGroup remove (Image image) {
        images.remove(image);
        return this;
    }

    public Image get (int index) {
        return images.get(index);
    }

    /**
     * Removes all elements except those with the specified type.
     * @param type
     * @return
     */
    public ImageGroup filterType (String type) {
//        for (int i = 0; i < images.size(); i++) {
//            Image image = images.get(0);
//            if (images.remove(image)) {
//                if (image.isType(type)) {
//                    images.add(image);
//                }
//            }
//        }
//        return this;

        ImageGroup imageGroup = new ImageGroup();

        for (int i = 0; i < this.images.size(); i++) {

            if (this.images.get(i).isType(type)) {
                imageGroup.add(this.images.get(i));
            }

        }

        return imageGroup;
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
//    public <T extends Image> List<T> filterType2 (String type, Class<T> classType) {
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
    public ImageGroup filterDistance(PointHolder position, double distance) {

        ImageGroup imageGroup = new ImageGroup();

        for (int i = 0; i < images.size(); i++) {

//            Image image = images.remove(0);

            double distanceToImage = Geometry.calculateDistance(
                    position,
//                    image.getPosition()
                    images.get(i).getPosition()
            );

            if (distanceToImage < distance) {

//                images.add(image);
                imageGroup.add(images.get(i));

            }
        }

//        this.images = imageGroup;

        return imageGroup;

    }

    public List<Image> getList() {
        return images;
    }

    public ArrayList<PointHolder> getPositions() {
        ArrayList<PointHolder> positions = new ArrayList<PointHolder>();
        for (Image image: images) {
            positions.add(new PointHolder(image.getPosition().getX(), image.getPosition().getY()));
        }
        return positions;
    }

    public PointHolder calculateCenter() {
        return Geometry.calculateCenterPosition(getPositions());
    }

    public PointHolder calculateCentroid() {
        return Geometry.calculateCentroidPosition(getPositions());
    }

    public Rectangle calculateBoundingBox() {
        return Geometry.calculateBoundingBox(getPositions());
    }

    public ArrayList<PointHolder> computeConvexHull() {
        return Geometry.computeConvexHull(getPositions());
    }

    /**
     * Finds and returns the nearest <em>visible</em> <code>Image</code>.
     * @param position
     * @return
     */
    public Image getNearestImage (PointHolder position) {

        double shortestDistance = Float.MAX_VALUE;
        Image nearestImage = null;

        for (Image image: images) {

            double currentDistance = Geometry.calculateDistance(position, image.getPosition());

            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                nearestImage = image;
            }
        }

        return nearestImage;
    }

    // TODO: setVisibility()

    // TODO: getPoints(set)

    // TODO: Geometry.getBoundingBox(set)

    // TODO: Geometry.getConvexHull(set)
}
