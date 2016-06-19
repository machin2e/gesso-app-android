package camp.computer.clay.sprite.util.physics;

import android.graphics.PointF;

import java.util.ArrayList;

public class Node {

    private Diagram diagram;			// the parent diagram
    private PointF position;			// node position, relative to the origin
    private ArrayList<Node> connections;	// list of references to connected nodes (children

    public Node(PointF position) {
        this.position = position;
        this.diagram = null;
        this.connections = new ArrayList<Node>();
    }

    public PointF getPosition() {
        return this.position;
    }

    public void setPosition(PointF point) {
        this.position.x = point.x;
        this.position.y = point.y;
    }

    public ArrayList<Node> getConnections() {
        return this.connections;
    }

    public Diagram getDiagram() {
        return this.diagram;
    }

    public void setDiagram (Diagram diagram) {
        if (this.diagram == diagram) {
            return;
        }

        if (this.diagram != null) {
            this.diagram.removeNode(this);
        }

        this.diagram = diagram;

        if (this.diagram != null) {
            this.diagram.addNode(this);
        }
    }

    public boolean addChild(Node child) {
        //if (child == null) throw new ArgumentNullException("child");
        if ((child != this) && !this.connections.contains(child)) {
            child.setDiagram(this.getDiagram());
            this.connections.add(child);
            return true;
        } else {
            return false;
        }
    }

    public boolean addParent(Node parent) {
        // if (parent == null) throw new ArgumentNullException("parent");
        return parent.addChild(this);
    }

    public boolean disconnect(Node other) {
        boolean c = this.connections.remove(other);
        boolean p = other.connections.remove(this);
        return c || p;
    }
}
