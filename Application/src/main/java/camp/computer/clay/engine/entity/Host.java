package camp.computer.clay.engine.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import camp.computer.clay.Clay;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Space;
import camp.computer.clay.util.image.Visibility;
import camp.computer.clay.util.image.util.ShapeGroup;

public class Host extends PortableEntity {

    public Host() {
        super();
        setup();
    }

    private void setup() {
        headerExtensions.add(new ArrayList<Entity>());
        headerExtensions.add(new ArrayList<Entity>());
        headerExtensions.add(new ArrayList<Entity>());
        headerExtensions.add(new ArrayList<Entity>());
    }

    // has Script/is Scriptable/ScriptableComponent (i.e., Host runs a Script)

    public void update() {
        updateImage();
    }

    // <HACK>
    public void updateImage() {

        ShapeGroup lightShapeGroup = null;

        // Get LED shapes
        if (lightShapeGroup == null) {
            lightShapeGroup = getComponent(Image.class).getShapes().filterLabel("^LED (1[0-2]|[1-9])$");
        }

        // Update Port and LED shape styles
        for (int i = 0; i < getComponent(Portable.class).getPorts().size(); i++) {
            Port port = getComponent(Portable.class).getPorts().get(i);
            Shape portShape = getComponent(Image.class).getShape(port.getLabel()); // Shape portShape = getShape(port);

            // Update color of Port shape based on type
            portShape.setColor(camp.computer.clay.util.Color.getColor(port.getType()));

            // Update color of LED based on corresponding Port's type
            lightShapeGroup.get(i).setColor(portShape.getColor());
        }

        // Call this so PortableEntity.update() will be called to update Geometry
        getComponent(Image.class).update();
    }
    // </HACK>




    //----------------------

    public List<List<Entity>> headerExtensions = new ArrayList<>();

    /**
     * Creates a new {@code ExtensionEntity} connected to {@hostPort}.
     *
     * @param hostPort
     */
    public Entity createExtension(Port hostPort, Transform initialPosition) {

        // TODO: Remove initialPosition... find the position by analyzing the geometry of the HostImage

        //Log.v("ExtensionEntity", "Creating ExtensionEntity from Port");

        //Shape hostPortShape = event.getAction().getFirstEvent().getTargetShape();
//        Shape hostPortShape = getShapes(hostPort);

        //Log.v("IASM", "(1) touch extensionEntity to select from store or (2) drag signal to base or (3) touch elsewhere to cancel");

        // TODO: Prompt to select extensionEntity to use! Then use that profile to create and configure ports for the extensionEntity.

        // Create ExtensionEntity Entity
        UUID extensionUuid = Clay.createEntity(Extension.class); // HACK: Because Extension is a Component
        Entity extensionEntity = Entity.Manager.get(extensionUuid);

        // Set the initial position of the ExtensionEntity
        extensionEntity.getComponent(Transform.class).set(initialPosition);

        // Configure Host's Port (i.e., the Path's source Port)
        if (hostPort.getType() == Port.Type.NONE || hostPort.getDirection() == Port.Direction.NONE) {
            hostPort.setType(Port.Type.POWER_REFERENCE); // Set the default type to reference (ground)
            hostPort.setDirection(Port.Direction.BOTH);
        }

        // Configure ExtensionEntity's Ports (i.e., the Path's target Port)
        Port extensionPort = extensionEntity.getComponent(Portable.class).getPorts().get(0);
        extensionPort.setDirection(Port.Direction.INPUT);
        extensionPort.setType(hostPort.getType());

        // Create Path from Host to ExtensionEntity and configure the new Path
        UUID pathUuid = Clay.createEntity(Path.class);
        Path path = (Path) Entity.getEntity(pathUuid);
        path.set(hostPort, extensionPort);

        // Remove focus from other Hosts and their Ports
        Group<Image> hostImages = Entity.Manager.filterType2(Host.class).getImages();
        for (int i = 0; i < hostImages.size(); i++) {
            Image hostImage = hostImages.get(i);
            PortableEntity host = (PortableEntity) hostImage.getEntity();
            hostImage.setTransparency(0.05f);
            host.getComponent(Portable.class).getPortShapes().setVisibility(Visibility.INVISIBLE);
            host.getComponent(Portable.class).setPathVisibility(Visibility.INVISIBLE);
        }

        // Show Path and all contained Ports
        Group<Path> paths = hostPort.getPaths();
        Group<Port> pathPorts = new Group<>();
        for (int i = 0; i < paths.size(); i++) {
            pathPorts.addAll(paths.get(i).getPorts());
        }

        pathPorts.setVisibility(Visibility.VISIBLE);
        paths.getImages().setVisibility(Visibility.VISIBLE);

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
        extensionEntity.setComponent(new Extension(extensionEntity));

        // <HACK>
        // TODO: Remove references to Profile in Portables. Remove Profile altogether!?
        extensionEntity.getComponent(Extension.class).setProfile(profile);
        // </HACK>

        // Update ExtensionEntity Position
        extensionEntity.getComponent(Transform.class).set(initialPosition);

        // Automatically select and connect all Paths to Host
        autoConnectToHost(extensionEntity);

        // TODO: Start IASM based on automatically configured Paths to Host.

        updateExtensionLayout();

        return extensionEntity;
    }

