package camp.computer.clay.model;

import java.util.ArrayList;

public class Port extends Model {

    private ArrayList<Path> paths = new ArrayList<Path>();

    public void addPath(Path path) {
        this.paths.add(path);
    }

    public Path getPath(int index) {
        return this.paths.get(index);
    }

    public ArrayList<Path> getPaths() {
        return this.paths;
    }
}
