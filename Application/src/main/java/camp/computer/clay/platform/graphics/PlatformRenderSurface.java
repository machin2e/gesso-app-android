package camp.computer.clay.platform.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import camp.computer.clay.platform.Application;
import camp.computer.clay.engine.system.InputSystem;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.util.BuilderImage.Circle;
import camp.computer.clay.util.BuilderImage.Geometry;
import camp.computer.clay.util.BuilderImage.Point;
import camp.computer.clay.util.BuilderImage.Segment;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.util.BuilderImage.Polygon;
import camp.computer.clay.util.BuilderImage.Polyline;
import camp.computer.clay.util.BuilderImage.Rectangle;
import camp.computer.clay.util.BuilderImage.Text;
import camp.computer.clay.util.BuilderImage.Triangle;
import camp.computer.clay.util.BuilderImage.Shape;
import camp.computer.clay.engine.World;

public class PlatformRenderSurface extends SurfaceView implements SurfaceHolder.Callback {

    // World Rendering Context
    public Bitmap canvasBitmap = null;
    public Canvas canvas = null;
    private int canvasWidth;
    private int canvasHeight;
    public Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public Matrix identityMatrix;

    // World PlatformRenderer
    private SurfaceHolder surfaceHolder;

    public PlatformRenderer platformRenderer;

    // Coordinate System (Grid)
    public Transform originPosition = new Transform();

    // World
    public World world;

    public PlatformRenderSurface(Context context) {
        super(context);
        setFocusable(true);
    }

    public PlatformRenderSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlatformRenderSurface(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        // Get dimensions of the Surface
        canvasWidth = getWidth();
        canvasHeight = getHeight();

        // Create a bitmap to use as a drawing buffer equal in size to the full size of the Surface
        canvasBitmap = Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888);
        canvas = new Canvas();
        canvas.setBitmap(canvasBitmap);

        // Create Identity Matrix
        identityMatrix = new Matrix();

