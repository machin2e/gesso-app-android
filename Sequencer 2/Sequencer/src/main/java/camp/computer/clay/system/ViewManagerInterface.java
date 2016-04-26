package camp.computer.clay.system;

public interface ViewManagerInterface {

    public void setClay(Clay clay);

    public Clay getClay();

    /**
     * Defines a timeline view and associated data structures for the specified device.
     * @param device
     */
    public void addUnitView(Device device);

    public void refreshListViewFromData(Device device);

    // TODO: removeTimelineView (Device unit);
    // TODO: hideTimelineView (Device unit);
}
