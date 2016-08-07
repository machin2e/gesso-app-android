package camp.computer.clay.model.interactivity;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.model.architecture.Patch;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.architecture.Layer;
import camp.computer.clay.visualization.architecture.Visualization;
import camp.computer.clay.visualization.image.PatchImage;
import camp.computer.clay.visualization.image.FrameImage;
import camp.computer.clay.visualization.image.PathImage;
import camp.computer.clay.visualization.image.PortImage;
import camp.computer.clay.visualization.util.geometry.Geometry;

public class Body {

    private Perspective perspective = null;

    public List<Interaction> interactions = new ArrayList<>();

    public Body() {
    }

    public void setPerspective(Perspective perspective) {
        this.perspective = perspective;
    }

    public boolean hasPerspective() {
        return perspective != null;
    }

    public Perspective getPerspective() {
        return this.perspective;
    }

    /**
     * Returns the most recent interaction.
     *
     * @return The most recent interaction.
     */
    public Interaction getInteraction() {
        if (interactions.size() > 0) {
            return interactions.get(interactions.size() - 1);
        } else {
            return null;
        }
    }

    public void onImpression(Impression impression) {

        switch (impression.getType()) {

            case TOUCH: {

                impression.setBody(this);

                // Having an idea is just accumulating intention. It's a suggestion from your existential
                // controller.

                // Start a new interaction
                Interaction interaction = new Interaction();
                interactions.add(interaction);

                // Add impression to interaction
                interaction.add(impression);

                // Record interactions on timeline
                // TODO: Cache and store the apply interactions before deleting them completely! Do it in
                // TODO: (cont'd) a background thread.
                if (interactions.size() > 3) {
                    interactions.remove(0);
                }

                // Process the impression
                onTouchListener(interaction);

                break;
            }

            case MOVE: {

                impression.setBody(this);

                // Current
                impression.isTouching[impression.pointerIndex] = true;

                Interaction interaction = getInteraction();
                interaction.add(impression);

                // Calculate drag distance
                interaction.dragDistance[impression.pointerIndex] = Geometry.calculateDistance(impression.getPosition(), interaction.getFirst().touchPositions[impression.pointerIndex]);

                // Classify/Callback
                if (interaction.dragDistance[impression.pointerIndex] > Impression.MINIMUM_DRAG_DISTANCE) {
                    onDragListener(interaction);
                }

                break;
            }

            case RELEASE: {

                impression.setBody(this);

                Interaction interaction = getInteraction();
                interaction.add(impression);

                // Stop listening for a hold impression
                interaction.timerHandler.removeCallbacks(interaction.timerRunnable);

                // Current
                impression.isTouching[impression.pointerIndex] = false;

                if (interaction.getDuration() < Impression.MAXIMUM_TAP_DURATION) {
                    onTapListener(interaction);
                } else {
                    onReleaseListener(interaction);
                }

                break;
            }
        }
    }

    private void onTouchListener(Interaction interaction) {

        Impression impression = interaction.getLast();

        Image touchedImage = perspective.getVisualization().getImageByPosition(impression.getPosition());
        impression.setTargetImage(touchedImage);

//        if (perspective.hasFocusImage()) {
//
//            if (perspective.getFocusImage().isType(FrameImage.TYPE, PortImage.TYPE, PathImage.TYPE)) {
//
//                if (impression.containsPoint() && impression.getImageByPosition().isType(PortImage.TYPE)) {
////                    Log.v("Impression", "BUH");
////                    perspective.InteractionfocusOnPort((PortImage) impression.getImageByPosition());
//                }
//            }
//
//            if (perspective.getFocusImage().isType(PortImage.TYPE, PathImage.TYPE)) {
//
//                if (impression.containsPoint() && impression.getImageByPosition().isType(PathImage.TYPE)) {
////                    perspective.InteractionfocusOnPath((PathImage) impression.getImageByPosition());
//                }
//            }
//        }
//
//        // Reset object impression state
//        if (!perspective.hasFocusImage() || perspective.getFocusImage().isType(FrameImage.TYPE, PortImage.TYPE)) {
//
//            if (impression.containsPoint() && impression.getImageByPosition().isType(FrameImage.TYPE)) {
////                perspective.InteractionfocusOnForm((FrameImage) impression.getImageByPosition());
//            }
//
//        }
//
//        if (!perspective.hasFocusImage() || perspective.getFocusImage().isType(FrameImage.TYPE, PortImage.TYPE, PathImage.TYPE)) {
//
//            if (!impression.containsPoint()) {
////                perspective.InteractionfocusReset();
//            }
//        }
    }

