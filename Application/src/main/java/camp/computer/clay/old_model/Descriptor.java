package camp.computer.clay.old_model;

import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Descriptor is a data structure for modeling and representing structured information. It can be used
 * with Message, which can serialize and propagate it over a network, Cache, and Store.
 */
public class Descriptor {

    private UUID uuid;

    private String label;
    private String description;

    private ArrayList<String> descriptionRange = null;

    private Descriptor parent;

    private ArrayList<Descriptor> children;

    private ArrayList<OnDescriptorUpdateListener> onDescriptorUpdateListeners;

    /**
     * The depth of the descriptor in the containing descriptor tree. Root descriptors have depth 0.
     */
    private int depth;

    private boolean isList;
    private Descriptor listChoice;

    public Descriptor(String label, String description) {

        this.uuid = UUID.randomUUID();

        this.label = label;

        this.isList = false;
        this.listChoice = null;

        this.depth = 0;

        this.parent = null;
        this.children = new ArrayList<Descriptor>();

        this.onDescriptorUpdateListeners = new ArrayList<OnDescriptorUpdateListener>();

        this.setDescription(description);
    }

    public Descriptor(String label) {
        this(label, null);
    }

    // TODO: diff detection to generate events.
    // TODO: processAction a copy of this tree, accepted as input (e.g., for an event), and compute changes to processAction to make changes to this tree based on others

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

    public ArrayList<String> getKeys () {
        ArrayList<String> keys = new ArrayList<String>();
        for (Descriptor child : children) {
            keys.add (child.getLabel());
        }
        return keys;
    }

    public String getLabel() {
        return this.label;
    }

    public void setDescription(String description) {
        setContent (description, true);
    }

    public void setContent (String content, boolean notifyContentTree) {
        this.description = content;

        // Notify observers, "peer datas'" observers, and children's observers (in data hierarchy)
        if (notifyContentTree) {
            this.notifyContentTree();
        }
    }

    // The callback interface
    public interface OnDescriptorUpdateListener {
        void notifyContentChanged ();
    }

    public void removeOnContentChangeListener (OnDescriptorUpdateListener onDescriptorUpdateListener) {
        if (this.onDescriptorUpdateListeners.contains(onDescriptorUpdateListener)) {
            this.onDescriptorUpdateListeners.remove(onDescriptorUpdateListener);
        }
    }

    public void addOnContentChangeListener (OnDescriptorUpdateListener onDescriptorUpdateListener) {
        if (!this.onDescriptorUpdateListeners.contains(onDescriptorUpdateListener)) {
            this.onDescriptorUpdateListeners.add(onDescriptorUpdateListener);
        }
    }

    // TODO: store

    private void notifyParent(Descriptor notifySource) {
        if (this.parent != null) {
            Log.v ("Content_Tree", "notifyParent");
            for (OnDescriptorUpdateListener onDescriptorUpdateListener : this.parent.onDescriptorUpdateListeners) {
                onDescriptorUpdateListener.notifyContentChanged();
            }

            // Notify parents recursively until encountering a list or tree root (null).
            if (!this.parent.isList) {
                this.parent.notifyParent(notifySource);
            }
        }
    }

    /**
     * Recursively notify the parents (until root or list), siblings, and siblings' children of
     * an event.
     */
    private void notifyContentTree() {

        Log.v("Content_Update", "notify: " + this.getLabel() + ", " + this.getDescription());

        Log.v("Content_Tree", "Descriptor Tree:");
        this.notifyParent(this);
        this.updateChildrenContent();
        for (Descriptor sibling : this.siblings ()) {
            Log.v ("Content_Tree", "\t" + sibling.getLabel() + " -> " + sibling.getDescription());
            sibling.updateChildrenContent();
        }
    }

    private void updateChildrenContent () {

        // Notify listeners (via list of callbacks)
        for (OnDescriptorUpdateListener onDescriptorUpdateListener : this.onDescriptorUpdateListeners) {
            onDescriptorUpdateListener.notifyContentChanged();
        }

        // Notify children
        for (Descriptor child : this.getChildren()) {
            Log.v ("Content_Tree", "\t\t" + child.getLabel() + " -> " + child.getDescription());
            Log.v("Content_Tree", "\t\t\t|children|:" + child.children.size());
            child.updateChildrenContent();
        }
    }

