package camp.computer.clay.sprite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.Path;
import camp.computer.clay.model.TouchInteraction;
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

    private boolean isVisible = false;
    public boolean showLinePaths = false;
    public boolean showDirectedPaths = true;
    public boolean showPathDocks = true;
    private boolean isEditorVisible = false;
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

    //public PathSprite(MachineSprite sourceMachineSprite, int sourcePortIndex, MachineSprite destinationMachineSprite, int destinationPortIndex) {
    public PathSprite(MachineSprite sourceMachineSprite, PortSprite sourcePortSprite, MachineSprite destinationMachineSprite, PortSprite destinationPortSprite) {

        // TODO: Create Path model, then access that model. Don't store the sprites. Look those up in the visualization.
        Path path = new Path(
                sourceMachineSprite,
                sourcePortSprite,
                destinationMachineSprite,
                destinationPortSprite
        );
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

        if (getVisibility()) {
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
    }

    public Path getPath() {
        return this.path;
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

            // Show destination port
            path.getDestinationPort().setVisibility(true);
            path.getDestinationPort().setPathVisibility(true);

            mapCanvas.save();

            // Color
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(15.0f);
            paint.setColor(path.getSourcePort().getUniqueColor());

            mapCanvas.drawLine(
                    path.getSourcePort().getPosition().x,
                    path.getSourcePort().getPosition().y,
                    path.getDestinationPort().getPosition().x,
                    path.getDestinationPort().getPosition().y,
                    paint
            );

            mapCanvas.restore();
        }
    }

    public void drawTrianglePath(Canvas mapCanvas, Paint paint) {

        // Show destination port
        path.getDestinationPort().setVisibility(true);
        path.getDestinationPort().setPathVisibility(true);

        mapCanvas.save();

        // Color
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15.0f);
        paint.setColor(path.getSourcePort().getUniqueColor());

        if (showDirectedPaths) {
            float rotationAngle = Geometry.calculateRotationAngle(
                    path.getSourcePort().getPosition(),
                    path.getDestinationPort().getPosition()
            );

            if (showPathDocks) {

                float distance = (float) Geometry.calculateDistance(
                        path.getSourcePort().getPosition(),
                        path.getDestinationMachine().getPosition()
                );

                PointF triangleCenterPosition = Geometry.calculatePoint(
                        path.getSourcePort().getPosition(),
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
                        path.getSourcePort().getPosition(),
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
                        path.getSourcePort().getPosition(),
                        path.getDestinationPort().getPosition()
                );

                for (int k = 0; ; k++) {

                    PointF triangleCenterPosition = Geometry.calculatePoint(
                            path.getSourcePort().getPosition(),
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

                if (isEditorVisible) {
                    // <SPREADSHEET>
                    mapCanvas.save();

                    PointF pathMidpoint = Geometry.calculateMidpoint(
                            path.getSourcePort().getPosition(),
                            path.getDestinationPort().getPosition()
                    );

                    paint.setStyle(Paint.Style.FILL);
                    mapCanvas.translate(pathMidpoint.x, pathMidpoint.y);
                    mapCanvas.rotate(rotationAngle + 180);
                    paint.setColor(path.getSourcePort().getUniqueColor());
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
        this.isVisible = isVisible;
        showFormLayer = isVisible;
        showStyleLayer = isVisible;
        showDataLayer = isVisible;
        showAnnotationLayer = isVisible;
    }

    public boolean getVisibility() {
        return this.isVisible;
    }

    public void setEditorVisibility(boolean isVisible) {
        this.isEditorVisible = isVisible;
    }

    public boolean getEditorVisibility() {
        return this.isEditorVisible;
    }

    @Override
    public boolean isTouching(PointF point) {
        return false;
    }

    public static final String CLASS_NAME = "PATH_SPRITE";

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
