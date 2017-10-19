package camp.computer.clay.engine.component;

public class Extension extends Component {

    public Extension() {
        super();
    }

    // Flag indicating whether the Extension is stored in a remote database
    // TODO: Consider replacing with URI or UUID of stored representation.
    private boolean isPersistent = false;

    public boolean isPersistent() {
        return this.isPersistent;
    }

    public void setPersistent(boolean isPersistent) {
        this.isPersistent = isPersistent;
    }
}
