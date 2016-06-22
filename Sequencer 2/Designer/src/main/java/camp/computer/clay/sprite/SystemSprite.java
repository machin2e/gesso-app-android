package camp.computer.clay.sprite;

import android.graphics.PointF;

import java.util.ArrayList;
import java.util.Random;

import camp.computer.clay.designer.MapView;
import camp.computer.clay.model.TouchArticulation;

public class SystemSprite extends Sprite {

    // Sprites
    private ArrayList<MachineSprite> machineSprites = new ArrayList<MachineSprite>();

    public SystemSprite() {
        initialize();
    }

    private void initialize() {
        initializeSprites();
    }

    public void initializeSprites() {
        Random random = new Random();
        machineSprites.add(new MachineSprite(random.nextInt(30) - 15, random.nextInt(30) - 15, random.nextInt(360)));
        machineSprites.add(new MachineSprite(340, 400, random.nextInt(360)));
        machineSprites.add(new MachineSprite(-200, -440, random.nextInt(360)));
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
    public void onTouchAction(TouchArticulation touchArticulation) {

    }

    public void updateState () {

        for (MachineSprite machineSprite : machineSprites) {
            machineSprite.updateChannelData();
        }
    }
}
