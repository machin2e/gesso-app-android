package camp.computer.clay.sequencer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.sequencer.R;

import java.util.ArrayList;
import java.util.UUID;

import camp.computer.clay.system.*;
import camp.computer.clay.system.Event;

public class TimelineListView extends DragSortListView {

    private static final boolean HIDE_LIST_ITEM_SEPARATOR = true;

    private Unit unit;

    /**
     * List of event holders that hold events on the timeline. This has to be repopulated on
     * initialization.
     */
    private ArrayList<EventHolder> eventHolders;

    private EventHolderAdapter adapter;

    private EventDesignerView eventDesignerView;

    public TimelineListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * Initialize the ListView.
     */
    public void init()
    {
        initLayout();

        initData();

        // Set up the eventHolders adaptor
//        this.adapter = new EventHolderAdapter(getContext(), R.layout.list_item_handle_right, this.eventHolders);
        this.adapter = new EventHolderAdapter(getContext(), R.layout.list_item_type_light, this.eventHolders);
        setAdapter(adapter);

        // Set up gesture recognition
        initTouchListeners();

        this.setDropListener(onDrop);
        this.setRemoveListener(onRemove);
        this.setDragScrollProfile(ssProfile);
    }

    public void setDragListeners () {
        this.setDropListener(onDrop);
        this.setRemoveListener(onRemove);
        this.setDragScrollProfile(ssProfile);
    }

