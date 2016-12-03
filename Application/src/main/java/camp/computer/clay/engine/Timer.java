package camp.computer.clay.engine;

import java.util.ArrayList;
import java.util.List;

public class Timer {

    private List<Schedule> schedules = new ArrayList<>();

    public void update(long time) {
        for (int i = 0; i < schedules.size(); i++) {
            Schedule schedule = schedules.get(i);
            long dt = time - schedule.previousTickTime;
            if (dt >= schedule.tickFrequency) {
                synchronized (this) {
                    schedule.previousTickTime = time;
                    schedule.execute((dt / Clock.NANOS_PER_MILLISECOND));
                }
            }
        }
    }

    public void add(Schedule schedule) {
        this.schedules.add(schedule);
    }
}
