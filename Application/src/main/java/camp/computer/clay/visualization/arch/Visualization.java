package camp.computer.clay.visualization.arch;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.VisualizationSurface;
import camp.computer.clay.model.arch.Model;
import camp.computer.clay.model.arch.Simulation;
import camp.computer.clay.model.interactivity.Interaction;
import camp.computer.clay.visualization.img.FrameImage;
import camp.computer.clay.visualization.img.PortImage;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.Number;
import camp.computer.clay.visualization.util.Point;
import camp.computer.clay.visualization.util.Rectangle;
import camp.computer.clay.visualization.util.Shape;

public class Visualization extends Image {

//    private <T> List<T> getModel(Class<T> type) {
//        List<T> arrayList = new ArrayList<T>();
//        return arrayList;
//    }

    public static <T extends Image> List<Point> getPositions(List<T> images) {
        List<Point> positions = new ArrayList<>();
        for (T image : images) {
            positions.add(image.getPosition());
        }
        return positions;
    }

    private List<Layer> layers = new ArrayList<>();

    public Visualization(Simulation simulation) {
        super(simulation);
        setup();
    }

    private void setup() {
    }

    public boolean hasLayer(String tag) {
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).getTag().equals(tag)) {
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

    // TODO: Remove Image parameter. Create that and return it.
    public void addImage(Model model, Image image, String layerTag) {

        // Position image
        if (image.isType("frame")) {
            locateImagePosition(image);
        }

        // Add image
        if (!hasLayer(layerTag)) {
            addLayer(layerTag);
        }
        getLayer(layerTag).add(model, image);

        // Update perspective
//        getSimulation().getBody(0).getPerspective().adjustScale(0);
        // getSimulation().getBody(0).getPerspective().setPosition(getSimulation().getBody(0).getPerspective().getVisualization().getImages().filterType(FrameImage.TYPE).calculateCenter());
        getSimulation().getBody(0).getPerspective().adjustPosition();
    }

    public Layer getLayer(String tag) {
        for (int i = 0; i < layers.size(); i++) {
            if (layers.get(i).getTag().equals(tag)) {
                return layers.get(i);
            }
        }
        return null;
    }

    public Layer getLayer(int id) {
        for (Layer layer : getLayers()) {
            if (layer.getIndex() == id) {
                return layer;
            }
        }
        return null;
    }

    private void locateImagePosition(Image image) {

        // Calculate random positions separated by minimum distance
        final float imageSeparationDistance = 525; // 500;

        List<Point> imagePositions = getImages().filterType(FrameImage.TYPE).getPositions();

        Point position = null;
        boolean foundPoint = false;

        Log.v("Position", "imagePositions.size = " + imagePositions.size());

        if (imagePositions.size() == 0) {

            position = new Point(0, 0);

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

    public Image getImage(Model model) {
        for (Layer layer : getLayers()) {
            Image image = layer.getImage(model);
            if (image != null) {
                return image;
            }
        }
        return null;
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

    public List<FrameImage> getFrameImages() {

        List<FrameImage> images = new ArrayList<>();

        for (Layer layer : getLayers()) {
            for (Image image : layer.getImages()) {
                if (image instanceof FrameImage) {
                    images.add((FrameImage) image);
                }
            }
        }

        return images;
    }

    public List<PortImage> getPortImages() {

        List<PortImage> sprites = new ArrayList<>();

        for (Layer layer : getLayers()) {
            for (Image image : layer.getImages()) {
                if (image instanceof PortImage) {
                    sprites.add((PortImage) image);
                }
            }
        }

        return sprites;
    }

    public <T> List<Image> getImages(List<T> models) {
        List<Image> images = new ArrayList<>();
        for (Layer layer : getLayers()) {
            for (T model : models) {
                Image image = layer.getImage((Model) model);
                if (image != null) {
                    images.add(image);
                }
            }
        }
        return images;
    }

    public ImageGroup getImages() {
        ImageGroup imageGroup = new ImageGroup();
        for (Layer layer : getLayers()) {
            for (Image layerImage : layer.getImages()) {
                imageGroup.add(layerImage);
            }
        }
        return imageGroup;
    }

    /**
     * Finds and returns the nearest <em>visible</em> <code>Image</code>.
     *
     * @param position
     * @return
     */
    public Image getNearestImage(Point position) {

        float shortestDistance = Float.MAX_VALUE;
        Image nearestImage = null;

        for (Image image : getImages().getList()) {

            float currentDistance = (float) Geometry.calculateDistance(
                    position,
                    image.getPosition()
            );

            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                nearestImage = image;
            }
        }

        return nearestImage;
    }

    public FrameImage getNearestBaseImage(Point position) {

        float shortestDistance = Float.MAX_VALUE;
        FrameImage nearestFrameImage = null;

        for (FrameImage frameImage : getFrameImages()) {

            // Update style of nearby machines
            float currentDistance = (float) Geometry.calculateDistance(
                    position,
                    frameImage.getPosition()
            );

            if (currentDistance < shortestDistance) {

                shortestDistance = currentDistance;
                nearestFrameImage = frameImage;

            }
        }

        return nearestFrameImage;
    }

    public PortImage getNearestPortImage(Point position) {

        float shortestDistance = Float.MAX_VALUE;
        PortImage nearestImage = null;

        for (PortImage image : getPortImages()) {

            // Update style of nearby machines
            float currentDistance = (float) Geometry.calculateDistance(
                    position,
                    image.getPosition()
            );

            if (currentDistance < shortestDistance) {
                shortestDistance = currentDistance;
                nearestImage = image;
            }
        }

        return nearestImage;
    }

    public Image getImageByPosition(Point point) {
        for (Image image : getImages().filterVisibility(true).getList()) {
            if (image.isTouching(point)) {
                return image;
            }
        }
        return null;
    }

    public Simulation getSimulation() {
        return (Simulation) getModel();
    }

    public void update() {

        getSimulation().getBody(0).getPerspective().update();

        for (Layer layer : getLayers()) {
            for (Image image : layer.getImages()) {
                image.update();
            }
        }
    }

    @Override
    public void draw(VisualizationSurface visualizationSurface) {

        if (Application.ENABLE_GEOMETRY_ANNOTATIONS) {
            // <AXES_ANNOTATION>
            visualizationSurface.getPaint().setColor(Color.CYAN);
            visualizationSurface.getPaint().setStrokeWidth(1.0f);
            visualizationSurface.getCanvas().drawLine(-1000, 0, 1000, 0, visualizationSurface.getPaint());
            visualizationSurface.getCanvas().drawLine(0, -1000, 0, 1000, visualizationSurface.getPaint());
            // </AXES_ANNOTATION>
        }

        // Draw images
        for (Integer index : getLayerIndices()) {
            Layer layer = getLayer(index);
            if (layer != null) {
                for (Image image : layer.getImages()) {
                    image.draw(visualizationSurface);
                }
            }
        }

        // Geometry.computeCirclePacking(getFrameImages(), 200, getImages().filterType(FrameImage.TYPE).calculateCentroid());

        // Draw annotations
        if (Application.ENABLE_GEOMETRY_ANNOTATIONS) {

            // <FPS_ANNOTATION>
            Point fpsPosition = getImages().filterType(FrameImage.TYPE).calculateCenter();
            fpsPosition.setY(fpsPosition.getY() - 200);
            visualizationSurface.getPaint().setColor(Color.RED);
            visualizationSurface.getPaint().setStyle(Paint.Style.FILL);
            visualizationSurface.getCanvas().drawCircle((float) fpsPosition.getX(), (float) fpsPosition.getY(), 10, visualizationSurface.getPaint());

            visualizationSurface.getPaint().setStyle(Paint.Style.FILL);
            visualizationSurface.getPaint().setTextSize(35);

            String fpsText = "FPS: " + (int) visualizationSurface.getRenderer().getFramesPerSecond();
            Rect fpsTextBounds = new Rect();
            visualizationSurface.getPaint().getTextBounds(fpsText, 0, fpsText.length(), fpsTextBounds);
            visualizationSurface.getCanvas().drawText(fpsText, (float) fpsPosition.getX() + 20, (float) fpsPosition.getY() + fpsTextBounds.height() / 2.0f, visualizationSurface.getPaint());
            // </FPS_ANNOTATION>

            // <CENTROID_ANNOTATION>
            Point centroidPosition = getImages().filterType(FrameImage.TYPE).calculateCentroid();
            visualizationSurface.getPaint().setColor(Color.RED);
            visualizationSurface.getPaint().setStyle(Paint.Style.FILL);
            visualizationSurface.getCanvas().drawCircle((float) centroidPosition.getX(), (float) centroidPosition.getY(), 10, visualizationSurface.getPaint());

            visualizationSurface.getPaint().setStyle(Paint.Style.FILL);
            visualizationSurface.getPaint().setTextSize(35);

            String text = "CENTROID";
            Rect bounds = new Rect();
            visualizationSurface.getPaint().getTextBounds(text, 0, text.length(), bounds);
            visualizationSurface.getCanvas().drawText(text, (float) centroidPosition.getX() + 20, (float) centroidPosition.getY() + bounds.height() / 2.0f, visualizationSurface.getPaint());
            // </CENTROID_ANNOTATION>

            // <CENTROID_ANNOTATION>
            List<Point> formImagePositions = getImages().filterType(FrameImage.TYPE).getPositions();
            Point formImagesCenterPosition = Geometry.calculateCenterPosition(formImagePositions);
            visualizationSurface.getPaint().setColor(Color.RED);
            visualizationSurface.getPaint().setStyle(Paint.Style.FILL);
            visualizationSurface.getCanvas().drawCircle((float) formImagesCenterPosition.getX(), (float) formImagesCenterPosition.getY(), 10, visualizationSurface.getPaint());

            visualizationSurface.getPaint().setStyle(Paint.Style.FILL);
            visualizationSurface.getPaint().setTextSize(35);

            String centerLabeltext = "CENTER";
            Rect centerLabelTextBounds = new Rect();
            visualizationSurface.getPaint().getTextBounds(centerLabeltext, 0, centerLabeltext.length(), centerLabelTextBounds);
            visualizationSurface.getCanvas().drawText(centerLabeltext, (float) formImagesCenterPosition.getX() + 20, (float) formImagesCenterPosition.getY() + centerLabelTextBounds.height() / 2.0f, visualizationSurface.getPaint());
            // </CENTROID_ANNOTATION>

            // <CONVEX_HULL>
            List<Point> formPositions = Visualization.getPositions(getFrameImages());
            List<Point> convexHullVertices = Geometry.computeConvexHull(formPositions);

            visualizationSurface.getPaint().setStrokeWidth(1.0f);
            visualizationSurface.getPaint().setColor(Color.RED);
            visualizationSurface.getPaint().setStyle(Paint.Style.STROKE);

            for (int i = 0; i < convexHullVertices.size() - 1; i++) {
                Shape.drawPolygon(convexHullVertices, visualizationSurface.getCanvas(), visualizationSurface.getPaint());
            }
            // </CONVEX_HULL>

            // <BOUNDING_BOX>
            visualizationSurface.getPaint().setStrokeWidth(1.0f);
            visualizationSurface.getPaint().setColor(Color.RED);
            visualizationSurface.getPaint().setStyle(Paint.Style.STROKE);

            Rectangle boundingBox = getImages().filterType(FrameImage.TYPE).calculateBoundingBox();
            Shape.drawPolygon(boundingBox.getVertices(), visualizationSurface.getCanvas(), visualizationSurface.getPaint());
            // </BOUNDING_BOX>
        }
    }

    public List<Integer> getLayerIndices() {
        List<Integer> layers = new ArrayList<>();
        for (Layer layer : getLayers()) {
            layers.add(layer.getIndex());
        }
        Collections.sort(layers);
        return layers;
    }

    public List<Layer> getLayers() {
        return new ArrayList<>(this.layers);
    }

    @Override
    public boolean isTouching(Point point) {
        return false;
    }

    @Override
    public boolean isTouching(Point point, double padding) {
        return false;
    }

    @Override
    public void onTouchInteraction(Interaction interaction) {
    }
}