    private DragSortListView.DropListener onDrop =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int from, int to) {
                    //String item=adapter.getItem(from);
                    EventHolder item = (EventHolder) adapter.getItem (from);

                    adapter.notifyDataSetChanged();
                    adapter.remove(item);
                    adapter.insert(item, to);

                    Log.v("move", "move from " + from + " to " + to);

                    // Update the event with the new behavior and state
                    camp.computer.clay.system.Event event = item.getEvent();
                    if (event != null) {

                        // <HACK>
                        // TODO: Make this list update AFTER the data model. Basically update the view, but do all changes to OM first.
//                        getClay().getStore().removeEvent(eventHolder.getEvent(), null);
                        getUnit().getTimeline().removeEvent(event); // if store behavior successful
                        // </HACK>

                        // Update state of the object associated with the selected view.
//                        eventHolders.remove(eventHolder);
                    }

                    // Create event object
//                    Timeline timeline = getUnit().getTimeline();
//                    event = new Event(timeline, behavior);
//        event.getBehavior().setState(behaviorState);

                    // Object: Add event to timeline
                    getUnit().getTimeline().getEvents().add(to, event); // if store event was successful

                    // Store: Update timeline indices
                    getClay().getStore().storeTimeline(getUnit().getTimeline());
                }
            };

    private DragSortListView.RemoveListener onRemove =
            new DragSortListView.RemoveListener() {
                @Override
                public void remove(int which) {
                    EventHolder eventHolder = (EventHolder) adapter.getItem (which);

                    adapter.remove(eventHolder);

                    removeEventHolder(eventHolder);
                }
            };

    private DragSortListView.DragScrollProfile ssProfile =
            new DragSortListView.DragScrollProfile() {
                @Override
                public float getSpeed(float w, long t) {
                    if (w > 0.8f) {
                        // Traverse all views in a millisecond
                        return ((float) adapter.getCount()) / 0.001f;
                    } else {
                        return 10.0f * w;
                    }
                }
            };



    private void createTimelineEvents(Timeline timeline) {

        // Create a behavior profile for each of the unit's behaviors
        for (camp.computer.clay.system.Event event : timeline.getEvents()) {
            EventHolder eventHolder = new EventHolder(event);
            eventHolders.add(eventHolder);
        }
    }

    public void setEventHolders(Timeline timeline) {

        // Clear the timeline view
        this.eventHolders.clear();
        this.adapter.notifyDataSetChanged();

        // Create and addUnit eventHolders for each behavior
        createTimelineEvents (timeline);

        // Add "create" option
        this.eventHolders.add(new EventHolder("create", "", EventHolderAdapter.SYSTEM_CONTROL_LAYOUT));

        // Add "update" firmware option
        // TODO: Conditionally show this, only if firmware update is available
        // this.eventHolders.addUnit(new EventHolder("update firmware", "", EventHolderAdapter.SYSTEM_CONTROL_LAYOUT));

        this.adapter.notifyDataSetChanged();
    }

    private void initTouchListeners() {
        // Note: Using the following prevents the drag-and-drop functionality of the ListView.
        // setOnTouchListener(new EventHolderTouchListener());
        setOnItemClickListener(new EventHolderTouchReleaseListener());
        setOnItemLongClickListener(new EventHolderLongTouchListener());
        // setOnDragListener(new EventHolderTouchDragListener());
    }

    /**
     * Set up the eventHolders source and populate the list of eventHolders to show in this ListView.
     *
     * TODO: Initialize eventHolders from cache or from remote source in this function.
     * TODO:    Do this because ViewPager will destroy this object when moving between pages.
     * TODO: Observe remote eventHolders source and update cached source and notify ListView...
     * TODO: ...when eventHolders set changes.
     */
    public void initData () {

        this.eventHolders = new ArrayList<EventHolder>();
    }

    private void initLayout() {
        if (TimelineListView.HIDE_LIST_ITEM_SEPARATOR) {
            setDivider(null);
            setDividerHeight(0);
            // setBackgroundColor(Color.BLACK);
        }
    }

    /**
     * Returns the list item corresponding to the specified position.
     * @param x
     * @param y
     * @return
     */
    public EventHolder getListItemAtPosition(int x, int y) {
        // Get the list item corresponding to the specified touch point
        int position = getViewIndexByPosition(x, y);
        EventHolder item = (EventHolder) getItemAtPosition(position);
        return item;
    }

    public View getViewByPosition (int xPosition, int yPosition) {
        View mDownView = null;
        // Find the child view that was touched (perform a hit test)
        Rect rect = new Rect();
        int childCount = this.getChildCount();
        int[] listViewCoords = new int[2];
        this.getLocationOnScreen(listViewCoords);
        int x = (int) xPosition - listViewCoords[0];
        int y = (int) yPosition - listViewCoords[1];
        View child;
        int i = 0;
        for ( ; i < childCount; i++) {
            child = this.getChildAt(i);
            child.getHitRect(rect);
            if (rect.contains(x, y)) {
                mDownView = child; // This is your down view
                break;
            }
        }

        return mDownView;
    }

    public int getViewIndexByPosition (int xPosition, int yPosition) {
        View mDownView = null;
        // Find the child view that was touched (perform a hit test)
        Rect rect = new Rect();
        int childCount = this.getChildCount();
        int[] listViewCoords = new int[2];
        this.getLocationOnScreen(listViewCoords);
        int x = (int) xPosition - listViewCoords[0];
        int y = (int) yPosition - listViewCoords[1];
        View child;
        int i = 0;
        for ( ; i < childCount; i++) {
            child = this.getChildAt(i);
            child.getHitRect(rect);
            if (rect.contains(x, y)) {
                mDownView = child; // This is your down view
                break;
            }
        }

        // Check if the specified position is within the bounds of a view in the ListView.
        // If so, select the item.
        if (mDownView != null) {
            int itemIndex = this.getFirstVisiblePosition() + i;
            return itemIndex;
        }

        return -1;
    }

    public void selectItemByIndex (int index) {

        int firstSelectedIndex = -1;
        for (int i = 0; i < eventHolders.size(); i++) {
            EventHolder eventHolder = eventHolders.get(i);
            if (eventHolder.isSelected()) {
                firstSelectedIndex = i;
                break;
            }
        }

        // Check if the specified position is within the bounds of a view in the ListView.
        // If so, select the item.

        if (firstSelectedIndex == -1) {

            // The item is the first one selected
            if (index < eventHolders.size()) {
                EventHolder eventHolder = (EventHolder) eventHolders.get(index);
                selectEventHolder(eventHolder);
                refreshListViewFromData();
            }

        } else {

            // The selected item is subsequent to the first selected, so select it.
            if (firstSelectedIndex <= index) {

                // Select all items between the first and current selection
                for (int i = firstSelectedIndex; i <= index; i++) {
                    EventHolder eventHolder = eventHolders.get(i);
                    selectEventHolder(eventHolder);
                }
                // Deselect all items after the current selection
                for (int i = index + 1; i < eventHolders.size(); i++) {
                    EventHolder eventHolder = eventHolders.get(i);
                    deselectEventHolder(eventHolder);
                }
                refreshListViewFromData();

            }

            // TODO: Handle upward selection case here!

        }
    }

    private void selectEventHolder(final EventHolder eventHolder) {

        // Do not select system controllers
//        if (item.type == EventHolderAdapter.SYSTEM_CONTROL_LAYOUT || item.type == EventHolderAdapter.CONTROL_PLACEHOLDER_LAYOUT) {
//            item.selected = false;
//            return;
//        }

        // Update state of the object associated with the selected view.
        if (eventHolder.isSelected() == false) {
            eventHolder.setSelected(true);
        }

    }

    private void deselectEventHolder(final EventHolder eventHolder) {

        // Update state of the object associated with the selected view.
        if (eventHolder.isSelected() == true) {
            eventHolder.setSelected(false);
        }

    }

    /**
     * Add eventHolders to the ListView.
     *
     * @param event
     */
    private void addData (EventHolder event) {
        if (adapter != null) {
            eventHolders.add(eventHolders.size() - 1, event);
            refreshListViewFromData();
        }
    }

    /**
     * Refresh the entire ListView from the eventHolders.
     */
    public void refreshListViewFromData() {
        // TODO: Perform callbacks into eventHolders model to propagate changes based on view state and eventHolders item state.
        adapter.notifyDataSetChanged();
    }

    private void displayEventDesigner(final EventHolder event) {
        int basicBehaviorCount = 3;
        final String[] behaviorOptions = new String[basicBehaviorCount];

        // TODO: loop, condition, branch
        behaviorOptions[0] = "update";
        behaviorOptions[1] = "delete";
        behaviorOptions[2] = "replace";

        // Show the list of behaviors
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Behavior options");
        builder.setItems(behaviorOptions, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int itemIndex) {

                if (behaviorOptions[itemIndex].toString().equals("delete")) {

                    removeEventHolder(event);

                } else if (behaviorOptions[itemIndex].toString().equals("update")) {

                    displayUpdateOptions(event);

                } else if (behaviorOptions[itemIndex].toString().equals("replace")) {

                    displayBehaviorFinder(event);

                }

                refreshListViewFromData();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void displayUpdateOptions (final EventHolder eventHolder) {

        displayDesignerView(eventHolder);

        if (eventHolder.type == EventHolderAdapter.LIGHT_CONTROL_LAYOUT) {
            eventDesignerView.displayUpdateLightsOptions(eventHolder);
        } else if (eventHolder.type == EventHolderAdapter.IO_CONTROL_LAYOUT) {
            eventDesignerView.displayUpdateIOOptions(eventHolder);
        } else if (eventHolder.type == EventHolderAdapter.MESSAGE_CONTROL_LAYOUT) {
            eventDesignerView.displayUpdateMessageOptions(eventHolder);
        } else if (eventHolder.type == EventHolderAdapter.WAIT_CONTROL_LAYOUT) {
            eventDesignerView.displayUpdateWaitOptions(eventHolder);
        } else if (eventHolder.type == EventHolderAdapter.SAY_CONTROL_LAYOUT) {
            eventDesignerView.displayUpdateSayOptions(eventHolder);
        } else if (eventHolder.type == EventHolderAdapter.COMPLEX_LAYOUT) {
            eventDesignerView.displayUpdateTagOptions(eventHolder);
        }

    }

    private void removeEventHolder(final EventHolder eventHolder) {

        // <HACK>
        // TODO: Make this list update AFTER the data model. Basically update the view, but do all changes to OM first.
        getClay().getStore().removeEvent(eventHolder.getEvent(), null);
        getUnit().getTimeline().removeEvent(eventHolder.getEvent()); // if store behavior successful
        // </HACK>

        // Update state of the object associated with the selected view.
        eventHolders.remove(eventHolder);

        // Update the view after removing the specified list item
        refreshListViewFromData();

        /*
        // <HACK>
        getClay().getStore().updateTimeline(this.unit.getTimeline());
        // </HACK>
        */

    }

    /**
     * Display the behaviors available for selection, starting with basic, cached, public.
     */
    private void displayBehaviorFinder (final EventHolder eventHolder) {

        // Get list of behaviors available for selection
        int behaviorScriptCount = getClay().getCache().getBehaviorScripts().size();
        final String[] behaviorScripts = new String[behaviorScriptCount];
        for (int i = 0; i < behaviorScriptCount; i++) {
            BehaviorScript cachedBehaviorScript = unit.getClay().getCache().getBehaviorScripts().get(i);
            behaviorScripts[i] = cachedBehaviorScript.getTag();
        }

        // Show the list of behaviors
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose");
        builder.setItems(behaviorScripts, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int itemIndex) {

                // <HACK>
                BehaviorScript selectedBehaviorScript = getClay().getCache().getBehaviorScripts().get(itemIndex);
                // </HACK>
                Log.v("Content_Manager", "to " + selectedBehaviorScript.getUuid());
//                Log.v("Content_Manager", "from:");
//                for (Behavior cb : getClay().getCache().getBehaviors()) {
//                    Log.v("Change_Behavior", "\t" + cb.getUuid().toString());
//                }

                replaceEventHolder(eventHolder, selectedBehaviorScript);

                refreshListViewFromData();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private Clay getClay () {
        return unit.getClay();
    }/**
     * Changes the specified eventHolder's type to the specified type.
     */
    private void replaceEventHolder (final EventHolder eventHolder, BehaviorScript behaviorScript) {

        // TODO: Refactor this method so it _reuses_ Clay's Event object. (Only the Behavior object needs to changed.)

        UUID behaviorScriptUuid = behaviorScript.getUuid();

        Log.v ("CM_Log", "replaceEventHolder");

        /* Update data model */
        /* Update view */

        // <HACK>
        // This removes the specified eventHolder from the list and replaces it with an eventHolder of a specific type.
        // TODO: Update the behavior object referenced by eventHolders, and update the view accordingly (i.e., eventHolder.behavior = <new behavior> then retrieve view for that behavior type).
        int index = eventHolders.indexOf(eventHolder);
        eventHolders.remove(index);
        refreshListViewFromData();

        // Assign the behavior state
        Behavior behavior = getClay().getStore().getBasicBehavior(behaviorScript);
//        Behavior behavior = new Behavior (behaviorScript);
//        getClay().getStore().storeBehavior(behavior);
        Log.v("New_Behavior", "woo");

        // Update the event with the new behavior and state
        Event event = eventHolder.getEvent();
        if (event != null) {

            // <HACK>
            // TODO: Make this list update AFTER the data model. Basically update the view, but do all changes to OM first.
            getClay().getStore().removeEvent(eventHolder.getEvent(), null);
            getUnit().getTimeline().removeEvent(eventHolder.getEvent()); // if store behavior successful
            // </HACK>

            // Update state of the object associated with the selected view.
            eventHolders.remove(eventHolder);
        }

        // Create event object
        Timeline timeline = this.getUnit().getTimeline();
        event = new Event(timeline, behavior);
//        event.getBehavior().setState(behaviorState);

        // Object: Add event to timeline
        getUnit().getTimeline().getEvents().add(index, event); // if store event was successful

        // Store: Store the event
        getClay().getStore().storeEvent(event);

        // Store: Update timeline indices
        getClay().getStore().storeTimeline(getUnit().getTimeline());

        // Notify Clay of the change to the event
//        getClay().notifyChange(event);
//        getUnit().notifyChange(event);

        // Send the event to the device
        // i.e., Tell the unit to create the behavior, addUnit it to the timeline, and update the state.

        // unit.send("update behavior " + behaviorUuid + " \"" + behaviorState.getState() + "\"");
        /*
        unit.send("create behavior " + behaviorUuid + " \"" + behavior.getTag() + " " + behaviorState.getState() + "\"");
        unit.send("addUnit behavior " + behaviorUuid);
        // unit.send("update behavior " + behaviorUuid + " \"" + behaviorState.getState() + "\"");
        */

        // Create and addUnit the new eventHolder to the timeline
        // TODO: DO NOT create a new event, just update the existing one!
        EventHolder replacementEventHolder = new EventHolder(event);

        // Add the replacement item to the timeline view
        eventHolders.add(index, replacementEventHolder);

        // Finally, addUnit the behavior to the unit
//        unit.cacheBehavior(behaviorUuid);
//        getUnit().cacheBehavior(behavior);
//        getUnit().getTimeline().addEvent(event);

        // </HACK>


        // <HACK>
//        getClay().getStore().updateTimeline(this.unit.getTimeline());
        // </HACK>
    }

    public Unit getUnit () {
        return this.unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    private void displayDesignerView (EventHolder eventHolder) {
        if (eventDesignerView == null) {
            eventDesignerView = new EventDesignerView(getUnit(), this);
        }
    }

    private class EventHolderTouchDragListener implements OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            Log.v ("Gesture_Log", "OnDragLister from CustomListView");
            return false;
        }
    }

    private class EventHolderTouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.v ("Gesture_Log", "OnTouchListener from CustomListView");

            return false;
        }
    }

    private class EventHolderLongTouchListener implements OnItemLongClickListener
    {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            final EventHolder eventHolder = (EventHolder) eventHolders.get (position);

            // Check if the list item was a constructor
            if (eventHolder.type == EventHolderAdapter.SYSTEM_CONTROL_LAYOUT) {
                if (eventHolder.title == "create") {
                    // Nothing?
                }
                // TODO: (?)

            } else if (eventHolder.type != EventHolderAdapter.SYSTEM_CONTROL_LAYOUT && eventHolder.type != EventHolderAdapter.CONTROL_PLACEHOLDER_LAYOUT) {

                if (eventHolder.type == EventHolderAdapter.COMPLEX_LAYOUT) {

                    decomposeEventHolder(eventHolder);
                    return true;

                } else {

                    displayEventDesigner(eventHolder);
                    return true;

                }

                // Request the ListView to be redrawn so the views in it will be displayed
                // according to their updated state information.
//                refreshListViewFromData();
            }

            return false;
        }
    }

    private class EventHolderTouchReleaseListener implements OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, final int position, long id)
        {
            Log.v("Gesture_Log", "OnItemClickListener from CustomListView");

            final EventHolder eventHolder = (EventHolder) eventHolders.get (position);

            // Check if the list item was a constructor
            if (eventHolder.type == EventHolderAdapter.SYSTEM_CONTROL_LAYOUT) {

                if (eventHolder.title == "create") {
                    // Add a placeholder if one doesn't already exist
                    if (!hasPlaceholder ()) {

                        // menu:
                        // [ create, branch ]
                        //   - choose
                        //   - behavior

                        String title = "choose"; // color in "human" behavior indicator color
                        String subtitle = "touch to choose behavior"; // super small
                        int type = EventHolderAdapter.CONTROL_PLACEHOLDER_LAYOUT;

                        // Add the behavior to the timeline
                        addData(new EventHolder(title, subtitle, type));

                        // TODO: (?) Create a behavior?
                    }
                }
//                else if (eventHolder.title == "abstract") {
//
//                    composeEventHolderSelection();
//
//                }

            } else if (eventHolder.type == EventHolderAdapter.CONTROL_PLACEHOLDER_LAYOUT) {

                displayBehaviorFinder(eventHolder);

            } else {

                if (!hasSelectedEventHolders()) {
                    displayUpdateOptions(eventHolder);
                }

            }

        }

    }/**
     * Returns true if a placeholder event is found in the sequence.
     * @return
     */
    private boolean hasPlaceholder() {
        for (EventHolder eventHolder : eventHolders) {
            if (eventHolder.type == EventHolderAdapter.CONTROL_PLACEHOLDER_LAYOUT) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if there are any selected items on the timeline.
     * @return True if there are any selected items. Otherwise, returns false.
     */
    public boolean hasSelectedEventHolders() {
        for (EventHolder eventHolder : this.eventHolders) {
            if (eventHolder.isSelected()) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<EventHolder> getSelectedEventHolders () {
        ArrayList<EventHolder> selectedEventHolders = new ArrayList<>();
        for (EventHolder eventHolder : this.eventHolders) {
            if (eventHolder.isSelected()) {
                selectedEventHolders.add(eventHolder);
            }
        }
        return selectedEventHolders;
    }

    private int getFirstSelectedEventHolderIndex() {
        int index = 0;
        for (EventHolder eventHolder : this.eventHolders) {
            if (eventHolder.isSelected()) {
                break;
            }
            index++;
        }
        return index;
    }

    private void deselectEventHolders () {
        for (EventHolder eventHolder : this.eventHolders) {
            eventHolder.setSelected(false);
        }
    }

    private ArrayList<String> getSelectedEventTags () {
        ArrayList<String> selectedListItemLabels = new ArrayList<>();
        for (EventHolder eventHolder : this.eventHolders) {
            if (eventHolder.isSelected()) {
                String tag = eventHolder.getEvent().getBehavior().getTag();
                selectedListItemLabels.add(tag);
            }
        }
        return selectedListItemLabels;
    }

    /**
     * Creates a behavior composition from multiple selected behaviors.
     */
    public void composeEventHolderSelection () {

        // Get list of the selected items
        ArrayList<EventHolder> selectedEventHolders = getSelectedEventHolders();
        ArrayList<String> selectedEventTags = getSelectedEventTags();
        int index = getFirstSelectedEventHolderIndex();

        // Return if there are fewer than two selected items
        if (selectedEventHolders.size() < 2) {
            deselectEventHolders();
            return;
        }

        // Get the first event in the sequence
        EventHolder firstEvent = selectedEventHolders.get(0);

        // Create new behavior. Add behaviors to it.
        String behaviorListString = TextUtils.join(", ", selectedEventTags);
//        Behavior newBehavior = new Behavior(behaviorListString);
        ArrayList<Behavior> children = new ArrayList<Behavior>();
        ArrayList<BehaviorState> states = new ArrayList<BehaviorState>();
        for (EventHolder eventHolder : selectedEventHolders) {
            // TODO: Copy the behaviors
            Behavior b = eventHolder.getEvent().getBehavior();
//            newBehavior.addBehavior (b);
            children.add(b);
            //states.addAll(eventHolder.getEvent().getBehaviorState());
            for (BehaviorState behaviorState : eventHolder.getEvent().getBehaviorState()) {
                getClay().getStore().removeState (behaviorState);
                BehaviorState newBehaviorState = new BehaviorState(behaviorState.getState());
                states.add(newBehaviorState);
            }
        }
        //Get_Verified_Behavior(children)
//        Behavior pBehavior = getClay().getStore().getBehaviorComposition(children);
//        Log.v ("New_Behavior_Parent", "pBehavior = " + pBehavior);
        // Store the new behavior
        // TODO: First look in the database to make sure that duplicate behavior tree structures...
        // TODO: ...instead, if exists, get reference to it and reuse the structure!
        // TODO:    Do this by checking if the actions appear with the same sibling indices and a common parent behavior UUID.
        // TODO: hasBehaviorStructure (newBehavior)
//        getClay().getStore().storeBehavior(newBehavior);
//        getClay().cacheBehavior(newBehavior);

        Behavior behavior = getClay().getStore().getBehaviorComposition(children);
        behavior.setTag(behaviorListString);
        Log.v ("Compose_Behavior", "Composed behavior: " + behavior);
        Log.v("Compose_Behavior", "\tUUID: " + behavior);
        getClay().getStore().storeBehavior(behavior);
        getClay().getCache().getBehaviors().add(behavior);

        // Remove old behaviors from timeline in store
        for (EventHolder eventHolder : selectedEventHolders) {
            getClay().getStore().removeEvent(eventHolder.getEvent(), null);
        }

        // Remove old behaviors from the timeline
        for (EventHolder eventHolder : selectedEventHolders) {
            getUnit().getTimeline().removeEvent(eventHolder.getEvent()); // if store behavior successful
        }

        // Create event for the behavior and add it to the unit's timeline
//        Event compositionEvent = new Event(getUnit().getTimeline(), newBehavior);
//        getUnit().getTimeline().addEvent(index, compositionEvent);
//        getClay().getStore().storeEvent(compositionEvent);
        Event compositionEvent = new Event(getUnit().getTimeline(), behavior);
        // insert new event for abstract behavior
        //            foundUnit.getTimeline().addEvent(event);
        compositionEvent.getBehaviorState().clear();
        compositionEvent.getBehaviorState().addAll(states);
        Log.v("New_Behavior_Parent", "Added " + states.size() + " states to new event (composition).");
        Log.v ("New_Behavior_Parent", "Leaf count: " + behavior.getLeafCount());
        for (BehaviorState behaviorState : compositionEvent.getBehaviorState()) {
            Log.v("New_Behavior_Parent", "\t" + behaviorState.getState());
        }
        getUnit().getTimeline().addEvent(index, compositionEvent);
        getClay().getStore().storeEvent(compositionEvent);

        // Store: Reindex the timeline events
        getClay().getStore().storeTimeline(getUnit().getTimeline());

        // View: Remove the selected items from the list
        for (EventHolder eventHolder : selectedEventHolders) {
            eventHolders.remove(eventHolder);
        }

        // Add a new event holder to hold the new event that was made for the composed behavior
        EventHolder compositionEventHolder = new EventHolder (compositionEvent);
//        compositionEventHolder.title = behaviorListString;
//        compositionEventHolder.summary = "" + selectedEventHolders.size() + " actions";
        eventHolders.add(index, compositionEventHolder);

        // Display designer
        // NOTE: This causes the states to be stored wrong! Title isn't state. It's a tag/title.
//        displayDesignerView(compositionEventHolder);
//        eventDesignerView.displayUpdateTagOptions(compositionEventHolder);

        // Update timeline view after modifying the list
        refreshListViewFromData();

    }

    /**
     * Unpacks the behaviors in a behavior package containing multiple behaviors.
     */
    private void decomposeEventHolder (EventHolder eventHolder) {

        // Return if the item is not a complex item.
        if (eventHolder.type != EventHolderAdapter.COMPLEX_LAYOUT) {
            return;
        }

        // Get the list of behaviors in the behavior composition
        ArrayList<Behavior> behaviors = eventHolder.getEvent().getBehavior().getBehaviors();

        // Get position of the selected item
        int index = eventHolders.indexOf(eventHolder);

        // Remove the event from the timeline
        getUnit().getTimeline().removeEvent(eventHolder.getEvent());

        // Remove the event from the database
        getClay().getStore().removeEvent(eventHolder.getEvent(), null);

        // Remove the selected item from the list (it will be replaced by the abstracted behaviors)
        eventHolders.remove (index);

        // Create event for each of the behaviors in the composition, create an event holder for
        // each one, and add the event holder to the timeline.
        int stateIndex = 0;
        for (Behavior behavior : behaviors) {

            //!!!!!!!!!!!!!!!!
            // Restore the behavior's state
            // TODO: BUG? This doesn't seem to restore the state!
//            if (behavior.getBehaviors().size() == 0) {
//                getClay().getStore().restoreState(eventHolder.getEvent());
//            }
            //!!!!!!!!!!!!!!!!

            // Create event for the behavior and add it to the timeline
            Event decomposedEvent = new Event (getUnit ().getTimeline (), behavior);

            // Reassign states to events based on their number of children
            decomposedEvent.getBehaviorState().clear();
            if (behavior.hasScript()) {
                // Leaf node (one state for the leaf behavior node)
                BehaviorState behaviorState = eventHolder.getEvent().getBehaviorState().get(stateIndex);
                getClay().getStore().removeState (behaviorState);
                BehaviorState newBehaviorState = new BehaviorState(behaviorState.getState());
                decomposedEvent.getBehaviorState().add(newBehaviorState);
                stateIndex++;
            } else {
                // Non-leaf node (multiple states per behavior node)
                Log.v ("New_Behavior_Parent", "getLeafCount(): " + behavior.getLeafCount());
                for (int i = 0; i < behavior.getLeafCount(); i++) {
                //for (int i = 0; i < behavior.getBehaviors().size(); i++) {
                    BehaviorState behaviorState = eventHolder.getEvent().getBehaviorState().get(stateIndex);
                    getClay().getStore().removeState (behaviorState);
                    BehaviorState newBehaviorState = new BehaviorState(behaviorState.getState());
                    decomposedEvent.getBehaviorState().add(newBehaviorState);
                    stateIndex++;
                }
            }
            Log.v ("New_Behavior_Parent", "Decomposed into event with " + decomposedEvent.getBehaviorState().size() + " states.");
            for (BehaviorState behaviorState : decomposedEvent.getBehaviorState()) {
                Log.v("New_Behavior_Parent", "\t" + behaviorState.getState());
            }

            // Add the new event to the timeline
            getUnit().getTimeline().addEvent(index, decomposedEvent);

            // Store the new event
            getClay ().getStore ().storeEvent (decomposedEvent);

            // Create event holder for the event and add it to the timeline visualization
            EventHolder decomposedEventHolder = new EventHolder (decomposedEvent);
            eventHolders.add(index, decomposedEventHolder);

            // Increment the index of the insertion position
            index++;
        }

        getClay().getStore().storeTimeline(getUnit().getTimeline());
        // </HACK>

        refreshListViewFromData(); // Update view after removing items from the list

    }


}
