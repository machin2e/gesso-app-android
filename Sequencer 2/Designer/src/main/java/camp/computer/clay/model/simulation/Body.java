package camp.computer.clay.model.simulation;

import android.content.Context;
import android.graphics.PointF;
import android.os.Vibrator;
import android.util.Log;

import java.util.ArrayList;

import camp.computer.clay.designer.ApplicationView;
import camp.computer.clay.model.interaction.Perspective;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.model.interaction.TouchInteractivity;
import camp.computer.clay.sprite.MachineSprite;
import camp.computer.clay.sprite.PathSprite;
import camp.computer.clay.sprite.PortSprite;
import camp.computer.clay.sprite.util.Geometry;

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

    public void onTouchListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
        Log.v("MapViewEvent", "onTouchListener");

        // Current
        touchInteraction.isTouching[touchInteraction.pointerId] = true;

        // Initialize touched sprite to none
        touchInteractivity.touchedSprite[touchInteraction.pointerId] = null;

        // First
        if (touchInteraction == touchInteractivity.getFirstInteraction()) {

            // Reset dragging state
            touchInteractivity.isDragging[touchInteraction.pointerId] = false;
            touchInteractivity.dragDistance[touchInteraction.pointerId] = 0;

            boolean foundTouchTarget = false;

            // TODO: Move touch checking into Visualization.getTouchedSprite(Body);

            if (!foundTouchTarget
                    && (getPerspective().getFocus() instanceof MachineSprite || getPerspective().getFocus() instanceof PortSprite || getPerspective().getFocus() instanceof PathSprite)) {
                for (MachineSprite machineSprite : getPerspective().getVisualization().getMachineSprites()) {

                    if (touchInteractivity.touchedSprite[touchInteraction.pointerId] == null) {
                        for (PortSprite portSprite : machineSprite.portSprites) {

                            // If perspective is on path, then constraint interactions to ports in the path
                            if (getPerspective().getFocus() instanceof PathSprite) {
                                PathSprite focusedPathSprite = (PathSprite) getPerspective().getFocus();
                                if (!focusedPathSprite.getPath().contains(portSprite)) {
                                    Log.v("InteractionHistory", "Skipping port not in path.");
                                    continue;
                                }
                            }

                            if (portSprite.isTouching(touchInteraction.touch[touchInteraction.pointerId])) {
                                Log.v("PortTouch", "start touch on port " + portSprite);

//                                    // <TOUCH_ACTION>
//                                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TOUCH);
//                                    portSprite.touch(touchInteraction);
//                                    // </TOUCH_ACTION>

                                touchInteractivity.isTouchingSprite[touchInteraction.pointerId] = true;
                                touchInteractivity.touchedSprite[touchInteraction.pointerId] = portSprite;

                                if (getPerspective().getFocus() instanceof PathSprite) {
                                    PathSprite focusedPathSprite = (PathSprite) getPerspective().getFocus();
                                    Path path = (Path) focusedPathSprite.getModel();
                                    if (path.getSourcePort() == portSprite) {
                                        // <PERSPECTIVE>
                                        getPerspective().setFocus(portSprite);
                                        getPerspective().disablePanning();
                                        // </PERSPECTIVE>
                                    }
                                } else {
                                    // <PERSPECTIVE>
                                    getPerspective().setFocus(portSprite);
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
                    && (getPerspective().getFocus() instanceof PortSprite || getPerspective().getFocus() instanceof PathSprite)) {
                for (MachineSprite machineSprite : getPerspective().getVisualization().getMachineSprites()) {
                    if (touchInteractivity.touchedSprite[touchInteraction.pointerId] == null) {
                        for (PortSprite portSprite : machineSprite.portSprites) {
                            for (PathSprite pathSprite : portSprite.pathSprites) {

                                float distanceToLine = (float) Geometry.calculateLineToPointDistance(
                                        pathSprite.getPath().getSourcePort().getPosition(),
                                        pathSprite.getPath().getDestinationPort().getPosition(),
                                        touchInteraction.touch[touchInteraction.pointerId],
                                        true
                                );

                                //Log.v("DistanceToLine", "distanceToLine: " + distanceToLine);

                                if (distanceToLine < 60) {

                                    Log.v("PathTouch", "start touch on path " + pathSprite);

//                                        // <TOUCH_ACTION>
//                                        TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TOUCH);
//                                        pathSprite.touch(touchInteraction);
//                                        // </TOUCH_ACTION>

                                    touchInteractivity.isTouchingSprite[touchInteraction.pointerId] = true;
                                    touchInteractivity.touchedSprite[touchInteraction.pointerId] = pathSprite;

                                    // <PERSPECTIVE>
                                    getPerspective().setFocus(pathSprite);
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
            if (getPerspective().getFocus() == null || getPerspective().getFocus() instanceof MachineSprite || getPerspective().getFocus() instanceof PortSprite) {
                for (MachineSprite machineSprite : getPerspective().getVisualization().getMachineSprites()) {
                    // Log.v ("MapViewTouch", "Object at " + machineSprite.x + ", " + machineSprite.y);
                    // Check if one of the objects is touched
                    if (touchInteractivity.touchedSprite[touchInteraction.pointerId] == null) {
                        if (machineSprite.isTouching(touchInteraction.touch[touchInteraction.pointerId])) {

//                                // <TOUCH_ACTION>
//                                TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TOUCH);
//                                machineSprite.touch(touchInteraction);
//                                // </TOUCH_ACTION>

                            // TODO: Add this to an onTouch callback for the sprite's channel nodes
                            // TODO: i.e., callback Sprite.onTouch (via Sprite.touch())

                            touchInteractivity.isTouchingSprite[touchInteraction.pointerId] = true;
                            touchInteractivity.touchedSprite[touchInteraction.pointerId] = machineSprite;

                            // <PERSPECTIVE>
                            getPerspective().setFocus(machineSprite);
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
                    && (getPerspective().getFocus() == null || getPerspective().getFocus() instanceof MachineSprite || getPerspective().getFocus() instanceof PortSprite || getPerspective().getFocus() instanceof PathSprite)) {
                // Touch the canvas
                if (touchInteractivity.touchedSprite[touchInteraction.pointerId] == null) {

                    // <INTERACTION>
                    touchInteractivity.isTouchingSprite[touchInteraction.pointerId] = false;
                    // </INTERACTION>

                    // <PERSPECTIVE>
                    this.getPerspective().setFocus(null);
                    // this.isPanningEnabled = false;
                    // </PERSPECTIVE>
                }
            }
        }
    }

    //private void onMoveListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
    public void onMoveListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
        Log.v("MapViewEvent", "onMoveListener");

        int pointerId = touchInteraction.pointerId;

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
            touchInteractivity.isDragging[touchInteraction.pointerId] = true;
            onDragListener(touchInteractivity, touchInteraction);
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

        // Dragging and holding.
        if (touchInteractivity.getFirstInteraction().touchTime[touchInteraction.pointerId] - touchInteraction.touchTime[touchInteraction.pointerId] < TouchInteraction.MINIMUM_HOLD_DURATION) {

            // Dragging only (not holding)

            // TODO: Put into callback
            //if (touchInteractivity.isTouchingSprite[touchInteraction.pointerId]) {
            if (touchInteractivity.touchedSprite[touchInteraction.pointerId] != null) {

                if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof MachineSprite) {

                    MachineSprite machineSprite = (MachineSprite) touchInteractivity.touchedSprite[touchInteraction.pointerId];
//                    TouchInteraction touchInteraction = new TouchInteraction(TouchInteraction.TouchInteractionType.DRAG);
                    machineSprite.touch(touchInteraction);
                    machineSprite.showHighlights = true;
                    machineSprite.setPosition(new PointF(touchInteraction.touch[touchInteraction.pointerId].x, touchInteraction.touch[touchInteraction.pointerId].y));

                } else if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof PortSprite) {

                    PortSprite portSprite = (PortSprite) touchInteractivity.touchedSprite[touchInteraction.pointerId];
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.DRAG);
//                    portSprite.touch(touchInteraction);
                    portSprite.setCandidatePathDestinationPosition(touchInteraction.touch[touchInteraction.pointerId]);
                    portSprite.setCandidatePathVisibility(true);

                    // Initialize port type and flow direction
                    Port port = (Port) portSprite.getModel();
                    if (port.portDirection == Port.PortDirection.NONE) {
                        port.portDirection = Port.PortDirection.INPUT;
                    }
                    if (port.portType == Port.PortType.NONE) {
                        port.portType = Port.PortType.getNextType(port.portType); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length
                    }

                    // Show ports of nearby machines
                    for (MachineSprite nearbyMachineSprite: getPerspective().getVisualization().getMachineSprites()) {

                        // Update style of nearby machines
                        float distanceToMachineSprite = (float) Geometry.calculateDistance(
                                touchInteraction.touch[touchInteraction.pointerId],
                                nearbyMachineSprite.getPosition()
                        );

                        if (distanceToMachineSprite < nearbyMachineSprite.boardHeight + 60) {
                            nearbyMachineSprite.setTransparency(1.0f);
                            nearbyMachineSprite.showPorts();

                            for (PortSprite nearbyPortSprite: nearbyMachineSprite.portSprites) {
                                if (nearbyPortSprite != portSprite) {
                                    // Scaffold interaction to connect path to with nearby ports
                                    float distanceToNearbyPortSprite = (float) Geometry.calculateDistance(
                                            touchInteraction.touch[touchInteraction.pointerId],
                                            nearbyPortSprite.getPosition()
                                    );
                                    if (distanceToNearbyPortSprite < nearbyPortSprite.shapeRadius + 40) {
                                        /* portSprite.setPosition(nearbyPortSprite.getRelativePosition()); */
                                        if (nearbyPortSprite != touchInteraction.overlappedSprite) {
                                            touchInteraction.overlappedSprite = nearbyPortSprite;
                                            Vibrator v = (Vibrator) ApplicationView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                                            // Vibrate for 500 milliseconds
                                            v.vibrate(50); // Vibrate once for "YES"
                                        }
                                        break;
                                    }
                                } else {
                                    // TODO: Vibrate twice for "NO"
                                }
                            }

                        } else if (distanceToMachineSprite < nearbyMachineSprite.boardHeight + 100) {
                            if (nearbyMachineSprite != portSprite.getMachineSprite()) {
                                nearbyMachineSprite.setTransparency(0.5f);
                            }
                        } else {
                            if (nearbyMachineSprite != portSprite.getMachineSprite()) {
                                nearbyMachineSprite.setTransparency(0.1f);
                                nearbyMachineSprite.hidePorts();
                            }
                        }
                    }

                    portSprite.setVisibility(true);
                    portSprite.showPaths();

                }
            } else {
                if (getPerspective().isPanningEnabled()) {
                    getPerspective().setScale(0.8f);
                    getPerspective().setOffset((int) (touchInteraction.touch[touchInteraction.pointerId].x - touchInteractivity.getFirstInteraction().touch[touchInteraction.pointerId].x), (int) (touchInteraction.touch[touchInteraction.pointerId].y - touchInteractivity.getFirstInteraction().touch[touchInteraction.pointerId].y));
                }
            }

        } else {

            // TODO: Put into callback
            //if (touchInteractivity.isTouchingSprite[touchInteraction.pointerId]) {
            if (touchInteractivity.touchedSprite[touchInteraction.pointerId] != null) {
                if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof MachineSprite) {
                    MachineSprite machineSprite = (MachineSprite) touchInteractivity.touchedSprite[touchInteraction.pointerId];
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.DRAG);
                    machineSprite.touch(touchInteraction);
                    machineSprite.showHighlights = true;
                    machineSprite.setPosition(touchInteraction.touch[touchInteraction.pointerId]);
                } else if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof PortSprite) {
                    PortSprite portSprite = (PortSprite) touchInteractivity.touchedSprite[touchInteraction.pointerId];
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.DRAG);
                    portSprite.touch(touchInteraction);

                    // Initialize port type and flow direction
                    Port port = (Port) portSprite.getModel();
                    if (port.portDirection == Port.PortDirection.NONE) {
                        port.portDirection = Port.PortDirection.INPUT;
                    }
                    if (port.portType == Port.PortType.NONE) {
                        port.portType = Port.PortType.getNextType(port.portType); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length
                    }

                    // Show ports of nearby machines
                    for (MachineSprite nearbyMachineSprite: getPerspective().getVisualization().getMachineSprites()) {

                        // Update style of nearby machines
                        float distanceToMachineSprite = (float) Geometry.calculateDistance(
                                touchInteraction.touch[touchInteraction.pointerId],
                                nearbyMachineSprite.getPosition()
                        );
                        Log.v("DistanceToSprite", "distanceToMachineSprite: " + distanceToMachineSprite);
                        if (distanceToMachineSprite < nearbyMachineSprite.boardHeight + 50) {
                            nearbyMachineSprite.setTransparency(1.0f);
                            nearbyMachineSprite.showPorts();

                            for (PortSprite nearbyPortSprite: nearbyMachineSprite.portSprites) {
                                if (nearbyPortSprite != portSprite) {
                                    // Scaffold interaction to connect path to with nearby ports
                                    float distanceToNearbyPortSprite = (float) Geometry.calculateDistance(
                                            touchInteraction.touch[touchInteraction.pointerId],
                                            nearbyPortSprite.getPosition()
                                    );
                                    if (distanceToNearbyPortSprite < nearbyPortSprite.shapeRadius + 20) {
                                        /* portSprite.setPosition(nearbyPortSprite.getRelativePosition()); */
                                        if (nearbyPortSprite != touchInteraction.overlappedSprite) {
                                            touchInteraction.overlappedSprite = nearbyPortSprite;
                                            Vibrator v = (Vibrator) ApplicationView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                                            // Vibrate for 500 milliseconds
                                            v.vibrate(50); // Vibrate once for "YES"
                                        }
                                        break;
                                    }
                                } else {
                                    // TODO: Vibrate twice for "NO"
                                }
                            }

                        } else if (distanceToMachineSprite < nearbyMachineSprite.boardHeight + 80) {
                            if (nearbyMachineSprite != portSprite.getMachineSprite()) {
                                nearbyMachineSprite.setTransparency(0.5f);
                            }
                        } else {
                            if (nearbyMachineSprite != portSprite.getMachineSprite()) {
                                nearbyMachineSprite.setTransparency(0.1f);
                                nearbyMachineSprite.hidePorts();
                            }
                        }
                    }

                }
            } else {
                if (getPerspective().isPanningEnabled()) {
                    getPerspective().setOffset((int) (touchInteraction.touch[touchInteraction.pointerId].x - touchInteractivity.getFirstInteraction().touch[touchInteraction.pointerId].x), (int) (touchInteraction.touch[touchInteraction.pointerId].y - touchInteractivity.getFirstInteraction().touch[touchInteraction.pointerId].y));
                }
            }
        }
    }

    //private void onReleaseListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
    public void onReleaseListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        touchInteractivity.timerHandler.removeCallbacks(touchInteractivity.timerRunnable);

        int pointerId = touchInteraction.pointerId;

        Log.v("MapViewEvent", "onReleaseListener");

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
        if (touchInteraction.touchTime[pointerId] - touchInteractivity.getFirstInteraction().touchTime[pointerId] < TouchInteraction.MAXIMUM_TAP_DURATION) {

            onTapListener(touchInteractivity, touchInteraction);

        } else {

            if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof MachineSprite) {
                MachineSprite machineSprite = (MachineSprite) touchInteractivity.touchedSprite[touchInteraction.pointerId];

                // TODO: Add this to an onTouch callback for the sprite's channel nodes
                // Check if the touched board's I/O node is touched
                // Check if one of the objects is touched
                if (Geometry.calculateDistance(touchInteractivity.getFirstInteraction().touch[touchInteraction.pointerId], machineSprite.getPosition()) < 80) {
                    Log.v("MapView", "\tSource board touched.");

//                    // <TOUCH_ACTION>
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TAP);
//                    // TODO: propagate RELEASE before TAP
//                    machineSprite.touch(touchInteraction);
//                    // </TOUCH_ACTION>

                    // No touch on board or port. Touch is on map. So hide ports.
                    for (MachineSprite otherMachineSprite : getPerspective().getVisualization().getMachineSprites()) {
                        otherMachineSprite.hidePorts();
                        otherMachineSprite.hidePaths();
                        otherMachineSprite.setTransparency(0.1f);
                    }
                    machineSprite.showPorts();
                    getPerspective().setScale(0.8f);
                    machineSprite.showPaths();
                    machineSprite.setTransparency(1.0f);
                    ApplicationView.getApplicationView().speakPhrase("choose a channel to get data.");

                    getPerspective().disablePanning();
                }

            } else if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof PortSprite) {
                PortSprite portSprite = (PortSprite) touchInteractivity.touchedSprite[touchInteraction.pointerId];
//                TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.RELEASE);
                portSprite.touch(touchInteraction);

                // Show ports of nearby machines
                boolean useNearbyPortSprite = false;
                    for (MachineSprite nearbyMachineSprite: getPerspective().getVisualization().getMachineSprites()) {

                        // Update style of nearby machines
                        float distanceToMachineSprite = (float) Geometry.calculateDistance(
                                touchInteraction.touch[touchInteraction.pointerId],
                                nearbyMachineSprite.getPosition()
                        );
                        if (distanceToMachineSprite < nearbyMachineSprite.boardHeight + 50) {



                        // TODO: use overlappedSprite instanceof PortSprite



                        for (PortSprite nearbyPortSprite: nearbyMachineSprite.portSprites) {
                            // Scaffold interaction to connect path to with nearby ports
                            float distanceToNearbyPortSprite = (float) Geometry.calculateDistance(
                                    touchInteraction.touch[touchInteraction.pointerId],
                                    nearbyPortSprite.getPosition()
                            );
                            if (nearbyPortSprite != portSprite) {
                                if (distanceToNearbyPortSprite < nearbyPortSprite.shapeRadius + 20) {
                                    /* portSprite.setPosition(touchInteraction.touch[touchInteraction.pointerId]); */

                                    useNearbyPortSprite = true;

                                    Port port = (Port) portSprite.getModel();
                                    Port nearbyPort = (Port) nearbyPortSprite.getModel();

                                    if (port.portDirection == Port.PortDirection.NONE) {
                                        port.portDirection = Port.PortDirection.INPUT;
                                    }
                                    if (port.portType == Port.PortType.NONE) {
                                        port.portType = Port.PortType.getNextType(port.portType); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length
                                    }

                                    nearbyPort.portDirection = Port.PortDirection.OUTPUT;
                                    nearbyPort.portType = Port.PortType.getNextType(nearbyPort.portType);

                                    // Create and add path to port
                                    PathSprite pathSprite = portSprite.addPath(
                                            portSprite.getMachineSprite(),
                                            portSprite,
                                            nearbyPortSprite.getMachineSprite(),
                                            nearbyPortSprite
                                    );


                                    pathSprite.getPath().getSourcePort().showPaths();
                                    pathSprite.setVisibility(true);
//                                        pathSprite.showDirectedPaths = true;
//                                        pathSprite.showPathDocks = false;




                                    Vibrator v = (Vibrator) ApplicationView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                                    // Vibrate for 500 milliseconds
                                    v.vibrate(50);
                                    //v.vibrate(50); off
                                    //v.vibrate(50); // second tap
                                    touchInteraction.overlappedSprite = null;
                                    break;
                                }

                            }
                        }
                    }
                }

                Log.v("TouchPort", "Touched port");

                if (!useNearbyPortSprite) {

                    Port port = (Port) portSprite.getModel();

                    port.portDirection = Port.PortDirection.INPUT;

                    if (port.portType == Port.PortType.NONE) {
                        port.portType = Port.PortType.getNextType(port.portType);
                    }
                }

            } else if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof PathSprite) {
                PathSprite pathSprite = (PathSprite) touchInteractivity.touchedSprite[touchInteraction.pointerId];

                if (pathSprite.getEditorVisibility()) {
                    pathSprite.setEditorVisibility(false);
                } else {
                    pathSprite.setEditorVisibility(true);
                }

            } else {
                if (touchInteractivity.touchedSprite[touchInteraction.pointerId] == null) {
                    // No touch on board or port. Touch is on map. So hide ports.
                    for (MachineSprite machineSprite: getPerspective().getVisualization().getMachineSprites()) {
                        machineSprite.hidePorts();
                        machineSprite.setScale(1.0f);
                        machineSprite.hidePaths();
                        machineSprite.setTransparency(1.0f);
                    }
                    getPerspective().setScale(1.0f);
                }
            }

            // Reset map interactivity
            getPerspective().enablePanning();

        }

        // Stop touching sprite
        // Style. Reset the style of touched boards.
        if (touchInteraction.isTouching[touchInteraction.pointerId] || touchInteractivity.touchedSprite[touchInteraction.pointerId] != null) {
            touchInteraction.isTouching[touchInteraction.pointerId] = false;
            if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof MachineSprite) {
                MachineSprite machineSprite = (MachineSprite) touchInteractivity.touchedSprite[touchInteraction.pointerId];

                machineSprite.showHighlights = false;
//                machineSprite.setScale(1.0f);
                touchInteractivity.touchedSprite[touchInteraction.pointerId] = null;
            }
        }

        // Stop dragging
        touchInteractivity.isDragging[touchInteraction.pointerId] = false;

        // Auto-adjust the perspective
        ArrayList<PointF> machineCenterPoints = new ArrayList<PointF>();
        for (MachineSprite machineSprite: getPerspective().getVisualization().getMachineSprites()) {
            machineCenterPoints.add(machineSprite.getPosition());
        }
        PointF centroidPoint = Geometry.calculateCentroid(machineCenterPoints);
        centroidPoint.x = -centroidPoint.x;
        centroidPoint.y = -centroidPoint.y;
