package camp.computer.clay.visualization;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

import camp.computer.clay.application.VisualizationSurface;
import camp.computer.clay.model.simulation.Machine;
import camp.computer.clay.model.simulation.Model;
import camp.computer.clay.model.simulation.Simulation;
import camp.computer.clay.model.interaction.TouchInteraction;
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
        initialize();
    }

    private void initialize() {
        // initializeImages();
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

    public void initializeImages() {

        String machineLayerName = "machines";
        addLayer(machineLayerName);
        Layer defaultLayer = getLayer(machineLayerName);

        Simulation simulation = (Simulation) getModel();

        // Create machine sprites
        for (Machine machine: simulation.getMachines()) {
            MachineImage machineImage = new MachineImage(machine);
            machineImage.setVisualization(this);

            addImage(machine, machineImage, machineLayerName);
        }

        // Calculate random positions separated by minimum distance
        float minimumDistance = 550;
        ArrayList<PointF> imagePositions = new ArrayList<PointF>();
        while (imagePositions.size() < simulation.getMachines().size()) {
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

        for (int i = 0; i < simulation.getMachines().size(); i++) {

            MachineImage machineImage = (MachineImage) defaultLayer.getImage2(simulation.getMachine(i));

            machineImage.setRelativePosition(imagePositions.get(i));
            machineImage.setRotation(Number.getRandom().nextInt(360));

            machineImage.initializePortImages();
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

    public ArrayList<MachineImage> getMachineImages() {

        ArrayList<MachineImage> images = new ArrayList<MachineImage>();

        for (Layer layer: getLayers()) {
            for (Image image: layer.getImages()) {
                if (image instanceof MachineImage) {
                    images.add((MachineImage) image);
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

    public MachineImage getNearestMachineImage(PointF position) {

        float shortestDistance = Float.MAX_VALUE;
        MachineImage nearestMachineImage = null;

        for (MachineImage machineImage: getVisualization().getMachineImages()) {

            // Update style of nearby machines
            float distanceToMachineImage = (float) Geometry.calculateDistance(
                    position,
                    machineImage.getPosition()
            );

            if (distanceToMachineImage < shortestDistance) {

                shortestDistance = distanceToMachineImage;
                nearestMachineImage = machineImage;

            }
        }

        return nearestMachineImage;
    }

    public PortImage getNearestPortImage(PointF position) {

        float shortestDistance = Float.MAX_VALUE;
        PortImage nearestImage = null;

        for (PortImage image: getVisualization().getPortImages()) {

            // Update style of nearby machines
            float distanceToImage = (float) Geometry.calculateDistance(
                    position,
                    image.getPosition()
            );

            if (distanceToImage < shortestDistance) {
                shortestDistance = distanceToImage;
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
    }

    public ArrayList<Integer> getLayerIds() {
        ArrayList<Integer> layers = new ArrayList<Integer>();
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

    public PointF getCentroidPosition() {
        // Auto-adjust the perspective
        ArrayList<PointF> spritePositions = new ArrayList<PointF>();

        for (Layer layer: getLayers()) {
            for (Image image : layer.getImages()) {
                if (image.isVisible()) {
                    spritePositions.add(image.getPosition());
                }
            }
        }

        PointF centroidPosition = Geometry.calculateCentroid(spritePositions);
        return centroidPosition;
    }
}