    //            //
    // Navigation //
    //            //

    public Descriptor parent () {
        return this.parent;
    }

    public ArrayList<Descriptor> siblings () {
        ArrayList<Descriptor> siblings = new ArrayList<Descriptor>();
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

    public Descriptor set (String content) {
        return this.set (content, true);
    }

    public Descriptor set (String content, boolean notifyContentTree) {
        if ((this.descriptionRange == null) || (this.descriptionRange != null && this.descriptionRange.contains(content))) {
            Log.v("Descriptor", "setValue '" + this.label + "' to '" + content + "'");
            if (this.isList) {
                Log.v("Content_Decision_List", "LIST");

                // Update listChoice
                for (Descriptor childEntry : this.getChildren()) {
                    if (childEntry.contains("number")) {
                        if (childEntry.get("number").getDescription().equals(content)) {
                            this.listChoice = childEntry;
                            break;
                        }
                    }
                }

                if (listChoice != null) {
                    //this.listChoice.setDescription(description, notifyContentTree);
                    this.setContent(content, notifyContentTree);
                }
            } else {
                this.setContent(content, notifyContentTree);
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

    public ArrayList<String> getDescriptionRange() {
        return this.descriptionRange;
    }

    public void setDescriptionRange(ArrayList<String> descriptionRange) {
        this.descriptionRange = descriptionRange;
    }

    public Descriptor from(ArrayList<String> contentRange) {
        this.setDescriptionRange(contentRange);
        return this;
    }

    // e.g., data.put("type").from("switch", "wave", "pulse").case(directionEntry, "input", "pulse").case(directionEntry, "output", "wave")
    // pop-up chat with swipe left and right
    // small arrow centered above and below description value selectors
    public Descriptor from(String... contentRangeValues) {
        ArrayList<String> contentRange = new ArrayList<String>();
        for (int i = 0; i < contentRangeValues.length; i++) {
            contentRange.add(contentRangeValues[i]);
        }
        this.from(contentRange);
        return this;
    }

    public ArrayList<Descriptor> getChildren () {
        return this.children;
    }

    public Descriptor get (String key) {
        for (Descriptor descriptor : this.children) {
            if (descriptor.getLabel().equals(key)) {
                Log.v ("Descriptor", "getEvent '" + key + "'");
                return descriptor;
            }
        }
        Log.v ("Descriptor", "failed to getEvent '" + key + "'");
        return null;
    }

    public Descriptor choice () {
        if (this.isList) {
            return this.listChoice;
        } else {
            return this;
        }
    }

    public boolean contains (String key) {
        for (Descriptor descriptor : this.children) {
            if (descriptor.getLabel().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public Descriptor put (String key) {
        return put (key, null);
    }

    public Descriptor list (String key) {
        Descriptor descriptor = put (key);
        descriptor.isList = true;
        return descriptor;
    }

    public Descriptor put (String key, String content) {
        if (content == null) {
            Log.v("Descriptor", "setValue '" + key);
        } else {
            Log.v("Descriptor", "setValue '" + key + "' to '" + content + "'");
        }

        Descriptor descriptorEntry = this.get(key);
        if (descriptorEntry == null) {
            descriptorEntry = new Descriptor(key, content);
            this.addChild(descriptorEntry);

            if (this.isList) {
                if (this.listChoice == null) {
                    this.listChoice = descriptorEntry;
                }
            }
        } else {
            if ((descriptorEntry.descriptionRange == null) || (descriptorEntry.descriptionRange != null && descriptorEntry.descriptionRange.contains(content))) {
                descriptorEntry.setDescription(content);
            }
        }
        return descriptorEntry;
    }

    public void addChild (Descriptor descriptor) {
        this.children.add(descriptor);
        descriptor.parent = this;
        descriptor.depth = this.depth + 1;
    }

    public int getDepth () {
        return this.depth;
    }
}
