package camp.computer.clay.visualization.images;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.ArrayList;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.VisualizationSurface;
import camp.computer.clay.model.simulation.Base;
import camp.computer.clay.model.simulation.Port;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.visualization.arch.Image;
import camp.computer.clay.visualization.util.Animation;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.PointHolder;
import camp.computer.clay.visualization.util.Rectangle;
import camp.computer.clay.visualization.util.Shape;

public class BaseImage extends Image {

    public final static String TYPE = "base";

    // TODO: Replace these with dynamic counts.
    final static int PORT_GROUP_COUNT = 4;
    final static int PORT_COUNT = 12;

    // <STYLE>
    // TODO: Make these private once the map is working well and the sprite is working well.
    public double boardHeight = 250.0f;
    public double boardWidth = 250.0f;
    public Rectangle shape = new Rectangle(boardWidth, boardHeight);

    private String boardColorString = "f7f7f7"; // "404040"; // "414141";
    private int boardColor = Color.parseColor("#ff" + boardColorString); // Color.parseColor("#212121");
    private boolean showBoardOutline = true;
    private String boardOutlineColorString = "414141";
    private int boardOutlineColor = Color.parseColor("#ff" + boardOutlineColorString); // Color.parseColor("#737272");
    private double boardOutlineThickness = 3.0f;

    private double targetTransparency = 1.0f;
    private double currentTransparency = targetTransparency;

    private double portGroupWidth = 50;
    private double portGroupHeight = 13;
    private String portGroupColorString = "3b3b3b";
    private int portGroupColor = Color.parseColor("#ff" + portGroupColorString);
    private boolean showPortGroupOutline = false;
    private String portGroupOutlineColorString = "000000";
    private int portGroupOutlineColor = Color.parseColor("#ff" + portGroupOutlineColorString);
    private double portGroupOutlineThickness = boardOutlineThickness;

    private double distanceLightsToEdge = 12.0f;
    private double lightWidth = 12;
    private double lightHeight = 20;
    private boolean showLightOutline = true;
    private double lightOutlineThickness = 1.0f;
    private int lightOutlineColor = Color.parseColor("#e7e7e7");
    // </STYLE>

    public BaseImage(Base base) {
        super(base);
        setType(TYPE);
        setup();
    }

    private void setup() {
        setupStyle();
    }

    private void setupStyle() {
    }

    public void setupPortImages() {

        // Add a port sprite for each of the associated base's ports
        for (Port port: getBase().getPorts()) {
            PortImage portImage = new PortImage(port);
            portImage.setVisualization(getVisualization());
            getVisualization().addImage(port, portImage, "ports");
        }
    }

    public Base getBase() {
        return (Base) getModel();
    }

    public ArrayList<PortImage> getPortImages() {
        ArrayList<PortImage> portImages = new ArrayList<PortImage>();
        Base base = getBase();

        for (Port port: base.getPorts()) {
            PortImage portImage = (PortImage) getVisualization().getImage(port);
            portImages.add(portImage);
        }

        return portImages;
    }

    public PortImage getPortImage(int index) {
        Base base = getBase();
        PortImage portImage = (PortImage) getVisualization().getImage(base.getPort(index));
        return portImage;
    }

    // TODO: Remove this! Store Port index/id
    public int getPortImageIndex(PortImage portImage) {
        Port port = (Port) getVisualization().getModel(portImage);
        if (getBase().getPorts().contains(port)) {
            return this.getBase().getPorts().indexOf(port);
        }
        return -1;
    }

    public void update() {
    }

    public void draw(VisualizationSurface visualizationSurface) {
        if (isVisible()) {
//            drawPortPeripheralImages(visualizationSurface);
            drawPortGroupImages(visualizationSurface);
            drawBoardImage(visualizationSurface);
            drawLightImages(visualizationSurface);

            if (Application.ENABLE_GEOMETRY_ANNOTATIONS) {
                visualizationSurface.getPaint().setColor(Color.GREEN);
                visualizationSurface.getPaint().setStyle(Paint.Style.STROKE);
                visualizationSurface.getCanvas().drawCircle((float) getPosition().getX(), (float) getPosition().getY(), (float) shape.getWidth(), visualizationSurface.getPaint());
                visualizationSurface.getCanvas().drawCircle((float) getPosition().getX(), (float) getPosition().getY(), (float) shape.getWidth() / 2.0f, visualizationSurface.getPaint());
            }
        }
    }

    public Rectangle getShape() {
        return this.shape;
    }

