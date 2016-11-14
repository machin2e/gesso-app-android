package camp.computer.clay.engine.system;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Physics;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;

public class PhysicsSystem extends System {

    public PhysicsSystem(World world) {
        super(world);
    }

    @Override
    public void update() {
        // <HACK>
        Group<Entity> entities = world.Manager.getEntities().filterWithComponents(Host.class, Transform.class, Physics.class);
        // </HACK>

        for (int i = 0; i < entities.size(); i++) {
            entities.get(i).getComponent(Transform.class).set(
                    entities.get(i).getComponent(Physics.class).targetTransform
            );
        }
    }
}
