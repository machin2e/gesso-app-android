package camp.computer.clay.model.interaction;

import android.util.Log;

import java.util.ArrayList;

<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/interaction/Body.java
import camp.computer.clay.model.simulation._Actor;
import camp.computer.clay.model.simulation.Path;
import camp.computer.clay.model.simulation.Port;
import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.images.FrameImage;
import camp.computer.clay.visualization.images.PathImage;
import camp.computer.clay.visualization.images.PortImage;
import camp.computer.clay.visualization.util.Geometry;
=======
import camp.computer.clay.model.interaction.OnTouchActionListener;
import camp.computer.clay.model.interaction.Perspective;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.model.interaction.TouchInteractivity;
import camp.computer.clay.viz.arch.Image;
import camp.computer.clay.viz.img.old_FrameImage;
import camp.computer.clay.viz.img.old_PathImage;
import camp.computer.clay.viz.img.old_PortImage;
import camp.computer.clay.viz.util.Geometry;
>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Body.java

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
<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/interaction/Body.java
        if (touchInteractivity.dragDistance[touchInteraction.pointerIndex] > TouchInteraction.MINIMUM_DRAG_DISTANCE) {
            onDragListener(touchInteractivity, touchInteraction);
        }
=======
//        if (touchInteractivity.isDragging()) {
        onDragListener(touchInteractivity, touchInteraction);
//        }
>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Body.java
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

<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/interaction/Body.java
        touchInteraction.setType(TouchInteraction.Type.TOUCH);
=======
        Image targetImage = perspective.getViz().getImage(touchInteraction.getPosition());
        touchInteraction.setTargetImage(targetImage);
//        Log.v("Touch", "touched: " + targetImage);

        touchInteraction.setType(OnTouchActionListener.Type.TOUCH);
>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Body.java

        Image touchedImage = perspective.getVisualization().getImageByPosition(touchInteraction.getPosition());
        touchInteraction.setTarget(touchedImage);

//        if (perspective.hasFocus()) {
//
//            if (perspective.getFocus().isType(old_FrameImage.TYPE, old_PortImage.TYPE, old_PathImage.TYPE)) {
//
<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/interaction/Body.java
//                if (touchInteraction.isTouching() && touchInteraction.getImageByPosition().isType(PortImage.TYPE)) {
////                    Log.v("Interaction", "BUH");
////                    perspective.InteractionfocusOnPort((PortImage) touchInteraction.getImageByPosition());
=======
//                if (touchInteraction.containsPoint() && touchInteraction.getImage().isType(old_PortImage.TYPE)) {
////                    Log.v("Interaction", "BUH");
////                    perspective.InteractionfocusOnPort((old_PortImage) touchInteraction.getImage());
>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Body.java
//                }
//            }
//
//            if (perspective.getFocus().isType(old_PortImage.TYPE, old_PathImage.TYPE)) {
//
<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/interaction/Body.java
//                if (touchInteraction.isTouching() && touchInteraction.getImageByPosition().isType(PathImage.TYPE)) {
////                    perspective.InteractionfocusOnPath((PathImage) touchInteraction.getImageByPosition());
=======
//                if (touchInteraction.containsPoint() && touchInteraction.getImage().isType(old_PathImage.TYPE)) {
////                    perspective.InteractionfocusOnPath((old_PathImage) touchInteraction.getImage());
>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Body.java
//                }
//            }
//        }
//
//        // Reset object interaction state
//        if (!perspective.hasFocus() || perspective.getFocus().isType(old_FrameImage.TYPE, old_PortImage.TYPE)) {
//
<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/interaction/Body.java
//            if (touchInteraction.isTouching() && touchInteraction.getImageByPosition().isType(FrameImage.TYPE)) {
////                perspective.InteractionfocusOnForm((FrameImage) touchInteraction.getImageByPosition());
=======
//            if (touchInteraction.containsPoint() && touchInteraction.getImage().isType(old_FrameImage.TYPE)) {
////                perspective.InteractionfocusOnFrame((old_FrameImage) touchInteraction.getImage());
>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Body.java
//            }
//
//        }
//
//        if (!perspective.hasFocus() || perspective.getFocus().isType(old_FrameImage.TYPE, old_PortImage.TYPE, old_PathImage.TYPE)) {
//
//            if (!touchInteraction.isTouching()) {
////                perspective.InteractionfocusReset();
//            }
//        }
    }

    public void onHoldListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/interaction/Body.java
        touchInteraction.setType(TouchInteraction.Type.HOLD);
=======
        Image targetImage = perspective.getViz().getImage(touchInteraction.getPosition());
        touchInteraction.setTargetImage(targetImage);
