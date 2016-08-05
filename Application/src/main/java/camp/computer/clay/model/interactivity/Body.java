package camp.computer.clay.model.interactivity;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.model.arch.Peripheral;
import camp.computer.clay.model.arch.Path;
import camp.computer.clay.model.arch.Port;
import camp.computer.clay.visualization.arch.Image;
import camp.computer.clay.visualization.arch.Layer;
import camp.computer.clay.visualization.img.FrameImage;
import camp.computer.clay.visualization.img.PathImage;
import camp.computer.clay.visualization.img.PeripheralImage;
import camp.computer.clay.visualization.img.PortImage;
import camp.computer.clay.visualization.util.Geometry;

public class Body {

    private Perspective perspective;

    public List<TouchInteractivity> touchInteractivities = new ArrayList<>();

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

//        if (perspective.hasFocusImage()) {
//
//            if (perspective.getFocusImage().isType(FrameImage.TYPE, PortImage.TYPE, PathImage.TYPE)) {
//
//                if (touchInteraction.isTouching() && touchInteraction.getImageByPosition().isType(PortImage.TYPE)) {
////                    Log.v("Interaction", "BUH");
////                    perspective.InteractionfocusOnPort((PortImage) touchInteraction.getImageByPosition());
//                }
//            }
//
//            if (perspective.getFocusImage().isType(PortImage.TYPE, PathImage.TYPE)) {
//
//                if (touchInteraction.isTouching() && touchInteraction.getImageByPosition().isType(PathImage.TYPE)) {
////                    perspective.InteractionfocusOnPath((PathImage) touchInteraction.getImageByPosition());
//                }
//            }
//        }
//
//        // Reset object interaction state
//        if (!perspective.hasFocusImage() || perspective.getFocusImage().isType(FrameImage.TYPE, PortImage.TYPE)) {
//
//            if (touchInteraction.isTouching() && touchInteraction.getImageByPosition().isType(FrameImage.TYPE)) {
////                perspective.InteractionfocusOnForm((FrameImage) touchInteraction.getImageByPosition());
//            }
//
//        }
//
//        if (!perspective.hasFocusImage() || perspective.getFocusImage().isType(FrameImage.TYPE, PortImage.TYPE, PathImage.TYPE)) {
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
        Log.v("Interaction", "focus: " + perspective.getFocusImage());
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

//                perspective.setScale(0.9f);
//                perspective.setOffset(
//                        touchInteraction.getPosition().getX() - touchInteractivity.getFirst().getPosition().getX(),
//                        touchInteraction.getPosition().getY() - touchInteractivity.getFirst().getPosition().getY()
//                );

