package camp.computer.clay.model.interactivity;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.model.architecture.Body;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.architecture.ImageSet;
import camp.computer.clay.visualization.architecture.Visualization;
import camp.computer.clay.visualization.image.PatchImage;
import camp.computer.clay.visualization.image.FrameImage;
import camp.computer.clay.visualization.image.PathImage;
import camp.computer.clay.visualization.image.PortImage;
import camp.computer.clay.visualization.util.geometry.Geometry;
import camp.computer.clay.visualization.util.geometry.Point;
import camp.computer.clay.visualization.util.geometry.Rectangle;
import camp.computer.clay.visualization.util.Time;
import camp.computer.clay.visualization.util.Visibility;

public class Perspective {

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
    // TODO: Infer this from thisInteraction history/perspective
    private Image focusImage = null;

    private boolean isAdjustable = true;

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

        if (duration == 0) {

            this.targetPosition.setX(-targetPosition.getX());
            this.targetPosition.setY(-targetPosition.getY());

            this.originalPosition.set(targetPosition);

            this.position.set(targetPosition);

        } else {

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
    }

    public void setOffset(double xOffset, double yOffset) {
        this.targetPosition.offset(xOffset, yOffset);
        this.originalPosition.offset(xOffset, yOffset);
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
        this.isAdjustable = isAdjustable;
    }

    public boolean isAdjustable() {
        return isAdjustable;
    }

