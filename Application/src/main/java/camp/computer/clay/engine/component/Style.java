package camp.computer.clay.engine.component;

import java.util.List;

import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.util.Color;
import camp.computer.clay.util.ImageBuilder.Shape;

public class Style extends Component {
    // TODO: Consider renaming to "Appearance"

    public double targetTransparency = 1.0;

    public double transparency = targetTransparency;

    public Style() {
        super();
    }

    // <TODO:MOVE_TO_SYSTEM>
    // TODO: Delete?
    public void setTransparency(Entity entity, final double transparency) {

//        if (image.getImage() == null) {
//            image.setImage(new ImageBuilder());
//        }

        Image image = entity.getComponent(Image.class);
        List<Shape> shapes = image.getImage().getShapes();

        entity.getComponent(Style.class).targetTransparency = transparency;

        for (int i = 0; i < shapes.size(); i++) {

            Shape shape = shapes.get(i);

            // Color
            int intColor = android.graphics.Color.parseColor(shapes.get(i).getColor());
            intColor = Color.setTransparency(intColor, entity.getComponent(Style.class).targetTransparency);
            shape.setColor(Color.getHexColorString(intColor));

            // Outline Color
            int outlineColorIndex = android.graphics.Color.parseColor(shapes.get(i).getOutlineColor());
            outlineColorIndex = Color.setTransparency(outlineColorIndex, entity.getComponent(Style.class).targetTransparency);
            shape.setOutlineColor(Color.getHexColorString(outlineColorIndex));
        }

        entity.getComponent(Style.class).transparency = entity.getComponent(Style.class).targetTransparency;
    }
    // </TODO:MOVE_TO_SYSTEM>
}
