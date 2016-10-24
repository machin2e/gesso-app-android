package camp.computer.clay.space.image;

import android.graphics.Color;
import android.graphics.Paint;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.engine.entity.Path;
import camp.computer.clay.engine.entity.Port;
import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.ActionListener;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Segment;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Space;
import camp.computer.clay.util.image.Visibility;

public class PathImage extends Image<Path> {

    // A single PathImage is created to represent all Paths between a Host and an Extension.

//    private Visibility dockVisibility = Visibility.VISIBLE;

    private double triangleWidth = 20;
    private double triangleHeight = triangleWidth * (Math.sqrt(3.0) / 2);
    private double triangleSpacing = 35;

    public PathImage(Path path) {
        super(path);
        setup();
    }

    private void setup() {
        setupGeometry();
        setupActions();
        layerIndex = -10;
    }

    private void setupGeometry() {
        Segment segment;

        // Board
        segment = new Segment<>();
        segment.setOutlineThickness(2.0);
        segment.setLabel("Path");
        segment.setColor("#1f1f1e"); // #f7f7f7
        segment.setOutlineThickness(1);
        addShape(segment);
    }

    private void setupActions() {
        setOnActionListener(new ActionListener() {
            @Override
            public void onAction(Action action) {

                Event event = action.getLastEvent();

                if (event.getType() == Event.Type.NONE) {

                } else if (event.getType() == Event.Type.SELECT) {

                } else if (event.getType() == Event.Type.HOLD) {

                } else if (event.getType() == Event.Type.MOVE) {

                } else if (event.getType() == Event.Type.UNSELECT) {

                }
            }
        });
    }

    public Path getPath() {
        return getEntity();
    }

    public void draw(Display display) {

        if (isVisible()) {
            Path path = getPath();
            if (path.getType() == Path.Type.MESH) {
                // Draw Path between Ports
                drawTrianglePath(display);
            } else if (path.getType() == Path.Type.ELECTRONIC) {
                drawLinePath(display);
            }
        } else {
            Path path = getPath();
            if (path.getType() == Path.Type.ELECTRONIC) {
                drawPhysicalPath(display);
            }
        }

        /*
        // Bounding Box
        display.paint.setColor(Color.RED);
        display.paint.setStrokeWidth(2.0f);
        display.paint.setStyle(Paint.Style.STROKE);
        Rectangle boundingBox = getBoundingBox();
        Log.v("PathImage", "x: " + boundingBox.getPosition().x + ", y: " + boundingBox.getPosition().y + ", rot: " + boundingBox.getRotation() + ", width: " + boundingBox.getWidth() + ", height: " + boundingBox.getHeight());
        display.drawRectangle(boundingBox);

        // Center Transform
        display.canvas.drawCircle(0, 0, 5, display.paint);
        */
    }

//    public void setDockVisibility(Visibility visibility) {
//        this.dockVisibility = visibility;
//    }

//    public Visibility getDockVisibility() {
//        return this.dockVisibility;
//    }

//    public boolean isDockVisible() {
//        return this.dockVisibility == Visibility.VISIBLE;
//    }

    public void drawTrianglePath(Display display) {

        Paint paint = display.paint;

        Path path = getPath();

        Shape sourcePortShape = Space.getSpace().getShape(path.getSource());
        Shape targetPortShape = Space.getSpace().getShape(path.getTarget());

        // Show target port
        targetPortShape.setVisibility(Visibility.VISIBLE);
        //// TODO: targetPortShape.setPathVisibility(Visibility.VISIBLE);

        // Color
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15.0f);
        paint.setColor(Color.parseColor(sourcePortShape.getColor()));

