package camp.computer.clay.visualization.architecture;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.Surface;
import camp.computer.clay.model.architecture.Model;
import camp.computer.clay.model.architecture.Simulation;
import camp.computer.clay.model.interactivity.Action;
import camp.computer.clay.visualization.image.FrameImage;
import camp.computer.clay.visualization.image.PatchImage;
import camp.computer.clay.visualization.image.PortImage;
import camp.computer.clay.visualization.util.Visibility;
import camp.computer.clay.visualization.util.Probability;
import camp.computer.clay.visualization.util.geometry.Geometry;
import camp.computer.clay.visualization.util.geometry.Point;
import camp.computer.clay.visualization.util.geometry.Rectangle;
import camp.computer.clay.visualization.util.geometry.Shape;

public class Visualization extends Image {

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
        if (image instanceof FrameImage) {
            locateImagePosition(image);
        }

        // Add image
        if (!hasLayer(layerTag)) {
            addLayer(layerTag);
        }
        getLayer(layerTag).add(model, image);

        // Update perspective
//        getSimulation().getBody(0).getPerspective().adjustScale(0);
        // getSimulation().getBody(0).getPerspective().setPosition(getSimulation().getBody(0).getPerspective().getVisualization().getImages().filterType(FrameImage.TYPE).getCenterPoint());
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

        List<Point> imagePositions = getImages().filterType(FrameImage.class).getPositions();

        Point position = null;
        boolean foundPoint = false;

        Log.v("Position", "imagePositions.size = " + imagePositions.size());

