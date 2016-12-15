package camp.computer.clay.engine.system;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Model;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Primitive;
import camp.computer.clay.engine.component.Style;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.util.FilterStrategy;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Group;

public class AppearanceSystem extends System {

    // TODO: Use this system to change the ModelBuilder's currently displayed "frame" or "suit"? Maybe ModelSystem is better for that. And update style here?

    Group<Entity> entities;

    public AppearanceSystem(World world) {
        super(world);
        setup();
    }

    private void setup() {
        // Setup subscriptions for needed Entity groups.
        entities = world.entityManager.subscribe(
                new FilterStrategy(Group.Filters.filterWithComponents, Host.class, Style.class, Transform.class, Model.class)
        );
        // TODO: (if needed) entityManager = world.entityManager.registerResponse(Group.Filters.filterWithComponents, Style.class, Transform.class, ModelBuilder.class);
    }

    /**
     * Updates appearance of {@code Entity} objects. This includes color, transparency, etc.
     *
     * @param dt
     */
    @Override
    public void update(long dt) {
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);

            // Update Style
            if (entity.hasComponent(Extension.class)) {
            } else if (entity.hasComponent(Host.class)) {
                setHostLightColor(entity);
            } else if (entity.hasComponent(Port.class)) {
            } else if (entity.hasComponent(Path.class)) {
            }
        }
    }

    public void setHostLightColor(Entity host) {

        // TODO: Optimize regex calls to use ids/hashes! Cache_OLD!

        // Get LED primitives
        Group<Entity> ports = Portable.getPorts(host);
        Group<Entity> lightShapes = Model.getPrimitives(host, "^LED (1[0-2]|[1-9])$");

        // Update color of LEDs to color of corresponding Port shape
        for (int i = 0; i < ports.size(); i++) {
            String portColor = camp.computer.clay.util.Color.getColor(Port.getType(ports.get(i)));
            lightShapes.get(i).getComponent(Primitive.class).shape.setColor(portColor);
        }
    }
}
