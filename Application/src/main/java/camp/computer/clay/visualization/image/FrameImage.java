package camp.computer.clay.visualization.image;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.Surface;
import camp.computer.clay.model.architecture.Frame;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interactivity.Action;
import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.architecture.Visualization;
import camp.computer.clay.visualization.util.Visibility;
import camp.computer.clay.visualization.util.geometry.Geometry;
import camp.computer.clay.visualization.util.geometry.Point;
import camp.computer.clay.visualization.util.geometry.Rectangle;
import camp.computer.clay.visualization.util.geometry.Shape;

public class FrameImage extends Image {

    // TODO: Replace these with dynamic counts.
    final static int PORT_GROUP_COUNT = 4;
    final static int PORT_COUNT = 12;

    // <STYLE>
    // TODO: Make these private once the map is working well and the sprite is working well.

    // Shapes
    private Rectangle boardShape = new Rectangle(250, 250);

    // Color, Transparency
    private String colorString = "f7f7f7"; // "404040"; // "414141";
    private int color = Color.parseColor("#ff" + colorString); // Color.parseColor("#212121");
    private boolean outlineVisibility = true;
    private String outlineColorString = "414141";
    private int outlineColor = Color.parseColor("#ff" + outlineColorString); // Color.parseColor("#737272");
    private double outlineThickness = 3.0;

    private double portGroupWidth = 50;
    private double portGroupHeight = 13;
    private String portGroupColorString = "3b3b3b";
    private int portGroupColor = Color.parseColor("#ff" + portGroupColorString);
    private boolean showPortGroupOutline = false;
    private String portGroupOutlineColorString = "000000";
    private int portGroupOutlineColor = Color.parseColor("#ff" + portGroupOutlineColorString);
    private double portGroupOutlineThickness = outlineThickness;

    private double distanceLightsToEdge = 12.0f;
    private double lightWidth = 12;
    private double lightHeight = 20;
    private boolean showLightOutline = true;
    private double lightOutlineThickness = 1.0f;
    private int lightOutlineColor = Color.parseColor("#e7e7e7");
    // </STYLE>

    public FrameImage(Frame frame) {
        super(frame);

        // Create shapes for image
        boardShape = new Rectangle(250, 250);

        addShape(boardShape);

    }

    public void setupPortImages() {

        // Add a port sprite for each of the associated base's ports
        for (Port port : getFrame().getPorts()) {
            PortImage portImage = new PortImage(port);
            portImage.setVisualization(getVisualization());
            getVisualization().addImage(port, portImage, "ports");
        }
    }

    public Frame getFrame() {
        return (Frame) getModel();
    }

    public List<PortImage> getPortImages() {
        List<PortImage> portImages = new ArrayList<>();

        for (Port port : getFrame().getPorts()) {
            PortImage portImage = (PortImage) getVisualization().getImage(port);
            portImages.add(portImage);
        }

        return portImages;
    }

    public PortImage getPortImage(int index) {
        Frame frame = getFrame();
        PortImage portImage = (PortImage) getVisualization().getImage(frame.getPort(index));
        return portImage;
    }

    // TODO: Remove this! Store Port index/id
    public int getPortImageIndex(PortImage portImage) {
        Port port = (Port) getVisualization().getModel(portImage);
        if (getFrame().getPorts().contains(port)) {
            return this.getFrame().getPorts().indexOf(port);
        }
        return -1;
    }

    public void update() {

        String transparencyString = String.format("%02x", (int) currentTransparency * 255);

        // Frame color
        color = Color.parseColor("#" + transparencyString + colorString);
        outlineColor = Color.parseColor("#" + transparencyString + outlineColorString);

        // Header color
        portGroupColor = Color.parseColor("#" + transparencyString + portGroupColorString);
        portGroupOutlineColor = Color.parseColor("#" + transparencyString + portGroupOutlineColorString);

        updateLightImages();
        updatePortGroupImages();
    }

    public void draw(Surface surface) {
        if (isVisible()) {
            drawPortGroupImages(surface);
            drawBoardImage(surface);
            drawLightImages(surface);

            if (Application.ENABLE_GEOMETRY_ANNOTATIONS) {
                surface.getPaint().setColor(Color.GREEN);
                surface.getPaint().setStyle(Paint.Style.STROKE);
                Surface.drawCircle(getPosition(), boardShape.getWidth(), 0, surface);
                Surface.drawCircle(getPosition(), boardShape.getWidth() / 2.0f, 0, surface);
            }
        }
    }

