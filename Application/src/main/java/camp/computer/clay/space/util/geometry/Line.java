package camp.computer.clay.space.util.geometry;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.application.visual.Display;
import camp.computer.clay.model.architecture.Entity;
import camp.computer.clay.space.architecture.Shape;

public class Line<T extends Entity> extends Shape<T> {

    protected Point source = new Point(0, 0);
    protected Point target = new Point(0, 0);

    public Line()
    {
    }

    public Line(T entity)
    {
        this.entity = entity;
    }

    @Override
    public List<Point> getVertices()
    {
        List<Point> vertices = new ArrayList<>();
        vertices.add(getSource());
        vertices.add(getTarget());
        return vertices;
    }

    @Override
    public List<Line> getSegments()
    {
        ArrayList<Line> segments = new ArrayList<>();
        segments.add(new Line(getSource(), getTarget()));
        return segments;
    }

    @Override
    public void draw(Display display)
    {
        if (isVisible()) {
            Display.drawLine(this, display);
        }
    }

    public Line(Point source, Point target)
    {
        this.source = source;
        this.target = target;
    }

    public Point getSource()
    {
        return this.source;
    }

    public void setSource(Point source)
    {
        //this.source = source;
        this.source.set(source);
    }

    public Point getTarget()
    {
        return this.target;
    }

    public void setTarget(Point target)
    {
        this.target = target;
    }

    public double getLength()
    {
        return Geometry.calculateDistance(source, target);
    }
}
