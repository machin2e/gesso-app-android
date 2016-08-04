package camp.computer.clay.viz.arch;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import camp.computer.clay.model.data.ImageSet;
import camp.computer.clay.model.sim.Model;
import camp.computer.clay.model.sim.Simulation;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.viz.util.Geometry;
import camp.computer.clay.viz.util.Number;
import camp.computer.clay.viz.util.Palette;
import camp.computer.clay.viz.util.Point;

public class Viz extends Image {

    private Palette palette = null;

    private List<Layer> layers = new ArrayList<>();

    public Viz(Simulation simulation) {
        super(simulation);
        setup();
    }

    private void setup() {
    }

    public Simulation getSimulation() {
        return (Simulation) getModel();
    }

    private boolean hasLayer(String tag) {
        for (Layer layer : layers) {
            if (layer.getTag().equals(tag)) {
                return true;
            }
        }
        return false;
    }

    public void addLayer(String tag) {
        if (!hasLayer(tag)) {
            Layer layer = new Layer(this);
            layer.setTag(tag);
            layers.add(layer);
        }
    }

    public Layer getLayer(String tag) {
        for (Layer layer : layers) {
            if (layer.getTag().equals(tag)) {
                return layer;
            }
        }
        return null;
    }

    public Layer getLayer(int index) {
        for (Layer layer : getLayers()) {
            if (layer.getIndex() == index) {
                return layer;
            }
        }
        return null;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public List<Integer> getLayerIndices() {
        List<Integer> layers = new ArrayList<>();
        for (Layer layer : getLayers()) {
            layers.add(layer.getIndex());
        }
        Collections.sort(layers);
        return layers;
    }

    public List<String> getLayerTags() {
        List<String> layerTags = new ArrayList<>();
        for (Layer layer : layers) {
            layerTags.add(layer.getTag());
        }
        return layerTags;
    }

    public void addImage(Image image, String layerName) {

        // Position
//        if (image.getModel().getClass() == Frame.class) {
//            generateImagePosition(image);
//        }

        // Layer
        if (!hasLayer(layerName)) {
            addLayer(layerName);
        }

        // Image
        getLayer(layerName).add(image);
        Log.v("Viz", "Adding image " + image);

//        getSimulation().getBody(0).getPerspective().focusReset();
    }

    public Model getModel(Image image) {
        for (Layer layer : getLayers()) {
            Model model = layer.getModel(image);
            if (model != null) {
                return model;
            }
        }
        return null;
    }

    public Image getImage(Model model) {
        for (Layer layer : getLayers()) {
            Image image = layer.getImage(model);
            if (image != null) {
                return image;
            }
        }
        return null;
    }

    public <T extends Model> ImageSet getImages(List<T> models) {
        ImageSet imageSet = new ImageSet();
        for (T model : models) {
            imageSet.add(getImage(model));
        }
        return imageSet;
    }

    /**
     * Returns the top image at the specified point.
     *
     * @param point
     * @return
     */
    public Image getImage(Point point) {
        for (Image image : getImages().filterVisibility(Visibility.VISIBLE).getList()) {
            if (image.isTouching(point)) {
                return image;
            }
        }
        return this;
    }

    public ImageSet getImages() {
        ImageSet imageSet = new ImageSet();
        for (Layer layer : getLayers()) {
            for (Image layerImage : layer.getImages()) {
                imageSet.add(layerImage);
            }
        }
        return imageSet;
    }

    public void generate() {

        getSimulation().getBody(0).getPerspective().update();

        for (Layer layer : getLayers()) {
            for (Image image : layer.getImages()) {
                image.generate();
            }
        }
    }

    @Override
    public void draw(Viz viz) {
    }

    @Override
    public boolean isTouching(Point point) {
        return false;
    }

    @Override
    public void onTouchInteraction(TouchInteraction touchInteraction) {

        Log.v("Touch", "Viz.onAction");

        getSimulation().getBody(0).getPerspective().focusReset();

    }

    public void setPalette(Palette palette) {
        this.palette = palette;
    }

    public Palette getPalette() {
        return palette;
    }

    private void generateImagePosition(Image image) {

        // Calculate random positions separated by minimum distance
        final float imageSeparationDistance = 500;

        //ArrayList<Point> imagePositions = getImages().old_filterType(old_FrameImage.TYPE).getPositions();
        List<Point> imagePositions = getImages().getPositions();

        Point position = null;
        boolean foundPoint = false;

        Log.v("Position", "imagePositions.size = " + imagePositions.size());

        if (imagePositions.size() == 0) {

            position = new Point(Number.generateRandomInteger(-300, 300), Number.generateRandomInteger(-300, 300));

        } else if (imagePositions.size() == 1) {

            position = Geometry.calculatePoint(
                    imagePositions.get(0),
                    Number.generateRandomInteger(0, 360),
                    imageSeparationDistance
            );

        } else {

            List<Point> hullPoints = Geometry.computeConvexHull(imagePositions);

            int sourceIndex = Number.generateRandomInteger(0, hullPoints.size() - 1);
            int targetIndex = sourceIndex + 1;

            Point midpoint = Geometry.calculateMidpoint(hullPoints.get(sourceIndex), hullPoints.get(targetIndex));
            position = Geometry.calculatePoint(
                    midpoint,
                    Geometry.calculateRotationAngle(hullPoints.get(sourceIndex), hullPoints.get(targetIndex)) + 90,
                    imageSeparationDistance
            );
        }

        // Assign the found position to the image
        image.setPosition(position);
        image.setRotation(Number.getRandomGenerator().nextInt(360));
    }

    public void drawTriangle(Point position, double angle, double width, double height) {
        if (palette != null) {
            palette.drawTriangle(position, angle, width, height);
        }
    }

    public void drawTriangle(Point position, Point a, Point b, Point c) {
        if (palette != null) {
            palette.drawTriangle(position, a, b, c);
        }
    }

    public void drawLine(Point source, Point target) {
        if (palette != null) {
            palette.drawLine(source, target);
        }
    }

    public void drawCircle(Point position, double radius, double angle) {
        if (palette != null) {
            palette.drawCircle(position, radius, angle);
        }
    }

    public void drawText(Point position, String text, double size) {
        if (palette != null) {
            palette.drawText(position, text, size);
        }
    }

    public void drawRectangle(Point position, double angle, double width, double height) {
        if (palette != null) {
            palette.drawRectangle(position, angle, width, height);
        }
    }

    public void drawRoundRectangle(Point position, double angle, double width, double height, double radius) {
        if (palette != null) {
            palette.drawRoundRectangle(position, angle, width, height, radius);
        }
    }

    public void drawTrianglePath(Point startPosition, Point stopPosition, double triangleWidth, double triangleHeight) {
        if (palette != null) {
            palette.drawTrianglePath(startPosition, stopPosition, triangleWidth, triangleHeight);
        }
    }

    public void drawRegularPolygon(Point position, double radius, int sideCount) {
        palette.drawRegularPolygon(position, radius, sideCount);
    }

    public void drawShape(List<Point> vertices) {
        palette.drawShape(vertices);
    }
}
