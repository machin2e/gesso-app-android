package camp.computer.clay.engine.component;

import android.util.Log;

import camp.computer.clay.platform.Application;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.system.PortableLayoutSystem;
import camp.computer.clay.util.BuilderImage.Geometry;
import camp.computer.clay.util.BuilderImage.Rectangle;
import camp.computer.clay.util.BuilderImage.Shape;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.util.Visible;
import camp.computer.clay.util.time.Clock;

public class Camera extends Component {

    public static final int DEFAULT_SCALE_PERIOD = 200;

    public static final double DEFAULT_ADJUSTMENT_PERIOD = 200;

    public static double MAXIMUM_SCALE = 1.5;

    // Width of perspective --- actions (e.g., touches) are interpreted relative to this point
    public double width;

    // Height of perspective
    public double height;

    // Scale
    protected final double DEFAULT_SCALE = 1.0f;
    public double targetScale = DEFAULT_SCALE;
    protected int scalePeriod = DEFAULT_SCALE_PERIOD;
    public double scaleDelta = 0;

    // Position
    protected final Transform DEFAULT_POSITION = new Transform(0, 0);
    public Transform targetPosition = DEFAULT_POSITION;
    public int positionFrameIndex = 0;
    public int positionFrameLimit = 0;
    public Transform originalPosition = new Transform();

