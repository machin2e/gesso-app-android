package camp.computer.clay.system.old_model;

import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Content is a data structure for modeling and representing structured information. It can be used
 * with Message, which can serialize and propagate it over a network, Cache, and Store.
 */
public class Content {

    private UUID uuid;

    private String key;
    private String content;

    private ArrayList<String> contentRange = null;
    private ArrayList<Content> children;
    private Content parent;

    private ArrayList<OnContentChangeListener> onContentChangeListeners;

    private int depth;

    private boolean isList;
    private Content listChoice;

    public Content(String key, String content) {

        this.uuid = UUID.randomUUID();

        this.key = key;

        this.isList = false;
        this.listChoice = null;

        this.depth = 0;

        this.parent = null;
        this.children = new ArrayList<Content>();

        this.onContentChangeListeners = new ArrayList<OnContentChangeListener>();

        this.setContent(content);
    }

    public Content(String key) {
        this(key, null);
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

    public void setKey (String key) {
        this.key = key;
    }

    public ArrayList<String> getKeys () {
        ArrayList<String> keys = new ArrayList<String>();
        for (Content child : children) {
            keys.add (child.getKey());
        }
        return keys;
    }

    public String getKey () {
        return this.key;
    }

    public void setContent (String content) {
        setContent (content, true);
    }

    public void setContent (String content, boolean notifyContentTree) {
        this.content = content;

        // Notify observers, "peer datas'" observers, and children's observers (in data hierarchy)
        if (notifyContentTree) {
            this.notifyContentTree();
        }
    }

    // The callback interface
    public interface OnContentChangeListener {
        void notifyContentChanged ();
    }

    public void removeOnContentChangeListener (OnContentChangeListener onContentChangeListener) {
        if (this.onContentChangeListeners.contains(onContentChangeListener)) {
            this.onContentChangeListeners.remove(onContentChangeListener);
        }
    }

    public void addOnContentChangeListener (OnContentChangeListener onContentChangeListener) {
        if (!this.onContentChangeListeners.contains(onContentChangeListener)) {
            this.onContentChangeListeners.add(onContentChangeListener);
        }
    }

    // TODO: store

    private void notifyParent(Content notifySource) {
        if (this.parent != null) {
            Log.v ("Content_Tree", "notifyParent");
            for (OnContentChangeListener onContentChangeListener: this.parent.onContentChangeListeners) {
                onContentChangeListener.notifyContentChanged();
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

        Log.v("Content_Update", "notify: " + this.getKey() + ", " + this.getContent());

        Log.v("Content_Tree", "Content Tree:");
        this.notifyParent(this);
        this.updateChildrenContent();
        for (Content sibling : this.siblings ()) {
            Log.v ("Content_Tree", "\t" + sibling.getKey() + " -> " + sibling.getContent());
            sibling.updateChildrenContent();
        }
    }

    private void updateChildrenContent () {

        // Notify listeners (via list of callbacks)
        for (OnContentChangeListener onContentChangeListener: this.onContentChangeListeners) {
            onContentChangeListener.notifyContentChanged();
        }

        // Notify children
        for (Content child : this.getChildren()) {
            Log.v ("Content_Tree", "\t\t" + child.getKey() + " -> " + child.getContent());
            Log.v("Content_Tree", "\t\t\t|children|:" + child.children.size());
            child.updateChildrenContent();
        }
    }

    //            //
    // Navigation //
    //            //

    public Content parent () {
        return this.parent;
    }

    public ArrayList<Content> siblings () {
        ArrayList<Content> siblings = new ArrayList<Content>();
        if (parent != null) {
            for (Content content : this.parent.getChildren()) {
                if (content != this) {
                    siblings.add(content);
                }
            }
        }
        return siblings;
    }

    //            //
    // Operations //
    //            //

    public Content set (String content) {
        return this.set (content, true);
    }

    public Content set (String content, boolean notifyContentTree) {
        if ((this.contentRange == null) || (this.contentRange != null && this.contentRange.contains(content))) {
            Log.v("Content", "set '" + this.key + "' to '" + content + "'");
            if (this.isList) {
                Log.v("Content_Decision_List", "LIST");

                // Update listChoice
                for (Content childEntry : this.getChildren()) {
                    if (childEntry.contains("number")) {
                        if (childEntry.get("number").getContent().equals(content)) {
                            this.listChoice = childEntry;
                            break;
                        }
                    }
                }

                if (listChoice != null) {
                    //this.listChoice.setContent(content, notifyContentTree);
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

    public String getContent () {
        return this.content;
    }

    //                       //
    // Structure Description //
    //                       //

    public ArrayList<String> getContentRange () {
        return this.contentRange;
    }

    public void setContentRange(ArrayList<String> contentRange) {
        this.contentRange = contentRange;
    }

    public Content from(ArrayList<String> contentRange) {
        this.setContentRange(contentRange);
        return this;
    }

    // e.g., data.put("type").from("switch", "wave", "pulse").case(directionEntry, "input", "pulse").case(directionEntry, "output", "wave")
    // pop-up chat with swipe left and right
    // small arrow centered above and below content value selectors
    public Content from(String... contentRangeValues) {
        ArrayList<String> contentRange = new ArrayList<String>();
        for (int i = 0; i < contentRangeValues.length; i++) {
            contentRange.add(contentRangeValues[i]);
        }
        this.from(contentRange);
        return this;
    }

    public ArrayList<Content> getChildren () {
        return this.children;
    }

    public Content get (String key) {
        for (Content content : this.children) {
            if (content.getKey().equals(key)) {
                Log.v ("Content", "get '" + key + "'");
                return content;
            }
        }
        Log.v ("Content", "failed to get '" + key + "'");
        return null;
    }

    public Content choice () {
        if (this.isList) {
            return this.listChoice;
        } else {
            return this;
        }
    }

    public boolean contains (String key) {
        for (Content content : this.children) {
            if (content.getKey().equals(key)) {
                return true;
            }
        }
        return false;
    }

    public Content put (String key) {
        return put (key, null);
    }

    public Content list (String key) {
        Content content = put (key);
        content.isList = true;
        return content;
    }

    public Content put (String key, String content) {
        if (content == null) {
            Log.v("Content", "set '" + key);
        } else {
            Log.v("Content", "set '" + key + "' to '" + content + "'");
        }

        Content contentEntry = this.get(key);
        if (contentEntry == null) {
            contentEntry = new Content(key, content);
            this.addChild(contentEntry);

            if (this.isList) {
                if (this.listChoice == null) {
                    this.listChoice = contentEntry;
                }
            }
        } else {
            if ((contentEntry.contentRange == null) || (contentEntry.contentRange != null && contentEntry.contentRange.contains(content))) {
                contentEntry.setContent(content);
            }
        }
        return contentEntry;
    }

    public void addChild (Content content) {
        this.children.add(content);
        content.parent = this;
        content.depth = this.depth + 1;
    }

    public int getDepth () {
        return this.depth;
    }
}
