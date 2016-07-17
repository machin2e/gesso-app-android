package camp.computer.clay.application;

import camp.computer.clay.visualization.util.Time;

/**
 * VisualizationRenderer is a background thread that periodically updates the visualization state
 * and renders it. By default, the renderer targets frames per second, each time advancing the
 * visualization's state then re-rendering it.
 */
public class VisualizationRenderer extends Thread {

    // <SETTINGS>
    final public static long DEFAULT_TARGET_FRAMES_PER_SECOND = 30L;

    public static boolean ENABLE_THREAD_SLEEP = true;

    public static boolean ENABLE_STATISTICS = true;

    private long targetFramesPerSecond = DEFAULT_TARGET_FRAMES_PER_SECOND;
    // </SETTINGS>

    private VisualizationSurface visualizationSurface;

    private boolean isRunning = false;

    VisualizationRenderer(VisualizationSurface visualizationSurface) {
        super();
        this.visualizationSurface = visualizationSurface;
    }

    public void setRunning (boolean isRunning) {
        this.isRunning = isRunning;
    }

    // <STATISTICS>
    private float framesPerSecond = 0L;
    // </STATISTICS>

    @Override
    public void run () {

        long framePeriod = 1000 / targetFramesPerSecond; // Frame period in milliseconds
        long frameStartTime;
        long frameStopTime;
        long frameSleepTime;

        while (isRunning) {

            frameStartTime = Time.getCurrentTime();

            // Advance the visualization state
            visualizationSurface.update();

            frameStopTime = Time.getCurrentTime();

            if (ENABLE_STATISTICS) {
                // Store actual frames per second
                framesPerSecond = (1000.0f / (float) (frameStopTime - frameStartTime));
            }

            // Sleep the thread until the time remaining in the frame's allocated draw time expires.
            // This reduces energy consumption thereby increasing battery life.
            if (ENABLE_THREAD_SLEEP) {
                frameSleepTime = framePeriod - (frameStopTime - frameStartTime);
                try {
                    if (frameSleepTime > 0) {
                        Thread.sleep(frameSleepTime);
                    }
                } catch (Exception e) {
                }
            }

        }
    }

    public long getTargetFramesPerSecond () {
        return targetFramesPerSecond;
    }

    public void setTargetFramesPerSecond (long framesPerSecond) {
        this.targetFramesPerSecond = framesPerSecond;
    }

    public float getFramesPerSecond () {
        return framesPerSecond;
    }
}
