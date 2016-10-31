package camp.computer.clay.engine.component;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.Clay;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.World;
import camp.computer.clay.util.image.Visibility;

public class Host extends Component {

    public List<List<Entity>> headerExtensions = new ArrayList<>();

    public Host() {
        super();
    }

    public void setupHeaderExtensions() {
        headerExtensions.add(new ArrayList<Entity>());
        headerExtensions.add(new ArrayList<Entity>());
        headerExtensions.add(new ArrayList<Entity>());
        headerExtensions.add(new ArrayList<Entity>());
    }

    //----------------------

    /**
     * Creates a new {@code ExtensionEntity} connected to {@hostPort}.
     *
     * @param hostPort
     */
    public Entity createExtension(Entity hostPort, Transform initialPosition) {

        // TODO: Remove initialPosition... find the position by analyzing the geometry of the HostImage

        //Log.v("ExtensionEntity", "Creating ExtensionEntity from PortEntity");

        //Shape hostPortShape = event.getAction().getFirstEvent().getTargetShape();
//        Shape hostPortShape = getShapes(hostPortEntity);

        //Log.v("IASM", "(1) touch extensionEntity to select from store or (2) drag signal to base or (3) touch elsewhere to cancel");

        // TODO: Prompt to select extensionEntity to use! Then use that profile to create and configure portEntities for the extensionEntity.

        // Create Extension Entity
        Entity extensionEntity = Clay.createEntity(Extension.class); // HACK: Because Extension is a Component

        // Set the initial position of the Extension
        extensionEntity.getComponent(Transform.class).set(initialPosition);

        // Configure Host's Port (i.e., the Path's source Port)
        if (hostPort.getComponent(Port.class).getType() == Port.Type.NONE || hostPort.getComponent(Port.class).getDirection() == Port.Direction.NONE) {
            hostPort.getComponent(Port.class).setType(Port.Type.POWER_REFERENCE); // Set the default type to reference (ground)
            hostPort.getComponent(Port.class).setDirection(Port.Direction.BOTH);
        }

        // Configure Extension's Ports (i.e., the Path's target Port)
        Entity extensionPortEntity = extensionEntity.getComponent(Portable.class).getPorts().get(0);
        extensionPortEntity.getComponent(Port.class).setDirection(Port.Direction.INPUT);
        extensionPortEntity.getComponent(Port.class).setType(hostPort.getComponent(Port.class).getType());

        // Create Path from Host to Extension and configure the new Path
        Entity pathEntity = Clay.createEntity(Path.class);
        pathEntity.getComponent(Path.class).set(hostPort, extensionPortEntity);

        // Remove focus from other Hosts and their Ports
        Group<Image> hostImages = Entity.Manager.filterWithComponent(Host.class).getImages();
        for (int i = 0; i < hostImages.size(); i++) {
            Image hostImage = hostImages.get(i);
            Entity host = hostImage.getEntity();
            hostImage.setTransparency(0.05f);
//            host.getComponent(Portable.class).getPortShapes().setVisibility(Visibility.INVISIBLE);
            host.getComponent(Portable.class).getPorts().setVisibility(false);
            host.getComponent(Portable.class).setPathVisibility(false);
        }

        // Get all Ports in all Paths from the Host
        Group<Entity> hostPaths = hostPort.getComponent(Port.class).getPaths();
        Group<Entity> hostPorts = new Group<>();
        for (int i = 0; i < hostPaths.size(); i++) {
            Group<Entity> pathPorts = hostPaths.get(i).getComponent(Path.class).getPorts();
            hostPorts.addAll(pathPorts);
        }

        // Show all of Host's Paths and all Ports contained in those Paths
        hostPaths.setVisibility(true);
        hostPorts.setVisibility(true);

        // Update layout
        updateExtensionLayout();

        return extensionEntity;
    }