        double pathRotation = Geometry.getAngle(sourcePortShape.getPosition(), targetPortShape.getPosition());
        double triangleRotation = pathRotation + 90.0f;
        Transform sourcePoint = Geometry.getRotateTranslatePoint(sourcePortShape.getPosition(), pathRotation, 2 * triangleSpacing);
        Transform targetPoint = Geometry.getRotateTranslatePoint(targetPortShape.getPosition(), pathRotation + 180, 2 * triangleSpacing);

//        if (dockVisibility == Visibility.VISIBLE) {
//
//            paint.setStyle(Paint.Style.FILL);
//            display.drawTriangle(sourcePoint, triangleRotation, triangleWidth, triangleHeight);
//
//            paint.setStyle(Paint.Style.FILL);
//            display.drawTriangle(targetPoint, triangleRotation, triangleWidth, triangleHeight);
//
//        } else {

            display.drawTrianglePath(sourcePoint, targetPoint, triangleWidth, triangleHeight);
//        }
    }

    private void drawLinePath(Display display) {

        Paint paint = display.paint;

        Path path = getPath();
        Shape sourcePortShape = Space.getSpace().getShape(path.getSource());
        Shape targetPortShape = Space.getSpace().getShape(path.getTarget());

        if (sourcePortShape != null && targetPortShape != null) {

            // Show target port
            targetPortShape.setVisibility(Visibility.VISIBLE);
            //// TODO: targetPortShape.setPathVisibility(Visibility.VISIBLE);

            // Color
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(15.0f);
            paint.setColor(Color.parseColor(sourcePortShape.getColor()));

            double pathRotationAngle = Geometry.getAngle(sourcePortShape.getPosition(), targetPortShape.getPosition());
            Transform pathStartCoordinate = Geometry.getRotateTranslatePoint(sourcePortShape.getPosition(), pathRotationAngle, 0);
            Transform pathStopCoordinate = Geometry.getRotateTranslatePoint(targetPortShape.getPosition(), pathRotationAngle + 180, 0);

//            display.drawSegment(pathStartCoordinate, pathStopCoordinate);

            // TODO: Create Segment and add it to the PathImage. Update its geometry to change position, rotation, etc.
//            double pathRotation = getSpace().getImages(getPath().getHosts()).getRotation();

            Segment segment = (Segment) getShape("Path");
            segment.setOutlineThickness(15.0);
            segment.setOutlineColor(sourcePortShape.getColor());

            segment.setSource(pathStartCoordinate);
            segment.setTarget(pathStopCoordinate);

            display.drawSegment(segment);
        }
    }

    private void drawPhysicalPath(Display display) {

        Path path = getPath();

        // Get Host and Extension Ports
        Port hostPort = path.getSource();
        Port extensionPort = path.getTarget();

        // Draw the connection to the Host's Port

        PortableImage hostImage = (PortableImage) hostPort.getPortable().getComponent(Image.class);
        PortableImage extensionImage = (PortableImage) extensionPort.getPortable().getComponent(Image.class);

        if (hostImage.headerContactPositions.size() > hostPort.getIndex() && extensionImage.headerContactPositions.size() > extensionPort.getIndex()) {
            Transform hostConnectorPosition = hostImage.headerContactPositions.get(hostPort.getIndex()).getPosition();
            Transform extensionConnectorPosition = extensionImage.headerContactPositions.get(extensionPort.getIndex()).getPosition();

            // Draw connection between Ports
            display.paint.setColor(android.graphics.Color.parseColor(camp.computer.clay.util.Color.getColor(extensionPort.getType())));
            display.paint.setStrokeWidth(10.0f);
//            display.drawSegment(hostConnectorPosition, extensionConnectorPosition);

//            Polyline polyline = new Polyline();
//            polyline.addVertex(hostConnectorPosition);
//            polyline.addVertex(extensionConnectorPosition);
//            display.drawPolyline(polyline);

            // TODO: Create Segment and add it to the PathImage. Update its geometry to change position, rotation, etc.
            Segment segment = (Segment) getShape("Path");
            segment.setOutlineThickness(10.0);
            segment.setOutlineColor(camp.computer.clay.util.Color.getColor(extensionPort.getType()));

            segment.setSource(hostConnectorPosition);
            segment.setTarget(extensionConnectorPosition);

            display.drawSegment(segment);
        }
    }
}
