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
import camp.computer.clay.scene.architecture.Figure;
import camp.computer.clay.scene.architecture.FigureSet;
import camp.computer.clay.scene.architecture.Scene;
import camp.computer.clay.scene.figure.BaseFigure;
import camp.computer.clay.scene.figure.PatchFigure;
import camp.computer.clay.scene.figure.PortFigure;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Rectangle;
import camp.computer.clay.scene.util.Time;
import camp.computer.clay.scene.util.Visibility;

public class Camera {

    // TODO: Caption generation for each Perspective/Camera

    public static double MAXIMUM_SCALE = 1.0;

    private double width; // Width of perspective --- actionSequences (e.g., touches) are interpreted relative to this point

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
    private Figure focusFigure = null;

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

    public void setPosition(Point targetPosition) {
        setPosition(targetPosition, positionPeriod);
    }

    public void setPosition(Point targetPosition, double duration) {

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

        PortFigure portFigure = (PortFigure) action.getTarget();

        // Show ports of nearby forms
        FigureSet nearbyFigures = getScene().getFigures(Base.class, Patch.class).filterProximity(action.getPosition(), 200 + 60);

        List<Figure> figures = getScene().getFigures(Base.class, Patch.class).getList();
        for (int i = 0; i < figures.size(); i++) {
            Figure figure = figures.get(i);

            if (figure == portFigure.getParentFigure() || nearbyFigures.contains(figure)) {

                if (figure instanceof BaseFigure) {
                    BaseFigure nearbyFigure = (BaseFigure) figure;
                    nearbyFigure.setTransparency(1.0f);
                    nearbyFigure.showPortFigures();
                } else if (figure instanceof PatchFigure) {
                    PatchFigure nearbyFigure = (PatchFigure) figure;
                    nearbyFigure.setTransparency(1.0f);
                    nearbyFigure.showPortFigures();
                }

            } else {

                if (figure instanceof BaseFigure) {
                    BaseFigure nearbyFigure = (BaseFigure) figure;
                    nearbyFigure.setTransparency(0.1f);
                    nearbyFigure.hidePortFigures();
                } else if (figure instanceof PatchFigure) {
                    PatchFigure nearbyFigure = (PatchFigure) figure;
                    nearbyFigure.setTransparency(0.1f);
                    nearbyFigure.hidePortFigures();
                }

            }
        }

        // Check if a machine sprite was nearby
        Figure nearestFormFigure = getScene().getFigures().filterType(Base.class).getNearest(action.getPosition());
        if (nearestFormFigure != null) {

            // TODO: Vibrate

            // Adjust perspective
            //getCamera().setPosition(nearestFormFigure.getPosition());
            setScale(0.6f, 100); // Zoom out to show overview

        } else {

            // Show ports and paths
            portFigure.setVisibility(Visibility.VISIBLE);
            portFigure.showPaths();

            // Adjust perspective
            Point centerPoint = getScene().getFigures(Base.class).getCenterPoint();
            double scale = 0.6;
            setPosition(centerPoint);
            setScale(scale); // Zoom out to show overview

        }

        /*
        // Show the ports in the path
        List<Path> portPaths = getCamera().getScene().getModel().getGraph(port);
        List<Port> portConnections = getCamera().getScene().getModel().getPorts(portPaths);
        for (Port portConnection: portConnections) {
            PortFigure portFigureConnection = (PortFigure) getCamera().getScene().getFigure(portConnection);
            portFigureConnection.setVisibility(true);
            portFigureConnection.showPathFigures();
        }
        */

    }

    public void focusMoveView(Action action) {

        // Move perspective
        ActionSequence actionSequence = action.getActionSequence();
        setOffset(actionSequence.offsetX, actionSequence.offsetY);
    }

