package camp.computer.clay.designer;

public class MapViewDrawer extends Thread {

    static final long FPS = 30;

    private MapView view;

    volatile boolean running = false;

    MapViewDrawer(MapView surfaceView) {
        super ();
        this.view = surfaceView;
    }

    public void setRunning (boolean r) {
        running = r;
    }

    @Override
    public void run () {

        long ticksPS = 1000 / FPS;
        long startTime;
        long sleepTime;

        while (running) {

            startTime = java.lang.System.currentTimeMillis ();
            view.updateSurfaceView ();

            // Sleep until the time remaining in the frame's allocated draw time (for the specified FPS) is reached.
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
