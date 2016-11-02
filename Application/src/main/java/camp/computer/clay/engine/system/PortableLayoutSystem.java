package camp.computer.clay.engine.system;

import java.util.List;

import camp.computer.clay.Clay;
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
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Segment;
import camp.computer.clay.util.geometry.Shape;
import camp.computer.clay.util.image.Visibility2;
import camp.computer.clay.engine.World;

public class PortableLayoutSystem extends System {

    // TODO: Make static methods non-static and call them in the update function or from other systems.

    @Override
    public boolean update(World world) {
        return false;
    }

    public static void setPortableSeparation(double distance) {
        Group<Entity> extensions = Entity.Manager.filterWithComponent(Extension.class);
        for (int i = 0; i < extensions.size(); i++) {
            Entity extension = extensions.get(i);
            if (extension.getComponent(Portable.class).getHosts().size() > 0) {
                Entity host = extension.getComponent(Portable.class).getHosts().get(0);
                PortableLayoutSystem.setExtensionDistance(host, distance);
            }
        }
    }

    /**
     * Creates a new {@code ExtensionEntity} connected to {@hostPort}.
     *
     * @param hostPort
     */
    public static Entity createExtension(Entity hostPort, Transform initialPosition) {

        // IASM Message:
        // (1) touch extensionEntity to select from store, or
        // (2) drag signal to base, or
        // (3) touch elsewhere to cancel

        // TODO: Prompt to select Extension from repository then copy that Extension configuration!
        // TODO: (...) Then use that profile to create and configure Ports for the Extension.

        // Create Extension Entity
        Entity extension = World.createEntity(Extension.class); // HACK: Because Extension is a Component

        // Set the initial position of the Extension
        extension.getComponent(Transform.class).set(initialPosition);

        // Configure Host's Port (i.e., the Path's source Port)
        if (hostPort.getComponent(Port.class).getType() == Port.Type.NONE || hostPort.getComponent(Port.class).getDirection() == Port.Direction.NONE) {
            hostPort.getComponent(Port.class).setType(Port.Type.POWER_REFERENCE); // Set the default type to reference (ground)
            hostPort.getComponent(Port.class).setDirection(Port.Direction.BOTH);
        }

        // Configure Extension's Ports (i.e., the Path's target Port)
        Entity extensionPort = extension.getComponent(Portable.class).getPorts().get(0);
        extensionPort.getComponent(Port.class).setDirection(Port.Direction.INPUT);
        extensionPort.getComponent(Port.class).setType(hostPort.getComponent(Port.class).getType());

        // Create Path from Host to Extension and configure the new Path
        // TODO: Create the Path and then apply it. It should automatically configure the
        // TODO: (...) Extension's Ports (so the previous segment of code can be removed and
        // TODO: (...) automated!). The idea here is that a Path can be created given two Ports,
        // TODO: (...) then a System will automatically configure the Ports based on the newly-
        // TODO: (...) existing Path's Port dependencies.
        Entity path = World.createEntity(Path.class);
        path.getComponent(Path.class).set(hostPort, extensionPort);

        return extension;
    }

    /**
     * Adds and existing {@code ExtensionEntity}.
     *
     * @param profile
     * @param initialPosition
     * @return
     */
    public static Entity restoreExtension(Entity host, Profile profile, Transform initialPosition) {
        // NOTE: Previously called fetchExtension(...)

        // Log.v("IASM", "(1) touch extensionEntity to select from store or (2) drag signal to base or (3) touch elsewhere to cancel");

        // Create the Extension
        Entity extension = World.createEntity(Extension.class);

        // <HACK>
        // TODO: Remove references to Profile in Portables. Remove Profile altogether!?
        Clay.configureFromProfile(extension, profile);
        // </HACK>

        // Update ExtensionEntity Position
        extension.getComponent(Transform.class).set(initialPosition);

        // Automatically select and connect all Paths to HostEntity
        autoConnectToHost(host, extension);

        // TODO: Start IASM based on automatically configured Paths to HostEntity.

        updateExtensionLayout(host);

        return extension;
    }

