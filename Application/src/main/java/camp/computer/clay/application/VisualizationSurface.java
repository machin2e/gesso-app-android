package camp.computer.clay.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import camp.computer.clay.model.interactivity.Body;
import camp.computer.clay.model.interactivity.TouchInteraction;
import camp.computer.clay.visualization.arch.Visualization;
import camp.computer.clay.visualization.util.Point;

public class VisualizationSurface extends SurfaceView implements SurfaceHolder.Callback {

    // Visualization Rendering Context
    private Bitmap canvasBitmap = null;
    private Canvas canvas = null;
    private int canvasWidth;
    private int canvasHeight;
    private Paint paint = new Paint (Paint.ANTI_ALIAS_FLAG);
    private Matrix identityMatrix;

    // Visualization Renderer
    private SurfaceHolder surfaceHolder;
    private VisualizationRenderer visualizationRenderer;

    // Coordinate System (Grid)
    private Point originPosition = new Point();

    // Visualization
    private Visualization visualization;

    public VisualizationSurface(Context context) {
        super(context);

        setFocusable(true);
    }

    public VisualizationSurface(Context context, AttributeSet attrs) {
        super (context, attrs);
    }

    public VisualizationSurface(Context context, AttributeSet attrs, int defStyle) {
        super (context, attrs, defStyle);
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
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        // Kill the background Thread
//        boolean retry = true;
//        // visualizationRenderer.setRunning (false);
//        while (retry) {
//            try {
//                visualizationRenderer.join ();
//                retry = false;
//            } catch (InterruptedException e) {
//                e.printStackTrace ();
//            }
//        }
    }

    public void onResume() {
        // Log.v("MapView", "onResume");

        surfaceHolder = getHolder ();
        getHolder().addCallback (this);

        // Create and start background Thread
        visualizationRenderer = new VisualizationRenderer(this);
        visualizationRenderer.setRunning (true);
        visualizationRenderer.start ();

//        // Start communications
//        getClay ().getCommunication ().startDatagramServer();

        // Remove this?
        update();

    }

    public void onPause() {
        // Log.v("MapView", "onPause");

        // Pause the communications
//        getClay ().getCommunication ().stopDatagramServer (); // HACK: This was commented out to prevent the server from "crashing" into an invalid state!

        // Kill the background Thread
        boolean retry = true;
        visualizationRenderer.setRunning (false);

        while (retry) {
            try {
                visualizationRenderer.join ();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace ();
            }
        }
    }

    protected void doDraw(Canvas canvas) {
        setCanvas(canvas);

        if (this.visualization == null || this.canvas == null) {
            return;
        }

        // <PERSPECTIVE>
        // Adjust the perspective
        canvas.save ();
        canvas.translate (
//                originPosition.x + visualization.getSimulation().getBody(0).getPerspective().getPosition().x + (float) Application.getDisplay().getSensorAdapter().getRotationY(),
//                originPosition.y + visualization.getSimulation().getBody(0).getPerspective().getPosition().y - (float) Application.getDisplay().getSensorAdapter().getRotationX()
                (float) originPosition.getX() + (float) visualization.getSimulation().getBody(0).getPerspective().getPosition().getX(),
                (float) originPosition.getY() + (float) visualization.getSimulation().getBody(0).getPerspective().getPosition().getY()
        );
        // this.canvas.rotate((float) ApplicationView.getDisplay().getSensorAdapter().getRotationZ());
        canvas.scale (
                (float) visualization.getSimulation().getBody(0).getPerspective().getScale(),
                (float) visualization.getSimulation().getBody(0).getPerspective().getScale()
        );
        // </PERSPECTIVE>

        // TODO: Get Simulation
        // TODO: Get Simulation's selected Visualization

        // Draw the background
        canvas.drawColor(Color.WHITE);

        // Scene
        getVisualization().draw(this);

        // Paint the bitmap to the "primary" canvas.
        canvas.drawBitmap (canvasBitmap, identityMatrix, null);
//        canvas.save();
//        canvas.concat(identityMatrix);
//        canvas.drawBitmap(canvasBitmap, 0, 0, paint);
//        canvas.restore();

        canvas.restore();
    }

    /**
     * The function run in background thread, not UI thread.
     */
    public void update() {

        if (visualization == null) {
            return;
        }

        Canvas canvas = null;

        try {
            canvas = getHolder().lockCanvas();

            if (canvas != null) {
                synchronized (getHolder()) {

                    // Update
                    visualization.update();

                    // Draw
                    doDraw(canvas);
                }
            }
        } finally {
            if (canvas != null) {
                getHolder().unlockCanvasAndPost(canvas);
            }
        }
    }

    public VisualizationRenderer getRenderer () {
        return this.visualizationRenderer;
    }

