package camp.computer.clay.application.graphics;

import camp.computer.clay.util.time.Clock;

/**
 * PlatformRenderer is a background thread that periodically updates the space state
 * and renders it. By default, the renderer targets frames per second, each time advancing the
 * space's state then re-rendering it.
 */
public class PlatformRenderer extends Thread {

    // <SETTINGS>
    final public static int DEFAULT_TARGET_FRAMES_PER_SECOND = 30;

    final public static int DEFAULT_FPS_MOVING_AVERAGE_SAMPLE_COUNT = 1;

    public static boolean ENABLE_THREAD_SLEEP = true;

    public static boolean ENABLE_STATISTICS = true;

    private int targetFramesPerSecond = DEFAULT_TARGET_FRAMES_PER_SECOND;
    // </SETTINGS>

    private PlatformRenderSurface platformRenderSurface;

    private boolean isRunning = false;

    PlatformRenderer(PlatformRenderSurface platformRenderSurface) {
        super();
        this.platformRenderSurface = platformRenderSurface;
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }

    // <STATISTICS>
    private double currentFramesPerSecond = 0;
    private int fpsSampleIndex = 0;
    private final int fpsSampleLimit = DEFAULT_FPS_MOVING_AVERAGE_SAMPLE_COUNT; // Moving FPS average for getLastEvent second.
    private double[] fpsSamples = new double[fpsSampleLimit];
    // </STATISTICS>

    @Override
    public void run() {

        long framePeriod = 1000 / targetFramesPerSecond; // PhoneHost period in milliseconds
        long frameStartTime;
        long frameStopTime;
        long frameSleepTime;

        while (isRunning) {

            frameStartTime = Clock.getCurrentTime();

            // Advance the space state
            platformRenderSurface.update();

            frameStopTime = Clock.getCurrentTime();

//            if (ENABLE_STATISTICS) {
            // Store actual frames per second
            currentFramesPerSecond = (1000.0f / (float) (frameStopTime - frameStartTime));

//                // Store moving average
//                fpsSamples[fpsSampleIndex] = currentFramesPerSecond;
//                fpsSampleIndex = (fpsSampleIndex + 1) % fpsSampleLimit;
//            }

            // Sleep the thread until the time remaining in the frame's allocated draw time expires.
            // This reduces energy consumption thereby increasing battery life.
//            if (ENABLE_THREAD_SLEEP) {
//            frameSleepTime = framePeriod - (frameStopTime - frameStartTime);
//                Log.v("SleepTime", "sleepTime: " + frameSleepTime);
            try {
//                if (frameSleepTime > 0) {
//                    Thread.sleep(frameSleepTime);
//                } else {
                Thread.sleep(10);
//                }
            } catch (Exception e) {
                e.printStackTrace();
            }
//            }

        }
    }

    public long getTargetFramesPerSecond() {
        return targetFramesPerSecond;
    }

    public void setTargetFramesPerSecond(int framesPerSecond) {
        this.targetFramesPerSecond = framesPerSecond;
    }

    public double getFramesPerSecond() {

//        double fpsTotal = 0;
//
//        for (int i = 0; i < fpsSampleLimit; i++) {
//            fpsTotal = fpsTotal + fpsSamples[i];
//        }
//
//        return (fpsTotal / fpsSampleLimit);

        return currentFramesPerSecond;
    }
}
