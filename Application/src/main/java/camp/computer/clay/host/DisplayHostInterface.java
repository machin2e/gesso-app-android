package camp.computer.clay.host;

import camp.computer.clay.Clay;
import camp.computer.clay.old_model.PhoneHost;

public interface DisplayHostInterface {

    public void setClay(Clay clay);

    public Clay getClay();

    /**
     * Defines a timeline view and associated data structures for the specified phoneHost.
     * @param phoneHost
     */
    public void addDeviceView(PhoneHost phoneHost);

    public void refreshListViewFromData(PhoneHost phoneHost);

    // TODO: removeTimelineView (Extension unit);
    // TODO: hideTimelineView (Extension unit);
}
