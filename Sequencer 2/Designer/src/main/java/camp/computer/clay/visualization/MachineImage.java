package camp.computer.clay.visualization;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.VisualizationSurface;
import camp.computer.clay.model.simulation.Machine;
import camp.computer.clay.model.simulation.Port;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.visualization.util.Animation;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.Shape;

public class MachineImage extends Image {

    // TODO: Replace these with dynamic counts.
    final static int PORT_GROUP_COUNT = 4;
    final static int PORT_COUNT = 12;

    // <STYLE>
    // TODO: Make these private once the map is working well and the sprite is working well.
    public float boardHeight = 250.0f;
    public float boardWidth = 250.0f;
    private String boardColorString = "f7f7f7"; // "414141";
    private int boardColor = Color.parseColor("#ff" + boardColorString); // Color.parseColor("#212121");
    private boolean showBoardOutline = true;
    private String boardOutlineColorString = "414141";
    private int boardOutlineColor = Color.parseColor("#ff" + boardOutlineColorString); // Color.parseColor("#737272");
    private float boardOutlineThickness = 3.0f;

    private float targetTransparency = 1.0f;
    private float currentTransparency = targetTransparency;

    private float portGroupWidth = 50;
    private float portGroupHeight = 13;
    private String portGroupColorString = "3b3b3b";
    private int portGroupColor = Color.parseColor("#ff" + portGroupColorString);
    private boolean showPortGroupOutline = false;
    private String portGroupOutlineColorString = "000000";
    private int portGroupOutlineColor = Color.parseColor("#ff" + portGroupOutlineColorString);
    private float portGroupOutlineThickness = boardOutlineThickness;

    public boolean showHighlights = false;
    private int boardHighlightColor = Color.parseColor("#1976D2");
    private float boardHighlightThickness = 20;

    private float distanceLightsToEdge = 12.0f;
    private float lightWidth = 12;
    private float lightHeight = 20;
    private boolean showLightOutline = true;
    private float lightOutlineThickness = 1.0f;
    private int lightOutlineColor = Color.parseColor("#e7e7e7");
    // </STYLE>

    public MachineImage(Machine machine) {
        super(machine);

        initializeStyle();
    }

    private void initializeStyle () {
    }

    public void initializePortImages() {

        // Add a port sprite for each of the associated machine's ports
        for (Port port: getMachine().getPorts()) {
            PortImage portImage = new PortImage(port);
            portImage.setVisualization(getVisualization());
            getVisualization().addImage(port, portImage, "ports");
        }
    }

    public Machine getMachine() {
        return (Machine) getModel();
    }

    public ArrayList<PortImage> getPortImages() {
        ArrayList<PortImage> portImages = new ArrayList<PortImage>();
        Machine machine = getMachine();

        for (Port port: machine.getPorts()) {
            PortImage portImage = (PortImage) getVisualization().getImage(port);
            portImages.add(portImage);
        }

        return portImages;
    }

    public PortImage getPortImage(int index) {
        Machine machine = getMachine();
        PortImage portImage = (PortImage) getVisualization().getImage(machine.getPort(index));
        return portImage;
    }

    // TODO: Remove this! Store Port index/id
    public int getPortImageIndex(PortImage portImage) {
        Port port = (Port) getVisualization().getModel(portImage);
        if (getMachine().getPorts().contains(port)) {
            return this.getMachine().getPorts().indexOf(port);
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

            if (Application.ENABLE_DEBUG_ANNOTATIONS) {
                visualizationSurface.getPaint().setColor(Color.GREEN);
                visualizationSurface.getPaint().setStyle(Paint.Style.STROKE);
                visualizationSurface.getCanvas().drawCircle(getPosition().x, getPosition().y, boardWidth, visualizationSurface.getPaint());
                visualizationSurface.getCanvas().drawCircle(getPosition().x, getPosition().y, boardWidth / 2.0f, visualizationSurface.getPaint());
            }
        }
    }

