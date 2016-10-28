package camp.computer.clay.util.time;

/**
 * References:
 * - <a href="https://developer.android.com/reference/android/os/SystemClock.html">Android SystemClock Documentation</a>
 */
public class Clock {

    public static final double MILLISECONDS_PER_SECOND = 1000.0f;

    public Clock() {
        setup();
    }

    public static long getCurrentTime() {
        return java.lang.System.currentTimeMillis();

        // May also be able to use:
        // - java.lang.System.nanoTime();
        // - android.os.SystemClock.elapsedRealtime();
    }

//    private long timeFrequency = 0L; // What is this? The count frequency of the "high performance counter"
    private long frameTime = 0L; // Timestamp at which the current frame was started (last call to reset())
    private double previousFrameDuration = 0.0D;
//    private long frameDuration = 0L; // deltaLastFrame
//    private double previousFrameDuration = 0.0D;
    // LARGE_INTEGER timeLastFrame
    //
    // boolean initialize()
    // boolean shutdown()
    // void newFrame()
    // float getPreviousFrameDuration()

    private void setup() {
        // Set the previous frame time to JVM's high-resolution time source, in nanoseconds.
        frameTime = java.lang.System.currentTimeMillis(); // Alternative: java.lang.System.currentTimeMillis();
    }

    public boolean start() {
        return true;
    }

    public boolean stop() {
        return true;
    }

    public void reset() { // newFrame

        long frameTime = getCurrentTime();
        long frameDuration = frameTime - this.frameTime;

        this.frameTime = frameTime;
        this.previousFrameDuration = frameDuration;
    }

    // Call this from the Time/Clock Thread
    private void tick() {
        // TODO: Call "updateImage" method for (subscribing?) Entities in Simulation
    }

    public long getFrameTime() {
        return frameTime;
    }

    /**
     * Returns the units of time since the last frame. Used to updateImage the simulation state
     * independent of drawing to the frame.
     *
     * This allows the simulation to run independently. It helps address issues that can arise
     * in animation when the simulation state changes depend on the frame rate.
     *
     * @return
     */
    public double dt() { // getPreviousFrameDuration()
        return getCurrentTime() - frameTime;
    }

    public double getPreviousFrameDuration() {
        return previousFrameDuration;
    }
}
