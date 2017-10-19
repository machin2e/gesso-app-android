package camp.computer.clay.engine.component;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Group;
import camp.computer.clay.lib.Geometry.Shape;

public class Model extends Component {

    // TODO: Support for multiple geometry configurations.
    // TODO: Replace index with tag LUT-returned asset UID
    public long assetIndex = 0;

    // TODO: Replace with AssetReference. Consider using using multiple AssetReferences to replace assetIndex.
    public Group<Entity> primitives = new Group<>();

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
}
