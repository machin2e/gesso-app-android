package camp.computer.clay.engine;

import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Workspace;
import camp.computer.clay.engine.component.util.HostLayoutStrategy;
import camp.computer.clay.engine.event.Event;
import camp.computer.clay.engine.event.EventResponse;
import camp.computer.clay.engine.system.AppearanceSystem;
import camp.computer.clay.engine.system.BoundarySystem;
import camp.computer.clay.engine.system.CameraSystem;
import camp.computer.clay.engine.system.EventSystem;
import camp.computer.clay.engine.system.InputSystem;
import camp.computer.clay.engine.system.ModelSystem;
import camp.computer.clay.engine.system.PhysicsSystem;
import camp.computer.clay.engine.system.LayoutSystem;
import camp.computer.clay.engine.system.RenderSystem;
import camp.computer.clay.platform.Platform;
import camp.computer.clay.util.Random;

public class Engine {

    private static Engine instance;

    private Clock clock;

    public long tickCount = 0;

    public World world;

    protected Engine() {

        setup();

        // Configure clock to respond to a tick Event by executing all EventResponses subscribing
        // to the Event in the EventSystem.
        clock = new Clock(new EventResponse() {
            @Override
            public void execute(Event event) {
                World.getInstance().getSystem(EventSystem.class).execute(event);
            }
        });
        clock.start();

        /*
        // Create an event response for each clock tick event that updates the Engine each time
        // it is received.
        World.getInstance().eventManager.subscribe("CLOCK_TICK", new EventResponse() {
            @Override
            public void execute(Event event) {
                update(16);
            }
        });
        */
    }

    public static Engine getInstance() {
        if (instance == null) {
            instance = new Engine();
        }
        return instance;
    }

    private void setup() {

        // Create World
        world = new World();
        world.addSystem(new InputSystem(world));
        world.addSystem(new EventSystem(world));
        world.addSystem(new ModelSystem(world));
        world.addSystem(new AppearanceSystem(world));
        world.addSystem(new PhysicsSystem(world));
        world.addSystem(new LayoutSystem(world));
        world.addSystem(new BoundarySystem(world));
        world.addSystem(new CameraSystem(world));
        world.addSystem(new RenderSystem(world));

        // Create Workspace
        world.createEntity(Workspace.class);

        // Create Camera
        world.createEntity(Camera.class);

        // <DELETE>
        Platform.getInstance().getRenderSurface().setWorld(world);
        // </DELETE>

        // <VIRTUAL_HOSTS>
        int minHostCount = 5;
        int maxHostCount = 6;
        int hostCount = Random.generateRandomInteger(minHostCount, maxHostCount);
        for (int i = 0; i < hostCount; i++) {
            execute(new Event("CREATE_HOST"));
        }
        // </VIRTUAL_HOSTS>

        World.getInstance().eventManager.subscribe("CLOCK_TICK", new EventResponse() {
            @Override
            public void execute(Event event) {
                world.update(event.dt / Clock.NANOS_PER_MILLISECOND);
            }
        });

        // <DELETE>
        // TODO: Place in a LayoutSystem
        world.getSystem(LayoutSystem.class).updateWorldLayout(new HostLayoutStrategy());
        // </DELETE>
    }

    public void enqueue(Event event) {
        world.getSystem(EventSystem.class).enqueue(event);
    }

    public void execute(Event event) {
        world.getSystem(EventSystem.class).execute(event);
    }
}
