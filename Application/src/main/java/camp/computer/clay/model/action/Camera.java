package camp.computer.clay.model.action;

import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.model.Extension;
import camp.computer.clay.model.Group;
import camp.computer.clay.model.Host;
import camp.computer.clay.model.Path;
import camp.computer.clay.model.Port;
import camp.computer.clay.model.Portable;
import camp.computer.clay.model.util.PathGroup;
import camp.computer.clay.model.util.PortGroup;
import camp.computer.clay.space.image.HostImage;
import camp.computer.clay.space.image.PortableImage;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.image.Image;
import camp.computer.clay.util.image.Space;
import camp.computer.clay.util.image.Visibility;
import camp.computer.clay.util.image.util.ImageGroup;
import camp.computer.clay.util.image.util.ShapeGroup;
import camp.computer.clay.util.time.Time;

public class Camera {

    // TODO: Caption generation for each Perspective/Camera

    public static double MAXIMUM_SCALE = 1.0;

    private double width; // Width of perspective --- actions (e.g., touches) are interpreted relative to this point

    private double height; // Height of perspective

    public void setWidth(double width) {
        this.width = width;
    }

    public double getWidth() {
        return this.width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getHeight() {
        return this.height;
    }

    // The space displayed from this perspective
    private Space space = null;

    // Focus in Camera
    private Image focusImage = null;

    public Camera() {
    }

    public Camera(Space space) {
        this.space = space;
    }

    public Point getPosition() {
        return this.position;
    }

    public final double DEFAULT_SCALE = 1.0f;
    public static final int DEFAULT_SCALE_PERIOD = 200;

    public final Point DEFAULT_POSITION = new Point(0, 0);
    public static final double DEFAULT_ADJUSTMENT_PERIOD = 200;

    private double targetScale = DEFAULT_SCALE;
    public double scale = DEFAULT_SCALE;
    private int scalePeriod = DEFAULT_SCALE_PERIOD;
    private double scaleDelta = 0;

    private Point targetPosition = DEFAULT_POSITION;
    private Point position = new Point(targetPosition.getX(), targetPosition.getY());
    private double positionPeriod = DEFAULT_ADJUSTMENT_PERIOD;
    private int positionFrameIndex = 0;
    private int positionFrameLimit = 0;

    private Point originalPosition = new Point();

    public void setPosition(Point position) {
        setPosition(position, positionPeriod);
    }

    public void setPosition(Point targetPosition, double duration) {

//        Log.v("Camera", "position relativeX: " + position.getX() + ", relativeY: " + position.getY());
//        Log.v("Camera", "originalPosition relativeX: " + originalPosition.getX() + ", relativeY: " + originalPosition.getY());
//        Log.v("Camera", "targetPosition relativeX: " + targetPosition.getX() + ", relativeY: " + targetPosition.getY());
//        Log.v("Camera", "-");

        if (targetPosition.getX() == position.getX() && targetPosition.getY() == position.getY()) {

            return;
        }

        if (duration == 0) {

            this.targetPosition.set(-targetPosition.getX(), -targetPosition.getY());

            this.originalPosition.set(targetPosition);

            this.position.set(targetPosition);

        } else {

            /*
            // Solution 1: This works without per-frame adjustment. It's a starting point for that.
            // this.targetPosition.setX(-targetPosition.getX() * targetScale);
            // this.targetPosition.setY(-targetPosition.getY() * targetScale);
            */

            this.targetPosition.set(-targetPosition.getX(), -targetPosition.getY());

            // <PLAN_ANIMATION>
            originalPosition.set(position);

            positionFrameLimit = (int) (Application.getView().getFramesPerSecond() * (duration / Time.MILLISECONDS_PER_SECOND));
            // ^ use positionFrameLimit as index into function to change animation by maing stepDistance vary with positionFrameLimit
            positionFrameIndex = 0;
            // </PLAN_ANIMATION>
        }
    }

    public void setOffset(double xOffset, double yOffset) {
        this.targetPosition.offset(xOffset, yOffset);
        this.originalPosition.offset(xOffset, yOffset);
        this.position.offset(xOffset, yOffset);
    }

    public void setScale(double scale) {
        setScale(scale, scalePeriod);
    }

    public void setScale(double scale, double duration) {

        this.targetScale = scale;

        if (duration == 0) {
            this.scale = scale;
        } else {
            double frameCount = Application.getView().getFramesPerSecond() * (duration / Time.MILLISECONDS_PER_SECOND);
            // ^ use positionFrameLimit as index into function to change animation by maing stepDistance vary with positionFrameLimit
            scaleDelta = Math.abs(scale - this.scale) / frameCount;
        }
    }

    public double getScale() {
        return this.scale;
    }

    public void update() {

        /*
        // Solution 1: This works without per-frame adjustment. It's a starting point for that.
        scale = this.targetScale;

        position.setX(targetPosition.getX());
        position.setY(targetPosition.getY());

        position.setX(position.getX() * scale);
        position.setY(position.getY() * scale);
        */

        // Scale
        if (scale != targetScale) {

            if (scale > targetScale) {
                scale -= scaleDelta;
            } else {
                scale += scaleDelta;
            }

            if (Math.abs(scale - targetScale) < scaleDelta) {
                scale = targetScale;
            }

        }

        // Position
        if (positionFrameIndex < positionFrameLimit) {

            double totalDistanceToTarget = Point.calculateDistance(originalPosition, targetPosition);
            double totalDistanceToTargetX = targetPosition.getX() - originalPosition.getX();
            double totalDistanceToTargetY = targetPosition.getY() - originalPosition.getY();

            // double currentDistanceToTarget = Geometry.calculateDistance(position, targetPosition);
            // double currentDistance = (distanceToTarget - currentDistanceToTarget) / distanceToTarget;
            double currentDistanceTarget = ((((double) (positionFrameIndex + 1) / (double) positionFrameLimit) * totalDistanceToTarget) / totalDistanceToTarget) /* (1.0 / scale) */;
            // Log.v("Progress", "frame: " + (positionFrameIndex + 1) + " of " + positionFrameLimit + ", done: " + currentDistance + ", target: " + currentDistanceTarget + ", left: " + (1.0 - currentDistance));

            position.set(scale * (currentDistanceTarget * totalDistanceToTargetX + originalPosition.getX()), scale * (currentDistanceTarget * totalDistanceToTargetY + originalPosition.getY()));

            positionFrameIndex++;

        } else if (positionFrameIndex == positionFrameLimit) {

            position.setX(targetPosition.getX() * scale);
            position.setY(targetPosition.getY() * scale);

        }

    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public Space getSpace() {
        return this.space;
    }

    // <REFACTOR>

    /**
     * Adjusts the focus for the prototype {@code Path} being created.
     *
     * @param sourcePort
     * @param targetPosition
     */
    public void setFocus(Port sourcePort, Point targetPosition) {
        // TODO: setFocus(Path prototypePath)

        // Check if a Host Image is nearby
        Image nearestHostImage = getSpace().getImages().filterType(Host.class).getNearestImage(targetPosition);
        if (nearestHostImage != null) {

            Portable sourcePortable = sourcePort.getPortable();
            PortableImage sourcePortableImage = (PortableImage) space.getImage(sourcePortable);

            double distanceToPortable = Point.calculateDistance(sourcePortableImage.getPosition(), targetPosition);

            if (distanceToPortable > 800) {
                setScale(0.6f, 100); // Zoom out to show overview
            } else {
                setScale(1.0f, 100); // Zoom out to show overview
            }

        }
    }

    public void setFocus(Host host) {

//        Actor actor = event.getActor();
//        Action action = event.getAction();

//        if (action.isDragging()) {
//
//            // Zoom out to show overview
//            setScale(0.8);
//            //adjustScale();
//
//        } else {

        HostImage hostImage = (HostImage) space.getImage(host);

        // Reduce transparency of other all Portables (not electrically connected to the PhoneHost)
        ImageGroup otherPortableImages = getSpace().getImages().filterType(Host.class, Extension.class);
        otherPortableImages.remove(hostImage);
        otherPortableImages.setTransparency(0.1);

//        // Get the previous Action
//        Action previousAction = null;
//        if (actor.actions.size() > 1) {
//            previousAction = actor.actions.get(actor.actions.size() - 2);
////                Log.v("PreviousTouch", "Previous: " + previousAction.getFirstEvent().getTargetImage());
////                Log.v("PreviousTouch", "Current: " + event.getTargetImage());
//        }

        // Camera
//        if (hostImage.getHost().getPaths().size() > 0
//                && (previousAction != null && previousAction.getFirstEvent().getTargetImage() != event.getTargetImage())) {

//                Log.v("Touch_", "A");

//                for (PortImage portImage : baseImage.getPortShapes()) {
//                    List<PathImage> pathImages = portImage.getPathImages();
//                    for (PathImage pathImage : pathImages) {
//                        pathImage.setVisibility(Visibility.INVISIBLE);
//                    }
//                }

        // Get ports along every Path connected to the Ports on the touched PhoneHost
        Group<Port> basePathPorts = new Group<>();
        Group<Port> hostPorts = hostImage.getHost().getPorts();
        for (int i = 0; i < hostPorts.size(); i++) {
            Port port = hostPorts.get(i);

            // TODO: ((PortImage) getCamera().getSpace().getImage(port)).getVisiblePaths()

            if (!basePathPorts.contains(port)) {
                basePathPorts.add(port);
            }

            PathGroup portPaths = port.getPaths();
            for (int i1 = 0; i1 < portPaths.size(); i1++) {
                Path path = portPaths.get(i1);
                if (!basePathPorts.contains(path.getSource())) {
                    basePathPorts.add(path.getSource());
                }
                if (!basePathPorts.contains(path.getTarget())) {
                    basePathPorts.add(path.getTarget());
                }
            }
        }

        // Camera
        ShapeGroup hostPathPortShapes = getSpace().getShapes().filterEntity(basePathPorts);
        Rectangle boundingBox = Geometry.calculateBoundingBox(hostPathPortShapes.getPositions());

        adjustScale(boundingBox);
        setPosition(boundingBox.getPosition());

//        } else {
//
////                Log.v("Touch_", "B");
//
//            // Do this on second press, or when none of the machine's ports have paths.
//            // This provides lookahead, so you can be triggered to processAction again to recover
//            // the perspective.
//
////                for (PortImage portImage : baseImage.getPortShapes()) {
////                    List<PathImage> pathImages = portImage.getPathImages();
////                    for (PathImage pathImage : pathImages) {
////                        pathImage.setVisibility(Visibility.INVISIBLE);
////                    }
////                }
//
//            // TODO: (on second press, also hide external ports, send peripherals) getCamera().setScale(1.2f);
//            // TODO: (cont'd) getCamera().setPosition(baseImage.getPosition());
//
//            setScale(1.2f);
//            setPosition(hostImage.getPosition());
//        }
//        }

    }

    /**
     * Adjusts the focus so the {@code Path}s in {@code paths} are in view.
     *
     * @param paths
     */
    public void setFocus(PathGroup paths) {

        // Camera
        PortGroup ports = paths.getPorts();
        ShapeGroup shapes = getSpace().getShapes(ports);

        List<Point> positions = shapes.getPositions();
        Rectangle boundingBox = Geometry.calculateBoundingBox(positions);

        // Update Scale
        adjustScale(boundingBox);

        // Update Position
        setPosition(Geometry.calculateCenter(positions));
    }

    public void setFocus(Space space) {

        // No pointerCoordinates on board or port. Touch is on map. So hide ports.
        ImageGroup portableImages = this.space.getImages(Host.class, Extension.class);
        for (int i = 0; i < portableImages.size(); i++) {
            PortableImage portableImage = (PortableImage) portableImages.get(i);
            portableImage.getPortShapes().setVisibility(Visibility.Value.INVISIBLE);
            portableImage.setPathVisibility(Visibility.Value.INVISIBLE);
            portableImage.setDockVisibility(Visibility.Value.VISIBLE);
            portableImage.setTransparency(1.0);
        }

        // Adjust scale and position
        adjustScale();
        adjustPosition();
    }
    // </REFACTOR>

    public void adjustPosition() {
        List<Point> figurePositions = getSpace().getImages().filterType(Host.class, Extension.class).getPositions();
        Point centerPosition = Geometry.calculateCenter(figurePositions);
        setPosition(centerPosition);
    }

    public void adjustScale() {
        adjustScale(Camera.DEFAULT_SCALE_PERIOD);
    }

    public void adjustScale(double duration) {
        List<Point> figureVertices = getSpace().getImages().filterType(Host.class, Extension.class).getVertices();
        if (figureVertices.size() > 0) {
            Rectangle boundingBox = getSpace().getImages().filterType(Host.class, Extension.class).getBoundingBox();
            adjustScale(boundingBox, duration);
        }
    }

    public void adjustScale(Rectangle boundingBox) {
        adjustScale(boundingBox, Camera.DEFAULT_SCALE_PERIOD);
    }

    public void adjustScale(Rectangle boundingBox, double duration) {

//        // Multiply the bounding box
//        double paddingMultiplier = 1.0; // 1.10;
//        boundingBox.setWidth(boundingBox.getWidth() * paddingMultiplier);
//        boundingBox.setHeight(boundingBox.getHeight() * paddingMultiplier);

        double horizontalScale = getWidth() / boundingBox.getWidth();
        double verticalScale = getHeight() / boundingBox.getHeight();

        if (horizontalScale <= MAXIMUM_SCALE || horizontalScale <= MAXIMUM_SCALE) {
            if (horizontalScale < verticalScale) {
                setScale(horizontalScale, duration);
            } else if (horizontalScale > horizontalScale) {
                setScale(verticalScale, duration);
            }
        } else {
            setScale(DEFAULT_SCALE, DEFAULT_SCALE_PERIOD);
        }
    }
}
