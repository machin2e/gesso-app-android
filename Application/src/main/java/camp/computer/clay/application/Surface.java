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

import camp.computer.clay.model.architecture.Actor;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.scene.architecture.Scene;
import camp.computer.clay.scene.util.Visibility;
import camp.computer.clay.scene.util.geometry.Circle;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Line;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Polygon;
import camp.computer.clay.scene.util.geometry.Rectangle;
import camp.computer.clay.scene.util.geometry.Triangle;

public class Surface extends SurfaceView implements SurfaceHolder.Callback {

    // Scene Rendering Context
    private Bitmap canvasBitmap = null;
    private Canvas canvas = null;
    private int canvasWidth;
    private int canvasHeight;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Matrix identityMatrix;

    // Scene Renderer
    private SurfaceHolder surfaceHolder;
    private Renderer renderer;

    // Coordinate System (Grid)
    private Point originPosition = new Point();

    // Scene
    private Scene scene;

    public Surface(Context context) {
        super(context);

        setFocusable(true);
    }

    public Surface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Surface(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        canvasWidth = getWidth();
        canvasHeight = getHeight();
        canvasBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        canvas.setBitmap(canvasBitmap);

        identityMatrix = new Matrix();

        // Center the scene coordinate system
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

        surfaceHolder = getHolder();
        getHolder().addCallback(this);

        // Create and start background Thread
        renderer = new Renderer(this);
        renderer.setRunning(true);
        renderer.start();

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
        renderer.setRunning(false);

        while (retry) {
            try {
                renderer.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void doDraw(Canvas canvas) {
        setCanvas(canvas);

        if (this.scene == null || this.canvas == null) {
            return;
        }

        // <PERSPECTIVE>
        // Adjust the perspective
        canvas.save();
        canvas.translate(
//                (float) originPosition.getX() + (float) scene.getSpace().getActor(0).getCamera().getCoordinate().getX() + (float) Application.getDisplay().getSensorAdapter().getRotationY(),
//                (float) originPosition.getY() + (float) scene.getSpace().getActor(0).getCamera().getCoordinate().getY() - (float) Application.getDisplay().getSensorAdapter().getRotationX()
                (float) originPosition.getX() + (float) scene.getModel().getActor(0).getCamera().getPosition().getX(),
                (float) originPosition.getY() + (float) scene.getModel().getActor(0).getCamera().getPosition().getY()
        );
        // this.canvas.rotate((float) ApplicationView.getDisplay().getSensorAdapter().getRotationZ());
        canvas.scale(
                (float) scene.getModel().getActor(0).getCamera().getScale(),
                (float) scene.getModel().getActor(0).getCamera().getScale()
        );
        // </PERSPECTIVE>

        // TODO: Get Space
        // TODO: Get Space's selected Scene

        // Draw the background
        canvas.drawColor(Color.WHITE);

        // Scene
        canvas.save();
        getScene().draw(this);
        canvas.restore();

        canvas.restore();

        // Annotation
        if (scene.goalVisibility == Visibility.VISIBLE) {

            canvas.save();

            // Project Title
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(100);

            String projectTitleText = "GOAL";
            Rect projectTitleTextBounds = new Rect();
            paint.getTextBounds(projectTitleText, 0, projectTitleText.length(), projectTitleTextBounds);
            canvas.drawText(
                    projectTitleText,
                    (getWidth() / 2.0f) - (projectTitleTextBounds.width() / 2.0f),
                    (250) - (projectTitleTextBounds.height() / 2.0f),
                    paint
            );

            // Menu
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(5.0f);

            canvas.drawLine(
                    (getWidth() / 2.0f) - 75f,
                    getHeight() - 250f,
                    (getWidth() / 2.0f) + 75f,
                    getHeight() - 250f,
                    paint

            );

            canvas.drawLine(
                    (getWidth() / 2.0f) - 75f,
                    getHeight() - 215f,
                    (getWidth() / 2.0f) + 75f,
                    getHeight() - 215f,
                    paint

            );

            canvas.drawLine(
                    (getWidth() / 2.0f) - 75f,
                    getHeight() - 180f,
                    (getWidth() / 2.0f) + 75f,
                    getHeight() - 180f,
                    paint

            );

            canvas.restore();
        }

        // Paint the bitmap to the "primary" canvas.
        canvas.drawBitmap(canvasBitmap, identityMatrix, null);

        /*
        // Alternative to the above
        canvas.save();
        canvas.concat(identityMatrix);
        canvas.drawBitmap(canvasBitmap, 0, 0, paint);
        canvas.restore();
        */
    }

    /**
     * The function run in background thread, not UI thread.
     */
    public void update() {

        if (scene == null) {
            return;
        }

        Canvas canvas = null;

        try {
            canvas = getHolder().lockCanvas();

            if (canvas != null) {
                synchronized (getHolder()) {

                    // Update
                    scene.update();

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

    public Renderer getRenderer() {
        return this.renderer;
    }

    private void setCanvas(Canvas canvas) {
        this.canvas = canvas;
    }

    public Canvas getCanvas() {
        return this.canvas;
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void setScene(Scene scene) {
        this.scene = scene;

        // Get screen width and height of the device
        DisplayMetrics metrics = new DisplayMetrics();
        Application.getDisplay().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        scene.getModel().getActor(0).getCamera().setWidth(screenWidth);
        scene.getModel().getActor(0).getCamera().setHeight(screenHeight);
    }

    public Scene getScene() {
        return this.scene;
    }

    //----------------------------------------------------------------------------------------------
    // Action Construct
    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        // - Motion events contain information about all of the pointers that are currently active
        //   even if some of them have not moved since the getStopAction event was delivered.
        //
        // - The number of pointers only ever changes by one as individual pointers go up and down,
        //   except when the gesture is canceled.
        //
        // - Use the getPointerId(int) method to obtain the pointer id of a pointer to track it
        //   across all subsequent motion events in a gesture. Then for successive motion events,
        //   use the findPointerIndex(int) method to obtain the pointer index for a given pointer
        //   id in that motion event.

        int pointerIndex = ((motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
        int pointerId = motionEvent.getPointerId(pointerIndex);
        int touchInteractionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);
        int pointerCount = motionEvent.getPointerCount();

        if (this.scene == null) {
            return false;
        }

        // Log.v("InteractionHistory", "Started pointerCoordinates composition.");

        // Get active actor
        Actor actor = scene.getModel().getActor(0);

        // Create pointerCoordinates action
        Action action = new Action();

        if (pointerCount <= Action.MAXIMUM_POINT_COUNT) {
            if (pointerIndex <= Action.MAXIMUM_POINT_COUNT - 1) {

                // Current
                // Update pointerCoordinates state based the pointerCoordinates given by the host OS (e.g., Android).
                for (int i = 0; i < pointerCount; i++) {
                    int id = motionEvent.getPointerId(i);
                    Point perspectivePosition = actor.getCamera().getPosition();
                    double perspectiveScale = actor.getCamera().getScale();
                    action.pointerCoordinates[id].setX((motionEvent.getX(i) - (originPosition.getX() + perspectivePosition.getX())) / perspectiveScale);
                    action.pointerCoordinates[id].setY((motionEvent.getY(i) - (originPosition.getY() + perspectivePosition.getY())) / perspectiveScale);
                }

                // ACTION_DOWN is called only for the getStartAction pointer that touches the screen. This
                // starts the gesture. The pointer data for this pointer is always at index 0 in
                // the MotionEvent.
                //
                // ACTION_POINTER_DOWN is called for extra pointers that enter the screen beyond
                // the getStartAction. The pointer data for this pointer is at the index returned by
                // getActionIndex().
                //
                // ACTION_MOVE is sent when a change has happened during a press gesture for any
                // pointer.
                //
                // ACTION_POINTER_UP is sent when a non-primary pointer goes up.
                //
                // ACTION_UP is sent when the getStopAction pointer leaves the screen.
                //
                // REFERENCES:
                // - https://developer.android.com/training/gestures/multi.html

                // Update the state of the touched object based on the current pointerCoordinates action state.
                if (touchInteractionType == MotionEvent.ACTION_DOWN) {
                    action.setType(Action.Type.SELECT);
                    action.pointerIndex = pointerId;
                    actor.doAction(action);
                } else if (touchInteractionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO: Handle additional pointers after the getStartAction pointerCoordinates!
                } else if (touchInteractionType == MotionEvent.ACTION_MOVE) {
                    action.setType(Action.Type.MOVE);
                    action.pointerIndex = pointerId;
                    actor.doAction(action);
                } else if (touchInteractionType == MotionEvent.ACTION_UP) {
                    action.setType(Action.Type.UNSELECT);
                    action.pointerIndex = pointerId;
                    actor.doAction(action);
                } else if (touchInteractionType == MotionEvent.ACTION_POINTER_UP) {
                    // TODO: Handle additional pointers after the getStartAction pointerCoordinates!
                } else if (touchInteractionType == MotionEvent.ACTION_CANCEL) {
                    // TODO:
                } else {
                    // TODO:
                }
            }
        }

        return true;
    }

    public static void drawLine(Line line, Surface surface) {

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        // Color
        canvas.drawLine(
                (float) line.getSource().getX(),
                (float) line.getSource().getY(),
                (float) line.getSource().getX(),
                (float) line.getSource().getY(),
                paint
        );

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

    public static void drawCircle(Circle circle, Surface surface) {

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        // Calculate rotated coordinate
        Point rotatedPosition = null;
        if (circle.getCoordinate().getOrigin() != null) {
//            Log.v("RectDraw", "!= null");
//            rotatedPosition = Geometry.calculatePoint(circle.getCoordinate().getOrigin(), circle.getRotation() + Geometry.calculateRotationAngle(position.getOrigin(), position), Geometry.calculateDistance(position.getOrigin(), position));
            rotatedPosition = Geometry.calculateRotatedPoint(circle.getCoordinate().getOrigin(), circle.getCoordinate().getOrigin().getRotation(), circle.getCoordinate());
        } else {
//            Log.v("RectDraw", "NULL");
            rotatedPosition = new Point(circle.getCoordinate());
        }

        // Color
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor(circle.getColor()));

        canvas.drawCircle(
                (float) rotatedPosition.getX(),
                (float) rotatedPosition.getY(),
                (float) circle.getRadius(),
                paint
        );

        // Draw pointerCoordinates in shape
        if (circle.getOutlineThickness() > 0) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.parseColor(circle.getOutlineColor()));
            paint.setStrokeWidth((float) circle.getOutlineThickness());

            canvas.drawCircle(
                    (float) rotatedPosition.getX(),
                    (float) rotatedPosition.getY(),
                    (float) circle.getRadius(),
                    paint
            );
        }

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

    //public static void drawRectangle(Point position, double angle, double width, double height, Surface surface) {
    public static void drawRectangle(Point position2, double angle, double width, double height, Surface surface) {

        // TODO: Absolute rotate at 0,0; Translate with position. (or, make algorithm to translate WRT another reference point)

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        // Calculate center point
        Point referencePoint = position2.getOrigin();
        if (referencePoint == null) {
            referencePoint = new Point();
        }

//        Point position = Geometry.calculateRotatedPoint(referencePoint, referencePoint.getRotation(), position2);
        Point position = position2;

        float dx = (float) (Geometry.calculateDistance(referencePoint, position2) * Math.cos(Math.toRadians(Geometry.calculateRotationAngle(referencePoint, position2))));
        float dy = (float) (Geometry.calculateDistance(referencePoint, position2) * Math.sin(Math.toRadians(Geometry.calculateRotationAngle(referencePoint, position2))));

//        // Rotate shape about its center point
//        Point rotatedPosition = null;
//        if (position.getOrigin() != null) {
////            Log.v("RectDraw", "!= null");
//            rotatedPosition = Geometry.calculatePoint(position.getOrigin(), angle + Geometry.calculateRotationAngle(position.getOrigin(), position), Geometry.calculateDistance(position.getOrigin(), position));
//        } else {
////            Log.v("RectDraw", "NULL");
//            rotatedPosition = new Point(position);
//        }

//        // Calculate coordinates of Rectangle
//        Point originalTopLeft = new Point(position.getX() - (width / 2.0f), position.getY() - (height / 2.0f));
//        Point originalTopRight = new Point(position.getX() + (width / 2.0f), position.getY() - (height / 2.0f));
//        Point originalBottomRight = new Point(position.getX() + (width / 2.0f), position.getY() + (height / 2.0f));
//        Point originalBottomLeft = new Point(position.getX() - (width / 2.0f), position.getY() + (height / 2.0f));
//
//        // Rotate shape about its center point
//        Point topLeft = Geometry.calculateRotatedPoint(position, angle, originalTopLeft);
//        Point topRight = Geometry.calculateRotatedPoint(position, angle, originalTopRight);
//        Point bottomRight = Geometry.calculateRotatedPoint(position, angle, originalBottomRight);
//        Point bottomLeft = Geometry.calculateRotatedPoint(position, angle, originalBottomLeft);
//
//        // Rotate shape vertices about the shape's reference point
//        Point referencePoint = position.getOrigin();
////        if (referencePoint == null) {
////            referencePoint = new Point();
////        }
//        Point rotatedTopLeft = Geometry.calculateRotatedPoint(referencePoint, referencePoint.getRotation(), topLeft);
//        Point rotatedTopRight = Geometry.calculateRotatedPoint(referencePoint, referencePoint.getRotation(), topRight);
//        Point rotatedBottomRight = Geometry.calculateRotatedPoint(referencePoint, referencePoint.getRotation(), bottomRight);
//        Point rotatedBottomLeft = Geometry.calculateRotatedPoint(referencePoint, referencePoint.getRotation(), bottomLeft);

        // Calculate coordinates of Rectangle before rotation and translation
        Point originalTopLeft = new Point(position.getX() - (width / 2.0f), position.getY() - (height / 2.0f));
        Point originalTopRight = new Point(position.getX() + (width / 2.0f), position.getY() - (height / 2.0f));
        Point originalBottomRight = new Point(position.getX() + (width / 2.0f), position.getY() + (height / 2.0f));
        Point originalBottomLeft = new Point(position.getX() - (width / 2.0f), position.getY() + (height / 2.0f));

        // Rotate shape about its center point
        Point rotatedTopLeft = Geometry.calculateRotatedPoint(position, angle, originalTopLeft);
        Point rotatedTopRight = Geometry.calculateRotatedPoint(position, angle, originalTopRight);
        Point rotatedBottomRight = Geometry.calculateRotatedPoint(position, angle, originalBottomRight);
        Point rotatedBottomLeft = Geometry.calculateRotatedPoint(position, angle, originalBottomLeft);

        // Rotate shape vertices about the shape's reference point
//        Point referencePoint = position.getOrigin();
//        if (referencePoint == null) {
//            referencePoint = new Point();
//        }
//        Point rotatedTopLeft = Geometry.calculateRotatedPoint(referencePoint, referencePoint.getRotation(), topLeft);
//        Point rotatedTopRight = Geometry.calculateRotatedPoint(referencePoint, referencePoint.getRotation(), topRight);
//        Point rotatedBottomRight = Geometry.calculateRotatedPoint(referencePoint, referencePoint.getRotation(), bottomRight);
//        Point rotatedBottomLeft = Geometry.calculateRotatedPoint(referencePoint, referencePoint.getRotation(), bottomLeft);

//        float dx = (float) (Geometry.calculateDistance(referencePoint, position) * Math.cos(Math.toRadians(Geometry.calculateRotationAngle(referencePoint, position))));
//        float dy = (float) (Geometry.calculateDistance(referencePoint, position) * Math.sin(Math.toRadians(Geometry.calculateRotationAngle(referencePoint, position))));

        // Draw pointerCoordinates in shape
        android.graphics.Path path = new android.graphics.Path();
        path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
        path.moveTo((float) rotatedTopLeft.getX(), (float) rotatedTopLeft.getY());
        path.lineTo((float) rotatedTopRight.getX(), (float) rotatedTopRight.getY());
        path.lineTo((float) rotatedBottomRight.getX(), (float) rotatedBottomRight.getY());
        path.lineTo((float) rotatedBottomLeft.getX(), (float) rotatedBottomLeft.getY());
        path.close();

        canvas.drawPath(path, paint);
    }

    public static void drawRectangle(Rectangle rectangle, Surface surface) {

        if (rectangle.isVisible()) {

            Canvas canvas = surface.getCanvas();
            Paint paint = surface.getPaint();

            // Rotate shape about its center point
            Point topLeft = Geometry.calculateRotatedPoint(rectangle.getCoordinate(), rectangle.getRotation(), rectangle.getTopLeft());
            Point topRight = Geometry.calculateRotatedPoint(rectangle.getCoordinate(), rectangle.getRotation(), rectangle.getTopRight());
            Point bottomRight = Geometry.calculateRotatedPoint(rectangle.getCoordinate(), rectangle.getRotation(), rectangle.getBottomRight());
            Point bottomLeft = Geometry.calculateRotatedPoint(rectangle.getCoordinate(), rectangle.getRotation(), rectangle.getBottomLeft());

//            Point topLeft = Geometry.calculateRotatedPoint(rectangle.getCoordinate(), rectangle.getCoordinate().getRelativeRotation(), rectangle.getTopLeft());
//            Point topRight = Geometry.calculateRotatedPoint(rectangle.getCoordinate(), rectangle.getCoordinate().getRelativeRotation(), rectangle.getTopRight());
//            Point bottomRight = Geometry.calculateRotatedPoint(rectangle.getCoordinate(), rectangle.getCoordinate().getRelativeRotation(), rectangle.getBottomRight());
//            Point bottomLeft = Geometry.calculateRotatedPoint(rectangle.getCoordinate(), rectangle.getCoordinate().getRelativeRotation(), rectangle.getBottomLeft());

//            Point topLeft = rectangle.getTopLeft();
//            Point topRight = rectangle.getTopRight();
//            Point bottomRight = rectangle.getBottomRight();
//            Point bottomLeft = rectangle.getBottomLeft();

            // Rotate shape vertices about the shape's origin (reference) point
            Point originCoordinate = rectangle.getCoordinate().getOrigin();
            Point rotatedTopLeft = Geometry.calculateRotatedPoint(originCoordinate, originCoordinate.getRotation(), topLeft);
            Point rotatedTopRight = Geometry.calculateRotatedPoint(originCoordinate, originCoordinate.getRotation(), topRight);
            Point rotatedBottomRight = Geometry.calculateRotatedPoint(originCoordinate, originCoordinate.getRotation(), bottomRight);
            Point rotatedBottomLeft = Geometry.calculateRotatedPoint(originCoordinate, originCoordinate.getRotation(), bottomLeft);
//            Point originCoordinate = rectangle.getCoordinate();
//            Point rotatedTopLeft = Geometry.calculateRotatedPoint(originCoordinate, originCoordinate.getRotation(), topLeft);
//            Point rotatedTopRight = Geometry.calculateRotatedPoint(originCoordinate, originCoordinate.getRotation(), topRight);
//            Point rotatedBottomRight = Geometry.calculateRotatedPoint(originCoordinate, originCoordinate.getRotation(), bottomRight);
//            Point rotatedBottomLeft = Geometry.calculateRotatedPoint(originCoordinate, originCoordinate.getRotation(), bottomLeft);
//            Point rotatedTopLeft = topLeft;
//            Point rotatedTopRight = topRight;
//            Point rotatedBottomRight = bottomRight;
//            Point rotatedBottomLeft = bottomLeft;

            // Draw pointerCoordinates in shape
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.parseColor(rectangle.getColor()));

            android.graphics.Path path = new android.graphics.Path();
            path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
            path.moveTo((float) rotatedTopLeft.getX(), (float) rotatedTopLeft.getY());
            path.lineTo((float) rotatedTopRight.getX(), (float) rotatedTopRight.getY());
            path.lineTo((float) rotatedBottomRight.getX(), (float) rotatedBottomRight.getY());
            path.lineTo((float) rotatedBottomLeft.getX(), (float) rotatedBottomLeft.getY());
            path.close();

            canvas.drawPath(path, paint);

            // Draw pointerCoordinates in shape
            if (rectangle.getOutlineThickness() > 0) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setColor(Color.parseColor(rectangle.getOutlineColor()));
                paint.setStrokeWidth((float) rectangle.getOutlineThickness());

                path = new android.graphics.Path();
                path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
                path.moveTo((float) rotatedTopLeft.getX(), (float) rotatedTopLeft.getY());
                path.lineTo((float) rotatedTopRight.getX(), (float) rotatedTopRight.getY());
                path.lineTo((float) rotatedBottomRight.getX(), (float) rotatedBottomRight.getY());
                path.lineTo((float) rotatedBottomLeft.getX(), (float) rotatedBottomLeft.getY());
                path.close();

                canvas.drawPath(path, paint);
            }

        }
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

            // Draw pointerCoordinates in shape
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

    public static void drawPolygon(Polygon polygon, Surface surface) {
        drawPolygon(polygon.getVertices(), surface);
    }

    public static void drawPolygon(List<Point> vertices, Surface surface) {

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        android.graphics.Path path = new android.graphics.Path();
        for (int i = 0; i < vertices.size(); i++) {

            // Draw pointerCoordinates in shape
            path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
            if (i == 0) {
                path.moveTo((float) vertices.get(i).getX(), (float) vertices.get(i).getY());
            }

            path.lineTo((float) vertices.get(i).getX(), (float) vertices.get(i).getY());
        }

        path.close();

        canvas.drawPath(path, paint);
    }

    public static void drawTriangle(Triangle triangle, Surface surface) {
        // TODO:
    }

    public static void drawTriangle(Point position, double angle, double width, double height, Surface surface) {

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        // Calculate pointerCoordinates before rotation
        Point p1 = new Point(position.getX() + -(width / 2.0f), position.getY() + (height / 2.0f));
        Point p2 = new Point(position.getX() + 0, position.getY() - (height / 2.0f));
        Point p3 = new Point(position.getX() + (width / 2.0f), position.getY() + (height / 2.0f));

        // Calculate pointerCoordinates after rotation
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
