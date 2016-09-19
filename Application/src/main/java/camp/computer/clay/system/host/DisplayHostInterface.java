package camp.computer.clay.system.host;

import camp.computer.clay.system.Clay;
import camp.computer.clay.system.old_model.Host;

public interface DisplayHostInterface {

    public void setClay(Clay clay);

    public Clay getClay();

    /**
     * Defines a timeline view and associated data structures for the specified host.
     * @param host
     */
    public void addDeviceView(Host host);

    public void refreshListViewFromData(Host host);

    // TODO: removeTimelineView (Extension unit);
    // TODO: hideTimelineView (Extension unit);
}
