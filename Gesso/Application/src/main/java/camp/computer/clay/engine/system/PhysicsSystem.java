package camp.computer.clay.engine.system;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Physics;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.util.FilterStrategy;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Group;

public class PhysicsSystem extends System {

    Group<Entity> entities;

    public PhysicsSystem(World world) {
        super(world);
        setup();
    }

    private void setup() {
        entities = world.entityManager.subscribe(
                new FilterStrategy(Group.Filters.filterWithComponents, Host.class, Physics.class, Transform.class)
        );
    }

    @Override
    public void update(long dt) {
        for (int i = 0; i < entities.size(); i++) {

            /*
            // TODO: Update this to use time-based velocity and acceleration!
            entityManager.get(i).getComponent(Transform.class).set(
                    entityManager.get(i).getComponent(Physics.class).targetTransform
            );
            */

            Transform transformComponent = entities.get(i).getComponent(Transform.class);
            Physics physicsComponent = entities.get(i).getComponent(Physics.class);

            transformComponent.x += (physicsComponent.targetTransform.x - transformComponent.x) * physicsComponent.velocity.x * dt;
            transformComponent.y += (physicsComponent.targetTransform.y - transformComponent.y) * physicsComponent.velocity.y * dt;

            transformComponent.rotation += (physicsComponent.targetTransform.rotation - transformComponent.rotation) * physicsComponent.velocity.y * dt;
        }
    }
}
