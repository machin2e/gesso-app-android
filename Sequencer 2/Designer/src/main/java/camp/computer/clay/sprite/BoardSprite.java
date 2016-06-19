package camp.computer.clay.sprite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Random;

import camp.computer.clay.sprite.util.FlowPathSprite;
import camp.computer.clay.sprite.util.Geometry;

public class BoardSprite extends Sprite {

    private int channelCount = 12;

    private PointF position = new PointF(); // Sprite position
    private float scale = 1.0f; // Sprite scale factor
    private float angle; // Sprite heading angle

    public ArrayList<PortScopeSprite> portScopeSprites = new ArrayList<PortScopeSprite>();

//    public ArrayList<PointF> channelScopePositions = new ArrayList<PointF>();
//    public ArrayList<ChannelType> channelTypes = new ArrayList<ChannelType>();
//    public ArrayList<ChannelDirection> channelDirections = new ArrayList<>();
//
//    public void addPath(BoardSprite touchedBoardSpriteSource, int touchedChannelScopeSource, BoardSprite touchedBoardSpriteDestination, int touchedChannelScopeDestination) {
//        FlowPath channelPath = new FlowPath();
//        channelPath.source = touchedBoardSpriteSource;
//        channelPath.sourceChannel = touchedChannelScopeSource;
//        channelPath.destination = touchedBoardSpriteDestination;
//        channelPath.destinationChannel = touchedChannelScopeDestination;
//        flowPathSprites.add(channelPath);
//    }
//
//    public class FlowPath {
//        BoardSprite source;
//        int sourceChannel;
//        BoardSprite destination;
//        int destinationChannel;
//    }
//
//    public ArrayList<FlowPath> flowPathSprites = new ArrayList<FlowPath>();

    public PointF getPosition() {
        return this.position;
    }

    public float getAngle() {
        return this.angle;
    }

    public float getScale() {
        return this.scale;
    }

    public void setTransparency (float transparency) {
        int transparencyInteger = (int) (255.0f * transparency);
        String transparencyString = String.format("%02x", transparencyInteger);
        // Board color
        this.boardColor = Color.parseColor("#" + transparencyString + boardColorString);
        this.boardOutlineColor = Color.parseColor("#" + transparencyString + boardOutlineColorString);
        // Header color
        this.headerColor = Color.parseColor("#" + transparencyString + headerColorString);
        this.headerOutlineColor = Color.parseColor("#" + transparencyString + headerOutlineColorString);
    }

    // --- STYLE ---
    // TODO: Make these private once the map is working well and the sprite is working well.
    public float boardWidth = 250;
    float boardHeight = 250;
    private String boardColorString = "ffffff"; // "414141";
    private int boardColor = Color.parseColor("#ff" + boardColorString); // Color.parseColor("#212121");
    boolean showBoardOutline = true;
    private String boardOutlineColorString = "212121";
    int boardOutlineColor = Color.parseColor("#ff" + boardOutlineColorString); // Color.parseColor("#737272");
    float boardOutlineThickness = 1.0f;

    float headerWidth = 40;
    float headerHeight = 15;
    private String headerColorString = "000000";
    int headerColor = Color.parseColor("#ff" + headerColorString);
    boolean showHeaderOutline = false;
    private String headerOutlineColorString = "000000";
    int headerOutlineColor = Color.parseColor("#ff" + headerOutlineColorString);
    float headerOutlineThickness = boardOutlineThickness;

    public boolean showHighlights = false;
    int boardHighlightColor = Color.parseColor("#1976D2");
    float boardHighlightThickness = 20;

    float distanceLightsToEdge = 15.0f;
    float lightWidth = 12;
    float lightHeight = 20;
    public static int CHANNEL_COLOR_OFF = Color.parseColor("#efefef");
    public static int[] CHANNEL_COLOR_PALETTE = new int[] { Color.parseColor("#19B5FE"), Color.parseColor("#2ECC71"), Color.parseColor("#F22613"), Color.parseColor("#F9690E"), Color.parseColor("#9A12B3"), Color.parseColor("#F9BF3B"), Color.parseColor("#DB0A5B"), Color.parseColor("#BF55EC"), Color.parseColor("#A2DED0"), Color.parseColor("#1E8BC3"), Color.parseColor("#36D7B7"), Color.parseColor("#EC644B") };
    boolean showLightOutline = true;
    int lightOutlineColor = Color.parseColor("#212121");
    float lightOutlineThickness = 0.0f;

//    public boolean[] showChannelScopes = new boolean[channelCount];
//    boolean showChannelLabel = false;
//    boolean showChannelData = true;
//    boolean showChannelNodeOutline = false;
//    float labelTextSize = 30.0f;
//    float channelNodeRadius = 40.0f;
//    private Map<ChannelType, Integer> channelTypeColors = new HashMap<ChannelType, Integer>();
    // String[] channelNodeColor = new String[] { "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1" };
//    float DISTANCE_FROM_BOARD = 45.0f;
//    float DISTANCE_BETWEEN_NODES = 5.0f;
//    float[][] channelDataPoints = new float[channelCount][(int) channelNodeRadius * 2];

