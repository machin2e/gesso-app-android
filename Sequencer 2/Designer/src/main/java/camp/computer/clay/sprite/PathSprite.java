package camp.computer.clay.sprite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.Path;
import camp.computer.clay.model.TouchAction;
import camp.computer.clay.sprite.util.Geometry;

public class PathSprite extends Sprite {

    private Path path;

    // TODO: private Channel channel;

    // --- STYLE ---
    public static float DISTANCE_FROM_BOARD = 45.0f;
    public static float DISTANCE_BETWEEN_NODES = 5.0f;
    public static int FLOW_PATH_COLOR_NONE = Color.parseColor("#efefef");

    public boolean showFormLayer = false;
    public boolean showStyleLayer = true;
    public boolean showDataLayer = true;
    private boolean showAnnotationLayer = false;

    public float shapeRadius = 40.0f;
    boolean showShapeOutline = false;

    boolean showChannelLabel = false;
    float labelTextSize = 30.0f;
    // ^^^ STYLE ^^^

    // TODO: Transfer data from in port, run it through the spreadsheet, and transfer it to the out port
//    // --- DATA ---
//    public int dataSampleCount = 40;
//    public float[] dataSamples = new float[dataSampleCount];
//    // ^^^ DATA ^^^

    // --- STYLE ---

    public boolean showLinePaths = false;
    public boolean showDirectedPaths = true;
    public boolean showPathDocks = true;
    float pathTerminalLength = 100.0f;
    float triangleWidth = 25;
    float triangleHeight = triangleWidth * ((float) Math.sqrt(3.0) / 2);
    float triangleSpacing = 35;

    // ^^^ STYLE ^^^

    // <MODEL>
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
        // TODO: I2C, UART, SPI, MIDI, etc.

        // TODO: Change the index to a UUID?
        int index;

        ChannelType(int index) {
            this.index = index;
        }

        public static ChannelType getNextType(ChannelType currentChannelType) {
            return ChannelType.values()[(currentChannelType.index + 1) % ChannelType.values().length];
        }
    }
    public ChannelType channelType = ChannelType.NONE;
    public ChannelDirection channelDirection = ChannelDirection.NONE;
    // TODO: Physical dimensions
    // </MODEL>

    public PathSprite(MachineSprite touchedMachineSpriteSource, int touchedChannelScopeSource, MachineSprite touchedMachineSpriteDestination, int touchedChannelScopeDestination) {
        Path path = new Path();
        path.source = touchedMachineSpriteSource;
        path.sourcePort = touchedChannelScopeSource;
        path.destination = touchedMachineSpriteDestination;
        path.destinationPort = touchedChannelScopeDestination;
        this.path = path;

        initialize();
    }

    private void initialize() {
        initializeChannelDirections();
        initializeChannelTypes();
    }

    private void initializeChannelTypes() {
        channelType = ChannelType.NONE; // 0 for "none" (disabled)
    }

    private void initializeChannelDirections() {
        channelDirection = ChannelDirection.NONE; // 0 for "none" (disabled)
    }

    @Override
    public void draw(MapView mapView) {

        Canvas mapCanvas = mapView.getCanvas();
        Paint paint = mapView.getPaint();

//        drawStyleLayer(mapCanvas, paint);
//        drawDataLayer(mapCanvas, paint);
        drawAnnotationLayer(mapCanvas, paint);

        if (this.showDirectedPaths) {
            drawTrianglePath(mapCanvas, paint);
        } else {
            drawLinePath(mapCanvas, paint);
        }
    }

    /**
     * Draws the shape of the sprite filled with a solid color. Graphically, this represents a
     * placeholder for the sprite.
     * @param mapCanvas
     * @param paint
     */
    public void drawShapeLayer(Canvas mapCanvas, Paint paint) {

        if (showFormLayer) {

            mapCanvas.save();

            // Color
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(PortSprite.FLOW_PATH_COLOR_NONE);
            mapCanvas.drawCircle(
                    0,
                    0,
                    shapeRadius,
                    paint
            );

            // Outline
            if (showShapeOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                paint.setColor(Color.BLACK);
                mapCanvas.drawCircle(
                        0,
                        0,
                        shapeRadius,
                        paint
                );
            }

            mapCanvas.restore();
        }
    }

