package camp.computer.clay.visualization;

import android.graphics.PointF;

import java.util.ArrayList;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.simulation.Machine;
import camp.computer.clay.model.simulation.Simulation;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.visualization.util.Geometry;
import camp.computer.clay.visualization.util.Number;

public class Visualization extends Image {

//    private <T> ArrayList<T> getModel(Class<T> type) {
//        ArrayList<T> arrayList = new ArrayList<T>();
//        return arrayList;
//    }

    public static <T> ArrayList<PointF> getPositions(ArrayList<T> sprites) {
        ArrayList<PointF> positions = new ArrayList<PointF>();
        for (T sprite: sprites) {
            positions.add(new PointF(((Image) sprite).getPosition().x, ((Image) sprite).getPosition().y));
        }
        return positions;
    }

    private ArrayList<Layer> layers = new ArrayList<Layer>();

    public Visualization(Simulation simulation) {
        super(simulation);
        initialize();
    }

    private void initialize() {
        // initializeImages();
    }

    public Layer getLayer(int index) {
        return this.layers.get(index);
    }

    public void initializeImages() {

        Layer defaultLayer = new Layer(this);
        this.layers.add(defaultLayer);

        Simulation simulation = (Simulation) getModel();

        // Create machine sprites
        for (Machine machine: simulation.getMachines()) {
            MachineImage machineImage = new MachineImage(machine);
            machineImage.setParentImage(this);
            machineImage.setVisualization(this);

            defaultLayer.addImage(machine, machineImage);
        }

        // Calculate random positions separated by minimum distance
        float minimumDistance = 550;
        ArrayList<PointF> machineImageCenterPoints = new ArrayList<PointF>();
        while (machineImageCenterPoints.size() < simulation.getMachines().size()) {
            boolean foundPoint = false;
            if (machineImageCenterPoints.size() == 0) {
                machineImageCenterPoints.add(new PointF(0, 0));
            } else {
                for (int i = 0; i < machineImageCenterPoints.size(); i++) {
                    for (int tryCount = 0; tryCount < 360; tryCount++) {
                        boolean fail = false;
                        PointF candidatePoint = Geometry.calculatePoint(machineImageCenterPoints.get(i), Number.generateRandomInteger(0, 360), minimumDistance);
                        for (int j = 0; j < machineImageCenterPoints.size(); j++) {
                            if (Geometry.calculateDistance(machineImageCenterPoints.get(j), candidatePoint) < minimumDistance) {
                                fail = true;
                                break;
                            }
                        }
                        if (fail == false) {
                            machineImageCenterPoints.add(candidatePoint);
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

            MachineImage machineImage = (MachineImage) defaultLayer.getImage(simulation.getMachine(i));

            machineImage.setRelativePosition(machineImageCenterPoints.get(i));
            machineImage.setRotation(Number.getRandom().nextInt(360));

            machineImage.initializePortImages();
        }
    }

    public ArrayList<MachineImage> getMachineImages() {

        ArrayList<MachineImage> sprites = new ArrayList<MachineImage>();

        for (Layer layer: this.layers) {
            for (Image image : layer.getImages()) {
                if (image instanceof MachineImage) {
                    sprites.add((MachineImage) image);
                }
            }
        }

        return sprites;
    }

    public Simulation getSimulation() {
        return (Simulation) getModel();
    }

    @Override
    public void draw(MapView mapView) {
        for (Layer layer: this.layers) {
            for (Image image : layer.getImages()) {
                image.draw(mapView);
            }
        }
    }

    @Override
    public boolean isTouching(PointF point) {
        return false;
    }

    @Override
    public void onTouchAction(TouchInteraction touchInteraction) {

    }

    public void update() {
        for (Layer layer: this.layers) {
            for (Image image : layer.getImages()) {
                image.update();
            }
        }
    }

    public PointF getCentroidPosition() {
        // Auto-adjust the perspective
        ArrayList<PointF> spritePositions = new ArrayList<PointF>();

        for (Layer layer: this.layers) {
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
