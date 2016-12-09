package camp.computer.clay.platform;

import camp.computer.clay.platform.Application;
import camp.computer.clay.platform.graphics.RenderSurface;

public class Platform {

    public Platform() {
        setup();
    }

    private void setup() {
    }

    public RenderSurface getRenderSurface() {
        return Application.getInstance().renderSurface;
    }
}
