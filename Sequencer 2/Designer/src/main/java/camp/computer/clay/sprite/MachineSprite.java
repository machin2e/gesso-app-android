package camp.computer.clay.sprite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.TouchArticulation;
import camp.computer.clay.sprite.util.Animation;
import camp.computer.clay.sprite.util.Geometry;

public class MachineSprite extends Sprite {

    private int channelCount = 12;

    private PointF position = new PointF(); // Sprite position
    private float scale = 1.0f; // Sprite scale factor
    private float angle; // Sprite heading angle

    public ArrayList<PortSprite> portSprites = new ArrayList<PortSprite>();

    public PointF getPosition() {
        return this.position;
    }

    public float getAngle() {
        return this.angle;
    }

    public float getScale() {
        return this.scale;
    }

    private float targetTransparency = 1.0f;

    public void setTransparency (final float transparency) {

        if (this.targetTransparency != transparency) {

            Animation.scaleValue(255.0f * targetTransparency, 255.0f * transparency, 200, new Animation.OnScaleListener() {
                @Override
                public void onScale(float currentScale) {
                    int transparencyInteger = (int) currentScale;
                    String transparencyString = String.format("%02x", transparencyInteger);
                    // Machine color
                    boardColor = Color.parseColor("#" + transparencyString + boardColorString);
                    boardOutlineColor = Color.parseColor("#" + transparencyString + boardOutlineColorString);
                    // Header color
                    headerColor = Color.parseColor("#" + transparencyString + headerColorString);
                    headerOutlineColor = Color.parseColor("#" + transparencyString + headerOutlineColorString);
                }
            });

            this.targetTransparency = transparency;
        }
    }

    // --- STYLE ---
    // TODO: Make these private once the map is working well and the sprite is working well.
    public float boardWidth = 250;
    float boardHeight = 250;
    private String boardColorString = "f7f7f7"; // "414141";
    private int boardColor = Color.parseColor("#ff" + boardColorString); // Color.parseColor("#212121");
    boolean showBoardOutline = true;
    private String boardOutlineColorString = "414141";
    int boardOutlineColor = Color.parseColor("#ff" + boardOutlineColorString); // Color.parseColor("#737272");
    float boardOutlineThickness = 3.0f;

    float headerWidth = 50;
    float headerHeight = 13;
    private String headerColorString = "3b3b3b";
    int headerColor = Color.parseColor("#ff" + headerColorString);
    boolean showHeaderOutline = false;
    private String headerOutlineColorString = "000000";
    int headerOutlineColor = Color.parseColor("#ff" + headerOutlineColorString);
    float headerOutlineThickness = boardOutlineThickness;

    public boolean showHighlights = false;
    int boardHighlightColor = Color.parseColor("#1976D2");
    float boardHighlightThickness = 20;

    float distanceLightsToEdge = 12.0f;
    float lightWidth = 12;
    float lightHeight = 20;
    boolean showLightOutline = true;
    float lightOutlineThickness = 1.0f;
    int lightOutlineColor = Color.parseColor("#e7e7e7");

//    public boolean[] showPorts = new boolean[channelCount];
    // ^^^ STYLE ^^^

    private void initializeStyle () {
//        for (int i = 0; i < channelCount; i++) {
//            showPorts[i] = false;
//        }
    }

    public MachineSprite(float x, float y, float angle) {

        this.position.set(x, y);
        this.angle = angle;
        this.scale = 1.0f;

        initializeStyle();

        // Ports
        for (int i = 0; i < channelCount; i++) {
            PortSprite portSprite = new PortSprite(this);
            portSprite.setPosition(this.position.x, this.position.y);
            portSprites.add(portSprite);
        }
    }

