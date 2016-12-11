package camp.computer.clay.platform.graphics;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.text.DecimalFormat;
import java.util.List;

import camp.computer.clay.engine.Engine;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Boundary;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Model;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Physics;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Primitive;
import camp.computer.clay.engine.component.Structure;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.Visibility;
import camp.computer.clay.engine.component.util.FilterStrategy;
import camp.computer.clay.engine.component.util.Signal;
import camp.computer.clay.engine.component.util.Visible;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.event.Event;
import camp.computer.clay.engine.manager.Group;
import camp.computer.clay.engine.system.InputSystem;
import camp.computer.clay.lib.Geometry.Circle;
import camp.computer.clay.lib.Geometry.Polygon;
import camp.computer.clay.lib.Geometry.Rectangle;
import camp.computer.clay.lib.Geometry.Segment;
import camp.computer.clay.lib.Geometry.Shape;
import camp.computer.clay.lib.Geometry.Text;
import camp.computer.clay.platform.Application;
import camp.computer.clay.platform.util.DeviceDimensionsHelper;

public class RenderSurface extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;

    public Renderer renderer;

    public Transform originTransform = new Transform();

    // <REFACTOR>
    // TODO: Remove world from platform layer.
    public World world;
    // </REFACTOR>

    public RenderSurface(Context context) {
        super(context);
        setFocusable(true);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(layoutParams);
    }

    public RenderSurface(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RenderSurface(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        // Create a bitmap to use as a drawing buffer equal in size to the full size of the Surface
        Bitmap canvasBitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas();
        canvas.setBitmap(canvasBitmap);

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
        // renderer.setRunning (false);
        while (retry) {
            try {
                renderer.join ();
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
        renderer = new Renderer(this);
        renderer.setRunning(true);
        renderer.start();

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

    public SurfaceHolder getSurfaceHolder() {
        return surfaceHolder;
    }

    /*
    // NOTES:
    // - Motion eventManager contain information about all of the pointers that are currently active
    //   even if some of them have not moved since the getLastEvent event was delivered.
    //
    // - The index of pointers only ever changes by one as individual pointers go up and down,
    //   except when the gesture is canceled.
    //
    // - Use the getPointerId(int) method to obtain the pointer id of a pointer to track it
    //   across all subsequent motion eventManager in a gesture. Then for successive motion eventManager,
    //   use the findPointerIndex(int) method to obtain the pointer index for a given pointer
    //   id in that motion event.
    //
    // DOCUMENTATION:

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
    // - https://developer.android.com/training/gestures/scale.html
    */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {

        if (this.world == null) {
            return false;
        }

        int pointerIndex = ((motionEvent.getAction() & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT);
        int pointerId = motionEvent.getPointerId(pointerIndex);
        int touchInteractionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);
        final int pointerCount = motionEvent.getPointerCount();

        // Get active inputSystem
        InputSystem inputSystem = world.getSystem(InputSystem.class);

        // Create pointerCoordinates event
        Event event = new Event("NONE");

        if (pointerCount <= Event.MAXIMUM_POINT_COUNT) {
            if (pointerIndex <= Event.MAXIMUM_POINT_COUNT - 1) {

                // Current
                // Update pointerCoordinates state based the pointerCoordinates given by the host OS (e.g., Android).
                for (int i = 0; i < pointerCount; i++) {
                    int id = motionEvent.getPointerId(i);
                    event.surfaceCoordinates[id].x = motionEvent.getX(i);
                    event.surfaceCoordinates[id].y = motionEvent.getY(i);
                }

                // Update the state of the touched object based on the current pointerCoordinates event state.
                if (touchInteractionType == MotionEvent.ACTION_DOWN) {

                    holdEventTimerHandler.removeCallbacks(holdEventTimerRunnable);
                    holdEventTimerHandler.postDelayed(holdEventTimerRunnable, Event.MINIMUM_HOLD_DURATION);

                    event.setType("SELECT");
                    event.pointerIndex = pointerId;
                    inputSystem.queue(event);

                } else if (touchInteractionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO: Handle additional pointers after the getFirstEvent pointerCoordinates!
                } else if (touchInteractionType == MotionEvent.ACTION_MOVE) {

                    event.setType("MOVE");
                    event.pointerIndex = pointerId;

                    inputSystem.queue(event);

                } else if (touchInteractionType == MotionEvent.ACTION_UP) {

                    holdEventTimerHandler.removeCallbacks(holdEventTimerRunnable);

                    event.setType("UNSELECT");
                    event.pointerIndex = pointerId;
                    inputSystem.queue(event);

                } else if (touchInteractionType == MotionEvent.ACTION_POINTER_UP) {
                    // TODO: Handle additional pointers after the getFirstEvent pointerCoordinates!
                } else if (touchInteractionType == MotionEvent.ACTION_CANCEL) {
                    // TODO:
                }
            }
        }

        return true;
    }

    // <PLATFORM_SCHEDULED_EVENT>
    private Handler holdEventTimerHandler = new Handler();

    private Runnable holdEventTimerRunnable = new Runnable() {
        @Override
        public void run() {

            /*
            if (previousEvent.getDistance() < Event.MINIMUM_MOVE_DISTANCE) {
                InputSystem inputSystem = world.getSystem(InputSystem.class);

                Event event = new Event();
                // event.pointerCoordinates[id].x = (motionEvent.getX(i) - (originTransform.x + perspectivePosition.x)) / perspectiveScale;
                // event.pointerCoordinates[id].y = (motionEvent.getY(i) - (originTransform.y + perspectivePosition.y)) / perspectiveScale;
                event.setType(Event.Type.HOLD);
                event.pointerIndex = 0; // HACK // TODO: event.pointerIndex = pointerId;

//                // Set previous Event
//                if (previousEvent != null) {
//                    event.setPreviousEvent(previousEvent);
//                } else {
//                    event.setPreviousEvent(null);
//                }
//                previousEvent = event;

                inputSystem.enqueue(event);

                Log.v("PlatformRenderSurface", "event.getDistance: " + event.getDistance());
            }

                }
            }
            */
        }
    };
    // </PLATFORM_SCHEDULED_EVENT>

    // <TODO: REMOVE_REFERENCE_TO_WORLD>
    public void setWorld(World world) {
        this.world = world;
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
                    if (entity.getComponent(Model.class).meshIndex == 1) {
                        drawEditablePath(entity, canvas, paint, palette);
                    } else if (entity.getComponent(Model.class).meshIndex == 0) {
                        if (Path.getMode(entity) == Signal.Mode.ELECTRONIC) {
                            drawOverviewPath(entity, canvas, paint, palette);
                        }
                    }
                }
                // </REFACTOR>

            }
            // <REFACTOR>
            // This was added so Prototype Extension/Path would draw without Extension/Path components
            else if (entity.hasComponent(Model.class)) {

                // Draw a gray line from the Host's start position and the destination, under it.

                Visibility visibility = entity.getComponent(Visibility.class);
                if (visibility != null && visibility.getVisibile() == Visible.VISIBLE) {
                    Group<Entity> shapes = Model.getPrimitives(entity);
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

    private static Typeface overlayTypeface = Typeface.createFromAsset(Application.getInstance().getAssets(), World.OVERLAY_FONT);
    private static Typeface overlayTypefaceBold = Typeface.create(overlayTypeface, Typeface.NORMAL);

    private static Typeface geometryAnnotationTypeface = Typeface.createFromAsset(Application.getInstance().getAssets(), World.GEOMETRY_ANNOTATION_FONT);
    private static Typeface geometryAnnotationTypefaceBold = Typeface.create(geometryAnnotationTypeface, Typeface.NORMAL);

    double minFps = Double.MAX_VALUE;
    double maxFps = Double.MIN_VALUE;

    private DecimalFormat decimalFormat = new DecimalFormat("#.##");

    Group<Entity> entities;
    Group<Entity> hostEntities;
    Group<Entity> portEntities;
    Group<Entity> extensionEntities;
    Group<Entity> pathEntities;
    Group<Entity> primitiveEntities;
    Group<Entity> cameraEntities;

    Group<Entity> boundaryEntities;

    boolean isValid = false;

    public void drawOverlay(Canvas canvas, Paint paint) {

        if (!isValid) {
            this.entities = world.entityManager.subscribe(null, null);
            hostEntities = world.entityManager.subscribe(new FilterStrategy(Group.Filters.filterWithComponents, Host.class), null);
            portEntities = world.entityManager.subscribe(new FilterStrategy(Group.Filters.filterWithComponents, Port.class), null);
            extensionEntities = world.entityManager.subscribe(new FilterStrategy(Group.Filters.filterWithComponents, Extension.class), null);
            pathEntities = world.entityManager.subscribe(new FilterStrategy(Group.Filters.filterWithComponents, Path.class), null);
            primitiveEntities = world.entityManager.subscribe(new FilterStrategy(Group.Filters.filterWithComponents, Primitive.class), null);
            cameraEntities = world.entityManager.subscribe(new FilterStrategy(Group.Filters.filterWithComponents, Camera.class), null);

            boundaryEntities = world.entityManager.subscribe(new FilterStrategy(Group.Filters.filterWithComponents, Extension.class, Boundary.class), null);

            isValid = true;
        }

        // <REFACTOR>
        // TODO: Move to Engine/World
        long engineTickCount = Engine.getInstance().world.tickCount;
        int entityCount = entities.size();
        int hostCount = hostEntities.size();
        int portCount = portEntities.size();
        int extensionCount = extensionEntities.size();
        int pathCount = pathEntities.size();
        int primitiveCount = primitiveEntities.size();
        int cameraCount = cameraEntities.size();

        int worldUpdateTime = (int) world.updateTime;
        int worldRenderTime = (int) world.renderTime;
        int worldLookupCount = (int) world.lookupCount;
        world.lookupCount = 0;
        Entity camera = cameraEntities.get(0); // HACK
        // </REFACTOR>

        // Font
        paint.setColor(Color.parseColor(World.OVERLAY_FONT_COLOR));
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(World.OVERLAY_FONT_SIZE);
        paint.setTypeface(overlayTypefaceBold);

        float linePosition = 0;
        String lineText = "";
        Rect textBounds = new Rect();

        // <TICK_COUNT>
        canvas.save();
        lineText = "Ticks: " + (int) engineTickCount;
        paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
        linePosition += World.OVERLAY_TOP_MARGIN;
        canvas.drawText(lineText, World.OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </TICK_COUNT>

        // <FRAME_COUNT>
        canvas.save();
        lineText = "Frames: " + (int) renderer.frameCount;
        paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
        linePosition += World.OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(lineText, World.OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </FRAME_COUNT>

        // <FPS_LABEL>
        canvas.save();
        lineText = "FPS: " + (int) minFps + " " + (int) renderer.getFramesPerSecond() + " " + (int) maxFps;
        paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
        linePosition += World.OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(lineText, World.OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();

        // <HACK>
        double fps = renderer.getFramesPerSecond();
        if (fps < minFps) {
            minFps = fps;
        } else if (fps > maxFps) {
            maxFps = fps;
        }
        // </HACK>
        // </FPS_LABEL>

        // <FRAME_TIME>
        canvas.save();
        lineText = "Frame Time: " + (int) renderer.frameTimeDelta;
        paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
        linePosition += World.OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(lineText, World.OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </FRAME_TIME>

        // <FRAME_SLEEP_TIME>
        canvas.save();
        lineText = "Frame Sleep Time: " + (int) renderer.currentSleepTime;
        paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
        linePosition += World.OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(lineText, World.OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </FRAME_SLEEP_TIME>

        // <UPDATE_TIME>
        canvas.save();
        lineText = "Update Time: " + (int) worldUpdateTime;
        paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
        linePosition += World.OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(lineText, World.OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </UPDATE_TIME>

        // <RENDER_TIME>
        canvas.save();
        lineText = "Render Time: " + worldRenderTime;
        paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
        linePosition += World.OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(lineText, World.OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </RENDER_TIME>

        // <RENDER_TIME>
        canvas.save();
        lineText = "Filter Count: " + worldLookupCount;
        paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
        linePosition += World.OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(lineText, World.OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </RENDER_TIME>

        // <ENTITY_STATISTICS>
        // Entities
        canvas.save();
        lineText = "Entities: " + entityCount;
        paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
        linePosition += World.OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(lineText, World.OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();

        // Hosts
        canvas.save();
        lineText = "Hosts: " + hostCount;
        paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
        linePosition += World.OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(lineText, World.OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();

        // Ports
        canvas.save();
        lineText = "Ports: " + portCount;
        paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
        linePosition += World.OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(lineText, World.OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();

        // Extensions
        canvas.save();
        lineText = "Extensions: " + extensionCount;
        paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
        linePosition += World.OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(lineText, World.OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();

        // Paths
        canvas.save();
        lineText = "Paths: " + pathCount;
        paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
        linePosition += World.OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(lineText, World.OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();

        // Primitives
        canvas.save();
        lineText = "Primitives: " + primitiveCount;
        paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
        linePosition += World.OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(lineText, World.OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();

        // Cameras
        canvas.save();
        lineText = "Cameras: " + cameraCount;
        paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
        linePosition += World.OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(lineText, World.OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </ENTITY_STATISTICS>

        // <CAMERA_SCALE_MONITOR>
        canvas.save();
        lineText = "Camera Scale: " + decimalFormat.format(camera.getComponent(Transform.class).scale);
        paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
        linePosition += World.OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(lineText, World.OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </CAMERA_SCALE_MONITOR>

        // <CAMERA_POSITION_MONITOR>
        canvas.save();
        lineText = "Camera Position: " + decimalFormat.format(camera.getComponent(Transform.class).x) + ", " + decimalFormat.format(camera.getComponent(Transform.class).y);
        paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
        linePosition += World.OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(lineText, World.OVERLAY_LEFT_MARGIN, linePosition, paint);
        canvas.restore();
        // </CAMERA_POSITION_MONITOR>

        // <CAMERA_TARGET_POSITION_MONITOR>
        canvas.save();
        lineText = "Camera Target: " + decimalFormat.format(camera.getComponent(Physics.class).targetTransform.x) + ", " + decimalFormat.format(camera.getComponent(Physics.class).targetTransform.y);
        paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
        linePosition += World.OVERLAY_LINE_SPACING + textBounds.height();
        canvas.drawText(lineText, World.OVERLAY_LEFT_MARGIN, linePosition, paint);
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

        if (!isValid) {
            this.entities = world.entityManager.subscribe(null, null);
            hostEntities = world.entityManager.subscribe(new FilterStrategy(Group.Filters.filterWithComponents, Host.class), null);
            portEntities = world.entityManager.subscribe(new FilterStrategy(Group.Filters.filterWithComponents, Port.class), null);
            extensionEntities = world.entityManager.subscribe(new FilterStrategy(Group.Filters.filterWithComponents, Extension.class), null);
            pathEntities = world.entityManager.subscribe(new FilterStrategy(Group.Filters.filterWithComponents, Path.class), null);
            cameraEntities = world.entityManager.subscribe(new FilterStrategy(Group.Filters.filterWithComponents, Camera.class), null);

            boundaryEntities = world.entityManager.subscribe(new FilterStrategy(Group.Filters.filterWithComponents, Extension.class, Boundary.class), null);

            isValid = true;
        }

        // <BOUNDARY>
        // TODO: Clean up: Group<Entity> entities2 = World.getInstance().entityManager.get().filterActive(true).filterWithComponents(Path.class, Boundary.class).getModels().getPrimitives();
        // TODO: FIX PATH! Integrate into multi-"skin" (or multi-configuration) model/image.: Group<Entity> entities2 = World.getInstance().entityManager.get().filterActive(true).filterWithComponents(Path.class, Boundary.class).getModels().getPrimitives();
        //Group<Entity> boundaryEntities = World.getInstance().entityManager.get().filterActive(true).filterWithComponents(Extension.class, Boundary.class).getModels().getPrimitives();
        paint.setColor(Color.parseColor(World.GEOMETRY_ANNOTATION_FONT_COLOR));
        paint.setStrokeWidth(2.0f);
        paint.setStyle(Paint.Style.STROKE);

        for (int i = 0; i < boundaryEntities.size(); i++) {
            Boundary boundaryComponent = boundaryEntities.get(i).getComponent(Boundary.class);

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
        if (World.ENABLE_ANNOTATION_ENTITY_TRANSFORM) {
            // Style
            paint.setColor(Color.parseColor(World.GEOMETRY_ANNOTATION_FONT_COLOR));
            paint.setStrokeWidth(1.0f);
            paint.setStyle(Paint.Style.FILL);
            paint.setTextSize(World.GEOMETRY_ANNOTATION_FONT_SIZE);

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
                linePosition += textBounds.height() + World.GEOMETRY_ANNOTATION_LINE_SPACING;
                canvas.restore();
                // </X>

                // <Y>
                canvas.save();
                lineText = "y: " + (float) transformComponent.y;
                paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
                canvas.drawText(lineText, (float) transformComponent.x, linePosition, paint);
                linePosition += textBounds.height() + World.GEOMETRY_ANNOTATION_LINE_SPACING;
                canvas.restore();
                // </Y>

                // <ROTATION>
                canvas.save();
                lineText = "r: " + (float) transformComponent.rotation;
                paint.getTextBounds(lineText, 0, lineText.length(), textBounds);
                canvas.drawText(lineText, (float) transformComponent.x, linePosition, paint);
                linePosition += textBounds.height() + World.GEOMETRY_ANNOTATION_LINE_SPACING;
                canvas.restore();
                // </ROTATION>

                canvas.restore();
            }
        }
        // </TRANSFORM_POSITION>

        // <CAMERA_BOUNDING_BOX>
        paint.setStrokeWidth(2.0f);
        paint.setStyle(Paint.Style.STROKE);

        Entity camera = cameraEntities.get(0);
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
        paint.setColor(Color.parseColor(World.OVERLAY_FONT_COLOR));
        paint.setStrokeWidth(1.0f);
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(World.OVERLAY_FONT_SIZE);

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

        Entity sourcePort = Path.getSourcePort(path);
        Entity sourcePortShapeE = Model.getPrimitive(sourcePort, "Port");
        Shape hostSourcePortShape = sourcePortShapeE.getComponent(Primitive.class).shape;

        boolean isSingletonPath = (Path.getTargetPort(path) == null);

        if (isSingletonPath) {

            // Singleton Path

            Entity sourcePortPathShape = Model.getPrimitive(path, "Source Port");
            Shape sourcePortShape = sourcePortPathShape.getComponent(Primitive.class).shape;

            // Color. Update color of mPort shape based on its type.
            Signal.Type pathType = Path.getType(path);
            String pathColor = camp.computer.clay.util.Color.getColor(pathType);
            sourcePortShape.setColor(pathColor);

            // TODO: Create Segment and add it to the PathImage. Update its geometry to change position, rotation, etc.
            Segment pathShape = (Segment) Model.getPrimitive(path, "Path").getComponent(Primitive.class).shape;
            pathShape.setOutlineThickness(15.0);
            pathShape.setOutlineColor(sourcePortShape.getColor());

            // <REFACTOR>
            palette.outlineThickness = World.PATH_OVERVIEW_THICKNESS;
            palette.outlineColor = pathColor;
            // </REFACTOR>

            // Draw primitives in Path
            drawShape(Model.getPrimitive(path, "Path"), canvas, paint, palette); // drawSegment(segment, palette);
            drawShape(sourcePortPathShape, canvas, paint, palette); // drawCircle((Circle) sourcePortShape, palette); // drawShape(sourcePortShape, palette);

        } else {

            Shape sourcePortShape = Model.getPrimitive(path, "Source Port").getComponent(Primitive.class).shape;
            Shape targetPortShape = Model.getPrimitive(path, "Target Port").getComponent(Primitive.class).shape;

            sourcePortShape.setColor(hostSourcePortShape.getColor());

            // Update color of Port shape based on its type
            Signal.Type pathType = Path.getType(path);
            String pathColor = camp.computer.clay.util.Color.getColor(pathType);
            sourcePortShape.setColor(pathColor);
            targetPortShape.setColor(pathColor);

            // TODO: Create Segment and add it to the PathImage. Update its geometry to change position, rotation, etc.

            palette.outlineColor = sourcePortShape.getColor();
            palette.outlineThickness = World.PATH_EDITVIEW_THICKNESS;

            // Draw primitives in Path
            drawSegment(Model.getPrimitive(path, "Source Port").getComponent(Transform.class), Model.getPrimitive(path, "Target Port").getComponent(Transform.class), canvas, paint, palette);
            drawShape(Model.getPrimitive(path, "Source Port"), canvas, paint, palette);
            drawShape(Model.getPrimitive(path, "Target Port"), canvas, paint, palette);
        }
    }

    public void drawOverviewPath(Entity path, Canvas canvas, Paint paint, Palette palette) {

        // Get Host and Extension Ports
        Entity hostPort = Path.getSourcePort(path);
        Entity extensionPort = Path.getTargetPort(path);

        boolean isSingletonPath = (Path.getTargetPort(path) == null);

        if (isSingletonPath) {

            // TODO: Singleton Path

        } else {

            // Draw the connection between the Host's Port and the Extension's Port
            Entity host = hostPort.getComponent(Structure.class).parentEntity;
            Entity extension = extensionPort.getComponent(Structure.class).parentEntity;

            Group<Entity> pinContactPoints = Model.getPrimitives(host, "^Pin (1[0-2]|[1-9])$");
            Group<Entity> extensionPinContactPoints = Model.getPrimitives(extension, "^Pin (1[0-2]|[1-9])$");

            if (pinContactPoints.size() > Port.getIndex(hostPort)
                    && extensionPinContactPoints.size() > Port.getIndex(extensionPort)) {

                // Draw connection between Ports
                // <REFACTOR>
                // TODO: Cache_OLD the integer color code.
                paint.setColor(android.graphics.Color.parseColor(camp.computer.clay.util.Color.getColor(Port.getType(extensionPort))));
                // </REFACTOR>
                paint.setStrokeWidth(10.0f);

                // TODO: Create Segment and add it to the PathImage. Update its geometry to change position, rotation, etc.
                Entity shapeEntity = Model.getPrimitive(path, "Path");
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

    public void drawShape(Entity primitive, Canvas canvas, Paint paint, Palette palette) {

        // <HACK>
        Shape shape = primitive.getComponent(Primitive.class).shape;

        // Palette
        palette.color = shape.getColor();
        palette.outlineColor = shape.getOutlineColor();
        palette.outlineThickness = shape.outlineThickness;

        // TODO: drawShape(shape, palette);
        if (shape.getClass() == Segment.class) {
            Segment segment = (Segment) shape;
            drawSegment(segment.getSource(), segment.getTarget(), canvas, paint, palette);
        } else if (shape.getClass() == Rectangle.class) {
            Rectangle rectangle = (Rectangle) shape;
            drawRectangle(primitive.getComponent(Transform.class), rectangle.width, rectangle.height, rectangle.cornerRadius, canvas, paint, palette);
        } else if (shape.getClass() == Polygon.class) {
            Polygon polygon = (Polygon) shape;
            drawPolygon(polygon.getVertices(), canvas, paint, palette);
        } else if (shape.getClass() == Circle.class) {
            Circle circle = (Circle) shape;
            drawCircle(primitive.getComponent(Transform.class), circle.radius, canvas, paint, palette);
        } else if (shape.getClass() == Text.class) {
            Text text = (Text) shape;
            drawText(primitive.getComponent(Transform.class), text.getText(), text.size, canvas, paint, palette);
        }

        // TODO: drawPolyline
        // TODO: drawTriangle
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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

        // Draw Points in Shape
        if (palette.outlineThickness > 0) {

            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(Color.parseColor(palette.outlineColor));
            paint.setStrokeWidth((float) palette.outlineThickness);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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

        /*
        // Font
        Typeface overlayTypeface = Typeface.createFromAsset(Application.getInstance().getAssets(), text.font);
        Typeface overlayTypefaceBold = Typeface.create(overlayTypeface, Typeface.NORMAL);
        paint.setTypeface(overlayTypefaceBold);
        */

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
