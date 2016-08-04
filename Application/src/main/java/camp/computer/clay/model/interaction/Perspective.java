package camp.computer.clay.model.interaction;

import android.util.Log;

import java.util.ArrayList;

import camp.computer.clay.application.Application;
import camp.computer.clay.model.simulation.Path;
import camp.computer.clay.model.simulation.Port;
import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.architecture.ImageGroup;
import camp.computer.clay.visualization.architecture.Visualization;
import camp.computer.clay.visualization.images.FrameImage;
import camp.computer.clay.visualization.images.PathImage;
import camp.computer.clay.visualization.images.PortImage;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.Point;
import camp.computer.clay.visualization.util.Rectangle;
import camp.computer.clay.visualization.util.Time;

public class Perspective {

    // TODO: Move position into Body, so can share Perspective among different bodies
    // ^ actually NO, because then a Body couldn't adopt a different Perspective

    private double width; // Width of perspective --- interactions (e.g., touches) are interpreted relative to this point
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

    // The visualization displayed from this perspective
    private Visualization visualization;

    // Focus in Perspective
    // TODO: Infer this from interaction history/perspective
    private Image focusImage = null;

    private boolean isMovable = true;

    public Perspective(Visualization visualization) {
        this.visualization = visualization;
    }

    public Point getPosition() {
        return this.position;
    }

    public final double DEFAULT_SCALE = 1.0f;
    public static final int DEFAULT_SCALE_PERIOD = 250;

    public final Point DEFAULT_POSITION = new Point(0, 0);
    public static final double DEFAULT_ADJUSTMENT_PERIOD = 250;

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

        if (targetPosition.getX() == position.getX() && targetPosition.getY() == position.getY()) {
            return;
        }

        /*
        // Solution 1: This works without per-frame adjustment. It's a starting point for that.
        // this.targetPosition.setX(-targetPosition.getX() * targetScale);
        // this.targetPosition.setY(-targetPosition.getY() * targetScale);
        */

        this.targetPosition.setX(-targetPosition.getX());
        this.targetPosition.setY(-targetPosition.getY());

        // <PLAN_ANIMATION>
        originalPosition.set(position);

