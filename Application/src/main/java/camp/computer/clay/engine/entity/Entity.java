package camp.computer.clay.engine.entity;

import camp.computer.clay.engine.component.Component;
import camp.computer.clay.engine.manager.EntityManager;
import camp.computer.clay.engine.manager.Group;

public final class Entity {

    // <HACK>
    public long uid = EntityManager.INVALID_UUID; // = EntityManager.entityCounter++; // entityManager.INVALID_UID;
    // </HACK>

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public boolean isActive = false;
    public boolean isDestroyable = false;

    private Group<Component> components = new Group<>();

    public <C extends Component> void addComponent(C component) {
        component.setEntity(this); // Associate Component with Entity
        components.add(component); // Add to Entity
    }

    public <C extends Component> C getComponent(Class<C> type) {
        for (int i = 0; i < components.size(); i++) {
            if (components.get(i).getClass() == type) {
                return type.cast(components.get(i));
            }
        }
        return null;
    }

    public Group<Component> getComponents() {
        return new Group<>(components);
    }

    public boolean hasComponent(Class<? extends Component> type) {
        return getComponent(type) != null;
    }

    public boolean hasComponents(Class<? extends Component>... types) {
        for (int i = 0; i < types.length; i++) {
            if (!hasComponent(types[i])) {
                return false;
            }
        }
        return true;
    }

    public void removeComponents() {
        components.clear();
    }

    public <C extends Component> C removeComponent(Class<C> type) {
        C component = getComponent(type);
        if (component != null) {
            components.remove(component);
        }
        return component;
    }
}
