package camp.computer.clay.engine.system;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.util.Color;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Space;

public class UpdateSystem extends System {

    @Override
    public boolean update(Space space) {

        // Update Actors
        space.getActor().update(); // HACK

        updateEntities(Entity.Manager);

        return true;
    }


    // <ENTITY>
    public void updateEntities(Group<Entity> entities) {
        for (int i = 0; i < entities.size(); i++) {

            Entity entity = entities.get(i);

            // entity.updateImage();

            // <HACK>
            if (entity.hasComponent(Extension.class)) {
                updateExtensionImage(entity);
            } else if (entity.hasComponent(Host.class)) {
                updateHostImage(entity);
            } else if (entity.hasComponent(Port.class)) {
                updatePortImage(entity);
            } else if (entity.hasComponent(Camera.class)) {
                entity.getComponent(Camera.class).update(); // TODO: Bring Camera update code into this file/system.
            }
            // </HACK>
        }
    }
    // </ENTITY>


    // <IMAGE>

    // Previously: Image.update()
    // Required Components: Image, Transform
    public void updateImage(Entity entity) {
        Image image = entity.getComponent(Image.class);

        // Update Shapes
        for (int i = 0; i < image.getShapes().size(); i++) {
            Shape shape = image.getShapes().get(i);
            shape.update(image.getEntity().getComponent(Transform.class));
        }
    }
    // </IMAGE>


    // <HOST>
    public void updateHostImage(Entity host) {

        // Get LED shapes
        Group<Shape> lightShapeGroup = host.getComponent(Image.class).getShapes().filterLabel("^LED (1[0-2]|[1-9])$");

        // Update Port LED color
        for (int i = 0; i < host.getComponent(Portable.class).getPorts().size(); i++) {

            // Update color of LED based on corresponding Port's type
            Entity port = host.getComponent(Portable.class).getPorts().get(i);
            String portColor = camp.computer.clay.util.Color.getColor(port.getComponent(Port.class).getType());
            lightShapeGroup.get(i).setColor(portColor);
        }

        // Call this so PortableEntity.updateImage() will be called to updateImage Geometry
        updateImage(host);
    }
    // </HOST>

    // <EXTENSION>
    private void updateExtensionImage(Entity extension) {

        // Create additional Images or Shapes to match the corresponding Entity
        updateExtensionGeometry(extension);
        updateExtensionStyle(extension);

        // <HACK>
        updateExtensionPathRoutes(extension);
        // </HACK>

        updateImage(extension);
    }

    /**
     * Update the {@code Image} to match the state of the corresponding {@code Entity}.
     */
    public void updateExtensionGeometry(Entity extension) {

        // TODO: Clean up/delete images/shapes for any removed portEntities...

        updateExtensionPortGeometry(extension);
        updateExtensionHeaderGeometry(extension);
    }

    /**
     * Add or remove {@code Shape}'s for each of the {@code ExtensionEntity}'s {@code PortEntity}s.
     */
    private void updateExtensionPortGeometry(Entity extension) {

        // TODO: Replace above with code that updates the position of Port images, creates new Ports, etc.

        // Update Port positions based on the index of Port
        for (int i = 0; i < extension.getComponent(Portable.class).getPorts().size(); i++) {
            Entity port = extension.getComponent(Portable.class).getPorts().get(i);

            double portSpacing = 115;
            port.getComponent(Transform.class).x = (i * portSpacing) - (((extension.getComponent(Portable.class).getPorts().size() - 1) * portSpacing) / 2.0);
            port.getComponent(Transform.class).y = 175; // i.e., Distance from board

            port.getComponent(Image.class).invalidate();
        }
    }

