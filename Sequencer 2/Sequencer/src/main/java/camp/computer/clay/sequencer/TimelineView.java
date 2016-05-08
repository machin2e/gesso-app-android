package camp.computer.clay.sequencer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.mobeta.android.dslv.DragSortListView;
import com.mobeta.android.sequencer.R;

import java.util.ArrayList;
import java.util.UUID;

import camp.computer.clay.system.*;
import camp.computer.clay.system.Event;

public class TimelineView extends DragSortListView {

    private static final boolean HIDE_LIST_ITEM_SEPARATOR = true;

    private Device device;

    /**
     * List of event holders that hold events on the timeline. This has to be repopulated on
     * initialization.
     */
    private ArrayList<EventHolder> eventHolders;

    private EventHolderAdapter adapter;

    private EventDesignerView eventDesignerView;

    public TimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setUpView();

        // <HACK>
        TimelineView.timelineView = this;
        // </HACK>

        // <HACK>
        if (ApplicationView.getApplicationView().mViewPager.currentListView == null) {
            ApplicationView.getApplicationView().mViewPager.currentListView = this;
        }
        // </HACK>

        // <HACK>
        // Show the action button
        ApplicationView.getApplicationView().getCursorView().init();
        ApplicationView.getApplicationView().getCursorView().updatePosition();
        ApplicationView.getApplicationView().getCursorView().show(true);
        // </HACK>
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

                if (scrollState == SCROLL_STATE_IDLE) {
                    // Hide the action buttons
                    FloatingActionButton fab = (FloatingActionButton) ApplicationView.getApplicationView().findViewById(R.id.fab_create);
                    if (fab.isHidden()) {
                        fab.show(true);
                    }
                } else if (scrollState == SCROLL_STATE_FLING) {
                    // Hide the action buttons
                    FloatingActionButton fab = (FloatingActionButton) ApplicationView.getApplicationView().findViewById(R.id.fab_create);
                    if (!fab.isHidden()) {
                        fab.hide(true);
                    }
                }
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

                /*
                // Auto-hide/auto-show action bar
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
                */

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
                        getDevice().getTimeline().removeEvent(event);
                        // </HACK>
                    }

                    // Object: Add event to timeline
