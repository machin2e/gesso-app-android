package camp.computer.clay.util.geometry;

import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.util.image.Shape;

public class Text extends Shape {

    protected String text = "";

    public Text() {
    }

    @Override
    protected List<Transform> getVertices() {
        return null;
    }

    @Override
    public void draw(Display display) {

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