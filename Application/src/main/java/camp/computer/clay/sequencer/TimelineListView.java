package camp.computer.clay.sequencer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.text.InputType;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.UUID;

import camp.computer.clay.system.Behavior;
import camp.computer.clay.system.BehaviorState;
import camp.computer.clay.system.Clay;
import camp.computer.clay.system.Event;
import camp.computer.clay.system.Timeline;
import camp.computer.clay.system.Unit;

public class TimelineListView extends ListView {

    private static final boolean HIDE_LIST_ITEM_SEPARATOR = true;
    private static final boolean HIDE_ABSTRACT_OPTION = true;

    private Unit unit;

    // The eventHolders to display in _this_ ListView. This has to be repopulated on initialization.
    private ArrayList<EventHolder> eventHolders;

    private TimelineDeviceAdapter adapter;

    public TimelineListView(Context context) {
        super(context);
        init();
    }

    public TimelineListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TimelineListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
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
        this.adapter = new TimelineDeviceAdapter(getContext(), R.layout.list_item_type_light, this.eventHolders);
        setAdapter(adapter);

        // Set up gesture recognition
        initTouchListeners();
    }

    private void createTimelineEvents(Timeline timeline) {

        // Create a behavior profile for each of the unit's behaviors
        for (Event event : timeline.getEvents()) {
            EventHolder eventHolder = new EventHolder(event);
            eventHolders.add(eventHolder);
        }

        Log.v("Behavior_Count", "profile count: " + this.eventHolders.size());
    }

    public void setEventHolders(Timeline timeline) {
        this.eventHolders.clear();
        this.adapter.notifyDataSetChanged();

//        this.eventHolders.add(new EventHolder("view", "", TimelineDeviceAdapter.SYSTEM_CONTROL_LAYOUT));

        // create some objects... and add them into the array list
        if (!TimelineListView.HIDE_ABSTRACT_OPTION) {
            this.eventHolders.add(new EventHolder("abstract", "", TimelineDeviceAdapter.SYSTEM_CONTROL_LAYOUT));
        }

        // Create and add eventHolders for each behavior
        createTimelineEvents (timeline);

        // Add "create" option
        this.eventHolders.add(new EventHolder("create", "", TimelineDeviceAdapter.SYSTEM_CONTROL_LAYOUT));

        // Add "update" firmware option
        // TODO: Conditionally show this, only if firmware update is available
        // this.eventHolders.add(new EventHolder("update firmware", "", TimelineDeviceAdapter.SYSTEM_CONTROL_LAYOUT));

        this.adapter.notifyDataSetChanged();
    }

    /**
     * Set up the eventHolders source and populate the list of eventHolders to show in this ListView.
     */
    public void initData () {
        // TODO: Initialize eventHolders from cache or from remote source in this function. Do this because the ViewPager will destroy this object when moving between pages.
        // TODO: Observe remote eventHolders source and update cached source and notify ListView when eventHolders set changes.

        // setup the eventHolders source
        this.eventHolders = new ArrayList<EventHolder>();

//        // create some objects... and add them into the array list
//        if (!TimelineListView.HIDE_ABSTRACT_OPTION) {
//            this.eventHolders.add(new EventHolder("abstract", "Subtitle", TimelineDeviceAdapter.SYSTEM_CONTROL_LAYOUT));
//        }

        // this.eventHolders.add(new EventHolder("view", "Subtitle", TimelineDeviceAdapter.SYSTEM_CONTROL_LAYOUT));

        // Basic behaviors
//        this.eventHolders.add(new EventHolder("lights", "", TimelineDeviceAdapter.LIGHT_CONTROL_LAYOUT));
//        this.eventHolders.add(new EventHolder("io", "", TimelineDeviceAdapter.IO_CONTROL_LAYOUT));
//        this.eventHolders.add(new EventHolder("message", "turn lights on", TimelineDeviceAdapter.MESSAGE_CONTROL_LAYOUT));
//        this.eventHolders.add(new EventHolder("wait", "500 ms", TimelineDeviceAdapter.WAIT_CONTROL_LAYOUT));
//        this.eventHolders.add(new EventHolder("say", "oh, that's great", TimelineDeviceAdapter.SAY_CONTROL_LAYOUT));

//        this.eventHolders.add(new EventHolder("create", "", TimelineDeviceAdapter.SYSTEM_CONTROL_LAYOUT));
    }

    private void initLayout() {
        if (TimelineListView.HIDE_LIST_ITEM_SEPARATOR) {
            setDivider(null);
            setDividerHeight(0);
        }
    }

    private void initTouchListeners() {
        setOnTouchListener(new ListTouchListener());
        setOnItemClickListener(new ListSelection());
        setOnItemLongClickListener(new ListLongSelection());
        setOnDragListener(new ListDrag());
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

    private void displayEventOptions (final EventHolder event) {
        int basicBehaviorCount = 3;
        final String[] behaviorOptions = new String[basicBehaviorCount];
        // loop, condition, branch
        behaviorOptions[0] = "update";
        behaviorOptions[1] = "delete";
        behaviorOptions[2] = "replace";
        // TODO: behaviorOptions[3] = (event.selected ? "deselect" : "select");
        // TODO: behaviorOptions[4] = (event.repeat ? "do once" : "repeat");
        // TODO: behaviorOptions[5] = "add condition";
        // TODO: cause/effect (i.e., condition)
        // TODO: HTTP API interface (general wrapper, with authentication options)

        // Show the list of behaviors
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Behavior options");
        builder.setItems(behaviorOptions, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int itemIndex) {

                if (behaviorOptions[itemIndex].toString().equals("delete")) {

                    deleteEventHolder(event);

                } else if (behaviorOptions[itemIndex].toString().equals("update")) {

                    displayUpdateOptions(event);

                } else if (behaviorOptions[itemIndex].toString().equals("replace")) {

                    selectBehaviorType(event);

                } else if (behaviorOptions[itemIndex].toString().equals("select") || behaviorOptions[itemIndex].toString().equals("deselect")) {

                    if (behaviorOptions[itemIndex].toString().equals("select")) {
                        selectListItem(event);
                    } else if (behaviorOptions[itemIndex].toString().equals("deselect")) {
                        deselectListItem(event);
                    }

                } else if (behaviorOptions[itemIndex].toString().equals("repeat") || behaviorOptions[itemIndex].toString().equals("do once")) {

                    if (behaviorOptions[itemIndex].toString().equals("repeat")) {
//                        repeatListItem(event);
                    } else if (behaviorOptions[itemIndex].toString().equals("do once")) {
                        stepListItem(event);
                    }

                }

                refreshListViewFromData();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void displayUpdateOptions(final EventHolder event) {

        if (event.type == TimelineDeviceAdapter.LIGHT_CONTROL_LAYOUT) {
            displayUpdateLightsOptions(event);
        } else if (event.type == TimelineDeviceAdapter.IO_CONTROL_LAYOUT) {
            displayUpdateIOOptions(event);
        } else if (event.type == TimelineDeviceAdapter.MESSAGE_CONTROL_LAYOUT) {
            displayUpdateMessageOptions(event);
        } else if (event.type == TimelineDeviceAdapter.WAIT_CONTROL_LAYOUT) {
            displayUpdateWaitOptions(event);
        } else if (event.type == TimelineDeviceAdapter.SAY_CONTROL_LAYOUT) {
            displayUpdateSayOptions(event);
        } else if (event.type == TimelineDeviceAdapter.COMPLEX_LAYOUT) {
            displayUpdateTagOptions(event);
        }

    }

    public void displayUpdateLightsOptions(final EventHolder eventHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle ("Change the channel.");
        builder.setMessage ("What do you want to do?");

        // Declare transformation layout
        LinearLayout transformLayout = new LinearLayout (getContext());
        transformLayout.setOrientation (LinearLayout.VERTICAL);

        // Set up the LED label
        final TextView lightLabel = new TextView (getContext());
        lightLabel.setText("Enable LED feedback");
        lightLabel.setPadding(70, 20, 70, 20);
        transformLayout.addView(lightLabel);

        LinearLayout lightLayout = new LinearLayout (getContext());
        lightLayout.setOrientation(LinearLayout.HORIZONTAL);
        final ArrayList<ToggleButton> lightToggleButtons = new ArrayList<> ();
        for (int i = 0; i < 12; i++) {
            final String channelLabel = Integer.toString(i + 1);
            final ToggleButton toggleButton = new ToggleButton (getContext());
            toggleButton.setPadding(0, 0, 0, 0);
            toggleButton.setText(channelLabel);
            toggleButton.setTextOn(channelLabel);
            toggleButton.setTextOff(channelLabel);

            // Get the behavior state
            String lightStateString = eventHolder.getEvent().getBehavior().getState().getState();

            String[] lightStates = lightStateString.split(" ");

            // Recover configuration options for event
            if (lightStates[i].equals("T")) {
                toggleButton.setChecked(true);
            } else {
                toggleButton.setChecked(false);
            }

            // e.g., LinearLayout.LayoutParams param = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            params.setMargins(0, 0, 0, 0);
            toggleButton.setLayoutParams(params);
            lightToggleButtons.add(toggleButton); // Add the button to the list.
            lightLayout.addView(toggleButton);
        }

        transformLayout.addView (lightLayout);

        // Assign the layout to the alert dialog.
        builder.setView (transformLayout);

        // Set up the buttons
        builder.setPositiveButton ("DONE", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {

                String updatedStateString = "";

//                String[] lightStates = new String[12];

                for (int i = 0; i < 12; i++) {

                    final ToggleButton lightEnableButton = lightToggleButtons.get (i);

                    // LED enable. Is the LED on or off?

                    if (lightEnableButton.isChecked ()) {
                        updatedStateString = updatedStateString.concat ("T");
//                        event.lightStates.set(i, true);
//                        lightStates[i] = "T";
                    } else {
                        updatedStateString = updatedStateString.concat ("F");
//                        event.lightStates.set(i, false);
//                        lightStates[i] = "F";
                    }
                    // transformString = transformString.concat (","); // Add comma

                    // TODO: Set LED color.

                    // Add space between channel states.
                    if (i < (12 - 1)) {
                        updatedStateString = updatedStateString.concat (" ");
                    }
                }

                // Update the behavior state
                // <HACK>
                eventHolder.getEvent().setTimeline(unit.getTimeline());
                // </HACK>
                eventHolder.updateState (updatedStateString);

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                refreshListViewFromData();
            }
        });
        builder.setNegativeButton ("Cancel", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {
                dialog.cancel ();
            }
        });

        builder.show ();
    }

    public void displayUpdateIOOptions (final EventHolder eventHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle ("Change the channel.");
        builder.setMessage ("What do you want to do?");

        // TODO: Populate with the current transform values (if any).
        // TODO: Specify the units to receive the change.

        // Declare transformation layout
        LinearLayout transformLayout = new LinearLayout (getContext());
        transformLayout.setOrientation(LinearLayout.VERTICAL);

        // Channels

        final ArrayList<ToggleButton> channelEnableToggleButtons = new ArrayList<> ();
        final ArrayList<Button> channelDirectionButtons = new ArrayList<> ();
        final ArrayList<Button> channelModeButtons = new ArrayList<> ();
        final ArrayList<ToggleButton> channelValueToggleButtons = new ArrayList<> ();

        // Set up the channel label
        final TextView channelEnabledLabel = new TextView (getContext());
        channelEnabledLabel.setText("Enable channels");
        channelEnabledLabel.setPadding(70, 20, 70, 20);
        transformLayout.addView(channelEnabledLabel);

        // Get behavior state
        String stateString = eventHolder.getEvent().getBehavior().getState().getState();
        final String[] ioStates = stateString.split(" ");

        LinearLayout channelEnabledLayout = new LinearLayout (getContext());
        channelEnabledLayout.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < 12; i++) {
            final String channelLabel = Integer.toString (i + 1);
            final ToggleButton toggleButton = new ToggleButton (getContext());
//            toggleButton.setBackgroundColor(Color.TRANSPARENT);
            toggleButton.setPadding(0, 0, 0, 0);
            toggleButton.setText(channelLabel);
            toggleButton.setTextOn(channelLabel);
            toggleButton.setTextOff (channelLabel);

            // Get behavior state for channel
            char ioState = ioStates[i].charAt(0);

            // Update view
            if (ioState == 'T') {
                toggleButton.setChecked(true);
            } else {
                toggleButton.setChecked(false);
            }

            // e.g., LinearLayout.LayoutParams param = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            params.setMargins (0, 0, 0, 0);
            toggleButton.setLayoutParams(params);
            channelEnableToggleButtons.add(toggleButton); // Add the button to the list.
            channelEnabledLayout.addView (toggleButton);
        }
        transformLayout.addView (channelEnabledLayout);

        // Set up the label
        final TextView signalLabel = new TextView (getContext());
        signalLabel.setText("Set channel direction, mode, and value"); // INPUT: Discrete/Digital, Continuous/Analog; OUTPUT: Discrete, Continuous/PWM
        signalLabel.setPadding(70, 20, 70, 20);
        transformLayout.addView(signalLabel);

        // Show I/O options
        final LinearLayout ioLayout = new LinearLayout (getContext());
        ioLayout.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < 12; i++) {
            final String channelLabel = Integer.toString (i + 1);
            final Button toggleButton = new Button (getContext());
            toggleButton.setPadding(0, 0, 0, 0);
//            toggleButton.setBackgroundColor(Color.TRANSPARENT);

            // Get behavior state for channel
            char ioState = ioStates[i].charAt(0);
            char ioDirectionState = ioStates[i].charAt(1);
            char ioSignalTypeState = ioStates[i].charAt(2);
            char ioSignalValueState = ioStates[i].charAt(3);

            // Update view
            if (ioState == 'T') {
                toggleButton.setText("" + ioDirectionState);
                toggleButton.setEnabled(true);
            } else {
                toggleButton.setText(" ");
                toggleButton.setEnabled (false); // TODO: Initialize with current state at the behavior's location in the loop! That is allow defining _changes to_ an existing state, so always work from grounded state material.
            }

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            toggleButton.setLayoutParams(params);
            channelDirectionButtons.add(toggleButton); // Add the button to the list.
            ioLayout.addView(toggleButton);

        }
        transformLayout.addView (ioLayout);

        /*
        // Set up the I/O mode label
        final TextView ioModeLabel = new TextView (this);
        ioModeLabel.setText ("I/O Mode"); // INPUT: Discrete/Digital, Continuous/Analog; OUTPUT: Discrete, Continuous/PWM
        ioModeLabel.setPadding (70, 20, 70, 20);
        transformLayout.addView (ioModeLabel);
        */

        // Show I/O selection mode (Discrete or Continuous)
        LinearLayout channelModeLayout = new LinearLayout (getContext());
        channelModeLayout.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < 12; i++) {
            final Button toggleButton = new Button (getContext());
            toggleButton.setPadding (0, 0, 0, 0);
//            toggleButton.setBackgroundColor(Color.TRANSPARENT);

            // Get behavior state for channel
            char ioState = ioStates[i].charAt(0);
            char ioDirectionState = ioStates[i].charAt(1);
            char ioSignalTypeState = ioStates[i].charAt(2);
            char ioSignalValueState = ioStates[i].charAt(3);

            // Recover configuration options from event
            if (ioState == 'I') {
                toggleButton.setText("" + ioSignalTypeState);
                toggleButton.setEnabled(true);
            } else {
                toggleButton.setText(" ");
                toggleButton.setEnabled (false); // TODO: Initialize with current state at the behavior's location in the loop! That is allow defining _changes to_ an existing state, so always work from grounded state material.
            }

            // e.g., LinearLayout.LayoutParams param = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            toggleButton.setLayoutParams(params);
            channelModeButtons.add(toggleButton); // Add the button to the list.
            channelModeLayout.addView(toggleButton);
        }
        transformLayout.addView(channelModeLayout);

        // Value. Show channel value.
        LinearLayout channelValueLayout = new LinearLayout (getContext());
        channelValueLayout.setOrientation(LinearLayout.HORIZONTAL);
