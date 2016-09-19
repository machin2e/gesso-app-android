package camp.computer.clay.scene.image;

import android.graphics.Paint;

import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.Event;
import camp.computer.clay.model.interaction.ActionListener;
import camp.computer.clay.scene.architecture.Image;
import camp.computer.clay.scene.util.Visibility;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Shape;

public class PathImage extends Image<Path> {

    // </STYLE>
    public boolean showDocks = true;
    private double triangleWidth = 20;
    private double triangleHeight = triangleWidth * (Math.sqrt(3.0) / 2);
    private double triangleSpacing = 35;
    // </STYLE>

    public PathImage(Path path) {
        super(path);
        setup();
    }

    private void setup() {
        setupActions();
    }

    private void setupActions() {
        setOnActionListener(new ActionListener() {
            @Override
            public void onAction(Action action) {

                Event event = action.getLastEvent();

                if (event.getType() == Event.Type.NONE) {

                } else if (event.getType() == Event.Type.SELECT) {

                } else if (event.getType() == Event.Type.HOLD) {

                } else if (event.getType() == Event.Type.MOVE) {

                } else if (event.getType() == Event.Type.UNSELECT) {

                }
            }
        });
    }

    public void update() {
    }

    public void setVisibility(Visibility visibility) {
        this.visibility = visibility;
    }

    public void draw(Display display) {

        if (isVisible()) {
            // Draw path between ports with style dependant on path type
            Path path = getPath();
            if (path.getType() == Path.Type.MESH) {
                drawTrianglePath(display);
            } else if (path.getType() == Path.Type.ELECTRONIC) {
                drawLinePath(display);
            }
        }
    }

    public Path getPath() {
        return getFeature();
    }

    // TODO: Refactor. Put in Geometry/Shape.
    public void drawTrianglePath(Display display) {

        Paint paint = display.getPaint();

        Path path = getPath();

        Shape sourcePortShape = getScene().getShape(path.getSource());
        Shape targetPortShape = getScene().getShape(path.getTarget());

        // Show target port
        targetPortShape.setVisibility(Visibility.VISIBLE);
        //// TODO: targetPortShape.setPathVisibility(Visibility.VISIBLE);

        // Color
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15.0f);
        //paint.setColor(sourcePortShape.getUniqueColor());

        double pathRotationAngle = Geometry.calculateRotationAngle(
                sourcePortShape.getPosition(),
                targetPortShape.getPosition()
        );

        double triangleRotationAngle = pathRotationAngle + 90.0f;

        Point pathStartCoordinate = Geometry.calculatePoint(
                sourcePortShape.getPosition(),
                pathRotationAngle,
                2 * triangleSpacing
        );

        Point pathStopCoordinate = Geometry.calculatePoint(
                targetPortShape.getPosition(),
                pathRotationAngle + 180,
                2 * triangleSpacing
        );

        if (showDocks) {

            paint.setStyle(Paint.Style.FILL);
            Display.drawTriangle(
                    pathStartCoordinate,
                    triangleRotationAngle,
                    triangleWidth,
                    triangleHeight,
                    display
            );

            paint.setStyle(Paint.Style.FILL);
            Display.drawTriangle(
                    pathStopCoordinate,
                    triangleRotationAngle,
                    triangleWidth,
                    triangleHeight,
                    display
            );

        } else {

            Display.drawTrianglePath(
                    pathStartCoordinate,
                    pathStopCoordinate,
                    triangleWidth,
                    triangleHeight,
                    display
            );
        }
    }

    private void drawLinePath(Display display) {

        Paint paint = display.getPaint();

        Path path = getPath();

        Shape sourcePortShape = getScene().getShape(path.getSource());
        Shape targetPortShape = getScene().getShape(path.getTarget());

        // Show target port
        targetPortShape.setVisibility(Visibility.VISIBLE);
        //// TODO: targetPortShape.setPathVisibility(Visibility.VISIBLE);

        // Color
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(15.0f);
        //// TODO: paint.setColor(sourcePortShape.getUniqueColor());

        double pathRotationAngle = Geometry.calculateRotationAngle(
                sourcePortShape.getPosition(),
                targetPortShape.getPosition()
        );

        Point pathStartCoordinate = Geometry.calculatePoint(
                sourcePortShape.getPosition(),
                pathRotationAngle,
                0
        );

        Point pathStopCoordinate = Geometry.calculatePoint(
                targetPortShape.getPosition(),
                pathRotationAngle + 180,
                0
        );

        Display.drawLine(
                pathStartCoordinate,
                pathStopCoordinate,
                display
        );

    }
}