    // TODO: Make PortableLayoutSystem. Iterate through Hosts and lay out Extensions each PortableLayoutSystem.update().
    private static boolean autoConnectToHost(Entity host, Entity extension) {

        // Automatically select, connect paths to, and configure the HostEntity's Ports
        for (int i = 0; i < extension.getComponent(Portable.class).getPorts().size(); i++) {

            // Select an available HostEntity PortEntity
            Entity selectedHostPort = autoSelectNearestAvailableHostPort(host, extension);

            // Configure HostEntity's PortEntity
            selectedHostPort.getComponent(Port.class).setType(extension.getComponent(Portable.class).getPorts().get(i).getComponent(Port.class).getType());
            selectedHostPort.getComponent(Port.class).setDirection(extension.getComponent(Portable.class).getPorts().get(i).getComponent(Port.class).getDirection());

            // Create PathEntity from ExtensionEntity PortEntity to HostEntity PortEntity
            Entity path = World.createEntity(Path.class);
            path.getComponent(Path.class).set(selectedHostPort, extension.getComponent(Portable.class).getPorts().get(i));

            path.getComponent(Path.class).setType(Path.Type.ELECTRONIC);
        }

        return true;
    }

    private static Entity autoSelectNearestAvailableHostPort(Entity host, Entity extension) {

        // Select an available Port on the Host
        Entity selectedHostPort = null;
        double distanceToSelectedPort = Double.MAX_VALUE;
        for (int j = 0; j < host.getComponent(Portable.class).getPorts().size(); j++) {
            if (host.getComponent(Portable.class).getPorts().get(j).getComponent(Port.class).getType() == Port.Type.NONE) {

                Image hostImage = host.getComponent(Image.class);

//                Entity host = hostImage.getEntity();
                Portable hostPortable = host.getComponent(Portable.class);
                Entity portEntity = hostPortable.getPorts().get(j);

                double distanceToPort = Geometry.distance(
//                        hostPortable.getPortShapes().filterEntity(portEntity).get(0).getPosition(),
                        portEntity.getComponent(Transform.class),
                        extension.getComponent(Image.class).getEntity().getComponent(Transform.class)
                );

                // Check if the port is the nearest
                if (distanceToPort < distanceToSelectedPort) {
                    selectedHostPort = host.getComponent(Portable.class).getPorts().get(j);
                    distanceToSelectedPort = distanceToPort;
                }
            }
        }
        // TODO: selectedHostPortEntity = (PortEntity) getPortShapes().getNearestImage(extensionImage.getPosition()).getEntity();
        return selectedHostPort;
    }

