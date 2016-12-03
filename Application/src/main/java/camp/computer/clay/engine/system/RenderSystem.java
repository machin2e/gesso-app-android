package camp.computer.clay.engine.system;

import camp.computer.clay.engine.World;

public class RenderSystem extends System {

    public RenderSystem(World world) {
        super(world);
    }

    @Override
    public void update(long dt) {
        // TODO: Create Renderable components parsable by platform renderer.
        // TODO: Push Renderables to platform renderer in a synchronized call.
    }
}
