package camp.computer.clay.engine.component;

import java.util.List;

import camp.computer.clay.lib.ImageBuilder.Shape;

public class Renderable extends Component {

    /**
     * List of {@code Shape}s with absolute position and rotation.
     */
    public List<Shape> shapes;

    public Renderable() {
        super();
    }
}