        if (imagePositions.size() == 0) {

            position = new Point(0, 0);

        } else if (imagePositions.size() == 1) {

            position = Geometry.calculatePoint(
                    imagePositions.get(0),
                    Probability.generateRandomInteger(0, 360),
                    imageSeparationDistance
            );

        } else {

            List<Point> hullPoints = Geometry.computeConvexHull(imagePositions);

            int sourceIndex = Probability.generateRandomInteger(0, hullPoints.size() - 1);
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
        image.setRotation(Probability.getRandomGenerator().nextInt(360));
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

    public ImageSet getImages() {
        ImageSet imageSet = new ImageSet();
        for (Integer index : getLayerIndices()) {
            Layer layer = getLayer(index);
            if (layer != null) {
                imageSet.add(layer.getImages());
            }
        }
        return imageSet;
    }

    public Image getImageByPosition(Point point) {
        for (Image image : getImages().filterVisibility(Visibility.VISIBLE).getList()) {
            if (image.containsPoint(point)) {
                return image;
            }
        }
        return this;
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
    public void draw(Surface surface) {

        if (Application.ENABLE_GEOMETRY_ANNOTATIONS) {
            // <AXES_ANNOTATION>
            surface.getPaint().setColor(Color.CYAN);
            surface.getPaint().setStrokeWidth(1.0f);
            surface.getCanvas().drawLine(-1000, 0, 1000, 0, surface.getPaint());
            surface.getCanvas().drawLine(0, -1000, 0, 1000, surface.getPaint());
            // </AXES_ANNOTATION>
        }

        // Draw images
        for (Integer index : getLayerIndices()) {
            Layer layer = getLayer(index);
            if (layer != null) {
                for (Image image : layer.getImages()) {
                    image.draw(surface);
                }
            }
        }

        Geometry.computeCirclePacking(getImages().filterType(FrameImage.class, PatchImage.class).getList(), 200, getImages().filterType(FrameImage.class, PatchImage.class).getCentroidPoint());

        // Draw annotations
        if (Application.ENABLE_GEOMETRY_ANNOTATIONS) {

            // <FPS_ANNOTATION>
            Point fpsPosition = getImages().filterType(FrameImage.class).getCenterPoint();
            fpsPosition.setY(fpsPosition.getY() - 200);
            surface.getPaint().setColor(Color.RED);
            surface.getPaint().setStyle(Paint.Style.FILL);
            surface.getCanvas().drawCircle((float) fpsPosition.getX(), (float) fpsPosition.getY(), 10, surface.getPaint());

            surface.getPaint().setStyle(Paint.Style.FILL);
            surface.getPaint().setTextSize(35);

            String fpsText = "FPS: " + (int) surface.getRenderer().getFramesPerSecond();
            Rect fpsTextBounds = new Rect();
            surface.getPaint().getTextBounds(fpsText, 0, fpsText.length(), fpsTextBounds);
            surface.getCanvas().drawText(fpsText, (float) fpsPosition.getX() + 20, (float) fpsPosition.getY() + fpsTextBounds.height() / 2.0f, surface.getPaint());
            // </FPS_ANNOTATION>

            // <CENTROID_ANNOTATION>
            Point centroidPosition = getImages().filterType(FrameImage.class).getCentroidPoint();
            surface.getPaint().setColor(Color.RED);
            surface.getPaint().setStyle(Paint.Style.FILL);
            surface.getCanvas().drawCircle((float) centroidPosition.getX(), (float) centroidPosition.getY(), 10, surface.getPaint());

            surface.getPaint().setStyle(Paint.Style.FILL);
            surface.getPaint().setTextSize(35);

            String text = "CENTROID";
            Rect bounds = new Rect();
            surface.getPaint().getTextBounds(text, 0, text.length(), bounds);
            surface.getCanvas().drawText(text, (float) centroidPosition.getX() + 20, (float) centroidPosition.getY() + bounds.height() / 2.0f, surface.getPaint());
            // </CENTROID_ANNOTATION>

            // <CENTROID_ANNOTATION>
            List<Point> formImagePositions = getImages().filterType(FrameImage.class).getPositions();
            Point formImagesCenterPosition = Geometry.calculateCenterPosition(formImagePositions);
            surface.getPaint().setColor(Color.RED);
            surface.getPaint().setStyle(Paint.Style.FILL);
            surface.getCanvas().drawCircle((float) formImagesCenterPosition.getX(), (float) formImagesCenterPosition.getY(), 10, surface.getPaint());

            surface.getPaint().setStyle(Paint.Style.FILL);
            surface.getPaint().setTextSize(35);

            String centerLabeltext = "CENTER";
            Rect centerLabelTextBounds = new Rect();
            surface.getPaint().getTextBounds(centerLabeltext, 0, centerLabeltext.length(), centerLabelTextBounds);
            surface.getCanvas().drawText(centerLabeltext, (float) formImagesCenterPosition.getX() + 20, (float) formImagesCenterPosition.getY() + centerLabelTextBounds.height() / 2.0f, surface.getPaint());
            // </CENTROID_ANNOTATION>

            // <CONVEX_HULL>
            List<Point> formPositions = Visualization.getPositions(getFrameImages());
            List<Point> convexHullVertices = Geometry.computeConvexHull(formPositions);

            surface.getPaint().setStrokeWidth(1.0f);
            surface.getPaint().setColor(Color.RED);
            surface.getPaint().setStyle(Paint.Style.STROKE);

            for (int i = 0; i < convexHullVertices.size() - 1; i++) {
                Surface.drawPolygon(convexHullVertices, surface);
            }
            // </CONVEX_HULL>

            // <BOUNDING_BOX>
            surface.getPaint().setStrokeWidth(1.0f);
            surface.getPaint().setColor(Color.RED);
            surface.getPaint().setStyle(Paint.Style.STROKE);

            Rectangle boundingBox = getImages().filterType(FrameImage.class).getBoundingBox();
            Surface.drawPolygon(boundingBox.getVertices(), surface);
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
    public boolean containsPoint(Point point) {
        return false;
    }

    @Override
    public boolean containsPoint(Point point, double padding) {
        return false;
    }

    @Override
    public void onImpression(Action action) {
    }
}
