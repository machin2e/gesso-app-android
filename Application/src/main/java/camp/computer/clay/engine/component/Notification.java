package camp.computer.clay.engine.component;

import camp.computer.clay.engine.system.RenderSystem;

public class Notification extends Component {

    // <TODO:REPLACE_WITH_TIMING_BASED_DESTRUCTION_WITHOUT_STATE>
    public enum State {
        WAITING,
        RUNNING,
        COMPLETE
    }
    // </TODO:REPLACE_WITH_TIMING_BASED_DESTRUCTION_WITHOUT_STATE>

    public String message = "notification!!!!!";

    public long timeout = 1000;

    public RenderSystem.State state = RenderSystem.State.WAITING;

    public Notification() {
        super();
    }
}
