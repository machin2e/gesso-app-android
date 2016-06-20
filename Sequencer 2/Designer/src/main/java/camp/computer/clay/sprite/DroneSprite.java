package camp.computer.clay.sprite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.ArrayList;

import camp.computer.clay.sprite.util.Animation;
import camp.computer.clay.sprite.util.Geometry;

public class DroneSprite extends Sprite {

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

    public void setTransparency (final float transparency) {
        Animation.scaleValue(255.0f, 255.0f * transparency, 80, new Animation.OnScaleCallback() {
            @Override
            public void onScale(float currentScale) {
                int transparencyInteger = (int) currentScale;
                String transparencyString = String.format("%02x", transparencyInteger);
                // Drone color
                boardColor = Color.parseColor("#" + transparencyString + boardColorString);
                boardOutlineColor = Color.parseColor("#" + transparencyString + boardOutlineColorString);
                // Header color
                headerColor = Color.parseColor("#" + transparencyString + headerColorString);
                headerOutlineColor = Color.parseColor("#" + transparencyString + headerOutlineColorString);
            }
        });
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

    public DroneSprite(float x, float y, float angle) {

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
            PortSprite portSprite = new PortSprite();
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
        updateChannelScopePositions();
    }

    public int getChannelCount() {
        return channelCount;
    }

    public PortSprite getPortSprite (int index) {
        return this.portSprites.get(index);
    }

    private void updateChannelScopePositions() {

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

                // Rotate (Drone)
                portScopeSpritePosition.set(
                        portScopeSpritePosition.x * cosBoardAngle - portScopeSpritePosition.y * sinBoardAngle,
                        portScopeSpritePosition.x * sinBoardAngle + portScopeSpritePosition.y * cosBoardAngle
                );

                // Translate (Drone)
                portScopeSpritePosition.x = portScopeSpritePosition.x + this.position.x;
                portScopeSpritePosition.y = portScopeSpritePosition.y + this.position.y;

            }
        }
    }

    public void updateChannelData () {
        for (int j = 0; j < this.channelCount; j++) {
            this.portSprites.get(j).updateChannelData();
        }
    }

    public void draw(Canvas mapCanvas, Paint paint) {

        DroneSprite droneSprite = this;

        mapCanvas.save();

        mapCanvas.translate(droneSprite.getPosition().x, droneSprite.getPosition().y);
        mapCanvas.rotate(droneSprite.getAngle());

        mapCanvas.scale(droneSprite.getScale(), droneSprite.getScale());

        // --- BOARD HIGHLIGHT ---
        if (droneSprite.showHighlights) {
            mapCanvas.save();
            // Color
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(droneSprite.boardHighlightColor);
            mapCanvas.drawRect(
                    0 - (droneSprite.boardWidth / 2.0f) - droneSprite.boardHighlightThickness,
                    0 - (droneSprite.boardHeight / 2.0f) - droneSprite.boardHighlightThickness,
                    0 + (droneSprite.boardWidth / 2.0f) + droneSprite.boardHighlightThickness,
                    0 + (droneSprite.boardHeight / 2.0f) + droneSprite.boardHighlightThickness,
                    paint);
            mapCanvas.restore();
        }
        // ^^^ BOARD HIGHLIGHT ^^^

        // --- HEADER HIGLIGHT ---
        if (droneSprite.showHighlights) {
            for (int i = 0; i < 4; i++) {

                mapCanvas.save();

                mapCanvas.rotate(90 * i);
                mapCanvas.translate(0, 0);

                mapCanvas.save();
                mapCanvas.translate(
                        0,
                        (droneSprite.boardHeight / 2.0f) + (droneSprite.headerHeight / 2.0f)
                );
                mapCanvas.rotate(0);

                mapCanvas.save();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(droneSprite.boardHighlightColor);
                mapCanvas.drawRect(
                        0 - (droneSprite.headerWidth / 2.0f) - droneSprite.boardHighlightThickness,
                        0 - (droneSprite.headerHeight / 2.0f) - droneSprite.boardHighlightThickness,
                        0 + (droneSprite.headerWidth / 2.0f) + droneSprite.boardHighlightThickness,
                        0 + (droneSprite.headerHeight / 2.0f) + droneSprite.boardHighlightThickness,
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
        paint.setColor(droneSprite.boardColor);
        mapCanvas.drawRect(
                0 - (droneSprite.boardWidth / 2.0f),
                0 - (droneSprite.boardHeight / 2.0f),
                0 + (droneSprite.boardWidth / 2.0f),
                0 + (droneSprite.boardHeight / 2.0f),
                paint
        );
        // Outline
        if (droneSprite.showBoardOutline) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(droneSprite.boardOutlineColor);
            paint.setStrokeWidth(droneSprite.boardOutlineThickness);
            mapCanvas.drawRect(
                    0 - (droneSprite.boardWidth / 2.0f),
                    0 - (droneSprite.boardHeight / 2.0f),
                    0 + (droneSprite.boardWidth / 2.0f),
                    0 + (droneSprite.boardHeight / 2.0f),
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
                    (droneSprite.boardHeight / 2.0f) + (droneSprite.headerHeight / 2.0f)
            );
            mapCanvas.rotate(0);

            mapCanvas.save();
            // Color
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(droneSprite.headerColor);
            mapCanvas.drawRect(
                    0 - (droneSprite.headerWidth / 2.0f),
                    0 - (droneSprite.headerHeight / 2.0f),
                    0 + (droneSprite.headerWidth / 2.0f),
                    0 + (droneSprite.headerHeight / 2.0f),
                    paint
            );
            // Outline
            if (droneSprite.showHeaderOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(droneSprite.headerOutlineThickness);
                paint.setColor(droneSprite.headerOutlineColor);
                mapCanvas.drawRect(
                        0 - (droneSprite.headerWidth / 2.0f),
                        0 - (droneSprite.headerHeight / 2.0f),
                        0 + (droneSprite.headerWidth / 2.0f),
                        0 + (droneSprite.headerHeight / 2.0f),
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
                        (droneSprite.boardWidth / 2.0f) - (droneSprite.lightHeight / 2.0f) - droneSprite.distanceLightsToEdge
                );
                mapCanvas.rotate(0);

                mapCanvas.save();
                // Color
                paint.setStyle(Paint.Style.FILL);
                paint.setStrokeWidth(3);
                //paint.setColor(droneSprite.channelTypeColors.get(droneSprite.channelTypes.get(3 * i + j)));
                if (droneSprite.portSprites.get(3 * i + j).portType != PortSprite.PortType.NONE) {
                    paint.setColor(droneSprite.getPortSprite(3 * i + j).getUniqueColor());
                } else {
                    paint.setColor(PortSprite.FLOW_PATH_COLOR_NONE);
                }
                mapCanvas.drawRoundRect(
                        0 - (droneSprite.lightWidth / 2.0f),
                        0 - (droneSprite.lightHeight / 2.0f),
                        0 + (droneSprite.lightWidth / 2.0f),
                        0 + (droneSprite.lightHeight / 2.0f),
                        5.0f,
                        5.0f,
                        paint
                );
                // Outline
                if (droneSprite.showLightOutline) {
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(droneSprite.lightOutlineThickness);
                    paint.setColor(droneSprite.lightOutlineColor);
                    mapCanvas.drawRoundRect(
                            0 - (droneSprite.lightWidth / 2.0f),
                            0 - (droneSprite.lightHeight / 2.0f),
                            0 + (droneSprite.lightWidth / 2.0f),
                            0 + (droneSprite.lightHeight / 2.0f),
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

        mapCanvas.translate(droneSprite.getPosition().x, droneSprite.getPosition().y);
        mapCanvas.rotate(droneSprite.getAngle());

        mapCanvas.scale(droneSprite.getScale(), droneSprite.getScale());

        for (int i = 0; i < 4; i++) {

            mapCanvas.save();

            mapCanvas.rotate(-90 * i);
            mapCanvas.translate(0, 0);

            for (int j = 0; j < 3; j++) {

                PortSprite portSprite = portSprites.get(3 * i + j);

//                mapCanvas.save();
//                mapCanvas.translate(
//                        -((portSprite.shapeRadius + PortSprite.DISTANCE_BETWEEN_NODES) * 2.0f) + j * ((portSprite.shapeRadius + PortSprite.DISTANCE_BETWEEN_NODES) * 2),
//                        (droneSprite.boardWidth / 2.0f) + portSprite.shapeRadius + PortSprite.DISTANCE_FROM_BOARD
//                );
//                mapCanvas.rotate(0);

                mapCanvas.save();
                mapCanvas.translate(
                        -((portSprite.shapeRadius + PortSprite.DISTANCE_BETWEEN_NODES) * 2.0f) + j * ((portSprite.shapeRadius + PortSprite.DISTANCE_BETWEEN_NODES) * 2),
                        (droneSprite.boardWidth / 2.0f) + portSprite.shapeRadius + PortSprite.DISTANCE_FROM_BOARD
                );
                if (droneSprite.portSprites.get(3 * i + j).portDirection == PortSprite.PortDirection.OUTPUT) {
                    mapCanvas.rotate(180.0f);
                } else {
                    mapCanvas.rotate(0.0f);
                }

                droneSprite.updateChannelScopePositions(); // TODO: Move this into step()/updateState()


                portSprite.draw(mapCanvas, paint);

                mapCanvas.restore();
            }

            mapCanvas.restore();
        }

        mapCanvas.restore();
        // ^^^ PORT SCOPES ^^^

        drawPaths(mapCanvas, paint);
    }

    public void drawPaths(Canvas mapCanvas, Paint paint) {
        // --- PATH ---

        for (int j = 0; j < channelCount; j++) {
            for (int i = 0; i < portSprites.get(j).pathSprites.size(); i++) {
                if (this.showChannelPaths[j]) {
                    PathSprite pathSprite = portSprites.get(j).pathSprites.get(i);

                    pathSprite.draw(mapCanvas, paint);
                }
            }
        }
        // ^^^ PATH ^^^
    }

    public void showChannelScopes() {
        for (int i = 0; i < portSprites.size(); i++) {
            portSprites.get(i).setVisibility(true);
            this.portSprites.get(i).setPathVisibility(true);
        }
    }

    public void showChannelScope (int channelIndex) {
        portSprites.get(channelIndex).setVisibility(true);
        this.portSprites.get(channelIndex).setPathVisibility(true);
    }

    public void hideChannelScopes() {
        for (int i = 0; i < portSprites.size(); i++) {
            portSprites.get(i).setVisibility(false);
            this.portSprites.get(i).setPathVisibility(false);
        }
    }

    private void hideChannelScope (int channelIndex) {
        portSprites.get(channelIndex).setVisibility(false);
        this.portSprites.get(channelIndex).setPathVisibility(false);
    }

    public void showChannelPaths() {
        for (int i = 0; i < this.showChannelPaths.length; i++) {
            this.showChannelPaths[i] = true;
        }
    }

    public void hideChannelPaths() {
        for (int i = 0; i < this.showChannelPaths.length; i++) {
            this.showChannelPaths[i] = false;
            this.portSprites.get(i).showPathDocks();
        }
    }

    public void showChannelPath(int destinationChannel, boolean showFullPath) {
        this.showChannelPaths[destinationChannel] = true;
        if (showFullPath) {
            this.portSprites.get(destinationChannel).showPaths();
        } else {
            this.portSprites.get(destinationChannel).showPathDocks();
        }
    }

    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
        this.updateChannelScopePositions();
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    //-------------------------
    // Interaction
    //-------------------------

    public boolean isTouching (PointF point) {
        return Geometry.getDistance((int) this.getPosition().x, (int) this.getPosition().y, point.x, point.y) < (this.boardWidth / 2.0f);
    }
}