//                    getDevice().getTimeline().getEvents().add (toIndex, event);
                    getDevice().getTimeline().addEvent(toIndex, event); // if store event was successful

                    // Store: Update timeline indices
                    getClay().getStore().storeTimeline(getDevice().getTimeline());

                    // Device: Send messages to update device state
                    // TODO: "start event <event-uuid> [after event <event-uuid>]"
                    // TODO: "start event <event-uuid> [before event <event-uuid>]"
                    if (toIndex == (adapter.getCount() - 1)) {
                        // <HACK>
                        getDevice().enqueueMessage("start event " + event.getUuid());
                        getDevice().enqueueMessage("set event " + event.getUuid() + " action " + event.getAction().getScript().getUuid()); // <HACK />
                        getDevice().enqueueMessage("set event " + event.getUuid() + " state \"" + event.getState().get(0).getState().toString() + "\"");
                        // </HACK>
                    } else {
                        Event nextEvent = getDevice().getTimeline().getEvents().get(toIndex + 1);
                        // <HACK>
                        getDevice().enqueueMessage("start event " + event.getUuid() + " before event " + nextEvent.getUuid());
                        getDevice().enqueueMessage("set event " + event.getUuid() + " action " + event.getAction().getScript().getUuid()); // <HACK />
                        getDevice().enqueueMessage("set event " + event.getUuid() + " state \"" + event.getState().get(0).getState().toString() + "\"");
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
        this.adapter = new EventHolderAdapter(getContext(), this.eventHolders);
        setAdapter(adapter);
    }

    private void setUpLayout() {
        if (TimelineView.HIDE_LIST_ITEM_SEPARATOR) {
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

        // Create and addDevice eventHolders for each behavior
        createEventHolders(timeline);

        // Add "create" option
//        this.eventHolders.add(new EventHolder("create", "create"));

        // Add "update" firmware option
        // TODO: Conditionally show this, only if firmware update is available
        // this.eventHolders.addDevice(new EventHolder("update firmware", "", EventHolderAdapter.SYSTEM_CONTROL_LAYOUT));

        this.adapter.notifyDataSetChanged();
    }

    /**
     * Creates event holders for the timeline.
     * @param timeline Timeline
     */
    private void createEventHolders (Timeline timeline) {

        // Create a behavior profile for each of the device's behaviors
        for (Event event : timeline.getEvents()) {
            EventHolder eventHolder = new EventHolder(event);
            eventHolders.add(eventHolder);
        }
    }

    /**
     * Add eventHolders to the ListView.
     * @param eventHolder
     */
    public void addEventHolder(EventHolder eventHolder) {
        if (adapter != null) {
            eventHolders.add(eventHolders.size(), eventHolder);
            refreshTimelineView(); // <HACK />

            scrollToBottom();
        }
    }

    /**
     * Add eventHolders to the ListView.
     * @param eventHolder
     */
    public void addEventHolder(int index, EventHolder eventHolder) {
        if (adapter != null) {
            eventHolders.add(index, eventHolder);
            refreshTimelineView(); // <HACK />
        }
    }

    /**
     * Changes the specified eventHolder's type to the specified type.
     */
    public void replaceEventHolder(final EventHolder eventHolder, Action action) {

        // TODO: Refactor this method so it _reuses_ Clay's Event object. (Only the Action object needs to changed.)

        // <HACK>
        // This removes the specified eventHolder from the list and replaces it with an eventHolder of a specific type.
        // TODO: Update the action object referenced by eventHolders, and update the view accordingly (i.e., eventHolder.action = <new action> then retrieve view for that action type).
        int index = eventHolders.indexOf(eventHolder);
        eventHolders.remove(index);
        refreshTimelineView(); // <HACK />

        // Update the event with the new action and state
        Event event = eventHolder.getEvent();
        if (event != null) {

            // <HACK>
            // TODO: Make this list update AFTER the data model. Basically update the view, but do all changes to OM first.
            getClay().getStore().removeEvent(eventHolder.getEvent());
            // TODO: getDevice().sendMessage ("stop event " + event.getUuid());
            // TODO: ^ That won't work right unless an event on the MCU has all actions and states, not just 1:1 for event:action.
            getDevice().getTimeline().removeEvent (eventHolder.getEvent()); // if store action successful
            // </HACK>

            // Update state of the object associated with the selected view.
            eventHolders.remove(eventHolder);
            refreshTimelineView();
        }

        // Create event object
        Timeline timeline = this.getDevice().getTimeline();
        event = new Event(timeline, action);
//        event.getAction().setState(behaviorState);

        // Object: Add event to timeline
        getDevice().getTimeline().getEvents().add(index, event); // if store event was successful

        // Store: Store the event
        getClay().getStore().storeEvent(event);

        // Store: Update timeline indices
        getClay().getStore().storeTimeline(getDevice().getTimeline());

        // Notify Clay of the change to the event
//        getClay().notifyChange(event);
//        getDevice().notifyChange(event);

        // Send the event to the device
        Log.v("Sending_Message", "start event " + event.getUuid());
        Log.v("Sending_Message", "set event " + event.getUuid() + " action " + action.getUuid());
        Log.v("Sending_Message", "set event " + event.getUuid() + " state " + event.getState().get(0).getState());

        // <HACK>
        // TODO: Replace this with a queue.
        getDevice().enqueueMessage("start event " + event.getUuid());
        getDevice().enqueueMessage("set event " + event.getUuid() + " action " + action.getScript().getUuid()); // <HACK />
//        getDevice().enqueueMessage("set event " + event.getUuid() + " state \"" + event.getState().get(0).getState().toString() + "\"");
        // </HACK>



        String contextString = "TIT:none TIT:none TIW:none TOP:3,'waveform_sample_value'|'pulse_duty_cycle';F,0.02|'pulse_period_seconds' TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none";
        String contextContent = "set event " + event.getUuid() + " context \"" + contextString + "\"";
        getDevice().enqueueMessage(contextContent);

//        String stateString = "TIT:none:none TIT:none:none TIW:none TOP:0.02,64450:3,'waveform-sample-value'|'pulse_duty_cycle' TIT:none:none TIT:none:none TIT:none:none TIT:none:none TIT:none:none TIT:none:none TIT:none:none TIT:none:none";
        String stateString = "none none none 0.02,64450 none none none none none none none none";
        String content = "set event " + event.getUuid() + " state \"" + stateString + "\"";
        getDevice().enqueueMessage(content);





        // Create and addDevice the new eventHolder to the timeline
        // TODO: DO NOT create a new event, just update the existing one!
        EventHolder replacementEventHolder = new EventHolder(event);

        // Add the replacement item to the timeline view
        eventHolders.add(index, replacementEventHolder);

        refreshTimelineView();
    }

    public void removeEventHolder(final EventHolder eventHolder) {

        if (eventHolder.getEvent() != null) {

            // <HACK>
            // TODO: Replace this with a queue.
            getDevice().enqueueMessage("stop event " + eventHolder.getEvent().getUuid());
            // </HACK>

            // <HACK>
            // TODO: Make this list update AFTER the data model. Basically update the view, but do all changes to OM first.
            getClay().getStore().removeEvent(eventHolder.getEvent());
            getDevice().getTimeline().removeEvent(eventHolder.getEvent()); // if store behavior successful
            // </HACK>
        }

        // Update state of the object associated with the selected view.
        eventHolders.remove(eventHolder);

        // Update the view after removing the specified list item
//        refreshTimelineView();

        refreshAvatarView();

        /*
        // <HACK>
        getClay().getStore().updateTimeline(this.device.getTimeline());
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
//            newBehavior.addAction (b);
            children.add(b);
            //states.addAll(eventHolder.getEvent().getState());
            for (State state : eventHolder.getEvent().getState()) {
                getClay().getStore().removeState (state);
                State newState = new State(state.getState());
                states.add(newState);
            }
        }

        Action action = getClay().getStore().getActionComposition(children);
        action.setTag(behaviorListString);
        Log.v ("Compose_Behavior", "Composed action: " + action);
        Log.v("Compose_Behavior", "\tUUID: " + action);
        getClay().getStore().storeAction(action);
        getClay().getCache().getActions().add(action);

        // Remove old behaviors from timeline in store
        for (EventHolder eventHolder : selectedEventHolders) {
            getClay().getStore().removeEvent(eventHolder.getEvent());
        }

        // Remove old behaviors from the timeline
        for (EventHolder eventHolder : selectedEventHolders) {
            getDevice().getTimeline().removeEvent(eventHolder.getEvent()); // if store action successful
        }

        // Create event for the action and add it to the device's timeline
//        Event compositionEvent = new Event(getDevice().getTimeline(), newBehavior);
//        getDevice().getTimeline().addEvent(index, compositionEvent);
//        getClay().getStore().storeEvent(compositionEvent);
        Event compositionEvent = new Event(getDevice().getTimeline(), action);
        // insert new event for abstract action
        //            foundUnit.getTimeline().addEvent(event);
        compositionEvent.getState().clear();
        compositionEvent.getState().addAll(states);
        Log.v("New_Behavior_Parent", "Added " + states.size() + " states to new event (composition).");
        Log.v ("New_Behavior_Parent", "Leaf count: " + action.getLeafCount());
        for (State state : compositionEvent.getState()) {
            Log.v("New_Behavior_Parent", "\t" + state.getState());
        }
        getDevice().getTimeline().addEvent(index, compositionEvent);
        getClay().getStore().storeEvent(compositionEvent);

        // Store: Reindex the timeline events
        getClay().getStore().storeTimeline(getDevice().getTimeline());

        // View: Remove the selected items from the list
        for (EventHolder eventHolder : selectedEventHolders) {
            eventHolders.remove(eventHolder);
        }

        // Add a new event holder to hold the new event that was made for the composed action
        EventHolder compositionEventHolder = new EventHolder (compositionEvent);
//        compositionEventHolder.tag = behaviorListString;
//        compositionEventHolder.summary = "" + selectedEventHolders.size() + " actions";
        eventHolders.add(index, compositionEventHolder);

        // Display designer
        // NOTE: This causes the states to be stored wrong! Title isn't state. It's a tag/tag.
//        displayDesignerView(compositionEventHolder);
//        eventDesignerView.displayUpdateTagOptions(compositionEventHolder);

        // Update timeline view after modifying the list
        refreshTimelineView();

    }

    /**
     * Unpacks the behaviors in a behavior package containing multiple behaviors.
     */
    private void decomposeEventHolder (EventHolder eventHolder) {

        // Return if the item is not a complex item.
        if (!eventHolder.getType().equals("complex")) {
            return;
        }

        // Get the list of actions in the behavior composition
        ArrayList<Action> actions = eventHolder.getEvent().getAction().getActions();

        // Get position of the selected item
        int index = eventHolders.indexOf(eventHolder);

        // Remove the event from the timeline
        getDevice().getTimeline().removeEvent(eventHolder.getEvent());

        // Remove the event from the database
        getClay().getStore().removeEvent(eventHolder.getEvent());

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
            Event decomposedEvent = new Event (getDevice().getTimeline (), action);

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
            getDevice().getTimeline().addEvent(index, decomposedEvent);

            // Store the new event
            getClay ().getStore ().storeEvent (decomposedEvent);

            // Create event holder for the event and add it to the timeline visualization
            EventHolder decomposedEventHolder = new EventHolder (decomposedEvent);
            eventHolders.add(index, decomposedEventHolder);

            // Increment the index of the insertion position
            index++;
        }

        getClay().getStore().storeTimeline(getDevice().getTimeline());
        // </HACK>

        refreshTimelineView(); // Update view after removing items from the list

    }

    /**
     * Timeline and timeline view access operations
     */

    public ArrayList<EventHolder> getEventHolders () {
        return eventHolders;
    }

    /**
     * Returns the list item corresponding to the specified position.
     * @param x
     * @param y
     * @return
     */
    public EventHolder getEventHolderByPosition(int x, int y) {
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
        for (int i = 0; i < childCount; i++) {
            child = this.getChildAt(i);
            child.getHitRect(rect);
            if (rect.contains(x, y)) {
                mDownView = child; // This is your down view
                break;
            }
        }

        return mDownView;
    }

//    private Dictionary<Integer, Integer> listViewItemHeights = new Hashtable<Integer, Integer>();
//
//    public int getScroll() {
//        View c = this.getChildAt(0); //this is the first visible row
//        int scrollY = -c.getTop();
//        listViewItemHeights.put(this.getFirstVisiblePosition(), c.getHeight());
//        for (int i = 0; i < this.getFirstVisiblePosition(); ++i) {
//            if (listViewItemHeights.get(i) != null) // (this is a sanity check)
//                scrollY += listViewItemHeights.get(i); //add all heights of the views that are gone
//        }
//        return scrollY;
//    }

    public Point getTimelinePoint (int xPosition, int yPosition) {
        View mDownView = null;
        // Find the child view that was touched (perform a hit test)
        Rect rect = new Rect();
//        int childCount = this.getChildCount();
        int[] listViewCoords = new int[2];
        int x = (int) xPosition - listViewCoords[0];
        int y = (int) yPosition - listViewCoords[1];
//        View child;
//        int i = 0;
//        for ( ; i < childCount; i++) {
//            child = this.getChildAt(i);
//            child.getHitRect(rect);
//            if (rect.contains(x, y)) {
//                mDownView = child; // This is your down view
//                break;
//            }
//        }
//
//        // Check if the specified position is within the bounds of a view in the ListView.
//        // If so, select the item.
//        if (mDownView != null) {
//            int itemIndex = this.getFirstVisiblePosition() + i;
//            return itemIndex;
//        }
//
//        return -1;

        Point point = new Point (x, y);
        return point;
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
    public void refreshTimelineView() {
        // TODO: Perform callbacks into eventHolders model to propagate changes based on view state and eventHolders item state.
//        adapter.notifyDataSetChanged();
        final TimelineView lv = this;
        ApplicationView.getApplicationView().runOnUiThread(new Runnable() {
            public void run() {
                //reload content
//                arraylist.erase();
//                arraylist.addAll(db.readAll());
                adapter.notifyDataSetChanged();
                lv.invalidateViews();
                lv.refreshDrawableState();
            }
        });
    }



    /**
     * Resources:
     * - http://stackoverflow.com/questions/2250770/how-to-refresh-android-listview
     */
    public void redrawListViewFromData() {
        // TODO: Perform callbacks into eventHolders model to propagate changes based on view state and eventHolders item state.
//        adapter.notifyDataSetChanged();
        final TimelineView lv = this;
        ApplicationView.getApplicationView().runOnUiThread(new Runnable() {
            public void run() {
                //reload content
//                arraylist.erase();
//                arraylist.addAll(db.readAll());
//                adapter.notifyDataSetChanged();
                lv.invalidateViews();
                lv.refreshDrawableState();
            }
        });
    }

    /**
     * Resources:
     * - http://stackoverflow.com/questions/2250770/how-to-refresh-android-listview
     */
    public void redrawEventView(final View view) {
        // TODO: Perform callbacks into eventHolders model to propagate changes based on view state and eventHolders item state.
//        adapter.notifyDataSetChanged();
//        final TimelineListView lv = this;
        ApplicationView.getApplicationView().runOnUiThread(new Runnable() {
            public void run() {
                // Update view's layout
                view.requestLayout();
                view.invalidate();
            }
        });
    }

    public void refreshAvatarView() {
        // <FAB>
        addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

                // Remove the layout
                removeOnLayoutChangeListener(this);
                Log.e("Move_Finger", "Updated timeline layout.");

                // Update position
                ApplicationView.getApplicationView().getCursorView().updatePosition();
            }
        });

        refreshTimelineView();
        // </FAB>
    }

    private void displayEventDesigner(final EventHolder eventHolder) {
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

                    removeEventHolder(eventHolder);

                } else if (behaviorOptions[itemIndex].toString().equals("update")) {

                    displayUpdateOptions(eventHolder);

                } else if (behaviorOptions[itemIndex].toString().equals("replace")) {

                    displayActionBrowser(new ActionSelectionListener() {
                        @Override
                        public void onSelect(Action action) {
                            replaceEventHolder(eventHolder, action);
                            refreshTimelineView(); // <HACK />
                        }
                    });

                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /** Graphical Interfaces */

    public void displayUpdateOptions (final EventHolder eventHolder) {

        displayDesignerView(eventHolder);

        // <HACK>
        if (eventHolder.getType().equals("light")) {
            eventDesignerView.displayUpdateLightsOptions(eventHolder);
        } else if (eventHolder.getType().equals("signal")) {
            eventDesignerView.displayUpdateSignalOptions(eventHolder);
        } if (eventHolder.getType().equals("message")) {
            eventDesignerView.displayUpdateMessageOptions(eventHolder);
        } if (eventHolder.getType().equals("pause")) {
            eventDesignerView.displayUpdateWaitOptions(eventHolder);
        } if (eventHolder.getType().equals("say")) {
            eventDesignerView.displayUpdateSayOptions(eventHolder);
        } if (eventHolder.getType().equals("complex")) {
            eventDesignerView.displayUpdateTagOptions(eventHolder);
        } if (eventHolder.getType().equals("tone")) {
            eventDesignerView.displayUpdateToneOptions(eventHolder);
        }
        // </HACK>

    }

    public Point getPointUnderTimeline() {
        Log.v("Move_Finger", "getPointUnderTimeline");

        int viewCount = getChildCount();
        //View bottomView = getChildAt(viewCount - 1);
        Log.v("Move_Finger", "\tviewCount: " + viewCount);

        Point point = null;

        if (viewCount > 0) {
            View bottomView = getChildAt(viewCount - 1);

            Log.v("Move_Finger", "\tviewCount: " + viewCount);
            Log.v("Move_Finger", "\tbottomView: " + bottomView);

//            if (bottomView != null) {
            point = new Point (0, 0);
            point.y = bottomView.getBottom();
//            int y = bottomView.getScrollY();
                Log.v("Top_Pos", "top: " + point.y);
//            }
        }

        return point;
    }

    public void scrollToBottom () {
        this.post(new Runnable() {
            public void run() {
                setSelection(getCount() - 1);
            }
        });
    }

//    public void updatePosition(FloatingActionButton fab) {
//
//        // Get screen width and height of the device
//        DisplayMetrics metrics;
//        int screenWidth = 0, screenHeight = 0;
//        metrics = new DisplayMetrics();
//        ApplicationView.getApplicationView().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        screenHeight = metrics.heightPixels;
//        screenWidth = metrics.widthPixels;
//
//        if (eventHolders.size() == 0) {
//            int width = fab.getWidth();
//            int height = fab.getHeight();
//            Point dest = new Point((int) (screenWidth / 2.0)  - (int) (width / 2.0), (int) (screenHeight / 2.0) - (int) (height / 2.0));
////            ApplicationView.getApplicationView().moveToPoint(fab, dest, 400);
//
//            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
//            params.leftMargin = (int) dest.x;
//            params.topMargin = (int) dest.y;
//            return;
//        }
//
//        // Get point under last event on timeline
//        Point point = TimelineListView.getTimelineListView().getPointUnderTimeline();
//        if (point != null) {
//            point.x = 135;
//            point.y = point.y + (int) (0.01 * fab.getHeight());
//        }
//
//        if (point != null && point.y < (screenHeight - fab.getHeight())) {
//            ApplicationView.getApplicationView().moveToPoint(fab, point, 400);
//        } else {
//            int width = fab.getWidth();
//            int height = fab.getHeight();
//            Point dest = new Point((int) screenWidth - (int) (width * 1.1), (int) (screenHeight / 2.0) - (int) (height / 2.0));
//
//            ApplicationView.getApplicationView().moveToPoint(fab, dest, 400);
//
//        }
//    }

    public interface ActionSelectionListener {
        public void onSelect (Action action);
    }

    public void displayActionBrowser(final ActionSelectionListener actionSelectionListener) {

        // Get list of behaviors available for selection
        int actionScriptCount = getClay().getCache().getScripts().size();
        final String[] actionScripts = new String[actionScriptCount];
        for (int i = 0; i < actionScriptCount; i++) {
            Script cachedScript = device.getClay().getCache().getScripts().get(i);
            actionScripts[i] = cachedScript.getTag();
        }

        // Show the list of behaviors
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(actionScripts, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int itemIndex) {

                // <HACK>
                Script selectedScript = getClay().getCache().getScripts().get(itemIndex);
                // </HACK>

                // Assign the action state
                Action action = getClay().getStore().getAction(selectedScript);

                actionSelectionListener.onSelect(action);
            }
        });
        AlertDialog alert = builder.create();
        // alert.getListView().setBackgroundColor(Color.BLACK);


        alert.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface alert) {
                ListView listView = ((AlertDialog) alert).getListView();
                final ListAdapter originalAdapter = listView.getAdapter();

                listView.setAdapter(new ListAdapter() {

                    @Override
                    public int getCount() {
                        return originalAdapter.getCount();
                    }

                    @Override
                    public Object getItem(int id) {
                        return originalAdapter.getItem(id);
                    }

                    @Override
                    public long getItemId(int id) {
                        return originalAdapter.getItemId(id);
                    }

                    @Override
                    public int getItemViewType(int id) {
                        return originalAdapter.getItemViewType(id);
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = originalAdapter.getView(position, convertView, parent);
                        TextView textView = (TextView) view;
//                        textView.setTypeface(MyFontUtil.getTypeface(MyActivity,MY_DEFAULT_FONT));
                        textView.setTextColor(Color.WHITE);
                        textView.setAllCaps(true);
                        textView.setTextSize(10.0f);
                        return view;
                    }

                    @Override
                    public int getViewTypeCount() {
                        return originalAdapter.getViewTypeCount();
                    }

                    @Override
                    public boolean hasStableIds() {
                        return originalAdapter.hasStableIds();
                    }

                    @Override
                    public boolean isEmpty() {
                        return originalAdapter.isEmpty();
                    }

                    @Override
                    public void registerDataSetObserver(DataSetObserver observer) {
                        originalAdapter.registerDataSetObserver(observer);

                    }

                    @Override
                    public void unregisterDataSetObserver(DataSetObserver observer) {
                        originalAdapter.unregisterDataSetObserver(observer);

                    }

                    @Override
                    public boolean areAllItemsEnabled() {
                        return originalAdapter.areAllItemsEnabled();
                    }

                    @Override
                    public boolean isEnabled(int position) {
                        return originalAdapter.isEnabled(position);
                    }

                });

            }

        });

        // alert.getListView().setBackgroundColor(ApplicationView.getApplicationView().getResources().getColor(R.color.timeline_segment_color));

        alert.show();
    }

    public interface DeviceSelectionListener {
        public void onSelect (Device device);
    }

    public void displayDeviceBrowser(final DeviceSelectionListener deviceSelectionListener) {

        // Get list of behaviors available for selection
        int deviceCount = getClay().getDevices().size();
        final String[] deviceTitles = new String[deviceCount];
        for (int i = 0; i < deviceCount; i++) {
            Device device = getClay().getDevices().get(i);
            deviceTitles[i] = device.getUuid().toString();
        }

        // Show the list of behaviors
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(deviceTitles, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int itemIndex) {

                // TODO: Change the DeviceViewPager to the timeline associated with the specified device.

                String deviceUuidString = deviceTitles[itemIndex];

                Device device = getClay().getDeviceByUuid(UUID.fromString(deviceUuidString));

                deviceSelectionListener.onSelect(device);
            }
        });
        AlertDialog alert = builder.create();
        // alert.getListView().setBackgroundColor(Color.BLACK);


        alert.setOnShowListener(new DialogInterface.OnShowListener() {

            @Override
            public void onShow(DialogInterface alert) {
                ListView listView = ((AlertDialog) alert).getListView();
                final ListAdapter originalAdapter = listView.getAdapter();

                listView.setAdapter(new ListAdapter() {

                    @Override
                    public int getCount() {
                        return originalAdapter.getCount();
                    }

                    @Override
                    public Object getItem(int id) {
                        return originalAdapter.getItem(id);
                    }

                    @Override
                    public long getItemId(int id) {
                        return originalAdapter.getItemId(id);
                    }

                    @Override
                    public int getItemViewType(int id) {
                        return originalAdapter.getItemViewType(id);
                    }

                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = originalAdapter.getView(position, convertView, parent);
                        TextView textView = (TextView) view;
//                        textView.setTypeface(MyFontUtil.getTypeface(MyActivity,MY_DEFAULT_FONT));
                        textView.setTextColor(Color.WHITE);
                        textView.setAllCaps(true);
                        textView.setTextSize(10.0f);
                        return view;
                    }

                    @Override
                    public int getViewTypeCount() {
                        return originalAdapter.getViewTypeCount();
                    }

                    @Override
                    public boolean hasStableIds() {
                        return originalAdapter.hasStableIds();
                    }

                    @Override
                    public boolean isEmpty() {
                        return originalAdapter.isEmpty();
                    }

                    @Override
                    public void registerDataSetObserver(DataSetObserver observer) {
                        originalAdapter.registerDataSetObserver(observer);

                    }

                    @Override
                    public void unregisterDataSetObserver(DataSetObserver observer) {
                        originalAdapter.unregisterDataSetObserver(observer);

                    }

                    @Override
                    public boolean areAllItemsEnabled() {
                        return originalAdapter.areAllItemsEnabled();
                    }

                    @Override
                    public boolean isEnabled(int position) {
                        return originalAdapter.isEnabled(position);
                    }

                });

            }

        });

        // alert.getListView().setBackgroundColor(ApplicationView.getApplicationView().getResources().getColor(R.color.timeline_segment_color));

        alert.show();
    }

    /**
     * Display the behaviors available for selection, starting with basic, cached, public.
     */
