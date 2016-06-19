package camp.computer.clay.sprite.util.physics;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Random;

public class Diagram {

    public static final float ATTRACTION_CONSTANT = 0.1f;		// spring constant
    public static final float REPULSION_CONSTANT = 10000;	// charge constant

    public static final float DEFAULT_DAMPING = 0.5f;
    public static final int DEFAULT_SPRING_LENGTH = 300;
    public static final int DEFAULT_MAX_ITERATIONS = 500;

    private ArrayList<Node> mNodes = new ArrayList<Node>();

    public ArrayList<Node> getNodes () {
        return this.mNodes;
    }

    public Diagram () {
    }

    public boolean addNode(Node node) {
        // if (node == null) throw new ArgumentNullException("node");

        if (!mNodes.contains(node)) {
            // add node, associate with diagram, then add all connected nodes
            mNodes.add(node);
            node.setDiagram(this);
            for (Node child : node.getConnections()) {
                addNode(child);
            }
            return true;
        }
        else {
            return false;
        }
    }

    public void Arrange() {
        Arrange(Diagram.DEFAULT_DAMPING, Diagram.DEFAULT_SPRING_LENGTH, Diagram.DEFAULT_MAX_ITERATIONS, true);
    }

    public void Arrange(boolean deterministic) {
        Arrange(Diagram.DEFAULT_DAMPING, Diagram.DEFAULT_SPRING_LENGTH, Diagram.DEFAULT_MAX_ITERATIONS, deterministic);
    }

    class NodeLayoutInfo {

        public Node Node;			// reference to the node in the simulation
        public VectorF Velocity;		// the node's current velocity, expressed in vector form
        public PointF NextPosition;	// the node's position after the next iteration

        /// <summary>
        /// Initialises a new instance of the Diagram.NodeLayoutInfo class, using the specified parameters.
        /// </summary>
        /// <param name="node"></param>
        /// <param name="velocity"></param>
        /// <param name="nextPosition"></param>
        public NodeLayoutInfo(Node node, VectorF velocity, PointF nextPosition) {
            Node = node;
            Velocity = velocity;
            NextPosition = nextPosition;
        }
    }

    public void Arrange(float damping, int springLength, int maxIterations, boolean deterministic) {
        // random starting positions can be made deterministic by seeding System.Random with a constant
        Random rnd = deterministic ? new Random(0) : new Random();

        // copy nodes into an array of metadata and randomise initial coordinates for each node
        NodeLayoutInfo[] layout = new NodeLayoutInfo[mNodes.size()];
        for (int i = 0; i < mNodes.size(); i++) {
            layout[i] = new NodeLayoutInfo(mNodes.get(i), new VectorF(0, 0), new PointF(0, 0));
            layout[i].Node.setPosition(new PointF(rnd.nextInt(100) - 50, rnd.nextInt(100) - 50));
        }

        int stopCount = 0;
        int iterations = 0;

        while (true) {
            double totalDisplacement = 0;

            for (int i=0; i<layout.length; i++) {
                NodeLayoutInfo current = layout[i];

                // express the node's current position as a vector, relative to the origin
                VectorF currentPosition = new VectorF(CalcDistance(new PointF(0, 0), current.Node.getPosition()), GetBearingAngle(new PointF(0, 0), current.Node.getPosition()));
                VectorF netForce = new VectorF(0, 0);

                // determine repulsion between nodes
                for (Node other : mNodes) {
                    if (other != current.Node) netForce = VectorF.add(netForce, CalcRepulsionForce(current.Node, other));
                }

                // determine attraction caused by connections
                for (Node child : current.Node.getConnections()) {
                    netForce = VectorF.add(netForce, CalcAttractionForce(current.Node, child, springLength));
                }
                for (Node parent : mNodes) {
                    if (parent.getConnections().contains(current.Node)) netForce = VectorF.add(netForce, CalcAttractionForce(current.Node, parent, springLength));
                }

                // apply net force to node velocity
                current.Velocity = VectorF.multiply(VectorF.add(current.Velocity, netForce), damping);

                // apply velocity to node position
                current.NextPosition = VectorF.add(currentPosition, current.Velocity).ToPoint();
            }

            // move nodes to resultant positions (and calculate total displacement)
            for (int i = 0; i < layout.length; i++) {
                NodeLayoutInfo current = layout[i];

                totalDisplacement += CalcDistance(current.Node.getPosition(), current.NextPosition);
                current.Node.getPosition().set(current.NextPosition.x, current.NextPosition.y);
            }

            iterations++;
            if (totalDisplacement < 10) stopCount++;
            if (stopCount > 15) break;
            if (iterations > maxIterations) break;
        }

        // center the diagram around the origin
        Rectangle logicalBounds = GetDiagramBounds();
        PointF midPoint = new PointF(logicalBounds.x + (logicalBounds.width / 2), logicalBounds.y + (logicalBounds.height / 2));

        for (Node node : mNodes) {
            //node.mLocation -= (Size) midPoint;
            node.getPosition().x = node.getPosition().x - midPoint.x;
            node.getPosition().y = node.getPosition().y - midPoint.y;
        }
    }

    private VectorF CalcAttractionForce(Node x, Node y, double springLength) {
        int proximity = Math.max(CalcDistance(x.getPosition(), y.getPosition()), 1);

        // Hooke's Law: F = -kx
        double force = ATTRACTION_CONSTANT * Math.max(proximity - springLength, 0);
        double angle = GetBearingAngle(x.getPosition(), y.getPosition());

        return new VectorF((float) force, (float) angle);
    }

