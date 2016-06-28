package camp.computer.clay.designer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import camp.computer.clay.model.Body;
import camp.computer.clay.model.Machine;
import camp.computer.clay.model.Perspective;
import camp.computer.clay.model.Port;
import camp.computer.clay.model.Simulation;
import camp.computer.clay.model.TouchInteraction;
import camp.computer.clay.model.TouchInteractivity;
import camp.computer.clay.sprite.Visualization;
import camp.computer.clay.sprite.MachineSprite;
import camp.computer.clay.sprite.PathSprite;
import camp.computer.clay.sprite.PortSprite;
import camp.computer.clay.sprite.Sprite;
import camp.computer.clay.sprite.util.Animation;
import camp.computer.clay.sprite.util.Geometry;

public class MapView extends SurfaceView implements SurfaceHolder.Callback {

    private MapViewRenderer mapViewRenderer;

    private SurfaceHolder surfaceHolder;

    // Drawing context
    private Bitmap canvasBitmap = null;
    private Canvas canvas = null;
    private int canvasWidth, canvasHeight;
    private Paint paint = new Paint (Paint.ANTI_ALIAS_FLAG);
    public Matrix identityMatrix;
    public Matrix canvasMatrix;

    // Map
    private PointF originPosition = new PointF ();

    ArrayList<Visualization> visualizationSprites = new ArrayList<Visualization>();

    public MapView(Context context) {
        super(context);
        initialize();
    }

    public MapView (Context context, AttributeSet attrs) {
        super (context, attrs);
        initialize();
    }