//    public void displayActionBrowser(final EventHolder eventHolder) {
//
//        // Get list of behaviors available for selection
//        int actionScriptCount = getClay().getCache().getScripts().size();
//        final String[] actionScripts = new String[actionScriptCount];
//        for (int i = 0; i < actionScriptCount; i++) {
//            Script cachedScript = device.getClay().getCache().getScripts().get(i);
//            actionScripts[i] = cachedScript.getTag();
//        }
//
//        // Show the list of behaviors
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setItems(actionScripts, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int itemIndex) {
//
//                // <HACK>
//                Script selectedScript = getClay().getCache().getScripts().get(itemIndex);
//                // </HACK>
//
//                // Assign the action state
//                Action action = getClay().getStore().getAction(selectedScript);
//
//                replaceEventHolder(eventHolder, action);
//
//                refreshTimelineView(); // <HACK />
//            }
//        });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }

    private Clay getClay () {
        return device.getClay();
    }

    public Device getDevice() {
        return this.device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    private void displayDesignerView (EventHolder eventHolder) {
        if (eventDesignerView == null) {
            eventDesignerView = new EventDesignerView(getDevice(), this);
        }
    }

    public EventDesignerView getEventDesigner () {
        if (eventDesignerView == null) {
            eventDesignerView = new EventDesignerView(getDevice(), this);
        }
        return eventDesignerView;
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

            resetEventViews();
            refreshTimelineView();
            refreshAvatarView();

            if (!hasSelectedEventHolders()) {
                final EventHolder eventHolder = (EventHolder) eventHolders.get (position);
                displayUpdateOptions(eventHolder);
            }

            return true;
        }
    }

    // Based on: http://stackoverflow.com/questions/10276251/how-to-animate-a-view-with-translate-animation-in-android
    public void expandEventView2 (EventHolder eventHolder, final View view, int translateDuration)
    {
        FrameLayout root = (FrameLayout) ApplicationView.getApplicationView().findViewById(R.id.application_view);
        DisplayMetrics dm = new DisplayMetrics();
        ApplicationView.getApplicationView().getWindowManager().getDefaultDisplay().getMetrics( dm );
        int statusBarOffset = dm.heightPixels - root.getMeasuredHeight();

        int originalPos[] = new int[2];
        view.getLocationOnScreen( originalPos );

        /*
        int xDest = dm.widthPixels/2;
        xDest -= (view.getMeasuredWidth()/2);
        int yDest = dm.heightPixels/2 - (view.getMeasuredHeight()/2) - statusBarOffset;
        */

//        int xDest = destinationPoint.x;
//        int yDest = destinationPoint.y;


//        final int amountToMoveRight = xDest - originalPos[0];
//        final int amountToMoveDown = yDest - originalPos[1];
        final int amountToMoveRight = 0;
        final int amountToMoveDown = 150;
        TranslateAnimation animation = new TranslateAnimation(0, amountToMoveRight, 0, amountToMoveDown);
        animation.setDuration(translateDuration);
        // animation.setFillAfter(true);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                AbsListView.LayoutParams params = (AbsListView.LayoutParams) view.getLayoutParams();
                params.height += amountToMoveDown;
//                params.bottomMargin += amountToMoveDown;
                // view.setLayoutParams(params);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        view.startAnimation (animation);
    }

    public void expandEventView(EventHolder eventHolder) {
        if (eventHolder.isStateVisible()) {

            //getEventHolderByPosition()

            //final View view = (EventHolder) eventHolders.get (position);
//            int eventHolderIndex = eventHolders.indexOf(eventHolder);
//            View this.getChildAt(eventHolderIndex);
            

//            View topView = (View) view.findViewById(R.id.timeline_top_segment);
//            topView.getLayoutParams().height = 0;
//            View bottomView = (View) view.findViewById(R.id.timeline_bottom_segment);
//            bottomView.getLayoutParams().height = 0;

            eventHolder.setStateVisible(false);
        } else {
            resetEventViews();

//            View topView = (View) view.findViewById(R.id.timeline_top_segment);
//            topView.getLayoutParams().height = 200;
//            View bottomView = (View) view.findViewById(R.id.timeline_bottom_segment);
//            bottomView.getLayoutParams().height = 200;

            eventHolder.setStateVisible(true);
        }

//            View topView = (View) view.findViewById(R.id.event_upper_layout);
//            if (topView.getLayoutParams().height != 200) {
//                topView.getLayoutParams().height = 200;
////                topView.setVisibility(VISIBLE);
//            } else {
//                topView.getLayoutParams().height = 0;
////                topView.setVisibility(GONE);
//            }
//
//            View bottomView = (View) view.findViewById(R.id.event_bottom_layout);
//            if (bottomView.getLayoutParams().height != 200) {
//                bottomView.getLayoutParams().height = 200;
////                bottomView.setVisibility(VISIBLE);
//            } else {
//                bottomView.getLayoutParams().height = 0;
////                bottomView.setVisibility(GONE);
//            }

        Log.v ("Touch", "longTouch");

//            redrawEventView (topView);
//            redrawEventView (bottomView);

        refreshTimelineView();
        refreshAvatarView();
    }

    // <HACK>
    public static TimelineView getTimelineView() {
        return TimelineView.timelineView;
    }

    private static TimelineView timelineView = null;
    // </HACK>

    public void resetViewBackgrounds () {

        int childCount = this.getChildCount();
        View view;
        int i = 0;
        for ( ; i < childCount; i++) {
            view = this.getChildAt(i);

            view.setBackgroundColor(Color.TRANSPARENT);
            view.requestLayout();
            view.invalidate();
        }
    }

    public void resetHighlights () {
        for (int i = 0; i < eventHolders.size(); ) {
            if (eventHolders.get(i).getType().equals("highlight")) {
                eventHolders.remove(i);
                continue;
            }
            i++;
        }
        refreshTimelineView();
    }

    public void resetEventViews () {
        if (eventHolders != null && eventHolders.size() > 0) {
            for (EventHolder eventHolder : eventHolders) {
                eventHolder.setStateVisible(false);
            }
//            refreshTimelineView();
//            refreshAvatarView();
        }
    }

    public int findNearestTimelineIndex (int x, int y) {
        View view = this.getViewByPosition(x, y);
        EventHolder eventHolder = getEventHolderByPosition(x, y);
        int index = -1;

        // TODO: Insert highlight _between_ existing events (temporary "highlight" view? EventHolder for action once added?)
//        if (view != null) {
//            view.setBackgroundColor(Color.LTGRAY);
//            view.requestLayout();
//            view.invalidate();
//        }


        if (eventHolder != null) {
            if (eventHolder.getEvent() != null && eventHolder.getEvent().getAction() != null) {
                Log.v("Nearby", "type: " + eventHolder.getEvent().getAction().getTag());
                index = this.getPositionForView(view);
            }
        }

        // TODO: Get nearby action points (for conversational interaction). And type.

        return index;
    }

    public void findNearbyViews(int x, int y) {
        View view = this.getViewByPosition(x, y);
        EventHolder eventHolder = getEventHolderByPosition(x, y);
        Log.v("Nearby", "view: " + view);
        Log.v("Nearby", "eventHolder: " + eventHolder);

        if (view != null) {
            view.setBackgroundColor(Color.LTGRAY);
            view.requestLayout();
            view.invalidate();
        }

        if (eventHolder != null) {
            if (eventHolder.getEvent() != null && eventHolder.getEvent().getAction() != null) {
                Log.v("Nearby", "type: " + eventHolder.getEvent().getAction().getTag());
            }
        }

        // TODO: Get nearby action points (for conversational interaction). And type.
    }

    private class EventHolderTouchReleaseListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
            Log.v("Gesture_Log", "OnItemClickListener from CustomListView");

            final EventHolder eventHolder = (EventHolder) eventHolders.get (position);

            expandEventView (eventHolder);
//            expandEventView2 (eventHolder, view, 100);



            // TODO: Show options for editing...

//            // Check if the list item was a constructor
//            if (eventHolder.getEvent().equals("create")) {
//                if (eventHolder.tag == "create") {
//                    // Nothing?
//                }
//                // TODO: (?)
//
//            } else if (!eventHolder.getType().equals("create") && !eventHolder.getType().equals("choose")) {
//
//                if (eventHolder.getType().equals("complex")) {
//
//                    decomposeEventHolder(eventHolder);
//                    return true;
//
//                } else {
//
//                    displayEventDesigner(eventHolder);
//                    return true;
//
//                }
//
//                // Request the ListView to be redrawn so the views in it will be displayed
//                // according to their updated state information.
////                refreshTimelineView();
//            }
        }
    }/**
     * Returns true if a placeholder event is found in the sequence.
     * @return
     */
    private boolean hasPlaceholder() {
        for (EventHolder eventHolder : eventHolders) {
            if (eventHolder.getType().equals("choose")) {
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
//                refreshTimelineView();
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
//                refreshTimelineView();

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
