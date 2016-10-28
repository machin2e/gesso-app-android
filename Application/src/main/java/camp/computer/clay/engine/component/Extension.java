package camp.computer.clay.engine.component;

import java.util.UUID;

import camp.computer.clay.Clay;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.util.Color;
import camp.computer.clay.util.geometry.Circle;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Space;
import camp.computer.clay.util.image.Visibility;

public class Extension extends Component {

    public Extension(Entity entity) {
        super(entity);
    }

    // <FROM_EXTENSION_ENTITY>

    public void update() {
        Image extensionImage = getEntity().getComponent(Image.class);
        extensionImage.update();

        updateExtensionImage();
    }

    private void updateExtensionImage() {

        // Create additional Images or Shapes to match the corresponding Entity
        updateGeometry();
        updateStyle();

        // <HACK>
        updatePathRoutes();
        // </HACK>

        // Call this so PortableImage.update() is called and Geometry is updated!
        getEntity().getComponent(Image.class).update();
    }

    /**
     * Update the {@code Image} to match the state of the corresponding {@code Entity}.
     */
    public void updateGeometry() {
//        super.updateGeometry();

        updatePortGeometry();
        updateHeaderGeometry();

        // TODO: Clean up/delete images/shapes for any removed portEntities...
    }

    /**
     * Add or remove {@code Shape}'s for each of the {@code ExtensionEntity}'s {@code PortEntity}s.
     */
    private void updatePortGeometry() {

        Image image = getEntity().getComponent(Image.class);

        // Remove PortEntity shapes from the Image that do not have a corresponding PortEntity in the Entity
        Group<Shape> portShapes = image.getShapes(Port.class);
        for (int i = 0; i < portShapes.size(); i++) {
            Shape portShape = portShapes.get(i);
            if (!getEntity().getComponent(Portable.class).getPortEntities().contains(portShape.getEntity())) {
                portShapes.remove(portShape);
                image.invalidate();
            }
        }

        // Create PortEntity shapes for each of ExtensionEntity's Ports if they don't already exist
        for (int i = 0; i < getEntity().getComponent(Portable.class).getPortEntities().size(); i++) {
            Entity portEntity = getEntity().getComponent(Portable.class).getPortEntities().get(i);

            if (image.getShape(portEntity) == null) {

                // Ports
                Circle<Entity> circle = new Circle<>(portEntity);
                circle.setRadius(40);
                circle.setLabel("PortEntity " + (getEntity().getComponent(Portable.class).getPortEntities().size() + 1));
                circle.setPosition(-90, 175);
                // circle.setRotation(0);

                circle.setColor("#efefef");
                circle.setOutlineThickness(0);

                circle.setVisibility(Visibility.INVISIBLE);

                image.addShape(circle);

                image.invalidate();
            }
        }

        // Update PortEntity positions based on the index of portEntities
        for (int i = 0; i < getEntity().getComponent(Portable.class).getPortEntities().size(); i++) {
            Entity portEntity = getEntity().getComponent(Portable.class).getPortEntities().get(i);
            Circle portShape = (Circle) image.getShape(portEntity);

            if (portShape != null) {
                double portSpacing = 100;
                portShape.getImagePosition().x = (i * portSpacing) - (((getEntity().getComponent(Portable.class).getPortEntities().size() - 1) * portSpacing) / 2.0);
                // TODO: Also update y coordinate
            }
        }
    }