    public void onHoldListener(Interaction interaction) {

        Impression impression = interaction.getLast();

        impression.setType(Impression.Type.HOLD);

        Image targetImage = perspective.getVisualization().getImageByPosition(impression.getPosition());
        impression.setTargetImage(targetImage);

        interaction.isHolding[impression.pointerIndex] = true;

        if (impression.isTouching()) {

            if (impression.getTargetImage() instanceof FrameImage) {

                // TODO:

            } else if (impression.getTargetImage() instanceof PortImage) {

                // TODO:

            }
        }
    }

    private void onDragListener(Interaction interaction) {

        Impression impression = interaction.getLast();

        impression.setType(Impression.Type.DRAG);

        Image targetImage = perspective.getVisualization().getImageByPosition(impression.getPosition());
        impression.setTargetImage(targetImage);

        Log.v("onDragListener", "" + impression.getType() + ": " + impression.getTargetImage());

        Log.v("Impression", "onDrag");
        Log.v("Impression", "focus: " + perspective.getFocusImage());
        Log.v("Impression", "apply: " + impression.getTargetImage());
        Log.v("Impression", "-");

        if (interaction.getSize() > 1) {
            impression.setTargetImage(interaction.getFirst().getTargetImage());
        }

        interaction.isDragging[impression.pointerIndex] = true;

        // Holding
        if (interaction.isHolding[impression.pointerIndex]) {

            // Holding and dragging

            // TODO: Put into callback
            if (impression.isTouching()) {

                if (impression.getTargetImage() instanceof FrameImage) {

                    // Frame

                    FrameImage frameImage = (FrameImage) impression.getTargetImage();
                    frameImage.apply(impression);
                    frameImage.setPosition(impression.getPosition());

                    // Perspective
                    perspective.focusOnFrame(this, interaction, impression);

                } else if (impression.getTargetImage() instanceof PortImage) {

                    // Port

                    PortImage portImage = (PortImage) impression.getTargetImage();
                    portImage.isTouched = true;
//                    Impression impression = new Impression(impression.getPosition(), Impression.Type.DRAG);
//                    portSprite.touchPositions(impression);

                    portImage.setDragging(true);
                    portImage.setPosition(impression.getPosition());

                } else if (impression.getTargetImage() instanceof Visualization) {

                    // Visualization

                    if (perspective.isAdjustable()) {

//                        perspective.setScale(0.9f);
//                        perspective.setOffset(
//                                impression.getPosition().getX() - interaction.getFirst().getPosition().getX(),
//                                impression.getPosition().getY() - interaction.getFirst().getPosition().getY()
//                        );

                        perspective.focusOnVisualization(interaction);

                    }

                }

            }

        } else {

            // Not holding. Drag was detected prior to the hold duration threshold.

            if (impression.isTouching()) {

                if (impression.getTargetImage() instanceof FrameImage) {

                    // Frame

                    FrameImage frameImage = (FrameImage) impression.getTargetImage();
                    frameImage.apply(impression);
                    frameImage.setPosition(impression.getPosition());

                    perspective.focusOnFrame(this, interaction, impression);

                } else if (impression.getTargetImage() instanceof PortImage) {

                    // Port

                    PortImage portImage = (PortImage) impression.getTargetImage();
                    portImage.apply(impression);

                    perspective.focusOnNewPath(interaction, impression);

                } else if (impression.getTargetImage() instanceof PatchImage) {

                    // Patch

                    PatchImage patchImage = (PatchImage) impression.getTargetImage();
                    patchImage.setPosition(impression.getPosition());
                    patchImage.apply(impression);

                } else if (impression.getTargetImage() instanceof Visualization) {

                    // Visualization

                    if (interaction.getSize() > 1) {

                        perspective.setOffset(
                                impression.getPosition().getX() - interaction.getFirst().getPosition().getX(),
                                impression.getPosition().getY() - interaction.getFirst().getPosition().getY()
                        );

                    }

                }
            }
        }
    }

