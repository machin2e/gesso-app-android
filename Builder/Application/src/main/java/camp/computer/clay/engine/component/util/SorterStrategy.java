package camp.computer.clay.engine.component.util;

public class SorterStrategy {
    private Sorter sorter;

    public SorterStrategy(Sorter sorter) {
        this.sorter = sorter;
    }

    public Sorter getSorter() {
        return sorter;
    }
}
