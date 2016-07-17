package camp.computer.clay.visualization.arch;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.visualization.util.Geometry;
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

    public boolean remove (Image image) {
        return images.remove(image);
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
        for (int i = 0; ; i++) {
            if (!images.get(i).getType().equals(type)) {
                images.remove(i);

                if ((i + 1) == images.size()) {
                    break;
                }
            }
        }
        return this;
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

    public List<Image> getImages() {
        return this.images;
    }

    public ArrayList<PointF> getPositions() {
        ArrayList<PointF> positions = new ArrayList<PointF>();
        for (Image image: images) {
            positions.add(new PointF(image.getPosition().x, image.getPosition().y));
        }
        return positions;
    }

    public PointF calculateCenter() {
        return Geometry.calculateCenterPosition(getPositions());
    }

    public PointF calculateCentroid() {
        return Geometry.calculateCentroidPosition(getPositions());
    }

    public Rectangle calculateBoundingBox() {
        return Geometry.calculateBoundingBox(getPositions());
    }

    public ArrayList<PointF> computeConvexHull() {
        return Geometry.computeConvexHull(getPositions());
    }

    /**
     * Finds and returns the nearest <em>visible</em> <code>Image</code>.
     * @param position
     * @return
     */
    public Image getNearestImage (PointF position) {

        float shortestDistance = Float.MAX_VALUE;
        Image nearestImage = null;

        for (Image image: getImages()) {

            float currentDistance = Geometry.calculateDistance(position, image.getPosition());

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
