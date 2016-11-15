package camp.computer.clay.engine.component;

import camp.computer.clay.util.ImageBuilder.ImageBuilder;

public class Image extends Component {

    public ImageBuilder image = null;

    public Image() {
        super();
    }

    // <LAYER>
    public static final int DEFAULT_LAYER_INDEX = 0;

    public int layerIndex = DEFAULT_LAYER_INDEX;
    // </LAYER>

    public void setImage(ImageBuilder imageBuilder) {
        this.image = imageBuilder;
    }

    public ImageBuilder getImage() {
        return this.image;
    }
}
