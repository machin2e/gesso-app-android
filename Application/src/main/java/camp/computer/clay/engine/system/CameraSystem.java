package camp.computer.clay.engine.system;

import android.util.Log;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.util.Visible;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.platform.Application;
import camp.computer.clay.util.BuilderImage.Geometry;
import camp.computer.clay.engine.World;
import camp.computer.clay.util.BuilderImage.Rectangle;
import camp.computer.clay.util.BuilderImage.Shape;
import camp.computer.clay.util.time.Clock;

public class CameraSystem extends System {

    public CameraSystem(World world) {
        super(world);
    }

    @Override
    public void update() {

        Group<Entity> cameras = Entity.Manager.filterWithComponent(Camera.class);
        for (int i = 0; i < cameras.size(); i++) {
            updateCamera(cameras.get(i));
        }
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

    // <REFACTOR/DELETE>
    // TODO: Put into PlatformRenderSurface? Viewport? Elsewhere? Screen descriptor structure?
    public void setWidth(Entity camera, double width) {
        camera.getComponent(Camera.class).width = width;
    }

    public double getWidth(Entity camera) {
        return camera.getComponent(Camera.class).width;
    }

    public void setHeight(Entity camera, double height) {
        camera.getComponent(Camera.class).height = height;
    }

    public double getHeight(Entity camera) {
        return camera.getComponent(Camera.class).height;
    }
    // </REFACTOR/DELETE>

    private void setPosition(Entity camera, Transform position, double duration) {

        double x = position.x;
        double y = position.y;

        Camera cameraComponent = camera.getComponent(Camera.class);

        if (duration == 0.0) {

            cameraComponent.targetPosition.set(-x, -y);
            cameraComponent.originalPosition.set(x, y);
            //this.position.set(x, y);
            cameraComponent.getEntity().getComponent(Transform.class).set(x, y);

            cameraComponent.positionFrameIndex = cameraComponent.positionFrameLimit;

        } else {

            /*
            // Solution 1: This works without per-frame adjustment. It's a starting point for that.
            // this.targetPosition.setAbsoluteX(-targetPosition.x * targetScale);
            // this.targetPosition.setAbsoluteY(-targetPosition.y * targetScale);
            */

            cameraComponent.targetPosition.set(-x, -y);

            // <PLAN_ANIMATION>
            cameraComponent.originalPosition.set(cameraComponent.getEntity().getComponent(Transform.class));

            // TODO: Replace this with deltaTime so no longer will be doing frame animation
            cameraComponent.positionFrameLimit = (int) (Application.getView().getFramesPerSecond() * (duration / Clock.MILLISECONDS_PER_SECOND));
            // ^ use positionFrameLimit as index into function to change animation by maing stepDistance vary with positionFrameLimit
            cameraComponent.positionFrameIndex = 0;
            // </PLAN_ANIMATION>
        }
    }

    public void adjustPosition(Entity camera) {
        Transform centerPosition = Entity.Manager.filterWithComponent(Host.class, Extension.class).getCenterPoint();
        setPosition(camera, centerPosition, Camera.DEFAULT_ADJUSTMENT_PERIOD);
    }

    public void setOffset(Entity camera, double dx, double dy) {
        Camera cameraComponent = camera.getComponent(Camera.class);
        cameraComponent.targetPosition.offset(dx, dy);
        cameraComponent.originalPosition.offset(dx, dy);
        cameraComponent.getEntity().getComponent(Transform.class).offset(dx, dy);
        cameraComponent.positionFrameIndex = 0;
        cameraComponent.positionFrameLimit = 0;
    }

    public void setOffset(Entity camera, Transform point) {
        setOffset(camera, point.x, point.y);
    }

    public void setScale(Entity camera, double scale, double duration) {

        Camera cameraComponent = camera.getComponent(Camera.class);
        Transform transform = camera.getComponent(Transform.class);

        cameraComponent.targetScale = scale;

        if (duration == 0) {
            transform.scale = scale;
        } else {
            double frameCount = Application.getView().getFramesPerSecond() * (duration / Clock.MILLISECONDS_PER_SECOND);
            // ^ use positionFrameLimit as index into function to change animation by maing stepDistance vary with positionFrameLimit
            cameraComponent.scaleDelta = Math.abs(scale - transform.scale) / frameCount;
        }
    }

    public double getScale(Entity camera) {
        return camera.getComponent(Transform.class).scale;
    }

    public void adjustScale(Entity camera) {
        adjustScale(camera, Camera.DEFAULT_SCALE_PERIOD);
    }

    public void adjustScale(Entity camera, double duration) {
        Rectangle boundingBox = Entity.Manager.filterWithComponent(Host.class, Extension.class).getBoundingBox();
        if (boundingBox.width > 0 && boundingBox.height > 0) {
            adjustScale(camera, boundingBox, duration);
        }
    }

    /**
     * Adjusts the {@code CameraEntity} to fit the bounding box {@code boundingBox}. This sets the
     * duration of the scale adjustment to the default value {@code DEFAULT_SCALE_PERIOD}.
     *
     * @param boundingBox The bounding box to fit into the display area.
     */
    public void adjustScale(Entity camera, Rectangle boundingBox) {
        adjustScale(camera, boundingBox, Camera.DEFAULT_SCALE_PERIOD);
    }

    /**
     * Adjusts the {@code CameraEntity} to fit the bounding box {@code boundingBox}.
     *
     * @param boundingBox The bounding box to fit into the display area.
     * @param duration    The duration of the scale adjustment.
     */
    public void adjustScale(Entity camera, Rectangle boundingBox, double duration) {

        /*
        // Multiply the bounding box
        double paddingMultiplier = 1.0; // 1.10;
        boundingBox.setWidth(boundingBox.getWidth() * paddingMultiplier);
        boundingBox.setHeight(boundingBox.getHeight() * paddingMultiplier);
        */

        double horizontalScale = getWidth(camera) / boundingBox.getWidth();
        double verticalScale = getHeight(camera) / boundingBox.getHeight();

        if (horizontalScale <= Camera.MAXIMUM_SCALE || horizontalScale <= Camera.MAXIMUM_SCALE) {
            if (horizontalScale < verticalScale) {
                setScale(camera, horizontalScale, duration);
            } else if (horizontalScale > horizontalScale) {
                setScale(camera, verticalScale, duration);
            }
        } else {
            setScale(camera, Camera.MAXIMUM_SCALE, Camera.DEFAULT_SCALE_PERIOD);
        }
    }

    /**
     * Adjusts the focus for the prototype {@code PathEntity} being created.
     *
     * @param sourcePort
     * @param targetPosition
     */
    public void setFocus(Entity camera, Entity sourcePort, Transform targetPosition) {

        Log.v("SetFocus", "setFocus(sourcePortEntity, targetPosition)");

//        // Check if a Host is nearby
////        Image nearestHostImage = Entity.Manager.filterWithComponent(Host.class).getNearest(targetPosition);
////        if (nearestHostImage != null) {
//
//            double distanceToPortable = Geometry.distance(sourcePort.getComponent(Transform.class), targetPosition);
//
//            if (distanceToPortable > 600) { // 800
//                setScale(0.6f, 100); // Zoom out to show overview
//            } else {
//                // setScale(1.0f, 100); // Zoom out to show overview
//                setFocus(sourcePort.getParent());
//            }
////        }
    }

    public void setFocus(Entity camera, Entity entity) {

        if (entity.hasComponent(Host.class)) {

            Log.v("SetFocus", "setFocus(HostEntity)");

            // <REFACTOR>
            /*
            Group<Entity> otherPortables = Entity.Manager.filterWithComponent(Host.class, Extension.class).remove(entity);
            otherPortables.setTransparency(0.1);
            */

            // Get all Ports in all Path connected to any of the Host's Ports
            Group<Entity> hostPathPorts = new Group<>();
            Group<Entity> hostPorts = entity.getComponent(Portable.class).getPorts();
            for (int i = 0; i < hostPorts.size(); i++) {
                Entity port = hostPorts.get(i);

                if (!hostPathPorts.contains(port)) {
                    hostPathPorts.add(port);
                }

                Group<Entity> portPaths = port.getComponent(Port.class).getPaths();
                for (int j = 0; j < portPaths.size(); j++) {
                    Entity pathEntity = portPaths.get(j);
                    if (!hostPathPorts.contains(pathEntity.getComponent(Path.class).getSource())) {
                        hostPathPorts.add(pathEntity.getComponent(Path.class).getSource());
                    }
                    if (!hostPathPorts.contains(pathEntity.getComponent(Path.class).getTarget())) {
                        hostPathPorts.add(pathEntity.getComponent(Path.class).getTarget());
                    }
                }
            }
            // </REFACTOR>

            Group<Shape> hostPathPortShapes = hostPathPorts.getImages().getShapes();
            Rectangle boundingBox = Geometry.getBoundingBox(hostPathPortShapes.getVertices());

            // Update scale and position
            adjustScale(camera, boundingBox);
            setPosition(camera, entity.getComponent(Transform.class), Camera.DEFAULT_ADJUSTMENT_PERIOD);

        } else if (entity.hasComponent(Extension.class)) {

            Log.v("SetFocus", "setFocus(Extension)");

            // <REFACTOR>
            /*
            Group<Entity> otherPortables = Entity.Manager.filterWithComponent(Host.class, Extension.class).remove(entity);
            otherPortables.setTransparency(0.1);
            */

            // Get Ports along every Path connected to the Ports on the selected Host
            Group<Entity> hostPathPorts = new Group<>();
            Group<Entity> extensionPorts = entity.getComponent(Portable.class).getPorts();
            for (int i = 0; i < extensionPorts.size(); i++) {
                Entity port = extensionPorts.get(i);

                if (!hostPathPorts.contains(port)) {
                    hostPathPorts.add(port);
                }

                Group<Entity> portPaths = port.getComponent(Port.class).getPaths();
                for (int j = 0; j < portPaths.size(); j++) {
                    Entity path = portPaths.get(j);
                    if (!hostPathPorts.contains(path.getComponent(Path.class).getSource())) {
                        hostPathPorts.add(path.getComponent(Path.class).getSource());
                    }
                    if (!hostPathPorts.contains(path.getComponent(Path.class).getTarget())) {
                        hostPathPorts.add(path.getComponent(Path.class).getTarget());
                    }
                }
            }
            // </REFACTOR>

            // Increase distance between Host and Extension
            Entity host = entity.getComponent(Portable.class).getHosts().get(0);
            world.portableLayoutSystem.setExtensionDistance(host, World.HOST_TO_EXTENSION_LONG_DISTANCE);

            Group<Shape> hostPathPortShapes = hostPathPorts.getImages().getShapes();
            Rectangle boundingBox = Geometry.getBoundingBox(hostPathPortShapes.getVertices());

            // Update scale and position
            adjustScale(camera, boundingBox);
            setPosition(camera, boundingBox.getPosition(), Camera.DEFAULT_ADJUSTMENT_PERIOD);

        }
    }

    public void setFocus(Entity camera, World world) {

        Log.v("SetFocus", "setFocus(World)");

        // Hide Portables' Ports.
        Entity.Manager.filterWithComponent(Path.class, Port.class).setVisibility(Visible.INVISIBLE);

        // Update distance between Hosts and Extensions
        world.portableLayoutSystem.setPortableSeparation(World.HOST_TO_EXTENSION_SHORT_DISTANCE);

        // Update scale and position
        adjustScale(camera);
        adjustPosition(camera);
    }
}