    private void updateExtensionHeaderGeometry(Entity extension) {

        // <FACTOR_OUT>
        // References:
        // [1] http://www.shenzhen2u.com/image/data/Connector/Break%20Away%20Header-Machine%20Pin%20size.png

        final int contactCount = extension.getComponent(Portable.class).getPorts().size();
        final double errorToleranceA = 0.0; // ±0.60 mm according to [1]
        final double errorToleranceB = 0.0; // ±0.15 mm according to [1]

        double A = 2.54 * contactCount + errorToleranceA;
        double B = 2.54 * (contactCount - 1) + errorToleranceB;

        final double errorToleranceContactSeparation = 0.0; // ±0.1 mm according to [1]
        double contactOffset = (A - B) / 2.0; // Measure in millimeters (mm)
        double contactSeparation = 2.54; // Measure in millimeters (mm)
        // </FACTOR_OUT>

        Image portableImage = extension.getComponent(Image.class);

        // Update Headers Geometry to match the corresponding ExtensionEntity Profile
        Rectangle header = (Rectangle) portableImage.getShape("Header");
        double headerWidth = Space.PIXEL_PER_MILLIMETER * A;
        header.setWidth(headerWidth);

        // Update Contact Positions for Header
        for (int i = 0; i < extension.getComponent(Portable.class).getPorts().size(); i++) {
            double x = Space.PIXEL_PER_MILLIMETER * ((contactOffset + i * contactSeparation) - (A / 2.0));
            if (i < extension.getComponent(Portable.class).headerContactPositions.size()) {
                extension.getComponent(Portable.class).headerContactPositions.get(i).getImagePosition().x = x;
            } else {
                Point point = new Point(new Transform(x, 107));
                extension.getComponent(Portable.class).headerContactPositions.add(point);
                portableImage.addShape(point);
            }
        }
    }

    private void updateExtensionStyle(Entity extension) {
        updateExtensionPortStyle(extension);
    }

    private void updateExtensionPortStyle(Entity extension) {
        // Update Port style
        for (int i = 0; i < extension.getComponent(Portable.class).getPorts().size(); i++) {
            Entity portEntity = extension.getComponent(Portable.class).getPorts().get(i);
            Shape portShape = extension.getComponent(Image.class).getShape(portEntity);

            // Update color of Port shape based on type
            if (portShape != null) {
                portShape.setColor(Color.getColor(portEntity.getComponent(Port.class).getType()));
            }
        }
    }

    private void updateExtensionPathRoutes(Entity extension) {
        // TODO: Create routes between extension and host.
    }
    // </EXTENSION>


    // <PORT>
    public void updatePortImage(Entity port) {

        // Update color of Port shape based on its type
        Port.Type portType = port.getComponent(Port.class).getType();
        String portColor = camp.computer.clay.util.Color.getColor(portType);
        port.getComponent(Image.class).getShape("Port").setColor(portColor);

        // Call this so PortableEntity.updateImage() will be called to updateImage Geometry
        updatePortImageGeometry(port);
    }

    public void updatePortImageGeometry(Entity entity) {

        Image image = entity.getComponent(Image.class);

        // Update Shapes
        for (int i = 0; i < image.getShapes().size(); i++) {
            Shape shape = image.getShapes().get(i);

            // <HACK>
            Transform imagePosition = image.getEntity().getComponent(Transform.class);
            Transform referencePoint = image.getEntity().getParent().getComponent(Transform.class);
            Transform transformedPoint = new Transform();
            transformedPoint.x = referencePoint.x + Geometry.distance(0, 0, imagePosition.x, imagePosition.y) * Math.cos(Math.toRadians(referencePoint.rotation + Geometry.getAngle(0, 0, imagePosition.x, imagePosition.y)));
            transformedPoint.y = referencePoint.y + Geometry.distance(0, 0, imagePosition.x, imagePosition.y) * Math.sin(Math.toRadians(referencePoint.rotation + Geometry.getAngle(0, 0, imagePosition.x, imagePosition.y)));
            // </HACK>

            shape.update(transformedPoint);
        }
    }
    // </PORT>
}
