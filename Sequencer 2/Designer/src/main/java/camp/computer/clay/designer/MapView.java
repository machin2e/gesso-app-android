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

import camp.computer.clay.model.TouchInteraction;
import camp.computer.clay.sprite.MachineSprite;
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

    private void drawSprite(Sprite sprite) {
        sprite.draw(this);
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
    public float scale = DEFAULT_SCALE_FACTOR;
    private int scaleDuration = DEFAULT_SCALE_DURATION;

    public void setScale (float targetScale) {
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

        // Draw the background
        this.canvas.drawColor (Color.WHITE);

        // Scene
        drawScene(this);

        // Paint the bitmap to the "primary" canvas.
        canvas.drawBitmap (canvasBitmap, identityMatrix, null);

        this.canvas.restore();
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

    private void drawScene (MapView mapView) {
        // drawTitle();

        for (SystemSprite systemSprite : systemSprites) {
            drawSprite(systemSprite);
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
    private boolean isPanningDisabled = false;

    // TODO: In the queue, store the touch actions persistently after exceeding maximum number for immediate interactions.
    //private ArrayList<TouchInteraction> touchActionHistory = new ArrayList<TouchInteraction>();
    private ArrayList<TouchInteraction> touchInteraction = new ArrayList<TouchInteraction>();
    // Touch Interaction is a sequence of actions
    // classifyOngoingInteraction --> make callback
    // classifyCompleteInteraction --> make callback
    private Sprite overlappedSprite = null;

    // Gesture Envelope for Making a Wireless Channel
    // Gestural language. Grammar for the gestures composing it. Think of these as templates for
    // gestures that Clay attempts to evaluate and cleans up after, following each touch action.
    private Sprite touchSourceSprite = null;
    private int sourceChannelScopeIndex = -1;
    private Sprite touchDestinationSprite = null;
    private int destinationChannelScopeIndex = -1;

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

        // TODO: Encapsulate TouchInteraction in TouchEvent
        TouchInteraction touchAction = new TouchInteraction(touch[pointerId], TouchInteraction.TouchInteractionType.TOUCH);
        touchInteraction.add(touchAction);

        // Previous
        isTouchingPrevious[pointerId] = isTouching[pointerId]; // (or) isTouchingPrevious[pointerId] = false;
        touchPrevious[pointerId].x = touch[pointerId].x;
        touchPrevious[pointerId].y = touch[pointerId].y;
        touchPreviousTime[pointerId] = java.lang.System.currentTimeMillis ();

        // Current
        isTouching[pointerId] = true;

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
                    if (machineSprite.isTouching(touchStart[pointerId])) {

                        // <TOUCH_ACTION>
                        TouchInteraction touchInteraction = new TouchInteraction(touch[pointerId], TouchInteraction.TouchInteractionType.TOUCH);
                        machineSprite.touch(touchInteraction);
                        // </TOUCH_ACTION>

                        // TODO: Add this to an onTouch callback for the sprite's channel nodes
                        // TODO: i.e., callback Sprite.onTouch (via Sprite.touch())

                        this.isTouchingSprite[pointerId] = true;
                        this.touchedSprite[pointerId] = machineSprite;

                        isPanningDisabled = true;

                        // Break to limit the number of objects that can be touch by a finger to one (1:1 finger:touch relationship).
                        break;

                    }

                    for (PortSprite portSprite: machineSprite.portSprites) {
                        if (portSprite.isTouching(touchStart[pointerId])) {
                            Log.v ("PortTouch", "start touch on port " + portSprite);

                            // <TOUCH_ACTION>
                            TouchInteraction touchInteraction = new TouchInteraction(touch[pointerId], TouchInteraction.TouchInteractionType.TOUCH);
                            portSprite.touch(touchInteraction);
                            // </TOUCH_ACTION>

                            this.isTouchingSprite[pointerId] = true;
                            this.touchedSprite[pointerId] = portSprite;

                            isPanningDisabled = true;

                            break;
                        }
                    }
                }
            }

            // Touch the canvas
            if (this.touchedSprite[pointerId] == null) {
                this.isTouchingSprite[pointerId] = false;
//                this.isPanningDisabled = false;
            }

            // Start timer to check for hold
            timerHandler.removeCallbacks(timerRunnable);
            timerHandler.postDelayed(timerRunnable, MINIMUM_HOLD_DURATION);
        }
    }

    private void onTapCallback (int pointerId) {

    }

    private void onDoubleTapCallback (int pointerId) {

    }

    private void onHoldCallback (int pointerId) {
        Log.v("MapViewEvent", "onHoldCallback");

        if (dragDistance[pointerId] < this.MINIMUM_DRAG_DISTANCE) {
            // Holding but not (yet) dragging.

            /*
            // Disable panning
            isPanningDisabled = false;

            // Hide scopes
            if (sourceChannelScopeIndex == -1) {
                for (SystemSprite systemSprite : this.systemSprites) {
                    for (MachineSprite machineSprite : systemSprite.getMachineSprites()) {
                        machineSprite.hidePorts();
                        machineSprite.hidePaths();
                    }
                }
                this.setScale(1.0f);
            }
            */

            // Show scope for source board
            if (touchedSprite[pointerId] != null) {
                if (touchedSprite[pointerId] instanceof MachineSprite) {
                    MachineSprite machineSprite = (MachineSprite) touchedSprite[pointerId];
                    TouchInteraction touchInteraction = new TouchInteraction(touch[pointerId], TouchInteraction.TouchInteractionType.HOLD);
                    machineSprite.touch(touchInteraction);

                    //machineSprite.showPorts();
                    //machineSprite.showPaths();
                    //touchSourceSprite = machineSprite;
                    this.setScale(0.8f);
                } else if (touchedSprite[pointerId] instanceof PortSprite) {
                    PortSprite portSprite = (PortSprite) touchedSprite[pointerId];
                    TouchInteraction touchInteraction = new TouchInteraction(touch[pointerId], TouchInteraction.TouchInteractionType.HOLD);
                    portSprite.touch(touchInteraction);

//                    portSprite.showPorts();
//                    portSprite.showPaths();
                    this.setScale(0.8f);
                    touchSourceSprite = portSprite;
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

        // TODO: Encapsulate TouchInteraction in TouchEvent
        TouchInteraction touchAction = new TouchInteraction(touch[pointerId], TouchInteraction.TouchInteractionType.PRE_DRAG);
        touchInteraction.add(touchAction);

    }

    private void onDragCallback (int pointerId) {
        //Log.v("MapViewEvent", "onDragCallback");

        // TODO: Encapsulate TouchInteraction in TouchEvent
        TouchInteraction touchAction = new TouchInteraction(touch[pointerId], TouchInteraction.TouchInteractionType.DRAG);
        touchInteraction.add(touchAction);

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
                    TouchInteraction touchAction = new TouchInteraction(TouchInteraction.TouchInteractionType.DRAG);
                    machineSprite.touch(touchAction);
                        machineSprite.showHighlights = true;
                        machineSprite.setPosition(touch[pointerId].x, touch[pointerId].y);
                    */
                } else if (touchedSprite[pointerId] instanceof PortSprite) {
                    PortSprite portSprite = (PortSprite) touchedSprite[pointerId];
                    TouchInteraction touchInteraction = new TouchInteraction(touch[pointerId], TouchInteraction.TouchInteractionType.DRAG);
                    portSprite.touch(touchInteraction);
                }
            } else {
                if (!isPanningDisabled) {
                    currentPosition.offset((int) (touch[pointerId].x - touchStart[pointerId].x), (int) (touch[pointerId].y - touchStart[pointerId].y));
                }
            }

        } else {

            // TODO: Put into callback
            //if (this.isTouchingSprite[pointerId]) {
            if (touchedSprite[pointerId] != null) {
                if (touchedSprite[pointerId] instanceof MachineSprite) {
                    MachineSprite machineSprite = (MachineSprite) touchedSprite[pointerId];
                    TouchInteraction touchInteraction = new TouchInteraction(touch[pointerId], TouchInteraction.TouchInteractionType.DRAG);
                    machineSprite.touch(touchInteraction);
                        machineSprite.showHighlights = true;
                        machineSprite.setPosition(touch[pointerId].x, touch[pointerId].y);
                } else if (touchedSprite[pointerId] instanceof PortSprite) {
                    PortSprite portSprite = (PortSprite) touchedSprite[pointerId];
                    TouchInteraction touchInteraction = new TouchInteraction(touch[pointerId], TouchInteraction.TouchInteractionType.DRAG);
                    portSprite.touch(touchInteraction);



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
                if (!isPanningDisabled) {
                    currentPosition.offset((int) (touch[pointerId].x - touchStart[pointerId].x), (int) (touch[pointerId].y - touchStart[pointerId].y));
                }
            }

//            // Holding and dragging.
//
//            if (this.isTouchingSprite[pointerId]) {
//                if (touchedSprite[pointerId] instanceof MachineSprite) {
//
//                    // TODO: Check if (1) drag through a channel connector node, then if (2) drag to another board, then (3) set up communication channel (or abandon if not all steps done)
//                    if (touchSourceSprite != null) {
//
//                        if (touchSourceSprite instanceof MachineSprite) {
//                            MachineSprite machineSprite3 = (MachineSprite) touchSourceSprite;
//
//                            // Start touch on a channel scope
//                            if (sourceChannelScopeIndex == -1) {
//
//                                if (touchSourceSprite != null) {
//                                    // If no channel source has been touched yet, check if one is dragged over.
//                                    // TODO: Add this to an onTouch callback for the sprite's channel nodes
//
//                                    // TODO: Move this into the following condition for PortSprite
//                                    // Check if the touched board's I/O node is touched
//                                    for (int i = 0; i < machineSprite3.getChannelCount(); i++) {
//                                        if (machineSprite3.portSprites.get(i).showFormLayer) {
//                                            // Check if one of the objects is touched
//                                            if (Geometry.calculateDistance(touch[pointerId], machineSprite3.getPortSprite(i).getPosition()) < 60) {
//
//                                                // <TOUCH_ACTION>
//                                                TouchInteraction touchAction = new TouchInteraction(TouchInteraction.TouchInteractionType.DRAG);
//                                                machineSprite3.getPortSprite(i).touch(touchAction);
//                                                // </TOUCH_ACTION>
//
//                                                Log.v("MapViewTouch", "touched node " + (i + 1));
//                                                sourceChannelScopeIndex = i;
//                                                machineSprite3.portSprites.get(i).portType = PortSprite.PortType.getNextType(machineSprite3.portSprites.get(i).portType); // (boardSprite.channelTypes.get(i) + 1) % boardSprite.channelTypeColors.length
//                                            }
//                                        }
//                                    }
//                                }
//
//                            } else if (touchDestinationSprite == null) {
//
//                                if (sourceChannelScopeIndex >= 0) {
//                                    Log.v("MapViewTouch", "\tLooking for destination");
//
//                                    if (touchSourceSprite instanceof MachineSprite) {
//                                        MachineSprite machineSprite2 = (MachineSprite) touchDestinationSprite;
//                                        // Check if a board was touched
//                                        for (SystemSprite systemSprite : this.systemSprites) {
//                                            for (MachineSprite machineSprite : systemSprite.getMachineSprites()) {
//                                                // Log.v ("MapViewTouch", "Object at " + machineSprite.x + ", " + machineSprite.y);
//
//                                                // TODO: Add this to an isTouch? function of the sprite object
//                                                // Check if one of the objects is touched
//                                                if (Geometry.calculateDistance(touch[pointerId], machineSprite.getPosition()) < (machineSprite.boardWidth / 3.0f)) {
//
//                                                    // <TOUCH_ACTION>
//                                                    TouchInteraction touchAction = new TouchInteraction(TouchInteraction.TouchInteractionType.DRAG);
//                                                    machineSprite.touch(touchAction);
//                                                    // </TOUCH_ACTION>
//
//                                                    // TODO: Add this to an onTouch callback for the sprite's channel nodes
//
//                                                    Log.v("MapViewTouch", "\tTouching object at " + machineSprite.getPosition().x + ", " + machineSprite.getPosition().y);
//                                                    //this.isTouchingSprite[pointerId] = true;
//                                                    machineSprite2 = machineSprite;
//
//                                                    machineSprite2.showPorts();
//                                                    this.setScale(0.8f);
//
//                                                    // TODO: Callback: call Sprite.onTouchDestination (via Sprite.touch())
//                                                }
//                                            }
//                                        }
//                                    } else if (touchedSprite[pointerId] instanceof PortSprite) {
//                                        PortSprite portSprite = (PortSprite) touchedSprite[pointerId];
//                                    }
//                                }
//                            } else if (destinationChannelScopeIndex == -1) {
//                                Log.v("MapViewTouch", "\tLooking for destination SCOPE");
//                                if (touchDestinationSprite != null) {
//                                    // If no channel source has been touched yet, check if one is dragged over.
//
//                                    if (touchSourceSprite instanceof MachineSprite) {
//                                        MachineSprite machineSprite2 = (MachineSprite) touchDestinationSprite;
//                                        // TODO: Add this to an onTouch callback for the sprite's channel nodes
//                                        // Check if the touched board's I/O node is touched
//                                        for (int i = 0; i < machineSprite2.getChannelCount(); i++) {
//                                            if (machineSprite2.portSprites.get(i).showFormLayer) {
//                                                // Check if one of the objects is touched
//                                                if (Geometry.calculateDistance(touch[pointerId], machineSprite2.portSprites.get(i).getPosition()) < 60.0f) {
//
//                                                    // <TOUCH_ACTION>
//                                                    TouchInteraction touchAction = new TouchInteraction(TouchInteraction.TouchInteractionType.DRAG);
//                                                    machineSprite2.getPortSprite(i).touch(touchAction);
//                                                    // </TOUCH_ACTION>
//
//                                                    Log.v("MapViewTouch", "touched node " + (i + 1));
//                                                    destinationChannelScopeIndex = i;
//                                                    machineSprite2.getPortSprite(i).portType = PortSprite.PortType.getNextType(machineSprite2.portSprites.get(i).portType); // (boardSprite.channelTypes.get(i) + 1) % boardSprite.channelTypeColors.length
//                                                }
//                                            }
//
//                                        }
//                                    } else if (touchedSprite[pointerId] instanceof PortSprite) {
//                                        PortSprite portSprite = (PortSprite) touchedSprite[pointerId];
//                                    }
//                                }
//                            }
//
//                        } else if (touchedSprite[pointerId] instanceof PortSprite) {
//                            PortSprite portSprite = (PortSprite) touchedSprite[pointerId];
//                        }
//
//                    } else {
//
//                        // TODO: Put into callback
//                        if (this.isTouchingSprite[pointerId]) {
//                            if (touchSourceSprite instanceof MachineSprite) {
//                                MachineSprite machineSprite = (MachineSprite) touchedSprite[pointerId];
//                                machineSprite.showHighlights = true;
//                                machineSprite.setPosition(touch[pointerId].x, touch[pointerId].y);
//                            } else if (touchedSprite[pointerId] instanceof PortSprite) {
//                                PortSprite portSprite = (PortSprite) touchedSprite[pointerId];
//                            }
//
//                        } else if (!isPanningDisabled) {
//                            currentPosition.offset((int) (touch[pointerId].x - touchStart[pointerId].x), (int) (touch[pointerId].y - touchStart[pointerId].y));
//                        }
//                    }
//                }
//            }
        }
    }

    private void onReleaseCallback (int pointerId) {
        Log.v("MapViewEvent", "onReleaseCallback");

        // TODO: Encapsulate TouchInteraction in TouchEvent
        TouchInteraction touchAction = new TouchInteraction(touch[pointerId], TouchInteraction.TouchInteractionType.RELEASE);
        touchInteraction.add(touchAction);
        // TODO: resolveInteraction
        touchInteraction.clear();

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

        boolean isInteractionInProgress = false;

        // Classify/Callbacks
        if (touchStopTime - touchStartTime < MAXIMUM_TAP_DURATION) {

            // Step 1: Touch source board
            if (/*touchSourceSprite == null && */ sourceChannelScopeIndex == -1
                    && touchDestinationSprite == null && destinationChannelScopeIndex == -1) {
                Log.v("MapView", "Looking for source board touch.");

                // Hide channel scopes (unless dragging)

                for (SystemSprite systemSprite : this.systemSprites) {
                    for (MachineSprite machineSprite : systemSprite.getMachineSprites()) {

                        // TODO: Add this to an onTouch callback for the sprite's channel nodes
                        // Check if the touched board's I/O node is touched
                        // Check if one of the objects is touched
                        if (Geometry.calculateDistance(touchStart[pointerId], machineSprite.getPosition()) < 80) {
                            Log.v("MapView", "\tSource board touched.");

                            // <TOUCH_ACTION>
                            TouchInteraction touchInteraction = new TouchInteraction(touch[pointerId], TouchInteraction.TouchInteractionType.TAP);
                            // TODO: propagate RELEASE before TAP
                            machineSprite.touch(touchInteraction);
                            // </TOUCH_ACTION>

                            touchSourceSprite = machineSprite;

                            // No touch on board or scope. Touch is on map. So hide scopes.
                            for (SystemSprite systemSprite2 : this.systemSprites) {
                                for (MachineSprite machineSprite2 : systemSprite2.getMachineSprites()) {
                                    machineSprite2.hidePorts();
                                    this.setScale(1.0f);
                                    machineSprite2.hidePaths();
                                    machineSprite2.setTransparency(0.1f);
                                }
                            }
                            machineSprite.showPorts();
                            this.setScale(0.8f);
                            machineSprite.showPaths();
                            machineSprite.setTransparency(1.0f);
                            ApplicationView.getApplicationView().speakPhrase("choose a channel to get data.");

                            isInteractionInProgress = true;

                            isPanningDisabled = true;

                            break;
                        }
                    }
                }
            }

            // Step 2: Touch source channel scope
            if (!isInteractionInProgress) {
                if (touchSourceSprite != null /* && sourceChannelScopeIndex == -1 */
                        && touchDestinationSprite == null && destinationChannelScopeIndex == -1) {
                    Log.v("MapView", "Looking for source channel scope touch.");

                    if (touchSourceSprite instanceof MachineSprite) {
                        MachineSprite touchSourceMachineSprite = (MachineSprite) touchSourceSprite;

                        if (!isDragging[pointerId]) {

                            for (SystemSprite systemSprite : this.systemSprites) {
                                for (MachineSprite machineSprite : systemSprite.getMachineSprites()) {

                                    // Check if the touched board's chanenl scope is touched
                                    for (int scopeIndex = 0; scopeIndex < machineSprite.getChannelCount(); scopeIndex++) {
                                        // Check if one of the objects is touched
                                        // TODO: Create BoardChannelSprite.isTouching()
                                        if (Geometry.calculateDistance(touchStart[pointerId], machineSprite.portSprites.get(scopeIndex).getPosition()) < 80) {

                                            // <TOUCH_ACTION>
                                            // TODO: RELEASE
                                            TouchInteraction touchInteraction = new TouchInteraction(touch[pointerId], TouchInteraction.TouchInteractionType.TAP);
                                            machineSprite.portSprites.get(scopeIndex).touch(touchInteraction);
                                            // </TOUCH_ACTION>

                                            if (machineSprite == touchSourceSprite) {

                                                if (sourceChannelScopeIndex == -1) {

                                                    // First touch on the source channel scope

                                                    if (machineSprite.portSprites.get(scopeIndex).portType == PortSprite.PortType.NONE) {

                                                        Log.v("MapView", "\tSource channel scope " + (scopeIndex + 1) + " touched.");
                                                        sourceChannelScopeIndex = scopeIndex;
                                                        machineSprite.portSprites.get(scopeIndex).portType = PortSprite.PortType.getNextType(machineSprite.portSprites.get(scopeIndex).portType); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length

                                                        ApplicationView.getApplicationView().speakPhrase("setting as input. you can send the data to another board if you want. touch another board.");

                                                        isInteractionInProgress = true;

                                                        break;

                                                    } else {

                                                        // TODO: If second press, change the channel.

                                                        Log.v("MapView", "\tSource channel scope " + (scopeIndex + 1) + " touched.");

                                                        for (SystemSprite systemSprite2 : this.systemSprites) {
                                                            for (MachineSprite machineSprite2 : systemSprite2.getMachineSprites()) {
                                                                machineSprite2.hidePorts();
                                                                this.setScale(1.0f);
                                                                machineSprite2.hidePaths();
                                                            }
                                                        }
                                                        machineSprite.showPort(scopeIndex);
                                                        machineSprite.showPath(scopeIndex, true);

                                                        ApplicationView.getApplicationView().speakPhrase("setting as input. you can send the data to another board if you want. touch another board.");

                                                        isInteractionInProgress = true;

                                                        break;

                                                    }

                                                } else {

                                                    // TODO: Create BoardChannelSprite.isTouching()
                                                    if (sourceChannelScopeIndex == scopeIndex) {
                                                        // Touched already-selected channel scope (for some number repetitions greater than 1, or after the first)
                                                        Log.v("MapView", "\tSame source channel scope " + (scopeIndex + 1) + " touched.");
                                                        // sourceChannelScopeIndex = i; // No need to re-select the scope
                                                        machineSprite.portSprites.get(scopeIndex).portType = PortSprite.PortType.getNextType(machineSprite.portSprites.get(scopeIndex).portType); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length

                                                        // Narrate
                                                        // ApplicationView.getApplicationView().speakPhrase("setting as input. you can send the data to another board if you want. touch another board.");

                                                        // TODO: Offer options and propose ways to proceed.

                                                        isInteractionInProgress = true;

                                                        break;
                                                    } else {
                                                        //Touched a different node, so update the source...

                                                        // Touched already-selected channel scope (for some number repetitions greater than 1, or after the first)
                                                        touchSourceMachineSprite.portSprites.get(sourceChannelScopeIndex).portType = PortSprite.PortType.NONE; // TODO: Revert to previous type, if there is a previous type. MachineSprite.ChannelType.getNextType(machineSprite.channelTypes.get(i))

                                                        // Select the just-touched scope as the source.
                                                        sourceChannelScopeIndex = scopeIndex;
                                                        machineSprite.portSprites.get(scopeIndex).portType = PortSprite.PortType.getNextType(machineSprite.portSprites.get(scopeIndex).portType); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length
                                                        Log.v("MapView", "\tDifferent source channel scope " + (scopeIndex + 1) + " touched.");

                                                        // Narrate
                                                        // ApplicationView.getApplicationView().speakPhrase("setting as input. you can send the data to another board if you want. touch another board.");

                                                        // TODO: Offer options and propose ways to proceed.

                                                        isInteractionInProgress = true;

                                                        break;
                                                    }
                                                }

                                            } else {

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } else if (touchedSprite[pointerId] instanceof PortSprite) {
                        PortSprite portSprite = (PortSprite) touchedSprite[pointerId];
                    }
                }
            }

            // Step 3: Touch destination board
            if (!isInteractionInProgress) {
                if (touchSourceSprite != null && sourceChannelScopeIndex != -1
                        && touchDestinationSprite == null && destinationChannelScopeIndex == -1) {
                    Log.v("MapView", "Looking for destination board touch.");

                    // Hide channel scopes (unless dragging)
                    if (!isDragging[pointerId]) {

                        for (SystemSprite systemSprite : this.systemSprites) {
                            for (MachineSprite machineSprite : systemSprite.getMachineSprites()) {

                                // Check if one of the objects is touched
                                if (Geometry.calculateDistance(touchStart[pointerId], machineSprite.getPosition()) < 80) {

                                    // <TOUCH_ACTION>
                                    // TODO: RELEASE
                                    TouchInteraction touchInteraction = new TouchInteraction(touch[pointerId], TouchInteraction.TouchInteractionType.TAP);
                                    machineSprite.touch(touchInteraction);
                                    // </TOUCH_ACTION>

                                    Log.v("MapView", "\tDestination board touched.");
                                    touchDestinationSprite = machineSprite;
                                    machineSprite.showPorts();
                                    this.setScale(0.8f);

                                    ApplicationView.getApplicationView().speakPhrase("that board will be the destination. now choose the output channel.");

                                    isInteractionInProgress = true;

                                    break;
                                }

                            }
                        }

                    }

                }
            }

            // Step 4: Touch destination channel scope
            if (!isInteractionInProgress) {
                if (touchSourceSprite != null && sourceChannelScopeIndex != -1
                        && touchDestinationSprite != null && destinationChannelScopeIndex == -1) {
                    Log.v("MapView", "Looking for destination channel scope touch.");

                    if (touchSourceSprite instanceof MachineSprite && touchDestinationSprite instanceof MachineSprite) {
                        MachineSprite touchSourceMachineSprite = (MachineSprite) touchSourceSprite;
                        MachineSprite touchDestinationMachineSprite = (MachineSprite) touchDestinationSprite;

                        // Hide channel scopes (unless dragging)
                        if (!isDragging[pointerId]) {

                            for (SystemSprite systemSprite : this.systemSprites) {
                                for (MachineSprite machineSprite : systemSprite.getMachineSprites()) {

                                    // TODO: Add this to an onTouch callback for the sprite's channel nodes
                                    // Check if the touched board's I/O node is touched
                                    for (int i = 0; i < machineSprite.getChannelCount(); i++) {
                                        // Check if one of the objects is touched
                                        if (Geometry.calculateDistance(touchStart[pointerId], machineSprite.portSprites.get(i).getPosition()) < 80) {

                                            // <TOUCH_ACTION>
                                            // TODO: RELEASE
                                            TouchInteraction touchInteraction = new TouchInteraction(touch[pointerId], TouchInteraction.TouchInteractionType.TAP);
                                            machineSprite.getPortSprite(i).touch(touchInteraction);
                                            // </TOUCH_ACTION>

                                            if (machineSprite == touchDestinationSprite) {
                                                Log.v("MapView", "\tDestination channel scope " + (i + 1) + " touched.");
                                                destinationChannelScopeIndex = i;
                                                machineSprite.portSprites.get(i).portType = PortSprite.PortType.getNextType(machineSprite.portSprites.get(i).portType); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length


                                                ApplicationView.getApplicationView().speakPhrase("got it. the channel is set up. you can connect components to it now and start using them.");
                                                ApplicationView.getApplicationView().speakPhrase("do you want me to help you connect the components?"); // i.e., start interactive assembly... start by showing component browser. then choose component and get instructions for connecting it. show "okay, done" button.

                                                Log.v("MapViewLink", "Created data path.");

                                                touchSourceMachineSprite.portSprites.get(sourceChannelScopeIndex).addPath(
                                                        touchSourceMachineSprite,
                                                        sourceChannelScopeIndex,
                                                        touchDestinationMachineSprite,
                                                        destinationChannelScopeIndex
                                                );

                                                touchSourceMachineSprite.portSprites.get(sourceChannelScopeIndex).portDirection = PortSprite.PortDirection.INPUT;

                                                touchDestinationMachineSprite.portSprites.get(destinationChannelScopeIndex).portDirection = PortSprite.PortDirection.OUTPUT;

                                                // Reset connection state
                                                touchSourceSprite = null;
                                                touchDestinationSprite = null;
                                                sourceChannelScopeIndex = -1;
                                                destinationChannelScopeIndex = -1;

                                                isInteractionInProgress = true;

                                                break;
                                            }
                                        }
                                    }

                                }
                            }
                        }

                    } else if (touchedSprite[pointerId] instanceof PortSprite) {
                        PortSprite portSprite = (PortSprite) touchedSprite[pointerId];
                    }
                }
            }

        } else {

            if (isDragging[pointerId]) {

                if (touchSourceSprite instanceof MachineSprite && touchDestinationSprite instanceof MachineSprite) {
                    MachineSprite touchSourceMachineSprite = (MachineSprite) touchSourceSprite;
                    MachineSprite touchDestinationMachineSprite = (MachineSprite) touchDestinationSprite;

                    // Connection: A complete connection made.
                    if (touchSourceSprite != null && sourceChannelScopeIndex != -1
                            && touchDestinationSprite != null && destinationChannelScopeIndex != -1) {

                        Log.v("MapViewLink", "Created data path.");

                        touchSourceMachineSprite.portSprites.get(sourceChannelScopeIndex).addPath(
                                touchSourceMachineSprite,
                                sourceChannelScopeIndex,
                                touchDestinationMachineSprite,
                                destinationChannelScopeIndex
                        );

                        touchSourceMachineSprite.portSprites.get(sourceChannelScopeIndex).portDirection = PortSprite.PortDirection.INPUT;

                        touchDestinationMachineSprite.portSprites.get(destinationChannelScopeIndex).portDirection = PortSprite.PortDirection.OUTPUT;

                        // Reset connection state
                        touchSourceSprite = null;
                        touchDestinationSprite = null;
                        sourceChannelScopeIndex = -1;
                        destinationChannelScopeIndex = -1;

                    } else if (touchSourceSprite != null) {

                        Log.v("MapViewLink", "Partial data path was abandoned.");

                        // Reset selected source channel scope
                        if (sourceChannelScopeIndex != -1) {
                            touchSourceMachineSprite.portSprites.get(sourceChannelScopeIndex).portType = PortSprite.PortType.NONE;
                        }

                        // Reset selected destination channel scope
                        if (destinationChannelScopeIndex != -1) {
                            touchSourceMachineSprite.portSprites.get(destinationChannelScopeIndex).portType = PortSprite.PortType.NONE;
                        }

                        // Hide scopes.
                        for (SystemSprite systemSprite : this.systemSprites) {
                            for (MachineSprite machineSprite : systemSprite.getMachineSprites()) {
                                machineSprite.hidePorts();
                                this.setScale(1.0f);
                                machineSprite.hidePaths();
                            }
                        }

                        // Reset connection state
                        touchSourceSprite = null;
                        touchDestinationSprite = null;
                        sourceChannelScopeIndex = -1;
                        destinationChannelScopeIndex = -1;

                    }

                } else if (touchedSprite[pointerId] instanceof PortSprite) {
                    PortSprite portSprite = (PortSprite) touchedSprite[pointerId];
                }
            }
        }

        if (!isInteractionInProgress) {

            Log.v("MapViewTouch", "Partial data path was abandoned...");

            if (touchedSprite[pointerId] instanceof MachineSprite) {
                MachineSprite machineSprite = (MachineSprite) touchedSprite[pointerId];
            } else if (touchedSprite[pointerId] instanceof PortSprite) {
                PortSprite portSprite = (PortSprite) touchedSprite[pointerId];
                TouchInteraction touchInteraction = new TouchInteraction(touch[pointerId], TouchInteraction.TouchInteractionType.RELEASE);
                portSprite.touch(touchInteraction);


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
                                        // portSprite.portType = PortSprite.PortType.getNextType(portSprite.portType); // (machineSprite.channelTypes.get(i) + 1) % machineSprite.channelTypeColors.length

                                        nearbyPortSprite.portDirection = PortSprite.PortDirection.OUTPUT;
                                        nearbyPortSprite.portType = PortSprite.PortType.getNextType(nearbyPortSprite.portType);

                                        // Create and add path to port
                                        portSprite.addPath(
                                                portSprite.getMachineSprite(),
                                                portSprite.getIndex(),
                                                nearbyPortSprite.getMachineSprite(),
                                                nearbyPortSprite.getIndex()
                                        );


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
            }

            if (touchSourceSprite != null && sourceChannelScopeIndex != -1 && touchDestinationSprite != null) {
                ApplicationView.getApplicationView().speakPhrase("the channel was interrupted.");
            }

            // Reset selected destination channel scope
            if (destinationChannelScopeIndex != -1) {
                if (touchDestinationSprite instanceof MachineSprite) {
                    MachineSprite touchDestinationMachineSprite = (MachineSprite) touchDestinationSprite;

                    touchDestinationMachineSprite.getPortSprite(destinationChannelScopeIndex).portType = PortSprite.PortType.NONE;
                }
            }

            // No touch on board or scope. Touch is on map. So hide scopes.
            for (SystemSprite systemSprite : this.systemSprites) {
                for (MachineSprite machineSprite : systemSprite.getMachineSprites()) {
                    machineSprite.hidePorts();
                    machineSprite.setScale(1.0f);
                    machineSprite.hidePaths();
                    machineSprite.setTransparency(1.0f);
                }
            }

            // Reset connection state
            touchSourceSprite = null;
            touchDestinationSprite = null;
            sourceChannelScopeIndex = -1;
            destinationChannelScopeIndex = -1;

            // Reset map interactivity
            isPanningDisabled = false;

            this.setScale(1.0f);

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