    /**
     * Adds and existing {@code ExtensionEntity}.
     *
     * @param profile
     * @param initialPosition
     * @return
     */
    public Entity restoreExtension(Profile profile, Transform initialPosition) {
        // NOTE: Previously called fetchExtension(...)

        // Log.v("IASM", "(1) touch extensionEntity to select from store or (2) drag signal to base or (3) touch elsewhere to cancel");

        // Create the ExtensionEntity
        Entity extensionEntity = new Entity();

        // Add Extension Component (for type identification)
        extensionEntity.addComponent(new Extension());

        // <HACK>
        // TODO: Remove references to Profile in Portables. Remove Profile altogether!?
        extensionEntity.getComponent(Extension.class).setProfile(profile);
        // </HACK>

        // Update ExtensionEntity Position
        extensionEntity.getComponent(Transform.class).set(initialPosition);

        // Automatically select and connect all Paths to HostEntity
        autoConnectToHost(extensionEntity);

        // TODO: Start IASM based on automatically configured Paths to HostEntity.

        updateExtensionLayout();

        return extensionEntity;
    }

    private boolean autoConnectToHost(Entity extensionEntity) {

        // Automatically select, connect paths to, and configure the HostEntity's Ports
        for (int i = 0; i < extensionEntity.getComponent(Portable.class).getPorts().size(); i++) {

            // Select an available HostEntity PortEntity
            Entity selectedHostPortEntity = autoSelectNearestAvailableHostPort(extensionEntity);

            // Configure HostEntity's PortEntity
            selectedHostPortEntity.getComponent(Port.class).setType(extensionEntity.getComponent(Portable.class).getPorts().get(i).getComponent(Port.class).getType());
            selectedHostPortEntity.getComponent(Port.class).setDirection(extensionEntity.getComponent(Portable.class).getPorts().get(i).getComponent(Port.class).getDirection());

            // Create PathEntity from ExtensionEntity PortEntity to HostEntity PortEntity
            Entity pathEntity = Clay.createEntity(Path.class);
            pathEntity.getComponent(Path.class).set(selectedHostPortEntity, extensionEntity.getComponent(Portable.class).getPorts().get(i));

            pathEntity.getComponent(Path.class).setType(Path.Type.ELECTRONIC);
        }

        return true;
    }

    private Entity autoSelectNearestAvailableHostPort(Entity extensionEntity) {
        // Select an available HostEntity PortEntity
        Entity selectedHostPortEntity = null;
        double distanceToSelectedPort = Double.MAX_VALUE;
        for (int j = 0; j < getEntity().getComponent(Portable.class).getPorts().size(); j++) {
            if (getEntity().getComponent(Portable.class).getPorts().get(j).getComponent(Port.class).getType() == Port.Type.NONE) {

                Image hostImage = getEntity().getComponent(Image.class);

                Entity host = hostImage.getEntity();
                Portable hostPortable = host.getComponent(Portable.class);
                Entity portEntity = hostPortable.getPorts().get(j);

                double distanceToPort = Geometry.distance(
//                        hostPortable.getPortShapes().filterEntity(portEntity).get(0).getPosition(),
                        portEntity.getComponent(Transform.class),
                        extensionEntity.getComponent(Image.class).getEntity().getComponent(Transform.class)
                );

                // Check if the port is the nearest
                if (distanceToPort < distanceToSelectedPort) {
                    selectedHostPortEntity = getEntity().getComponent(Portable.class).getPorts().get(j);
                    distanceToSelectedPort = distanceToPort;
                }
            }
        }
        // TODO: selectedHostPortEntity = (PortEntity) getPortShapes().getNearestImage(extensionImage.getPosition()).getEntity();
        return selectedHostPortEntity;
    }

