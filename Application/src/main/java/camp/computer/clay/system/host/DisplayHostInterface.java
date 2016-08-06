package camp.computer.clay.system.host;

import camp.computer.clay.system.Clay;
import camp.computer.clay.system.old_model.Device;

public interface DisplayHostInterface {

    public void setClay(Clay clay);

    public Clay getClay();

    /**
     * Defines a timeline view and associated data structures for the specified device.
     * @param device
     */
    public void addDeviceView(Device device);

    public void refreshListViewFromData(Device device);

    // TODO: removeTimelineView (Patch unit);
    // TODO: hideTimelineView (Patch unit);
}
