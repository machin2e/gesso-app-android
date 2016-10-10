package camp.computer.clay.util.geometry;

import java.util.ArrayList;
import java.util.List;

public abstract class Geometry {

    public static double getAngle(Point source, Point target) {
        return Geometry.getAngle(source.getAbsoluteX(), source.getAbsoluteY(), target.getAbsoluteX(), target.getAbsoluteY());
    }

    /**
     * Calculates the rotation angle in degrees from {@code source} to {@code target}.
     * <p>
     * Returns angle in degrees in the range [0,360), rotating CLOCKWISE, 0 and 360 degrees
     * represents NORTH, 90 degrees represents EAST, etc...
     * <p>
     * Assumes all pointerCoordinates are in the same coordinate parentSpace.  If they are not,
     * you will need to call SwingUtilities.convertPointToScreen or equivalent
     * on all arguments before passing them  to this function.
     *
     * @param x1 Point we are rotating around.
     * @param y1
     * @param x2 Point to which we want to calculate the rotation, relative to the center point.
     * @param y2
     * @return rotation in degrees.  This is the rotation from centerPt to targetPt.
     */
    public static double getAngle(double x1, double y1, double x2, double y2) {

        // calculate the rotation theta from the deltaY and deltaX values
        // (atan2 returns radians values from [-PI,PI])
        // 0 currently pointerCoordinates EAST.
        // NOTE: By preserving Y and X param order to atan2,  we are expecting
        // a CLOCKWISE rotation direction.
        double theta = Math.atan2(y2 - y1, x2 - x1);

        // rotate the theta rotation clockwise by 90 degrees
        // (this makes 0 point NORTH)
        // NOTE: adding to an rotation rotates it clockwise.
        // subtracting would rotate it counter-clockwise
//        theta += Math.PI / 2.0;

        // convert from radians to degrees
        // this will give you an rotation from [0->270],[-180,0]
        double angle = Math.toDegrees(theta);

        // convert to positive range [0-360)
        // since we want to prevent negative angles, adjust them now.
        // we can assume that atan2 will not return a negative value
        // greater than one partial rotation
//        if (rotation < 0) {
//            rotation += 360;
//        }

        return angle;
    }

    /**
     * Calculates coordinates of a point rotated about about another point by {@code angle} degrees.
     * <p>
     * References:
     * - http://www.gamefromscratch.com/post/2012/11/24/GameDev-math-recipes-Rotating-one-point-around-another-point.aspx
     *
     * @return
     */
//    public static Point rotatePoint(Point center, double angle, Point point) {
//        return Geometry.rotateTranslatePoint(center, angle + Geometry.getAngle(center, point), calculateDistance(center, point));
//    }

//    public static Point rotatePoint(double x1, double y1, double angle, double x2, double y2) {
//        return Geometry.rotateTranslatePoint(x1, y1, angle + Geometry.getAngle(x1, y1, x2, y2), calculateDistance(x1, y1, x2, y2));
//    }
    public static Point rotateTranslatePoint(Point originPoint, double rotation, double distance) {
        Point point = new Point();
        point.setAbsoluteX(originPoint.getAbsoluteX() + distance * Math.cos(Math.toRadians(rotation)));
        point.setAbsoluteY(originPoint.getAbsoluteY() + distance * Math.sin(Math.toRadians(rotation)));
        return point;
    }

//    public static Point rotateTranslatePoint(double x, double y, double rotation, double distance) {
//        Point point = new Point();
//        point.setAbsoluteX(x + distance * Math.cos(Math.toRadians(rotation)));
//        point.setAbsoluteY(y + distance * Math.sin(Math.toRadians(rotation)));
//        return point;
//    }

    public static Point midpoint(Point source, Point target) {
        Point midpoint = new Point(
                (source.x + target.x) / 2.0f,
                (source.y + target.y) / 2.0f,
                source.getReferencePoint()
        );
        return midpoint;
    }

//    //Compute the dot product AB . AC
//    private static double dotProduct(Point linePointA, Point linePointB, Point pointC) {
//        Point AB = new Point();
//        Point BC = new Point();
//        AB.x = (linePointB.x - linePointA.x);
//        AB.y = (linePointB.y - linePointA.y);
//        BC.x = (pointC.x - linePointB.x);
//        BC.y = (pointC.y - linePointB.y);
//        double dot = AB.x * BC.x + AB.y * BC.y;
//        return dot;
//    }
//
//    //Compute the cross product AB x AC
//    private static double crossProduct(Point linePointA, Point linePointB, Point pointC) {
//        Point AB = new Point();
//        Point AC = new Point();
//        AB.x = (linePointB.x - linePointA.x);
//        AB.y = (linePointB.y - linePointA.y);
//        AC.x = (pointC.x - linePointA.x);
//        AC.y = (pointC.y - linePointA.y);
//        double cross = AB.x * AC.y - AB.y * AC.x;
//        return cross;
//    }
//
//    /**
//     * Calculates the distance between the point {@code point} and the line or segment through
//     * {@code linePointA} and {@code linePointB}.
//     *
//     * @param linePointA
//     * @param linePointB
//     * @param point
//     * @param isSegment
//     * @return
//     */
//    //
//    //if isSegment is true, AB is a segment, not a line.
//    // References:
//    // - http://stackoverflow.com/questions/4438244/how-to-calculate-shortest-2d-distance-between-a-point-and-a-line-segment-in-all
//    public static double distanceToLine(Point linePointA, Point linePointB, Point point, boolean isSegment) {
//        double distance = crossProduct(linePointA, linePointB, point) / calculateDistance(linePointA, linePointB);
//        if (isSegment) {
//            double dot1 = dotProduct(linePointA, linePointB, point);
//            if (dot1 > 0) {
//                return calculateDistance(linePointB, point);
//            }
//
//            double dot2 = dotProduct(linePointB, linePointA, point);
//            if (dot2 > 0) {
//                return calculateDistance(linePointA, point);
//            }
//        }
//        return Math.abs(distance);
//    }

