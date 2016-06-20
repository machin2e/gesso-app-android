package camp.computer.clay.sprite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Random;

public class PortSprite extends Sprite {

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
    private int uniqueColor = Color.BLACK;
    // ^^^ STYLE ^^^

    // <MODEL>
    public enum PortDirection {

        NONE(0),
        OUTPUT(1),
        INPUT(2),
        BOTH(3); // i.e., for I2C, etc.

        // TODO: Change the index to a UUID?
        int index;

        PortDirection(int index) {
            this.index = index;
        }
    }

    public enum PortType {

        NONE(0),
        SWITCH(1),
        PULSE(2),
        WAVE(3);
//        POWER(4),
//        GROUND(5);

        // TODO: Change the index to a UUID?
        int index;

        PortType(int index) {
            this.index = index;
        }

        public static PortType getNextType(PortType currentPortType) {
            return PortType.values()[(currentPortType.index + 1) % PortType.values().length];
        }
    }

    public PortType portType = PortType.NONE;
    public PortDirection portDirection = PortDirection.NONE;

    // TODO: Physical dimensions
    // </MODEL>

    // --- DATA ---
    private int dataSampleCount = 40;
    private float[] dataSamples = new float[dataSampleCount];
    // ^^^ DATA ^^^

    public ArrayList<PathSprite> pathSprites = new ArrayList<PathSprite>();

    private PointF position = new PointF(0, 0); // Sprite position
    private float scale = 1.0f; // Sprite scale factor
    private float angle = 0.0f; // Sprite heading angle

    public PortSprite() {
        this.uniqueColor = updateUniqueColor();
        initialize();
    }

    private void initialize() {
        initializeData();
    }

    private void initializeData () {
        for (int i = 0; i < this.dataSamples.length; i++) {
            this.dataSamples[i] = -(this.shapeRadius / 2.0f) + 0;
        }
    }

    public PointF getPosition() {
        return this.position;
    }

    public void setPosition(float x, float y) {
        this.position.x = x;
        this.position.y = y;
//        this.updateChannelScopePositions();
    }

    public void addPath(DroneSprite sourceDrone, int sourcePort, DroneSprite destinationDrone, int destinationPort) {
        PathSprite path = new PathSprite(sourceDrone, sourcePort, destinationDrone, destinationPort);
        destinationDrone.getPortSprite(destinationPort).setUniqueColor(this.uniqueColor);
        pathSprites.add(path);
    }

    public int getUniqueColor() {
        return this.uniqueColor;
    }

    private void setUniqueColor(int uniqueColor) {
        this.uniqueColor = uniqueColor;
    }

    public int updateUniqueColor() {
        this.uniqueColor = camp.computer.clay.sprite.util.Color.getUniqueColor(this);
        return this.uniqueColor;
    }

    public void showPaths() {
        for (PathSprite pathSprite : pathSprites) {
            pathSprite.showPathDocks = false;
        }
    }

    public void showPathDocks() {
        for (PathSprite pathSprite : pathSprites) {
            pathSprite.showPathDocks = true;
        }
    }