                perspective.focusOnPerspectiveAdjustment(touchInteractivity);

            }

        } else {

            // Dragging only (not holding)

            // TODO: Put into callback
            if (touchInteraction.isTouching()) {

                if (touchInteraction.getTarget().isType(FrameImage.TYPE)) {

                    FrameImage frameImage = (FrameImage) touchInteraction.getTarget();
                    frameImage.touch(touchInteraction);
                    frameImage.setPosition(touchInteraction.getPosition());

                    perspective.focusOnFrame(this, touchInteractivity, touchInteraction);

                } else if (touchInteraction.getTarget().isType(PortImage.TYPE)) {

                    PortImage portImage = (PortImage) touchInteraction.getTarget();
                    portImage.touch(touchInteraction);

                    perspective.focusOnNewPath(touchInteractivity, touchInteraction);
                }

            } else if (perspective.isAdjustable()) {

                perspective.setScale(0.9f);
                if (touchInteractivity.getSize() > 1) {
                    perspective.setOffset(
                            touchInteraction.getPosition().getX() - touchInteractivity.getPrevious(touchInteraction).getPosition().getX(),
                            touchInteraction.getPosition().getY() - touchInteractivity.getPrevious(touchInteraction).getPosition().getY()
                    );
                }

            }

//            else {
//
//                perspective.focusOnPerspectiveAdjustment(touchInteractivity);
//
//            }

        }
    }

    private void onTapListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        touchInteraction.setType(TouchInteraction.Type.TAP);

        Image targetImage = perspective.getVisualization().getImageByPosition(touchInteraction.getPosition());
        touchInteraction.setTarget(targetImage);

        Log.v("Interaction", "onTap");
        Log.v("Interaction", "focus: " + perspective.getFocusImage());
        Log.v("Interaction", "touch: " + touchInteraction.getTarget());
        Log.v("Interaction", "-");

        if (touchInteraction.isTouching()) {

            if (touchInteraction.getTarget().isType(FrameImage.TYPE)) {

                // Frame
                FrameImage frameImage = (FrameImage) touchInteraction.getTarget();
                perspective.focusOnFrame(this, touchInteractivity, touchInteraction);
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
        Log.v("Interaction", "focus: " + perspective.getFocusImage());
        Log.v("Interaction", "touch: " + touchInteraction.getTarget());
        Log.v("Interaction", "-");

        if (touchInteraction.isTouching()) {

            if (touchInteraction.getTarget().isType(FrameImage.TYPE)) {

                // If first touch was on the same form, then respond
                if (touchInteractivity.getFirst().isTouching() && touchInteractivity.getFirst().getTarget().isType(FrameImage.TYPE)) {

                    // Frame
                    FrameImage frameImage = (FrameImage) touchInteraction.getTarget();
                    frameImage.touch(touchInteraction);

                    // Perspective
                    perspective.focusReset();
                }

            } else if (touchInteraction.getTarget().isType(PortImage.TYPE)) {

                // PortImage portImage = (PortImage) touchInteraction.getImageByPosition();
                PortImage sourcePortImage = (PortImage) touchInteractivity.getFirst().getTarget();

//                if (sourcePortImage.getCandidatePeripheralVisibility() == true) {
//
//                    // Model
//                    Peripheral peripheral = new Peripheral();
//                    getPerspective().getVisualization().getSimulation().addPeripheral(peripheral);
//
//                    // Visualization (Layer)
//                    String layerTag = "peripherals";
//                    getPerspective().getVisualization().addLayer(layerTag);
//                    Layer defaultLayer = getPerspective().getVisualization().getLayer(layerTag);
//
//                    // Image
//                    PeripheralImage peripheralImage = new PeripheralImage(peripheral);
//                    peripheralImage.setPosition(touchInteraction.getPosition());
//                    peripheralImage.setVisualization(getPerspective().getVisualization());
//
//                    // Visualization
//                    getPerspective().getVisualization().addImage(peripheral, peripheralImage, layerTag);
//
//                }

                // Show ports of nearby forms
                boolean useNearbyPortImage = false;
                for (FrameImage nearbyFrameImage : perspective.getVisualization().getFrameImages()) {

                    Log.v("Interaction", "A");

                    // Update style of nearby machines
                    double distanceToFrameImage = Geometry.calculateDistance(
                            touchInteraction.getPosition(),
                            nearbyFrameImage.getPosition()
                    );

                    if (distanceToFrameImage < nearbyFrameImage.getShape().getHeight() + 50) {

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
                                        port.setType(Port.Type.next(port.getType())); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length
                                    }

                                    nearbyPort.setDirection(Port.Direction.OUTPUT);
                                    nearbyPort.setType(Port.Type.next(nearbyPort.getType()));

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
                                        perspective.focusOnPath(sourcePort);
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
                        port.setType(Port.Type.next(port.getType()));
                    }
                }

                sourcePortImage.setCandidatePathVisibility(false);

                // ApplicationView.getDisplay().speakPhrase("setting as input. you can send the data to another board if you want. touchPositions another board.");

//                // Perspective
//                ArrayList<Port> pathPorts = port.getPorts(paths);
//                ArrayList<Image> pathPortImages = getVisualization().getImages(pathPorts);
//                ArrayList<Point> pathPortPositions = Visualization.getPositions(pathPortImages);
//                Rectangle boundingBox = Geometry.calculateBoundingBox(pathPortPositions);
//                getVisualization().getSimulation().getBody(0).getPerspective().adjustScale(boundingBox);
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

            if (touchInteractivity.getFirst().getTarget() != null
                    && touchInteractivity.getFirst().getTarget().isType(PortImage.TYPE)) {

                // PortImage portImage = (PortImage) touchInteraction.getImageByPosition();
                PortImage sourcePortImage = (PortImage) touchInteractivity.getFirst().getTarget();

                if (sourcePortImage.getCandidatePeripheralVisibility() == true) {

                    // Model
                    Peripheral peripheral = new Peripheral();
                    getPerspective().getVisualization().getSimulation().addPeripheral(peripheral);

                    // Visualization (Layer)
                    String layerTag = "peripherals";
                    getPerspective().getVisualization().addLayer(layerTag);
                    Layer defaultLayer = getPerspective().getVisualization().getLayer(layerTag);

                    // Image
                    PeripheralImage peripheralImage = new PeripheralImage(peripheral);
                    peripheralImage.setPosition(touchInteraction.getPosition());
//                    peripheralImage.setRotation();
                    peripheralImage.setVisualization(getPerspective().getVisualization());

                    double pathRotationAngle = Geometry.calculateRotationAngle(
                            sourcePortImage.getPosition(),
                            peripheralImage.getPosition()
                    );
                    peripheralImage.setRotation(pathRotationAngle);

                    // Visualization
                    getPerspective().getVisualization().addImage(peripheral, peripheralImage, layerTag);

                }

            }

//            // No touchPositions on board or port. Touch is on map. So hide ports.
//            for (FrameImage formImage : perspective.getVisualization().getFrameImages()) {
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
//            adjustScale();

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