//    /**
//     * Draws the sprite's detail front layer.
//     * @param mapCanvas
//     * @param paint
//     */
//    public void drawStyleLayer(Canvas mapCanvas, Paint paint) {
//
//        if (showStyleLayer) {
//
//            if (channelType != ChannelType.NONE) {
//
//                mapCanvas.save();
//                // Color
//                paint.setStyle(Paint.Style.FILL);
//                paint.setColor(PathSprite.PATH_COLOR_PALETTE[1]); // [3 * i + j]);
//                mapCanvas.drawCircle(
//                        0,
//                        0,
//                        shapeRadius,
//                        paint
//                );
//
//                // Outline
//                if (showShapeOutline) {
//                    paint.setStyle(Paint.Style.STROKE);
//                    paint.setStrokeWidth(3);
//                    paint.setColor(Color.BLACK);
//                    mapCanvas.drawCircle(
//                            0,
//                            0,
//                            shapeRadius,
//                            paint
//                    );
//                }
//
//                mapCanvas.restore();
//            }
//        }
//    }

//    /**
//     * Draws the sprite's data layer.
//     * @param mapCanvas
//     * @param paint
//     */
//    private void drawDataLayer(Canvas mapCanvas, Paint paint) {
//
//        if (showDataLayer) {
//
//            if (channelType != ChannelType.NONE) {
//
//                mapCanvas.save();
//
//                // Outline
//                paint.setStyle(Paint.Style.STROKE);
//                paint.setStrokeWidth(2.0f);
//                paint.setColor(Color.WHITE);
////                int step = 1;
////                for (int k = 0; k + step < dataSamples.length - 1; k += step) {
////                    mapCanvas.drawLine(
////                            dataSamples[k],
////                            -shapeRadius + k,
////                            dataSamples[k + step],
////                            -shapeRadius + k + step,
////                            paint
////                    );
////                }
//                int step = 1;
//                float plotStep = (float) ((2.0f * (float) shapeRadius) / (float) dataSamples.length);
//                for (int k = 0; k < dataSamples.length - 1; k++) {
//                    mapCanvas.drawLine(
//                            dataSamples[k],
//                            -shapeRadius + k * plotStep,
//                            dataSamples[k + 1],
//                            -shapeRadius + (k + 1) * plotStep,
//                            paint
//                    );
//                }
//
//                mapCanvas.restore();
//            }
//        }
//    }

    /**
     * Draws the sprite's annotation layer. Contains labels and other text.
     * @param mapCanvas
     * @param paint
     */
    public void drawAnnotationLayer(Canvas mapCanvas, Paint paint) {

        if (showAnnotationLayer) {

            if (channelType != ChannelType.NONE) {

                mapCanvas.save();

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

                mapCanvas.restore();
            }
        }
    }

    //-------

    private void drawLinePath (Canvas mapCanvas, Paint paint) {

        if (showLinePaths) {
            path.destination.showPort(path.destinationPort);

            mapCanvas.save();
            // Color
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(15.0f);
            paint.setColor(path.source.getPortSprite(path.sourcePort).getUniqueColor());

            mapCanvas.drawLine(
                    path.source.portSprites.get(path.sourcePort).getPosition().x,
                    path.source.portSprites.get(path.sourcePort).getPosition().y,
                    path.destination.portSprites.get(path.destinationPort).getPosition().x,
                    path.destination.portSprites.get(path.destinationPort).getPosition().y,
                    paint
            );

            mapCanvas.restore();
        }
    }

    public void drawTrianglePath(Canvas mapCanvas, Paint paint) {

        path.destination.showPort(path.destinationPort);

        mapCanvas.save();

        // Color
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15.0f);
        paint.setColor(path.source.getPortSprite(path.sourcePort).getUniqueColor());

        if (showDirectedPaths) {
            float rotationAngle = Geometry.calculateRotationAngle(
                    path.source.portSprites.get(path.sourcePort).getPosition(),
                    path.destination.portSprites.get(path.destinationPort).getPosition()
            );

            if (showPathDocks) {

                float distance = (float) Geometry.calculateDistance(
                        path.source.portSprites.get(path.sourcePort).getPosition(),
                        path.destination.portSprites.get(path.destinationPort).getPosition()
                );

                PointF triangleCenterPosition = Geometry.calculatePoint(
                        path.source.portSprites.get(path.sourcePort).getPosition(),
                        rotationAngle,
                        2 * triangleSpacing
                );

                drawTriangle(
                        triangleCenterPosition,
                        rotationAngle + 180,
                        triangleWidth,
                        triangleHeight,
                        mapCanvas,
                        paint
                );

                PointF triangleCenterPositionDestination = Geometry.calculatePoint(
                        path.source.portSprites.get(path.sourcePort).getPosition(),
                        rotationAngle,
                        distance - 2 * triangleSpacing
                );

                drawTriangle(
                        triangleCenterPositionDestination,
                        rotationAngle + 180,
                        triangleWidth,
                        triangleHeight,
                        mapCanvas,
                        paint
                );

            } else {

                float pathDistance = (float) Geometry.calculateDistance(
                        path.source.portSprites.get(path.sourcePort).getPosition(),
                        path.destination.portSprites.get(path.destinationPort).getPosition()
                );

                for (int k = 0; ; k++) {

                    PointF triangleCenterPosition = Geometry.calculatePoint(
                            path.source.portSprites.get(path.sourcePort).getPosition(),
                            rotationAngle,
                            k * triangleSpacing
                    );

                    if (k * triangleSpacing > pathDistance) {
                        break;
                    }

                    if ((k * triangleSpacing) >= (2 * triangleSpacing)
                            && (k * triangleSpacing) <= (pathDistance - 2 * triangleSpacing)) {

                        drawTriangle(
                                triangleCenterPosition,
                                rotationAngle + 180,
                                triangleWidth,
                                triangleHeight,
                                mapCanvas,
                                paint
                        );
                    }
                }

                // <SPREADSHEET>
                mapCanvas.save();

                PointF pathMidpoint = Geometry.calculateMidpoint(
                        path.source.portSprites.get(path.sourcePort).getPosition(),
                        path.destination.portSprites.get(path.destinationPort).getPosition()
                );

                paint.setStyle(Paint.Style.FILL);
                mapCanvas.translate(pathMidpoint.x, pathMidpoint.y);
                mapCanvas.rotate(rotationAngle + 180);
                paint.setColor(path.source.getPortSprite(path.sourcePort).getUniqueColor());
                float spreadsheetSpriteWidth = 50.0f;
                mapCanvas.drawRect(
                        -(spreadsheetSpriteWidth / 2.0f),
                        -(spreadsheetSpriteWidth / 2.0f),
                        (spreadsheetSpriteWidth / 2.0f),
                        (spreadsheetSpriteWidth / 2.0f),
                        paint
                );

                mapCanvas.restore();
                // </SPREADSHEET>
            }
        }

        mapCanvas.restore();
    }

    private void drawTriangle(PointF position, float angle, float width, float height, Canvas canvas, Paint paint) {

        canvas.save();

        canvas.translate(position.x, position.y);
        canvas.rotate(angle);

        PointF p1 = new PointF(-(width / 2.0f), -(height / 2.0f));
        PointF p2 = new PointF(0, (height / 2.0f));
        PointF p3 = new PointF((width / 2.0f), -(height / 2.0f));

//        paint.setStrokeWidth(0);
//        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.FILL);
        // paint.setAntiAlias(true);

        android.graphics.Path path = new android.graphics.Path();
        path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
        path.moveTo(p1.x, p1.y);
        path.lineTo(p2.x, p2.y);
        path.lineTo(p3.x, p3.y);
        path.close();

        canvas.drawPath(path, paint);

        canvas.restore();
    }

    public void setVisibility(boolean isVisible) {
        showFormLayer = isVisible;
        showStyleLayer = isVisible;
        showDataLayer = isVisible;
        showAnnotationLayer = isVisible;
    }

    @Override
    public boolean isTouching(PointF point) {
        return false;
    }

    public static final String CLASS_NAME = "PATH_SPRITE";

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
