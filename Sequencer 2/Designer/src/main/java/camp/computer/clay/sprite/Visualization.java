package camp.computer.clay.sprite;

import android.graphics.PointF;

import java.util.ArrayList;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.simulation.Machine;
import camp.computer.clay.model.simulation.Simulation;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.sprite.util.Geometry;
import camp.computer.clay.sprite.util.Number;

public class Visualization extends Sprite {

//    private <T> ArrayList<T> getModel(Class<T> type) {
//        ArrayList<T> arrayList = new ArrayList<T>();
//        return arrayList;
//    }

    public static <T> ArrayList<PointF> getPositions(ArrayList<T> sprites) {
        ArrayList<PointF> positions = new ArrayList<PointF>();
        for (T sprite: sprites) {
            positions.add(new PointF(((Sprite) sprite).getPosition().x, ((Sprite) sprite).getPosition().y));
        }
        return positions;
    }

    // Sprites
//    private HashMap<Model, Sprite> sprites = new HashMap<Model, Sprite>();

    private ArrayList<Layer> layers = new ArrayList<Layer>();

    public Visualization(Simulation simulation) {
        super(simulation);
        initialize();
    }

    private void initialize() {
        // initializeSprites();
    }

    public Layer getLayer(int index) {
        return this.layers.get(index);
    }

    public void initializeSprites() {

        Layer defaultLayer = new Layer(this);
        this.layers.add(defaultLayer);

        Simulation simulation = (Simulation) getModel();

        // Create machine sprites
        for (Machine machine: simulation.getMachines()) {
            MachineSprite machineSprite = new MachineSprite(machine);
            machineSprite.setParentSprite(this);
            machineSprite.setVisualization(this);

            defaultLayer.addSprite(machine, machineSprite);
        }

        // Calculate random positions separated by minimum distance
        float minimumDistance = 550;
        ArrayList<PointF> machineSpriteCenterPoints = new ArrayList<PointF>();
        while (machineSpriteCenterPoints.size() < simulation.getMachines().size()) {
            boolean foundPoint = false;
            if (machineSpriteCenterPoints.size() == 0) {
                machineSpriteCenterPoints.add(new PointF(0, 0));
            } else {
                for (int i = 0; i < machineSpriteCenterPoints.size(); i++) {
                    for (int tryCount = 0; tryCount < 360; tryCount++) {
                        boolean fail = false;
                        PointF candidatePoint = Geometry.calculatePoint(machineSpriteCenterPoints.get(i), Number.getRandomInteger(0, 360), minimumDistance);
                        for (int j = 0; j < machineSpriteCenterPoints.size(); j++) {
                            if (Geometry.calculateDistance(machineSpriteCenterPoints.get(j), candidatePoint) < minimumDistance) {
                                fail = true;
                                break;
                            }
                        }
                        if (fail == false) {
                            machineSpriteCenterPoints.add(candidatePoint);
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

            MachineSprite machineSprite = (MachineSprite) defaultLayer.getSprite(simulation.getMachine(i));

            machineSprite.setRelativePosition(machineSpriteCenterPoints.get(i));
            machineSprite.setRotation(Number.getRandom().nextInt(360));

            machineSprite.initializePortSprites();
        }
    }

//    public void addSprite(Model model, Sprite sprite) {
////        if (!this.sprites.containsKey(model)) {
//            this.sprites.put(model, sprite);
////        }
//    }
//
//    public Sprite getSprite(Model model) {
//        return this.sprites.get(model);
//    }
//
//    public Model getModel(Sprite sprite) {
//        for (Model model: this.sprites.keySet()) {
//            if (this.sprites.get(model) == sprite) {
//                return model;
//            }
//        }
//        return null;
//    }

//    public void removeSprite(Model model, Sprite sprite) {
//        if (this.sprites.containsKey(model)) {
//            this.sprites.remove(model);
//        }
//    }

//    public ArrayList<Sprite> getSprites() {
//        return this.sprites;
//    }

//    public ArrayList<Sprite> getSprites(Class<?> type) {
//
//        ArrayList<Sprite> sprites = new ArrayList<Sprite>();
//
//        for (Sprite sprite: this.sprites.values()) {
//            if (sprite.getClass() == type) {
//                sprites.add(sprite);
//            }
//        }
//
//        return sprites;
//    }

    public ArrayList<MachineSprite> getMachineSprites() {

        ArrayList<MachineSprite> sprites = new ArrayList<MachineSprite>();

        for (Layer layer: this.layers) {
            for (Sprite sprite: layer.getSprites()) {
                if (sprite instanceof MachineSprite) {
                    sprites.add((MachineSprite) sprite);
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

        Simulation simulation = getSimulation();

        for (Layer layer: this.layers) {
            for (Sprite sprite: layer.getSprites()) {
                sprite.draw(mapView);
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
        Simulation simulation = getSimulation();

        for (Layer layer: this.layers) {
            for (Sprite sprite: layer.getSprites()) {
                sprite.update();
            }
        }
    }

    public PointF getCentroidPosition() {
        // Auto-adjust the perspective
        ArrayList<PointF> spritePositions = new ArrayList<PointF>();

        Simulation simulation = getSimulation();

        for (Layer layer: this.layers) {
            for (Sprite sprite: layer.getSprites()) {
                if (sprite.isVisible()) {
                    spritePositions.add(sprite.getPosition());
                }
            }
        }

        PointF centroidPosition = Geometry.calculateCentroid(spritePositions);
        return centroidPosition;
    }
}
