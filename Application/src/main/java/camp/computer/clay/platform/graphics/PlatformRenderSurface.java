package camp.computer.clay.platform.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.util.AttributeSet;
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
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Model;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Physics;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Primitive;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.Visibility;
import camp.computer.clay.engine.component.util.Visible;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Event;
import camp.computer.clay.engine.manager.Group;
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
import camp.computer.clay.platform.util.DeviceDimensionsHelper;

public class PlatformRenderSurface extends SurfaceView implements SurfaceHolder.Callback {

    // World Rendering Context
//    public Canvas canvas = null;
//    public Matrix identityMatrix;

    // World PlatformRenderClock
    private SurfaceHolder surfaceHolder;

    public PlatformRenderClock platformRenderClock;

    // Coordinate System (Grid)
    public Transform originTransform = new Transform();

    // World
    public World world;

    public PlatformRenderSurface(Context context) {
        super(context);
        setFocusable(true);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);
    }

    public PlatformRenderSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PlatformRenderSurface(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        // Create a bitmap to use as a drawing buffer equal in size to the full size of the Surface
        Bitmap canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(canvasBitmap);

        // Create Identity Matrix
        // Matrix identityMatrix = new Matrix();

        // Center the world coordinate system
        originTransform.set(canvas.getWidth() / 2.0f, canvas.getHeight() / 2.0f);

        surfaceHolder = getHolder();
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

        // TODO: surfaceHolder = getHolder(); for resuming application. Needed?
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

    public SurfaceHolder getSurfaceHolder() {
        return surfaceHolder;
    }

    private Event previousEvent = null;

    Entity camera = null;

    // https://developer.android.com/training/gestures/scale.html
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        if (this.world == null) {
            return false;
        }

        if (this.camera == null) {
            this.camera = world.Manager.getEntities().filterWithComponent(Camera.class).get(0);
        }

        Transform cameraTransform = camera.getComponent(Transform.class);
        double cameraScale = world.getSystem(CameraSystem.class).getScale(camera);

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

//                double unscaledSurfaceX = 0;
//                double unscaledSurfaceY = 0;

                // Current
                // Update pointerCoordinates state based the pointerCoordinates given by the host OS (e.g., Android).
                for (int i = 0; i < pointerCount; i++) {
                    int id = motionEvent.getPointerId(i);

                    event.surfaceCoordinates[id].x = motionEvent.getX(i);
                    event.surfaceCoordinates[id].y = motionEvent.getY(i);

//                    unscaledSurfaceX = (motionEvent.getX(i) - (originTransform.x + cameraTransform.x));
//                    unscaledSurfaceY = (motionEvent.getY(i) - (originTransform.y + cameraTransform.y));

                    // TODO: Update equations so cameraScale is always the correct scale, the current scale, and computed as needed.
                    event.pointerCoordinates[id].x = (motionEvent.getX(i) - (originTransform.x + cameraTransform.x)) / cameraScale;
                    event.pointerCoordinates[id].y = (motionEvent.getY(i) - (originTransform.y + cameraTransform.y)) / cameraScale;

                }

//                Log.v("PlatformRenderSurface", "x: " + motionEvent.getX() + ", y: " + motionEvent.getY());
//                Log.v("PlatformRenderSurface", "x': " + unscaledSurfaceX + ", y': " + unscaledSurfaceY);
//                Log.v("PlatformRenderSurface", "x'': " + event.pointerCoordinates[0].x + ", y'': " + event.pointerCoordinates[0].y);
//                Log.v("PlatformRenderSurface", "---");

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

            if (previousEvent.getDistance() < Event.MINIMUM_MOVE_DISTANCE) {
                InputSystem inputSystem = world.getSystem(InputSystem.class);

                Event event = new Event();
                // event.pointerCoordinates[id].x = (motionEvent.getX(i) - (originTransform.x + perspectivePosition.x)) / perspectiveScale;
                // event.pointerCoordinates[id].y = (motionEvent.getY(i) - (originTransform.y + perspectivePosition.y)) / perspectiveScale;
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

    // <TODO: REMOVE_REFERENCE_TO_WORLD>
    public void setWorld(World world) {
        this.world = world;

        // Set camera viewport dimensions from the width and height of the device
        Entity camera = world.Manager.getEntities().filterWithComponent(Camera.class).get(0);
        world.getSystem(CameraSystem.class).setWidth(camera, DeviceDimensionsHelper.getDisplayWidth(Application.getContext()));
        world.getSystem(CameraSystem.class).setHeight(camera, DeviceDimensionsHelper.getDisplayHeight(Application.getContext()));
    }
    // </TODO: REMOVE_REFERENCE_TO_WORLD>

    public void drawRenderables(Group<Entity> entities, Canvas canvas, Paint paint, Palette palette) {

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

            if (entity.hasComponent(Path.class)) {

                // <REFACTOR>
                // TODO: Make the rendering state/configuration automatic... enable or disable geometry sets in the image?
                Visibility visibility = entity.getComponent(Visibility.class);
                if (visibility != null && visibility.getVisibile() == Visible.VISIBLE) {
                    drawEditablePath(entity, canvas, paint, palette);
                } else if (visibility != null && visibility.getVisibile() == Visible.INVISIBLE) {
                    if (Path.getMode(entity) == Path.Mode.ELECTRONIC) {
                        drawOverviewPath(entity, canvas, paint, palette);
                    }
                }
                // </REFACTOR>

            }
            // <REFACTOR>
            // This was added so Prototype Extension/Path would draw without Extension/Path components
            else if (entity.hasComponent(Model.class)) {

                Visibility visibility = entity.getComponent(Visibility.class);
                if (visibility != null && visibility.getVisibile() == Visible.VISIBLE) {
                    Group<Entity> shapes = Model.getShapes(entity);
                    canvas.save();
                    for (int j = 0; j < shapes.size(); j++) {
                        drawShape(shapes.get(j), canvas, paint, palette);
                    }
                    canvas.restore();
                }

            }
            // </REFACTOR>
        }
    }

    // <REFACTOR_INTO_ENGINE>
    public static String NOTIFICATION_FONT = "fonts/ProggyClean.ttf";
    public static float NOTIFICATION_FONT_SIZE = 45;
    public static final long DEFAULT_NOTIFICATION_TIMEOUT = 1000;
    public static final float DEFAULT_NOTIFICATION_OFFSET_X = 0;
    public static final float DEFAULT_NOTIFICATION_OFFSET_Y = -50;

    public static int OVERLAY_TOP_MARGIN = 25;
    public static int OVERLAY_LEFT_MARGIN = 25;
    public static int OVERLAY_LINE_SPACING = 10;
    public static String OVERLAY_FONT = "fonts/ProggySquare.ttf";
    public static float OVERLAY_FONT_SIZE = 25;
    public static String OVERLAY_FONT_COLOR = "#ffff0000";

    public static String GEOMETRY_ANNOTATION_FONT = "fonts/ProggySquare.ttf";
    public static float GEOMETRY_ANNOTATION_FONT_SIZE = 35;
    public static String GEOMETRY_ANNOTATION_FONT_COLOR = "#ffff0000";
    public static int GEOMETRY_ANNOTATION_LINE_SPACING = 10;
    // </REFACTOR_INTO_ENGINE>

    private static Typeface overlayTypeface = Typeface.createFromAsset(Application.getInstance().getAssets(), OVERLAY_FONT);
    private static Typeface overlayTypefaceBold = Typeface.create(overlayTypeface, Typeface.NORMAL);

    private static Typeface geometryAnnotationTypeface = Typeface.createFromAsset(Application.getInstance().getAssets(), GEOMETRY_ANNOTATION_FONT);
    private static Typeface geometryAnnotationTypefaceBold = Typeface.create(geometryAnnotationTypeface, Typeface.NORMAL);

    double minFps = Double.MAX_VALUE;
    double maxFps = Double.MIN_VALUE;

    public void drawOverlay(Canvas canvas, Paint paint) {

        // Font
        paint.setTypeface(overlayTypefaceBold);

        int linePosition = 0;

        // <FRAME_COUNT>
        canvas.save();
        paint.setColor(Color.parseColor(OVERLAY_FONT_COLOR));
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(OVERLAY_FONT_SIZE);

        String framesText = "Frames: " + (int) platformRenderClock.frameCount;
        Rect framesTextBounds = new Rect();
        paint.getTextBounds(framesText, 0, framesText.length(), framesTextBounds);
        linePosition += OVERLAY_TOP_MARGIN + framesTextBounds.height();
        canvas.drawText(framesText, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </FRAME_COUNT>

        // <TICK_COUNT>
        canvas.save();
        String tickCountText = "Ticks: " + (int) platformRenderClock.tickCount;
        Rect tickCountTextBounds = new Rect();
        paint.getTextBounds(tickCountText, 0, tickCountText.length(), tickCountTextBounds);
        linePosition += OVERLAY_LINE_SPACING + tickCountTextBounds.height();
        canvas.drawText(tickCountText, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </TICK_COUNT>

        // <FPS_LABEL>
        canvas.save();
        paint.setColor(Color.parseColor(OVERLAY_FONT_COLOR));
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(OVERLAY_FONT_SIZE);

        String fpsText = "FPS: " + (int) minFps + " " + (int) platformRenderClock.getFramesPerSecond() + " " + (int) maxFps;
        Rect fpsTextBounds = new Rect();
        paint.getTextBounds(fpsText, 0, fpsText.length(), fpsTextBounds);
        linePosition += OVERLAY_LINE_SPACING + fpsTextBounds.height();
        canvas.drawText(fpsText, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();

        // <HACK>
        double fps = platformRenderClock.getFramesPerSecond();
        if (fps < minFps) {
            minFps = fps;
        } else if (fps > maxFps) {
            maxFps = fps;
        }
        // </HACK>
        // </FPS_LABEL>

        // <FRAME_TIME>
        canvas.save();
        String frameTimeText = "Frame Time: " + (int) platformRenderClock.frameTimeDelta;
        Rect frameTimeTextBounds = new Rect();
        paint.getTextBounds(frameTimeText, 0, frameTimeText.length(), frameTimeTextBounds);
        linePosition += OVERLAY_LINE_SPACING + frameTimeTextBounds.height();
        canvas.drawText(frameTimeText, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </FRAME_TIME>

        // <FRAME_SLEEP_TIME>
        canvas.save();
        String frameSleepTimeText = "Frame Sleep Time: " + (int) platformRenderClock.currentSleepTime;
        Rect frameSleepTimeTextBounds = new Rect();
        paint.getTextBounds(frameSleepTimeText, 0, frameSleepTimeText.length(), frameSleepTimeTextBounds);
        linePosition += OVERLAY_LINE_SPACING + frameSleepTimeTextBounds.height();
        canvas.drawText(frameSleepTimeText, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </FRAME_SLEEP_TIME>

        // <UPDATE_TIME>
        canvas.save();
        String updateTimeText = "Update Time: " + (int) world.updateTime;
        Rect updateTimeTextBounds = new Rect();
        paint.getTextBounds(updateTimeText, 0, updateTimeText.length(), updateTimeTextBounds);
        linePosition += OVERLAY_LINE_SPACING + updateTimeTextBounds.height();
        canvas.drawText(updateTimeText, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </UPDATE_TIME>

        // <RENDER_TIME>
        canvas.save();
        String renderTimeText = "Render Time: " + (int) world.renderTime;
        Rect renderTimeTextBounds = new Rect();
        paint.getTextBounds(renderTimeText, 0, renderTimeText.length(), renderTimeTextBounds);
        linePosition += OVERLAY_LINE_SPACING + renderTimeTextBounds.height();
        canvas.drawText(renderTimeText, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </RENDER_TIME>

        // <RENDER_TIME>
        canvas.save();
        String filterWithComponentCountText = "Filter Count: " + (int) world.lookupCount;
        Rect filterWithComponentCountTextBounds = new Rect();
        paint.getTextBounds(filterWithComponentCountText, 0, filterWithComponentCountText.length(), filterWithComponentCountTextBounds);
        linePosition += OVERLAY_LINE_SPACING + filterWithComponentCountTextBounds.height();
        canvas.drawText(filterWithComponentCountText, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        world.lookupCount = 0;
        // </RENDER_TIME>

        // <ENTITY_STATISTICS>
        canvas.save();
        int entityCount = world.Manager.getEntities().size();
        int hostCount = world.Manager.getEntities().filterWithComponent(Host.class).size();
        int portCount = world.Manager.getEntities().filterWithComponent(Port.class).size();
        int extensionCount = world.Manager.getEntities().filterWithComponent(Extension.class).size();
        int pathCount = world.Manager.getEntities().filterWithComponent(Path.class).size();
        int cameraCount = world.Manager.getEntities().filterWithComponent(Camera.class).size();

        // Entities
        String text = "Entities: " + entityCount;
        Rect textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(text, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();

        // Hosts
        canvas.save();
        text = "Hosts: " + hostCount;
        textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(text, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();

        // Ports
        canvas.save();
        text = "Ports: " + portCount;
        textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(text, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();

        // Extensions
        canvas.save();
        text = "Extensions: " + extensionCount;
        textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(text, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();

        // Paths
        canvas.save();
        text = "Paths: " + pathCount;
        textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(text, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();

        // Cameras
        canvas.save();
        text = "Cameras: " + cameraCount;
        textBounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), textBounds);
        linePosition += OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(text, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </ENTITY_STATISTICS>

        // <CAMERA_SCALE_MONITOR>
        canvas.save();
        Entity camera = world.Manager.getEntities().filterWithComponent(Camera.class).get(0); // HACK
        String cameraScaleText = "Camera Scale: " + camera.getComponent(Transform.class).scale;
        Rect cameraScaleTextBounds = new Rect();
        paint.getTextBounds(cameraScaleText, 0, cameraScaleText.length(), cameraScaleTextBounds);
        linePosition += OVERLAY_LINE_SPACING + cameraScaleTextBounds.height();
        canvas.drawText(cameraScaleText, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </CAMERA_SCALE_MONITOR>

        // <CAMERA_POSITION_MONITOR>
        canvas.save();
        String cameraPositionText = "Camera Position: " + camera.getComponent(Transform.class).x + ", " + camera.getComponent(Transform.class).y;
        Rect cameraPositionTextBounds = new Rect();
        paint.getTextBounds(cameraPositionText, 0, cameraPositionText.length(), cameraPositionTextBounds);
        linePosition += OVERLAY_LINE_SPACING + cameraPositionTextBounds.height();
        canvas.drawText(cameraPositionText, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </CAMERA_POSITION_MONITOR>

        // <CAMERA_TARGET_POSITION_MONITOR>
        canvas.save();
        String cameraTargetPositionText = "Camera Target: " + camera.getComponent(Physics.class).targetTransform.x + ", " + camera.getComponent(Physics.class).targetTransform.y;
        Rect cameraTargetPositionTextBounds = new Rect();
        paint.getTextBounds(cameraTargetPositionText, 0, cameraTargetPositionText.length(), cameraTargetPositionTextBounds);
        linePosition += OVERLAY_LINE_SPACING + cameraTargetPositionTextBounds.height();
        canvas.drawText(cameraTargetPositionText, OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </CAMERA_TARGET_POSITION_MONITOR>

//        // <BOUNDARY_COUNT>
//        canvas.save();
//        String shapeBoundaryCountText = "Boundary Count: appx. " + Boundary.innerBoundaries.size();
//        Rect shapeBoundaryCountBounds = new Rect();
//        paint.getTextBounds(shapeBoundaryCountText, 0, shapeBoundaryCountText.length(), shapeBoundaryCountBounds);
//        linePosition += OVERLAY_LINE_SPACING + shapeBoundaryCountBounds.height();
//        canvas.drawText(shapeBoundaryCountText, OVERLAY_LEFT_MARGIN, linePosition, paint);
//        canvas.restore();
//        // </BOUNDARY_COUNT>
    }

    public void drawGeometryAnnotations(Group<Entity> entities, Canvas canvas, Paint paint) {

        // <BOUNDARY>
        // TODO: Clean up: Group<Entity> entities2 = World.getWorld().Manager.getEntities().filterActive(true).filterWithComponents(Path.class, Boundary.class).getModels().getShapes();
        // TODO: FIX PATH! Integrate into multi-"skin" (or multi-configuration) model/image.: Group<Entity> entities2 = World.getWorld().Manager.getEntities().filterActive(true).filterWithComponents(Path.class, Boundary.class).getModels().getShapes();
        Group<Entity> entities2 = World.getWorld().Manager.getEntities().filterActive(true).filterWithComponents(Extension.class, Boundary.class).getModels().getShapes();
        paint.setColor(Color.parseColor(GEOMETRY_ANNOTATION_FONT_COLOR));
        paint.setStrokeWidth(2.0f);
        paint.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < entities2.size(); i++) {
            Boundary boundaryComponent = entities2.get(i).getComponent(Boundary.class);

            // Outline
            if (boundaryComponent != null) {
                canvas.save();
                drawPolygon(boundaryComponent.boundary, canvas, paint, null);
                canvas.restore();
            }

            // Vertices
            if (boundaryComponent.boundary != null) {
                for (int j = 0; j < boundaryComponent.boundary.size(); j++) {
                    canvas.save();
                    canvas.drawCircle(
                            (float) boundaryComponent.boundary.get(j).x,
                            (float) boundaryComponent.boundary.get(j).y,
                            5,
                            paint
                    );
                    canvas.restore();
                }
            }
        }
        // </BOUNDARY>

        // <TRANSFORM_POSITION>
        // Style
        paint.setColor(Color.parseColor(GEOMETRY_ANNOTATION_FONT_COLOR));
        paint.setStrokeWidth(1.0f);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(GEOMETRY_ANNOTATION_FONT_SIZE);

        // Font
        paint.setTypeface(geometryAnnotationTypeface);

        float linePosition = 0f;
        String lineText = "";
        Rect textBounds = new Rect();

        for (int i = 0; i < entities.size(); i++) {

            Transform transformComponent = entities.get(i).getComponent(Transform.class);

            linePosition = (float) transformComponent.y;

            canvas.save();

            // <X>
            canvas.save();
            lineText = "x: " + (float) transformComponent.x;
            paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
            canvas.drawText(lineText, (float) transformComponent.x, linePosition, paint);
            linePosition += textBounds.height() + GEOMETRY_ANNOTATION_LINE_SPACING;
            canvas.restore();
            // </X>

            // <Y>
            canvas.save();
            lineText = "y: " + (float) transformComponent.y;
            paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
            canvas.drawText(lineText, (float) transformComponent.x, linePosition, paint);
            linePosition += textBounds.height() + GEOMETRY_ANNOTATION_LINE_SPACING;
            canvas.restore();
            // </Y>

            // <ROTATION>
            canvas.save();
            lineText = "r: " + (float) transformComponent.rotation;
            paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
            canvas.drawText(lineText, (float) transformComponent.x, linePosition, paint);
            linePosition += textBounds.height() + GEOMETRY_ANNOTATION_LINE_SPACING;
            canvas.restore();
            // </ROTATION>

            canvas.restore();
        }
        // </TRANSFORM_POSITION>

        // <CAMERA_BOUNDING_BOX>
        paint.setStrokeWidth(2.0f);
        paint.setStyle(Paint.Style.STROKE);

        Entity camera = World.getWorld().Manager.getEntities().filterWithComponent(Camera.class).get(0);
        if (camera != null) {
            Rectangle boundingBox = camera.getComponent(Camera.class).boundingBox;

            if (boundingBox != null) {
                canvas.save();
                canvas.drawRect(
                        (float) (boundingBox.getPosition().x - boundingBox.getWidth() / 2.0),
                        (float) (boundingBox.getPosition().y + boundingBox.getWidth() / 2.0),
                        (float) (boundingBox.getPosition().x + boundingBox.getWidth() / 2.0),
                        (float) (boundingBox.getPosition().y - boundingBox.getWidth() / 2.0),
                        paint
                );
                canvas.restore();
            }
        }
        // </CAMERA_BOUNDING_BOX>

        // <CAMERA_BOUNDARY>
        paint.setStrokeWidth(1.0f);
        paint.setStyle(Paint.Style.FILL);

        if (camera != null) {
            List<Transform> boundary = camera.getComponent(Camera.class).boundary;

            if (boundary != null) {
                for (int i = 0; i < boundary.size(); i++) {
                    canvas.save();
                    canvas.drawCircle(
                            (float) boundary.get(i).x,
                            (float) boundary.get(i).y,
                            5,
                            paint
                    );
                    canvas.restore();
                }
            }
        }
        // </CAMERA_BOUNDARY>
    }

    public void drawGeometryOverlay(Canvas canvas, Paint paint) {

        // Font
        paint.setTypeface(overlayTypefaceBold);

        // <DISPLAY_SURFACE_AXES>
        canvas.save();
        paint.setColor(Color.parseColor(OVERLAY_FONT_COLOR));
        paint.setStrokeWidth(1.0f);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(OVERLAY_FONT_SIZE);

        // Horizontal Axis
        canvas.drawLine(
                0,
                DeviceDimensionsHelper.getDisplayHeight(Application.getContext()) / 2.0f,
                DeviceDimensionsHelper.getDisplayWidth(Application.getContext()),
                DeviceDimensionsHelper.getDisplayHeight(Application.getContext()) / 2.0f,
                paint
        );

        // Vertical Axis
        canvas.drawLine(
                DeviceDimensionsHelper.getDisplayWidth(Application.getContext()) / 2.0f,
                0,
                DeviceDimensionsHelper.getDisplayWidth(Application.getContext()) / 2.0f,
                DeviceDimensionsHelper.getDisplayHeight(Application.getContext()),
                paint
        );

        canvas.restore();
        // </DISPLAY_SURFACE_AXES>
    }

    // <REFACTOR>
    // TODO: 11/16/2016 Optimize! Big and slow! Should be fast!
    public void drawEditablePath(Entity path, Canvas canvas, Paint paint, Palette palette) {

        Entity sourcePort = Path.getSource(path);
        Entity sourcePortShapeE = Model.getShape(sourcePort, "Port");
        Shape hostSourcePortShape = sourcePortShapeE.getComponent(Primitive.class).shape;

        boolean isSingletonPath = (Path.getTarget(path) == null);

        if (isSingletonPath) {

            // Singleton Path

            Entity sourcePortPathShape = Model.getShape(path, "Source Port");
            Shape sourcePortModel = sourcePortPathShape.getComponent(Primitive.class).shape;

            // Color. Update color of mPort shape based on its type.
            Path.Type pathType = Path.getType(path);
            String pathColor = camp.computer.clay.util.Color.getColor(pathType);
            sourcePortModel.setColor(pathColor);

            // TODO: Create Segment and add it to the PathImage. Update its geometry to change position, rotation, etc.
            Segment segment = (Segment) Model.getShape(path, "Path").getComponent(Primitive.class).shape;
            segment.setOutlineThickness(15.0);
            segment.setOutlineColor(sourcePortModel.getColor());

            // <REFACTOR>
            palette.outlineThickness = World.PATH_OVERVIEW_THICKNESS;
            palette.outlineColor = pathColor;
            // </REFACTOR>

            // Draw shapes in Path
            drawShape(Model.getShape(path, "Path"), canvas, paint, palette); // drawSegment(segment, palette);
            drawShape(sourcePortPathShape, canvas, paint, palette); // drawCircle((Circle) sourcePortShape, palette); // drawShape(sourcePortShape, palette);

            // <DRAW_BOUNDARY>
            // Draw Boundary
            paint.setStrokeWidth(3.0f);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.CYAN);
            drawPolygon(Boundary.get(sourcePortPathShape), canvas, paint, palette);
            // </DRAW_BOUNDARY>

        } else {

            Shape sourcePortShape = Model.getShape(path, "Source Port").getComponent(Primitive.class).shape;
            Shape targetPortShape = Model.getShape(path, "Target Port").getComponent(Primitive.class).shape;

            sourcePortShape.setColor(hostSourcePortShape.getColor());

            // Update color of Port shape based on its type
            Path.Type pathType = Path.getType(path);
            String pathColor = camp.computer.clay.util.Color.getColor(pathType);
            sourcePortShape.setColor(pathColor);
            targetPortShape.setColor(pathColor);

            // TODO: Create Segment and add it to the PathImage. Update its geometry to change position, rotation, etc.

            Segment segment = (Segment) Model.getShape(path, "Path").getComponent(Primitive.class).shape;
//            segment.setOutlineThickness(World.PATH_EDITVIEW_THICKNESS);
//            segment.setOutlineColor(sourcePortShape.getColor());
            palette.outlineColor = sourcePortShape.getColor();
            palette.outlineThickness = World.PATH_EDITVIEW_THICKNESS;

            // Draw shapes in Path
            drawSegment(Model.getShape(path, "Source Port").getComponent(Transform.class), Model.getShape(path, "Target Port").getComponent(Transform.class), canvas, paint, palette);
            drawShape(Model.getShape(path, "Source Port"), canvas, paint, palette);
            drawShape(Model.getShape(path, "Target Port"), canvas, paint, palette);

            // <DRAW_BOUNDARY>
            paint.setStrokeWidth(3.0f);
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.CYAN);
            drawPolygon(Boundary.get(Model.getShape(path, "Source Port")), canvas, paint, palette);
            drawPolygon(Boundary.get(Model.getShape(path, "Target Port")), canvas, paint, palette);
            // </DRAW_BOUNDARY>

        }
    }

    public void drawOverviewPath(Entity path, Canvas canvas, Paint paint, Palette palette) {

        // Get Host and Extension Ports
        Entity hostPort = Path.getSource(path);
        Entity extensionPort = Path.getTarget(path);

        boolean isSingletonPath = (Path.getTarget(path) == null);

        if (isSingletonPath) {

            // TODO: Singleton Path

        } else {

            // Draw the connection between the Host's Port and the Extension's Port
            Entity host = hostPort.getParent();
            Entity extension = extensionPort.getParent();

            if (host.getComponent(Portable.class).headerContactGeometries.size() > Port.getIndex(hostPort)
                    && extension.getComponent(Portable.class).headerContactGeometries.size() > Port.getIndex(extensionPort)) {

                // Draw connection between Ports
                // <REFACTOR>
                // TODO: Cache the integer color code.
                paint.setColor(android.graphics.Color.parseColor(camp.computer.clay.util.Color.getColor(Port.getType(extensionPort))));
                // </REFACTOR>
                paint.setStrokeWidth(10.0f);

                // TODO: Create Segment and add it to the PathImage. Update its geometry to change position, rotation, etc.
                Entity shapeEntity = Model.getShape(path, "Path");
                Segment segment = (Segment) shapeEntity.getComponent(Primitive.class).shape;
                segment.setOutlineThickness(10.0);
                segment.setOutlineColor(camp.computer.clay.util.Color.getColor(Port.getType(extensionPort)));

                palette.outlineThickness = 10.0;
                palette.outlineColor = camp.computer.clay.util.Color.getColor(Port.getType(extensionPort));

                drawShape(shapeEntity, canvas, paint, palette);
            }
        }
    }
    // </REFACTOR>

    public void drawShape(Entity shape, Canvas canvas, Paint paint, Palette palette) {

        // <HACK>
//        shape.getComponent(Primitive.class).shape.setPosition(
//        );
//                shape.getComponent(Transform.class)
//        shape.getComponent(Primitive.class).shape.setRotation(
//                shape.getComponent(Transform.class).getRotation()
//        );

        Shape shapeMesh = shape.getComponent(Primitive.class).shape;

        // Palette
//        Palette palette = new Palette();
        palette.color = shapeMesh.getColor();
        palette.outlineColor = shapeMesh.getOutlineColor();
        palette.outlineThickness = shapeMesh.outlineThickness;

        // TODO: drawShape(shape, palette);
        if (shapeMesh.getClass() == Point.class) {
            // TODO:
        } else if (shapeMesh.getClass() == Segment.class) {
            Segment segment = (Segment) shapeMesh;
            drawSegment(segment.getSource(), segment.getTarget(), canvas, paint, palette);
        } else if (shapeMesh.getClass() == Polyline.class) {
            // TODO: drawPolyline((Polyline) shape, palette);
        } else if (shapeMesh.getClass() == Triangle.class) {
            // TODO: drawTriangle((Triangle) shape);
        } else if (shapeMesh.getClass() == Rectangle.class) {
            Rectangle rectangle = (Rectangle) shapeMesh;
            drawRectangle(shape.getComponent(Transform.class), rectangle.width, rectangle.height, rectangle.cornerRadius, canvas, paint, palette);
        } else if (shapeMesh.getClass() == Polygon.class) {
            Polygon polygon = (Polygon) shapeMesh;
            drawPolygon(polygon.getVertices(), canvas, paint, palette);
        } else if (shapeMesh.getClass() == Circle.class) {
            Circle circle = (Circle) shapeMesh;
            drawCircle(shape.getComponent(Transform.class), circle.radius, canvas, paint, palette);
        } else if (shapeMesh.getClass() == Text.class) {
            Text text = (Text) shapeMesh;
            drawText(shape.getComponent(Transform.class), text.getText(), text.size, canvas, paint, palette);
        }
        // </HACK>
    }
    // </TODO: REFACTOR>

    public void drawSegment(Transform source, Transform target, Canvas canvas, Paint paint, Palette palette) {

        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor(palette.outlineColor));
        paint.setStrokeWidth((float) palette.outlineThickness);

        canvas.drawLine((float) source.x, (float) source.y, (float) target.x, (float) target.y, paint);
    }

    // TODO: Refactor with transforms
    public void drawPolyline(List<Transform> vertices, Canvas canvas, Paint paint, Palette palette) {

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

    public void drawRectangle(Transform transform, double width, double height, double cornerRadius, Canvas canvas, Paint paint, Palette palette) {

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
                    paint
            );
        }

        canvas.restore();
    }

    public void drawCircle(Transform transform, double radius, Canvas canvas, Paint paint, Palette palette) {

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

    // TODO: Refactor with transforms
    public void drawPolygon(List<Transform> vertices, Canvas canvas, Paint paint, Palette palette) {

        // <HACK>
        if (vertices == null || vertices.size() < 1) {
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

        canvas.drawPath(path, paint);
    }

    public void drawText(Transform position, String text, double size, Canvas canvas, Paint paint, Palette palette) {

        canvas.save();
        canvas.translate((float) position.x, (float) position.y);
        canvas.rotate((float) position.rotation);

        // Style
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize((float) size);

        // Font
//        Typeface overlayTypeface = Typeface.createFromAsset(Application.getInstance().getAssets(), text.font);
//        Typeface overlayTypefaceBold = Typeface.create(overlayTypeface, Typeface.NORMAL);
//        paint.setTypeface(overlayTypefaceBold);

        // Style (Guaranteed)
        String printText = text.toUpperCase();
        paint.setStyle(Paint.Style.FILL);

        // Draw
        Rect textBounds = new Rect();
        paint.getTextBounds(printText, 0, printText.length(), textBounds);
        canvas.drawText(printText, (float) 0 - textBounds.width() / 2.0f, (float) 0 + textBounds.height() / 2.0f, paint);

        canvas.restore();
    }
}
