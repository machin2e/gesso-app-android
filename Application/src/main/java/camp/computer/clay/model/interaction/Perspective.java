package camp.computer.clay.model.interaction;

import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.app.Application;
import camp.computer.clay.app.R;
import camp.computer.clay.model.sim.Body;
import camp.computer.clay.model.sim.Frame;
import camp.computer.clay.model.sim.Path;
import camp.computer.clay.model.sim.Port;
import camp.computer.clay.viz.arch.Image;
import camp.computer.clay.model.data.ImageGroup;
import camp.computer.clay.viz.arch.Visibility;
import camp.computer.clay.viz.arch.Viz;
import camp.computer.clay.viz.img.FrameImage;
import camp.computer.clay.viz.img.PathImage;
import camp.computer.clay.viz.img.PortImage;
import camp.computer.clay.viz.util.Geometry;
import camp.computer.clay.viz.util.Point;
import camp.computer.clay.viz.util.Rectangle;
import camp.computer.clay.viz.util.Time;

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

    // The viz displayed from this perspective
    private Viz viz;

    // Focus in Perspective
    // TODO: Infer this from interaction history/perspective
    private Image focusImage = null;

    private boolean isMovable = true;

    public Perspective(Viz viz) {
        this.viz = viz;
    }

    public Point getPosition() {
        return this.position;
    }

    public final double DEFAULT_SCALE = 1.0f;
    public static final int DEFAULT_SCALE_PERIOD = 175;

    public final Point DEFAULT_POSITION = new Point(0, 0);
    public static final double DEFAULT_ADJUSTMENT_PERIOD = 175;

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

    public Viz getViz() {
        return this.viz;
    }

    public Image getFocus() {
        return this.focusImage;
    }

    public boolean hasFocus() {
        return this.focusImage != null;
    }

    public void setFocus(Image image) {
        this.focusImage = image;
    }

    public void touch_focusReset() {
        Log.v("TouchedImage", "touch_focusReset");

        // <PERSPECTIVE>
        setFocus(null);
        // this.isAdjustable = false;
        // </PERSPECTIVE>

    }

