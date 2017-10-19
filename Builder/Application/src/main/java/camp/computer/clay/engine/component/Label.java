package camp.computer.clay.engine.component;

import camp.computer.clay.engine.entity.Entity;

public class Label extends Component {

    public String label = "";

    public Label() {
        super();
    }

    public static void setLabel(Entity entity, String label) {
        entity.getComponent(Label.class).label = label;
    }

    public static String getLabel(Entity entity) {
        return entity.getComponent(Label.class).label;
    }
}
