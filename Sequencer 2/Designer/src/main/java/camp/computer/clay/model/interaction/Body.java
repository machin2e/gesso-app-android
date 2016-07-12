package camp.computer.clay.model.interaction;

import android.graphics.PointF;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.mobeta.android.sequencer.R;

import java.util.ArrayList;

import camp.computer.clay.application.Application;
import camp.computer.clay.model.simulation.Actor;
import camp.computer.clay.model.simulation.Path;
import camp.computer.clay.model.simulation.Port;
import camp.computer.clay.visualization.Image;
import camp.computer.clay.visualization.MachineImage;
import camp.computer.clay.visualization.PathImage;
import camp.computer.clay.visualization.PortImage;
import camp.computer.clay.visualization.Visualization;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.Rectangle;

public class Body extends Actor {

    private Perspective perspective;

    private TouchInteractivity touchInteractivity;

    public Body() {
    }

    public void setPerspective(Perspective perspective) {
        this.perspective = perspective;
    }

    public Perspective getPerspective() {
        return this.perspective;
    }

    private void adjustPerspectiveScale() {

        Log.v("SetScale", "adjustPerspectiveScale");

        // Adjust scale
        ArrayList<PointF> machineImagePositions = Visualization.getPositions(getPerspective().getVisualization().getMachineImages());
        if (machineImagePositions.size() > 0) {
            Rectangle boundingBox = Geometry.calculateBoundingBox(machineImagePositions);
            adjustPerspectiveScale(boundingBox);
        }

//        PointF spriteBoundingBoxCenter = new PointF(((boundingBox[2] - boundingBox[0]) / 2.0f), ((boundingBox[3] - boundingBox[1]) / 2.0f));
//        float spriteBoundingBoxWidth = boundingBox[2] - boundingBox[0];
//        float spriteBoundingBoxHeight = boundingBox[3] - boundingBox[1];
//
//        float widthDifference = spriteBoundingBoxWidth - getPerspective().getWidth();
//        float heightDifference = spriteBoundingBoxHeight - getPerspective().getHeight();
//
//        float widthPadding = 0;
//        float newWidthScale = getPerspective().getWidth() / (spriteBoundingBoxWidth + widthPadding);
//        float newHeightScale = getPerspective().getHeight() / (spriteBoundingBoxHeight + widthPadding);
//
//        if (widthDifference > 0 /* || heightDifference > 0 */) {
////            if (newWidthScale > newHeightScale) {
//            getPerspective().setScale(newWidthScale);
////            } else {
////                getPerspective().setScale(newHeightScale);
////            }
//        } else {
//            getPerspective().setScale(1.0f);
//        }
    }

    public void adjustPerspectiveScale(Rectangle boundingBox) {

        PointF boundingBoxCenter = boundingBox.getPosition();
//        float boundingBoxWidth = boundingBox[2] - boundingBox[0];
//        float boundingBoxHeight = boundingBox[3] - boundingBox[1];

        float widthDifference = boundingBox.getWidth() - getPerspective().getWidth();
        float heightDifference = boundingBox.getHeight() - getPerspective().getHeight();

        float padding = 200;
        float newWidthScale = getPerspective().getWidth() / (boundingBox.getWidth() + padding);
        float newHeightScale = getPerspective().getHeight() / (boundingBox.getHeight() + padding);

        Log.v("Perspective", "boundingWidth: " + boundingBox.getWidth());
        Log.v("Perspective", "perspectiveWidth: " + getPerspective().getWidth());

        Log.v("Perspective", "boundingHeight: " + boundingBox.getHeight());
        Log.v("Perspective", "perspectiveHeight: " + getPerspective().getHeight());

        Log.v("Perspective", "widthDifference: " + widthDifference);
        Log.v("Perspective", "heightDifference: " + heightDifference);

        if (widthDifference > 0 && widthDifference > heightDifference) {
            getPerspective().setScale(newWidthScale);
        } else if (heightDifference > 0 && heightDifference > widthDifference) {
            getPerspective().setScale(newHeightScale);
        } else {
            getPerspective().setScale(1.0f);
        }
    }

    public void onStartInteractivity(TouchInteraction touchInteraction) {
        Log.v("MapViewEvent", "onStartInteractivity");

        // Having an idea is just accumulating intention. It's a suggestion from your existential
        // controller.

        touchInteractivity = new TouchInteractivity();

        touchInteractivity.addInteraction(touchInteraction);

        onTouchListener(touchInteractivity, touchInteraction);
    }

    //private void onContinueInteractivity(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
    public void onContinueInteractivity(TouchInteraction touchInteraction) {
        Log.v("MapViewEvent", "onContinueInteractivity");

        // Current
        touchInteraction.isTouching[touchInteraction.pointerId] = true;

        // Calculate drag distance
        touchInteractivity.dragDistance[touchInteraction.pointerId] = Geometry.calculateDistance(touchInteraction.touchPositions[touchInteraction.pointerId], touchInteractivity.getFirstInteraction().touchPositions[touchInteraction.pointerId]);

        // Classify/Callback
        if (touchInteractivity.dragDistance[touchInteraction.pointerId] < TouchInteraction.MINIMUM_DRAG_DISTANCE) {
            // Pre-dragging
            onPreDragListener(touchInteractivity, touchInteraction);
        } else {
            // Dragging
            onDragListener(touchInteractivity, touchInteraction);
        }

        touchInteractivity.addInteraction(touchInteraction);
    }

