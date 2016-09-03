package camp.computer.clay.scene.architecture;

import java.util.LinkedList;
import java.util.List;

import camp.computer.clay.model.architecture.Construct;
import camp.computer.clay.scene.util.Visibility;
import camp.computer.clay.scene.util.geometry.Geometry;
import camp.computer.clay.scene.util.geometry.Point;
import camp.computer.clay.scene.util.geometry.Rectangle;

/**
 * FigureSet is an interface for managing and manipulating sets of figures.
 */
public class FigureSet {

    private List<Figure> figures = new LinkedList<>();

    public FigureSet() {
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

    public FigureSet remove(Figure figure) {
        figures.remove(figure);
        return this;
    }

    public Figure get(int index) {
        return figures.get(index);
    }

    /**
     * Removes all elements except those with the specified type.
     *
     * @param types
     * @return
     */
    public <T extends Construct> FigureSet filterType(Class<?>... types) {

        FigureSet figureSet = new FigureSet();

        for (int i = 0; i < this.figures.size(); i++) {
            for (int j = 0; j < types.length; j++) {
                Class<?> type = types[j];
                //for (Class<?> type : types) {
                //if (this.figures.get(i).getClass() == type) {
                if (this.figures.get(i).getConstruct().getClass() == type) {
                    figureSet.add(this.figures.get(i));
                }
            }
        }

        return figureSet;
    }

    /**
     * Filters figures to those that are within the specified distance from the specified point.
     *
     * @param point
     * @param distance
     * @return
     */
    public FigureSet filterProximity(Point point, double distance) {

        FigureSet figureSet = new FigureSet();

        for (int i = 0; i < figures.size(); i++) {

            Figure figure = figures.get(i);

            double distanceToImage = Geometry.calculateDistance(
                    point,
                    figure.getPosition()
            );

            if (distanceToImage < distance) {
                figureSet.add(figure);
            }

        }

        return figureSet;

    }

    public FigureSet filterVisibility(Visibility visibility) {

        FigureSet figureSet = new FigureSet();
        for (int i = 0; i < figures.size(); i++) {

            Figure figure = figures.get(i);

            if (figure.getVisibility() == visibility) {
                figureSet.add(figure);
            }

        }

        return figureSet;
    }

    public List<Figure> getList() {
        return figures;
    }

    public List<Point> getPositions() {
        List<Point> positions = new LinkedList<>();
        for (Figure figure : figures) {
            positions.add(new Point(figure.getPosition().getX(), figure.getPosition().getY()));
        }
        return positions;
    }

    public List<Point> getVertices() {
        List<Point> positions = new LinkedList<>();
        for (Figure figure : figures) {
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

    public List<Point> getBoundingPolygon() {
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

        for (Figure figure : figures) {

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
