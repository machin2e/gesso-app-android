package camp.computer.clay.sprite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;

import java.util.ArrayList;

import camp.computer.clay.sprite.util.FlowPathSprite;

public class PortScopeSprite extends Sprite {

    // --- DATA ---

    public int channelSampleSize = 40;
    public float[] channelDataPoints = new float[channelSampleSize * 2];

    // ^^^ DATA ---

    // --- STYLE ---

    public static float DISTANCE_FROM_BOARD = 45.0f;
    public static float DISTANCE_BETWEEN_NODES = 5.0f;

    public boolean showChannelScopes = false;
    public float channelNodeRadius = 40.0f;

    boolean showChannelLabel = false;
    boolean showChannelData = true;
    boolean showChannelNodeOutline = false;
    float labelTextSize = 30.0f;

    // ^^^ STYLE ^^^

    public ArrayList<FlowPathSprite> flowPathSprites = new ArrayList<FlowPathSprite>();

    private PointF position = new PointF(0, 0); // Sprite position
    private float scale = 1.0f; // Sprite scale factor
    private float angle = 0.0f; // Sprite heading angle

//    public ArrayList<PointF> channelScopePositions = new ArrayList<PointF>();
    public ChannelType channelType = PortScopeSprite.ChannelType.NONE;
    public ChannelDirection channelDirection = PortScopeSprite.ChannelDirection.NONE;

    public PortScopeSprite() {
        initializeChannelTypes();
        initializeChannelDirections();
        initializeData();
    }

    public void initializeData () {
        for (int i = 0; i < this.channelDataPoints.length; i++) {
            this.channelDataPoints[i] = -(this.channelNodeRadius / 2.0f) + 0;
        }
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
        WAVE(3);
//        POWER(4),
//        GROUND(5);

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
        channelType = PortScopeSprite.ChannelType.NONE; // 0 for "none" (disabled)
    }

    private void initializeChannelDirections() {
        channelDirection = PortScopeSprite.ChannelDirection.NONE; // 0 for "none" (disabled)
    }