    @Override
    public void draw(Canvas mapCanvas, Paint paint) {
        drawShapeLayer(mapCanvas, paint);
        drawStyleLayer(mapCanvas, paint);
        drawDataLayer(mapCanvas, paint);
        drawAnnotationLayer(mapCanvas, paint);

        // draw pathSprites
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

    /**
     * Draws the sprite's detail front layer.
     * @param mapCanvas
     * @param paint
     */
    public void drawStyleLayer(Canvas mapCanvas, Paint paint) {

        if (showStyleLayer) {

            if (portType != PortSprite.PortType.NONE) {

                mapCanvas.save();
                // Color
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(this.uniqueColor); // [3 * i + j]);
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
    }

    /**
     * Draws the sprite's data layer.
     * @param mapCanvas
     * @param paint
     */
    private void drawDataLayer(Canvas mapCanvas, Paint paint) {

        if (showDataLayer) {

            if (portType != PortSprite.PortType.NONE) {

                mapCanvas.save();

                // Outline
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2.0f);
                paint.setColor(Color.WHITE);
//                int step = 1;
//                for (int k = 0; k + step < dataSamples.length - 1; k += step) {
//                    mapCanvas.drawLine(
//                            dataSamples[k],
//                            -shapeRadius + k,
//                            dataSamples[k + step],
//                            -shapeRadius + k + step,
//                            paint
//                    );
//                }
                int step = 1;
                float plotStep = (float) ((2.0f * (float) shapeRadius) / (float) dataSamples.length);
                for (int k = 0; k < dataSamples.length - 1; k++) {
                    mapCanvas.drawLine(
                            dataSamples[k],
                            -shapeRadius + k * plotStep,
                            dataSamples[k + 1],
                            -shapeRadius + (k + 1) * plotStep,
                            paint
                    );
                }

                mapCanvas.restore();
            }
        }
    }

    /**
     * Draws the sprite's annotation layer. Contains labels and other text.
     * @param mapCanvas
     * @param paint
     */
    public void drawAnnotationLayer(Canvas mapCanvas, Paint paint) {

        if (showAnnotationLayer) {

            if (portType != PortSprite.PortType.NONE) {

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

    private int previousSwitchState = 0;
    private float switchPeriod = 20.0f;
    private int switchHalfPeriodSampleCount = 0;
    private float pulsePeriod = 20.0f;
    private float pulseDutyCycle = 0.5f;
    private int pulsePeriodSampleCount = 0;
    private int previousPulseState = 0;
    private float xWaveStart = 0;
    public void updateChannelData () {
        Random random = new Random();
        if (portType == PortType.SWITCH) {
            // Shift data to make room for new samples
            for (int k = 0; k < dataSamples.length - 1; k++) {
                dataSamples[k] = dataSamples[k + 1];
            }
            // Add new samples for the channel type
            float sample = getSyntheticSwitchSample();
            dataSamples[dataSamples.length - 1] = sample;
            switchHalfPeriodSampleCount = (switchHalfPeriodSampleCount + 1) % ((int) switchPeriod / 2);
            if (switchHalfPeriodSampleCount == 0) {
                previousSwitchState = (previousSwitchState + 1) % 2;
            }
        } else if (portType == PortType.PULSE) {
            // Shift data to make room for new samples
            for (int k = 0; k < dataSamples.length - 1; k++) {
                dataSamples[k] = dataSamples[k + 1];
            }
            // Add new samples for the channel type
            float sample = getSyntheticPulseSample();
            dataSamples[dataSamples.length - 1] = sample;
            //pulsePeriodSampleCount = (pulsePeriodSampleCount + 1) % ((int) (pulseDutyCycle * pulsePeriod) / 2);
            pulsePeriodSampleCount = (pulsePeriodSampleCount + 1) % (1 + (int) (pulseDutyCycle * pulsePeriod));
            if (pulsePeriodSampleCount == 0) {
                pulseDutyCycle = random.nextFloat();
                previousPulseState = (previousPulseState + 1) % 2;
            }
        } else if (portType == PortType.WAVE) {
            // Add new sample for the channel type
            for (int k = 0; k < dataSamples.length; k++) {
                dataSamples[k] = getSyntheticWaveSample(k);
            }
            //xWaveStart = (xWaveStart + ((2.0f * (float) Math.PI) / ((float) this.dataSamples[j].length))) % (2.0f * (float) Math.PI);
            xWaveStart = (xWaveStart + 0.5f) % ((float) Math.PI * 2.0f);
        }
    }

    private float getSyntheticSwitchSample() {
        return -(shapeRadius / 2.0f) + previousSwitchState * shapeRadius;
    }

    private float getSyntheticPulseSample() {
        return -(shapeRadius / 2.0f) + previousPulseState * shapeRadius;
    }

    private float getSyntheticWaveSample(int x) {
        return ((float) Math.sin(xWaveStart + x * 0.2)) * shapeRadius * 0.5f;
    }

    public void setVisibility(boolean isVisible) {
        showFormLayer = isVisible;
        showStyleLayer = isVisible;
        showDataLayer = isVisible;
        showAnnotationLayer = isVisible;
    }

    public void setPathVisibility (boolean isVisible) {
        for (PathSprite pathSprite : this.pathSprites) {
            pathSprite.setVisibility(isVisible);
        }
    }

    @Override
    public boolean isTouching(PointF point) {
        return false;
    }
}