    public Visualization getVisualization() {
        return this.visualization;
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

    public void focusOnNewPath(Interaction interaction, Action action) {

        PortImage portImage = (PortImage) action.getTarget();

        // Show ports of nearby forms
        ImageSet nearbyImages = getVisualization().getImages().filterType(FrameImage.class).filterProximity(action.getPosition(), 200 + 60);
        for (Image image : getVisualization().getImages().filterType(FrameImage.class).getList()) {

            if (image == portImage.getParentImage() || nearbyImages.contains(image)) {

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
        Image nearestFormImage = getVisualization().getImages().filterType(FrameImage.class).getNearest(action.getPosition());
        if (nearestFormImage != null) {

            // TODO: Vibrate

            // Adjust perspective
            //getPerspective().setPosition(nearestFormImage.getPosition());
            setScale(0.6f, 100); // Zoom out to show overview

        } else {

            // Show ports and paths
            portImage.setVisibility(Visibility.VISIBLE);
            portImage.showPaths();

            // Adjust perspective
            setPosition(getVisualization().getImages().filterType(FrameImage.class).getCenterPoint());
            setScale(0.6f); // Zoom out to show overview

        }

                    /*
                    // Show the ports in the path
                    List<Path> portPaths = getPerspective().getVisualization().getEnvironment().getGraph(port);
                    List<Port> portConnections = getPerspective().getVisualization().getEnvironment().getPorts(portPaths);
                    for (Port portConnection: portConnections) {
                        PortImage portImageConnection = (PortImage) getPerspective().getVisualization().getImage(portConnection);
                        portImageConnection.setVisibility(true);
                        portImageConnection.showPathImages();
                    }
                    */

    }

    public void focusOnVisualization(Interaction interaction) {

        // Dragging perspective

        Log.v("Drag", "moving perspective");

        setOffset(interaction.offsetX, interaction.offsetY);
    }

    public void focusOnFrame(Action action) {

        Body body = action.getBody();
        Interaction interaction = action.getInteraction();

        if (interaction.isDragging()) {

            // Zoom out to show overview
            setScale(0.8);
            //adjustScale();

        } else {

            FrameImage frameImage = (FrameImage) action.getTarget();

            // <UPDATE_PERSPECTIVE>
            // Remove focus from other form
            ImageSet otherFormImages = getVisualization().getImages().filterType(FrameImage.class).remove(frameImage);
            for (Image image : otherFormImages.getList()) {
                FrameImage otherFrameImage = (FrameImage) image;
//                otherFrameImage.hidePortImages();
//                otherFrameImage.hidePathImages();
                otherFrameImage.setTransparency(0.1f);
            }

            Interaction previousInteraction = null;
            if (body.interactions.size() > 1) {
                previousInteraction = body.interactions.get(body.interactions.size() - 2);
                Log.v("PreviousTouch", "Previous: " + previousInteraction.getFirst().getTarget());
                Log.v("PreviousTouch", "Current: " + action.getTarget());
            }

            // Perspective
            if (frameImage.getFrame().getPaths().size() > 0
                    && (previousInteraction != null && previousInteraction.getFirst().getTarget() != action.getTarget())) {

                Log.v("Touch_", "A");

//                for (PortImage portImage : frameImage.getPortImages()) {
//                    List<PathImage> pathImages = portImage.getPathImages();
//                    for (PathImage pathImage : pathImages) {
//                        pathImage.setVisibility(Visibility.INVISIBLE);
//                    }
//                }

                // Get ports along every path connected to the ports on the touched form
                List<Port> formPathPorts = new ArrayList<>();
                for (Port port : frameImage.getFrame().getPorts()) {

                    // TODO: ((PortImage) getPerspective().getVisualization().getImage(port)).getVisiblePaths()

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

                // Perspective
                List<Image> formPathPortImages = getVisualization().getImages(formPathPorts);

                List<Point> formPortPositions = Visualization.getPositions(formPathPortImages);
                Rectangle boundingBox = Geometry.calculateBoundingBox(formPortPositions);

                setAdjustability(false);
                adjustScale(boundingBox);
                setPosition(boundingBox.getPosition());

            } else {

                Log.v("Touch_", "B");

                // Do this on second press, or when none of the machine's ports have paths.
                // This provides lookahead, so you can be triggered to processAction again to recover
                // the perspective.

//                for (PortImage portImage : frameImage.getPortImages()) {
//                    List<PathImage> pathImages = portImage.getPathImages();
//                    for (PathImage pathImage : pathImages) {
//                        pathImage.setVisibility(Visibility.INVISIBLE);
//                    }
//                }

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

        List<Path> paths = sourcePort.getGraph();
        for (Path connectedPath : paths) {

            // Show ports
            ((PortImage) getVisualization().getImage(connectedPath.getSource())).setVisibility(Visibility.VISIBLE);
            ((PortImage) getVisualization().getImage(connectedPath.getSource())).showPaths();
            ((PortImage) getVisualization().getImage(connectedPath.getTarget())).setVisibility(Visibility.VISIBLE);
            ((PortImage) getVisualization().getImage(connectedPath.getTarget())).showPaths();

            // Show path
            getVisualization().getImage(connectedPath).setVisibility(Visibility.VISIBLE);
        }

        // Perspective
        List<Port> pathPorts = sourcePort.getPorts(paths);
        List<Image> pathPortImages = getVisualization().getImages(pathPorts);
        List<Point> pathPortPositions = Visualization.getPositions(pathPortImages);
        setPosition(Geometry.calculateCenterPosition(pathPortPositions));

        Rectangle boundingBox = Geometry.calculateBoundingBox(pathPortPositions);

        adjustScale(boundingBox);
    }

    public void focusReset() {

        // No touchPoints on board or port. Touch is on map. So hide ports.
        for (FrameImage frameImage : getVisualization().getFrameImages()) {
            frameImage.hidePortImages();
            frameImage.hidePathImages();
            frameImage.setTransparency(1.0);
        }

        for (Image deviceImageRaw : getVisualization().getImages().filterType(PatchImage.class).getList()) {
            PatchImage patchImage = (PatchImage) deviceImageRaw;
            patchImage.hidePortImages();
            patchImage.hidePathImages();
            patchImage.setTransparency(1.0);
        }

        List<Point> formImagePositions = getVisualization().getImages().filterType(FrameImage.class, PatchImage.class).getPositions();
        Point formImagesCenterPosition = Geometry.calculateCenterPosition(formImagePositions);

        adjustScale();

        //getPerspective().setPosition(getPerspective().getVisualization().getList().filterType(FrameImage.TYPE).getCentroidPoint());
        setPosition(formImagesCenterPosition);

        // Reset map thisInteraction
        setAdjustability(true);

    }

    public void adjustPosition() {
        List<Point> imagePositions = getVisualization().getImages().filterType(FrameImage.class, PatchImage.class).getPositions();
        Point centerPosition = Geometry.calculateCenterPosition(imagePositions);
        setPosition(centerPosition);

    }

    public void adjustScale() {
        adjustScale(Perspective.DEFAULT_SCALE_PERIOD);
    }

    public void adjustScale(double duration) {
        List<Point> imagePositions = getVisualization().getImages().filterType(FrameImage.class, PatchImage.class).getPositions();
        if (imagePositions.size() > 0) {
            Rectangle boundingBox = Geometry.calculateBoundingBox(imagePositions);
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
