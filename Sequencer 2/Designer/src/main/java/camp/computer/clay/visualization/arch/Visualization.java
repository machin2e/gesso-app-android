package camp.computer.clay.visualization.arch;

import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
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

public class Visualization extends Image {

//    private <T> ArrayList<T> getModel(Class<T> type) {
//        ArrayList<T> arrayList = new ArrayList<T>();
//        return arrayList;
//    }

    public static <T> ArrayList<PointF> getPositions(ArrayList<T> images) {
        ArrayList<PointF> positions = new ArrayList<PointF>();
        for (T image: images) {
            positions.add(new PointF(((Image) image).getPosition().x, ((Image) image).getPosition().y));
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
        ArrayList<PointF> imagePositions = new ArrayList<PointF>();
        while (imagePositions.size() < simulation.getBases().size()) {
            boolean foundPoint = false;
            if (imagePositions.size() == 0) {
                imagePositions.add(new PointF(0, 0));
            } else {
                for (int i = 0; i < imagePositions.size(); i++) {
                    for (int tryCount = 0; tryCount < 360; tryCount++) {
                        boolean fail = false;
                        PointF candidatePoint = Geometry.calculatePoint(imagePositions.get(i), Number.generateRandomInteger(0, 360), minimumDistance);
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

            BaseImage baseImage = (BaseImage) defaultLayer.getImage2(simulation.getMachine(i));

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

        ArrayList<BaseImage> images = new ArrayList<BaseImage>();

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

        ArrayList<PortImage> sprites = new ArrayList<PortImage>();

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
        ArrayList<Image> images = new ArrayList<Image>();
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

    public ImageGroup getImageGroup () {
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
    public Image getNearestImage (PointF position) {

        float shortestDistance = Float.MAX_VALUE;
        Image nearestImage = null;

        for (Image image: getImageGroup().getImages()) {

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

    public BaseImage getNearestBaseImage(PointF position) {

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

    public PortImage getNearestPortImage(PointF position) {

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
        for (Layer layer: getLayers()) {
            for (Image image : layer.getImages()) {
                image.update();
            }
        }
    }



    @Override
    public void draw(VisualizationSurface visualizationSurface) {

        // Draw grid
        /*
        Canvas canvas = mapView.getCanvas();
        Paint paint = mapView.getPaint();

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
        */

        if (Application.ENABLE_DEBUG_ANNOTATIONS) {
            // <AXES_ANNOTATION>
            visualizationSurface.getPaint().setColor(Color.BLUE);
            visualizationSurface.getPaint().setStrokeWidth(1.0f);
            visualizationSurface.getCanvas().drawLine(-1000, 0, 1000, 0, visualizationSurface.getPaint());
            visualizationSurface.getCanvas().drawLine(0, -1000, 0, 1000, visualizationSurface.getPaint());
            // </AXES_ANNOTATION>
        }

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

        // Draw annotations
        if (Application.ENABLE_DEBUG_ANNOTATIONS) {

            // <CENTROID_ANNOTATION>
            PointF centroidPosition = getImageGroup().filterType(BaseImage.TYPE).calculateCentroid();
            visualizationSurface.getPaint().setColor(Color.RED);
            visualizationSurface.getPaint().setStyle(Paint.Style.FILL);
            visualizationSurface.getCanvas().drawCircle(centroidPosition.x, centroidPosition.y, 10, visualizationSurface.getPaint());

            visualizationSurface.getPaint().setStyle(Paint.Style.FILL);
            visualizationSurface.getPaint().setTextSize(35);

            String text = "CENTROID";
            Rect bounds = new Rect();
            visualizationSurface.getPaint().getTextBounds(text, 0, text.length(), bounds);
            visualizationSurface.getCanvas().drawText(text, centroidPosition.x + 20, centroidPosition.y + bounds.height() / 2.0f, visualizationSurface.getPaint());
            // </CENTROID_ANNOTATION>

            // <CENTROID_ANNOTATION>
            ArrayList<PointF> baseImagePositions = getImageGroup().filterType(BaseImage.TYPE).getPositions();
            PointF baseImagesCenterPosition = Geometry.calculateCenterPosition(baseImagePositions);
            visualizationSurface.getPaint().setColor(Color.RED);
            visualizationSurface.getPaint().setStyle(Paint.Style.FILL);
            visualizationSurface.getCanvas().drawCircle(baseImagesCenterPosition.x, baseImagesCenterPosition.y, 10, visualizationSurface.getPaint());

            visualizationSurface.getPaint().setStyle(Paint.Style.FILL);
            visualizationSurface.getPaint().setTextSize(35);

            String centerLabeltext = "CENTER";
            Rect centerLabelTextBounds = new Rect();
            visualizationSurface.getPaint().getTextBounds(centerLabeltext, 0, centerLabeltext.length(), centerLabelTextBounds);
            visualizationSurface.getCanvas().drawText(centerLabeltext, baseImagesCenterPosition.x + 20, baseImagesCenterPosition.y + centerLabelTextBounds.height() / 2.0f, visualizationSurface.getPaint());
            // </CENTROID_ANNOTATION>

            // <CONVEX_HULL>
            ArrayList<PointF> basePositions = Visualization.getPositions(getBaseImages());
            ArrayList<PointF> hull = Geometry.computeConvexHull(basePositions);

            visualizationSurface.getPaint().setColor(Color.RED);
            visualizationSurface.getPaint().setStyle(Paint.Style.FILL);

            for (int i = 0; i < hull.size() - 1; i++) {
                PointF p1 = hull.get(i);
                PointF p2 = hull.get(i + 1);

                visualizationSurface.getCanvas().drawLine(p1.x, p1.y, p2.x, p2.y, visualizationSurface.getPaint());
            }
            // </CONVEX_HULL>
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

    public ArrayList<Layer> getLayers() {
        return new ArrayList<>(this.layers.values());
    }

    @Override
    public boolean isTouching(PointF point) {
        return false;
    }

    @Override
    public boolean isTouching(PointF point, float padding) {
        return false;
    }

    @Override
    public void onTouchInteraction(TouchInteraction touchInteraction) {
    }

//    public PointF getCentroidPosition() {
//
//        // Auto-adjust the perspective
//        ArrayList<PointF> spritePositions = new ArrayList<PointF>();
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
