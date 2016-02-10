package camp.computer.clay.sequencer;

/*
* Defines a simple object to be displayed in a list view.
*
* This serves as "placeholder" representing the data for the view corresponding to an object in the
* object model.
*/

import android.graphics.Color;

import java.util.ArrayList;
import java.util.UUID;

import camp.computer.clay.system.Behavior;

public class EventManager {

    private UUID uuid;

    public UUID getUuid () {
        return this.uuid;
    }

    // The UUID of the behavior represented by this object.
    public UUID behaviorUuid;
    private Behavior behavior;

    // TODO: Store the managed Behavior's UUID.
    // TODO: Store reference to the managed Behavior (retrieved via the local cache).

    public String title;

    // for Lights behavior
//    public ArrayList<Boolean> lightStates;
//    public ArrayList<Integer> lightColors;

    // for I/O behavior
//    public ArrayList<Boolean> ioStates; // T or F
//    public ArrayList<Character> ioDirection; // I or O
//    public ArrayList<Character> ioSignalType; // T or P or W
//    public ArrayList<Character> ioSignalValue; // (type T:) T or F

    // for Message
//    public String message;

    // for Wait behavior
//    public int time;

    // for Say
//    public String phrase;

    // for Complex
    public String summary;
    public ArrayList<EventManager> eventManagers;

    public static int DEFAULT_TYPE = TimelineUnitAdapter.IO_CONTROL_LAYOUT;

    public int type; // Used by the custom BaseAdapter to select the layout for the list_item_type_light.

    public boolean selected = false;

    public boolean hasFocus = false;

    public boolean repeat = false;
    public String transform;

    // default constructor
    public EventManager() {
        this("Title", "Subtitle", DEFAULT_TYPE);
    }

    public EventManager(Behavior behavior) {

        // Get behavior type
        if (behavior.getTag().equals("lights")) {
            this.type = TimelineUnitAdapter.LIGHT_CONTROL_LAYOUT;
        } else if (behavior.getTag().equals("io")) {
            this.type = TimelineUnitAdapter.IO_CONTROL_LAYOUT;
        } else if (behavior.getTag().equals("message")) {
            this.type = TimelineUnitAdapter.MESSAGE_CONTROL_LAYOUT;
        } else if (behavior.getTag().equals("wait")) {
            this.type = TimelineUnitAdapter.WAIT_CONTROL_LAYOUT;
        } else if (behavior.getTag().equals("say")) {
            this.type = TimelineUnitAdapter.SAY_CONTROL_LAYOUT;
        }
        initializeType();

        // Get behavior UUID
        this.behaviorUuid = behavior.getUuid();

        // <HACK>
        this.behavior = behavior;
        // </HACK>
    }

    // main constructor
    public EventManager(String title, String subTitle, int type) {
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

    // main constructor
    public EventManager(Behavior behavior, int layoutType) {
        super();

        // Assign instance UUID to the list item
        this.uuid = UUID.randomUUID();

        this.behaviorUuid = behavior.getUuid();
        this.behavior = behavior;

        // Set parameters
        this.title = behavior.getTag();
        this.type = layoutType;

        // Initialize
        this.selected = false;

        // Initialize type
        initializeType();
    }

    public Behavior getBehavior () {
        return this.behavior;
    }

    private void initializeType() {

        if (this.type == TimelineUnitAdapter.LIGHT_CONTROL_LAYOUT) {

//            // Initialize light states to false (off)
//            lightStates = new ArrayList<>();
//            for (int i = 0; i < 12; i++) {
//                lightStates.add(false);
//            }

//            // Initialize light color to blue
//            lightColors = new ArrayList<>();
//            for (int i = 0; i < 12; i++) {
//                lightColors.add(Color.rgb(0, 0, 255));
//            }

        } else if (this.type == TimelineUnitAdapter.IO_CONTROL_LAYOUT) {

//            // Initialize I/O states to false (off)
//            ioStates = new ArrayList<>();
//            ioDirection = new ArrayList<>();
//            ioSignalType = new ArrayList<>();
//            ioSignalValue = new ArrayList<>();
//            for (int i = 0; i < 12; i++) {
//                ioStates.add(false);
//                ioDirection.add('I');
//                ioSignalType.add('T');
//                ioSignalValue.add('L');
//            }

        } else if (this.type == TimelineUnitAdapter.MESSAGE_CONTROL_LAYOUT) {

//            message = "hello";

        } else if (this.type == TimelineUnitAdapter.WAIT_CONTROL_LAYOUT) {

//            this.time = 250;

        } else if (this.type == TimelineUnitAdapter.SAY_CONTROL_LAYOUT) {

//            phrase = "oh, that's great";

        } else if (this.type == TimelineUnitAdapter.COMPLEX_LAYOUT) {

            eventManagers = new ArrayList<>();
            summary = "invalid complex layout";

        }
    }

    // String representation
    public String toString() {
        return this.title;
    }
}
