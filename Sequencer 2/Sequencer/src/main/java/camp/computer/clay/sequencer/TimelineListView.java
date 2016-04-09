package camp.computer.clay.sequencer;

import android.app.ActionBar;
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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.mobeta.android.dslv.DragSortItemView;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.sequencer.R;

import java.util.ArrayList;

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
        setUpView();
    }

    /**
     * Initialize the ListView.
     */
    public void setUpView()
    {
        setUpLayout();

        setUpData();

        // Set up gesture recognition
        setUpInteractivity();
    }

    /**
     * Interactivity
     */



    private void setUpInteractivity() {

        setUpTouchListeners();

//        this.setDropListener(onDropEventHolder);
//        this.setRemoveListener(onRemoveEventHolder);
//        this.setDragScrollProfile(dragScrollProfile);
        setUpDragListeners();

        setUpScrollListeners();
    }

    private void setUpTouchListeners() {
        // Note: Using the following prevents the drag-and-drop functionality of the ListView.
        // setOnTouchListener(new EventHolderTouchListener());
        setOnItemClickListener(new EventHolderTouchReleaseListener());
        setOnItemLongClickListener(new EventHolderLongTouchListener());
        // setOnDragListener(new EventHolderTouchDragListener());
    }

    public void setUpDragListeners() {
        this.setDropListener(onDropEventHolder);
        this.setRemoveListener(onRemoveEventHolder);
        this.setDragScrollProfile(dragScrollProfile);
    }

    private void setUpScrollListeners () {
        setOnScrollListener(new OnScrollListener() {
            private int currentVisibleItemCount;
            private int currentScrollState;
            private int currentFirstVisibleItem;
            private int totalItem;
//            private LinearLayout lBelow;


            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.v("Scroller", "onScrollStateChanged");
                // TODO Auto-generated method stub
                this.currentScrollState = scrollState;
                this.isScrollCompleted();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                Log.v("Scroller", "onScroll");
                // TODO Auto-generated method stub
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItem = totalItemCount;

                // save index and top position
                int index = getFirstVisiblePosition();
                View v = getChildAt(0);
                int top = (v == null) ? 0 : (v.getTop() - getPaddingTop());

                ActionBar mActionBar = ApplicationView.getApplicationView().getActionBar();
                int mActionBarHeight = 40; // mActionBar.getHeight();
                Log.v("Scroller", "mActionBarHeight: " + mActionBarHeight);

                float y = top * -1.0f;

                if (y >= mActionBarHeight && mActionBar.isShowing()) {
                    mActionBar.hide();
                    Log.v("Scroller", "hiding");
                } else if (y == 0 && !mActionBar.isShowing()) {
                    mActionBar.show();
                    Log.v("Scroller", "showing");
                }

                Log.v("Scroller", "top: " + top);
                Log.v("Scroller", "y: " + y);

//                if (getChildCount() > 0) {
//                    // TODO: Update size of top elements in timeline
//                    DragSortItemView eventView = (DragSortItemView) getChildAt(0);
//                    eventView.setPadding(0, 50, 0, 50);
//                    Log.v("Scroller", "eventView: " + eventView);
//                }
            }

            private void isScrollCompleted() {
                Log.v("Scroller", "isScrollCompleted");
                if (totalItem - currentFirstVisibleItem == currentVisibleItemCount && this.currentScrollState == SCROLL_STATE_IDLE) {

                }
            }
        });