    // TODO: Remove this?
    public static int getHeaderIndex(Entity host, Entity extension) {

        int[] indexCounts = new int[4];
        for (int i = 0; i < indexCounts.length; i++) {
            indexCounts[i] = 0;
        }

        Shape boardShape = host.getComponent(Image.class).getShape("Board");
        List<Transform> hostShapeBoundary = boardShape.getBoundary();

        Group<Entity> extensionPorts = extension.getComponent(Portable.class).getPorts();
        for (int j = 0; j < extensionPorts.size(); j++) {

            Entity extensionPort = extensionPorts.get(j);

            if (extensionPort == null || extensionPort.getComponent(Port.class).getPaths().size() == 0 || extensionPort.getComponent(Port.class).getPaths().get(0) == null) {
                continue;
            }

            Entity hostPort = extensionPort.getComponent(Port.class).getPaths().get(0).getComponent(Path.class).getHostPort(); // HACK: Using hard-coded index 0.
            Transform hostPortPosition = hostPort.getComponent(Image.class).getShape("Port").getPosition();

            double minimumSegmentDistance = Double.MAX_VALUE; // Stores the distance to the nearest segment
            int nearestSegmentIndex = 0; // Stores the index of the nearest segment (on the connected HostEntity)
            for (int i = 0; i < hostShapeBoundary.size(); i++) {

                if (i < hostShapeBoundary.size() - 1) {
                    Transform segmentMidpoint = Geometry.midpoint(hostShapeBoundary.get(i), hostShapeBoundary.get(i + 1));

                    double distance = Geometry.distance(hostPortPosition, segmentMidpoint);

                    if (distance < minimumSegmentDistance) {
                        minimumSegmentDistance = distance;
                        nearestSegmentIndex = i;
                    }
                } else {
                    // Check last segment (first and last point in array)
                    Transform segmentMidpoint = Geometry.midpoint(hostShapeBoundary.get(i), hostShapeBoundary.get(0));

                    double distance = Geometry.distance(hostPortPosition, segmentMidpoint);

                    if (distance < minimumSegmentDistance) {
                        minimumSegmentDistance = distance;
                        nearestSegmentIndex = i;
                    }
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

    public static void setExtensionDistance(Entity host, double distance) {
        host.getComponent(Host.class).distanceToExtensions = distance;
        updateExtensionLayout(host);
    }

    public static void updateExtensionLayout(Entity host) {

        // Get Extensions connected to the Host
        Group<Entity> extensions = host.getComponent(Portable.class).getExtensions();

        Host hostComponent = host.getComponent(Host.class);

        // Reset current layout in preparation for updating it in the presently-running updateImage step.
        for (int i = 0; i < hostComponent.headerExtensions.size(); i++) {
            hostComponent.headerExtensions.get(i).clear();
        }

        // Assign the Extensions connected to this HostEntity to the most-strongly-connected Header.
        // This can be thought of as the "high level layout" of ExtensionEntity relative to the HostEntity.
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
                extension.getComponent(Image.class).invalidate();
            }
        }
    }

    public static void updateExtensionHeaderIndex(Entity host, Entity extension) {
        if (extension.getComponent(Image.class) == null || extension.getComponent(Portable.class).getHosts().size() == 0) {
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

//        Group<Image> hostImages = Entity.Manager.filterType2(HostEntity.class).getImages();
        Group<Image> hostImages = Entity.Manager.filterWithComponent(Host.class).getImages();

        // Set position on grid layout
        if (hostImages.size() == 1) {
            hostImages.get(0).getEntity().getComponent(Transform.class).set(0, 0);
        } else if (hostImages.size() == 2) {
            hostImages.get(0).getEntity().getComponent(Transform.class).set(-300, 0);
            hostImages.get(1).getEntity().getComponent(Transform.class).set(300, 0);
        } else if (hostImages.size() == 5) {
            hostImages.get(0).getEntity().getComponent(Transform.class).set(-300, -600);
            hostImages.get(0).getEntity().getComponent(Transform.class).setRotation(0);
            hostImages.get(1).getEntity().getComponent(Transform.class).set(300, -600);
            hostImages.get(1).getEntity().getComponent(Transform.class).setRotation(20);
            hostImages.get(2).getEntity().getComponent(Transform.class).set(-300, 0);
            hostImages.get(2).getEntity().getComponent(Transform.class).setRotation(40);
            hostImages.get(3).getEntity().getComponent(Transform.class).set(300, 0);
            hostImages.get(3).getEntity().getComponent(Transform.class).setRotation(60);
            hostImages.get(4).getEntity().getComponent(Transform.class).set(-300, 600);
            hostImages.get(4).getEntity().getComponent(Transform.class).setRotation(80);
        }

        // TODO: Set position on "scatter" layout

        // Set rotation
        // image.setRotation(Probability.getRandomGenerator().nextInt(360));
    }
    // <HOST_LAYOUT>

    // <PROTOTYPES>
    public void setExtensionPrototypePosition(Transform position) {
//        this.extensionPrototypePosition.set(position);
        // <HACK>
        // TODO: This is a crazy expensive operation. Optimize the shit out of this.
        Entity extensionPrototype = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0);
        // </HACK>
        extensionPrototype.getComponent(Transform.class).set(position);

        // <REFACTOR>
        // <HACK>
        // TODO: This is a crazy expensive operation. Optimize the shit out of this.
        Entity pathPrototype = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypePath").get(0);
        // </HACK>
        Segment segment = (Segment) pathPrototype.getComponent(Image.class).getShape("Path");
        Transform prototypePathSourceTransform = segment.getSource();

        double pathRotationAngle = Geometry.getAngle(
                prototypePathSourceTransform,
                extensionPrototype.getComponent(Transform.class)
        );
        extensionPrototype.getComponent(Transform.class).setRotation(pathRotationAngle);
        // <REFACTOR>

        // <HACK>
        // TODO: Move! Needed, but should be in a better place so it doesn't have to be explicitly called!
        extensionPrototype.getComponent(Image.class).invalidate();
        World.getWorld().boundarySystem.updateImage(extensionPrototype);
        // </HACK>
    }

    public void setPathPrototypeVisibility2(Visibility2 visibility2) {
//        pathPrototypeVisibility2 = visibility2;
        // <HACK>
        // TODO: This is a crazy expensive operation. Optimize the shit out of this.
        Entity pathPrototype = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypePath").get(0);
        // </HACK>
        if (visibility2 == Visibility2.INVISIBLE) {
            pathPrototype.getComponent(Visibility.class).isVisible = false;
        } else if (visibility2 == Visibility2.VISIBLE) {
            pathPrototype.getComponent(Visibility.class).isVisible = true;
        }
    }

    public Visibility2 getPathPrototypeVisibility2() {
//        return pathPrototypeVisibility2;
        // <HACK>
        // TODO: This is a crazy expensive operation. Optimize the shit out of this.
        Entity pathPrototype = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypePath").get(0);
        // </HACK>
        return pathPrototype.getComponent(Visibility.class).isVisible == true ? Visibility2.VISIBLE : Visibility2.INVISIBLE;
    }

    public void setPathPrototypeSourcePosition(Transform position) {
//        this.pathPrototypeSourcePosition.set(position);
        // <HACK>
        // TODO: This is a crazy expensive operation. Optimize the shit out of this.
        Entity pathPrototype = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypePath").get(0);
        // </HACK>
        Segment segment = (Segment) pathPrototype.getComponent(Image.class).getShape("Path");
        segment.setSource(position);
    }

    public void setPathPrototypeDestinationPosition(Transform position) {
//        this.pathPrototypeDestinationCoordinate.set(position);
        // <HACK>
        // TODO: This is a crazy expensive operation. Optimize the shit out of this.
        Entity pathPrototype = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypePath").get(0);
        // </HACK>
        Segment segment = (Segment) pathPrototype.getComponent(Image.class).getShape("Path");
        segment.setTarget(position);
    }

    public void setExtensionPrototypeVisibility2(Visibility2 visibility2) {
//        extensionPrototypeVisibility2 = visibility2;
        // <HACK>
        // TODO: This is a crazy expensive operation. Optimize the shit out of this.
        Entity extensionPrototype = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0);
        // </HACK>
        if (visibility2 == Visibility2.INVISIBLE) {
            extensionPrototype.getComponent(Visibility.class).isVisible = false;
        } else if (visibility2 == Visibility2.VISIBLE) {
            extensionPrototype.getComponent(Visibility.class).isVisible = true;
        }
    }

    public Visibility2 getExtensionPrototypeVisibility2() {
//        return extensionPrototypeVisibility2;
        // <HACK>
        // TODO: This is a crazy expensive operation. Optimize the shit out of this.
        Entity extensionPrototype = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0);
        // </HACK>
        return extensionPrototype.getComponent(Visibility.class).isVisible == true ? Visibility2.VISIBLE : Visibility2.INVISIBLE;
    }
    // </PROTOTYPES>
}
