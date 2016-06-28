package camp.computer.clay.sprite;

import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.Machine;
import camp.computer.clay.model.Simulation;
import camp.computer.clay.model.TouchInteraction;
import camp.computer.clay.sprite.util.Geometry;
import camp.computer.clay.sprite.util.Number;

public class Visualization extends Sprite {

    // Sprites
    private ArrayList<MachineSprite> machineSprites = new ArrayList<MachineSprite>();

    public Visualization(Simulation simulation) {
        super(simulation);
        initialize();
    }

    private void initialize() {
        // initializeSprites();
    }

    public void initializeSprites() {
        Simulation simulation = (Simulation) getModel();
        for (Machine machine: simulation.getMachines()) {
            MachineSprite machineSprite = new MachineSprite(machine);
            machineSprite.setParentSprite(this);
            machineSprites.add(machineSprite);
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

        for (int i = 0; i < machineSpriteCenterPoints.size(); i++) {
            Log.v("Points", "x: " + machineSpriteCenterPoints.get(i).x + ", y: " + machineSpriteCenterPoints.get(i).y);

            machineSprites.get(i).setRelativePosition(machineSpriteCenterPoints.get(i));
            machineSprites.get(i).setRotation(Number.getRandom().nextInt(360));
            machineSprites.get(i).initializePortSprites();
        }

        Log.v("Rot", "-----");
//        machineSprites.get(0).setRelativePosition(new PointF(0, 0));
//        machineSprites.get(0).setRotation(Number.getRandom().nextInt(360));
//        machineSprites.get(0).initializePortSprites();
//
//        //machineSprites.get(1).setRelativePosition(new PointF(340, 400));
//        machineSprites.get(1).setRelativePosition(new PointF(400, 400));
//        machineSprites.get(1).setRotation(Number.getRandom().nextInt(360));
//        machineSprites.get(1).initializePortSprites();
//
//        machineSprites.get(2).setRelativePosition(new PointF(-200, -400));
//        machineSprites.get(2).setRotation(Number.getRandom().nextInt(360));
//        machineSprites.get(2).initializePortSprites();
        Log.v("Rot", "-----");
    }

    public ArrayList<MachineSprite> getMachineSprites() {
        return this.machineSprites;
    }

    @Override
    public void draw(MapView mapView) {
        for (Sprite sprite: this.machineSprites) {
            sprite.draw(mapView);
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
        for (MachineSprite machineSprite: machineSprites) {
            machineSprite.update();
        }
    }
}