//        setOnScrollChangeListener(new OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                Log.v ("Scroller", "timeline scroll");
//                Log.v ("Scroller", "scrollX: " + scrollX);
//                Log.v ("Scroller", "oldScrollX: " + oldScrollX);
//                Log.v ("Scroller", "scrollY: " + scrollY);
//                Log.v ("Scroller", "oldScrollY: " + oldScrollY);
//
//                float y = scrollY; // ((ScrollView)FindViewById (Resource.Id.scrollum)).ScrollY;
//
//                ActionBar mActionBar = ApplicationView.getApplicationView().getActionBar();
//                int mActionBarHeight = mActionBar.getHeight();
//
//                if (y >= mActionBarHeight && mActionBar.isShowing()) {
//                    mActionBar.hide();
//                } else if (y == 0 && !mActionBar.isShowing()) {
//                    mActionBar.show();
//                }
//            }
//        });
    }

    private DragSortListView.DropListener onDropEventHolder =
            new DragSortListView.DropListener() {
                @Override
                public void drop(int fromIndex, int toIndex) {
                    //String item=adapter.getItem(from);
                    EventHolder eventHolder = (EventHolder) adapter.getItem(fromIndex);

                    // Timeline: Move the event holder
                    adapter.notifyDataSetChanged();
                    adapter.remove(eventHolder);
                    adapter.insert(eventHolder, toIndex);
                    Log.v("Timeline", "Moving event from " + fromIndex + " to " + toIndex + ".");

                    // Object: Update the event with the new behavior and state
                    Event event = eventHolder.getEvent();
                    if (event != null) {
                        // <HACK>
                        // TODO: Make this list update AFTER the data model. Basically update the view, but do all changes to OM first.
                        getUnit().getTimeline().removeEvent(event);
                        // </HACK>
                    }

                    // Object: Add event to timeline
//                    getUnit().getTimeline().getEvents().add (toIndex, event);
                    getUnit().getTimeline().addEvent(toIndex, event); // if store event was successful

                    // Store: Update timeline indices
                    getClay().getStore().storeTimeline(getUnit().getTimeline());

                    // Device: Send messages to update device state
                    // TODO: "start event <event-uuid> [after event <event-uuid>]"
                    // TODO: "start event <event-uuid> [before event <event-uuid>]"
                    if (toIndex == (adapter.getCount() - 1)) {
                        // <HACK>
                        getUnit().sendMessageTcp("start event " + event.getUuid());
                        getUnit().sendMessageTcp("set event " + event.getUuid() + " action " + event.getAction().getScript().getUuid()); // <HACK />
                        getUnit().sendMessageTcp("set event " + event.getUuid() + " state \"" + event.getState().get(0).getState().toString() + "\"");
                        // </HACK>
                    } else {
                        Event nextEvent = getUnit().getTimeline().getEvents().get(toIndex + 1);
                        // <HACK>
                        getUnit().sendMessageTcp("start event " + event.getUuid() + " before event " + nextEvent.getUuid());
                        getUnit().sendMessageTcp("set event " + event.getUuid() + " action " + event.getAction().getScript().getUuid()); // <HACK />
                        getUnit().sendMessageTcp("set event " + event.getUuid() + " state \"" + event.getState().get(0).getState().toString() + "\"");
                        // </HACK>
                    }
                }
            };

    private DragSortListView.RemoveListener onRemoveEventHolder =
            new DragSortListView.RemoveListener() {
                @Override
                public void remove(int which) {
                    EventHolder eventHolder = (EventHolder) adapter.getItem(which);

                    adapter.remove(eventHolder);

                    removeEventHolder(eventHolder);
                }
            };

    /**
     * Interface for controlling scroll speed as a function of touch position and time.
     * Use setDragScrollProfile(DragScrollProfile) to set custom profile.
     *
     * Source: http://bauerca.github.io/drag-sort-listview/reference/com/mobeta/android/dslv/DragSortListView.DragScrollProfile.html
     */
    private DragSortListView.DragScrollProfile dragScrollProfile =
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

    /**
     * Setup
     */

    /**
     * Set up the eventHolders source and populate the list of eventHolders to show in this ListView.
     *
     * TODO: Initialize eventHolders from cache or from remote source in this function.
     * TODO:    Do this because ViewPager will destroy this object when moving between pages.
     * TODO: Observe remote eventHolders source and update cached source and notify ListView...
     * TODO: ...when eventHolders set changes.
     */
    private void setUpData() {

        this.eventHolders = new ArrayList<EventHolder>();

        // Set up the eventHolders adaptor
//        this.adapter = new EventHolderAdapter(getContext(), R.layout.list_item_handle_right, this.eventHolders);
        this.adapter = new EventHolderAdapter(getContext(), R.layout.list_item_type_light, this.eventHolders);
        setAdapter(adapter);
    }

    private void setUpLayout() {
        if (TimelineListView.HIDE_LIST_ITEM_SEPARATOR) {
            setDivider(null);
            setDividerHeight(0);
        }
    }

    /**
     * Timeline transform operations (e.g., add, remove, filter, etc).
     */

    public void setTimeline (Timeline timeline) {

        // Clear the timeline view
        this.eventHolders.clear();
        this.adapter.notifyDataSetChanged();

        // Create and addUnit eventHolders for each behavior
        createEventHolders(timeline);

        // Add "create" option
        this.eventHolders.add(new EventHolder("create", "", EventHolderAdapter.SYSTEM_CONTROL_LAYOUT));

        // Add "update" firmware option
        // TODO: Conditionally show this, only if firmware update is available
        // this.eventHolders.addUnit(new EventHolder("update firmware", "", EventHolderAdapter.SYSTEM_CONTROL_LAYOUT));

        this.adapter.notifyDataSetChanged();
    }

    /**
     * Creates event holders for the timeline.
     * @param timeline Timeline
     */
    private void createEventHolders (Timeline timeline) {

        // Create a behavior profile for each of the unit's behaviors
        for (camp.computer.clay.system.Event event : timeline.getEvents()) {
            EventHolder eventHolder = new EventHolder(event);
            eventHolders.add(eventHolder);
        }
    }

    /**
     * Add eventHolders to the ListView.
     * @param eventHolder
     */
    private void addEventHolder (EventHolder eventHolder) {
        if (adapter != null) {
            eventHolders.add(eventHolders.size() - 1, eventHolder);
            refreshListViewFromData();
        }
    }

    /**
     * Changes the specified eventHolder's type to the specified type.
     */
    private void replaceEventHolder (final EventHolder eventHolder, Script script) {

        // TODO: Refactor this method so it _reuses_ Clay's Event object. (Only the Action object needs to changed.)

        // <HACK>
        // This removes the specified eventHolder from the list and replaces it with an eventHolder of a specific type.
        // TODO: Update the action object referenced by eventHolders, and update the view accordingly (i.e., eventHolder.action = <new action> then retrieve view for that action type).
        int index = eventHolders.indexOf(eventHolder);
        eventHolders.remove(index);
        refreshListViewFromData();

        // Assign the action state
        Action action = getClay().getStore().getBasicBehavior(script);

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
        Log.v("Sending_Message", "start event " + event.getUuid());
        Log.v("Sending_Message", "set event " + event.getUuid() + " action " + action.getUuid());
        Log.v("Sending_Message", "set event " + event.getUuid() + " state " + event.getState().get(0).getState());

        // <HACK>
        // TODO: Replace this with a queue.
        getUnit().sendMessageTcp("start event " + event.getUuid());
        getUnit().sendMessageTcp("set event " + event.getUuid() + " action " + action.getScript().getUuid()); // <HACK />
        getUnit().sendMessageTcp("set event " + event.getUuid() + " state \"" + event.getState().get(0).getState().toString() + "\"");
        // </HACK>

        // Create and addUnit the new eventHolder to the timeline
        // TODO: DO NOT create a new event, just update the existing one!
        EventHolder replacementEventHolder = new EventHolder(event);

        // Add the replacement item to the timeline view
        eventHolders.add(index, replacementEventHolder);
    }

    private void removeEventHolder(final EventHolder eventHolder) {

        // <HACK>
        // TODO: Replace this with a queue.
        getUnit().sendMessageTcp("stop event " + eventHolder.getEvent().getUuid());
        // </HACK>

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
     * Timeline view transform operations
     */

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
        eventHolders.remove(index);

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

    /**
     * Timeline and timeline view access operations
     */

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

    /** Data transform operations */

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

    /** Graphical Interfaces */

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

        else if (eventHolder.type == 50) {
            eventDesignerView.displayUpdateToneOptions (eventHolder);
        }

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

                replaceEventHolder(eventHolder, selectedScript);

                refreshListViewFromData();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private Clay getClay () {
        return unit.getClay();
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

//            // Update layout based on state
//            if (eventHolder.summary.equals("small")) {
//                // Update left padding
////                view.setPadding(0, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
//                AbsListView.LayoutParams mCompressedParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 150);
//                view.setLayoutParams(mCompressedParams);
//                eventHolder.summary = "large";
//            } else {
//                // Update left padding to indent the item
////                view.setPadding(120, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
//                AbsListView.LayoutParams mCompressedParams = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 400);
//                view.setLayoutParams(mCompressedParams);
//                eventHolder.summary = "small";
//            }

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
                        addEventHolder(new EventHolder(title, subtitle, type));

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
     * EventHolder Selection
     */

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

    public void selectEventHolderByIndex(int index) {

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
//                refreshListViewFromData();
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
//                refreshListViewFromData();

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


}
