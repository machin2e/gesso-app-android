package camp.computer.clay.application.graphics;

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

import camp.computer.clay.application.Application;
import camp.computer.clay.model.Actor;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.util.geometry.Circle;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Line;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.geometry.Polygon;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.geometry.Triangle;
import camp.computer.clay.util.image.Space;

public class Display extends SurfaceView implements SurfaceHolder.Callback {

    // Space Rendering Context
    private Bitmap canvasBitmap = null;
    public Canvas canvas = null;
    private int canvasWidth;
    private int canvasHeight;
    public Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Matrix identityMatrix;

    // Space DisplayOutput
    private SurfaceHolder surfaceHolder;
    private DisplayOutput displayOutput;

    // Coordinate System (Grid)
    private Point originPosition = new Point();

    // Space
    private Space space;

    public Display(Context context) {
        super(context);
        setFocusable(true);
    }

    public Display(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public Display(Context context, AttributeSet attrs, int defStyle) {
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

        // Center the parentSpace coordinate system
        //originPosition.setAbsolute(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f);
        originPosition.set(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
//        // Kill the background Thread
//        boolean retry = true;
//        // displayOutput.setRunning (false);
//        while (retry) {
//            try {
//                displayOutput.join ();
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
        displayOutput = new DisplayOutput(this);
        displayOutput.setRunning(true);
        displayOutput.start();

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
        displayOutput.setRunning(false);

        while (retry) {
            try {
                displayOutput.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    protected void doDraw(Canvas canvas) {
        setCanvas(canvas);

        if (this.space == null || this.canvas == null) {
            return;
        }

        canvas.save();

        // Adjust the perspective
        adjustCamera();

        // Draw the background
        canvas.drawColor(Color.WHITE);

        // Space
        getSpace().draw(this);

        canvas.restore();

//        // Annotation
//        if (space.getTitleVisibility().getValue() == Visibility.Value.VISIBLE) {
//
//            canvas.save();
//
//            // Project Title
//            paint.setColor(Color.BLACK);
//            paint.setStyle(Paint.Style.FILL);
//            paint.setTextSize(100);
//
//            String projectTitleText = space.getTitleText(); // "Goal";
//            Rect projectTitleTextBounds = new Rect();
//            paint.getTextBounds(projectTitleText, 0, projectTitleText.length(), projectTitleTextBounds);
//            canvas.drawText(projectTitleText, (getWidth() / 2.0f) - (projectTitleTextBounds.width() / 2.0f), (250) - (projectTitleTextBounds.height() / 2.0f), paint);
//
//            /*
//            // Menu
//            paint.setColor(Color.BLACK);
//            paint.setStyle(Paint.Style.FILL);
//            paint.setStrokeWidth(5.0f);
//
//            canvas.drawLine((getWidth() / 2.0f) - 75f, getHeight() - 250f, (getWidth() / 2.0f) + 75f, getHeight() - 250f, paint
//
//            );
//
//            canvas.drawLine((getWidth() / 2.0f) - 75f, getHeight() - 215f, (getWidth() / 2.0f) + 75f, getHeight() - 215f, paint
//
//            );
//
//            canvas.drawLine((getWidth() / 2.0f) - 75f, getHeight() - 180f, (getWidth() / 2.0f) + 75f, getHeight() - 180f, paint
//
//            );
//            */
//
//            canvas.restore();
//        }

        canvas.save();

        // <FPS_LABEL>
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(35);

        String fpsText = "FPS: " + (int) getDisplayOutput().getFramesPerSecond();
        Rect fpsTextBounds = new Rect();
        paint.getTextBounds(fpsText, 0, fpsText.length(), fpsTextBounds);
        canvas.drawText(fpsText, 25, 25 + fpsTextBounds.height(), paint);
        // </FPS_LABEL>

        canvas.restore();

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
     * Adjust the perspective
     */
    private void adjustCamera() {
        //canvas.translate((float) originPosition.getAbsoluteX() + (float) parentSpace.getEntity().getActor(0).getCamera().getPosition().getAbsoluteX() + (float) Application.getView().getOrientationInput().getRotationY(), (float) originPosition.getAbsoluteY() + (float) parentSpace.getEntity().getActor(0).getCamera().getPosition().getAbsoluteY() - (float) Application.getView().getOrientationInput().getRotationX());
        canvas.translate((float) originPosition.x + (float) space.getEntity().getActor(0).getCamera().getPosition().x /* + (float) Application.getView().getOrientationInput().getRotationY()*/, (float) originPosition.y + (float) space.getEntity().getActor(0).getCamera().getPosition().y /* - (float) Application.getView().getOrientationInput().getRotationX() */);
//                (float) originPosition.getAbsoluteX() + (float) parentSpace.getEntity().getActor(0).getCamera().getPosition().getAbsoluteX(), (float) originPosition.getAbsoluteY() + (float) parentSpace.getEntity().getActor(0).getCamera().getPosition().getAbsoluteY());
        // this.canvas.rotate((float) ApplicationView.getView().getOrientationInput().getRotationZ());
        canvas.scale((float) space.getEntity().getActor(0).getCamera().getScale(), (float) space.getEntity().getActor(0).getCamera().getScale());
    }

    /**
     * The function run in background thread, not UI thread.
     */
    public void update() {

        if (space == null) {
            return;
        }

        Canvas canvas = null;

        // Update
//        parentSpace.update();
        space.doUpdate();

        SurfaceHolder holder = getHolder();

        try {
            canvas = holder.lockCanvas();

            if (canvas != null) {
                synchronized (holder) {

                    // Update
                    //parentSpace.update();

                    // Draw
                    doDraw(canvas);
                }
            }
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public DisplayOutput getDisplayOutput() {
        return this.displayOutput;
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

    public void setSpace(Space space) {
        this.space = space;

        // Get screen width and height of the device
        DisplayMetrics metrics = new DisplayMetrics();
        Application.getView().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        space.getEntity().getActor(0).getCamera().setWidth(screenWidth);
        space.getEntity().getActor(0).getCamera().setHeight(screenHeight);
    }

    public Space getSpace() {
        return this.space;
    }

    //----------------------------------------------------------------------------------------------
    // Event Entity
    //----------------------------------------------------------------------------------------------

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        // - Motion events contain information about all of the pointers that are currently active
        //   even if some of them have not moved since the getLastEvent event was delivered.
        //
        // - The index of pointers only ever changes by one as individual pointers go up and down,
        //   except when the gesture is canceled.
        //
        // - Use the getPointerId(int) method to obtain the pointer id of a pointer to track it
        //   across all subsequent motion events in a gesture. Then for successive motion events,
        //   use the findPointerIndex(int) method to obtain the pointer index for a given pointer
        //   id in that motion event.

        int pointerIndex = ((motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
        int pointerId = motionEvent.getPointerId(pointerIndex);
        int touchInteractionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);
        final int pointerCount = motionEvent.getPointerCount();

        if (this.space == null) {
            return false;
        }

        // Log.v("InteractionHistory", "Started pointerCoordinates composition.");

        // Get active actor
        Actor actor = space.getEntity().getActor(0);

        /*
        boolean processHistory = false;
        if (processHistory) {

            final int historySize = motionEvent.getHistorySize();

            for (int historyIndex = 0; historyIndex < historySize; historyIndex++) {

                // Create pointerCoordinates event
                Event event = new Event();

                if (pointerCount <= Event.MAXIMUM_POINT_COUNT) {
                    if (pointerIndex <= Event.MAXIMUM_POINT_COUNT - 1) {

                        // Current
                        // Update pointerCoordinates state based the pointerCoordinates given by the host OS (e.g., Android).
                        for (int i = 0; i < pointerCount; i++) {
                            int id = motionEvent.getPointerId(i);
                            Point perspectivePosition = actor.getCamera().getPosition();
                            double perspectiveScale = actor.getCamera().getScale();
                            event.pointerCoordinates[id].setAbsoluteX((motionEvent.getHistoricalX(i, historyIndex) - (originPosition.getAbsoluteX() + perspectivePosition.getAbsoluteX())) / perspectiveScale);
                            event.pointerCoordinates[id].setAbsoluteY((motionEvent.getHistoricalY(i, historyIndex) - (originPosition.getAbsoluteY() + perspectivePosition.getAbsoluteY())) / perspectiveScale);
                        }

                        // ACTION_DOWN is called only for the getFirstEvent pointer that touches the screen. This
                        // starts the gesture. The pointer data for this pointer is always at index 0 in
                        // the MotionEvent.
                        //
                        // ACTION_POINTER_DOWN is called for extra pointers that enter the screen beyond
                        // the getFirstEvent. The pointer data for this pointer is at the index returned by
                        // getActionIndex().
                        //
                        // ACTION_MOVE is sent when a change has happened during a press gesture for any
                        // pointer.
                        //
                        // ACTION_POINTER_UP is sent when a non-primary pointer goes up.
                        //
                        // ACTION_UP is sent when the getLastEvent pointer leaves the screen.
                        //
                        // REFERENCES:
                        // - https://developer.android.com/training/gestures/multi.html

                        // Update the state of the touched object based on the current pointerCoordinates event state.
                        if (touchInteractionType == MotionEvent.ACTION_DOWN) {
                            event.setType(Event.Type.SELECT);
                            event.pointerIndex = pointerId;
                            actor.processAction(event);
                        } else if (touchInteractionType == MotionEvent.ACTION_POINTER_DOWN) {
                            // TODO: Handle additional pointers after the getFirstEvent pointerCoordinates!
                        } else if (touchInteractionType == MotionEvent.ACTION_MOVE) {
                            event.setType(Event.Type.MOVE);
                            event.pointerIndex = pointerId;
                            actor.processAction(event);
                        } else if (touchInteractionType == MotionEvent.ACTION_UP) {
                            event.setType(Event.Type.UNSELECT);
                            event.pointerIndex = pointerId;
                            actor.processAction(event);
                        } else if (touchInteractionType == MotionEvent.ACTION_POINTER_UP) {
                            // TODO: Handle additional pointers after the getFirstEvent pointerCoordinates!
                        } else if (touchInteractionType == MotionEvent.ACTION_CANCEL) {
                            // TODO:
                        } else {
                            // TODO:
                        }
                    }
                }
            }
        }
        */

        // Create pointerCoordinates event
        Event event = new Event();

        if (pointerCount <= Event.MAXIMUM_POINT_COUNT) {
            if (pointerIndex <= Event.MAXIMUM_POINT_COUNT - 1) {

                // Current
                // Update pointerCoordinates state based the pointerCoordinates given by the host OS (e.g., Android).
                for (int i = 0; i < pointerCount; i++) {
                    int id = motionEvent.getPointerId(i);
                    Point perspectivePosition = actor.getCamera().getPosition();
                    double perspectiveScale = actor.getCamera().getScale();
//                    event.pointerCoordinates[id].setAbsoluteX((motionEvent.getAbsoluteX(i) - (originPosition.x + perspectivePosition.x)) / perspectiveScale);
//                    event.pointerCoordinates[id].setAbsoluteY((motionEvent.getAbsoluteY(i) - (originPosition.y + perspectivePosition.y)) / perspectiveScale);
                    event.pointerCoordinates[id].x = (motionEvent.getX(i) - (originPosition.x + perspectivePosition.x)) / perspectiveScale;
                    event.pointerCoordinates[id].y = (motionEvent.getY(i) - (originPosition.y + perspectivePosition.y)) / perspectiveScale;
                }

                // ACTION_DOWN is called only for the getFirstEvent pointer that touches the screen. This
                // starts the gesture. The pointer data for this pointer is always at index 0 in
                // the MotionEvent.
                //
                // ACTION_POINTER_DOWN is called for extra pointers that enter the screen beyond
                // the getFirstEvent. The pointer data for this pointer is at the index returned by
                // getActionIndex().
                //
                // ACTION_MOVE is sent when a change has happened during a press gesture for any
                // pointer.
                //
                // ACTION_POINTER_UP is sent when a non-primary pointer goes up.
                //
                // ACTION_UP is sent when the getLastEvent pointer leaves the screen.
                //
                // REFERENCES:
                // - https://developer.android.com/training/gestures/multi.html

                // Update the state of the touched object based on the current pointerCoordinates event state.
                if (touchInteractionType == MotionEvent.ACTION_DOWN) {
                    event.setType(Event.Type.SELECT);
                    event.pointerIndex = pointerId;
                    actor.processAction(event);
                } else if (touchInteractionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO: Handle additional pointers after the getFirstEvent pointerCoordinates!
                } else if (touchInteractionType == MotionEvent.ACTION_MOVE) {
                    event.setType(Event.Type.MOVE);
                    event.pointerIndex = pointerId;
                    actor.processAction(event);
                } else if (touchInteractionType == MotionEvent.ACTION_UP) {
                    event.setType(Event.Type.UNSELECT);
                    event.pointerIndex = pointerId;
                    actor.processAction(event);
                } else if (touchInteractionType == MotionEvent.ACTION_POINTER_UP) {
                    // TODO: Handle additional pointers after the getFirstEvent pointerCoordinates!
                } else if (touchInteractionType == MotionEvent.ACTION_CANCEL) {
                    // TODO:
                } else {
                    // TODO:
                }
            }
        }

        return true;
    }

    public void drawLine(Line line) {

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(line.outlineColorCode);
        paint.setStrokeWidth((float) line.getOutlineThickness());

        // Color
        canvas.drawLine((float) line.getSource().getAbsoluteX(), (float) line.getSource().getAbsoluteY(), (float) line.getTarget().getAbsoluteX(), (float) line.getTarget().getAbsoluteY(), paint);
    }

    public void drawLine(Point source, Point target) {

        // Color
        canvas.drawLine((float) source.getAbsoluteX(), (float) source.getAbsoluteY(), (float) target.getAbsoluteX(), (float) target.getAbsoluteY(), paint);
        // TODO: canvas.drawLine((float) source.x, (float) source.y, (float) target.x, (float) target.y, paint);

    }

    public void drawCircle(Point position, double radius, double angle) {

        // Color
        //canvas.drawCircle((float) position.getAbsoluteX(), (float) position.getAbsoluteY(), (float) radius, paint);
        canvas.drawCircle((float) position.x, (float) position.y, (float) radius, paint);

    }

    public void drawCircle(Circle circle) {

        canvas.save();

        canvas.translate(
                (float) circle.getPosition().x,
                (float) circle.getPosition().y
        );

        canvas.rotate((float) circle.getPosition().rotation);

        // Fill
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(circle.colorCode);
        canvas.drawCircle(
                0,
                0,
                (float) circle.radius,
                paint
        );

        // Outline
        if (circle.getOutlineThickness() > 0) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(circle.outlineColorCode);
            paint.setStrokeWidth((float) circle.outlineThickness);

            canvas.drawCircle(
                    0,
                    0,
                    (float) circle.radius,
                    paint
            );
        }

        canvas.restore();
    }

    public void drawText(Point position, String text, double size) {

        // Style
        paint.setTextSize((float) size);

        // Style (Guaranteed)
        text = text.toUpperCase();
        paint.setStyle(Paint.Style.FILL);

        // Draw
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        canvas.drawText(text, (float) position.x, (float) position.y + bounds.height() / 2.0f, paint);
    }

    public void drawRectangle(Point position, double angle, double width, double height) {

        canvas.save();

        canvas.translate(
//                (float) position.getAbsoluteX(),
//                (float) position.getAbsoluteY()
                (float) position.x,
                (float) position.y
        );

        canvas.rotate((float) angle);

        canvas.drawRect(
                (float) (0 - (width / 2.0f)),
                (float) (0 - (height / 2.0f)),
                (float) (0 + (width / 2.0f)),
                (float) (0 + (height / 2.0f)),
                paint
        );

        canvas.restore();
    }

    public void drawRectangle(Rectangle rectangle) {

        canvas.save();

        // Set style
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(rectangle.colorCode);

//        // <Image>
//        canvas.translate(
//                (float) rectangle.getPosition().getReferencePoint().getX(),
//                (float) rectangle.getPosition().getReferencePoint().getY()
//        );
//
//        // canvas.rotate((float) rectangle.getAbsoluteRotation());
//        canvas.rotate((float) rectangle.getPosition().getReferencePoint().getRotation());
//        // </Image>

        // <Shape>
        canvas.translate(
                (float) rectangle.getPosition().x,
                (float) rectangle.getPosition().y
        );

        // canvas.rotate((float) rectangle.getAbsoluteRotation());
        canvas.rotate((float) rectangle.getPosition().rotation);
        // </Shape>

        /*
        canvas.drawRect(
                (float) rectangle.getRelativeLeft(),
                (float) rectangle.getRelativeTop(),
                (float) rectangle.getRelativeRight(),
                (float) rectangle.getRelativeBottom(),
                paint
        );
        */

        canvas.drawRoundRect(
//                (float) rectangle.getRelativeLeft(),
//                (float) rectangle.getRelativeTop(),
//                (float) rectangle.getRelativeRight(),
//                (float) rectangle.getRelativeBottom(),
//                (float) rectangle.getCornerRadius(),
//                (float) rectangle.getCornerRadius(),
                (float) (0 - (rectangle.width / 2.0)),
                (float) (0 - (rectangle.height / 2.0)),
                (float) (0 + (rectangle.width / 2.0)),
                (float) (0 + (rectangle.height / 2.0)),
                (float) rectangle.cornerRadius,
                (float) rectangle.cornerRadius,
                paint
        );

        // Draw pointerCoordinates in shape
        if (rectangle.getOutlineThickness() > 0) {

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(rectangle.outlineColorCode);
            paint.setStrokeWidth((float) rectangle.outlineThickness);

        /*
        canvas.drawRect(
                (float) rectangle.getRelativeLeft(),
                (float) rectangle.getRelativeTop(),
                (float) rectangle.getRelativeRight(),
                (float) rectangle.getRelativeBottom(),
                paint
        );
        */

            canvas.drawRoundRect(
//                    (float) rectangle.getRelativeLeft(),
//                    (float) rectangle.getRelativeTop(),
//                    (float) rectangle.getRelativeRight(),
//                    (float) rectangle.getRelativeBottom(),
//                    (float) rectangle.getCornerRadius(),
//                    (float) rectangle.getCornerRadius(),
                    (float) (0 - (rectangle.width / 2.0)),
                    (float) (0 - (rectangle.height / 2.0)),
                    (float) (0 + (rectangle.width / 2.0)),
                    (float) (0 + (rectangle.height / 2.0)),
                    (float) rectangle.cornerRadius,
                    (float) rectangle.cornerRadius,
                    paint
            );
        }

        canvas.restore();
    }

    public void drawTrianglePath(Point startPosition, Point stopPosition, double triangleWidth, double triangleHeight) {

        double pathRotationAngle = Geometry.calculateRotationAngle(startPosition, stopPosition);

        double triangleRotationAngle = pathRotationAngle + 90.0f;

        double pathDistance = Geometry.calculateDistance(startPosition, stopPosition);

        int triangleCount = (int) (pathDistance / (triangleHeight + 15));
        double triangleSpacing2 = pathDistance / triangleCount;

        for (int k = 0; k <= triangleCount; k++) {

            // Calculate triangle position
            Point triangleCenterPosition2 = Geometry.calculatePoint(startPosition, pathRotationAngle, k * triangleSpacing2);

            paint.setStyle(Paint.Style.FILL);
            drawTriangle(triangleCenterPosition2, triangleRotationAngle, triangleWidth, triangleHeight);
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
     */
    public void drawRegularPolygon(Point position, int radius, int sideCount) {

        android.graphics.Path path = new android.graphics.Path();
        for (int i = 0; i < sideCount; i++) {

            Point vertexPosition = new Point((position.x + radius * Math.cos(2.0f * Math.PI * (double) i / (double) sideCount)), (position.y + radius * Math.sin(2.0f * Math.PI * (double) i / (double) sideCount)));

            // Draw pointerCoordinates in shape
            path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
            if (i == 0) {
                path.moveTo((float) vertexPosition.x, (float) vertexPosition.y);
            }

            path.lineTo((float) vertexPosition.x, (float) vertexPosition.y);
        }

//        path.lineTo(position.x, position.y);
        path.close();

        canvas.drawPath(path, paint);
    }

    // TODO: Refactor with transforms
    public void drawPolygon(Polygon polygon) {
        drawPolygon(polygon.getVertices());
    }

    // TODO: Refactor with transforms
    public void drawPolygon(List<Point> vertices) {

        android.graphics.Path path = new android.graphics.Path();
        for (int i = 0; i < vertices.size(); i++) {

            // Draw pointerCoordinates in shape
            path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
            if (i == 0) {
                path.moveTo((float) vertices.get(i).x, (float) vertices.get(i).y);
            }

            path.lineTo((float) vertices.get(i).x, (float) vertices.get(i).y);
        }

        path.close();

        canvas.drawPath(path, paint);
    }

    // TODO: Refactor with transforms
    public void drawTriangle(Triangle triangle) {
        // TODO:
    }

    // TODO: Refactor with transforms
    public void drawTriangle(Point position, double angle, double width, double height) {

        // Calculate pointerCoordinates before rotation
        Point p1 = new Point(position.x + -(width / 2.0f), position.y + (height / 2.0f));
        Point p2 = new Point(position.x + 0, position.y - (height / 2.0f));
        Point p3 = new Point(position.x + (width / 2.0f), position.y + (height / 2.0f));

        // Calculate pointerCoordinates after rotation
        Point rp1 = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, p1), Geometry.calculateDistance(position, p1));
        Point rp2 = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, p2), Geometry.calculateDistance(position, p2));
        Point rp3 = Geometry.calculatePoint(position, angle + Geometry.calculateRotationAngle(position, p3), Geometry.calculateDistance(position, p3));

        android.graphics.Path path = new android.graphics.Path();
        path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
        path.moveTo((float) rp1.x, (float) rp1.y);
        path.lineTo((float) rp2.x, (float) rp2.y);
        path.lineTo((float) rp3.x, (float) rp3.y);
        path.close();

        canvas.drawPath(path, paint);
    }
}
