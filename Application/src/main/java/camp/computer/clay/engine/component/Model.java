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
        Entity primitiveEntity = World.getWorld().createEntity(Primitive.class);
        primitiveEntity.getComponent(Primitive.class).shape = shape;

        primitiveEntity.getComponent(TransformConstraint.class).setReferenceEntity(entity);

        // Add Shape entity to Model component
        entity.getComponent(Model.class).shapes.add(primitiveEntity.getUuid());

        return primitiveEntity.getUuid();
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
