package camp.computer.clay.model.player;

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
