package camp.computer.clay.visualization.image;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.VisualizationSurface;
import camp.computer.clay.model.architecture.Device;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.architecture.Port;
import camp.computer.clay.model.interactivity.Impression;
import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.Point;
import camp.computer.clay.visualization.util.Rectangle;
import camp.computer.clay.visualization.util.Shape;

public class DeviceImage extends Image {

    public final static String TYPE = "peripheral";

    // TODO: Replace these with dynamic counts.
    final static int PORT_GROUP_COUNT = 4;
    final static int PORT_COUNT = 3;

    // <STYLE>
    // TODO: Make these private once the map is working well and the sprite is working well.

    // Shapes
    private Rectangle shape = new Rectangle(200, 200);

    // Color, Transparency
    private String colorString = "f7f7f7"; // "404040"; // "414141";
    private int color = Color.parseColor("#ff" + colorString); // Color.parseColor("#212121");
    private boolean outlineVisibility = true;
    private String outlineColorString = "414141";
    private int outlineColor = Color.parseColor("#ff" + outlineColorString); // Color.parseColor("#737272");
    private double outlineThickness = 3.0;
    private double targetTransparency = 1.0;
    private double currentTransparency = targetTransparency;

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

    public DeviceImage(Device device) {
        super(device);
        setType(TYPE);
    }

    public Device getPeripheral() {
        return (Device) getModel();
    }

    public List<PortImage> getPortImages() {
        List<PortImage> portImages = new ArrayList<>();
        Device device = getPeripheral();

        for (Port port : device.getPorts()) {
            PortImage portImage = (PortImage) getVisualization().getImage(port);
            portImages.add(portImage);
        }

        return portImages;
    }

    public PortImage getPortImage(int index) {
        Device device = getPeripheral();
        PortImage portImage = (PortImage) getVisualization().getImage(device.getPort(index));
        return portImage;
    }

    // TODO: Remove this! Store Port index/id
    public int getPortImageIndex(PortImage portImage) {
        Port port = (Port) getVisualization().getModel(portImage);
        if (getPeripheral().getPorts().contains(port)) {
            return this.getPeripheral().getPorts().indexOf(port);
        }
        return -1;
    }

    public void update() {
//        updateLightImages();
//        updatePortGroupImages();
    }

    public void draw(VisualizationSurface visualizationSurface) {
        if (isVisible()) {
//            drawPortGroupImages(visualizationSurface);
            drawBoardImage(visualizationSurface);
//            drawLightImages(visualizationSurface);

            if (Application.ENABLE_GEOMETRY_ANNOTATIONS) {
                visualizationSurface.getPaint().setColor(Color.GREEN);
                visualizationSurface.getPaint().setStyle(Paint.Style.STROKE);
                Shape.drawCircle(getPosition(), shape.getWidth(), 0, visualizationSurface.getCanvas(), visualizationSurface.getPaint());
                Shape.drawCircle(getPosition(), shape.getWidth() / 2.0f, 0, visualizationSurface.getCanvas(), visualizationSurface.getPaint());
            }
        }
    }

    public Rectangle getShape() {
        return this.shape;
    }

