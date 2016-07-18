package camp.computer.clay.visualization.arch;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import camp.computer.clay.application.Application;
import camp.computer.clay.application.VisualizationSurface;
import camp.computer.clay.model.simulation.Base;
import camp.computer.clay.model.simulation.Model;
import camp.computer.clay.model.simulation.Simulation;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.visualization.images.BaseImage;
import camp.computer.clay.visualization.images.PortImage;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.Number;
import camp.computer.clay.visualization.util.PointHolder;
import camp.computer.clay.visualization.util.Rectangle;
import camp.computer.clay.visualization.util.Shape;

public class Visualization extends Image {

//    private <T> ArrayList<T> getModel(Class<T> type) {
//        ArrayList<T> arrayList = new ArrayList<T>();
//        return arrayList;
//    }

    public static <T> ArrayList<PointHolder> getPositions(ArrayList<T> images) {
        ArrayList<PointHolder> positions = new ArrayList<>();
        for (T image: images) {
            positions.add(new PointHolder(((Image) image).getPosition().getX(), ((Image) image).getPosition().getY()));
        }
        return positions;
    }

    private HashMap<String, Layer> layers = new HashMap<String, Layer>();

    private float gridScale = 1.5f;

    public Visualization(Simulation simulation) {
        super(simulation);
        setup();
    }

    private void setup() {
        // setupImages();
    }

    public void setGridScale(float scale) {
        this.gridScale = scale;
    }

    public float getGridScale() {
        return this.gridScale;
    }

    public boolean hasLayer(String name) {
        return this.layers.containsKey(name);
    }

    public void addLayer(String name) {
        if (!this.layers.containsKey(name)) {
            Layer layer = new Layer(this);
            this.layers.put(name, layer);
        }
    }

    // TODO: Remove Image parameter. Create that and return it.
    public void addImage (Model model, Image image, String layerName) {
        if (!hasLayer(layerName)) {
            addLayer(layerName);
        }
        getLayer(layerName).addImage(model, image);
    }

    public Set<String> getLayerNames() {
        return this.layers.keySet();
    }

    public Layer getLayer(String name) {
        return this.layers.get(name);
    }

    public Layer getLayer(int id) {
        for (Layer layer: getLayers()) {
            if (layer.getId() == id) {
                return layer;
            }
        }
        return null;
    }

    public void setupImages() {

        String machineLayerName = "machines";
        addLayer(machineLayerName);
        Layer defaultLayer = getLayer(machineLayerName);

        Simulation simulation = (Simulation) getModel();

        // Create machine sprites
        for (Base base : simulation.getBases()) {
            BaseImage baseImage = new BaseImage(base);
            baseImage.setVisualization(this);

            addImage(base, baseImage, machineLayerName);
        }

        // Calculate random positions separated by minimum distance
        final float minimumDistance = 550;
        ArrayList<PointHolder> imagePositions = new ArrayList<>();
        while (imagePositions.size() < simulation.getBases().size()) {
            boolean foundPoint = false;
            if (imagePositions.size() == 0) {
                imagePositions.add(new PointHolder(0, 0));
            } else {
                for (int i = 0; i < imagePositions.size(); i++) {
                    for (int tryCount = 0; tryCount < 360; tryCount++) {
                        boolean fail = false;
                        PointHolder candidatePoint = Geometry.calculatePoint(imagePositions.get(i), Number.generateRandomInteger(0, 360), minimumDistance);
                        for (int j = 0; j < imagePositions.size(); j++) {
                            if (Geometry.calculateDistance(imagePositions.get(j), candidatePoint) < minimumDistance) {
                                fail = true;
                                break;
                            }
                        }
                        if (fail == false) {
                            imagePositions.add(candidatePoint);
                            foundPoint = true;
                            break;
                        }
                        if (foundPoint) {
                            break;
                        }
                    }
                    if (foundPoint) {
                        break;
                    }
                }
            }
        }

        for (int i = 0; i < simulation.getBases().size(); i++) {

            BaseImage baseImage = (BaseImage) defaultLayer.getImage2(simulation.getBase(i));

            baseImage.setRelativePosition(imagePositions.get(i));
            baseImage.setRotation(Number.getRandomGenerator().nextInt(360));

            baseImage.setupPortImages();
        }
    }

