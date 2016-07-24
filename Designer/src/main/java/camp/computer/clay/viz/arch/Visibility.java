package camp.computer.clay.viz.arch;

public enum Visibility {

    INVISIBLE(0),
    VISIBLE(1);

    // TODO: Change the index to a UUID?
    int index;

    Visibility(int index) {
        this.index = index;
    }
}
