package camp.computer.clay.engine.component;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.util.ImageBuilder.ImageBuilder;
import camp.computer.clay.engine.World;

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
        World.getWorld().updateLayers();
    }
}