    public int getChannelCount() {
        return channelCount;
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

    private void updatePortPositions(MapView mapView) {

        double boardAngleRadians = Math.toRadians(this.angle);
        float sinBoardAngle = (float) Math.sin(boardAngleRadians);
        float cosBoardAngle = (float) Math.cos(boardAngleRadians);

        for (int i = 0; i < 4; i++) {

            // Cache calculations
            double boardFacingAngleRadians = Math.toRadians(-90.0 * i);
            float sinBoardFacingAngle = (float) Math.sin(boardFacingAngleRadians);
            float cosBoardFacingAngle = (float) Math.cos(boardFacingAngleRadians);

            for (int j = 0; j < 3; j++) {

                PortSprite portSprite = portSprites.get(3 * i + j);
                PointF portSpritePosition = portSprites.get(3 * i + j).getPosition();

                // Translate (Nodes)
                float nodeRadiusPlusPadding = portSprite.shapeRadius + PortSprite.DISTANCE_BETWEEN_NODES;
                portSpritePosition.x = ((-(nodeRadiusPlusPadding * 2.0f) + j * (nodeRadiusPlusPadding * 2.0f)));
                portSpritePosition.y = (((boardWidth / 2.0f) + nodeRadiusPlusPadding)) + portSprite.shapeRadius;

                // Rotate (Nodes)
                portSpritePosition.set(
                        portSpritePosition.x * cosBoardFacingAngle - portSpritePosition.y * sinBoardFacingAngle,
                        portSpritePosition.x * sinBoardFacingAngle + portSpritePosition.y * cosBoardFacingAngle
                );

                // Rotate (Machine)
                portSpritePosition.set(
                        portSpritePosition.x * cosBoardAngle - portSpritePosition.y * sinBoardAngle,
                        portSpritePosition.x * sinBoardAngle + portSpritePosition.y * cosBoardAngle
                );

                // Translate (Machine)
                portSpritePosition.x = portSpritePosition.x + this.position.x;
                portSpritePosition.y = portSpritePosition.y + this.position.y;

//                // Scale (Map)
//                portSpritePosition.x = portSpritePosition.x * mapView.getScale();
//                portSpritePosition.y = portSpritePosition.y * mapView.getScale();

            }
        }
    }

    public void updateChannelData () {
        for (int j = 0; j < this.channelCount; j++) {
            this.portSprites.get(j).updateChannelData();
        }
    }

    public void draw(MapView mapView) {

        Canvas mapCanvas = mapView.getCanvas();
        Paint paint = mapView.getPaint();

        MachineSprite machineSprite = this;

        mapCanvas.save();

        mapCanvas.translate(machineSprite.getPosition().x, machineSprite.getPosition().y);
        mapCanvas.rotate(machineSprite.getAngle());

        mapCanvas.scale(machineSprite.getScale(), machineSprite.getScale());

        // --- BOARD HIGHLIGHT ---
        if (machineSprite.showHighlights) {
            mapCanvas.save();
            // Color
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(machineSprite.boardHighlightColor);
            mapCanvas.drawRect(
                    0 - (machineSprite.boardWidth / 2.0f) - machineSprite.boardHighlightThickness,
                    0 - (machineSprite.boardHeight / 2.0f) - machineSprite.boardHighlightThickness,
                    0 + (machineSprite.boardWidth / 2.0f) + machineSprite.boardHighlightThickness,
                    0 + (machineSprite.boardHeight / 2.0f) + machineSprite.boardHighlightThickness,
                    paint);
            mapCanvas.restore();
        }
        // ^^^ BOARD HIGHLIGHT ^^^

        // --- HEADER HIGLIGHT ---
        if (machineSprite.showHighlights) {
            for (int i = 0; i < 4; i++) {

                mapCanvas.save();

                mapCanvas.rotate(90 * i);
                mapCanvas.translate(0, 0);

                mapCanvas.save();
                mapCanvas.translate(
                        0,
                        (machineSprite.boardHeight / 2.0f) + (machineSprite.headerHeight / 2.0f)
                );
                mapCanvas.rotate(0);

                mapCanvas.save();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(machineSprite.boardHighlightColor);
                mapCanvas.drawRect(
                        0 - (machineSprite.headerWidth / 2.0f) - machineSprite.boardHighlightThickness,
                        0 - (machineSprite.headerHeight / 2.0f) - machineSprite.boardHighlightThickness,
                        0 + (machineSprite.headerWidth / 2.0f) + machineSprite.boardHighlightThickness,
                        0 + (machineSprite.headerHeight / 2.0f) + machineSprite.boardHighlightThickness,
                        paint
                );
                mapCanvas.restore();

                mapCanvas.restore();

                mapCanvas.restore();

            }
        }
        // ^^^ HEADER HIGHLIGHT ^^^

        // --- BOARD ---
        mapCanvas.save();
        // Color
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(machineSprite.boardColor);
        mapCanvas.drawRect(
                0 - (machineSprite.boardWidth / 2.0f),
                0 - (machineSprite.boardHeight / 2.0f),
                0 + (machineSprite.boardWidth / 2.0f),
                0 + (machineSprite.boardHeight / 2.0f),
                paint
        );
        // Outline
        if (machineSprite.showBoardOutline) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(machineSprite.boardOutlineColor);
            paint.setStrokeWidth(machineSprite.boardOutlineThickness);
            mapCanvas.drawRect(
                    0 - (machineSprite.boardWidth / 2.0f),
                    0 - (machineSprite.boardHeight / 2.0f),
                    0 + (machineSprite.boardWidth / 2.0f),
                    0 + (machineSprite.boardHeight / 2.0f),
                    paint
            );
        }
        mapCanvas.restore();
        // ^^^ BOARD ^^^

        // --- HEADERS ---
        for (int i = 0; i < 4; i++) {

            mapCanvas.save();

            mapCanvas.rotate(90 * i);
            mapCanvas.translate(0, 0);

            mapCanvas.save();
            mapCanvas.translate(
                    0,
                    (machineSprite.boardHeight / 2.0f) + (machineSprite.headerHeight / 2.0f)
            );
            mapCanvas.rotate(0);

            mapCanvas.save();
            // Color
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(machineSprite.headerColor);
            mapCanvas.drawRect(
                    0 - (machineSprite.headerWidth / 2.0f),
                    0 - (machineSprite.headerHeight / 2.0f),
                    0 + (machineSprite.headerWidth / 2.0f),
                    0 + (machineSprite.headerHeight / 2.0f),
                    paint
            );
            // Outline
            if (machineSprite.showHeaderOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(machineSprite.headerOutlineThickness);
                paint.setColor(machineSprite.headerOutlineColor);
                mapCanvas.drawRect(
                        0 - (machineSprite.headerWidth / 2.0f),
                        0 - (machineSprite.headerHeight / 2.0f),
                        0 + (machineSprite.headerWidth / 2.0f),
                        0 + (machineSprite.headerHeight / 2.0f),
                        paint
                );
            }
            mapCanvas.restore();

            mapCanvas.restore();

            mapCanvas.restore();

        }
        // ^^^ HEADERS ^^^

        // --- LIGHTS ---
        for (int i = 0; i < 4; i++) {

            mapCanvas.save();

            mapCanvas.rotate(-90 * i);
            mapCanvas.translate(0, 0);

            for (int j = 0; j < 3; j++) {

                mapCanvas.save();
                mapCanvas.translate(
                        -20 + j * 20,
                        (machineSprite.boardWidth / 2.0f) - (machineSprite.lightHeight / 2.0f) - machineSprite.distanceLightsToEdge
                );
                mapCanvas.rotate(0);

                mapCanvas.save();
                // Color
                paint.setStyle(Paint.Style.FILL);
                paint.setStrokeWidth(3);
                //paint.setColor(machineSprite.channelTypeColors.get(machineSprite.channelTypes.get(3 * i + j)));
                if (machineSprite.portSprites.get(3 * i + j).portType != PortSprite.PortType.NONE) {
                    paint.setColor(machineSprite.getPortSprite(3 * i + j).getUniqueColor());
                } else {
                    paint.setColor(PortSprite.FLOW_PATH_COLOR_NONE);
                }
                mapCanvas.drawRoundRect(
                        0 - (machineSprite.lightWidth / 2.0f),
                        0 - (machineSprite.lightHeight / 2.0f),
                        0 + (machineSprite.lightWidth / 2.0f),
                        0 + (machineSprite.lightHeight / 2.0f),
                        5.0f,
                        5.0f,
                        paint
                );
                // Outline
                if (machineSprite.showLightOutline) {
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(machineSprite.lightOutlineThickness);
                    paint.setColor(machineSprite.lightOutlineColor);
                    mapCanvas.drawRoundRect(
                            0 - (machineSprite.lightWidth / 2.0f),
                            0 - (machineSprite.lightHeight / 2.0f),
                            0 + (machineSprite.lightWidth / 2.0f),
                            0 + (machineSprite.lightHeight / 2.0f),
                            5.0f,
                            5.0f,
                            paint
                    );
                }
                mapCanvas.restore();

                mapCanvas.restore();

            }

            mapCanvas.restore();

        }
        // ^^^ LIGHTS ^^^

        // --- PORTS ---
        mapCanvas.restore();

        mapCanvas.save();

        mapCanvas.translate(machineSprite.getPosition().x, machineSprite.getPosition().y);
        mapCanvas.rotate(machineSprite.getAngle());

        mapCanvas.scale(machineSprite.getScale(), machineSprite.getScale());

        for (int i = 0; i < 4; i++) {

            mapCanvas.save();

            mapCanvas.rotate(-90 * i);
            mapCanvas.translate(0, 0);

            for (int j = 0; j < 3; j++) {

                PortSprite portSprite = portSprites.get(3 * i + j);

//                mapCanvas.save();
//                mapCanvas.translate(
//                        -((portSprite.shapeRadius + PortSprite.DISTANCE_BETWEEN_NODES) * 2.0f) + j * ((portSprite.shapeRadius + PortSprite.DISTANCE_BETWEEN_NODES) * 2),
//                        (machineSprite.boardWidth / 2.0f) + portSprite.shapeRadius + PortSprite.DISTANCE_FROM_BOARD
//                );
//                mapCanvas.rotate(0);

                mapCanvas.save();
                mapCanvas.translate(
                        -((portSprite.shapeRadius + PortSprite.DISTANCE_BETWEEN_NODES) * 2.0f) + j * ((portSprite.shapeRadius + PortSprite.DISTANCE_BETWEEN_NODES) * 2),
                        (machineSprite.boardWidth / 2.0f) + portSprite.shapeRadius + PortSprite.DISTANCE_FROM_BOARD
                );
                if (machineSprite.portSprites.get(3 * i + j).portDirection == PortSprite.PortDirection.OUTPUT) {
                    mapCanvas.rotate(180.0f);
                } else {
                    mapCanvas.rotate(0.0f);
                }

                machineSprite.updatePortPositions(mapView); // TODO: Move this into step()/updateState()


                portSprite.draw(mapView);

                mapCanvas.restore();
            }

            mapCanvas.restore();
        }

        // TODO: Put this in/under PortSprite

        mapCanvas.restore();
        // ^^^ PORTS ^^^

        // TODO: Put this under PortSprite
        drawPaths(mapView);

        for (PortSprite portSprite : portSprites) {
            portSprite.drawCandidatePath(mapView);
        }
    }

