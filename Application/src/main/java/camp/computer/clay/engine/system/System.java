package camp.computer.clay.engine.system;

import camp.computer.clay.engine.World;

public abstract class System {

    // TODO: private int priority = 0;

    protected World world = null;

    public System(World world) {
        this.world = world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public World getWorld() {
        return this.world;
    }

    public abstract void update();
}
