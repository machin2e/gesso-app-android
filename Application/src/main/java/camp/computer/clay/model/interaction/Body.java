package camp.computer.clay.model.interaction;

import android.util.Log;

import java.util.ArrayList;

import camp.computer.clay.model.simulation._Actor;
import camp.computer.clay.model.simulation.Path;
import camp.computer.clay.model.simulation.Port;
import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.images.FrameImage;
import camp.computer.clay.visualization.images.PathImage;
import camp.computer.clay.visualization.images.PortImage;
import camp.computer.clay.visualization.util.Geometry;

public class Body extends _Actor {

    private Perspective perspective;

    public ArrayList<TouchInteractivity> touchInteractivities = new ArrayList<>();

    public Body() {
    }

    public void setPerspective(Perspective perspective) {
        this.perspective = perspective;
    }

    public Perspective getPerspective() {
        return this.perspective;
    }

    public TouchInteractivity getLatestTouchInteractivity() {
        if (touchInteractivities.size() > 0) {
            return this.touchInteractivities.get(touchInteractivities.size() - 1);
        } else {
            return null;
        }
    }

    public void onStartInteractivity(TouchInteraction touchInteraction) {

        // Having an idea is just accumulating intention. It's a suggestion from your existential
        // controller.

        TouchInteractivity touchInteractivity = new TouchInteractivity();
        touchInteractivity.add(touchInteraction);

        touchInteractivities.add(touchInteractivity);

        // TODO: Cache and store the touch interactivites before deleting them completely! Do it in
        // TODO: (cont'd) a background thread.
        if (touchInteractivities.size() > 3) {
            touchInteractivities.remove(0);
        }

        onTouchListener(touchInteractivity, touchInteraction);
    }

    public void onContinueInteractivity(TouchInteraction touchInteraction) {

        // Current
        touchInteraction.isTouching[touchInteraction.pointerIndex] = true;

        TouchInteractivity touchInteractivity = getLatestTouchInteractivity();
        touchInteractivity.add(touchInteraction);

        // Calculate drag distance
        touchInteractivity.dragDistance[touchInteraction.pointerIndex] = Geometry.calculateDistance(touchInteraction.getPosition(), touchInteractivity.getFirst().touchPositions[touchInteraction.pointerIndex]);

        // Classify/Callback
        if (touchInteractivity.dragDistance[touchInteraction.pointerIndex] > TouchInteraction.MINIMUM_DRAG_DISTANCE) {
            onDragListener(touchInteractivity, touchInteraction);
        }
    }

    public void onCompleteInteractivity(TouchInteraction touchInteraction) {

        TouchInteractivity touchInteractivity = getLatestTouchInteractivity();
        touchInteractivity.add(touchInteraction);

        // Stop listening for a hold interaction
        touchInteractivity.timerHandler.removeCallbacks(touchInteractivity.timerRunnable);

        // Current
        touchInteraction.isTouching[touchInteraction.pointerIndex] = false;

        if (touchInteractivity.getDuration() < TouchInteraction.MAXIMUM_TAP_DURATION) {
            onTapListener(touchInteractivity, touchInteraction);
        } else {
            onReleaseListener(touchInteractivity, touchInteraction);
        }
    }

    private void onTouchListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        touchInteraction.setType(TouchInteraction.Type.TOUCH);

