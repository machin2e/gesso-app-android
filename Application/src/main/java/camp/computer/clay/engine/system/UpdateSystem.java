package camp.computer.clay.engine.system;

import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.entity.Entity;
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
                entity.getComponent(Host.class).update();
            } else if (entity.hasComponent(Camera.class)) {
                entity.getComponent(Camera.class).update();
            }
            // </HACK>
        }
    }
}
