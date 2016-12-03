package camp.computer.clay.engine;

import camp.computer.clay.platform.Application;
import camp.computer.clay.platform.graphics.RenderSurface;

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

    public RenderSurface getRenderSurface() {
        return Application.getInstance().renderSurface;
    }
}
