package camp.computer.clay.engine.system;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Physics;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Group;

public class PhysicsSystem extends System {

    private Group<Entity> entities;

    public PhysicsSystem(World world) {
        super(world);
        setup();
    }

    private void setup() {
        entities = world.entities.subscribe(Group.Filters.filterWithComponents, Host.class, Physics.class, Transform.class);
    }

    @Override
    public void update(long dt) {
        for (int i = 0; i < entities.size(); i++) {
            // TODO: Update this to use time-based velocity and acceleration!
            entities.get(i).getComponent(Transform.class).set(
                    entities.get(i).getComponent(Physics.class).targetTransform
            );
        }
    }
}
