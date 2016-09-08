package camp.computer.clay.scene.architecture;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.model.architecture.Construct;
import camp.computer.clay.scene.util.Visibility;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Rectangle;
import camp.computer.clay.scene.util.geometry.Shape;

/**
 * FigureGroup is an interface for managing and manipulating sets of figures.
 */
public class FigureGroup {

    private List<Figure> figures = new LinkedList<>();

    public FigureGroup() {
    }

    public void add(Figure figure) {
        this.figures.add(figure);
    }

    public void add(List<Figure> figures) {
        this.figures.addAll(figures);
    }

    public boolean contains(Figure figure) {
        return figures.contains(figure);
    }

    public FigureGroup remove(Figure figure) {
        figures.remove(figure);
        return this;
    }

    public Figure get(int index) {
        return figures.get(index);
    }

    public Figure getFirst() {
        if (figures.size() > 0) {
            return figures.get(0);
        }
        return null;
    }

    public Figure getLast() {
        if (figures.size() > 0) {
            return figures.get(figures.size() - 1);
        }
        return null;
    }

    /**
     * Removes all elements except those with the specified type.
     *
     * @param types
     * @return
     */
    public <T extends Construct> FigureGroup filterType(Class<?>... types) {

        FigureGroup figureGroup = new FigureGroup();

        for (int i = 0; i < this.figures.size(); i++) {
            for (int j = 0; j < types.length; j++) {
                Class<?> type = types[j];
                //for (Class<?> type : types) {
                //if (this.figures.getAction(i).getClass() == type) {
                if (this.figures.get(i).getConstruct().getClass() == type) {
                    figureGroup.add(this.figures.get(i));
                }
            }
        }

        return figureGroup;
    }

    /**
     * Filters figures to those that are within the specified distance from the specified point.
     *
     * @param point
     * @param distance
     * @return
     */
    public FigureGroup filterArea(Point point, double distance) {

        FigureGroup figureGroup = new FigureGroup();

        for (int i = 0; i < figures.size(); i++) {

            Figure figure = figures.get(i);

            double distanceToImage = Geometry.calculateDistance(
                    point,
                    figure.getPosition()
            );

            if (distanceToImage < distance) {
                figureGroup.add(figure);
            }

        }

        return figureGroup;

    }

    /**
     * Filters figures that fall within the area defined by {@code shape}.
     *
     * @param shape The {@code Shape} covering the area to filter.
     * @return The {@code FigureGroup} containing the area covered by {@code shape}.
     */
    public FigureGroup filterArea(Shape shape) {

        FigureGroup figureGroup = new FigureGroup();

        for (int i = 0; i < figures.size(); i++) {
            Figure figure = figures.get(i);
            if (shape.contains(figure.getPosition())) {
                figureGroup.add(figure);
            }
        }

        return figureGroup;
    }

    public FigureGroup filterVisibility(Visibility visibility) {

        FigureGroup figureGroup = new FigureGroup();

        for (int i = 0; i < figures.size(); i++) {
            Figure figure = figures.get(i);
            if (figure.getVisibility() == visibility) {
                figureGroup.add(figure);
            }

        }

        return figureGroup;
    }

    public List<Figure> getList() {
        return figures;
    }

    public List<Point> getPositions() {
        List<Point> positions = new LinkedList<>();
        for (int i = 0; i < figures.size(); i++) {
            Figure figure = figures.get(i);
            positions.add(new Point(figure.getPosition().getX(), figure.getPosition().getY()));
        }
        return positions;
    }

    public List<Point> getVertices() {
        List<Point> positions = new LinkedList<>();
        for (int i = 0; i < figures.size(); i++) {
            Figure figure = figures.get(i);
            positions.addAll(figure.getAbsoluteVertices());
        }
        return positions;
    }

    public Point getCenterPoint() {
        return Geometry.calculateCenterPosition(getPositions());
    }

    public Point getCentroidPoint() {
        return Geometry.calculateCentroidPosition(getPositions());
    }

    public Rectangle getBoundingBox() {
        return Geometry.calculateBoundingBox(getVertices());
    }

    public List<Point> getBoundingShape() {
        return Geometry.computeConvexHull(getPositions());
    }

    /**
     * Finds and returns the nearest <em>visible</em> <code>Figure</code>.
     *
     * @param position
     * @return
     */
    public Figure getNearest(Point position) {

        double shortestDistance = Float.MAX_VALUE;
        Figure nearestFigure = null;

        for (int i = 0; i < figures.size(); i++) {
            Figure figure = figures.get(i);

            double currentDistance = Geometry.calculateDistance(position, figure.getPosition());

            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                nearestFigure = figure;
            }
        }

        return nearestFigure;
    }

    public void setTransparency(double transparency) {
        for (int i = 0; i < figures.size(); i++) {
            Figure figure = figures.get(i);
            figure.setTransparency(transparency);
        }
    }

    public void setVisibility(Visibility visibility) {
        for (int i = 0; i < figures.size(); i++) {
            Figure figure = figures.get(i);
            figure.setVisibility(visibility);
        }
    }
}
