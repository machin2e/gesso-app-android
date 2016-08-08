package camp.computer.clay.model.architecture;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.model.interactivity.*;
import camp.computer.clay.model.interactivity.Action;
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

    public void onImpression(Action action) {

        action.setBody(this);

        switch (action.getType()) {

            case TOUCH: {

                // Having an idea is just accumulating intention. It's a suggestion from your existential
                // controller.

                // Start a new interaction
                Interaction interaction = new Interaction();
                interactions.add(interaction);

                // Add action to interaction
                interaction.add(action);

                // Record interactions on timeline
                // TODO: Cache and store the apply interactions before deleting them completely! Do it in
                // TODO: (cont'd) a background thread.
                if (interactions.size() > 3) {
                    interactions.remove(0);
                }

                // Process the action
                onTouchListener(interaction);

                break;
            }

            case MOVE: {

                Interaction interaction = getInteraction();
                interaction.add(action);

                // Current
                action.isTouching[action.pointerIndex] = true;

                // Calculate drag distance
                interaction.dragDistance[action.pointerIndex] = Geometry.calculateDistance(action.getPosition(), interaction.getFirst().touchPoints[action.pointerIndex]);

                // Classify/Callback
                if (interaction.dragDistance[action.pointerIndex] > Action.MIN_DRAG_DISTANCE) {
                    onDragListener(interaction);
                }

                break;
            }

            case RELEASE: {

                Interaction interaction = getInteraction();
                interaction.add(action);

                // Current
                action.isTouching[action.pointerIndex] = false;

                // Stop listening for a hold action
                interaction.timerHandler.removeCallbacks(interaction.timerRunnable);

                if (interaction.getDuration() < Action.MAX_TAP_DURATION) {
                    onTapListener(interaction);
                } else {
                    onReleaseListener(interaction);
                }

                break;
            }
        }
    }

    private void onTouchListener(Interaction interaction) {

        Action action = interaction.getLast();

        Image targetImage = perspective.getVisualization().getImageByPosition(action.getPosition());
        action.setTargetImage(targetImage);

    }

    public void onHoldListener(Interaction interaction) {

        Action action = interaction.getLast();

        action.setType(Action.Type.HOLD);

        Image targetImage = perspective.getVisualization().getImageByPosition(action.getPosition());
        action.setTargetImage(targetImage);

        interaction.isHolding[action.pointerIndex] = true;

        if (action.isTouching()) {

            if (action.getTargetImage() instanceof FrameImage) {

                // TODO:

            } else if (action.getTargetImage() instanceof PortImage) {

                // TODO:

            }
        }
    }

    private void onDragListener(Interaction interaction) {

        camp.computer.clay.model.interactivity.Action action = interaction.getLast();

        action.setType(Action.Type.DRAG);

        Image targetImage = perspective.getVisualization().getImageByPosition(action.getPosition());
        action.setTargetImage(targetImage);

        Log.v("onDragListener", "" + action.getType() + ": " + action.getTargetImage());

        Log.v("Action", "onDrag");
        Log.v("Action", "focus: " + perspective.getFocusImage());
        Log.v("Action", "apply: " + action.getTargetImage());
        Log.v("Action", "-");

        if (interaction.getSize() > 1) {
            action.setTargetImage(interaction.getFirst().getTargetImage());
        }

        interaction.isDragging[action.pointerIndex] = true;

        // Holding
        if (interaction.isHolding[action.pointerIndex]) {

            // Holding and dragging

            // TODO: Put into callback
            if (action.isTouching()) {

                if (action.getTargetImage() instanceof FrameImage) {

                    // Frame

                    FrameImage frameImage = (FrameImage) action.getTargetImage();
                    frameImage.apply(action);
                    frameImage.setPosition(action.getPosition());

                    // Perspective
                    perspective.focusOnFrame(this, interaction, action);

                } else if (action.getTargetImage() instanceof PortImage) {

                    // Port

                    PortImage portImage = (PortImage) action.getTargetImage();
                    portImage.isTouched = true;
//                    Action action = new Action(action.getPosition(), Action.Type.DRAG);
//                    portSprite.touchPoints(action);

                    portImage.setDragging(true);
                    portImage.setPosition(action.getPosition());

                } else if (action.getTargetImage() instanceof Visualization) {

                    // Visualization

                    if (perspective.isAdjustable()) {

//                        perspective.setScale(0.9f);
//                        perspective.setOffset(
//                                action.getPosition().getX() - interaction.getFirst().getPosition().getX(),
//                                action.getPosition().getY() - interaction.getFirst().getPosition().getY()
//                        );

                        perspective.focusOnVisualization(interaction);

                    }

                }

            }

        } else {

            // Not holding. Drag was detected prior to the hold duration threshold.

            if (action.isTouching()) {

                if (action.getTargetImage() instanceof FrameImage) {

                    // Frame

                    FrameImage frameImage = (FrameImage) action.getTargetImage();
                    frameImage.apply(action);
                    frameImage.setPosition(action.getPosition());

                    perspective.focusOnFrame(this, interaction, action);

                } else if (action.getTargetImage() instanceof PortImage) {

                    // Port

                    PortImage portImage = (PortImage) action.getTargetImage();
                    portImage.apply(action);

                    perspective.focusOnNewPath(interaction, action);

                } else if (action.getTargetImage() instanceof PatchImage) {

                    // Patch

                    PatchImage patchImage = (PatchImage) action.getTargetImage();
                    patchImage.setPosition(action.getPosition());
                    patchImage.apply(action);

                } else if (action.getTargetImage() instanceof Visualization) {

                    // Visualization

                    if (interaction.getSize() > 1) {

                        perspective.setOffset(
                                action.getPosition().getX() - interaction.getFirst().getPosition().getX(),
                                action.getPosition().getY() - interaction.getFirst().getPosition().getY()
                        );

                    }

                }
            }
        }
    }

    private void onTapListener(Interaction interaction) {

        Action action = interaction.getLast();

        action.setType(Action.Type.TAP);

        Image targetImage = perspective.getVisualization().getImageByPosition(action.getPosition());
        action.setTargetImage(targetImage);

        Log.v("Action", "onTap");
        Log.v("Action", "focus: " + perspective.getFocusImage());
        Log.v("Action", "apply: " + action.getTargetImage());
        Log.v("Action", "-");

        if (action.isTouching()) {

            if (action.getTargetImage() instanceof FrameImage) {

                // Frame
                FrameImage frameImage = (FrameImage) action.getTargetImage();
                frameImage.apply(action);

                // Perspective
                perspective.focusOnFrame(this, interaction, action);

            } else if (action.getTargetImage() instanceof PortImage) {

                // Port
                PortImage portImage = (PortImage) action.getTargetImage();
                portImage.apply(action);

            } else if (action.getTargetImage() instanceof PathImage) {

                // Path
                PathImage pathImage = (PathImage) action.getTargetImage();
                pathImage.apply(action);

            } else if (action.getTargetImage() instanceof PatchImage) {

                // Patch
                PatchImage patchImage = (PatchImage) action.getTargetImage();
                patchImage.apply(action);

            } else if (action.getTargetImage() instanceof Visualization) {

                // Visualization
                perspective.focusReset();

            }

        }

    }

    private void onReleaseListener(Interaction interaction) {

        Action action = interaction.getLast();

        action.setType(Action.Type.RELEASE);

        Image targetImage = perspective.getVisualization().getImageByPosition(action.getPosition());
        action.setTargetImage(targetImage);

        Log.v("Action", "onRelease");
        Log.v("Action", "focus: " + perspective.getFocusImage());
        Log.v("Action", "apply: " + action.getTargetImage());
        Log.v("Action", "-");

        if (action.isTouching()) {

            // First apply was on a frame image...
            if (interaction.getFirst().getTargetImage() instanceof FrameImage) {

                if (action.getTargetImage() instanceof FrameImage) {

                    // If first apply was on the same form, then respond
                    if (interaction.getFirst().isTouching() && interaction.getFirst().getTargetImage() instanceof FrameImage) {

                        // Frame
                        FrameImage frameImage = (FrameImage) action.getTargetImage();
                        frameImage.apply(action);

                        // Perspective
                        perspective.focusReset();
                    }

                }

            } else if (interaction.getFirst().getTargetImage() instanceof PortImage) {

                // First apply was on a port image...

                if (action.getTargetImage() instanceof FrameImage) {

                    // ...last apply was on a frame image.

                    PortImage sourcePortImage = (PortImage) interaction.getFirst().getTargetImage();
                    sourcePortImage.setCandidatePathVisibility(false);

                } else if (action.getTargetImage() instanceof PortImage) {

                    // ...last apply was on a port image.

                    // PortImage portImage = (PortImage) action.getImageByPosition();
                    PortImage sourcePortImage = (PortImage) interaction.getFirst().getTargetImage();

                    if (sourcePortImage.isDragging()) {

                        // Get nearest port image
                        PortImage nearestPortImage = (PortImage) getPerspective().getVisualization().getImages().filterType(PortImage.class).getNearest(action.getPosition());
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
//                    peripheralImage.setPosition(action.getPosition());
//                    peripheralImage.setVisualization(getPerspective().getVisualization());
//
//                    // Visualization
//                    getPerspective().getVisualization().addImage(peripheral, peripheralImage, layerTag);
//
//                }

                        // Show ports of nearby forms
                        boolean useNearbyPortImage = false;
                        for (FrameImage nearbyFrameImage : perspective.getVisualization().getFrameImages()) {

                            Log.v("Action", "A");

                            // Update style of nearby machines
                            double distanceToFrameImage = Geometry.calculateDistance(
                                    action.getPosition(),
                                    nearbyFrameImage.getPosition()
                            );

                            if (distanceToFrameImage < nearbyFrameImage.getBoundingRectangle().getHeight() + 50) {

                                Log.v("Action", "B");

                                // TODO: Use overlappedImage instanceof PortImage

                                for (PortImage nearbyPortImage : nearbyFrameImage.getPortImages()) {

                                    if (nearbyPortImage != sourcePortImage) {
                                        if (nearbyPortImage.containsPoint(action.getPosition(), 50)) {

                                            Log.v("Action", "C");

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

                                                Log.v("Action", "D.1");

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

//                portImage.apply(action);

                        if (!useNearbyPortImage) {

                            Port port = (Port) sourcePortImage.getModel();

                            port.setDirection(Port.Direction.INPUT);

                            if (port.getType() == Port.Type.NONE) {
                                port.setType(Port.Type.next(port.getType()));
                            }
                        }

                        sourcePortImage.setCandidatePathVisibility(false);

                        // ApplicationView.getDisplay().speakPhrase("setting as input. you can send the data to another board if you want. touchPoints another board.");

//                // Perspective
//                ArrayList<Port> pathPorts = port.getPorts(paths);
//                ArrayList<Image> pathPortImages = getVisualization().getImages(pathPorts);
//                ArrayList<Point> pathPortPositions = Visualization.getPositions(pathPortImages);
//                Rectangle boundingBox = Geometry.getBoundingBox(pathPortPositions);
//                getVisualization().getSimulation().getBody(0).getPerspective().adjustScale(boundingBox);
//
//                getVisualization().getSimulation().getBody(0).getPerspective().setPosition(Geometry.calculateCenterPosition(pathPortPositions));

//                action.setTargetImage(interaction.getFirst().getImageByPosition());
//                action.setType(Action.Type.RELEASE);
//                Log.v("onHoldListener", "Source port: " + action.getImageByPosition());
//                targetImage.apply(action);

                    }

                } else if (action.getTargetImage() instanceof PatchImage) {

                    PortImage sourcePortImage = (PortImage) interaction.getFirst().getTargetImage();

                    // Update Image
                    sourcePortImage.setCandidatePathVisibility(false);
                    sourcePortImage.setCandidatePeripheralVisibility(false);

                } else if (action.getTargetImage() instanceof Visualization) {

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
                        patchImage.setPosition(action.getPosition());
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

                if (action.getTargetImage() instanceof PathImage) {
                    // Path --> Path
                    PathImage pathImage = (PathImage) action.getTargetImage();
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