    public static int CalcDistance(PointF a, PointF b) {
        double xDist = (a.x - b.x);
        double yDist = (a.y - b.y);
        return (int)Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
    }

    private VectorF CalcRepulsionForce(Node x, Node y) {
        int proximity = Math.max(CalcDistance(x.getPosition(), y.getPosition()), 1);

        // Coulomb's Law: F = k(Qq/r^2)
        double force = -(REPULSION_CONSTANT / Math.pow(proximity, 2));
        double angle = GetBearingAngle(x.getPosition(), y.getPosition());

        return new VectorF((float) force, (float) angle);
    }

//    public void Clear() {
//        mNodes.clear();
//    }

    public boolean ContainsNode(Node node) {
        return mNodes.contains(node);
    }

    private float GetBearingAngle(PointF start, PointF end) {
        PointF half = new PointF(start.x + ((end.x - start.x) / 2), start.y + ((end.y - start.y) / 2));

        double diffX = (double)(half.x - start.x);
        double diffY = (double)(half.y - start.y);

        if (diffX == 0) diffX = 0.001;
        if (diffY == 0) diffY = 0.001;

        double angle;
        if (Math.abs(diffX) > Math.abs(diffY)) {
            angle = Math.tanh(diffY / diffX) * (180.0 / Math.PI);
            if (((diffX < 0) && (diffY > 0)) || ((diffX < 0) && (diffY < 0))) angle += 180;
        }
        else {
            angle = Math.tanh(diffX / diffY) * (180.0 / Math.PI);
            if (((diffY < 0) && (diffX > 0)) || ((diffY < 0) && (diffX < 0))) angle += 180;
            angle = (180 - (angle + 90));
        }

        return (float) angle;
    }

    private Rectangle GetDiagramBounds() {
        float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
        float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;
        for (Node node : mNodes) {
            if (node.getPosition().x < minX) minX = node.getPosition().x;
            if (node.getPosition().x > maxX) maxX = node.getPosition().x;
            if (node.getPosition().y < minY) minY = node.getPosition().y;
            if (node.getPosition().x > maxY) maxY = node.getPosition().y;
        }

//        this.x = left;
//        this.y = top;
//        this.width = right - left;
//        this.height = bottom - top;



        return new Rectangle(minX, minY, maxX, maxY);
    }

    public boolean removeNode(Node node) {
        node.setDiagram(null);
        for (Node other : mNodes) {
            if ((other != node) && other.getConnections().contains(node)) other.disconnect(node);
        }
        return mNodes.remove(node);
    }







    private PointF ScalePoint(PointF point, double scale) {
        return new PointF((int)((double)point.x * scale), (int)((double)point.y * scale));
    }

    public void drawDiagram(Canvas mapCanvas, Paint paint, Rectangle bounds) {
        PointF center = new PointF(bounds.x + (bounds.width/ 2), bounds.y + (bounds.height / 2));

        // determine the scaling factor
        Rectangle logicalBounds = GetDiagramBounds();
        double scale = 1;
        if (logicalBounds.width > logicalBounds.height) {
            if (logicalBounds.width != 0) scale = (double)Math.min(bounds.width, bounds.height) / (double)logicalBounds.width;
        }
        else {
            if (logicalBounds.height != 0) scale = (double)Math.min(bounds.width, bounds.height) / (double)logicalBounds.height;
        }

        // draw all of the connectors first
        for (Node node : mNodes) {
            PointF source = ScalePoint(node.getPosition(), scale);

            // connectors
            for (Node other : node.getConnections()) {
//                PointF destination = ScalePoint(other.getPosition(), scale);
                DrawConnector(mapCanvas, paint, source, other.getPosition(), other);
            }
        }

        // then draw all of the nodes
        for (Node node : mNodes) {
//            PointF destination = ScalePoint(node.mLocation, scale);

            //Size nodeSize = node.Size;
            float nodeWidth = 8.0f;
            float nodeHeight = 8.0f;
            //Rectangle nodeBounds = new Rectangle(node.getPosition().x - (nodeWidth / 2), node.getPosition().y - (nodeHeight / 2), nodeWidth, nodeHeight);
            Rectangle nodeBounds = new Rectangle(node.getPosition().x - (nodeWidth / 2), node.getPosition().y - (nodeWidth / 2), nodeWidth, nodeHeight);
            DrawNode(mapCanvas, paint, nodeBounds);
        }

    }

    public void DrawConnector(Canvas mapCanvas, Paint paint, PointF from, PointF to, Node other) {
        mapCanvas.save();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3.0f);
        paint.setColor(Color.GREEN);
        mapCanvas.drawLine(from.x, from.y, to.x, to.y, paint);
        mapCanvas.restore();
    }

    public void DrawNode(Canvas mapCanvas, Paint paint, Rectangle bounds) {
        mapCanvas.save();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.GREEN);
        mapCanvas.drawOval(bounds.x, bounds.y, bounds.x + bounds.width, bounds.y + bounds.height, paint);
        mapCanvas.restore();

//        graphics.FillEllipse(mFill, bounds);
//        graphics.DrawEllipse(mStroke, bounds);
    }



}