//        centroidPoint.x = centroidPoint.x - getPerspective().getPosition().x;
//        centroidPoint.y = centroidPoint.y - getPerspective().getPosition().y;
        getPerspective().setPosition(centroidPoint);



        // Adjust scale
        float[] bounds = Geometry.calculateBoundingBox(machineCenterPoints);
        PointF boundCenter = new PointF(((bounds[2] - bounds[0]) / 2.0f), ((bounds[3] - bounds[1]) / 2.0f));
        float boundWidth = bounds[2] - bounds[1];
        float boundHeight = bounds[3] - bounds[0];

        //float boundToScaleRatio = ;
        float currentWidthSpan = getPerspective().getScale() * getPerspective().getWidth() /* screen width */;
        float boundWidthSpan = getPerspective().getScale() * boundWidth;


    }

    private void onTapListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof MachineSprite) {
            MachineSprite machineSprite = (MachineSprite) touchInteractivity.touchedSprite[touchInteraction.pointerId];


            // TODO: Add this to an onTouch callback for the sprite's channel nodes
            // Check if the touched board's I/O node is touched
            // Check if one of the objects is touched
            if (machineSprite.isTouching(touchInteraction.touch[touchInteraction.pointerId])) {
                Log.v("MapView", "\tTouched machine.");

                // Machine
                Machine machine = (Machine) machineSprite.getModel();

                ApplicationView.getApplicationView().speakPhrase(machine.getNameTag());

                // <TOUCH_ACTION>
//                TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TAP);
                // TODO: propagate RELEASE before TAP
                machineSprite.touch(touchInteraction);
                // </TOUCH_ACTION>

                // Remove focus from other machines.
                for (MachineSprite otherMachineSprite: getPerspective().getVisualization().getMachineSprites()) {
                    otherMachineSprite.hidePorts();
                    otherMachineSprite.hidePaths();
                    otherMachineSprite.setTransparency(0.1f);
                }

                // Focus on machine.
                machineSprite.showPorts();
                machineSprite.showPaths();
                machineSprite.setTransparency(1.0f);
                ApplicationView.getApplicationView().speakPhrase("choose a channel to get data.");

                // Scale map.
                getPerspective().setScale(0.8f);

                getPerspective().disablePanning();
            }


        } else if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof PortSprite) {
            PortSprite portSprite = (PortSprite) touchInteractivity.touchedSprite[touchInteraction.pointerId];

            Log.v("MapView", "\tPort " + (portSprite.getIndex() + 1) + " touched.");

            if (portSprite.isTouching(touchInteraction.touch[touchInteraction.pointerId])) {
//                TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TAP);
                portSprite.touch(touchInteraction);

                Log.v("MapView", "\tSource port " + (portSprite.getIndex() + 1) + " touched.");

                Port port = (Port) portSprite.getModel();

                if (port.getType() == Port.PortType.NONE) {

                    port.portDirection = Port.PortDirection.INPUT;

                    port.portType = Port.PortType.getNextType(port.getType());

                    ApplicationView.getApplicationView().speakPhrase("setting as input. you can send the data to another board if you want. touch another board.");

                } else {

                    // TODO: Replace with state of perspective. i.e., Check if seeing a single path.
                    if (portSprite.pathSprites.size() == 0) {

                        Port.PortType nextPortType = port.getType();
                        while ((nextPortType == Port.PortType.NONE)
                                || (nextPortType == port.getType())) {
                            nextPortType = Port.PortType.getNextType(nextPortType);
                        }
                        port.setPortType(nextPortType);

                    } else {

                        if (portSprite.hasVisiblePaths()) {

                            // TODO: Replace with state of perspective. i.e., Check if seeing a single path.
                            ArrayList<PathSprite> visiblePathSprites = portSprite.getVisiblePaths();
                            if (visiblePathSprites.size() == 1) {

                                Port.PortType nextPortType = port.portType;
                                while ((nextPortType == Port.PortType.NONE)
                                        || (nextPortType == port.getType())) {
                                    nextPortType = Port.PortType.getNextType(nextPortType);
                                }
                                port.setPortType(nextPortType);

                            }

                        } else {

                            // TODO: If second press, change the channel.

                            // Remove focus from other machines and their ports.
                            for (MachineSprite machineSprite : getPerspective().getVisualization().getMachineSprites()) {
                                machineSprite.hidePorts();
                                machineSprite.hidePaths();
                            }

                            // Focus on the port
                            portSprite.getMachineSprite().showPath(portSprite.getIndex(), true);
                            portSprite.setVisibility(true);
                            portSprite.setPathVisibility(true);

                            ApplicationView.getApplicationView().speakPhrase("setting as input. you can send the data to another board if you want. touch another board.");
                        }

                    }
                }
            }

        } else if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof PathSprite) {
            PathSprite pathSprite = (PathSprite) touchInteractivity.touchedSprite[touchInteraction.pointerId];

            if (pathSprite.getEditorVisibility()) {
                pathSprite.setEditorVisibility(false);
            } else {
                pathSprite.setEditorVisibility(true);
            }

        } else if (touchInteractivity.touchedSprite[touchInteraction.pointerId] == null) {

            // No touch on board or port. Touch is on map. So hide ports.
            for (MachineSprite machineSprite : getPerspective().getVisualization().getMachineSprites()) {
                machineSprite.hidePorts();
                machineSprite.setScale(1.0f);
                machineSprite.hidePaths();
                machineSprite.setTransparency(1.0f);
            }
            getPerspective().setScale(1.0f);

            // Reset map interactivity
            getPerspective().enablePanning();
        }

    }

    private void onDoubleTapCallback (TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

    }

    public void onHoldListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
        Log.v("MapViewEvent", "onHoldListener");

        if (touchInteractivity.dragDistance[touchInteraction.pointerId] < TouchInteraction.MINIMUM_DRAG_DISTANCE) {
            // Holding but not (yet) dragging.

            // Show ports for sourceMachine board
            if (touchInteractivity.touchedSprite[touchInteraction.pointerId] != null) {
                if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof MachineSprite) {
                    MachineSprite machineSprite = (MachineSprite) touchInteractivity.touchedSprite[touchInteraction.pointerId];
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.HOLD);
                    machineSprite.touch(touchInteraction);

                    //machineSprite.showPorts();
                    //machineSprite.showPaths();
                    //touchSourceSprite = machineSprite;
                    getPerspective().setScale(0.8f);
                } else if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof PortSprite) {
                    PortSprite portSprite = (PortSprite) touchInteractivity.touchedSprite[touchInteraction.pointerId];
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.HOLD);
                    portSprite.touch(touchInteraction);

//                    portSprite.showPorts();
//                    portSprite.showPaths();
                    getPerspective().setScale(0.8f);
                }
            }
        }
    }
}