package camp.computer.clay.visualization.img;

import android.graphics.Canvas;
import android.graphics.Paint;

import camp.computer.clay.application.VisualizationSurface;
import camp.computer.clay.model.arch.Path;
import camp.computer.clay.model.interactivity.Interaction;
import camp.computer.clay.visualization.arch.Image;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.Point;
import camp.computer.clay.visualization.util.Shape;

public class PathImage extends Image {

    public final static String TYPE = "path";

    // </SETTINGS>
    private boolean showFormLayer = false;
    private boolean showStyleLayer = true;
    private boolean showDataLayer = true;
    private boolean showAnnotationLayer = false;
    // </SETTINGS>

    // </STYLE>
    private boolean isVisible = false;
    public boolean showDocks = true;
    private double triangleWidth = 20;
    private double triangleHeight = triangleWidth * ((double) Math.sqrt(3.0) / 2);
    private double triangleSpacing = 35;
    // </STYLE>

    public PathImage(Path path) {
        super(path);
        setType(TYPE);
        setup();
    }

    private void setup() {
    }

    public void update() {
    }

    public void draw(VisualizationSurface visualizationSurface) {

        if (isVisible()) {
            Canvas canvas = visualizationSurface.getCanvas();
            Paint paint = visualizationSurface.getPaint();

            drawTrianglePath(canvas, paint);
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

        double pathRotationAngle = Geometry.calculateRotationAngle(
                sourcePortImage.getPosition(),
                targetPortImage.getPosition()
        );

        double triangleRotationAngle = pathRotationAngle + 90.0f;

        Point pathStartPosition = Geometry.calculatePoint(
                sourcePortImage.getPosition(),
                pathRotationAngle,
                2 * triangleSpacing
        );

        Point pathStopPosition = Geometry.calculatePoint(
                targetPortImage.getPosition(),
                pathRotationAngle + 180,
                2 * triangleSpacing
        );

        if (showDocks) {

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
    public boolean isTouching(Point point) {

//        if (isVisible()) {
//            Log.v("Touch_", "FLOOOO");
//            Path path = getPath();
//
//            PortImage sourcePortImage = (PortImage) getVisualization().getImage(path.getSource());
//            PortImage targetPortImage = (PortImage) getVisualization().getImage(path.getImageByPosition());
//
//            double distanceToLine = Geometry.calculateLineToPointDistance(
//                    sourcePortImage.getPosition(),
//                    targetPortImage.getPosition(),
//                    point,
//                    true
//            );
//
//            if (distanceToLine < 60) {
//                return true;
//            } else {
//                return false;
//            }
//        }
        return false;
    }

    public boolean isTouching (Point point, double padding) {
        return false;
    }

    public static final String CLASS_NAME = "PATH_SPRITE";

    @Override
    public void onTouchInteraction(Interaction interaction) {

        if (interaction.getType() == Interaction.Type.NONE) {
            // Log.v("onTouchInteraction", "Interaction.NONE to " + CLASS_NAME);
        } else if (interaction.getType() == Interaction.Type.TOUCH) {
            // Log.v("onTouchInteraction", "Interaction.TOUCH to " + CLASS_NAME);
        } else if (interaction.getType() == Interaction.Type.TAP) {
            // Log.v("onTouchInteraction", "Interaction.TAP to " + CLASS_NAME);
        } else if (interaction.getType() == Interaction.Type.HOLD) {
            // Log.v("onTouchInteraction", "Interaction.HOLD to " + CLASS_NAME);
        } else if (interaction.getType() == Interaction.Type.MOVE) {
            // Log.v("onTouchInteraction", "Interaction.MOVE to " + CLASS_NAME);
        } else if (interaction.getType() == Interaction.Type.TWITCH) {
            // Log.v("onTouchInteraction", "Interaction.TWITCH to " + CLASS_NAME);
        } else if (interaction.getType() == Interaction.Type.DRAG) {
            // Log.v("onTouchInteraction", "Interaction.DRAG to " + CLASS_NAME);
        } else if (interaction.getType() == Interaction.Type.RELEASE) {
            // Log.v("onTouchInteraction", "Interaction.RELEASE to " + CLASS_NAME);
        }
    }
}
