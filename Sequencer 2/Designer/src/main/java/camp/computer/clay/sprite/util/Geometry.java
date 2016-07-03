package camp.computer.clay.sprite.util;

import android.graphics.PointF;

import java.util.ArrayList;

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
//        theta += Math.PI / 2.0;

        // convert from radians to degrees
        // this will give you an angle from [0->270],[-180,0]
        double angle = Math.toDegrees(theta);

        // convert to positive range [0-360)
        // since we want to prevent negative angles, adjust them now.
        // we can assume that atan2 will not return a negative value
        // greater than one partial rotation
//        if (angle < 0) {
//            angle += 360;
//        }

        return (float) angle;
    }

    /**
     * Calculates new point coordinates for a given point rotated by an angle about another origin
     * point.
     * @return
     */
    public static PointF calculateRotatedPoint(PointF originPoint, float angle, PointF point) {
        return Geometry.calculatePoint(originPoint, angle + Geometry.calculateRotationAngle(originPoint, point), (float) Geometry.calculateDistance(originPoint, point));
    }

    public static PointF calculatePoint(PointF originPoint, float rotation, float distance) {
        PointF point = new PointF();
        point.x = originPoint.x + distance * (float) Math.cos(Math.toRadians(rotation));
        point.y = originPoint.y + distance * (float) Math.sin(Math.toRadians(rotation));
        return point;
    }

    public static PointF calculateMidpoint(PointF from, PointF to) {
        PointF midpoint = new PointF();
        midpoint.x = ((from.x + to.x) / 2.0f);
        midpoint.y = ((from.y + to.y) / 2.0f);
        return midpoint;
    }

    //Compute the dot product AB . AC
    public static double calculateDotProduct(PointF linePointA, PointF linePointB, PointF pointC)
    {
        PointF AB = new PointF();
        PointF BC = new PointF();
        AB.x = linePointB.x - linePointA.x;
        AB.y = linePointB.y - linePointA.y;
        BC.x = pointC.x - linePointB.x;
        BC.y = pointC.y - linePointB.y;
        double dot = AB.x * BC.x + AB.y * BC.y;

        return dot;
    }

    //Compute the cross product AB x AC
    public static double calculuateCrossProduct(PointF linePointA, PointF linePointB, PointF pointC)
    {
        PointF AB = new PointF();
        PointF AC = new PointF();
        AB.x = linePointB.x - linePointA.x;
        AB.y = linePointB.y - linePointA.y;
        AC.x = pointC.x - linePointA.x;
        AC.y = pointC.y - linePointA.y;
        double cross = AB.x * AC.y - AB.y * AC.x;

        return cross;
    }

    //Compute the distance from AB to C
    //if isSegment is true, AB is a segment, not a line.
    public static double calculateLineToPointDistance(PointF linePointA, PointF linePointB, PointF pointC,
                                                      boolean isSegment)
    {
        double dist = calculuateCrossProduct(linePointA, linePointB, pointC) / Geometry.calculateDistance(linePointA, linePointB);
        if (isSegment)
        {
            double dot1 = calculateDotProduct(linePointA, linePointB, pointC);
            if (dot1 > 0)
                return Geometry.calculateDistance(linePointB, pointC);

            double dot2 = calculateDotProduct(linePointB, linePointA, pointC);
            if (dot2 > 0)
                return Geometry.calculateDistance(linePointA, pointC);
        }
        return Math.abs(dist);
    }

    public static PointF calculateCentroid(ArrayList<PointF> points)  {
        float centroidX = 0.0f;
        float centroidY = 0.0f;

        for(PointF point : points) {
            centroidX += point.x;
            centroidY += point.y;
        }
        return new PointF(centroidX / points.size(), centroidY / points.size());
    }

    public static float[] calculateBoundingBox(ArrayList<PointF> points) {
        float[] boundaryPoints = new float[4]; // left, top, right, bottom
        // TODO: center, width, height

        float minX = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE;
        float minY = Float.MAX_VALUE;
        float maxY = Float.MIN_VALUE;

        for (PointF point: points) {
            if (point.x < minX) {
                minX = point.x;
            }
            if (point.y < minY) {
                minY = point.y;
            }
            if (point.x > maxX) {
                maxX = point.x;
            }
            if (point.y > minY) {
                minY = point.y;
            }
        }

        float left = minX;
        float top = minY;
        float right = maxX;
        float bottom = maxY;

        boundaryPoints[0] = left;
        boundaryPoints[1] = top;
        boundaryPoints[2] = right;
        boundaryPoints[3] = bottom;

        return boundaryPoints;
    }

    public static PointF calculateNearestPoint(PointF sourcePoint, ArrayList<PointF> points) {

        // Initialize point
        PointF nearestPoint = points.get(0);
        float nearestDistance = (float) Geometry.calculateDistance(sourcePoint, nearestPoint);

        // Search for the nearest point
        for (PointF point: points) {
            float distance = (float) Geometry.calculateDistance(sourcePoint, point);
            if (distance < nearestDistance) {
                nearestPoint.set(point);
            }
        }

        return nearestPoint;
    }

    public static ArrayList<PointF> quickHull(ArrayList<PointF> points)
    {
        ArrayList<PointF> convexHull = new ArrayList<PointF>();
        if (points.size() < 3)
            return (ArrayList) points.clone();

        int minPoint = -1, maxPoint = -1;
        float minX = Integer.MAX_VALUE;
        float maxX = Integer.MIN_VALUE;
        for (int i = 0; i < points.size(); i++)
        {
            if (points.get(i).x < minX)
            {
                minX = points.get(i).x;
                minPoint = i;
            }
            if (points.get(i).x > maxX)
            {
                maxX = points.get(i).x;
                maxPoint = i;
            }
        }
        PointF A = points.get(minPoint);
        PointF B = points.get(maxPoint);
        convexHull.add(A);
        convexHull.add(B);
        points.remove(A);
        points.remove(B);

        ArrayList<PointF> leftSet = new ArrayList<PointF>();
        ArrayList<PointF> rightSet = new ArrayList<PointF>();

        for (int i = 0; i < points.size(); i++)
        {
            PointF p = points.get(i);
            if (pointLocation(A, B, p) == -1)
                leftSet.add(p);
            else if (pointLocation(A, B, p) == 1)
                rightSet.add(p);
        }
        hullSet(A, B, rightSet, convexHull);
        hullSet(B, A, leftSet, convexHull);

        return convexHull;
    }

    public static float distance(PointF A, PointF B, PointF C)
    {
        float ABx = B.x - A.x;
        float ABy = B.y - A.y;
        float num = ABx * (A.y - C.y) - ABy * (A.x - C.x);
        if (num < 0)
            num = -num;
        return num;
    }

    public static void hullSet(PointF A, PointF B, ArrayList<PointF> set,
                        ArrayList<PointF> hull)
    {
        int insertPosition = hull.indexOf(B);
        if (set.size() == 0)
            return;
        if (set.size() == 1)
        {
            PointF p = set.get(0);
            set.remove(p);
            hull.add(insertPosition, p);
            return;
        }
        float dist = Integer.MIN_VALUE;
        int furthestPoint = -1;
        for (int i = 0; i < set.size(); i++)
        {
            PointF p = set.get(i);
            float distance = distance(A, B, p);
            if (distance > dist)
            {
                dist = distance;
                furthestPoint = i;
            }
        }
        PointF P = set.get(furthestPoint);
        set.remove(furthestPoint);
        hull.add(insertPosition, P);

        // Determine who's to the left of AP
        ArrayList<PointF> leftSetAP = new ArrayList<PointF>();
        for (int i = 0; i < set.size(); i++)
        {
            PointF M = set.get(i);
            if (pointLocation(A, P, M) == 1)
            {
                leftSetAP.add(M);
            }
        }

        // Determine who's to the left of PB
        ArrayList<PointF> leftSetPB = new ArrayList<PointF>();
        for (int i = 0; i < set.size(); i++)
        {
            PointF M = set.get(i);
            if (pointLocation(P, B, M) == 1)
            {
                leftSetPB.add(M);
            }
        }
        hullSet(A, P, leftSetAP, hull);
        hullSet(P, B, leftSetPB, hull);

    }

    public static int pointLocation(PointF A, PointF B, PointF P)
    {
        float cp1 = (B.x - A.x) * (P.y - A.y) - (B.y - A.y) * (P.x - A.x);
        if (cp1 > 0)
            return 1;
        else if (cp1 == 0)
            return 0;
        else
            return -1;
    }
}