    public void drawBoardImage(VisualizationSurface visualizationSurface) {

        Canvas canvas = visualizationSurface.getCanvas();
        Paint paint = visualizationSurface.getPaint();

        // Color
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(this.boardColor);
        Shape.drawRectangle(getPosition(), getRotation(), shape.getWidth(), shape.getHeight(), canvas, paint);

        // Outline
        if (this.showBoardOutline) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(this.boardOutlineColor);
            paint.setStrokeWidth((float) boardOutlineThickness);
            Shape.drawRectangle(getPosition(), getRotation(), shape.getWidth(), shape.getHeight(), canvas, paint);
        }
    }

    public void drawPortGroupImages(VisualizationSurface visualizationSurface) {

        Canvas canvas = visualizationSurface.getCanvas();
        Paint paint = visualizationSurface.getPaint();

        // <SHAPE>
        PointHolder[] portGroupCenterPositions = new PointHolder[PORT_GROUP_COUNT];

        // Positions before rotation
        portGroupCenterPositions[0] = new PointHolder(
                getPosition().getX() + 0,
                getPosition().getY() + ((shape.getHeight() / 2.0f) + (portGroupHeight / 2.0f))
        );
        portGroupCenterPositions[1] = new PointHolder(
                getPosition().getX() + ((shape.getWidth() / 2.0f) + (portGroupHeight / 2.0f)),
                getPosition().getY() + 0
        );
        portGroupCenterPositions[2] = new PointHolder(
                getPosition().getX() + 0,
                getPosition().getY() - ((shape.getHeight() / 2.0f) + (portGroupHeight / 2.0f))
        );
        portGroupCenterPositions[3] = new PointHolder(
                getPosition().getX() - ((shape.getWidth() / 2.0f) + (portGroupHeight / 2.0f)),
                getPosition().getY() + 0
        );
        // </SHAPE>

        for (int i = 0; i < PORT_GROUP_COUNT; i++) {

            // Calculate rotated position
            portGroupCenterPositions[i] = Geometry.calculateRotatedPoint(getPosition(), getRotation() + (((i - 1) * 90) - 90) + ((i - 1) * 90), portGroupCenterPositions[i]);

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

    public void drawPortPeripheralImages(VisualizationSurface visualizationSurface) {

        Canvas canvas = visualizationSurface.getCanvas();
        Paint paint = visualizationSurface.getPaint();

        // <SHAPE>
        PointHolder[] portGroupCenterPositions = new PointHolder[PORT_GROUP_COUNT];

        // Positions before rotation
        portGroupCenterPositions[0] = new PointHolder(
                getPosition().getX() + 0,
                getPosition().getY() + ((shape.getHeight()) + (portGroupHeight / 2.0f))
        );
        portGroupCenterPositions[1] = new PointHolder(
                getPosition().getX() + ((shape.getWidth()) + (portGroupHeight / 2.0f)),
                getPosition().getY() + 0
        );
        portGroupCenterPositions[2] = new PointHolder(
                getPosition().getX() + 0,
                getPosition().getY() - ((shape.getHeight()) + (portGroupHeight / 2.0f))
        );
        portGroupCenterPositions[3] = new PointHolder(
                getPosition().getX() - ((shape.getWidth()) + (portGroupHeight / 2.0f)),
                getPosition().getY() + 0
        );
        // </SHAPE>

        for (int i = 0; i < PORT_GROUP_COUNT; i++) {

            // Calculate rotated position
            portGroupCenterPositions[i] = Geometry.calculateRotatedPoint(getPosition(), getRotation() + (((i - 1) * 90) - 90) + ((i - 1) * 90), portGroupCenterPositions[i]);

            // Color
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(this.portGroupColor);
            canvas.drawCircle((float) portGroupCenterPositions[i].getX(), (float) portGroupCenterPositions[i].getY(), 20, paint);

            // Outline
            if (this.showPortGroupOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth((float) portGroupOutlineThickness);
                paint.setColor(this.portGroupOutlineColor);
                canvas.drawCircle((float) portGroupCenterPositions[i].getX(), (float) portGroupCenterPositions[i].getY(), 20, paint);
            }

        }
    }

    public void drawLightImages(VisualizationSurface visualizationSurface) {

        Canvas canvas = visualizationSurface.getCanvas();
        Paint paint = visualizationSurface.getPaint();

        // <SHAPE>
        PointHolder[] lightCenterPositions = new PointHolder[PORT_COUNT];
        lightCenterPositions[0] = new PointHolder(
                getPosition().getX() + (-20),
                getPosition().getY() + ((shape.getHeight() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );
        lightCenterPositions[1] = new PointHolder(
                getPosition().getX() + (0),
                getPosition().getY() + ((shape.getHeight() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );
        lightCenterPositions[2] = new PointHolder(
                getPosition().getX() + (+20),
                getPosition().getY() + ((shape.getHeight() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );

        lightCenterPositions[3] = new PointHolder(
                getPosition().getX() + ((shape.getWidth() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().getY() + (+20)
        );
        lightCenterPositions[4] = new PointHolder(
                getPosition().getX() + ((shape.getWidth() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().getY() + (0)
        );
        lightCenterPositions[5] = new PointHolder(
                getPosition().getX() + ((shape.getWidth() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().getY() + (-20)
        );

        lightCenterPositions[6] = new PointHolder(
                getPosition().getX() + (+20),
                getPosition().getY() - ((shape.getHeight() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );
        lightCenterPositions[7] = new PointHolder(
                getPosition().getX() + (0),
                getPosition().getY() - ((shape.getHeight() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );
        lightCenterPositions[8] = new PointHolder(
                getPosition().getX() + (-20),
                getPosition().getY() - ((shape.getHeight() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );

        lightCenterPositions[9] = new PointHolder(
                getPosition().getX() - ((shape.getWidth() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().getY() + (-20)
        );
        lightCenterPositions[10] = new PointHolder(
                getPosition().getX() - ((shape.getWidth() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().getY() + (0)
        );
        lightCenterPositions[11] = new PointHolder(
                getPosition().getX() - ((shape.getWidth() / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().getY() + (+20)
        );
        double[] lightRotationAngle = new double[12];
        lightRotationAngle[0]  = 0;
        lightRotationAngle[1]  = 0;
        lightRotationAngle[2]  = 0;
        lightRotationAngle[3]  = 90;
        lightRotationAngle[4]  = 90;
        lightRotationAngle[5]  = 90;
        lightRotationAngle[6]  = 180;
        lightRotationAngle[7]  = 180;
        lightRotationAngle[8]  = 180;
        lightRotationAngle[9]  = 270;
        lightRotationAngle[10] = 270;
        lightRotationAngle[11] = 270;
        // </SHAPE>

        for (int i = 0; i < PORT_COUNT; i++) {

            // Calculate rotated position
            lightCenterPositions[i] = Geometry.calculateRotatedPoint(getPosition(), getRotation(), lightCenterPositions[i]);

            // Color
            paint.setStyle(Paint.Style.FILL);
            paint.setStrokeWidth(3);
            Port port = (Port) getBase().getPort(i);
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

    // TODO: Move this into Image (expose to all Images)
    public void setTransparency (final double transparency) {

        targetTransparency = transparency;

        currentTransparency = targetTransparency;
        String transparencyString = String.format("%02x", (int) currentTransparency * 255);

        // Base color
        boardColor = Color.parseColor("#" + transparencyString + boardColorString);
        boardOutlineColor = Color.parseColor("#" + transparencyString + boardOutlineColorString);

        // Header color
        portGroupColor = Color.parseColor("#" + transparencyString + portGroupColorString);
        portGroupOutlineColor = Color.parseColor("#" + transparencyString + portGroupOutlineColorString);

//        if (this.targetTransparency != transparency) {
//
//            Animation.scaleValue(255.0f * targetTransparency, 255.0f * transparency, 200, new Animation.OnScaleListener() {
//                @Override
//                public void onScale(double currentScale) {
//                    currentTransparency = currentScale / 255.0f;
//                    String transparencyString = String.format("%02x", (int) currentScale);
//
//                    // Base color
//                    boardColor = Color.parseColor("#" + transparencyString + boardColorString);
//                    boardOutlineColor = Color.parseColor("#" + transparencyString + boardOutlineColorString);
//
//                    // Header color
//                    portGroupColor = Color.parseColor("#" + transparencyString + portGroupColorString);
//                    portGroupOutlineColor = Color.parseColor("#" + transparencyString + portGroupOutlineColorString);
//                }
//            });
//
//            this.targetTransparency = transparency;
//        }
    }

    public void showPortImages() {
        for (PortImage portImage: getPortImages()) {
            portImage.setVisibility(true);
            portImage.showDocks();
        }
    }

    public void hidePortImages() {
        for (PortImage portImage: getPortImages()) {
            portImage.setVisibility(false);
        }
    }

    public void showPathImages() {
        for (PortImage portImage: getPortImages()) {
            portImage.setPathVisibility(true);
        }
    }

    public void hidePathImages() {
        for (PortImage portImage: getPortImages()) {
            portImage.setPathVisibility(false);
            portImage.showDocks();
        }
    }

    //-------------------------
    // Interaction
    //-------------------------

    public boolean isTouching (PointHolder point) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getPosition().getX(), (int) this.getPosition().getY(), point.getX(), point.getY()) < (this.shape.getHeight() / 2.0f);
        } else {
            return false;
        }
    }

    public boolean isTouching (PointHolder point, double padding) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getPosition().getX(), (int) this.getPosition().getY(), point.getX(), point.getY()) < (this.shape.getHeight() / 2.0f + padding);
        } else {
            return false;
        }
    }

    public static final String CLASS_NAME = "BASE_SPRITE";

    @Override
    public void onTouchInteraction(TouchInteraction touchInteraction) {

        if (touchInteraction.getType() == TouchInteraction.Type.NONE) {
            Log.v("onTouchInteraction", "TouchInteraction.NONE to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.TOUCH) {
            Log.v("onTouchInteraction", "TouchInteraction.TOUCH to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.TAP) {
            Log.v("onTouchInteraction", "TouchInteraction.TAP to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.HOLD) {
            Log.v("onTouchInteraction", "TouchInteraction.HOLD to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.MOVE) {
            Log.v("onTouchInteraction", "TouchInteraction.MOVE to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.PRE_DRAG) {
            Log.v("onTouchInteraction", "TouchInteraction.PRE_DRAG to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.DRAG) {
            Log.v("onTouchInteraction", "TouchInteraction.DRAG to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.RELEASE) {
            Log.v("onTouchInteraction", "TouchInteraction.RELEASE to " + CLASS_NAME);
        }
    }
}

