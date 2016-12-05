package camp.computer.clay.lib.Geometry;

import java.util.List;

import camp.computer.clay.engine.component.Transform;

public abstract class Shape {

    // <TODO>
    // TODO: 11/15/2016 Delete this after creating Shape component.
    protected String label = ""; // Component

    // public boolean isBoundary = false;

    //    protected Transform imagePosition = null;
    protected Transform position = new Transform(0, 0);
    // TODO: private Transform facingAngle/normal; // Consider placing into Transform.
    // </TODO>

    protected String color = "#fff7f7f7";
    protected String outlineColor = "#ff000000";
    public double outlineThickness = 1.0;

    public Shape() {
    }

    public Shape(Transform position) {
        this.position.set(position);
    }

    public Transform getPosition() {
//        // <HACK>
//        position.set(0, 0);
//        position.rotation = 0;
//        // </HACK>
        return position;
    }

    public void setPosition(double x, double y) {
        this.position.set(x, y);
    }

    public void setPosition(Transform point) {
        this.position.set(point.x, point.y);
    }

    public void setRotation(double angle) {
        this.position.rotation = angle;
    }

    public double getRotation() {
        return this.position.rotation;
//        return 0;
    }

    public abstract List<Transform> getVertices();

    public void setColor(String color) {
        this.color = color;

        // <ANDROID>
        this.colorCode = android.graphics.Color.parseColor(color);
        // </ANDROID>
    }

    public String getColor() {
        return color;
    }

    // <ANDROID>
    public int colorCode = android.graphics.Color.WHITE;
    public int outlineColorCode = android.graphics.Color.BLACK;
    // </ANDROID>

    public void setOutlineColor(String color) {
        this.outlineColor = color;

        // <ANDROID>
        this.outlineColorCode = android.graphics.Color.parseColor(color);
        // </ANDROID>
    }

    public String getOutlineColor() {
        return outlineColor;
    }

    public void setOutlineThickness(double thickness) {
        this.outlineThickness = thickness;
    }

    public double getOutlineThickness() {
        return outlineThickness;
    }

    // <REFACTOR>
    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }
    // </REFACTOR>
}