    //private void onCompleteInteractivity(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
    public void onCompleteInteractivity(TouchInteraction touchInteraction) {

        Log.v("MapViewEvent", "onCompleteInteractivity");

        // Stop listening for a hold interaction
        touchInteractivity.timerHandler.removeCallbacks(touchInteractivity.timerRunnable);

        // Current
        touchInteraction.isTouching[touchInteraction.pointerId] = false;

        // Classify/Callbacks
        if (touchInteractivity.isHolding[touchInteraction.pointerId]) {
            onReleaseListener(touchInteractivity, touchInteraction);
        } else {
            if (touchInteractivity.getDuration() < TouchInteraction.MAXIMUM_TAP_DURATION) {
                onTapListener(touchInteractivity, touchInteraction);
            } else {
                onPressListener(touchInteractivity, touchInteraction);
            }
        }

        touchInteractivity.addInteraction(touchInteraction);
    }

    private void onTouchListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        touchInteraction.setType(TouchInteraction.TouchInteractionType.TOUCH);

        // Current
        touchInteraction.isTouching[touchInteraction.pointerId] = true;

        // First
        if (touchInteraction == touchInteractivity.getFirstInteraction()) {

            // TODO: Move touchPositions checking into Visualization.getTouchedSprite(Body);

            if (getPerspective().getFocus() instanceof MachineImage || getPerspective().getFocus() instanceof PortImage || getPerspective().getFocus() instanceof PathImage) {
                for (MachineImage machineImage : getPerspective().getVisualization().getMachineImages()) {

                    if (touchInteractivity.touchedImage[touchInteraction.pointerId] == null) {
                        for (PortImage portImage: machineImage.getPortImages()) {

                            // If perspective is on path, then constraint interactions to ports in the path
                            if (getPerspective().getFocus() instanceof PathImage) {
                                PathImage focusedPathImage = (PathImage) getPerspective().getFocus();
                                if (!focusedPathImage.getPath().contains((Port) portImage.getModel())) {
                                    // Log.v("InteractionHistory", "Skipping port not in path.");
                                    continue;
                                }
                            }

                            if (portImage.isTouching(touchInteraction.touchPositions[touchInteraction.pointerId], 50)) {

                                // Interactivity
                                touchInteractivity.isTouchingImage[touchInteraction.pointerId] = true;
                                touchInteractivity.touchedImage[touchInteraction.pointerId] = portImage;

                                // Perspective
                                if (getPerspective().getFocus() instanceof PathImage) {
                                    PathImage focusedPathImage = (PathImage) getPerspective().getFocus();
                                    Path path = (Path) focusedPathImage.getModel();
                                    if (path.getSource() == portImage.getPort()) {
                                        // <PERSPECTIVE>
                                        getPerspective().setFocus(portImage);
                                        getPerspective().disablePanning();
                                        // </PERSPECTIVE>
                                    }
                                } else {
                                    // <PERSPECTIVE>
                                    getPerspective().setFocus(portImage);
                                    getPerspective().disablePanning();
                                    // </PERSPECTIVE>
                                }

                                // TODO: Action

//                                    // <TOUCH_ACTION>
//                                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touchPositions[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TOUCH);
//                                    portSprite.touchPositions(touchInteraction);
//                                    // </TOUCH_ACTION>

                                break;
                            }
                        }
                    }
                }
            }

            if (getPerspective().getFocus() instanceof PortImage || getPerspective().getFocus() instanceof PathImage) {
                for (MachineImage machineImage : getPerspective().getVisualization().getMachineImages()) {
                    if (touchInteractivity.touchedImage[touchInteraction.pointerId] == null) {
                        for (PortImage portImage : machineImage.getPortImages()) {
                            for (PathImage pathImage: portImage.getPathImages()) {

                                PortImage sourcePortImage = (PortImage) getPerspective().getVisualization().getImage(pathImage.getPath().getSource());
                                PortImage targetPortImage = (PortImage) getPerspective().getVisualization().getImage(pathImage.getPath().getTarget());

                                float distanceToLine = (float) Geometry.calculateLineToPointDistance(
                                        // TODO: getPerspective().getVisualization().getImage(<Port/Model>)
                                        sourcePortImage.getPosition(),
                                        targetPortImage.getPosition(),
                                        touchInteraction.touchPositions[touchInteraction.pointerId],
                                        true
                                );

                                //Log.v("DistanceToLine", "distanceToLine: " + distanceToLine);

                                if (distanceToLine < 60) {

                                    Log.v("PathTouch", "start touchPositions on path " + pathImage);

//                                        // <TOUCH_ACTION>
//                                        TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touchPositions[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TOUCH);
//                                        pathSprite.touchPositions(touchInteraction);
//                                        // </TOUCH_ACTION>

                                    touchInteractivity.isTouchingImage[touchInteraction.pointerId] = true;
                                    touchInteractivity.touchedImage[touchInteraction.pointerId] = pathImage;

                                    // <PERSPECTIVE>
                                    getPerspective().setFocus(pathImage);
                                    getPerspective().disablePanning();
                                    // </PERSPECTIVE>

                                    break;
                                }
                            }
                        }
                    }
                }

                // TODO: Check for touchPositions on path flow editor (i.e., spreadsheet or JS editors)
            }

            // Reset object interaction state
            if (getPerspective().getFocus() == null || getPerspective().getFocus() instanceof MachineImage || getPerspective().getFocus() instanceof PortImage) {
                for (MachineImage machineImage : getPerspective().getVisualization().getMachineImages()) {
                    // Log.v ("MapViewTouch", "Object at " + machineSprite.x + ", " + machineSprite.y);
                    // Check if one of the objects is touched
                    if (touchInteractivity.touchedImage[touchInteraction.pointerId] == null) {
                        if (machineImage.isTouching(touchInteraction.touchPositions[touchInteraction.pointerId])) {

//                                // <TOUCH_ACTION>
//                                TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touchPositions[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TOUCH);
//                                machineSprite.touchPositions(touchInteraction);
//                                // </TOUCH_ACTION>

                            // TODO: Add this to an onTouch callback for the sprite's channel nodes
                            // TODO: i.e., callback Image.onTouch (via Image.touchPositions())

                            touchInteractivity.isTouchingImage[touchInteraction.pointerId] = true;
                            touchInteractivity.touchedImage[touchInteraction.pointerId] = machineImage;

                            // <PERSPECTIVE>
                            getPerspective().setFocus(machineImage);
                            getPerspective().disablePanning();
                            // </PERSPECTIVE>

                            // Break to limit the number of objects that can be touchPositions by a finger to one (1:1 finger:touchPositions relationship).
                            break;

                        }
                    }
                }
            }

            if (getPerspective().getFocus() == null || getPerspective().getFocus() instanceof MachineImage || getPerspective().getFocus() instanceof PortImage || getPerspective().getFocus() instanceof PathImage) {
                // Touch the canvas
                if (touchInteractivity.touchedImage[touchInteraction.pointerId] == null) {

                    // <INTERACTION>
                    touchInteractivity.isTouchingImage[touchInteraction.pointerId] = false;
                    // </INTERACTION>

                    // <PERSPECTIVE>
                    this.getPerspective().setFocus(null);
                    // this.isPanningEnabled = false;
                    // </PERSPECTIVE>
                }
            }
        }

    }

    private void onTapListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof MachineImage) {
            MachineImage machineImage = (MachineImage) touchInteractivity.touchedImage[touchInteraction.pointerId];


            // TODO: Add this to an onTouch callback for the sprite's channel nodes
            // Check if the touched board's I/O node is touched
            // Check if one of the objects is touched
            if (machineImage.isTouching(touchInteraction.touchPositions[touchInteraction.pointerId])) {
                Log.v("MapView", "\tTouched machine.");

                // ApplicationView.getDisplay().speakPhrase(machine.getNameTag());

                // <TOUCH_ACTION>
//                TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touchPositions[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TAP);
                // TODO: propagate RELEASE before TAP
                machineImage.touch(touchInteraction);
                // </TOUCH_ACTION>

                // Remove focus from other machines.
                for (MachineImage otherMachineImage: getPerspective().getVisualization().getMachineImages()) {
                    otherMachineImage.hidePortImages();
                    otherMachineImage.hidePathImages();
                    otherMachineImage.setTransparency(0.1f);
                }

                // Focus on machine.
                machineImage.showPortImages();
                machineImage.showPathImages();
                machineImage.setTransparency(1.0f);

                // ApplicationView.getDisplay().speakPhrase("choose a channel to get data.");

                for (PortImage portImage: machineImage.getPortImages()) {
                    ArrayList<Path> paths = getPerspective().getVisualization().getSimulation().getPathsByPort(portImage.getPort());
                    for (Path connectedPath : paths) {
                        // Show ports
                        ((PortImage) getPerspective().getVisualization().getImage(connectedPath.getSource())).setVisibility(true);
//                        ((PortImage) getPerspective().getVisualization().getImage(connectedPath.getSource())).showPathImages();
                        ((PortImage) getPerspective().getVisualization().getImage(connectedPath.getTarget())).setVisibility(true);
//                        ((PortImage) getPerspective().getVisualization().getImage(connectedPath.getTarget())).showPathImages();
                        // Show path
                        getPerspective().getVisualization().getImage(connectedPath).setVisibility(true);
                    }
                }

                // Perspective
                if (machineImage.getMachine().getPaths().size() > 0
                        && touchInteractivity.getPreviousInteraction().getOverlappedImage() != machineImage) {

                    // Get ports along every path connected to the ports on the touched machine
                    ArrayList<Port> machinePathPorts = new ArrayList<>();
                    for (Port port: machineImage.getMachine().getPorts()) {

                        if (!machinePathPorts.contains(port)) {
                            machinePathPorts.add(port);
                        }

                        ArrayList<Path> portPaths = getPerspective().getVisualization().getSimulation().getPathsByPort(port);
                        for (Path path: portPaths) {
                            if (!machinePathPorts.contains(path.getSource())) {
                                machinePathPorts.add(path.getSource());
                            }
                            if (!machinePathPorts.contains(path.getTarget())) {
                                machinePathPorts.add(path.getTarget());
                            }
                        }
                    }
                    ArrayList<Image> machinePathPortImages = getPerspective().getVisualization().getImages(machinePathPorts);

                    ArrayList<PointF> machinePortPositions = Visualization.getPositions(machinePathPortImages);
                    Rectangle boundingBox = Geometry.calculateBoundingBox(machinePortPositions);

                    getPerspective().setPosition(boundingBox.getPosition());

                    adjustPerspectiveScale(boundingBox);

                } else {

                    // Do this on second press, or when none of the machine's ports have paths.
                    // This provides lookahead, so you can be triggered to touch again to recover
                    // the perspective.

                    getPerspective().setScale(1.2f);
                    getPerspective().setPosition(machineImage.getPosition());
                }

                // TODO: (on second press, also hide external ports, expose peripherals) getPerspective().setScale(1.2f);
                // TODO: (cont'd) getPerspective().setPosition(machineImage.getPosition());

                getPerspective().disablePanning();
            }


        } else if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof PortImage) {
            PortImage portImage = (PortImage) touchInteractivity.touchedImage[touchInteraction.pointerId];

            Log.v("MapView", "\tPort " + (portImage.getIndex() + 1) + " touched.");

            if (portImage.isTouching(touchInteraction.touchPositions[touchInteraction.pointerId])) {
//                TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touchPositions[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TAP);
                portImage.touch(touchInteraction);

                Log.v("MapView", "\tSource port " + (portImage.getIndex() + 1) + " touched.");

                Port port = (Port) portImage.getModel();

                if (port.getType() == Port.Type.NONE) {

                    port.setDirection(Port.Direction.INPUT);
                    port.setType(Port.Type.getNextType(port.getType()));

                    // TODO: Speak ~ "setting as input. you can send the data to another board if you want. touchPositions another board."

                } else if (!port.hasPath() && getPerspective().getVisualization().getSimulation().getAncestorPathsByPort(port).size() == 0) {

                    // TODO: Replace with state of perspective. i.e., Check if seeing a single path.

                    Port.Type nextType = port.getType();
                    while ((nextType == Port.Type.NONE) || (nextType == port.getType())) {
                        nextType = Port.Type.getNextType(nextType);
                    }
                    port.setType(nextType);

                } else if (!portImage.hasVisiblePaths() && !portImage.hasVisibleAncestorPaths()) {

                    // TODO: Replace hasVisiblePaths() with check for focusedSprite/Path

                    // TODO: If second press, change the channel.

                    // Remove focus from other machines and their ports.
                    for (MachineImage machineImage : getPerspective().getVisualization().getMachineImages()) {
                        machineImage.setTransparency(0.05f);
                        machineImage.hidePortImages();
                        machineImage.hidePathImages();
                    }

                    // Reduce focus on the machine
                    portImage.getMachineImage().setTransparency(0.05f);

                    // Focus on the port
                    //portImage.getMachineImage().showPathImage(portImage.getIndex(), true);
                    portImage.showPaths();
                    portImage.setVisibility(true);
                    portImage.setPathVisibility(true);

                    ArrayList<Path> paths = getPerspective().getVisualization().getSimulation().getPathsByPort(portImage.getPort());
                    for (Path connectedPath: paths) {
                        // Show ports
                        ((PortImage) getPerspective().getVisualization().getImage(connectedPath.getSource())).setVisibility(true);
                        ((PortImage) getPerspective().getVisualization().getImage(connectedPath.getSource())).showPaths();
                        ((PortImage) getPerspective().getVisualization().getImage(connectedPath.getTarget())).setVisibility(true);
                        ((PortImage) getPerspective().getVisualization().getImage(connectedPath.getTarget())).showPaths();
                        // Show path
                        getPerspective().getVisualization().getImage(connectedPath).setVisibility(true);
                    }

                    // ApplicationView.getDisplay().speakPhrase("setting as input. you can send the data to another board if you want. touchPositions another board.");

                    // Perspective
                    ArrayList<Port> pathPorts = getPerspective().getVisualization().getSimulation().getPortsInPaths(paths);
                    ArrayList<Image> pathPortImages = getPerspective().getVisualization().getImages(pathPorts);
                    ArrayList<PointF> pathPortPositions = Visualization.getPositions(pathPortImages);
                    Rectangle boundingBox = Geometry.calculateBoundingBox(pathPortPositions);
                    adjustPerspectiveScale(boundingBox);

                    getPerspective().setPosition(Geometry.calculateCenterPosition(pathPortPositions));

                } else if (portImage.hasVisiblePaths() || portImage.hasVisibleAncestorPaths()) {

                    // Paths are being shown. Touching a port changes the port type. This will also
                    // updates the corresponding path requirement.

                    // TODO: Replace with state of perspective. i.e., Check if seeing a single path.

                    Port.Type nextType = port.getType();
                    while ((nextType == Port.Type.NONE) || (nextType == port.getType())) {
                        nextType = Port.Type.getNextType(nextType);
                    }
                    port.setType(nextType);

                }
            }

        } else if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof PathImage) {
            PathImage pathImage = (PathImage) touchInteractivity.touchedImage[touchInteraction.pointerId];

//            if (pathImage.getEditorVisibility()) {
//                pathImage.setEditorVisibility(false);
//            } else {
//                pathImage.setEditorVisibility(true);
//            }

            // TODO: Show path programmer (Create and curate actions built in JS by inserting
            // TODO: (cont'd) exposed ports into action ports. This defines a path)
            final RelativeLayout timelineView = (RelativeLayout) Application.getDisplay().findViewById(R.id.path_editor_view);
            timelineView.setVisibility(View.VISIBLE);


        } else if (touchInteractivity.touchedImage[touchInteraction.pointerId] == null) {

            // No touchPositions on board or port. Touch is on map. So hide ports.
            for (MachineImage machineImage : getPerspective().getVisualization().getMachineImages()) {
                machineImage.hidePortImages();
                machineImage.hidePathImages();
                machineImage.setTransparency(1.0f);
            }

            adjustPerspectiveScale();

            getPerspective().setPosition(getPerspective().getVisualization().getCentroidPosition());

            // Reset map interactivity
            getPerspective().enablePanning();
        }

    }

    private void onPressListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof MachineImage) {
            MachineImage machineImage = (MachineImage) touchInteractivity.touchedImage[touchInteraction.pointerId];

            // TODO: Add this to an onTouch callback for the sprite's channel nodes
            // Check if the touched board's I/O node is touched
            // Check if one of the objects is touched
            if (Geometry.calculateDistance(touchInteractivity.getFirstInteraction().touchPositions[touchInteraction.pointerId], machineImage.getPosition()) < 80) {
                Log.v("MapView", "\tSource board touched.");

//                    // <TOUCH_ACTION>
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touchPositions[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TAP);
//                    // TODO: propagate RELEASE before TAP
//                    machineSprite.touchPositions(touchInteraction);
//                    // </TOUCH_ACTION>

                // No touchPositions on board or port. Touch is on map. So hide ports.
                for (MachineImage otherMachineImage : getPerspective().getVisualization().getMachineImages()) {
                    otherMachineImage.hidePortImages();
                    otherMachineImage.hidePathImages();
                    otherMachineImage.setTransparency(0.1f);
                }
                machineImage.showPortImages();
//                getPerspective().setScale(0.8f);
                machineImage.showPathImages();
                machineImage.setTransparency(1.0f);
                // ApplicationView.getDisplay().speakPhrase("choose a channel to get data.");

                getPerspective().disablePanning();
            }

            // Zoom out to show overview
//            getPerspective().setScale(1.0f);

        } else if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof PortImage) {

            PortImage portImage = (PortImage) touchInteractivity.touchedImage[touchInteraction.pointerId];
//            TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touchPositions[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.RELEASE);
            portImage.touch(touchInteraction);

            // Show ports of nearby machines
            boolean useNearbyPortImage = false;
            for (MachineImage nearbyMachineImage: getPerspective().getVisualization().getMachineImages()) {

                // Update style of nearby machines
                float distanceToMachineImage = (float) Geometry.calculateDistance(
                        touchInteraction.touchPositions[touchInteraction.pointerId],
                        nearbyMachineImage.getPosition()
                );

                if (distanceToMachineImage < nearbyMachineImage.boardHeight + 50) {

                    // TODO: use overlappedImage instanceof PortImage

                    for (PortImage nearbyPortImage: nearbyMachineImage.getPortImages()) {

                        // Scaffold interaction to connect path to with nearby ports
                        float distanceToNearbyPortImage = (float) Geometry.calculateDistance(
                                touchInteraction.touchPositions[touchInteraction.pointerId],
                                nearbyPortImage.getPosition()
                        );

                        if (nearbyPortImage != portImage) {
                            if (distanceToNearbyPortImage < nearbyPortImage.shapeRadius + 50) {

                                Port port = (Port) portImage.getModel();
                                Port nearbyPort = (Port) nearbyPortImage.getModel();

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
                                Port sourcePort = (Port) getPerspective().getVisualization().getModel(portImage);
                                Port targetPort = (Port) getPerspective().getVisualization().getModel(nearbyPortImage);

                                if (!getPerspective().getVisualization().getSimulation().hasAncestor(sourcePort, targetPort)) {

                                    if (sourcePort.getPaths().size() == 0) {

                                        Path path = new Path(sourcePort, targetPort);
                                        sourcePort.addPath(path);

                                        PathImage pathImage = new PathImage(path);
                                        pathImage.setVisualization(getPerspective().getVisualization());
                                        getPerspective().getVisualization().addImage(path, pathImage, "paths");

                                        PortImage targetPortImage = (PortImage) getPerspective().getVisualization().getImage(path.getTarget());
                                        if (targetPort.getPaths().size() == 0) {
                                            targetPortImage.setUniqueColor(portImage.getUniqueColor());
                                        }

                                        // Remove focus from other machines and their ports.
                                        for (MachineImage machineImage : getPerspective().getVisualization().getMachineImages()) {
                                            machineImage.setTransparency(0.05f);
                                            machineImage.hidePortImages();
                                            machineImage.hidePathImages();
                                        }

                                        portImage.setVisibility(true);
                                        portImage.showPaths();
                                        targetPortImage.setVisibility(true);
                                        targetPortImage.showPaths();
                                        pathImage.setVisibility(true);

                                        ArrayList<Path> paths = getPerspective().getVisualization().getSimulation().getPathsByPort(targetPort);
                                        for (Path connectedPath : paths) {
                                            // Show ports
                                            ((PortImage) getPerspective().getVisualization().getImage(connectedPath.getSource())).setVisibility(true);
                                            ((PortImage) getPerspective().getVisualization().getImage(connectedPath.getSource())).showPaths();
                                            ((PortImage) getPerspective().getVisualization().getImage(connectedPath.getTarget())).setVisibility(true);
                                            ((PortImage) getPerspective().getVisualization().getImage(connectedPath.getTarget())).showPaths();
                                            // Show path
                                            getPerspective().getVisualization().getImage(connectedPath).setVisibility(true);
                                        }

                                        // ApplicationView.getDisplay().speakPhrase("setting as input. you can send the data to another board if you want. touchPositions another board.");

                                        // Perspective
                                        ArrayList<Port> pathPorts = getPerspective().getVisualization().getSimulation().getPortsInPaths(paths);
                                        ArrayList<Image> pathPortImages = getPerspective().getVisualization().getImages(pathPorts);
                                        ArrayList<PointF> pathPortPositions = Visualization.getPositions(pathPortImages);
                                        getPerspective().setPosition(Geometry.calculateCenterPosition(pathPortPositions));

                                        Rectangle boundingBox = Geometry.calculateBoundingBox(pathPortPositions);

//                                        Log.v("Images", "pathPortImages.size = " + pathPortImages.size());
//                                        Log.v("Images", "pathPortPositions.size = " + pathPortPositions.size());
//                                        for (PointF pathPortPosition: pathPortPositions) {
//                                            Log.v("Images", "x: " + pathPortPosition.x + ", y: " + pathPortPosition.y);
//                                        }
//                                        Log.v("Images", "boundingBox.length = " + boundingBox.length);
//                                        for (float boundingPosition: boundingBox) {
//                                            Log.v("Images", "bounds: " + boundingPosition);
//                                        }

                                        adjustPerspectiveScale(boundingBox);

                                    } else {

                                        Path path = new Path(sourcePort, targetPort);
                                        sourcePort.addPath(path);

                                        PathImage pathImage = new PathImage(path);
                                        pathImage.setVisualization(getPerspective().getVisualization());
                                        getPerspective().getVisualization().addImage(path, pathImage, "paths");

                                        PortImage targetPortImage = (PortImage) getPerspective().getVisualization().getImage(path.getTarget());
                                        targetPortImage.setUniqueColor(portImage.getUniqueColor());
//                                        portImage.pathImages.add(pathImage);

                                        // Remove focus from other machines and their ports.
                                        for (MachineImage machineImage : getPerspective().getVisualization().getMachineImages()) {
                                            machineImage.setTransparency(0.05f);
                                            machineImage.hidePortImages();
                                            machineImage.hidePathImages();
                                        }

                                        portImage.setVisibility(true);
                                        portImage.showPaths();
                                        targetPortImage.setVisibility(true);
                                        targetPortImage.showPaths();
                                        pathImage.setVisibility(true);

                                        ArrayList<Path> paths = getPerspective().getVisualization().getSimulation().getPathsByPort(targetPort);
                                        for (Path connectedPath : paths) {
                                            // Show ports
                                            (getPerspective().getVisualization().getImage(connectedPath.getSource())).setVisibility(true);
                                            ((PortImage) getPerspective().getVisualization().getImage(connectedPath.getSource())).showPaths();
                                            (getPerspective().getVisualization().getImage(connectedPath.getTarget())).setVisibility(true);
                                            ((PortImage) getPerspective().getVisualization().getImage(connectedPath.getTarget())).showPaths();
                                            // Show path
                                            getPerspective().getVisualization().getImage(connectedPath).setVisibility(true);
                                        }

                                        // ApplicationView.getDisplay().speakPhrase("setting as input. you can send the data to another board if you want. touchPositions another board.");

                                        // Perspective
                                        ArrayList<Port> pathPorts = getPerspective().getVisualization().getSimulation().getPortsInPaths(paths);
                                        ArrayList<Image> pathPortImages = getPerspective().getVisualization().getImages(pathPorts);
                                        ArrayList<PointF> pathPortPositions = Visualization.getPositions(pathPortImages);
                                        getPerspective().setPosition(Geometry.calculateCenterPosition(pathPortPositions));

                                        Rectangle boundingBox = Geometry.calculateBoundingBox(pathPortPositions);

//                                        Log.v("Images", "pathPortImages.size = " + pathPortImages.size());
//                                        Log.v("Images", "pathPortPositions.size = " + pathPortPositions.size());
//                                        for (PointF pathPortPosition: pathPortPositions) {
//                                            Log.v("Images", "x: " + pathPortPosition.x + ", y: " + pathPortPosition.y);
//                                        }
//                                        Log.v("Images", "boundingBox.length = " + boundingBox.length);
//                                        for (float boundingPosition: boundingBox) {
//                                            Log.v("Images", "bounds: " + boundingPosition);
//                                        }

                                        adjustPerspectiveScale(boundingBox);

                                    }
                                }

                                // TODO: Vibrate

                                touchInteraction.setOverlappedImage(null);

                                break;
                            }
                        }
                    }
                }
            }

            Log.v("TouchPort", "Touched port");

            if (!useNearbyPortImage) {

                Port port = (Port) portImage.getModel();

                port.setDirection(Port.Direction.INPUT);

                if (port.getType() == Port.Type.NONE) {
                    port.setType(Port.Type.getNextType(port.getType()));
                }
            }

        } else if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof PathImage) {

            PathImage pathImage = (PathImage) touchInteractivity.touchedImage[touchInteraction.pointerId];

            if (pathImage.getEditorVisibility()) {
                pathImage.setEditorVisibility(false);
            } else {
                pathImage.setEditorVisibility(true);
            }

        } else if (touchInteractivity.touchedImage[touchInteraction.pointerId] == null) {

            // No touchPositions on board or port. Touch is on map. So hide ports.
            for (MachineImage machineImage: getPerspective().getVisualization().getMachineImages()) {
                machineImage.hidePortImages();
                machineImage.hidePathImages();
                machineImage.setTransparency(1.0f);
            }

            // Adjust panning
            // Auto-adjust the perspective
            PointF centroidPosition = getPerspective().getVisualization().getCentroidPosition();
            getPerspective().setPosition(new PointF(centroidPosition.x, centroidPosition.y));

            adjustPerspectiveScale();

        }

        // Reset map interactivity
        getPerspective().enablePanning();
    }

    public void onHoldListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
        Log.v("MapViewEvent", "onHoldListener");