        // Center the world coordinate system
        originPosition.set(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // TODO: Resize the Viewport to width and height
        Log.v("Display", "surfaceChanged");
        Log.v("Display", "width: " + width + ", height: " + height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        /*
        // Kill the background Thread
        boolean retry = true;
        // platformRenderer.setRunning (false);
        while (retry) {
            try {
                platformRenderer.join ();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace ();
            }
        }
        */
    }

    public void onResume() {

        surfaceHolder = getHolder();
        getHolder().addCallback(this);

        // Create and start background Thread
        platformRenderer = new PlatformRenderer(this);
        platformRenderer.setRunning(true);
        platformRenderer.start();

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
        platformRenderer.setRunning(false);

        while (retry) {
            try {
                platformRenderer.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Event previousEvent = null;

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        if (this.world == null) {
            return false;
        }

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

        // Get active inputSystem
        InputSystem inputSystem = world.inputSystem;

        // Create pointerCoordinates event
        Event event = new Event();

        if (pointerCount <= Event.MAXIMUM_POINT_COUNT) {
            if (pointerIndex <= Event.MAXIMUM_POINT_COUNT - 1) {

                Entity camera = Entity.Manager.filterWithComponent(Camera.class).get(0);

                // Current
                // Update pointerCoordinates state based the pointerCoordinates given by the host OS (e.g., Android).
                for (int i = 0; i < pointerCount; i++) {
                    int id = motionEvent.getPointerId(i);
                    Transform perspectivePosition = camera.getComponent(Camera.class).getEntity().getComponent(Transform.class);
                    double perspectiveScale = camera.getComponent(Camera.class).getScale();
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

                    previousEvent = null;

                    // Set previous Event
                    if (previousEvent != null) {
                        event.previousEvent = previousEvent;
                    } else {
                        event.previousEvent = null;
                    }
                    previousEvent = event;

                    holdEventTimerHandler.removeCallbacks(holdEventTimerRunnable);
                    holdEventTimerHandler.postDelayed(holdEventTimerRunnable, Event.MINIMUM_HOLD_DURATION);

//                    new Timer().schedule(new TimerTask() {
//                        @Override
//                        public void run() {
//                            // this code will be executed after 2 seconds
//                        }
//                    }, 2000);

                    event.setType(Event.Type.SELECT);
                    event.pointerIndex = pointerId;
                    inputSystem.queueEvent(event);
                } else if (touchInteractionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO: Handle additional pointers after the getFirstEvent pointerCoordinates!
                } else if (touchInteractionType == MotionEvent.ACTION_MOVE) {

                    // Set previous Event
                    if (previousEvent != null) {
                        event.previousEvent = previousEvent;
                    } else {
                        event.previousEvent = null;
                    }
                    previousEvent = event;

                    event.setType(Event.Type.MOVE);
                    event.pointerIndex = pointerId;

                    if (previousEvent.getDistance() > Event.MINIMUM_MOVE_DISTANCE) {
                        holdEventTimerHandler.removeCallbacks(holdEventTimerRunnable);
                        inputSystem.queueEvent(event);
                    }

                } else if (touchInteractionType == MotionEvent.ACTION_UP) {

                    // Set previous Event
                    if (previousEvent != null) {
                        event.previousEvent = previousEvent;
                    } else {
                        event.previousEvent = null;
                    }
                    previousEvent = event;

                    holdEventTimerHandler.removeCallbacks(holdEventTimerRunnable);

                    event.setType(Event.Type.UNSELECT);
                    event.pointerIndex = pointerId;
                    inputSystem.queueEvent(event);
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

    public Handler holdEventTimerHandler = new Handler();

    public Runnable holdEventTimerRunnable = new Runnable() {
        @Override
        public void run() {

            int pointerIndex = 0;

//            if (getFirstEvent().isPointing[pointerIndex]) {
//                if (getDragDistance() < Event.MINIMUM_MOVE_DISTANCE) {

//            Event event = new Event();
//            event.setType(Event.Type.HOLD);
//            event.pointerIndex = getFirstEvent().pointerIndex;
//            event.pointerCoordinates[0] = new Transform(getFirstEvent().getPosition()); // HACK. This should contain the state of ALL pointers (just set the previous event's since this is a synthetic event?)
//            getFirstEvent().getInputSystem().queueEvent(event);
//
//            isHolding[pointerIndex] = true;

            if (previousEvent.getDistance() < Event.MINIMUM_MOVE_DISTANCE) {
                InputSystem inputSystem = world.inputSystem;

                Event event = new Event();
                // event.pointerCoordinates[id].x = (motionEvent.getX(i) - (originPosition.x + perspectivePosition.x)) / perspectiveScale;
                // event.pointerCoordinates[id].y = (motionEvent.getY(i) - (originPosition.y + perspectivePosition.y)) / perspectiveScale;
                event.setType(Event.Type.HOLD);
                event.pointerIndex = 0; // HACK // TODO: event.pointerIndex = pointerId;

                // Set previous Event
                if (previousEvent != null) {
                    event.previousEvent = previousEvent;
                } else {
                    event.previousEvent = null;
                }
                previousEvent = event;

                inputSystem.queueEvent(event);

                Log.v("PlatformRenderSurface", "event.getDistance: " + event.getDistance());
            }

//                }
//            }
        }
    };

    // TODO: Make generic Timer function that spawns a background thread that blocks for <time> then calls a function.
//                new Timer().schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        // this code will be executed after 2 seconds
//
//                        Event event = new Event();
//                        // event.pointerCoordinates[id].x = (motionEvent.getX(i) - (originPosition.x + perspectivePosition.x)) / perspectiveScale;
//                        // event.pointerCoordinates[id].y = (motionEvent.getY(i) - (originPosition.y + perspectivePosition.y)) / perspectiveScale;
//                        event.setType(Event.Type.HOLD);
//                        event.pointerIndex = 0; // HACK // TODO: event.pointerIndex = pointerId;
//                        queueEvent(event);
//
//                        Log.v("HoldCallback", "Holding");
//                    }
//                }, 1000);

//                final Handler handler = new Handler();

//                Thread thread = new Thread() {
//                    @Override
//                    public void run() {
//                        try {
//                            sleep(1000);
//
//                            Log.v("HOLD", "WAAAAAAAAAAIT");
//
//                            // If needs to run on UI thread.
//                            /*
//                            Application.getView().runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    Log.v("HOLD", "WAAAAAAAAAAIT");
//                                }
//                            });
//                            */
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                };
//
//                thread.start();

    /**
     * The function run in background thread, not UI thread.
     */
    public void update() {

        if (world == null) {
            return;
        }

        Canvas canvas = null;
        SurfaceHolder holder = getHolder();

        try {
            canvas = holder.lockCanvas();
            if (canvas != null) {
                synchronized (holder) {
                    world.updateSystems(canvas);
                }
            }
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public PlatformRenderer getPlatformRenderer() {
        return this.platformRenderer;
    }

    public void setWorld(World world) {
        this.world = world;

        // Get screen width and height of the device
        DisplayMetrics metrics = new DisplayMetrics();
        Application.getView().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        // Set camera viewport dimensions
        Entity camera = Entity.Manager.filterWithComponent(Camera.class).get(0);
        camera.getComponent(Camera.class).setWidth(screenWidth);
        camera.getComponent(Camera.class).setHeight(screenHeight);
    }

    public World getWorld() {
        return this.world;
    }

    // <PATH_IMAGE_HELPERS>
    /*
    private double triangleWidth = 20;
    private double triangleHeight = triangleWidth * (Math.sqrt(3.0) / 2);
    private double triangleSpacing = 35;

    public void drawTrianglePath(Entity pathEntity, PlatformRenderSurface platformRenderSurface) {

        Paint paint = platformRenderSurface.paint;

        Shape sourcePortShape = World.getWorld().getShape(pathEntity.getComponent(Path.class).getSource());
        Shape targetPortShape = World.getWorld().getShape(pathEntity.getComponent(Path.class).getTarget());

        // Show target port
//        targetPortShape.setVisibility(Visible.VISIBLE);
        //// TODO: targetPortShape.setPathVisibility(Visible.VISIBLE);

        // Color
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15.0f);
        paint.setColor(Color.parseColor(sourcePortShape.getColor()));

        double pathRotation = Geometry.getAngle(sourcePortShape.getPosition(), targetPortShape.getPosition());
        Transform sourcePoint = Geometry.getRotateTranslatePoint(sourcePortShape.getPosition(), pathRotation, 2 * triangleSpacing);
        Transform targetPoint = Geometry.getRotateTranslatePoint(targetPortShape.getPosition(), pathRotation + 180, 2 * triangleSpacing);

        platformRenderSurface.drawTrianglePath(sourcePoint, targetPoint, triangleWidth, triangleHeight);
    }
    */

    public void drawLinePath(Entity pathEntity, PlatformRenderSurface platformRenderSurface) {

        Paint paint = platformRenderSurface.paint;

        Shape sourcePortShape = pathEntity.getComponent(Path.class).getSource().getComponent(Image.class).getImage().getShape("Port");
        Shape targetPortShape = pathEntity.getComponent(Path.class).getTarget().getComponent(Image.class).getImage().getShape("Port");

        // TODO: Transform sourcePortPositition = pathEntity.getComponent(Path.class).getSource().getComponent(Transform.class);
        // TODO: Transform targetPortPositition = pathEntity.getComponent(Path.class).getTarget().getComponent(Transform.class);
        Transform sourcePortPositition = sourcePortShape.getPosition();
        Transform targetPortPositition = targetPortShape.getPosition();

//        if (sourcePortShape != null && targetPortShape != null) {

            // Show target port
//            targetPortShape.setVisibility(Visible.VISIBLE);
            //// TODO: targetPortShape.setPathVisibility(Visible.VISIBLE);

            // Color
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(15.0f);
            paint.setColor(Color.parseColor(sourcePortShape.getColor()));

            double pathRotationAngle = Geometry.getAngle(sourcePortPositition, targetPortPositition);
            Transform pathStartCoordinate = Geometry.getRotateTranslatePoint(sourcePortPositition, pathRotationAngle, 0);
            Transform pathStopCoordinate = Geometry.getRotateTranslatePoint(targetPortPositition, pathRotationAngle + 180, 0);

//            display.drawSegment(pathStartCoordinate, pathStopCoordinate);

            // TODO: Create Segment and add it to the PathImage. Update its geometry to change position, rotation, etc.
//            double pathRotation = getWorld().getImages(getPath().getHosts()).getRotation();

            Segment segment = (Segment) pathEntity.getComponent(Image.class).getImage().getShape("PathEntity");
            segment.setOutlineThickness(15.0);
            segment.setOutlineColor(sourcePortShape.getColor());

            segment.setSource(pathStartCoordinate);
            segment.setTarget(pathStopCoordinate);

            platformRenderSurface.drawSegment(segment);
//        }
    }

    public void drawPhysicalPath(Entity pathEntity, PlatformRenderSurface platformRenderSurface) {

        // Get Host and Extension Ports
        Entity hostPortEntity = pathEntity.getComponent(Path.class).getSource();
        Entity extensionPortEntity = pathEntity.getComponent(Path.class).getTarget();

        // Draw the connection between the Host Port and the Extension Port
        Image hostImage = hostPortEntity.getParent().getComponent(Image.class);
        Image extensionImage = extensionPortEntity.getParent().getComponent(Image.class);

        Entity host = hostImage.getEntity();
        Entity extension = extensionImage.getEntity();

        if (host.getComponent(Portable.class).headerContactPositions.size() > hostPortEntity.getComponent(Port.class).getIndex()
                && extension.getComponent(Portable.class).headerContactPositions.size() > extensionPortEntity.getComponent(Port.class).getIndex()) {

            Transform hostConnectorPosition = host.getComponent(Portable.class).headerContactPositions.get(hostPortEntity.getComponent(Port.class).getIndex()).getPosition();
            Transform extensionConnectorPosition = extension.getComponent(Portable.class).headerContactPositions.get(extensionPortEntity.getComponent(Port.class).getIndex()).getPosition();

            // Draw connection between Ports
            platformRenderSurface.paint.setColor(android.graphics.Color.parseColor(camp.computer.clay.util.Color.getColor(extensionPortEntity.getComponent(Port.class).getType())));
            platformRenderSurface.paint.setStrokeWidth(10.0f);

            // TODO: Create Segment and add it to the PathImage. Update its geometry to change position, rotation, etc.
            Segment segment = (Segment) pathEntity.getComponent(Image.class).getImage().getShape("PathEntity");
            segment.setOutlineThickness(10.0);
            segment.setOutlineColor(camp.computer.clay.util.Color.getColor(extensionPortEntity.getComponent(Port.class).getType()));

            segment.setSource(hostConnectorPosition);
            segment.setTarget(extensionConnectorPosition);

            platformRenderSurface.drawSegment(segment);
        }
    }
    // </PATH_IMAGE_HELPERS>

    // TODO: Replace with more direct drawing? What's the minimal layering between image
    // TODO: (...) representation and platform-level rendering?
    public void drawShape(Shape shape) {
        if (shape.getClass() == Point.class) {
            // TODO:
        } else if (shape.getClass() == Segment.class) {
            drawSegment((Segment) shape);
        } else if (shape.getClass() == Polyline.class) {
            drawPolyline((Polyline) shape);
        } else if (shape.getClass() == Triangle.class) {
            drawTriangle((Triangle) shape);
        } else if (shape.getClass() == Rectangle.class) {
            drawRectangle((Rectangle) shape);
        } else if (shape.getClass() == Polygon.class) {
            drawPolygon((Polygon) shape);
        } else if (shape.getClass() == Circle.class) {
            drawCircle((Circle) shape);
        } else if (shape.getClass() == Text.class) {
            // TODO:
        }
    }

    public void drawSegment(Transform source, Transform target) {
        canvas.drawLine((float) source.x, (float) source.y, (float) target.x, (float) target.y, paint);
    }

    public void drawSegment(Segment segment) {

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(segment.outlineColorCode);
        paint.setStrokeWidth((float) segment.getOutlineThickness());

        // Color
        canvas.drawLine((float) segment.getSource().x, (float) segment.getSource().y, (float) segment.getTarget().x, (float) segment.getTarget().y, paint);
    }

    public void drawPolyline(Polyline polyline) {
        drawPolyline(polyline.getPoints());
    }

    // TODO: Refactor with transforms
    public void drawPolyline(List<Transform> vertices) {

        for (int i = 0; i < vertices.size() - 1; i++) {

            canvas.drawLine(
                    (float) vertices.get(i).x,
                    (float) vertices.get(i).y,
                    (float) vertices.get(i + 1).x,
                    (float) vertices.get(i + 1).y,
                    paint
            );
        }
    }

    public void drawCircle(Circle circle) {

        canvas.save();

        canvas.translate((float) circle.getPosition().x, (float) circle.getPosition().y);
        canvas.rotate((float) circle.getPosition().rotation);

        // Fill
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(circle.colorCode);
        canvas.drawCircle(0, 0, (float) circle.radius, paint);

        // Outline
        if (circle.getOutlineThickness() > 0) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(circle.outlineColorCode);
            paint.setStrokeWidth((float) circle.outlineThickness);

            canvas.drawCircle(0, 0, (float) circle.radius, paint);
        }

        canvas.restore();
    }

    public void drawCircle(Transform position, double radius, double angle) {

        canvas.save();

        canvas.translate((float) position.x, (float) position.y);
        canvas.rotate((float) angle);

        canvas.drawCircle(0.0f, 0.0f, (float) radius, paint);

        canvas.restore();

    }

    public void drawRectangle(Rectangle rectangle) {

        // Set style
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(rectangle.colorCode);

        canvas.save();
        canvas.translate((float) rectangle.getPosition().x, (float) rectangle.getPosition().y);
        canvas.rotate((float) rectangle.getRotation());

        canvas.drawRoundRect(
                (float) (0 - (rectangle.width / 2.0)),
                (float) (0 - (rectangle.height / 2.0)),
                (float) (0 + (rectangle.width / 2.0)),
                (float) (0 + (rectangle.height / 2.0)),
                (float) rectangle.cornerRadius,
                (float) rectangle.cornerRadius,
                paint
        );

        // Draw Points in Shape
        if (rectangle.getOutlineThickness() > 0) {

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(rectangle.outlineColorCode);
            paint.setStrokeWidth((float) rectangle.outlineThickness);

            canvas.drawRoundRect(
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

    public void drawRectangle(Transform position, double angle, double width, double height) {

        canvas.save();

        canvas.translate((float) position.x, (float) position.y);
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

    public void drawText(Transform position, String text, double size) {

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

    public void drawTrianglePath(Transform startPosition, Transform stopPosition, double triangleWidth, double triangleHeight) {

        double pathRotationAngle = Geometry.getAngle(startPosition, stopPosition);

        double triangleRotationAngle = pathRotationAngle + 90.0f;

        double pathDistance = Geometry.distance(startPosition, stopPosition);

        int triangleCount = (int) (pathDistance / (triangleHeight + 15));
        double triangleSpacing2 = pathDistance / triangleCount;

        for (int k = 0; k <= triangleCount; k++) {

            // Calculate triangle position
            Transform triangleCenterPosition = Geometry.getRotateTranslatePoint(startPosition, pathRotationAngle, k * triangleSpacing2);

            paint.setStyle(Paint.Style.FILL);
            drawTriangle(triangleCenterPosition, triangleRotationAngle, triangleWidth, triangleHeight);
        }
    }

    // TODO: Refactor with transforms
    public void drawPolygon(Polygon polygon) {
        drawPolygon(polygon.getBoundary());
    }

    // TODO: Refactor with transforms
    public void drawPolygon(List<Transform> vertices) {

        // Draw vertex Points in Shape
        android.graphics.Path path = new android.graphics.Path();
        path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
        path.moveTo((float) vertices.get(0).x, (float) vertices.get(0).y);
        for (int i = 1; i < vertices.size(); i++) {
            path.lineTo((float) vertices.get(i).x, (float) vertices.get(i).y);
        }
//        path.lineTo((float) boundary.get(0).x, (float) boundary.get(0).y);
        path.close();

        canvas.drawPath(path, paint);
    }

    // TODO: Refactor with transforms
    public void drawTriangle(Triangle triangle) {
        // TODO:
    }

    // TODO: Refactor with transforms
    public void drawTriangle(Transform position, double angle, double width, double height) {

        // Calculate pointerCoordinates before rotation
        Transform p1 = new Transform(position.x + -(width / 2.0f), position.y + (height / 2.0f));
        Transform p2 = new Transform(position.x + 0, position.y - (height / 2.0f));
        Transform p3 = new Transform(position.x + (width / 2.0f), position.y + (height / 2.0f));

        // Calculate pointerCoordinates after rotation
        Transform rp1 = Geometry.getRotateTranslatePoint(position, angle + Geometry.getAngle(position, p1), Geometry.distance(position, p1));
        Transform rp2 = Geometry.getRotateTranslatePoint(position, angle + Geometry.getAngle(position, p2), Geometry.distance(position, p2));
        Transform rp3 = Geometry.getRotateTranslatePoint(position, angle + Geometry.getAngle(position, p3), Geometry.distance(position, p3));

        android.graphics.Path path = new android.graphics.Path();
        path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
        path.moveTo((float) rp1.x, (float) rp1.y);
        path.lineTo((float) rp2.x, (float) rp2.y);
        path.lineTo((float) rp3.x, (float) rp3.y);
        path.close();

        canvas.drawPath(path, paint);
    }
}
