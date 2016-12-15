package camp.computer.clay.platform.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import camp.computer.clay.engine.Clock;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Primitive;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.util.FilterStrategy;
import camp.computer.clay.engine.component.util.SorterStrategy;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Group;

/**
 * Renderer is a background thread that periodically updates the world state
 * and renders it. By default, the renderer targets frames per second, each time advancing the
 * world's state then re-rendering it.
 */
public class Renderer extends Thread {

    // <SETTINGS>
    public static int DEFAULT_TARGET_FPS = 30;
    public static int DEFAULT_FRAME_SLEEP_TIME = 10;

    private int targetFPS = DEFAULT_TARGET_FPS;
    // </SETTINGS>

    // <STATISTICS>
    private double currentFPS = 0;
    public double frameTimeDelta = 0;
    public double currentSleepTime = 0;
    // </STATISTICS>

    private RenderSurface renderSurface;

    private boolean isRunning = false;
    public long frameCount = 0;
    public long tickCount = 0; // TODO: Move tickCount into Engine

    public double dt = Clock.getTime(Clock.Unit.MILLISECONDS);

    Group<Entity> entities;
    Group<Entity> cameraEntities;

    Renderer(RenderSurface renderSurface) {
        super();
        this.renderSurface = renderSurface;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    @Override
    public void run() {

        // <REFACTOR>
        if (entities == null && World.getInstance() != null) {
            entities = World.getInstance().entityManager.subscribe(new FilterStrategy(Group.Filters.filterWithComponents, Primitive.class), new SorterStrategy(Group.Sorters.layerSorter));
            cameraEntities = World.getInstance().entityManager.subscribe(new FilterStrategy(Group.Filters.filterWithComponents, Camera.class));
        }

        if (entities == null) {
            return;
        }
        // </REFACTOR>

        long targetFramePeriod = 1000 / targetFPS; // Period in milliseconds
        long frameStartTime = 0;
        long frameStopTime;
        long frameSleepTime;
        long sleepStartTime = 0;

        // <REFACTOR>
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Palette palette = new Palette();
        // </REFACTOR>

        while (isRunning) {

            if (frameCount > 0) {
                tickCount++;
            }

            // We need to make sure that the surface is ready
            SurfaceHolder holder = renderSurface.getSurfaceHolder();
            if (!holder.getSurface().isValid()) {
                continue;
            }

            dt = Clock.getTime(Clock.Unit.MILLISECONDS) - frameStartTime;
            frameStartTime = Clock.getTime(Clock.Unit.MILLISECONDS);

//            platformRenderSurface.world.update();

            Canvas canvas = holder.lockCanvas();

            try {
                if (canvas != null) {
                    synchronized (holder) {

                        frameCount++;

                        // TODO: Flatten the call hierarchy invoked in the renderer.

//                        platformRenderSurface.world.update();

                        canvas.save();

                        // <CAMERA_VIEWPORT>
                        Transform cameraTransform = cameraEntities.get(0).getComponent(Transform.class);
                        canvas.translate(
                                (float) (renderSurface.originTransform.x + cameraTransform.x) /* + (float) Application.getPlatform().getOrientationInput().getRotationY()*/,
                                (float) (renderSurface.originTransform.y + cameraTransform.y) /* - (float) Application.getPlatform().getOrientationInput().getRotationX() */
                        );

                        canvas.scale((float) cameraTransform.scale, (float) cameraTransform.scale);
                        // </CAMERA_VIEWPORT>

                        // <CLEAR_CANVAS>
                        canvas.drawColor(Color.WHITE);
                        // </CLEAR_CANVAS>

                        // <UPDATE>
                        // TODO: Draw Renderables.
                        // TODO: This call is expensive. Make it way faster. Cache_OLD? Sublist?
                        renderSurface.drawRenderables(entities, canvas, paint, palette);
                        // </UPDATE>

                        if (World.ENABLE_GEOMETRY_ANNOTATIONS) {
                            renderSurface.drawGeometryAnnotations(entities, canvas, paint);
                        }

                        canvas.restore();

                        // <OVERLAY>
                        if (World.ENABLE_OVERLAY) {
                            renderSurface.drawOverlay(canvas, paint);
                        }

                        if (World.ENABLE_GEOMETRY_OVERLAY) {
                            renderSurface.drawGeometryOverlay(canvas, paint);
                        }
                        // </OVERLAY>

                    }
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }

            // Store actual frames per second
            frameStopTime = Clock.getTime(Clock.Unit.MILLISECONDS);
            currentFPS = (1000.0f / (float) (frameStopTime - frameStartTime));
            frameTimeDelta = frameStopTime - frameStartTime;

            // Sleep the thread until the time remaining in the frame's allocated draw time expires.
            // This reduces energy consumption thereby increasing battery life.
            frameSleepTime = targetFramePeriod - (frameStopTime - frameStartTime);
            try {
                sleepStartTime = Clock.getTime(Clock.Unit.MILLISECONDS);
                if (frameSleepTime > 0) {
                    Thread.sleep(frameSleepTime);
                } else {
                    Thread.sleep(DEFAULT_FRAME_SLEEP_TIME);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            currentSleepTime = Clock.getTime(Clock.Unit.MILLISECONDS) - sleepStartTime;
        }
    }

    public double getFramesPerSecond() {
        return currentFPS;
    }
}
