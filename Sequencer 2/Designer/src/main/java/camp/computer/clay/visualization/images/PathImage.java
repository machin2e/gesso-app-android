package camp.computer.clay.visualization.images;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import camp.computer.clay.application.VisualizationSurface;
import camp.computer.clay.model.simulation.Path;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.visualization.arch.Image;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.Shape;

public class PathImage extends Image {

    public final static String TYPE = "path";

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

    // --- STYLE ---

    private boolean isVisible = false;
    public boolean showLinePaths = false;
    public boolean showDirectedPaths = true;
    public boolean showPathDocks = true;
    float pathTerminalLength = 100.0f;
    float triangleWidth = 20;
    float triangleHeight = triangleWidth * ((float) Math.sqrt(3.0) / 2);
    float triangleSpacing = 35;

    // ^^^ STYLE ^^^

    public PathImage(Path path) {
        super(path);
        setType(TYPE);
        setup();
    }

    private void setup() {
        setupPathDirections();
        setupPathTypes();
    }

    private void setupPathTypes() {
        getPath().setType(Path.Type.NONE);
    }

    private void setupPathDirections() {
        getPath().setDirection(Path.Direction.NONE);
    }

    public void update() {
    }

    public void draw(VisualizationSurface visualizationSurface) {

        if (isVisible()) {
            Canvas mapCanvas = visualizationSurface.getCanvas();
            Paint paint = visualizationSurface.getPaint();

            if (this.showDirectedPaths) {
                drawTrianglePath(mapCanvas, paint);
            }
        }
    }

    public Path getPath() {
        return (Path) getModel();
    }

    public void drawTrianglePath(Canvas mapCanvas, Paint paint) {

        Path path = getPath();

        PortImage sourcePortImage = (PortImage) getVisualization().getImage(path.getSource());
        PortImage targetPortImage = (PortImage) getVisualization().getImage(path.getTarget());

        // Show target port
        targetPortImage.setVisibility(true);
        targetPortImage.setPathVisibility(true);

        // Color
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15.0f);
        paint.setColor(sourcePortImage.getUniqueColor());

        if (showDirectedPaths) {
            float pathRotationAngle = Geometry.calculateRotationAngle(
                    sourcePortImage.getPosition(),
                    targetPortImage.getPosition()
            );

            float triangleRotationAngle = pathRotationAngle + 90.0f;

            PointF pathStartPosition = Geometry.calculatePoint(
                    sourcePortImage.getPosition(),
                    pathRotationAngle,
                    2 * triangleSpacing
            );

            PointF pathStopPosition = Geometry.calculatePoint(
                    targetPortImage.getPosition(),
                    pathRotationAngle + 180,
                    2 * triangleSpacing
            );

            if (showPathDocks) {

                paint.setStyle(Paint.Style.FILL);
                Shape.drawTriangle(
                        pathStartPosition,
                        triangleRotationAngle,
                        triangleWidth,
                        triangleHeight,
                        mapCanvas,
                        paint
                );

                paint.setStyle(Paint.Style.FILL);
                Shape.drawTriangle(
                        pathStopPosition,
                        triangleRotationAngle,
                        triangleWidth,
                        triangleHeight,
                        mapCanvas,
                        paint
                );

            } else {

                Shape.drawTrianglePath(
                        pathStartPosition,
                        pathStopPosition,
                        triangleWidth,
                        triangleHeight,
                        mapCanvas,
                        paint
                );

                PointF pathMidpoint = Geometry.calculateMidpoint(
                        sourcePortImage.getPosition(),
                        targetPortImage.getPosition()
                );
            }
        }
    }

    public void setVisibility(boolean isVisible) {

        this.isVisible = isVisible;
        showFormLayer = isVisible;
        showStyleLayer = isVisible;
        showDataLayer = isVisible;
        showAnnotationLayer = isVisible;
    }

    public boolean isVisible() {
        return this.isVisible;
    }

    @Override
    public boolean isTouching(PointF point) {
        return false;
    }

    public boolean isTouching (PointF point, float padding) {
        return false;
    }

    public static final String CLASS_NAME = "PATH_SPRITE";

    @Override
    public void onTouchInteraction(TouchInteraction touchInteraction) {

        if (touchInteraction.getType() == TouchInteraction.Type.NONE) {
            Log.v("onTouchInteraction", "TouchInteraction.NONE to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.TOUCH) {
            Log.v("onTouchInteraction", "TouchInteraction.TOUCH to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.TAP) {
            Log.v("onTouchInteraction", "TouchInteraction.TAP to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.HOLD) {
            Log.v("onTouchInteraction", "TouchInteraction.HOLD to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.MOVE) {
            Log.v("onTouchInteraction", "TouchInteraction.MOVE to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.PRE_DRAG) {
            Log.v("onTouchInteraction", "TouchInteraction.PRE_DRAG to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.DRAG) {
            Log.v("onTouchInteraction", "TouchInteraction.DRAG to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.Type.RELEASE) {
            Log.v("onTouchInteraction", "TouchInteraction.RELEASE to " + CLASS_NAME);
        }
    }
}
