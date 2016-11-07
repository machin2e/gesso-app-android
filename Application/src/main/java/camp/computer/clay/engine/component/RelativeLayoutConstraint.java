package camp.computer.clay.engine.component;

import java.util.UUID;

import camp.computer.clay.engine.entity.Entity;

public class RelativeLayoutConstraint extends Component {

    /**
     * The Entity with this component will be drawn relative to the reference Entity's Transform
     * component.
     */
    private UUID entityUuid;

    public Entity getReferenceEntity() {
        return Entity.Manager.get(entityUuid);
    }

    public void setReferenceEntity(Entity entity) {
        this.entityUuid = entity.getUuid();
    }
}
