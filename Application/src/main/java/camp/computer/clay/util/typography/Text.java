package camp.computer.clay.util.typography;

import java.util.List;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.geometry.Point;

public class Text extends Shape {

    protected String text = "";

    public Text() {
    }

    @Override
    protected List<Point> getVertices() {
        return null;
    }

    @Override
    public void draw(Display display) {

    }

    public Text(Point position) {
        super(position);
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
