package camp.computer.clay.sprite.util;

import android.graphics.PointF;

public abstract class Geometry {

    public static double calculateDistance(PointF from, PointF to) {
        return calculateDistance(from.x, from.y, to.x, to.y);
    }

    public static double calculateDistance(float x, float y, float x2, float y2) {
        double distanceSquare = Math.pow (x - x2, 2) + Math.pow (y - y2, 2);
        double distance = Math.sqrt (distanceSquare);
        return distance;
    }

    /**
     * Calculates the angle from centerPt to targetPt in degrees.
     * The return should range from [0,360), rotating CLOCKWISE,
     * 0 and 360 degrees represents NORTH,
     * 90 degrees represents EAST, etc...
     *
     * Assumes all points are in the same coordinate space.  If they are not,
     * you will need to call SwingUtilities.convertPointToScreen or equivalent
     * on all arguments before passing them  to this function.
     *
     * @param centerPt   Point we are rotating around.
     * @param targetPt   Point we want to calcuate the angle to.
     * @return angle in degrees.  This is the angle from centerPt to targetPt.
     */
    public static float calculateRotationAngle(PointF centerPt, PointF targetPt) {

        // calculate the angle theta from the deltaY and deltaX values
        // (atan2 returns radians values from [-PI,PI])
        // 0 currently points EAST.
        // NOTE: By preserving Y and X param order to atan2,  we are expecting
        // a CLOCKWISE angle direction.
        double theta = Math.atan2(targetPt.y - centerPt.y, targetPt.x - centerPt.x);

        // rotate the theta angle clockwise by 90 degrees
        // (this makes 0 point NORTH)
        // NOTE: adding to an angle rotates it clockwise.
        // subtracting would rotate it counter-clockwise
        theta += Math.PI/2.0;

        // convert from radians to degrees
        // this will give you an angle from [0->270],[-180,0]
        double angle = Math.toDegrees(theta);

        // convert to positive range [0-360)
        // since we want to prevent negative angles, adjust them now.
        // we can assume that atan2 will not return a negative value
        // greater than one partial rotation
        if (angle < 0) {
            angle += 360;
        }

        return (float) angle;
    }

    public static PointF calculatePoint(PointF from, float angle, float distance) {
        PointF point = new PointF();
        point.x = from.x + distance * (float) Math.cos(Math.toRadians(angle - 90));
        point.y = from.y + distance * (float) Math.sin(Math.toRadians(angle - 90));
        return point;
    }

    public static PointF calculateMidpoint(PointF from, PointF to) {
        PointF midpoint = new PointF();
        midpoint.x = ((from.x + to.x) / 2.0f);
        midpoint.y = ((from.y + to.y) / 2.0f);
        return midpoint;
    }

    //Compute the dot product AB . AC
    public static double DotProduct(PointF pointA, PointF pointB, PointF pointC)
    {
        PointF AB = new PointF();
        PointF BC = new PointF();
        AB.x = pointB.x - pointA.x;
        AB.y = pointB.y - pointA.y;
        BC.x = pointC.x - pointB.x;
        BC.y = pointC.y - pointB.y;
        double dot = AB.x * BC.x + AB.y * BC.y;

        return dot;
    }

    //Compute the cross product AB x AC
    public static double CrossProduct(PointF pointA, PointF pointB, PointF pointC)
    {
        PointF AB = new PointF();
        PointF AC = new PointF();
        AB.x = pointB.x - pointA.x;
        AB.y = pointB.y - pointA.y;
        AC.x = pointC.x - pointA.x;
        AC.y = pointC.y - pointA.y;
        double cross = AB.x * AC.y - AB.y * AC.x;

        return cross;
    }

//    //Compute the distance from A to B
//    double Distance(PointF pointA, PointF pointB)
//    {
//        float d1 = pointA[0] - pointB[0];
//        float d2 = pointA[1] - pointB[1];
//
//        return Math.sqrt(d1 * d1 + d2 * d2);
//    }

    //Compute the distance from AB to C
    //if isSegment is true, AB is a segment, not a line.
    public static double LineToPointDistance2D(PointF pointA, PointF pointB, PointF pointC,
                                 boolean isSegment)
    {
        double dist = CrossProduct(pointA, pointB, pointC) / Geometry.calculateDistance(pointA, pointB);
        if (isSegment)
        {
            double dot1 = DotProduct(pointA, pointB, pointC);
            if (dot1 > 0)
                return Geometry.calculateDistance(pointB, pointC);

            double dot2 = DotProduct(pointB, pointA, pointC);
            if (dot2 > 0)
                return Geometry.calculateDistance(pointA, pointC);
        }
        return Math.abs(dist);
    }
}