    private void onTapListener(Interaction interaction) {

        Impression impression = interaction.getLast();

        impression.setType(Impression.Type.TAP);

        Image targetImage = perspective.getVisualization().getImageByPosition(impression.getPosition());
        impression.setTargetImage(targetImage);

        Log.v("Impression", "onTap");
        Log.v("Impression", "focus: " + perspective.getFocusImage());
        Log.v("Impression", "apply: " + impression.getTargetImage());
        Log.v("Impression", "-");

        if (impression.isTouching()) {

            if (impression.getTargetImage() instanceof FrameImage) {

                // Frame
                FrameImage frameImage = (FrameImage) impression.getTargetImage();
                frameImage.apply(impression);

                // Perspective
                perspective.focusOnFrame(this, interaction, impression);

            } else if (impression.getTargetImage() instanceof PortImage) {

                // Port
                PortImage portImage = (PortImage) impression.getTargetImage();
                portImage.apply(impression);

            } else if (impression.getTargetImage() instanceof PathImage) {

                // Path
                PathImage pathImage = (PathImage) impression.getTargetImage();
                pathImage.apply(impression);

            } else if (impression.getTargetImage() instanceof PatchImage) {

                // Patch
                PatchImage patchImage = (PatchImage) impression.getTargetImage();
                patchImage.apply(impression);

            } else if (impression.getTargetImage() instanceof Visualization) {

                // Visualization
                perspective.focusReset();

            }

        }

    }