    public void drawPaths(MapView mapView) {
        for (int j = 0; j < channelCount; j++) {
            for (int i = 0; i < portSprites.get(j).pathSprites.size(); i++) {
                PathSprite pathSprite = portSprites.get(j).pathSprites.get(i);
                pathSprite.draw(mapView);
            }
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

    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
//        this.updatePortPositions();
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    //-------------------------
    // Interaction
    //-------------------------

    public boolean isTouching (PointF point) {
        if (getVisibility()) {
            return Geometry.calculateDistance((int) this.getPosition().x, (int) this.getPosition().y, point.x, point.y) < (this.boardWidth / 2.0f);
        } else {
            return false;
        }
    }

    public static final String CLASS_NAME = "MACHINE_SPRITE";

    @Override
    public void onTouchAction(TouchArticulation touchArticulation) {

        if (touchArticulation.getType() == TouchArticulation.TouchInteractionType.NONE) {
            Log.v("onTouchAction", "TouchArticulation.NONE to " + CLASS_NAME);
        } else if (touchArticulation.getType() == TouchArticulation.TouchInteractionType.TOUCH) {
            Log.v("onTouchAction", "TouchArticulation.TOUCH to " + CLASS_NAME);
        } else if (touchArticulation.getType() == TouchArticulation.TouchInteractionType.TAP) {
            Log.v("onTouchAction", "TouchArticulation.TAP to " + CLASS_NAME);
        } else if (touchArticulation.getType() == TouchArticulation.TouchInteractionType.DOUBLE_DAP) {
            Log.v("onTouchAction", "TouchArticulation.DOUBLE_TAP to " + CLASS_NAME);
        } else if (touchArticulation.getType() == TouchArticulation.TouchInteractionType.HOLD) {
            Log.v("onTouchAction", "TouchArticulation.HOLD to " + CLASS_NAME);
        } else if (touchArticulation.getType() == TouchArticulation.TouchInteractionType.MOVE) {
            Log.v("onTouchAction", "TouchArticulation.MOVE to " + CLASS_NAME);
        } else if (touchArticulation.getType() == TouchArticulation.TouchInteractionType.PRE_DRAG) {
            Log.v("onTouchAction", "TouchArticulation.PRE_DRAG to " + CLASS_NAME);
        } else if (touchArticulation.getType() == TouchArticulation.TouchInteractionType.DRAG) {
            Log.v("onTouchAction", "TouchArticulation.DRAG to " + CLASS_NAME);
        } else if (touchArticulation.getType() == TouchArticulation.TouchInteractionType.RELEASE) {
            Log.v("onTouchAction", "TouchArticulation.RELEASE to " + CLASS_NAME);
        }
    }
}