    private void setCanvas (Canvas canvas) {
        this.canvas = canvas;
    }

    public Canvas getCanvas() {
        return this.canvas;
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void setVisualization(Visualization visualization) {
        this.visualization = visualization;

        // Get screen width and height of the device
        DisplayMetrics metrics = new DisplayMetrics();
        Application.getDisplay().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        visualization.getSimulation().getBody(0).getPerspective().setWidth(screenWidth);
        visualization.getSimulation().getBody(0).getPerspective().setHeight(screenHeight);
    }

    public Visualization getVisualization() {
        return this.visualization;
    }

    //----------------------------------------------------------------------------------------------
    // Interaction Model
    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onTouchEvent (MotionEvent motionEvent) {

        // - Motion events contain information about all of the pointers that are currently active
        //   even if some of them have not moved since the last event was delivered.
        //
        // - The number of pointers only ever changes by one as individual pointers go up and down,
        //   except when the gesture is canceled.
        //
        // - Use the getPointerId(int) method to obtain the pointer id of a pointer to track it
        //   across all subsequent motion events in a gesture. Then for successive motion events,
        //   use the findPointerIndex(int) method to obtain the pointer index for a given pointer
        //   id in that motion event.

        int pointerIndex = ((motionEvent.getAction () & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
        int pointerId = motionEvent.getPointerId (pointerIndex);
        int touchInteractionType = (motionEvent.getAction () & MotionEvent.ACTION_MASK);
        int pointerCount = motionEvent.getPointerCount ();

        if (this.visualization == null) {
            return false;
        }

        // Log.v("InteractionHistory", "Started touchPositions composition.");

        // Get active body
        Body currentBody = visualization.getSimulation().getBody(0);

        // Create touchPositions interaction
        TouchInteraction touchInteraction = new TouchInteraction(TouchInteraction.Type.NONE);
        touchInteraction.setBody(currentBody);

        if (pointerCount <= TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT) {
            if (pointerIndex <= TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT - 1) {

                // Current
                // Update touchPositions state based the points given by the host OS (e.g., Android).
                for (int i = 0; i < pointerCount; i++) {
                    int id = motionEvent.getPointerId (i);
                    Point perspectivePosition = visualization.getSimulation().getBody(0).getPerspective().getPosition();
                    double perspectiveScale = visualization.getSimulation().getBody(0).getPerspective().getScale();
                    touchInteraction.touchPositions[id].setX((motionEvent.getX (i) - (originPosition.getX() + perspectivePosition.getX())) / perspectiveScale);
                    touchInteraction.touchPositions[id].setY((motionEvent.getY (i) - (originPosition.getY() + perspectivePosition.getY())) / perspectiveScale);
                }

                // ACTION_DOWN is called only for the first pointer that touches the screen. This
                // starts the gesture. The pointer data for this pointer is always at index 0 in
                // the MotionEvent.
                //
                // ACTION_POINTER_DOWN is called for extra pointers that enter the screen beyond
                // the first. The pointer data for this pointer is at the index returned by
                // getActionIndex().
                //
                // ACTION_MOVE is sent when a change has happened during a press gesture for any
                // pointer.
                //
                // ACTION_POINTER_UP is sent when a non-primary pointer goes up.
                //
                // ACTION_UP is sent when the last pointer leaves the screen.
                //
                // REFERENCES:
                // - https://developer.android.com/training/gestures/multi.html

                // Update the state of the touched object based on the current touchPositions interaction state.
                if (touchInteractionType == MotionEvent.ACTION_DOWN) {
                    touchInteraction.setType(TouchInteraction.Type.TOUCH);
                    touchInteraction.pointerIndex = pointerId;
                    currentBody.onStartInteractivity(touchInteraction);
                } else if (touchInteractionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO: Handle additional pointers after the first touchPositions!
                } else if (touchInteractionType == MotionEvent.ACTION_MOVE) {
                    touchInteraction.setType(TouchInteraction.Type.MOVE);
                    touchInteraction.pointerIndex = pointerId;
                    currentBody.onContinueInteractivity(touchInteraction);
                } else if (touchInteractionType == MotionEvent.ACTION_UP) {
                    touchInteraction.setType(TouchInteraction.Type.RELEASE);
                    touchInteraction.pointerIndex = pointerId;
                    currentBody.onCompleteInteractivity(touchInteraction);
                } else if (touchInteractionType == MotionEvent.ACTION_POINTER_UP) {
                    // TODO: Handle additional pointers after the first touchPositions!
                } else if (touchInteractionType == MotionEvent.ACTION_CANCEL) {
                    // TODO:
                } else {
                    // TODO:
                }
            }
        }

        return true;
    }
}
