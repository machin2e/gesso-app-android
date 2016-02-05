package camp.computer.clay.system;

public interface ViewManagerInterface {

    public void setClay(Clay clay);

    public Clay getClay();

    /**
     * Defines a timeline view and associated data structures for the specified unit.
     * @param unit
     */
    public void addUnitView(Unit unit);
    // TODO: refreshTimelineView (Unit unit);
    // TODO: removeTimelineView (Unit unit);
    // TODO: hideTimelineView (Unit unit);
}
