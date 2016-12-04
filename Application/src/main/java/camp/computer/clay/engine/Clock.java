package camp.computer.clay.engine;

public class Clock extends Thread {

    public static long NANOS_PER_MILLISECOND = 1000000;
    public static long NANOS_PER_SECOND = 1000000000;

    private Timer timer = new Timer();

    private long previousTime = 0;

    public Clock() {
    }

    @Override
    public void run() {
        while (true) {
            long time = System.nanoTime();
            synchronized (this) {
                if (timer != null) {
                    // timer.update(time - previousTime);
                    timer.update(time);
                }
            }
        }
    }

    public void setTimer(Timer timer) {
        synchronized (this) {
            this.timer = timer;
        }
    }

    public Timer getTimer() {
        synchronized (this) {
            return timer;
        }
    }

    public static long getCurrentTime() {
        return java.lang.System.currentTimeMillis();

        // May also be able to use:
        // - java.lang.System.nanoTime();
        // - android.os.SystemClock.elapsedRealtime();
    }
}
