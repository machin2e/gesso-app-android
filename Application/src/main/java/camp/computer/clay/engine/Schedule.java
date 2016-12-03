package camp.computer.clay.engine;

public abstract class Schedule {

    long tickFrequency = -1;
    long previousTickTime = -1;

    public abstract void execute(long dt);
}
