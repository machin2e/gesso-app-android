package camp.computer.clay.platform.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

import camp.computer.clay.engine.Event;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Boundary;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Component;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.ShapeComponent;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.system.InputSystem;
import camp.computer.clay.lib.ImageBuilder.Circle;
import camp.computer.clay.lib.ImageBuilder.Point;
import camp.computer.clay.lib.ImageBuilder.Polygon;
import camp.computer.clay.lib.ImageBuilder.Polyline;
import camp.computer.clay.lib.ImageBuilder.Rectangle;
import camp.computer.clay.lib.ImageBuilder.Segment;
import camp.computer.clay.lib.ImageBuilder.Shape;
import camp.computer.clay.lib.ImageBuilder.Text;
import camp.computer.clay.lib.ImageBuilder.Triangle;
import camp.computer.clay.platform.Application;
import camp.computer.clay.util.Geometry;

public class PlatformRenderSurface extends SurfaceView implements SurfaceHolder.Callback {

    // World Rendering Context
    public Bitmap canvasBitmap = null;
    public Canvas canvas = null;
    private int canvasWidth;
    private int canvasHeight;
    public Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    public Matrix identityMatrix;

    public Palette palette = new Palette();

    // World PlatformRenderClock
    private SurfaceHolder surfaceHolder;

    public PlatformRenderClock platformRenderClock;

    // Coordinate System (Grid)
    public Transform originPosition = new Transform();

    // World
    public World world;

    public PlatformRenderSurface(Context context) {
        super(context);
        setFocusable(true);

        palette.canvas = canvas;
        palette.paint = paint;
    }

    public PlatformRenderSurface(Context context, AttributeSet attrs) {
        super(context, attrs);

        palette.canvas = canvas;
        palette.paint = paint;
    }