    private boolean autoConnectToHost(Entity extensionEntity) {

        // Automatically select, connect paths to, and configure the Host's Ports
        for (int i = 0; i < extensionEntity.getComponent(Portable.class).getPorts().size(); i++) {

            // Select an available Host Port
            Port selectedHostPort = autoSelectNearestAvailableHostPort(extensionEntity);

            // Configure Host's Port
            selectedHostPort.setType(extensionEntity.getComponent(Portable.class).getPorts().get(i).getType());
            selectedHostPort.setDirection(extensionEntity.getComponent(Portable.class).getPorts().get(i).getDirection());

            // Create Path from ExtensionEntity Port to Host Port
            UUID pathUuid = Clay.createEntity(Path.class);
            Path path = (Path) Entity.getEntity(pathUuid);
            path.set(selectedHostPort, extensionEntity.getComponent(Portable.class).getPorts().get(i));

            path.setType(Path.Type.ELECTRONIC);
        }

        return true;
    }

    private Port autoSelectNearestAvailableHostPort(Entity extensionEntity) {
        // Select an available Host Port
        Port selectedHostPort = null;
        double distanceToSelectedPort = Double.MAX_VALUE;
        for (int j = 0; j < getComponent(Portable.class).getPorts().size(); j++) {
            if (getComponent(Portable.class).getPorts().get(j).getType() == Port.Type.NONE) {

                Image hostImage = getComponent(Image.class);
                PortableEntity host = (PortableEntity) hostImage.getEntity();

                double distanceToPort = Geometry.distance(
                        host.getComponent(Portable.class).getPortShapes().filterEntity(getComponent(Portable.class).getPorts().get(j)).get(0).getPosition(),
                        extensionEntity.getComponent(Image.class).getEntity().getComponent(Transform.class)
                );

                // Check if the port is the nearest
                if (distanceToPort < distanceToSelectedPort) {
                    selectedHostPort = getComponent(Portable.class).getPorts().get(j);
                    distanceToSelectedPort = distanceToPort;
                }
            }
        }
        // TODO: selectedHostPort = (Port) getPortShapes().getNearestImage(extensionImage.getPosition()).getEntity();
        return selectedHostPort;
    }

    // TODO: Remove this?
    public int getHeaderIndex(Entity extensionEntity) {

        int[] indexCounts = new int[4];
        for (int i = 0; i < indexCounts.length; i++) {
            indexCounts[i] = 0;
        }

        Shape boardShape = getComponent(Image.class).getShape("Board");
        List<Transform> hostShapeBoundary = boardShape.getBoundary();

        Group<Port> extensionPorts = extensionEntity.getComponent(Portable.class).getPorts();
        for (int j = 0; j < extensionPorts.size(); j++) {

            Port extensionPort = extensionPorts.get(j);

            if (extensionPort == null || extensionPort.getPaths().size() == 0 || extensionPort.getPaths().get(0) == null) {
                continue;
            }

            Port hostPort = extensionPort.getPaths().get(0).getHostPort(); // HACK b/c using index 0
            Transform hostPortPosition = Space.getSpace().getShape(hostPort).getPosition();

            double minimumSegmentDistance = Double.MAX_VALUE; // Stores the distance to the nearest segment
            int nearestSegmentIndex = 0; // Stores the index of the nearest segment (on the connected Host)
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

        // Get Extensions connected to the Host.
        Group<Entity> extensionEntities = getComponent(Portable.class).getExtensions();

        // Reset current layout in preparation for updating it in the presently-running update step.
        for (int i = 0; i < headerExtensions.size(); i++) {
            headerExtensions.get(i).clear();
        }

        // Assign the Extensions connected to this Host to the most-strongly-connected Header.
        // This can be thought of as the "high level layout" of ExtensionEntity relative to the Host.
        for (int i = 0; i < extensionEntities.size(); i++) {
            Entity extensionEntity = extensionEntities.get(i);
            updateExtensionHeaderIndex(extensionEntity);
        }

        // Update each ExtensionEntity's placement, relative to the connected Host.
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
                            getComponent(Transform.class)
                    );
                } else if (headerIndex == 1) {
                    extensionEntity.getComponent(Transform.class).set(
                            distanceToExtensions,
                            0 + offset,
                            getComponent(Transform.class)
                    );
                } else if (headerIndex == 2) {
                    extensionEntity.getComponent(Transform.class).set(
                            0 + offset,
                            distanceToExtensions,
                            getComponent(Transform.class)
                    );
                } else if (headerIndex == 3) {
                    extensionEntity.getComponent(Transform.class).set(
                            -distanceToExtensions,
                            0 + offset,
                            getComponent(Transform.class)
                    );
                }

                // Update the ExtensionEntity's rotation.
                double hostEntityRotation = getComponent(Transform.class).getRotation();
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
