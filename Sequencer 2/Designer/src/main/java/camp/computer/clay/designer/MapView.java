package camp.computer.clay.designer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;

import camp.computer.clay.sprites.BoardSprite;

public class MapView extends SurfaceView implements SurfaceHolder.Callback {

    private MapViewRenderer mapViewRenderer;

    private SurfaceHolder surfaceHolder;

    // Canvas
    private Bitmap canvasBitmap = null;
    private Canvas mapCanvas = null;
    private int canvasWidth, canvasHeight;
    private Paint paint = new Paint (Paint.ANTI_ALIAS_FLAG);
    private Matrix identityMatrix;

    // Map
    private PointF originPosition = new PointF ();
    private PointF currentPosition = new PointF ();

    // Sprites
    private ArrayList<BoardSprite> boardSprites = new ArrayList<BoardSprite>();

    public MapView(Context context) {
        super(context);
        initializeSprites();
    }

    public MapView (Context context, AttributeSet attrs) {
        super (context, attrs);
        initializeSprites();
    }

    public MapView (Context context, AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);
        initializeSprites();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        canvasWidth = getWidth ();
        canvasHeight = getHeight();
        canvasBitmap = Bitmap.createBitmap (canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        mapCanvas = new Canvas ();
        mapCanvas.setBitmap(canvasBitmap);


        // TODO: Move setPosition to a better location!
//        getClay().getPerspective ().setPosition(mapCanvas.getWidth() / 2, mapCanvas.getHeight() / 2);
        originPosition.set(mapCanvas.getWidth() / 2.0f, mapCanvas.getHeight() / 2.0f);

        currentPosition.set(originPosition.x, originPosition.y);

        identityMatrix = new Matrix ();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void initializeSprites() {
        boardSprites.add(new BoardSprite(0, 0, 0));
        boardSprites.add(new BoardSprite(300, 400, 30));
        boardSprites.add(new BoardSprite(-200, -440, -55));
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

    void drawTitle () {

        paint.setStyle (Paint.Style.FILL);
        paint.setStrokeWidth (0);
        paint.setColor (Color.BLACK);
        paint.setTextSize (80);

        // Set style for behavior's label
        String title = "Clay";

        Rect textBounds = new Rect ();
        paint.getTextBounds (title, 0, title.length (), textBounds);
        //mapCanvas.drawText (title, 0 - (textBounds.width () / 2), 0 - (textBounds.height () / 2), paint);

        mapCanvas.drawText (title, 50, 50, paint);

    }

    void drawSquare (float xCenter, float yCenter, float angle) {

        float width = 100;
        float height = 100;

        mapCanvas.save();

        // Position
        // Reference: http://stackoverflow.com/questions/8712652/rotating-image-on-a-canvas-in-android
//        mapCanvas.translate(xCenter, yCenter);
//        mapCanvas.rotate(angle);
        mapCanvas.rotate(angle, xCenter, yCenter);

        // Fill
        paint.setStyle (Paint.Style.FILL);
        paint.setColor (Color.LTGRAY);
        mapCanvas.drawRect(xCenter - (width / 2.0f), yCenter - (height / 2.0f), xCenter + (width / 2.0f), yCenter + (height / 2.0f), paint);

        // Stroke
        paint.setStyle (Paint.Style.STROKE);
        paint.setStrokeWidth (3);
        paint.setColor (Color.BLACK);
        mapCanvas.drawRect(xCenter - (width / 2.0f), yCenter - (height / 2.0f), xCenter + (width / 2.0f), yCenter + (height / 2.0f), paint);

        mapCanvas.restore();

    }

    private void drawSprite(BoardSprite boardSprite) {
        boardSprite.draw(mapCanvas, paint);
    }

    //----------------------------------------------------------------------------------------------
    // Coordinate System
    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    // Perspective
    //----------------------------------------------------------------------------------------------

    public static float DEFAULT_SCALE_FACTOR = 1.0f;

    //private Point originPosition = new Point (0, 0);
    private float scale = DEFAULT_SCALE_FACTOR;

    //----------------------------------------------------------------------------------------------
    // Layout
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Move the perspective
        mapCanvas.save ();
        //mapCanvas.translate (originPosition.x, originPosition.y);
        mapCanvas.translate (currentPosition.x, currentPosition.y);
        mapCanvas.scale (scale, scale);
//        mapCanvas.translate (getClay ().getPerspective ().getPosition ().x, getClay ().getPerspective ().getPosition ().y);
//        mapCanvas.scale (getClay ().getPerspective ().getScaleFactor (), getClay ().getPerspective ().getScaleFactor ());

        // Draw the background
        mapCanvas.drawColor (Color.WHITE);

        // Scene
        drawScene();

        // Paint the bitmap to the "primary" canvas.
        canvas.drawBitmap (canvasBitmap, identityMatrix, null);

        mapCanvas.restore();
    }

    private void updateState () {
        for (BoardSprite boardSprite : boardSprites) {
            boardSprite.updateChannelData();
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
                    onDraw(canvas);
                }
            }
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost (canvas);
            }
        }
    }

    private void drawScene () {
        // drawTitle();

        for (BoardSprite boardSprite : boardSprites) {
            drawSprite(boardSprite);
        }
    }

    //----------------------------------------------------------------------------------------------
    // Human Interaction Model
    //----------------------------------------------------------------------------------------------

    public static int MAXIMUM_TOUCH_POINT_COUNT = 5;

    public static int MAXIMUM_TAP_DURATION = 200;
    public static int MAXIMUM_DOUBLE_TAP_DURATION = 400;
    public static int MINIMUM_HOLD_DURATION = 800;

    public static int MINIMUM_DRAG_DISTANCE = 35;

    //    private Point[] touch = new Point[MAXIMUM_TOUCH_COUNT];
    private float[] xTouch = new float[MAXIMUM_TOUCH_POINT_COUNT];
    private float[] yTouch = new float[MAXIMUM_TOUCH_POINT_COUNT];
    private long[] timeTouch = new long[MAXIMUM_TOUCH_POINT_COUNT];
    private boolean[] isTouching = new boolean[MAXIMUM_TOUCH_POINT_COUNT];
    private boolean[] isDragging = new boolean[MAXIMUM_TOUCH_POINT_COUNT];
    private double[] dragDistance = new double[MAXIMUM_TOUCH_POINT_COUNT];

    private float[] xTouchPrevious = new float[MAXIMUM_TOUCH_POINT_COUNT];
    private float[] yTouchPrevious = new float[MAXIMUM_TOUCH_POINT_COUNT];
    private long[] timeTouchPrevious = new long[MAXIMUM_TOUCH_POINT_COUNT];
    private boolean[] isTouchingPrevious = new boolean[MAXIMUM_TOUCH_POINT_COUNT];
    private boolean[] isTouchingActionPrevious = new boolean[MAXIMUM_TOUCH_POINT_COUNT];

    // Point where the touch started.
    private float[] xTouchStart = new float[MAXIMUM_TOUCH_POINT_COUNT];
    private float[] yTouchStart = new float[MAXIMUM_TOUCH_POINT_COUNT];
    private long timeTouchStart = java.lang.System.currentTimeMillis ();

    // Point where the touch ended.
    private float[] xTouchStop = new float[MAXIMUM_TOUCH_POINT_COUNT];
    private float[] yTouchStop = new float[MAXIMUM_TOUCH_POINT_COUNT];
    private long timeTouchStop = java.lang.System.currentTimeMillis ();

    // Touch state
    private boolean hasTouches = false; // i.e., At least one touch is detected.
    private int touchCount = 0; // i.e., The total number of touch points detected.
    private boolean[] isTouchingBehavior = new boolean[MAXIMUM_TOUCH_POINT_COUNT];

    // Interactivity state
    private boolean disablePanning = false;
    private BoardSprite touchedBoardSpriteSource = null;
    private int touchedChannelScopeSource = -1;
    private BoardSprite touchedBoardSpriteDestination = null;
    private int touchedChannelScopeDestination = -1;

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

            /* and here comes the "trick" */
            // timerHandler.postDelayed(this, 100);
        }
    };

    @Override
    public boolean onTouchEvent (MotionEvent motionEvent) {
         Log.v("MapViewEvent", "onTouchEvent");

        int pointerIndex = ((motionEvent.getAction () & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT);
        int pointerId = motionEvent.getPointerId (pointerIndex);
        int touchAction = (motionEvent.getAction () & MotionEvent.ACTION_MASK);
        int pointCount = motionEvent.getPointerCount ();

        if (pointCount <= MAXIMUM_TOUCH_POINT_COUNT) {
            if (pointerIndex <= MAXIMUM_TOUCH_POINT_COUNT - 1) {

                // Current
                // Update touch state based the points given by the host OS (e.g., Android).
                for (int i = 0; i < pointCount; i++) {
                    int id = motionEvent.getPointerId (i);
                    xTouch[id] = (motionEvent.getX (i) - currentPosition.x) / scale;
                    yTouch[id] = (motionEvent.getY (i) - currentPosition.y) / scale;
                    timeTouch[id] = java.lang.System.currentTimeMillis ();

//                    xTouch[id] = (motionEvent.getX (i) - currentPosition.x) / scale + mapCanvas.getClipBounds().left;
//                    yTouch[id] = (motionEvent.getY (i) - currentPosition.y) / scale + mapCanvas.getClipBounds().top;
                }

                // Update the state of the touched object based on the current touch interaction state.
                if (touchAction == MotionEvent.ACTION_DOWN) {

                    // Previous
                    isTouchingPrevious[pointerId] = isTouching[pointerId]; // (or) isTouchingPrevious[pointerId] = false;
                    xTouchPrevious[pointerId] = xTouch[pointerId];
                    yTouchPrevious[pointerId] = yTouch[pointerId];
                    timeTouchPrevious[pointerId] = java.lang.System.currentTimeMillis ();

                    // Current
                    isTouching[pointerId] = true;

                    // First
                    if (this.isTouching[pointerId] == true && this.isTouchingPrevious[pointerId] == false) {

                        // Set the first point of touch
                        this.xTouchStart[pointerId] = this.xTouch[pointerId];
                        this.yTouchStart[pointerId] = this.yTouch[pointerId];
                        this.timeTouchStart = java.lang.System.currentTimeMillis ();

                        // Reset dragging state
                        this.isDragging[pointerId] = false;
                        this.dragDistance[pointerId] = 0;

                        // Reset object interaction state
                        boolean touchedSprite = false;
                        for (BoardSprite boardSprite : boardSprites) {
                            // Log.v ("MapViewTouch", "Object at " + boardSprite.x + ", " + boardSprite.y);

                            // TODO: Add this to an isTouch? function of the sprite object
                            // Check if one of the objects is touched
                            if (getDistance((int) boardSprite.getPosition().x, (int) boardSprite.getPosition().y, (int) xTouchStart[pointerId], (int) yTouchStart[pointerId]) < (boardSprite.boardWidth / 2.0f)) {

                                // TODO: Add this to an onTouch callback for the sprite's channel nodes

                                // Log.v ("MapViewTouch", "\tTouching object at " + boardSprite.x + ", " + boardSprite.y);
                                this.isTouchingBehavior[pointerId] = true;
                                touchedBoardSpriteSource = boardSprite;

                                touchedSprite = true;

                                // TODO: Callback: call Sprite.onTouch (via Sprite.touch())

                                disablePanning = true;

                                // Break to limit the number of objects that can be touch by a finger to one (1:1 finger:touch relationship).
                                break;

                            }

                            // Start touch on a channel scope
                            if (boardSprite.showChannelScopes) {

                                if (touchedChannelScopeSource == -1) {

                                    /*
                                    // TODO: Add this to an onTouch callback for the sprite's channel nodes
                                    // Check if the touched board's I/O node is touched
                                    for (int i = 0; i < boardSprite.channelScopePositions.size(); i++) {
                                        PointF channelNodePoint = boardSprite.channelScopePositions.get(i);
                                        // Check if one of the objects is touched
                                        if (getDistance((int) channelNodePoint.x, (int) channelNodePoint.y, (int) xTouchStart[pointerId], (int) yTouchStart[pointerId]) < 60) {
                                            Log.v("MapViewTouch", "touched node " + (i + 1));
                                            touchedChannelScopeSource = i;
                                            boardSprite.channelTypes.set(
                                                    i,
                                                    BoardSprite.ChannelType.getNextType(boardSprite.channelTypes.get(i)) // (boardSprite.channelTypes.get(i) + 1) % boardSprite.channelTypeColors.length
                                            );
                                        }
                                    }
                                    */

                                }

                            }

                        }

//                        if (touchedSprite == false) {
//                            if ()
//                            touchedBoardSpriteSource = null;
//                        }

                        // Touch the canvas
                        if (touchedBoardSpriteSource == null) {
                            this.isTouchingBehavior[pointerId] = false;
                            disablePanning = false;
                        }

                        // Start timer to check for hold
                        timerHandler.removeCallbacks(timerRunnable);
                        timerHandler.postDelayed(timerRunnable, MINIMUM_HOLD_DURATION);

//                        // Move the touched object to be the top object layer
//                        if (touchedBoardSpriteSource != null) {
//                            boardSprites.remove(touchedBoardSpriteSource);
//                            boardSprites.add(touchedBoardSpriteSource);
//                        }

                        // Log.v ("MapViewTouch", "Started touching at " + xTouchStart[pointerId] + ", " + yTouchStart[pointerId]);
                    }

                } else if (touchAction == MotionEvent.ACTION_POINTER_DOWN) {

//                    isTouching[pointerId] = true;

                } else if (touchAction == MotionEvent.ACTION_MOVE) {

                    // Previous
                    isTouchingPrevious[pointerId] = isTouching[pointerId];
                    xTouchPrevious[pointerId] = xTouch[pointerId];
                    yTouchPrevious[pointerId] = yTouch[pointerId];

//                    // Current
//                    isTouching[pointerId] = true;
//                    xTouch[pointerId] = motionEvent.getX (i);
//                    yTouch[pointerId] = motionEvent.getY (i);

                    // Drag distance
                    double dragDistanceSquare = Math.pow(xTouch[pointerId] - xTouchStart[pointerId], 2) + Math.pow(yTouch[pointerId] - yTouchStart[pointerId], 2);
                    dragDistance[pointerId] = (dragDistanceSquare != 0 ? Math.sqrt(dragDistanceSquare) : 0);

//            Log.v ("Clay", "dragDistance = " + dragDistance[finger]);

                    // Classify/Callback
                    if (dragDistance[pointerId] > this.MINIMUM_DRAG_DISTANCE) {
                        // Dragging.
                        this.isDragging[pointerId] = true;

                        // Process
                        // TODO: Put into callback

                        // Dragging and holding.
                        if (timeTouch[pointerId] - timeTouchStart > MINIMUM_HOLD_DURATION) {

                            // Holding and dragging.

                            // TODO: Check if (1) drag through a channel connector node, then if (2) drag to another board, then (3) set up communication channel (or abandon if not all steps done)

                            // Start touch on a channel scope
                            if (touchedChannelScopeSource == -1) {

                                if (touchedBoardSpriteSource != null) {
                                    if (touchedBoardSpriteSource.showChannelScopes) {
                                        // If no channel source has been touched yet, check if one is dragged over.

                                        // TODO: Add this to an onTouch callback for the sprite's channel nodes
                                        // Check if the touched board's I/O node is touched
                                        for (int i = 0; i < touchedBoardSpriteSource.channelScopePositions.size(); i++) {
                                            // Check if one of the objects is touched
                                            PointF channelScopePosition = touchedBoardSpriteSource.channelScopePositions.get(i);
                                            if (getDistance((int) channelScopePosition.x, (int) channelScopePosition.y, (int) xTouch[pointerId], (int) yTouch[pointerId]) < 60) {
                                                Log.v("MapViewTouch", "touched node " + (i + 1));
                                                touchedChannelScopeSource = i;
                                                touchedBoardSpriteSource.channelTypes.set(
                                                        i,
                                                        BoardSprite.ChannelType.getNextType(touchedBoardSpriteSource.channelTypes.get(i)) // (boardSprite.channelTypes.get(i) + 1) % boardSprite.channelTypeColors.length
                                                );
                                            }
                                        }

                                    }
                                }
                            }

                            else if (touchedBoardSpriteDestination == null) {

                                if (touchedChannelScopeSource >= 0) {
                                    Log.v ("MapViewTouch", "\tLooking for destination");

                                    // Check if a board was touched
                                    for (BoardSprite boardSprite : boardSprites) {
                                        // Log.v ("MapViewTouch", "Object at " + boardSprite.x + ", " + boardSprite.y);

                                        // TODO: Add this to an isTouch? function of the sprite object
                                        // Check if one of the objects is touched
                                        if (getDistance((int) boardSprite.getPosition().x, (int) boardSprite.getPosition().y, (int) xTouch[pointerId], (int) yTouch[pointerId]) < (boardSprite.boardWidth / 3.0f)) {

                                            // TODO: Add this to an onTouch callback for the sprite's channel nodes

                                            Log.v ("MapViewTouch", "\tTouching object at " + boardSprite.getPosition().x + ", " + boardSprite.getPosition().y);
                                            //this.isTouchingBehavior[pointerId] = true;
                                            touchedBoardSpriteDestination = boardSprite;

                                            touchedBoardSpriteDestination.showChannelScopes = true;

                                            // TODO: Callback: call Sprite.onTouchDestination (via Sprite.touch())
                                        }
                                    }

                                }
                            }

                            else if (touchedChannelScopeDestination == -1) {
                                Log.v ("MapViewTouch", "\tLooking for destination SCOPE");
                                if (touchedBoardSpriteDestination != null) {
                                    if (touchedBoardSpriteDestination.showChannelScopes) {
                                        // If no channel source has been touched yet, check if one is dragged over.

                                        // TODO: Add this to an onTouch callback for the sprite's channel nodes
                                        // Check if the touched board's I/O node is touched
                                        for (int i = 0; i < touchedBoardSpriteDestination.channelScopePositions.size(); i++) {
                                            // Check if one of the objects is touched
                                            PointF channelScopePosition = touchedBoardSpriteDestination.channelScopePositions.get(i);
                                            if (getDistance((int) channelScopePosition.x, (int) channelScopePosition.y, (int) xTouch[pointerId], (int) yTouch[pointerId]) < 60) {
                                                Log.v("MapViewTouch", "touched node " + (i + 1));
                                                touchedChannelScopeDestination = i;
                                                touchedBoardSpriteDestination.channelTypes.set(
                                                        i,
                                                        BoardSprite.ChannelType.getNextType(touchedBoardSpriteDestination.channelTypes.get(i)) // (boardSprite.channelTypes.get(i) + 1) % boardSprite.channelTypeColors.length
                                                );
                                            }
                                        }

                                    }
                                }
                            }

                        } else {
                            // Dragging only (not holding)

                            // TODO: Put into callback
                            if (!disablePanning) {
                                currentPosition.offset((int) (xTouch[pointerId] - xTouchStart[pointerId]), (int) (yTouch[pointerId] - yTouchStart[pointerId]));
                            } else {
                                if (touchedBoardSpriteSource != null) {
                                    if (this.isTouchingBehavior[pointerId]) {
                                        //touchedBoardSpriteSource.scale = 1.3f;
                                        touchedBoardSpriteSource.showHighlights = true;
                                        touchedBoardSpriteSource.setPosition(xTouch[pointerId], yTouch[pointerId]);
                                    }
                                }
                            }

                        }

                    }

                } else if (touchAction == MotionEvent.ACTION_UP) {

                    // Previous
                    isTouchingPrevious[pointerId] = isTouching[pointerId];
                    xTouchPrevious[pointerId] = xTouch[pointerId];
                    yTouchPrevious[pointerId] = yTouch[pointerId];

                    // Current
                    isTouching[pointerId] = false;

                    // Stop touching. Check if this is the start of a touch gesture (i.e., the first touch in a sequence of touch events for the given finger)
                    if (this.isTouching[pointerId] == false && this.isTouchingPrevious[pointerId] == true) {
                        this.xTouchStop[pointerId] = this.xTouch[pointerId];
                        this.yTouchStop[pointerId] = this.yTouch[pointerId];
                        this.timeTouchStop = java.lang.System.currentTimeMillis ();
                    }

                    // Classify/Callbacks
                    if (timeTouchStop - timeTouchStart < MAXIMUM_TAP_DURATION) {
                        Log.v("MapViewTouch", "a) touched board");

                        boolean handledTouch = false;

                        if (/*touchedBoardSpriteSource == null && */ touchedChannelScopeSource == -1
                                && touchedBoardSpriteDestination == null && touchedChannelScopeDestination == -1) {
                            Log.v("MapViewTouch", "b) touched board");

                            // Hide channel scopes (unless dragging)
                            if (!isDragging[pointerId]) {

                                for (BoardSprite boardSprite : this.boardSprites) {

                                    // TODO: Add this to an onTouch callback for the sprite's channel nodes
                                    // Check if the touched board's I/O node is touched
                                    // Check if one of the objects is touched
                                    if (getDistance((int) boardSprite.getPosition().x, (int) boardSprite.getPosition().y, (int) xTouchStart[pointerId], (int) yTouchStart[pointerId]) < 80) {
                                        Log.v("MapViewTouch", "c) touched board");

                                        touchedBoardSpriteSource = boardSprite;

                                        if (touchedBoardSpriteSource.showChannelScopes) {
                                            // Touched board that's showing channel scopes.
                                            touchedBoardSpriteSource.showChannelScopes = false;
                                            touchedBoardSpriteSource = null;

                                            // Reset style and visualization.
                                            for (BoardSprite boardSprite2 : this.boardSprites) {
                                                boardSprite2.showChannelScopes = false;
                                                boardSprite2.setTransparency(1.0f);
                                            }

                                            ApplicationView.getApplicationView().speakPhrase("stopping");
                                        } else {
                                            // No touch on board or scope. Touch is on map. So hide scopes.
                                            for (BoardSprite boardSprite2 : this.boardSprites) {
                                                boardSprite2.showChannelScopes = false;
                                                boardSprite2.setTransparency(0.2f);
                                            }
                                            touchedBoardSpriteSource.showChannelScopes = true;
                                            touchedBoardSpriteSource.setTransparency(1.0f);
                                            ApplicationView.getApplicationView().speakPhrase("choose a channel to get data.");
                                        }

                                        handledTouch = true;

                                        break;
                                    }
                                }
                            }
                        }

                        if (!handledTouch) {
                            if (touchedBoardSpriteSource != null && touchedChannelScopeSource == -1
                                    && touchedBoardSpriteDestination == null && touchedChannelScopeDestination == -1) {
                                // Hide channel scopes (unless dragging)
                                if (!isDragging[pointerId]) {

                                    for (BoardSprite boardSprite : this.boardSprites) {

                                        // TODO: Add this to an onTouch callback for the sprite's channel nodes
                                        // Check if the touched board's I/O node is touched
                                        for (int i = 0; i < boardSprite.channelScopePositions.size(); i++) {
                                            PointF channelNodePoint = boardSprite.channelScopePositions.get(i);
                                            // Check if one of the objects is touched
                                            if (getDistance((int) channelNodePoint.x, (int) channelNodePoint.y, (int) xTouchStart[pointerId], (int) yTouchStart[pointerId]) < 80) {
                                                Log.v("MapViewTouch", "touched node " + (i + 1));
                                                touchedChannelScopeSource = i;
                                                boardSprite.channelTypes.set(
                                                        i,
                                                        BoardSprite.ChannelType.getNextType(boardSprite.channelTypes.get(i)) // (boardSprite.channelTypes.get(i) + 1) % boardSprite.channelTypeColors.length
                                                );

                                                ApplicationView.getApplicationView().speakPhrase("setting as input. you can send the data to another board if you want. touch another board.");

                                                handledTouch = true;

                                                break;
                                            }
                                        }

                                    }
                                }

                            }
                        }

                        if (!handledTouch) {
                            if (touchedBoardSpriteSource != null && touchedChannelScopeSource != -1
                                    && touchedBoardSpriteDestination == null && touchedChannelScopeDestination == -1) {

                                // Hide channel scopes (unless dragging)
                                if (!isDragging[pointerId]) {

                                    for (BoardSprite boardSprite : this.boardSprites) {

                                        // TODO: Add this to an onTouch callback for the sprite's channel nodes
                                        // Check if the touched board's I/O node is touched
//                                    for (int i = 0; i < boardSprite.channelScopePositions.size(); i++) {
//                                        PointF channelNodePoint = boardSprite.channelScopePositions.get(i);
                                        // Check if one of the objects is touched
                                        if (getDistance((int) boardSprite.getPosition().x, (int) boardSprite.getPosition().y, (int) xTouchStart[pointerId], (int) yTouchStart[pointerId]) < 80) {
                                            Log.v("MapViewTouch", "c) touched board");
                                            touchedBoardSpriteDestination = boardSprite;
                                            boardSprite.showChannelScopes = true;

                                            ApplicationView.getApplicationView().speakPhrase("that board will be the destination. now choose the output channel.");

                                            handledTouch = true;

                                            break;
//                                            touchedChannelScopeSource = i;
//                                            boardSprite.channelTypes.set(
//                                                    i,
//                                                    BoardSprite.ChannelType.getNextType(boardSprite.channelTypes.get(i)) // (boardSprite.channelTypes.get(i) + 1) % boardSprite.channelTypeColors.length
//                                            );
                                        }
//                                    }

                                    }

                                }

                            }
                        }

                        if (!handledTouch) {
                            if (touchedBoardSpriteSource != null && touchedChannelScopeSource != -1
                                    && touchedBoardSpriteDestination != null && touchedChannelScopeDestination == -1) {

                                // Hide channel scopes (unless dragging)
                                if (!isDragging[pointerId]) {

                                    for (BoardSprite boardSprite : this.boardSprites) {

                                        // TODO: Add this to an onTouch callback for the sprite's channel nodes
                                        // Check if the touched board's I/O node is touched
                                        for (int i = 0; i < boardSprite.channelScopePositions.size(); i++) {
                                            PointF channelNodePoint = boardSprite.channelScopePositions.get(i);
                                            // Check if one of the objects is touched
                                            if (getDistance((int) channelNodePoint.x, (int) channelNodePoint.y, (int) xTouchStart[pointerId], (int) yTouchStart[pointerId]) < 80) {
                                                Log.v("MapViewTouch", "touched node " + (i + 1));
                                                touchedChannelScopeDestination = i;
                                                boardSprite.channelTypes.set(
                                                        i,
                                                        BoardSprite.ChannelType.getNextType(boardSprite.channelTypes.get(i)) // (boardSprite.channelTypes.get(i) + 1) % boardSprite.channelTypeColors.length
                                                );


                                                ApplicationView.getApplicationView().speakPhrase("got it. the channel is set up. you can connect components to it now and start using them.");
                                                ApplicationView.getApplicationView().speakPhrase("do you want me to help you connect the components?"); // i.e., start interactive assembly... start by showing component browser. then choose component and get instructions for connecting it. show "okay, done" button.

                                                Log.v("MapViewLink", "Created data path.");

                                                // Reset connection state
                                                touchedBoardSpriteSource = null;
                                                touchedBoardSpriteDestination = null;
                                                touchedChannelScopeSource = -1;
                                                touchedChannelScopeDestination = -1;



                                                handledTouch = true;

                                                break;
                                            }
                                        }

                                    }
                                }

                            }
                        }

                        if (!handledTouch) {
                            // No touch on board or scope. Touch is on map. So hide scopes.
                            for (BoardSprite boardSprite : this.boardSprites) {
                                boardSprite.showChannelScopes = false;
                                boardSprite.setTransparency(1.0f);
                            }



                            Log.v("MapViewLink", "Partial data path was abandoned.");

                            if (touchedBoardSpriteSource != null && touchedChannelScopeSource != -1 && touchedBoardSpriteDestination != null) {
                                ApplicationView.getApplicationView().speakPhrase("the channel was interrupted.");
                            }

                            // Reset selected source channel scope
                            if (touchedChannelScopeSource != -1) {
                                touchedBoardSpriteSource.channelTypes.set(touchedChannelScopeSource, BoardSprite.ChannelType.NONE);
                            }

                            // Reset selected destination channel scope
                            if (touchedChannelScopeDestination != -1) {
                                touchedBoardSpriteSource.channelTypes.set(touchedChannelScopeDestination, BoardSprite.ChannelType.NONE);
                            }

                            // Hide scopes.
                            for (BoardSprite boardSprite : this.boardSprites) {
                                boardSprite.showChannelScopes = false;
                            }

                            // Reset connection state
                            touchedBoardSpriteSource = null;
                            touchedBoardSpriteDestination = null;
                            touchedChannelScopeSource = -1;
                            touchedChannelScopeDestination = -1;

                            // Reset map interactivity
                            disablePanning = false;
                        }

                    } else {

                        if (isDragging[pointerId]) {

                            // Connection: A complete connection made.
                            if (touchedBoardSpriteSource != null && touchedChannelScopeSource != -1
                                    && touchedBoardSpriteDestination != null && touchedChannelScopeDestination != -1) {

                                Log.v("MapViewLink", "Created data path.");

                                // Reset connection state
                                touchedBoardSpriteSource = null;
                                touchedBoardSpriteDestination = null;
                                touchedChannelScopeSource = -1;
                                touchedChannelScopeDestination = -1;

                            } else if (touchedBoardSpriteSource != null) {

                                Log.v("MapViewLink", "Partial data path was abandoned.");

                                // Reset selected source channel scope
                                if (touchedChannelScopeSource != -1) {
                                    touchedBoardSpriteSource.channelTypes.set(touchedChannelScopeSource, BoardSprite.ChannelType.NONE);
                                }

                                // Reset selected destination channel scope
                                if (touchedChannelScopeDestination != -1) {
                                    touchedBoardSpriteSource.channelTypes.set(touchedChannelScopeDestination, BoardSprite.ChannelType.NONE);
                                }

                                // Hide scopes.
                                for (BoardSprite boardSprite : this.boardSprites) {
                                    boardSprite.showChannelScopes = false;
                                }

                                // Style. Reset the style of touched boards.
                                touchedBoardSpriteSource.showHighlights = false;
                                touchedBoardSpriteSource.setScale(1.0f);

                                // Reset connection state
                                touchedBoardSpriteSource = null;
                                touchedBoardSpriteDestination = null;
                                touchedChannelScopeSource = -1;
                                touchedChannelScopeDestination = -1;

                            }
                        }

                    }

                    // Object interaction
                    // disablePanning = false;
//                    touchedBoardSpriteSource = null;
//                    touchedBoardSpriteDestination = null;
//                    touchedChannelScopeSource = -1;
//                    touchedChannelScopeDestination = -1;

                    this.isDragging[pointerId] = false;

//                    getClay ().getPerson().untouch (pointerId, xTouches[pointerId], yTouches[pointerId]);
//                    getClay ().getPerson().classify(pointerId);

                    //originPosition.offset ((int) (xTouch[finger] - xTouchStart[finger]), (int) (yTouch[finger] - yTouchStart[finger]));
//                    int id = motionEvent.getPointerId (0);
//                    originPosition.offset ((int) (xTouches[id] - xTouchStart[id]), (int) (yTouches[id] - yTouchStart[id]));

                } else if (touchAction == MotionEvent.ACTION_POINTER_UP) {

//                    isTouch[pointerId] = false;
//                    isTouchingPrevious[pointerId] = false;

                } else if (touchAction == MotionEvent.ACTION_CANCEL) {

//                    isTouch[pointerId] = false;
//                    isTouchingPrevious[pointerId] = false;

                } else {

//                    isTouch[pointerId] = false;
//                    isTouchingPrevious[pointerId] = false;

                }
            }
        }

        return true;
    }



    private void onHoldCallback (int pointerId) {
        if (dragDistance[pointerId] < this.MINIMUM_DRAG_DISTANCE) {
            // Holding but not (yet) dragging.

            // Disable panning
            disablePanning = false;

            // Hide scopes
            if (touchedChannelScopeSource == -1) {
                for (BoardSprite boardSprite : this.boardSprites) {
                    boardSprite.showChannelScopes = false;
                }
            }

            // Show scope for source board
            if (touchedBoardSpriteSource != null) {
                touchedBoardSpriteSource.showChannelScopes = true;
            }

        }
    }

    //----------------------------------------------------------------------------------------------

    public double getDistance (int x, int y, int x2, int y2) {
        double distanceSquare = Math.pow (x - x2, 2) + Math.pow (y - y2, 2);
        double distance = Math.sqrt (distanceSquare);
        return distance;
    }
}