    public PlatformRenderSurface(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        palette.canvas = canvas;
        palette.paint = paint;
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
        // platformRenderClock.setRunning (false);
        while (retry) {
            try {
                platformRenderClock.join ();
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
        platformRenderClock = new PlatformRenderClock(this);
        platformRenderClock.setRunning(true);
        platformRenderClock.start();

        // Start communications
        // getClay().getCommunication().startDatagramServer();

        // Remove this?
//        update();

    }

    public void onPause() {
        // Pause the communications
        //getClay().getCommunication ().stopDatagramServer (); // HACK: This was commented out to prevent the server from "crashing" into an invalid state!

        // Kill the background Thread
        boolean retry = true;
        platformRenderClock.setRunning(false);

        while (retry) {
            try {
                platformRenderClock.join();
                retry = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private Event previousEvent = null;

    // https://developer.android.com/training/gestures/scale.html
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

//        Log.v("HoldCallback", "started timer");
//        startTimer();

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

                Entity camera = world.Manager.getEntities().filterWithComponent(Camera.class).get(0);

                // Current
                // Update pointerCoordinates state based the pointerCoordinates given by the host OS (e.g., Android).
                for (int i = 0; i < pointerCount; i++) {
                    int id = motionEvent.getPointerId(i);
                    Transform cameraTransform = camera.getComponent(Transform.class);
                    double cameraScale = world.cameraSystem.getScale(camera);
                    event.pointerCoordinates[id].x = (motionEvent.getX(i) - (originPosition.x + cameraTransform.x)) / cameraScale;
                    event.pointerCoordinates[id].y = (motionEvent.getY(i) - (originPosition.y + cameraTransform.y)) / cameraScale;
//                    event.pointerCoordinates[id].x = (motionEvent.getX(i) - (cameraTransform.x)) / cameraScale;
//                    event.pointerCoordinates[id].y = (motionEvent.getY(i) - (cameraTransform.y)) / cameraScale;
                }

//                Log.v("PlatformRenderSurface", "x: " + event.pointerCoordinates[0].x + ", y: " + event.pointerCoordinates[0].y);

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

                    // Set previous Event
                    previousEvent = event;

                    holdEventTimerHandler.removeCallbacks(holdEventTimerRunnable);
                    holdEventTimerHandler.postDelayed(holdEventTimerRunnable, Event.MINIMUM_HOLD_DURATION);

                    event.setType(Event.Type.SELECT);
                    event.pointerIndex = pointerId;
                    inputSystem.queueEvent(event);
                } else if (touchInteractionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO: Handle additional pointers after the getFirstEvent pointerCoordinates!
                } else if (touchInteractionType == MotionEvent.ACTION_MOVE) {

                    // Set previous Event
                    event.setPreviousEvent(previousEvent);
                    previousEvent = event;

                    event.setType(Event.Type.MOVE);
                    event.pointerIndex = pointerId;

                    if (previousEvent.getDistance() > Event.MINIMUM_MOVE_DISTANCE) {
                        holdEventTimerHandler.removeCallbacks(holdEventTimerRunnable);
                        inputSystem.queueEvent(event);
                    }

                } else if (touchInteractionType == MotionEvent.ACTION_UP) {

                    // Set previous Event
                    event.setPreviousEvent(previousEvent);
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

    private Handler holdEventTimerHandler = new Handler();

    private Runnable holdEventTimerRunnable = new Runnable() {
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
                    event.setPreviousEvent(previousEvent);
                } else {
                    event.setPreviousEvent(null);
                }
                previousEvent = event;

                inputSystem.queueEvent(event);

                Log.v("PlatformRenderSurface", "event.getDistance: " + event.getDistance());
            }

//                }
//            }
        }
    };

    /**
     * The function run in background thread, not UI thread.
     */
    SurfaceHolder holder = getHolder();

    public void update() {

        if (world == null) {
            return;
        }

//        Canvas canvas = null;
//        SurfaceHolder holder = getHolder();

        world.update();

        try {
            canvas = holder.lockCanvas();
            if (canvas != null) {
                palette.canvas = canvas;
                synchronized (holder) {
                    // TODO!!!!!!!!!!!! FLATTEN THE CALLBACK TREE!!!!!!!!!!!!! FUCK!!!!!!!!
                    world.draw();
                }
            }
        } finally {
            if (canvas != null) {
                holder.unlockCanvasAndPost(canvas);
            }
        }
    }

    public PlatformRenderClock getPlatformRenderer() {
        return this.platformRenderClock;
    }

    // TODO: Remove this! Render shouldn't need to know about the whole world!
    public void setWorld(World world) {
        this.world = world;

        // Get screen width and height of the device
        DisplayMetrics metrics = new DisplayMetrics();
        Application.getView().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        // Set camera viewport dimensions
        Entity camera = world.Manager.getEntities().filterWithComponent(Camera.class).get(0);
        world.cameraSystem.setWidth(camera, screenWidth);
        world.cameraSystem.setHeight(camera, screenHeight);
    }

    // TODO: 11/16/2016 Optimize! Big and slow! Should be fast!
    public void drawEditablePath(Entity path, Palette palette) {

        Entity sourcePort = Path.getSource(path);
        Entity sourcePortShapeE = Image.getShape(sourcePort, "Port");
        Shape hostSourcePortShape = sourcePortShapeE.getComponent(ShapeComponent.class).shape; // Path.getSource(path).getComponent(Image.class).getImage().getShape("Port");
        Shape extensionTargetPortShape = null;

        boolean isSingletonPath = (Path.getTarget(path) == null);

        if (!isSingletonPath) {

            Entity targetPortShapeE = Image.getShape(Path.getTarget(path), "Port");
            extensionTargetPortShape = Image.getShape(Path.getTarget(path), "Port").getComponent(ShapeComponent.class).shape; // Path.getTarget(path).getComponent(Image.class).getImage().getShape("Port");

            Shape sourcePortShape = Image.getShape(path, "Source Port").getComponent(ShapeComponent.class).shape; // path.getComponent(Image.class).getImage().getShape("Source Port");
            Shape targetPortShape = Image.getShape(path, "Target Port").getComponent(ShapeComponent.class).shape; // path.getComponent(Image.class).getImage().getShape("Target Port");

            path.getComponent(Transform.class).set(
                    (sourcePortShape.getPosition().x + targetPortShape.getPosition().x) / 2.0,
                    (sourcePortShape.getPosition().y + targetPortShape.getPosition().y) / 2.0
            );

            sourcePortShape.setColor(hostSourcePortShape.getColor());

            if (Path.getState(path) != Component.State.EDITING) {
                sourcePortShape.setPosition(hostSourcePortShape.getPosition());
                targetPortShape.setPosition(extensionTargetPortShape.getPosition());
                // TODO: sourcePortShape.setPosition(sourcePortShapeE.getComponent(Transform.class));
                // TODO: targetPortShape.setPosition(targetPortShapeE.getComponent(Transform.class));
            }

            // <HACK>
//            BoundarySystem.updateShapeBoundary(sourcePortShape);
//            BoundarySystem.updateShapeBoundary(targetPortShape);
            // </HACK>

            // TODO: Transform sourcePortPosition = pathEntity.getComponent(Path.class).getSource().getComponent(Transform.class);
            // TODO: Transform targetPortPosition = pathEntity.getComponent(Path.class).getTarget().getComponent(Transform.class);
            Transform sourcePortPosition = sourcePortShape.getPosition();
            Transform targetPortPosition = targetPortShape.getPosition();
            // TODO: Transform sourcePortPosition = sourcePortShapeE.getComponent(Transform.class);
            // TODO: Transform targetPortPosition = targetPortShapeE.getComponent(Transform.class);

            // Update color of Port shape based on its type
            Path.Type pathType = Path.getType(path);
            String pathColor = camp.computer.clay.util.Color.getColor(pathType);
            sourcePortShape.setColor(pathColor);
            targetPortShape.setColor(pathColor);

            // Color
            palette.paint.setStyle(Paint.Style.STROKE);
            palette.paint.setStrokeWidth(15.0f);
            palette.paint.setColor(Color.parseColor(pathColor));

            double pathRotationAngle = Geometry.getAngle(sourcePortPosition, targetPortPosition);
            Transform pathStartCoordinate = Geometry.getRotateTranslatePoint(sourcePortPosition, pathRotationAngle, 0);
            Transform pathStopCoordinate = Geometry.getRotateTranslatePoint(targetPortPosition, pathRotationAngle + 180, 0);

            // TODO: Create Segment and add it to the PathImage. Update its geometry to change position, rotation, etc.

            Segment segment = (Segment) Image.getShape(path, "Path").getComponent(ShapeComponent.class).shape; // path.getComponent(Image.class).getImage().getShape("Path");
            segment.setOutlineThickness(15.0);
            segment.setOutlineColor(sourcePortShape.getColor());

            segment.setSource(pathStartCoordinate);
            segment.setTarget(pathStopCoordinate);

            // Draw shapes in Path
            drawShape(segment, palette);
            drawShape(sourcePortShape, palette);
            drawShape(targetPortShape, palette);

            // Draw Boundaries
            palette.paint.setStrokeWidth(3.0f);
            palette.paint.setStyle(Paint.Style.STROKE);
            palette.paint.setColor(Color.CYAN);
            drawPolygon(Boundary.getBoundary(Image.getShape(path, "Source Port")), palette);
            drawPolygon(Boundary.getBoundary(Image.getShape(path, "Target Port")), palette);
//        }

        } else {

            // Singleton Path

            Entity sourcePortPathShapeE = Image.getShape(path, "Source Port");
            Shape sourcePortShape = sourcePortPathShapeE.getComponent(ShapeComponent.class).shape; // path.getComponent(Image.class).getImage().getShape("Source Port");

            path.getComponent(Transform.class).set(sourcePortShape.getPosition());

            if (Path.getState(path) != Component.State.EDITING) {
                sourcePortShape.setPosition(hostSourcePortShape.getPosition());
            }

            // Update color of Port shape based on its type
            Path.Type pathType = Path.getType(path);
            String pathColor = camp.computer.clay.util.Color.getColor(pathType);
            sourcePortShape.setColor(pathColor);

            sourcePortShape.setColor(pathColor);

            // Color
            palette.paint.setStyle(Paint.Style.STROKE);
            palette.paint.setStrokeWidth(15.0f);
            palette.paint.setColor(Color.parseColor(pathColor));

            // TODO: Create Segment and add it to the PathImage. Update its geometry to change position, rotation, etc.
            Segment segment = (Segment) Image.getShape(path, "Path").getComponent(ShapeComponent.class).shape;
            segment.setOutlineThickness(15.0);
            segment.setOutlineColor(sourcePortShape.getColor());

            segment.setSource(sourcePortPathShapeE.getComponent(Transform.class));
            if (Path.getState(path) != Component.State.EDITING) {
                segment.setTarget(sourcePortPathShapeE.getComponent(Transform.class));
            }

            // Draw shapes in Path
            drawSegment(segment, palette);
            drawShape(sourcePortShape, palette);
            // drawShape(targetPortShape);

            // Draw Boundary
            palette.paint.setStrokeWidth(3.0f);
            palette.paint.setStyle(Paint.Style.STROKE);
            palette.paint.setColor(Color.CYAN);
            drawPolygon(Boundary.getBoundary(Image.getShape(path, "Source Port")), palette);

        }
    }

    public void drawOverviewPath(Entity path, Palette palette) {

        // Get Host and Extension Ports
        Entity hostPort = Path.getSource(path);
        Entity extensionPort = Path.getTarget(path);

        if (extensionPort == null) {

            // TODO: Singleton Path

        } else {

            // Draw the connection between the Host's Port and the Extension's Port
            Image hostImage = hostPort.getParent().getComponent(Image.class);
            Image extensionImage = extensionPort.getParent().getComponent(Image.class);

            Entity host = hostImage.getEntity();
            Entity extension = extensionImage.getEntity();

            if (host.getComponent(Portable.class).headerContactPositions.size() > Port.getIndex(hostPort)
                    && extension.getComponent(Portable.class).headerContactPositions.size() > Port.getIndex(extensionPort)) {

                int hostPortIndex = Port.getIndex(hostPort);
                int extensionPortIndex = Port.getIndex(extensionPort);
                Transform hostConnectorPosition = host.getComponent(Portable.class).headerContactPositions.get(hostPortIndex).getPosition();
                Transform extensionConnectorPosition = extension.getComponent(Portable.class).headerContactPositions.get(extensionPortIndex).getPosition();

                // Draw connection between Ports
                palette.paint.setColor(android.graphics.Color.parseColor(camp.computer.clay.util.Color.getColor(Port.getType(extensionPort))));
                palette.paint.setStrokeWidth(10.0f);

                // TODO: Create Segment and add it to the PathImage. Update its geometry to change position, rotation, etc.
                Segment segment = (Segment) Image.getShape(path, "Path").getComponent(ShapeComponent.class).shape; // path.getComponent(Image.class).getImage().getShape("Path");
                segment.setOutlineThickness(10.0);
                segment.setOutlineColor(camp.computer.clay.util.Color.getColor(Port.getType(extensionPort)));

                segment.setSource(hostConnectorPosition);
                segment.setTarget(extensionConnectorPosition);

                drawSegment(segment, palette);
            }
        }
    }
    // </PATH_IMAGE_HELPERS>

    public void drawShape(Entity shape, Palette palette) {

        // <HACK>
        shape.getComponent(ShapeComponent.class).shape.setPosition(
                shape.getComponent(Transform.class)
        );
        shape.getComponent(ShapeComponent.class).shape.setRotation(
                shape.getComponent(Transform.class).getRotation()
        );

        drawShape(shape.getComponent(ShapeComponent.class).shape, palette);
        // </HACK>
    }

    // TODO: Replace with more direct drawing? What's the minimal layering between image
    // TODO: (...) representation and platform-level rendering?
    public void drawShape(Shape shape, Palette palette) {
        if (shape.getClass() == Point.class) {
            // TODO:
        } else if (shape.getClass() == Segment.class) {
            drawSegment((Segment) shape, palette);
        } else if (shape.getClass() == Polyline.class) {
            drawPolyline((Polyline) shape, palette);
        } else if (shape.getClass() == Triangle.class) {
            // TODO: drawTriangle((Triangle) shape);
        } else if (shape.getClass() == Rectangle.class) {
            drawRectangle((Rectangle) shape, palette);
        } else if (shape.getClass() == Polygon.class) {
            drawPolygon((Polygon) shape, palette);
        } else if (shape.getClass() == Circle.class) {
            drawCircle((Circle) shape, palette);
        } else if (shape.getClass() == Text.class) {
            drawText((Text) shape, palette);
        }
    }

    public void drawSegment(Transform source, Transform target, Palette palette) {
        canvas.drawLine((float) source.x, (float) source.y, (float) target.x, (float) target.y, paint);
    }

    public void drawSegment(Segment segment, Palette palette) {

        palette.paint.setStyle(Paint.Style.STROKE);
        palette.paint.setColor(segment.outlineColorCode);
        palette.paint.setStrokeWidth((float) segment.getOutlineThickness());

        // Color
        palette.canvas.drawLine((float) segment.getSource().x, (float) segment.getSource().y, (float) segment.getTarget().x, (float) segment.getTarget().y, palette.paint);
    }

    public void drawPolyline(Polyline polyline, Palette palette) {
        drawPolyline(polyline.getPoints(), palette);
    }

    // TODO: Refactor with transforms
    public void drawPolyline(List<Transform> vertices, Palette palette) {

        for (int i = 0; i < vertices.size() - 1; i++) {

            palette.canvas.drawLine(
                    (float) vertices.get(i).x,
                    (float) vertices.get(i).y,
                    (float) vertices.get(i + 1).x,
                    (float) vertices.get(i + 1).y,
                    palette.paint
            );
        }
    }

    public void drawCircle(Circle circle, Palette palette) {

        palette.canvas.save();

        palette.canvas.translate((float) circle.getPosition().x, (float) circle.getPosition().y);
        palette.canvas.rotate((float) circle.getPosition().rotation);

        // Fill
        palette.paint.setStyle(Paint.Style.FILL);
        palette.paint.setColor(circle.colorCode);
        palette.canvas.drawCircle(0, 0, (float) circle.radius, palette.paint);

        // Outline
        if (circle.getOutlineThickness() > 0) {
            palette.paint.setStyle(Paint.Style.STROKE);
            palette.paint.setColor(circle.outlineColorCode);
            palette.paint.setStrokeWidth((float) circle.outlineThickness);

            palette.canvas.drawCircle(0, 0, (float) circle.radius, paint);
        }

        palette.canvas.restore();
    }

//    public void drawCircle(Transform position, double radius, double angle) {
//
//        canvas.save();
//
//        canvas.translate((float) position.x, (float) position.y);
//        canvas.rotate((float) angle);
//
//        canvas.drawCircle(0.0f, 0.0f, (float) radius, paint);
//
//        canvas.restore();
//
//    }

    public void drawRectangle(Rectangle rectangle, Palette palette) {

        // Set style
        palette.paint.setStyle(Paint.Style.FILL);
        palette.paint.setColor(rectangle.colorCode);

        palette.canvas.save();
        palette.canvas.translate((float) rectangle.getPosition().x, (float) rectangle.getPosition().y);
        palette.canvas.rotate((float) rectangle.getRotation());

        palette.canvas.drawRoundRect(
                (float) (0 - (rectangle.width / 2.0)),
                (float) (0 - (rectangle.height / 2.0)),
                (float) (0 + (rectangle.width / 2.0)),
                (float) (0 + (rectangle.height / 2.0)),
                (float) rectangle.cornerRadius,
                (float) rectangle.cornerRadius,
                palette.paint
        );

        // Draw Points in Shape
        if (rectangle.getOutlineThickness() > 0) {

            palette.paint.setStyle(Paint.Style.STROKE);
            palette.paint.setColor(rectangle.outlineColorCode);
            palette.paint.setStrokeWidth((float) rectangle.outlineThickness);

            palette.canvas.drawRoundRect(
                    (float) (0 - (rectangle.width / 2.0)),
                    (float) (0 - (rectangle.height / 2.0)),
                    (float) (0 + (rectangle.width / 2.0)),
                    (float) (0 + (rectangle.height / 2.0)),
                    (float) rectangle.cornerRadius,
                    (float) rectangle.cornerRadius,
                    palette.paint
            );
        }

        palette.canvas.restore();
    }

//    public void drawRectangle(Transform position, double angle, double width, double height) {
//
//        canvas.save();
//
//        canvas.translate((float) position.x, (float) position.y);
//        canvas.rotate((float) angle);
//
//        canvas.drawRect(
//                (float) (0 - (width / 2.0f)),
//                (float) (0 - (height / 2.0f)),
//                (float) (0 + (width / 2.0f)),
//                (float) (0 + (height / 2.0f)),
//                paint
//        );
//
//        canvas.restore();
//    }

    public void drawText(Text text, Palette palette) {

        palette.canvas.save();
        palette.canvas.translate((float) text.getPosition().x, (float) text.getPosition().y);
        palette.canvas.rotate((float) text.getPosition().rotation);

        // Style
        palette.paint.setColor(Color.BLACK);
        palette.paint.setStyle(Paint.Style.FILL);
        palette.paint.setTextSize((float) text.size);

        // Font
        Typeface typeface = Typeface.createFromAsset(Application.getView().getAssets(), text.font);
        Typeface boldTypeface = Typeface.create(typeface, Typeface.NORMAL);
        paint.setTypeface(boldTypeface);

        // Style (Guaranteed)
        String printText = text.getText().toUpperCase();
        palette.paint.setStyle(Paint.Style.FILL);

        // Draw
        Rect textBounds = new Rect();
        palette.paint.getTextBounds(printText, 0, printText.length(), textBounds);
        palette.canvas.drawText(printText, (float) 0 - textBounds.width() / 2.0f, (float) 0 + textBounds.height() / 2.0f, palette.paint);

        palette.canvas.restore();
    }

    public void drawText(Transform position, String text, double size, Palette palette) {

        // Style
        palette.paint.setTextSize((float) size);

        /*
        // Font
        Typeface typeface = Typeface.createFromAsset(Application.getView().getAssets(), NOTIFICATION_FONT);
        Typeface boldTypeface = Typeface.create(typeface, Typeface.NORMAL);
        paint.setTypeface(boldTypeface);
        */

        // Style (Guaranteed)
        text = text.toUpperCase();
        palette.paint.setStyle(Paint.Style.FILL);

        // Draw
        Rect bounds = new Rect();
        palette.paint.getTextBounds(text, 0, text.length(), bounds);
        palette.canvas.drawText(text, (float) position.x, (float) position.y + bounds.height() / 2.0f, palette.paint);
    }

//    public void drawTrianglePath(Transform startPosition, Transform stopPosition, double triangleWidth, double triangleHeight) {
//
//        double pathRotationAngle = Geometry.getAngle(startPosition, stopPosition);
//
//        double triangleRotationAngle = pathRotationAngle + 90.0f;
//
//        double pathDistance = Geometry.distance(startPosition, stopPosition);
//
//        int triangleCount = (int) (pathDistance / (triangleHeight + 15));
//        double triangleSpacing2 = pathDistance / triangleCount;
//
//        for (int k = 0; k <= triangleCount; k++) {
//
//            // Calculate triangle position
//            Transform triangleCenterPosition = Geometry.getRotateTranslatePoint(startPosition, pathRotationAngle, k * triangleSpacing2);
//
//            paint.setStyle(Paint.Style.FILL);
//            drawTriangle(triangleCenterPosition, triangleRotationAngle, triangleWidth, triangleHeight);
//        }
//    }

    // TODO: Refactor with transforms
    public void drawPolygon(Polygon polygon, Palette palette) {
        // TODO: drawPolygon(BoundarySystem.getBoundary(polygon), palette);
    }

    // TODO: Refactor with transforms
    public void drawPolygon(List<Transform> vertices, Palette palette) {

        // Draw vertex Points in Shape
        android.graphics.Path path = new android.graphics.Path();
        path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
        path.moveTo((float) vertices.get(0).x, (float) vertices.get(0).y);
        for (int i = 1; i < vertices.size(); i++) {
            path.lineTo((float) vertices.get(i).x, (float) vertices.get(i).y);
        }
//        path.lineTo((float) boundary.get(0).x, (float) boundary.get(0).y);
        path.close();

        palette.canvas.drawPath(path, palette.paint);
    }

//    // TODO: Refactor with transforms
//    public void drawTriangle(Transform position, double angle, double width, double height) {
//
//        // Calculate pointerCoordinates before rotation
//        Transform p1 = new Transform(position.x + -(width / 2.0f), position.y + (height / 2.0f));
//        Transform p2 = new Transform(position.x + 0, position.y - (height / 2.0f));
//        Transform p3 = new Transform(position.x + (width / 2.0f), position.y + (height / 2.0f));
//
//        // Calculate pointerCoordinates after rotation
//        Transform rp1 = Geometry.getRotateTranslatePoint(position, angle + Geometry.getAngle(position, p1), Geometry.distance(position, p1));
//        Transform rp2 = Geometry.getRotateTranslatePoint(position, angle + Geometry.getAngle(position, p2), Geometry.distance(position, p2));
//        Transform rp3 = Geometry.getRotateTranslatePoint(position, angle + Geometry.getAngle(position, p3), Geometry.distance(position, p3));
//
//        android.graphics.Path path = new android.graphics.Path();
//        path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
//        path.moveTo((float) rp1.x, (float) rp1.y);
//        path.lineTo((float) rp2.x, (float) rp2.y);
//        path.lineTo((float) rp3.x, (float) rp3.y);
//        path.close();
//
//        canvas.drawPath(path, paint);
//    }
}
