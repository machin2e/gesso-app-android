package camp.computer.clay.engine;

import java.util.ArrayList;
import java.util.List;

public class Timer {

    private List<Schedule> schedules = new ArrayList<>();

    public void update(long time) {
        for (int i = 0; i < schedules.size(); i++) {
            Schedule schedule = schedules.get(i);
            if (time - schedule.previousTickTime >= schedule.tickFrequency) {
                schedule.previousTickTime = time;
                schedule.execute(time - schedule.previousTickTime);
            }
        }
    }

    public void add(Schedule schedule) {
        this.schedules.add(schedule);
    }
}
