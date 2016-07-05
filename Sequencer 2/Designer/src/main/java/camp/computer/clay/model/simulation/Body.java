package camp.computer.clay.model.simulation;

import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;

import camp.computer.clay.model.interaction.Perspective;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.model.interaction.TouchInteractivity;
import camp.computer.clay.visualization.MachineImage;
import camp.computer.clay.visualization.PathImage;
import camp.computer.clay.visualization.PortImage;
import camp.computer.clay.visualization.Visualization;
import camp.computer.clay.visualization.util.Geometry;

public class Body extends Actor {

    private Perspective perspective;

    public Body() {
    }

    public void setPerspective(Perspective perspective) {
        this.perspective = perspective;
    }

    public Perspective getPerspective() {
        return this.perspective;
    }

    private void adjustPerspectiveScale() {

        // Adjust scale
        ArrayList<PointF> machinePositions = Visualization.getPositions(getPerspective().getVisualization().getMachineImages());
        float[] spriteBoundingBox = Geometry.calculateBoundingBox(machinePositions);
        PointF spriteBoundingBoxCenter = new PointF(((spriteBoundingBox[2] - spriteBoundingBox[0]) / 2.0f), ((spriteBoundingBox[3] - spriteBoundingBox[1]) / 2.0f));
        float spriteBoundingBoxWidth = spriteBoundingBox[2] - spriteBoundingBox[0];
        float spriteBoundingBoxHeight = spriteBoundingBox[3] - spriteBoundingBox[1];

        float widthDifference = spriteBoundingBoxWidth - getPerspective().getWidth();
        float heightDifference = spriteBoundingBoxHeight - getPerspective().getHeight();

        float widthPadding = 0;
        float newWidthScale = getPerspective().getWidth() / (spriteBoundingBoxWidth + widthPadding);
        float newHeightScale = getPerspective().getHeight() / (spriteBoundingBoxHeight + widthPadding);

        if (widthDifference > 0 /* || heightDifference > 0 */) {
//            if (newWidthScale > newHeightScale) {
            getPerspective().setScale(newWidthScale);
//            } else {
//                getPerspective().setScale(newHeightScale);
//            }
        } else {
            getPerspective().setScale(1.0f);
        }
    }

