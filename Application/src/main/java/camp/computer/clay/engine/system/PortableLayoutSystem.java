package camp.computer.clay.engine.system;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Component;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Label;
import camp.computer.clay.engine.component.Model;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Primitive;
import camp.computer.clay.engine.component.Structure;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.TransformConstraint;
import camp.computer.clay.engine.component.util.LayoutStrategy;
import camp.computer.clay.engine.component.util.Signal;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Group;
import camp.computer.clay.lib.Geometry.Point;
import camp.computer.clay.lib.Geometry.Rectangle;
import camp.computer.clay.lib.Geometry.Segment;

public class PortableLayoutSystem extends System {

    Group<Entity> entities, hosts, extensions, ports, paths;

    public PortableLayoutSystem(World world) {
        super(world);

        entities = world.entityManager.subscribe(Group.Filters.filterWithComponents, Model.class, Transform.class);
        // TODO: Group<Entity> entitiesWithBoundary = world.entitiesWithBoundary.get().filterActive(true).filterWithComponents(ModelBuilder.class, Transform.class);

        //paths = world.entitiesWithBoundary.get().filterActive(true).filterWithComponent(Path.class);
        paths = world.entityManager.subscribe(Group.Filters.filterWithComponents, Path.class);

        //Group<Entity> hosts = world.entitiesWithBoundary.get().filterWithComponent(Host.class);
        hosts = world.entityManager.subscribe(Group.Filters.filterWithComponents, Host.class);

        //extensions = world.entitiesWithBoundary.get().filterWithComponent(Extension.class);
        extensions = world.entityManager.subscribe(Group.Filters.filterWithComponents, Extension.class);

//        Group<Entity> ports = world.entitiesWithBoundary.get().filterWithComponent(Port.class);
        ports = world.entityManager.subscribe(Group.Filters.filterWithComponents, Port.class);
    }