    public Camera() {
        super();
    }

//    // <REFACTOR/DELETE>
//    // TODO: Put into PlatformRenderSurface? Viewport? Elsewhere? Screen descriptor structure?
//    public void setWidth(double width) {
//        this.width = width;
//    }
//
//    public double getWidth() {
//        return this.width;
//    }
//
//    public void setHeight(double height) {
//        this.height = height;
//    }
//
//    public double getHeight() {
//        return this.height;
//    }
//    // </REFACTOR/DELETE>

//    private void setPosition(Transform position, double duration) {
//
//        double x = position.x;
//        double y = position.y;
//
//        if (duration == 0.0) {
//
//            this.targetPosition.set(-x, -y);
//            this.originalPosition.set(x, y);
//            //this.position.set(x, y);
//            getEntity().getComponent(Transform.class).set(x, y);
//
//            positionFrameIndex = positionFrameLimit;
//
//        } else {
//
//            /*
//            // Solution 1: This works without per-frame adjustment. It's a starting point for that.
//            // this.targetPosition.setAbsoluteX(-targetPosition.x * targetScale);
//            // this.targetPosition.setAbsoluteY(-targetPosition.y * targetScale);
//            */
//
//            this.targetPosition.set(-x, -y);
//
//            // <PLAN_ANIMATION>
//            originalPosition.set(getEntity().getComponent(Transform.class));
//
//            // TODO: Replace this with deltaTime so no longer will be doing frame animation
//            positionFrameLimit = (int) (Application.getView().getFramesPerSecond() * (duration / Clock.MILLISECONDS_PER_SECOND));
//            // ^ use positionFrameLimit as index into function to change animation by maing stepDistance vary with positionFrameLimit
//            positionFrameIndex = 0;
//            // </PLAN_ANIMATION>
//        }
//    }
//
//    public void adjustPosition() {
//        Transform centerPosition = Entity.Manager.filterWithComponent(Host.class, Extension.class).getCenterPoint();
//        setPosition(centerPosition, DEFAULT_ADJUSTMENT_PERIOD);
//    }
//
//    public void setOffset(double dx, double dy) {
//        targetPosition.offset(dx, dy);
//        originalPosition.offset(dx, dy);
//        getEntity().getComponent(Transform.class).offset(dx, dy);
//        positionFrameIndex = 0;
//        positionFrameLimit = 0;
//    }
//
//    public void setOffset(Transform point) {
//        setOffset(point.x, point.y);
//    }
//
//    public void setScale(double scale, double duration) {
//
//        this.targetScale = scale;
//
//        Transform transform = getEntity().getComponent(Transform.class);
//
//        if (duration == 0) {
//            transform.scale = scale;
//        } else {
//            double frameCount = Application.getView().getFramesPerSecond() * (duration / Clock.MILLISECONDS_PER_SECOND);
//            // ^ use positionFrameLimit as index into function to change animation by maing stepDistance vary with positionFrameLimit
//            scaleDelta = Math.abs(scale - transform.scale) / frameCount;
//        }
//    }
//
//    public double getScale() {
//        Transform transform = getEntity().getComponent(Transform.class);
//        return transform.scale;
//    }
//
//    public void adjustScale() {
//        adjustScale(Camera.DEFAULT_SCALE_PERIOD);
//    }
//
//    public void adjustScale(double duration) {
//        Rectangle boundingBox = Entity.Manager.filterWithComponent(Host.class, Extension.class).getBoundingBox();
//        if (boundingBox.width > 0 && boundingBox.height > 0) {
//            adjustScale(boundingBox, duration);
//        }
//    }
//
//    /**
//     * Adjusts the {@code CameraEntity} to fit the bounding box {@code boundingBox}. This sets the
//     * duration of the scale adjustment to the default value {@code DEFAULT_SCALE_PERIOD}.
//     *
//     * @param boundingBox The bounding box to fit into the display area.
//     */
//    public void adjustScale(Rectangle boundingBox) {
//        adjustScale(boundingBox, Camera.DEFAULT_SCALE_PERIOD);
//    }
//
//    /**
//     * Adjusts the {@code CameraEntity} to fit the bounding box {@code boundingBox}.
//     *
//     * @param boundingBox The bounding box to fit into the display area.
//     * @param duration    The duration of the scale adjustment.
//     */
//    public void adjustScale(Rectangle boundingBox, double duration) {
//
//        /*
//        // Multiply the bounding box
//        double paddingMultiplier = 1.0; // 1.10;
//        boundingBox.setWidth(boundingBox.getWidth() * paddingMultiplier);
//        boundingBox.setHeight(boundingBox.getHeight() * paddingMultiplier);
//        */
//
//        double horizontalScale = getWidth() / boundingBox.getWidth();
//        double verticalScale = getHeight() / boundingBox.getHeight();
//
//        if (horizontalScale <= MAXIMUM_SCALE || horizontalScale <= MAXIMUM_SCALE) {
//            if (horizontalScale < verticalScale) {
//                setScale(horizontalScale, duration);
//            } else if (horizontalScale > horizontalScale) {
//                setScale(verticalScale, duration);
//            }
//        } else {
//            setScale(MAXIMUM_SCALE, DEFAULT_SCALE_PERIOD);
//        }
//    }
//
//    /**
//     * Adjusts the focus for the prototype {@code PathEntity} being created.
//     *
//     * @param sourcePort
//     * @param targetPosition
//     */
//    public void setFocus(Entity sourcePort, Transform targetPosition) {
//
//        Log.v("SetFocus", "setFocus(sourcePortEntity, targetPosition)");
//
////        // Check if a Host is nearby
//////        Image nearestHostImage = Entity.Manager.filterWithComponent(Host.class).getNearest(targetPosition);
//////        if (nearestHostImage != null) {
////
////            double distanceToPortable = Geometry.distance(sourcePort.getComponent(Transform.class), targetPosition);
////
////            if (distanceToPortable > 600) { // 800
////                setScale(0.6f, 100); // Zoom out to show overview
////            } else {
////                // setScale(1.0f, 100); // Zoom out to show overview
////                setFocus(sourcePort.getParent());
////            }
//////        }
//    }
//
//    public void setFocus(Entity entity) {
//
//        if (entity.hasComponent(Host.class)) {
//
//            Log.v("SetFocus", "setFocus(HostEntity)");
//
//            // <REFACTOR>
//            /*
//            Group<Entity> otherPortables = Entity.Manager.filterWithComponent(Host.class, Extension.class).remove(entity);
//            otherPortables.setTransparency(0.1);
//            */
//
//            // Get all Ports in all Path connected to any of the Host's Ports
//            Group<Entity> hostPathPorts = new Group<>();
//            Group<Entity> hostPorts = entity.getComponent(Portable.class).getPorts();
//            for (int i = 0; i < hostPorts.size(); i++) {
//                Entity port = hostPorts.get(i);
//
//                if (!hostPathPorts.contains(port)) {
//                    hostPathPorts.add(port);
//                }
//
//                Group<Entity> portPaths = port.getComponent(Port.class).getPaths();
//                for (int j = 0; j < portPaths.size(); j++) {
//                    Entity pathEntity = portPaths.get(j);
//                    if (!hostPathPorts.contains(pathEntity.getComponent(Path.class).getSource())) {
//                        hostPathPorts.add(pathEntity.getComponent(Path.class).getSource());
//                    }
//                    if (!hostPathPorts.contains(pathEntity.getComponent(Path.class).getTarget())) {
//                        hostPathPorts.add(pathEntity.getComponent(Path.class).getTarget());
//                    }
//                }
//            }
//            // </REFACTOR>
//
//            Group<Shape> hostPathPortShapes = hostPathPorts.getImages().getShapes();
//            Rectangle boundingBox = Geometry.getBoundingBox(hostPathPortShapes.getVertices());
//
//            // Update scale and position
//            adjustScale(boundingBox);
//            setPosition(entity.getComponent(Transform.class), DEFAULT_ADJUSTMENT_PERIOD);
//
//        } else if (entity.hasComponent(Extension.class)) {
//
//            Log.v("SetFocus", "setFocus(Extension)");
//
//            // <REFACTOR>
//            /*
//            Group<Entity> otherPortables = Entity.Manager.filterWithComponent(Host.class, Extension.class).remove(entity);
//            otherPortables.setTransparency(0.1);
//            */
//
//            // Get Ports along every Path connected to the Ports on the selected Host
//            Group<Entity> hostPathPorts = new Group<>();
//            Group<Entity> extensionPorts = entity.getComponent(Portable.class).getPorts();
//            for (int i = 0; i < extensionPorts.size(); i++) {
//                Entity port = extensionPorts.get(i);
//
//                if (!hostPathPorts.contains(port)) {
//                    hostPathPorts.add(port);
//                }
//
//                Group<Entity> portPaths = port.getComponent(Port.class).getPaths();
//                for (int j = 0; j < portPaths.size(); j++) {
//                    Entity path = portPaths.get(j);
//                    if (!hostPathPorts.contains(path.getComponent(Path.class).getSource())) {
//                        hostPathPorts.add(path.getComponent(Path.class).getSource());
//                    }
//                    if (!hostPathPorts.contains(path.getComponent(Path.class).getTarget())) {
//                        hostPathPorts.add(path.getComponent(Path.class).getTarget());
//                    }
//                }
//            }
//            // </REFACTOR>
//
//            // Increase distance between Host and Extension
//            Entity host = entity.getComponent(Portable.class).getHosts().get(0);
//            PortableLayoutSystem.setExtensionDistance(host, World.HOST_TO_EXTENSION_LONG_DISTANCE);
//
//            Group<Shape> hostPathPortShapes = hostPathPorts.getImages().getShapes();
//            Rectangle boundingBox = Geometry.getBoundingBox(hostPathPortShapes.getVertices());
//
//            // Update scale and position
//            adjustScale(boundingBox);
//            setPosition(boundingBox.getPosition(), DEFAULT_ADJUSTMENT_PERIOD);
//
//        }
//    }
//
//    public void setFocus(World world) {
//
//        Log.v("SetFocus", "setFocus(World)");
//
//        // Hide Portables' Ports.
//        Entity.Manager.filterWithComponent(Path.class, Port.class).setVisibility(Visible.INVISIBLE);
//
//        // Update distance between Hosts and Extensions
//        PortableLayoutSystem.setPortableSeparation(World.HOST_TO_EXTENSION_SHORT_DISTANCE);
//
//        // Update scale and position
//        adjustScale();
//        adjustPosition();
//    }
}
