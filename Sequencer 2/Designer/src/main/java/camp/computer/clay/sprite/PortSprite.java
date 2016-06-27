package camp.computer.clay.sprite;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.Machine;
import camp.computer.clay.model.Path;
import camp.computer.clay.model.Port;
import camp.computer.clay.model.TouchInteraction;
import camp.computer.clay.sprite.util.Geometry;

public class PortSprite extends Sprite {

    // --- STYLE ---
    public static float DISTANCE_FROM_BOARD = 45.0f;
    public static float DISTANCE_BETWEEN_NODES = 10.0f;
    public static int FLOW_PATH_COLOR_NONE = Color.parseColor("#efefef");

    private boolean showFormLayer = true;
    private boolean showStyleLayer = true;
    private boolean showDataLayer = true;
    private boolean showAnnotationLayer = false;

    public float shapeRadius = 40.0f;
    private boolean showShapeOutline = false;

    private int uniqueColor = Color.BLACK;

    public int getIndex() {
        return getMachineSprite().getPortSpriteIndex(this);
    }
    // ^^^ STYLE ^^^

    // --- DATA ---
    private int dataSampleCount = 40;
    private float[] dataSamples = new float[dataSampleCount];
    // ^^^ DATA ^^^

    public ArrayList<PathSprite> pathSprites = new ArrayList<PathSprite>();

    public PortSprite(Port port) {
        super(port);
        initialize();
    }

    private void initialize() {
        this.uniqueColor = updateUniqueColor();
        initializeData();
        setVisibility(false);
    }

    private void initializeData () {
        for (int i = 0; i < this.dataSamples.length; i++) {
            this.dataSamples[i] = -(this.shapeRadius / 2.0f) + 0;
        }
    }

    public MachineSprite getMachineSprite() {
        return (MachineSprite) getParentSprite();
    }

//    public void setPosition(PointF position) {
//        this.setPosition(
//                position.x,
//                position.y);
//    }