    public void drawBoardImage(VisualizationSurface visualizationSurface) {

        Canvas canvas = visualizationSurface.getCanvas();
        Paint paint = visualizationSurface.getPaint();

        // Color
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(this.boardColor);
        Shape.drawRectangle(getPosition(), getRotation(), boardWidth, boardHeight, canvas, paint);

        // Outline
        if (this.showBoardOutline) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(this.boardOutlineColor);
            paint.setStrokeWidth(this.boardOutlineThickness);
            Shape.drawRectangle(getPosition(), getRotation(), boardWidth, boardHeight, canvas, paint);
        }
    }

    public void drawPortGroupImages(VisualizationSurface visualizationSurface) {

        Canvas canvas = visualizationSurface.getCanvas();
        Paint paint = visualizationSurface.getPaint();

        // <SHAPE>
        PointF[] portGroupCenterPositions = new PointF[PORT_GROUP_COUNT];

        // Positions before rotation
        portGroupCenterPositions[0] = new PointF(
                getPosition().x + 0,
                getPosition().y + ((boardHeight / 2.0f) + (portGroupHeight / 2.0f))
        );
        portGroupCenterPositions[1] = new PointF(
                getPosition().x + ((boardWidth / 2.0f) + (portGroupHeight / 2.0f)),
                getPosition().y + 0
        );
        portGroupCenterPositions[2] = new PointF(
                getPosition().x + 0,
                getPosition().y - ((boardHeight / 2.0f) + (portGroupHeight / 2.0f))
        );
        portGroupCenterPositions[3] = new PointF(
                getPosition().x - ((boardWidth / 2.0f) + (portGroupHeight / 2.0f)),
                getPosition().y + 0
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
                paint.setStrokeWidth(this.portGroupOutlineThickness);
                paint.setColor(this.portGroupOutlineColor);
                Shape.drawRectangle(portGroupCenterPositions[i], getRotation(), portGroupWidth, portGroupHeight, canvas, paint);
            }

        }
    }

    public void drawPortPeripheralImages(VisualizationSurface visualizationSurface) {

        Canvas canvas = visualizationSurface.getCanvas();
        Paint paint = visualizationSurface.getPaint();

        // <SHAPE>
        PointF[] portGroupCenterPositions = new PointF[PORT_GROUP_COUNT];

        // Positions before rotation
        portGroupCenterPositions[0] = new PointF(
                getPosition().x + 0,
                getPosition().y + ((boardHeight) + (portGroupHeight / 2.0f))
        );
        portGroupCenterPositions[1] = new PointF(
                getPosition().x + ((boardWidth) + (portGroupHeight / 2.0f)),
                getPosition().y + 0
        );
        portGroupCenterPositions[2] = new PointF(
                getPosition().x + 0,
                getPosition().y - ((boardHeight) + (portGroupHeight / 2.0f))
        );
        portGroupCenterPositions[3] = new PointF(
                getPosition().x - ((boardWidth) + (portGroupHeight / 2.0f)),
                getPosition().y + 0
        );
        // </SHAPE>

        for (int i = 0; i < PORT_GROUP_COUNT; i++) {

            // Calculate rotated position
            portGroupCenterPositions[i] = Geometry.calculateRotatedPoint(getPosition(), getRotation() + (((i - 1) * 90) - 90) + ((i - 1) * 90), portGroupCenterPositions[i]);

            // Color
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(this.portGroupColor);
            canvas.drawCircle(portGroupCenterPositions[i].x, portGroupCenterPositions[i].y, 20, paint);

            // Outline
            if (this.showPortGroupOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(this.portGroupOutlineThickness);
                paint.setColor(this.portGroupOutlineColor);
                canvas.drawCircle(portGroupCenterPositions[i].x, portGroupCenterPositions[i].y, 20, paint);
            }

        }
    }

    public void drawLightImages(VisualizationSurface visualizationSurface) {

        Canvas canvas = visualizationSurface.getCanvas();
        Paint paint = visualizationSurface.getPaint();

        // <SHAPE>
        PointF[] lightCenterPositions = new PointF[PORT_COUNT];
        lightCenterPositions[0] = new PointF(
                getPosition().x + (-20),
                getPosition().y + ((boardHeight / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );
        lightCenterPositions[1] = new PointF(
                getPosition().x + (0),
                getPosition().y + ((boardHeight / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );
        lightCenterPositions[2] = new PointF(
                getPosition().x + (+20),
                getPosition().y + ((boardHeight / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );

        lightCenterPositions[3] = new PointF(
                getPosition().x + ((boardWidth / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().y + (+20)
        );
        lightCenterPositions[4] = new PointF(
                getPosition().x + ((boardWidth / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().y + (0)
        );
        lightCenterPositions[5] = new PointF(
                getPosition().x + ((boardWidth / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().y + (-20)
        );

        lightCenterPositions[6] = new PointF(
                getPosition().x + (+20),
                getPosition().y - ((boardHeight / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );
        lightCenterPositions[7] = new PointF(
                getPosition().x + (0),
                getPosition().y - ((boardHeight / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );
        lightCenterPositions[8] = new PointF(
                getPosition().x + (-20),
                getPosition().y - ((boardHeight / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f))
        );

        lightCenterPositions[9] = new PointF(
                getPosition().x - ((boardWidth / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().y + (-20)
        );
        lightCenterPositions[10] = new PointF(
                getPosition().x - ((boardWidth / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().y + (0)
        );
        lightCenterPositions[11] = new PointF(
                getPosition().x - ((boardWidth / 2.0f) + (-distanceLightsToEdge) + -(lightHeight / 2.0f)),
                getPosition().y + (+20)
        );
        float[] lightRotationAngle = new float[12];
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
            Port port = (Port) getMachine().getPort(i);
            if (port.getType() != Port.Type.NONE) {
                paint.setColor(camp.computer.clay.visualization.util.Color.setTransparency(this.getPortImage(i).getUniqueColor(), currentTransparency));
            } else {
                paint.setColor(camp.computer.clay.visualization.util.Color.setTransparency(PortImage.FLOW_PATH_COLOR_NONE, currentTransparency));
            }
            Shape.drawRectangle(lightCenterPositions[i], getRotation() + lightRotationAngle[i], lightWidth, lightHeight, canvas, paint);

            // Outline
            if (this.showLightOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(this.lightOutlineThickness);
                paint.setColor(this.lightOutlineColor);
                Shape.drawRectangle(lightCenterPositions[i], getRotation() + lightRotationAngle[i], lightWidth, lightHeight, canvas, paint);
            }
        }
    }

    // TODO: Move this into Image (expose to all Images)
    public void setTransparency (final float transparency) {

        if (this.targetTransparency != transparency) {

            Animation.scaleValue(255.0f * targetTransparency, 255.0f * transparency, 200, new Animation.OnScaleListener() {
                @Override
                public void onScale(float currentScale) {
                    currentTransparency = currentScale / 255.0f;
                    String transparencyString = String.format("%02x", (int) currentScale);

                    // Machine color
                    boardColor = Color.parseColor("#" + transparencyString + boardColorString);
                    boardOutlineColor = Color.parseColor("#" + transparencyString + boardOutlineColorString);

                    // Header color
                    portGroupColor = Color.parseColor("#" + transparencyString + portGroupColorString);
                    portGroupOutlineColor = Color.parseColor("#" + transparencyString + portGroupOutlineColorString);
                }
            });

            this.targetTransparency = transparency;
        }
    }

    public void showPortImages() {
        for (PortImage portImage: getPortImages()) {
            portImage.setVisibility(true);
            portImage.setPathVisibility(true);
        }
    }

    public void showPortImage(int index) {
        PortImage portImage = getPortImages().get(index);
        portImage.setVisibility(true);
        portImage.setPathVisibility(true);
    }

    public void hidePortImages() {
        for (PortImage portImage: getPortImages()) {
            portImage.setVisibility(false);
            portImage.setPathVisibility(false);
        }
    }

    private void hidePortImage(int index) {
        PortImage portImage = getPortImages().get(index);
        portImage.setVisibility(false);
        portImage.setPathVisibility(false);
    }

    public void showPathImages() {
        for (PortImage portImage: getPortImages()) {
            portImage.setPathVisibility(true);
        }
    }

    public void hidePathImages() {
        for (PortImage portImage: getPortImages()) {
            portImage.setPathVisibility(false);
            portImage.showPathDocks();
        }
    }

    public void showPathImage(int index, boolean isFullPathVisible) {
        PortImage portImage = getPortImages().get(index);
        portImage.setVisibility(true);
        if (isFullPathVisible) {
            portImage.showPaths();
        } else {
            portImage.showPathDocks();
        }
    }

    //-------------------------
    // Interaction
    //-------------------------

    public boolean isTouching (PointF point) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getPosition().x, (int) this.getPosition().y, point.x, point.y) < (this.boardHeight / 2.0f);
        } else {
            return false;
        }
    }

    public boolean isTouching (PointF point, float padding) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getPosition().x, (int) this.getPosition().y, point.x, point.y) < (this.boardHeight / 2.0f + padding);
        } else {
            return false;
        }
    }

    public static final String CLASS_NAME = "MACHINE_SPRITE";

    @Override
    public void onTouchInteraction(TouchInteraction touchInteraction) {

        if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.NONE) {
            Log.v("onTouchInteraction", "TouchInteraction.NONE to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.TOUCH) {
            Log.v("onTouchInteraction", "TouchInteraction.TOUCH to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.TAP) {
            Log.v("onTouchInteraction", "TouchInteraction.TAP to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.DOUBLE_DAP) {
            Log.v("onTouchInteraction", "TouchInteraction.DOUBLE_TAP to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.HOLD) {
            Log.v("onTouchInteraction", "TouchInteraction.HOLD to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.MOVE) {
            Log.v("onTouchInteraction", "TouchInteraction.MOVE to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.PRE_DRAG) {
            Log.v("onTouchInteraction", "TouchInteraction.PRE_DRAG to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.DRAG) {
            Log.v("onTouchInteraction", "TouchInteraction.DRAG to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.RELEASE) {
            Log.v("onTouchInteraction", "TouchInteraction.RELEASE to " + CLASS_NAME);
        }
    }
}

