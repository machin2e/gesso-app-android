package camp.computer.clay.system.old_model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Descriptor is a data structure for modeling and representing structured information. It can be used
 * with Message, which can serialize and propagate it over a network, Cache, and Store.
 */
public class Descriptor {

    private UUID uuid;

    private String label;
    private String description;

    private List<String> descriptionRange = null;

    private Descriptor parent = null;

    private List<Descriptor> children = new ArrayList<>();

    private List<OnDescriptorUpdateListener> onDescriptorUpdateListeners;

    /**
     * The depth of the descriptor in the containing descriptor tree. Root descriptors have depth 0.
     */
    private int depth = 0;

    private boolean isList = false;

    private Descriptor listChoice = null;

    public Descriptor(String label, String description) {

        this.uuid = UUID.randomUUID();

        this.label = label;

        this.onDescriptorUpdateListeners = new ArrayList<>();

        this.setDescription(description);
    }

    public Descriptor(String label) {
        this(label, null);
    }

    // TODO: diff detection to generate events.
    // TODO: apply a copy of this tree, accepted as input (e.g., for an event), and compute changes to apply to make changes to this tree based on others

    // TODO: Linked data. Support it. Consider using @ prefix. Research JSON-LD and RDFa.
    // TODO: Semantic relationships between entries. Add them.
    // TODO: State synchronization. Add it to support distributed data structure. (a la transclusion or aliasing)
    // TODO: Aliasing. Add support.
    // TODO: Add "type" field.
    // TODO: Tags. Add support for tagging entries.

    // Concepts: embed, map, nesting, object, property, has, concept, actions (schema that does things, micro-program)

    public void setLabel(String label) {
        this.label = label;
    }

    public List<String> getLabels() {
        List<String> labels = new ArrayList<>();
        for (Descriptor child : children) {
            labels.add(child.getLabel());
        }
        return labels;
    }

    public String getLabel() {
        return this.label;
    }

    public void setDescription(String description) {
        setDescription(description, true);
    }

    public void setDescription(String description, boolean notifySubscribers) {
        this.description = description;

        // Notify observers, "peer datas'" observers, and children's observers (in data hierarchy)
        if (notifySubscribers) {
            notifyDescriptorTree();
        }
    }

    // The callback interface
    public interface OnDescriptorUpdateListener {
        void notifyDescriptorChanged();
    }

    public void removeOnContentChangeListener(OnDescriptorUpdateListener onDescriptorUpdateListener) {
        if (this.onDescriptorUpdateListeners.contains(onDescriptorUpdateListener)) {
            this.onDescriptorUpdateListeners.remove(onDescriptorUpdateListener);
        }
    }

    public void addOnContentChangeListener(OnDescriptorUpdateListener onDescriptorUpdateListener) {
        if (!this.onDescriptorUpdateListeners.contains(onDescriptorUpdateListener)) {
            this.onDescriptorUpdateListeners.add(onDescriptorUpdateListener);
        }
    }

    // TODO: store

    /**
     * Notifies the parent of descriptor {@code descriptor}.
     */
    private void notifyParent(Descriptor descriptor) {
        if (this.parent != null) {
            Log.v("Content_Tree", "notifyParent");
            for (OnDescriptorUpdateListener onDescriptorUpdateListener : this.parent.onDescriptorUpdateListeners) {
                onDescriptorUpdateListener.notifyDescriptorChanged();
            }

            // Notify parents recursively until encountering a list or tree root (null).
            if (!this.parent.isList) {
                this.parent.notifyParent(descriptor);
            }
        }
    }

    /**
     * Recursively notify the parents (until root or list), siblings, and siblings' children of
     * an event.
     */
    private void notifyDescriptorTree() {

        Log.v("Content_Update", "notify: " + this.getLabel() + ", " + this.getDescription());

        Log.v("Content_Tree", "Descriptor Tree:");
        this.notifyParent(this);
        this.updateChildrenDescriptions();
        for (Descriptor sibling : this.siblings()) {
            Log.v("Content_Tree", "\t" + sibling.getLabel() + " -> " + sibling.getDescription());
            sibling.updateChildrenDescriptions();
        }
    }

    private void updateChildrenDescriptions() {

        // Notify listeners (via list of callbacks)
        for (OnDescriptorUpdateListener onDescriptorUpdateListener : this.onDescriptorUpdateListeners) {
            onDescriptorUpdateListener.notifyDescriptorChanged();
        }

        // Notify children
        for (Descriptor child : this.getChildren()) {
            Log.v("Content_Tree", "\t\t" + child.getLabel() + " -> " + child.getDescription());
            Log.v("Content_Tree", "\t\t\t|children|:" + child.children.size());
            child.updateChildrenDescriptions();
        }
    }