>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Body.java

        Image targetImage = perspective.getVisualization().getImageByPosition(touchInteraction.getPosition());
        touchInteraction.setTarget(targetImage);

        touchInteractivity.isHolding[touchInteraction.pointerIndex] = true;

        if (touchInteraction.isTouching()) {

            if (touchInteraction.getTarget().isType(old_FrameImage.TYPE)) {

                // TODO:

            } else if (touchInteraction.getTarget().isType(old_PortImage.TYPE)) {

                // TODO:

            }
        }
    }

    private void onDragListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        touchInteraction.setType(TouchInteraction.Type.DRAG);

<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/interaction/Body.java
        Image targetImage = perspective.getVisualization().getImageByPosition(touchInteraction.getPosition());
        touchInteraction.setTarget(targetImage);

=======
        Image targetImage = perspective.getViz().getImage(touchInteraction.getPosition());
        touchInteraction.setTargetImage(targetImage);

        touchInteraction.setType(OnTouchActionListener.Type.DRAG);

//        if (touchInteractivity.getFirst().getTarget() != null) {
        if (targetImage != null) {
            //targetImage.touch(touchInteraction);
//            touchInteractivity.getFirst().getTarget().touch(touchInteraction);
            targetImage.touch(touchInteraction);
        }

>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Body.java
        Log.v("onDragListener", "" + touchInteraction.getType() + ": " + touchInteraction.getTarget());

        Log.v("Interaction", "onDrag");
        Log.v("Interaction", "focus: " + perspective.getFocus());
        Log.v("Interaction", "touch: " + touchInteraction.getTarget());
        Log.v("Interaction", "-");

        if (touchInteractivity.getSize() > 1) {
            touchInteraction.setTargetImage(touchInteractivity.getFirst().getTarget());
        }

        touchInteractivity.isDragging[touchInteraction.pointerIndex] = true;

        // Dragging and holding
        if (touchInteractivity.isHolding[touchInteraction.pointerIndex]) {

            // Holding and dragging

            // TODO: Put into callback
            if (touchInteraction.isTouching()) {

                if (touchInteraction.getTarget().isType(old_FrameImage.TYPE)) {

                    old_FrameImage oldFrameImage = (old_FrameImage) touchInteraction.getTarget();
                    oldFrameImage.touch(touchInteraction);
                    oldFrameImage.setPosition(touchInteraction.getPosition());

                    // Zoom out to show overview
//                    perspective.setScale(0.8f);

                } else if (touchInteraction.getTarget().isType(old_PortImage.TYPE)) {

<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/interaction/Body.java
                    PortImage portImage = (PortImage) touchInteraction.getTarget();
                    portImage.isTouched = true;
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.getPosition(), TouchInteraction.Type.DRAG);
=======
                    old_PortImage oldPortImage = (old_PortImage) touchInteraction.getTarget();
                    oldPortImage.isTouched = true;
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.getPosition(), OnTouchActionListener.Type.DRAG);
>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Body.java
//                    portSprite.touchPositions(touchInteraction);

                    oldPortImage.setPosition(touchInteraction.getPosition());
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

                if (touchInteraction.getTarget().isType(old_FrameImage.TYPE)) {

                    old_FrameImage oldFrameImage = (old_FrameImage) touchInteraction.getTarget();
                    oldFrameImage.touch(touchInteraction);
                    oldFrameImage.setPosition(touchInteraction.getPosition());

                    perspective.drag_focusOnForm();

                } else if (touchInteraction.getTarget().isType(old_PortImage.TYPE)) {

                    old_PortImage oldPortImage = (old_PortImage) touchInteraction.getTarget();
                    oldPortImage.touch(touchInteraction);

                    perspective.drag_focusOnPortNewPath(touchInteractivity, touchInteraction);
                }

            } else {

                perspective.drag_focusReset(touchInteractivity);

            }

        }
    }

    private void onTapListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/interaction/Body.java
        touchInteraction.setType(TouchInteraction.Type.TAP);
=======
        Image targetImage = perspective.getViz().getImage(touchInteraction.getPosition());
        touchInteraction.setTargetImage(targetImage);

        touchInteraction.setType(OnTouchActionListener.Type.TAP);