        Image touchedImage = perspective.getVisualization().getImageByPosition(touchInteraction.getPosition());
        touchInteraction.setTarget(touchedImage);

//        if (perspective.hasFocus()) {
//
//            if (perspective.getFocus().isType(FrameImage.TYPE, PortImage.TYPE, PathImage.TYPE)) {
//
//                if (touchInteraction.isTouching() && touchInteraction.getImageByPosition().isType(PortImage.TYPE)) {
////                    Log.v("Interaction", "BUH");
////                    perspective.InteractionfocusOnPort((PortImage) touchInteraction.getImageByPosition());
//                }
//            }
//
//            if (perspective.getFocus().isType(PortImage.TYPE, PathImage.TYPE)) {
//
//                if (touchInteraction.isTouching() && touchInteraction.getImageByPosition().isType(PathImage.TYPE)) {
////                    perspective.InteractionfocusOnPath((PathImage) touchInteraction.getImageByPosition());
//                }
//            }
//        }
//
//        // Reset object interaction state
//        if (!perspective.hasFocus() || perspective.getFocus().isType(FrameImage.TYPE, PortImage.TYPE)) {
//
//            if (touchInteraction.isTouching() && touchInteraction.getImageByPosition().isType(FrameImage.TYPE)) {
////                perspective.InteractionfocusOnForm((FrameImage) touchInteraction.getImageByPosition());
//            }
//
//        }
//
//        if (!perspective.hasFocus() || perspective.getFocus().isType(FrameImage.TYPE, PortImage.TYPE, PathImage.TYPE)) {
//
//            if (!touchInteraction.isTouching()) {
////                perspective.InteractionfocusReset();
//            }
//        }
    }

    public void onHoldListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        touchInteraction.setType(TouchInteraction.Type.HOLD);

        Image targetImage = perspective.getVisualization().getImageByPosition(touchInteraction.getPosition());
        touchInteraction.setTarget(targetImage);

        touchInteractivity.isHolding[touchInteraction.pointerIndex] = true;

        if (touchInteraction.isTouching()) {

            if (touchInteraction.getTarget().isType(FrameImage.TYPE)) {

                // TODO:

            } else if (touchInteraction.getTarget().isType(PortImage.TYPE)) {

                // TODO:

            }
        }
    }

    private void onDragListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        touchInteraction.setType(TouchInteraction.Type.DRAG);

        Image targetImage = perspective.getVisualization().getImageByPosition(touchInteraction.getPosition());
        touchInteraction.setTarget(targetImage);

        Log.v("onDragListener", "" + touchInteraction.getType() + ": " + touchInteraction.getTarget());

        Log.v("Interaction", "onDrag");
        Log.v("Interaction", "focus: " + perspective.getFocus());
        Log.v("Interaction", "touch: " + touchInteraction.getTarget());
        Log.v("Interaction", "-");

        if (touchInteractivity.getSize() > 1) {
            touchInteraction.setTarget(touchInteractivity.getFirst().getTarget());
        }

        touchInteractivity.isDragging[touchInteraction.pointerIndex] = true;

        // Dragging and holding
        if (touchInteractivity.isHolding[touchInteraction.pointerIndex]) {

            // Holding and dragging

            // TODO: Put into callback
            if (touchInteraction.isTouching()) {

                if (touchInteraction.getTarget().isType(FrameImage.TYPE)) {

                    FrameImage frameImage = (FrameImage) touchInteraction.getTarget();
                    frameImage.touch(touchInteraction);
                    frameImage.setPosition(touchInteraction.getPosition());

                    // Zoom out to show overview
//                    perspective.setScale(0.8f);

                } else if (touchInteraction.getTarget().isType(PortImage.TYPE)) {

                    PortImage portImage = (PortImage) touchInteraction.getTarget();
                    portImage.isTouched = true;
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.getPosition(), TouchInteraction.Type.DRAG);
//                    portSprite.touchPositions(touchInteraction);

                    portImage.setPosition(touchInteraction.getPosition());
                }

            } else if (perspective.isAdjustable()) {

                perspective.setScale(0.9f);
                perspective.setOffset(
                        touchInteraction.getPosition().getX() - touchInteractivity.getFirst().getPosition().getX(),
                        touchInteraction.getPosition().getY() - touchInteractivity.getFirst().getPosition().getY()
                );

            }

        } else {

            // Dragging only (not holding)

            // TODO: Put into callback
            if (touchInteraction.isTouching()) {

                if (touchInteraction.getTarget().isType(FrameImage.TYPE)) {

                    FrameImage frameImage = (FrameImage) touchInteraction.getTarget();
                    frameImage.touch(touchInteraction);
                    frameImage.setPosition(touchInteraction.getPosition());

                    perspective.drag_focusOnForm();

                } else if (touchInteraction.getTarget().isType(PortImage.TYPE)) {

                    PortImage portImage = (PortImage) touchInteraction.getTarget();
                    portImage.touch(touchInteraction);

                    perspective.drag_focusOnPortNewPath(touchInteractivity, touchInteraction);
                }

            } else {

                perspective.drag_focusReset(touchInteractivity);

            }

        }
    }

    private void onTapListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        touchInteraction.setType(TouchInteraction.Type.TAP);

        Image targetImage = perspective.getVisualization().getImageByPosition(touchInteraction.getPosition());
        touchInteraction.setTarget(targetImage);

        Log.v("Interaction", "onTap");
        Log.v("Interaction", "focus: " + perspective.getFocus());
        Log.v("Interaction", "touch: " + touchInteraction.getTarget());
        Log.v("Interaction", "-");

        if (touchInteraction.isTouching()) {

            if (touchInteraction.getTarget().isType(FrameImage.TYPE)) {

                // Frame
                FrameImage frameImage = (FrameImage) touchInteraction.getTarget();
                perspective.tap_focusOnForm(this, touchInteractivity, touchInteraction);
                frameImage.touch(touchInteraction);

            } else if (touchInteraction.getTarget().isType(PortImage.TYPE)) {

                // Port
                PortImage portImage = (PortImage) touchInteraction.getTarget();
                portImage.touch(touchInteraction);

            } else if (touchInteraction.getTarget().isType(PathImage.TYPE)) {

                // Path
                PathImage pathImage = (PathImage) touchInteraction.getTarget();
                pathImage.touch(touchInteraction);

//                perspective.tap_focusOnPath();
            }

        } else if (!touchInteraction.isTouching()) {

            perspective.focusReset();
        }

    }

    private void onReleaseListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
        touchInteraction.setType(TouchInteraction.Type.RELEASE);

        Image targetImage = perspective.getVisualization().getImageByPosition(touchInteraction.getPosition());
        touchInteraction.setTarget(targetImage);

        Log.v("Interaction", "onRelease");
        Log.v("Interaction", "focus: " + perspective.getFocus());
        Log.v("Interaction", "touch: " + touchInteraction.getTarget());
        Log.v("Interaction", "-");

        if (touchInteraction.isTouching()) {

            if (touchInteraction.getTarget().isType(FrameImage.TYPE)) {

                // If first touch was on the same form, then respond
                if (touchInteractivity.getFirst().isTouching() && touchInteractivity.getFirst().getTarget().isType(FrameImage.TYPE)) {
                    // Frame
                    FrameImage frameImage = (FrameImage) touchInteraction.getTarget();
                    frameImage.touch(touchInteraction);
                }

            } else if (touchInteraction.getTarget().isType(PortImage.TYPE)) {

                // PortImage portImage = (PortImage) touchInteraction.getImageByPosition();
                PortImage sourcePortImage = (PortImage) touchInteractivity.getFirst().getTarget();

                // Show ports of nearby forms
                boolean useNearbyPortImage = false;
                for (FrameImage nearbyFrameImage : perspective.getVisualization().getFormImages()) {

                    Log.v("Interaction", "A");

                    // Update style of nearby machines
                    double distanceToFormImage = Geometry.calculateDistance(
                            touchInteraction.getPosition(),
                            nearbyFrameImage.getPosition()
                    );

                    if (distanceToFormImage < nearbyFrameImage.boardHeight + 50) {

                        Log.v("Interaction", "B");

                        // TODO: Use overlappedImage instanceof PortImage

                        for (PortImage nearbyPortImage : nearbyFrameImage.getPortImages()) {

                            if (nearbyPortImage != sourcePortImage) {
                                if (nearbyPortImage.isTouching(touchInteraction.getPosition(), 50)) {

                                    Log.v("Interaction", "C");

                                    Port port = sourcePortImage.getPort();
                                    Port nearbyPort = nearbyPortImage.getPort();

                                    useNearbyPortImage = true;

                                    if (port.getDirection() == Port.Direction.NONE) {
                                        port.setDirection(Port.Direction.INPUT);
                                    }
                                    if (port.getType() == Port.Type.NONE) {
                                        port.setType(Port.Type.getNextType(port.getType())); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length
                                    }

                                    nearbyPort.setDirection(Port.Direction.OUTPUT);
                                    nearbyPort.setType(Port.Type.getNextType(nearbyPort.getType()));

                                    // Create and add path to port
                                    Port sourcePort = (Port) perspective.getVisualization().getModel(sourcePortImage);
                                    Port targetPort = (Port) perspective.getVisualization().getModel(nearbyPortImage);

                                    if (!sourcePort.hasAncestor(targetPort)) {

                                        Log.v("Interaction", "D.1");

                                        Path path = new Path(sourcePort, targetPort);
                                        sourcePort.addPath(path);

                                        PathImage pathImage = new PathImage(path);
                                        pathImage.setVisualization(perspective.getVisualization());
                                        perspective.getVisualization().addImage(path, pathImage, "paths");

                                        PortImage targetPortImage = (PortImage) perspective.getVisualization().getImage(path.getTarget());
                                        targetPortImage.setUniqueColor(sourcePortImage.getUniqueColor());

                                        // Perspective
                                        perspective.release_focusOnPath(sourcePort);
                                    }

                                    break;
                                }
                            }
                        }
                    }
                }

//                portImage.touch(touchInteraction);

                if (!useNearbyPortImage) {

                    Port port = (Port) sourcePortImage.getModel();

                    port.setDirection(Port.Direction.INPUT);

                    if (port.getType() == Port.Type.NONE) {
                        port.setType(Port.Type.getNextType(port.getType()));
                    }
                }

                sourcePortImage.setCandidatePathVisibility(false);

                // ApplicationView.getDisplay().speakPhrase("setting as input. you can send the data to another board if you want. touchPositions another board.");

//                // Perspective
//                ArrayList<Port> pathPorts = port.getPortsInPaths(paths);
//                ArrayList<Image> pathPortImages = getVisualization().getImages(pathPorts);
//                ArrayList<Point> pathPortPositions = Visualization.getPositions(pathPortImages);
//                Rectangle boundingBox = Geometry.calculateBoundingBox(pathPortPositions);
//                getVisualization().getSimulation().getBody(0).getPerspective().adjustPerspectiveScale(boundingBox);
//
//                getVisualization().getSimulation().getBody(0).getPerspective().setPosition(Geometry.calculateCenterPosition(pathPortPositions));

//                touchInteraction.setTarget(touchInteractivity.getFirst().getImageByPosition());
//                touchInteraction.setType(TouchInteraction.Type.RELEASE);
//                Log.v("onHoldListener", "Source port: " + touchInteraction.getImageByPosition());
//                targetImage.touch(touchInteraction);

            } else if (touchInteraction.getTarget().isType(PathImage.TYPE)) {

                PathImage pathImage = (PathImage) touchInteraction.getTarget();
            }

        } else if (!touchInteraction.isTouching()) {

//            // No touchPositions on board or port. Touch is on map. So hide ports.
//            for (FrameImage formImage : perspective.getVisualization().getFormImages()) {
//                formImage.hidePortImages();
//                formImage.hidePathImages();
//                formImage.setTransparency(1.0f);
//            }
//
//            // Adjust panning
//            // Auto-adjust the perspective
//            Point centroidPosition = perspective.getVisualization().getImages().filterType(FrameImage.TYPE).calculateCentroid();
//            perspective.setPosition(new Point(centroidPosition.x, centroidPosition.y));
//
//            adjustPerspectiveScale();

            // Check if first touch was on an image
            if (touchInteractivity.getFirst().isTouching()) {
                if (touchInteractivity.getFirst().getTarget().isType(PortImage.TYPE)) {
                    ((PortImage) touchInteractivity.getFirst().getTarget()).setCandidatePathVisibility(false);
                }
            }

            perspective.focusReset();

        }

        // Interactivity
        perspective.setAdjustability(true);
    }
}