    // TODO: Move into Port
    public PathSprite addPath(MachineSprite sourceMachineSprite, PortSprite sourcePortSprite, MachineSprite destinationMachineSprite, PortSprite destinationPortSprite) {


        // TODO: Create Path model, then access that model. Don't store the sprites. Look those up in the visualization.
        Path path = new Path(
                sourceMachineSprite,
                sourcePortSprite,
                destinationMachineSprite,
                destinationPortSprite
        );
        PathSprite pathSprite = new PathSprite(path);
        pathSprite.setParentSprite(this);

        destinationPortSprite.setUniqueColor(this.uniqueColor);
        this.pathSprites.add(pathSprite);
        return pathSprite;
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

    public void draw(MapView mapView) {
        this.mapView = mapView;

        if (getVisibility()) {

            drawShapeLayer(mapView);
            drawStyleLayer(mapView);
            drawDataLayer(mapView);
            drawAnnotationLayer(mapView);

            // draw pathSprites
            //drawCandidatePath(mapView);
        }
    }

    /**
     * Draws the shape of the sprite filled with a solid color. Graphically, this represents a
     * placeholder for the sprite.
     * @param mapView
     */
    public void drawShapeLayer(MapView mapView) {

        if (showFormLayer) {

            Canvas mapCanvas = mapView.getCanvas();
            Paint paint = mapView.getPaint();

//            mapCanvas.save();
//
//            mapCanvas.translate(this.getPosition().x, this.getPosition().y);

            // Color
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(PortSprite.FLOW_PATH_COLOR_NONE);
            mapCanvas.drawCircle(
                    getPosition().x,
                    getPosition().y,
                    shapeRadius,
                    paint
            );

            // Outline
            if (showShapeOutline) {
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                paint.setColor(Color.BLACK);
                mapCanvas.drawCircle(
                        getPosition().x,
                        getPosition().y,
                        shapeRadius,
                        paint
                );
            }

//            mapCanvas.restore();
        }
    }

    /**
     * Draws the sprite's detail front layer.
     * @param mapView
     */
    public void drawStyleLayer(MapView mapView) {

        if (showStyleLayer) {

            Canvas mapCanvas = mapView.getCanvas();
            Paint paint = mapView.getPaint();

            Port port = (Port) getModel();

            if (port.getType() != Port.PortType.NONE) {

                mapCanvas.save();

//                mapCanvas.translate(this.getPosition().x, this.getPosition().y);

                // Color
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(this.uniqueColor); // [3 * i + j]);
                mapCanvas.drawCircle(
                        this.getPosition().x,
                        this.getPosition().y,
                        shapeRadius,
                        paint
                );

                // Outline
                if (showShapeOutline) {
                    paint.setStyle(Paint.Style.STROKE);
                    paint.setStrokeWidth(3);
                    paint.setColor(Color.BLACK);
                    mapCanvas.drawCircle(
                            this.getPosition().x,
                            this.getPosition().y,
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
     * @param mapView
     */
    private void drawDataLayer(MapView mapView) {

        if (showDataLayer) {

            Canvas mapCanvas = mapView.getCanvas();
            Paint paint = mapView.getPaint();

            Port port = (Port) getModel();

            if (port.getType() != Port.PortType.NONE) {

                mapCanvas.save();

                mapCanvas.translate(this.getPosition().x, this.getPosition().y);

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
     * @param mapView
     */
    public void drawAnnotationLayer(MapView mapView) {

        if (showAnnotationLayer) {

            Canvas mapCanvas = mapView.getCanvas();
            Paint paint = mapView.getPaint();

            Port port = (Port) getModel();

            if (port.getType() != Port.PortType.NONE) {

                mapCanvas.save();

                mapCanvas.translate(this.getPosition().x, this.getPosition().y);

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
    public void update() {
        Random random = new Random();
        Port port = (Port) getModel();
        if (port.getType() == Port.PortType.SWITCH) {
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
        } else if (port.getType() == Port.PortType.PULSE) {
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
        } else if (port.getType() == Port.PortType.WAVE) {
            // Add new sample for the channel type
            for (int k = 0; k < dataSamples.length; k++) {
                dataSamples[k] = getSyntheticWaveSample(k);
            }
            //xWaveStart = (xWaveStart + ((2.0f * (float) Math.PI) / ((float) this.dataSamples[j].length))) % (2.0f * (float) Math.PI);
            xWaveStart = (xWaveStart + 0.5f) % ((float) Math.PI * 2.0f);
        }

        updatePosition();
    }

    private void updatePosition() {

        // <TODO>
        // TODO: Replace this with getParentSpriteBounds() -- get bounding box based on parent sprite's shape and orientation (to get width and height)
        MachineSprite machineSprite = (MachineSprite) getParentSprite();
        Machine machine = (Machine) machineSprite.getModel();
        // </TODO>

        // Ports
        float portRadius = 40.0f;
        PointF[] relativePortPositions = new PointF[machine.getPorts().size()];
        relativePortPositions[0] = new PointF(
                -1 * ((portRadius * 2) + PortSprite.DISTANCE_BETWEEN_NODES),
                +1 * ((machineSprite.boardWidth/ 2.0f) + PortSprite.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[1] = new PointF(
                0,
                +1 * ((machineSprite.boardWidth / 2.0f) + PortSprite.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[2] = new PointF(
                +1 * ((portRadius * 2) + PortSprite.DISTANCE_BETWEEN_NODES),
                +1 * ((machineSprite.boardWidth / 2.0f) + PortSprite.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[3] = new PointF(
                +1 * ((machineSprite.boardWidth / 2.0f) + PortSprite.DISTANCE_FROM_BOARD + portRadius),
                +1 * ((portRadius * 2) + PortSprite.DISTANCE_BETWEEN_NODES)
        );
        relativePortPositions[4] = new PointF(
                +1 * ((machineSprite.boardWidth / 2.0f) + PortSprite.DISTANCE_FROM_BOARD + portRadius),
                0
        );
        relativePortPositions[5] = new PointF(
                +1 * ((machineSprite.boardWidth / 2.0f) + PortSprite.DISTANCE_FROM_BOARD + portRadius),
                -1 * ((portRadius * 2) + PortSprite.DISTANCE_BETWEEN_NODES)
        );
        relativePortPositions[6] = new PointF(
                +1 * ((portRadius * 2) + PortSprite.DISTANCE_BETWEEN_NODES),
                -1 * ((machineSprite.boardWidth / 2.0f) + PortSprite.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[7] = new PointF(
                0,
                -1 * ((machineSprite.boardWidth / 2.0f) + PortSprite.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[8] = new PointF(
                -1 * ((portRadius * 2) + PortSprite.DISTANCE_BETWEEN_NODES),
                -1 * ((machineSprite.boardWidth / 2.0f) + PortSprite.DISTANCE_FROM_BOARD + portRadius)
        );
        relativePortPositions[9] = new PointF(
                -1 * ((machineSprite.boardWidth / 2.0f) + PortSprite.DISTANCE_FROM_BOARD + portRadius),
                -1 * ((portRadius * 2) + PortSprite.DISTANCE_BETWEEN_NODES)
        );
        relativePortPositions[10] = new PointF(
                -1 * ((machineSprite.boardWidth / 2.0f) + PortSprite.DISTANCE_FROM_BOARD + portRadius),
                0
        );
        relativePortPositions[11] = new PointF(
                -1 * ((machineSprite.boardWidth / 2.0f) + PortSprite.DISTANCE_FROM_BOARD + portRadius),
                +1 * ((portRadius * 2) + PortSprite.DISTANCE_BETWEEN_NODES)
        );

        setRelativePosition(relativePortPositions[getIndex()]);
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

    public boolean hasVisiblePaths () {
        for (PathSprite pathSprite: pathSprites) {
            if (pathSprite.getVisibility() && !pathSprite.showPathDocks) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<PathSprite> getVisiblePaths() {
        ArrayList<PathSprite> visiblePathSprites = new ArrayList<PathSprite>();
        for (PathSprite pathSprite: pathSprites) {
            if (pathSprite.getVisibility()) {
                visiblePathSprites.add(pathSprite);
            }
        }
        return visiblePathSprites;
    }

    public MapView mapView = null;

    @Override
    public boolean isTouching(PointF point) {
        if (getVisibility()) {

//            boolean touching = (Geometry.calculateDistance(point, this.getRelativePosition()) < (this.shapeRadius + 10));
//            if (touching) {
//                Matrix m = new Matrix();
//                mapView.canvasMatrix.invert(m);
//
//                float[] touch2 = new float[]{this.getPosition().x, this.getPosition().y};
//                m.mapPoints(touch2);
//
//                int X = (int) touch2[0];
//                int Y = (int) touch2[1];
//
//                Log.v("mtouch", "X: " + X + ", Y: " + Y);
//            }

            return (Geometry.calculateDistance(point, this.getPosition()) < (this.shapeRadius + 10));
        } else {
            return false;
        }
    }

    public static final String CLASS_NAME = "PORT_SPRITE";

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
            this.setCandidatePathDestinationPosition(touchInteraction.getPosition());
            this.setCandidatePathVisibility(true);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.RELEASE) {
            Log.v("onTouchAction", "TouchInteraction.RELEASE to " + CLASS_NAME);

            this.setCandidatePathVisibility(false);
        }
    }

    private boolean isCandidatePathVisible = false;
    private PointF candidatePathDestinationPosition = new PointF(40, 80);

    public void setCandidatePathVisibility(boolean isVisible) {
        this.isCandidatePathVisible = isVisible;
    }

    public boolean getCandidatePathVisibility() {
        return this.isCandidatePathVisible;
    }

    public void setCandidatePathDestinationPosition(PointF position) {
        this.candidatePathDestinationPosition.x = position.x;
        this.candidatePathDestinationPosition.y = position.y;
    }

    public void drawCandidatePath(MapView mapView) {
        if (isCandidatePathVisible) {

            Port port = (Port) getModel();

            if (port.portType != Port.PortType.NONE) {

                Canvas mapCanvas = mapView.getCanvas();
                Paint paint = mapView.getPaint();

                // Color
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(15.0f);
                paint.setColor(this.getUniqueColor());

                mapCanvas.drawLine(
                        this.getPosition().x,
                        this.getPosition().y,
                        candidatePathDestinationPosition.x,
                        candidatePathDestinationPosition.y,
                        paint
                );

                // Color
                float radius = this.shapeRadius;
                paint.setStyle(Paint.Style.FILL);
                paint.setColor(this.getUniqueColor());
                mapCanvas.drawCircle(
                        candidatePathDestinationPosition.x,
                        candidatePathDestinationPosition.y,
                        radius,
                        paint
                );
            }
        }
    }
}
