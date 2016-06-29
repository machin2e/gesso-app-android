package camp.computer.clay.sprite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.Machine;
import camp.computer.clay.model.Port;
import camp.computer.clay.model.TouchInteraction;
import camp.computer.clay.sprite.util.Animation;
import camp.computer.clay.sprite.util.Geometry;
import camp.computer.clay.sprite.util.Shape;

public class MachineSprite extends Sprite {

    // TODO: Replace these with dynamic counts.
    final static int PORT_GROUP_COUNT = 4;
    final static int PORT_COUNT = 12;

    // TODO: Delete this? Could do reverse lookup through the model.
    public ArrayList<PortSprite> portSprites = new ArrayList<PortSprite>();

    // --- STYLE ---
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
    // ^^^ STYLE ^^^

    private void initializeStyle () {
    }

    // TODO: Replace with SpriteHolder: public MachineSprite(float x, float y, float angle) {
    public MachineSprite(Machine machine) {
        super(machine);

        initializeStyle();
    }

    public void initializePortSprites() {

        Machine machine = (Machine) getModel();

        // Add a port sprite for each of the associated machine's ports
        int i = 0;
        Machine machineModel = (Machine) this.getModel();
        for (Port port: machineModel.getPorts()) {
            PortSprite portSprite = new PortSprite(port);
            portSprite.setParentSprite(this);
            portSprites.add(portSprite);
            i++;
        }
    }

    public PortSprite getPortSprite (int index) {
        return this.portSprites.get(index);
    }

    public int getPortSpriteIndex(PortSprite portSprite) {
        if (this.portSprites.contains(portSprite)) {
            return this.portSprites.indexOf(portSprite);
        }
        return -1;
    }

    public void update() {
        for (int j = 0; j < this.portSprites.size(); j++) {
            this.portSprites.get(j).update();
        }
    }

    public void draw(MapView mapView) {

        if (getVisibility()) {

            drawStyleLayer(mapView);

            // <PORT_SPRITE>
            // TODO: Put this under PortSprite
            for (PortSprite portSprite : this.portSprites) {
                portSprite.draw(mapView);
            }
            // </PORT_SPRITE>
        }
    }

    private void drawStyleLayer(MapView mapView) {

        drawBoardLayer(mapView);
        drawHeadersLayer(mapView);
        drawLightsLayer(mapView);
    }

    public void drawBoardLayer(MapView mapView) {

        Canvas mapCanvas = mapView.getCanvas();
        Paint paint = mapView.getPaint();

        // Color
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(this.boardColor);
        Shape.drawRectangle(getPosition(), getRotation(), boardWidth, boardHeight, mapCanvas, paint);

        // Outline
        if (this.showBoardOutline) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(this.boardOutlineColor);
            paint.setStrokeWidth(this.boardOutlineThickness);
            Shape.drawRectangle(getPosition(), getRotation(), boardWidth, boardHeight, mapCanvas, paint);
        }
    }

    public void drawHeadersLayer(MapView mapView) {

        Canvas mapCanvas = mapView.getCanvas();
        Paint paint = mapView.getPaint();

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
            Shape.drawRectangle(portGroupCenterPositions[i], getRotation() + ((i * 90) + 90), portGroupWidth, portGroupHeight, mapCanvas, paint);

            // Outline
            if (this.showPortGroupOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(this.portGroupOutlineThickness);
                paint.setColor(this.portGroupOutlineColor);
                Shape.drawRectangle(portGroupCenterPositions[i], getRotation(), portGroupWidth, portGroupHeight, mapCanvas, paint);
            }

        }
    }

    public void drawLightsLayer(MapView mapView) {

        Canvas mapCanvas = mapView.getCanvas();
        Paint paint = mapView.getPaint();

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
            Port port = (Port) this.portSprites.get(i).getModel();
            if (port.portType != Port.PortType.NONE) {
                paint.setColor(camp.computer.clay.sprite.util.Color.setTransparency(this.getPortSprite(i).getUniqueColor(), currentTransparency));
            } else {
                paint.setColor(camp.computer.clay.sprite.util.Color.setTransparency(PortSprite.FLOW_PATH_COLOR_NONE, currentTransparency));
            }
            Shape.drawRectangle(lightCenterPositions[i], getRotation() + lightRotationAngle[i], lightWidth, lightHeight, mapCanvas, paint);

            // Outline
            if (this.showLightOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(this.lightOutlineThickness);
                paint.setColor(this.lightOutlineColor);
                Shape.drawRectangle(lightCenterPositions[i], getRotation() + lightRotationAngle[i], lightWidth, lightHeight, mapCanvas, paint);
            }
        }
    }

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

    public void showPorts() {
        for (int i = 0; i < portSprites.size(); i++) {
            this.portSprites.get(i).setVisibility(true);
            this.portSprites.get(i).setPathVisibility(true);
        }
    }

    public void showPort(int channelIndex) {
        this.portSprites.get(channelIndex).setVisibility(true);
        this.portSprites.get(channelIndex).setPathVisibility(true);
    }

    public void hidePorts() {
        for (int i = 0; i < portSprites.size(); i++) {
            portSprites.get(i).setVisibility(false);
            this.portSprites.get(i).setPathVisibility(false);
        }
    }

    private void hidePort(int channelIndex) {
        portSprites.get(channelIndex).setVisibility(false);
        this.portSprites.get(channelIndex).setPathVisibility(false);
    }

    public void showPaths() {
        for (int i = 0; i < portSprites.size(); i++) {
            this.portSprites.get(i).setPathVisibility(true);
        }
    }

    public void hidePaths() {
        for (int i = 0; i < portSprites.size(); i++) {
            this.portSprites.get(i).setVisibility(false);
            this.portSprites.get(i).showPathDocks();
        }
    }

    public void showPath(int pathIndex, boolean isFullPathVisible) {
        this.portSprites.get(pathIndex).setVisibility(true);
        if (isFullPathVisible) {
            this.portSprites.get(pathIndex).showPaths();
        } else {
            this.portSprites.get(pathIndex).showPathDocks();
        }
    }

    //-------------------------
    // Interaction
    //-------------------------

    public boolean isTouching (PointF point) {
        if (getVisibility()) {
            return Geometry.calculateDistance((int) this.getPosition().x, (int) this.getPosition().y, point.x, point.y) < (this.boardHeight / 2.0f);
        } else {
            return false;
        }
    }

    public static final String CLASS_NAME = "MACHINE_SPRITE";

    @Override
    public void onTouchAction(TouchInteraction touchInteraction) {

        if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.NONE) {
            Log.v("onTouchAction", "TouchInteraction.NONE to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.TOUCH) {
            Log.v("onTouchAction", "TouchInteraction.TOUCH to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.TAP) {
            Log.v("onTouchAction", "TouchInteraction.TAP to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.DOUBLE_DAP) {
            Log.v("onTouchAction", "TouchInteraction.DOUBLE_TAP to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.HOLD) {
            Log.v("onTouchAction", "TouchInteraction.HOLD to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.MOVE) {
            Log.v("onTouchAction", "TouchInteraction.MOVE to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.PRE_DRAG) {
            Log.v("onTouchAction", "TouchInteraction.PRE_DRAG to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.DRAG) {
            Log.v("onTouchAction", "TouchInteraction.DRAG to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.RELEASE) {
            Log.v("onTouchAction", "TouchInteraction.RELEASE to " + CLASS_NAME);
        }
    }
}