//        if (touchInteractivity.dragDistance[touchInteraction.pointerId] < TouchInteraction.MINIMUM_DRAG_DISTANCE) {
            // Holding but not (yet) dragging.

        touchInteractivity.isHolding[touchInteraction.pointerId] = true;

        // Show ports for sourceMachine board
        if (touchInteractivity.touchedImage[touchInteraction.pointerId] != null) {
            if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof MachineImage) {

//                MachineImage machineImage = (MachineImage) touchInteractivity.touchedImage[touchInteraction.pointerId];
////                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touchPositions[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.HOLD);
//                machineImage.touch(touchInteraction);
//
//                //machineSprite.showPortImages();
//                //machineSprite.showPathImages();
//                //touchSourceSprite = machineSprite;
//                getPerspective().setScale(0.8f);

            } else if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof PortImage) {

                Log.v("Holding", "Holding port");

//                PortImage portImage = (PortImage) touchInteractivity.touchedImage[touchInteraction.pointerId];
////                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touchPositions[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.HOLD);
//                portImage.touch(touchInteraction);
//
////                    portSprite.showPortImages();
////                    portSprite.showPathImages();
//                getPerspective().setScale(0.8f);

            }
        }
//        }

        // TODO: add onHoldForDuration(duration) callback, to set multiple hold callbacks (general interface).
        // TODO: ^ do same for consecutive taps in the same area.
    }

    private void onPreDragListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        // TODO: Encapsulate TouchInteraction in TouchEvent
