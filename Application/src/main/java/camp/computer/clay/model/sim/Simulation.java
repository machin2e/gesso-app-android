package camp.computer.clay.model.sim;

import java.util.ArrayList;
import java.util.List;

public class Simulation extends Model {

    private List<Body> bodies = new ArrayList<>();

    private List<Frame> frames = new ArrayList<>();

    public void addFrame(Frame frame) {
        this.frames.add(frame);
    }

    public Frame getForm(int index) {
        return this.frames.get(index);
    }

    public List<Frame> getFrames() {
        return this.frames;
    }

    public List<Port> getPorts() {
        List<Port> ports = new ArrayList<Port>();
        for (Frame frame : this.frames) {
            ports.addAll(frame.getPorts());
        }
        return ports;
    }

    public List<Path> getPaths() {
        List<Path> paths = new ArrayList<Path>();
        for (Frame frame : this.frames) {
            for (Port port : frame.getPorts()) {
                paths.addAll(port.getPaths());
            }
        }
        return paths;
    }

    public void addBody(Body body) {
        this.bodies.add(body);
    }

    public Body getBody(int index) {
        return this.bodies.get(index);
    }

    public List<Body> getBodies() {
        return this.bodies;
    }
}
