package camp.computer.clay.engine.component;

import camp.computer.clay.lib.Geometry.Shape;

public class Primitive extends Component {

    // TODO: Move list of Shapes from ModelBuilder to this class.
    // TODO: Convert ModelBuilder to Renderable in a format that contains everything needed for rendering (e.g., a bitmap or composited image that can be drawn).

    public Shape shape;

    public Primitive() {
        super();
    }
}