    //            //
    // Navigation //
    //            //

    public Descriptor parent() {
        return this.parent;
    }

    public List<Descriptor> siblings() {
        List<Descriptor> siblings = new ArrayList<>();
        if (parent != null) {
            for (Descriptor descriptor : this.parent.getChildren()) {
                if (descriptor != this) {
                    siblings.add(descriptor);
                }
            }
        }
        return siblings;
    }

    //            //
    // Operations //
    //            //

    public Descriptor set(String description) {
        return this.set(description, true);
    }

    public Descriptor set(String description, boolean notifyDescriptorTree) {
        if ((this.descriptionRange == null) || (this.descriptionRange != null && this.descriptionRange.contains(description))) {
            Log.v("Descriptor", "set '" + this.label + "' to '" + description + "'");
            if (this.isList) {
                Log.v("Content_Decision_List", "LIST");

                // Update listChoice
                for (Descriptor childEntry : this.getChildren()) {
                    if (childEntry.contains("number")) {
                        if (childEntry.get("number").getDescription().equals(description)) {
                            this.listChoice = childEntry;
                            break;
                        }
                    }
                }

                if (listChoice != null) {
                    //this.listChoice.setDescription(description, notifyDescriptorTree);
                    this.setDescription(description, notifyDescriptorTree);
                }
            } else {
                this.setDescription(description, notifyDescriptorTree);
            }
        }
        return this;
    }

    //               //
    // Data Exchange //
    //               //

    public String getDescription() {
        return this.description;
    }

    //                       //
    // Structure Description //
    //                       //

    public List<String> getDescriptionRange() {
        return this.descriptionRange;
    }

    public void setDescriptionRange(List<String> descriptionRange) {
        this.descriptionRange = descriptionRange;
    }

    public Descriptor from(List<String> descriptors) {
        this.setDescriptionRange(descriptors);
        return this;
    }

    // e.g., data.put("type").from("switch", "wave", "pulse").case(directionEntry, "input", "pulse").case(directionEntry, "output", "wave")
    // pop-up chat with swipe left and right
    // small arrow centered above and below description value selectors
    public Descriptor from(String... descriptions) {
        List<String> descriptors = new ArrayList<>();
        for (int i = 0; i < descriptions.length; i++) {
            descriptors.add(descriptions[i]);
        }
        this.from(descriptors);
        return this;
    }

    public List<Descriptor> getChildren() {
        return this.children;
    }

    public Descriptor get(String label) {
        for (Descriptor descriptor : this.children) {
            if (descriptor.getLabel().equals(label)) {
                Log.v("Descriptor", "get '" + label + "'");
                return descriptor;
            }
        }
        Log.v("Descriptor", "failed to get '" + label + "'");
        return null;
    }

    public Descriptor choice() {
        if (this.isList) {
            return this.listChoice;
        } else {
            return this;
        }
    }

    public boolean contains(String label) {
        for (Descriptor descriptor : this.children) {
            if (descriptor.getLabel().equals(label)) {
                return true;
            }
        }
        return false;
    }

    public Descriptor put(String label) {
        return put(label, null);
    }

    public Descriptor list(String label) {
        Descriptor descriptor = put(label);
        descriptor.isList = true;
        return descriptor;
    }

    public Descriptor put(String label, String description) {
        if (description == null) {
            Log.v("Descriptor", "set '" + label);
        } else {
            Log.v("Descriptor", "set '" + label + "' to '" + description + "'");
        }

        Descriptor descriptorEntry = this.get(label);
        if (descriptorEntry == null) {
            descriptorEntry = new Descriptor(label, description);
            this.addChild(descriptorEntry);

            if (this.isList) {
                if (this.listChoice == null) {
                    this.listChoice = descriptorEntry;
                }
            }
        } else {
            if ((descriptorEntry.descriptionRange == null) || (descriptorEntry.descriptionRange != null && descriptorEntry.descriptionRange.contains(description))) {
                descriptorEntry.setDescription(description);
            }
        }
        return descriptorEntry;
    }

    public void addChild(Descriptor descriptor) {
        this.children.add(descriptor);
        descriptor.parent = this;
        descriptor.depth = this.depth + 1;
    }

    public int getDepth() {
        return this.depth;
    }
}
