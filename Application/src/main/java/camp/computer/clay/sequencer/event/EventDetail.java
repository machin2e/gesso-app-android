//package camp.computer.clay.sequencer.event;
//
//import android.app.AlertDialog;
//import android.content.DialogInterface;
//import android.text.InputType;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.CompoundButton;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.NumberPicker;
//import android.widget.SeekBar;
//import android.widget.TextView;
//import android.widget.ToggleButton;
//
//import java.util.ArrayList;
//
//import camp.computer.clay.sequencer.TimelineUnitAdapter;
//import camp.computer.clay.sequencer.BehaviorProfile;
//
//public class EventDetail {
//
//    EventDetail () {
//
//    }
//
//    private void displayListItemOptions(final BehaviorProfile item) {
//        int basicBehaviorCount = 3;
//        final String[] behaviorOptions = new String[basicBehaviorCount];
//        // loop, condition, branch
//        behaviorOptions[0] = "update";
//        behaviorOptions[1] = "delete";
//        behaviorOptions[2] = "replace";
//        // TODO: behaviorOptions[3] = (item.selected ? "deselect" : "select");
//        // TODO: behaviorOptions[4] = (item.repeat ? "do once" : "repeat");
//        // TODO: behaviorOptions[5] = "add condition";
//        // TODO: cause/effect (i.e., condition)
//        // TODO: HTTP API interface (general wrapper, with authentication options)
//
//        // Show the list of behaviors
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle("Behavior options");
//        builder.setItems(behaviorOptions, new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int itemIndex) {
//
//                if (behaviorOptions[itemIndex].toString().equals("delete")) {
//
//                    deleteListItem (item);
//
//                } else if (behaviorOptions[itemIndex].toString().equals("update")) {
//
//                    displayUpdateOptions(item);
//
//                } else if (behaviorOptions[itemIndex].toString().equals("replace")) {
//
//                    selectBehaviorType(item);
//
//                } else if (behaviorOptions[itemIndex].toString().equals("select") || behaviorOptions[itemIndex].toString().equals("deselect")) {
//
//                    if (behaviorOptions[itemIndex].toString().equals("select")) {
//                        selectListItem(item);
//                    } else if (behaviorOptions[itemIndex].toString().equals("deselect")) {
//                        deselectListItem(item);
//                    }
//
//                } else if (behaviorOptions[itemIndex].toString().equals("repeat") || behaviorOptions[itemIndex].toString().equals("do once")) {
//
//                    if (behaviorOptions[itemIndex].toString().equals("repeat")) {
//                        repeatListItem(item);
//                    } else if (behaviorOptions[itemIndex].toString().equals("do once")) {
//                        stepListItem(item);
//                    }
//
//                }
//
//                refreshListViewFromData();
//            }
//        });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }
//
//    public void displayUpdateOptions(final BehaviorProfile item) {
//
//        if (item.type == TimelineUnitAdapter.LIGHT_CONTROL_LAYOUT) {
//            displayUpdateLightsOptions(item);
//        } else if (item.type == TimelineUnitAdapter.IO_CONTROL_LAYOUT) {
//            displayUpdateIOOptions(item);
//        } else if (item.type == TimelineUnitAdapter.MESSAGE_CONTROL_LAYOUT) {
//            displayUpdateMessageOptions(item);
//        } else if (item.type == TimelineUnitAdapter.WAIT_CONTROL_LAYOUT) {
//            displayUpdateWaitOptions(item);
//        } else if (item.type == TimelineUnitAdapter.SAY_CONTROL_LAYOUT) {
//            displayUpdateSayOptions(item);
//        }
//
//    }
//
//    public void displayUpdateLightsOptions(final BehaviorProfile item) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle ("Change the channel.");
//        builder.setMessage ("What do you want to do?");
//
//        // Declare transformation layout
//        LinearLayout transformLayout = new LinearLayout (getContext());
//        transformLayout.setOrientation (LinearLayout.VERTICAL);
//
//        // Set up the LED label
//        final TextView lightLabel = new TextView (getContext());
//        lightLabel.setText("Enable LED feedback");
//        lightLabel.setPadding(70, 20, 70, 20);
//        transformLayout.addView(lightLabel);
//
//        LinearLayout lightLayout = new LinearLayout (getContext());
//        lightLayout.setOrientation(LinearLayout.HORIZONTAL);
//        final ArrayList<ToggleButton> lightToggleButtons = new ArrayList<> ();
//        for (int i = 0; i < 12; i++) {
//            final String channelLabel = Integer.toString(i + 1);
//            final ToggleButton toggleButton = new ToggleButton (getContext());
//            toggleButton.setPadding(0, 0, 0, 0);
//            toggleButton.setText(channelLabel);
//            toggleButton.setTextOn(channelLabel);
//            toggleButton.setTextOff(channelLabel);
//
//            // Recover configuration options for event
//            if (item.lightStates.get(i) == true) {
//                toggleButton.setChecked(true);
//            } else {
//                toggleButton.setChecked(false);
//            }
//
//            // e.g., LinearLayout.LayoutParams param = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
//            params.setMargins(0, 0, 0, 0);
//            toggleButton.setLayoutParams(params);
//            lightToggleButtons.add(toggleButton); // Add the button to the list.
//            lightLayout.addView(toggleButton);
//        }
//
//        transformLayout.addView (lightLayout);
//
//        // Assign the layout to the alert dialog.
//        builder.setView (transformLayout);
//
//        // Set up the buttons
//        builder.setPositiveButton ("DONE", new DialogInterface.OnClickListener () {
//            @Override
//            public void onClick (DialogInterface dialog, int which) {
//
//                String transformString = "apply ";
//
//                for (int i = 0; i < 12; i++) {
//
//                    final ToggleButton lightEnableButton = lightToggleButtons.get (i);
//
//                    // LED enable. Is the LED on or off?
//
//                    if (lightEnableButton.isChecked ()) {
//                        transformString = transformString.concat ("T");
//                        item.lightStates.set(i, true);
//                    } else {
//                        transformString = transformString.concat ("F");
//                        item.lightStates.set(i, false);
//                    }
//                    // transformString = transformString.concat (","); // Add comma
//
//                    // TODO: Set LED color.
//
//                    // Add space between channel states.
//                    if (i < (12 - 1)) {
//                        transformString = transformString.concat (" ");
//                    }
//                }
//
//                // TODO: Store the state of the lights in the object associated with the BehaviorProfile
//
//                // Refresh the timeline view
//                refreshListViewFromData();
//            }
//        });
//        builder.setNegativeButton ("Cancel", new DialogInterface.OnClickListener () {
//            @Override
//            public void onClick (DialogInterface dialog, int which) {
//                dialog.cancel ();
//            }
//        });
//
//        builder.show ();
//    }
//
//    public void displayUpdateIOOptions2 (final BehaviorProfile item) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle ("I/O");
//
//        LinearLayout layout = new LinearLayout(getContext());
//
//        NumberPicker numberPicker = new NumberPicker(getContext());
//        numberPicker.setMinValue(1);
//        numberPicker.setMaxValue(12);
//        numberPicker.setWrapSelectorWheel(false);
//        numberPicker.setPadding(0, 0, 5, 0);
//        layout.addView(numberPicker);
//
//        NumberPicker directionPicker = new NumberPicker(getContext());
//        final String[] directionOptions = { "input", "output"};
//        directionPicker.setMinValue(0);
//        directionPicker.setMaxValue(directionOptions.length - 1);
//        directionPicker.setDisplayedValues(directionOptions);
//        directionPicker.setWrapSelectorWheel(false);
//        directionPicker.setPadding(5, 0, 5, 0);
//        layout.addView(directionPicker);
//
//        NumberPicker signalTypePicker = new NumberPicker(getContext());
//        final String[] signalTypeOptions = { "switch", "pulse", "wave" };
//        signalTypePicker.setMinValue(0);
//        signalTypePicker.setMaxValue(signalTypeOptions.length - 1);
//        signalTypePicker.setDisplayedValues(signalTypeOptions);
//        signalTypePicker.setWrapSelectorWheel(false);
//        signalTypePicker.setPadding(5, 0, 5, 0);
//        layout.addView(signalTypePicker);
//
//        NumberPicker signalValuePicker = new NumberPicker(getContext());
//        final String[] signalValueOptions = { "off", "on" };
//        signalValuePicker.setMinValue(0);
//        signalValuePicker.setMaxValue(signalValueOptions.length - 1);
//        signalValuePicker.setDisplayedValues(signalValueOptions);
//        signalValuePicker.setWrapSelectorWheel(false);
//        signalValuePicker.setPadding(5, 0, 0, 0);
//        layout.addView(signalValuePicker);
//
//        //Set a value change listener for NumberPicker
//        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
//            @Override
//            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
//                //Display the newly selected value from picker
////                tv.setText("Selected value : " + values[newVal]);
//            }
//        });
//
//        // Set up the input
////        final EditText input = new EditText(getContext());
//        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
////        input.setInputType(InputType.TYPE_CLASS_TEXT);//input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//        builder.setView(layout);
//
//        // Set up the buttons
//        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                // Update the state of the behavior
////                item.message = input.getText().toString();
//
//                // Refresh the timeline view
//                refreshListViewFromData();
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show ();
//    }
//
//    public void displayUpdateIOOptions (final BehaviorProfile item) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle ("Change the channel.");
//        builder.setMessage ("What do you want to do?");
//
//        // TODO: Populate with the current transform values (if any).
//        // TODO: Specify the units to receive the change.
//
//        // Declare transformation layout
//        LinearLayout transformLayout = new LinearLayout (getContext());
//        transformLayout.setOrientation(LinearLayout.VERTICAL);
//
//        // Channels
//
//        final ArrayList<ToggleButton> channelEnableToggleButtons = new ArrayList<> ();
//        final ArrayList<Button> channelDirectionButtons = new ArrayList<> ();
//        final ArrayList<Button> channelModeButtons = new ArrayList<> ();
//        final ArrayList<ToggleButton> channelValueToggleButtons = new ArrayList<> ();
//
//        // Set up the channel label
//        final TextView channelEnabledLabel = new TextView (getContext());
//        channelEnabledLabel.setText("Enable channels");
//        channelEnabledLabel.setPadding(70, 20, 70, 20);
//        transformLayout.addView(channelEnabledLabel);
//
//        LinearLayout channelEnabledLayout = new LinearLayout (getContext());
//        channelEnabledLayout.setOrientation(LinearLayout.HORIZONTAL);
//        for (int i = 0; i < 12; i++) {
//            final String channelLabel = Integer.toString (i + 1);
//            final ToggleButton toggleButton = new ToggleButton (getContext());
////            toggleButton.setBackgroundColor(Color.TRANSPARENT);
//            toggleButton.setPadding(0, 0, 0, 0);
//            toggleButton.setText(channelLabel);
//            toggleButton.setTextOn(channelLabel);
//            toggleButton.setTextOff (channelLabel);
//
//            // Recover configuration options from event
//            if (item.ioStates.get(i) == true) {
//                toggleButton.setChecked(true);
//            } else {
//                toggleButton.setChecked(false);
//            }
//
//            // e.g., LinearLayout.LayoutParams param = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
//            params.setMargins (0, 0, 0, 0);
//            toggleButton.setLayoutParams(params);
//            channelEnableToggleButtons.add(toggleButton); // Add the button to the list.
//            channelEnabledLayout.addView (toggleButton);
//        }
//        transformLayout.addView (channelEnabledLayout);
//
//        // Set up the label
//        final TextView signalLabel = new TextView (getContext());
//        signalLabel.setText("Set channel direction, mode, and value"); // INPUT: Discrete/Digital, Continuous/Analog; OUTPUT: Discrete, Continuous/PWM
//        signalLabel.setPadding(70, 20, 70, 20);
//        transformLayout.addView (signalLabel);
//
//        // Show I/O options
//        final LinearLayout ioLayout = new LinearLayout (getContext());
//        ioLayout.setOrientation (LinearLayout.HORIZONTAL);
//        for (int i = 0; i < 12; i++) {
//            final String channelLabel = Integer.toString (i + 1);
//            final Button toggleButton = new Button (getContext());
//            toggleButton.setPadding(0, 0, 0, 0);
////            toggleButton.setBackgroundColor(Color.TRANSPARENT);
//
//            // Recover configuration options from event
//            if (item.ioStates.get(i) == true) {
//                toggleButton.setText("" + item.ioDirection.get(i));
//                toggleButton.setEnabled(true);
//            } else {
//                toggleButton.setText(" ");
//                toggleButton.setEnabled (false); // TODO: Initialize with current state at the behavior's location in the loop! That is allow defining _changes to_ an existing state, so always work from grounded state material.
//            }
//
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
//            toggleButton.setLayoutParams(params);
//            channelDirectionButtons.add(toggleButton); // Add the button to the list.
//            ioLayout.addView(toggleButton);
//
//        }
//        transformLayout.addView (ioLayout);
//
//        /*
//        // Set up the I/O mode label
//        final TextView ioModeLabel = new TextView (this);
//        ioModeLabel.setText ("I/O Mode"); // INPUT: Discrete/Digital, Continuous/Analog; OUTPUT: Discrete, Continuous/PWM
//        ioModeLabel.setPadding (70, 20, 70, 20);
//        transformLayout.addView (ioModeLabel);
//        */
//
//        // Show I/O selection mode (Discrete or Continuous)
//        LinearLayout channelModeLayout = new LinearLayout (getContext());
//        channelModeLayout.setOrientation (LinearLayout.HORIZONTAL);
//        for (int i = 0; i < 12; i++) {
//            final Button toggleButton = new Button (getContext());
//            toggleButton.setPadding (0, 0, 0, 0);
////            toggleButton.setBackgroundColor(Color.TRANSPARENT);
//
//            // Recover configuration options from event
//            if (item.ioStates.get(i) == true) {
//                toggleButton.setText("" + item.ioSignalType.get(i));
//                toggleButton.setEnabled(true);
//            } else {
//                toggleButton.setText(" ");
//                toggleButton.setEnabled (false); // TODO: Initialize with current state at the behavior's location in the loop! That is allow defining _changes to_ an existing state, so always work from grounded state material.
//            }
//
//            // e.g., LinearLayout.LayoutParams param = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
//            toggleButton.setLayoutParams(params);
//            channelModeButtons.add(toggleButton); // Add the button to the list.
//            channelModeLayout.addView(toggleButton);
//        }
//        transformLayout.addView (channelModeLayout);
//
//        // Value. Show channel value.
//        LinearLayout channelValueLayout = new LinearLayout (getContext());
//        channelValueLayout.setOrientation (LinearLayout.HORIZONTAL);
////        channelLayout.setLayoutParams (new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT));
//        for (int i = 0; i < 12; i++) {
//            // final String buttonLabel = Integer.toString (i + 1);
//            final ToggleButton toggleButton = new ToggleButton (getContext());
//            toggleButton.setPadding(0, 0, 0, 0);
////            toggleButton.setBackgroundColor(Color.TRANSPARENT);
//            toggleButton.setEnabled(false);
//            toggleButton.setText(" ");
//            toggleButton.setTextOn ("H");
//            toggleButton.setTextOff ("L");
//            // e.g., LinearLayout.LayoutParams param = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
//            params.setMargins(0, 0, 0, 0);
//            toggleButton.setLayoutParams(params);
//            channelValueToggleButtons.add(toggleButton); // Add the button to the list.
//            channelValueLayout.addView(toggleButton);
//
//            // Recover configuration options from event
//            if (item.ioStates.get(i) == true && item.ioSignalType.get(i) == 'T') {
//                if (item.ioSignalValue.get(i) == 'H') {
//                    toggleButton.setEnabled(true);
//                    toggleButton.setChecked(true);
//                } else {
//                    toggleButton.setEnabled(true); // TODO: Initialize with current state at the behavior's location in the loop! That is allow defining _changes to_ an existing state, so always work from grounded state material.
//                    toggleButton.setChecked(false);
//                }
//            } else {
//                toggleButton.setText(" ");
//                toggleButton.setEnabled(false);
//                toggleButton.setChecked(false);
//            }
//        }
//        transformLayout.addView (channelValueLayout);
//
//        // Set up interactivity for channel enable buttons.
//        for (int i = 0; i < 12; i++) {
//
//            final ToggleButton channelEnableButton = channelEnableToggleButtons.get (i);
//            final Button channelDirectionButton = channelDirectionButtons.get (i);
//            final Button channelModeButton = channelModeButtons.get (i);
//            final ToggleButton channelValueToggleButton = channelValueToggleButtons.get (i);
//
//            channelEnableButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//                @Override
//                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//
////                    if (isChecked) {
////                        channelEnableButton.setBackgroundColor(Color.LTGRAY);
////                    } else {
////                        channelEnableButton.setBackgroundColor(Color.TRANSPARENT);
////                    }
//
//                    channelDirectionButton.setEnabled(isChecked);
//                    if (channelDirectionButton.getText().toString().equals(" ")) {
//                        channelDirectionButton.setText("I");
//                    }
//
//                    channelModeButton.setEnabled(isChecked);
//                    if (channelModeButton.getText().toString().equals(" ")) {
//                        channelModeButton.setText("T");
//                    }
//
//                    if (isChecked == false) {
//                        // Reset the signal value
//                        channelValueToggleButton.setText(" ");
//                        channelValueToggleButton.setChecked(false);
//                        channelValueToggleButton.setEnabled(isChecked);
//                    }
//                }
//            });
//        }
//
//        // Setup interactivity for I/O options
//        for (int i = 0; i < 12; i++) {
//
//            final Button channelDirectionButton = channelDirectionButtons.get (i);
//            final Button channelModeButton = channelModeButtons.get (i);
//            final ToggleButton channelValueToggleButton = channelValueToggleButtons.get (i);
//
//            channelDirectionButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String currentText = channelDirectionButton.getText().toString();
//                    if (currentText.equals(" ")) {
//                        channelDirectionButton.setText("I");
//                        channelModeButton.setEnabled(true);
//
//                        // Update modes for input channel.
////                        if (channelModeButton.getText ().toString ().equals (" ")) {
////                            channelModeButton.setText ("T");
////                        }
//                        channelModeButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                String currentText = channelModeButton.getText().toString();
//                                if (currentText.equals(" ")) { // Do not change. Keep current state.
//
//                                    channelModeButton.setText("T"); // Toggle.
//
//                                    // Update values for output channel.
//                                    channelValueToggleButton.setEnabled(true);
//                                    if (channelValueToggleButton.getText().toString().equals(" ")) {
//                                        channelValueToggleButton.setText("L");
//                                    }
//
//                                } else if (currentText.equals("T")) {
//                                    channelModeButton.setText("W"); // Waveform.
//
//                                    while (channelValueToggleButton.isEnabled()) {
//                                        channelValueToggleButton.performClick(); // Update values for output channel.
//                                    }
//                                } else if (currentText.equals("W")) {
//                                    channelModeButton.setText("P"); // Pulse.
//
//                                    while (channelValueToggleButton.isEnabled()) {
//                                        channelValueToggleButton.performClick(); // Update values for output channel.
//                                    }
//                                } else if (currentText.equals("P")) {
//                                    channelModeButton.setText("T"); // Toggle
//
//                                    // Update values for output channel.
//                                    while (!channelValueToggleButton.isEnabled()) {
//                                        channelValueToggleButton.performClick(); // Update values for output channel.
//                                    }
//
////                                    if (channelValueToggleButton.getText().toString().equals(" ")) {
////                                        channelValueToggleButton.setText("L");
////                                    }
//                                }
//                            }
//                        });
//
//                    } else if (currentText.equals("I")) {
//
//                        // Change to output signal
//                        channelDirectionButton.setText("O");
//
//                        // Update modes for output channel.
//                        channelModeButton.setEnabled(true);
//                        if (channelModeButton.getText().toString().equals(" ")) {
//                            channelModeButton.setText("T");
//                        }
//
//                        if (channelModeButton.getText().equals("T")) {
//                            channelValueToggleButton.setEnabled(true);
//                            channelValueToggleButton.setChecked(false);
//                        }
//
//                        channelModeButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                String currentText = channelModeButton.getText().toString();
//                                if (currentText.equals(" ")) { // Do not change. Keep current state.
//
//                                    channelModeButton.setText("T"); // Toggle.
//
//                                    // Update values for output channel.
//                                    channelValueToggleButton.setEnabled(true);
//                                    if (channelValueToggleButton.getText().toString().equals(" ")) {
//                                        channelValueToggleButton.setText("L");
//                                    }
//
//                                } else if (currentText.equals("T")) {
//
//                                    // Change to waveform signal
//                                    channelModeButton.setText("W");
//
//                                    // Remove signal value
//                                    channelValueToggleButton.setChecked(false);
//                                    channelValueToggleButton.setText(" ");
//                                    channelValueToggleButton.setEnabled(false);
//                                } else if (currentText.equals("W")) {
//
//                                    // Change to pulse signal
//                                    channelModeButton.setText("P");
//
//                                    // Remove signal value
//                                    channelValueToggleButton.setChecked(false);
//                                    channelValueToggleButton.setText(" ");
//                                    channelValueToggleButton.setEnabled(false);
//                                } else if (currentText.equals("P")) {
//                                    channelModeButton.setText("T"); // Toggle
//
//                                    // Update values for output channel.
//                                    channelValueToggleButton.setEnabled(true);
//                                    if (channelValueToggleButton.getText().toString().equals(" ")) {
//                                        channelValueToggleButton.setText("L");
//                                    }
//                                }
//                            }
//                        });
//
//                    } else if (currentText.equals("O")) {
//
//                        // Change to input signal
//                        channelDirectionButton.setText("I");
//                        channelModeButton.setEnabled(true);
//
//                        // Update modes for input channel.
//                        if (channelModeButton.getText().toString().equals(" ")) {
//                            channelModeButton.setText("T");
//                        }
//
//                        // Remove signal value
//                        channelValueToggleButton.setChecked(false);
//                        channelValueToggleButton.setText(" ");
//                        channelValueToggleButton.setEnabled(false);
//
//                        channelModeButton.setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View v) {
//                                String currentText = channelModeButton.getText().toString();
//                                if (currentText.equals(" ")) { // Do not change. Keep current state.
//                                    channelModeButton.setText("T"); // Toggle.
//                                } else if (currentText.equals("T")) {
//                                    channelModeButton.setText("W"); // Waveform.
//                                    channelValueToggleButton.setEnabled(false); // Update values for output channel.
//                                } else if (currentText.equals("W")) {
//                                    channelModeButton.setText("P"); // Pulse.
//                                    channelValueToggleButton.setEnabled(false); // Update values for output channel.
//                                } else if (currentText.equals("P")) {
//                                    channelModeButton.setText("T"); // Toggle
//                                    channelValueToggleButton.setEnabled(false);
//                                }
//                            }
//                        });
//                    }
//                }
//            });
//        }
//
//        // Assign the layout to the alert dialog.
//        builder.setView (transformLayout);
//
//        // Set up the buttons
//        builder.setPositiveButton ("DONE", new DialogInterface.OnClickListener () {
//
//            @Override
//            public void onClick (DialogInterface dialog, int which) {
//                String transformString = "apply ";
//
//                for (int i = 0; i < 12; i++) {
//
//                    final ToggleButton channelEnableButton = channelEnableToggleButtons.get (i);
//                    final Button channelDirectionButton = channelDirectionButtons.get (i);
//                    final Button channelModeButton = channelModeButtons.get (i);
//                    final Button channelValueToggleButton = channelValueToggleButtons.get (i);
//
//                    // Channel enable. Is the channel enabled?
//
//                    if (channelEnableButton.isChecked ()) {
//                        transformString = transformString.concat ("T");
//                        item.ioStates.set(i, true);
//                    } else {
//                        transformString = transformString.concat ("F");
//                        item.ioStates.set(i, false);
//                    }
//                    // transformString = transformString.concat (","); // Add comma
//
//                    // Channel I/O direction. Is the I/O input or output?
//
//                    if (channelDirectionButton.isEnabled ()) {
//                        String channelDirectionString = channelDirectionButton.getText ().toString ();
//                        transformString = transformString.concat (channelDirectionString);
//                        item.ioDirection.set(i, channelDirectionString.charAt(0));
//                    } else {
//                        String channelDirectionString = channelDirectionButton.getText ().toString ();
//                        transformString = transformString.concat ("-");
//                        item.ioDirection.set(i, channelDirectionString.charAt(0));
//                    }
//                    // transformString = transformString.concat (","); // Add comma
//
//                    // Channel I/O mode. Is the channel toggle switch (discrete), waveform (continuous), or pulse?
//
//                    if (channelModeButton.isEnabled ()) {
//                        String channelModeString = channelModeButton.getText ().toString ();
//                        transformString = transformString.concat (channelModeString);
//                        item.ioSignalType.set(i, channelModeString.charAt(0));
//                    } else {
//                        String channelModeString = channelModeButton.getText ().toString ();
//                        transformString = transformString.concat ("-");
//                        item.ioSignalType.set(i, channelModeString.charAt(0));
//                    }
//
//                    // Channel value.
//                    // TODO: Create behavior transform to apply channel values separately. This transform should only configure the channel operational flow state.
//
//                    if (channelValueToggleButton.isEnabled ()) {
//                        String channelValueString = channelValueToggleButton.getText ().toString ();
//                        transformString = transformString.concat (channelValueString);
//                        item.ioSignalValue.set(i, channelValueString.charAt(0));
//                    } else {
//                        String channelValueString = channelValueToggleButton.getText ().toString ();
//                        transformString = transformString.concat ("-");
//                        item.ioSignalValue.set(i, channelValueString.charAt(0));
//                    }
//
//                    // Add space between channel states.
//                    if (i < (12 - 1)) {
//                        transformString = transformString.concat (" ");
//                    }
//                }
//
//                // Refresh the timeline view
//                refreshListViewFromData();
//            }
//        });
//        builder.setNegativeButton ("Cancel", new DialogInterface.OnClickListener () {
//            @Override
//            public void onClick (DialogInterface dialog, int which) {
//                dialog.cancel ();
//            }
//        });
//
//        builder.show ();
//    }
//
//    public void displayUpdateMessageOptions(final BehaviorProfile item) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle ("what's the message?");
//
//        // Set up the input
//        final EditText input = new EditText(getContext());
//        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        input.setInputType(InputType.TYPE_CLASS_TEXT);//input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//        builder.setView(input);
//
//        // Recover values
//        input.setText(item.message);
//        input.setSelection(input.getText().length());
//
//        // Set up the buttons
//        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                // Update the state of the behavior
//                item.message = input.getText().toString();
//
//                // Refresh the timeline view
//                refreshListViewFromData();
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show ();
//    }
//
//    public void displayUpdateSayOptions(final BehaviorProfile item) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle ("tell me the behavior");
//
//        // Set up the input
//        final EditText input = new EditText(getContext());
//        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        input.setInputType(InputType.TYPE_CLASS_TEXT);//input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
//        builder.setView(input);
//
//        // Recover configuration options from stored object
//        input.setText(item.phrase);
//        input.setSelection(input.getText().length());
//
//        // Set up the buttons
//        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//                // Save configuration options to object
//                item.phrase = input.getText().toString();
//
//                // Refresh the timeline view
//                refreshListViewFromData();
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//
//        builder.show ();
//    }
//
//    public void displayUpdateWaitOptions(final BehaviorProfile item) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle ("Time Transform");
//        builder.setMessage("How do you want to change time?");
//
//        // Declare transformation layout
//        LinearLayout transformLayout = new LinearLayout (getContext());
//        transformLayout.setOrientation(LinearLayout.VERTICAL);
//
//        // Set up the label
//        final TextView waitLabel = new TextView (getContext());
//        waitLabel.setPadding(70, 20, 70, 20);
//        transformLayout.addView(waitLabel);
//
//        final SeekBar waitVal = new SeekBar (getContext());
//        waitVal.setMax(1000);
//        waitVal.setHapticFeedbackEnabled(true); // TODO: Emulate this in the custom interface
//
//        // Recover configuration for event
//        waitLabel.setText ("Wait (" + item.time + " ms)");
//        waitVal.setProgress(item.time);
//
//        waitVal.setOnSeekBarChangeListener (new SeekBar.OnSeekBarChangeListener () {
//            @Override
//            public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
//                waitLabel.setText ("Wait (" + progress + " ms)");
//            }
//
//            @Override
//            public void onStartTrackingTouch (SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch (SeekBar seekBar) {
//
//            }
//        });
//        transformLayout.addView (waitVal);
//
//        // Assign the layout to the alert dialog.
//        builder.setView (transformLayout);
//
//        // Set up the buttons
//        builder.setPositiveButton ("DONE", new DialogInterface.OnClickListener () {
//            @Override
//            public void onClick (DialogInterface dialog, int which) {
//
//                // Create transform string
//                String transformString = "";
//
//                // Add wait
////                transformString = transformString.concat (Integer.toString (waitVal.getProgress ()));
////                Hack_TimeTransformTitle = transformString;
////                Behavior behavior = new Behavior ("time");
////                behavior.setTransform(Hack_TimeTransformTitle);
////                BehaviorConstruct behaviorConstruct = new BehaviorConstruct (perspective);
////                behaviorConstruct.setBehavior(behavior);
////                perspective.addBehaviorConstruct(behaviorConstruct);
//                item.time = waitVal.getProgress ();
//
//                // Refresh the timeline view
//                refreshListViewFromData();
//            }
//        });
//        builder.setNegativeButton ("Cancel", new DialogInterface.OnClickListener () {
//            @Override
//            public void onClick (DialogInterface dialog, int which) {
//                dialog.cancel ();
//            }
//        });
//
//        builder.show ();
//    }
//}