>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Body.java

        Image targetImage = perspective.getVisualization().getImageByPosition(touchInteraction.getPosition());
        touchInteraction.setTarget(targetImage);

        Log.v("Interaction", "onTap");
        Log.v("Interaction", "focus: " + perspective.getFocus());
        Log.v("Interaction", "touch: " + touchInteraction.getTarget());
        Log.v("Interaction", "-");

        if (touchInteraction.isTouching()) {

            if (touchInteraction.getTarget().isType(old_FrameImage.TYPE)) {

                // Frame
<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/interaction/Body.java
                FrameImage frameImage = (FrameImage) touchInteraction.getTarget();
                perspective.tap_focusOnForm(this, touchInteractivity, touchInteraction);
                frameImage.touch(touchInteraction);
=======
                old_FrameImage oldFrameImage = (old_FrameImage) touchInteraction.getTarget();
                perspective.tap_focusOnFrame(this, touchInteractivity, touchInteraction);
                oldFrameImage.touch(touchInteraction);
>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Body.java

            } else if (touchInteraction.getTarget().isType(old_PortImage.TYPE)) {

                // Port
                old_PortImage oldPortImage = (old_PortImage) touchInteraction.getTarget();
                oldPortImage.touch(touchInteraction);

            } else if (touchInteraction.getTarget().isType(old_PathImage.TYPE)) {

                // Path
                old_PathImage oldPathImage = (old_PathImage) touchInteraction.getTarget();
                oldPathImage.touch(touchInteraction);

//                perspective.tap_focusOnPath();
            }

        } else if (!touchInteraction.isTouching()) {

            perspective.focusReset();
        }

    }

    private void onReleaseListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
        touchInteraction.setType(TouchInteraction.Type.RELEASE);

<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/interaction/Body.java
        Image targetImage = perspective.getVisualization().getImageByPosition(touchInteraction.getPosition());
        touchInteraction.setTarget(targetImage);
=======
        Image targetImage = perspective.getViz().getImage(touchInteraction.getPosition());
        touchInteraction.setTargetImage(targetImage);
