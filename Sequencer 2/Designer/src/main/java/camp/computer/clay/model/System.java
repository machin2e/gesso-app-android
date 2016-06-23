package camp.computer.clay.model;

import java.util.ArrayList;

public class System extends Model {

    private ArrayList<Machine> machines = new ArrayList<Machine>();

    public void addMachine(Machine path) {
        this.machines.add(path);
    }

    public Machine getMachine(int index) {
        return this.machines.get(index);
    }

    public ArrayList<Machine> getMachines() {
        return this.machines;
    }
}
