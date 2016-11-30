package camp.computer.clay.platform.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Group;
import camp.computer.clay.util.time.Clock;

/**
 * PlatformRenderClock is a background thread that periodically updates the world state
 * and renders it. By default, the renderer targets frames per second, each time advancing the
 * world's state then re-rendering it.
 */
public class PlatformRenderClock extends Thread {

    // <SETTINGS>
    public static int DEFAULT_TARGET_FPS = 30;
    public static int DEFAULT_FRAME_SLEEP_TIME = 10;

    private int targetFPS = DEFAULT_TARGET_FPS;
    // </SETTINGS>

    // <STATISTICS>
    private double currentFPS = 0;
    public double currentFrameTime = 0;
    public double currentSleepTime = 0;
    // </STATISTICS>

    private PlatformRenderSurface platformRenderSurface;

    private boolean isRunning = false;
    public long tickCount;

    PlatformRenderClock(PlatformRenderSurface platformRenderSurface) {
        super();
        this.platformRenderSurface = platformRenderSurface;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    public double dt = Clock.getCurrentTime();

    @Override
    public void run() {

        long framePeriod = 1000 / targetFPS; // Period in milliseconds
        long frameStartTime = 0;
        long frameStopTime;
        long frameSleepTime;
        long sleepStartTime = 0;

        while (isRunning) {

            // We need to make sure that the surface is ready
            SurfaceHolder holder = platformRenderSurface.getSurfaceHolder();
            if (!holder.getSurface().isValid()) {
                continue;
            }

            dt = Clock.getCurrentTime() - frameStartTime;
            frameStartTime = Clock.getCurrentTime();

            // Advance the world state
            tickCount++;

//            platformRenderSurface.world.update();

            Canvas canvas = holder.lockCanvas();

            // <REFACTOR>
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            Palette palette = new Palette();
            palette.canvas = canvas;
            palette.paint = paint;
            // </REFACTOR>

            try {
                if (canvas != null) {
                    synchronized (holder) {

                        // TODO!!!!!!!!!!!! FLATTEN THE CALLBACK HIERARCHY!!!!!!!!!!!!! FUCK!!!!!!!!

                        platformRenderSurface.world.update();

                        canvas.save();

                        // <CAMERA_VIEWPORT>
                        Entity camera = World.getWorld().Manager.getEntities().filterWithComponent(Camera.class).get(0);
                        Transform cameraTransform = camera.getComponent(Transform.class);
                        canvas.translate(
                                (float) (platformRenderSurface.originTransform.x + cameraTransform.x) /* + (float) Application.getPlatform().getOrientationInput().getRotationY()*/,
                                (float) (platformRenderSurface.originTransform.y + cameraTransform.y) /* - (float) Application.getPlatform().getOrientationInput().getRotationX() */
                        );

                        canvas.scale((float) cameraTransform.scale, (float) cameraTransform.scale);
                        // </CAMERA_VIEWPORT>

                        // <CLEAR_CANVAS>
                        canvas.drawColor(Color.WHITE);
                        // </CLEAR_CANVAS>

                        Group<Entity> entities = World.getWorld().Manager.getEntities().filterActive(true).filterWithComponent(Image.class).sortByLayer();

                        // <UPDATE>
                        // TODO: Draw Renderables.
                        // TODO: This call is expensive. Make it way faster. Cache? Sublist?
                        platformRenderSurface.drawRenderableEntities(entities, canvas, paint, palette);
                        // </UPDATE>

                        if (World.ENABLE_GEOMETRY_ANNOTATIONS) {
                            platformRenderSurface.drawGeometryAnnotations(entities, canvas, paint);
                        }

                        canvas.restore();

                        // <OVERLAY>
                        if (World.ENABLE_OVERLAY) {
                            platformRenderSurface.drawOverlay(canvas, paint);
                        }

                        if (World.ENABLE_GEOMETRY_OVERLAY) {
                            platformRenderSurface.drawGeometryOverlay(canvas, paint);
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
            frameStopTime = Clock.getCurrentTime();
            currentFPS = (1000.0f / (float) (frameStopTime - frameStartTime));
            currentFrameTime = frameStopTime - frameStartTime;

            // Sleep the thread until the time remaining in the frame's allocated draw time expires.
            // This reduces energy consumption thereby increasing battery life.
            frameSleepTime = framePeriod - (frameStopTime - frameStartTime);
            try {
                sleepStartTime = Clock.getCurrentTime();
                if (frameSleepTime > 0) {
                    Thread.sleep(frameSleepTime);
                } else {
                    Thread.sleep(DEFAULT_FRAME_SLEEP_TIME);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            currentSleepTime = Clock.getCurrentTime() - sleepStartTime;
        }
    }

    public double getFramesPerSecond() {
        return currentFPS;
    }
}
