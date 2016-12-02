package camp.computer.clay.engine.system;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Model;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Primitive;
import camp.computer.clay.engine.component.Style;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Group;

public class AppearanceSystem extends System {

    // TODO: Use this system to change the Model's currently displayed "frame" or "suit"? Maybe ModelSystem is better for that. And update style here?

    public AppearanceSystem(World world) {
        super(world);
    }

    @Override
    public void update() {
        Group<Entity> entitiesWithTransform = world.Manager.getEntities().filterActive(true).filterWithComponents(Style.class, Transform.class, Model.class);

        // Update Style
        for (int i = 0; i < entitiesWithTransform.size(); i++) {
            Entity entity = entitiesWithTransform.get(i);

            // Update Style
            if (entity.hasComponent(Extension.class)) {
//                updateExtensionStyle(entity);
            } else if (entity.hasComponent(Host.class)) {
                updateHostAppearance(entity);
            } else if (entity.hasComponent(Port.class)) {
            } else if (entity.hasComponent(Path.class)) {
            }
        }
    }

    // <STYLE>
    public void updateHostAppearance(Entity host) {

        // TODO: Optimize regex calls to use ids/hashes! Cache!

        // Get LED shapes
        Group<Entity> ports = Portable.getPorts(host);
        Group<Entity> lightShapes = Model.getShapes(host, "^LED (1[0-2]|[1-9])$");

        // Update color of LEDs to color of corresponding Port shape
        for (int i = 0; i < ports.size(); i++) {
            String portColor = camp.computer.clay.util.Color.getColor(Port.getType(ports.get(i)));
            lightShapes.get(i).getComponent(Primitive.class).shape.setColor(portColor);
        }
    }

//    private void updateExtensionStyle(Entity extension) {
//
//        // Update Port Colors
//        Group<Entity> ports = Portable.getPorts(extension);
//        for (int i = 0; i < ports.size(); i++) {
//            Entity portEntity = ports.get(i);
//
////            Shape portShape = extension.getComponent(Model.class).getShape(portEntity);
////
////            // Update color of Port shape based on type
////            if (portShape != null) {
////                portShape.setColor(Color.getColor(portEntity.getComponent(Port.class).getType()));
////            }
//        }
//    }
    // </STYLE>
}
