package camp.computer.clay.space.image;

import android.graphics.Color;
import android.graphics.Paint;

import camp.computer.clay.application.graphics.Display;
import camp.computer.clay.model.Path;
import camp.computer.clay.model.Port;
import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.ActionListener;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.geometry.Point;
import camp.computer.clay.util.image.Image;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Visibility;

public class PathImage extends Image<Path> {

    private Visibility dockVisibility = new Visibility(Visibility.Value.VISIBLE);

    private double triangleWidth = 20;
    private double triangleHeight = triangleWidth * (Math.sqrt(3.0) / 2);
    private double triangleSpacing = 35;

    public PathImage(Path path) {
        super(path);
        setup();
    }

    private void setup() {
        setupActions();
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

    public void update() {
    }

    public void setDockVisibility(Visibility.Value visibility) {
        this.dockVisibility.setValue(visibility);
    }

    public Visibility getDockVisibility() {
        return this.dockVisibility;
    }

    public boolean isDockVisible() {
        return this.dockVisibility.getValue() == Visibility.Value.VISIBLE;
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
    }

    public void drawTrianglePath(Display display) {

        Paint paint = display.getPaint();

        Path path = getPath();

        Shape sourcePortShape = getSpace().getShape(path.getSource());
        Shape targetPortShape = getSpace().getShape(path.getTarget());

        // Show target port
        targetPortShape.setVisibility(Visibility.Value.VISIBLE);
        //// TODO: targetPortShape.setPathVisibility(Visibility.VISIBLE);

        // Color
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15.0f);
        paint.setColor(Color.parseColor(sourcePortShape.getColor()));

        double pathRotation = Geometry.calculateRotationAngle(sourcePortShape.getPosition(), targetPortShape.getPosition());
        double triangleRotation = pathRotation + 90.0f;
        Point sourcePoint = Geometry.calculatePoint(sourcePortShape.getPosition(), pathRotation, 2 * triangleSpacing);
        Point targetPoint = Geometry.calculatePoint(targetPortShape.getPosition(), pathRotation + 180, 2 * triangleSpacing);

        if (dockVisibility.getValue() == Visibility.Value.VISIBLE) {

            paint.setStyle(Paint.Style.FILL);
            Display.drawTriangle(sourcePoint, triangleRotation, triangleWidth, triangleHeight, display);

            paint.setStyle(Paint.Style.FILL);
            Display.drawTriangle(targetPoint, triangleRotation, triangleWidth, triangleHeight, display);

        } else {

            Display.drawTrianglePath(sourcePoint, targetPoint, triangleWidth, triangleHeight, display);
        }
    }

    private void drawLinePath(Display display) {

        Paint paint = display.getPaint();

        Path path = getPath();
        Shape sourcePortShape = getSpace().getShape(path.getSource());
        Shape targetPortShape = getSpace().getShape(path.getTarget());

        if (sourcePortShape != null && targetPortShape != null) {

            // Show target port
            targetPortShape.setVisibility(Visibility.Value.VISIBLE);
            //// TODO: targetPortShape.setPathVisibility(Visibility.VISIBLE);

            // Color
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(15.0f);
            paint.setColor(Color.parseColor(sourcePortShape.getColor()));

            double pathRotationAngle = Geometry.calculateRotationAngle(sourcePortShape.getPosition(), targetPortShape.getPosition());
            Point pathStartCoordinate = Geometry.calculatePoint(sourcePortShape.getPosition(), pathRotationAngle, 0);
            Point pathStopCoordinate = Geometry.calculatePoint(targetPortShape.getPosition(), pathRotationAngle + 180, 0);

            Display.drawLine(pathStartCoordinate, pathStopCoordinate, display);
        }

    }

    private void drawPhysicalPath(Display display) {

        Path path = getPath();

        // Get Host and Extension Ports
        Port hostPort = path.getSource();
        Port extensionPort = path.getTarget();

        // Draw the connection to the Host's Port

        PortableImage hostImage = (PortableImage) space.getImage(hostPort.getPortable());
        PortableImage extensionImage = (PortableImage) space.getImage(extensionPort.getPortable());

        Point hostConnectorPosition = hostImage.portConnectorPositions.get(hostPort.getNumber() - 1);
        Point extensionConnectorPosition = extensionImage.portConnectorPositions.get(extensionPort.getNumber() - 1);

        // Draw connection between Ports
        display.getPaint().setColor(android.graphics.Color.parseColor(camp.computer.clay.util.Color.getColor(extensionPort.getType())));
        display.getPaint().setStrokeWidth(15.0f);
        Display.drawLine(hostConnectorPosition, extensionConnectorPosition, display);
    }
}
