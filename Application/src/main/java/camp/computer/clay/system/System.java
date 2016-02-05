package camp.computer.clay.system;

import java.util.ArrayList;

public class System {

    private Clay clay = null;

    private ArrayList<Timeline> loops = new ArrayList<Timeline>();

    public System(Clay clay) {
        super();

        this.clay = clay;
    }

    public Clay getClay () {
        return this.clay;
    }

    public void addLoop (Timeline loop) {
        this.loops.add(loop);
    }

    public ArrayList<Timeline> getLoops () {
        return this.loops;
    }
}
