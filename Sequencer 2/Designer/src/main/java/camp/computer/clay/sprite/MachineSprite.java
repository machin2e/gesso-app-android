package camp.computer.clay.sprite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.TouchAction;
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

    public boolean[] showChannelPaths = new boolean[channelCount];
    // ^^^ STYLE ^^^

    private void initializeStyle () {
        for (int i = 0; i < channelCount; i++) {
            showChannelPaths[i] = false;
        }
    }

    public MachineSprite(float x, float y, float angle) {

        this.position.set(x, y);
        this.angle = angle;
        this.scale = 1.0f;

//        initializeChannelTypes();
//        initializeChannelDirections();
//        initializeChannelData();
        initializeStyle();

        //public String[] channelTypeColors = new String[] { "#efefef", "#1467f1", "#62df42", "#cc0033", "#ff9900" };
//        channelTypeColors.put(ChannelType.NONE, Color.parseColor("#efefef"));
//        channelTypeColors.put(ChannelType.SWITCH, Color.parseColor("#1467f1"));
//        channelTypeColors.put(ChannelType.PULSE, Color.parseColor("#62df42"));
//        channelTypeColors.put(ChannelType.WAVE, Color.parseColor("#cc0033"));
//        channelTypeColors.put(ChannelType.POWER, Color.parseColor("#ff9900"));
//        channelTypeColors.put(ChannelType.GROUND, Color.parseColor("#acacac"));

        // Port scopes
        for (int i = 0; i < channelCount; i++) {
            PortSprite portSprite = new PortSprite(this);
            portSprite.setPosition(this.position.x, this.position.y);
            portSprites.add(portSprite);
        }

//        // Channel node points
//        if (channelScopePositions.size() == 0) {
//            for (int i = 0; i < channelCount; i++) {
//                PointF point = new PointF();
//                point.x = this.position.x;
//                point.y = this.position.y;
//                channelScopePositions.add(point);
//            }
//        }
//        updatePortScopePositions(MapView);
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

    private void updatePortScopePositions(MapView mapView) {

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
                PointF portScopeSpritePosition = portSprites.get(3 * i + j).getPosition();

                // Translate (Nodes)
                float nodeRadiusPlusPadding = portSprite.shapeRadius + PortSprite.DISTANCE_BETWEEN_NODES;
                portScopeSpritePosition.x = ((-(nodeRadiusPlusPadding * 2.0f) + j * (nodeRadiusPlusPadding * 2.0f)));
                portScopeSpritePosition.y = (((boardWidth / 2.0f) + nodeRadiusPlusPadding)) + portSprite.shapeRadius;

                // Rotate (Nodes)
                portScopeSpritePosition.set(
                        portScopeSpritePosition.x * cosBoardFacingAngle - portScopeSpritePosition.y * sinBoardFacingAngle,
                        portScopeSpritePosition.x * sinBoardFacingAngle + portScopeSpritePosition.y * cosBoardFacingAngle
                );

                // Rotate (Machine)
                portScopeSpritePosition.set(
                        portScopeSpritePosition.x * cosBoardAngle - portScopeSpritePosition.y * sinBoardAngle,
                        portScopeSpritePosition.x * sinBoardAngle + portScopeSpritePosition.y * cosBoardAngle
                );

                // Translate (Machine)
                portScopeSpritePosition.x = portScopeSpritePosition.x + this.position.x;
                portScopeSpritePosition.y = portScopeSpritePosition.y + this.position.y;

//                // Scale (Map)
//                portScopeSpritePosition.x = portScopeSpritePosition.x * mapView.getScale();
//                portScopeSpritePosition.y = portScopeSpritePosition.y * mapView.getScale();

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

        // --- PORT SCOPES ---
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

                machineSprite.updatePortScopePositions(mapView); // TODO: Move this into step()/updateState()


                portSprite.draw(mapView);

                mapCanvas.restore();
            }

            mapCanvas.restore();
        }

        // TODO: Put this in/under PortSprite

        mapCanvas.restore();
        // ^^^ PORT SCOPES ^^^

        // TODO: Put this under PortSprite
        drawPaths(mapView);

        for (PortSprite portSprite : portSprites) {
            portSprite.drawCandidatePath(mapView);
        }
    }

    public void drawPaths(MapView mapView) {
        for (int j = 0; j < channelCount; j++) {
            for (int i = 0; i < portSprites.get(j).pathSprites.size(); i++) {
                if (this.showChannelPaths[j]) {
                    PathSprite pathSprite = portSprites.get(j).pathSprites.get(i);

                    pathSprite.draw(mapView);
                }
            }
        }
    }

    public void showPorts() {
        for (int i = 0; i < portSprites.size(); i++) {
            portSprites.get(i).setVisibility(true);
            this.portSprites.get(i).setPathVisibility(true);
        }
    }

    public void showPort(int channelIndex) {
        portSprites.get(channelIndex).setVisibility(true);
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
        for (int i = 0; i < this.showChannelPaths.length; i++) {
            this.showChannelPaths[i] = true;
        }
    }

    public void hidePaths() {
        for (int i = 0; i < this.showChannelPaths.length; i++) {
            this.showChannelPaths[i] = false;
            this.portSprites.get(i).showPathDocks();
        }
    }

    public void showPath(int pathIndex, boolean isFullPathVisible) {
        this.showChannelPaths[pathIndex] = true;
        if (isFullPathVisible) {
            this.portSprites.get(pathIndex).showPaths();
        } else {
            this.portSprites.get(pathIndex).showPathDocks();
        }
    }

    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
//        this.updatePortScopePositions();
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    //-------------------------
    // Interaction
    //-------------------------

    public boolean isTouching (PointF point) {
        return Geometry.calculateDistance((int) this.getPosition().x, (int) this.getPosition().y, point.x, point.y) < (this.boardWidth / 2.0f);
    }

    public static final String CLASS_NAME = "MACHINE_SPRITE";

    @Override
    public void onTouchAction(TouchAction touchAction) {

        if (touchAction.getType() == TouchAction.TouchActionType.NONE) {
            Log.v("onTouchAction", "TouchAction.NONE to " + CLASS_NAME);
        } else if (touchAction.getType() == TouchAction.TouchActionType.TOUCH) {
            Log.v("onTouchAction", "TouchAction.TOUCH to " + CLASS_NAME);
        } else if (touchAction.getType() == TouchAction.TouchActionType.TAP) {
            Log.v("onTouchAction", "TouchAction.TAP to " + CLASS_NAME);
        } else if (touchAction.getType() == TouchAction.TouchActionType.DOUBLE_DAP) {
            Log.v("onTouchAction", "TouchAction.DOUBLE_TAP to " + CLASS_NAME);
        } else if (touchAction.getType() == TouchAction.TouchActionType.HOLD) {
            Log.v("onTouchAction", "TouchAction.HOLD to " + CLASS_NAME);
        } else if (touchAction.getType() == TouchAction.TouchActionType.MOVE) {
            Log.v("onTouchAction", "TouchAction.MOVE to " + CLASS_NAME);
        } else if (touchAction.getType() == TouchAction.TouchActionType.PRE_DRAG) {
            Log.v("onTouchAction", "TouchAction.PRE_DRAG to " + CLASS_NAME);
        } else if (touchAction.getType() == TouchAction.TouchActionType.DRAG) {
            Log.v("onTouchAction", "TouchAction.DRAG to " + CLASS_NAME);
        } else if (touchAction.getType() == TouchAction.TouchActionType.RELEASE) {
            Log.v("onTouchAction", "TouchAction.RELEASE to " + CLASS_NAME);
        }
    }
}

