package camp.computer.clay.engine.component;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.entity.Entity;

public class RelativeLayoutConstraint extends Component {

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
