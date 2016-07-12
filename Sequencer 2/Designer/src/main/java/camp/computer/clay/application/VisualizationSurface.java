package camp.computer.clay.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import camp.computer.clay.model.interaction.Body;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.visualization.Visualization;
import camp.computer.clay.visualization.util.Geometry;

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
    private PointF originPosition = new PointF ();

    // Visualization
    private Visualization visualization;

    public VisualizationSurface(Context context) {
        super(context);
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

    }

    public void onResume() {
        Log.v("MapView", "onResume");

        surfaceHolder = getHolder ();
        getHolder().addCallback (this);

        // Create and start background Thread
        visualizationRenderer = new VisualizationRenderer(this);
        visualizationRenderer.setRunning (true);
        visualizationRenderer.start ();

//        // Start communications
//        getClay ().getCommunication ().startDatagramServer();

        updateSurfaceView();

    }

    public void onPause() {
        Log.v("MapView", "onPause");

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
//        super.onDraw(canvas);

        if (this.visualization == null || this.canvas == null) {
            return;
        }

        // <PERSPECTIVE>
        // Move the perspective
        this.canvas.save ();
        this.canvas.translate (
                originPosition.x + visualization.getSimulation().getBody(0).getPerspective().getPosition().x + (float) Application.getDisplay().getSensorAdapter().getRotationY(),
                originPosition.y + visualization.getSimulation().getBody(0).getPerspective().getPosition().y - (float) Application.getDisplay().getSensorAdapter().getRotationX()
        );
        // this.canvas.rotate((float) ApplicationView.getDisplay().getSensorAdapter().getRotationZ());
        this.canvas.scale (
                visualization.getSimulation().getBody(0).getPerspective().getScale(),
                visualization.getSimulation().getBody(0).getPerspective().getScale()
        );
        // </PERSPECTIVE>

        // TODO: Get Simulation
        // TODO: Get Simulation's selected Visualization

        // Draw the background
        this.canvas.drawColor(Color.WHITE);

        // Scene
        drawVisualization(visualization);

        // Paint the bitmap to the "primary" canvas.
//        canvas.drawBitmap (canvasBitmap, identityMatrix, null);
        canvas.save();
        canvas.concat(identityMatrix);
        canvas.drawBitmap(canvasBitmap, 0, 0, paint);
        canvas.restore();

        this.canvas.restore();
    }

    private void drawVisualization(Visualization visualization) {
        this.visualization.draw(this);

        Geometry.packCircles(getVisualization().getMachineImages(), 200, getVisualization().getCentroidPosition());
    }

    /**
     * The function run in background thread, not UI thread.
     */
    public void updateSurfaceView () {

        if (visualization == null) {
            return;
        }

        Canvas canvas = null;

        try {
            canvas = surfaceHolder.lockCanvas ();

            synchronized (surfaceHolder) {

                // Update
                visualization.update();

                // Draw
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

        Log.v("InteractionHistory", "Started touchPositions composition.");

        // Get active body
        Body currentBody = visualization.getSimulation().getBody(0);

        // Create touchPositions interaction
        TouchInteraction touchInteraction = new TouchInteraction(TouchInteraction.TouchInteractionType.NONE);
        touchInteraction.setBody(currentBody);

        if (pointerCount <= TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT) {
            if (pointerIndex <= TouchInteraction.MAXIMUM_TOUCH_POINT_COUNT - 1) {

                // Current
                // Update touchPositions state based the points given by the host OS (e.g., Android).
                for (int i = 0; i < pointerCount; i++) {
                    int id = motionEvent.getPointerId (i);
                    PointF perspectivePosition = visualization.getSimulation().getBody(0).getPerspective().getPosition();
                    float perspectiveScale = visualization.getSimulation().getBody(0).getPerspective().getScale();
                    touchInteraction.touchPositions[id].x = (motionEvent.getX (i) - (originPosition.x + perspectivePosition.x)) / perspectiveScale;
                    touchInteraction.touchPositions[id].y = (motionEvent.getY (i) - (originPosition.y + perspectivePosition.y)) / perspectiveScale;
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
                    touchInteraction.setType(TouchInteraction.TouchInteractionType.TOUCH);
                    touchInteraction.pointerId = pointerId;
                    currentBody.onStartInteractivity(touchInteraction);
                } else if (touchInteractionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO: Handle additional pointers after the first touchPositions!
                } else if (touchInteractionType == MotionEvent.ACTION_MOVE) {
                    touchInteraction.setType(TouchInteraction.TouchInteractionType.MOVE);
                    touchInteraction.pointerId = pointerId;
                    currentBody.onContinueInteractivity(touchInteraction);
                } else if (touchInteractionType == MotionEvent.ACTION_UP) {
                    touchInteraction.setType(TouchInteraction.TouchInteractionType.RELEASE);
                    touchInteraction.pointerId = pointerId;
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
