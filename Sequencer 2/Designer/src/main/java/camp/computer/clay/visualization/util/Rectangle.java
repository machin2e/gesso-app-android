package camp.computer.clay.visualization.util;

import android.graphics.PointF;

import java.util.ArrayList;

public class Rectangle extends Shape {

    // TODO: Replace with PointHolder
    private PointHolder position = new PointHolder(0, 0);

    private double width = 0;
    private double height = 0;

    public Rectangle (double width, double height) {
        this.width = width;
        this.height = height;
    }

    public Rectangle(PointHolder position, double width, double height) {
        this.position.set(position);
        this.width = width;
        this.height = height;
    }

    public Rectangle (double left, double top, double right, double bottom) {
        this.width = (right - left);
        this.height = (bottom - top);
        this.position = new PointHolder (
                (right + left) / 2.0,
                (top + bottom) / 2.0
        );
    }

    public PointHolder getPosition () {
        return position;
    }

    public void setPosition (PointF position) {
        this.position.set(position.x, position.y);
    }

    public PointHolder getTopLeft () {
        return new PointHolder (getLeft(), getTop());
    }

    public PointHolder getTopRight () {
        return new PointHolder (getRight(), getTop());
    }

    public PointHolder getBottomRight () {
        return new PointHolder (getRight(), getBottom());
    }

    public PointHolder getBottomLeft () {
        return new PointHolder (getLeft(), getBottom());
    }

    public ArrayList<PointHolder> getVertices () {
        ArrayList<PointHolder> vertices = new ArrayList<>();
        vertices.add(getTopLeft());
        vertices.add(getTopRight());
        vertices.add(getBottomRight());
        vertices.add(getBottomLeft());
        return vertices;
    }

    public double getWidth () {
        return this.width;
    }

    public void setWidth (double width) {
        this.width = width;
    }

    public double getHeight () {
        return this.height;
    }

    public void setHeight (double height) {
        this.height = height;
    }

    public double getLeft () {
        return this.position.getX() - (width / 2.0f);
    }

    public double getTop () {
        return this.position.getY() - (height / 2.0f);
    }

    public double getRight () {
        return this.position.getX() + (width / 2.0f);
    }

    public double getBottom () {
        return this.position.getY() + (height / 2.0f);
    }

    public double getArea () {
        return this.width * this.height;
    }

    public double getPerimeter () {
        return 2 * (this.width + this.height);
    }

    public double getDiagonalLength () {
        return (double) Math.sqrt(Math.pow(this.width, 2) + Math.pow(this.height, 2));
    }
}
