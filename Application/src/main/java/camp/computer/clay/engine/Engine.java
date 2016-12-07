package camp.computer.clay.engine;

import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Workspace;
import camp.computer.clay.engine.component.util.HostLayoutStrategy;
import camp.computer.clay.engine.event.Event;
import camp.computer.clay.engine.system.AppearanceSystem;
import camp.computer.clay.engine.system.BoundarySystem;
import camp.computer.clay.engine.system.CameraSystem;
import camp.computer.clay.engine.system.EventSystem;
import camp.computer.clay.engine.system.InputSystem;
import camp.computer.clay.engine.system.ModelSystem;
import camp.computer.clay.engine.system.PhysicsSystem;
import camp.computer.clay.engine.system.PortableLayoutSystem;
import camp.computer.clay.engine.system.RenderSystem;
import camp.computer.clay.util.Random;

public class Engine {

//    private static Engine instance;

    public long tickCount = 0;

    public Platform platform;

//    public World world;

    public Engine(Platform platform) {
        this.platform = platform;

        setup();

        platform.getClock().getTimer().add(new Schedule() {
            @Override
            public void execute(long dt) {
                update(dt);
            }
        });
    }

//    public static Engine getInstance() {
//        if (instance == null) {
//            instance = new Engine();
//        }
//        return instance;
//    }

    private void setup() {

        World world;

        // Create World
        world = new World(this);
        world.addSystem(new InputSystem(world));
        world.addSystem(new EventSystem(world));
        world.addSystem(new ModelSystem(world));
        world.addSystem(new AppearanceSystem(world));
        world.addSystem(new PhysicsSystem(world));
        world.addSystem(new PortableLayoutSystem(world));
        world.addSystem(new BoundarySystem(world));
        world.addSystem(new CameraSystem(world));
        world.addSystem(new RenderSystem(world));

        // Create Workspace
        world.createEntity(Workspace.class);

        // Create Camera
        world.createEntity(Camera.class);

        // <REFACTOR>
        platform.getRenderSurface().setWorld(world);
        // </REFACTOR>

        // <VIRTUAL_HOSTS>
        int minHostCount = 5;
        int maxHostCount = 6;
        int hostCount = Random.generateRandomInteger(minHostCount, maxHostCount);
        for (int i = 0; i < hostCount; i++) {
//            world.createEntity(Host.class);
            world.getSystem(EventSystem.class).execute(new Event("CREATE_HOST"));
        }
        // </VIRTUAL_HOSTS>

        addWorld(world);
    }

    public void update(long dt) {
//        Log.v("TIMING", "Engine Update");
        tickCount++;

        // TODO: Move eventManager queued in Engine into associated World.
    }

    public void addWorld(final World world) {
        platform.getClock().getTimer().add(new Schedule() {
            @Override
            public void execute(long dt) {
                world.update(dt);
            }
        });

        // <HACK>
        // TODO: Place in a LayoutSystem
        world.getSystem(PortableLayoutSystem.class).updateWorldLayout(new HostLayoutStrategy());
        // </HACK>
    }
}
