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
import camp.computer.clay.util.geometry.Circle;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.geometry.Rectangle;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Space;
import camp.computer.clay.util.image.Visibility;

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
                updateExtensionImage(entity); // entity.getComponent(Extension.class).updateImage();
            } else if (entity.hasComponent(Host.class)) {
                updateHostImage(entity); // entity.getComponent(Host.class).updateImage();
            } else if (entity.hasComponent(Port.class)) {
                updatePortImage(entity);
            } else if (entity.hasComponent(Camera.class)) {
                entity.getComponent(Camera.class).update();
            }
            // </HACK>
        }
    }
    // </ENTITY>



    // <IMAGE>

    // Previously: Image.update()
    public void updateImage(Entity entity) {
        updateImageGeometry(entity);
    }

    // Previously: Image.updateGeometry()
    // Required Components: Image, Transform
    public void updateImageGeometry(Entity entity) {

        Image image = entity.getComponent(Image.class);

        // Update Shapes
        for (int i = 0; i < image.getShapes().size(); i++) {
            Shape shape = image.getShapes().get(i);

//            // <HACK>
//            if (getEntity().hasComponent(Port.class)) {
//                getEntity().getComponent(Transform.class).set(getEntity().getParent().getComponent(Transform.class));
//            }
//            // </HACK>

            // Update the Shape
            shape.update(image.getEntity().getComponent(Transform.class));
        }
    }
    // </IMAGE>



    // <HOST>
    public void updateHostImage(Entity host) {

        Group<Shape> lightShapeGroup = null;

        // Get LED shapes
        if (lightShapeGroup == null) {
            lightShapeGroup = host.getComponent(Image.class).getShapes().filterLabel("^LED (1[0-2]|[1-9])$");
        }

        // Update Port LED color
        for (int i = 0; i < host.getComponent(Portable.class).getPorts().size(); i++) {

            // Update color of LED based on corresponding Port's type
            Entity port = host.getComponent(Portable.class).getPorts().get(i);
            String portColor = camp.computer.clay.util.Color.getColor(port.getComponent(Port.class).getType());
            lightShapeGroup.get(i).setColor(portColor);
        }

        // Call this so PortableEntity.updateImage() will be called to updateImage Geometry
//        host.getComponent(Image.class).updateImage();
        updateImage(host);
    }
    // </

    // <EXTENSION>
    private void updateExtensionImage(Entity extension) {

        // Create additional Images or Shapes to match the corresponding Entity
        updateExtensionGeometry(extension);
        updateExtensionStyle(extension);

        // <HACK>
        updateExtensionPathRoutes(extension);
        // </HACK>

        // Call this so PortableImage.updateImage() is called and Geometry is updated!
//        extension.getComponent(Image.class).updateImage();
        updateImage(extension);
    }

    /**
     * Update the {@code Image} to match the state of the corresponding {@code Entity}.
     */
    public void updateExtensionGeometry(Entity extension) {

        updatePortGeometry(extension);
        updateHeaderGeometry(extension);

        // TODO: Clean up/delete images/shapes for any removed portEntities...
    }

    /**
     * Add or remove {@code Shape}'s for each of the {@code ExtensionEntity}'s {@code PortEntity}s.
     */
    private void updatePortGeometry(Entity extension) {

        Image image = extension.getComponent(Image.class);

        // Remove PortEntity shapes from the Image that do not have a corresponding PortEntity in the Entity
        Group<Shape> portShapes = image.getShapes(Port.class);
        for (int i = 0; i < portShapes.size(); i++) {
            Shape portShape = portShapes.get(i);
            if (!extension.getComponent(Portable.class).getPorts().contains(portShape.getEntity())) {
                portShapes.remove(portShape);
                image.invalidate();
            }
        }

        // Create PortEntity shapes for each of ExtensionEntity's Ports if they don't already exist
        for (int i = 0; i < extension.getComponent(Portable.class).getPorts().size(); i++) {
            Entity portEntity = extension.getComponent(Portable.class).getPorts().get(i);

            if (image.getShape(portEntity) == null) {

                // Ports
                Circle<Entity> circle = new Circle<>(portEntity);
                circle.setRadius(50);
                circle.setLabel("Port " + (extension.getComponent(Portable.class).getPorts().size() + 1));
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
        for (int i = 0; i < extension.getComponent(Portable.class).getPorts().size(); i++) {
            Entity portEntity = extension.getComponent(Portable.class).getPorts().get(i);
            Circle portShape = (Circle) image.getShape(portEntity);

            if (portShape != null) {
                double portSpacing = 100;
                portShape.getImagePosition().x = (i * portSpacing) - (((extension.getComponent(Portable.class).getPorts().size() - 1) * portSpacing) / 2.0);
                // TODO: Also updateImage y coordinate
            }
        }
    }

    private void updateHeaderGeometry(Entity extension) {

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
        // Update PortEntity style
        for (int i = 0; i < extension.getComponent(Portable.class).getPorts().size(); i++) {
            Entity portEntity = extension.getComponent(Portable.class).getPorts().get(i);
            Shape portShape = extension.getComponent(Image.class).getShape(portEntity);

            // Update color of PortEntity shape based on type
            if (portShape != null) {
                portShape.setColor(Color.getColor(portEntity.getComponent(Port.class).getType()));
            }
        }
    }

    private void updateExtensionPathRoutes(Entity extension) {

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
    // </EXTENSION>



    // <PORT>
    public void updatePortImage(Entity port) {

//        Group<Shape> lightShapeGroup = null;
//
//        // Get LED shapes
//        if (lightShapeGroup == null) {
//            lightShapeGroup = port.getComponent(Image.class).getShapes().filterLabel("^LED (1[0-2]|[1-9])$");
//        }
//
//        // Update PortEntity and LED shape styles
//        for (int i = 0; i < port.getComponent(Portable.class).getPorts().size(); i++) {
//            Entity portEntity = port.getComponent(Portable.class).getPorts().get(i);
//            /*
//            Shape portShape = hostEntity.getComponent(Image.class).getShape(portEntity.getLabel()); // Shape portShape = getShape(portEntity);
//
//            // Update color of PortEntity shape based on type
//            portShape.setColor(camp.computer.clay.util.Color.getColor(portEntity.getComponent(Port.class).getType()));
//
//            // Update color of LED based on corresponding PortEntity's type
//            lightShapeGroup.get(i).setColor(portShape.getColor());
//            */
//        }

        // Update color of PortEntity shape based on type
        port.getComponent(Image.class).getShape("Port").setColor(camp.computer.clay.util.Color.getColor(port.getComponent(Port.class).getType()));

        // Update color of LED based on corresponding PortEntity's type
//        lightShapeGroup.get(i).setColor(portShape.getColor());

        // Call this so PortableEntity.updateImage() will be called to updateImage Geometry
//        port.getComponent(Image.class).updateImage();
//        updateImage(port);
        updatePortImageGeometry(port);
    }

    public void updatePortImageGeometry(Entity entity) {

        Image image = entity.getComponent(Image.class);

        // Update Shapes
        for (int i = 0; i < image.getShapes().size(); i++) {
            Shape shape = image.getShapes().get(i);

//            // <HACK>
//            if (getEntity().hasComponent(Port.class)) {
//                getEntity().getComponent(Transform.class).set(getEntity().getParent().getComponent(Transform.class));
//            }
//            // </HACK>

            // Update the Shape

//            image.getEntity().getComponent(Transform.class).x += image.getEntity().getParent().getComponent(Transform.class).x;
//            image.getEntity().getComponent(Transform.class).y += image.getEntity().getParent().getComponent(Transform.class).y;
            Transform imagePosition = image.getEntity().getComponent(Transform.class);
            Transform referencePoint = image.getEntity().getParent().getComponent(Transform.class);
            Transform transformedPoint  = new Transform();
            transformedPoint.x = referencePoint.x + Geometry.distance(0, 0, imagePosition.x, imagePosition.y) * Math.cos(Math.toRadians(referencePoint.rotation + Geometry.getAngle(0, 0, imagePosition.x, imagePosition.y)));
            transformedPoint.y = referencePoint.y + Geometry.distance(0, 0, imagePosition.x, imagePosition.y) * Math.sin(Math.toRadians(referencePoint.rotation + Geometry.getAngle(0, 0, imagePosition.x, imagePosition.y)));

            shape.update(transformedPoint);

            //shape.update(image.getEntity().getComponent(Transform.class));

//            shape.update(image.getEntity().getParent().getComponent(Transform.class)); // Note the call to getParent() here!
        }
    }

//    public void transformPosition(Transform point, Transform referencePoint) {
//        point.x = referencePoint.x + Geometry.distance(0, 0, imagePosition.x, imagePosition.y) * Math.cos(Math.toRadians(referencePoint.rotation + Geometry.getAngle(0, 0, imagePosition.x, imagePosition.y)));
//        point.y = referencePoint.y + Geometry.distance(0, 0, imagePosition.x, imagePosition.y) * Math.sin(Math.toRadians(referencePoint.rotation + Geometry.getAngle(0, 0, imagePosition.x, imagePosition.y)));
//    }
//
//    public void transformRotation(Transform referencePoint) {
//        this.position.rotation = referencePoint.rotation + imagePosition.rotation;
//    }
    // </PORT>
}
