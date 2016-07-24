package camp.computer.clay.viz.util;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.viz.arch.Shape;
import camp.computer.clay.viz.arch.Viz;

public class Polygon extends Shape {

    private List<Point> vertices = new ArrayList<>();

    public Polygon (Point position, List<Point> vertices) {
        super(position);
        this.vertices.addAll(vertices);
    }

    public Point getVertex (int index) {
        return vertices.get(index);
    }

    public void setVertices (List<Point> vertices) {
        this.vertices.clear();
        this.vertices.addAll(vertices);
    }

    @Override
    public List<Point> getVertices() {
        return vertices;
    }

    @Override
    public List<Line> getSegments() {
        ArrayList<Line> segments = new ArrayList<>();
        for (int i = 0; i < vertices.size() - 1; i++) {
            segments.add(new Line(vertices.get(i), vertices.get(i + 1)));
        }
        return segments;
    }

    @Override
    public void draw(Viz viz) {
        viz.drawShape(vertices);
    }
}
