package camp.computer.clay.application;

/**
 * Maintenance/worker thread to periodically redraw the MapView.
 */
public class VisualizationRenderer extends Thread {

    final public static long FRAMES_PER_SECOND = 30;

    private VisualizationSurface visualizationSurface;

    volatile boolean isRunning = false;

    VisualizationRenderer(VisualizationSurface visualizationSurface) {
        super ();
        this.visualizationSurface = visualizationSurface;
    }

    public void setRunning (boolean isRunning) {
        this.isRunning = isRunning;
    }

    @Override
    public void run () {

        long ticksPS = 1000 / FRAMES_PER_SECOND;
        long startTime;
        long sleepTime;

        while (isRunning) {

            startTime = java.lang.System.currentTimeMillis ();

            visualizationSurface.updateSurfaceView ();

            // Sleep until the time remaining in the frame's allocated draw time (for the specified FRAMES_PER_SECOND) is reached.
            sleepTime = ticksPS - (java.lang.System.currentTimeMillis () - startTime);
            try {
                if (sleepTime > 0) {
                    sleep(sleepTime);
                } else {
                    sleep(10);
                }
            } catch (Exception e) {
            }

        }
    }
}
