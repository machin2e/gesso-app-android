package camp.computer.clay.engine.system;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Space;

public class UpdateSystem extends System {
    @Override
    public boolean update(Space space) {

        // Update Actors
        space.getActor().update(); // HACK

        updateEntities();

        return true;
    }

    // TODO: Remove reference to Image. WTF.
    public void updateEntities() {
        for (int i = 0; i < Entity.Manager.size(); i++) {
            Entity entity = Entity.Manager.get(i);
//            Image image = entity.getComponent(Image.class);
//            if (image != null) {
////                image.draw(this);
//                image.update();
//            }

//            entity.update();
            // <HACK>
            if (entity.hasComponent(Extension.class)) {
                entity.getComponent(Extension.class).update();
            } else if (entity.hasComponent(Host.class)) {
//                entity.getComponent(Host.class).update();
                updateHostImage(entity);
            } else if (entity.hasComponent(Camera.class)) {
                entity.getComponent(Camera.class).update();
            }
            // </HACK>
        }
    }

    // <HOST>
    public void updateHostImage(Entity hostEntity) {

        Group<Shape> lightShapeGroup = null;

        // Get LED shapes
        if (lightShapeGroup == null) {
            lightShapeGroup = hostEntity.getComponent(Image.class).getShapes().filterLabel("^LED (1[0-2]|[1-9])$");
        }

        // Update PortEntity and LED shape styles
        for (int i = 0; i < hostEntity.getComponent(Portable.class).getPortEntities().size(); i++) {
            Entity portEntity = hostEntity.getComponent(Portable.class).getPortEntities().get(i);
            Shape portShape = hostEntity.getComponent(Image.class).getShape(portEntity.getLabel()); // Shape portShape = getShape(portEntity);

            // Update color of PortEntity shape based on type
            portShape.setColor(camp.computer.clay.util.Color.getColor(portEntity.getComponent(Port.class).getType()));

            // Update color of LED based on corresponding PortEntity's type
            lightShapeGroup.get(i).setColor(portShape.getColor());
        }

        // Call this so PortableEntity.update() will be called to update Geometry
        hostEntity.getComponent(Image.class).update();
    }
    // </HOST>
}
