package camp.computer.clay.engine.system;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Physics;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Group;

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

//            if (entities.get(i).hasComponent(Camera.class)) {
//                Transform source = entities.get(i).getComponent(Transform.class);
//                Transform target = entities.get(i).getComponent(Physics.class).targetTransform;
//                entities.get(i).getComponent(Transform.class).x += (target.x - source.x) * 0.0002;
//                entities.get(i).getComponent(Transform.class).y += (target.y - source.y) * 0.0002;
//            } else {
            entities.get(i).getComponent(Transform.class).set(
                    entities.get(i).getComponent(Physics.class).targetTransform
            );
//            }
        }

        // <HACK>
//        entities = world.Manager.getEntities().filterWithComponents(Camera.class);
        // </HACK>

//        for (int i = 0; i < entities.size(); i++) {
//
////            if (entities.get(i).hasComponent(Camera.class)) {
////            Transform source = entities.get(i).getComponent(Transform.class);
////            Transform target = entities.get(i).getComponent(Physics.class).targetTransform;
////            entities.get(i).getComponent(Transform.class).x += (target.x - source.x) * 0.05;
////            entities.get(i).getComponent(Transform.class).y += (target.y - source.y) * 0.05;
////            } else {
////            entities.get(i).getComponent(Transform.class).set(
////                    entities.get(i).getComponent(Physics.class).targetTransform
////            );
////            }
//
////            entities.get(i).getComponent(Transform.class).set(
////                    entities.get(i).getComponent(Physics.class).targetTransform
////            );
//        }
    }
}
