package camp.computer.clay.util.geometry;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.util.image.Image;
import camp.computer.clay.util.image.Space;

public abstract class Geometry {

    public static double calculateRotationAngle(Point source, Point target) {
        return Geometry.calculateRotationAngle(source.getX(), source.getY(), target.getX(), target.getY());
    }

    /**
     * Calculates the rotation angle in degrees from {@code source} to {@code target}.
     * <p>
     * Returns angle in degrees in the range [0,360), rotating CLOCKWISE, 0 and 360 degrees
     * represents NORTH, 90 degrees represents EAST, etc...
     * <p>
     * Assumes all pointerCoordinates are in the same coordinate space.  If they are not,
     * you will need to call SwingUtilities.convertPointToScreen or equivalent
     * on all arguments before passing them  to this function.
     *
     * @param x1 Point we are rotating around.
     * @param y1
     * @param x2 Point to which we want to calculate the rotation, relative to the center point.
     * @param y2
     * @return rotation in degrees.  This is the rotation from centerPt to targetPt.
     */
    public static double calculateRotationAngle(double x1, double y1, double x2, double y2) {

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
    public static Point calculateRotatedPoint(Point center, double angle, Point point) {
        return Geometry.calculatePoint(center, angle + Geometry.calculateRotationAngle(center, point), calculateDistance(center, point));
    }

    public static Point calculateRotatedPoint(double x1, double y1, double angle, double x2, double y2) {
        return Geometry.calculatePoint(x1, y1, angle + Geometry.calculateRotationAngle(x1, y1, x2, y2), calculateDistance(x1, y1, x2, y2));
    }

    public static Point calculatePoint(Point originPoint, double rotation, double distance) {
        Point point = new Point();
        point.setX(originPoint.getX() + distance * Math.cos(Math.toRadians(rotation)));
        point.setY(originPoint.getY() + distance * Math.sin(Math.toRadians(rotation)));
        return point;
    }

    public static Point calculatePoint(double x, double y, double rotation, double distance) {
        Point point = new Point();
        point.setX(x + distance * Math.cos(Math.toRadians(rotation)));
        point.setY(y + distance * Math.sin(Math.toRadians(rotation)));
        return point;
    }

    public static Point calculateMidpoint(Line line) {
        return calculateMidpoint(line.getSource(), line.getTarget());
    }

    public static Point calculateMidpoint(Point source, Point target) {
        Point midpoint = new Point(
                (source.getRelativeX() + target.getRelativeX()) / 2.0f,
                (source.getRelativeY() + target.getRelativeY()) / 2.0f,
                source.getReferencePoint()
        );
        return midpoint;
    }

    //Compute the dot product AB . AC
    private static double calculateDotProduct(Point linePointA, Point linePointB, Point pointC) {
        Point AB = new Point();
        Point BC = new Point();
        AB.setX(linePointB.getX() - linePointA.getX());
        AB.setY(linePointB.getY() - linePointA.getY());
        BC.setX(pointC.getX() - linePointB.getX());
        BC.setY(pointC.getY() - linePointB.getY());
        double dot = AB.getX() * BC.getX() + AB.getY() * BC.getY();
        return dot;
    }

    //Compute the cross product AB relativeX AC
    private static double calculateCrossProduct(Point linePointA, Point linePointB, Point pointC) {
        Point AB = new Point();
        Point AC = new Point();
        AB.setX(linePointB.getX() - linePointA.getX());
        AB.setY(linePointB.getY() - linePointA.getY());
        AC.setX(pointC.getX() - linePointA.getX());
        AC.setY(pointC.getY() - linePointA.getY());
        double cross = AB.getX() * AC.getY() - AB.getY() * AC.getX();
        return cross;
    }

    /**
     * Calculates the distance between the point {@code point} and the line or segment through
     * {@code linePointA} and {@code linePointB}.
     *
     * @param linePointA
     * @param linePointB
     * @param point
     * @param isSegment
     * @return
     */
    //
    //if isSegment is true, AB is a segment, not a line.
    // References:
    // - http://stackoverflow.com/questions/4438244/how-to-calculate-shortest-2d-distance-between-a-point-and-a-line-segment-in-all
    public static double calculateLineToPointDistance(Point linePointA, Point linePointB, Point point, boolean isSegment) {
        double distance = calculateCrossProduct(linePointA, linePointB, point) / calculateDistance(linePointA, linePointB);
        if (isSegment) {
            double dot1 = calculateDotProduct(linePointA, linePointB, point);
            if (dot1 > 0) {
                return calculateDistance(linePointB, point);
            }

            double dot2 = calculateDotProduct(linePointB, linePointA, point);
            if (dot2 > 0) {
                return calculateDistance(linePointA, point);
            }
        }
        return Math.abs(distance);
    }

    public static Point calculateCentroid(List<Point> points) {

        Point centroidPosition = new Point(0, 0);

        for (int i = 0; i < points.size(); i++) {

            Point point = points.get(i);

            centroidPosition.set(
                    centroidPosition.getX() + point.getX(),
                    centroidPosition.getY() + point.getY()
            );
        }

        centroidPosition.set(
                centroidPosition.getX() / points.size(),
                centroidPosition.getY() / points.size()
        );

        return centroidPosition;
    }

    // TODO: Cache the result on a per-shape basis... remove per-step Rectangle allocation
    public static Rectangle calculateBoundingBox(List<Point> points) {

        double minX = Double.MAX_VALUE;
        double maxX = -Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = -Double.MAX_VALUE;

        for (int i = 0; i < points.size(); i++) {
            Point point = points.get(i);

            if (point.getX() < minX) {
                minX = point.getX();
            }
            if (point.getY() < minY) {
                minY = point.getY();
            }
            if (point.getX() > maxX) {
                maxX = point.getX();
            }
            if (point.getY() > maxY) {
                maxY = point.getY();
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
    public static Point calculateCenter(List<Point> points) {
        return calculateBoundingBox(points).getPosition();
    }

    /**
     * Returns the {@code Point} in {@code points} nearest to {@code point}.
     *
     * @param point
     * @param points
     * @return
     */
    public static Point calculateNearestPoint(Point point, List<Point> points) {

        // Initialize point
        Point nearestPoint = points.get(0);
        double nearestDistance = calculateDistance(point, nearestPoint);

        // Search for the nearest point
        for (int i = 0; i < points.size(); i++) {
            double distance = calculateDistance(points.get(i), point);
            if (distance < nearestDistance) {
                nearestPoint.set(point);
            }
        }

        return nearestPoint;
    }

    /**
     * Computes the convex hull using the "quick hull" algorithm.
     * <p>
     * <strong>References</strong>
     * - Another implementation is <em>GrahamScan</em> (http://algs4.cs.princeton.edu/99hull/GrahamScan.java.html).
     *
     * @param points
     * @return
     */
    public static List<Point> computeConvexHull(List<Point> points) {

        List<Point> convexHull = new ArrayList<>();

        if (points.size() < 3) {
            //return (ArrayList) pointerCoordinates.clone();
            for (int i = 0; i < points.size(); i++) {
                Point pointCopy = new Point(points.get(i));
                convexHull.add(pointCopy);
            }
        }

        int minPoint = -1;
        int maxPoint = -1;

        double minX = Integer.MAX_VALUE;
        double maxX = Integer.MIN_VALUE;

        for (int i = 0; i < points.size(); i++) {

            if (points.get(i).getX() < minX) {
                minX = points.get(i).getX();
                minPoint = i;
            }

            if (points.get(i).getX() > maxX) {
                maxX = points.get(i).getX();
                maxPoint = i;
            }
        }

        Point A = points.get(minPoint);
        Point B = points.get(maxPoint);

        convexHull.add(A);
        convexHull.add(B);

        points.remove(A);
        points.remove(B);

        List<Point> leftSet = new ArrayList<>();
        List<Point> rightSet = new ArrayList<>();

        for (int i = 0; i < points.size(); i++) {
            Point p = points.get(i);
            if (pointLocation(A, B, p) == -1) {
                leftSet.add(p);
            } else if (pointLocation(A, B, p) == 1) {
                rightSet.add(p);
            }
        }

        hullSet(A, B, rightSet, convexHull);
        hullSet(B, A, leftSet, convexHull);

        return convexHull;
    }

    private static void hullSet(Point A, Point B, List<Point> set, List<Point> hull) {
        int insertPosition = hull.indexOf(B);

        if (set.size() == 0) {
            return;
        }

        if (set.size() == 1) {
            Point p = set.get(0);
            set.remove(p);
            hull.add(insertPosition, p);
            return;
        }

        double dist = Integer.MIN_VALUE;
        int furthestPoint = -1;

        for (int i = 0; i < set.size(); i++) {
            Point p = set.get(i);
            double distance = distance(A, B, p);
            if (distance > dist) {
                dist = distance;
                furthestPoint = i;
            }
        }

        Point P = set.get(furthestPoint);
        set.remove(furthestPoint);
        hull.add(insertPosition, P);

        // Determine who's to the left of AP
        List<Point> leftSetAP = new ArrayList<>();
        for (int i = 0; i < set.size(); i++) {
            Point M = set.get(i);
            if (pointLocation(A, P, M) == 1) {
                leftSetAP.add(M);
            }
        }

        // Determine who's to the left of PB
        List<Point> leftSetPB = new ArrayList<>();
        for (int i = 0; i < set.size(); i++) {
            Point M = set.get(i);
            if (pointLocation(P, B, M) == 1) {
                leftSetPB.add(M);
            }
        }

        hullSet(A, P, leftSetAP, hull);
        hullSet(P, B, leftSetPB, hull);

    }

    private static double distance(Point A, Point B, Point C) {
        double ABx = B.getX() - A.getX();
        double ABy = B.getY() - A.getY();
        double num = ABx * (A.getY() - C.getY()) - ABy * (A.getX() - C.getX());
        if (num < 0) {
            num = -num;
        }
        return num;
    }

    private static int pointLocation(Point A, Point B, Point P) {
        double cp1 = (B.getX() - A.getX()) * (P.getY() - A.getY()) - (B.getY() - A.getY()) * (P.getX() - A.getX());
        if (cp1 > 0) {
            return 1;
        } else if (cp1 == 0) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * Compute list of pointerCoordinates that are separated by a minimal distance. Based on circle packing
     * algorithm.
     * <p>
     * References:
     * - http://graphicdna.blogspot.com/2009/09/2d-circle-packing-algorithm-ported-to-c.html
     * - http://gamedevelopment.tutsplus.com/tutorials/when-worlds-collide-simulating-circle-circle-collisions--gamedev-769
     * - http://mathematica.stackexchange.com/questions/2594/efficient-way-to-generate-random-points-with-a-predefined-lower-bound-on-their-p
     * - http://stackoverflow.com/questions/4847269/circle-separation-distance-nearest-neighbor-problem?lq=1
     * - http://stackoverflow.com/questions/3265986/an-algorithm-to-space-out-overlapping-rectangles
     *
     * @param positions
     * @return
     */
    public static <T extends Image> List<T> computeCirclePacking(List<T> positions, double distance, Point packingCenter) {

        // Sort pointerCoordinates based on distance from center
        List<T> sortedImages = sortByDistanceToPoint(positions, packingCenter);
        List<Point> sortedPositions = Space.getPositions(sortedImages);

        double minSeparationSq = distance * distance;

        double iterationCounter = 1000;

        for (int i = 0; i < sortedPositions.size() - 1; i++) {
            for (int j = i + 1; j < sortedPositions.size(); j++) {

                if (i == j) {
                    continue;
                }

                // Vector/Segment connecting a pair of pointerCoordinates
                // TODO: Vector2 AB = mCircles[j].mCenter - mCircles[i].mCenter;
                Point vectorAB = new Point(
                        sortedPositions.get(j).getX() - sortedPositions.get(i).getX(),
                        sortedPositions.get(j).getY() - sortedPositions.get(i).getY()
                );

                double r = (sortedImages.get(i).getBoundingBox().getWidth() / 2.0f) + (sortedImages.get(i).getBoundingBox().getWidth() / 2.0f);

                // Length squared = (dx * dx) + (dy * dy);
                double vectorABLength = calculateDistance(sortedPositions.get(i), sortedPositions.get(j));
                double d = vectorABLength * vectorABLength - minSeparationSq;
                double minSepSq = Math.min(d, minSeparationSq);
                d -= minSepSq;

                if (d < (r * r) - 0.01)
//                if (d < (r * r) - 500)
                {
//                    Log.v("Sort", "r^2 - d = " + ((r * r) - d));
//                    Log.v("Sort", "--");
                    // Normalize (transform into unit vector)
                    // TODO: AB.Normalize();
                    double magnitude = (double) calculateDistance(
                            sortedPositions.get(i),
                            sortedPositions.get(j)
                    );
                    // (double) Geometry.calculateDistance(packingCenter, vectorAB);
                    vectorAB.setX(vectorAB.getX() / magnitude);
                    vectorAB.setY(vectorAB.getY() / magnitude);

                    // TODO: AB *= (double)((r - Math.Sqrt(d)) * 0.5f);
                    vectorAB.setX(vectorAB.getX() * (double) ((r - Math.sqrt(d)) * 0.5f));
                    vectorAB.setY(vectorAB.getY() * (double) ((r - Math.sqrt(d)) * 0.5f));

//                    if (positions.getEvent(j) != mDraggingCircle)
                    // TODO: positions.getEvent(j).mCenter += AB;
                    sortedPositions.get(j).setX(sortedPositions.get(j).getX() + vectorAB.getX());
                    sortedPositions.get(j).setY(sortedPositions.get(j).getY() + vectorAB.getY());
//                    if (positions.getEvent(i) != mDraggingCircle)
                    // TODO: positions.getEvent(i).mCenter -= AB;
                    sortedPositions.get(i).setX(sortedPositions.get(i).getX() - vectorAB.getX());
                    sortedPositions.get(i).setY(sortedPositions.get(i).getY() - vectorAB.getY());
                }

            }
        }

        double damping = 0.1f / iterationCounter;
        for (int i = 0; i < sortedPositions.size(); i++) {
//            if (mCircles[i] != mDraggingCircle)
//            {
            // TODO: Vector2 v = mCircles[i].mCenter - this.mPackingCenter;
            Point v = new Point(
                    sortedPositions.get(i).getX() - packingCenter.getX(),
                    sortedPositions.get(i).getY() - packingCenter.getY()
            );

            // TODO: v *= damping;
            v.setX(v.getX() * damping);
            v.setY(v.getY() * damping);

            // TODO: mCircles[i].mCenter -= v;
            sortedPositions.get(i).setX(sortedPositions.get(i).getX() - v.getX());
            sortedPositions.get(i).setY(sortedPositions.get(i).getY() - v.getY());

            sortedImages.get(i).setPosition(sortedPositions.get(i));
//            }
        }

        return sortedImages;

    }

    public static <T extends Image> List<T> sortByDistanceToPoint(List<T> positions, Point point) {

        // Initialize with unsorted list of pointerCoordinates
        List<T> sortedList = new ArrayList(positions);

        for (int i = 0; i < sortedList.size(); i++) {
            for (int j = 1; j < (sortedList.size() - i); j++) {

                T p1 = sortedList.get(j - 1);
                T p2 = sortedList.get(j);

                if (calculateDistance(p1.getPosition(), point) > calculateDistance(p2.getPosition(), point)) {
                    sortedList.remove(j - 1);
                    sortedList.add(j, p1);
                }

            }
        }

        return sortedList;

    }

    /**
     * General-purpose function that returns true if the given point is contained inside the shape
     * defined by the boundary pointerCoordinates.
     *
     * @param vertices The vertices defining the boundary polygon
     * @param point    The point to check
     * @return true If the point is inside the boundary, false otherwise
     * @see <a href="http://www.ecse.rpi.edu/Homepages/wrf/Research/Short_Notes/pnpoly.html">PNPOLY - Point Inclusion in Polygon Test (W. Randolph Franklin)</a>
     */
    public static boolean containsPoint(List<Point> vertices, Point point) {

        // Setup
        double minX = vertices.get(0).getX();
        double maxX = vertices.get(0).getX();
        double minY = vertices.get(0).getY();
        double maxY = vertices.get(0).getY();

        for (int i = 1; i < vertices.size(); i++) {
            Point q = vertices.get(i);
            minX = Math.min(q.getX(), minX);
            maxX = Math.max(q.getX(), maxX);
            minY = Math.min(q.getY(), minY);
            maxY = Math.max(q.getY(), maxY);
        }

        if (point.getX() < minX || point.getX() > maxX || point.getY() < minY || point.getY() > maxY) {
            return false;
        }

        // Procedure
        boolean isContained = false;
        for (int i = 0, j = vertices.size() - 1; i < vertices.size(); j = i++) {
            if ((vertices.get(i).getY() > point.getY()) != (vertices.get(j).getY() > point.getY()) &&
                    point.getX() < (vertices.get(j).getX() - vertices.get(i).getX()) * (point.getY() - vertices.get(i).getY()) / (vertices.get(j).getY() - vertices.get(i).getY()) + vertices.get(i).getX()) {
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
                    (0 + radius * (Math.cos(2.0f * Math.PI * (double) i / (double) segmentCount)) + Math.toRadians(position.getRelativeRotation())),
                    (0 + radius * (Math.sin(2.0f * Math.PI * (double) i / (double) segmentCount)) + Math.toRadians(position.getRelativeRotation())),
                    position
            );

            vertices.add(vertexPosition);
        }

        return vertices;
    }

    public static List<Point> getArc(Point centerPosition, double radius, double startAngle, double stopAngle, int segmentCount) {

        Log.v("Geometry", "getArc");

        List<Point> vertices = new ArrayList<>();

        double angleIncrement = (stopAngle - startAngle) / segmentCount;

        for (int i = 0; i < segmentCount; i++) {

            Point vertexPosition = new Point(
                    radius * Math.cos(Math.toRadians(startAngle + i * angleIncrement)), // + Math.toRadians(centerPosition.getRelativeAngle()),
                    radius * Math.sin(Math.toRadians(startAngle + i * angleIncrement)), // + Math.toRadians(centerPosition.getRelativeAngle()),
                    centerPosition
            );

            vertices.add(vertexPosition);
        }

        return vertices;
    }

    public static double calculateDistance(Point source, Point target) {
        return calculateDistance(source.getX(), source.getY(), target.getX(), target.getY());
    }

    public static double calculateDistance(double x1, double y1, double x2, double y2) {
        double distanceSquare = Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2);
        double distance = Math.sqrt(distanceSquare);
        return distance;
    }
}