        positionFrameLimit = (int) (Application.getDisplay().getFramesPerSecond() * (duration / Time.MILLISECONDS_PER_SECOND));
        // ^ use positionFrameLimit as index into function to change animation by maing stepDistance vary with positionFrameLimit
        positionFrameIndex = 0;
        // </PLAN_ANIMATION>
    }

    public void setOffset(double xOffset, double yOffset) {
        this.position.offset(xOffset, yOffset);
        this.originalPosition.offset(xOffset, yOffset);
        this.targetPosition.offset(xOffset, yOffset);
    }

    public void setScale(double targetScale) {
        setScale(targetScale, scalePeriod);
    }

    public void setScale(double targetScale, double duration) {

        this.targetScale = targetScale;

        if (duration == 0) {
            this.scale = targetScale;
        } else {
            double frameCount = Application.getDisplay().getFramesPerSecond() * (duration / Time.MILLISECONDS_PER_SECOND);
            // ^ use positionFrameLimit as index into function to change animation by maing stepDistance vary with positionFrameLimit
            scaleDelta = Math.abs(targetScale - scale) / frameCount;
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

            double newX = currentDistanceTarget * totalDistanceToTargetX + originalPosition.getX();
            double newY = currentDistanceTarget * totalDistanceToTargetY + originalPosition.getY();

            position.set(
                    newX * scale,
                    newY * scale
            );

            positionFrameIndex++;

        } else if (positionFrameIndex == positionFrameLimit) {

            position.setX(targetPosition.getX() * scale);
            position.setY(targetPosition.getY() * scale);

        }

    }

    public void setAdjustability(boolean isAdjustable) {
        this.isMovable = isAdjustable;
    }

    public boolean isAdjustable() {
        return isMovable;
    }

    public Visualization getVisualization() {
        return this.visualization;
    }

    public Image getFocusImage() {
        return this.focusImage;
    }

    public boolean hasFocusImage() {
        return this.focusImage != null;
    }

    public void setFocusImage(Image image) {
        this.focusImage = image;
    }

    public void focusOnPort(PortImage portImage) {
        Log.v("TouchedImage", "focusOnPort");

//        // Perspective
//        if (getPerspective().getFocusImage().isType(PathImage.TYPE)) {
//            PathImage focusedPathImage = (PathImage) getPerspective().getFocusImage();
//            Path path = (Path) focusedPathImage.getModel();
//            if (path.getSource() == portImage.getPort()) {
//                // <PERSPECTIVE>
//                getPerspective().setFocusImage(portImage);
//                getPerspective().setAdjustability(false);
//                // </PERSPECTIVE>
//            }
//        } else {
        // <PERSPECTIVE>
        setFocusImage(portImage);
        setAdjustability(false);
        // </PERSPECTIVE>

    }

    public void focusOnNewPath(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        PortImage portImage = (PortImage) touchInteraction.getTarget();

        // Show ports of nearby forms
        ImageGroup nearbyImages = getVisualization().getImages().filterType(FrameImage.TYPE).filterDistance(touchInteraction.getPosition(), 200 + 60);
        for (Image image : getVisualization().getImages().filterType(FrameImage.TYPE).getList()) {

            if (image == portImage.getFormImage() || nearbyImages.contains(image)) {

                FrameImage nearbyFrameImage = (FrameImage) image;
                nearbyFrameImage.setTransparency(1.0f);
                nearbyFrameImage.showPortImages();

            } else {

                FrameImage nearbyFrameImage = (FrameImage) image;
                nearbyFrameImage.setTransparency(0.1f);

                // TODO: Fix the glitching caused by enabling this.
                // nearbyFrameImage.hidePortImages();

            }
        }

        // Check if a machine sprite was nearby
        Image nearestFormImage = getVisualization().getImages().filterType(FrameImage.TYPE).getNearest(touchInteraction.getPosition());
        if (nearestFormImage != null) {

            // TODO: Vibrate

            // Adjust perspective
            //getPerspective().setPosition(nearestFormImage.getPosition());
            setScale(0.6f, 100); // Zoom out to show overview

        } else {

            // Show ports and paths
            portImage.setVisibility(true);
            portImage.showPaths();

            // Adjust perspective
            setPosition(getVisualization().getImages().filterType(FrameImage.TYPE).calculateCenter());
            setScale(0.6f); // Zoom out to show overview

        }

                    /*
                    // Show the ports in the path
                    ArrayList<Path> portPaths = getPerspective().getVisualization().getSimulation().getPathsByPort(port);
                    ArrayList<Port> portConnections = getPerspective().getVisualization().getSimulation().getPortsInPaths(portPaths);
                    for (Port portConnection: portConnections) {
                        PortImage portImageConnection = (PortImage) getPerspective().getVisualization().getImage(portConnection);
                        portImageConnection.setVisibility(true);
                        portImageConnection.showPathImages();
                    }
                    */

    }

    public void focusOnPerspectiveAdjustment(TouchInteractivity touchInteractivity) {

        // Dragging perspective

        Log.v("Drag", "moving perspective");

//                if (getPerspective().isAdjustable()) {
//                    getPerspective().setScale(0.9f);
//                    getPerspective().setOffset(
//                            touchInteraction.getPosition().getX() - touchInteractivity.getFirst().getPosition().getX(),
//                            touchInteraction.getPosition().getY() - touchInteractivity.getFirst().getPosition().getY()
//                    );
        setOffset(touchInteractivity.offsetX, touchInteractivity.offsetY);
//                    getPerspective().setPosition(
//                            new Point(
//                                    -(touchInteraction.getPosition().getX() - touchInteractivity.getFirst().getPosition().getX()),
//                                    -(touchInteraction.getPosition().getY() - touchInteractivity.getFirst().getPosition().getY())
//                            )
//                    );
//                    getPerspective().setPosition(touchInteraction.getPosition(), 0);
//                        (int) (touchInteraction.getPosition().getX() - touchInteractivity.getFirst().getPosition().getX()),
//                        (int) (touchInteraction.getPosition().getY() - touchInteractivity.getFirst().getPosition().getY()));
//                }

    }

    public void focusOnFrame(Body body, TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        if (touchInteractivity.isDragging[touchInteraction.pointerIndex]) {

            adjustScale();

        } else {
            FrameImage frameImage = (FrameImage) touchInteraction.getTarget();

            // <UPDATE_PERSPECTIVE>
            // Remove focus from other form
            ImageGroup otherFormImages = getVisualization().getImages().filterType(FrameImage.TYPE).remove(frameImage);
            for (Image image : otherFormImages.getList()) {
                FrameImage otherFrameImage = (FrameImage) image;
                otherFrameImage.hidePortImages();
                otherFrameImage.hidePathImages();
                otherFrameImage.setTransparency(0.1f);
            }

            TouchInteractivity previousInteractivity = null;
            if (body.touchInteractivities.size() > 1) {
                previousInteractivity = body.touchInteractivities.get(body.touchInteractivities.size() - 2);
                Log.v("PreviousTouch", "Previous: " + previousInteractivity.getFirst().getTarget());
                Log.v("PreviousTouch", "Current: " + touchInteraction.getTarget());
            }

            // Perspective
            if (frameImage.getForm().getPaths().size() > 0
                    && (previousInteractivity != null && previousInteractivity.getFirst().getTarget() != touchInteraction.getTarget())) {

                Log.v("Touch_", "A");

                for (PortImage portImage : frameImage.getPortImages()) {
                    ArrayList<PathImage> pathImages = portImage.getPathImages();
                    for (PathImage pathImage : pathImages) {
                        pathImage.setVisibility(false);
                    }
                }

                // Get ports along every path connected to the ports on the touched form
                ArrayList<Port> formPathPorts = new ArrayList<>();
                for (Port port : frameImage.getForm().getPorts()) {

                    // TODO: ((PortImage) getPerspective().getVisualization().getImage(port)).getVisiblePaths()

                    if (!formPathPorts.contains(port)) {
                        formPathPorts.add(port);
                    }

                    ArrayList<Path> portPaths = port.getPathsByPort();
                    for (Path path : portPaths) {
                        if (!formPathPorts.contains(path.getSource())) {
                            formPathPorts.add(path.getSource());
                        }
                        if (!formPathPorts.contains(path.getTarget())) {
                            formPathPorts.add(path.getTarget());
                        }
                    }
                }

                // Perspective
                ArrayList<Image> formPathPortImages = getVisualization().getImages(formPathPorts);

                ArrayList<Point> formPortPositions = Visualization.getPositions(formPathPortImages);
                Rectangle boundingBox = Geometry.calculateBoundingBox(formPortPositions);

                setAdjustability(false);
                adjustScale(boundingBox);
                setPosition(boundingBox.getPosition());

            } else {

                Log.v("Touch_", "B");

                // Do this on second press, or when none of the machine's ports have paths.
                // This provides lookahead, so you can be triggered to touch again to recover
                // the perspective.

                for (PortImage portImage : frameImage.getPortImages()) {
                    ArrayList<PathImage> pathImages = portImage.getPathImages();
                    for (PathImage pathImage : pathImages) {
                        pathImage.setVisibility(false);
                    }
                }

                // TODO: (on second press, also hide external ports, send peripherals) getPerspective().setScale(1.2f);
                // TODO: (cont'd) getPerspective().setPosition(frameImage.getPosition());

                setAdjustability(false);
                setScale(1.2f);
                setPosition(frameImage.getPosition());
            }
            // </UPDATE_PERSPECTIVE>
        }

    }

    public void focusOnPath(Port sourcePort) {

        // Remove focus from other forms and their ports
        for (FrameImage frameImage : getVisualization().getFrameImages()) {
            frameImage.setTransparency(0.05f);
            frameImage.hidePortImages();
            frameImage.hidePathImages();
        }

//        PortImage sourcePortImage = ((PortImage) getVisualization().getImage(sourcePort));
//        PortImage sourcePortImage = ((PortImage) getVisualization().getImage(sourcePort));
//        sourcePortImage.setVisibility(true);
//        sourcePortImage.showPaths();
//        targetPortImage.setVisibility(true);
//        targetPortImage.showPaths();
//        pathImage.setVisibility(true);

        ArrayList<Path> paths = sourcePort.getPathsByPort();
        for (Path connectedPath : paths) {
            // Show ports
            ((PortImage) getVisualization().getImage(connectedPath.getSource())).setVisibility(true);
            ((PortImage) getVisualization().getImage(connectedPath.getSource())).showPaths();
            ((PortImage) getVisualization().getImage(connectedPath.getTarget())).setVisibility(true);
            ((PortImage) getVisualization().getImage(connectedPath.getTarget())).showPaths();
            // Show path
            getVisualization().getImage(connectedPath).setVisibility(true);
        }

        // Perspective
        ArrayList<Port> pathPorts = sourcePort.getPortsInPaths(paths);
        ArrayList<Image> pathPortImages = getVisualization().getImages(pathPorts);
        ArrayList<Point> pathPortPositions = Visualization.getPositions(pathPortImages);
        setPosition(Geometry.calculateCenterPosition(pathPortPositions));

        Rectangle boundingBox = Geometry.calculateBoundingBox(pathPortPositions);

        adjustScale(boundingBox);
    }

    public void focusReset() {

        // No touchPositions on board or port. Touch is on map. So hide ports.
        for (FrameImage frameImage : getVisualization().getFrameImages()) {
            frameImage.hidePortImages();
            frameImage.hidePathImages();
            frameImage.setTransparency(1.0f);
        }

        ArrayList<Point> formImagePositions = getVisualization().getImages().filterType(FrameImage.TYPE).getPositions();
        Point formImagesCenterPosition = Geometry.calculateCenterPosition(formImagePositions);

        adjustScale();

        //getPerspective().setPosition(getPerspective().getVisualization().getList().filterType(FrameImage.TYPE).calculateCentroid());
        setPosition(formImagesCenterPosition);

        // Reset map interactivity
        setAdjustability(true);

    }

    public void adjustPosition() {
        ArrayList<Point> formImagePositions = getVisualization().getImages().filterType(FrameImage.TYPE).getPositions();
        Point formImagesCenterPosition = Geometry.calculateCenterPosition(formImagePositions);
        setPosition(formImagesCenterPosition);

    }

    public void adjustScale() {
        adjustScale(Perspective.DEFAULT_SCALE_PERIOD);
    }

    public void adjustScale(double duration) {
        ArrayList<Point> formImagePositions = getVisualization().getImages().filterType(FrameImage.TYPE).getPositions();
        if (formImagePositions.size() > 0) {
            Rectangle boundingBox = Geometry.calculateBoundingBox(formImagePositions);
            adjustScale(boundingBox, duration);
        }
    }

    public void adjustScale(Rectangle boundingBox) {
        adjustScale(boundingBox, Perspective.DEFAULT_SCALE_PERIOD);
    }

    public void adjustScale(Rectangle boundingBox, double duration) {

        double horizontalDifference = boundingBox.getWidth() - getWidth();
        double verticalDifference = boundingBox.getHeight() - getHeight();

        double horizontalScale = getWidth() / boundingBox.getWidth();
        double verticalScale = getHeight() / boundingBox.getHeight();

        if (horizontalDifference > 0 && horizontalDifference > verticalDifference) {
            setScale(horizontalScale, duration);
        } else if (verticalDifference > 0 && verticalDifference > horizontalDifference) {
            setScale(verticalScale, duration);
        } else {
            setScale(1.0f, duration);
        }
    }
}
