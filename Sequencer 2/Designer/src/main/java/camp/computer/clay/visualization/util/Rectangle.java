package camp.computer.clay.visualization.util;

import android.graphics.PointF;

public class Rectangle {

    // TODO: Replace with Point
    private PointF position = new PointF(0, 0);

    private float width = 0;
    private float height = 0;

    public Rectangle(PointF position, float width, float height) {
        this.position.set(position);
        this.width = width;
        this.height = height;
    }

    public Rectangle (float left, float top, float right, float bottom) {
        this.width = (right - left);
        this.height = (bottom - top);
        this.position = new PointF (
                left + this.width / 2.0f,
                top + this.height / 2.0f
        );
    }

    public PointF getPosition () {
        return position;
    }

    public void setPosition (PointF position) {
        this.position.x = position.x;
        this.position.y = position.y;
    }

    public float getWidth () {
        return this.width;
    }

    public void setWidth (float width) {
        this.width = width;
    }

    public float getHeight () {
        return this.height;
    }

    public void setHeight (float height) {
        this.height = height;
    }

    public float getLeft () {
        return this.position.x - (width / 2.0f);
    }

    public float getTop () {
        return this.position.y - (height / 2.0f);
    }

    public float getRight () {
        return this.position.x + (width / 2.0f);
    }

    public float getBottom () {
        return this.position.y + (height / 2.0f);
    }
}
