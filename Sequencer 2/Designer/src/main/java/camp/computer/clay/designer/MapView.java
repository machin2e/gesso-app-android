package camp.computer.clay.designer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

public class MapView extends SurfaceView implements SurfaceHolder.Callback {

    class Square {

        Square(float x, float y, float angle) {
            this.x = x;
            this.y = y;
            this.angle = angle;

            initializeChannelTypes();

            // Channel node points
            if (channelNodePoints.size() == 0) {
                for (int i = 0; i < 12; i++) {
                    PointF point = new PointF();
                    point.x = this.x;
                    point.y = this.y;
                    channelNodePoints.add(point);
                }
            }
            updateChannelNodePoints();
        }

        private void initializeChannelTypes() {
            for (int i = 0; i < channelNodeType.length; i++) {

                // Initialize type
                channelNodeType[i] = 0;
            }
        }

        private void updateChannelNodePoints() {

            double boardAngleRadians = Math.toRadians(this.angle);
            float sinBoardAngle = (float) Math.sin(boardAngleRadians);
            float cosBoardAngle = (float) Math.cos(boardAngleRadians);

            for (int i = 0; i < 4; i++) {

                // Cache calculations
                double boardFacingAngleRadians = Math.toRadians(-90.0 * i);
                float sinBoardFacingAngle = (float) Math.sin(boardFacingAngleRadians);
                float cosBoardFacingAngle = (float) Math.cos(boardFacingAngleRadians);

                for (int j = 0; j < 3; j++) {

                    PointF point = channelNodePoints.get(3 * i + j);

                    // Translate (Nodes)
                    float nodeRadiusPlusPadding = channelNodeRadius + distanceBetweenNodes;
                    point.x = ((-(nodeRadiusPlusPadding * 2.0f) + j * (nodeRadiusPlusPadding * 2.0f)));
                    point.y = (((boardWidth / 2.0f) + nodeRadiusPlusPadding));

                    // Rotate (Nodes)
                    point.set(
                            point.x * cosBoardFacingAngle - point.y * sinBoardFacingAngle,
                            point.x * sinBoardFacingAngle + point.y * cosBoardFacingAngle
                    );

                    // Rotate (Board)
                    point.set(
                            point.x * cosBoardAngle - point.y * sinBoardAngle,
                            point.x * sinBoardAngle + point.y * cosBoardAngle
                    );

                    // Translate (Board)
                    point.x = point.x + this.x;
                    point.y = point.y + this.y;

                }
            }
        }

        float x;
        float y;
        float scaleFactor = 1.0f;
        float angle;

        ArrayList<PointF> channelNodePoints = new ArrayList<PointF>();
        public int[] channelNodeType = new int[12];

        // --- STYLE ---
        float boardWidth = 250;
        float boardHeight = 250;
        int boardColor = Color.parseColor("#414141"); // Color.parseColor("#212121");
        boolean showBoardOutline = true;
        int boardOutlineColor = Color.parseColor("#212121"); // Color.parseColor("#737272");
        float boardOutlineThickness = 1.0f;

        float headerWidth = 60;
        float headerHeight = 15;
        int headerColor = Color.parseColor("#000000");
        boolean showHeaderOutline = false;
        int headerOutlineColor = boardOutlineColor;
        float headerOutlineThickness = boardOutlineThickness;

        boolean showHighlights = false;
        int boardHighlightColor = Color.parseColor("#1976D2");
        float boardHighlightThickness = 20;

        float distanceLightsToEdge = 15.0f;
        float lightWidth = 12;
        float lightHeight = 20;
        int[] lightColors = new int[] { Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE, Color.WHITE };
        boolean showLightOutline = true;
        int lightOutlineColor = Color.parseColor("#212121");
        float lightOutlineThickness = 0.0f;

