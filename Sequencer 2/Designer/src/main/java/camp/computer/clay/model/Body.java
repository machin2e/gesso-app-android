package camp.computer.clay.model;

import android.util.Log;

import camp.computer.clay.sprite.MachineSprite;
import camp.computer.clay.sprite.PathSprite;
import camp.computer.clay.sprite.PortSprite;
import camp.computer.clay.sprite.Visualization;
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

    //private void onTouchListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
    public void onTouchListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
        Log.v("MapViewEvent", "onTouchListener");

//        // TODO: Encapsulate TouchInteraction in TouchEvent
//        TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TOUCH);
//        touchInteractivity.addInteraction(touchInteraction);

        int pointerId = touchInteraction.pointerId;

        // Previous
//        touchInteraction.isTouchingPrevious[touchInteraction.pointerId] = touchInteraction.isTouching[touchInteraction.pointerId]; // (or) touchInteraction.isTouchingPrevious[touchInteraction.pointerId] = false;
//        touchInteraction.touchPrevious[touchInteraction.pointerId].x = touchInteraction.touch[touchInteraction.pointerId].x;
//        touchInteraction.touchPrevious[touchInteraction.pointerId].y = touchInteraction.touch[touchInteraction.pointerId].y;
//        touchInteraction.touchPreviousTime[touchInteraction.pointerId] = java.lang.System.currentTimeMillis ();

        // Current
        touchInteraction.isTouching[touchInteraction.pointerId] = true;

        // Initialize touched sprite to none
        touchInteractivity.touchedSprite[touchInteraction.pointerId] = null;

        Perspective currentPerspective = this.getPerspective();

        // First
//        if (touchInteraction.isTouching[touchInteraction.pointerId] == true && touchInteraction.isTouchingPrevious[touchInteraction.pointerId] == false) {
        if (touchInteraction == touchInteractivity.getFirstInteraction()) {

            Log.v("Toucher", "1");

            // Set the first point of touch
//            touchInteraction.touchStart[touchInteraction.pointerId].x = touchInteraction.touch[touchInteraction.pointerId].x;
//            touchInteraction.touchStart[touchInteraction.pointerId].y = touchInteraction.touch[touchInteraction.pointerId].y;
//            touchInteraction.touchStartTime = java.lang.System.currentTimeMillis ();

            // Reset dragging state
            touchInteractivity.isDragging[touchInteraction.pointerId] = false;
            touchInteractivity.dragDistance[touchInteraction.pointerId] = 0;

            // Reset object interaction state
//            for (Visualization visualization : visualizationSprites) {
                for (MachineSprite machineSprite : getPerspective().visualization.getMachineSprites()) {
                    // Log.v ("MapViewTouch", "Object at " + machineSprite.x + ", " + machineSprite.y);
                    if (getPerspective().focusSprite == null || getPerspective().focusSprite instanceof MachineSprite || getPerspective().focusSprite instanceof PortSprite) {
                        // Check if one of the objects is touched
                        if (touchInteractivity.touchedSprite[touchInteraction.pointerId] == null) {
                            if (machineSprite.isTouching(touchInteraction.touch[touchInteraction.pointerId])) {

                                Log.v("Toucher", "Machine");

//                                // <TOUCH_ACTION>
//                                TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TOUCH);
//                                machineSprite.touch(touchInteraction);
//                                // </TOUCH_ACTION>

                                // TODO: Add this to an onTouch callback for the sprite's channel nodes
                                // TODO: i.e., callback Sprite.onTouch (via Sprite.touch())

                                touchInteractivity.isTouchingSprite[touchInteraction.pointerId] = true;
                                touchInteractivity.touchedSprite[touchInteraction.pointerId] = machineSprite;

                                // <PERSPECTIVE>
                                currentPerspective.focusSprite = machineSprite;
                                currentPerspective.disablePanning();
                                // </PERSPECTIVE>

                                // Break to limit the number of objects that can be touch by a finger to one (1:1 finger:touch relationship).
                                break;

                            }
                        }
                    }

                    if (getPerspective().focusSprite instanceof MachineSprite || getPerspective().focusSprite instanceof PortSprite || getPerspective().focusSprite instanceof PathSprite) {

                        if (touchInteractivity.touchedSprite[touchInteraction.pointerId] == null) {
                            for (PortSprite portSprite : machineSprite.portSprites) {

                                // If perspective is on path, then constraint interactions to ports in the path
                                if (getPerspective().focusSprite instanceof PathSprite) {
                                    PathSprite focusedPathSprite = (PathSprite) getPerspective().focusSprite;
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

                                    // <PERSPECTIVE>
                                    currentPerspective.focusSprite = portSprite;
                                    currentPerspective.disablePanning();
                                    // </PERSPECTIVE>

                                    break;
                                }
                            }
                        }
                    }

                    if (getPerspective().focusSprite instanceof PortSprite || getPerspective().focusSprite instanceof PathSprite) {
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
                                        currentPerspective.focusSprite = pathSprite;
                                        currentPerspective.disablePanning();
                                        // </PERSPECTIVE>

                                        break;
                                    }
                                }
                            }
                        }
                    }

                    // TODO: Check for touch on path flow editor (i.e., spreadsheet or JS editors)
                }
//            }

            if (getPerspective().focusSprite == null || getPerspective().focusSprite instanceof MachineSprite || getPerspective().focusSprite instanceof PortSprite || getPerspective().focusSprite instanceof PathSprite) {
                // Touch the canvas
                if (touchInteractivity.touchedSprite[touchInteraction.pointerId] == null) {

                    // <INTERACTION>
                    touchInteractivity.isTouchingSprite[touchInteraction.pointerId] = false;
                    // </INTERACTION>

                    // <PERSPECTIVE>
                    this.getPerspective().focusSprite = null;
                    // this.isPanningEnabled = false;
                    // </PERSPECTIVE>
                }
            }
        }
    }

    public void onHoldListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
        Log.v("MapViewEvent", "onHoldListener");

        if (touchInteractivity.dragDistance[touchInteraction.pointerId] < TouchInteraction.MINIMUM_DRAG_DISTANCE) {
            // Holding but not (yet) dragging.

            /*
            // Disable panning
            isPanningEnabled = true;

            // Hide ports
            if (sourcePortIndex == -1) {
                for (Visualization systemSprite : this.visualizationSprites) {
                    for (MachineSprite machineSprite : systemSprite.getMachineSprites()) {
                        machineSprite.hidePorts();
                        machineSprite.hidePaths();
                    }
                }
                this.setScale(1.0f);
            }
            */

            // Show ports for sourceMachine board
            if (touchInteractivity.touchedSprite[touchInteraction.pointerId] != null) {
                if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof MachineSprite) {
                    MachineSprite machineSprite = (MachineSprite) touchInteractivity.touchedSprite[touchInteraction.pointerId];
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.HOLD);
                    machineSprite.touch(touchInteraction);

                    //machineSprite.showPorts();
                    //machineSprite.showPaths();
                    //touchSourceSprite = machineSprite;
                    getPerspective().visualization.setScale(0.8f);
                } else if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof PortSprite) {
                    PortSprite portSprite = (PortSprite) touchInteractivity.touchedSprite[touchInteraction.pointerId];
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.HOLD);
                    portSprite.touch(touchInteraction);

//                    portSprite.showPorts();
//                    portSprite.showPaths();
                    getPerspective().visualization.setScale(0.8f);
                }
            }
        }
    }
}
