package camp.computer.clay.sprite.util.physics;

import android.graphics.PointF;

public class VectorF {
    private float magnitude = 0.0f;
    private float direction = 0.0f;

    public VectorF (float magnitude, float angle) {
        this.magnitude = magnitude;
        this.direction = angle;

        if (this.magnitude < 0) {
            // resolve negative magnitude by reversing direction
            this.magnitude = -this.magnitude;
            this.direction = (180.0f + this.direction) % 360.0f;
        }

        // resolve negative direction
        if (this.direction < 0) this.direction = (360.0f + this.direction);
    }

    public float getMagnitude() {
        return this.magnitude;
    }

    public void setMagnitude(float magnitude) {
        this.magnitude = magnitude;
    }

    public float getDirection() {
        return this.direction;
    }

    public void setDirection(float direction) {
        this.direction = direction;
    }

    public static VectorF add(VectorF a, VectorF b) {
        // break into x-y components
        double aX = a.magnitude * Math.cos((Math.PI / 180.0) * a.direction);
        double aY = a.magnitude * Math.sin((Math.PI / 180.0) * a.direction);

        double bX = b.magnitude * Math.cos((Math.PI / 180.0) * b.direction);
        double bY = b.magnitude * Math.sin((Math.PI / 180.0) * b.direction);

        // add x-y components
        aX += bX;
        aY += bY;

        // pythagorus' theorem to get resultant magnitude
        float magnitude = (float) Math.sqrt(Math.pow(aX, 2) + Math.pow(aY, 2));

        // calculate direction using inverse tangent
        float direction;
        if (magnitude == 0)
            direction = 0;
        else
            direction = (180.0f / (float) Math.PI) * (float) Math.atan2(aY, aX);

        return new VectorF(magnitude, direction);
    }

    public static VectorF multiply(VectorF vector, float multiplier) {
        // only magnitude is affected by scalar multiplication
        return new VectorF(vector.magnitude * multiplier, vector.direction);
    }

    /// <summary>
    /// Converts the vector into an X-Y coordinate representation.
    /// </summary>
    /// <returns>An X-Y coordinate representation of the Vector.</returns>
    public PointF ToPoint() {
        // break into x-y components
        double aX = magnitude * Math.cos((Math.PI / 180.0) * direction);
        double aY = magnitude * Math.sin((Math.PI / 180.0) * direction);

        return new PointF((float) aX, (float) aY);
    }
}
