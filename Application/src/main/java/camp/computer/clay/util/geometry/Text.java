package camp.computer.clay.util.geometry;

import java.util.List;

import camp.computer.clay.application.graphics.PlatformRenderSurface;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.util.image.Shape;

public class Text extends Shape {

    protected String text = "";

    public Text() {
    }

    @Override
    public List<Transform> getVertices() {
        return null;
    }

    @Override
    public void draw(PlatformRenderSurface platformRenderSurface) {

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
