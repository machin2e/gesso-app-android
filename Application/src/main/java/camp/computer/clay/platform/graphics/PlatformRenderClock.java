package camp.computer.clay.platform.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.SurfaceHolder;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.util.time.Clock;

/**
 * PlatformRenderClock is a background thread that periodically updates the world state
 * and renders it. By default, the renderer targets frames per second, each time advancing the
 * world's state then re-rendering it.
 */
public class PlatformRenderClock extends Thread {

    // <SETTINGS>
    final public static int DEFAULT_TARGET_FPS = 30;

    private int targetFPS = DEFAULT_TARGET_FPS;

    public int sleepDuration = 2;
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

    private double frameTime;
    public double dt = Clock.getCurrentTime();

    @Override
    public void run() {

//        Canvas canvas = null;

        long framePeriod = 1000 / targetFPS; // Period in milliseconds
        long frameStartTime;
        long frameStopTime;
        long frameSleepTime;
        long sleepStartTime = 0;

        while (isRunning) {

            // We need to make sure that the surface is ready
            SurfaceHolder holder = platformRenderSurface.getHolder();
            if (!holder.getSurface().isValid()) {
                continue;
            }

            dt = Clock.getCurrentTime() - frameTime;
            frameTime = Clock.getCurrentTime();

            currentSleepTime = Clock.getCurrentTime() - sleepStartTime;

            frameStartTime = Clock.getCurrentTime();

            // Advance the world state
            tickCount++;

            platformRenderSurface.world.update();

            Canvas canvas = holder.lockCanvas();
            try {
                if (canvas != null) {
                    synchronized (holder) {
                        // TODO!!!!!!!!!!!! FLATTEN THE CALLBACK TREE!!!!!!!!!!!!! FUCK!!!!!!!!

                        canvas.save();

                        // <CAMERA_VIEWPORT>
                        Entity camera = World.getWorld().Manager.getEntities().filterWithComponent(Camera.class).get(0);
                        Transform cameraPosition = camera.getComponent(Transform.class);
                        canvas.translate(
                                (float) platformRenderSurface.originPosition.x + (float) cameraPosition.x /* + (float) Application.getPlatform().getOrientationInput().getRotationY()*/,
                                (float) platformRenderSurface.originPosition.y + (float) cameraPosition.y /* - (float) Application.getPlatform().getOrientationInput().getRotationX() */
                        );

                        double scale = cameraPosition.scale;
                        canvas.scale(
                                (float) scale,
                                (float) scale
                        );
                        // </CAMERA_VIEWPORT>

                        // <CLEAR_CANVAS>
                        canvas.drawColor(Color.WHITE);
                        // </CLEAR_CANVAS>

                        // <REFACTOR>
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        Palette palette = new Palette();
                        palette.canvas = canvas;
                        palette.paint = paint;
                        // </REFACTOR>

                        // <UPDATE>
                        // TODO: Draw Renderables.
                        platformRenderSurface.drawEntities(canvas, paint, palette);
                        // </UPDATE>

                        canvas.restore();

                        if (World.ENABLE_DEBUG_OVERLAY) {
                            platformRenderSurface.drawOverlay(canvas, paint);
                        }

                        if (World.ENABLE_DEBUG_GEOMETRY) {
                            platformRenderSurface.drawDebugOverlay(canvas, paint);
                        }
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
                    Thread.sleep(10);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public double getFramesPerSecond() {
        return currentFPS;
    }
}
