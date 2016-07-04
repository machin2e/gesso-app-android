package camp.computer.clay.sprite;

import java.util.ArrayList;
import java.util.HashMap;

import camp.computer.clay.model.simulation.Model;

public class Layer {

    private Visualization visualization;

    private HashMap<Model, Sprite> sprites = new HashMap<Model, Sprite>();

    public Layer(Visualization visualization) {
        this.visualization = visualization;
    }

    public Visualization getVisualization() {
        return this.visualization;
    }

    public void addSprite(Model model, Sprite sprite) {
        this.sprites.put(model, sprite);
    }

    public void removeSprite(Model model, Sprite sprite) {
        if (this.sprites.containsKey(model)) {
            this.sprites.remove(model);
        }
    }

    public Sprite getSprite(Model model) {
        return this.sprites.get(model);
    }

    public Model getModel(Sprite sprite) {
        for (Model model: this.sprites.keySet()) {
            if (this.sprites.get(model) == sprite) {
                return model;
            }
        }
        return null;
    }

    public ArrayList<Sprite> getSprites() {
        return new ArrayList<Sprite>(this.sprites.values());
    }

    public int getCardinality() {
        return this.sprites.size();
    }
}
