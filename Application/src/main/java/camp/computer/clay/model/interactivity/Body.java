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

    public List<Interplay> touchInteractivities = new ArrayList<>();

    public Body() {
    }

    public void setPerspective(Perspective perspective) {
        this.perspective = perspective;
    }

    public Perspective getPerspective() {
        return this.perspective;
    }

    public Interplay getLatestTouchInteractivity() {
        if (touchInteractivities.size() > 0) {
            return this.touchInteractivities.get(touchInteractivities.size() - 1);
        } else {
            return null;
        }
    }

    public void onStartInteractivity(Interaction interaction) {

        // Having an idea is just accumulating intention. It's a suggestion from your existential
        // controller.

        Interplay interplay = new Interplay();
        interplay.add(interaction);

        touchInteractivities.add(interplay);

        // TODO: Cache and store the touch interactivites before deleting them completely! Do it in
        // TODO: (cont'd) a background thread.
        if (touchInteractivities.size() > 3) {
            touchInteractivities.remove(0);
        }

        onTouchListener(interplay, interaction);
    }

    public void onContinueInteractivity(Interaction interaction) {

        // Current
        interaction.isTouching[interaction.pointerIndex] = true;

        Interplay interplay = getLatestTouchInteractivity();
        interplay.add(interaction);

        // Calculate drag distance
        interplay.dragDistance[interaction.pointerIndex] = Geometry.calculateDistance(interaction.getPosition(), interplay.getFirst().touchPositions[interaction.pointerIndex]);

        // Classify/Callback
        if (interplay.dragDistance[interaction.pointerIndex] > Interaction.MINIMUM_DRAG_DISTANCE) {
            onDragListener(interplay, interaction);
        }
    }

    public void onCompleteInteractivity(Interaction interaction) {

        Interplay interplay = getLatestTouchInteractivity();
        interplay.add(interaction);

        // Stop listening for a hold interaction
        interplay.timerHandler.removeCallbacks(interplay.timerRunnable);

        // Current
        interaction.isTouching[interaction.pointerIndex] = false;

        if (interplay.getDuration() < Interaction.MAXIMUM_TAP_DURATION) {
            onTapListener(interplay, interaction);
        } else {
            onReleaseListener(interplay, interaction);
        }
    }

    private void onTouchListener(Interplay interplay, Interaction interaction) {

        interaction.setType(Interaction.Type.TOUCH);

        Image touchedImage = perspective.getVisualization().getImageByPosition(interaction.getPosition());
        interaction.setTargetImage(touchedImage);

//        if (perspective.hasFocusImage()) {
//
//            if (perspective.getFocusImage().isType(FrameImage.TYPE, PortImage.TYPE, PathImage.TYPE)) {
//
//                if (interaction.isTouching() && interaction.getImageByPosition().isType(PortImage.TYPE)) {
////                    Log.v("Interaction", "BUH");
////                    perspective.InteractionfocusOnPort((PortImage) interaction.getImageByPosition());
//                }
//            }
//
//            if (perspective.getFocusImage().isType(PortImage.TYPE, PathImage.TYPE)) {
//
//                if (interaction.isTouching() && interaction.getImageByPosition().isType(PathImage.TYPE)) {
////                    perspective.InteractionfocusOnPath((PathImage) interaction.getImageByPosition());
//                }
//            }
//        }
//
//        // Reset object interaction state
//        if (!perspective.hasFocusImage() || perspective.getFocusImage().isType(FrameImage.TYPE, PortImage.TYPE)) {
//
//            if (interaction.isTouching() && interaction.getImageByPosition().isType(FrameImage.TYPE)) {
////                perspective.InteractionfocusOnForm((FrameImage) interaction.getImageByPosition());
//            }
//
//        }
//
//        if (!perspective.hasFocusImage() || perspective.getFocusImage().isType(FrameImage.TYPE, PortImage.TYPE, PathImage.TYPE)) {
//
//            if (!interaction.isTouching()) {
////                perspective.InteractionfocusReset();
//            }
//        }
    }

    public void onHoldListener(Interplay interplay, Interaction interaction) {

        interaction.setType(Interaction.Type.HOLD);

        Image targetImage = perspective.getVisualization().getImageByPosition(interaction.getPosition());
        interaction.setTargetImage(targetImage);

        interplay.isHolding[interaction.pointerIndex] = true;

        if (interaction.isTouching()) {

            if (interaction.getTargetImage().isType(FrameImage.TYPE)) {

                // TODO:

            } else if (interaction.getTargetImage().isType(PortImage.TYPE)) {

                // TODO:

            }
        }
    }

    private void onDragListener(Interplay interplay, Interaction interaction) {

        interaction.setType(Interaction.Type.DRAG);

        Image targetImage = perspective.getVisualization().getImageByPosition(interaction.getPosition());
        interaction.setTargetImage(targetImage);

        Log.v("onDragListener", "" + interaction.getType() + ": " + interaction.getTargetImage());

        Log.v("Interaction", "onDrag");
        Log.v("Interaction", "focus: " + perspective.getFocusImage());
        Log.v("Interaction", "touch: " + interaction.getTargetImage());
        Log.v("Interaction", "-");

        if (interplay.getSize() > 1) {
            interaction.setTargetImage(interplay.getFirst().getTargetImage());
        }

        interplay.isDragging[interaction.pointerIndex] = true;

        // Dragging and holding
        if (interplay.isHolding[interaction.pointerIndex]) {

            // Holding and dragging

            // TODO: Put into callback
            if (interaction.isTouching()) {

                if (interaction.getTargetImage().isType(FrameImage.TYPE)) {

                    FrameImage frameImage = (FrameImage) interaction.getTargetImage();
                    frameImage.touch(interaction);
                    frameImage.setPosition(interaction.getPosition());

                    // Zoom out to show overview
//                    perspective.setScale(0.8f);

                } else if (interaction.getTargetImage().isType(PortImage.TYPE)) {

                    PortImage portImage = (PortImage) interaction.getTargetImage();
                    portImage.isTouched = true;
//                    Interaction interaction = new Interaction(interaction.getPosition(), Interaction.Type.DRAG);
//                    portSprite.touchPositions(interaction);

                    portImage.setPosition(interaction.getPosition());
                }

            } else if (perspective.isAdjustable()) {

//                perspective.setScale(0.9f);
//                perspective.setOffset(
//                        interaction.getPosition().getX() - interplay.getFirst().getPosition().getX(),
//                        interaction.getPosition().getY() - interplay.getFirst().getPosition().getY()
//                );

                perspective.focusOnPerspectiveAdjustment(interplay);

            }

        } else {

            // Dragging only (not holding)

            // TODO: Put into callback
            if (interaction.isTouching()) {

                if (interaction.getTargetImage().isType(FrameImage.TYPE)) {

                    FrameImage frameImage = (FrameImage) interaction.getTargetImage();
                    frameImage.touch(interaction);
                    frameImage.setPosition(interaction.getPosition());

                    perspective.focusOnFrame(this, interplay, interaction);

                } else if (interaction.getTargetImage().isType(PortImage.TYPE)) {

                    PortImage portImage = (PortImage) interaction.getTargetImage();
                    portImage.touch(interaction);

                    perspective.focusOnNewPath(interplay, interaction);
                }

            } else if (perspective.isAdjustable()) {

                perspective.setScale(0.9f);
                if (interplay.getSize() > 1) {
                    perspective.setOffset(
                            interaction.getPosition().getX() - interplay.getPrevious(interaction).getPosition().getX(),
                            interaction.getPosition().getY() - interplay.getPrevious(interaction).getPosition().getY()
                    );
                }

            }

//            else {
//
//                perspective.focusOnPerspectiveAdjustment(interplay);
//
//            }

        }
    }

    private void onTapListener(Interplay interplay, Interaction interaction) {

        interaction.setType(Interaction.Type.TAP);

        Image targetImage = perspective.getVisualization().getImageByPosition(interaction.getPosition());
        interaction.setTargetImage(targetImage);

        Log.v("Interaction", "onTap");
        Log.v("Interaction", "focus: " + perspective.getFocusImage());
        Log.v("Interaction", "touch: " + interaction.getTargetImage());
        Log.v("Interaction", "-");

        if (interaction.isTouching()) {

            if (interaction.getTargetImage().isType(FrameImage.TYPE)) {

                // Frame
                FrameImage frameImage = (FrameImage) interaction.getTargetImage();
                perspective.focusOnFrame(this, interplay, interaction);
                frameImage.touch(interaction);

            } else if (interaction.getTargetImage().isType(PortImage.TYPE)) {

                // Port
                PortImage portImage = (PortImage) interaction.getTargetImage();
                portImage.touch(interaction);

            } else if (interaction.getTargetImage().isType(PathImage.TYPE)) {

                // Path
                PathImage pathImage = (PathImage) interaction.getTargetImage();
                pathImage.touch(interaction);

//                perspective.tap_focusOnPath();
            }

        } else if (!interaction.isTouching()) {

            perspective.focusReset();
        }

    }

    private void onReleaseListener(Interplay interplay, Interaction interaction) {
        interaction.setType(Interaction.Type.RELEASE);

        Image targetImage = perspective.getVisualization().getImageByPosition(interaction.getPosition());
        interaction.setTargetImage(targetImage);

        Log.v("Interaction", "onRelease");
        Log.v("Interaction", "focus: " + perspective.getFocusImage());
        Log.v("Interaction", "touch: " + interaction.getTargetImage());
        Log.v("Interaction", "-");

        if (interaction.isTouching()) {

            // First touch was on a frame image...
            if (interplay.getFirst().getTargetImage().isType(FrameImage.TYPE)) {

                if (interaction.getTargetImage().isType(FrameImage.TYPE)) {

                    // If first touch was on the same form, then respond
                    if (interplay.getFirst().isTouching() && interplay.getFirst().getTargetImage().isType(FrameImage.TYPE)) {

                        // Frame
                        FrameImage frameImage = (FrameImage) interaction.getTargetImage();
                        frameImage.touch(interaction);

                        // Perspective
                        perspective.focusReset();
                    }

                }

            } else if (interplay.getFirst().getTargetImage().isType(PortImage.TYPE)) {

                // First touch was on a port image...

                if (interaction.getTargetImage().isType(FrameImage.TYPE)) {

                    // ...last touch was on a frame image.

                    PortImage sourcePortImage = (PortImage) interplay.getFirst().getTargetImage();
                    sourcePortImage.setCandidatePathVisibility(false);

                } else if (interaction.getTargetImage().isType(PortImage.TYPE)) {

                    // ...last touch was on a port image.

                    // PortImage portImage = (PortImage) interaction.getImageByPosition();
                    PortImage sourcePortImage = (PortImage) interplay.getFirst().getTargetImage();

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
//                    peripheralImage.setPosition(interaction.getPosition());
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
                                interaction.getPosition(),
                                nearbyFrameImage.getPosition()
                        );

                        if (distanceToFrameImage < nearbyFrameImage.getShape().getHeight() + 50) {

                            Log.v("Interaction", "B");

                            // TODO: Use overlappedImage instanceof PortImage

                            for (PortImage nearbyPortImage : nearbyFrameImage.getPortImages()) {

                                if (nearbyPortImage != sourcePortImage) {
                                    if (nearbyPortImage.isTouching(interaction.getPosition(), 50)) {

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

//                portImage.touch(interaction);

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

//                interaction.setTargetImage(interplay.getFirst().getImageByPosition());
//                interaction.setType(Interaction.Type.RELEASE);
//                Log.v("onHoldListener", "Source port: " + interaction.getImageByPosition());
//                targetImage.touch(interaction);

                }

            } else if (interplay.getFirst().getTargetImage().isType(PathImage.TYPE)) {

                // First touch was on a path image...

                if (interaction.getTargetImage().isType(PathImage.TYPE)) {
                    PathImage pathImage = (PathImage) interaction.getTargetImage();
                }
            }

        } else if (!interaction.isTouching()) {

            if (interplay.getFirst().getTargetImage() != null
                    && interplay.getFirst().getTargetImage().isType(PortImage.TYPE)) {

                // PortImage portImage = (PortImage) interaction.getImageByPosition();
                PortImage sourcePortImage = (PortImage) interplay.getFirst().getTargetImage();

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
                    peripheralImage.setPosition(interaction.getPosition());
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
            if (interplay.getFirst().isTouching()) {
                if (interplay.getFirst().getTargetImage().isType(PortImage.TYPE)) {
                    ((PortImage) interplay.getFirst().getTargetImage()).setCandidatePathVisibility(false);
                }
            }

            perspective.focusReset();

        }

        // Interplay
        perspective.setAdjustability(true);
    }
}
