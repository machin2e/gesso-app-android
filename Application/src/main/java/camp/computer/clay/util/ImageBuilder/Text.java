package camp.computer.clay.util.ImageBuilder;

import java.util.List;

import camp.computer.clay.engine.component.Transform;

public class Text extends Shape {

    protected String text = "";

    public String font = null;
    public double size = 12;

    public Text() {
    }

    @Override
    public List<Transform> getVertices() {
        return null;
    }

    public Text(Transform position) {
        super(position);
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