    private void drawBoardImage(VisualizationSurface visualizationSurface) {

        Canvas canvas = visualizationSurface.getCanvas();
        Paint paint = visualizationSurface.getPaint();

        // Color
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(this.color);
        Shape.drawRectangle(getPosition(), getRotation(), shape.getWidth(), shape.getHeight(), canvas, paint);

        // Outline
        if (this.outlineVisibility) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(this.outlineColor);
            paint.setStrokeWidth((float) outlineThickness);
            Shape.drawRectangle(getPosition(), getRotation(), shape.getWidth(), shape.getHeight(), canvas, paint);
        }
    }

    Point[] portGroupCenterPositions = new Point[PORT_GROUP_COUNT];

    private void updatePortGroupImages() {
        // <SHAPE>
        // Positions before rotation
        portGroupCenterPositions[0] = new Point(
                getPosition().getX() + 0,
                getPosition().getY() + ((shape.getHeight() / 2.0f) + (portGroupHeight / 2.0f))
        );
        portGroupCenterPositions[1] = new Point(
                getPosition().getX() + ((shape.getWidth() / 2.0f) + (portGroupHeight / 2.0f)),
                getPosition().getY() + 0
        );
        portGroupCenterPositions[2] = new Point(
                getPosition().getX() + 0,
                getPosition().getY() - ((shape.getHeight() / 2.0f) + (portGroupHeight / 2.0f))
        );
        portGroupCenterPositions[3] = new Point(
                getPosition().getX() - ((shape.getWidth() / 2.0f) + (portGroupHeight / 2.0f)),
                getPosition().getY() + 0
        );
        // </SHAPE>

        for (int i = 0; i < PORT_GROUP_COUNT; i++) {

            // Calculate rotated position
            portGroupCenterPositions[i] = Geometry.calculateRotatedPoint(getPosition(), getRotation() + (((i - 1) * 90) - 90) + ((i - 1) * 90), portGroupCenterPositions[i]);
        }
    }

    private void drawPortGroupImages(VisualizationSurface visualizationSurface) {

        Canvas canvas = visualizationSurface.getCanvas();
        Paint paint = visualizationSurface.getPaint();

        for (int i = 0; i < PORT_GROUP_COUNT; i++) {

            // Color
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(this.portGroupColor);
            Shape.drawRectangle(portGroupCenterPositions[i], getRotation() + ((i * 90) + 90), portGroupWidth, portGroupHeight, canvas, paint);

            // Outline
            if (this.showPortGroupOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth((float) portGroupOutlineThickness);
                paint.setColor(this.portGroupOutlineColor);
                Shape.drawRectangle(portGroupCenterPositions[i], getRotation(), portGroupWidth, portGroupHeight, canvas, paint);
            }

        }
    }

    Point[] lightCenterPositions = new Point[PORT_COUNT];
    double[] lightRotationAngle = new double[PORT_COUNT];

    private void updateLightImages() {
        // <SHAPE>
        lightCenterPositions[0] = new Point(
                getPosition().getX() + (-20),
                getPosition().getY() + ((shape.getHeight() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );
        lightCenterPositions[1] = new Point(
                getPosition().getX() + (0),
                getPosition().getY() + ((shape.getHeight() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );
        lightCenterPositions[2] = new Point(
                getPosition().getX() + (+20),
                getPosition().getY() + ((shape.getHeight() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );

        lightCenterPositions[3] = new Point(
                getPosition().getX() + ((shape.getWidth() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().getY() + (+20)
        );
        lightCenterPositions[4] = new Point(
                getPosition().getX() + ((shape.getWidth() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().getY() + (0)
        );
        lightCenterPositions[5] = new Point(
                getPosition().getX() + ((shape.getWidth() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().getY() + (-20)
        );

        lightCenterPositions[6] = new Point(
                getPosition().getX() + (+20),
                getPosition().getY() - ((shape.getHeight() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );
        lightCenterPositions[7] = new Point(
                getPosition().getX() + (0),
                getPosition().getY() - ((shape.getHeight() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );
        lightCenterPositions[8] = new Point(
                getPosition().getX() + (-20),
                getPosition().getY() - ((shape.getHeight() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );

        lightCenterPositions[9] = new Point(
                getPosition().getX() - ((shape.getWidth() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().getY() + (-20)
        );
        lightCenterPositions[10] = new Point(
                getPosition().getX() - ((shape.getWidth() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().getY() + (0)
        );
        lightCenterPositions[11] = new Point(
                getPosition().getX() - ((shape.getWidth() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
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

    private void drawLightImages(VisualizationSurface visualizationSurface) {

        Canvas canvas = visualizationSurface.getCanvas();
        Paint paint = visualizationSurface.getPaint();

        for (int i = 0; i < PORT_COUNT; i++) {

            // Color
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(3);
            Port port = (Port) getPeripheral().getPort(i);
            if (port.getType() != Port.Type.NONE) {
                paint.setColor(camp.computer.clay.visualization.util.Color.setTransparency(this.getPortImage(i).getUniqueColor(), (float) currentTransparency));
            } else {
                paint.setColor(camp.computer.clay.visualization.util.Color.setTransparency(PortImage.FLOW_PATH_COLOR_NONE, (float) currentTransparency));
            }
            Shape.drawRectangle(lightCenterPositions[i], getRotation() + lightRotationAngle[i], lightWidth, lightHeight, canvas, paint);

            // Outline
            if (this.showLightOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth((float) lightOutlineThickness);
                paint.setColor(this.lightOutlineColor);
                Shape.drawRectangle(lightCenterPositions[i], getRotation() + lightRotationAngle[i], lightWidth, lightHeight, canvas, paint);
            }
        }
    }

    // TODO: Move this into Image (send to all Images)
    public void setTransparency(final double transparency) {

        targetTransparency = transparency;

        currentTransparency = targetTransparency;
        String transparencyString = String.format("%02x", (int) currentTransparency * 255);

        // Frame color
        color = Color.parseColor("#" + transparencyString + colorString);
        outlineColor = Color.parseColor("#" + transparencyString + outlineColorString);

        // Header color
        portGroupColor = Color.parseColor("#" + transparencyString + portGroupColorString);
        portGroupOutlineColor = Color.parseColor("#" + transparencyString + portGroupOutlineColorString);

    }

    public void showPortImages() {
        for (PortImage portImage : getPortImages()) {
            portImage.setVisibility(true);
            portImage.showDocks();
        }
    }

    public void hidePortImages() {
        for (PortImage portImage : getPortImages()) {
            portImage.setVisibility(false);
        }
    }

    public void showPathImages() {
        for (PortImage portImage : getPortImages()) {
            portImage.setPathVisibility(true);
        }
    }

    public void hidePathImages() {
        for (PortImage portImage : getPortImages()) {
            portImage.setPathVisibility(false);
            portImage.showDocks();
        }
    }

    //-------------------------
    // Impression
    //-------------------------

    public boolean contains(Point point) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getPosition().getX(), (int) this.getPosition().getY(), point.getX(), point.getY()) < (this.shape.getHeight() / 2.0f);
        } else {
            return false;
        }
    }

    public boolean contains(Point point, double padding) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getPosition().getX(), (int) this.getPosition().getY(), point.getX(), point.getY()) < (this.shape.getHeight() / 2.0f + padding);
        } else {
            return false;
        }
    }

    @Override
    public void onInteraction(Impression impression) {

        if (impression.getType() == Impression.Type.NONE) {
            // Log.v("onInteraction", "Impression.NONE to " + CLASS_NAME);
        } else if (impression.getType() == Impression.Type.TOUCH) {
            // Log.v("onInteraction", "Impression.TOUCH to " + CLASS_NAME);
        } else if (impression.getType() == Impression.Type.TAP) {

            Log.v("Device", "Tapped peripheral. Port image count: " + getPortImages().size());

            // Focus on touched form
            showPortImages();
            showPathImages();
            setTransparency(1.0);

            // TODO: Speak "choose a channel to get data."

            // Show ports and paths of touched form
            for (PortImage portImage : getPortImages()) {
                List<Path> paths = portImage.getPort().getGraph();
                for (Path path : paths) {
                    // Show ports
                    getVisualization().getImage(path.getSource()).setVisibility(true);
                    getVisualization().getImage(path.getTarget()).setVisibility(true);
                    // Show path
                    getVisualization().getImage(path).setVisibility(true);
                }
            }

        } else if (impression.getType() == Impression.Type.HOLD) {
            // Log.v("onInteraction", "Impression.HOLD to " + CLASS_NAME);
        } else if (impression.getType() == Impression.Type.MOVE) {
            // Log.v("onInteraction", "Impression.MOVE to " + CLASS_NAME);
        } else if (impression.getType() == Impression.Type.DRAG) {
            // Log.v("onInteraction", "Impression.DRAG to " + CLASS_NAME);
        } else if (impression.getType() == Impression.Type.RELEASE) {
            // Log.v("onInteraction", "Impression.RELEASE to " + CLASS_NAME);
        }
    }
}