        boolean showChannelConnectors = false;
        boolean showChannelLabel = false;
        boolean showChannelData = true;
        boolean showChannelNodeOutline = false;
        float labelTextSize = 30.0f;
        float channelNodeRadius = 40.0f;
        String[] channelTypeColors = new String[] { "#efefef", "#1467f1", "#62df42", "#cc0033", "#ff9900" };
        // String[] channelNodeColor = new String[] { "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1" };
        float distanceNodeToBoard = 45.0f;
        float distanceBetweenNodes = 5.0f;
        float[] channelDataPoints = new float[(int) channelNodeRadius * 2];
        // ^^^ STYLE ^^^

    }

    ArrayList<Square> squareList = new ArrayList<Square>();

    private MapViewDrawer mapViewDrawer;

    private SurfaceHolder surfaceHolder;

    // Canvas
    private Bitmap canvasBitmap = null;
    private Canvas mapCanvas = null;
    private int canvasWidth, canvasHeight;
    private Paint paint = new Paint (Paint.ANTI_ALIAS_FLAG);
    private Matrix identityMatrix;

    // Coodinates
    private Point originPoint = new Point ();
    private Point currentPoint = new Point ();

    public MapView(Context context) {
        super(context);
        initializeData();
    }

    public MapView (Context context, AttributeSet attrs) {
        super (context, attrs);
        initializeData();
    }

    public MapView (Context context, AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);
        initializeData();
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
        originPoint.set((int) (mapCanvas.getWidth() / 2.0f), (int) (mapCanvas.getHeight() / 2.0f));
        currentPoint.set(originPoint.x, originPoint.y);

        identityMatrix = new Matrix ();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    public void initializeData() {
        squareList.add(new Square(0, 0, 0));
        squareList.add(new Square(300, 400, 30));
        squareList.add(new Square(-200, -440, -55));
    }