//        channelLayout.setLayoutParams (new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT));
        for (int i = 0; i < 12; i++) {
            // final String buttonLabel = Integer.toString (i + 1);
            final ToggleButton toggleButton = new ToggleButton (getContext());
            toggleButton.setPadding(0, 0, 0, 0);
//            toggleButton.setBackgroundColor(Color.TRANSPARENT);
            toggleButton.setEnabled(false);
            toggleButton.setText(" ");
            toggleButton.setTextOn ("H");
            toggleButton.setTextOff ("L");
            // e.g., LinearLayout.LayoutParams param = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            params.setMargins(0, 0, 0, 0);
            toggleButton.setLayoutParams(params);
            channelValueToggleButtons.add(toggleButton); // Add the button to the list.
            channelValueLayout.addView(toggleButton);

            // Get behavior state for channel
            char ioState = ioStates[i].charAt(0);
            char ioDirectionState = ioStates[i].charAt(1);
            char ioSignalTypeState = ioStates[i].charAt(2);
            char ioSignalValueState = ioStates[i].charAt(3);

            // Recover configuration options from event
            if (ioState == 'T' && ioSignalTypeState == 'T') {
                if (ioSignalValueState == 'H') {
                    toggleButton.setEnabled(true);
                    toggleButton.setChecked(true);
                } else {
                    toggleButton.setEnabled(true); // TODO: Initialize with current state at the behavior's location in the loop! That is allow defining _changes to_ an existing state, so always work from grounded state material.
                    toggleButton.setChecked(false);
                }
            } else {
                toggleButton.setText(" ");
                toggleButton.setEnabled(false);
                toggleButton.setChecked(false);
            }
        }
        transformLayout.addView(channelValueLayout);

        // Set up interactivity for channel enable buttons.
        for (int i = 0; i < 12; i++) {

            final ToggleButton channelEnableButton = channelEnableToggleButtons.get (i);
            final Button channelDirectionButton = channelDirectionButtons.get (i);
            final Button channelModeButton = channelModeButtons.get (i);
            final ToggleButton channelValueToggleButton = channelValueToggleButtons.get (i);

            channelEnableButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

//                    if (isChecked) {
//                        channelEnableButton.setBackgroundColor(Color.LTGRAY);
//                    } else {
//                        channelEnableButton.setBackgroundColor(Color.TRANSPARENT);
//                    }

                    channelDirectionButton.setEnabled(isChecked);
                    if (channelDirectionButton.getText().toString().equals(" ")) {
                        channelDirectionButton.setText("I");
                    }

                    channelModeButton.setEnabled(isChecked);
                    if (channelModeButton.getText().toString().equals(" ")) {
                        channelModeButton.setText("T");
                    }

                    if (isChecked == false) {
                        // Reset the signal value
                        channelValueToggleButton.setText(" ");
                        channelValueToggleButton.setChecked(false);
                        channelValueToggleButton.setEnabled(isChecked);
                    }
                }
            });
        }

        // Setup interactivity for I/O options
        for (int i = 0; i < 12; i++) {

            final Button channelDirectionButton = channelDirectionButtons.get (i);
            final Button channelModeButton = channelModeButtons.get (i);
            final ToggleButton channelValueToggleButton = channelValueToggleButtons.get (i);

            channelDirectionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String currentText = channelDirectionButton.getText().toString();
                    if (currentText.equals(" ")) {
                        channelDirectionButton.setText("I");
                        channelModeButton.setEnabled(true);

                        // Update modes for input channel.
//                        if (channelModeButton.getText ().toString ().equals (" ")) {
//                            channelModeButton.setText ("T");
//                        }
                        channelModeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String currentText = channelModeButton.getText().toString();
                                if (currentText.equals(" ")) { // Do not change. Keep current state.

                                    channelModeButton.setText("T"); // Toggle.

                                    // Update values for output channel.
                                    channelValueToggleButton.setEnabled(true);
                                    if (channelValueToggleButton.getText().toString().equals(" ")) {
                                        channelValueToggleButton.setText("L");
                                    }

                                } else if (currentText.equals("T")) {
                                    channelModeButton.setText("W"); // Waveform.

                                    while (channelValueToggleButton.isEnabled()) {
                                        channelValueToggleButton.performClick(); // Update values for output channel.
                                    }
                                } else if (currentText.equals("W")) {
                                    channelModeButton.setText("P"); // Pulse.

                                    while (channelValueToggleButton.isEnabled()) {
                                        channelValueToggleButton.performClick(); // Update values for output channel.
                                    }
                                } else if (currentText.equals("P")) {
                                    channelModeButton.setText("T"); // Toggle

                                    // Update values for output channel.
                                    while (!channelValueToggleButton.isEnabled()) {
                                        channelValueToggleButton.performClick(); // Update values for output channel.
                                    }

//                                    if (channelValueToggleButton.getText().toString().equals(" ")) {
//                                        channelValueToggleButton.setText("L");
//                                    }
                                }
                            }
                        });

                    } else if (currentText.equals("I")) {

                        // Change to output signal
                        channelDirectionButton.setText("O");

                        // Update modes for output channel.
                        channelModeButton.setEnabled(true);
                        if (channelModeButton.getText().toString().equals(" ")) {
                            channelModeButton.setText("T");
                        }

                        if (channelModeButton.getText().equals("T")) {
                            channelValueToggleButton.setEnabled(true);
                            channelValueToggleButton.setChecked(false);
                        }

                        channelModeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String currentText = channelModeButton.getText().toString();
                                if (currentText.equals(" ")) { // Do not change. Keep current state.

                                    channelModeButton.setText("T"); // Toggle.

                                    // Update values for output channel.
                                    channelValueToggleButton.setEnabled(true);
                                    if (channelValueToggleButton.getText().toString().equals(" ")) {
                                        channelValueToggleButton.setText("L");
                                    }

                                } else if (currentText.equals("T")) {

                                    // Change to waveform signal
                                    channelModeButton.setText("W");

                                    // Remove signal value
                                    channelValueToggleButton.setChecked(false);
                                    channelValueToggleButton.setText(" ");
                                    channelValueToggleButton.setEnabled(false);
                                } else if (currentText.equals("W")) {

                                    // Change to pulse signal
                                    channelModeButton.setText("P");

                                    // Remove signal value
                                    channelValueToggleButton.setChecked(false);
                                    channelValueToggleButton.setText(" ");
                                    channelValueToggleButton.setEnabled(false);
                                } else if (currentText.equals("P")) {
                                    channelModeButton.setText("T"); // Toggle

                                    // Update values for output channel.
                                    channelValueToggleButton.setEnabled(true);
                                    if (channelValueToggleButton.getText().toString().equals(" ")) {
                                        channelValueToggleButton.setText("L");
                                    }
                                }
                            }
                        });

                    } else if (currentText.equals("O")) {

                        // Change to input signal
                        channelDirectionButton.setText("I");
                        channelModeButton.setEnabled(true);

                        // Update modes for input channel.
                        if (channelModeButton.getText().toString().equals(" ")) {
                            channelModeButton.setText("T");
                        }

                        // Remove signal value
                        channelValueToggleButton.setChecked(false);
                        channelValueToggleButton.setText(" ");
                        channelValueToggleButton.setEnabled(false);

                        channelModeButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String currentText = channelModeButton.getText().toString();
                                if (currentText.equals(" ")) { // Do not change. Keep current state.
                                    channelModeButton.setText("T"); // Toggle.
                                } else if (currentText.equals("T")) {
                                    channelModeButton.setText("W"); // Waveform.
                                    channelValueToggleButton.setEnabled(false); // Update values for output channel.
                                } else if (currentText.equals("W")) {
                                    channelModeButton.setText("P"); // Pulse.
                                    channelValueToggleButton.setEnabled(false); // Update values for output channel.
                                } else if (currentText.equals("P")) {
                                    channelModeButton.setText("T"); // Toggle
                                    channelValueToggleButton.setEnabled(false);
                                }
                            }
                        });
                    }
                }
            });
        }

        // Assign the layout to the alert dialog.
        builder.setView (transformLayout);

        // Set up the buttons
        builder.setPositiveButton ("DONE", new DialogInterface.OnClickListener () {

            @Override
            public void onClick (DialogInterface dialog, int which) {
                String updatedStateString = "";

                for (int i = 0; i < 12; i++) {

                    final ToggleButton channelEnableButton = channelEnableToggleButtons.get (i);
                    final Button channelDirectionButton = channelDirectionButtons.get (i);
                    final Button channelModeButton = channelModeButtons.get (i);
                    final Button channelValueToggleButton = channelValueToggleButtons.get (i);

                    // Channel enable. Is the channel enabled?

                    // Get behavior state for channel
                    char ioState = ioStates[i].charAt(0);
                    char ioDirectionState = ioStates[i].charAt(1);
                    char ioSignalTypeState = ioStates[i].charAt(2);
                    char ioSignalValueState = ioStates[i].charAt(3);

                    // Update the view
                    if (channelEnableButton.isChecked ()) {
                        updatedStateString = updatedStateString.concat ("T");
//                        event.ioStates.set(i, true);
                    } else {
                        updatedStateString = updatedStateString.concat ("F");
//                        event.ioStates.set(i, false);
                    }
                    // transformString = transformString.concat (","); // Add comma

                    // Channel I/O direction. Is the I/O input or output?

                    if (channelDirectionButton.isEnabled ()) {
                        String channelDirectionString = channelDirectionButton.getText ().toString ();
                        updatedStateString = updatedStateString.concat (channelDirectionString);
//                        event.ioDirection.set(i, channelDirectionString.charAt(0));
                    } else {
                        String channelDirectionString = channelDirectionButton.getText ().toString ();
                        updatedStateString = updatedStateString.concat ("-");
//                        event.ioDirection.set(i, channelDirectionString.charAt(0));
                    }
                    // transformString = transformString.concat (","); // Add comma

                    // Channel I/O mode. Is the channel toggle switch (discrete), waveform (continuous), or pulse?

                    if (channelModeButton.isEnabled ()) {
                        String channelModeString = channelModeButton.getText ().toString ();
                        updatedStateString = updatedStateString.concat (channelModeString);
//                        event.ioSignalType.set(i, channelModeString.charAt(0));
                    } else {
                        String channelModeString = channelModeButton.getText ().toString ();
                        updatedStateString = updatedStateString.concat ("-");
//                        event.ioSignalType.set(i, channelModeString.charAt(0));
                    }

                    // Channel value.
                    // TODO: Create behavior transform to apply channel values separately. This transform should only configure the channel operational flow state.

                    if (channelValueToggleButton.isEnabled ()) {
                        String channelValueString = channelValueToggleButton.getText ().toString ();
                        updatedStateString = updatedStateString.concat (channelValueString);
//                        event.ioSignalValue.set(i, channelValueString.charAt(0));
                    } else {
                        String channelValueString = channelValueToggleButton.getText ().toString ();
                        updatedStateString = updatedStateString.concat ("-");
//                        event.ioSignalValue.set(i, channelValueString.charAt(0));
                    }

                    // Add space between channel states.
                    if (i < (12 - 1)) {
                        updatedStateString = updatedStateString.concat (" ");
                    }
                }

                // Update the behavior state
                // <HACK>
                eventHolder.getEvent().setTimeline(unit.getTimeline());
                // </HACK>
                eventHolder.updateState (updatedStateString);

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                refreshListViewFromData();

//                // Update the behavior state
//                BehaviorState behaviorState = new BehaviorState(eventHolder.getEvent().getBehavior(), eventHolder.getEvent().getBehavior().getTag(), updatedStateString);
//                eventHolder.getEvent().getBehavior().setState(behaviorState);
//
//                // ...then add it to the device.
//                String behaviorUuid = eventHolder.getEvent().getBehavior().getUuid().toString();
//                unit.send("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getBehaviorState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay ().getContentManager().storeBehaviorState(behaviorState);
//                eventHolder.getEvent().setBehavior(eventHolder.getEvent().getBehavior(), behaviorState);
//                //getClay ().getContentManager().updateBehaviorState(behaviorState);
//                getClay ().getContentManager().updateTimeline(unit.getTimeline());
//
//                // Refresh the timeline view
//                refreshListViewFromData();
            }
        });
        builder.setNegativeButton ("Cancel", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {
                dialog.cancel ();
            }
        });

        builder.show ();
    }

    /**
     * Update's the tag (or label) of a timeline view.
     * @param item
     */
    public void displayUpdateTagOptions(final EventHolder item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle ("Tag the view.");

        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT); //input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Recover values
        input.setText(item.title);
        input.setSelection(input.getText().length());

        // Set up the buttons
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Update the state of the behavior
                item.title = input.getText().toString();

                // TODO: Update the corresponding behavior state... this should propagate back through the object model... and cloud...
