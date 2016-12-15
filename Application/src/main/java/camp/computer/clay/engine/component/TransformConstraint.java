package camp.computer.clay.engine.component;

import camp.computer.clay.engine.entity.Entity;

public class TransformConstraint extends Component {

    // TODO: Rename to TransformConstraint?
    // TODO: Don't implement as Component? Create separate system and totally decouple constraints?

    // TODO: 11/12/2016 public Type type = RELATIVE_LAYOUT;
    /**
     * The Entity with this component will be drawn relative to the reference Entity's Transform
     * component.
     */
    private Entity referenceEntity;

    public Transform relativeTransform;

    public TransformConstraint() {
        setup();
    }

    private void setup() {
        relativeTransform = new Transform();
    }

    public Entity getReferenceEntity() {
        return referenceEntity;
    }

    public void setReferenceEntity(Entity entity) {
        this.referenceEntity = entity;
    }
}