    public boolean[] showChannelPaths = new boolean[channelCount];
    // ^^^ STYLE ^^^

    private void initializeStyle () {
        for (int i = 0; i < channelCount; i++) {
            showChannelPaths[i] = false;
        }
    }

    public BoardSprite(float x, float y, float angle) {

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
            PortScopeSprite portScopeSprite = new PortScopeSprite();
            portScopeSprite.setPosition(this.position.x, this.position.y);
            portScopeSprites.add(portScopeSprite);
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

                PortScopeSprite portScopeSprite = portScopeSprites.get(3 * i + j);
                PointF portScopeSpritePosition = portScopeSprites.get(3 * i + j).getPosition();

                // Translate (Nodes)
                float nodeRadiusPlusPadding = portScopeSprite.channelNodeRadius + PortScopeSprite.DISTANCE_BETWEEN_NODES;
                portScopeSpritePosition.x = ((-(nodeRadiusPlusPadding * 2.0f) + j * (nodeRadiusPlusPadding * 2.0f)));
                portScopeSpritePosition.y = (((boardWidth / 2.0f) + nodeRadiusPlusPadding)) + portScopeSprite.channelNodeRadius;

                // Rotate (Nodes)
                portScopeSpritePosition.set(
                        portScopeSpritePosition.x * cosBoardFacingAngle - portScopeSpritePosition.y * sinBoardFacingAngle,
                        portScopeSpritePosition.x * sinBoardFacingAngle + portScopeSpritePosition.y * cosBoardFacingAngle
                );

                // Rotate (Board)
                portScopeSpritePosition.set(
                        portScopeSpritePosition.x * cosBoardAngle - portScopeSpritePosition.y * sinBoardAngle,
                        portScopeSpritePosition.x * sinBoardAngle + portScopeSpritePosition.y * cosBoardAngle
                );

                // Translate (Board)
                portScopeSpritePosition.x = portScopeSpritePosition.x + this.position.x;
                portScopeSpritePosition.y = portScopeSpritePosition.y + this.position.y;

            }
        }
    }

    private float xWaveStart = 0;
    public void updateChannelData () {
        for (int j = 0; j < this.channelCount; j++) {
            Random random = new Random();
            PortScopeSprite portScopeSprite = this.portScopeSprites.get(j);
            if (portScopeSprite.channelType == PortScopeSprite.ChannelType.SWITCH) {
                // Add new sample for the channel type
                int squareWidth = 20;
                float sample = -(portScopeSprite.channelNodeRadius / 2.0f) + random.nextInt(2) * portScopeSprite.channelNodeRadius;
                for (int k = portScopeSprite.channelDataPoints.length - squareWidth; k < portScopeSprite.channelDataPoints.length; k++) {
                    portScopeSprite.channelDataPoints[k] = sample;
                }
                // Shift data
                for (int k = 0; k < portScopeSprite.channelDataPoints.length - 1; k++) {
                    portScopeSprite.channelDataPoints[k] = portScopeSprite.channelDataPoints[k + 1];
                }
            } else if (this.portScopeSprites.get(j).channelType == PortScopeSprite.ChannelType.PULSE) {
                // Shift data
                for (int k = 0; k < portScopeSprite.channelDataPoints.length - 1; k++) {
                    portScopeSprite.channelDataPoints[k] = portScopeSprite.channelDataPoints[k + 1];
                }
                // Add new sample for the channel type
                portScopeSprite.channelDataPoints[portScopeSprite.channelDataPoints.length - 1] = -(portScopeSprite.channelNodeRadius / 2.0f) + random.nextInt(2) * portScopeSprite.channelNodeRadius;
            } else if (this.portScopeSprites.get(j).channelType == PortScopeSprite.ChannelType.WAVE) {
                // Shift data
                for (int k = 0; k < portScopeSprite.channelDataPoints.length; k++) {
                    // Add new sample for the channel type
                    portScopeSprite.channelDataPoints[k] = ((float) Math.sin(xWaveStart + k * 0.2)) * portScopeSprite.channelNodeRadius * 0.5f;
                }
                //xWaveStart = (xWaveStart + ((2.0f * (float) Math.PI) / ((float) this.channelDataPoints[j].length))) % (2.0f * (float) Math.PI);
                xWaveStart = (xWaveStart + 0.9f) % ((float) Math.PI * 2.0f);
            }
        }
    }

    public void draw(Canvas mapCanvas, Paint paint) {

        BoardSprite boardSprite = this;

        mapCanvas.save();

        mapCanvas.translate(boardSprite.getPosition().x, boardSprite.getPosition().y);
        mapCanvas.rotate(boardSprite.getAngle());

        mapCanvas.scale(boardSprite.getScale(), boardSprite.getScale());

        // --- BOARD HIGHLIGHT ---
        if (boardSprite.showHighlights) {
            mapCanvas.save();
            // Color
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(boardSprite.boardHighlightColor);
            mapCanvas.drawRect(
                    0 - (boardSprite.boardWidth / 2.0f) - boardSprite.boardHighlightThickness,
                    0 - (boardSprite.boardHeight / 2.0f) - boardSprite.boardHighlightThickness,
                    0 + (boardSprite.boardWidth / 2.0f) + boardSprite.boardHighlightThickness,
                    0 + (boardSprite.boardHeight / 2.0f) + boardSprite.boardHighlightThickness,
                    paint);
            mapCanvas.restore();
        }
        // ^^^ BOARD HIGHLIGHT ^^^

        // --- HEADER HIGLIGHT ---
        if (boardSprite.showHighlights) {
            for (int i = 0; i < 4; i++) {

                mapCanvas.save();

                mapCanvas.rotate(90 * i);
                mapCanvas.translate(0, 0);

                mapCanvas.save();
                mapCanvas.translate(
                        0,
                        (boardSprite.boardHeight / 2.0f) + (boardSprite.headerHeight / 2.0f)
                );
                mapCanvas.rotate(0);

                mapCanvas.save();
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(boardSprite.boardHighlightColor);
                mapCanvas.drawRect(
                        0 - (boardSprite.headerWidth / 2.0f) - boardSprite.boardHighlightThickness,
                        0 - (boardSprite.headerHeight / 2.0f) - boardSprite.boardHighlightThickness,
                        0 + (boardSprite.headerWidth / 2.0f) + boardSprite.boardHighlightThickness,
                        0 + (boardSprite.headerHeight / 2.0f) + boardSprite.boardHighlightThickness,
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
        paint.setColor(boardSprite.boardColor);
        mapCanvas.drawRect(
                0 - (boardSprite.boardWidth / 2.0f),
                0 - (boardSprite.boardHeight / 2.0f),
                0 + (boardSprite.boardWidth / 2.0f),
                0 + (boardSprite.boardHeight / 2.0f),
                paint
        );
        // Outline
        if (boardSprite.showBoardOutline) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(boardSprite.boardOutlineColor);
            paint.setStrokeWidth(boardSprite.boardOutlineThickness);
            mapCanvas.drawRect(
                    0 - (boardSprite.boardWidth / 2.0f),
                    0 - (boardSprite.boardHeight / 2.0f),
                    0 + (boardSprite.boardWidth / 2.0f),
                    0 + (boardSprite.boardHeight / 2.0f),
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
                    (boardSprite.boardHeight / 2.0f) + (boardSprite.headerHeight / 2.0f)
            );
            mapCanvas.rotate(0);

            mapCanvas.save();
            // Color
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(boardSprite.headerColor);
            mapCanvas.drawRect(
                    0 - (boardSprite.headerWidth / 2.0f),
                    0 - (boardSprite.headerHeight / 2.0f),
                    0 + (boardSprite.headerWidth / 2.0f),
                    0 + (boardSprite.headerHeight / 2.0f),
                    paint
            );
            // Outline
            if (boardSprite.showHeaderOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(boardSprite.headerOutlineThickness);
                paint.setColor(boardSprite.headerOutlineColor);
                mapCanvas.drawRect(
                        0 - (boardSprite.headerWidth / 2.0f),
                        0 - (boardSprite.headerHeight / 2.0f),
                        0 + (boardSprite.headerWidth / 2.0f),
                        0 + (boardSprite.headerHeight / 2.0f),
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
                        (boardSprite.boardWidth / 2.0f) - (boardSprite.lightHeight / 2.0f) - boardSprite.distanceLightsToEdge
                );
                mapCanvas.rotate(0);

                mapCanvas.save();
                // Color
                paint.setStyle(Paint.Style.FILL);
                paint.setStrokeWidth(3);
                //paint.setColor(boardSprite.channelTypeColors.get(boardSprite.channelTypes.get(3 * i + j)));
                if (boardSprite.portScopeSprites.get(3 * i + j).channelType != PortScopeSprite.ChannelType.NONE) {
                    paint.setColor(CHANNEL_COLOR_PALETTE[3 * i + j]);
                } else {
                    paint.setColor(CHANNEL_COLOR_OFF);
                }
                mapCanvas.drawRoundRect(
                        0 - (boardSprite.lightWidth / 2.0f),
                        0 - (boardSprite.lightHeight / 2.0f),
                        0 + (boardSprite.lightWidth / 2.0f),
                        0 + (boardSprite.lightHeight / 2.0f),
                        5.0f,
                        5.0f,
                        paint
                );
                // Outline
                if (boardSprite.showLightOutline) {
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(boardSprite.lightOutlineThickness);
                    paint.setColor(boardSprite.lightOutlineColor);
                    mapCanvas.drawRoundRect(
                            0 - (boardSprite.lightWidth / 2.0f),
                            0 - (boardSprite.lightHeight / 2.0f),
                            0 + (boardSprite.lightWidth / 2.0f),
                            0 + (boardSprite.lightHeight / 2.0f),
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

        mapCanvas.translate(boardSprite.getPosition().x, boardSprite.getPosition().y);
        mapCanvas.rotate(boardSprite.getAngle());

        mapCanvas.scale(boardSprite.getScale(), boardSprite.getScale());

        for (int i = 0; i < 4; i++) {

            mapCanvas.save();

            mapCanvas.rotate(-90 * i);
            mapCanvas.translate(0, 0);

            for (int j = 0; j < 3; j++) {

                PortScopeSprite portScopeSprite = portScopeSprites.get(3 * i + j);

//                mapCanvas.save();
//                mapCanvas.translate(
//                        -((portScopeSprite.channelNodeRadius + PortScopeSprite.DISTANCE_BETWEEN_NODES) * 2.0f) + j * ((portScopeSprite.channelNodeRadius + PortScopeSprite.DISTANCE_BETWEEN_NODES) * 2),
//                        (boardSprite.boardWidth / 2.0f) + portScopeSprite.channelNodeRadius + PortScopeSprite.DISTANCE_FROM_BOARD
//                );
//                mapCanvas.rotate(0);

                mapCanvas.save();
                mapCanvas.translate(
                        -((portScopeSprite.channelNodeRadius + PortScopeSprite.DISTANCE_BETWEEN_NODES) * 2.0f) + j * ((portScopeSprite.channelNodeRadius + PortScopeSprite.DISTANCE_BETWEEN_NODES) * 2),
                        (boardSprite.boardWidth / 2.0f) + portScopeSprite.channelNodeRadius + PortScopeSprite.DISTANCE_FROM_BOARD
                );
                if (boardSprite.portScopeSprites.get(3 * i + j).channelDirection == PortScopeSprite.ChannelDirection.OUTPUT) {
                    mapCanvas.rotate(180.0f);
                } else {
                    mapCanvas.rotate(0.0f);
                }

                boardSprite.updateChannelScopePositions(); // TODO: Move this into step()/updateState()


                portScopeSprite.draw(mapCanvas, paint);

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
            for (int i = 0; i < portScopeSprites.get(j).flowPathSprites.size(); i++) {
                if (this.showChannelPaths[j]) {
                    FlowPathSprite flowPathSprite = portScopeSprites.get(j).flowPathSprites.get(i);

                    flowPathSprite.draw(mapCanvas, paint);
                }
            }
        }
        // ^^^ PATH ^^^
    }

    public void showChannelScopes() {
        for (int i = 0; i < portScopeSprites.size(); i++) {
            portScopeSprites.get(i).showChannelScopes = true;
        }
    }

    public void showChannelScope (int channelIndex) {
        portScopeSprites.get(channelIndex).showChannelScopes = true;
    }

    public void hideChannelScopes() {
        for (int i = 0; i < portScopeSprites.size(); i++) {
            portScopeSprites.get(i).showChannelScopes = false;
        }
    }

    private void hideChannelScope (int channelIndex) {
        portScopeSprites.get(channelIndex).showChannelScopes = false;
    }

    public void showChannelPaths() {
        for (int i = 0; i < this.showChannelPaths.length; i++) {
            this.showChannelPaths[i] = true;
        }
    }

    public void hideChannelPaths() {
        for (int i = 0; i < this.showChannelPaths.length; i++) {
            this.showChannelPaths[i] = false;
            this.portScopeSprites.get(i).showPartialPaths();
        }
    }

    public void showChannelPath(int destinationChannel, boolean showFullPath) {
        this.showChannelPaths[destinationChannel] = true;
        if (showFullPath) {
            this.portScopeSprites.get(destinationChannel).showFullPaths();
        } else {
            this.portScopeSprites.get(destinationChannel).showPartialPaths();
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

