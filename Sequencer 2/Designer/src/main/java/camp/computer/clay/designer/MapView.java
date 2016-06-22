package camp.computer.clay.designer;

import android.annotation.SuppressLint;
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

import camp.computer.clay.model.TouchArticulation;
import camp.computer.clay.sprite.MachineSprite;
import camp.computer.clay.sprite.PathSprite;
import camp.computer.clay.sprite.PortSprite;
import camp.computer.clay.sprite.Sprite;
import camp.computer.clay.sprite.SystemSprite;
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
    private Matrix identityMatrix;
    private Matrix originMatrix;

    // Map
    private PointF originPosition = new PointF ();
    private PointF currentPosition = new PointF ();

    ArrayList<SystemSprite> systemSprites = new ArrayList<SystemSprite>();

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

    private void initialize() {
        initializeSprites();
        initializeTouchInteractionProcessor();
    }

    public void initializeSprites() {
        systemSprites.add(new SystemSprite());
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        canvasWidth = getWidth ();
        canvasHeight = getHeight();
        canvasBitmap = Bitmap.createBitmap (canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas ();
        canvas.setBitmap(canvasBitmap);

        identityMatrix = new Matrix ();

        originMatrix = new Matrix ();
        originMatrix.setTranslate(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f);
//
//        canvas.setMatrix(originMatrix);

        // TODO: Move setPosition to a better location!
//        getClay().getPerspective ().setPosition(canvas.getWidth() / 2, canvas.getHeight() / 2);
        originPosition.set(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f);
//        originPosition.set(0, 0);

        currentPosition.set(originPosition.x, originPosition.y);
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
    // Coordinate System
    //----------------------------------------------------------------------------------------------

    public Matrix getOriginMatrix(boolean scale) {
        Matrix originMatrixCopy = new Matrix();
        float[] v = new float[9];
        this.originMatrix.getValues(v);
//        if (scale) {
//            originMatrixCopy.setScale(this.scale, this.scale);
//        }
        originMatrixCopy.setTranslate(v[Matrix.MTRANS_X], v[Matrix.MTRANS_Y]);
        return originMatrixCopy;
    }

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

    public void setOrigin (PointF position) {
        this.originPosition.x = position.x;
        this.originPosition.y = position.y;
    }

    //----------------------------------------------------------------------------------------------
    // Layout
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // <PERSPECTIVE>
        // Move the perspective
        this.canvas.save ();
        //canvas.translate (originPosition.x, originPosition.y);
        this.canvas.translate (currentPosition.x, currentPosition.y);
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
        canvas.drawBitmap (canvasBitmap, identityMatrix, null);

        this.canvas.restore();
    }

    private void drawScene (MapView mapView) {
        for (SystemSprite systemSprite : systemSprites) {
            drawSprite(systemSprite);
        }
    }

    private void drawSprite(Sprite sprite) {
        sprite.draw(this);
    }

    @SuppressLint("WrongCall")
    public void updateSurfaceView () {
        // The function run in background thread, not UI thread.

        Canvas canvas = null;

        try {
            canvas = surfaceHolder.lockCanvas ();

            synchronized (surfaceHolder) {
                updateState();
                if (canvas != null) {
                    onDraw(canvas);
                }
            }
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost (canvas);
            }
        }
    }

    private void updateState() {
        if (!hasTouches()) {
            for (SystemSprite systemSprite : systemSprites) {
                systemSprite.updateState();
            }
        }
    }

    //----------------------------------------------------------------------------------------------
    // Body Interaction Model
    //----------------------------------------------------------------------------------------------

    public static int MAXIMUM_TOUCH_POINT_COUNT = 5;

    public static int MAXIMUM_TAP_DURATION = 200;
    public static int MAXIMUM_DOUBLE_TAP_DURATION = 400;
    public static int MINIMUM_HOLD_DURATION = 800;

    public static int MINIMUM_DRAG_DISTANCE = 35;

    private PointF[] touch = new PointF[MAXIMUM_TOUCH_POINT_COUNT];
    private long[] touchTime = new long[MAXIMUM_TOUCH_POINT_COUNT];
    private boolean[] isTouching = new boolean[MAXIMUM_TOUCH_POINT_COUNT];
    private boolean[] isDragging = new boolean[MAXIMUM_TOUCH_POINT_COUNT];
    private double[] dragDistance = new double[MAXIMUM_TOUCH_POINT_COUNT];

    private PointF[] touchPrevious = new PointF[MAXIMUM_TOUCH_POINT_COUNT];
    private long[] touchPreviousTime = new long[MAXIMUM_TOUCH_POINT_COUNT];
    private boolean[] isTouchingPrevious = new boolean[MAXIMUM_TOUCH_POINT_COUNT];
    private boolean[] isTouchingActionPrevious = new boolean[MAXIMUM_TOUCH_POINT_COUNT];

    // Point where the touch started.
    private PointF[] touchStart = new PointF[MAXIMUM_TOUCH_POINT_COUNT];
    private long touchStartTime = java.lang.System.currentTimeMillis ();

    // Point where the touch ended.
    private PointF[] touchStop = new PointF[MAXIMUM_TOUCH_POINT_COUNT];
    private long touchStopTime = java.lang.System.currentTimeMillis ();

    // Touch state
    private boolean hasTouches = false; // i.e., At least one touch is detected.
    private int touchCount = 0; // i.e., The total number of touch points detected.
    private boolean[] isTouchingSprite = new boolean[MAXIMUM_TOUCH_POINT_COUNT];
    private Sprite[] touchedSprite = new Sprite[MAXIMUM_TOUCH_POINT_COUNT];

    // Interactivity state
    private boolean isPanningEnabled = true;

    // TODO: In the queue, store the touch actions persistently after exceeding maximum number for immediate interactions.
    //private ArrayList<TouchArticulation> touchActionHistory = new ArrayList<TouchArticulation>();
    private ArrayList<TouchArticulation> touchArticulation = new ArrayList<TouchArticulation>();
    // Touch Interaction is a sequence of actions
    // classifyOngoingInteraction --> make callback
    // classifyCompleteInteraction --> make callback
    private Sprite overlappedSprite = null;

    // Gesture Envelope for Making a Wireless Channel
    // Gestural language. Grammar for the gestures composing it. Think of these as templates for
    // gestures that Clay attempts to evaluate and cleans up after, following each touch action.



    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            /* do what you need to do */
            //foobar();
            int pointerId = 0;
            if (isTouching[pointerId])
                if (dragDistance[pointerId] < MINIMUM_DRAG_DISTANCE) {
                    onHoldCallback(pointerId);
                }

            // Uncomment this for periodic callback
            // timerHandler.postDelayed(this, 100);
        }
    };

    private void initializeTouchInteractionProcessor() {
        for (int i = 0; i < MAXIMUM_TOUCH_POINT_COUNT; i++) {
            touch[i] = new PointF();
            touchPrevious[i] = new PointF();
            touchStart[i] = new PointF();
            touchStop[i] = new PointF();
        }
    }

    public boolean hasTouches () {
        for (int i = 0; i < MAXIMUM_TOUCH_POINT_COUNT; i++) {
            if (isTouching[i]) {
                return true;
            }
        }
        return false;
    }

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
        //int touchAction = (motionEvent.getAction () & MotionEvent.ACTION_MASK);
        int touchActionType = (motionEvent.getAction () & MotionEvent.ACTION_MASK);
        int pointCount = motionEvent.getPointerCount ();

        if (pointCount <= MAXIMUM_TOUCH_POINT_COUNT) {
            if (pointerIndex <= MAXIMUM_TOUCH_POINT_COUNT - 1) {

                // Current
                // Update touch state based the points given by the host OS (e.g., Android).
                for (int i = 0; i < pointCount; i++) {
                    int id = motionEvent.getPointerId (i);
                    touch[id].x = (motionEvent.getX (i) - currentPosition.x) / scale;
                    touch[id].y = (motionEvent.getY (i) - currentPosition.y) / scale;
                    touchTime[id] = java.lang.System.currentTimeMillis ();

//                    xTouch[id] = (motionEvent.getX (i) - currentPosition.x) / scale + canvas.getClipBounds().left;
//                    yTouch[id] = (motionEvent.getY (i) - currentPosition.y) / scale + canvas.getClipBounds().top;
                }

                // Update the state of the touched object based on the current touch interaction state.
                if (touchActionType == MotionEvent.ACTION_DOWN) {
                    onTouchCallback(pointerId);
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                    onMoveCallback(pointerId);
                } else if (touchActionType == MotionEvent.ACTION_UP) {
                   onReleaseCallback(pointerId);
                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                    // TODO:
                } else {
                    // TODO:
                }
            }
        }

        return true;
    }

    private void onTouchCallback (int pointerId) {
        Log.v("MapViewEvent", "onTouchCallback");

        // TODO: Encapsulate TouchArticulation in TouchEvent
        TouchArticulation touchAction = new TouchArticulation(touch[pointerId], TouchArticulation.TouchInteractionType.TOUCH);
        touchArticulation.add(touchAction);

        // Previous
        isTouchingPrevious[pointerId] = isTouching[pointerId]; // (or) isTouchingPrevious[pointerId] = false;
        touchPrevious[pointerId].x = touch[pointerId].x;
        touchPrevious[pointerId].y = touch[pointerId].y;
        touchPreviousTime[pointerId] = java.lang.System.currentTimeMillis ();

        // Current
        isTouching[pointerId] = true;

        // Initialize touched sprite to none
        this.touchedSprite[pointerId] = null;

        // First
        if (this.isTouching[pointerId] == true && this.isTouchingPrevious[pointerId] == false) {

            // Set the first point of touch
            this.touchStart[pointerId].x = this.touch[pointerId].x;
            this.touchStart[pointerId].y = this.touch[pointerId].y;
            this.touchStartTime = java.lang.System.currentTimeMillis ();

            // Reset dragging state
            this.isDragging[pointerId] = false;
            this.dragDistance[pointerId] = 0;

            // Reset object interaction state
            for (SystemSprite systemSprite : systemSprites) {
                for (MachineSprite machineSprite : systemSprite.getMachineSprites()) {
                    // Log.v ("MapViewTouch", "Object at " + machineSprite.x + ", " + machineSprite.y);

                    // Check if one of the objects is touched
                    if (this.touchedSprite[pointerId] == null) {
                        if (machineSprite.isTouching(touchStart[pointerId])) {

                            // <TOUCH_ACTION>
                            TouchArticulation touchArticulation = new TouchArticulation(touch[pointerId], TouchArticulation.TouchInteractionType.TOUCH);
                            machineSprite.touch(touchArticulation);
                            // </TOUCH_ACTION>

                            // TODO: Add this to an onTouch callback for the sprite's channel nodes
                            // TODO: i.e., callback Sprite.onTouch (via Sprite.touch())

                            this.isTouchingSprite[pointerId] = true;
                            this.touchedSprite[pointerId] = machineSprite;

                            isPanningEnabled = false;

                            // Break to limit the number of objects that can be touch by a finger to one (1:1 finger:touch relationship).
                            break;

                        }
                    }

                    if (this.touchedSprite[pointerId] == null) {
                        for (PortSprite portSprite : machineSprite.portSprites) {
                            if (portSprite.isTouching(touchStart[pointerId])) {
                                Log.v("PortTouch", "start touch on port " + portSprite);

                                // <TOUCH_ACTION>
                                TouchArticulation touchArticulation = new TouchArticulation(touch[pointerId], TouchArticulation.TouchInteractionType.TOUCH);
                                portSprite.touch(touchArticulation);
                                // </TOUCH_ACTION>

                                this.isTouchingSprite[pointerId] = true;
                                this.touchedSprite[pointerId] = portSprite;

                                isPanningEnabled = false;

                                break;
                            }
                        }
                    }

                    if (this.touchedSprite[pointerId] == null) {
                        for (PortSprite portSprite : machineSprite.portSprites) {
                            for (PathSprite pathSprite : portSprite.pathSprites) {

                                float distanceToLine = (float) Geometry.LineToPointDistance2D(
                                        pathSprite.getPath().getSourcePort().getPosition(),
                                        pathSprite.getPath().getDestinationPort().getPosition(),
                                        this.touch[pointerId],
                                        true
                                );

                                //Log.v("DistanceToLine", "distanceToLine: " + distanceToLine);

                                if (distanceToLine < 60) {

                                    Log.v("PathTouch", "start touch on path " + pathSprite);

                                    // <TOUCH_ACTION>
                                    TouchArticulation touchArticulation = new TouchArticulation(touch[pointerId], TouchArticulation.TouchInteractionType.TOUCH);
                                    pathSprite.touch(touchArticulation);
                                    // </TOUCH_ACTION>

                                    this.isTouchingSprite[pointerId] = true;
                                    this.touchedSprite[pointerId] = pathSprite;

                                    isPanningEnabled = false;

                                    break;
                                }
                            }
                        }
                    }

                    // TODO: Check for touch on path flow editor (i.e., spreadsheet or JS editors)
                }
            }

            // Touch the canvas
            if (this.touchedSprite[pointerId] == null) {
                this.isTouchingSprite[pointerId] = false;
//                this.isPanningEnabled = true;
            }

            // Start timer to check for hold
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.postDelayed(timerRunnable, MINIMUM_HOLD_DURATION);
        }
    }

    private void onHoldCallback (int pointerId) {
        Log.v("MapViewEvent", "onHoldCallback");

        if (dragDistance[pointerId] < this.MINIMUM_DRAG_DISTANCE) {
            // Holding but not (yet) dragging.

            /*
            // Disable panning
            isPanningEnabled = true;

            // Hide ports
            if (sourcePortIndex == -1) {
                for (SystemSprite systemSprite : this.systemSprites) {
                    for (MachineSprite machineSprite : systemSprite.getMachineSprites()) {
                        machineSprite.hidePorts();
                        machineSprite.hidePaths();
                    }
                }
                this.setScale(1.0f);
            }
            */

            // Show ports for sourceMachine board
            if (touchedSprite[pointerId] != null) {
                if (touchedSprite[pointerId] instanceof MachineSprite) {
                    MachineSprite machineSprite = (MachineSprite) touchedSprite[pointerId];
                    TouchArticulation touchArticulation = new TouchArticulation(touch[pointerId], TouchArticulation.TouchInteractionType.HOLD);
                    machineSprite.touch(touchArticulation);

                    //machineSprite.showPorts();
                    //machineSprite.showPaths();
                    //touchSourceSprite = machineSprite;
                    this.setScale(0.8f);
                } else if (touchedSprite[pointerId] instanceof PortSprite) {
                    PortSprite portSprite = (PortSprite) touchedSprite[pointerId];
                    TouchArticulation touchArticulation = new TouchArticulation(touch[pointerId], TouchArticulation.TouchInteractionType.HOLD);
                    portSprite.touch(touchArticulation);

//                    portSprite.showPorts();
//                    portSprite.showPaths();
                    this.setScale(0.8f);
                }
            }
        }
    }

    private void onMoveCallback (int pointerId) {
        Log.v("MapViewEvent", "onMoveCallback");

        // Previous
        isTouchingPrevious[pointerId] = isTouching[pointerId];
        touchPrevious[pointerId].x = touch[pointerId].x;
        touchPrevious[pointerId].y = touch[pointerId].y;

        // Calculate drag distance
        dragDistance[pointerId] = Geometry.calculateDistance(touch[pointerId], touchStart[pointerId]);

        // Classify/Callback
        if (dragDistance[pointerId] < this.MINIMUM_DRAG_DISTANCE) {
            // Pre-dragging
            onPreDragCallback(pointerId);
        } else {
            // Dragging
            this.isDragging[pointerId] = true;
            onDragCallback(pointerId);
        }
    }

    private void onPreDragCallback (int pointerId) {

        // TODO: Encapsulate TouchArticulation in TouchEvent
        TouchArticulation touchAction = new TouchArticulation(touch[pointerId], TouchArticulation.TouchInteractionType.PRE_DRAG);
        touchArticulation.add(touchAction);

    }

    private void onDragCallback (int pointerId) {
        //Log.v("MapViewEvent", "onDragCallback");

        // TODO: Encapsulate TouchArticulation in TouchEvent
        TouchArticulation touchAction = new TouchArticulation(touch[pointerId], TouchArticulation.TouchInteractionType.DRAG);
        touchArticulation.add(touchAction);

        // Process
        // TODO: Put into callback

        // Dragging and holding.
        if (touchTime[pointerId] - touchStartTime < MINIMUM_HOLD_DURATION) {

            // Dragging only (not holding)

            // TODO: Put into callback
            //if (this.isTouchingSprite[pointerId]) {
            if (touchedSprite[pointerId] != null) {
                if (touchedSprite[pointerId] instanceof MachineSprite) {
                    /*
                    MachineSprite machineSprite = (MachineSprite) touchedSprite[pointerId];
                    TouchArticulation touchAction = new TouchArticulation(TouchArticulation.TouchInteractionType.DRAG);
                    machineSprite.touch(touchAction);
                        machineSprite.showHighlights = true;
                        machineSprite.setPosition(touch[pointerId].x, touch[pointerId].y);
                    */
                } else if (touchedSprite[pointerId] instanceof PortSprite) {
                    PortSprite portSprite = (PortSprite) touchedSprite[pointerId];
                    TouchArticulation touchArticulation = new TouchArticulation(touch[pointerId], TouchArticulation.TouchInteractionType.DRAG);
                    portSprite.touch(touchArticulation);


                    // Initialize port type and flow direction
                    portSprite.portDirection = PortSprite.PortDirection.INPUT;
                    if (portSprite.portType == PortSprite.PortType.NONE) {
                        portSprite.portType = PortSprite.PortType.getNextType(portSprite.portType); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length
                    }

                    // Show ports of nearby machines
                    for (SystemSprite systemSprite: this.systemSprites) {
                        for (MachineSprite nearbyMachineSprite: systemSprite.getMachineSprites()) {

                            // Update style of nearby machines
                            float distanceToMachineSprite = (float) Geometry.calculateDistance(
                                    touch[pointerId],
                                    nearbyMachineSprite.getPosition()
                            );
                            Log.v("DistanceToSprite", "distanceToMachineSprite: " + distanceToMachineSprite);
                            if (distanceToMachineSprite < nearbyMachineSprite.boardWidth + 50) {
                                nearbyMachineSprite.setTransparency(1.0f);
                                nearbyMachineSprite.showPorts();

                                for (PortSprite nearbyPortSprite: nearbyMachineSprite.portSprites) {
                                    if (nearbyPortSprite != portSprite) {
                                        // Scaffold interaction to connect path to with nearby ports
                                        float distanceToNearbyPortSprite = (float) Geometry.calculateDistance(
                                                touch[pointerId],
                                                nearbyPortSprite.getPosition()
                                        );
                                        if (distanceToNearbyPortSprite < nearbyPortSprite.shapeRadius + 20) {
                                            portSprite.setPosition(nearbyPortSprite.getPosition());
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

                            } else if (distanceToMachineSprite < nearbyMachineSprite.boardWidth + 80) {
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
                if (isPanningEnabled) {
                    this.setScale(0.8f);
                    this.currentPosition.offset((int) (touch[pointerId].x - touchStart[pointerId].x), (int) (touch[pointerId].y - touchStart[pointerId].y));
                }
            }

        } else {

            // TODO: Put into callback
            //if (this.isTouchingSprite[pointerId]) {
            if (touchedSprite[pointerId] != null) {
                if (touchedSprite[pointerId] instanceof MachineSprite) {
                    MachineSprite machineSprite = (MachineSprite) touchedSprite[pointerId];
                    TouchArticulation touchArticulation = new TouchArticulation(touch[pointerId], TouchArticulation.TouchInteractionType.DRAG);
                    machineSprite.touch(touchArticulation);
                        machineSprite.showHighlights = true;
                        machineSprite.setPosition(touch[pointerId].x, touch[pointerId].y);
                } else if (touchedSprite[pointerId] instanceof PortSprite) {
                    PortSprite portSprite = (PortSprite) touchedSprite[pointerId];
                    TouchArticulation touchArticulation = new TouchArticulation(touch[pointerId], TouchArticulation.TouchInteractionType.DRAG);
                    portSprite.touch(touchArticulation);


                    // Initialize port type and flow direction
                    portSprite.portDirection = PortSprite.PortDirection.INPUT;
                    if (portSprite.portType == PortSprite.PortType.NONE) {
                        portSprite.portType = PortSprite.PortType.getNextType(portSprite.portType); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length
                    }

                    // Show ports of nearby machines
                    for (SystemSprite systemSprite: this.systemSprites) {
                        for (MachineSprite nearbyMachineSprite: systemSprite.getMachineSprites()) {

                            // Update style of nearby machines
                            float distanceToMachineSprite = (float) Geometry.calculateDistance(
                                    touch[pointerId],
                                    nearbyMachineSprite.getPosition()
                            );
                            Log.v("DistanceToSprite", "distanceToMachineSprite: " + distanceToMachineSprite);
                            if (distanceToMachineSprite < nearbyMachineSprite.boardWidth + 50) {
                                nearbyMachineSprite.setTransparency(1.0f);
                                nearbyMachineSprite.showPorts();

                                for (PortSprite nearbyPortSprite: nearbyMachineSprite.portSprites) {
                                    if (nearbyPortSprite != portSprite) {
                                        // Scaffold interaction to connect path to with nearby ports
                                        float distanceToNearbyPortSprite = (float) Geometry.calculateDistance(
                                                touch[pointerId],
                                                nearbyPortSprite.getPosition()
                                        );
                                        if (distanceToNearbyPortSprite < nearbyPortSprite.shapeRadius + 20) {
                                            portSprite.setPosition(nearbyPortSprite.getPosition());
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

                            } else if (distanceToMachineSprite < nearbyMachineSprite.boardWidth + 80) {
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
                if (isPanningEnabled) {
                    currentPosition.offset((int) (touch[pointerId].x - touchStart[pointerId].x), (int) (touch[pointerId].y - touchStart[pointerId].y));
                }
            }
        }
    }

    private void onReleaseCallback (int pointerId) {
        Log.v("MapViewEvent", "onReleaseCallback");

        // TODO: Encapsulate TouchArticulation in TouchEvent
        TouchArticulation touchAction = new TouchArticulation(touch[pointerId], TouchArticulation.TouchInteractionType.RELEASE);
        touchArticulation.add(touchAction);
        // TODO: resolveInteraction
        // TODO: cacheInteraction/recordInteraction(InDatabase)
        touchArticulation.clear();

        // Previous
        isTouchingPrevious[pointerId] = isTouching[pointerId];
        touchPrevious[pointerId].x = touch[pointerId].x;
        touchPrevious[pointerId].y = touch[pointerId].y;

        // Current
        isTouching[pointerId] = false;

        // Stop touching. Check if this is the start of a touch gesture (i.e., the first touch in a sequence of touch events for the given finger)
        if (this.isTouching[pointerId] == false && this.isTouchingPrevious[pointerId] == true) {
            this.touchStop[pointerId].x = this.touch[pointerId].x;
            this.touchStop[pointerId].y = this.touch[pointerId].y;
            this.touchStopTime = java.lang.System.currentTimeMillis ();
        }

        // Classify/Callbacks
        if (touchStopTime - touchStartTime < MAXIMUM_TAP_DURATION) {

            onTapCallback(pointerId);

        } else {

            if (touchedSprite[pointerId] instanceof MachineSprite) {
                MachineSprite machineSprite = (MachineSprite) touchedSprite[pointerId];


                // TODO: Add this to an onTouch callback for the sprite's channel nodes
                // Check if the touched board's I/O node is touched
                // Check if one of the objects is touched
                if (Geometry.calculateDistance(touchStart[pointerId], machineSprite.getPosition()) < 80) {
                    Log.v("MapView", "\tSource board touched.");

                    // <TOUCH_ACTION>
                    TouchArticulation touchArticulation = new TouchArticulation(touch[pointerId], TouchArticulation.TouchInteractionType.TAP);
                    // TODO: propagate RELEASE before TAP
                    machineSprite.touch(touchArticulation);
                    // </TOUCH_ACTION>

                    // No touch on board or port. Touch is on map. So hide ports.
                    for (SystemSprite systemSprite2 : this.systemSprites) {
                        for (MachineSprite machineSprite2 : systemSprite2.getMachineSprites()) {
                            machineSprite2.hidePorts();
//                            this.setScale(1.0f);
                            machineSprite2.hidePaths();
                            machineSprite2.setTransparency(0.1f);
                        }
                    }
                    machineSprite.showPorts();
                    this.setScale(0.8f);
                    machineSprite.showPaths();
                    machineSprite.setTransparency(1.0f);
                    ApplicationView.getApplicationView().speakPhrase("choose a channel to get data.");

//                    isInteractionInProgress = true;

                    isPanningEnabled = false;
                }

            } else if (touchedSprite[pointerId] instanceof PortSprite) {
                PortSprite portSprite = (PortSprite) touchedSprite[pointerId];
                TouchArticulation touchArticulation = new TouchArticulation(touch[pointerId], TouchArticulation.TouchInteractionType.RELEASE);
                portSprite.touch(touchArticulation);


                // Show ports of nearby machines
                for (SystemSprite systemSprite: this.systemSprites) {
                    for (MachineSprite nearbyMachineSprite: systemSprite.getMachineSprites()) {

                        // Update style of nearby machines
                        float distanceToMachineSprite = (float) Geometry.calculateDistance(
                                touch[pointerId],
                                nearbyMachineSprite.getPosition()
                        );
                        Log.v("DistanceToSprite", "distanceToMachineSprite: " + distanceToMachineSprite);
                        if (distanceToMachineSprite < nearbyMachineSprite.boardWidth + 50) {
//                            nearbyMachineSprite.setTransparency(1.0f);
//                            nearbyMachineSprite.showPorts();



                            // TODO: use overlappedSprite instanceof PortSprite



                            for (PortSprite nearbyPortSprite: nearbyMachineSprite.portSprites) {
                                // Scaffold interaction to connect path to with nearby ports
                                float distanceToNearbyPortSprite = (float) Geometry.calculateDistance(
                                        touch[pointerId],
                                        nearbyPortSprite.getPosition()
                                );
                                if (nearbyPortSprite != portSprite) {
                                    if (distanceToNearbyPortSprite < nearbyPortSprite.shapeRadius + 20) {
                                        portSprite.setPosition(touch[pointerId]);

                                        portSprite.portDirection = PortSprite.PortDirection.INPUT;
                                        if (portSprite.portType == PortSprite.PortType.NONE) {
                                            portSprite.portType = PortSprite.PortType.getNextType(portSprite.portType); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length
                                        }

                                        nearbyPortSprite.portDirection = PortSprite.PortDirection.OUTPUT;
                                        nearbyPortSprite.portType = PortSprite.PortType.getNextType(nearbyPortSprite.portType);

                                        // Create and add path to port
                                        PathSprite pathSprite = portSprite.addPath(
                                                portSprite.getMachineSprite(),
                                                portSprite,
                                                nearbyPortSprite.getMachineSprite(),
                                                nearbyPortSprite
                                        );


                                        pathSprite.showDirectedPaths = true;
                                        pathSprite.showPathDocks = false;




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

//                            else if (distanceToMachineSprite < nearbyMachineSprite.boardWidth + 80) {
//                                nearbyMachineSprite.setTransparency(0.5f);
//                            } else {
//                                nearbyMachineSprite.setTransparency(0.1f);
//                                nearbyMachineSprite.hidePorts();
//                            }
                    }
                }

            } else if (touchedSprite[pointerId] instanceof PathSprite) {
                PathSprite pathSprite = (PathSprite) touchedSprite[pointerId];

                if (pathSprite.getEditorVisibility()) {
                    pathSprite.setEditorVisibility(false);
                } else {
                    pathSprite.setEditorVisibility(true);
                }

            } else {
                if (touchedSprite[pointerId] == null) {
                    // No touch on board or port. Touch is on map. So hide ports.
                    for (SystemSprite systemSprite : this.systemSprites) {
                        for (MachineSprite machineSprite : systemSprite.getMachineSprites()) {
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
            isPanningEnabled = true;

        }

        // Stop touching sprite
        // Style. Reset the style of touched boards.
        if (isTouching[pointerId] || touchedSprite[pointerId] != null) {
            isTouching[pointerId] = false;
            if (touchedSprite[pointerId] instanceof MachineSprite) {
                MachineSprite machineSprite = (MachineSprite) touchedSprite[pointerId];

                machineSprite.showHighlights = false;
                machineSprite.setScale(1.0f);
                touchedSprite[pointerId] = null;
            }
        }

        // Stop dragging
        this.isDragging[pointerId] = false;
    }

    private void onTapCallback (int pointerId) {

        if (touchedSprite[pointerId] instanceof MachineSprite) {
            MachineSprite machineSprite = (MachineSprite) touchedSprite[pointerId];


            // TODO: Add this to an onTouch callback for the sprite's channel nodes
            // Check if the touched board's I/O node is touched
            // Check if one of the objects is touched
            if (machineSprite.isTouching(touch[pointerId])) {
                Log.v("MapView", "\tTouched machine.");

                // <TOUCH_ACTION>
                TouchArticulation touchArticulation = new TouchArticulation(touch[pointerId], TouchArticulation.TouchInteractionType.TAP);
                // TODO: propagate RELEASE before TAP
                machineSprite.touch(touchArticulation);
                // </TOUCH_ACTION>

                // Remove focus from other machines.
                for (SystemSprite systemSprite: this.systemSprites) {
                    for (MachineSprite otherMachineSprite: systemSprite.getMachineSprites()) {
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

                isPanningEnabled = false;
            }


        } else if (touchedSprite[pointerId] instanceof PortSprite) {
            PortSprite portSprite = (PortSprite) touchedSprite[pointerId];

            if (portSprite.isTouching(touch[pointerId])) {
                TouchArticulation touchArticulation = new TouchArticulation(touch[pointerId], TouchArticulation.TouchInteractionType.TAP);
                portSprite.touch(touchArticulation);

                Log.v("MapView", "\tSource port " + (portSprite.getIndex() + 1) + " touched.");

                if (portSprite.getType() == PortSprite.PortType.NONE) {

                    portSprite.portType = PortSprite.PortType.getNextType(portSprite.getType());

                    ApplicationView.getApplicationView().speakPhrase("setting as input. you can send the data to another board if you want. touch another board.");

                } else {

                    // TODO: Replace with state of perspective. i.e., Check if seeing a single path.
                    if (portSprite.pathSprites.size() == 0) {

                        PortSprite.PortType nextPortType = portSprite.getType();
                        while ((nextPortType == PortSprite.PortType.NONE)
                                || (nextPortType == portSprite.getType())) {
                            nextPortType = PortSprite.PortType.getNextType(nextPortType);
                        }
                        portSprite.setPortType(nextPortType);

                    } else {

                        if (portSprite.hasVisiblePaths()) {

                            // TODO: Replace with state of perspective. i.e., Check if seeing a single path.
                            ArrayList<PathSprite> visiblePathSprites = portSprite.getVisiblePaths();
                            if (visiblePathSprites.size() == 1) {

                                PortSprite.PortType nextPortType = portSprite.portType;
                                while ((nextPortType == PortSprite.PortType.NONE)
                                        || (nextPortType == portSprite.getType())) {
                                    nextPortType = PortSprite.PortType.getNextType(nextPortType);
                                }
                                portSprite.setPortType(nextPortType);

                            }

                        } else {

                            // TODO: If second press, change the channel.

                            // Remove focus from other machines and their ports.
                            for (SystemSprite systemSprite : this.systemSprites) {
                                for (MachineSprite machineSprite : systemSprite.getMachineSprites()) {
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

        } else if (touchedSprite[pointerId] instanceof PathSprite) {
            PathSprite pathSprite = (PathSprite) touchedSprite[pointerId];

            if (pathSprite.getEditorVisibility()) {
                pathSprite.setEditorVisibility(false);
            } else {
                pathSprite.setEditorVisibility(true);
            }

        } else {
            if (touchedSprite[pointerId] == null) {
                // No touch on board or port. Touch is on map. So hide ports.
                for (SystemSprite systemSprite : this.systemSprites) {
                    for (MachineSprite machineSprite : systemSprite.getMachineSprites()) {
                        machineSprite.hidePorts();
                        machineSprite.setScale(1.0f);
                        machineSprite.hidePaths();
                        machineSprite.setTransparency(1.0f);
                    }
                }
                this.setScale(1.0f);
            }
        }

    }

    private void onDoubleTapCallback (int pointerId) {

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