    public static Point getCentroidPoint(List<Point> points) {

        Point centroidPosition = new Point(0, 0);

        for (int i = 0; i < points.size(); i++) {

            Point point = points.get(i);

//            centroidPosition.setAbsolute(
//                    centroidPosition.getAbsoluteX() + point.getAbsoluteX(),
//                    centroidPosition.getAbsoluteY() + point.getAbsoluteY()
//            );
            centroidPosition.set(
                    centroidPosition.x + point.x,
                    centroidPosition.y + point.y
            );
        }

//        centroidPosition.setAbsolute(
//                centroidPosition.getAbsoluteX() / points.size(),
//                centroidPosition.getAbsoluteY() / points.size()
//        );
        centroidPosition.set(
                centroidPosition.x / points.size(),
                centroidPosition.y / points.size()
        );

        return centroidPosition;
    }

    // TODO: Cache the result on a per-shape basis... remove per-step Rectangle allocation
    public static Rectangle getBoundingBox(List<Point> points) {

        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);

            double absoluteX = point.getAbsoluteX();
            double absoluteY = point.getAbsoluteY();

            if (absoluteX < minX) {
                minX = absoluteX;
            }
            if (absoluteY < minY) {
                minY = absoluteY;
            }
            if (absoluteX > maxX) {
                maxX = absoluteX;
            }
            if (absoluteY > maxY) {
                maxY = absoluteY;
            }
        }

        return new Rectangle(minX, minY, maxX, maxY);
    }

    /**
     * Calculates and returns the center {@code Point} of the {@code Point}s in {@code points}.
     *
     * @param points
     * @return
     */
    public static Point getCenterPoint(List<Point> points) {
        return getBoundingBox(points).getPosition();
    }

//    /**
//     * Returns the {@code Point} in {@code points} nearest to {@code point}.
//     *
//     * @param point
//     * @param points
//     * @return
//     */
//    public static Point getNearestPoint(Point point, List<Point> points) {
//
//        // Initialize point
//        Point nearestPoint = points.get(0);
//        double nearestDistance = calculateDistance(point, nearestPoint);
//
//        // Search for the nearest point
//        for (int i = 0; i < points.size(); i++) {
//            double distance = calculateDistance(points.get(i), point);
//            if (distance < nearestDistance) {
//                nearestPoint.set(point);
//            }
//        }
//
//        return nearestPoint;
//    }

    /**
     * General-purpose function that returns true if the given point is contained inside the shape
     * defined by the boundary pointerCoordinates.
     *
     * @param vertices The vertices defining the boundary polygon
     * @param point    The point to check
     * @return true If the point is inside the boundary, false otherwise
     * @see <a href="http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html">PNPOLY - Point Inclusion in Polygon Test (W. Randolph Franklin)</a>
     */
    public static boolean contains(List<Point> vertices, Point point) {

        // Setup
        double minX = vertices.get(0).getAbsoluteX();
        double maxX = vertices.get(0).getAbsoluteX();
        double minY = vertices.get(0).getAbsoluteY();
        double maxY = vertices.get(0).getAbsoluteY();

        for (int i = 1; i < vertices.size(); i++) {
            Point vertex = vertices.get(i);
            minX = Math.min(vertex.getAbsoluteX(), minX);
            maxX = Math.max(vertex.getAbsoluteX(), maxX);
            minY = Math.min(vertex.getAbsoluteY(), minY);
            maxY = Math.max(vertex.getAbsoluteY(), maxY);
        }

        if (point.getAbsoluteX() < minX || point.getAbsoluteX() > maxX || point.getAbsoluteY() < minY || point.getAbsoluteY() > maxY) {
            return false;
        }

        // Procedure
        boolean isContained = false;
        for (int i = 0, j = vertices.size() - 1; i < vertices.size(); j = i++) {
            if ((vertices.get(i).getAbsoluteY() > point.getAbsoluteY()) != (vertices.get(j).getAbsoluteY() > point.getAbsoluteY()) &&
                    point.getAbsoluteX() < (vertices.get(j).getAbsoluteX() - vertices.get(i).getAbsoluteX()) * (point.getAbsoluteY() - vertices.get(i).getAbsoluteY()) / (vertices.get(j).getAbsoluteY() - vertices.get(i).getAbsoluteY()) + vertices.get(i).getAbsoluteX()) {
                isContained = !isContained;
            }
        }

        return isContained;
    }

    public static List<Point> getRegularPolygon(Point position, double radius, int segmentCount) {

        List<Point> vertices = new ArrayList<>();

        for (int i = 0; i < segmentCount; i++) {

            // Calculate point prior to rotation
            Point vertexPosition = new Point(
                    (0 + radius * (Math.cos(2.0f * Math.PI * (double) i / (double) segmentCount)) + Math.toRadians(position.rotation)),
                    (0 + radius * (Math.sin(2.0f * Math.PI * (double) i / (double) segmentCount)) + Math.toRadians(position.rotation)),
                    position
            );

            vertices.add(vertexPosition);
        }

        return vertices;
    }

