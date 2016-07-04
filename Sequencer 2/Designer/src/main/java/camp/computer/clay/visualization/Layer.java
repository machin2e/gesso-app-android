package camp.computer.clay.visualization;

import java.util.ArrayList;
import java.util.HashMap;

import camp.computer.clay.model.simulation.Model;

public class Layer {

    private Visualization visualization;

    private HashMap<Model, Image> sprites = new HashMap<Model, Image>();

    public Layer(Visualization visualization) {
        this.visualization = visualization;
    }

    public Visualization getVisualization() {
        return this.visualization;
    }

    public void addSprite(Model model, Image image) {
        this.sprites.put(model, image);
    }

    public void removeSprite(Model model, Image image) {
        if (this.sprites.containsKey(model)) {
            this.sprites.remove(model);
        }
    }

    public Image getSprite(Model model) {
        return this.sprites.get(model);
    }

    public Model getModel(Image image) {
        for (Model model: this.sprites.keySet()) {
            if (this.sprites.get(model) == image) {
                return model;
            }
        }
        return null;
    }

    public ArrayList<Image> getSprites() {
        return new ArrayList<Image>(this.sprites.values());
    }

    public int getCardinality() {
        return this.sprites.size();
    }
}
