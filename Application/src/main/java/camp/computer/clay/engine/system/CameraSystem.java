package camp.computer.clay.engine.system;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.image.World;

public class CameraSystem extends System {

    @Override
    public boolean update(World world) {

        Group<Entity> cameras = Entity.Manager.filterWithComponent(Camera.class);
        for (int i = 0; i < cameras.size(); i++) {
            updateCamera(cameras.get(i));
        }

        return true;
    }

    private void updateCamera(Entity camera) {

        /*
        // Solution 1: This works without per-frame adjustment. It's a starting point for that.
        scale = this.targetScale;

        position.setAbsoluteX(targetPosition.getAbsoluteX());
        position.setAbsoluteY(targetPosition.getAbsoluteY());

        position.setAbsoluteX(position.getAbsoluteX() * scale);
        position.setAbsoluteY(position.getAbsoluteY() * scale);
        */

        Camera c = camera.getComponent(Camera.class);
        Transform t = camera.getComponent(Transform.class);

        // Scale
        if (Math.abs(t.scale - c.targetScale) <= c.scaleDelta) {
            t.scale = c.targetScale;
        } else if (t.scale > c.targetScale) {
            t.scale -= c.scaleDelta;
        } else {
            t.scale += c.scaleDelta;
        }

        // Position
        if (c.positionFrameIndex < c.positionFrameLimit) {

            double totalDistanceToTarget = Geometry.distance(c.originalPosition, c.targetPosition);
            double totalDistanceToTargetX = c.targetPosition.x - c.originalPosition.x;
            double totalDistanceToTargetY = c.targetPosition.y - c.originalPosition.y;

            double currentDistanceTarget = ((((double) (c.positionFrameIndex + 1) / (double) c.positionFrameLimit) * totalDistanceToTarget) / totalDistanceToTarget) /* (1.0 / scale) */;

            c.getEntity().getComponent(Transform.class).set(
                    t.scale * (currentDistanceTarget * totalDistanceToTargetX + c.originalPosition.x),
                    t.scale * (currentDistanceTarget * totalDistanceToTargetY + c.originalPosition.y)
            );

            c.positionFrameIndex++;

        } else { // if (positionFrameIndex >= positionFrameLimit) {

            c.getEntity().getComponent(Transform.class).x = c.targetPosition.x * t.scale;
            c.getEntity().getComponent(Transform.class).y = c.targetPosition.y * t.scale;

        }
    }
}
