package camp.computer.clay.viz.arch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import camp.computer.clay.model.sim.Model;

public class Layer {

    private static int LAYER_COUNT = 0;

    private Viz viz;

    // TODO: Replace this with UUID
    // TODO: Add tags (can search by tags)
    private int index = -1;

    private String tag = "default";

    private HashMap<Model, Image> images = new HashMap<>();

    public Layer(Viz viz) {
        this.viz = viz;

        // Set the layer ID
        this.index = LAYER_COUNT;
        LAYER_COUNT++;
    }

    public int getIndex() {
        return this.index;
    }

    public String getTag() {
        return this.tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Viz getViz() {
        return this.viz;
    }

    public void add(Image image) {
        this.images.put(image.getModel(), image);
    }

    public <T extends Model> void add(T model, Image image) {
        this.images.put(model, image);
    }

    public void remove(Model model) {
        if (this.images.containsKey(model)) {
            this.images.remove(model);
        }
    }

    public Image getImage(Model model) {
        return this.images.get(model);
    }

    public Model getModel(Image image) {
        for (Model model : this.images.keySet()) {
            if (this.images.get(model) == image) {
                return model;
            }
        }
        return null;
    }

    public List<Image> getImages() {
        return new ArrayList<>(this.images.values());
    }
}
