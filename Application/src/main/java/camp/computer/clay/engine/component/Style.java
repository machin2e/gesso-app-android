package camp.computer.clay.engine.component;

import camp.computer.clay.engine.entity.Entity;

public class Style extends Component {
    // TODO: Consider renaming to "Appearance"

    public double targetTransparency = 1.0;

    public double transparency = targetTransparency;

    public Style() {
        super();
    }

    // <TODO:MOVE_TO_SYSTEM>
    // TODO: Delete?
    public static void setTransparency(Entity entity, final double transparency) {

        /*
//        Model image = entity.getComponent(Model.class);
//        List<Shape> shapes = image.getModel().getShapes();
        Group<Entity> shapes = Model.getShapes(entity);

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
        */
    }
    // </TODO:MOVE_TO_SYSTEM>
}
