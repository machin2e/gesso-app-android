package camp.computer.clay.util.geometry;

import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class Geometry {

    public static void translatePoint(Point point, double x, double y) {
        point.x = point.x + x;
        point.y = point.y + y;
    }

    /**
     * Rotates {@code point} by {@code rotation} degrees about point (0, 0).
     * @param point
     * @param rotation
     */
    public static void rotatePoint(Point point, double rotation) {

        double distance = distance(0, 0, point.x, point.y);
        double totalRotation = rotation + Geometry.getAngle(0, 0, point.x, point.y);

        point.x = 0 + distance * Math.cos(Math.toRadians(totalRotation));
        point.y = 0 + distance * Math.sin(Math.toRadians(totalRotation));
    }

    /**
     * Rotates {@code point} by {@code rotation} degrees about {@code referencePoint}. Stores the
     * result in {@code point}.
     */
    public static void rotatePoint(Point point, double rotation, Point referencePoint) {

        double distance = distance(referencePoint, point);
        double totalRotation = rotation + Geometry.getAngle(referencePoint, point);

        point.x = referencePoint.x + distance * Math.cos(Math.toRadians(totalRotation));
        point.y = referencePoint.y + distance * Math.sin(Math.toRadians(totalRotation));
    }

    public static double distance(Point source, Point target) {
        return distance(source.x, source.y, target.x, target.y);
    }

    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    /**
     * General-purpose function that returns true if the given point is contained inside the shape
     * defined by the boundary pointerCoordinates.
     *
     * @param vertices The boundary defining the boundary polygon
     * @param point    The point to check
     * @return true If the point is inside the boundary, false otherwise
     * @see <a href="http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html">PNPOLY - Point Inclusion in Polygon Test (W. Randolph Franklin)</a>
     */
    public static boolean contains(List<Point> vertices, Point point) {

        double minX = vertices.get(0).x;
        double maxX = vertices.get(0).x;
        double minY = vertices.get(0).y;
        double maxY = vertices.get(0).y;

        for (int i = 1; i < vertices.size(); i++) {
            Point vertex = vertices.get(i);
            minX = Math.min(vertex.x, minX);
            maxX = Math.max(vertex.x, maxX);
            minY = Math.min(vertex.y, minY);
            maxY = Math.max(vertex.y, maxY);
        }

        if (point.x < minX || point.x > maxX || point.y < minY || point.y > maxY) {
            return false;
        }

        // Procedure
        boolean isContained = false;
        for (int i = 0, j = vertices.size() - 1; i < vertices.size(); j = i++) {
            if ((vertices.get(i).y > point.y) != (vertices.get(j).y > point.y) &&
                    point.x < (vertices.get(j).x - vertices.get(i).x) * (point.y - vertices.get(i).y) / (vertices.get(j).y - vertices.get(i).y) + vertices.get(i).x) {
                isContained = !isContained;
            }
        }

        return isContained;
    }

    public static Point midpoint(Point p1, Point p2) {
        Point midpoint = new Point(
                (p1.x + p2.x) / 2.0f,
                (p1.y + p2.y) / 2.0f
        );
        return midpoint;
    }

    public static Point midpoint(double x1, double y1, double x2, double y2) {
        Point midpoint = new Point(
                (x1 + x2) / 2.0f,
                (y1 + y2) / 2.0f
        );
        return midpoint;
    }

    public static double getAngle(Point source, Point target) {
        return Geometry.getAngle(source.x, source.y, target.x, target.y);
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
        if (angle < 0) {
            angle += 360;
        }

        return angle;
    }

    public static Point getRotateTranslatePoint(Point referencePoint, double rotation, double distance) {
        Point point = new Point();
        point.x = referencePoint.x + distance * Math.cos(Math.toRadians(rotation));
        point.y = referencePoint.y + distance * Math.sin(Math.toRadians(rotation));
        return point;
    }

    public static Point getCentroidPoint(List<Point> points) {

        Point centroidPoint = new Point(0, 0);

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);
            centroidPoint.set(centroidPoint.x + point.x, centroidPoint.y + point.y);
        }

        centroidPoint.set(centroidPoint.x / points.size(), centroidPoint.y / points.size());

        return centroidPoint;
    }

    public static Rectangle getBoundingBox(List<Point> points) {

        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);

            double x = point.x;
            double y = point.y;

            if (x < minX) {
                minX = x;
            }
            if (y < minY) {
                minY = y;
            }
            if (x > maxX) {
                maxX = x;
            }
            if (y > maxY) {
                maxY = y;
            }
        }

        Log.v("BBB", "minX: " + minX + ", maxX: " + maxX + ", minY: " + minY + ", maxY: " + maxY);

        return new Rectangle(minX, minY, maxX, maxY);
    }

//    public static List<Point> getBoundingBox(List<Point> endpoints) {
//
//        List<Point> boundary = new LinkedList<>();
//
//        double minX = Double.MAX_VALUE;
//        double maxX = -Double.MAX_VALUE;
//        double minY = Double.MAX_VALUE;
//        double maxY = -Double.MAX_VALUE;
//
//        for (int i = 0; i < endpoints.size(); i++) {
//            Point point = endpoints.get(i);
//
//            double x = point.x;
//            double y = point.y;
//
//            if (x < minX) {
//                minX = x;
//            }
//            if (y < minY) {
//                minY = y;
//            }
//            if (x > maxX) {
//                maxX = x;
//            }
//            if (y > maxY) {
//                maxY = y;
//            }
//        }
//
//        Log.v("BBB", "minX: " + minX + ", maxX: " + maxX + ", minY: " + minY + ", maxY: " + maxY);
//
//        boundary.add(new Point(minX, minY));
//        boundary.add(new Point(maxX, minY));
//        boundary.add(new Point(maxX, maxY));
//        boundary.add(new Point(minX, maxY));
//
//        return boundary;
//    }

    /**
     * Calculates and returns the center {@code Point} of the {@code Point}s in {@code endpoints}.
     *
     * @param points
     * @return
     */
    public static Point getCenterPoint(List<Point> points) {
        return getBoundingBox(points).getPosition();
    }

    public static List<Point> getRegularPolygon(Point position, double radius, int vertexCount) {

        List<Point> vertices = new ArrayList<>();

        int segmentCount = vertexCount - 1;

        // Calculate vertex Points on boundary of a circle
        for (int i = 0; i < segmentCount; i++) {
            Point vertex = new Point(
                    0 + radius * Math.cos(2.0f * Math.PI * (double) i / (double) segmentCount) + Math.toRadians(position.rotation),
                    0 + radius * Math.sin(2.0f * Math.PI * (double) i / (double) segmentCount) + Math.toRadians(position.rotation)
            );
            vertices.add(vertex);
        }

        return vertices;
    }
}
