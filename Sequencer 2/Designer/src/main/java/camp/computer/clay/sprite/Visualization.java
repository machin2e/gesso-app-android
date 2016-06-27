package camp.computer.clay.sprite;

import android.graphics.PointF;
import android.util.Log;

import java.util.ArrayList;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.Machine;
import camp.computer.clay.model.Simulation;
import camp.computer.clay.model.TouchInteraction;
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

        Log.v("Rot", "-----");
        machineSprites.get(0).setRelativePosition(new PointF(0, 0));
        machineSprites.get(0).setRotation(Number.getRandom().nextInt(1));
        machineSprites.get(0).initializePortSprites();

        //machineSprites.get(1).setRelativePosition(new PointF(340, 400));
        machineSprites.get(1).setRelativePosition(new PointF(400, 400));
        machineSprites.get(1).setRotation(Number.getRandom().nextInt(1));
        machineSprites.get(1).initializePortSprites();

        machineSprites.get(2).setRelativePosition(new PointF(-200, -400));
        machineSprites.get(2).setRotation(Number.getRandom().nextInt(1));
        machineSprites.get(2).initializePortSprites();
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
