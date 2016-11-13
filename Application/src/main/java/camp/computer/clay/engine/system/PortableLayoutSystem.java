package camp.computer.clay.engine.system;

import android.util.Log;

import java.util.List;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Label;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.Visibility;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.model.configuration.Configuration;
import camp.computer.clay.util.ImageBuilder.Geometry;
import camp.computer.clay.util.ImageBuilder.Segment;
import camp.computer.clay.util.ImageBuilder.Shape;
import camp.computer.clay.engine.component.util.Visible;
import camp.computer.clay.engine.World;
import camp.computer.clay.util.Random;

public class PortableLayoutSystem extends System {

    public PortableLayoutSystem(World world) {
        super(world);
    }

    @Override
    public void update() {
        updatePathPortConfiguration();
        updatePortConfiguration();
    }

    // Update Port configurations based on contained Paths
    private void updatePathPortConfiguration() {

        Group<Entity> paths = world.Manager.getEntities().filterActive(true).filterWithComponent(Path.class);
        for (int i = 0; i < paths.size(); i++) {
            Entity path = paths.get(i);

            Entity sourcePort = Path.getSource(path);
            Entity targetPort = Path.getTarget(path);

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

        // IASM Message:
        // (1) touch extensionEntity to select from store, or
        // (2) drag signal to base, or
        // (3) touch elsewhere to cancel

        // TODO: NativeUi to select Extension from repository then copy that Extension configuration!
        // TODO: (...) Then use that profile to create and configure Ports for the Extension.

        // Create Extension Entity
        Entity extension = world.createEntity(Extension.class); // HACK: Because Extension is a Component

        // Set the initial position of the Extension
        extension.getComponent(Transform.class).set(initialPosition);

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

        Shape boardShape = host.getComponent(Image.class).getImage().getShape("Board");
        List<Transform> hostShapeBoundary = BoundarySystem.getBoundary(boardShape);

        Group<Entity> extensionPorts = Portable.getPorts(extension);
        for (int j = 0; j < extensionPorts.size(); j++) {

            Entity extensionPort = extensionPorts.get(j);

            if (extensionPort == null || Port.getPaths(extensionPort).size() == 0 || Port.getPaths(extensionPort).get(0) == null) {
                continue;
            }

            Entity hostPort = Path.getHostPort(Port.getPaths(extensionPort).get(0)); // HACK: Using hard-coded index 0.
            Transform hostPortPosition = hostPort.getComponent(Image.class).getImage().getShape("Port").getPosition();

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

                // Invalidate Image Component so its geometry (i.e., shapes) will be updated.
                // <HACK>
                // TODO: World shouldn't call systems. System should operate on the world and interact with other systems/entities in it.
                world.imageSystem.invalidate(extension.getComponent(Image.class));
                // </HACK>
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

        int layoutStrategy = 1;

        if (layoutStrategy == 0) {
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
        } else if (layoutStrategy == 1) {

            Group<Entity> hosts = world.Manager.getEntities().filterWithComponent(Host.class);

            int minX = -800, maxX = 800;
            int minY = -800, maxY = 800;
            int minDistanceBetweenPoints = 800;

            for (int i = 0; i < hosts.size(); i++) {

                if (i == 0) {
                    // Set initial position to (0, 0)
                    hosts.get(i).getComponent(Transform.class).set(0, 0);
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
                                    hosts.get(j).getComponent(Transform.class),
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
                                        hosts.get(jj).getComponent(Transform.class)
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
                    hosts.get(i).getComponent(Transform.class).set(
                            minDistanceTransform
                    );
                }

                hosts.get(i).getComponent(Transform.class).setRotation(Random.generateRandomInteger(0, 360));

            }

        }

        // TODO: Set position on "scatter" layout

        // Set rotation
        // image.setRotation(Random.getRandomGenerator().nextInt(360));
    }
    // <HOST_LAYOUT>

    // <PROTOTYPES>
    public void setExtensionPrototypePosition(Transform position) {

        // <HACK>
        // TODO: This is a crazy expensive operation. Optimize the shit out of this.
        Entity extensionPrototype = world.Manager.getEntities().filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0);
        // </HACK>
        extensionPrototype.getComponent(Transform.class).set(position);

        // <REFACTOR>
        // <HACK>
        // TODO: This is a crazy expensive operation. Optimize the shit out of this.
        Entity pathPrototype = world.Manager.getEntities().filterWithComponent(Label.class).filterLabel("prototypePath").get(0);
        // </HACK>
        Segment segment = (Segment) pathPrototype.getComponent(Image.class).getImage().getShape("Path");
        Transform prototypePathSourceTransform = segment.getSource();

        double extensionRotation = Geometry.getAngle(
                prototypePathSourceTransform,
                extensionPrototype.getComponent(Transform.class)
        );
        extensionPrototype.getComponent(Transform.class).setRotation(extensionRotation);
        // <REFACTOR>

        // <HACK>
        // TODO: World shouldn't call systems. System should operate on the world and interact with other systems/entities in it.
//        world.imageSystem.invalidate(extensionPrototype.getComponent(Image.class));
        // </HACK>

        // <HACK>
        // TODO: Move! Needed, but should be in a better place so it doesn't have to be explicitly called!
//        world.boundarySystem.updateImage(extensionPrototype);
        // </HACK>
    }

    public void setPathPrototypeVisibility(Visible visible) {
        // <HACK>
        // TODO: This is a crazy expensive operation. Optimize the shit out of this.
        Entity pathPrototype = world.Manager.getEntities().filterWithComponent(Label.class).filterLabel("prototypePath").get(0);
        // </HACK>
        pathPrototype.getComponent(Visibility.class).setVisible(visible);
    }

    public Visible getPathPrototypeVisibility() {
        // <HACK>
        // TODO: This is a crazy expensive operation. Optimize the shit out of this.
        Entity pathPrototype = world.Manager.getEntities().filterWithComponent(Label.class).filterLabel("prototypePath").get(0);
        // </HACK>
        return pathPrototype.getComponent(Visibility.class).getVisibile();
    }

    public void setPathPrototypeSourcePosition(Transform position) {
        // <HACK>
        // TODO: This is a crazy expensive operation. Optimize the shit out of this.
        Entity pathPrototype = world.Manager.getEntities().filterWithComponent(Label.class).filterLabel("prototypePath").get(0);
        // </HACK>
        Segment segment = (Segment) pathPrototype.getComponent(Image.class).getImage().getShape("Path");
        segment.setSource(position);
    }

    public void setPathPrototypeDestinationPosition(Transform position) {
        // <HACK>
        // TODO: This is a crazy expensive operation. Optimize the shit out of this.
        Entity pathPrototype = world.Manager.getEntities().filterWithComponent(Label.class).filterLabel("prototypePath").get(0);
        // </HACK>
        Segment segment = (Segment) pathPrototype.getComponent(Image.class).getImage().getShape("Path");
        segment.setTarget(position);
    }

    public void setExtensionPrototypeVisibility(Visible visible) {
        // <HACK>
        // TODO: This is a crazy expensive operation. Optimize the shit out of this.
        Entity extensionPrototype = world.Manager.getEntities().filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0);
        // </HACK>
        extensionPrototype.getComponent(Visibility.class).setVisible(visible);
    }

    public Visible getExtensionPrototypeVisibility() {
        // <HACK>
        // TODO: This is a crazy expensive operation. Optimize the shit out of this.
        Entity extensionPrototype = world.Manager.getEntities().filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0);
        // </HACK>
        return extensionPrototype.getComponent(Visibility.class).getVisibile();
    }
    // </PROTOTYPES>
}