//                item.restoreBehavior().setTag(input.getText().toString())
//                item.restoreBehavior().setTag(input.getText().toString());

                // Send changes to unit
                // TODO: "create behavior (...)"
                String tagString = input.getText().toString();
//                unit.send (tagString);

                // Create the behavior
                Behavior behavior = new Behavior(tagString);

                // Extract behaviors from the selected event holders and add them to the behavior package.
                for (EventHolder selectedEventHolder : item.eventHolders) {
                    Behavior selectedBehavior = selectedEventHolder.getEvent().getBehavior();
                    behavior.addBehavior(selectedBehavior);
                }

                // Refresh the timeline view
                refreshListViewFromData();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show ();
    }

    public void displayUpdateMessageOptions(final EventHolder eventHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle ("what's the message?");

        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);//input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Get the behavior state
        String message = eventHolder.getEvent().getBehavior().getState().getState();

        // Update the view
        input.setText(message);
        input.setSelection(input.getText().length());

        // Set up the buttons
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Update the behavior profile state
                String updatedStateString = input.getText().toString();

                // Update the behavior state
                // <HACK>
                eventHolder.getEvent().setTimeline(unit.getTimeline());
                // </HACK>
                eventHolder.updateState (updatedStateString);

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                refreshListViewFromData();

//                // Update the behavior state
//                BehaviorState behaviorState = new BehaviorState(eventHolder.getEvent().getBehavior(), eventHolder.getEvent().getBehavior().getTag(), stateString);
//                eventHolder.getEvent().setBehavior(eventHolder.getEvent().getBehavior(), behaviorState);
//
//                // ...then add it to the device...
//                String behaviorUuid = eventHolder.getEvent().getBehavior().getUuid().toString();
//                unit.send("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getBehaviorState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay().getContentManager().storeBehaviorState(behaviorState);
//                eventHolder.getEvent().setBehavior(eventHolder.getEvent().getBehavior(), behaviorState);
//                //getClay ().getContentManager().updateBehaviorState(behaviorState);
//                getClay().getContentManager().updateTimeline(unit.getTimeline());
//
//                // Refresh the timeline view
//                refreshListViewFromData();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show ();
    }

    public void displayUpdateSayOptions(final EventHolder eventHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle ("tell me the behavior");

        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);//input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Get behavior state
        String phrase = eventHolder.getEvent().getBehavior().getState().getState();

        // Update the view
        input.setText(phrase);
        input.setSelection(input.getText().length());

        // Set up the buttons
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Save configuration options to object
//                item.phrase = input.getText().toString();

                String updatedStateString = input.getText().toString();

                // Update the behavior state
                // <HACK>
                eventHolder.getEvent().setTimeline(unit.getTimeline());
                // </HACK>
                eventHolder.updateState (updatedStateString);

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                refreshListViewFromData();

