package camp.computer.clay.engine.component;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.util.ImageBuilder.ImageBuilder;

public class Image extends Component {

    private ImageBuilder imageBuilder = null;

    public List<Transform> boundary = new ArrayList<>();

    public void setImage(ImageBuilder imageBuilder) {
        this.imageBuilder = imageBuilder;

        // Compute Image-level bounding box
//        Group<Shape> shapes = new Group<>();
//        shapes.addAll(getImage().getShapes());
//        boundary.addAll(shapes.getBoundingBox().getVertices());

//        for (int i = 0; i < getImage().getShapes().size(); i++) {
//            //if (shapes.get(i).contains(point)) {
////            if (Geometry.contains(image.getImage().getShapes().get(i).getBoundary(), point)) {
//            getImage().getShapes().get(i).getBoundary()
//        }
    }

    public ImageBuilder getImage() {
        return this.imageBuilder;
    }

    public double targetTransparency = 1.0;

    public double transparency = targetTransparency;

    public Image() {
        super();
    }

    // <LAYER>
    public static final int DEFAULT_LAYER_INDEX = 0;

    public int layerIndex = DEFAULT_LAYER_INDEX;

    public int getLayerIndex() {
        return this.layerIndex;
    }

    public void setLayerIndex(int layerIndex) {
        this.layerIndex = layerIndex;
        updateLayers();
    }

    /**
     * Sorts {@code Image}s by layer.
     */
    public void updateLayers() {

        Group<Image> images = World.getWorld().Manager.getEntities().getImages();

        for (int i = 0; i < images.size() - 1; i++) {
            for (int j = i + 1; j < images.size(); j++) {
                // Check for out-of-order pairs, and swap them
                if (images.get(i).layerIndex > images.get(j).layerIndex) {
                    Image image = images.get(i);
                    images.set(i, images.get(j));
                    images.set(j, image);
                }
            }
        }

        /*
        // TODO: Sort using this after making Group implement List
        Collections.sort(Database.arrayList, new Comparator<MyObject>() {
            @Override
            public int compare(MyObject o1, MyObject o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        });
        */
    }
}
