package camp.computer.clay.engine.component;

import java.util.TimerTask;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.entity.Entity;

public class Timer extends Component {

    public enum State {
        WAITING,
        RUNNING,
        COMPLETE
    }

    public State state = State.WAITING;

    // public long startTime = 0L;
    public long timeout = 0L;

    public Timer() {
        super();
    }

    public void onTimeout(final Entity notification) {
        new java.util.Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                notification.getComponent(Timer.class).state = State.COMPLETE;
                notification.isActive = false;

                // TODO: entities.requestDelete(...)
                World.getWorld().entities.remove(notification);
            }
        }, notification.getComponent(Timer.class).timeout);
    }
}
