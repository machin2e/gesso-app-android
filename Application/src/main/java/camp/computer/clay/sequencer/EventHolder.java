package camp.computer.clay.sequencer;

/*
* Defines a simple object to be displayed in a list view.
*
* This serves as "placeholder" representing the data for the view corresponding to an object in the
* object model.
*/

import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

import camp.computer.clay.system.Behavior;
import camp.computer.clay.system.BehaviorState;
import camp.computer.clay.system.Event;

public class EventHolder {

    private UUID uuid;

    public UUID getUuid () {
        return this.uuid;
    }

    // The UUID of the behavior represented by this object.
    public UUID eventUuid;
    private Event event;

    // TODO: Store the managed Behavior's UUID.
    // TODO: Store reference to the managed Behavior (retrieved via the local cache).

    public String title;

    // for Complex
    public String summary;
    public ArrayList<EventHolder> eventHolders;

    public static int DEFAULT_TYPE = TimelineDeviceAdapter.IO_CONTROL_LAYOUT;

    public int type; // Used by the custom BaseAdapter to select the layout for the list_item_type_light.

    public boolean selected = false;

    public boolean hasFocus = false;

    public boolean repeat = false;
//    public String transform;

    // default constructor
    public EventHolder() {
        this("Title", "Subtitle", DEFAULT_TYPE);
    }

    public EventHolder(Event event) {
        super();

        // Assign instance UUID to the list item
        this.uuid = UUID.randomUUID();

        this.eventUuid = event.getUuid();
        this.event = event;

        // Set parameters
        Behavior behavior = event.getBehavior();

        // <HACK>
        // TODO: Update getTitle so it retrieves the value from the associated Event (e.g., from an Event's Behavior.getTag() method).
        this.title = behavior.getTag();
        // <HACK>

        // <HACK>
        // Get the layout type to prepare for view generation
        // TODO: Automatically generate or retrieve layouts dynamically based on UUID here based on behavior events model, rather than referencing them as stored in XML.
        if (behavior.getTag().equals("lights")) {
            this.type = TimelineDeviceAdapter.LIGHT_CONTROL_LAYOUT;
        } else if (behavior.getTag().equals("io")) {
            this.type = TimelineDeviceAdapter.IO_CONTROL_LAYOUT;
        } else if (behavior.getTag().equals("message")) {
            this.type = TimelineDeviceAdapter.MESSAGE_CONTROL_LAYOUT;
        } else if (behavior.getTag().equals("wait")) {
            this.type = TimelineDeviceAdapter.WAIT_CONTROL_LAYOUT;
        } else if (behavior.getTag().equals("say")) {
            this.type = TimelineDeviceAdapter.SAY_CONTROL_LAYOUT;
        } else {
            this.type = TimelineDeviceAdapter.COMPLEX_LAYOUT;
        }
        // </HACK>

        // Initialize
        this.selected = false;

        // Initialize type
        initializeType();
    }

    // TODO: Remove this constructor!
    public EventHolder(String title, String subTitle, int type) {
        super();

        // Assign instance UUID to the list item
        this.uuid = UUID.randomUUID();

        // Set parameters
        this.title = title;
//        this.message = subTitle;
        this.type = type;

        // Initialize
        this.selected = false;

        // Initialize type
        initializeType();
    }

//    // main constructor
//    public EventHolder(Event event, int layoutType) {
//        super();
//
//        // Assign instance UUID to the list item
//        this.uuid = UUID.randomUUID();
//
//        this.eventUuid = event.getUuid();
//        this.event = event;
//
//        // Set parameters
//        Behavior behavior = event.getBehavior();
//        this.title = behavior.getTag();
//        this.type = layoutType;
//
//        // Initialize
//        this.selected = false;
//
//        // Initialize type
//        initializeType();
//    }

    public Event getEvent() {
        return this.event;
    }

    private void initializeType() {

        if (this.type == TimelineDeviceAdapter.LIGHT_CONTROL_LAYOUT) {

//            // Initialize light states to false (off)
//            lightStates = new ArrayList<>();
//            for (int i = 0; i < 12; i++) {
//                lightStates.addUnit(false);
//            }

//            // Initialize light color to blue
//            lightColors = new ArrayList<>();
//            for (int i = 0; i < 12; i++) {
//                lightColors.addUnit(Color.rgb(0, 0, 255));
//            }

        } else if (this.type == TimelineDeviceAdapter.IO_CONTROL_LAYOUT) {

//            // Initialize I/O states to false (off)
//            ioStates = new ArrayList<>();
//            ioDirection = new ArrayList<>();
//            ioSignalType = new ArrayList<>();
//            ioSignalValue = new ArrayList<>();
//            for (int i = 0; i < 12; i++) {
//                ioStates.addUnit(false);
//                ioDirection.addUnit('I');
//                ioSignalType.addUnit('T');
//                ioSignalValue.addUnit('L');
//            }

        } else if (this.type == TimelineDeviceAdapter.MESSAGE_CONTROL_LAYOUT) {

//            message = "hello";

        } else if (this.type == TimelineDeviceAdapter.WAIT_CONTROL_LAYOUT) {

//            this.time = 250;

        } else if (this.type == TimelineDeviceAdapter.SAY_CONTROL_LAYOUT) {

//            phrase = "oh, that's great";

        } else if (this.type == TimelineDeviceAdapter.COMPLEX_LAYOUT) {

            eventHolders = new ArrayList<>();
            summary = "invalid complex layout";

        }
    }

    // String representation
    public String toString() {
        return this.title;
    }

    /**
     * Updates the state of the event's behavior. Creates a new BehaviorState object and associates
     * it to the Behavior for this Event.
     * @param stateString The new state to assign to this event's behavior.
     */
    public void updateState(String stateString) {
        Log.v("CM_Log2", "updateState");
        Log.v("CM_Log2", "\tstateString: " + stateString);
        Log.v("CM_Log2", "\tevent: " + getEvent());
        Log.v("CM_Log2", "\tbehavior: " + getEvent().getBehavior());
        BehaviorState behaviorState = new BehaviorState(getEvent().getBehavior(), stateString);
        Log.v("CM_Log2", "\tbehaviorState: " + behaviorState);
        getEvent().getBehavior().setState(behaviorState);
    }
}