    @Override
    public void update(long dt) {

        updatePorts();
        resetPorts();

        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            if (entity.hasComponent(Extension.class)) {
                updateExtensionModelDimensions(entity);
            } else if (entity.hasComponent(Path.class)) {
                updateModelMesh(entity);
            }
        }
    }

    /**
     * Automatically determines and assigns a valid position for all {@code HostEntity} {@code ModelBuilder}s.
     * <p>
     * To enable layout algorithms to be changed at runtime, {@code updateWorldLayout()} adopts the
     * <em>strategy design pattern</em> (see https://en.wikipedia.org/wiki/Strategy_pattern).
     */
    public void updateWorldLayout(LayoutStrategy layoutStrategy) {
        layoutStrategy.execute(hosts);
    }

    /**
     * Update the {@code ModelBuilder} to match the state of the corresponding {@code Entity}.
     */
    public void updateExtensionModelDimensions(Entity extension) {

        // TODO: Clean up/delete images/primitives for any removed ports...

        updatePortToolPositions(extension);
        updateHeaderDimensions(extension);
    }

    /**
     * Updates the position of the {@code Port}'s circular control interfaces by updating the
     * relative position of the shape defining the interface with respect to the PCB shape.
     */
    private void updatePortToolPositions(Entity extension) {

        // TODO: Replace above with code that updates the position of Port images, creates new Ports, etc.

        if (extension.hasComponent(Extension.class)) {
            // Update Port positions based on the index of Port
            Group<Entity> ports = Portable.getPorts(extension);
            double halfTotalPortsWidth = (((ports.size() - 1) * World.EXTENSION_PORT_SEPARATION_DISTANCE) / 2.0);
            for (int i = 0; i < ports.size(); i++) {
                ports.get(i).getComponent(TransformConstraint.class).relativeTransform.x = (i * World.EXTENSION_PORT_SEPARATION_DISTANCE) - halfTotalPortsWidth;
                ports.get(i).getComponent(TransformConstraint.class).relativeTransform.y = 175; // i.e., Distance from board
            }
        }
    }

    // Header Dimensions
    // References:
    // [1] http://www.shenzhen2u.com/image/data/Connector/Break%20Away%20Header-Machine%20Pin%20size.png
    final double errorToleranceA = 0.0; // ±0.60 mm according to [1]
    final double errorToleranceB = 0.0; // ±0.15 mm according to [1]
    double contactSeparation = 2.54; // Measure in millimeters (mm)

    private void updateHeaderDimensions(Entity extension) {

        if (extension.hasComponent(Extension.class)) {
            // <FACTOR_OUT>
            final int contactCount = Portable.getPorts(extension).size();

            double A = 2.54 * contactCount + errorToleranceA;
            double B = 2.54 * (contactCount - 1) + errorToleranceB;

            // final double errorToleranceContactSeparation = 0.0; // ±0.1 mm according to [1]
            double contactOffset = (A - B) / 2.0; // Measure in millimeters (mm)
            double extensionHeaderWidth = World.PIXEL_PER_MILLIMETER * A;
            // </FACTOR_OUT>

            // Update Headers Primitive to match the corresponding ExtensionEntity Configuration
            Entity extensionHeaderPrimitive = Model.getPrimitive(extension, "Header");
            Rectangle extensionHeaderShape = (Rectangle) extensionHeaderPrimitive.getComponent(Primitive.class).shape;
            extensionHeaderShape.setWidth(extensionHeaderWidth);

            // TODO: 11/18/2016 Check if there are zero ports. If so, add one. There should always be at least one.

            // Update Contact Positions for Header
            Group<Entity> extensionHeaderContactPrimitives = Model.getPrimitives(extension, "^Pin (1[0-2]|[1-9])$");
            for (int i = 0; i < Portable.getPorts(extension).size(); i++) {
                double x = World.PIXEL_PER_MILLIMETER * ((contactOffset + i * contactSeparation) - (A / 2.0));
                if (i < extensionHeaderContactPrimitives.size()) {
                    Entity headerContactPrimitive = extensionHeaderContactPrimitives.get(i);

                    Entity boardPrimitive = Model.getPrimitive(extension, "Board");
                    Rectangle boardShape = (Rectangle) boardPrimitive.getComponent(Primitive.class).shape;
                    double headerContactOffset = boardShape.height / 2.0f + 7.0f;

                    headerContactPrimitive.getComponent(TransformConstraint.class).relativeTransform.set(x, headerContactOffset); // was 107
                } else {
                    // Add header contact shape
                    Point headerContactShape = new Point();
                    Entity headerContactPrimitive = Model.addShape(extension, headerContactShape);
                    headerContactPrimitive.getComponent(Label.class).label = "Pin " + (i + 1); // HACK?
                }
            }
        }
    }

    /**
     * Updates the mesh in the {@code Entity}'s {@code Model} to match the selected mesh
     * (in {@code meshIndex}).
     */
    private void updateModelMesh(Entity path) {
        if (path.isActive == true) {
            if (path.getComponent(Model.class).meshIndex == 0) { // TODO: Replace magic number index
                selectOverviewPathMesh(path);
            } else if (path.getComponent(Model.class).meshIndex == 1) { // TODO: Replace magic number index
                selectEditablePathMesh(path);
            }
        }
    }

    private void selectOverviewPathMesh(Entity path) {

        if (Path.getMode(path) == Signal.Mode.ELECTRONIC) {

            boolean isSingletonPath = (Path.getTargetPort(path) == null);

            if (!isSingletonPath) {

                // Get Host and Extension Ports
                Entity hostPort = Path.getSourcePort(path);
                Entity host = hostPort.getComponent(Structure.class).parentEntity;
                int hostPortIndex = Port.getIndex(hostPort);

                Entity extensionPort = Path.getTargetPort(path);
                Entity extension = extensionPort.getComponent(Structure.class).parentEntity;
                int extensionPortIndex = Port.getIndex(extensionPort);

                // <REFACTOR>
                Transform hostContactTransform = Model.getPrimitives(host, "^Pin (1[0-2]|[1-9])$").get(hostPortIndex).getComponent(Transform.class); // host.getComponent(Portable.class).headerContactPrimitives.get(hostPortIndex).getComponent(Transform.class);
                Transform extensionContactTransform = Model.getPrimitives(extension, "^Pin (1[0-2]|[1-9])$").get(extensionPortIndex).getComponent(Transform.class); // extension.getComponent(Portable.class).headerContactPrimitives.get(extensionPortIndex).getComponent(Transform.class);
                // </REFACTOR>

                Entity pathPrimitive = Model.getPrimitive(path, "Path");
                Segment pathShape = (Segment) pathPrimitive.getComponent(Primitive.class).shape;
                pathShape.setSource(hostContactTransform);
                pathShape.setTarget(extensionContactTransform);
            }
        }
    }

    private void selectEditablePathMesh(Entity path) {

        boolean isSingletonPath = (Path.getTargetPort(path) == null);

        if (!isSingletonPath) {

            Entity sourcePort = Path.getSourcePort(path);
            Entity sourcePortPrimitive = Model.getPrimitive(sourcePort, "Port");
            Entity targetPortPrimitive = Model.getPrimitive(Path.getTargetPort(path), "Port");

            // <REFACTOR>
            if (Path.getState(path) != Component.State.EDITING) {
                // TODO: sourcePortShape.setPosition(sourcePortShapeE.getComponent(Transform.class));
                // TODO: targetPortShape.setPosition(targetPortShapeE.getComponent(Transform.class));
                Model.getPrimitive(path, "Source Port").getComponent(Transform.class).set(sourcePortPrimitive.getComponent(Transform.class));
                Model.getPrimitive(path, "Target Port").getComponent(Transform.class).set(targetPortPrimitive.getComponent(Transform.class));
            }
            // </REFACTOR>

        } else {

            Entity sourcePort = Path.getSourcePort(path);
            Entity sourcePortPathPrimitive = Model.getPrimitive(path, "Source Port");
            Entity sourcePortPrimitive = Model.getPrimitive(sourcePort, "Port");

            // <REFACTOR>
            path.getComponent(Transform.class).set(sourcePort.getComponent(Transform.class));

            if (Path.getState(path) != Component.State.EDITING) {
                sourcePortPathPrimitive.getComponent(Transform.class).set(sourcePortPrimitive.getComponent(Transform.class));
            }
            // </REFACTOR>

            // <REFACTOR>
            Segment pathShape = (Segment) Model.getPrimitive(path, "Path").getComponent(Primitive.class).shape;
            pathShape.setSource(sourcePortPathPrimitive.getComponent(Transform.class));
            if (Path.getState(path) != Component.State.EDITING) {
                pathShape.setTarget(sourcePortPathPrimitive.getComponent(Transform.class));
            }
            // </REFACTOR>

        }
    }

    /**
     * Updates {@code Port} configurations to reflect the containing {@code Path} configuration.
     */
    private void updatePorts() {

        // Update Port configurations based on contained Paths
        for (int i = 0; i < paths.size(); i++) {
            Entity path = paths.get(i);

            // Update the source port configuration to reflect the path configuration.
            Entity sourcePort = Path.getSourcePort(path);
            Port.setType(sourcePort, Path.getType(path));

            // Update the target port (if any) configuration to reflect the path configuration.
            Entity targetPort = Path.getTargetPort(path);
            if (targetPort != null) {
                Port.setType(targetPort, Path.getType(path));
            }
        }
    }

    // Cleans up path configurations

    /**
     * The purpose of this method is to reset {@code Port} configurations for {@code Port}s that
     * are not contained by a {@code Path}. However, it is also sets {@code Port} configurations
     * that are already in the default "reset" configuration. Therefore, this method also serves to
     * <em>maintain</em> {@code Port}s not contained by a {@code Path} in the default "reset" state.
     */
    private void resetPorts() {

        // Reset Ports that are not contained in any Path. Resets Ports when a Path is moved or
        // removed.
        for (int i = 0; i < ports.size(); i++) {
            Entity port = ports.get(i);
            boolean isPortInPath = false;
            for (int j = 0; j < paths.size(); j++) {
                Entity path = paths.get(j);
                if (Path.contains(path, port)) {
                    isPortInPath = true;
                    break;
                }
            }
            if (!isPortInPath) {
                Port.setType(port, Signal.Type.NONE);
                Port.setDirection(port, Signal.Direction.NONE);
            }
        }
    }

    /*
    public void setPortableSeparation(double distance) {
        for (int i = 0; i < extensions.size(); i++) {
            Entity extension = extensions.get(i);
            if (Portable.getHosts(extension).size() > 0) {
                Entity host = Portable.getHosts(extension).get(0);
                setExtensionDistance(host, distance);
            }
        }
    }
    */

    // <REFACTOR>

    /*
    // TODO: Make PortableLayoutSystem. Iterate through Hosts and lay out Extensions each PortableLayoutSystem.update().
    public boolean autoConnectToHost(Entity host, Entity extension) {

        // Automatically select, connect paths to, and configure the HostEntity's Ports
        Group<Entity> ports = Portable.getPorts(extension);
        for (int i = 0; i < ports.size(); i++) {

            // Select an available HostEntity PortEntity
            Entity selectedHostPort = autoSelectNearestAvailableHostPort(host, extension);

//            // Configure HostEntity's PortEntity
//            Port.setType(selectedHostPort, Port.getType(ports.get(i)));
//            Port.setDirection(selectedHostPort, Port.getDirection(ports.get(i)));

            // Create Path from Extension Ports to Host Ports
            Entity path = world.createEntity(Path.class);
            Path.set(path, selectedHostPort, ports.get(i));

            Path.setType(path, Port.getType(ports.get(i)));

            Path.setMode(path, Path.Mode.ELECTRONIC);
        }

        return true;
    }
    */

    /*
    private Entity autoSelectNearestAvailableHostPort(Entity host, Entity extension) {

        // Select an available Port on the Host
        Entity nearestHostPort = null;
        double distanceToSelectedPort = Double.MAX_VALUE;

        Group<Entity> ports = Portable.getPorts(host);
        for (int j = 0; j < ports.size(); j++) {
            if (Port.getType(ports.get(j)) == Signal.Type.NONE) {

                Entity port = ports.get(j);

                double distanceToPort = Geometry.distance(
                        port.getComponent(Transform.class),
                        extension.getComponent(Transform.class)
                );

                // Check if the Port is the nearest
                if (distanceToPort < distanceToSelectedPort) {
                    nearestHostPort = port;
                    distanceToSelectedPort = distanceToPort;
                }
            }
        }
        // TODO: selectedHostPortEntity = (PortEntity) getPortShapes().getNearestImage(extensionImage.getPosition()).getEntity();
        return nearestHostPort;
    }
    */

    /*
    // TODO: Remove this?
    public int getHeaderIndex(Entity host, Entity extension) {

        int[] indexCounts = new int[4];
        for (int i = 0; i < indexCounts.length; i++) {
            indexCounts[i] = 0;
        }

        Entity boardShape = Model.getPrimitive(host, "Board"); // host.getComponent(ModelBuilder.class).getModelComponent().getPrimitive("Board");
        List<Transform> hostShapeBoundary = Boundary.get(boardShape);

        Group<Entity> extensionPorts = Portable.getPorts(extension);
        for (int j = 0; j < extensionPorts.size(); j++) {

            Entity extensionPort = extensionPorts.get(j);

            if (extensionPort == null || Port.getPaths(extensionPort).size() == 0 || Port.getPaths(extensionPort).get(0) == null) {
                continue;
            }

            Entity hostPort = Path.getHostPort(Port.getPaths(extensionPort).get(0)); // HACK: Using hard-coded index 0.
            Transform hostPortPosition = Model.getPrimitive(hostPort, "Port").getComponent(Transform.class); // hostPort.getComponent(ModelBuilder.class).getModelComponent().getPrimitive("Port").getPosition();

            double minimumSegmentDistance = Double.MAX_VALUE; // Stores the distance to the nearest segment
            int nearestSegmentIndex = 0; // Stores the index of the nearest segment (on the connected HostEntity)
            for (int i = 0; i < hostShapeBoundary.size(); i++) {

                // Terminal points of segment
                Transform p1 = hostShapeBoundary.get(i);
                Transform p2 = (i < hostShapeBoundary.size() - 1 ? hostShapeBoundary.get(i + 1) : hostShapeBoundary.get(0));

                Transform segmentMidpoint = Geometry.midpoint(p1, p2);

                double distance = Geometry.distance(hostPortPosition, segmentMidpoint);

                if (distance < minimumSegmentDistance) {
                    minimumSegmentDistance = distance;
                    nearestSegmentIndex = i;
                }

            }

            indexCounts[nearestSegmentIndex]++;
        }

        // Get the segment with the most counts
        int segmentIndex = -1;
        segmentIndex = 0;
        for (int i = 0; i < indexCounts.length; i++) {
            if (indexCounts[i] > indexCounts[segmentIndex]) {
                segmentIndex = i;
            }
        }

        return segmentIndex;
    }
    */

    /*
    public void setExtensionDistance(Entity host, double distance) {
        // TODO: How is distanceToExtensions different from portableSeparation in setPortableSeparation()?
        host.getComponent(Host.class).distanceToExtensions = distance;
//        updateExtensionLayout(host);
    }
    */

    /*
    public void updateExtensionLayout(Entity host) {

        // Get Extensions connected to the Host
        Group<Entity> extensions = Portable.getExtensions(host);

        Host hostComponent = host.getComponent(Host.class);

        // Reset current layout in preparation for updating it in the presently-running updateImage step.
        for (int i = 0; i < hostComponent.headerExtensions.size(); i++) {
            hostComponent.headerExtensions.get(i).clear();
        }

        // Assign the Extensions connected to this Host to the most-strongly-connected Header.
        // This can be thought of as the "high level layout" of Extension relative to the Host.
        for (int i = 0; i < extensions.size(); i++) {
            updateExtensionHeaderIndex(host, extensions.get(i));
        }

        // Update each Extension's placement, relative to the connected Host.
        for (int headerIndex = 0; headerIndex < hostComponent.headerExtensions.size(); headerIndex++) {
            for (int extensionIndex = 0; extensionIndex < hostComponent.headerExtensions.get(headerIndex).size(); extensionIndex++) {

                Entity extension = hostComponent.headerExtensions.get(headerIndex).get(extensionIndex);

                final double extensionSeparationDistance = 25.0;
                double extensionWidth = 200;
                int extensionCount = hostComponent.headerExtensions.get(headerIndex).size();
                double offset = extensionIndex * 250 - (((extensionCount - 1) * (extensionWidth + extensionSeparationDistance)) / 2.0);

                // Update the Extension's position
                if (headerIndex == 0) {
                    extension.getComponent(Transform.class).set(
                            0 + offset,
                            -hostComponent.distanceToExtensions,
                            host.getComponent(Transform.class)
                    );
                } else if (headerIndex == 1) {
                    extension.getComponent(Transform.class).set(
                            hostComponent.distanceToExtensions,
                            0 + offset,
                            host.getComponent(Transform.class)
                    );
                } else if (headerIndex == 2) {
                    extension.getComponent(Transform.class).set(
                            0 + offset,
                            hostComponent.distanceToExtensions,
                            host.getComponent(Transform.class)
                    );
                } else if (headerIndex == 3) {
                    extension.getComponent(Transform.class).set(
                            -hostComponent.distanceToExtensions,
                            0 + offset,
                            host.getComponent(Transform.class)
                    );
                }

                // Update the Extension's rotation.
                double hostRotation = host.getComponent(Transform.class).getRotation();
                if (headerIndex == 0) {
                    extension.getComponent(Transform.class).setRotation(hostRotation + 0);
                } else if (headerIndex == 1) {
                    extension.getComponent(Transform.class).setRotation(hostRotation + 90);
                } else if (headerIndex == 2) {
                    extension.getComponent(Transform.class).setRotation(hostRotation + 180);
                } else if (headerIndex == 3) {
                    extension.getComponent(Transform.class).setRotation(hostRotation + 270);
                }
            }
        }
    }

    private void updateExtensionHeaderIndex(Entity host, Entity extension) {
        if (extension.getComponent(Model.class) == null || Portable.getHosts(extension).size() == 0) {
            return;
        }
        int segmentIndex = getHeaderIndex(host, extension);
        host.getComponent(Host.class).headerExtensions.get(segmentIndex).add(extension);
    }

    private void updateExtensionPathRoutes(Entity extension) {
        // TODO: Create routes between extension and host.
    }
    */
}
