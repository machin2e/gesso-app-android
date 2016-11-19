package camp.computer.clay.engine.system;

import android.util.Log;

import java.util.List;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Boundary;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Physics;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.RelativeLayoutConstraint;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.lib.ImageBuilder.Point;
import camp.computer.clay.lib.ImageBuilder.Rectangle;
import camp.computer.clay.model.configuration.Configuration;
import camp.computer.clay.util.Geometry;
import camp.computer.clay.util.Random;

public class PortableLayoutSystem extends System {

    public PortableLayoutSystem(World world) {
        super(world);
    }

    @Override
    public void update() {
        updatePathPortConfiguration();
        updatePortConfiguration();

        Group<Entity> entities = world.Manager.getEntities().filterActive(true).filterWithComponents(Image.class, Transform.class);
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            if (entity.hasComponent(Extension.class)) {
                updateExtensionGeometry(entity);
            }
        }
    }

    // Update Port configurations based on contained Paths
    private void updatePathPortConfiguration() {

        Group<Entity> paths = world.Manager.getEntities().filterActive(true).filterWithComponent(Path.class);
        for (int i = 0; i < paths.size(); i++) {
            Entity path = paths.get(i);

            Entity sourcePort = Path.getSource(path);
            Entity targetPort = Path.getTarget(path);

            // <HACK>
            // TODO/NOTE: For Prototype Entities that are missing some data...
            if (sourcePort == null) {
                continue;
            }
            // </HACK>

            Path.Type pathType = Path.getType(path);
            if (pathType == Path.Type.NONE) {
                Port.setType(sourcePort, Port.Type.NONE);
            } else if (pathType == Path.Type.SWITCH) {
                Port.setType(sourcePort, Port.Type.SWITCH);
            } else if (pathType == Path.Type.PULSE) {
                Port.setType(sourcePort, Port.Type.PULSE);
            } else if (pathType == Path.Type.WAVE) {
                Port.setType(sourcePort, Port.Type.WAVE);
            } else if (pathType == Path.Type.POWER_REFERENCE) {
                Port.setType(sourcePort, Port.Type.POWER_REFERENCE);
            } else if (pathType == Path.Type.POWER_CMOS) {
                Port.setType(sourcePort, Port.Type.POWER_CMOS);
            } else if (pathType == Path.Type.POWER_TTL) {
                Port.setType(sourcePort, Port.Type.POWER_TTL);
            }

            if (targetPort != null) {
                if (pathType == Path.Type.NONE) {
                    Port.setType(targetPort, Port.Type.NONE);
                } else if (pathType == Path.Type.SWITCH) {
                    Port.setType(targetPort, Port.Type.SWITCH);
                } else if (pathType == Path.Type.PULSE) {
                    Port.setType(targetPort, Port.Type.PULSE);
                } else if (pathType == Path.Type.WAVE) {
                    Port.setType(targetPort, Port.Type.WAVE);
                } else if (pathType == Path.Type.POWER_REFERENCE) {
                    Port.setType(targetPort, Port.Type.POWER_REFERENCE);
                } else if (pathType == Path.Type.POWER_CMOS) {
                    Port.setType(targetPort, Port.Type.POWER_CMOS);
                } else if (pathType == Path.Type.POWER_TTL) {
                    Port.setType(targetPort, Port.Type.POWER_TTL);
                }
            }
        }
    }

    // Cleans up path configurations
    // Clears configuration if there are no Paths containing the Port
    private void updatePortConfiguration() {

        // Clear Ports that are not contained in any Path
        Group<Entity> ports = world.Manager.getEntities().filterWithComponent(Port.class);
        Group<Entity> paths = world.Manager.getEntities().filterWithComponent(Path.class);
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
                Port.setType(port, Port.Type.NONE);
                Port.setDirection(port, Port.Direction.NONE);
            }
        }
    }

    public void setPortableSeparation(double distance) {
        Group<Entity> extensions = world.Manager.getEntities().filterWithComponent(Extension.class);
        for (int i = 0; i < extensions.size(); i++) {
            Entity extension = extensions.get(i);
            if (Portable.getHosts(extension).size() > 0) {
                Entity host = Portable.getHosts(extension).get(0);
                setExtensionDistance(host, distance);
            }
        }
    }

    /**
     * Creates a new {@code ExtensionEntity} connected to {@hostPort}.
     *
     * @param hostPort
     */
    public Entity createCustomExtension(Entity hostPort, Transform initialPosition) {
        // TODO: Remove initialPosition. Should be able to figure out the positioning since have the initial port (and thus a side of the board where the most ports are connected).

        // IASM Message:
        // (1) touch extensionEntity to select from store, or
        // (2) drag signal to base, or
        // (3) touch elsewhere to cancel

        // TODO: NativeUi to select Extension from repository then copy that Extension configuration!
        // TODO: (...) Then use that profile to create and configure Ports for the Extension.

        // Create Extension Entity
        Entity extension = world.createEntity(Extension.class); // HACK: Because Extension is a Component

        // Set the initial position of the Extension
        extension.getComponent(Transform.class).set(initialPosition); // TODO: Set Physics.targetPosition instead? Probs!

        // Configure Host's Port (i.e., the Path's source Port)
        if (Port.getType(hostPort) == Port.Type.NONE || Port.getDirection(hostPort) == Port.Direction.NONE) {
            Port.setType(hostPort, Port.Type.POWER_REFERENCE); // Set the default type to reference (ground)
            Port.setDirection(hostPort, Port.Direction.BOTH);
        }

        // Configure Extension's Ports (i.e., the Path's target Port)
        Entity extensionPort = Portable.getPorts(extension).get(0);
//        extensionPort.getComponent(Port.class).setDirection(Port.Direction.INPUT);
//        extensionPort.getComponent(Port.class).setType(hostPort.getComponent(Port.class).getType());

        // Create Path from Host to Extension and configure the new Path
        // TODO: Create the Path and then apply it. It should automatically configure the
        // TODO: (...) Extension's Ports (so the previous segment of code can be removed and
        // TODO: (...) automated!). The idea here is that a Path can be created given two Ports,
        // TODO: (...) then a System will automatically configure the Ports based on the newly-
        // TODO: (...) existing Path's Port dependencies.
        if (!Port.hasPath(hostPort)) {
            Entity path = world.createEntity(Path.class);
            Path.set(path, hostPort, extensionPort);
        } else {
            Entity path = Port.getPaths(hostPort).get(0);
            Path.set(path, hostPort, extensionPort);
            Path.setTarget(path, extensionPort);
        }

        return extension;
    }

    /**
     * Adds and existing {@code ExtensionEntity}.
     *
     * @param configuration
     * @param initialPosition
     * @return
     */
    public Entity createExtensionFromProfile(Entity host, Configuration configuration, Transform initialPosition) {
        // NOTE: Previously called fetchExtension(...)

        // Log.v("IASM", "(1) touch extensionEntity to select from store or (2) drag signal to base or (3) touch elsewhere to cancel");

        // Create the Extension
        Entity extension = world.createEntity(Extension.class);

        // <HACK>
        // TODO: Remove references to Configuration in Portables. Remove Configuration altogether!?
        world.configureExtensionFromProfile(extension, configuration);
        // </HACK>

        Log.v("Configuration", "extension from profile # ports: " + Portable.getPorts(extension).size());

        // Update ExtensionEntity Position
        extension.getComponent(Transform.class).set(initialPosition);

        // Automatically select and connect all Paths to HostEntity
        autoConnectToHost(host, extension);

        // TODO: Start IASM based on automatically configured Paths to HostEntity.

        updateExtensionLayout(host);

        return extension;
    }

    // TODO: Make PortableLayoutSystem. Iterate through Hosts and lay out Extensions each PortableLayoutSystem.update().
    private boolean autoConnectToHost(Entity host, Entity extension) {

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

            // <HACK>
            switch (Port.getType(ports.get(i))) {
                case NONE:
                    Path.setType(path, Path.Type.NONE);
                    break;
                case SWITCH:
                    Path.setType(path, Path.Type.SWITCH);
                    break;
                case PULSE:
                    Path.setType(path, Path.Type.PULSE);
                    break;
                case WAVE:
                    Path.setType(path, Path.Type.WAVE);
                    break;
                case POWER_REFERENCE:
                    Path.setType(path, Path.Type.POWER_REFERENCE);
                    break;
                case POWER_TTL:
                    Path.setType(path, Path.Type.POWER_TTL);
                    break;
                case POWER_CMOS:
                    Path.setType(path, Path.Type.POWER_CMOS);
                    break;
            }
            // </HACK>

            Path.setMode(path, Path.Mode.ELECTRONIC);
        }

        return true;
    }

    private Entity autoSelectNearestAvailableHostPort(Entity host, Entity extension) {

        // Select an available Port on the Host
        Entity nearestHostPort = null;
        double distanceToSelectedPort = Double.MAX_VALUE;

        Group<Entity> ports = Portable.getPorts(host);
        for (int j = 0; j < ports.size(); j++) {
            if (Port.getType(ports.get(j)) == Port.Type.NONE) {

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

    // TODO: Remove this?
    public int getHeaderIndex(Entity host, Entity extension) {

        int[] indexCounts = new int[4];
        for (int i = 0; i < indexCounts.length; i++) {
            indexCounts[i] = 0;
        }

        Entity boardShape = Image.getShape(host, "Board"); // host.getComponent(Image.class).getImage().getShape("Board");
        List<Transform> hostShapeBoundary = Boundary.get(boardShape);

        Group<Entity> extensionPorts = Portable.getPorts(extension);
        for (int j = 0; j < extensionPorts.size(); j++) {

            Entity extensionPort = extensionPorts.get(j);

            if (extensionPort == null || Port.getPaths(extensionPort).size() == 0 || Port.getPaths(extensionPort).get(0) == null) {
                continue;
            }

            Entity hostPort = Path.getHostPort(Port.getPaths(extensionPort).get(0)); // HACK: Using hard-coded index 0.
            Transform hostPortPosition = Image.getShape(hostPort, "Port").getComponent(Transform.class); // hostPort.getComponent(Image.class).getImage().getShape("Port").getPosition();

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

    public void setExtensionDistance(Entity host, double distance) {
        // TODO: How is distanceToExtensions different from portableSeparation in setPortableSeparation()?
        host.getComponent(Host.class).distanceToExtensions = distance;
        updateExtensionLayout(host);
    }

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

    public void updateExtensionHeaderIndex(Entity host, Entity extension) {
        if (extension.getComponent(Image.class) == null || Portable.getHosts(extension).size() == 0) {
            return;
        }
        int segmentIndex = getHeaderIndex(host, extension);
        host.getComponent(Host.class).headerExtensions.get(segmentIndex).add(extension);
    }

    // <HOST_LAYOUT>


    /**
     * Automatically determines and assigns a valid position for all {@code HostEntity} {@code Image}s.
     */
    public void adjustLayout() {

        int layoutStrategy = 0;

        if (layoutStrategy == 0) {

            Group<Entity> hosts = world.Manager.getEntities().filterWithComponent(Host.class);

            int minDistanceBetweenPoints = 800;

            for (int i = 0; i < hosts.size(); i++) {

                if (i == 0) {
                    // Set initial position to (0, 0)
                    hosts.get(i).getComponent(Physics.class).targetTransform.set(0, 0);
                } else {

                    // Iterate through previously-placed points to find a new one
                    Transform minDistanceTransform = null;
                    double minTotalDistance = Double.MAX_VALUE;
                    for (int j = 0; j < hosts.size(); j++) {

                        // Generate point at each angle
                        int startAngle = Random.getRandomInteger(0, 360);
                        for (int angle = startAngle; angle < startAngle + 360; angle++) {

                            // Generate candidate point i
                            Transform newPoint = Geometry.getRotateTranslatePoint(
                                    hosts.get(j).getComponent(Physics.class).targetTransform,
                                    angle % 360,
                                    minDistanceBetweenPoints
                            );

                            // Check if point is valid. Check if minimum distance from all previous points.
                            boolean isValid = true;
                            double totalDistanceToPreviousPoints = 0;
                            for (int jj = 0; jj < hosts.size(); jj++) {

                                // Get distance between previously generated points and point i
                                double distanceBetweenPoints = Geometry.distance(
                                        newPoint,
                                        hosts.get(jj).getComponent(Physics.class).targetTransform
                                );

                                // Check if point is valid
                                if (distanceBetweenPoints < minDistanceBetweenPoints) {
                                    isValid = false;
                                    break;
                                }

                                // Add distance to point
                                totalDistanceToPreviousPoints += distanceBetweenPoints;
                            }

                            // Check if point is best candidate (nearest to all other points)
                            if (isValid) {
                                if (totalDistanceToPreviousPoints < minTotalDistance) {
                                    minTotalDistance = totalDistanceToPreviousPoints;
                                    minDistanceTransform = newPoint;
                                }
                            }
                        }
                    }

                    // Set the new point
                    hosts.get(i).getComponent(Physics.class).targetTransform.set(
                            minDistanceTransform
                    );
                }

                hosts.get(i).getComponent(Physics.class).targetTransform.setRotation(Random.generateRandomInteger(0, 360));

            }

        } else if (layoutStrategy == 1) {

            // TODO: Make Grid Layout

            /*
            Group<Entity> hosts = world.Manager.getEntities().filterWithComponent(Host.class);

            // Set position on grid layout
            if (hosts.size() == 1) {
                hosts.get(0).getComponent(Transform.class).set(0, 0);
            } else if (hosts.size() == 2) {
                hosts.get(0).getComponent(Transform.class).set(-300, 0);
                hosts.get(1).getComponent(Transform.class).set(300, 0);
            } else if (hosts.size() == 5) {
                hosts.get(0).getComponent(Transform.class).set(-300, -600);
                hosts.get(0).getComponent(Transform.class).setRotation(0);
                hosts.get(1).getComponent(Transform.class).set(300, -600);
                hosts.get(1).getComponent(Transform.class).setRotation(20);
                hosts.get(2).getComponent(Transform.class).set(-300, 0);
                hosts.get(2).getComponent(Transform.class).setRotation(40);
                hosts.get(3).getComponent(Transform.class).set(300, 0);
                hosts.get(3).getComponent(Transform.class).setRotation(60);
                hosts.get(4).getComponent(Transform.class).set(-300, 600);
                hosts.get(4).getComponent(Transform.class).setRotation(80);
            }
            */
        }

        // TODO: Set position on "scatter" layout

        // Set rotation
        // image.setRotation(Random.getRandomGenerator().nextInt(360));
    }
    // <HOST_LAYOUT>

    private void updateExtensionPathRoutes(Entity extension) {
        // TODO: Create routes between extension and host.
    }

    /**
     * Update the {@code Image} to match the state of the corresponding {@code Entity}.
     */
    public void updateExtensionGeometry(Entity extension) {

        // TODO: Clean up/delete images/shapes for any removed ports...

        updateExtensionPortButtonPositions(extension);
        updateExtensionHeaderDimensions(extension);
    }

    /**
     * Add or remove {@code Shape}'s for each of the {@code ExtensionEntity}'s {@code PortEntity}s.
     */
    private void updateExtensionPortButtonPositions(Entity extension) {

        // TODO: Replace above with code that updates the position of Port images, creates new Ports, etc.

        // Update Port positions based on the index of Port
        Group<Entity> ports = Portable.getPorts(extension);
        double halfTotalPortsWidth = (((ports.size() - 1) * World.EXTENSION_PORT_SEPARATION_DISTANCE) / 2.0);
        for (int i = 0; i < ports.size(); i++) {
            ports.get(i).getComponent(RelativeLayoutConstraint.class).relativeTransform.x = (i * World.EXTENSION_PORT_SEPARATION_DISTANCE) - halfTotalPortsWidth;
            ports.get(i).getComponent(RelativeLayoutConstraint.class).relativeTransform.y = 175; // i.e., Distance from board
        }
    }

    // Header Dimensions
    // References:
    // [1] http://www.shenzhen2u.com/image/data/Connector/Break%20Away%20Header-Machine%20Pin%20size.png
    final double errorToleranceA = 0.0; // ±0.60 mm according to [1]
    final double errorToleranceB = 0.0; // ±0.15 mm according to [1]
    double contactSeparation = 2.54; // Measure in millimeters (mm)

    private void updateExtensionHeaderDimensions(Entity extension) {

        // <FACTOR_OUT>
        final int contactCount = Portable.getPorts(extension).size();

        double A = 2.54 * contactCount + errorToleranceA;
        double B = 2.54 * (contactCount - 1) + errorToleranceB;

        // final double errorToleranceContactSeparation = 0.0; // ±0.1 mm according to [1]
        double contactOffset = (A - B) / 2.0; // Measure in millimeters (mm)
        // </FACTOR_OUT>

        // Update Headers Geometry to match the corresponding ExtensionEntity Configuration
        Entity shape = Image.getShape(extension, "Header");
        double headerWidth = World.PIXEL_PER_MILLIMETER * A;
        Rectangle headerShape = (Rectangle) shape.getComponent(camp.computer.clay.engine.component.Geometry.class).shape;
        headerShape.setWidth(headerWidth);

        // TODO: 11/18/2016 Check if there are zero ports. If so, add one. There should always be at least one.

        // Update Contact Positions for Header
        for (int i = 0; i < Portable.getPorts(extension).size(); i++) {
            double x = World.PIXEL_PER_MILLIMETER * ((contactOffset + i * contactSeparation) - (A / 2.0));
            if (i < extension.getComponent(Portable.class).headerContactGeometries.size()) {
                //extension.getComponent(Portable.class).headerContactGeometries.get(i).getImagePosition().x = x;
                Entity headerContactGeometry = extension.getComponent(Portable.class).headerContactGeometries.get(i);
//                headerContactGeometry.getComponent(Transform.class).x = x;
                headerContactGeometry.getComponent(RelativeLayoutConstraint.class).relativeTransform.set(x, 107);
            } else {
                Point headerContactShape = new Point();
//                extension.getComponent(Portable.class).headerContactGeometries.add(headerContactShape);
//                portableImage.getImage().addShape(point);

                // Add new Port shape and set Position
                // TODO: Find better place!
//                Entity headerContactGeometry = world.createEntity(Geometry.class);
//                headerContactGeometry.getComponent(Geometry.class).shape = headerContactShape;
//                headerContactGeometry.getComponent(Transform.class).set(x, 107);

                // <ENTITY>
                long eid = Image.addShape(extension, headerContactShape);
                // <HACK>
                // Set Label
                Entity headerContactGeometry = World.getWorld().Manager.get(eid); // HACK
//                headerContactGeometry.getComponent(RelativeLayoutConstraint.class).relativeTransform.set(x, 107);
                //shapeEntity.getComponent(RelativeLayoutConstraint.class).relativeTransform.rotation = rotation;

                // <REFACTOR_TO_REDUCE_REDUNDANCY>
                Image.addShape(extension, headerContactShape);
                extension.getComponent(Portable.class).headerContactGeometries.add(headerContactGeometry);
//                headerContactGeometry.getComponent(RelativeLayoutConstraint.class).relativeTransform.set(x, 107);
                headerContactGeometry.getComponent(RelativeLayoutConstraint.class).relativeTransform.set(x, 107);
                // </REFACTOR_TO_REDUCE_REDUNDANCY>

            }
        }
    }
    // </EXTENSION>
}
