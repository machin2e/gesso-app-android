package camp.computer.clay.visualization.architecture;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.model.architecture.Construct;

public class Layer {

    private static int LAYER_ID_COUNT = 0;

    private Visualization visualization;

    // TODO: Replace this with UUID
    // TODO: Add tags (can search by tags)
    private int id = -1;

    private String tag = "default";

    private List<Image> images = new LinkedList<>();

    public Layer(Visualization visualization) {
        this.visualization = visualization;

        // Set the layer ID
        this.id = LAYER_ID_COUNT;
        LAYER_ID_COUNT++;
    }

    public int getIndex() {
        return this.id;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Visualization getVisualization() {
        return this.visualization;
    }

    public void add(Image image) {
        images.add(image);
        image.setVisualization(visualization);
    }

    public Image getImage(Construct construct) {
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);
            if (image.getConstruct() == construct) {
                return image;
            }
        }
        return null;
    }

    public Construct getModel(Image image) {
        for (int i = 0; i < images.size(); i++) {
            if (images.get(i) == image) {
                return images.get(i).getConstruct();
            }
        }
        return null;
    }

    public List<Image> getImages() {
        return images;
    }

    public int getCardinality() {
        return this.images.size();
    }
}
