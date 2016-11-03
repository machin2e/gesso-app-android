package camp.computer.clay.engine.system;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.util.BuilderImage.Geometry;
import camp.computer.clay.engine.World;

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

//    // <REFACTOR/DELETE>
//    // TODO: Put into PlatformRenderSurface? Elsewhere? Screen descriptor structure?
//    public void setWidth(Entity camera, double width) {
//        camera.getComponent(Camera.class).width = width;
//    }
//
//    public double getWidth(Entity camera) {
//        return camera.getComponent(Camera.class).width;
//    }
//
//    public void setHeight(Entity camera, double height) {
//        camera.getComponent(Camera.class).height = height;
//    }
//
//    public double getHeight(Entity camera) {
//        return camera.getComponent(Camera.class).height;
//    }
//    // </REFACTOR/DELETE>
//
//    private void setPosition(Entity camera, Transform position, double duration) {
//
//        double x = position.x;
//        double y = position.y;
//
//        Camera cameraState = camera.getComponent(Camera.class);
//
//        if (duration == 0.0) {
//
//            cameraState.targetPosition.set(-x, -y);
//            cameraState.originalPosition.set(x, y);
//            //this.position.set(x, y);
//            camera.getComponent(Transform.class).set(x, y);
//
//            cameraState.positionFrameIndex = cameraState.positionFrameLimit;
//
//        } else {
//
//            /*
//            // Solution 1: This works without per-frame adjustment. It's a starting point for that.
//            // this.targetPosition.setAbsoluteX(-targetPosition.x * targetScale);
//            // this.targetPosition.setAbsoluteY(-targetPosition.y * targetScale);
//            */
//
//            cameraState.targetPosition.set(-x, -y);
//
//            // <PLAN_ANIMATION>
//            cameraState.originalPosition.set(camera.getComponent(Transform.class));
//
//            cameraState.positionFrameLimit = (int) (Application.getPlatform().getFramesPerSecond() * (duration / Clock.MILLISECONDS_PER_SECOND));
//            // ^ use positionFrameLimit as index into function to change animation by maing stepDistance vary with positionFrameLimit
//            cameraState.positionFrameIndex = 0;
//            // </PLAN_ANIMATION>
//        }
//    }
//
//    public void adjustPosition(Entity camera) {
//        Camera cameraState = camera.getComponent(Camera.class);
//        Transform centerPosition = Entity.Manager.filterWithComponent(Host.class, Extension.class).getCenterPoint();
//        Log.v("AdjustCenter", "centerPosition.x: " + centerPosition.x + ", y: " + centerPosition.y);
//        setPosition(camera, centerPosition, cameraState.DEFAULT_ADJUSTMENT_PERIOD);
//    }
//
//    public void setOffset(Entity camera, double dx, double dy) {
//        Camera cameraState = camera.getComponent(Camera.class);
//        cameraState.targetPosition.offset(dx, dy);
//        cameraState.originalPosition.offset(dx, dy);
//        camera.getComponent(Transform.class).offset(dx, dy);
//    }
//
//    public void setOffset(Entity camera, Transform point) {
//        setOffset(camera, point.x, point.y);
//    }
//
//    public void setScale(Entity camera, double scale, double duration) {
//
//        Camera cameraState = camera.getComponent(Camera.class);
//
//        cameraState.targetScale = scale;
//
//        Transform transform = camera.getComponent(Transform.class);
//
//        if (duration == 0) {
//            transform.scale = scale;
//        } else {
//            double frameCount = Application.getPlatform().getFramesPerSecond() * (duration / Clock.MILLISECONDS_PER_SECOND);
//            // ^ use positionFrameLimit as index into function to change animation by maing stepDistance vary with positionFrameLimit
//            cameraState.scaleDelta = Math.abs(scale - transform.scale) / frameCount;
//        }
//    }
//
//    public double getScale(Entity camera) {
//        Transform transform = camera.getComponent(Transform.class);
//        return transform.scale;
//    }
//
//    public void adjustScale(Entity camera) {
//        adjustScale(camera, Camera.DEFAULT_SCALE_PERIOD);
//    }
//
//    public void adjustScale(Entity camera, double duration) {
//        Rectangle boundingBox = Entity.Manager.filterWithComponent(Host.class, Extension.class).getBoundingBox();
//        if (boundingBox.width > 0 && boundingBox.height > 0) {
//            adjustScale(camera, boundingBox, duration);
//        }
//    }
//
//    /**
//     * Adjusts the {@code CameraEntity} to fit the bounding box {@code boundingBox}. This sets the
//     * duration of the scale adjustment to the default value {@code DEFAULT_SCALE_PERIOD}.
//     *
//     * @param boundingBox The bounding box to fit into the display area.
//     */
//    public void adjustScale(Entity camera, Rectangle boundingBox) {
//        adjustScale(camera, boundingBox, Camera.DEFAULT_SCALE_PERIOD);
//    }
//
//    /**
//     * Adjusts the {@code CameraEntity} to fit the bounding box {@code boundingBox}.
//     *
//     * @param boundingBox The bounding box to fit into the display area.
//     * @param duration    The duration of the scale adjustment.
//     */
//    public void adjustScale(Entity camera, Rectangle boundingBox, double duration) {
//
//        Camera cameraState = camera.getComponent(Camera.class);
//
//        /*
//        // Multiply the bounding box
//        double paddingMultiplier = 1.0; // 1.10;
//        boundingBox.setWidth(boundingBox.getWidth() * paddingMultiplier);
//        boundingBox.setHeight(boundingBox.getHeight() * paddingMultiplier);
//        */
//
//        double horizontalScale = getWidth(camera) / boundingBox.getWidth();
//        double verticalScale = getHeight(camera) / boundingBox.getHeight();
//
//        if (horizontalScale <= cameraState.MAXIMUM_SCALE || horizontalScale <= cameraState.MAXIMUM_SCALE) {
//            if (horizontalScale < verticalScale) {
//                setScale(camera, horizontalScale, duration);
//            } else if (horizontalScale > horizontalScale) {
//                setScale(camera, verticalScale, duration);
//            }
//        } else {
//            setScale(camera, cameraState.MAXIMUM_SCALE, cameraState.DEFAULT_SCALE_PERIOD);
//        }
//    }
//
//    /**
//     * Adjusts the focus for the prototype {@code PathEntity} being created.
//     *
//     * @param sourcePortEntity
//     * @param targetPosition
//     */
//    public void setFocus(Entity sourcePortEntity, Transform targetPosition) {
//
//        Log.v("SetFocus", "setFocus(sourcePortEntity, targetPosition)");
//
////        // Check if a HostEntity Image is nearby
////        Image nearestHostImage = getWorld().getImages().filterType2(HostEntity.class).getNearestImage(targetPosition);
////        if (nearestHostImage != null) {
////
////            PortableEntity sourcePortable = sourcePortEntity.getPortable();
////            PortableImage sourcePortableImage = (PortableImage) sourcePortable.getImage();
////
////            double distanceToPortable = Geometry.distance(sourcePortableImage.getPosition(), targetPosition);
////
////            if (distanceToPortable > 800) {
////                setScale(0.6f, 100); // Zoom out to show overview
////            } else {
////                setScale(1.0f, 100); // Zoom out to show overview
////            }
////        }
//    }
//
//    public void setFocus(Entity camera, Entity entity) {
//
//        Camera cameraState = camera.getComponent(Camera.class);
//
//        if (entity.hasComponent(Host.class)) {
//
//            Log.v("SetFocus", "setFocus(HostEntity)");
//
//            // <REFACTOR>
////        HostImage hostImage = (HostImage) hostEntity.getComponent(Image.class);
//
////        // Reduce transparency of other all Portables (not electrically connected to the PhoneHost)
////        ImageGroup otherPortableImages = getWorld().getImages().filterType(HostEntity.class, ExtensionEntity.class);
////        otherPortableImages.remove(hostImage);
////        otherPortableImages.setTransparency(0.1);
//
//            // TODO: Group<PortableEntity> otherPortables = getWorld().getEntities();
////        Group<Entity> otherPortables = Entity.Manager.filter(Group.Filters.filterType, HostEntity.class, ExtensionEntity.class);
////            Group<Entity> otherPortables = Entity.Manager.filter(Group.Filters.filterType, HostEntity.class, Entity.class);
//            Group<Entity> otherPortables = Entity.Manager.filterWithComponent(Host.class, Extension.class);
//            Log.v("Entities", "otherPortables.size: " + otherPortables.size());
//            otherPortables.remove(entity);
//            otherPortables.setTransparency(0.1);
//
//            // Get ports along every PathEntity connected to the Ports on the touched PhoneHost
//            Group<Entity> basePathPortEntities = new Group<>();
//            Group<Entity> hostPortEntities = entity.getComponent(Portable.class).getPorts();
//            for (int i = 0; i < hostPortEntities.size(); i++) {
//                Entity portEntity = hostPortEntities.get(i);
//
//                if (!basePathPortEntities.contains(portEntity)) {
//                    basePathPortEntities.add(portEntity);
//                }
//
//                Group<Entity> portPathEntities = portEntity.getComponent(Port.class).getPaths();
//                for (int j = 0; j < portPathEntities.size(); j++) {
//                    Entity pathEntity = portPathEntities.get(j);
//                    if (!basePathPortEntities.contains(pathEntity.getComponent(Path.class).getSource())) {
//                        basePathPortEntities.add(pathEntity.getComponent(Path.class).getSource());
//                    }
//                    if (!basePathPortEntities.contains(pathEntity.getComponent(Path.class).getTarget())) {
//                        basePathPortEntities.add(pathEntity.getComponent(Path.class).getTarget());
//                    }
//                }
//            }
//            // </REFACTOR>
//
//            Group<Shape> hostPathPortShapes = basePathPortEntities.getImages().getShapes();
//            Rectangle boundingBox = Geometry.getBoundingBox(hostPathPortShapes.getVertices());
//
//            // Update scale and position
//            adjustScale(camera, boundingBox);
//            setPosition(camera, entity.getComponent(Transform.class), cameraState.DEFAULT_ADJUSTMENT_PERIOD);
//
//        } else if (entity.hasComponent(Extension.class)) {
//
//            Log.v("SetFocus", "setFocus(ExtensionEntity)");
//
//            // <REFACTOR>
//            Group<Entity> otherPortables = Entity.Manager.filterWithComponent(Host.class, Extension.class);
//            Log.v("Entities", "otherPortables.size: " + otherPortables.size());
//            otherPortables.remove(entity);
//            otherPortables.setTransparency(0.1);
//
//            // Get Ports along every Path connected to the Ports on the selected Host
//            Group<Entity> hostPathPortEntities = new Group<>();
//            Group<Entity> extensionPortEntities = entity.getComponent(Portable.class).getPorts();
//            for (int i = 0; i < extensionPortEntities.size(); i++) {
//                Entity portEntity = extensionPortEntities.get(i);
//
//                if (!hostPathPortEntities.contains(portEntity)) {
//                    hostPathPortEntities.add(portEntity);
//                }
//
//                Group<Entity> portPathEntities = portEntity.getComponent(Port.class).getPaths();
//                for (int j = 0; j < portPathEntities.size(); j++) {
//                    Entity pathEntity = portPathEntities.get(j);
//                    if (!hostPathPortEntities.contains(pathEntity.getComponent(Path.class).getSource())) {
//                        hostPathPortEntities.add(pathEntity.getComponent(Path.class).getSource());
//                    }
//                    if (!hostPathPortEntities.contains(pathEntity.getComponent(Path.class).getTarget())) {
//                        hostPathPortEntities.add(pathEntity.getComponent(Path.class).getTarget());
//                    }
//                }
//            }
//            // </REFACTOR>
//
//            // Increase distance between Host and Extension
//            Entity host = entity.getComponent(Portable.class).getHosts().get(0);
//            host.getComponent(Host.class).setExtensionDistance(World.HOST_TO_EXTENSION_LONG_DISTANCE);
//
//            Group<Shape> hostPathPortShapes = hostPathPortEntities.getImages().getShapes();
//            Rectangle boundingBox = Geometry.getBoundingBox(hostPathPortShapes.getVertices());
//
//            // Update scale and position
//            adjustScale(camera, boundingBox);
//            setPosition(camera, boundingBox.getPosition(), cameraState.DEFAULT_ADJUSTMENT_PERIOD);
//
//        }
//    }
//
//    public void setFocus(Entity camera, World world) {
//
//        Log.v("SetFocus", "setFocus(World)");
//
//        // Hide Portables' Ports.
//        world.hideAllPorts();
//
//        // Update distance between Hosts and Extensions
//        world.setPortableSeparation(World.HOST_TO_EXTENSION_SHORT_DISTANCE);
//
//        // Update scale and position
//        adjustScale(camera);
//        adjustPosition(camera);
//    }
}