    // TODO: Remove this?
    public int getHeaderIndex(Entity extensionEntity) {

        int[] indexCounts = new int[4];
        for (int i = 0; i < indexCounts.length; i++) {
            indexCounts[i] = 0;
        }

        Shape boardShape = getEntity().getComponent(Image.class).getShape("Board");
        List<Transform> hostShapeBoundary = boardShape.getBoundary();

        Group<Entity> extensionPortEntities = extensionEntity.getComponent(Portable.class).getPorts();
        for (int j = 0; j < extensionPortEntities.size(); j++) {

            Entity extensionPortEntity = extensionPortEntities.get(j);

            if (extensionPortEntity == null || extensionPortEntity.getComponent(Port.class).getPaths().size() == 0 || extensionPortEntity.getComponent(Port.class).getPaths().get(0) == null) {
                continue;
            }

            Entity hostPortEntity = extensionPortEntity.getComponent(Port.class).getPaths().get(0).getComponent(Path.class).getHostPort(); // HACK b/c using index 0
            Transform hostPortPosition = World.getWorld().getShape(hostPortEntity).getPosition();

            double minimumSegmentDistance = Double.MAX_VALUE; // Stores the distance to the nearest segment
            int nearestSegmentIndex = 0; // Stores the index of the nearest segment (on the connected HostEntity)
            for (int i = 0; i < hostShapeBoundary.size() - 1; i++) {

                Transform segmentMidpoint = Geometry.midpoint(hostShapeBoundary.get(i), hostShapeBoundary.get(i + 1));

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

    protected double distanceToExtensions = 500;

    public void setExtensionDistance(double distance) {
        distanceToExtensions = distance;
        updateExtensionLayout();
    }

    public void updateExtensionLayout() {

        // Get Extensions connected to the HostEntity.
        Group<Entity> extensionEntities = getEntity().getComponent(Portable.class).getExtensions();

        // Reset current layout in preparation for updating it in the presently-running updateImage step.
        for (int i = 0; i < headerExtensions.size(); i++) {
            headerExtensions.get(i).clear();
        }

        // Assign the Extensions connected to this HostEntity to the most-strongly-connected Header.
        // This can be thought of as the "high level layout" of ExtensionEntity relative to the HostEntity.
        for (int i = 0; i < extensionEntities.size(); i++) {
            Entity extensionEntity = extensionEntities.get(i);
            updateExtensionHeaderIndex(extensionEntity);
        }

        // Update each Extension's placement, relative to the connected Host.
        for (int headerIndex = 0; headerIndex < headerExtensions.size(); headerIndex++) {
            for (int extensionIndex = 0; extensionIndex < headerExtensions.get(headerIndex).size(); extensionIndex++) {

                Entity extensionEntity = headerExtensions.get(headerIndex).get(extensionIndex);

                final double extensionSeparationDistance = 25.0;
                double extensionWidth = 200;
                int extensionCount = headerExtensions.get(headerIndex).size();
                double offset = extensionIndex * 250 - (((extensionCount - 1) * (extensionWidth + extensionSeparationDistance)) / 2.0);

                // Update the ExtensionEntity's position.
                if (headerIndex == 0) {
                    extensionEntity.getComponent(Transform.class).set(
                            0 + offset,
                            -distanceToExtensions,
                            getEntity().getComponent(Transform.class)
                    );
                } else if (headerIndex == 1) {
                    extensionEntity.getComponent(Transform.class).set(
                            distanceToExtensions,
                            0 + offset,
                            getEntity().getComponent(Transform.class)
                    );
                } else if (headerIndex == 2) {
                    extensionEntity.getComponent(Transform.class).set(
                            0 + offset,
                            distanceToExtensions,
                            getEntity().getComponent(Transform.class)
                    );
                } else if (headerIndex == 3) {
                    extensionEntity.getComponent(Transform.class).set(
                            -distanceToExtensions,
                            0 + offset,
                            getEntity().getComponent(Transform.class)
                    );
                }

                // Update the ExtensionEntity's rotation.
                double hostEntityRotation = getEntity().getComponent(Transform.class).getRotation();
                if (headerIndex == 0) {
                    extensionEntity.getComponent(Transform.class).setRotation(hostEntityRotation + 0);
                } else if (headerIndex == 1) {
                    extensionEntity.getComponent(Transform.class).setRotation(hostEntityRotation + 90);
                } else if (headerIndex == 2) {
                    extensionEntity.getComponent(Transform.class).setRotation(hostEntityRotation + 180);
                } else if (headerIndex == 3) {
                    extensionEntity.getComponent(Transform.class).setRotation(hostEntityRotation + 270);
                }

                // Invalidate Image Component so its geometry (i.e., shapes) will be updated.
                extensionEntity.getComponent(Image.class).invalidate();
            }
        }
    }

    // TODO: Refactor this... it's really dumb right now.
    public void updateExtensionHeaderIndex(Entity extensionEntity) {
        if (extensionEntity.getComponent(Image.class) == null || extensionEntity.getComponent(Portable.class).getHosts().size() == 0) {
            return;
        }
        int segmentIndex = getHeaderIndex(extensionEntity);
        headerExtensions.get(segmentIndex).add(extensionEntity);
    }
}