    public Image getImage(Model model) {
        for (Layer layer: getLayers()) {
            Image image = layer.getImage2(model);
            if (image != null) {
                return image;
            }
        }
        return null;
    }

    public Model getModel(Image image) {
        for (Layer layer: getLayers()) {
            Model model = layer.getModel2(image);
            if (model != null) {
                return model;
            }
        }
        return null;
    }

    public ArrayList<BaseImage> getBaseImages() {

        ArrayList<BaseImage> images = new ArrayList<>();

        for (Layer layer: getLayers()) {
            for (Image image: layer.getImages()) {
                if (image instanceof BaseImage) {
                    images.add((BaseImage) image);
                }
            }
        }

        return images;
    }

    public ArrayList<PortImage> getPortImages() {

        ArrayList<PortImage> sprites = new ArrayList<>();

        for (Layer layer: getLayers()) {
            for (Image image : layer.getImages()) {
                if (image instanceof PortImage) {
                    sprites.add((PortImage) image);
                }
            }
        }

        return sprites;
    }

    public <T> ArrayList<Image> getImages(ArrayList<T> models) {
        ArrayList<Image> images = new ArrayList<>();
        for (Layer layer: getLayers()) {
            for (T model : models) {
                Image image = layer.getImage2((Model) model);
                if (image != null) {
                    images.add(image);
                }
            }
        }
        return images;
    }

    public ImageGroup getImages() {
        ImageGroup imageGroup = new ImageGroup();
        for (Layer layer: getLayers()) {
            for (Image layerImage: layer.getImages()) {
                imageGroup.add(layerImage);
            }
        }
        return imageGroup;
    }

