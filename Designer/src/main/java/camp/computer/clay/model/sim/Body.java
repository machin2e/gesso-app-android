package camp.computer.clay.model.sim;

import android.util.Log;

import java.util.ArrayList;

import camp.computer.clay.model.interaction.OnTouchActionListener;
import camp.computer.clay.model.interaction.Perspective;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.model.interaction.TouchInteractivity;
import camp.computer.clay.viz.arch.Image;
import camp.computer.clay.viz.img.FrameImage;
import camp.computer.clay.viz.img.PathImage;
import camp.computer.clay.viz.img.PortImage;
import camp.computer.clay.viz.util.Geometry;

public class Body {

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

    /**
     * Returns the latest touch interactivity.
     * @return
     */
    public TouchInteractivity getTouchInteractivity() {
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

        TouchInteractivity touchInteractivity = getTouchInteractivity();
        touchInteractivity.add(touchInteraction);

        // Calculate drag distance
//        touchInteractivity.dragDistance[touchInteraction.pointerIndex] = Geometry.calculateDistance(touchInteraction.getPosition(), touchInteractivity.getFirst().getPosition());

        // Classify/Callback
        if (touchInteractivity.isDragging()) {
            onDragListener(touchInteractivity, touchInteraction);
        }
    }

    public void onCompleteInteractivity(TouchInteraction touchInteraction) {

        TouchInteractivity touchInteractivity = getTouchInteractivity();
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

        touchInteraction.setType(OnTouchActionListener.Type.TOUCH);

        Image targetImage = perspective.getViz().getImage(touchInteraction.getPosition());
        touchInteraction.setTarget(targetImage);
        Log.v("Touch", "touched: " + targetImage);
        if (targetImage != null) {
            targetImage.touch(touchInteraction);
        }

//        if (perspective.hasFocus()) {
//
//            if (perspective.getFocus().isType(FrameImage.TYPE, PortImage.TYPE, PathImage.TYPE)) {
//
//                if (touchInteraction.containsPoint() && touchInteraction.getImage().isType(PortImage.TYPE)) {
////                    Log.v("Interaction", "BUH");
////                    perspective.InteractionfocusOnPort((PortImage) touchInteraction.getImage());
//                }
//            }
//
//            if (perspective.getFocus().isType(PortImage.TYPE, PathImage.TYPE)) {
//
//                if (touchInteraction.containsPoint() && touchInteraction.getImage().isType(PathImage.TYPE)) {
////                    perspective.InteractionfocusOnPath((PathImage) touchInteraction.getImage());
//                }
//            }
//        }
//
//        // Reset object interaction state
//        if (!perspective.hasFocus() || perspective.getFocus().isType(FrameImage.TYPE, PortImage.TYPE)) {
//
//            if (touchInteraction.containsPoint() && touchInteraction.getImage().isType(FrameImage.TYPE)) {
////                perspective.InteractionfocusOnFrame((FrameImage) touchInteraction.getImage());
//            }
//
//        }
//
//        if (!perspective.hasFocus() || perspective.getFocus().isType(FrameImage.TYPE, PortImage.TYPE, PathImage.TYPE)) {
//
//            if (!touchInteraction.containsPoint()) {
////                perspective.InteractionfocusReset();
//            }
//        }
    }

    public void onHoldListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        touchInteraction.setType(OnTouchActionListener.Type.HOLD);

        Image targetImage = perspective.getViz().getImage(touchInteraction.getPosition());
        touchInteraction.setTarget(targetImage);
        if (targetImage != null) {
            targetImage.touch(touchInteraction);
        }

//        touchInteractivity.isHolding[touchInteraction.pointerIndex] = true;

        if (touchInteraction.isTouching()) {

            if (touchInteraction.getTarget().isType(FrameImage.TYPE)) {

                // TODO:

            } else if (touchInteraction.getTarget().isType(PortImage.TYPE)) {

                // TODO:

            }
        }
    }

    private void onDragListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        touchInteraction.setType(OnTouchActionListener.Type.DRAG);

        Image targetImage = perspective.getViz().getImage(touchInteraction.getPosition());
        touchInteraction.setTarget(targetImage);
        if (targetImage != null) {
            targetImage.touch(touchInteraction);
        }

        Log.v("onDragListener", "" + touchInteraction.getType() + ": " + touchInteraction.getTarget());

        Log.v("Interaction", "onDrag");
        Log.v("Interaction", "focus: " + perspective.getFocus());
        Log.v("Interaction", "touch: " + touchInteraction.getTarget());
        Log.v("Interaction", "-");

        if (touchInteractivity.getSize() > 1) {
            touchInteraction.setTarget(touchInteractivity.getFirst().getTarget());
        }

