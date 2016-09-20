package camp.computer.clay.space.image;

import android.graphics.Paint;

import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Path;
import camp.computer.clay.model.interaction.Action;
import camp.computer.clay.model.interaction.Event;
import camp.computer.clay.model.interaction.ActionListener;
import camp.computer.clay.space.architecture.Image;
import camp.computer.clay.space.util.Visibility;
import camp.computer.clay.space.util.geometry.Geometry;
import camp.computer.clay.space.util.geometry.Point;
import camp.computer.clay.space.architecture.Shape;

public class PathImage extends Image<Path> {

    private Visibility dockVisibility = Visibility.VISIBLE;

    private double triangleWidth = 20;
    private double triangleHeight = triangleWidth * (Math.sqrt(3.0) / 2);
    private double triangleSpacing = 35;

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

    public Path getPath() {
        return getEntity();
    }

    public void update() {
    }

    public void setDockVisibility(Visibility visibility) {
        this.dockVisibility = visibility;
    }

    public Visibility getDockVisibility() {
        return this.dockVisibility;
    }

    public boolean isDockVisible() {
        return this.dockVisibility == Visibility.VISIBLE;
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

    // TODO: Refactor. Put in Geometry/Shape.
    public void drawTrianglePath(Display display) {

        Paint paint = display.getPaint();

        Path path = getPath();

        Shape sourcePortShape = getSpace().getShape(path.getSource());
        Shape targetPortShape = getSpace().getShape(path.getTarget());

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

        if (dockVisibility == Visibility.VISIBLE) {

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

        Shape sourcePortShape = getSpace().getShape(path.getSource());
        Shape targetPortShape = getSpace().getShape(path.getTarget());

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