    private void drawBoardImage(Surface surface) {

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        // Color
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(this.color);
        Surface.drawRectangle(getPosition(), getRotation(), boardShape.getWidth(), boardShape.getHeight(), surface);

        // Outline
        if (this.outlineVisibility) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(this.outlineColor);
            paint.setStrokeWidth((float) outlineThickness);
            Surface.drawRectangle(getPosition(), getRotation(), boardShape.getWidth(), boardShape.getHeight(), surface);
        }
    }

    Point[] portGroupCenterPositions = new Point[PORT_GROUP_COUNT];

    private void updatePortGroupImages() {
        // <SHAPE>
        // Positions before rotation
        portGroupCenterPositions[0] = new Point(
                getPosition().getX() + 0,
                getPosition().getY() + ((boardShape.getHeight() / 2.0f) + (portGroupHeight / 2.0f))
        );
        portGroupCenterPositions[1] = new Point(
                getPosition().getX() + ((boardShape.getWidth() / 2.0f) + (portGroupHeight / 2.0f)),
                getPosition().getY() + 0
        );
        portGroupCenterPositions[2] = new Point(
                getPosition().getX() + 0,
                getPosition().getY() - ((boardShape.getHeight() / 2.0f) + (portGroupHeight / 2.0f))
        );
        portGroupCenterPositions[3] = new Point(
                getPosition().getX() - ((boardShape.getWidth() / 2.0f) + (portGroupHeight / 2.0f)),
                getPosition().getY() + 0
        );
        // </SHAPE>

        for (int i = 0; i < PORT_GROUP_COUNT; i++) {

            // Calculate rotated position
            portGroupCenterPositions[i] = Geometry.calculateRotatedPoint(getPosition(), getRotation() + (((i - 1) * 90) - 90) + ((i - 1) * 90), portGroupCenterPositions[i]);
        }
    }

    private void drawPortGroupImages(Surface surface) {

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        for (int i = 0; i < PORT_GROUP_COUNT; i++) {

            // Color
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(this.portGroupColor);
            Surface.drawRectangle(portGroupCenterPositions[i], getRotation() + ((i * 90) + 90), portGroupWidth, portGroupHeight, surface);

            // Outline
            if (this.showPortGroupOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth((float) portGroupOutlineThickness);
                paint.setColor(this.portGroupOutlineColor);
                Surface.drawRectangle(portGroupCenterPositions[i], getRotation(), portGroupWidth, portGroupHeight, surface);
            }

        }
    }

    Point[] lightCenterPositions = new Point[PORT_COUNT];
    double[] lightRotationAngle = new double[12];

    private void updateLightImages() {
        // <SHAPE>
        lightCenterPositions[0] = new Point(
                getPosition().getX() + (-20),
                getPosition().getY() + ((boardShape.getHeight() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );
        lightCenterPositions[1] = new Point(
                getPosition().getX() + (0),
                getPosition().getY() + ((boardShape.getHeight() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );
        lightCenterPositions[2] = new Point(
                getPosition().getX() + (+20),
                getPosition().getY() + ((boardShape.getHeight() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );

        lightCenterPositions[3] = new Point(
                getPosition().getX() + ((boardShape.getWidth() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().getY() + (+20)
        );
        lightCenterPositions[4] = new Point(
                getPosition().getX() + ((boardShape.getWidth() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().getY() + (0)
        );
        lightCenterPositions[5] = new Point(
                getPosition().getX() + ((boardShape.getWidth() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().getY() + (-20)
        );

        lightCenterPositions[6] = new Point(
                getPosition().getX() + (+20),
                getPosition().getY() - ((boardShape.getHeight() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );
        lightCenterPositions[7] = new Point(
                getPosition().getX() + (0),
                getPosition().getY() - ((boardShape.getHeight() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );
        lightCenterPositions[8] = new Point(
                getPosition().getX() + (-20),
                getPosition().getY() - ((boardShape.getHeight() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );

        lightCenterPositions[9] = new Point(
                getPosition().getX() - ((boardShape.getWidth() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().getY() + (-20)
        );
        lightCenterPositions[10] = new Point(
                getPosition().getX() - ((boardShape.getWidth() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().getY() + (0)
        );
        lightCenterPositions[11] = new Point(
                getPosition().getX() - ((boardShape.getWidth() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().getY() + (+20)
        );

        lightRotationAngle[0] = 0;
        lightRotationAngle[1] = 0;
        lightRotationAngle[2] = 0;
        lightRotationAngle[3] = 90;
        lightRotationAngle[4] = 90;
        lightRotationAngle[5] = 90;
        lightRotationAngle[6] = 180;
        lightRotationAngle[7] = 180;
        lightRotationAngle[8] = 180;
        lightRotationAngle[9] = 270;
        lightRotationAngle[10] = 270;
        lightRotationAngle[11] = 270;
        // </SHAPE>

        // Calculate rotated position
        for (int i = 0; i < PORT_COUNT; i++) {
            lightCenterPositions[i] = Geometry.calculateRotatedPoint(getPosition(), getRotation(), lightCenterPositions[i]);
        }
    }

    private void drawLightImages(Surface surface) {

        Canvas canvas = surface.getCanvas();
        Paint paint = surface.getPaint();

        for (int i = 0; i < PORT_COUNT; i++) {

            // Color
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(3);
            Port port = (Port) getFrame().getPort(i);
            if (port.getType() != Port.Type.NONE) {
                paint.setColor(camp.computer.clay.visualization.util.Color.setTransparency(this.getPortImage(i).getUniqueColor(), (float) currentTransparency));
            } else {
                paint.setColor(camp.computer.clay.visualization.util.Color.setTransparency(PortImage.FLOW_PATH_COLOR_NONE, (float) currentTransparency));
            }
            Surface.drawRectangle(lightCenterPositions[i], getRotation() + lightRotationAngle[i], lightWidth, lightHeight, surface);

            // Outline
            if (this.showLightOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth((float) lightOutlineThickness);
                paint.setColor(this.lightOutlineColor);
                Surface.drawRectangle(lightCenterPositions[i], getRotation() + lightRotationAngle[i], lightWidth, lightHeight, surface);
            }
        }
    }

    public void showPortImages() {
        for (PortImage portImage : getPortImages()) {
            portImage.setVisibility(Visibility.VISIBLE);
            portImage.showDocks();
        }
    }

    public void hidePortImages() {
        for (PortImage portImage : getPortImages()) {
            portImage.setVisibility(Visibility.INVISIBLE);
        }
    }

    public void showPathImages() {
        for (PortImage portImage : getPortImages()) {
            portImage.setPathVisibility(Visibility.VISIBLE);
        }
    }

    public void hidePathImages() {
        for (PortImage portImage : getPortImages()) {
            portImage.setPathVisibility(Visibility.INVISIBLE);
            portImage.showDocks();
        }
    }

    //-------------------------
    // Action
    //-------------------------

    public boolean containsPoint(Point point) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getPosition().getX(), (int) this.getPosition().getY(), point.getX(), point.getY()) < (this.boardShape.getHeight() / 2.0f);
        } else {
            return false;
        }
    }

    public boolean containsPoint(Point point, double padding) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getPosition().getX(), (int) this.getPosition().getY(), point.getX(), point.getY()) < (this.boardShape.getHeight() / 2.0f + padding);
        } else {
            return false;
        }
    }

    @Override
    public void onAction(Action action) {

        if (action.getType() == Action.Type.NONE) {

        } else if (action.getType() == Action.Type.TOUCH) {

        } else if (action.getType() == Action.Type.TAP) {

            // Focus on touched form
            showPortImages();
            showPathImages();

            setTransparency(1.0);

            // TODO: Speak "choose a channel to get data."

            // Show ports and paths of touched form
            for (PortImage portImage : getPortImages()) {
                List<Path> paths = portImage.getPort().getGraph();
                Log.v("TouchFrame", "\tpaths.size = " + paths.size());
                for (Path path : paths) {
                    Log.v("TouchFrame", "\t\tsource = " + path.getSource());
                    Log.v("TouchFrame", "\t\ttarget = " + path.getTarget());
                    // Show ports
                    getVisualization().getImage(path.getSource()).setVisibility(Visibility.VISIBLE);
                    getVisualization().getImage(path.getTarget()).setVisibility(Visibility.VISIBLE);
                    // Show path
                    getVisualization().getImage(path).setVisibility(Visibility.VISIBLE);
                }
            }

        } else if (action.getType() == Action.Type.HOLD) {

        } else if (action.getType() == Action.Type.MOVE) {

        } else if (action.getType() == Action.Type.DRAG) {

        } else if (action.getType() == Action.Type.RELEASE) {

        }
    }
}

