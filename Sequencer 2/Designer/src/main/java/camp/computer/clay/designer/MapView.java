package camp.computer.clay.designer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;

import camp.computer.clay.model.simulation.Body;
import camp.computer.clay.model.simulation.Machine;
import camp.computer.clay.model.interaction.Perspective;
import camp.computer.clay.model.simulation.Port;
import camp.computer.clay.model.simulation.Simulation;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.model.interaction.TouchInteractivity;
import camp.computer.clay.sprite.Visualization;

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
        Perspective perspective = new Perspective(visualization);

//        WindowManager wm = (WindowManager) ApplicationView.getContext().getSystemService(Context.WINDOW_SERVICE);
//        Display display = wm.getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int displayWidth = size.x;
//        int displayHeight = size.y;
//
//        perspective.setWidth(displayWidth);
//        perspective.setHeight(displayHeight);

        body.setPerspective(perspective);

        // Add body to simulation
        simulation.addBody(body);
    }

    private void initializeSimulation() {

        // TODO: Move Simulation/Machine this into Simulation or Ecology (in Simulation) --- maybe combine Simulation+Ecology
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int letterIndex = 0;
        for (int i = 0; i < 5; i++) {
            Machine machine = new Machine();
            for (int j = 0; j < 12; j++) {
                machine.addPort(new Port());
                machine.addTag(alphabet.substring(letterIndex, letterIndex + 1));
                letterIndex = letterIndex % alphabet.length();
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

        simulation.getBody(0).getPerspective().setWidth(canvasWidth);
        simulation.getBody(0).getPerspective().setHeight(canvasHeight);

        identityMatrix = new Matrix ();

        // Center the visualization coordinate system
        originPosition.set(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f);

        // Update perspective on visualization
        simulation.getBody(0).getPerspective().setPosition(new PointF(0, 0));
//        simulation.getBody(0).getPerspective().width
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

//    public static float DEFAULT_SCALE_FACTOR = 1.0f;
//    public static int DEFAULT_SCALE_DURATION = 50;
//
//     private Point originPosition = new Point (0, 0);
//    private float targetScale = DEFAULT_SCALE_FACTOR;
//    public float scale = targetScale;
//    private int scaleDuration = DEFAULT_SCALE_DURATION;
//
//    public void setScale (float targetScale) {
//
//        if (this.targetScale != targetScale) {
//
//            if (this.scale != targetScale) {
//                Animation.scaleValue(scale, targetScale, scaleDuration, new Animation.OnScaleListener() {
//                    @Override
//                    public void onScale(float currentScale) {
//                        scale = currentScale;
//                    }
//                });
//            }
//
//            Vibrator v = (Vibrator) ApplicationView.getContext().getSystemService(Context.VIBRATOR_SERVICE);
//            // Vibrate for 500 milliseconds
//            v.vibrate(50);
//
//            this.targetScale = targetScale;
//        }
//    }

    //----------------------------------------------------------------------------------------------
    // Layout
    //----------------------------------------------------------------------------------------------

    protected void doDraw(Canvas canvas) {
//        super.onDraw(canvas);

        // <PERSPECTIVE>
        // Move the perspective
        this.canvas.save ();
        this.canvas.translate (
                originPosition.x + simulation.getBody(0).getPerspective().getPosition().x + (float) ApplicationView.getApplicationView().getSensorAdapter().getRotationY(),
                originPosition.y + simulation.getBody(0).getPerspective().getPosition().y - (float) ApplicationView.getApplicationView().getSensorAdapter().getRotationX()
        );
        // this.canvas.rotate((float) ApplicationView.getApplicationView().getSensorAdapter().getRotationZ());
        this.canvas.scale (
                simulation.getBody(0).getPerspective().getScale(),
                simulation.getBody(0).getPerspective().getScale()
        );
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
    // Interaction Model
    //----------------------------------------------------------------------------------------------

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

    // TODO: In the queue, store the touch actions persistently after exceeding maximum number for immediate interactions.
    private TouchInteractivity touchInteractivity = null;

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

        Log.v("InteractionHistory", "Started touch composition.");

        Body currentBody = simulation.getBody(0);

        TouchInteraction touchInteraction = new TouchInteraction(TouchInteraction.TouchInteractionType.NONE);
        touchInteraction.setBody(currentBody);

        if (pointCount <= TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT) {
            if (pointerIndex <= TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT - 1) {

                // Current
                // Update touch state based the points given by the host OS (e.g., Android).
                for (int i = 0; i < pointCount; i++) {
                    int id = motionEvent.getPointerId (i);
                    PointF perspectivePosition = simulation.getBody(0).getPerspective().getPosition();
                    float perspectiveScale = simulation.getBody(0).getPerspective().getScale();
                    touchInteraction.touch[id].x = (motionEvent.getX (i) - (originPosition.x + perspectivePosition.x)) / perspectiveScale;
                    touchInteraction.touch[id].y = (motionEvent.getY (i) - (originPosition.y + perspectivePosition.y)) / perspectiveScale;
                    touchInteraction.touchTime[id] = java.lang.System.currentTimeMillis ();
                }

                // Update the state of the touched object based on the current touch interaction state.
                if (touchInteractionType == MotionEvent.ACTION_DOWN) {
                    touchInteractivity = new TouchInteractivity(); // Create on first!
                    touchInteraction.setType(TouchInteraction.TouchInteractionType.TOUCH);
                    touchInteraction.pointerId = pointerId;
                    touchInteractivity.addInteraction(touchInteraction);
                    currentBody.onTouchListener(touchInteractivity, touchInteraction);
                } else if (touchInteractionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO:
                } else if (touchInteractionType == MotionEvent.ACTION_MOVE) {
                    touchInteraction.setType(TouchInteraction.TouchInteractionType.MOVE);
                    touchInteraction.pointerId = pointerId;
                    touchInteractivity.addInteraction(touchInteraction);
                    currentBody.onMoveListener(touchInteractivity, touchInteraction);
                } else if (touchInteractionType == MotionEvent.ACTION_UP) {
                    touchInteraction.setType(TouchInteraction.TouchInteractionType.RELEASE);
                    touchInteraction.pointerId = pointerId;
                    touchInteractivity.addInteraction(touchInteraction);
                    currentBody.onReleaseListener(touchInteractivity, touchInteraction);
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

    public Canvas getCanvas() {
        return this.canvas;
    }

    public Paint getPaint() {
        return this.paint;
    }
}