//    public void touch_focusOnForm(FrameImage formImage) {
//        Log.v("TouchedImage", "touch_focusOnForm");
//
//        setFocus(formImage);
//        setAdjustability(false);
//    }

    public void touch_focusOnPort(PortImage portImage) {
        Log.v("TouchedImage", "touch_focusOnPort");

//        // Perspective
//        if (getPerspective().getFocus().isType(PathImage.TYPE)) {
//            PathImage focusedPathImage = (PathImage) getPerspective().getFocus();
//            Path path = (Path) focusedPathImage.getModel();
//            if (path.getSource() == portImage.getPort()) {
//                // <PERSPECTIVE>
//                getPerspective().setFocus(portImage);
//                getPerspective().setAdjustability(false);
//                // </PERSPECTIVE>
//            }
//        } else {
        // <PERSPECTIVE>
        setFocus(portImage);
        setAdjustability(false);
        // </PERSPECTIVE>

    }

    public void touch_focusOnPath(PathImage pathImage) {
        Log.v("TouchedImage", "touch_focusOnPath");

        setFocus(pathImage);
        setAdjustability(false);

    }

    public void drag_focusOnFrame() {
        Log.v("Focus", "drag_focusOnFrame");

        // Perspective (zoom out to show overview)
        // adjustPerspectivePosition();
        adjustPerspectiveScale();

    }

    public void drag_focusOnPortNewPath(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        PortImage portImage = (PortImage) touchInteraction.getTarget();

        // Show ports of nearby forms
        ImageGroup nearbyImages = getViz().getImages().old_filterType(FrameImage.TYPE).filterDistance(touchInteraction.getPosition(), 200 + 60);
        for (Image image : getViz().getImages().old_filterType(FrameImage.TYPE).getList()) {

            if (image == portImage.getFrameImage() || nearbyImages.contains(image)) {

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
        Image nearestFormImage = getViz().getImages().old_filterType(FrameImage.TYPE).getNearest(touchInteraction.getPosition());
        if (nearestFormImage != null) {

            // TODO: Vibrate

            // Adjust perspective
            //getPerspective().setPosition(nearestFormImage.getPosition());
            setScale(0.3f, 100); // Zoom out to show overview

        } else {

            // Show ports and paths
            portImage.setVisibility(Visibility.VISIBLE);
            portImage.showPaths();

            // Adjust perspective
            setPosition(getViz().getImages().old_filterType(FrameImage.TYPE).calculateCenter());
            setScale(0.6f); // Zoom out to show overview

        }

                    /*
                    // Show the ports in the path
                    ArrayList<Path> portPaths = getPerspective().getViz().getSimulation().getConnectedPaths(port);
                    ArrayList<Port> portConnections = getPerspective().getViz().getSimulation().getPortsByPath(portPaths);
                    for (Port portConnection: portConnections) {
                        PortImage portImageConnection = (PortImage) getPerspective().getViz().getImage(portConnection);
                        portImageConnection.setVisibility(true);
                        portImageConnection.showPathImages();
                    }
                    */

    }

    public void drag_focusReset(TouchInteractivity touchInteractivity) {

        // Dragging perspective

        Log.v("Drag", "moving perspective");

//                if (getPerspective().isAdjustable()) {
//                    getPerspective().setScale(0.9f);
//                    getPerspective().setOffset(
//                            touchInteraction.getPosition().getX() - touchInteractivity.getFirst().getPosition().getX(),
//                            touchInteraction.getPosition().getY() - touchInteractivity.getFirst().getPosition().getY()
//                    );
//        setOffset(touchInteractivity.offsetX, touchInteractivity.offsetY);
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

    public void tap_focusOnFrame(Body body, TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        FrameImage frameImage = (FrameImage) touchInteraction.getTarget();

        // <UPDATE_PERSPECTIVE>
        // Remove focus from other form
        ImageGroup otherFormImages = getViz().getImages().old_filterType(FrameImage.TYPE).remove(frameImage);
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
        if (frameImage.getFrame().getPaths().size() > 0
                && (previousInteractivity != null && previousInteractivity.getFirst().getTarget() != touchInteraction.getTarget())) {

            Log.v("Touch_", "A");

            for (PortImage portImage : frameImage.getPortImages()) {
                List<PathImage> pathImages = portImage.getPathImages();
                for (PathImage pathImage : pathImages) {
                    pathImage.setVisibility(false);
                }
            }

            // Get ports along every path connected to the ports on the touched form
            List<Port> formPathPorts = new ArrayList<>();
            for (Port port : frameImage.getFrame().getPorts()) {

                // TODO: ((PortImage) getPerspective().getViz().getImage(port)).getVisiblePaths()

                if (!formPathPorts.contains(port)) {
                    formPathPorts.add(port);
                }

                List<Path> portPaths = port.getConnectedPaths();
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
            List<Point> formPortPositions = getViz().getImages(formPathPorts).getPositions();
            ;
            Rectangle boundingBox = Geometry.calculateBoundingBox(formPortPositions);

            setAdjustability(false);
            adjustPerspectiveScale(boundingBox);
            setPosition(boundingBox.getPosition());

        } else {

            Log.v("Touch_", "B");

            // Do this on second press, or when none of the machine's ports have paths.
            // This provides lookahead, so you can be triggered to touch again to recover
            // the perspective.

            for (PortImage portImage : frameImage.getPortImages()) {
                List<PathImage> pathImages = portImage.getPathImages();
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

    public void tap_focusOnPath() {

        // TODO: Show path programmer (Create and curate actions built in JS by inserting
        // TODO: (cont'd) exposed ports into action ports. This defines a path)
        final RelativeLayout timelineView = (RelativeLayout) Application.getDisplay().findViewById(R.id.path_editor_view);
        timelineView.setVisibility(View.VISIBLE);

    }

    public void release_focusOnPath(Port sourcePort) {

        // Remove focus from other forms and their ports
        for (Image image : getViz().getImages().filterType(Frame.class).getList()) {
            FrameImage frameImage = (FrameImage) image;
            frameImage.setTransparency(0.05f);
            frameImage.hidePortImages();
            frameImage.hidePathImages();
        }

//        PortImage sourcePortImage = ((PortImage) getViz().getImage(sourcePort));
//        PortImage sourcePortImage = ((PortImage) getViz().getImage(sourcePort));
//        sourcePortImage.setVisibility(true);
//        sourcePortImage.showPaths();
//        targetPortImage.setVisibility(true);
//        targetPortImage.showPaths();
//        pathImage.setVisibility(true);

        List<Path> paths = sourcePort.getConnectedPaths();
        for (Path connectedPath : paths) {
            // Show ports
            ((PortImage) getViz().getImage(connectedPath.getSource())).setVisibility(Visibility.VISIBLE);
            ((PortImage) getViz().getImage(connectedPath.getSource())).showPaths();
            ((PortImage) getViz().getImage(connectedPath.getTarget())).setVisibility(Visibility.VISIBLE);
            ((PortImage) getViz().getImage(connectedPath.getTarget())).showPaths();
            // Show path
            getViz().getImage(connectedPath).setVisibility(Visibility.VISIBLE);
        }

        // Perspective
        List<Port> pathPorts = sourcePort.getPortsByPath(paths);
        List<Point> pathPortPositions = getViz().getImages(pathPorts).getPositions();
        setPosition(Geometry.calculateCenter(pathPortPositions));

        Rectangle boundingBox = Geometry.calculateBoundingBox(pathPortPositions);

        adjustPerspectiveScale(boundingBox);
    }

    public void focusReset() {

        // No touchPositions on board or port. Touch is on map. So hide ports.
        for (Image image : getViz().getImages().filterType(Port.class).getList()) {
            image.setVisibility(Visibility.INVISIBLE);
//            FrameImage frameImage = (FrameImage) image;
//            frameImage.hidePortImages();
//            frameImage.hidePathImages();
//            frameImage.setTransparency(1.0f);
        }

        List<Point> frameImagePositions = getViz().getImages().filterType(Frame.class).getPositions();
//        List<Point> frameImagePositions = getViz().getImages().getPositions();
        Point frameImagesCenterPosition = Geometry.calculateCenter(frameImagePositions);

        adjustPerspectiveScale();

        //getPerspective().setPosition(getPerspective().getViz().getList().old_filterType(FrameImage.TYPE).calculateCentroid());
//        Log.v("Touch", "CENTER: " + frameImagesCenterPosition.getX() + ", " + frameImagesCenterPosition.getY());
        setPosition(frameImagesCenterPosition);

        // Reset map interactivity
        setAdjustability(true);

    }

    public void adjustPerspectivePosition() {
        setPosition(getViz().getImages().old_filterType(FrameImage.TYPE).calculateCentroid());
//        getPerspective().setPosition(getPerspective().getViz().getList().old_filterType(FrameImage.TYPE).calculateCenter());
    }

    public void adjustPerspectiveScale() {
        adjustPerspectiveScale(Perspective.DEFAULT_SCALE_PERIOD);
    }

    public void adjustPerspectiveScale(double duration) {
        Rectangle boundingBox = getViz().getImages().filterType(Frame.class).calculateBoundingBox();
//        if (formImagePositions.size() > 0) {
//            Rectangle boundingBox = Geometry.calculateBoundingBox(formImagePositions);
        adjustPerspectiveScale(boundingBox, duration);
//        }
    }

    public void adjustPerspectiveScale(Rectangle boundingBox) {
        adjustPerspectiveScale(boundingBox, Perspective.DEFAULT_SCALE_PERIOD);
    }

    public void adjustPerspectiveScale(Rectangle boundingBox, double duration) {

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