//        TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touchPositions[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.PRE_DRAG);
//        touchInteractivity.addInteraction(touchInteraction);

    }

    private void onDragListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        //Log.v("MapViewEvent", "onDragListener");

//        // TODO: Encapsulate TouchInteraction in TouchEvent
//        TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touchPositions[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.DRAG);
//        touchInteractivity.addInteraction(touchInteraction);

        // Process
        // TODO: Put into callback

        touchInteractivity.isDragging[touchInteraction.pointerId] = true;

        // Dragging and holding.
//        if (touchInteractivity.getDuration() < TouchInteraction.MINIMUM_HOLD_DURATION) {
        if (touchInteractivity.isHolding[touchInteraction.pointerId]) {

            Log.v("Holding", "Dragging (after holding)");

            // Holding and dragging

            // TODO: Put into callback
            if (touchInteractivity.touchedImage[touchInteraction.pointerId] != null) {

                if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof MachineImage) {

                    MachineImage machineImage = (MachineImage) touchInteractivity.touchedImage[touchInteraction.pointerId];
//                    TouchInteraction touchInteraction = new TouchInteraction(TouchInteraction.TouchInteractionType.DRAG);
                    machineImage.touch(touchInteraction);
                    machineImage.showHighlights = true;
                    machineImage.setPosition(new PointF(touchInteraction.touchPositions[touchInteraction.pointerId].x, touchInteraction.touchPositions[touchInteraction.pointerId].y));

                    // Zoom out to show overview
//                    getPerspective().setScale(0.8f);

                } else if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof PortImage) {

                    PortImage portImage = (PortImage) touchInteractivity.touchedImage[touchInteraction.pointerId];
                    portImage.isTouched = true;
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touchPositions[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.DRAG);
//                    portSprite.touchPositions(touchInteraction);

                    portImage.setPosition(touchInteraction.touchPositions[touchInteraction.pointerId]);
                }
            } else {
                if (getPerspective().isPanningEnabled()) {
                    getPerspective().setScale(0.9f);
                    getPerspective().setOffset((int) (touchInteraction.touchPositions[touchInteraction.pointerId].x - touchInteractivity.getFirstInteraction().touchPositions[touchInteraction.pointerId].x), (int) (touchInteraction.touchPositions[touchInteraction.pointerId].y - touchInteractivity.getFirstInteraction().touchPositions[touchInteraction.pointerId].y));
                }
            }

        } else {

            // Dragging only (not holding)

            // TODO: Put into callback
            //if (touchInteractivity.isTouchingImage[touchInteraction.pointerId]) {
            if (touchInteractivity.touchedImage[touchInteraction.pointerId] != null) {

                if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof MachineImage) {

                    MachineImage machineImage = (MachineImage) touchInteractivity.touchedImage[touchInteraction.pointerId];
//                    TouchInteraction touchInteraction = new TouchInteraction(TouchInteraction.TouchInteractionType.DRAG);
                    machineImage.touch(touchInteraction);
                    machineImage.showHighlights = true;
                    machineImage.setPosition(new PointF(touchInteraction.touchPositions[touchInteraction.pointerId].x, touchInteraction.touchPositions[touchInteraction.pointerId].y));

                    // Zoom out to show overview
                    getPerspective().setScale(0.8f);

                } else if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof PortImage) {

                    PortImage portImage = (PortImage) touchInteractivity.touchedImage[touchInteraction.pointerId];
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touchPositions[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.DRAG);
//                    portSprite.touchPositions(touchInteraction);

                    portImage.setCandidatePathDestinationPosition(touchInteraction.touchPositions[touchInteraction.pointerId]);
                    portImage.setCandidatePathVisibility(true);

                    // Initialize port type and flow direction
                    Port port = (Port) portImage.getModel();
                    if (port.getDirection() == Port.Direction.NONE) {
                        port.setDirection(Port.Direction.INPUT);
                    }
                    if (port.getType() == Port.Type.NONE) {
                        port.setType(Port.Type.getNextType(port.getType())); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length
                    }

                    // Show ports of nearby machines
                    MachineImage nearestMachineImage = null;
                    for (MachineImage nearbyMachineImage: getPerspective().getVisualization().getMachineImages()) {

                        // Update style of nearby machines
                        float distanceToMachineImage = (float) Geometry.calculateDistance(
                                touchInteraction.touchPositions[touchInteraction.pointerId],
                                nearbyMachineImage.getPosition()
                        );

                        if (distanceToMachineImage < nearbyMachineImage.boardHeight + 60) {
                            nearbyMachineImage.setTransparency(1.0f);
                            nearbyMachineImage.showPortImages();

                            for (PortImage nearbyPortImage: nearbyMachineImage.getPortImages()) {
                                if (nearbyPortImage != portImage) {
                                    // Scaffold interaction to connect path to with nearby ports
                                    float distanceToNearbyPortImage = (float) Geometry.calculateDistance(
                                            touchInteraction.touchPositions[touchInteraction.pointerId],
                                            nearbyPortImage.getPosition()
                                    );
                                    if (distanceToNearbyPortImage < nearbyPortImage.shapeRadius + 40) {
                                        /* portSprite.setPosition(nearbyPortSprite.getRelativePosition()); */
                                        if (nearbyPortImage != touchInteraction.getOverlappedImage()) {
                                            nearestMachineImage = nearbyMachineImage;
                                        }
                                        break;
                                    }
                                } else {
                                    // TODO: Vibrate twice for "NO"
                                }
                            }

                        } else if (distanceToMachineImage < nearbyMachineImage.boardHeight + 100) {
                            if (nearbyMachineImage != portImage.getMachineImage()) {
                                nearbyMachineImage.setTransparency(0.5f);
                            }
                        } else {
                            if (nearbyMachineImage != portImage.getMachineImage()) {
                                nearbyMachineImage.setTransparency(0.1f);
                                nearbyMachineImage.hidePortImages();
                            }
                        }
                    }

                    // Check if a machine sprite was nearby
                    if (nearestMachineImage != null) {

                        touchInteraction.setOverlappedImage(nearestMachineImage);

                        // TODO: Vibrate

                        // Adjust perspective
                        //getPerspective().setPosition(nearestMachineImage.getPosition());
//                        getPerspective().setScale(0.9f); // Zoom out to show overview

                    } else {

                        // Show ports and paths
                        portImage.setVisibility(true);
                        portImage.showPaths();

                        // Adjust perspective
                        getPerspective().setPosition(getPerspective().getVisualization().getCentroidPosition());
                        getPerspective().setScale(0.6f); // Zoom out to show overview

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

            } else {

                if (getPerspective().isPanningEnabled()) {
                    getPerspective().setScale(0.9f);
                    getPerspective().setOffset((int) (touchInteraction.touchPositions[touchInteraction.pointerId].x - touchInteractivity.getFirstInteraction().touchPositions[touchInteraction.pointerId].x), (int) (touchInteraction.touchPositions[touchInteraction.pointerId].y - touchInteractivity.getFirstInteraction().touchPositions[touchInteraction.pointerId].y));
                }
            }

        }
    }

    private void onReleaseListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        // Stop touching sprite
        // Style. Reset the style of touched boards.
        if (touchInteraction.isTouching[touchInteraction.pointerId] || touchInteractivity.touchedImage[touchInteraction.pointerId] != null) {
            touchInteraction.isTouching[touchInteraction.pointerId] = false;
            if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof MachineImage) {
                MachineImage machineImage = (MachineImage) touchInteractivity.touchedImage[touchInteraction.pointerId];

                machineImage.showHighlights = false;
//                machineSprite.setScale(1.0f);
                touchInteractivity.touchedImage[touchInteraction.pointerId] = null;

            } else if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof PortImage) {

                if (touchInteractivity.isHolding[touchInteraction.pointerId]) {

                    /*
                    PortImage portImage = (PortImage) touchInteractivity.touchedImage[touchInteraction.pointerId];
//            TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touchPositions[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.RELEASE);
//                portImage.touch(touchInteraction);

                    // Show ports of nearby machines
                    for (MachineImage nearbyMachineImage : getPerspective().getVisualization().getMachineImages()) {

                        // Update style of nearby machines
                        float distanceToMachineImage = (float) Geometry.calculateDistance(
                                touchInteraction.touchPositions[touchInteraction.pointerId],
                                nearbyMachineImage.getPosition()
                        );

                        if (distanceToMachineImage < nearbyMachineImage.boardHeight + 50) {

                            // TODO: use overlappedImage instanceof PortImage

                            for (PortImage nearbyPortImage : nearbyMachineImage.getPortImages()) {

                                // Scaffold interaction to connect path to with nearby ports
                                float distanceToNearbyPortImage = (float) Geometry.calculateDistance(
                                        touchInteraction.touchPositions[touchInteraction.pointerId],
                                        nearbyPortImage.getPosition()
                                );

                                if (nearbyPortImage != portImage) {
                                    if (distanceToNearbyPortImage < nearbyPortImage.shapeRadius + 50) {

                                        // Create and add path to port
                                        Port sourcePort = (Port) portImage.getModel();
                                        Port destinationPort = (Port) nearbyPortImage.getModel();

                                        // Copy new port state
                                        destinationPort.setType(sourcePort.getType());
                                        destinationPort.setDirection(sourcePort.getDirection());

                                        // Update paths' source
                                        for (Path path: sourcePort.getPaths()) {
                                            path.setSource(destinationPort);
                                        }

                                        // Update ancestor paths' destination
                                        for (Path path: getPerspective().getVisualization().getSimulation().getPaths()) {
                                            if (path.getTarget() == sourcePort) {
                                                path.setTarget(destinationPort);
                                            }
                                        }

                                        // Reset old port state
                                        sourcePort.setType(Port.Type.NONE);
                                        sourcePort.setDirection(Port.Direction.NONE);

                                        break;
                                    }
                                }
                            }
                        }
                    }
                    */
                }
            }
        }

        if (touchInteractivity.touchedImage[touchInteraction.pointerId] != null) {
            touchInteractivity.touchedImage[touchInteraction.pointerId].isTouched = false;
        }

        // Stop dragging
        touchInteractivity.isDragging[touchInteraction.pointerId] = false;
        touchInteractivity.isHolding[touchInteraction.pointerId] = false;

        /*
        // Adjust panning
//                    getPerspective().setScale(1.0f);
        ArrayList<PointF> machinePositions = Visualization.getPositions(getPerspective().getVisualization().getMachineImage());
        float[] spriteBoundingBox = Geometry.calculateBoundingBox(machinePositions);

        float minVisibleX = (-getPerspective().getPosition().x - ((getPerspective().getWidth() / 2.0f) * getPerspective().getScale()));
        float maxVisibleX = (-getPerspective().getPosition().x + ((getPerspective().getWidth() / 2.0f) * getPerspective().getScale()));
        float minVisibleY = (getPerspective().getPosition().y - ((getPerspective().getHeight() / 2.0f) * getPerspective().getScale()));
        float maxVisibleY = (getPerspective().getPosition().y + ((getPerspective().getHeight() / 2.0f) * getPerspective().getScale()));

        Log.v("Bounds", "minVisibleX: " + minVisibleX);
        Log.v("Bounds", "maxVisibleX: " + maxVisibleX);
        Log.v("Bounds", "spriteBoundingBox[0]: " + spriteBoundingBox[0]);
        Log.v("Bounds", "spriteBoundingBox[2]: " + spriteBoundingBox[2]);

        PointF centroidPosition = getPerspective().getVisualization().getCentroidPosition();

        if (minVisibleX < (spriteBoundingBox[0] - (getPerspective().getWidth() / 2.0f))) {
            this.getPerspective().setPosition(new PointF(spriteBoundingBox[0], getPerspective().getPosition().y));
        }

        if (maxVisibleX > (spriteBoundingBox[2] + (getPerspective().getWidth() / 2.0f))) {
            this.getPerspective().setPosition(new PointF(spriteBoundingBox[2], getPerspective().getPosition().y));
        }

        if (minVisibleY < (spriteBoundingBox[1] - (getPerspective().getHeight() / 2.0f))) {
            this.getPerspective().setPosition(new PointF(-getPerspective().getPosition().x, spriteBoundingBox[1]));
        }

        if (maxVisibleY > (spriteBoundingBox[3] + (getPerspective().getHeight() / 2.0f))) {
            this.getPerspective().setPosition(new PointF(-getPerspective().getPosition().x, spriteBoundingBox[3]));
        }
        */

    }
}
