package camp.computer.clay.viz.img;

import android.graphics.Canvas;
import android.graphics.Paint;

import camp.computer.clay.app.Surface;
import camp.computer.clay.model.interaction.OnTouchActionListener;
import camp.computer.clay.model.sim.Path;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.viz.arch.Image;
import camp.computer.clay.viz.arch.Visibility;
import camp.computer.clay.viz.util.Geometry;
import camp.computer.clay.viz.util.Point;

public class old_PathImage extends Image {

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

    public old_PathImage(Path path) {
        super(path);
        setType(TYPE);
        setup();
    }

    private void setup() {
    }

    public void generate() {
    }

    public void draw(Surface surface) {

        if (isVisible()) {
            Canvas canvas = surface.getCanvas();
            Paint paint = surface.getPaint();

            drawTrianglePath(canvas, paint);
        }
    }

    public Path getPath() {
        return (Path) getModel();
    }

    public void drawTrianglePath(Canvas canvas, Paint paint) {

        Path path = getPath();

        old_PortImage sourceOldPortImage = (old_PortImage) getViz().getImage(path.getSource());
        old_PortImage targetOldPortImage = (old_PortImage) getViz().getImage(path.getTarget());

        // Show target port
        targetOldPortImage.setVisibility(Visibility.VISIBLE);
        targetOldPortImage.setPathVisibility(Visibility.VISIBLE);

        // Color
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15.0f);
        paint.setColor(sourceOldPortImage.getUniqueColor());

        double pathRotationAngle = Geometry.calculateRotationAngle(
                sourceOldPortImage.getPosition(),
                targetOldPortImage.getPosition()
        );

        double triangleRotationAngle = pathRotationAngle + 90.0f;

        Point pathStartPosition = Geometry.calculatePoint(
                sourceOldPortImage.getPosition(),
                pathRotationAngle,
                2 * triangleSpacing
        );

        Point pathStopPosition = Geometry.calculatePoint(
                targetOldPortImage.getPosition(),
                pathRotationAngle + 180,
                2 * triangleSpacing
        );

        if (showDocks) {

            paint.setStyle(Paint.Style.FILL);
            getViz().drawTriangle(
                    pathStartPosition,
                    triangleRotationAngle,
                    triangleWidth,
                    triangleHeight
            );

            paint.setStyle(Paint.Style.FILL);
            getViz().drawTriangle(
                    pathStopPosition,
                    triangleRotationAngle,
                    triangleWidth,
                    triangleHeight
            );

        } else {

            getViz().drawTrianglePath(
                    pathStartPosition,
                    pathStopPosition,
                    triangleWidth,
                    triangleHeight
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
//            old_PortImage sourcePortImage = (old_PortImage) getViz().getImage(path.getSource());
//            old_PortImage targetPortImage = (old_PortImage) getViz().getImage(path.getImage());
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
    public void onTouchInteraction(TouchInteraction touchInteraction) {

        if (touchInteraction.getType() == OnTouchActionListener.Type.NONE) {
            // Log.v("onTouchInteraction", "TouchInteraction.NONE to " + CLASS_NAME);
        } else if (touchInteraction.getType() == OnTouchActionListener.Type.TOUCH) {
            // Log.v("onTouchInteraction", "TouchInteraction.TOUCH to " + CLASS_NAME);
        } else if (touchInteraction.getType() == OnTouchActionListener.Type.TAP) {
            // Log.v("onTouchInteraction", "TouchInteraction.TAP to " + CLASS_NAME);
        } else if (touchInteraction.getType() == OnTouchActionListener.Type.HOLD) {
            // Log.v("onTouchInteraction", "TouchInteraction.HOLD to " + CLASS_NAME);
        } else if (touchInteraction.getType() == OnTouchActionListener.Type.DRAG) {
            // Log.v("onTouchInteraction", "TouchInteraction.DRAG to " + CLASS_NAME);
        } else if (touchInteraction.getType() == OnTouchActionListener.Type.RELEASE) {
            // Log.v("onTouchInteraction", "TouchInteraction.RELEASE to " + CLASS_NAME);
        }
    }
}