    public PointF getPosition() {
        return this.position;
    }

    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
//        this.updateChannelScopePositions();
    }

    public void addPath(BoardSprite touchedBoardSpriteSource, int touchedChannelScopeSource, BoardSprite touchedBoardSpriteDestination, int touchedChannelScopeDestination) {
        FlowPathSprite channelPath = new FlowPathSprite(touchedBoardSpriteSource, touchedChannelScopeSource, touchedBoardSpriteDestination, touchedChannelScopeDestination);
        flowPathSprites.add(channelPath);
    }

    public void showFullPaths() {
        for (FlowPathSprite flowPathSprite : flowPathSprites) {
            flowPathSprite.showOnlyPathTerminals = false;
        }
    }

    public void showPartialPaths() {
        for (FlowPathSprite flowPathSprite : flowPathSprites) {
            flowPathSprite.showOnlyPathTerminals = true;
        }
    }

    @Override
    public void draw(Canvas mapCanvas, Paint paint) {
        drawChannelScopeShadows(mapCanvas, paint);
        drawChannelScopes(mapCanvas, paint);
    }



    public void drawChannelScopeShadows (Canvas mapCanvas, Paint paint) {

//        BoardSprite boardSprite = this;

        mapCanvas.save();

//        mapCanvas.translate(boardSprite.getPosition().x, boardSprite.getPosition().y);
//        mapCanvas.rotate(boardSprite.getAngle());
//
//        mapCanvas.scale(boardSprite.getScale(), boardSprite.getScale());

        // --- NODES ---

//        if (boardSprite.showChannelScopes) {

//        for (int i = 0; i < 4; i++) {
//
//            mapCanvas.save();
//
//            mapCanvas.rotate(-90 * i);
//            mapCanvas.translate(0, 0);
//
//            for (int j = 0; j < 3; j++) {

                //if (boardSprite.showChannelScopes[3 * i + j]) {
                if (showChannelScopes) {

//                    mapCanvas.save();
//                    mapCanvas.translate(
//                            -((boardSprite.channelNodeRadius + boardSprite.distanceBetweenNodes) * 2.0f) + j * ((boardSprite.channelNodeRadius + boardSprite.distanceBetweenNodes) * 2),
//                            (boardSprite.boardWidth / 2.0f) + boardSprite.channelNodeRadius + boardSprite.distanceNodeToBoard
//                    );
//                    mapCanvas.rotate(0);

                    mapCanvas.save();
                    // Color
                    paint.setStyle(Paint.Style.FILL);
                    // paint.setColor(boardSprite.channelTypeColors.get(boardSprite.channelTypes.get(3 * i + j)));
//                    if (boardSprite.channelTypes.get(3 * i + j) != ChannelType.NONE) {
//                        paint.setColor(CHANNEL_COLOR_PALETTE[3 * i + j]);
//                    } else {
                    paint.setColor(BoardSprite.CHANNEL_COLOR_OFF);
//                    }
//                    boardSprite.updateChannelScopePositions();
                    mapCanvas.drawCircle(
                            0,
                            0,
                            channelNodeRadius,
                            paint
                    );
                    // Outline
                    if (showChannelNodeOutline) {
                        paint.setStyle(Paint.Style.STROKE);
                        paint.setStrokeWidth(3);
                        paint.setColor(Color.BLACK);
                        mapCanvas.drawCircle(
                                0,
                                0,
                                channelNodeRadius,
                                paint
                        );
                    }
                    /*
                    // Label
                    if (showChannelLabel) {
                        paint.setTextSize(labelTextSize);
                        Rect textBounds = new Rect();
                        String channelNumberText = String.valueOf(3 * i + j + 1);
                        paint.getTextBounds(channelNumberText, 0, channelNumberText.length(), textBounds);
                        paint.setStyle(Paint.Style.FILL);
                        paint.setStrokeWidth(3);
                        paint.setColor(Color.BLACK);
                        mapCanvas.drawText(channelNumberText, -(textBounds.width() / 2.0f), textBounds.height() / 2.0f, paint);
                    }
                    */
                    // Outline
                    if (showChannelData) {
                        if (channelType != PortScopeSprite.ChannelType.NONE) {
                            paint.setStyle(Paint.Style.STROKE);
                            paint.setStrokeWidth(2.0f);
                            paint.setColor(Color.WHITE);
                            int step = 1;
                            for (int k = 0; k + step < channelDataPoints.length - 1; k += step) {
                                mapCanvas.drawLine(
                                        channelDataPoints[k],
                                        -channelNodeRadius + k,
                                        channelDataPoints[k + step],
                                        -channelNodeRadius + k + step,
                                        paint
                                );
                            }
                        }
                    }
                    mapCanvas.restore();

//                    mapCanvas.restore();

                }
//            }
//
//            mapCanvas.restore();
//        }
        // ^^^ NODES ^^^

        mapCanvas.restore();
    }

    public void drawChannelScopes (Canvas mapCanvas, Paint paint) {

//        BoardSprite boardSprite = this;

        mapCanvas.save();

//        mapCanvas.translate(boardSprite.getPosition().x, boardSprite.getPosition().y);
//        mapCanvas.rotate(boardSprite.getAngle());
//
//        mapCanvas.scale(boardSprite.getScale(), boardSprite.getScale());

        // --- NODES ---

//        if (boardSprite.showChannelScopes) {

//        for (int i = 0; i < 4; i++) {
//
//            mapCanvas.save();
//
//            mapCanvas.rotate(-90 * i);
//            mapCanvas.translate(0, 0);
//
//            for (int j = 0; j < 3; j++) {

                if (channelType != PortScopeSprite.ChannelType.NONE) {

                    if (showChannelScopes) {

//                        mapCanvas.save();
//                        mapCanvas.translate(
//                                -((boardSprite.channelNodeRadius + boardSprite.distanceBetweenNodes) * 2.0f) + j * ((boardSprite.channelNodeRadius + boardSprite.distanceBetweenNodes) * 2),
//                                (boardSprite.boardWidth / 2.0f) + boardSprite.channelNodeRadius + boardSprite.distanceNodeToBoard
//                        );
//                        if (boardSprite.portScopeSprites.get(3 * i + j).channelDirection == PortScopeSprite.ChannelDirection.OUTPUT) {
//                            mapCanvas.rotate(180.0f);
//                        } else {
//                            mapCanvas.rotate(0.0f);
//                        }

                        mapCanvas.save();
                        // Color
                        paint.setStyle(Paint.Style.FILL);
                        // paint.setColor(boardSprite.channelTypeColors.get(boardSprite.channelTypes.get(3 * i + j)));
//                        if (boardSprite.channelTypes.get(3 * i + j) != ChannelType.NONE) {
                        paint.setColor(BoardSprite.CHANNEL_COLOR_PALETTE[1]); // [3 * i + j]);
//                        } else {
//                            paint.setColor(CHANNEL_COLOR_OFF);
//                        }
//                        boardSprite.updateChannelScopePositions();
                        mapCanvas.drawCircle(
                                0,
                                0,
                                channelNodeRadius,
                                paint
                        );
                        // Outline
                        if (showChannelNodeOutline) {
                            paint.setStyle(Paint.Style.STROKE);
                            paint.setStrokeWidth(3);
                            paint.setColor(Color.BLACK);
                            mapCanvas.drawCircle(
                                    0,
                                    0,
                                    channelNodeRadius,
                                    paint
                            );
                        }
                        // Label
//                        if (showChannelLabel) {
//                            paint.setTextSize(labelTextSize);
//                            Rect textBounds = new Rect();
//                            String channelNumberText = String.valueOf(3 * i + j + 1);
//                            paint.getTextBounds(channelNumberText, 0, channelNumberText.length(), textBounds);
//                            paint.setStyle(Paint.Style.FILL);
//                            paint.setStrokeWidth(3);
//                            paint.setColor(Color.BLACK);
//                            mapCanvas.drawText(channelNumberText, -(textBounds.width() / 2.0f), textBounds.height() / 2.0f, paint);
//                        }
                        // Outline
                        if (showChannelData) {
                            if (channelType != PortScopeSprite.ChannelType.NONE) {
                                paint.setStyle(Paint.Style.STROKE);
                                paint.setStrokeWidth(2.0f);
                                paint.setColor(Color.WHITE);
                                int step = 1;
                                for (int k = 0; k + step < channelDataPoints.length - 1; k += step) {
                                    mapCanvas.drawLine(
                                            channelDataPoints[k],
                                            -channelNodeRadius + k,
                                            channelDataPoints[k + step],
                                            -channelNodeRadius + k + step,
                                            paint
                                    );
                                }
                            }
                        }
                        mapCanvas.restore();

//                        mapCanvas.restore();

                    }
                }
//            }

//            mapCanvas.restore();
//        }
//        // ^^^ NODES ^^^
//
        mapCanvas.restore();
    }

    @Override
    public boolean isTouching(PointF point) {
        return false;
    }
}
