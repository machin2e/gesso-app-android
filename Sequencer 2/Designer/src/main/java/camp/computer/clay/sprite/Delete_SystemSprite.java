package camp.computer.clay.sprite;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Random;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.Machine;
import camp.computer.clay.model.Model;
import camp.computer.clay.model.TouchInteraction;
import camp.computer.clay.sprite.util.Number;

public class Delete_SystemSprite extends Sprite {

    // Sprites
    private ArrayList<MachineSprite> machineSprites = new ArrayList<MachineSprite>();

    public Delete_SystemSprite(camp.computer.clay.model.System system) {
        super(system);
        initialize();
    }

    private void initialize() {
        initializeSprites();
    }

    public void initializeSprites() {
        camp.computer.clay.model.System system = (camp.computer.clay.model.System) getModel();
        for (Machine machine: system.getMachines()) {
            MachineSprite machineSprite = new MachineSprite(machine);
            machineSprite.setParentSprite(this);
            machineSprite.setPosition(new PointF(Number.getRandomInteger(-300, 300), Number.getRandomInteger(-500, 500)));
            machineSprite.setRotation(Number.getRandomInteger(0, 360));
            machineSprites.add(machineSprite);
            // random.nextInt(30) - 15, random.nextInt(30) - 15, 0));
//            machineSprites.add(new MachineSprite(340, 400, 0));
//            machineSprites.add(new MachineSprite(-200, -440, 0));
        }

        machineSprites.get(0).setPosition(new PointF(Number.getRandomInteger(-15, 15), Number.getRandomInteger(-15, 15)));
        machineSprites.get(0).setRotation(Number.getRandomInteger(0, 1));

        machineSprites.get(1).setPosition(new PointF(340, 400));
        machineSprites.get(1).setRotation(Number.getRandomInteger(0, 1));

        machineSprites.get(2).setPosition(new PointF(-200, -440));
        machineSprites.get(2).setRotation(Number.getRandomInteger(0, 1));
    }

    public ArrayList<MachineSprite> getMachineSprites() {
        return this.machineSprites;
    }

    @Override
    public void draw(MapView mapView) {
        // drawTitle();

        for (MachineSprite machineSprite : machineSprites) {
            machineSprite.draw(mapView);
        }
    }

    @Override
    public boolean isTouching(PointF point) {
        return false;
    }

    @Override
    public void onTouchAction(TouchInteraction touchInteraction) {

    }

    public void updateState () {

        for (MachineSprite machineSprite : machineSprites) {
            machineSprite.updateChannelData();
        }
    }
}