    public void onStartInteractivity(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
        Log.v("MapViewEvent", "onStartInteractivity");

        // Current
        touchInteraction.isTouching[touchInteraction.pointerId] = true;

        // Initialize touched sprite to none
        touchInteractivity.touchedImage[touchInteraction.pointerId] = null;

        // First
        if (touchInteraction == touchInteractivity.getFirstInteraction()) {

            // Reset dragging state
            touchInteractivity.isDragging[touchInteraction.pointerId] = false;
            touchInteractivity.dragDistance[touchInteraction.pointerId] = 0;

            boolean foundTouchTarget = false;

            // TODO: Move touch checking into Visualization.getTouchedSprite(Body);

            if (!foundTouchTarget
                    && (getPerspective().getFocus() instanceof MachineImage || getPerspective().getFocus() instanceof PortImage || getPerspective().getFocus() instanceof PathImage)) {
                for (MachineImage machineImage : getPerspective().getVisualization().getMachineImages()) {

                    if (touchInteractivity.touchedImage[touchInteraction.pointerId] == null) {
                        for (PortImage portImage : machineImage.portImages) {

                            // If perspective is on path, then constraint interactions to ports in the path
                            if (getPerspective().getFocus() instanceof PathImage) {
                                PathImage focusedPathImage = (PathImage) getPerspective().getFocus();
                                if (!focusedPathImage.getPath().contains((Port) portImage.getModel())) {
                                    // Log.v("InteractionHistory", "Skipping port not in path.");
                                    continue;
                                }
                            }

                            if (portImage.isTouching(touchInteraction.touch[touchInteraction.pointerId])) {
                                Log.v("PortTouch", "start touch on port " + portImage);

//                                    // <TOUCH_ACTION>
//                                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TOUCH);
//                                    portSprite.touch(touchInteraction);
//                                    // </TOUCH_ACTION>

                                touchInteractivity.isTouchingImage[touchInteraction.pointerId] = true;
                                touchInteractivity.touchedImage[touchInteraction.pointerId] = portImage;

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

                                foundTouchTarget = true;

                                break;
                            }
                        }
                    }
                }
            }

            if (!foundTouchTarget
                    && (getPerspective().getFocus() instanceof PortImage || getPerspective().getFocus() instanceof PathImage)) {
                for (MachineImage machineImage : getPerspective().getVisualization().getMachineImages()) {
                    if (touchInteractivity.touchedImage[touchInteraction.pointerId] == null) {
                        for (PortImage portImage : machineImage.portImages) {
                            for (PathImage pathImage : portImage.pathImages) {

                                PortImage sourcePortImage = (PortImage) getPerspective().getVisualization().getLayer(0).getImage(pathImage.getPath().getSource());
                                PortImage destinationPortImage = (PortImage) getPerspective().getVisualization().getLayer(0).getImage(pathImage.getPath().getDestination());

                                float distanceToLine = (float) Geometry.calculateLineToPointDistance(
                                        // TODO: getPerspective().getVisualization().getLayer(0).getImage(<Port/Model>)
                                        sourcePortImage.getPosition(),
                                        destinationPortImage.getPosition(),
                                        touchInteraction.touch[touchInteraction.pointerId],
                                        true
                                );

                                //Log.v("DistanceToLine", "distanceToLine: " + distanceToLine);

                                if (distanceToLine < 60) {

                                    Log.v("PathTouch", "start touch on path " + pathImage);

//                                        // <TOUCH_ACTION>
//                                        TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TOUCH);
//                                        pathSprite.touch(touchInteraction);
//                                        // </TOUCH_ACTION>

                                    touchInteractivity.isTouchingImage[touchInteraction.pointerId] = true;
                                    touchInteractivity.touchedImage[touchInteraction.pointerId] = pathImage;

                                    // <PERSPECTIVE>
                                    getPerspective().setFocus(pathImage);
                                    getPerspective().disablePanning();
                                    // </PERSPECTIVE>

                                    foundTouchTarget = true;

                                    break;
                                }
                            }
                        }
                    }
                }

                // TODO: Check for touch on path flow editor (i.e., spreadsheet or JS editors)
            }

            // Reset object interaction state
            if (getPerspective().getFocus() == null || getPerspective().getFocus() instanceof MachineImage || getPerspective().getFocus() instanceof PortImage) {
                for (MachineImage machineImage : getPerspective().getVisualization().getMachineImages()) {
                    // Log.v ("MapViewTouch", "Object at " + machineSprite.x + ", " + machineSprite.y);
                    // Check if one of the objects is touched
                    if (touchInteractivity.touchedImage[touchInteraction.pointerId] == null) {
                        if (machineImage.isTouching(touchInteraction.touch[touchInteraction.pointerId])) {

//                                // <TOUCH_ACTION>
//                                TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TOUCH);
//                                machineSprite.touch(touchInteraction);
//                                // </TOUCH_ACTION>

                            // TODO: Add this to an onTouch callback for the sprite's channel nodes
                            // TODO: i.e., callback Image.onTouch (via Image.touch())

                            touchInteractivity.isTouchingImage[touchInteraction.pointerId] = true;
                            touchInteractivity.touchedImage[touchInteraction.pointerId] = machineImage;

                            // <PERSPECTIVE>
                            getPerspective().setFocus(machineImage);
                            getPerspective().disablePanning();
                            // </PERSPECTIVE>

                            foundTouchTarget = true;

                            // Break to limit the number of objects that can be touch by a finger to one (1:1 finger:touch relationship).
                            break;

                        }
                    }
                }
            }

            if (!foundTouchTarget
                    && (getPerspective().getFocus() == null || getPerspective().getFocus() instanceof MachineImage || getPerspective().getFocus() instanceof PortImage || getPerspective().getFocus() instanceof PathImage)) {
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

    //private void onContinueInteractivity(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
    public void onContinueInteractivity(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
        Log.v("MapViewEvent", "onContinueInteractivity");

        // Current
        touchInteraction.isTouching[touchInteraction.pointerId] = true;

        // Calculate drag distance
        touchInteractivity.dragDistance[touchInteraction.pointerId] = Geometry.calculateDistance(touchInteraction.touch[touchInteraction.pointerId], touchInteractivity.getFirstInteraction().touch[touchInteraction.pointerId]);

        // Classify/Callback
        if (touchInteractivity.dragDistance[touchInteraction.pointerId] < TouchInteraction.MINIMUM_DRAG_DISTANCE) {
            // Pre-dragging
            onPreDragListener(touchInteractivity, touchInteraction);
        } else {
            // Dragging
            onDragListener(touchInteractivity, touchInteraction);
        }
    }

    //private void onCompleteInteractivity(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
    public void onCompleteInteractivity(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        touchInteractivity.timerHandler.removeCallbacks(touchInteractivity.timerRunnable);

        int pointerId = touchInteraction.pointerId;

        Log.v("MapViewEvent", "onCompleteInteractivity");

        // TODO: Encapsulate TouchInteraction in TouchEvent
//        TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.RELEASE);
//        touchInteractivity.addInteraction(touchInteraction);
        // TODO: resolveInteraction
        // TODO: cacheInteraction/recordInteraction(InDatabase)
        //touchInteractionSegment.clear();

//        // Previous
        TouchInteraction previousInteraction = touchInteractivity.getPreviousInteraction(touchInteraction);

        // Current
        touchInteraction.isTouching[touchInteraction.pointerId] = false;

        // Classify/Callbacks
        if (touchInteractivity.getDuration() < TouchInteraction.MAXIMUM_TAP_DURATION) {

            onTapListener(touchInteractivity, touchInteraction);

        } else {

            onPressListener(touchInteractivity, touchInteraction);

        }

        // Stop touching sprite
        // Style. Reset the style of touched boards.
        if (touchInteraction.isTouching[touchInteraction.pointerId] || touchInteractivity.touchedImage[touchInteraction.pointerId] != null) {
            touchInteraction.isTouching[touchInteraction.pointerId] = false;
            if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof MachineImage) {
                MachineImage machineImage = (MachineImage) touchInteractivity.touchedImage[touchInteraction.pointerId];

                machineImage.showHighlights = false;
//                machineSprite.setScale(1.0f);
                touchInteractivity.touchedImage[touchInteraction.pointerId] = null;
            }
        }

        // Stop dragging
        touchInteractivity.isDragging[touchInteraction.pointerId] = false;

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

    private void onTapListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof MachineImage) {
            MachineImage machineImage = (MachineImage) touchInteractivity.touchedImage[touchInteraction.pointerId];


            // TODO: Add this to an onTouch callback for the sprite's channel nodes
            // Check if the touched board's I/O node is touched
            // Check if one of the objects is touched
            if (machineImage.isTouching(touchInteraction.touch[touchInteraction.pointerId])) {
                Log.v("MapView", "\tTouched machine.");

                // ApplicationView.getApplicationView().speakPhrase(machine.getNameTag());

                // <TOUCH_ACTION>
//                TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TAP);
                // TODO: propagate RELEASE before TAP
                machineImage.touch(touchInteraction);
                // </TOUCH_ACTION>

                // Remove focus from other machines.
                for (MachineImage otherMachineImage: getPerspective().getVisualization().getMachineImages()) {
                    otherMachineImage.hidePorts();
                    otherMachineImage.hidePaths();
                    otherMachineImage.setTransparency(0.1f);
                }

                // Focus on machine.
                machineImage.showPorts();
                machineImage.showPaths();
                machineImage.setTransparency(1.0f);
                // ApplicationView.getApplicationView().speakPhrase("choose a channel to get data.");

                for (PortImage portImage: machineImage.portImages) {
                    ArrayList<Path> paths = getPerspective().getVisualization().getSimulation().getPathsByPort(portImage.getPort());
                    for (Path connectedPath : paths) {
                        // Show ports
                        ((PortImage) getPerspective().getVisualization().getLayer(0).getImage(connectedPath.getSource())).setVisibility(true);
//                        ((PortImage) getPerspective().getVisualization().getLayer(0).getImage(connectedPath.getSource())).showPaths();
                        ((PortImage) getPerspective().getVisualization().getLayer(0).getImage(connectedPath.getDestination())).setVisibility(true);
//                        ((PortImage) getPerspective().getVisualization().getLayer(0).getImage(connectedPath.getDestination())).showPaths();
                        // Show path
                        getPerspective().getVisualization().getLayer(0).getImage(connectedPath).setVisibility(true);
                    }
                }

                // Scale map.
                getPerspective().setScale(1.2f);
                getPerspective().setPosition(machineImage.getPosition());

                getPerspective().disablePanning();
            }


        } else if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof PortImage) {
            PortImage portImage = (PortImage) touchInteractivity.touchedImage[touchInteraction.pointerId];

//          //

            Log.v("MapView", "\tPort " + (portImage.getIndex() + 1) + " touched.");

            if (portImage.isTouching(touchInteraction.touch[touchInteraction.pointerId])) {
//                TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TAP);
                portImage.touch(touchInteraction);

                Log.v("MapView", "\tSource port " + (portImage.getIndex() + 1) + " touched.");

                Port port = (Port) portImage.getModel();

                if (port.getType() == Port.Type.NONE) {

                    port.setDirection(Port.Direction.INPUT);
                    port.setType(Port.Type.getNextType(port.getType()));

                    // ApplicationView.getApplicationView().speakPhrase("setting as input. you can send the data to another board if you want. touch another board.");

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
                        machineImage.hidePorts();
                        machineImage.hidePaths();
                    }

                    // Focus on the port
                    portImage.getMachineImage().showPath(portImage.getIndex(), true);
                    portImage.setVisibility(true);
                    portImage.setPathVisibility(true);

                    ArrayList<Path> paths = getPerspective().getVisualization().getSimulation().getPathsByPort(portImage.getPort());
                    for (Path connectedPath: paths) {
                        // Show ports
                        ((PortImage) getPerspective().getVisualization().getLayer(0).getImage(connectedPath.getSource())).setVisibility(true);
                        ((PortImage) getPerspective().getVisualization().getLayer(0).getImage(connectedPath.getSource())).showPaths();
                        ((PortImage) getPerspective().getVisualization().getLayer(0).getImage(connectedPath.getDestination())).setVisibility(true);
                        ((PortImage) getPerspective().getVisualization().getLayer(0).getImage(connectedPath.getDestination())).showPaths();
                        // Show path
                        getPerspective().getVisualization().getLayer(0).getImage(connectedPath).setVisibility(true);
                    }

                    // ApplicationView.getApplicationView().speakPhrase("setting as input. you can send the data to another board if you want. touch another board.");

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

            if (pathImage.getEditorVisibility()) {
                pathImage.setEditorVisibility(false);
            } else {
                pathImage.setEditorVisibility(true);
            }

        } else if (touchInteractivity.touchedImage[touchInteraction.pointerId] == null) {

            // No touch on board or port. Touch is on map. So hide ports.
            for (MachineImage machineImage : getPerspective().getVisualization().getMachineImages()) {
                machineImage.hidePorts();
                machineImage.hidePaths();
                machineImage.setTransparency(1.0f);
            }

            adjustPerspectiveScale();

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
            if (Geometry.calculateDistance(touchInteractivity.getFirstInteraction().touch[touchInteraction.pointerId], machineImage.getPosition()) < 80) {
                Log.v("MapView", "\tSource board touched.");

//                    // <TOUCH_ACTION>
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TAP);
//                    // TODO: propagate RELEASE before TAP
//                    machineSprite.touch(touchInteraction);
//                    // </TOUCH_ACTION>

                // No touch on board or port. Touch is on map. So hide ports.
                for (MachineImage otherMachineImage : getPerspective().getVisualization().getMachineImages()) {
                    otherMachineImage.hidePorts();
                    otherMachineImage.hidePaths();
                    otherMachineImage.setTransparency(0.1f);
                }
                machineImage.showPorts();
                getPerspective().setScale(0.8f);
                machineImage.showPaths();
                machineImage.setTransparency(1.0f);
                // ApplicationView.getApplicationView().speakPhrase("choose a channel to get data.");

                getPerspective().disablePanning();
            }

            // Zoom out to show overview
            getPerspective().setScale(1.0f);

        } else if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof PortImage) {

            PortImage portImage = (PortImage) touchInteractivity.touchedImage[touchInteraction.pointerId];
//            TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.RELEASE);
            portImage.touch(touchInteraction);

            // Show ports of nearby machines
            boolean useNearbyPortImage = false;
            for (MachineImage nearbyMachineImage: getPerspective().getVisualization().getMachineImages()) {

                // Update style of nearby machines
                float distanceToMachineImage = (float) Geometry.calculateDistance(
                        touchInteraction.touch[touchInteraction.pointerId],
                        nearbyMachineImage.getPosition()
                );

                if (distanceToMachineImage < nearbyMachineImage.boardHeight + 50) {

                    // TODO: use overlappedImage instanceof PortImage

                    for (PortImage nearbyPortImage: nearbyMachineImage.portImages) {

                        // Scaffold interaction to connect path to with nearby ports
                        float distanceToNearbyPortImage = (float) Geometry.calculateDistance(
                                touchInteraction.touch[touchInteraction.pointerId],
                                nearbyPortImage.getPosition()
                        );

                        if (nearbyPortImage != portImage) {
                            if (distanceToNearbyPortImage < nearbyPortImage.shapeRadius + PortImage.DISTANCE_BETWEEN_NODES) {

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
                                Port sourcePort = (Port) getPerspective().getVisualization().getLayer(0).getModel(portImage);
                                Port destinationPort = (Port) getPerspective().getVisualization().getLayer(0).getModel(nearbyPortImage);

                                if (!getPerspective().getVisualization().getSimulation().hasAncestor(sourcePort, destinationPort)) {

                                    if (sourcePort.getPaths().size() == 0) {

                                        Path path = new Path(sourcePort, destinationPort);
                                        sourcePort.addPath(path);

                                        PathImage pathImage = new PathImage(path);
                                        pathImage.setParentImage(portImage);
                                        pathImage.setVisualization(getPerspective().getVisualization());
                                        getPerspective().getVisualization().getLayer(0).addImage(path, pathImage);

                                        PortImage destinationPortImage = (PortImage) getPerspective().getVisualization().getLayer(0).getImage(path.getDestination());
                                        if (destinationPort.getPaths().size() == 0) {
                                            destinationPortImage.setUniqueColor(portImage.getUniqueColor());
                                        }
                                        portImage.pathImages.add(pathImage);

                                        portImage.setVisibility(true);
                                        portImage.showPaths();
                                        destinationPortImage.setVisibility(true);
                                        destinationPortImage.showPaths();
                                        pathImage.setVisibility(true);

                                        ArrayList<Path> paths = getPerspective().getVisualization().getSimulation().getPathsByPort(destinationPort);
                                        for (Path connectedPath : paths) {
                                            // Show ports
                                            ((PortImage) getPerspective().getVisualization().getLayer(0).getImage(connectedPath.getSource())).setVisibility(true);
                                            ((PortImage) getPerspective().getVisualization().getLayer(0).getImage(connectedPath.getSource())).showPaths();
                                            ((PortImage) getPerspective().getVisualization().getLayer(0).getImage(connectedPath.getDestination())).setVisibility(true);
                                            ((PortImage) getPerspective().getVisualization().getLayer(0).getImage(connectedPath.getDestination())).showPaths();
                                            // Show path
                                            getPerspective().getVisualization().getLayer(0).getImage(connectedPath).setVisibility(true);
                                        }

                                    } else {

//                                    Path path = sourcePort.getPath(0);
//                                    path.addPort(destinationPort);

                                        Path path = new Path(sourcePort, destinationPort);
                                        sourcePort.addPath(path);

                                        PathImage pathImage = new PathImage(path);
                                        pathImage.setParentImage(portImage);
                                        pathImage.setVisualization(getPerspective().getVisualization());
                                        getPerspective().getVisualization().getLayer(0).addImage(path, pathImage);

                                        PortImage destinationPortImage = (PortImage) getPerspective().getVisualization().getLayer(0).getImage(path.getDestination());
                                        destinationPortImage.setUniqueColor(portImage.getUniqueColor());
                                        portImage.pathImages.add(pathImage);

                                        portImage.setVisibility(true);
                                        portImage.showPaths();
                                        destinationPortImage.setVisibility(true);
                                        destinationPortImage.showPaths();
                                        pathImage.setVisibility(true);

                                        ArrayList<Path> paths = getPerspective().getVisualization().getSimulation().getPathsByPort(destinationPort);
                                        for (Path connectedPath : paths) {
                                            // Show ports
                                            ((PortImage) getPerspective().getVisualization().getLayer(0).getImage(connectedPath.getSource())).setVisibility(true);
                                            ((PortImage) getPerspective().getVisualization().getLayer(0).getImage(connectedPath.getSource())).showPaths();
                                            ((PortImage) getPerspective().getVisualization().getLayer(0).getImage(connectedPath.getDestination())).setVisibility(true);
                                            ((PortImage) getPerspective().getVisualization().getLayer(0).getImage(connectedPath.getDestination())).showPaths();
                                            // Show path
                                            getPerspective().getVisualization().getLayer(0).getImage(connectedPath).setVisibility(true);
                                        }

                                    }
                                }

                                /*
                                Vibrator v = (Vibrator) ApplicationView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                                // Vibrate for 500 milliseconds
                                v.vibrate(50);
                                //v.vibrate(50); off
                                //v.vibrate(50); // second tap
                                */

                                touchInteraction.overlappedImage = null;

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

            // No touch on board or port. Touch is on map. So hide ports.
            for (MachineImage machineImage: getPerspective().getVisualization().getMachineImages()) {
                machineImage.hidePorts();
                machineImage.hidePaths();
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

    private void onDoubleTapCallback (TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

    }

    public void onHoldListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
        Log.v("MapViewEvent", "onHoldListener");

        if (touchInteractivity.dragDistance[touchInteraction.pointerId] < TouchInteraction.MINIMUM_DRAG_DISTANCE) {
            // Holding but not (yet) dragging.

            // Show ports for sourceMachine board
            if (touchInteractivity.touchedImage[touchInteraction.pointerId] != null) {
                if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof MachineImage) {
                    MachineImage machineImage = (MachineImage) touchInteractivity.touchedImage[touchInteraction.pointerId];
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.HOLD);
                    machineImage.touch(touchInteraction);

                    //machineSprite.showPorts();
                    //machineSprite.showPaths();
                    //touchSourceSprite = machineSprite;
                    getPerspective().setScale(0.8f);
                } else if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof PortImage) {
                    PortImage portImage = (PortImage) touchInteractivity.touchedImage[touchInteraction.pointerId];
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.HOLD);
                    portImage.touch(touchInteraction);

//                    portSprite.showPorts();
//                    portSprite.showPaths();
                    getPerspective().setScale(0.8f);
                }
            }
        }
    }

    private void onPreDragListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        // TODO: Encapsulate TouchInteraction in TouchEvent
//        TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.PRE_DRAG);
//        touchInteractivity.addInteraction(touchInteraction);

    }

    private void onDragListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        //Log.v("MapViewEvent", "onDragListener");

//        // TODO: Encapsulate TouchInteraction in TouchEvent
//        TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.DRAG);
//        touchInteractivity.addInteraction(touchInteraction);

        // Process
        // TODO: Put into callback

        touchInteractivity.isDragging[touchInteraction.pointerId] = true;

        // Dragging and holding.
        if (touchInteractivity.getDuration() < TouchInteraction.MINIMUM_HOLD_DURATION) {

            // Dragging only (not holding)

            // TODO: Put into callback
            //if (touchInteractivity.isTouchingImage[touchInteraction.pointerId]) {
            if (touchInteractivity.touchedImage[touchInteraction.pointerId] != null) {

                if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof MachineImage) {

                    MachineImage machineImage = (MachineImage) touchInteractivity.touchedImage[touchInteraction.pointerId];
//                    TouchInteraction touchInteraction = new TouchInteraction(TouchInteraction.TouchInteractionType.DRAG);
                    machineImage.touch(touchInteraction);
                    machineImage.showHighlights = true;
                    machineImage.setPosition(new PointF(touchInteraction.touch[touchInteraction.pointerId].x, touchInteraction.touch[touchInteraction.pointerId].y));

                    // Zoom out to show overview
                    getPerspective().setScale(0.8f);

                } else if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof PortImage) {

                    PortImage portImage = (PortImage) touchInteractivity.touchedImage[touchInteraction.pointerId];
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.DRAG);
//                    portSprite.touch(touchInteraction);
                    portImage.setCandidatePathDestinationPosition(touchInteraction.touch[touchInteraction.pointerId]);
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
                                touchInteraction.touch[touchInteraction.pointerId],
                                nearbyMachineImage.getPosition()
                        );