//    public static List<Point> getArc(Point centerPosition, double radius, double startAngle, double stopAngle, int segmentCount) {
//
//        Log.v("Geometry", "getArc");
//
//        List<Point> vertices = new ArrayList<>();
//
//        double angleIncrement = (stopAngle - startAngle) / segmentCount;
//
//        for (int i = 0; i < segmentCount; i++) {
//
//            Point vertexPosition = new Point(
//                    radius * Math.cos(Math.toRadians(startAngle + i * angleIncrement)), // + Math.toRadians(centerPosition.getRelativeAngle()),
//                    radius * Math.sin(Math.toRadians(startAngle + i * angleIncrement)), // + Math.toRadians(centerPosition.getRelativeAngle()),
//                    centerPosition
//            );
//
//            vertices.add(vertexPosition);
//        }
//
//        return vertices;
//    }

    public static double calculateDistance(Point source, Point target) {
        return calculateDistance(source.getAbsoluteX(), source.getAbsoluteY(), target.getAbsoluteX(), target.getAbsoluteY());
    }

    public static double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    // Returns 1 if the lines intersect, otherwise 0. In addition, if the lines
    // intersect the intersection point may be stored in the floats i_x and i_y.
    //
    // Credit:
    // This algorithm is adapted from "Tricks of the Windows Game Programming Gurus" by Andre
    // Lamothe, my first book covering graphics programming.
//    public static boolean testLineIntersection(float p0_x, float p0_y, float p1_x, float p1_y,
//                                               float p2_x, float p2_y, float p3_x, float p3_y /*, float*i_x, float*i_y*/) {
//        float s1_x, s1_y, s2_x, s2_y;
//        s1_x = p1_x - p0_x;
//        s1_y = p1_y - p0_y;
//        s2_x = p3_x - p2_x;
//        s2_y = p3_y - p2_y;
//
//        float s, t;
//        s = (-s1_y * (p0_x - p2_x) + s1_x * (p0_y - p2_y)) / (-s2_x * s1_y + s1_x * s2_y);
//        t = (s2_x * (p0_y - p2_y) - s2_y * (p0_x - p2_x)) / (-s2_x * s1_y + s1_x * s2_y);
//
//        if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
//            // Collision detected
////            if (i_x != NULL)
////            *i_x = p0_x + (t * s1_x);
////            if (i_y != NULL)
////            *i_y = p0_y + (t * s1_y);
//            return 1;
//        }
//
//        return 0; // No collision
//    }
//
//    // Returns 1 if the lines intersect, otherwise 0. In addition, if the lines
//    // intersect the intersection point may be stored in the floats i_x and i_y.
//    //
//    // Credit:
//    // This algorithm is adapted from "Tricks of the Windows Game Programming Gurus" by Andre
//    // Lamothe, my first book covering graphics programming.
//    public static boolean testLineIntersection(Point p0, Point p1, Point p2, Point p3 /*, float*i_x, float*i_y*/) {
//        double s1_x, s1_y, s2_x, s2_y;
//        s1_x = p1.getAbsoluteX() - p0.getAbsoluteX();
//        s1_y = p1.getAbsoluteY() - p0.getAbsoluteY();
//        s2_x = p3.getAbsoluteX() - p2.getAbsoluteX();
//        s2_y = p3.getAbsoluteY() - p2.getAbsoluteY();
//
//        double s, t;
//        s = (-s1_y * (p0.getAbsoluteX() - p2.getAbsoluteX()) + s1_x * (p0.getAbsoluteY() - p2.getAbsoluteY())) / (-s2_x * s1_y + s1_x * s2_y);
//        t = (s2_x * (p0.getAbsoluteY() - p2.getAbsoluteY()) - s2_y * (p0.getAbsoluteX() - p2.getAbsoluteX())) / (-s2_x * s1_y + s1_x * s2_y);
//
//        if (s >= 0 && s <= 1 && t >= 0 && t <= 1) {
//            // Collision detected
////            if (i_x != NULL)
////            *i_x = p0_x + (t * s1_x);
////            if (i_y != NULL)
////            *i_y = p0_y + (t * s1_y);
//            return true;
//        }
//
//        return false; // No collision
//    }
}
