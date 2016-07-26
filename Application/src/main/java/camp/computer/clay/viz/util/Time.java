package camp.computer.clay.viz.util;

public abstract class Time {

    public static final double MILLISECONDS_PER_SECOND = 1000.0f;

    public static long getCurrentTime() {
        return java.lang.System.currentTimeMillis();
    }
}
