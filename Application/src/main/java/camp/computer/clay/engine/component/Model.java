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

    // TODO: Add support for multiple geometry configurations.
    public long meshIndex = 0;

    // <REFACTOR>
    // TODO: Replace with AssetReference
    public List<Long> primitives;
    // </REFACTOR>

    public Model() {
        super();
        setup();
    }

    private void setup() {
        primitives = new ArrayList<>();
    }

    // <LAYER>
    public static final int DEFAULT_LAYER_INDEX = 0;

    public int layerIndex = DEFAULT_LAYER_INDEX;
    // </LAYER>

    public static Entity addShape(Entity entity, Shape shape) {

        // Create Shape entity and assign shape to it
        Entity primitiveEntity = World.getWorld().createEntity(Primitive.class);
        primitiveEntity.getComponent(Primitive.class).shape = shape;

        primitiveEntity.getComponent(TransformConstraint.class).setReferenceEntity(entity);

        // Add Shape entity to Model component
        entity.getComponent(Model.class).primitives.add(primitiveEntity.getUuid());

        return primitiveEntity;
    }

    // HACK: This is ridiculously expensive if you unpack it...
    public static Entity getPrimitive(Entity entity, String label) {
        List<Long> shapeUuids = entity.getComponent(Model.class).primitives;
        for (int i = 0; i < shapeUuids.size(); i++) {
            Entity shape = World.getWorld().entities.get(shapeUuids.get(i));
            if (Label.getLabel(shape).equals(label)) {
                return shape;
            }
        }
        return null;
    }

    public static Group<Entity> getPrimitives(Entity entity) {
        if (entity.getComponent(Model.class) == null) {
            Log.v("Gotcha", "Gotcha");
        }
        List<Long> shapeUuids = entity.getComponent(Model.class).primitives;
        Group<Entity> shapes = new Group<>();
        for (int i = 0; i < shapeUuids.size(); i++) {
            Entity shape = World.getWorld().entities.get(shapeUuids.get(i));
            shapes.add(shape);
        }
        return shapes;
    }

    public static Group<Entity> getPrimitives(Entity entity, String... labels) {
        Group<Entity> shapes = Model.getPrimitives(entity);
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
