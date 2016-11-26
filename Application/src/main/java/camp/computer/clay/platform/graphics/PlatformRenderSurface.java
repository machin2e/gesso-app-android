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
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Boundary;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Component;
import camp.computer.clay.engine.component.Geometry;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Event;
import camp.computer.clay.engine.system.CameraSystem;
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

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);

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
        InputSystem inputSystem = world.getSystem(InputSystem.class);

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
                    double cameraScale = world.getSystem(CameraSystem.class).getScale(camera);
                    event.pointerCoordinates[id].x = (motionEvent.getX(i) - (originPosition.x + cameraTransform.x)) / cameraScale;
                    event.pointerCoordinates[id].y = (motionEvent.getY(i) - (originPosition.y + cameraTransform.y)) / cameraScale;
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
                InputSystem inputSystem = world.getSystem(InputSystem.class);

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

    public boolean isUpdated = false;

    public void update() {

        if (world == null) {
            return;
        }

//        Canvas canvas = null;
//        SurfaceHolder holder = getHolder();

//        if (!isUpdated) {
        world.update();
        isUpdated = true;
//        }

        try {
            canvas = holder.lockCanvas();
            if (canvas != null) {
                if (isUpdated) {
                    palette.canvas = canvas;
                    synchronized (holder) {
                        // TODO!!!!!!!!!!!! FLATTEN THE CALLBACK TREE!!!!!!!!!!!!! FUCK!!!!!!!!
                        world.draw();
                    }
                    isUpdated = false;
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
        Application.getInstance().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenWidth = metrics.widthPixels;
        int screenHeight = metrics.heightPixels;

        // Set camera viewport dimensions
        Entity camera = world.Manager.getEntities().filterWithComponent(Camera.class).get(0);
        world.getSystem(CameraSystem.class).setWidth(camera, screenWidth);
        world.getSystem(CameraSystem.class).setHeight(camera, screenHeight);
    }

    // TODO: 11/16/2016 Optimize! Big and slow! Should be fast!
    public void drawEditablePath(Entity path, Palette palette) {

        Entity sourcePort = Path.getSource(path);
        Entity sourcePortShapeE = Image.getShape(sourcePort, "Port");
        Shape hostSourcePortShape = sourcePortShapeE.getComponent(Geometry.class).shape; // Path.getSource(path).getComponent(Image.class).getImage().getShape("Port");
        Shape extensionTargetPortShape = null;

        boolean isSingletonPath = (Path.getTarget(path) == null);

        if (!isSingletonPath) {

//            Entity targetPort = Path.getTarget(path);

            Entity targetPortShapeE = Image.getShape(Path.getTarget(path), "Port");
//            extensionTargetPortShape = Image.getShape(Path.getTarget(path), "Port").getComponent(Geometry.class).shape; // Path.getTarget(path).getComponent(Image.class).getImage().getShape("Port");

            Shape sourcePortShape = Image.getShape(path, "Source Port").getComponent(Geometry.class).shape; // path.getComponent(Image.class).getImage().getShape("Source Port");
            Shape targetPortShape = Image.getShape(path, "Target Port").getComponent(Geometry.class).shape; // path.getComponent(Image.class).getImage().getShape("Target Port");

//            path.getComponent(Transform.class).set(
////                    (sourcePortShape.getPosition().x + targetPortShape.getPosition().x) / 2.0,
////                    (sourcePortShape.getPosition().y + targetPortShape.getPosition().y) / 2.0
//                    (sourcePortShapeE.getComponent(Transform.class).x + targetPortShapeE.getComponent(Transform.class).x) / 2.0,
//                    (sourcePortShapeE.getComponent(Transform.class).x + targetPortShapeE.getComponent(Transform.class).x) / 2.0
//            );

            sourcePortShape.setColor(hostSourcePortShape.getColor());

            if (Path.getState(path) != Component.State.EDITING) {
//                Image.getShape(path, "Source Port").getComponent(Transform.class).set(sourcePort.getComponent(Transform.class)); // sourcePortShape.setPosition(hostSourcePortShape.getPosition());
//                Image.getShape(path, "Target Port").getComponent(Transform.class).set(targetPort.getComponent(Transform.class)); // targetPortShape.setPosition(extensionTargetPortShape.getPosition());
                // TODO: sourcePortShape.setPosition(sourcePortShapeE.getComponent(Transform.class));
                // TODO: targetPortShape.setPosition(targetPortShapeE.getComponent(Transform.class));
                Image.getShape(path, "Source Port").getComponent(Transform.class).set(sourcePortShapeE.getComponent(Transform.class)); // sourcePortShape.setPosition(hostSourcePortShape.getPosition());
                Image.getShape(path, "Target Port").getComponent(Transform.class).set(targetPortShapeE.getComponent(Transform.class)); // targetPortShape.setPosition(extensionTargetPortShape.getPosition());
            }

            // <HACK>
//            BoundarySystem.updateShapeBoundary(sourcePortShape);
//            BoundarySystem.updateShapeBoundary(targetPortShape);
            // </HACK>

            // TODO: Transform sourcePortPosition = pathEntity.getComponent(Path.class).getSource().getComponent(Transform.class);
            // TODO: Transform targetPortPosition = pathEntity.getComponent(Path.class).getTarget().getComponent(Transform.class);
//            Transform sourcePortPosition = sourcePortShape.getPosition();
//            Transform targetPortPosition = targetPortShape.getPosition();
//            Transform sourcePortPosition = sourcePortShapeE.getComponent(Transform.class);
//            Transform targetPortPosition = targetPortShapeE.getComponent(Transform.class);

            // Update color of Port shape based on its type
            Path.Type pathType = Path.getType(path);
            String pathColor = camp.computer.clay.util.Color.getColor(pathType);
            sourcePortShape.setColor(pathColor);
            targetPortShape.setColor(pathColor);

            // Color
//            palette.paint.setStyle(Paint.Style.STROKE);
//            palette.paint.setStrokeWidth((float) World.PATH_EDITVIEW_THICKNESS);
//            palette.paint.setColor(Color.parseColor(pathColor));

//            double pathRotationAngle = Geometry.getAngle(sourcePortPosition, targetPortPosition);
//            Transform pathStartCoordinate = Geometry.getRotateTranslatePoint(sourcePortPosition, pathRotationAngle, 0);
//            Transform pathStopCoordinate = Geometry.getRotateTranslatePoint(targetPortPosition, pathRotationAngle + 180, 0);

            // TODO: Create Segment and add it to the PathImage. Update its geometry to change position, rotation, etc.

            Segment segment = (Segment) Image.getShape(path, "Path").getComponent(Geometry.class).shape; // path.getComponent(Image.class).getImage().getShape("Path");
            segment.setOutlineThickness(World.PATH_EDITVIEW_THICKNESS);
            segment.setOutlineColor(sourcePortShape.getColor());

//            segment.setSource(Image.getShape(path, "Source Port").getComponent(Transform.class));
//            segment.setTarget(Image.getShape(path, "Target Port").getComponent(Transform.class));

            // <REFACTOR>
            palette.outlineThickness = World.PATH_EDITVIEW_THICKNESS;
            palette.outlineColor = pathColor;
            // </REFACTOR>

            // Draw shapes in Path
            drawSegment(Image.getShape(path, "Source Port").getComponent(Transform.class), Image.getShape(path, "Target Port").getComponent(Transform.class), palette); // drawShape(segment, palette);
            drawShape(Image.getShape(path, "Source Port"), palette); // drawCircle((Circle) sourcePortShape, palette); // drawShape(sourcePortShape, palette);
            drawShape(Image.getShape(path, "Target Port"), palette); // drawCircle((Circle) targetPortShape, palette); // drawShape(targetPortShape, palette);

            // <DRAW_BOUNDARY>
            palette.paint.setStrokeWidth(3.0f);
            palette.paint.setStyle(Paint.Style.STROKE);
            palette.paint.setColor(Color.CYAN);
            drawPolygon(Boundary.get(Image.getShape(path, "Source Port")), palette);
            drawPolygon(Boundary.get(Image.getShape(path, "Target Port")), palette);
            // </DRAW_BOUNDARY>

        } else {

            // Singleton Path

            Entity sourcePortPathShapeE = Image.getShape(path, "Source Port");
            Shape sourcePortShape = sourcePortPathShapeE.getComponent(Geometry.class).shape; // path.getComponent(Image.class).getImage().getShape("Source Port");

            path.getComponent(Transform.class).set(sourcePort.getComponent(Transform.class)); // path.getComponent(Transform.class).set(sourcePortShape.getPosition());

//            sourcePortPathShapeE.getComponent(Transform)

            if (Path.getState(path) != Component.State.EDITING) {
                sourcePortPathShapeE.getComponent(Transform.class).set(sourcePortShapeE.getComponent(Transform.class)); // sourcePortShape.setPosition(hostSourcePortShape.getPosition());
//                sourcePortPathShapeE.getComponent(Transform.class).set(sourcePort.getComponent(Transform.class));
            }

            // Update color of Port shape based on its type
            Path.Type pathType = Path.getType(path);
            String pathColor = camp.computer.clay.util.Color.getColor(pathType);
            sourcePortShape.setColor(pathColor);

            // Color
//            palette.paint.setStyle(Paint.Style.STROKE);
//            palette.paint.setStrokeWidth(15.0f);
//            palette.paint.setColor(Color.parseColor(pathColor));

            // TODO: Create Segment and add it to the PathImage. Update its geometry to change position, rotation, etc.
            Segment segment = (Segment) Image.getShape(path, "Path").getComponent(Geometry.class).shape;
            segment.setOutlineThickness(15.0);
            segment.setOutlineColor(sourcePortShape.getColor());

            segment.setSource(sourcePortPathShapeE.getComponent(Transform.class));
            if (Path.getState(path) != Component.State.EDITING) {
                segment.setTarget(sourcePortPathShapeE.getComponent(Transform.class));
            }

            // <REFACTOR>
            palette.outlineThickness = World.PATH_OVERVIEW_THICKNESS;
            palette.outlineColor = pathColor;
            // </REFACTOR>

            // Draw shapes in Path
            drawShape(Image.getShape(path, "Path"), palette); // drawSegment(segment, palette);
            drawShape(sourcePortPathShapeE, palette); // drawCircle((Circle) sourcePortShape, palette); // drawShape(sourcePortShape, palette);
            // drawShape(targetPortShape);

            // Draw Boundary
            palette.paint.setStrokeWidth(3.0f);
            palette.paint.setStyle(Paint.Style.STROKE);
            palette.paint.setColor(Color.CYAN);
            drawPolygon(Boundary.get(sourcePortPathShapeE), palette);

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
            Entity host = hostPort.getParent();
            Entity extension = extensionPort.getParent();

            if (host.getComponent(Portable.class).headerContactGeometries.size() > Port.getIndex(hostPort)
                    && extension.getComponent(Portable.class).headerContactGeometries.size() > Port.getIndex(extensionPort)) {

                int hostPortIndex = Port.getIndex(hostPort);
                int extensionPortIndex = Port.getIndex(extensionPort);
//                Transform hostConnectorPosition = host.getComponent(Portable.class).headerContactGeometries.get(hostPortIndex).getPosition();
//                Transform extensionConnectorPosition = extension.getComponent(Portable.class).headerContactGeometries.get(extensionPortIndex).getPosition();
                Transform hostContactTransform = host.getComponent(Portable.class).headerContactGeometries.get(hostPortIndex).getComponent(Transform.class);
                Transform extensionContactTransform = extension.getComponent(Portable.class).headerContactGeometries.get(extensionPortIndex).getComponent(Transform.class);

                // Draw connection between Ports
                palette.paint.setColor(android.graphics.Color.parseColor(camp.computer.clay.util.Color.getColor(Port.getType(extensionPort))));
                palette.paint.setStrokeWidth(10.0f);

                // TODO: Create Segment and add it to the PathImage. Update its geometry to change position, rotation, etc.
                Entity shapeEntity = Image.getShape(path, "Path");
                Segment segment = (Segment) shapeEntity.getComponent(Geometry.class).shape; // path.getComponent(Image.class).getImage().getShape("Path");
                segment.setOutlineThickness(10.0);
                segment.setOutlineColor(camp.computer.clay.util.Color.getColor(Port.getType(extensionPort)));

                palette.outlineThickness = 10.0;
                palette.outlineColor = camp.computer.clay.util.Color.getColor(Port.getType(extensionPort));

                segment.setSource(hostContactTransform);
                segment.setTarget(extensionContactTransform);

                drawShape(shapeEntity, palette);
            }
        }
    }
    // </PATH_IMAGE_HELPERS>

    public void drawShape(Entity shape, Palette palette) {

        // <HACK>
        shape.getComponent(Geometry.class).shape.setPosition(
                shape.getComponent(Transform.class)
        );
        shape.getComponent(Geometry.class).shape.setRotation(
                shape.getComponent(Transform.class).getRotation()
        );

        Shape s = shape.getComponent(Geometry.class).shape;

        // Palette
        palette.paint.setStyle(Paint.Style.STROKE);
        palette.paint.setColor(s.outlineColorCode);
        palette.paint.setStrokeWidth((float) s.getOutlineThickness());

        palette.color = s.getColor();
        palette.outlineColor = s.getOutlineColor();
        palette.outlineThickness = s.outlineThickness;

        // TODO: drawShape(shape, palette);
        if (s.getClass() == Point.class) {
            // TODO:
        } else if (s.getClass() == Segment.class) {
            Segment segment = (Segment) s;
            drawSegment(segment.getSource(), segment.getTarget(), palette);
        } else if (s.getClass() == Polyline.class) {
            // TODO: drawPolyline((Polyline) shape, palette);
        } else if (s.getClass() == Triangle.class) {
            // TODO: drawTriangle((Triangle) shape);
        } else if (s.getClass() == Rectangle.class) {
            Rectangle rectangle = (Rectangle) s;
            drawRectangle(shape.getComponent(Transform.class), rectangle.width, rectangle.height, rectangle.cornerRadius, palette);
        } else if (s.getClass() == Polygon.class) {
            Polygon polygon = (Polygon) s;
            drawPolygon(polygon.getVertices(), palette);
        } else if (s.getClass() == Circle.class) {
            Circle circle = (Circle) s;
            drawCircle(shape.getComponent(Transform.class), circle.radius, palette);
        } else if (s.getClass() == Text.class) {
            Text text = (Text) s;
            drawText(shape.getComponent(Transform.class), text.getText(), text.size, palette);
        }
        // </HACK>
    }

    public void drawSegment(Transform source, Transform target, Palette palette) {

        palette.paint.setStyle(Paint.Style.STROKE);
        palette.paint.setColor(Color.parseColor(palette.outlineColor));
        palette.paint.setStrokeWidth((float) palette.outlineThickness);

        canvas.drawLine((float) source.x, (float) source.y, (float) target.x, (float) target.y, paint);
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

    public void drawCircle(Transform transform, double radius, Palette palette) {

        // Set style
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor(palette.color));

        canvas.save();
        canvas.translate((float) transform.x, (float) transform.y);
        canvas.rotate((float) transform.rotation);

        canvas.drawCircle(0.0f, 0.0f, (float) radius, paint);

        // Draw Points in Shape
        if (palette.outlineThickness > 0) {

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.parseColor(palette.outlineColor));
            paint.setStrokeWidth((float) palette.outlineThickness);

            canvas.drawCircle(0.0f, 0.0f, (float) radius, paint);
        }

        canvas.restore();

    }

    public void drawRectangle(Transform transform, double width, double height, double cornerRadius, Palette palette) {

        // Set style
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.parseColor(palette.color));

        canvas.save();
        canvas.translate((float) transform.x, (float) transform.y);
        canvas.rotate((float) transform.rotation);

        canvas.drawRoundRect(
                (float) (0 - (width / 2.0)),
                (float) (0 - (height / 2.0)),
                (float) (0 + (width / 2.0)),
                (float) (0 + (height / 2.0)),
                (float) cornerRadius,
                (float) cornerRadius,
                paint
        );

        // Draw Points in Shape
        if (palette.outlineThickness > 0) {

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.parseColor(palette.outlineColor));
            paint.setStrokeWidth((float) palette.outlineThickness);

            canvas.drawRoundRect(
                    (float) (0 - (width / 2.0)),
                    (float) (0 - (height / 2.0)),
                    (float) (0 + (width / 2.0)),
                    (float) (0 + (height / 2.0)),
                    (float) cornerRadius,
                    (float) cornerRadius,
                    palette.paint
            );
        }

        canvas.restore();
    }

    public void drawText(Transform position, String text, double size, Palette palette) {

        palette.canvas.save();
        palette.canvas.translate((float) position.x, (float) position.y);
        palette.canvas.rotate((float) position.rotation);

        // Style
        palette.paint.setColor(Color.BLACK);
        palette.paint.setStyle(Paint.Style.FILL);
        palette.paint.setTextSize((float) size);

        // Font
//        Typeface typeface = Typeface.createFromAsset(Application.getInstance().getAssets(), text.font);
//        Typeface boldTypeface = Typeface.create(typeface, Typeface.NORMAL);
//        paint.setTypeface(boldTypeface);

        // Style (Guaranteed)
        String printText = text.toUpperCase();
        palette.paint.setStyle(Paint.Style.FILL);

        // Draw
        Rect textBounds = new Rect();
        palette.paint.getTextBounds(printText, 0, printText.length(), textBounds);
        palette.canvas.drawText(printText, (float) 0 - textBounds.width() / 2.0f, (float) 0 + textBounds.height() / 2.0f, palette.paint);

        palette.canvas.restore();
    }

    // TODO: Refactor with transforms
    public void drawPolygon(List<Transform> vertices, Palette palette) {

        // <HACK>
        if (vertices == null) {
            return;
        }
        // </HACK>

        // Draw vertex Points in Shape
        android.graphics.Path path = new android.graphics.Path();
        path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
        path.moveTo((float) vertices.get(0).x, (float) vertices.get(0).y);
        for (int i = 1; i < vertices.size(); i++) {
            path.lineTo((float) vertices.get(i).x, (float) vertices.get(i).y);
        }
        // path.lineTo((float) boundary.get(0).x, (float) boundary.get(0).y);
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