    private void onReleaseListener(Interaction interaction) {

        Impression impression = interaction.getLast();

        impression.setType(Impression.Type.RELEASE);

        Image targetImage = perspective.getVisualization().getImageByPosition(impression.getPosition());
        impression.setTargetImage(targetImage);

        Log.v("Impression", "onRelease");
        Log.v("Impression", "focus: " + perspective.getFocusImage());
        Log.v("Impression", "apply: " + impression.getTargetImage());
        Log.v("Impression", "-");

        if (impression.isTouching()) {

            // First apply was on a frame image...
            if (interaction.getFirst().getTargetImage() instanceof FrameImage) {

                if (impression.getTargetImage() instanceof FrameImage) {

                    // If first apply was on the same form, then respond
                    if (interaction.getFirst().isTouching() && interaction.getFirst().getTargetImage() instanceof FrameImage) {

                        // Frame
                        FrameImage frameImage = (FrameImage) impression.getTargetImage();
                        frameImage.apply(impression);

                        // Perspective
                        perspective.focusReset();
                    }

                }

            } else if (interaction.getFirst().getTargetImage() instanceof PortImage) {

                // First apply was on a port image...

                if (impression.getTargetImage() instanceof FrameImage) {

                    // ...last apply was on a frame image.

                    PortImage sourcePortImage = (PortImage) interaction.getFirst().getTargetImage();
                    sourcePortImage.setCandidatePathVisibility(false);

                } else if (impression.getTargetImage() instanceof PortImage) {

                    // ...last apply was on a port image.

                    // PortImage portImage = (PortImage) impression.getImageByPosition();
                    PortImage sourcePortImage = (PortImage) interaction.getFirst().getTargetImage();

                    if (sourcePortImage.isDragging()) {

                        // Get nearest port image
                        PortImage nearestPortImage = (PortImage) getPerspective().getVisualization().getImages().filterType(PortImage.class).getNearest(impression.getPosition());
                        Port nearestPort = nearestPortImage.getPort();
                        Log.v("DND", "nearestPort: " + nearestPort);

                        // TODO: When dragging, enable pushing ports?

                        // Remove the paths from the port and move them to the selected port
//                        Port nearestPort = nearestPortImage.getPort();
//                        while (sourcePortImage.getPort().getPaths().size() > 0) {
//                            Path path = sourcePortImage.getPort().getPaths().remove(0);
//                            path.setSource(nearestPort);
//                            nearestPort.addPath(path);
//                        }

                        Port sourcePort = sourcePortImage.getPort();

                        List<Path> paths = getPerspective().getVisualization().getSimulation().getPaths();

                        // Copy configuration
                        nearestPort.setDirection(sourcePort.getDirection());
                        nearestPort.setType(sourcePort.getType());
                        nearestPortImage.setUniqueColor(sourcePortImage.getUniqueColor());

                        // Reset port configuration
                        sourcePort.setDirection(Port.Direction.NONE);
                        sourcePort.setType(Port.Type.NONE);
                        sourcePortImage.updateUniqueColor();

                        // Clear the port's list of paths
                        sourcePort.getPaths().clear();

                        // Copy paths
                        for (Path path : paths) {

                            // Update source
                            if (path.getSource() == sourcePort) {
                                path.setSource(nearestPort);
                                Log.v("DND", "Updating source");
                            }

                            // Update target
                            if (path.getTarget() == sourcePort) {
                                path.setTarget(nearestPort);
                                Log.v("DND", "Updating target");
                            }

//                            Path replacementPath = new Path(nearestPortImage.getPort(), path.getTarget());
//                            nearestPortImage.getPort().addPath(replacementPath);
//
//                            PathImage replacementPathImage = new PathImage(path);
//                            replacementPathImage.setVisualization(perspective.getVisualization());
//                            perspective.getVisualization().addImage(path, replacementPathImage, "paths");
//
//                            PortImage targetPortImage = (PortImage) perspective.getVisualization().getImage(path.getTarget());
//                            targetPortImage.setUniqueColor(sourcePortImage.getUniqueColor());

                            nearestPort.addPath(path);

                        }

                        // Restore port image's position
                        sourcePortImage.setDragging(false);

                        // Perspective
                        perspective.focusOnPath(sourcePort);

                    } else {

//                if (sourcePortImage.getCandidatePeripheralVisibility() == true) {
//
//                    // Model
//                    Patch peripheral = new Patch();
//                    getPerspective().getVisualization().getSimulation().addPatch(peripheral);
//
//                    // Visualization (Layer)
//                    String layerTag = "peripherals";
//                    getPerspective().getVisualization().addLayer(layerTag);
//                    Layer defaultLayer = getPerspective().getVisualization().getLayer(layerTag);
//
//                    // Image
//                    PatchImage peripheralImage = new PatchImage(peripheral);
//                    peripheralImage.setPosition(impression.getPosition());
//                    peripheralImage.setVisualization(getPerspective().getVisualization());
//
//                    // Visualization
//                    getPerspective().getVisualization().addImage(peripheral, peripheralImage, layerTag);
//
//                }

                        // Show ports of nearby forms
                        boolean useNearbyPortImage = false;
                        for (FrameImage nearbyFrameImage : perspective.getVisualization().getFrameImages()) {

                            Log.v("Impression", "A");

                            // Update style of nearby machines
                            double distanceToFrameImage = Geometry.calculateDistance(
                                    impression.getPosition(),
                                    nearbyFrameImage.getPosition()
                            );

                            if (distanceToFrameImage < nearbyFrameImage.getBoundingRectangle().getHeight() + 50) {

                                Log.v("Impression", "B");

                                // TODO: Use overlappedImage instanceof PortImage

                                for (PortImage nearbyPortImage : nearbyFrameImage.getPortImages()) {

                                    if (nearbyPortImage != sourcePortImage) {
                                        if (nearbyPortImage.containsPoint(impression.getPosition(), 50)) {

                                            Log.v("Impression", "C");

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

                                                Log.v("Impression", "D.1");

                                                Path path = new Path(sourcePort, targetPort);
                                                path.setType(Path.Type.MESH);
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

//                portImage.apply(impression);

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
//                Rectangle boundingBox = Geometry.getBoundingBox(pathPortPositions);
//                getVisualization().getSimulation().getBody(0).getPerspective().adjustScale(boundingBox);
//
//                getVisualization().getSimulation().getBody(0).getPerspective().setPosition(Geometry.calculateCenterPosition(pathPortPositions));

//                impression.setTargetImage(interaction.getFirst().getImageByPosition());
//                impression.setType(Impression.Type.RELEASE);
//                Log.v("onHoldListener", "Source port: " + impression.getImageByPosition());
//                targetImage.apply(impression);

                    }

                } else if (impression.getTargetImage() instanceof PatchImage) {

                    PortImage sourcePortImage = (PortImage) interaction.getFirst().getTargetImage();

                    // Update Image
                    sourcePortImage.setCandidatePathVisibility(false);
                    sourcePortImage.setCandidatePeripheralVisibility(false);

                } else if (impression.getTargetImage() instanceof Visualization) {

                    PortImage sourcePortImage = (PortImage) interaction.getFirst().getTargetImage();

                    if (sourcePortImage.getCandidatePeripheralVisibility() == true) {

                        // Model
                        Patch patch = new Patch();
                        patch.setParent(getPerspective().getVisualization().getSimulation());

                        // Add port to model
                        for (int j = 0; j < 3; j++) {
                            Port port = new Port();
                            patch.addPort(port);
                        }

                        getPerspective().getVisualization().getSimulation().addPatch(patch);

                        // Visualization (Layer)
                        String layerTag = "peripherals";
                        getPerspective().getVisualization().addLayer(layerTag);
                        Layer defaultLayer = getPerspective().getVisualization().getLayer(layerTag);

                        // Create Patch Image
                        PatchImage patchImage = new PatchImage(patch);
                        patchImage.setPosition(impression.getPosition());
                        // patchImage.setRotation();
                        patchImage.setVisualization(getPerspective().getVisualization());

                        double pathRotationAngle = Geometry.calculateRotationAngle(
                                sourcePortImage.getPosition(),
                                patchImage.getPosition()
                        );
                        patchImage.setRotation(pathRotationAngle + 90);

                        // Create Port Images for each of Patch's Ports
                        for (Port port : patch.getPorts()) {
                            PortImage portImage = new PortImage(port);
                            portImage.setVisualization(getPerspective().getVisualization());
                            getPerspective().getVisualization().addImage(port, portImage, "ports");
                        }

                        // Add Patch Image to Visualization
                        getPerspective().getVisualization().addImage(patch, patchImage, layerTag);

                        // Configure Ports
                        Port sourcePort = sourcePortImage.getPort();
                        Port destinationPort = patch.getPorts().get(0);

                        if (sourcePort.getDirection() == Port.Direction.NONE) {
                            sourcePort.setDirection(Port.Direction.OUTPUT);
                        }
//                        if (sourcePort.getType() == Port.Type.NONE) {
                        //sourcePort.setType(Port.Type.next(sourcePort.getType())); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length
                        sourcePort.setType(Port.Type.POWER_REFERENCE);
//                        }

                        destinationPort.setDirection(Port.Direction.INPUT);
                        //destinationPort.setType(Port.Type.next(destinationPort.getType()));
                        destinationPort.setType(sourcePort.getType());

                        // Create Path
                        Path path = new Path(sourcePortImage.getPort(), patch.getPorts().get(0));
                        path.setType(Path.Type.ELECTRONIC);
                        sourcePort.addPath(path);

                        PathImage pathImage = new PathImage(path);
                        pathImage.setVisualization(perspective.getVisualization());
                        perspective.getVisualization().addImage(path, pathImage, "paths");

                        PortImage targetPortImage = (PortImage) perspective.getVisualization().getImage(path.getTarget());
                        targetPortImage.setUniqueColor(sourcePortImage.getUniqueColor());

                        // Update Perspective
                        perspective.focusOnPath(sourcePortImage.getPort());

                    }

                    // Update Image
                    sourcePortImage.setCandidatePathVisibility(false);
                    sourcePortImage.setCandidatePeripheralVisibility(false);

                }

            } else if (interaction.getFirst().getTargetImage() instanceof PathImage) {

                // Path --> ?

                if (impression.getTargetImage() instanceof PathImage) {
                    // Path --> Path
                    PathImage pathImage = (PathImage) impression.getTargetImage();
                }

            } else if (interaction.getFirst().getTargetImage() instanceof Visualization) {

                // Visualization --> ?

                // Check if first apply was on an image
                if (interaction.getFirst().getTargetImage() instanceof PortImage) {
                    ((PortImage) interaction.getFirst().getTargetImage()).setCandidatePathVisibility(false);
                }

                perspective.focusReset();

            }

        }

        // Interaction
        perspective.setAdjustability(true);
    }
}