>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Body.java

        Log.v("Interaction", "onRelease");
        Log.v("Interaction", "focus: " + perspective.getFocus());
        Log.v("Interaction", "touch: " + touchInteraction.getTarget());
        Log.v("Interaction", "-");

        if (touchInteraction.isTouching()) {

            if (touchInteraction.getTarget().isType(old_FrameImage.TYPE)) {

                // If first touch was on the same form, then respond
                if (touchInteractivity.getFirst().isTouching() && touchInteractivity.getFirst().getTarget().isType(old_FrameImage.TYPE)) {
                    // Frame
                    old_FrameImage oldFrameImage = (old_FrameImage) touchInteraction.getTarget();
                    oldFrameImage.touch(touchInteraction);
                }

            } else if (touchInteraction.getTarget().isType(old_PortImage.TYPE)) {

<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/interaction/Body.java
                // PortImage portImage = (PortImage) touchInteraction.getImageByPosition();
                PortImage sourcePortImage = (PortImage) touchInteractivity.getFirst().getTarget();

                // Show ports of nearby forms
                boolean useNearbyPortImage = false;
                for (FrameImage nearbyFrameImage : perspective.getVisualization().getFormImages()) {
=======
                // old_PortImage portImage = (old_PortImage) touchInteraction.getImage();
                old_PortImage sourceOldPortImage = (old_PortImage) touchInteractivity.getFirst().getTarget();

                // Show ports of nearby forms
                boolean useNearbyPortImage = false;
                for (Image nearbyImage : perspective.getViz().getImages().old_filterType(old_FrameImage.TYPE).getList()) {

                    old_FrameImage nearbyOldFrameImage = (old_FrameImage) nearbyImage;
>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Body.java

                    Log.v("Interaction", "A");

                    // Update style of nearby machines
                    double distanceToFormImage = Geometry.calculateDistance(
                            touchInteraction.getPosition(),
                            nearbyOldFrameImage.getPosition()
                    );

<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/interaction/Body.java
                    if (distanceToFormImage < nearbyFrameImage.boardHeight + 50) {
=======
                    if (distanceToFrameImage < nearbyOldFrameImage.boardHeight + 50) {
>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Body.java

                        Log.v("Interaction", "B");

                        // TODO: Use overlappedImage instanceof old_PortImage

                        for (old_PortImage nearbyOldPortImage : nearbyOldFrameImage.getPortImages()) {

<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/interaction/Body.java
                            if (nearbyPortImage != sourcePortImage) {
                                if (nearbyPortImage.isTouching(touchInteraction.getPosition(), 50)) {
=======
                            if (nearbyOldPortImage != sourceOldPortImage) {
                                if (nearbyOldPortImage.isTouching(touchInteraction.getPosition())) {
>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Body.java

                                    Log.v("Interaction", "C");

                                    Port port = sourceOldPortImage.getPort();
                                    Port nearbyPort = nearbyOldPortImage.getPort();

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
<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/interaction/Body.java
                                    Port sourcePort = (Port) perspective.getVisualization().getModel(sourcePortImage);
                                    Port targetPort = (Port) perspective.getVisualization().getModel(nearbyPortImage);
=======
                                    Port sourcePort = (Port) perspective.getViz().getModel(sourceOldPortImage);
                                    Port targetPort = (Port) perspective.getViz().getModel(nearbyOldPortImage);
>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Body.java

                                    if (!sourcePort.hasAncestor(targetPort)) {

                                        Log.v("Interaction", "D.1");

                                        Path path = new Path(sourcePort, targetPort);
                                        sourcePort.addPath(path);

<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/interaction/Body.java
                                        PathImage pathImage = new PathImage(path);
                                        pathImage.setVisualization(perspective.getVisualization());
                                        perspective.getVisualization().addImage(path, pathImage, "paths");

                                        PortImage targetPortImage = (PortImage) perspective.getVisualization().getImage(path.getTarget());
                                        targetPortImage.setUniqueColor(sourcePortImage.getUniqueColor());
=======
                                        old_PathImage oldPathImage = new old_PathImage(path);
                                        oldPathImage.setViz(perspective.getViz());
                                        perspective.getViz().addImage(oldPathImage, "paths");

                                        old_PortImage targetOldPortImage = (old_PortImage) perspective.getViz().getImage(path.getTarget());
                                        targetOldPortImage.setUniqueColor(sourceOldPortImage.getUniqueColor());
>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Body.java

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

                    Port port = (Port) sourceOldPortImage.getModel();

                    port.setDirection(Port.Direction.INPUT);

                    if (port.getType() == Port.Type.NONE) {
                        port.setType(Port.Type.getNextType(port.getType()));
                    }
                }

                sourceOldPortImage.setCandidatePathVisibility(false);

                // ApplicationView.getDisplay().speakPhrase("setting as input. you can send the data to another board if you want. touchPositions another board.");

//                // Perspective
//                ArrayList<Port> pathPorts = port.getPortsInPaths(paths);
//                ArrayList<Image> pathPortImages = getVisualization().getImages(pathPorts);
//                ArrayList<Point> pathPortPositions = Visualization.getPositions(pathPortImages);
//                Rectangle boundingBox = Geometry.calculateBoundingBox(pathPortPositions);
//                getVisualization().getSimulation().getBody(0).getPerspective().adjustPerspectiveScale(boundingBox);
//
//                getVisualization().getSimulation().getBody(0).getPerspective().setPosition(Geometry.calculateCenterPosition(pathPortPositions));

<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/interaction/Body.java
//                touchInteraction.setTarget(touchInteractivity.getFirst().getImageByPosition());
//                touchInteraction.setType(TouchInteraction.Type.RELEASE);
//                Log.v("onHoldListener", "Source port: " + touchInteraction.getImageByPosition());
=======
//                touchInteraction.setTargetImage(touchInteractivity.getFirst().getImage());
//                touchInteraction.setType(OnTouchActionListener.Type.RELEASE);
//                Log.v("onHoldListener", "Source port: " + touchInteraction.getImage());
>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Body.java
//                targetImage.touch(touchInteraction);

            } else if (touchInteraction.getTarget().isType(old_PathImage.TYPE)) {

                old_PathImage oldPathImage = (old_PathImage) touchInteraction.getTarget();
            }

        } else if (!touchInteraction.isTouching()) {

//            // No touchPositions on board or port. Touch is on map. So hide ports.
<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/interaction/Body.java
//            for (FrameImage formImage : perspective.getVisualization().getFormImages()) {
=======
//            for (old_FrameImage formImage : perspective.getViz().getFrameImages()) {
>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Body.java
//                formImage.hidePortImages();
//                formImage.hidePathImages();
//                formImage.setTransparency(1.0f);
//            }
//
//            // Adjust panning
//            // Auto-adjust the perspective
<<<<<<< HEAD:Application/src/main/java/camp/computer/clay/model/interaction/Body.java
//            Point centroidPosition = perspective.getVisualization().getImages().filterType(FrameImage.TYPE).calculateCentroid();
=======
//            Point centroidPosition = perspective.getViz().getImages().old_filterType(old_FrameImage.TYPE).calculateCentroid();
>>>>>>> 4ce8be0ece817c35e9964b62d77b33121747f3e8:Application/src/main/java/camp/computer/clay/model/sim/Body.java
//            perspective.setPosition(new Point(centroidPosition.x, centroidPosition.y));
//
//            adjustPerspectiveScale();

            // Check if first touch was on an image
            if (touchInteractivity.getFirst().isTouching()) {
                if (touchInteractivity.getFirst().getTarget().isType(old_PortImage.TYPE)) {
                    ((old_PortImage) touchInteractivity.getFirst().getTarget()).setCandidatePathVisibility(false);
                }
            }

            perspective.focusReset();

        }

        // Interactivity
        perspective.setAdjustability(true);
    }
}
