package camp.computer.clay.model.interaction;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.model.architecture.Actor;
import camp.computer.clay.model.architecture.Base;
import camp.computer.clay.model.architecture.Patch;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.scene.architecture.Image;
import camp.computer.clay.scene.architecture.ImageGroup;
import camp.computer.clay.scene.architecture.Scene;
import camp.computer.clay.scene.image.BaseImage;
import camp.computer.clay.scene.image.PatchImage;
import camp.computer.clay.scene.image.PortImage;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Rectangle;
import camp.computer.clay.scene.util.Time;
import camp.computer.clay.scene.util.Visibility;

public class Camera {

    // TODO: Caption generation for each Perspective/Camera

    public static double MAXIMUM_SCALE = 1.0;

    private double width; // Width of perspective --- processes (e.g., touches) are interpreted relative to this point

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

    // The scene displayed from this perspective
    private Scene scene = null;

    // Focus in Camera
    private Image focusImage = null;

    public Camera() {
    }

    public Camera(Scene scene) {
        this.scene = scene;
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

    public void setCoordinate(Point targetPosition) {
        setCoordinate(targetPosition, positionPeriod);
    }

    public void setCoordinate(Point targetPosition, double duration) {

        Log.v("Camera", "position x: " + position.getX() + ", y: " + position.getY());
        Log.v("Camera", "originalPosition x: " + originalPosition.getX() + ", y: " + originalPosition.getY());
        Log.v("Camera", "targetPosition x: " + targetPosition.getX() + ", y: " + targetPosition.getY());
        Log.v("Camera", "-");

        if (targetPosition.getX() == position.getX() && targetPosition.getY() == position.getY()) {

            return;
        }

        if (duration == 0) {

            this.targetPosition.set(
                    -targetPosition.getX(),
                    -targetPosition.getY()
            );

            this.originalPosition.set(targetPosition);

            this.position.set(targetPosition);

        } else {

            /*
            // Solution 1: This works without per-frame adjustment. It's a starting point for that.
            // this.targetPosition.setX(-targetPosition.getX() * targetScale);
            // this.targetPosition.setY(-targetPosition.getY() * targetScale);
            */

            this.targetPosition.set(
                    -targetPosition.getX(),
                    -targetPosition.getY()
            );

            // <PLAN_ANIMATION>
            originalPosition.set(position);

            positionFrameLimit = (int) (Application.getDisplay().getFramesPerSecond() * (duration / Time.MILLISECONDS_PER_SECOND));
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
            double frameCount = Application.getDisplay().getFramesPerSecond() * (duration / Time.MILLISECONDS_PER_SECOND);
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

            position.set(
                    scale * (currentDistanceTarget * totalDistanceToTargetX + originalPosition.getX()),
                    scale * (currentDistanceTarget * totalDistanceToTargetY + originalPosition.getY())
            );

            positionFrameIndex++;

        } else if (positionFrameIndex == positionFrameLimit) {

            position.setX(targetPosition.getX() * scale);
            position.setY(targetPosition.getY() * scale);

        }

    }

    public void setScene(Scene scene) {
        this.scene = scene;
    }

    public Scene getScene() {
        return this.scene;
    }

    public void focusCreatePath(Action action) {

        PortImage portFigure = (PortImage) action.getTarget();

        // Show ports of nearby forms
        ImageGroup nearbyFigures = getScene().getImages(Base.class, Patch.class).filterArea(action.getCoordinate(), 200 + 60);

        List<Image> images = getScene().getImages(Base.class, Patch.class).getList();
        for (int i = 0; i < images.size(); i++) {
            Image image = images.get(i);

            if (image == portFigure.getParentImage() || nearbyFigures.contains(image)) {

                if (image instanceof BaseImage) {
                    BaseImage nearbyFigure = (BaseImage) image;
                    nearbyFigure.setTransparency(1.0f);
                    nearbyFigure.showPortImages();
                } else if (image instanceof PatchImage) {
                    PatchImage nearbyFigure = (PatchImage) image;
                    nearbyFigure.setTransparency(1.0f);
                    nearbyFigure.showPortFigures();
                }

            } else {

                if (image instanceof BaseImage) {
                    BaseImage nearbyFigure = (BaseImage) image;
                    nearbyFigure.setTransparency(0.1f);
                    nearbyFigure.hidePortImages();
                } else if (image instanceof PatchImage) {
                    PatchImage nearbyFigure = (PatchImage) image;
                    nearbyFigure.setTransparency(0.1f);
                    nearbyFigure.hidePortFigures();
                }

            }
        }

        // Check if a machine sprite was nearby
        Image nearestFormImage = getScene().getImages().filterType(Base.class).getNearest(action.getCoordinate());
        if (nearestFormImage != null) {

            // TODO: Vibrate

            // Adjust perspective
            //getCamera().setCoordinate(nearestFormImage.getCoordinate());
            setScale(0.6f, 100); // Zoom out to show overview

        } else {

            // Show ports and paths
            portFigure.setVisibility(Visibility.VISIBLE);
            portFigure.showPaths();

            // Adjust perspective
            Point centerPoint = getScene().getImages(Base.class).getCenterPoint();
            double scale = 0.6;
            setCoordinate(centerPoint);
            setScale(scale); // Zoom out to show overview

        }

        /*
        // Show the ports in the path
        List<Path> portPaths = getCamera().getScene().getUniverse().getGraph(port);
        List<Port> portConnections = getCamera().getScene().getUniverse().getPorts(portPaths);
        for (Port portConnection: portConnections) {
            PortImage portFigureConnection = (PortImage) getCamera().getScene().getImage(portConnection);
            portFigureConnection.setVisibility(true);
            portFigureConnection.showPathImages();
        }
        */

    }

    public void focusMoveView(Action action) {

        // Move perspective
        Process process = action.getActionSequence();
        setOffset(process.offsetX, process.offsetY);
    }

    public void focusSelectBase(Action action) {

        Actor actor = action.getActor();
        Process process = action.getActionSequence();

        if (process.isDragging()) {

            // Zoom out to show overview
            setScale(0.8);
            //adjustScale();

        } else {

            BaseImage baseFigure = (BaseImage) action.getTarget();

            // <UPDATE_PERSPECTIVE>
            // Remove focus from other form
            ImageGroup otherFormFigures = getScene().getImages().filterType(Base.class, Patch.class).remove(baseFigure);
            for (Image image : otherFormFigures.getList()) {
//                image.hidePortImages();
//                image.hidePathImages();
                image.setTransparency(0.1f);
            }

            Process previousProcess = null;
            if (actor.processes.size() > 1) {
                previousProcess = actor.processes.get(actor.processes.size() - 2);
                Log.v("PreviousTouch", "Previous: " + previousProcess.getFirstAction().getTarget());
                Log.v("PreviousTouch", "Current: " + action.getTarget());
            }

            // Camera
            if (baseFigure.getBase().getPaths().size() > 0
                    && (previousProcess != null && previousProcess.getFirstAction().getTarget() != action.getTarget())) {

                Log.v("Touch_", "A");

//                for (PortImage portImage : baseImage.getPortImages()) {
//                    List<PathImage> pathImages = portImage.getPathFigures();
//                    for (PathImage pathImage : pathImages) {
//                        pathImage.setVisibility(Visibility.INVISIBLE);
//                    }
//                }

                // Get ports along every path connected to the ports on the touched form
                List<Port> formPathPorts = new ArrayList<>();
                for (Port port : baseFigure.getBase().getPorts()) {

                    // TODO: ((PortImage) getCamera().getScene().getImage(port)).getVisiblePaths()

                    if (!formPathPorts.contains(port)) {
                        formPathPorts.add(port);
                    }

                    List<Path> portPaths = port.getGraph();
                    for (Path path : portPaths) {
                        if (!formPathPorts.contains(path.getSource())) {
                            formPathPorts.add(path.getSource());
                        }
                        if (!formPathPorts.contains(path.getTarget())) {
                            formPathPorts.add(path.getTarget());
                        }
                    }
                }

                // Camera
                List<Image> formPathPortImages = getScene().getImages(formPathPorts);
                List<Point> formPortPositions = Scene.getCoordinates(formPathPortImages);
                Rectangle boundingBox = Geometry.calculateBoundingBox(formPortPositions);

                adjustScale(boundingBox);
                setCoordinate(boundingBox.getCoordinate());

            } else {

                Log.v("Touch_", "B");

                // Do this on second press, or when none of the machine's ports have paths.
                // This provides lookahead, so you can be triggered to processAction again to recover
                // the perspective.

//                for (PortImage portImage : baseImage.getPortImages()) {
//                    List<PathImage> pathImages = portImage.getPathFigures();
//                    for (PathImage pathImage : pathImages) {
//                        pathImage.setVisibility(Visibility.INVISIBLE);
//                    }
//                }

                // TODO: (on second press, also hide external ports, send peripherals) getCamera().setScale(1.2f);
                // TODO: (cont'd) getCamera().setCoordinate(baseImage.getCoordinate());

                setScale(1.2f);
                setCoordinate(baseFigure.getCoordinate());
            }
            // </UPDATE_PERSPECTIVE>
        }

    }

    public void focusSelectPath(Port port) {

        // Remove focus from other forms and their ports
        List<Image> baseImages = getScene().getImages(Base.class).getList();
        for (int i = 0; i < baseImages.size(); i++) {
            BaseImage baseFigure = (BaseImage) baseImages.get(i);
            baseFigure.setTransparency(0.05f);
            baseFigure.hidePortImages();
            baseFigure.hidePathImages();
        }

        List<Path> paths = port.getGraph();
        for (Path connectedPath : paths) {

            // Show ports
            ((PortImage) getScene().getImage(connectedPath.getSource())).setVisibility(Visibility.VISIBLE);
            ((PortImage) getScene().getImage(connectedPath.getSource())).showPaths();
            ((PortImage) getScene().getImage(connectedPath.getTarget())).setVisibility(Visibility.VISIBLE);
            ((PortImage) getScene().getImage(connectedPath.getTarget())).showPaths();

            // Show path
            getScene().getImage(connectedPath).setVisibility(Visibility.VISIBLE);
        }

        // Camera
        List<Port> pathPorts = port.getPorts(paths);
        List<Image> pathPortImages = getScene().getImages(pathPorts);
        List<Point> pathPortPositions = Scene.getCoordinates(pathPortImages);
        Rectangle boundingBox = Geometry.calculateBoundingBox(pathPortPositions);

        // Camera Scale
        adjustScale(boundingBox);

        // Camera Position
        setCoordinate(Geometry.calculateCenterCoordinate(pathPortPositions));
    }

    public void focusSelectScene() { // Previously called "focusReset"

        // No points on board or port. Touch is on map. So hide ports.
        ImageGroup baseFigures = getScene().getImages(Base.class);
        for (int i = 0; i < baseFigures.getList().size(); i++) {
            BaseImage baseFigure = (BaseImage) baseFigures.get(i);
            baseFigure.hidePortImages();
            baseFigure.hidePathImages();
            baseFigure.setTransparency(1.0);
        }

        ImageGroup patchFigures = getScene().getImages(Patch.class);
        for (int i = 0; i < patchFigures.getList().size(); i++) {
            PatchImage patchFigure = (PatchImage) patchFigures.get(i);
            patchFigure.hidePortFigures();
            patchFigure.hidePathFigures();
            patchFigure.setTransparency(1.0);
        }

        // Adjust scale and position
        adjustScale();
        adjustPosition();
    }

    public void adjustPosition() {
        List<Point> figurePositions = getScene().getImages().filterType(Base.class, Patch.class).getCoordinates();
        Point centerPosition = Geometry.calculateCenterCoordinate(figurePositions);
        setCoordinate(centerPosition);
    }

    public void adjustScale() {
        adjustScale(Camera.DEFAULT_SCALE_PERIOD);
    }

    public void adjustScale(double duration) {
        List<Point> figureVertices = getScene().getImages().filterType(Base.class, Patch.class).getVertices();
        if (figureVertices.size() > 0) {
            Rectangle boundingBox = getScene().getImages().filterType(Base.class, Patch.class).getBoundingBox();
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
