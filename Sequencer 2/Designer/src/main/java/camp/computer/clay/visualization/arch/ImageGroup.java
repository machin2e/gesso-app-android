package camp.computer.clay.visualization.arch;

import android.graphics.PointF;

import java.util.ArrayList;

/**
 * ImageGroup is an interface for managing and manaipulating sets of images.
 */
public class ImageGroup {

    private ArrayList<Image> images = new ArrayList<Image>();

    public ImageGroup() {
    }

    public void add (Image image) {
        this.images.add(image);
    }

    public boolean contains (Image image) {
        return this.images.contains(image);
    }

    public void remove (Image image) {
        if (this.images.contains(image)) {
            this.images.remove(image);
        }
    }

    public Image get (int index) {
        return this.images.get(index);
    }

    /**
     * Removes all elements except those with the specified type.
     * @param type
     * @return
     */
    public ImageGroup filterType (String type) {
        for (int i = 0; ; i++) {
            if (!this.images.get(i).getType().equals(type)) {
                this.images.remove(i);

                if ((i + 1) == images.size()) {
                    break;
                }
            }
        }
        return this;
    }

    public ArrayList<PointF> getPositions() {
        ArrayList<PointF> positions = new ArrayList<PointF>();
        for (Image image: images) {
            positions.add(new PointF(image.getPosition().x, image.getPosition().y));
        }
        return positions;
    }

    // TODO: setVisibility()

    // TODO: getPoints(set)

    // TODO: Geometry.getBoundingBox(set)

    // TODO: Geometry.getConvexHull(set)
}
