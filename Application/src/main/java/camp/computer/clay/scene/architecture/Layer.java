package camp.computer.clay.scene.architecture;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.model.architecture.Construct;

public class Layer {

    private static int LAYER_ID_COUNT = 0;

    private Scene scene;

    // TODO: Replace this with UUID
    // TODO: Add tags (can search by tags)
    private int id = -1;

    private String tag = "default";

    private List<Figure> figures = new LinkedList<>();

    public Layer(Scene scene) {
        this.scene = scene;

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

    public Scene getScene() {
        return this.scene;
    }

    public void add(Figure figure) {
        figures.add(figure);
        figure.setScene(scene);
    }

    public Figure getFigure(Construct construct) {
        for (int i = 0; i < figures.size(); i++) {
            Figure figure = figures.get(i);
            if (figure.getConstruct() == construct) {
                return figure;
            }
        }
        return null;
    }

    public Construct getModel(Figure figure) {
        for (int i = 0; i < figures.size(); i++) {
            if (figures.get(i) == figure) {
                return figures.get(i).getConstruct();
            }
        }
        return null;
    }

    public List<Figure> getFigures() {
        return figures;
    }

    public int getCardinality() {
        return this.figures.size();
    }
}