                        if (distanceToMachineImage < nearbyMachineImage.boardHeight + 60) {
                            nearbyMachineImage.setTransparency(1.0f);
                            nearbyMachineImage.showPorts();

                            for (PortImage nearbyPortImage: nearbyMachineImage.portImages) {
                                if (nearbyPortImage != portImage) {
                                    // Scaffold interaction to connect path to with nearby ports
                                    float distanceToNearbyPortImage = (float) Geometry.calculateDistance(
                                            touchInteraction.touch[touchInteraction.pointerId],
                                            nearbyPortImage.getPosition()
                                    );
                                    if (distanceToNearbyPortImage < nearbyPortImage.shapeRadius + 40) {
                                        /* portSprite.setPosition(nearbyPortSprite.getRelativePosition()); */
                                        if (nearbyPortImage != touchInteraction.overlappedImage) {
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
                                nearbyMachineImage.hidePorts();
                            }
                        }
                    }

                    // Check if a machine sprite was nearby
                    if (nearestMachineImage != null) {

                        touchInteraction.overlappedImage = nearestMachineImage;

                        /*
                        Vibrator v = (Vibrator) ApplicationView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        v.vibrate(50); // Vibrate once for "YES"
                        */

                    } else {

                        // Show ports and paths
                        portImage.setVisibility(true);
                        portImage.showPaths();

                        // Adjust perspective
                        getPerspective().setScale(0.6f); // Zoom out to show overview

                    }

                }
            } else {
                if (getPerspective().isPanningEnabled()) {
                    getPerspective().setScale(0.9f);
                    getPerspective().setOffset((int) (touchInteraction.touch[touchInteraction.pointerId].x - touchInteractivity.getFirstInteraction().touch[touchInteraction.pointerId].x), (int) (touchInteraction.touch[touchInteraction.pointerId].y - touchInteractivity.getFirstInteraction().touch[touchInteraction.pointerId].y));
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
                    machineImage.setPosition(new PointF(touchInteraction.touch[touchInteraction.pointerId].x, touchInteraction.touch[touchInteraction.pointerId].y));

                    // Zoom out to show overview
                    getPerspective().setScale(0.8f);

                } else if (touchInteractivity.touchedImage[touchInteraction.pointerId] instanceof PortImage) {

                    PortImage portImage = (PortImage) touchInteractivity.touchedImage[touchInteraction.pointerId];
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.DRAG);
//                    portSprite.touch(touchInteraction);
                    portImage.setCandidatePathDestinationPosition(touchInteraction.touch[touchInteraction.pointerId]);
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
                                touchInteraction.touch[touchInteraction.pointerId],
                                nearbyMachineImage.getPosition()
                        );

