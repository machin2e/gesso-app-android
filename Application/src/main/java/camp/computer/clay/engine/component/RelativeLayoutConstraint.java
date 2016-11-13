package camp.computer.clay.engine.component;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.entity.Entity;

public class RelativeLayoutConstraint extends Component {

    // TODO: 11/12/2016 public Type type = RELATIVE_LAYOUT;

    /**
     * The Entity with this component will be drawn relative to the reference Entity's Transform
     * component.
     */
    private long entityUuid;

    public Entity getReferenceEntity() {
        return World.getWorld().Manager.getEntities().get(entityUuid);
    }

    public void setReferenceEntity(Entity entity) {
        this.entityUuid = entity.getUuid();
    }
}
