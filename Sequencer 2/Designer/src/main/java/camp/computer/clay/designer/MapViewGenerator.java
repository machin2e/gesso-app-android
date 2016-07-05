package camp.computer.clay.designer;

/**
 * Maintenance/worker thread to periodically redraw the MapView.
 */
public class MapViewGenerator extends Thread {

    final public static long FRAMES_PER_SECOND = 30;

    private MapView mapView;

    volatile boolean isRunning = false;

    MapViewGenerator(MapView surfaceView) {
        super ();
        this.mapView = surfaceView;
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

            mapView.updateSurfaceView ();

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
