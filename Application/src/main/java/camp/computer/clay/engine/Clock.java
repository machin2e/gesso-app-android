package camp.computer.clay.engine;

import camp.computer.clay.engine.event.Event;
import camp.computer.clay.engine.manager.EventResponse;

/**
 * {@code Clock} generates an {@code Event} ("CLOCK_TICK") each at specified frequency.
 */
public class Clock extends Thread {

    // TODO: "Spring-loaded" Timer. Timers instantiate their own clocking Clock thread that
    // TODO: makes a synchronized call to some method on each tick.

    // TODO: ^ Make Platform and Engine singletons so they can be accessed anywhere. EventQueue is in Engine. Can call Engine.enqueue(Event).

    // TODO: Replace schedule with an EventResponse that calls the update function. (DONE)
    // TODO: Then the Event/EventResponse architecture should be usable throughout the entire application. [[Maxim/Goal(not "Principle" since that's undirected and has the frivolity of academia: REDUCE the number of concepts in your architecture.]]

    // TODO: setFrequency(Hz -- updates per second)

    public enum Unit {
        NANOSECONDS,
        MILLISECONDS,
        SECONDS
    }

    public static long NANOS_PER_MILLISECOND = 1000000;
    public static long NANOS_PER_SECOND = 1000000000;

    private EventResponse eventResponse;

    public Clock(EventResponse eventResponse) {
        this.eventResponse = eventResponse;
    }

    private long previousTickTime = 0L;
    private long tickFrequency = Clock.NANOS_PER_SECOND / 30;

    @Override
    public void run() {
        while (true) {
            long time = System.nanoTime();
            long dt = time - previousTickTime;
            if (dt >= tickFrequency) {
                previousTickTime = time;
                synchronized (this) {
                    Event event = new Event("CLOCK_TICK");
                    event.dt = dt;
                    eventResponse.execute(event); // TODO: Attach timing status.
                }
            }
        }

    }

    public static long getTime(Unit unit) {
        switch (unit) {
            case MILLISECONDS:
            default: {
                return java.lang.System.currentTimeMillis();
            }
        }

        // May also be able to use:
        // - java.lang.System.nanoTime();
        // - android.os.SystemClock.elapsedRealtime();
    }
}
