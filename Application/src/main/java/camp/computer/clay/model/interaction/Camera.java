package camp.computer.clay.model.interaction;

import android.util.Log;

import java.util.List;

import camp.computer.clay.application.Launcher;
import camp.computer.clay.model.architecture.Actor;
import camp.computer.clay.model.architecture.Extension;
import camp.computer.clay.model.architecture.Group;
import camp.computer.clay.model.architecture.Host;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.space.architecture.Image;
import camp.computer.clay.space.architecture.ImageGroup;
import camp.computer.clay.space.architecture.ShapeGroup;
import camp.computer.clay.space.architecture.Space;
import camp.computer.clay.space.image.HostImage;
import camp.computer.clay.space.image.PortableImage;
import camp.computer.clay.space.util.Time;
import camp.computer.clay.space.util.Visibility;
import camp.computer.clay.space.util.geometry.Geometry;
import camp.computer.clay.space.util.geometry.Point;
import camp.computer.clay.space.util.geometry.Rectangle;

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

    public void setPosition(Point targetPosition) {
        setPosition(targetPosition, positionPeriod);
    }

    public void setPosition(Point targetPosition, double duration) {

//        Log.v("Camera", "position x: " + position.getX() + ", y: " + position.getY());
//        Log.v("Camera", "originalPosition x: " + originalPosition.getX() + ", y: " + originalPosition.getY());
//        Log.v("Camera", "targetPosition x: " + targetPosition.getX() + ", y: " + targetPosition.getY());
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

            positionFrameLimit = (int) (Launcher.getView().getFramesPerSecond() * (duration / Time.MILLISECONDS_PER_SECOND));
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
            double frameCount = Launcher.getView().getFramesPerSecond() * (duration / Time.MILLISECONDS_PER_SECOND);
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

            double totalDistanceToTarget = Geometry.calculateDistance(originalPosition, targetPosition);
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
    public void focusCreatePath(Action action) {

        Event lastEvent = action.getLastEvent();

        // Check if a machine sprite was nearby
        Image nearestFormImage = getSpace().getImages().filterType(Host.class).getNearest(lastEvent.getPosition());
        if (nearestFormImage != null) {

            // TODO: Vibrate

            // Adjust perspective
            //getCamera().setPosition(nearestFormImage.getPosition());
            setScale(0.6f, 100); // Zoom out to show overview

        } else {

            // Show ports and paths
//            boardImage.setVisibility(Visibility.VISIBLE);
//            boardImage.showPaths();

            // Adjust perspective
            Point centerPoint = getSpace().getImages(Host.class).getCenterPoint();
            double scale = 0.6;
            setPosition(centerPoint);
            setScale(scale); // Zoom out to show overview

        }

//        PortImage portFigure = (PortImage) event.getTargetImage();
//
//        // Show ports of nearby forms
//        ImageGroup nearbyFigures = getSpace().getImages(Host.class, Extension.class).filterArea(event.getPosition(), 200 + 60);
//
//        List<Image> images = getSpace().getImages(Host.class, Extension.class).getList();
//        for (int i = 0; i < images.size(); i++) {
//            Image image = images.get(i);
//
//            if (image == portFigure.getParentImage() || nearbyFigures.contains(image)) {
//
//                if (image instanceof HostImage) {
//                    HostImage nearbyFigure = (HostImage) image;
//                    nearbyFigure.setTransparency(1.0f);
//                    nearbyFigure.setPortVisibility();
//                } else if (image instanceof ExtensionImage) {
//                    ExtensionImage nearbyFigure = (ExtensionImage) image;
//                    nearbyFigure.setTransparency(1.0f);
//                    nearbyFigure.setPortVisibility();
//                }
//
//            } else {
//
//                if (image instanceof HostImage) {
//                    HostImage nearbyFigure = (HostImage) image;
//                    nearbyFigure.setTransparency(0.1f);
//                    nearbyFigure.hidePortShapes();
//                } else if (image instanceof ExtensionImage) {
//                    ExtensionImage nearbyFigure = (ExtensionImage) image;
//                    nearbyFigure.setTransparency(0.1f);
//                    nearbyFigure.hidePortShapes();
//                }
//
//            }
//        }
//
//        // Check if a machine sprite was nearby
//        Image nearestFormImage = getSpace().getImages().filterType(Host.class).getNearest(event.getPosition());
//        if (nearestFormImage != null) {
//
//            // TODO: Vibrate
//
//            // Adjust perspective
//            //getCamera().setPosition(nearestFormImage.getPosition());
//            setScale(0.6f, 100); // Zoom out to show overview
//
//        } else {
//
//            // Show ports and paths
//            portFigure.setVisibility(Visibility.VISIBLE);
//            portFigure.showPaths();
//
//            // Adjust perspective
//            Point centerPoint = getSpace().getImages(Host.class).getCenterPosition();
//            double scale = 0.6;
//            setPosition(centerPoint);
//            setScale(scale); // Zoom out to show overview
//
//        }

        /*
        // Show the ports in the path
        List<Path> portPaths = getCamera().getSpace().getEntity().getCompletePath(port);
        List<Port> portConnections = getCamera().getSpace().getEntity().getPorts(portPaths);
        for (Port portConnection: portConnections) {
            PortImage portFigureConnection = (PortImage) getCamera().getSpace().getImage(portConnection);
            portFigureConnection.setVisibility(true);
            portFigureConnection.setPathVisibility();
        }
        */

    }

    public void focusMoveCamera(Event event) {
        // Move perspective
        Action action = event.getAction();
        setOffset(action.offsetX, action.offsetY);
    }

    public void focusSelectHost(Event event) {
        Actor actor = event.getActor();
        Action action = event.getAction();

        if (action.isDragging()) {

            // Zoom out to show overview
            setScale(0.8);
            //adjustScale();

        } else {

            HostImage hostImage = (HostImage) event.getTargetImage();

            // Remove focus from other form
            ImageGroup otherHostImages = getSpace().getImages().filterType(Host.class, Extension.class).remove(hostImage);
            for (int i = 0; i < otherHostImages.size(); i++) {
                Image otherHostImage = otherHostImages.get(i);
//                image.hidePortShapes();
//                image.hidePathImages();
                otherHostImage.setTransparency(0.1f);
//                TODO: Set <Rectangle> for Host; remove <Entity> from Image
            }

            Action previousAction = null;
            if (actor.actions.size() > 1) {
                previousAction = actor.actions.get(actor.actions.size() - 2);
                Log.v("PreviousTouch", "Previous: " + previousAction.getFirstEvent().getTargetImage());
                Log.v("PreviousTouch", "Current: " + event.getTargetImage());
            }

            // Camera
            if (hostImage.getHost().getPaths().size() > 0
                    && (previousAction != null && previousAction.getFirstEvent().getTargetImage() != event.getTargetImage())) {

                Log.v("Touch_", "A");

//                for (PortImage portImage : baseImage.getPortShapes()) {
//                    List<PathImage> pathImages = portImage.getPathImages();
//                    for (PathImage pathImage : pathImages) {
//                        pathImage.setVisibility(Visibility.INVISIBLE);
//                    }
//                }

                // Get ports along every path connected to the ports on the touched form
                Group<Port> formPathPorts = new Group<>();
                Group<Port> hostPorts = hostImage.getHost().getPorts();
                for (int i = 0; i < hostPorts.size(); i++) {
                    Port port = hostPorts.get(i);

                    // TODO: ((PortImage) getCamera().getSpace().getImage(port)).getVisiblePaths()

                    if (!formPathPorts.contains(port)) {
                        formPathPorts.add(port);
                    }

                    List<Path> portPaths = port.getCompletePath();
                    for (int i1 = 0; i1 < portPaths.size(); i1++) {
                        Path path = portPaths.get(i1);
                        if (!formPathPorts.contains(path.getSource())) {
                            formPathPorts.add(path.getSource());
                        }
                        if (!formPathPorts.contains(path.getTarget())) {
                            formPathPorts.add(path.getTarget());
                        }
                    }
                }

                // Camera
                ShapeGroup hostPathPortShapes = getSpace().getShapes().filterEntity(formPathPorts);
                Rectangle boundingBox = Geometry.calculateBoundingBox(hostPathPortShapes.getPositions());

                adjustScale(boundingBox);
                setPosition(boundingBox.getPosition());

            } else {

                Log.v("Touch_", "B");

                // Do this on second press, or when none of the machine's ports have paths.
                // This provides lookahead, so you can be triggered to processAction again to recover
                // the perspective.

//                for (PortImage portImage : baseImage.getPortShapes()) {
//                    List<PathImage> pathImages = portImage.getPathImages();
//                    for (PathImage pathImage : pathImages) {
//                        pathImage.setVisibility(Visibility.INVISIBLE);
//                    }
//                }

                // TODO: (on second press, also hide external ports, send peripherals) getCamera().setScale(1.2f);
                // TODO: (cont'd) getCamera().setPosition(baseImage.getPosition());

                setScale(1.2f);
                setPosition(hostImage.getPosition());
            }
        }

    }

    public void focusSelectPath(Port port) {

        // Camera
        List<Path> paths = port.getCompletePath();
        Group<Port> ports = port.getPorts(paths);
        ShapeGroup shapes = getSpace().getShapes(ports);

//        for (int i = 0; i < ports.size(); i++) {
//            Extension extension = ports.get(i).getExtension();
//            if (extension != null) {
//                shapes.add(getSpace().getShape(extension));
//            }
//        }

        List<Point> positions = shapes.getPositions();
        Rectangle boundingBox = Geometry.calculateBoundingBox(positions);

        // Update Scale
        adjustScale(boundingBox);

        // Update Position
        setPosition(Geometry.calculateCenterPosition(positions));
    }

    public void focusSelectSpace() { // Previously called "focusReset"

        // No pointerCoordinates on board or port. Touch is on map. So hide ports.
        ImageGroup portableImages = space.getImages(Host.class, Extension.class);
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
        Point centerPosition = Geometry.calculateCenterPosition(figurePositions);
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

        // <PADDING_MULTIPLIER>
        double paddingMultiplier = 1.10;
        boundingBox.setWidth(boundingBox.getWidth() * paddingMultiplier);
        boundingBox.setHeight(boundingBox.getHeight() * paddingMultiplier);
        // </PADDING_MULTIPLIER>

        double horizontalDifference = boundingBox.getWidth() - getWidth();
        double verticalDifference = boundingBox.getHeight() - getHeight();

        double horizontalScale = getWidth() / boundingBox.getWidth();
        double verticalScale = getHeight() / boundingBox.getHeight();

        /*
        Log.v("ScaleRatio", "h diff: " + horizontalDifference);
        Log.v("ScaleRatio", "v diff: " + verticalDifference);
        Log.v("ScaleRatio", "h scale: " + horizontalScale);
        Log.v("ScaleRatio", "v scale: " + verticalScale);
        Log.v("ScaleRatio", "---");
        */

        // if (horizontalScale >= 0.5 && horizontalScale <= MAXIMUM_SCALE || verticalScale >= 0.5 && verticalScale <= MAXIMUM_SCALE) {
        if (horizontalScale <= MAXIMUM_SCALE || horizontalScale <= MAXIMUM_SCALE) {
            if (horizontalScale < verticalScale) {
                setScale(horizontalScale, duration);
            } else if (horizontalScale > horizontalScale) {
                setScale(verticalScale, duration);
            }
        } else {
            setScale(DEFAULT_SCALE, DEFAULT_SCALE_PERIOD);
        }

//        if (horizontalDifference > 0 && horizontalDifference > verticalDifference) {
//            setScale(horizontalScale, duration);
//        } else if (verticalDifference > 0 && verticalDifference > horizontalDifference) {
//            setScale(verticalScale, duration);
//        } else {
//            setScale(1.0f, duration);
//        }
    }
}