    /**
     * Finds and returns the nearest <em>visible</em> <code>Image</code>.
     * @param position
     * @return
     */
    public Image getNearestImage (PointHolder position) {

        float shortestDistance = Float.MAX_VALUE;
        Image nearestImage = null;

        for (Image image: getImages().getList()) {

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

    public BaseImage getNearestBaseImage(PointHolder position) {

        float shortestDistance = Float.MAX_VALUE;
        BaseImage nearestBaseImage = null;

        for (BaseImage baseImage : getBaseImages()) {

            // Update style of nearby machines
            float currentDistance = (float) Geometry.calculateDistance(
                    position,
                    baseImage.getPosition()
            );

            if (currentDistance < shortestDistance) {

                shortestDistance = currentDistance;
                nearestBaseImage = baseImage;

            }
        }

        return nearestBaseImage;
    }

    public PortImage getNearestPortImage(PointHolder position) {

        float shortestDistance = Float.MAX_VALUE;
        PortImage nearestImage = null;

        for (PortImage image: getPortImages()) {

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

    public Simulation getSimulation() {
        return (Simulation) getModel();
    }

    public void update() {

        getSimulation().getBody(0).getPerspective().update();

        for (Layer layer: getLayers()) {
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

        // Draw grid
        // drawGrid(visualizationSurface);

        // Draw images
        for (Integer id: getLayerIds()) {
            Layer layer = getLayer(id);
            if (layer == null) {
                break;
            }
            for (Image image: layer.getImages()) {
                image.draw(visualizationSurface);
            }
        }

        //Geometry.packCircles(getVisualization().getBaseImages(), 200, getVisualization().getList().filterType(BaseImage.TYPE).calculateCentroid());

        // Draw annotations
        if (Application.ENABLE_GEOMETRY_ANNOTATIONS) {

            // <FPS_ANNOTATION>
            PointHolder fpsPosition = getImages().filterType(BaseImage.TYPE).calculateCenter();
            fpsPosition.setY(fpsPosition.getY() - 200);
            visualizationSurface.getPaint().setColor(Color.RED);
            visualizationSurface.getPaint().setStyle(Paint.Style.FILL);
            visualizationSurface.getCanvas().drawCircle((float) fpsPosition.getX(), (float) fpsPosition.getY(), 10, visualizationSurface.getPaint());

            visualizationSurface.getPaint().setStyle(Paint.Style.FILL);
            visualizationSurface.getPaint().setTextSize(35);

            String fpsText = "FPS: " + (int) visualizationSurface.getRenderer().getFramesPerSecond();
            Rect fpsTextBounds = new Rect();
            visualizationSurface.getPaint().getTextBounds(fpsText, 0, fpsText.length(), fpsTextBounds);
            visualizationSurface.getCanvas().drawText(fpsText, (float) fpsPosition.getX() + 20,(float) fpsPosition.getY() + fpsTextBounds.height() / 2.0f, visualizationSurface.getPaint());
            // </FPS_ANNOTATION>

            // <CENTROID_ANNOTATION>
            PointHolder centroidPosition = getImages().filterType(BaseImage.TYPE).calculateCentroid();
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
            ArrayList<PointHolder> baseImagePositions = getImages().filterType(BaseImage.TYPE).getPositions();
            PointHolder baseImagesCenterPosition = Geometry.calculateCenterPosition(baseImagePositions);
            visualizationSurface.getPaint().setColor(Color.RED);
            visualizationSurface.getPaint().setStyle(Paint.Style.FILL);
            visualizationSurface.getCanvas().drawCircle((float) baseImagesCenterPosition.getX(), (float) baseImagesCenterPosition.getY(), 10, visualizationSurface.getPaint());

            visualizationSurface.getPaint().setStyle(Paint.Style.FILL);
            visualizationSurface.getPaint().setTextSize(35);

            String centerLabeltext = "CENTER";
            Rect centerLabelTextBounds = new Rect();
            visualizationSurface.getPaint().getTextBounds(centerLabeltext, 0, centerLabeltext.length(), centerLabelTextBounds);
            visualizationSurface.getCanvas().drawText(centerLabeltext, (float) baseImagesCenterPosition.getX() + 20, (float) baseImagesCenterPosition.getY() + centerLabelTextBounds.height() / 2.0f, visualizationSurface.getPaint());
            // </CENTROID_ANNOTATION>

            // <CONVEX_HULL>
            ArrayList<PointHolder> basePositions = Visualization.getPositions(getBaseImages());
            ArrayList<PointHolder> convexHullVertices = Geometry.computeConvexHull(basePositions);

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

            Rectangle boundingBox = getImages().filterType(BaseImage.TYPE).calculateBoundingBox();
            Shape.drawPolygon(boundingBox.getVertices(), visualizationSurface.getCanvas(), visualizationSurface.getPaint());
            // </BOUNDING_BOX>
        }
    }

    public ArrayList<Integer> getLayerIds() {
        ArrayList<Integer> layers = new ArrayList<>();
        for (Layer layer: getLayers()) {
            layers.add(layer.getId());
        }
        Collections.sort(layers);
        return layers;
    }

    private void drawGrid(VisualizationSurface visualizationSurface) {
        // Draw grid
        Canvas canvas = visualizationSurface.getCanvas();
        Paint paint = visualizationSurface.getPaint();

        // Draw grid
        float horizontalStep = 50;
        float verticalStep = 50;
        float pointRadius = 3;
        for (int i = -512; i < canvas.getWidth(); i += horizontalStep) {
            for (int j = -1024; j < canvas.getHeight(); j += verticalStep) {

                paint.setStyle(Paint.Style.FILL);
                paint.setColor(Color.LTGRAY);
                canvas.drawCircle(
                        i,
                        j,
                        pointRadius,
                        paint
                );

            }
        }
    }

    public ArrayList<Layer> getLayers() {
        return new ArrayList<>(this.layers.values());
    }

    @Override
    public boolean isTouching(PointHolder point) {
        return false;
    }

    @Override
    public boolean isTouching(PointHolder point, double padding) {
        return false;
    }

    @Override
    public void onTouchInteraction(TouchInteraction touchInteraction) {
    }

//    public PointHolder getCentroidPosition() {
//
//        // Auto-adjust the perspective
//        ArrayList<PointHolder> spritePositions = new ArrayList<PointHolder>();
//
//        for (Image image: getBaseImages()) {
//            if (image.isVisible()) {
//                spritePositions.add(image.getPosition());
//            }
//        }
//
//        return Geometry.calculateCentroidPosition(spritePositions);
//    }
}
