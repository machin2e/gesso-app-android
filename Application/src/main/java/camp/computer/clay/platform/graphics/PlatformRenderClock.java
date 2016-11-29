package camp.computer.clay.platform.graphics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.SurfaceHolder;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.system.RenderSystem;
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
    public static volatile double dt = Clock.getCurrentTime();

    static volatile float f = 0;
    static volatile float incr = 0.1f;

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
//            platformRenderSurface.update();

            //////////
//            Canvas canvas = null;
//        SurfaceHolder holder = getHolder();

//        if (!isUpdated) {

//            isUpdated = true;
//        }

//            platformRenderSurface.world.update();

//            double dt = Application.getInstance().platformRenderSurface.platformRenderClock.dt; // 1.0;

            f += incr * dt;
            if (f < -10.0f || f > 10.0f) {
                incr *= -1;
            }
            Log.v("ffff", "dt: " + dt + ", f: " + f);

            Canvas canvas = holder.lockCanvas();
            try {
                if (canvas != null) {
//                    if (isUpdated) {
//                        palette.canvas = canvas;
                    synchronized (holder) {
                        platformRenderSurface.world.update();
                        // TODO!!!!!!!!!!!! FLATTEN THE CALLBACK TREE!!!!!!!!!!!!! FUCK!!!!!!!!
//                        platformRenderSurface.world.draw(canvas);

//                        Bitmap canvasBitmap = platformRenderSurface.canvasBitmap;
//                        Matrix identityMatrix = platformRenderSurface.identityMatrix;

                        // Adjust the Camera
                        canvas.save();

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

                        canvas.drawColor(Color.WHITE);

                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setColor(Color.BLUE);

                        Palette palette = new Palette();
                        palette.canvas = canvas;
                        palette.paint = paint;
                        platformRenderSurface.world.getSystem(RenderSystem.class).drawEntities(canvas, paint, palette);

                        canvas.restore();

                        if (World.ENABLE_DRAW_OVERLAY) {
                            World.getWorld().getSystem(RenderSystem.class).drawOverlay(canvas, paint, platformRenderSurface);
                        }

                        World.getWorld().getSystem(RenderSystem.class).drawDebugOverlay(canvas, paint, platformRenderSurface);

                        // Paint the bitmap to the "primary" canvas.
//                        palette.canvas.drawBitmap(canvasBitmap, identityMatrix, null);

                        // Paint the bitmap to the "primary" canvas.
//                        canvas.drawBitmap(canvasBitmap, identityMatrix, null);
                    }
//                        isUpdated = false;
//                    }
                }
            } finally {
                if (canvas != null) {
                    holder.unlockCanvasAndPost(canvas);
                }
            }

            // draw
//            Canvas canvas = holder.lockCanvas();
//            if (canvas != null) {
//                // canvas.draw(...);
//                holder.unlockCanvasAndPost(canvas);
//            }
            ///////////

            frameStopTime = Clock.getCurrentTime();

            // Store actual frames per second
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
//                Thread.sleep(sleepDuration); // HACK
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public double getFramesPerSecond() {
        return currentFPS;
    }
}
