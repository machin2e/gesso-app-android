package camp.computer.clay.engine.component;

import java.util.HashMap;

import camp.computer.clay.util.BuilderImage.BuilderImage;
import camp.computer.clay.engine.World;
import camp.computer.clay.util.BuilderImage.Shape;

public class Image extends Component {

    private BuilderImage builderImage = null;

    public void setImage(BuilderImage builderImage) {
        this.builderImage = builderImage;
    }

    public BuilderImage getImage() {
        return this.builderImage;
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