    public void MapView_OnResume () {
        Log.v("MapView", "MapView_OnResume");

        surfaceHolder = getHolder ();
        getHolder ().addCallback (this);

        // Create and start background Thread
        mapViewDrawer = new MapViewDrawer (this);
        mapViewDrawer.setRunning (true);
        mapViewDrawer.start ();

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
        mapViewDrawer.setRunning (false);

        while (retry) {
            try {
                mapViewDrawer.join ();
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

    void drawDevice (Square square) {

        mapCanvas.save();

        mapCanvas.translate(square.x, square.y);
        mapCanvas.rotate(square.angle);

        mapCanvas.scale(square.scaleFactor, square.scaleFactor);

        // --- BOARD HIGHLIGHT ---
        if (square.showHighlights) {
            mapCanvas.save();
            // Color
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(square.boardHighlightColor);
            mapCanvas.drawRect(
                    0 - (square.boardWidth / 2.0f) - square.boardHighlightThickness,
                    0 - (square.boardHeight / 2.0f) - square.boardHighlightThickness,
                    0 + (square.boardWidth / 2.0f) + square.boardHighlightThickness,
                    0 + (square.boardHeight / 2.0f) + square.boardHighlightThickness,
                    paint);
            mapCanvas.restore();
        }
        // ^^^ BOARD HIGHLIGHT ^^^

        // --- HEADER HIGLIGHT ---
        if (square.showHighlights) {
            for (int i = 0; i < 4; i++) {

                mapCanvas.save();

                mapCanvas.rotate(90 * i);
                mapCanvas.translate(0, 0);

                mapCanvas.save();
                mapCanvas.translate(
                        0,
                        (square.boardHeight / 2.0f) + (square.headerHeight / 2.0f)
                );
                mapCanvas.rotate(0);

                mapCanvas.save();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(square.boardHighlightColor);
                mapCanvas.drawRect(
                        0 - (square.headerWidth / 2.0f) - square.boardHighlightThickness,
                        0 - (square.headerHeight / 2.0f) - square.boardHighlightThickness,
                        0 + (square.headerWidth / 2.0f) + square.boardHighlightThickness,
                        0 + (square.headerHeight / 2.0f) + square.boardHighlightThickness,
                        paint
                );
                mapCanvas.restore();

                mapCanvas.restore();

                mapCanvas.restore();

            }
        }
        // ^^^ HEADER HIGHLIGHT ^^^

        // --- BOARD ---
        mapCanvas.save();
        // Color
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(square.boardColor);
        mapCanvas.drawRect(
                0 - (square.boardWidth / 2.0f),
                0 - (square.boardHeight / 2.0f),
                0 + (square.boardWidth / 2.0f),
                0 + (square.boardHeight / 2.0f),
                paint
        );
        // Outline
        if (square.showBoardOutline) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(square.boardOutlineColor);
            paint.setStrokeWidth(square.boardOutlineThickness);
            mapCanvas.drawRect(
                    0 - (square.boardWidth / 2.0f),
                    0 - (square.boardHeight / 2.0f),
                    0 + (square.boardWidth / 2.0f),
                    0 + (square.boardHeight / 2.0f),
                    paint
            );
        }
        mapCanvas.restore();
        // ^^^ BOARD ^^^

        // --- HEADERS ---
        for (int i = 0; i < 4; i++) {

            mapCanvas.save();

            mapCanvas.rotate(90 * i);
            mapCanvas.translate(0, 0);

            mapCanvas.save();
            mapCanvas.translate(
                    0,
                    (square.boardHeight / 2.0f) + (square.headerHeight / 2.0f)
            );
            mapCanvas.rotate(0);

            mapCanvas.save();
            // Color
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(square.headerColor);
            mapCanvas.drawRect(
                    0 - (square.headerWidth / 2.0f),
                    0 - (square.headerHeight / 2.0f),
                    0 + (square.headerWidth / 2.0f),
                    0 + (square.headerHeight / 2.0f),
                    paint
            );
            // Outline
            if (square.showHeaderOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(square.headerOutlineThickness);
                paint.setColor(square.headerOutlineColor);
                mapCanvas.drawRect(
                        0 - (square.headerWidth / 2.0f),
                        0 - (square.headerHeight / 2.0f),
                        0 + (square.headerWidth / 2.0f),
                        0 + (square.headerHeight / 2.0f),
                        paint
                );
            }
            mapCanvas.restore();

            mapCanvas.restore();

            mapCanvas.restore();

        }
        // ^^^ HEADERS ^^^

        // --- LIGHTS ---
        for (int i = 0; i < 4; i++) {

            mapCanvas.save();

            mapCanvas.rotate(-90 * i);
            mapCanvas.translate(0, 0);

            for (int j = 0; j < 3; j++) {

                mapCanvas.save();
                mapCanvas.translate(
                        -20 + j * 20,
                        (square.boardWidth / 2.0f) - (square.lightHeight / 2.0f) - square.distanceLightsToEdge
                );
                mapCanvas.rotate(0);

                mapCanvas.save();
                // Color
                paint.setStyle(Paint.Style.FILL);
                paint.setStrokeWidth(3);
                // paint.setColor(square.lightColors[3 * i + j]);
                paint.setColor(Color.parseColor(square.channelTypeColors[square.channelNodeType[3 * i + j]]));
                mapCanvas.drawRoundRect(
                        0 - (square.lightWidth / 2.0f),
                        0 - (square.lightHeight / 2.0f),
                        0 + (square.lightWidth / 2.0f),
                        0 + (square.lightHeight / 2.0f),
                        5.0f,
                        5.0f,
                        paint
                );
                // Outline
                if (square.showLightOutline) {
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(square.lightOutlineThickness);
                    paint.setColor(square.lightOutlineColor);
                    mapCanvas.drawRoundRect(
                            0 - (square.lightWidth / 2.0f),
                            0 - (square.lightHeight / 2.0f),
                            0 + (square.lightWidth / 2.0f),
                            0 + (square.lightHeight / 2.0f),
                            5.0f,
                            5.0f,
                            paint
                    );
                }
                mapCanvas.restore();

                mapCanvas.restore();

            }

            mapCanvas.restore();

        }
        // ^^^ LIGHTS ^^^

        // --- NODES ---
        for (int i = 0; i < square.channelDataPoints.length; i++) {
            Random random = new Random();
            square.channelDataPoints[i] = -(square.channelNodeRadius / 2.0f) + random.nextInt((int) (1 * square.channelNodeRadius));
        }

        if (square.showChannelConnectors) {

            for (int i = 0; i < 4; i++) {

                mapCanvas.save();

                mapCanvas.rotate(-90 * i);
                mapCanvas.translate(0, 0);

                for (int j = 0; j < 3; j++) {

                    mapCanvas.save();
                    mapCanvas.translate(
                            -((square.channelNodeRadius + square.distanceBetweenNodes) * 2.0f) + j * ((square.channelNodeRadius + square.distanceBetweenNodes) * 2),
                            (square.boardWidth / 2.0f) + square.channelNodeRadius + square.distanceNodeToBoard
                    );
                    mapCanvas.rotate(0);

                    mapCanvas.save();
                    // Color
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(Color.parseColor(square.channelTypeColors[square.channelNodeType[3 * i + j]]));
                    square.updateChannelNodePoints();
                    mapCanvas.drawCircle(
                            0,
                            0,
                            square.channelNodeRadius,
                            paint
                    );
                    // Outline
                    if (square.showChannelNodeOutline) {
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(3);
                        paint.setColor(Color.BLACK);
                        mapCanvas.drawCircle(
                                0,
                                0,
                                square.channelNodeRadius,
                                paint
                        );
                    }
                    // Label
                    if (square.showChannelLabel) {
                        paint.setTextSize(square.labelTextSize);
                        Rect textBounds = new Rect();
                        String channelNumberText = String.valueOf(3 * i + j + 1);
                        paint.getTextBounds(channelNumberText, 0, channelNumberText.length(), textBounds);
                        paint.setStyle(Paint.Style.FILL);
                        paint.setStrokeWidth(3);
                        paint.setColor(Color.BLACK);
                        mapCanvas.drawText(channelNumberText, -(textBounds.width() / 2.0f), textBounds.height() / 2.0f, paint);
                    }
                    // Outline
                    if (square.showChannelData) {
                        if (square.channelNodeType[3 * i + j] != 0) {
                            paint.setStyle(Paint.Style.STROKE);
                            paint.setStrokeWidth(2.0f);
                            paint.setColor(Color.WHITE);
                            int step = 3;
                            for (int k = 0; k + step < square.channelDataPoints.length - 1; k += step) {
                                mapCanvas.drawLine(
                                        -square.channelNodeRadius + k,
                                        square.channelDataPoints[k],
                                        -square.channelNodeRadius + k + step,
                                        square.channelDataPoints[k + step],
                                        paint
                                );
                            }
                        }
                    }
                    mapCanvas.restore();

                    mapCanvas.restore();

                }

                mapCanvas.restore();

            }
        }
        // ^^^ NODES ^^^

        mapCanvas.restore();

    }

    //----------------------------------------------------------------------------------------------
    // Coordinate System
    //----------------------------------------------------------------------------------------------

    //----------------------------------------------------------------------------------------------
    // Perspective
    //----------------------------------------------------------------------------------------------

    public static float DEFAULT_SCALE_FACTOR = 1.0f;

    //private Point originPosition = new Point (0, 0);
    private float scaleFactor = DEFAULT_SCALE_FACTOR;

    //----------------------------------------------------------------------------------------------
    // Layout
    //----------------------------------------------------------------------------------------------

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Move the perspective
        mapCanvas.save ();
        //mapCanvas.translate (originPoint.x, originPoint.y);
        mapCanvas.translate (currentPoint.x, currentPoint.y);
        mapCanvas.scale (scaleFactor, scaleFactor);
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

    public void updateSurfaceView () {
        // The function run in background thread, not UI thread.

        Canvas canvas = null;

        try {
            canvas = surfaceHolder.lockCanvas ();

            synchronized (surfaceHolder) {
//                updateStates ();
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

        for (Square square : squareList) {
            drawDevice(square);
        }
    }

    //----------------------------------------------------------------------------------------------
    // Human Interaction Model
    //----------------------------------------------------------------------------------------------

    public final int MAXIMUM_TOUCH_POINT_COUNT = 5;
    public final int MINIMUM_DRAG_DISTANCE = 35;

    private boolean hasTouches = false; // i.e., At least one touch is detected.
    private int touchCount = 0; // i.e., The total number of touch points detected.

    //    private Point[] touch = new Point[MAXIMUM_TOUCH_COUNT];
    private double[] xTouch = new double[MAXIMUM_TOUCH_POINT_COUNT];
    private double[] yTouch = new double[MAXIMUM_TOUCH_POINT_COUNT];
    private long[] timeTouch = new long[MAXIMUM_TOUCH_POINT_COUNT];
    private boolean[] isTouching = new boolean[MAXIMUM_TOUCH_POINT_COUNT];
    private boolean[] isDragging = new boolean[MAXIMUM_TOUCH_POINT_COUNT];
    private double[] dragDistance = new double[MAXIMUM_TOUCH_POINT_COUNT];

    private double[] xTouchPrevious = new double[MAXIMUM_TOUCH_POINT_COUNT];
    private double[] yTouchPrevious = new double[MAXIMUM_TOUCH_POINT_COUNT];
    private long[] timeTouchPrevious = new long[MAXIMUM_TOUCH_POINT_COUNT];
    private boolean[] isTouchingPrevious = new boolean[MAXIMUM_TOUCH_POINT_COUNT];
    private boolean[] isTouchingActionPrevious = new boolean[MAXIMUM_TOUCH_POINT_COUNT];

    // Point where the touch started.
    private double[] xTouchStart = new double[MAXIMUM_TOUCH_POINT_COUNT];
    private double[] yTouchStart = new double[MAXIMUM_TOUCH_POINT_COUNT];
    private long timeTouchStart = java.lang.System.currentTimeMillis ();

    // Point where the touch ended.
    private double[] xTouchStop = new double[MAXIMUM_TOUCH_POINT_COUNT];
    private double[] yTouchStop = new double[MAXIMUM_TOUCH_POINT_COUNT];
    private long timeTouchStop = java.lang.System.currentTimeMillis ();

    private boolean[] isTouchingBehavior = new boolean[MAXIMUM_TOUCH_POINT_COUNT];
    private Square touchedSquare = null;

    private boolean disablePanning = false;

    @Override
    public boolean onTouchEvent (MotionEvent motionEvent) {
        // Log.v("MapView", "onTouchEvent");

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
                    xTouch[id] = (motionEvent.getX (i) - currentPoint.x) / scaleFactor;
                    yTouch[id] = (motionEvent.getY (i) - currentPoint.y) / scaleFactor;
                    timeTouch[id] = java.lang.System.currentTimeMillis ();

//                    xTouch[id] = (motionEvent.getX (i) - currentPoint.x) / scaleFactor + mapCanvas.getClipBounds().left;
//                    yTouch[id] = (motionEvent.getY (i) - currentPoint.y) / scaleFactor + mapCanvas.getClipBounds().top;
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
                        for (Square square : squareList) {
                            // Log.v ("MapViewTouch", "Object at " + square.x + ", " + square.y);

                            // Check if one of the objects is touched
                            if (getDistance((int) square.x, (int) square.y, (int) xTouchStart[pointerId], (int) yTouchStart[pointerId]) < 100) {

                                // Log.v ("MapViewTouch", "\tTouching object at " + square.x + ", " + square.y);
                                this.isTouchingBehavior[pointerId] = true;
                                touchedSquare = square;

                                // Callback: onTouch

                                disablePanning = true;
                            }

                            // Touch I/O node
                            if (square.showChannelConnectors) {

                                // Check if the touched board's I/O node is touched
                                for (int i = 0; i < square.channelNodePoints.size(); i++) {
                                    PointF channelNodePoint = square.channelNodePoints.get(i);
                                    // Check if one of the objects is touched
                                    if (getDistance((int) channelNodePoint.x, (int) channelNodePoint.y, (int) xTouchStart[pointerId], (int) yTouchStart[pointerId]) < 60) {
                                        Log.v("MapViewTouch", "touched node " + (i + 1));
                                        square.channelNodeType[i] = (square.channelNodeType[i] + 1) % square.channelTypeColors.length;
                                    }
                                }

                            }

                        }

//                        // Move the touched object to the top
//                        if (touchedSquare != null) {
//                            squareList.remove(touchedSquare);
//                            squareList.add(touchedSquare);
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

                    // Recognize
                    // TODO:

                    // Process
                    if (!disablePanning) {
                        currentPoint.offset((int) (xTouch[pointerId] - xTouchStart[pointerId]), (int) (yTouch[pointerId] - yTouchStart[pointerId]));
//                    currentPoint.offset ((int) (xTouch[pointerId] - xTouchPrevious[pointerId]), (int) (yTouch[pointerId] - yTouchPrevious[pointerId]));
                    } else {

                        // Drag. Check if a drag is occurring (defined by continuously touching the screen while deviating from the initail point of touch by more than 15 pixels)
                        if (touchedSquare != null) {
                            if (this.isTouchingBehavior[pointerId]) {
                                if (dragDistance[pointerId] > this.MINIMUM_DRAG_DISTANCE) {

                                    //touchedSquare.scaleFactor = 1.3f;
                                    touchedSquare.showHighlights = true;

                                    touchedSquare.x = (int) (xTouch[pointerId]);
                                    touchedSquare.y = (int) (yTouch[pointerId]);
                                    touchedSquare.updateChannelNodePoints();
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

                    // Check if this is the start of a touch gesture (i.e., the first touch in a sequence of touch events for the given finger)
                    if (this.isTouching[pointerId] == false && this.isTouchingPrevious[pointerId] == true) {
                        this.xTouchStop[pointerId] = this.xTouch[pointerId];
                        this.yTouchStop[pointerId] = this.yTouch[pointerId];
                        this.timeTouchStop = java.lang.System.currentTimeMillis ();
                        // Log.v ("MapViewTouch", "Stopped touching.");
                    }

                    if (touchedSquare != null) {
                        touchedSquare.showHighlights = false;
                        touchedSquare.scaleFactor = 1.0f;

                        // Tap
                        if (timeTouchStop - timeTouchStart < 200) {
                            touchedSquare.showChannelConnectors = !touchedSquare.showChannelConnectors;
                            if (!touchedSquare.showChannelConnectors) {
                                disablePanning = false;
                            }
                        } else {
                            touchedSquare.showChannelConnectors = false;
                        }

                    }

                    // Object interaction
                    // disablePanning = false;
                    touchedSquare = null;

//                    getClay ().getPerson().untouch (pointerId, xTouches[pointerId], yTouches[pointerId]);
//                    getClay ().getPerson().classify(pointerId);

                    //originPoint.offset ((int) (xTouch[finger] - xTouchStart[finger]), (int) (yTouch[finger] - yTouchStart[finger]));
//                    int id = motionEvent.getPointerId (0);
//                    originPoint.offset ((int) (xTouches[id] - xTouchStart[id]), (int) (yTouches[id] - yTouchStart[id]));

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

    //----------------------------------------------------------------------------------------------

    public double getDistance (int x, int y, int x2, int y2) {
        double distanceSquare = Math.pow (x - x2, 2) + Math.pow (y - y2, 2);
        double distance = Math.sqrt (distanceSquare);
        return distance;
    }
}
