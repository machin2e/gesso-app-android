package camp.computer.clay.platform;

import camp.computer.clay.platform.graphics.RenderSurface;

public class Platform {

    private static Platform instance;

    protected Platform() {
        setup();
    }

    private void setup() {
    }

    public static Platform getInstance() {
        if (instance == null) {
            instance = new Platform();
        }
        return instance;
    }

    public RenderSurface getRenderSurface() {
        return Application.getInstance().renderSurface;
    }

    public void execute(/* Task task */) {

    }
}
