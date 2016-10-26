package camp.computer.clay.engine.component;

import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.entity.Port;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.util.Color;
import camp.computer.clay.util.geometry.Circle;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Space;
import camp.computer.clay.util.image.Visibility;
import camp.computer.clay.util.image.util.ShapeGroup;

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

        // TODO: Clean up/delete images/shapes for any removed ports...
    }

    /**
     * Add or remove {@code Shape}'s for each of the {@code ExtensionEntity}'s {@code Port}s.
     */
    private void updatePortGeometry() {

        Image image = getEntity().getComponent(Image.class);

        // Remove Port shapes from the Image that do not have a corresponding Port in the Entity
        ShapeGroup portShapes = image.getShapes(Port.class);
        for (int i = 0; i < portShapes.size(); i++) {
            Shape portShape = portShapes.get(i);
            if (!getEntity().getComponent(Portable.class).getPorts().contains(portShape.getEntity())) {
                portShapes.remove(portShape);
                image.invalidate();
            }
        }

        // Create Port shapes for each of ExtensionEntity's Ports if they don't already exist
        for (int i = 0; i < getEntity().getComponent(Portable.class).getPorts().size(); i++) {
            Port port = getEntity().getComponent(Portable.class).getPorts().get(i);

            if (image.getShape(port) == null) {

                // Ports
                Circle<Port> circle = new Circle<>(port);
                circle.setRadius(40);
                circle.setLabel("Port " + (getEntity().getComponent(Portable.class).getPorts().size() + 1));
                circle.setPosition(-90, 175);
                // circle.setRotation(0);

                circle.setColor("#efefef");
                circle.setOutlineThickness(0);

                circle.setVisibility(Visibility.INVISIBLE);

                image.addShape(circle);

                image.invalidate();
            }
        }

        // Update Port positions based on the index of ports
        for (int i = 0; i < getEntity().getComponent(Portable.class).getPorts().size(); i++) {
            Port port = getEntity().getComponent(Portable.class).getPorts().get(i);
            Circle portShape = (Circle) image.getShape(port);

            if (portShape != null) {
                double portSpacing = 100;
                portShape.getImagePosition().x = (i * portSpacing) - (((getEntity().getComponent(Portable.class).getPorts().size() - 1) * portSpacing) / 2.0);
                // TODO: Also update y coordinate
            }
        }
    }

    private void updateHeaderGeometry() {

        // <FACTOR_OUT>
        // References:
        // [1] http://www.shenzhen2u.com/image/data/Connector/Break%20Away%20Header-Machine%20Pin%20size.png

        final int contactCount = getEntity().getComponent(Portable.class).getPorts().size();
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
        for (int i = 0; i < getEntity().getComponent(Portable.class).getPorts().size(); i++) {
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
        // Update Port style
        for (int i = 0; i < getEntity().getComponent(Portable.class).getPorts().size(); i++) {
            Port port = getEntity().getComponent(Portable.class).getPorts().get(i);
            Shape portShape = getEntity().getComponent(Image.class).getShape(port);

            // Update color of Port shape based on type
            if (portShape != null) {
                portShape.setColor(Color.getColor(port.getType()));
            }
        }
    }

    private void updatePathRoutes() {

        // TODO: Get position around "halo" around Host based on rect (a surrounding/containing rectangle) or circular (a surrounding/containing circle) layout algo and set so they don't overlap. Mostly set X to prevent overlap, then run the router and push back the halo distance for that side of the Host, if/as necessary

        // <HACK>
//        updateExtensionLayout();
        // </HACK>

        // TODO: !!!!!!!!!!!! Start Thursday by adding corner/turtle turn "nodes" that extend straight out from

        // TODO: only route paths with turtle graphics maneuvers... so paths are square btwn Host and ExtensionEntity

        // TODO: Goal: implement Ben's demo (input on one Host to analog output on another Host, with diff components)

        ///// TODO: Add label/title to Path, too. ADD OPTION TO FLAG ENTITIES WITH A QUESTION, OR JUST ASK QUESTION/ADD TODO DIRECTLY THERE! ANNOTATE STRUCTURE WITH DESCRIPTIVE/CONTEXTUAL METADATA.

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
            Port port = new Port();
            port.setIndex(i);
            port.setType(profile.getPorts().get(i).getType());
            port.setDirection(profile.getPorts().get(i).getDirection());
            getEntity().getComponent(Portable.class).addPort(port);
        }
    }

    // </FROM_EXTENSION_ENTITY>
}