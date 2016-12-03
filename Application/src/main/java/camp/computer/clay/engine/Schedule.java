package camp.computer.clay.engine;

public abstract class Schedule {

    long tickFrequency = Clock.NANOS_PER_SECOND / 60; // -1
    long previousTickTime = -1;

    public abstract void execute(long dt);
}