                        if (distanceToMachineImage < nearbyMachineImage.boardHeight + 60) {
                            nearbyMachineImage.setTransparency(1.0f);
                            nearbyMachineImage.showPorts();

                            for (PortImage nearbyPortImage: nearbyMachineImage.portImages) {
                                if (nearbyPortImage != portImage) {
                                    // Scaffold interaction to connect path to with nearby ports
                                    float distanceToNearbyPortImage = (float) Geometry.calculateDistance(
                                            touchInteraction.touch[touchInteraction.pointerId],
                                            nearbyPortImage.getPosition()
                                    );
                                    if (distanceToNearbyPortImage < nearbyPortImage.shapeRadius + 40) {
                                        /* portSprite.setPosition(nearbyPortSprite.getRelativePosition()); */
                                        if (nearbyPortImage != touchInteraction.overlappedImage) {
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
                                nearbyMachineImage.hidePorts();
                            }
                        }
                    }

                    // Check if a machine sprite was nearby
                    if (nearestMachineImage != null) {

                        touchInteraction.overlappedImage = nearestMachineImage;

                        /*
                        Vibrator v = (Vibrator) ApplicationView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                        // Vibrate for 500 milliseconds
                        v.vibrate(50); // Vibrate once for "YES"
                        */

                    } else {

                        // Show ports and paths
                        portImage.setVisibility(true);
                        portImage.showPaths();

                        // Adjust perspective
                        getPerspective().setScale(0.6f); // Zoom out to show overview

                    }

                }

            } else {

                if (getPerspective().isPanningEnabled()) {
                    getPerspective().setScale(0.9f);
                    getPerspective().setOffset((int) (touchInteraction.touch[touchInteraction.pointerId].x - touchInteractivity.getFirstInteraction().touch[touchInteraction.pointerId].x), (int) (touchInteraction.touch[touchInteraction.pointerId].y - touchInteractivity.getFirstInteraction().touch[touchInteraction.pointerId].y));
                }
            }

        }
    }
}
