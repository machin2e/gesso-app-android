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
    // TODO: Replace with AssetReference. Consider using using multiple AssetReferences to replace meshIndex.
    public List<Entity> primitives = new ArrayList<>();
    // </REFACTOR>

    // public ModelBuilder assetReference;
    public long assetUid = -1L;

    public Model() {
        super();
    }

    // <REFACTOR>
    // TODO: Move a more semantically reasonable file
    // Create Shape entity and assign shape to it
    public static Entity createPrimitiveFromShape(Shape shape) {

        Entity primitiveEntity = World.getInstance().createEntity(Primitive.class);

        primitiveEntity.getComponent(Primitive.class).shape = shape;
        primitiveEntity.getComponent(Label.class).label = shape.getTag();
        primitiveEntity.getComponent(Transform.class).z = shape.getPosition().z;

        return primitiveEntity;
    }
    // </REFACTOR>

    public static void addPrimitive(Entity entity, Entity primitive) {

        // <REFACTOR>
        // Set Structure (for managing hierarchical state, such as visibility)
//        primitive.getComponent(Structure.class).parentEntity = entity;
        // </REFACTOR>

        // Set TransformConstraint for relative positioning
        primitive.getComponent(TransformConstraint.class).setReferenceEntity(entity);

        // Add Shape entity to ModelBuilder component
        entity.getComponent(Model.class).primitives.add(primitive);

//        return primitive;
    }

    // HACK: This is ridiculously expensive if you unpack it...
    public static Entity getPrimitive(Entity entity, String label) {
        List<Entity> primitives = entity.getComponent(Model.class).primitives;
        for (int i = 0; i < primitives.size(); i++) {
            if (Label.getLabel(primitives.get(i)).equals(label)) {
                return primitives.get(i);
            }
        }
        return null;
    }

    public static Group<Entity> getPrimitives(Entity entity) {
        if (entity.getComponent(Model.class) == null) {
            Log.v("Gotcha", "Gotcha");
        }
        List<Entity> primitives = entity.getComponent(Model.class).primitives;
        Group<Entity> shapes = new Group<>();
        for (int i = 0; i < primitives.size(); i++) {
            shapes.add(primitives.get(i));
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