    private void updateHeaderGeometry() {

        // <FACTOR_OUT>
        // References:
        // [1] http://www.shenzhen2u.com/image/data/Connector/Break%20Away%20Header-Machine%20Pin%20size.png

        final int contactCount = getEntity().getComponent(Portable.class).getPortEntities().size();
        final double errorToleranceA = 0.0; // ±0.60 mm according to [1]
        final double errorToleranceB = 0.0; // ±0.15 mm according to [1]

        double A = 2.54 * contactCount + errorToleranceA;
        double B = 2.54 * (contactCount - 1) + errorToleranceB;

        final double errorToleranceContactSeparation = 0.0; // ±0.1 mm according to [1]
        double contactOffset = (A - B) / 2.0; // Measure in millimeters (mm)
        double contactSeparation = 2.54; // Measure in millimeters (mm)
        // </FACTOR_OUT>

        Image portableImage = getEntity().getComponent(Image.class);

        // Update Headers Geometry to match the corresponding ExtensionEntity Profile
        Rectangle header = (Rectangle) portableImage.getShape("Header");
        double headerWidth = Space.PIXEL_PER_MILLIMETER * A;
        header.setWidth(headerWidth);

        // Update Contact Positions for Header
        for (int i = 0; i < getEntity().getComponent(Portable.class).getPortEntities().size(); i++) {
            double x = Space.PIXEL_PER_MILLIMETER * ((contactOffset + i * contactSeparation) - (A / 2.0));
            if (i < getEntity().getComponent(Portable.class).headerContactPositions.size()) {
                getEntity().getComponent(Portable.class).headerContactPositions.get(i).getImagePosition().x = x;
            } else {
                Point point = new Point(new Transform(x, 107));
                getEntity().getComponent(Portable.class).headerContactPositions.add(point);
                portableImage.addShape(point);
            }
        }
    }

    private void updateStyle() {
        updatePortStyle();
    }

    private void updatePortStyle() {
        // Update PortEntity style
        for (int i = 0; i < getEntity().getComponent(Portable.class).getPortEntities().size(); i++) {
            Entity portEntity = getEntity().getComponent(Portable.class).getPortEntities().get(i);
            Shape portShape = getEntity().getComponent(Image.class).getShape(portEntity);

            // Update color of PortEntity shape based on type
            if (portShape != null) {
                portShape.setColor(Color.getColor(portEntity.getComponent(Port.class).getType()));
            }
        }
    }

    private void updatePathRoutes() {

        // TODO: Get position around "halo" around HostEntity based on rect (a surrounding/containing rectangle) or circular (a surrounding/containing circle) layout algo and set so they don't overlap. Mostly set X to prevent overlap, then run the router and push back the halo distance for that side of the HostEntity, if/as necessary

        // <HACK>
//        updateExtensionLayout();
        // </HACK>

        // TODO: !!!!!!!!!!!! Start Thursday by adding corner/turtle turn "nodes" that extend straight out from

        // TODO: only route paths with turtle graphics maneuvers... so paths are square btwn HostEntity and ExtensionEntity

        // TODO: Goal: implement Ben's demo (input on one HostEntity to analog output on another HostEntity, with diff components)

        ///// TODO: Add label/title to PathEntity, too. ADD OPTION TO FLAG ENTITIES WITH A QUESTION, OR JUST ASK QUESTION/ADD TODO DIRECTLY THERE! ANNOTATE STRUCTURE WITH DESCRIPTIVE/CONTEXTUAL METADATA.

        // TODO: Animate movement of Extensions when "extension halo" expands or contracts (breathes)
    }



    private Profile profile = null;

    public Profile getProfile() {
        return this.profile;
    }

    public boolean hasProfile() {
        return this.profile != null;
    }

    public void setProfile(Profile profile) {
        // Set the Profile used to configure the ExtensionEntity
        this.profile = profile;

        // Create Ports to match the Profile
        for (int i = 0; i < profile.getPorts().size(); i++) {

            UUID portUuid = Clay.createEntity(Port.class);
            Entity portEntity = Entity.getEntity(portUuid);

            portEntity.getComponent(Port.class).setIndex(i);
            portEntity.getComponent(Port.class).setType(profile.getPorts().get(i).getType());
            portEntity.getComponent(Port.class).setDirection(profile.getPorts().get(i).getDirection());
            getEntity().getComponent(Portable.class).addPort(portEntity);
        }
    }

    // </FROM_EXTENSION_ENTITY>
}