//                BehaviorState behaviorState = new BehaviorState(eventHolder.getEvent().getBehavior(), eventHolder.getEvent().getBehavior().getTag(), stateString);
////                eventHolder.getEvent().getBehavior().setState(behaviorState);
//                eventHolder.getEvent().setBehavior(eventHolder.getEvent().getBehavior(), behaviorState);
//
//                // ...then add it to the device...
//                String behaviorUuid = eventHolder.getEvent().getBehavior().getUuid().toString();
//                unit.send("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getBehaviorState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay ().getContentManager().storeBehaviorState(behaviorState);
//                eventHolder.getEvent().setBehavior(eventHolder.getEvent().getBehavior(), behaviorState);
//                //getClay ().getContentManager().updateBehaviorState(behaviorState);
//                getClay ().getContentManager().updateTimeline(unit.getTimeline());
//
//                // Refresh the timeline view
//                refreshListViewFromData();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show ();
    }

    public void displayUpdateWaitOptions(final EventHolder eventHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle ("Time Transform");
        builder.setMessage("How do you want to change time?");

        // Declare transformation layout
        LinearLayout transformLayout = new LinearLayout (getContext());
        transformLayout.setOrientation(LinearLayout.VERTICAL);

        // Set up the label
        final TextView waitLabel = new TextView (getContext());
        waitLabel.setPadding(70, 20, 70, 20);
        transformLayout.addView(waitLabel);

        final SeekBar waitVal = new SeekBar (getContext());
        waitVal.setMax(1000);
        waitVal.setHapticFeedbackEnabled(true); // TODO: Emulate this in the custom interface

        // Get the behavior state
        int time = Integer.parseInt(eventHolder.getEvent().getBehavior().getState().getState());

        // Update the view
        waitLabel.setText ("Wait (" + time + " ms)");
        waitVal.setProgress(time);

        waitVal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                waitLabel.setText("Wait (" + progress + " ms)");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        transformLayout.addView(waitVal);

        // Assign the layout to the alert dialog.
        builder.setView(transformLayout);

        // Set up the buttons
        builder.setPositiveButton ("DONE", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {

                // Create transform string
                String updatedStateString = "" + waitVal.getProgress();

                // Update the behavior state
                // <HACK>
                eventHolder.getEvent().setTimeline(unit.getTimeline());
                // </HACK>
                eventHolder.updateState(updatedStateString);

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                refreshListViewFromData();

//                // Add wait
//                BehaviorState behaviorState = new BehaviorState (eventHolder.getEvent().getBehavior(), eventHolder.getEvent().getBehavior().getTag(), "" + waitVal.getProgress());
//                eventHolder.getEvent().setBehavior(eventHolder.getEvent().getBehavior(), behaviorState);
//
//                // ...then add it to the device...
//                String behaviorUuid = eventHolder.getEvent().getBehavior().getUuid().toString();
//                unit.send("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getBehaviorState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay ().getContentManager().storeBehaviorState(behaviorState);
//                eventHolder.getEvent().setBehavior(eventHolder.getEvent().getBehavior(), behaviorState);
//                getClay ().getContentManager().updateTimeline(unit.getTimeline());
//
//                // Refresh the timeline view
//                refreshListViewFromData();
            }
        });
        builder.setNegativeButton ("Cancel", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {
                dialog.cancel ();
            }
        });

        builder.show ();
    }

    private void selectListItem (final EventHolder item) {

        // Do not select system controllers
        if (item.type == TimelineDeviceAdapter.SYSTEM_CONTROL_LAYOUT || item.type == TimelineDeviceAdapter.CONTROL_PLACEHOLDER_LAYOUT) {
            item.selected = false;
            return;
        }

        // Update state of the object associated with the selected view.
        if (item.selected == false) {
            item.selected = true;
        }

    }

    private void deselectListItem (final EventHolder item) {

        // Update state of the object associated with the selected view.
        if (item.selected == true) {
            item.selected = false;
        }

    }

    private void stepListItem(EventHolder item) {

        if (item.repeat == true) {
            item.repeat = false;
        }
    }

    private void deleteEventHolder(final EventHolder eventHolder) {

        // <HACK>
        // TODO: Make this list update AFTER the data model. Basically update the view, but do all changes to OM first.
        unit.getTimeline().removeEvent(eventHolder.getEvent());
        // </HACK>

        // Update state of the object associated with the selected view.
        eventHolders.remove(eventHolder);

        // Update the view after removing the specified list item
        refreshListViewFromData();

        /*
        // <HACK>
        getClay().getContentManager().updateTimeline(this.unit.getTimeline());
        // </HACK>
        */

    }

    private void selectBehaviorType (final EventHolder event) {

        // Display the behaviors available for selection, starting with basic, cached, public.
        int basicBehaviorCount = unit.getClay().getCacheManager().getCachedBehaviors().size();
        final String[] basicBehaviors = new String[basicBehaviorCount];

        for (int i = 0; i < basicBehaviorCount; i++) {
            Behavior cachedBehavior = unit.getClay().getCacheManager().getCachedBehaviors().get(i);
            basicBehaviors[i] = cachedBehavior.getTag();
        }

        // Show the list of behaviors
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Select a behavior");
        builder.setItems(basicBehaviors, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int itemIndex) {

                // <HACK>
                Behavior selectedBehavior = getClay().getCacheManager().getCachedBehaviors().get(itemIndex);
                // </HACK>
                Log.v("Content_Manager", "to " + selectedBehavior.getUuid());
//                Log.v("Content_Manager", "from:");
//                for (Behavior cb : getClay().getCacheManager().getCachedBehaviors()) {
//                    Log.v("Change_Behavior", "\t" + cb.getUuid().toString());
//                }

                changeEvent(event, selectedBehavior.getUuid());

                refreshListViewFromData();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private Clay getClay () {
        return unit.getClay();
    }

    /**
     * Changes the specified eventHolder's type to the specified type.
     */
    private void changeEvent(final EventHolder eventHolder, UUID behaviorUuid) {

        // TODO: Refactor this method so it _reuses_ Clay's Event object. (Only the Behavior object needs to changed.)

        Log.v ("CM_Log", "changeEvent");
        Log.v ("CM_Log", "\teventHolder = " + eventHolder);
        Log.v ("CM_Log", "\tbehaviorUuid = " + behaviorUuid);

        /* Update data model */

//        // <HACK>
//        // TODO: Make calls to update data model. From those calls, callback to update the views corresponding to the data model updates.
//        getUnit().getTimeline().removeEvent(eventHolder.getEvent());
//        // </HACK>

        /* Update view */

        // <HACK>
        // This removes the specified eventHolder from the list and replaces it with an eventHolder of a specific type.
        // TODO: Update the behavior object referenced by eventHolders, and update the view accordingly (i.e., eventHolder.behavior = <new behavior> then retrieve view for that behavior type).
        int index = eventHolders.indexOf(eventHolder);
        eventHolders.remove(index);
        refreshListViewFromData();

        // TODO: Remove the current eventHolder from the unit by UUID: unit.removeBehavior(oldUuid);

        // Get the behavior from the behavior repository
        Behavior behavior = getClay().getBehavior(behaviorUuid);

//        // Check if the type of behavior for the event's has changed. If so, set a flag to send an
//        // "create behavior" message and an "add behavior" message. If not, leave the flag alone,
//        // indicating that an "update" message should be sent to update the event.
//        boolean sendCreateBehaiorMessage = false;
//        if (eventHolder.getEvent() == null || behavior.getUuid() != eventHolder.getEvent().getBehavior().getUuid()) {
//            sendCreateBehaiorMessage = true;
//        }
//
//        boolean firstEventBehavior = false;
//        if (eventHolder.type == TimelineDeviceAdapter.CONTROL_PLACEHOLDER_LAYOUT) {
//            firstEventBehavior = true;
//        }

        // Assign the behavior state
        BehaviorState behaviorState = null;
        if (behavior.hasScript()) {
            // TODO: Assign a behavior state selected from a behavior selection (search and browse) interface. Or assign the default state.
             behaviorState = new BehaviorState(behavior, behavior.getState().getState());
        }

        // Update the event with the new behavior and state
        Event event = eventHolder.getEvent();
        if (event != null) {

            Log.v ("Content_Manager", "Changing behavior...");
            Log.v ("Content_Manager", "\tbehavior: " + behavior);
            Log.v("Content_Manager", "\tbehavior.state: " + behavior.getState());
            // <HACK>
            Timeline timeline = getUnit().getTimeline();
            event.setTimeline(timeline);
            // </HACK>

            // TODO: getUnit().notifyRemove(event);

            // Update the behavior on the timeline
            behavior.setState(behaviorState);
            event.setBehavior(behavior);

            // Notify Clay of the change
            //getClay().notifyChange (event);
            getUnit().notifyChange(event);

            // Tell the unit to create the behavior, add it to the timeline, and update the state.
//            getUnit().send("create behavior " + event.getUuid() + " \"" + behavior.getTag() + " " + behaviorState.getState() + "\"");
//            getUnit().send("add behavior " + event.getUuid());
            /*
            unit.send("create behavior " + behaviorUuid + " \"" + behavior.getTag() + " " + behaviorState.getState() + "\"");
            unit.send("add behavior " + behaviorUuid);
            // unit.send("update behavior " + behaviorUuid + " \"" + behaviorState.getState() + "\"");
            */

            // <HACK>
//            event.getBehavior().setState(behaviorState);
            // </HACK>
        } else {

            Log.v("Content_Manager", "changeEvent");
            Log.v("Content_Manager", "\tbehavior: " + behavior);
            Log.v("Content_Manager", "\tbehavior.uuid: " + behavior.getUuid());
            Log.v("Content_Manager", "\tbehavior.state: " + behavior.getState());

            // Create event object
            Timeline timeline = this.getUnit().getTimeline();
            event = new Event(timeline, behavior);
            event.getBehavior().setState(behaviorState);

            // Notify Clay of the change to the event
//            getClay().notifyChange(event);
            getUnit().notifyChange(event);

            // Send the event to the device
            // i.e., Tell the unit to create the behavior, add it to the timeline, and update the state.

            // unit.send("update behavior " + behaviorUuid + " \"" + behaviorState.getState() + "\"");
            /*
            unit.send("create behavior " + behaviorUuid + " \"" + behavior.getTag() + " " + behaviorState.getState() + "\"");
            unit.send("add behavior " + behaviorUuid);
            // unit.send("update behavior " + behaviorUuid + " \"" + behaviorState.getState() + "\"");
            */

            // ...then add it to the device...
//            String behaviorUuid = behavior.getUuid().toString();
            // <HACK>
//            event.getBehavior().setState(behaviorState);
            // </HACK>
        }

        // Create and add the new eventHolder to the timeline
        // TODO: DO NOT create a new event, just update the existing one!
        EventHolder replacementEvent = new EventHolder(event);

        // Add the replacement item to the timeline view
        eventHolders.add(index, replacementEvent);

        // Finally, add the behavior to the unit
//        unit.addBehavior(behaviorUuid);
        getUnit().addBehavior(behavior);

        // </HACK>


        // <HACK>
//        getClay().getContentManager().updateTimeline(this.unit.getTimeline());
        // </HACK>
    }

    public Unit getUnit () {
        return this.unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

//    /**
//     * Changes the specified item's type to the specified type.
//     * @param item
//     * @param layoutType
//     */
//    private void changeEvent(final EventHolder item, int layoutType) {
//
//        // <HACK>
//        // This removes the specified item from the list and replaces it with an item of a specific type.
//        // TODO: Update the behavior object referenced by eventHolders, and update the view accordingly (i.e., item.behavior = <new behavior> then retrieve view for that behavior type).
//        int index = eventHolders.indexOf(item);
//        eventHolders.remove(index);
//        refreshListViewFromData();
//
//        // Get the title for the new item
//        String title = "";
//        switch (layoutType) {
//            case TimelineDeviceAdapter.LIGHT_CONTROL_LAYOUT:
//                title = "lights";
//                break;
//            case TimelineDeviceAdapter.IO_CONTROL_LAYOUT:
//                title = "io";
//                break;
//            case TimelineDeviceAdapter.WAIT_CONTROL_LAYOUT:
//                title = "wait";
//                break;
//            case TimelineDeviceAdapter.MESSAGE_CONTROL_LAYOUT:
//                title = "message";
//                break;
//            case TimelineDeviceAdapter.SAY_CONTROL_LAYOUT:
//                title = "say";
//                break;
//            default:
//                title = "";
//                break;
//        }
//
//        // Create and add the new item to the timeline
//        EventHolder replacementItem = new EventHolder(title, "", layoutType);
//
//        // Create or request behavior and cache it for likely use in the near future
//        // TODO: Move this to Clay object model so it's architecture agnostic
//        String behaviorUuid = replacementItem.behaviorUuid.toString();
//        String defaultTransform = "lights F F F F F F F F F F F F";
//        String messageString = "create behavior " + behaviorUuid + " \"" + defaultTransform + "\"";
//        unit.send(messageString);
//        unit.send ("add behavior " + behaviorUuid);
//
//        eventHolders.add(index, replacementItem);
//
//        // </HACK>
//    }

    private class ListDrag implements OnDragListener {

        @Override
        public boolean onDrag(View v, DragEvent event) {
            Log.v ("Gesture_Log", "OnDragLister from CustomListView");
            return false;
        }
    }

    private class ListTouchListener implements OnTouchListener {

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            Log.v ("Gesture_Log", "OnTouchListener from CustomListView");

            return false;
        }
    }

    private class ListLongSelection implements OnItemLongClickListener
    {

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

            final EventHolder item = (EventHolder) eventHolders.get (position);

            // Check if the list item was a constructor
            if (item.type == TimelineDeviceAdapter.SYSTEM_CONTROL_LAYOUT) {
                if (item.title == "create") {
                    // Nothing?
                }
                // TODO: (?)

            } else if (item.type != TimelineDeviceAdapter.SYSTEM_CONTROL_LAYOUT && item.type != TimelineDeviceAdapter.CONTROL_PLACEHOLDER_LAYOUT) {

                if (item.type == TimelineDeviceAdapter.COMPLEX_LAYOUT) {

                    unpackSelectedEvents(item);
                    return true;

                } else {

                    displayEventOptions(item);
                    return true;

                }

                /*
                // Show options
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("You pressed item #" + (position + 1));
                builder.setPositiveButton("OK", null);
                builder.show();
                */

                // Request the ListView to be redrawn so the views in it will be displayed
                // according to their updated state information.
//                refreshListViewFromData();
            }

            return false;
        }
    }

    private class ListSelection implements OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, final View view, final int position, long id)
        {
            Log.v("Gesture_Log", "OnItemClickListener from CustomListView");

            final EventHolder item = (EventHolder) eventHolders.get (position);

            // Check if the list item was a constructor
            if (item.type == TimelineDeviceAdapter.SYSTEM_CONTROL_LAYOUT) {

                if (item.title == "create") {
                    // Add a placeholder if one doesn't already exist
                    if (!hasPlaceholder ()) {

                        // menu:
                        // [ create, branch ]
                        //   - choose
                        //   - behavior

                        String title = "choose"; // color in "human" behavior indicator color
                        String subtitle = "touch to choose behavior"; // super small
                        int type = TimelineDeviceAdapter.CONTROL_PLACEHOLDER_LAYOUT;

                        // Add the behavior to the timeline
                        addData(new EventHolder(title, subtitle, type));

                        // TODO: (?) Create a behavior?
                    }
                } else if (item.title == "abstract") {

                    packSelectedEvents();

                }

            } else if (item.type == TimelineDeviceAdapter.CONTROL_PLACEHOLDER_LAYOUT) {

                selectBehaviorType (item);

            } else {

                if (!hasSelectedItems()) {
                    displayUpdateOptions(item);
                }

            }

        }

    }

    /**
     * Returns true if a placeholder event is found in the sequence.
     * @return
     */
    private boolean hasPlaceholder() {
        for (EventHolder existingItem : eventHolders) {
            if (existingItem.type == TimelineDeviceAdapter.CONTROL_PLACEHOLDER_LAYOUT) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if there are any selected items on the timeline.
     * @return True if there are any selected items. Otherwise, returns false.
     */
    public boolean hasSelectedItems() {
        for (EventHolder eventHolder : this.eventHolders) {
            if (eventHolder.selected) {
                return true;
            }
        }
        return false;
    }

    private ArrayList<EventHolder> getSelectedEventHolders () {
        ArrayList<EventHolder> selectedEventHolders = new ArrayList<>();
        for (EventHolder eventHolder : this.eventHolders) {
            if (eventHolder.selected) {
                selectedEventHolders.add(eventHolder);
            }
        }
        return selectedEventHolders;
    }

    private int getFirstSelectedEventIndex () {
        int index = 0;
        for (EventHolder eventHolder : this.eventHolders) {
            if (eventHolder.selected) {
                break;
            }
            index++;
        }
        return index;
    }

    private void deselectEventHolders () {
        for (EventHolder eventHolder : this.eventHolders) {
            eventHolder.selected = false;
        }
    }

    private ArrayList<String> getSelectedEventTags () {
        ArrayList<String> selectedListItemLabels = new ArrayList<>();
        for (EventHolder eventHolder : this.eventHolders) {
            if (eventHolder.selected) {
                String tag = eventHolder.getEvent().getBehavior().getTag();
                selectedListItemLabels.add(tag);
            }
        }
        return selectedListItemLabels;
    }

    /**
     * Creates a behavior composition from multiple selected behaviors.
     */
    public void packSelectedEvents() {
        Log.v ("Content_Manager", "packSelectedEvents");

//        int index = 0;

        // Get list of the selected items
        ArrayList<EventHolder> selectedEventHolders = getSelectedEventHolders();
        ArrayList<String> selectedEventTags = getSelectedEventTags();
        int index = getFirstSelectedEventIndex();

//        for (EventHolder eventHolder : this.eventHolders) {
//            if (eventHolder.selected) {
//                eventHolder.selected = false; // Deselect the event about to be abstracted
//                selectedEventHolders.add(eventHolder);
//                String tag = eventHolder.getEvent().getBehavior().getTag();
//                selectedEventTags.add(tag);
//            }
//            if (selectedEventHolders.size() == 0) {
//                index++;
//            }
//        }

        // Return if there are fewer than two selected items
        if (selectedEventHolders.size() < 2) {
            return;
        }

        // Get the first event in the sequence
        EventHolder firstEvent = selectedEventHolders.get(0);

        // Create new behavior. Add behaviors to it.
        String behaviorListString = TextUtils.join(", ", selectedEventTags);
        Behavior newBehavior = new Behavior(behaviorListString);
        for (EventHolder event : selectedEventHolders) {
            Behavior b = event.getEvent().getBehavior();
            newBehavior.addBehavior(b);
            Log.v("Content_Manager", "\tCHILDREN: " + b.getUuid());
        }
        getClay().addBehavior(newBehavior);

        // Update timeline
        for (EventHolder eventHolder : selectedEventHolders) {
            getUnit().getTimeline().removeEvent(eventHolder.getEvent());
        }
        Event e = new Event(getUnit().getTimeline(), newBehavior);
        getUnit().getTimeline().addEvent(index, e);
        getClay().notifyChange(e);

        // Remove the selected items from the list
        for (EventHolder eventHolder : selectedEventHolders) {
            eventHolders.remove(eventHolder);
        }
        refreshListViewFromData(); // Update view after removing items from the list

        // Create a new abstract event in the list that represents the selected event sequence at the position of the first event in the sequence
//        getClay().createBehavior();
//        asdf;



        // <HACK>
        // This removes the specified event from the list and replaces it with an event of a specific type.
        // TODO: Replace view, not eventHolders! (i.e., event.type = TimelineDeviceAdapter.LIGHT_CONTROL_LAYOUT;)
//        int index = eventHolders.indexOf(event);
//        eventHolders.remove(index);
        // Add the new event.
        EventHolder packedEventHolder = new EventHolder(behaviorListString, "", TimelineDeviceAdapter.COMPLEX_LAYOUT);
        packedEventHolder.eventHolders.addAll(selectedEventHolders); // Add the selected items to the list
        packedEventHolder.summary = "" + selectedEventHolders.size() + " pack";
        eventHolders.add(index, packedEventHolder);
        // </HACK>

        displayUpdateTagOptions (packedEventHolder);

    }

    /**
     * Unpacks the behaviors in a behavior package containing multiple behaviors.
     */
    private void unpackSelectedEvents(EventHolder eventHolder) {

        Log.v ("Content_Manager", "unpackSelectedEvents");

        // Return if the item is not a complex item.
        if (eventHolder.type != TimelineDeviceAdapter.COMPLEX_LAYOUT) {
            return;
        }

        int index = 0;

        // Get the list of behaviors in the behavior composition
//        ArrayList<EventHolder> abstractedEventHolders = eventHolder.eventHolders;

        // Get the list of behaviors in the behavior composition
        ArrayList<Behavior> behaviors = eventHolder.getEvent().getBehavior().getBehaviors();

        Log.v ("Content_Manager", "\tbehaviors.size: " + behaviors.size());

        // Get position of the selected item
        index = eventHolders.indexOf(eventHolder);

        Log.v ("Content_Manager", "\tindex: " + index);

        // Remove the selected item from the list (it will be replaced by the abstracted behviors)
        eventHolders.remove(index);
        refreshListViewFromData(); // Update view after removing items from the list

//        // Add the abstracted items back to the list
//        for (EventHolder eventHolder2 : abstractedEventHolders) {
//            eventHolders.add(index, eventHolder2);
//            index++; // Increment the index of the insertion position
//        }


        // <HACK>
        // This removes the specified event from the list and replaces it with an event of a specific type.
        // TODO: Replace view, not eventHolders! (i.e., event.type = TimelineDeviceAdapter.LIGHT_CONTROL_LAYOUT;)
//        int index = eventHolders.indexOf(event);
//        eventHolders.remove(index);
        // Add the new event.
        for (Behavior behavior : behaviors) {
            Event e = new Event(getUnit().getTimeline(), behavior);
            EventHolder eh = new EventHolder(e);
//            eventHolders.add(eh);
            eventHolders.add(index, eh);
            index++; // Increment the index of the insertion position
        }

        refreshListViewFromData();
//        EventHolder packedEventHolder = new EventHolder(behaviorListString, "", TimelineDeviceAdapter.COMPLEX_LAYOUT);
//        packedEventHolder.eventHolders.addAll(selectedEventHolders); // Add the selected items to the list
//        packedEventHolder.summary = "" + selectedEventHolders.size() + " pack";
//        eventHolders.add(index, packedEventHolder);
        // </HACK>



        refreshListViewFromData(); // Update view after removing items from the list

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
            EventHolder item = eventHolders.get(i);
            if (item.selected) {
                firstSelectedIndex = i;
                break;
            }
        }

        // Check if the specified position is within the bounds of a view in the ListView.
        // If so, select the item.

        if (firstSelectedIndex == -1) {

            // The item is the first one selected
            if (index < eventHolders.size()) {
                EventHolder item = (EventHolder) eventHolders.get(index);
                selectListItem(item);
                refreshListViewFromData();
            }

        } else {

            // The selected item is subsequent to the first selected, so select it.
            if (firstSelectedIndex <= index) {

                // Select all items between the first and current selection
                for (int i = firstSelectedIndex; i <= index; i++) {
                    EventHolder item = eventHolders.get(i);
                    selectListItem(item);
                }
                // Deselect all items after the current selection
                for (int i = index + 1; i < eventHolders.size(); i++) {
                    EventHolder item = eventHolders.get(i);
                    deselectListItem(item);
                }
                refreshListViewFromData();

            }

            // TODO: Handle upward selection case here!

        }
    }
}
