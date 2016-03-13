package camp.computer.clay.sequencer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;

import camp.computer.clay.system.Clay;
import camp.computer.clay.system.Unit;

public class EventDesignerView {

    private Unit unit;

    private TimelineListView timelineListView;

    public EventDesignerView(Unit unit, TimelineListView timelineListView) {

        this.unit = unit;

        this.timelineListView = timelineListView;
    }

    public Context getContext () {
        return ApplicationView.getApplicationView();
    }

    public Clay getClay () {
        return getUnit().getClay();
    }

    public Unit getUnit () {
        return this.unit;
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
            String lightStateString = eventHolder.getEvent().getBehaviorState().get(0).getState();

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

                // Store: Store the new behavior state and update the event.
//                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getBehaviorState());
                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getBehaviorState().get(0));
                getClay().getStore().storeEvent(eventHolder.getEvent());

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                timelineListView.refreshListViewFromData();
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
//        String stateString = eventHolder.getEvent().getBehavior().getState().getState();
        String stateString = eventHolder.getEvent().getBehaviorState().get(0).getState();
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

                // Store: Store the new behavior state and update the event.
                //getClay().getStore().storeState(eventHolder.getEvent().getBehavior(), eventHolder.getEvent().getBehavior().getState());
//                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getBehaviorState());
                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getBehaviorState().get(0));
                getClay().getStore().storeEvent(eventHolder.getEvent());

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                timelineListView.refreshListViewFromData();

//                // Update the behavior state
//                BehaviorState behaviorState = new BehaviorState(eventHolder.getEvent().getBehavior(), eventHolder.getEvent().getBehavior().getTag(), updatedStateString);
//                eventHolder.getEvent().getBehavior().setState(behaviorState);
//
//                // ...then addUnit it to the device.
//                String behaviorUuid = eventHolder.getEvent().getBehavior().getUuid().toString();
//                unit.send("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getBehaviorState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay ().getStore().storeState(behaviorState);
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
     * @param eventHolder
     */
    public void displayUpdateTagOptions(final EventHolder eventHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle ("Tag the view.");

        // Set up the input
        final EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT); //input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Recover values
        input.setText(eventHolder.title);
        input.setSelection(input.getText().length());

        // Set up the buttons
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Update the state of the behavior
                eventHolder.title = input.getText().toString();

                // TODO: Update the corresponding behavior state... this should propagate back through the object model... and cloud...
//                item.restoreBehavior().setTag(input.getText().toString())
//                item.restoreBehavior().setTag(input.getText().toString());

                // Send changes to unit
                // TODO: "create behavior (...)"
                String tagString = input.getText().toString();
//                unit.send (tagString);

                // Create the behavior
//                Behavior behavior = new Behavior(tagString);
                Log.v("move", "event: " + eventHolder.getEvent());
                Log.v("move", "event.behavior: " + eventHolder.getEvent().getBehavior());
                eventHolder.getEvent().getBehavior().setTag(tagString);

//                // Extract behaviors from the selected event holders and addUnit them to the behavior package.
//                for (EventHolder selectedEventHolder : eventHolder.event) {
//                    Behavior selectedBehavior = selectedEventHolder.getEvent().getBehavior();
//                    behavior.cacheBehavior(selectedBehavior);
//                }

                // Store: Store the new behavior state and update the event.
//                getClay().getStore().storeState(eventHolder.getEvent().getBehavior(), eventHolder.getEvent().getBehavior().getState());
//                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getBehaviorState());
                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getBehaviorState().get(0));
                getClay().getStore().storeBehavior(eventHolder.getEvent().getBehavior());
//                getClay().getStore().storeEvent(eventHolder.getEvent());

                // Refresh the timeline view
                timelineListView.refreshListViewFromData();
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
        String message = eventHolder.getEvent().getBehaviorState().get(0).getState();

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
                eventHolder.updateState(updatedStateString);

                // Store: Store the new behavior state and update the event.
//                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getBehaviorState());
                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getBehaviorState().get(0));
                getClay().getStore().storeEvent(eventHolder.getEvent());

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                timelineListView.refreshListViewFromData();

//                // Update the behavior state
//                BehaviorState behaviorState = new BehaviorState(eventHolder.getEvent().getBehavior(), eventHolder.getEvent().getBehavior().getTag(), stateString);
//                eventHolder.getEvent().setBehavior(eventHolder.getEvent().getBehavior(), behaviorState);
//
//                // ...then addUnit it to the device...
//                String behaviorUuid = eventHolder.getEvent().getBehavior().getUuid().toString();
//                unit.send("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getBehaviorState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay().getStore().storeState(behaviorState);
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
        String phrase = eventHolder.getEvent().getBehaviorState().get(0).getState();

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

                // Store: Store the new behavior state and update the event.
//                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getBehaviorState());
                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getBehaviorState().get(0));
                getClay().getStore().storeEvent(eventHolder.getEvent());

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                timelineListView.refreshListViewFromData();

//                BehaviorState behaviorState = new BehaviorState(eventHolder.getEvent().getBehavior(), eventHolder.getEvent().getBehavior().getTag(), stateString);
////                eventHolder.getEvent().getBehavior().setState(behaviorState);
//                eventHolder.getEvent().setBehavior(eventHolder.getEvent().getBehavior(), behaviorState);
//
//                // ...then addUnit it to the device...
//                String behaviorUuid = eventHolder.getEvent().getBehavior().getUuid().toString();
//                unit.send("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getBehaviorState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay ().getStore().storeState(behaviorState);
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
        int time = Integer.parseInt(eventHolder.getEvent().getBehaviorState().get(0).getState());

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

                // Store: Store the new behavior state and update the event.
//                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getBehaviorState());
                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getBehaviorState().get(0));
                getClay().getStore().storeEvent(eventHolder.getEvent());

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                timelineListView.refreshListViewFromData();

//                // Add wait
//                BehaviorState behaviorState = new BehaviorState (eventHolder.getEvent().getBehavior(), eventHolder.getEvent().getBehavior().getTag(), "" + waitVal.getProgress());
//                eventHolder.getEvent().setBehavior(eventHolder.getEvent().getBehavior(), behaviorState);
//
//                // ...then addUnit it to the device...
//                String behaviorUuid = eventHolder.getEvent().getBehavior().getUuid().toString();
//                unit.send("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getBehaviorState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay ().getStore().storeState(behaviorState);
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
}
