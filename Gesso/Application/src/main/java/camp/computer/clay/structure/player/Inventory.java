package camp.computer.clay.structure.player;

import java.util.ArrayList;
import java.util.List;

public class Inventory {

    private List<Item> items;

    public Inventory() {
        setup();
    }

    private void setup() {
        items = new ArrayList<>();
    }
}