    public MapView (Context context, AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);
        initialize();
    }

    Simulation simulation = new Simulation();
    Visualization visualization = new Visualization(simulation);

    private void initialize() {

        initializeSimulation();

        initializeSprites();

        // Create body and set perspective
        Body body = new Body();
        Perspective perspective = new Perspective();
        perspective.visualization = visualization;
        body.setPerspective(perspective);

        // Add body to simulation
        simulation.addBody(body);
    }

    private void initializeSimulation() {

        // TODO: Move Simulation/Machine this into Simulation or Ecology (in Simulation) --- maybe combine Simulation+Ecology
        for (int i = 0; i < 5; i++) {
            Machine machine = new Machine();
            for (int j = 0; j < 12; j++) {
                machine.addPort(new Port());
            }
            simulation.addMachine(machine);
        }
    }

    private void initializeSprites() {

        visualization.initializeSprites();

        visualization.setParentSprite(null);
        visualization.setPosition(new PointF(0, 0));
        visualization.setRotation(0);
        visualizationSprites.add(visualization);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        canvasWidth = getWidth ();
        canvasHeight = getHeight();
        canvasBitmap = Bitmap.createBitmap (canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas ();
        canvas.setBitmap(canvasBitmap);

        identityMatrix = new Matrix ();

        // Center the visualization coordinate system
        originPosition.set(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f);

        // Update perspective on visualization
        simulation.getBody(0).getPerspective().setPosition(new PointF(originPosition.x, originPosition.y));
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void MapView_OnResume () {
        Log.v("MapView", "MapView_OnResume");

        surfaceHolder = getHolder ();
        getHolder ().addCallback (this);

        // Create and start background Thread
        mapViewRenderer = new MapViewRenderer(this);
        mapViewRenderer.setRunning (true);
        mapViewRenderer.start ();

//        // Start communications
//        getClay ().getCommunication ().startDatagramServer();

        updateSurfaceView();

    }

    public void MapView_OnPause () {
        Log.v("MapView", "MapView_OnPause");

        // Pause the communications
//        getClay ().getCommunication ().stopDatagramServer (); // HACK: This was commented out to prevent the server from "crashing" into an invalid state!

        // Kill the background Thread
        boolean retry = true;
        mapViewRenderer.setRunning (false);

        while (retry) {
            try {
                mapViewRenderer.join ();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace ();
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // Coordinate Simulation
    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    // Perspective
    //----------------------------------------------------------------------------------------------

    public static float DEFAULT_SCALE_FACTOR = 1.0f;
    public static int DEFAULT_SCALE_DURATION = 50;

    // private Point originPosition = new Point (0, 0);
    private float targetScale = DEFAULT_SCALE_FACTOR;
    public float scale = targetScale;
    private int scaleDuration = DEFAULT_SCALE_DURATION;

    public void setScale (float targetScale) {

        if (this.targetScale != targetScale) {

            if (this.scale != targetScale) {
                Animation.scaleValue(scale, targetScale, scaleDuration, new Animation.OnScaleListener() {
                    @Override
                    public void onScale(float currentScale) {
                        scale = currentScale;
                    }
                });
            }

            Vibrator v = (Vibrator) ApplicationView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            v.vibrate(50);

            this.targetScale = targetScale;
        }
    }

    //----------------------------------------------------------------------------------------------
    // Layout
    //----------------------------------------------------------------------------------------------

    protected void doDraw(Canvas canvas) {
//        super.onDraw(canvas);

        // <PERSPECTIVE>
        // Move the perspective
        this.canvas.save ();
        //canvas.translate (originPosition.x, originPosition.y);
        this.canvas.translate (
                simulation.getBody(0).getPerspective().getPosition().x - (float) ApplicationView.getApplicationView().getSensorAdapter().getRotationY(),
                simulation.getBody(0).getPerspective().getPosition().y - (float) ApplicationView.getApplicationView().getSensorAdapter().getRotationX()
        );
        // this.canvas.rotate((float) ApplicationView.getApplicationView().getSensorAdapter().getRotationZ());
        this.canvas.scale (scale, scale);
//        canvas.translate (getClay ().getPerspective ().getPosition ().x, getClay ().getPerspective ().getPosition ().y);
//        canvas.scale (getClay ().getPerspective ().getScaleFactor (), getClay ().getPerspective ().getScaleFactor ());
        // </PERSPECTIVE>

        // TODO: Get Simulation
        // TODO: Get Simulation's selected Visualization

        // Draw the background
        this.canvas.drawColor (Color.WHITE);

        // Scene
        drawScene(this);

        // Paint the bitmap to the "primary" canvas.
//        canvas.drawBitmap (canvasBitmap, identityMatrix, null);
        canvas.save();
        canvas.concat(identityMatrix);
        canvas.drawBitmap(canvasBitmap, 0, 0, paint);
        canvas.restore();

        this.canvas.restore();
    }

    private void drawScene (MapView mapView) {
        for (Visualization visualization : visualizationSprites) {
            visualization.draw(mapView);
        }
    }

    public void updateSurfaceView () {
        // The function run in background thread, not UI thread.

        Canvas canvas = null;

        try {
            canvas = surfaceHolder.lockCanvas ();

            synchronized (surfaceHolder) {
                updateState();
                if (canvas != null) {
                    doDraw(canvas);
                }
            }
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost (canvas);
            }
        }
    }

    private void updateState() {
//        if (!hasTouches()) {
            for (Visualization visualization : visualizationSprites) {
                visualization.update();
            }
//        }
    }

    //----------------------------------------------------------------------------------------------
    // Body Interaction Model
    //----------------------------------------------------------------------------------------------

//    public static int MAXIMUM_TOUCH_POINT_COUNT = 5;
//
//    public static int MAXIMUM_TAP_DURATION = 200;
//    public static int MAXIMUM_DOUBLE_TAP_DURATION = 400;
//    public static int TouchInteraction.MINIMUM_HOLD_DURATION = 600;
//
//    public static int MINIMUM_DRAG_DISTANCE = 35;
//
//    private PointF[] touch = new PointF[MAXIMUM_TOUCH_POINT_COUNT];
//    private long[] touchInteraction.touchTime = new long[MAXIMUM_TOUCH_POINT_COUNT];
//    private boolean[] touchInteraction.isTouching = new boolean[MAXIMUM_TOUCH_POINT_COUNT];
//    private boolean[] isDragging = new boolean[MAXIMUM_TOUCH_POINT_COUNT];
//    private double[] touchInteractivity.dragDistance = new double[MAXIMUM_TOUCH_POINT_COUNT];
//
//    private PointF[] touchInteraction.touchPrevious = new PointF[MAXIMUM_TOUCH_POINT_COUNT];
//    private long[] touchInteraction.touchPreviousTime = new long[MAXIMUM_TOUCH_POINT_COUNT];
//    private boolean[] touchInteraction.isTouchingPrevious = new boolean[MAXIMUM_TOUCH_POINT_COUNT];
//    private boolean[] touchInteraction.isTouchingActionPrevious = new boolean[MAXIMUM_TOUCH_POINT_COUNT];
//
//    // Point where the touch started.
//    private PointF[] touchInteraction.touchStart= new PointF[MAXIMUM_TOUCH_POINT_COUNT];
//    private long touchInteraction.touchStartTime = java.lang.System.currentTimeMillis ();
//
//    // Point where the touch ended.
//    private PointF[] touchStop = new PointF[MAXIMUM_TOUCH_POINT_COUNT];
//    private long touchStopTime = java.lang.System.currentTimeMillis ();
//
//    // Touch state
//    private boolean hasTouches = false; // i.e., At least one touch is detected.
//    private int touchCount = 0; // i.e., The total number of touch points detected.
//    private boolean[] touchInteractivity.isTouchingSprite = new boolean[MAXIMUM_TOUCH_POINT_COUNT];
//    private Sprite[] touchInteractivity.touchedSprite = new Sprite[MAXIMUM_TOUCH_POINT_COUNT];

    // Interactivity state
    // TODO: Add to body (i.e., enable looking around)

//    public enum Focus {
//
//        MAP(0),
//        MACHINE(1),
//        PORT(2),
//        PATH(3),
//        PRE_DRAG(4),
//        DRAG(5),
//        RELEASE(6),
//        TAP(7),
//        DOUBLE_DAP(8);
//
//        // TODO: Change the index to a UUID?
//        int index;
//
//        Focus(int index) {
//            this.index = index;
//        }
//    }

    private TouchInteraction.TouchInteractionType touchInteractionType;

    // TODO: In the queue, store the touch actions persistently after exceeding maximum number for immediate interactions.
    //private ArrayList<TouchInteraction> touchInteractionHistory = new ArrayList<TouchInteraction>();
//    private ArrayList<TouchInteraction> touchInteractionSegment = new ArrayList<TouchInteraction>();
    private TouchInteractivity touchInteractivity = null;
    // Touch Interaction is a sequence of actions
    // classifyOngoingInteraction --> make callback
    // classifyCompleteInteraction --> make callback
    private Sprite overlappedSprite = null;

    // Gesture Envelope for Making a Wireless Channel
    // Gestural language. Grammar for the gestures composing it. Think of these as templates for
    // gestures that Clay attempts to evaluate and cleans up after, following each touch action.


//    private Handler touchInteraction.timerHandler = new Handler();
//    private Runnable touchInteraction.timerRunnable = new Runnable() {
//        @Override
//        public void run() {
//            /* do what you need to do */
//            //foobar();
//            int pointerId = 0;
//            if (touchInteraction.isTouching[touchInteraction.pointerId])
//                if (touchInteractivity.dragDistance[touchInteraction.pointerId] < MINIMUM_DRAG_DISTANCE) {
//                    onHoldListener(touchInteraction.pointerId);
//                }
//
//            // Uncomment this for periodic callback
//            // touchInteraction.timerHandler.postDelayed(this, 100);
//        }
//    };

//    private void initializeTouchInteractionProcessor() {
//
//        for (int i = 0; i < MAXIMUM_TOUCH_POINT_COUNT; i++) {
//            touchInteraction.touch[i] = new PointF();
//            touchInteraction.touchPrevious[i] = new PointF();
//            touchInteraction.touchStart[i] = new PointF();
//            touchStop[i] = new PointF();
//        }
//    }
//
//    public boolean hasTouches () {
//        for (int i = 0; i < MAXIMUM_TOUCH_POINT_COUNT; i++) {
//            if (touchInteraction.isTouching[i]) {
//                return true;
//            }
//        }
//        return false;
//    }

    // Perspective/Activity:
    //
    // - default
    //    - focus on machine (after touching it)
    //       - focus on one port + all its paths (after touching it)
    //          - focus on one path(s) (after touching it)
    //          - search for dest. port of those appearing near touch (after dragging from a port)
    //    - scan/browse map (after dragging on map/device)
    //    - move machine (after holding it, then/before dragging it)


    @Override
    public boolean onTouchEvent (MotionEvent motionEvent) {

        int pointerIndex = ((motionEvent.getAction () & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT);
        int pointerId = motionEvent.getPointerId (pointerIndex);
        //int touchInteraction = (motionEvent.getAction () & MotionEvent.ACTION_MASK);
        int touchInteractionType = (motionEvent.getAction () & MotionEvent.ACTION_MASK);
        int pointCount = motionEvent.getPointerCount ();

//        PointF[] touch = new PointF[TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT];

        Log.v("InteractionHistory", "Started touch composition.");

        TouchInteraction touchInteraction = new TouchInteraction(TouchInteraction.TouchInteractionType.NONE);
        touchInteraction.setBody(simulation.getBody(0));

        if (pointCount <= TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT) {
            if (pointerIndex <= TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT - 1) {

                // Current
                // Update touch state based the points given by the host OS (e.g., Android).
                for (int i = 0; i < pointCount; i++) {
                    int id = motionEvent.getPointerId (i);
                    PointF perspectivePosition = simulation.getBody(0).getPerspective().getPosition();
                    touchInteraction.touch[id].x = (motionEvent.getX (i) - perspectivePosition.x) / scale;
                    touchInteraction.touch[id].y = (motionEvent.getY (i) - perspectivePosition.y) / scale;
                     touchInteraction.touchTime[id] = java.lang.System.currentTimeMillis ();
                }

                // Update the state of the touched object based on the current touch interaction state.
                if (touchInteractionType == MotionEvent.ACTION_DOWN) {
                    touchInteractivity = new TouchInteractivity(); // Create on first!
                    touchInteraction.setType(TouchInteraction.TouchInteractionType.TOUCH);
                    touchInteraction.pointerId = pointerId;
                    touchInteractivity.addInteraction(touchInteraction);
                    Body body = simulation.getBody(0);
                    body.onTouchListener(touchInteractivity, touchInteraction);
                } else if (touchInteractionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO:
                } else if (touchInteractionType == MotionEvent.ACTION_MOVE) {
                    touchInteraction.setType(TouchInteraction.TouchInteractionType.MOVE);
                    touchInteraction.pointerId = pointerId;
                    touchInteractivity.addInteraction(touchInteraction);
                    onMoveListener(touchInteractivity, touchInteraction);
                } else if (touchInteractionType == MotionEvent.ACTION_UP) {
                    touchInteraction.setType(TouchInteraction.TouchInteractionType.RELEASE);
                    touchInteraction.pointerId = pointerId;
                    touchInteractivity.addInteraction(touchInteraction);
                    onReleaseListener(touchInteractivity, touchInteraction);
                } else if (touchInteractionType == MotionEvent.ACTION_POINTER_UP) {
                    // TODO:
                } else if (touchInteractionType == MotionEvent.ACTION_CANCEL) {
                    // TODO:
                } else {
                    // TODO:
                }
            }
        }

        return true;
    }

//    //private void onTouchListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
//    private void onTouchListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
//        Log.v("MapViewEvent", "onTouchListener");
//
////        // TODO: Encapsulate TouchInteraction in TouchEvent
////        TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TOUCH);
////        touchInteractivity.addInteraction(touchInteraction);
//
//        int pointerId = touchInteraction.pointerId;
//
//        // Previous
////        touchInteraction.isTouchingPrevious[touchInteraction.pointerId] = touchInteraction.isTouching[touchInteraction.pointerId]; // (or) touchInteraction.isTouchingPrevious[touchInteraction.pointerId] = false;
////        touchInteraction.touchPrevious[touchInteraction.pointerId].x = touchInteraction.touch[touchInteraction.pointerId].x;
////        touchInteraction.touchPrevious[touchInteraction.pointerId].y = touchInteraction.touch[touchInteraction.pointerId].y;
////        touchInteraction.touchPreviousTime[touchInteraction.pointerId] = java.lang.System.currentTimeMillis ();
//
//        // Current
//        touchInteraction.isTouching[touchInteraction.pointerId] = true;
//
//        // Initialize touched sprite to none
//        touchInteractivity.touchedSprite[touchInteraction.pointerId] = null;
//
//        Perspective currentPerspective = this.simulation.getBody(0).getPerspective();
//
//        // First
////        if (touchInteraction.isTouching[touchInteraction.pointerId] == true && touchInteraction.isTouchingPrevious[touchInteraction.pointerId] == false) {
//        if (touchInteraction == touchInteractivity.getFirstInteraction()) {
//
//            Log.v("Toucher", "1");
//
//            // Set the first point of touch
////            touchInteraction.touchStart[touchInteraction.pointerId].x = touchInteraction.touch[touchInteraction.pointerId].x;
////            touchInteraction.touchStart[touchInteraction.pointerId].y = touchInteraction.touch[touchInteraction.pointerId].y;
////            touchInteraction.touchStartTime = java.lang.System.currentTimeMillis ();
//
//            // Reset dragging state
//            touchInteractivity.isDragging[touchInteraction.pointerId] = false;
//            touchInteractivity.dragDistance[touchInteraction.pointerId] = 0;
//
//            // Reset object interaction state
//            for (Visualization visualization : visualizationSprites) {
//                for (MachineSprite machineSprite : visualization.getMachineSprites()) {
//                    // Log.v ("MapViewTouch", "Object at " + machineSprite.x + ", " + machineSprite.y);
//                    if (simulation.getBody(0).getPerspective().focusSprite == null || simulation.getBody(0).getPerspective().focusSprite instanceof MachineSprite || simulation.getBody(0).getPerspective().focusSprite instanceof PortSprite) {
//                        // Check if one of the objects is touched
//                        if (touchInteractivity.touchedSprite[touchInteraction.pointerId] == null) {
//                            if (machineSprite.isTouching(touchInteraction.touch[touchInteraction.pointerId])) {
//
//                                Log.v("Toucher", "Machine");
//
////                                // <TOUCH_ACTION>
////                                TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TOUCH);
////                                machineSprite.touch(touchInteraction);
////                                // </TOUCH_ACTION>
//
//                                // TODO: Add this to an onTouch callback for the sprite's channel nodes
//                                // TODO: i.e., callback Sprite.onTouch (via Sprite.touch())
//
//                                touchInteractivity.isTouchingSprite[touchInteraction.pointerId] = true;
//                                touchInteractivity.touchedSprite[touchInteraction.pointerId] = machineSprite;
//
//                                // <PERSPECTIVE>
//                                currentPerspective.focusSprite = machineSprite;
//                                currentPerspective.disablePanning();
//                                // </PERSPECTIVE>
//
//                                // Break to limit the number of objects that can be touch by a finger to one (1:1 finger:touch relationship).
//                                break;
//
//                            }
//                        }
//                    }
//
//                    if (simulation.getBody(0).getPerspective().focusSprite instanceof MachineSprite || simulation.getBody(0).getPerspective().focusSprite instanceof PortSprite || simulation.getBody(0).getPerspective().focusSprite instanceof PathSprite) {
//
//                        if (touchInteractivity.touchedSprite[touchInteraction.pointerId] == null) {
//                            for (PortSprite portSprite : machineSprite.portSprites) {
//
//                                // If perspective is on path, then constraint interactions to ports in the path
//                                if (simulation.getBody(0).getPerspective().focusSprite instanceof PathSprite) {
//                                    PathSprite focusedPathSprite = (PathSprite) simulation.getBody(0).getPerspective().focusSprite;
//                                    if (!focusedPathSprite.getPath().contains(portSprite)) {
//                                        Log.v("InteractionHistory", "Skipping port not in path.");
//                                        continue;
//                                    }
//                                }
//
//                                if (portSprite.isTouching(touchInteraction.touch[touchInteraction.pointerId])) {
//                                    Log.v("PortTouch", "start touch on port " + portSprite);
//
////                                    // <TOUCH_ACTION>
////                                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TOUCH);
////                                    portSprite.touch(touchInteraction);
////                                    // </TOUCH_ACTION>
//
//                                    touchInteractivity.isTouchingSprite[touchInteraction.pointerId] = true;
//                                    touchInteractivity.touchedSprite[touchInteraction.pointerId] = portSprite;
//
//                                    // <PERSPECTIVE>
//                                    currentPerspective.focusSprite = portSprite;
//                                    currentPerspective.disablePanning();
//                                    // </PERSPECTIVE>
//
//                                    break;
//                                }
//                            }
//                        }
//                    }
//
//                    if (simulation.getBody(0).getPerspective().focusSprite instanceof PortSprite || simulation.getBody(0).getPerspective().focusSprite instanceof PathSprite) {
//                        if (touchInteractivity.touchedSprite[touchInteraction.pointerId] == null) {
//                            for (PortSprite portSprite : machineSprite.portSprites) {
//                                for (PathSprite pathSprite : portSprite.pathSprites) {
//
//                                    float distanceToLine = (float) Geometry.calculateLineToPointDistance(
//                                            pathSprite.getPath().getSourcePort().getPosition(),
//                                            pathSprite.getPath().getDestinationPort().getPosition(),
//                                            touchInteraction.touch[touchInteraction.pointerId],
//                                            true
//                                    );
//
//                                    //Log.v("DistanceToLine", "distanceToLine: " + distanceToLine);
//
//                                    if (distanceToLine < 60) {
//
//                                        Log.v("PathTouch", "start touch on path " + pathSprite);
//
////                                        // <TOUCH_ACTION>
////                                        TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TOUCH);
////                                        pathSprite.touch(touchInteraction);
////                                        // </TOUCH_ACTION>
//
//                                        touchInteractivity.isTouchingSprite[touchInteraction.pointerId] = true;
//                                        touchInteractivity.touchedSprite[touchInteraction.pointerId] = pathSprite;
//
//                                        // <PERSPECTIVE>
//                                        currentPerspective.focusSprite = pathSprite;
//                                        currentPerspective.disablePanning();
//                                        // </PERSPECTIVE>
//
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    // TODO: Check for touch on path flow editor (i.e., spreadsheet or JS editors)
//                }
//            }
//
//            if (simulation.getBody(0).getPerspective().focusSprite == null || simulation.getBody(0).getPerspective().focusSprite instanceof MachineSprite || simulation.getBody(0).getPerspective().focusSprite instanceof PortSprite || simulation.getBody(0).getPerspective().focusSprite instanceof PathSprite) {
//                // Touch the canvas
//                if (touchInteractivity.touchedSprite[touchInteraction.pointerId] == null) {
//
//                    // <INTERACTION>
//                    touchInteractivity.isTouchingSprite[touchInteraction.pointerId] = false;
//                    // </INTERACTION>
//
//                    // <PERSPECTIVE>
//                    this.simulation.getBody(0).getPerspective().focusSprite = null;
//                    // this.isPanningEnabled = false;
//                    // </PERSPECTIVE>
//                }
//            }
//        }
//    }

    //private void onMoveListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
    private void onMoveListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
        Log.v("MapViewEvent", "onMoveListener");

        int pointerId = touchInteraction.pointerId;

        // Previous
//        touchInteraction.isTouchingPrevious[touchInteraction.pointerId] = touchInteraction.isTouching[touchInteraction.pointerId];
//        touchInteraction.touchPrevious[touchInteraction.pointerId].x = touchInteraction.touch[touchInteraction.pointerId].x;
//        touchInteraction.touchPrevious[touchInteraction.pointerId].y = touchInteraction.touch[touchInteraction.pointerId].y;

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

        Perspective currentPerspective = this.simulation.getBody(0).getPerspective();

        //Log.v("MapViewEvent", "onDragListener");

//        // TODO: Encapsulate TouchInteraction in TouchEvent
//        TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.DRAG);
//        touchInteractivity.addInteraction(touchInteraction);

        // Process
        // TODO: Put into callback

        // Dragging and holding.
        if (touchInteractivity.getFirstInteraction().touchTime[touchInteraction.pointerId] - touchInteraction.touchTime[touchInteraction.pointerId] < TouchInteraction.MINIMUM_HOLD_DURATION) {

            Log.v("Toucher2", "A");

            // Dragging only (not holding)

            // TODO: Put into callback
            //if (touchInteractivity.isTouchingSprite[touchInteraction.pointerId]) {
            if (touchInteractivity.touchedSprite[touchInteraction.pointerId] != null) {
                Log.v("Toucher2", "B");
                if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof MachineSprite) {
                    MachineSprite machineSprite = (MachineSprite) touchInteractivity.touchedSprite[touchInteraction.pointerId];
//                    TouchInteraction touchInteraction = new TouchInteraction(TouchInteraction.TouchInteractionType.DRAG);
                    machineSprite.touch(touchInteraction);
                        machineSprite.showHighlights = true;
                        machineSprite.setPosition(new PointF(touchInteraction.touch[touchInteraction.pointerId].x, touchInteraction.touch[touchInteraction.pointerId].y));
                } else if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof PortSprite) {
                    Log.v("Toucher2", "C");
                    PortSprite portSprite = (PortSprite) touchInteractivity.touchedSprite[touchInteraction.pointerId];
//                    TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.DRAG);
//                    portSprite.touch(touchInteraction);
                    portSprite.setCandidatePathDestinationPosition(touchInteraction.touch[touchInteraction.pointerId]);
                    //portSprite.setCandidatePathDestinationPosition(touchInteraction.getPosition());
                    portSprite.setCandidatePathVisibility(true);


                    // Initialize port type and flow direction
                    Port port = (Port) portSprite.getModel();
                    port.portDirection = Port.PortDirection.INPUT;
                    if (port.portType == Port.PortType.NONE) {
                        port.portType = Port.PortType.getNextType(port.portType); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length
                    }

                    // Show ports of nearby machines
                    for (Visualization visualizationSprites: this.visualizationSprites) {
                        for (MachineSprite nearbyMachineSprite: visualizationSprites.getMachineSprites()) {

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
                                            if (nearbyPortSprite != overlappedSprite) {
                                                overlappedSprite = nearbyPortSprite;
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

                }
            } else {
                Log.v("Toucher", "Pan 2a");
                if (currentPerspective.isPanningEnabled()) {
                    Log.v("Toucher", "Pan 2");
                    this.setScale(0.8f);
                    simulation.getBody(0).getPerspective().setOffset((int) (touchInteraction.touch[touchInteraction.pointerId].x - touchInteractivity.getFirstInteraction().touch[touchInteraction.pointerId].x), (int) (touchInteraction.touch[touchInteraction.pointerId].y - touchInteractivity.getFirstInteraction().touch[touchInteraction.pointerId].y));
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
                    port.portDirection = Port.PortDirection.INPUT;
                    if (port.portType == Port.PortType.NONE) {
                        port.portType = Port.PortType.getNextType(port.portType); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length
                    }

                    // Show ports of nearby machines
                    for (Visualization visualization : this.visualizationSprites) {
                        for (MachineSprite nearbyMachineSprite: visualization.getMachineSprites()) {

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
                                            if (nearbyPortSprite != overlappedSprite) {
                                                overlappedSprite = nearbyPortSprite;
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

                }
            } else {
                Log.v("Toucher", "Pan 1a");
                if (currentPerspective.isPanningEnabled()) {
                    Log.v("Toucher", "Pan 1");
                    simulation.getBody(0).getPerspective().setOffset((int) (touchInteraction.touch[touchInteraction.pointerId].x - touchInteractivity.getFirstInteraction().touch[touchInteraction.pointerId].x), (int) (touchInteraction.touch[touchInteraction.pointerId].y - touchInteractivity.getFirstInteraction().touch[touchInteraction.pointerId].y));
                }
            }
        }
    }

    //private void onReleaseListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {
    private void onReleaseListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        touchInteractivity.timerHandler.removeCallbacks(touchInteractivity.timerRunnable);

        int pointerId = touchInteraction.pointerId;

        Perspective currentPerspective = this.simulation.getBody(0).getPerspective();

        Log.v("MapViewEvent", "onReleaseListener");

        // TODO: Encapsulate TouchInteraction in TouchEvent
//        TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.RELEASE);
//        touchInteractivity.addInteraction(touchInteraction);
        // TODO: resolveInteraction
        // TODO: cacheInteraction/recordInteraction(InDatabase)
        //touchInteractionSegment.clear();

//        // Previous
        TouchInteraction previousInteraction = touchInteractivity.getPreviousInteraction(touchInteraction);
//        previousInteraction.isTouching[touchInteraction.pointerId] = touchInteraction.isTouching[touchInteraction.pointerId];
//        previousInteraction.touch[touchInteraction.pointerId].x = touchInteraction.touch[touchInteraction.pointerId].x;
//        previousInteraction.touch[touchInteraction.pointerId].y = touchInteraction.touch[touchInteraction.pointerId].y;

        // Current
        touchInteraction.isTouching[touchInteraction.pointerId] = false;

        // Stop touching. Check if this is the start of a touch gesture (i.e., the first touch in a sequence of touch events for the given finger)
        if (touchInteraction.isTouching[touchInteraction.pointerId] == false && previousInteraction.isTouching[touchInteraction.pointerId] == true) {
            Log.v("Toucher", "2");
//            touchInteraction.touchStop[touchInteraction.pointerId].x = touchInteraction.touch[touchInteraction.pointerId].x;
//            touchInteraction.touchStop[touchInteraction.pointerId].y = touchInteraction.touch[touchInteraction.pointerId].y;
//            touchInteraction.touchStopTime = java.lang.System.currentTimeMillis ();
        }

        // Classify/Callbacks
        if (touchInteraction.touchTime[pointerId] - touchInteractivity.getFirstInteraction().touchTime[pointerId] < TouchInteraction.MAXIMUM_TAP_DURATION) {
            Log.v("Toucher2", "3");
            onTapListener(touchInteractivity, touchInteraction);

        } else {

            Log.v("Toucher2", "-1");

            if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof MachineSprite) {
                Log.v("Toucher2", "0");
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
                    for (Visualization visualization : this.visualizationSprites) {
                        for (MachineSprite otherMachineSprite : visualization.getMachineSprites()) {
                            otherMachineSprite.hidePorts();
                            otherMachineSprite.hidePaths();
                            otherMachineSprite.setTransparency(0.1f);
                        }
                    }
                    machineSprite.showPorts();
                    this.setScale(0.8f);
                    machineSprite.showPaths();
                    machineSprite.setTransparency(1.0f);
                    ApplicationView.getApplicationView().speakPhrase("choose a channel to get data.");

                    currentPerspective.disablePanning();
                }

            } else if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof PortSprite) {
                PortSprite portSprite = (PortSprite) touchInteractivity.touchedSprite[touchInteraction.pointerId];
//                TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.RELEASE);
                portSprite.touch(touchInteraction);
                Log.v("Toucher2", "1");


                // Show ports of nearby machines
                for (Visualization visualization : this.visualizationSprites) {
                    for (MachineSprite nearbyMachineSprite: visualization.getMachineSprites()) {

                        // Update style of nearby machines
                        float distanceToMachineSprite = (float) Geometry.calculateDistance(
                                touchInteraction.touch[touchInteraction.pointerId],
                                nearbyMachineSprite.getPosition()
                        );
                        Log.v("DistanceToSprite", "distanceToMachineSprite: " + distanceToMachineSprite);
                        if (distanceToMachineSprite < nearbyMachineSprite.boardHeight + 50) {

                            Log.v("Toucher2", "2");



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

                                        Port port = (Port) portSprite.getModel();
                                        Port nearbyPort = (Port) nearbyPortSprite.getModel();

                                        port.portDirection = Port.PortDirection.INPUT;
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


                                        pathSprite.showPathDocks = false;
                                        pathSprite.showDirectedPaths = true;
                                        pathSprite.setVisibility(true);
//                                        pathSprite.showDirectedPaths = true;
//                                        pathSprite.showPathDocks = false;




                                        Vibrator v = (Vibrator) ApplicationView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                                        // Vibrate for 500 milliseconds
                                        v.vibrate(50);
                                        //v.vibrate(50); off
                                        //v.vibrate(50); // second tap
                                        overlappedSprite = null;
                                        break;
                                    }
                                }
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

            } else {
                if (touchInteractivity.touchedSprite[touchInteraction.pointerId] == null) {
                    // No touch on board or port. Touch is on map. So hide ports.
                    for (Visualization visualization : this.visualizationSprites) {
                        for (MachineSprite machineSprite : visualization.getMachineSprites()) {
                            machineSprite.hidePorts();
                            machineSprite.setScale(1.0f);
                            machineSprite.hidePaths();
                            machineSprite.setTransparency(1.0f);
                        }
                    }
                    this.setScale(1.0f);
                }
            }

//            if (touchSourceSprite != null && sourcePortIndex != -1 && touchDestinationSprite != null) {
//                ApplicationView.getApplicationView().speakPhrase("the channel was interrupted.");
//            }
//
//            // Reset selected destinationMachine port
//            if (destinationPortIndex != -1) {
//                if (touchDestinationSprite instanceof MachineSprite) {
//                    MachineSprite touchDestinationMachineSprite = (MachineSprite) touchDestinationSprite;
//
//                    touchDestinationMachineSprite.getPortSprite(destinationPortIndex).portType = PortSprite.PortType.NONE;
//                }
//            }

            // Reset map interactivity
            currentPerspective.enablePanning();

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
    }

    private void onTapListener(TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

        Perspective currentPerspective = this.simulation.getBody(0).getPerspective();

        if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof MachineSprite) {
            MachineSprite machineSprite = (MachineSprite) touchInteractivity.touchedSprite[touchInteraction.pointerId];


            // TODO: Add this to an onTouch callback for the sprite's channel nodes
            // Check if the touched board's I/O node is touched
            // Check if one of the objects is touched
            if (machineSprite.isTouching(touchInteraction.touch[touchInteraction.pointerId])) {
                Log.v("MapView", "\tTouched machine.");

                // <TOUCH_ACTION>
//                TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TAP);
                // TODO: propagate RELEASE before TAP
                machineSprite.touch(touchInteraction);
                // </TOUCH_ACTION>

                // Remove focus from other machines.
                for (Visualization visualization : this.visualizationSprites) {
                    for (MachineSprite otherMachineSprite: visualization.getMachineSprites()) {
                        otherMachineSprite.hidePorts();
                        otherMachineSprite.hidePaths();
                        otherMachineSprite.setTransparency(0.1f);
                    }
                }

                // Focus on machine.
                machineSprite.showPorts();
                machineSprite.showPaths();
                machineSprite.setTransparency(1.0f);
                ApplicationView.getApplicationView().speakPhrase("choose a channel to get data.");

                // Scale map.
                this.setScale(0.8f);

                currentPerspective.disablePanning();
            }


        } else if (touchInteractivity.touchedSprite[touchInteraction.pointerId] instanceof PortSprite) {
            PortSprite portSprite = (PortSprite) touchInteractivity.touchedSprite[touchInteraction.pointerId];

            Log.v("Toucher", "A");

            Log.v("MapView", "\tPort " + (portSprite.getIndex() + 1) + " touched.");

            if (portSprite.isTouching(touchInteraction.touch[touchInteraction.pointerId])) {
//                TouchInteraction touchInteraction = new TouchInteraction(touchInteraction.touch[touchInteraction.pointerId], TouchInteraction.TouchInteractionType.TAP);
                portSprite.touch(touchInteraction);

                Log.v("MapView", "\tSource port " + (portSprite.getIndex() + 1) + " touched.");

                Port port = (Port) portSprite.getModel();

                if (port.getType() == Port.PortType.NONE) {

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
                            for (Visualization visualization : this.visualizationSprites) {
                                for (MachineSprite machineSprite : visualization.getMachineSprites()) {
                                    machineSprite.hidePorts();
                                    machineSprite.hidePaths();
                                }
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

            Log.v("Toucher", "B");

            if (pathSprite.getEditorVisibility()) {
                pathSprite.setEditorVisibility(false);
            } else {
                pathSprite.setEditorVisibility(true);
            }

        } else if (touchInteractivity.touchedSprite[touchInteraction.pointerId] == null) {

            Log.v("Toucher", "C");

            // No touch on board or port. Touch is on map. So hide ports.
            for (Visualization visualization : this.visualizationSprites) {
                for (MachineSprite machineSprite : visualization.getMachineSprites()) {
                    machineSprite.hidePorts();
                    machineSprite.setScale(1.0f);
                    machineSprite.hidePaths();
                    machineSprite.setTransparency(1.0f);
                }
            }
            this.setScale(1.0f);

            // Reset map interactivity
            currentPerspective.enablePanning();
        }

        Log.v("Toucher", "D");

    }

    private void onDoubleTapCallback (TouchInteractivity touchInteractivity, TouchInteraction touchInteraction) {

    }

    public float getScale() {
        return scale;
    }

    public Canvas getCanvas() {
        return this.canvas;
    }

    public Paint getPaint() {
        return this.paint;
    }
}
