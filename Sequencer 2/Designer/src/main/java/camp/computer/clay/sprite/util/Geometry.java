package camp.computer.clay.sprite.util;

import android.graphics.PointF;

public abstract class Geometry {

    public static double getDistance (PointF from, PointF to) {
        return getDistance(from.x, from.y, to.x, to.y);
    }

    public static double getDistance (float x, float y, float x2, float y2) {
        double distanceSquare = Math.pow (x - x2, 2) + Math.pow (y - y2, 2);
        double distance = Math.sqrt (distanceSquare);
        return distance;
    }
}
