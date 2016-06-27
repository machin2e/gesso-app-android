package camp.computer.clay.model;

import java.util.ArrayList;

public class Simulation extends Model {

    private ArrayList<Body> bodies = new ArrayList<Body>();

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

    public void addBody(Body body) {
        this.bodies.add(body);
    }

    public Body getBody(int index) {
        return this.bodies.get(index);
    }

    public ArrayList<Body> getBodies() {
        return this.bodies;
    }
}
