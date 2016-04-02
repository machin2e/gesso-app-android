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
                    EventHolder item = (EventHolder) adapter.getItem(from);

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
//        event.getAction().setState(behaviorState);

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
                    EventHolder eventHolder = (EventHolder) adapter.getItem(which);

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
        builder.setTitle("Action options");
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

        displayDesignerView (eventHolder);

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

        else if (eventHolder.type == 50) {
            eventDesignerView.displayUpdateToneOptions (eventHolder);
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
        int behaviorScriptCount = getClay().getCache().getScripts().size();
        final String[] behaviorScripts = new String[behaviorScriptCount];
        for (int i = 0; i < behaviorScriptCount; i++) {
            Script cachedScript = unit.getClay().getCache().getScripts().get(i);
            behaviorScripts[i] = cachedScript.getTag();
        }

        // Show the list of behaviors
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Choose");
        builder.setItems(behaviorScripts, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int itemIndex) {

                // <HACK>
                Script selectedScript = getClay().getCache().getScripts().get(itemIndex);
                // </HACK>
                Log.v("Content_Manager", "to " + selectedScript.getUuid());
//                Log.v("Content_Manager", "from:");
//                for (Action cb : getClay().getCache().getActions()) {
//                    Log.v("Change_Behavior", "\t" + cb.getUuid().toString());
//                }

                replaceEventHolder(eventHolder, selectedScript);

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
    private void replaceEventHolder (final EventHolder eventHolder, Script script) {

        // TODO: Refactor this method so it _reuses_ Clay's Event object. (Only the Action object needs to changed.)

        UUID behaviorScriptUuid = script.getUuid();

        Log.v ("CM_Log", "replaceEventHolder");

        /* Update data model */
        /* Update view */

        // <HACK>
        // This removes the specified eventHolder from the list and replaces it with an eventHolder of a specific type.
        // TODO: Update the action object referenced by eventHolders, and update the view accordingly (i.e., eventHolder.action = <new action> then retrieve view for that action type).
        int index = eventHolders.indexOf(eventHolder);
        eventHolders.remove(index);
        refreshListViewFromData();

        // Assign the action state
        Action action = getClay().getStore().getBasicBehavior(script);
//        Action action = new Action (script);
//        getClay().getStore().storeBehavior(action);
        Log.v("New_Behavior", "woo");

        // Update the event with the new action and state
        Event event = eventHolder.getEvent();
        if (event != null) {

            // <HACK>
            // TODO: Make this list update AFTER the data model. Basically update the view, but do all changes to OM first.
            getClay().getStore().removeEvent(eventHolder.getEvent(), null);
            // TODO: getUnit().sendMessage ("stop event " + event.getUuid());
            // TODO: ^ That won't work right unless an event on the MCU has all actions and states, not just 1:1 for event:action.
            getUnit().getTimeline().removeEvent(eventHolder.getEvent()); // if store action successful
            // </HACK>

            // Update state of the object associated with the selected view.
            eventHolders.remove(eventHolder);
        }

        // Create event object
        Timeline timeline = this.getUnit().getTimeline();
        event = new Event(timeline, action);
//        event.getAction().setState(behaviorState);

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
        // i.e., Tell the unit to create the action, addUnit it to the timeline, and update the state.

        // unit.sendMessage("update action " + behaviorUuid + " \"" + behaviorState.getState() + "\"");

        // <HACK>
        // NOTE: This only works for basic behaviors. Should change it so it also supports complex behaviors.
        // (action, regex, state)
        // ON START:
        // i.e., "cache action <action-uuid> <action-regex>"
        // ON ADD ACTION EVENT TO TIMELINE:
        // i.e., "start event <event-uuid> [at <index> [on <timeline-uuid>]]" (creates event for the action at index i... adds the event to the timeline, but ignores it until it has an action and state)
        // i.e., "set event <event-uuid> action <action-uuid>"
        // i.e., "set event <event-uuid> state "<state>"" (assigns the state string the specified event)
        // ON UPDATE ACTION STATE:
        // i.e., "set event <event-uuid> state "<state>"" (assigns the state string the specified event)
        // ON REMOVE ACTION FROM TIMELINE:
        // i.e., "stop event <action-uuid>" (removes event for action with the uuid)
//        getUnit().sendMessage ("cache action " + action.getUuid() + " \"" + action.getTag() + " " + event.getState().get(0).getState() + "\"");
        Log.v("Sending_Message", "start event " + event.getUuid());
        Log.v("Sending_Message", "set event " + event.getUuid() + " action " + action.getUuid());
        Log.v("Sending_Message", "set event " + event.getUuid() + " state " + event.getState().get(0).getState());
        getUnit().sendMessage ("start event " + event.getUuid());
        getUnit().sendMessage ("set event " + event.getUuid() + " action " + action.getUuid());
        // TODO: Set initial/default state for action type!
//        getUnit().sendMessage ("set event " + event.getUuid() + " state \"light " + event.getState().get(0).getState() + "\""); // <HACK />
        // unit.sendMessage("update action " + behaviorUuid + " \"" + behaviorState.getState() + "\"");
        // </HACK>


        // Create and addUnit the new eventHolder to the timeline
        // TODO: DO NOT create a new event, just update the existing one!
        EventHolder replacementEventHolder = new EventHolder(event);

        // Add the replacement item to the timeline view
        eventHolders.add(index, replacementEventHolder);

        // Finally, addUnit the action to the unit
//        unit.cacheBehavior(behaviorUuid);
//        getUnit().cacheBehavior(action);
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

//                // <HACK>
//                if (eventHolder.type == 50) {
//                    Log.v ("Tone", "updating summary: " + eventHolder.summary);
//                    if (eventHolder.summary.equals("yes")) {
//                        eventHolder.summary = "no";
//                    } else {
//                        eventHolder.summary = "yes";
//                    }
//                    refreshListViewFromData();
//                }
//                // </HACK>
//
//                else

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
                String tag = eventHolder.getEvent().getAction().getTag();
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

        // Create new action. Add behaviors to it.
        String behaviorListString = TextUtils.join(", ", selectedEventTags);
//        Action newBehavior = new Action(behaviorListString);
        ArrayList<Action> children = new ArrayList<Action>();
        ArrayList<State> states = new ArrayList<State>();
        for (EventHolder eventHolder : selectedEventHolders) {
            // TODO: Copy the behaviors
            Action b = eventHolder.getEvent().getAction();
//            newBehavior.addBehavior (b);
            children.add(b);
            //states.addAll(eventHolder.getEvent().getState());
            for (State state : eventHolder.getEvent().getState()) {
                getClay().getStore().removeState (state);
                State newState = new State(state.getState());
                states.add(newState);
            }
        }
        //Get_Verified_Behavior(children)
//        Action pBehavior = getClay().getStore().getBehaviorComposition(children);
//        Log.v ("New_Behavior_Parent", "pBehavior = " + pBehavior);
        // Store the new action
        // TODO: First look in the database to make sure that duplicate action tree structures...
        // TODO: ...instead, if exists, get reference to it and reuse the structure!
        // TODO:    Do this by checking if the actions appear with the same sibling indices and a common parent action UUID.
        // TODO: hasBehaviorStructure (newBehavior)
//        getClay().getStore().storeBehavior(newBehavior);
//        getClay().cacheBehavior(newBehavior);

        Action action = getClay().getStore().getBehaviorComposition(children);
        action.setTag(behaviorListString);
        Log.v ("Compose_Behavior", "Composed action: " + action);
        Log.v("Compose_Behavior", "\tUUID: " + action);
        getClay().getStore().storeBehavior(action);
        getClay().getCache().getActions().add(action);

        // Remove old behaviors from timeline in store
        for (EventHolder eventHolder : selectedEventHolders) {
            getClay().getStore().removeEvent(eventHolder.getEvent(), null);
        }

        // Remove old behaviors from the timeline
        for (EventHolder eventHolder : selectedEventHolders) {
            getUnit().getTimeline().removeEvent(eventHolder.getEvent()); // if store action successful
        }

        // Create event for the action and add it to the unit's timeline
//        Event compositionEvent = new Event(getUnit().getTimeline(), newBehavior);
//        getUnit().getTimeline().addEvent(index, compositionEvent);
//        getClay().getStore().storeEvent(compositionEvent);
        Event compositionEvent = new Event(getUnit().getTimeline(), action);
        // insert new event for abstract action
        //            foundUnit.getTimeline().addEvent(event);
        compositionEvent.getState().clear();
        compositionEvent.getState().addAll(states);
        Log.v("New_Behavior_Parent", "Added " + states.size() + " states to new event (composition).");
        Log.v ("New_Behavior_Parent", "Leaf count: " + action.getLeafCount());
        for (State state : compositionEvent.getState()) {
            Log.v("New_Behavior_Parent", "\t" + state.getState());
        }
        getUnit().getTimeline().addEvent(index, compositionEvent);
        getClay().getStore().storeEvent(compositionEvent);

        // Store: Reindex the timeline events
        getClay().getStore().storeTimeline(getUnit().getTimeline());

        // View: Remove the selected items from the list
        for (EventHolder eventHolder : selectedEventHolders) {
            eventHolders.remove(eventHolder);
        }

        // Add a new event holder to hold the new event that was made for the composed action
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

        // Get the list of actions in the behavior composition
        ArrayList<Action> actions = eventHolder.getEvent().getAction().getActions();

        // Get position of the selected item
        int index = eventHolders.indexOf(eventHolder);

        // Remove the event from the timeline
        getUnit().getTimeline().removeEvent(eventHolder.getEvent());

        // Remove the event from the database
        getClay().getStore().removeEvent(eventHolder.getEvent(), null);

        // Remove the selected item from the list (it will be replaced by the abstracted actions)
        eventHolders.remove (index);

        // Create event for each of the actions in the composition, create an event holder for
        // each one, and add the event holder to the timeline.
        int stateIndex = 0;
        for (Action action : actions) {

            //!!!!!!!!!!!!!!!!
            // Restore the action's state
            // TODO: BUG? This doesn't seem to restore the state!
//            if (action.getActions().size() == 0) {
//                getClay().getStore().restoreState(eventHolder.getEvent());
//            }
            //!!!!!!!!!!!!!!!!

            // Create event for the action and add it to the timeline
            Event decomposedEvent = new Event (getUnit ().getTimeline (), action);

            // Reassign states to events based on their number of children
            decomposedEvent.getState().clear();
            if (action.hasScript()) {
                // Leaf node (one state for the leaf action node)
                State state = eventHolder.getEvent().getState().get(stateIndex);
                getClay().getStore().removeState (state);
                State newState = new State(state.getState());
                decomposedEvent.getState().add(newState);
                stateIndex++;
            } else {
                // Non-leaf node (multiple states per action node)
                Log.v ("New_Behavior_Parent", "getLeafCount(): " + action.getLeafCount());
                for (int i = 0; i < action.getLeafCount(); i++) {
                //for (int i = 0; i < action.getActions().size(); i++) {
                    State state = eventHolder.getEvent().getState().get(stateIndex);
                    getClay().getStore().removeState (state);
                    State newState = new State(state.getState());
                    decomposedEvent.getState().add(newState);
                    stateIndex++;
                }
            }
            Log.v ("New_Behavior_Parent", "Decomposed into event with " + decomposedEvent.getState().size() + " states.");
            for (State state : decomposedEvent.getState()) {
                Log.v("New_Behavior_Parent", "\t" + state.getState());
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
