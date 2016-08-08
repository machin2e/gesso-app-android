package camp.computer.clay.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import camp.computer.clay.model.interactivity.Action;
import camp.computer.clay.model.architecture.Body;
import camp.computer.clay.visualization.architecture.Visualization;
import camp.computer.clay.visualization.util.geometry.Geometry;
import camp.computer.clay.visualization.util.geometry.Point;

public class Surface extends SurfaceView implements SurfaceHolder.Callback {

    // Visualization Rendering Context
    private Bitmap canvasBitmap = null;
    private Canvas canvas = null;
    private int canvasWidth;
    private int canvasHeight;
    private Paint paint = new Paint (Paint.ANTI_ALIAS_FLAG);
    private Matrix identityMatrix;

    // Visualization Renderer
    private SurfaceHolder surfaceHolder;
    private Renderer renderer;

    // Coordinate System (Grid)
    private Point originPosition = new Point();

    // Visualization
    private Visualization visualization;

    public Surface(Context context) {
        super(context);

        setFocusable(true);
    }

    public Surface(Context context, AttributeSet attrs) {
        super (context, attrs);
    }

    public Surface(Context context, AttributeSet attrs, int defStyle) {
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
//        // renderer.setRunning (false);
//        while (retry) {
//            try {
//                renderer.join ();
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
        renderer = new Renderer(this);
        renderer.setRunning (true);
        renderer.start ();

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
        renderer.setRunning (false);

        while (retry) {
            try {
                renderer.join ();
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
//                originPosition.x + visualization.getEnvironment().getBody(0).getPerspective().getPosition().x + (float) Application.getDisplay().getSensorAdapter().getRotationY(),
//                originPosition.y + visualization.getEnvironment().getBody(0).getPerspective().getPosition().y - (float) Application.getDisplay().getSensorAdapter().getRotationX()
                (float) originPosition.getX() + (float) visualization.getEnvironment().getBody(0).getPerspective().getPosition().getX(),
                (float) originPosition.getY() + (float) visualization.getEnvironment().getBody(0).getPerspective().getPosition().getY()
        );
        // this.canvas.rotate((float) ApplicationView.getDisplay().getSensorAdapter().getRotationZ());
        canvas.scale (
                (float) visualization.getEnvironment().getBody(0).getPerspective().getScale(),
                (float) visualization.getEnvironment().getBody(0).getPerspective().getScale()
        );
        // </PERSPECTIVE>

        // TODO: Get Environment
        // TODO: Get Environment's selected Visualization

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

    public Renderer getRenderer () {
        return this.renderer;
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

        visualization.getEnvironment().getBody(0).getPerspective().setWidth(screenWidth);
        visualization.getEnvironment().getBody(0).getPerspective().setHeight(screenHeight);
    }

    public Visualization getVisualization() {
        return this.visualization;
    }

    //----------------------------------------------------------------------------------------------
    // Action Model
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

        // Log.v("InteractionHistory", "Started touchPoints composition.");

        // Get active body
        Body body = visualization.getEnvironment().getBody(0);

        // Create touchPoints action
        Action action = new Action();

        if (pointerCount <= Action.MAX_TOUCH_POINT_COUNT) {
            if (pointerIndex <= Action.MAX_TOUCH_POINT_COUNT - 1) {

                // Current
                // Update touchPoints state based the points given by the host OS (e.g., Android).
                for (int i = 0; i < pointerCount; i++) {
                    int id = motionEvent.getPointerId (i);
                    Point perspectivePosition = body.getPerspective().getPosition();
                    double perspectiveScale = body.getPerspective().getScale();
                    action.touchPoints[id].setX((motionEvent.getX (i) - (originPosition.getX() + perspectivePosition.getX())) / perspectiveScale);
                    action.touchPoints[id].setY((motionEvent.getY (i) - (originPosition.getY() + perspectivePosition.getY())) / perspectiveScale);
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

                // Update the state of the touched object based on the current touchPoints action state.
                if (touchInteractionType == MotionEvent.ACTION_DOWN) {
                    action.setType(Action.Type.TOUCH);
                    action.pointerIndex = pointerId;
                    body.onAction(action);
                } else if (touchInteractionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO: Handle additional pointers after the first touchPoints!
                } else if (touchInteractionType == MotionEvent.ACTION_MOVE) {
                    action.setType(Action.Type.MOVE);
                    action.pointerIndex = pointerId;
                    body.onAction(action);
                } else if (touchInteractionType == MotionEvent.ACTION_UP) {
                    action.setType(Action.Type.RELEASE);
                    action.pointerIndex = pointerId;
                    body.onAction(action);
                } else if (touchInteractionType == MotionEvent.ACTION_POINTER_UP) {
                    // TODO: Handle additional pointers after the first touchPoints!
                } else if (touchInteractionType == MotionEvent.ACTION_CANCEL) {
                    // TODO:
                } else {
                    // TODO:
                }
            }
        }

        return true;
    }

    public static void drawLine(Point source, Point target, Surface surface) {

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        // Color
        canvas.drawLine(
                (float) source.getX(),
                (float) source.getY(),
                (float) target.getX(),
                (float) target.getY(),
                paint
        );

    }

    public static void drawCircle(Point position, double radius, double angle, Surface surface) {

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        // Color
        canvas.drawCircle(
                (float) position.getX(),
                (float) position.getY(),
                (float) radius,
                paint
        );

    }

    public static void drawText(Point position, String text, double size, Surface surface) {

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        // Style
        paint.setTextSize((float) size);

        // Style (Guaranteed)
        text = text.toUpperCase();
        paint.setStyle(Paint.Style.FILL);

        // Draw
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, (float) position.getX(), (float) position.getY() + bounds.height() / 2.0f, paint);
    }

    public static void drawRectangle(Point position, double angle, double width, double height, Surface surface) {

        // TODO: Absolute rotate at 0,0; Translate with position. (or, make algorithm to translate WRT another reference point)

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        // Calculate points before rotation
        Point topLeft = new Point(position.getX() - (width / 2.0f), position.getY() - (height / 2.0f));
        Point topRight = new Point(position.getX() + (width / 2.0f), position.getY() - (height / 2.0f));
        Point bottomRight = new Point(position.getX() + (width / 2.0f), position.getY() + (height / 2.0f));
        Point bottomLeft = new Point(position.getX() - (width / 2.0f), position.getY() + (height / 2.0f));

        // Calculate points after rotation
        Point rotatedTopLeft = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, topLeft), Geometry.calculateDistance(position, topLeft));
        Point rotatedTopRight = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, topRight), Geometry.calculateDistance(position, topRight));
        Point rotatedBottomRight = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, bottomRight), Geometry.calculateDistance(position, bottomRight));
        Point rotatedBottomLeft = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, bottomLeft), Geometry.calculateDistance(position, bottomLeft));

        // Draw points in shape
        android.graphics.Path path = new android.graphics.Path();
        path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
        path.moveTo((float) rotatedTopLeft.getX(), (float) rotatedTopLeft.getY());
        path.lineTo((float) rotatedTopRight.getX(), (float) rotatedTopRight.getY());
        path.lineTo((float) rotatedBottomRight.getX(), (float) rotatedBottomRight.getY());
        path.lineTo((float) rotatedBottomLeft.getX(), (float) rotatedBottomLeft.getY());
        path.close();

        canvas.drawPath(path, paint);
    }

    public static void drawTrianglePath(Point startPosition, Point stopPosition, double triangleWidth, double triangleHeight, Surface surface) {

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        double pathRotationAngle = Geometry.calculateRotationAngle(
                startPosition,
                stopPosition
        );

        double triangleRotationAngle = pathRotationAngle + 90.0f;

        double pathDistance = Geometry.calculateDistance(
                startPosition,
                stopPosition
        );

        int triangleCount = (int) (pathDistance / (triangleHeight + 15));
        double triangleSpacing2 = pathDistance / triangleCount;

        for (int k = 0; k <= triangleCount; k++) {

            // Calculate triangle position
            Point triangleCenterPosition2 = Geometry.calculatePoint(
                    startPosition,
                    pathRotationAngle,
                    k * triangleSpacing2
            );

            paint.setStyle(Paint.Style.FILL);
            Surface.drawTriangle(
                    triangleCenterPosition2,
                    triangleRotationAngle,
                    triangleWidth,
                    triangleHeight,
                    surface
            );
        }
    }

    /**
     * Draw regular shape.
     * <p>
     * Reference:
     * - https://en.wikipedia.org/wiki/Regular_polygon
     *
     * @param position
     * @param radius
     * @param sideCount
     * @param surface
     */
    public static void drawRegularPolygon(Point position, int radius, int sideCount, Surface surface) {

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        android.graphics.Path path = new android.graphics.Path();
        for (int i = 0; i < sideCount; i++) {

            Point vertexPosition = new Point(
                    (position.getX() + radius * Math.cos(2.0f * Math.PI * (double) i / (double) sideCount)),
                    (position.getY() + radius * Math.sin(2.0f * Math.PI * (double) i / (double) sideCount))
            );

            // Draw points in shape
            path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
            if (i == 0) {
                path.moveTo((float) vertexPosition.getX(), (float) vertexPosition.getY());
            }

            path.lineTo((float) vertexPosition.getX(), (float) vertexPosition.getY());
        }

//        path.lineTo(position.x, position.y);
        path.close();

        canvas.drawPath(path, paint);
    }

    public static void drawPolygon(List<Point> vertices, Surface surface) {

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        android.graphics.Path path = new android.graphics.Path();
        for (int i = 0; i < vertices.size(); i++) {

            // Draw points in shape
            path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
            if (i == 0) {
                path.moveTo((float) vertices.get(i).getX(), (float) vertices.get(i).getY());
            }

            path.lineTo((float) vertices.get(i).getX(), (float) vertices.get(i).getY());
        }

        path.close();

        canvas.drawPath(path, paint);
    }

    public static void drawTriangle(Point position, double angle, double width, double height, Surface surface) {

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        // Calculate points before rotation
        Point p1 = new Point(position.getX() + -(width / 2.0f), position.getY() + (height / 2.0f));
        Point p2 = new Point(position.getX() + 0, position.getY() - (height / 2.0f));
        Point p3 = new Point(position.getX() + (width / 2.0f), position.getY() + (height / 2.0f));

        // Calculate points after rotation
        Point rp1 = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, p1), (double) Geometry.calculateDistance(position, p1));
        Point rp2 = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, p2), (double) Geometry.calculateDistance(position, p2));
        Point rp3 = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, p3), (double) Geometry.calculateDistance(position, p3));

        android.graphics.Path path = new android.graphics.Path();
        path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
        path.moveTo((float) rp1.getX(), (float) rp1.getY());
        path.lineTo((float) rp2.getX(), (float) rp2.getY());
        path.lineTo((float) rp3.getX(), (float) rp3.getY());
        path.close();

        canvas.drawPath(path, paint);
    }
}
