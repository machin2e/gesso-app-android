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
import camp.computer.clay.system.BehaviorScript;
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
    }

    public void setEventHolders(Timeline timeline) {
        this.eventHolders.clear();
        this.adapter.notifyDataSetChanged();

//        this.eventHolders.addUnit(new EventHolder("view", "", TimelineDeviceAdapter.SYSTEM_CONTROL_LAYOUT));

        // create some objects... and addUnit them into the array list
        if (!TimelineListView.HIDE_ABSTRACT_OPTION) {
            this.eventHolders.add(new EventHolder("abstract", "", TimelineDeviceAdapter.SYSTEM_CONTROL_LAYOUT));
        }

        // Create and addUnit eventHolders for each behavior
        createTimelineEvents (timeline);

        // Add "create" option
        this.eventHolders.add(new EventHolder("create", "", TimelineDeviceAdapter.SYSTEM_CONTROL_LAYOUT));

        // Add "update" firmware option
        // TODO: Conditionally show this, only if firmware update is available
        // this.eventHolders.addUnit(new EventHolder("update firmware", "", TimelineDeviceAdapter.SYSTEM_CONTROL_LAYOUT));

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
    }

    private void initLayout() {
        if (TimelineListView.HIDE_LIST_ITEM_SEPARATOR) {
            setDivider(null);
            setDividerHeight(0);
        }
    }

    private void initTouchListeners() {
        setOnTouchListener(new EventHolderTouchListener());
        setOnItemClickListener(new EventHolderTouchReleaseListener());
        setOnItemLongClickListener(new EventHolderLongTouchListener());
        setOnDragListener(new EventHolderTouchDragListener());
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

                    removeEventHolder (event);

                } else if (behaviorOptions[itemIndex].toString().equals("update")) {

                    displayUpdateOptions (event);

                } else if (behaviorOptions[itemIndex].toString().equals("replace")) {

                    displayBehaviorFinder(event);

                } else if (behaviorOptions[itemIndex].toString().equals("select") || behaviorOptions[itemIndex].toString().equals("deselect")) {

                    if (behaviorOptions[itemIndex].toString().equals("select")) {
                        selectEventHolder (event);
                    } else if (behaviorOptions[itemIndex].toString().equals("deselect")) {
                        deselectEventHolder (event);
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
//                // ...then addUnit it to the device.
//                String behaviorUuid = eventHolder.getEvent().getBehavior().getUuid().toString();
//                unit.send("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getBehaviorState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay ().getStore().storeBehaviorState(behaviorState);
//                eventHolder.getEvent().setBehavior(eventHolder.getEvent().getBehavior(), behaviorState);
//                //getClay ().getStore().updateBehaviorState(behaviorState);
//                getClay ().getStore().updateTimeline(unit.getTimeline());
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

                // Extract behaviors from the selected event holders and addUnit them to the behavior package.
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
//                // ...then addUnit it to the device...
//                String behaviorUuid = eventHolder.getEvent().getBehavior().getUuid().toString();
//                unit.send("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getBehaviorState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay().getStore().storeBehaviorState(behaviorState);
//                eventHolder.getEvent().setBehavior(eventHolder.getEvent().getBehavior(), behaviorState);
//                //getClay ().getStore().updateBehaviorState(behaviorState);
//                getClay().getStore().updateTimeline(unit.getTimeline());
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
//                // ...then addUnit it to the device...
//                String behaviorUuid = eventHolder.getEvent().getBehavior().getUuid().toString();
//                unit.send("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getBehaviorState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay ().getStore().storeBehaviorState(behaviorState);
//                eventHolder.getEvent().setBehavior(eventHolder.getEvent().getBehavior(), behaviorState);
//                //getClay ().getStore().updateBehaviorState(behaviorState);
//                getClay ().getStore().updateTimeline(unit.getTimeline());
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
//                // ...then addUnit it to the device...
//                String behaviorUuid = eventHolder.getEvent().getBehavior().getUuid().toString();
//                unit.send("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getBehaviorState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay ().getStore().storeBehaviorState(behaviorState);
//                eventHolder.getEvent().setBehavior(eventHolder.getEvent().getBehavior(), behaviorState);
//                getClay ().getStore().updateTimeline(unit.getTimeline());
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

    private void selectEventHolder(final EventHolder item) {

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

    private void deselectEventHolder(final EventHolder item) {

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
    }

    /**
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
        Behavior behavior = new Behavior (behaviorScript);
        getClay().getStore().storeBehavior(behavior);

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
//        unit.addBehavior(behaviorUuid);
//        getUnit().addBehavior(behavior);
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
            if (eventHolder.type == TimelineDeviceAdapter.SYSTEM_CONTROL_LAYOUT) {
                if (eventHolder.title == "create") {
                    // Nothing?
                }
                // TODO: (?)

            } else if (eventHolder.type != TimelineDeviceAdapter.SYSTEM_CONTROL_LAYOUT && eventHolder.type != TimelineDeviceAdapter.CONTROL_PLACEHOLDER_LAYOUT) {

                if (eventHolder.type == TimelineDeviceAdapter.COMPLEX_LAYOUT) {

                    decomposeEventHolder(eventHolder);
                    return true;

                } else {

                    displayEventDesigner (eventHolder);
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
            if (eventHolder.type == TimelineDeviceAdapter.SYSTEM_CONTROL_LAYOUT) {

                if (eventHolder.title == "create") {
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
                }
//                else if (eventHolder.title == "abstract") {
//
//                    composeEventHolderSelection();
//
//                }

            } else if (eventHolder.type == TimelineDeviceAdapter.CONTROL_PLACEHOLDER_LAYOUT) {

                displayBehaviorFinder(eventHolder);

            } else {

                if (!hasSelectedEventHolders()) {
                    displayUpdateOptions(eventHolder);
                }

            }

        }

    }

    /**
     * Returns true if a placeholder event is found in the sequence.
     * @return
     */
    private boolean hasPlaceholder() {
        for (EventHolder eventHolder : eventHolders) {
            if (eventHolder.type == TimelineDeviceAdapter.CONTROL_PLACEHOLDER_LAYOUT) {
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

    private int getFirstSelectedEventHolderIndex() {
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
    public void composeEventHolderSelection () {
        Log.v ("Content_Manager", "composeEventHolderSelection");

        // Get list of the selected items
        ArrayList<EventHolder> selectedEventHolders = getSelectedEventHolders();
        ArrayList<String> selectedEventTags = getSelectedEventTags();
        int index = getFirstSelectedEventHolderIndex();

        // Return if there are fewer than two selected items
        if (selectedEventHolders.size() < 2) {
            return;
        }

        // Get the first event in the sequence
        EventHolder firstEvent = selectedEventHolders.get (0);

        // Create new behavior. Add behaviors to it.
        String behaviorListString = TextUtils.join(", ", selectedEventTags);
        Behavior newBehavior = new Behavior(behaviorListString);
        for (EventHolder eventHolder : selectedEventHolders) {
            Behavior b = eventHolder.getEvent().getBehavior();
            newBehavior.addBehavior(b);
        }
        // Store the new behavior
        getClay().getStore().storeBehavior(newBehavior);
        //getClay().addBehavior(newBehavior);

        // Remove old behaviors from timeline in store
        for (EventHolder eventHolder : selectedEventHolders) {
            getClay().getStore().removeEvent(eventHolder.getEvent(), null);
        }

        // Remove old behaviors from the timeline
        for (EventHolder eventHolder : selectedEventHolders) {
            getUnit().getTimeline().removeEvent(eventHolder.getEvent()); // if store behavior successful
        }

        // Create event for the behavior and add it to the unit's timeline
        Event e = new Event(getUnit().getTimeline(), newBehavior);
        getUnit().getTimeline().addEvent(index, e);
        getClay().getStore().storeEvent(e);
        //getClay().notifyChange(e);

        // Store: Reindex the timeline events
        getClay().getStore().storeTimeline(getUnit().getTimeline());

        // View: Remove the selected items from the list
        for (EventHolder eventHolder : selectedEventHolders) {
            eventHolders.remove(eventHolder);
        }
        refreshListViewFromData(); // Update view after removing items from the list

        // <HACK>
        // This removes the specified event from the list and replaces it with an event of a specific type.
        // TODO: Replace view, not eventHolders! (i.e., event.type = TimelineDeviceAdapter.LIGHT_CONTROL_LAYOUT;)
//        int index = eventHolders.indexOf(event);
//        eventHolders.remove(index);
        // Add the new event.
        EventHolder composedEventHolder = new EventHolder(behaviorListString, "", TimelineDeviceAdapter.COMPLEX_LAYOUT);
//        packedEventHolder.eventHolders.addAll(selectedEventHolders); // Add the selected items to the list
        composedEventHolder.summary = "" + selectedEventHolders.size() + " pack";
        eventHolders.add(index, composedEventHolder);
        // </HACK>

        displayUpdateTagOptions (composedEventHolder);

    }

    /**
     * Unpacks the behaviors in a behavior package containing multiple behaviors.
     */
    private void decomposeEventHolder (EventHolder eventHolder) {

        Log.v ("Content_Manager", "decomposeEventHolder");

        // Return if the item is not a complex item.
        if (eventHolder.type != TimelineDeviceAdapter.COMPLEX_LAYOUT) {
            return;
        }

        // Get the list of behaviors in the behavior composition
        ArrayList<Behavior> behaviors = eventHolder.getEvent().getBehavior().getBehaviors();
        Log.v ("Content_Manager", "\tbehaviors.size: " + behaviors.size());

        // Get position of the selected item
        int index = eventHolders.indexOf(eventHolder);
        Log.v("Content_Manager", "\tindex: " + index);

        // Remove the event from the timeline
        getUnit().getTimeline().removeEvent(eventHolder.getEvent());

        // Remove the event from the database
        getClay().getStore().removeEvent(eventHolder.getEvent(), null);

        // Remove the selected item from the list (it will be replaced by the abstracted behviors)
        eventHolders.remove(index);
        refreshListViewFromData(); // Update view after removing items from the list

        // <HACK>
        // This removes the specified event from the list and replaces it with an event of a specific type.
        // TODO: Replace view, not eventHolders! (i.e., event.type = TimelineDeviceAdapter.LIGHT_CONTROL_LAYOUT;)
        // Add the new event.
        for (Behavior behavior : behaviors) {
            // TODO: Restore the behavior's state...
            if (behavior.getBehaviors().size() == 0) {
                getClay().getStore().restoreBehaviorState(behavior);
            }
            Event decomposedEvent = new Event (getUnit ().getTimeline (), behavior);
            getUnit().getTimeline().addEvent(index, decomposedEvent);
            getClay ().getStore ().storeEvent (decomposedEvent);
            EventHolder decomposedEventHolder = new EventHolder (decomposedEvent);
            eventHolders.add (index, decomposedEventHolder);
            index++; // Increment the index of the insertion position
        }

        getClay().getStore().storeTimeline(getUnit().getTimeline());
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
                selectEventHolder(item);
                refreshListViewFromData();
            }

        } else {

            // The selected item is subsequent to the first selected, so select it.
            if (firstSelectedIndex <= index) {

                // Select all items between the first and current selection
                for (int i = firstSelectedIndex; i <= index; i++) {
                    EventHolder item = eventHolders.get(i);
                    selectEventHolder(item);
                }
                // Deselect all items after the current selection
                for (int i = index + 1; i < eventHolders.size(); i++) {
                    EventHolder item = eventHolders.get(i);
                    deselectEventHolder(item);
                }
                refreshListViewFromData();

            }

            // TODO: Handle upward selection case here!

        }
    }
}