//        touchInteractivity.isDragging[touchInteraction.pointerIndex] = true;

        // Dragging and holding
        if (touchInteractivity.isHolding()) {

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
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.getPosition(), OnTouchActionListener.Type.DRAG);
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

                    perspective.drag_focusOnFrame();

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

        touchInteraction.setType(OnTouchActionListener.Type.TAP);

        Image targetImage = perspective.getViz().getImage(touchInteraction.getPosition());
        touchInteraction.setTarget(targetImage);
        if (targetImage != null) {
            targetImage.touch(touchInteraction);
        }

        Log.v("Interaction", "onTap");
        Log.v("Interaction", "focus: " + perspective.getFocus());
        Log.v("Interaction", "touch: " + touchInteraction.getTarget());
        Log.v("Interaction", "-");

        if (touchInteraction.isTouching()) {

            if (touchInteraction.getTarget().isType(FrameImage.TYPE)) {

                // Frame
                FrameImage frameImage = (FrameImage) touchInteraction.getTarget();
                perspective.tap_focusOnFrame(this, touchInteractivity, touchInteraction);
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
        touchInteraction.setType(OnTouchActionListener.Type.RELEASE);

        Image targetImage = perspective.getViz().getImage(touchInteraction.getPosition());
        touchInteraction.setTarget(targetImage);
        if (targetImage != null) {
            targetImage.touch(touchInteraction);
        }

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

                // PortImage portImage = (PortImage) touchInteraction.getImage();
                PortImage sourcePortImage = (PortImage) touchInteractivity.getFirst().getTarget();

                // Show ports of nearby forms
                boolean useNearbyPortImage = false;
                for (Image nearbyImage : perspective.getViz().getImages().old_filterType(FrameImage.TYPE).getList()) {

                    FrameImage nearbyFrameImage = (FrameImage) nearbyImage;

                    Log.v("Interaction", "A");

                    // Update style of nearby machines
                    double distanceToFrameImage = Geometry.calculateDistance(
                            touchInteraction.getPosition(),
                            nearbyFrameImage.getPosition()
                    );

                    if (distanceToFrameImage < nearbyFrameImage.boardHeight + 50) {

                        Log.v("Interaction", "B");

                        // TODO: Use overlappedImage instanceof PortImage

                        for (PortImage nearbyPortImage : nearbyFrameImage.getPortImages()) {

                            if (nearbyPortImage != sourcePortImage) {
                                if (nearbyPortImage.isTouching(touchInteraction.getPosition())) {

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
                                    Port sourcePort = (Port) perspective.getViz().getModel(sourcePortImage);
                                    Port targetPort = (Port) perspective.getViz().getModel(nearbyPortImage);

                                    if (!sourcePort.hasAncestorPort(targetPort)) {

                                        Log.v("Interaction", "D.1");

                                        Path path = new Path(sourcePort, targetPort);
                                        sourcePort.addPath(path);

                                        PathImage pathImage = new PathImage(path);
                                        pathImage.setViz(perspective.getViz());
                                        perspective.getViz().addImage(pathImage, "paths");

                                        PortImage targetPortImage = (PortImage) perspective.getViz().getImage(path.getTarget());
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
//                ArrayList<Port> pathPorts = port.getPortsByPath(paths);
//                ArrayList<Image> pathPortImages = getViz().getImages(pathPorts);
//                ArrayList<Point> pathPortPositions = Viz.getPositions(pathPortImages);
//                Rectangle boundingBox = Geometry.calculateBoundingBox(pathPortPositions);
//                getViz().getSimulation().getBody(0).getPerspective().adjustPerspectiveScale(boundingBox);
//
//                getViz().getSimulation().getBody(0).getPerspective().setPosition(Geometry.calculateCenter(pathPortPositions));

//                touchInteraction.setTarget(touchInteractivity.getFirst().getImage());
//                touchInteraction.setType(OnTouchActionListener.Type.RELEASE);
//                Log.v("onHoldListener", "Source port: " + touchInteraction.getImage());
//                targetImage.touch(touchInteraction);

            } else if (touchInteraction.getTarget().isType(PathImage.TYPE)) {

                PathImage pathImage = (PathImage) touchInteraction.getTarget();
            }

        } else if (!touchInteraction.isTouching()) {

//            // No touchPositions on board or port. Touch is on map. So hide ports.
//            for (FrameImage formImage : perspective.getViz().getFrameImages()) {
//                formImage.hidePortImages();
//                formImage.hidePathImages();
//                formImage.setTransparency(1.0f);
//            }
//
//            // Adjust panning
//            // Auto-adjust the perspective
//            Point centroidPosition = perspective.getViz().getImages().old_filterType(FrameImage.TYPE).calculateCentroid();
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
