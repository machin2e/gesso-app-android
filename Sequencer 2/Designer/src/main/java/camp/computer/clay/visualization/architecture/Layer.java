package camp.computer.clay.visualization.architecture;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import camp.computer.clay.model.simulation.Model;

public class Layer {

    private static int LAYER_ID_COUNT = 0;

    private Visualization visualization;

    // TODO: Replace this with UUID
    // TODO: Add tags (can search by tags)
    private int id = -1;

    private ConcurrentHashMap<Model, Image> images = new ConcurrentHashMap<Model, Image>();

    public Layer(Visualization visualization) {
        this.visualization = visualization;

        // Set the layer ID
        this.id = LAYER_ID_COUNT;
        LAYER_ID_COUNT++;
    }

    public int getId() {
        return this.id;
    }

    public Visualization getVisualization() {
        return this.visualization;
    }

    public void add(Model model, Image image) {
        this.images.put(model, image);
    }

    public void removeImage(Model model, Image image) {
        if (this.images.containsKey(model)) {
            this.images.remove(model);
        }
    }

    public Image getImage2(Model model) {
        return this.images.get(model);
    }

    public Model getModel2(Image image) {
        for (Model model: this.images.keySet()) {
            if (this.images.get(model) == image) {
                return model;
            }
        }
        return null;
    }

    public ArrayList<Image> getImages() {
        return new ArrayList<Image>(this.images.values());
    }

    public int getCardinality() {
        return this.images.size();
    }
}
