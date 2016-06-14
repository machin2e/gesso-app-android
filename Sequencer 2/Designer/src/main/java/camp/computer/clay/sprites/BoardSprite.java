package camp.computer.clay.sprites;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class BoardSprite {

    private int channelCount = 12;

    private PointF position = new PointF(); // Sprite position
    private float scale = 1.0f; // Sprite scale factor
    private float angle; // Sprite heading angle

    public ArrayList<PointF> channelScopePositions = new ArrayList<PointF>();
    public ArrayList<ChannelType> channelTypes = new ArrayList<ChannelType>();
    public ArrayList<ChannelDirection> channelDirections = new ArrayList<>();

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

    float headerWidth = 60;
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
    int channelColorNone = Color.parseColor("#efefef");
    int[] channelColorPalette = new int[] { Color.parseColor("#19B5FE"), Color.parseColor("#2ECC71"), Color.parseColor("#F22613"), Color.parseColor("#F9690E"), Color.parseColor("#9A12B3"), Color.parseColor("#F9BF3B"), Color.parseColor("#DB0A5B"), Color.parseColor("#BF55EC"), Color.parseColor("#A2DED0"), Color.parseColor("#1E8BC3"), Color.parseColor("#36D7B7"), Color.parseColor("#EC644B") };
    boolean showLightOutline = true;
    int lightOutlineColor = Color.parseColor("#212121");
    float lightOutlineThickness = 0.0f;

    public boolean showChannelScopes = false;
    boolean showChannelLabel = false;
    boolean showChannelData = true;
    boolean showChannelNodeOutline = false;
    float labelTextSize = 30.0f;
    float channelNodeRadius = 40.0f;
    private Map<ChannelType, Integer> channelTypeColors = new HashMap<ChannelType, Integer>();
    // String[] channelNodeColor = new String[] { "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1", "#1467f1" };
    float distanceNodeToBoard = 45.0f;
    float distanceBetweenNodes = 5.0f;
    float[][] channelDataPoints = new float[channelCount][(int) channelNodeRadius * 2];
    // ^^^ STYLE ^^^

    public BoardSprite(float x, float y, float angle) {

        this.position.set(x, y);
        this.angle = angle;
        this.scale = 1.0f;

        initializeChannelTypes();
        initializeChannelDirections();
        initializeChannelData();

        //public String[] channelTypeColors = new String[] { "#efefef", "#1467f1", "#62df42", "#cc0033", "#ff9900" };
        channelTypeColors.put(ChannelType.NONE, Color.parseColor("#efefef"));
        channelTypeColors.put(ChannelType.SWITCH, Color.parseColor("#1467f1"));
        channelTypeColors.put(ChannelType.PULSE, Color.parseColor("#62df42"));
        channelTypeColors.put(ChannelType.WAVE, Color.parseColor("#cc0033"));
        channelTypeColors.put(ChannelType.POWER, Color.parseColor("#ff9900"));
        channelTypeColors.put(ChannelType.GROUND, Color.parseColor("#acacac"));

        // Channel node points
        if (channelScopePositions.size() == 0) {
            for (int i = 0; i < channelCount; i++) {
                PointF point = new PointF();
                point.x = this.position.x;
                point.y = this.position.y;
                channelScopePositions.add(point);
            }
        }
        updateChannelScopePositions();
    }

    public enum ChannelDirection {

        NONE(0),
        OUTPUT(1),
        INPUT(2);

        // TODO: Change the index to a UUID?
        int index;

        ChannelDirection(int index) {
            this.index = index;
        }
    }

    public enum ChannelType {

        NONE(0),
        SWITCH(1),
        PULSE(2),
        WAVE(3),
        POWER(4),
        GROUND(5);

        // TODO: Change the index to a UUID?
        int index;

        ChannelType(int index) {
            this.index = index;
        }

        public static ChannelType getNextType(ChannelType currentChannelType) {
            return ChannelType.values()[(currentChannelType.index + 1) % ChannelType.values().length];
        }
    }

    private void initializeChannelTypes() {
        channelTypes.clear();
        for (int i = 0; i < channelCount; i++) {
            channelTypes.add(ChannelType.NONE); // 0 for "none" (disabled)
        }
    }

    private void initializeChannelDirections() {
        channelDirections.clear();
        for (int i = 0; i < channelCount; i++) {
            channelDirections.add(ChannelDirection.NONE); // 0 for "none" (disabled)
        }
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

                PointF point = channelScopePositions.get(3 * i + j);

                // Translate (Nodes)
                float nodeRadiusPlusPadding = channelNodeRadius + distanceBetweenNodes;
                point.x = ((-(nodeRadiusPlusPadding * 2.0f) + j * (nodeRadiusPlusPadding * 2.0f)));
                point.y = (((boardWidth / 2.0f) + nodeRadiusPlusPadding));

                // Rotate (Nodes)
                point.set(
                        point.x * cosBoardFacingAngle - point.y * sinBoardFacingAngle,
                        point.x * sinBoardFacingAngle + point.y * cosBoardFacingAngle
                );

                // Rotate (Board)
                point.set(
                        point.x * cosBoardAngle - point.y * sinBoardAngle,
                        point.x * sinBoardAngle + point.y * cosBoardAngle
                );

                // Translate (Board)
                point.x = point.x + this.position.x;
                point.y = point.y + this.position.y;

            }
        }
    }

    public void initializeChannelData () {
        for (int j = 0; j < this.channelCount; j++) {
            for (int i = 0; i < this.channelDataPoints.length; i++) {
                this.channelDataPoints[j][i] = -(this.channelNodeRadius / 2.0f) + 0;
            }
        }
    }

    private float xWaveStart = 0;
    public void updateChannelData () {
        for (int j = 0; j < this.channelCount; j++) {
            Random random = new Random();
            if (this.channelTypes.get(j) == ChannelType.SWITCH) {
                // Add new sample for the channel type
                int squareWidth = 20;
                float sample = -(this.channelNodeRadius / 2.0f) + random.nextInt(2) * this.channelNodeRadius;
                for (int k = this.channelDataPoints[j].length - squareWidth; k < this.channelDataPoints[j].length; k++) {
                    this.channelDataPoints[j][k] = sample;
                }
                // Shift data
                for (int k = 0; k < this.channelDataPoints[j].length - 1; k++) {
                    this.channelDataPoints[j][k] = this.channelDataPoints[j][k + 1];
                }
            } else if (this.channelTypes.get(j) == ChannelType.PULSE) {
                // Shift data
                for (int k = 0; k < this.channelDataPoints[j].length - 1; k++) {
                    this.channelDataPoints[j][k] = this.channelDataPoints[j][k + 1];
                }
                // Add new sample for the channel type
                this.channelDataPoints[j][this.channelDataPoints[j].length - 1] = -(this.channelNodeRadius / 2.0f) + random.nextInt(2) * this.channelNodeRadius;
            } else if (this.channelTypes.get(j) == ChannelType.WAVE) {
                // Shift data
                for (int k = 0; k < this.channelDataPoints[j].length; k++) {
                    // Add new sample for the channel type
                    this.channelDataPoints[j][k] = ((float) Math.sin(xWaveStart + k * 0.2)) * this.channelNodeRadius * 0.5f;
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
                if (boardSprite.channelTypes.get(3 * i + j) != ChannelType.NONE) {
                    paint.setColor(channelColorPalette[3 * i + j]);
                } else {
                    paint.setColor(channelColorNone);
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

        // --- NODES ---

        if (boardSprite.showChannelScopes) {

            for (int i = 0; i < 4; i++) {

                mapCanvas.save();

                mapCanvas.rotate(-90 * i);
                mapCanvas.translate(0, 0);

                for (int j = 0; j < 3; j++) {

                    mapCanvas.save();
                    mapCanvas.translate(
                            -((boardSprite.channelNodeRadius + boardSprite.distanceBetweenNodes) * 2.0f) + j * ((boardSprite.channelNodeRadius + boardSprite.distanceBetweenNodes) * 2),
                            (boardSprite.boardWidth / 2.0f) + boardSprite.channelNodeRadius + boardSprite.distanceNodeToBoard
                    );
                    mapCanvas.rotate(0);

                    mapCanvas.save();
                    // Color
                    paint.setStyle(Paint.Style.FILL);
                    // paint.setColor(boardSprite.channelTypeColors.get(boardSprite.channelTypes.get(3 * i + j)));
                    if (boardSprite.channelTypes.get(3 * i + j) != ChannelType.NONE) {
                        paint.setColor(channelColorPalette[3 * i + j]);
                    } else {
                        paint.setColor(channelColorNone);
                    }
                    boardSprite.updateChannelScopePositions();
                    mapCanvas.drawCircle(
                            0,
                            0,
                            boardSprite.channelNodeRadius,
                            paint
                    );
                    // Outline
                    if (boardSprite.showChannelNodeOutline) {
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(3);
                        paint.setColor(Color.BLACK);
                        mapCanvas.drawCircle(
                                0,
                                0,
                                boardSprite.channelNodeRadius,
                                paint
                        );
                    }
                    // Label
                    if (boardSprite.showChannelLabel) {
                        paint.setTextSize(boardSprite.labelTextSize);
                        Rect textBounds = new Rect();
                        String channelNumberText = String.valueOf(3 * i + j + 1);
                        paint.getTextBounds(channelNumberText, 0, channelNumberText.length(), textBounds);
                        paint.setStyle(Paint.Style.FILL);
                        paint.setStrokeWidth(3);
                        paint.setColor(Color.BLACK);
                        mapCanvas.drawText(channelNumberText, -(textBounds.width() / 2.0f), textBounds.height() / 2.0f, paint);
                    }
                    // Outline
                    if (boardSprite.showChannelData) {
                        if (boardSprite.channelTypes.get(3 * i + j) != ChannelType.NONE) {
                            paint.setStyle(Paint.Style.STROKE);
                            paint.setStrokeWidth(2.0f);
                            paint.setColor(Color.WHITE);
                            int step = 1;
                            for (int k = 0; k + step < boardSprite.channelDataPoints[3 * i + j].length - 1; k += step) {
                                mapCanvas.drawLine(
                                        boardSprite.channelDataPoints[3 * i + j][k],
                                        -boardSprite.channelNodeRadius + k,
                                        boardSprite.channelDataPoints[3 * i + j][k + step],
                                        -boardSprite.channelNodeRadius + k + step,
                                        paint
                                );
                            }
                        }
                    }
                    mapCanvas.restore();

                    mapCanvas.restore();

                }

                mapCanvas.restore();

            }
        }
        // ^^^ NODES ^^^

        mapCanvas.restore();

    }

    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
        this.updateChannelScopePositions();
    }

    public void setScale(float scale) {
        this.scale = scale;
    }
}

