package camp.computer.clay.model.simulation;

import java.util.ArrayList;

import camp.computer.clay.model.interaction.Body;

public class Simulation extends Model {

    private ArrayList<Body> bodies = new ArrayList<>();

    private ArrayList<Frame> frames = new ArrayList<>();

    private ArrayList<Peripheral> peripherals = new ArrayList<>();

    public void addFrame(Frame path) {
        this.frames.add(path);
    }

    public Frame getFrame(int index) {
        return this.frames.get(index);
    }

    public ArrayList<Frame> getFrames() {
        return this.frames;
    }

    public ArrayList<Port> getPorts() {
        ArrayList<Port> ports = new ArrayList<Port>();
        for (Frame frame : this.frames) {
            ports.addAll(frame.getPorts());
        }
        return ports;
    }

    public ArrayList<Path> getPaths() {
        ArrayList<Path> paths = new ArrayList<Path>();
        for (Frame frame : this.frames) {
            for (Port port: frame.getPorts()) {
                paths.addAll(port.getPaths());
            }
        }
        return paths;
    }

    public void addPeripheral(Peripheral peripheral) {
        this.peripherals.add(peripheral);
    }

    public Peripheral getPeripheral(int index) {
        return this.peripherals.get(index);
    }

    public ArrayList<Peripheral> getPeripherals() {
        return this.peripherals;
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
