package camp.computer.clay.engine;

public class Platform {

    private Clock clock;

    public Platform() {
        setup();
    }

    private void setup() {
        clock = new Clock();
        clock.start();
    }

    public Clock getClock() {
        return clock;
    }
}
