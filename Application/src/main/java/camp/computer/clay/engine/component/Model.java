package camp.computer.clay.engine.component;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Group;
import camp.computer.clay.lib.Geometry.Shape;

public class Model extends Component {

    public List<Long> shapes;

    public Model() {
        super();
        setup();
    }

    private void setup() {
        shapes = new ArrayList<>();
    }

    // <LAYER>
    public static final int DEFAULT_LAYER_INDEX = 0;

    public int layerIndex = DEFAULT_LAYER_INDEX;
    // </LAYER>

    public static long addShape(Entity entity, Shape shape) {

        // Create Shape entity and assign shape to it
        Entity shapeEntity = World.getWorld().createEntity(Primitive.class);
        shapeEntity.getComponent(Primitive.class).shape = shape;

        shapeEntity.getComponent(RelativeLayoutConstraint.class).setReferenceEntity(entity);

//        shapeEntity.getComponent(RelativeLayoutConstraint.class).relativeTransform.set(shape.getPosition());
//        shapeEntity.getComponent(RelativeLayoutConstraint.class).relativeTransform.setRotation(shape.getRotation());

//        shapeEntity.getComponent(Transform.class).rotation = shape.getRotation();

        // Add Shape entity to Model component
        entity.getComponent(Model.class).shapes.add(shapeEntity.getUuid());

        return shapeEntity.getUuid();
    }

    public static Entity getShape(Entity entity, String label) {
        List<Long> shapeUuids = entity.getComponent(Model.class).shapes;
        for (int i = 0; i < shapeUuids.size(); i++) {
            Entity shape = World.getWorld().entities.get(shapeUuids.get(i));
            if (Label.getLabel(shape).equals(label)) {
                return shape;
            }
        }
        return null;
    }

//    public static Entity getShape(Entity entity, Shape shape) {
//        List<Long> shapeUuids = entity.getComponent(Model.class).shapes;
//        for (int i = 0; i < shapeUuids.size(); i++) {
//            Entity shapeEntity = World.getWorld().entities.get(shapeUuids.get(i));
//            if (shapeEntity.getComponent(Primitive.class).shape == shape) {
//                return shapeEntity;
//            }
//        }
//        return null;
//    }

    public static Group<Entity> getShapes(Entity entity) {
        if (entity.getComponent(Model.class) == null) {
            Log.v("Gotcha", "Gotcha");
        }
        List<Long> shapeUuids = entity.getComponent(Model.class).shapes;
        Group<Entity> shapes = new Group<>();
        for (int i = 0; i < shapeUuids.size(); i++) {
            Entity shape = World.getWorld().entities.get(shapeUuids.get(i));
            shapes.add(shape);
        }
        return shapes;
    }

    public static Group<Entity> getShapes(Entity entity, String... labels) {

        Group<Entity> shapes = Model.getShapes(entity);

        Group<Entity> matchingShapes = new Group<>();

        for (int i = 0; i < shapes.size(); i++) {
            for (int j = 0; j < labels.length; j++) {
                Pattern pattern = Pattern.compile(labels[j]);
                Matcher matcher = pattern.matcher(Label.getLabel(shapes.get(i)));
                if (matcher.matches()) {
                    matchingShapes.add(shapes.get(i));
                }
            }
        }

        return matchingShapes;
    }
}
