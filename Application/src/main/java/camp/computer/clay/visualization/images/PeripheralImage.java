package camp.computer.clay.visualization.images;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import camp.computer.clay.application.VisualizationSurface;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.model.simulation.Model;
import camp.computer.clay.visualization.architecture.Image;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.Point;
import camp.computer.clay.visualization.util.Shape;

public class PeripheralImage extends Image {

    public final static String TYPE = "peripheral";

    private double width = 175;
    private double height = 175;

    private int uniqueColor = Color.BLACK;

    public PeripheralImage(Model model) {
        super(model);
    }

    @Override
    public void update() {
    }

    @Override
    public void draw(VisualizationSurface visualizationSurface) {
        if (isVisible()) {

            // Port
//        setVisibility(true);
            drawPeripheralImage(visualizationSurface);
        }
    }

    private String colorString = "f7f7f7"; // "f7f7f7"; // "404040"; // "414141";
    private int color = Color.parseColor("#ff" + colorString); // Color.parseColor("#212121");
    private boolean showOutline = true;
    private String outlineColorString = "414141";
    private int outlineColor = Color.parseColor("#ff" + outlineColorString); // Color.parseColor("#737272");
    private double outlineThickness = 3.0f;

    public void drawPeripheralImage(VisualizationSurface visualizationSurface) {

        Canvas canvas = visualizationSurface.getCanvas();
        Paint paint = visualizationSurface.getPaint();

        // Color
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(this.color);
        Shape.drawRectangle(getPosition(), getRotation(), width, height, canvas, paint);

        // Outline
        if (this.showOutline) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(this.outlineColor);
            paint.setStrokeWidth((float) outlineThickness);
            Shape.drawRectangle(getPosition(), getRotation(), width, height, canvas, paint);
        }
    }

    @Override
    public boolean isTouching(Point point) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getPosition().getX(), (int) this.getPosition().getY(), point.getX(), point.getY()) < (height / 2.0f);
        } else {
            return false;
        }
    }

    @Override
    public boolean isTouching(Point point, double padding) {
        if (isVisible()) {
            return Geometry.calculateDistance((int) this.getPosition().getX(), (int) this.getPosition().getY(), point.getX(), point.getY()) < (height / 2.0f + padding);
        } else {
            return false;
        }
    }

    @Override
    public void onTouchInteraction(TouchInteraction touchInteraction) {

    }
}