    public void focusSelectBase(Action action) {

        Actor actor = action.getActor();
        ActionSequence actionSequence = action.getActionSequence();

        if (actionSequence.isDragging()) {

            // Zoom out to show overview
            setScale(0.8);
            //adjustScale();

        } else {

            BaseFigure baseFigure = (BaseFigure) action.getTarget();

            // <UPDATE_PERSPECTIVE>
            // Remove focus from other form
            FigureSet otherFormFigures = getScene().getFigures().filterType(Base.class, Patch.class).remove(baseFigure);
            for (Figure figure : otherFormFigures.getList()) {
//                figure.hidePortFigures();
//                figure.hidePathFigures();
                figure.setTransparency(0.1f);
            }

            ActionSequence previousActionSequence = null;
            if (actor.actionSequences.size() > 1) {
                previousActionSequence = actor.actionSequences.get(actor.actionSequences.size() - 2);
                Log.v("PreviousTouch", "Previous: " + previousActionSequence.getFirst().getTarget());
                Log.v("PreviousTouch", "Current: " + action.getTarget());
            }

            // Camera
            if (baseFigure.getBase().getPaths().size() > 0
                    && (previousActionSequence != null && previousActionSequence.getFirst().getTarget() != action.getTarget())) {

                Log.v("Touch_", "A");

//                for (PortFigure portImage : baseImage.getPortFigures()) {
//                    List<PathFigure> pathImages = portImage.getPathFigures();
//                    for (PathFigure pathImage : pathImages) {
//                        pathImage.setVisibility(Visibility.INVISIBLE);
//                    }
//                }

                // Get ports along every path connected to the ports on the touched form
                List<Port> formPathPorts = new ArrayList<>();
                for (Port port : baseFigure.getBase().getPorts()) {

                    // TODO: ((PortFigure) getCamera().getScene().getFigure(port)).getVisiblePaths()

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
                List<Figure> formPathPortFigures = getScene().getFigures(formPathPorts);
                List<Point> formPortPositions = Scene.getPositions(formPathPortFigures);
                Rectangle boundingBox = Geometry.calculateBoundingBox(formPortPositions);

                adjustScale(boundingBox);
                setPosition(boundingBox.getPosition());

            } else {

                Log.v("Touch_", "B");

                // Do this on second press, or when none of the machine's ports have paths.
                // This provides lookahead, so you can be triggered to processAction again to recover
                // the perspective.

//                for (PortFigure portImage : baseImage.getPortFigures()) {
//                    List<PathFigure> pathImages = portImage.getPathFigures();
//                    for (PathFigure pathImage : pathImages) {
//                        pathImage.setVisibility(Visibility.INVISIBLE);
//                    }
//                }

                // TODO: (on second press, also hide external ports, send peripherals) getCamera().setScale(1.2f);
                // TODO: (cont'd) getCamera().setPosition(baseImage.getPosition());

                setScale(1.2f);
                setPosition(baseFigure.getPosition());
            }
            // </UPDATE_PERSPECTIVE>
        }

    }

    public void focusSelectPath(Port port) {

        // Remove focus from other forms and their ports
        List<Figure> baseFigures = getScene().getFigures(Base.class).getList();
        for (int i = 0; i < baseFigures.size(); i++) {
            BaseFigure baseFigure = (BaseFigure) baseFigures.get(i);
            baseFigure.setTransparency(0.05f);
            baseFigure.hidePortFigures();
            baseFigure.hidePathFigures();
        }

        List<Path> paths = port.getGraph();
        for (Path connectedPath : paths) {

            // Show ports
            ((PortFigure) getScene().getFigure(connectedPath.getSource())).setVisibility(Visibility.VISIBLE);
            ((PortFigure) getScene().getFigure(connectedPath.getSource())).showPaths();
            ((PortFigure) getScene().getFigure(connectedPath.getTarget())).setVisibility(Visibility.VISIBLE);
            ((PortFigure) getScene().getFigure(connectedPath.getTarget())).showPaths();

            // Show path
            getScene().getFigure(connectedPath).setVisibility(Visibility.VISIBLE);
        }

        // Camera
        List<Port> pathPorts = port.getPorts(paths);
        List<Figure> pathPortFigures = getScene().getFigures(pathPorts);
        List<Point> pathPortPositions = Scene.getPositions(pathPortFigures);
        Rectangle boundingBox = Geometry.calculateBoundingBox(pathPortPositions);

        // Camera Scale
        adjustScale(boundingBox);

        // Camera Position
        setPosition(Geometry.calculateCenterPosition(pathPortPositions));
    }

    public void focusSelectVisualization() { // Previously called "focusReset"

        // No points on board or port. Touch is on map. So hide ports.
        FigureSet baseFigures = getScene().getFigures(Base.class);
        for (int i = 0; i < baseFigures.getList().size(); i++) {
            BaseFigure baseFigure = (BaseFigure) baseFigures.get(i);
            baseFigure.hidePortFigures();
            baseFigure.hidePathFigures();
            baseFigure.setTransparency(1.0);
        }

        FigureSet patchFigures = getScene().getFigures(Patch.class);
        for (int i = 0; i < patchFigures.getList().size(); i++) {
            PatchFigure patchFigure = (PatchFigure) patchFigures.get(i);
            patchFigure.hidePortFigures();
            patchFigure.hidePathFigures();
            patchFigure.setTransparency(1.0);
        }

        // Adjust scale and position
        adjustScale();
        adjustPosition();
    }

    public void adjustPosition() {
        List<Point> figurePositions = getScene().getFigures().filterType(Base.class, Patch.class).getPositions();
        Point centerPosition = Geometry.calculateCenterPosition(figurePositions);
        setPosition(centerPosition);
    }

    public void adjustScale() {
        adjustScale(Camera.DEFAULT_SCALE_PERIOD);
    }

    public void adjustScale(double duration) {
        List<Point> figureVertices = getScene().getFigures().filterType(Base.class, Patch.class).getVertices();
        if (figureVertices.size() > 0) {
            Rectangle boundingBox = getScene().getFigures().filterType(Base.class, Patch.class).getBoundingBox();
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
