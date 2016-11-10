package camp.computer.clay.platform.graphics;

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

    PlatformRenderClock(PlatformRenderSurface platformRenderSurface) {
        super();
        this.platformRenderSurface = platformRenderSurface;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    @Override
    public void run() {

        long framePeriod = 1000 / targetFPS; // Period in milliseconds
        long frameStartTime;
        long frameStopTime;
        long frameSleepTime;
        long sleepStartTime = 0;

        while (isRunning) {

            currentSleepTime = Clock.getCurrentTime() - sleepStartTime;

            frameStartTime = Clock.getCurrentTime();

            // Advance the world state
            platformRenderSurface.update();

            frameStopTime = Clock.getCurrentTime();

            // Store actual frames per second
            currentFPS = (1000.0f / (float) (frameStopTime - frameStartTime));
            currentFrameTime = frameStopTime - frameStartTime;

            // Sleep the thread until the time remaining in the frame's allocated draw time expires.
            // This reduces energy consumption thereby increasing battery life.
            frameSleepTime = framePeriod - (frameStopTime - frameStartTime);
            try {
                /*
                if (frameSleepTime > 0) {
                    Thread.sleep(frameSleepTime);
                } else {
                    Thread.sleep(10);
                }
                */
                sleepStartTime = Clock.getCurrentTime();
                Thread.sleep(sleepDuration); // HACK
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public double getFramesPerSecond() {
        return currentFPS;
    }
}
