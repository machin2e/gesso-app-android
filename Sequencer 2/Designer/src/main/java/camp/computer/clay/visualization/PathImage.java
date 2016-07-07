package camp.computer.clay.visualization;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.Log;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.simulation.Path;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.Shape;

public class PathImage extends Image {

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
    float triangleWidth = 20;
    float triangleHeight = triangleWidth * ((float) Math.sqrt(3.0) / 2);
    float triangleSpacing = 35;

    // ^^^ STYLE ^^^

    public PathImage(Path path) {
        super(path);

        initialize();
    }

    private void initialize() {
        initializePathDirections();
        initializePathTypes();
    }

    private void initializePathTypes() {
        getPath().setType(Path.Type.NONE);
    }

    private void initializePathDirections() {
        getPath().setDirection(Path.Direction.NONE);
    }

    public void update() {
    }

    public void draw(MapView mapView) {

        if (isVisible()) {
            Canvas mapCanvas = mapView.getCanvas();
            Paint paint = mapView.getPaint();

            if (this.showDirectedPaths) {
                drawTrianglePath(mapCanvas, paint);
            } else {
                drawLinePath(mapCanvas, paint);
            }
        }
    }

    public Path getPath() {
        return (Path) getModel();
    }

    //-------

    private void drawLinePath (Canvas mapCanvas, Paint paint) {

        if (showLinePaths) {

            Path path = (Path) getModel();

            PortImage sourcePortImage = (PortImage) getVisualization().getImage(path.getSource());
            PortImage destinationPortImage = (PortImage) getVisualization().getImage(path.getDestination());

            // Show destination port
            destinationPortImage.setVisibility(true);
            destinationPortImage.setPathVisibility(true);

            mapCanvas.save();

            // Color
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(15.0f);
            paint.setColor(sourcePortImage.getUniqueColor());

            mapCanvas.drawLine(
                    sourcePortImage.getPosition().x,
                    sourcePortImage.getPosition().y,
                    destinationPortImage.getPosition().x,
                    destinationPortImage.getPosition().y,
                    paint
            );

            mapCanvas.restore();
        }
    }

    public void drawTrianglePath(Canvas mapCanvas, Paint paint) {

        Path path = (Path) getModel();

        PortImage sourcePortImage = (PortImage) getVisualization().getImage(path.getSource());
        PortImage destinationPortImage = (PortImage) getVisualization().getImage(path.getDestination());

        // Show destination port
        destinationPortImage.setVisibility(true);
        destinationPortImage.setPathVisibility(true);

        // Color
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15.0f);
        paint.setColor(sourcePortImage.getUniqueColor());

        if (showDirectedPaths) {
            float pathRotationAngle = Geometry.calculateRotationAngle(
                    sourcePortImage.getPosition(),
                    destinationPortImage.getPosition()
            );

            float triangleRotationAngle = pathRotationAngle + 90.0f;

            PointF pathStartPosition = Geometry.calculatePoint(
                    sourcePortImage.getPosition(),
                    pathRotationAngle,
                    2 * triangleSpacing
            );

            PointF pathStopPosition = Geometry.calculatePoint(
                    destinationPortImage.getPosition(),
                    pathRotationAngle + 180,
                    2 * triangleSpacing
            );

            if (showPathDocks) {

                float pathDistance = (float) Geometry.calculateDistance(
                        sourcePortImage.getPosition(),
                        destinationPortImage.getPosition()
                );

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
                        destinationPortImage.getPosition()
                );

                if (isEditorVisible) {
                    // <SPREADSHEET>
                    paint.setStyle(Paint.Style.FILL);
                    paint.setColor(sourcePortImage.getUniqueColor());
                    float spreadsheetImageWidth = 50.0f;
                    mapCanvas.drawRect(
                            pathMidpoint.x + -(spreadsheetImageWidth / 2.0f),
                            pathMidpoint.y + -(spreadsheetImageWidth / 2.0f),
                            pathMidpoint.x + (spreadsheetImageWidth / 2.0f),
                            pathMidpoint.y + (spreadsheetImageWidth / 2.0f),
                            paint
                    );
                    // </SPREADSHEET>
                }
            }
        }
    }

    public void setVisibility(boolean isVisible) {

        // Hide the path editor by default
        if (this.isVisible == false) {
            setEditorVisibility(false);
        }

        this.isVisible = isVisible;
        showFormLayer = isVisible;
        showStyleLayer = isVisible;
        showDataLayer = isVisible;
        showAnnotationLayer = isVisible;
    }

    public boolean isVisible() {
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

    public boolean isTouching (PointF point, float padding) {
        return false;
    }

    public static final String CLASS_NAME = "PATH_SPRITE";

    @Override
    public void onTouchInteraction(TouchInteraction touchInteraction) {

        if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.NONE) {
            Log.v("onTouchInteraction", "TouchInteraction.NONE to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.TOUCH) {
            Log.v("onTouchInteraction", "TouchInteraction.TOUCH to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.TAP) {
            Log.v("onTouchInteraction", "TouchInteraction.TAP to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.DOUBLE_DAP) {
            Log.v("onTouchInteraction", "TouchInteraction.DOUBLE_TAP to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.HOLD) {
            Log.v("onTouchInteraction", "TouchInteraction.HOLD to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.MOVE) {
            Log.v("onTouchInteraction", "TouchInteraction.MOVE to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.PRE_DRAG) {
            Log.v("onTouchInteraction", "TouchInteraction.PRE_DRAG to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.DRAG) {
            Log.v("onTouchInteraction", "TouchInteraction.DRAG to " + CLASS_NAME);
        } else if (touchInteraction.getType() == TouchInteraction.TouchInteractionType.RELEASE) {
            Log.v("onTouchInteraction", "TouchInteraction.RELEASE to " + CLASS_NAME);
        }
    }
}
