package camp.computer.clay.sequencer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
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

        // Title
        builder.setTitle ("light");

        // Instructions
        // builder.setMessage ("What do you want to do?");

        // Layout
        LinearLayout transformLayout = new LinearLayout (getContext());
        transformLayout.setOrientation (LinearLayout.VERTICAL);

        // Enable or disable lights
        final TextView lightLabel = new TextView (getContext());
        lightLabel.setText("Choose some lights");
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
            String lightStateString = eventHolder.getEvent().getState().get(0).getState();

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
        transformLayout.addView(lightLayout);

        // Select light color
        final TextView lightColorLabel = new TextView (getContext());
        lightColorLabel.setText("Choose light colors");
        lightColorLabel.setPadding(70, 20, 70, 20);
        transformLayout.addView(lightColorLabel);

        LinearLayout lightColorLayout = new LinearLayout (getContext());
        lightColorLayout.setOrientation(LinearLayout.HORIZONTAL);
        final ArrayList<Button> lightColorButtons = new ArrayList<Button> ();
        final ArrayList<Integer> lightColor = new ArrayList<Integer> ();
        final ArrayList<String> lightHexColor = new ArrayList<String> ();
        for (int i = 0; i < 12; i++) {
            final String channelLabel = Integer.toString(i + 1);
            final Button colorButton = new Button (getContext());
            colorButton.setPadding(0, 0, 0, 0);
            colorButton.setText(Integer.toString(i));

            lightColor.add(0);
            lightHexColor.add ("#ff000000");

            colorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //        // Show color picker
//        ColorPickerDialog colorPickerDialog = ColorPickerDialog.createColorPickerDialog(getContext(), R.style.CustomColorPicker);
//        colorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
//            @Override
//            public void onColorPicked(int color, String hexVal) {
//                System.out.println("Got color: " + color);
//                System.out.println("Got color in hex form: " + hexVal);
//
//                // Make use of the picked color here
//            }
//        });
//        colorPickerDialog.show();

                    ColorPickerDialog colorPickerDialog = ColorPickerDialog.createColorPickerDialog (getContext(), ColorPickerDialog.DARK_THEME);
                    colorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
                        @Override
                        public void onColorPicked(int color, String hexVal) {
                            colorButton.setBackgroundColor(color);
                            lightColor.set(Integer.valueOf(String.valueOf(colorButton.getText())), color);
                            lightHexColor.set (Integer.valueOf(String.valueOf(colorButton.getText())), hexVal);

                            // edited to support big numbers bigger than 0x80000000
                            // int color = (int)Long.parseLong(myColorString, 16);
//                            int r = (color >> 16) & 0xFF;
//                            int g = (color >> 8) & 0xFF;
//                            int b = (color >> 0) & 0xFF;
                        }
                    });
                    colorPickerDialog.show();
                }
            });

            /*
            // Get the behavior state
            String lightStateString = eventHolder.getEvent().getState().get(0).getState();

            String[] lightStates = lightStateString.split(" ");

            // Recover configuration options for event
            if (lightStates[i].equals("T")) {
                colorButton.setChecked(true);
            } else {
                colorButton.setChecked(false);
            }
            */

            // e.g., LinearLayout.LayoutParams param = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            params.setMargins(0, 0, 0, 0);
            colorButton.setLayoutParams(params);
            lightColorButtons.add(colorButton); // Add the button to the list.
            lightColorLayout.addView(colorButton);
        }
        transformLayout.addView (lightColorLayout);

        // Assign the layout to the alert dialog.
        builder.setView (transformLayout);

        // Set up the buttons
        builder.setPositiveButton ("DONE", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {

                String updatedStateString = "";
                Byte[] colorBytesString = new Byte[12 * 3]; // i.e., 12 lights, each with 3 color bytes
                for (int i = 0; i < 12; i++) {

                    final ToggleButton lightEnableButton = lightToggleButtons.get (i);
                    final Button lightColorButton = lightColorButtons.get (i);

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

                    // Get LED color
                    int color = lightColor.get(i);
                    int r = (color & 0x00FF0000) >> 16; // ((color >> 16) & 0xFF);
                    int g = (color & 0x0000FF00) >> 8;
                    int b = (color & 0x000000FF) >> 0;
//                    Log.v ("Color", Integer.toBinaryString(color));
//                    Log.v ("Color", Integer.toBinaryString((color & 0x00FF0000) >> 16));
//                    Log.v ("Color", Integer.toBinaryString((color & 0x0000FF00) >> 8));
//                    Log.v ("Color", Integer.toBinaryString((color & 0x000000FF) >> 0));
                    colorBytesString[3 * i + 0] = (byte) r;
                    colorBytesString[3 * i + 1] = (byte) g;
                    colorBytesString[3 * i + 2] = (byte) b;

                    // Add space between channel states.
                    if (i < (12 - 1)) {
                        updatedStateString = updatedStateString.concat (" ");
                    }
                }

                String colorHexString = "";
                byte[] colorBytes = new byte[12 * 3];
                for (int i = 0; i < 12 * 3; i++) {
                    colorBytes[i] = colorBytesString[i];
                    String s1 = String.format("%8s", Integer.toBinaryString(colorBytesString[i] & 0xFF)).replace(' ', '0');
                    colorHexString += s1 + " "; // Integer.toString((int) colorHexBytes[i]) + " ";
                }
                Log.v("Color", "color hex: " + colorHexString);


                String lightHexColorString = "";
                for (int i = 0; i < lightHexColor.size(); i++) {
                    lightHexColorString += " " + lightHexColor.get(i).substring(3).toUpperCase();
                }
                updatedStateString = lightHexColorString.trim();

                // Update the behavior state
                // <HACK>
                eventHolder.getEvent().setTimeline(unit.getTimeline());
                // </HACK>
                eventHolder.updateState (updatedStateString);

                // Store: Store the new behavior state and update the event.
//                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState());
                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState().get(0));
                getClay().getStore().storeEvent(eventHolder.getEvent());

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
//                getUnit().sendMessage ("start event " + eventHolder.getEvent().getUuid());
//                getUnit().sendMessage ("set event " + eventHolder.getEvent().getUuid() + " action " + eventHolder.getEvent().getAction().getUuid());
//                getUnit().sendMessage ("set event " + eventHolder.getEvent().getUuid() + " state \"light " + eventHolder.getEvent().getState().get(0).getState() + "\""); // <HACK />
                String content = "set event " + eventHolder.getEvent().getUuid() + " state \"" + updatedStateString + "\"";
                Log.v ("Color", content);
                getUnit().sendMessage (content); // <HACK />
                /*
                String packedBytes = new String(colorBytes, Charset.forName("UTF-8")); // "US-ASCII"
                String msg2 = "set event " + eventHolder.getEvent().getUuid() + " state \"color " + packedBytes + "\"";
                getUnit().sendMessage (msg2); // <HACK />
                for (int i = 0; i < msg2.length(); i++) {
                    Log.v ("Color", String.valueOf(Integer.valueOf(msg2.getBytes()[i])));
                }
                */
                // unit.sendMessage("update action " + behaviorUuid + " \"" + behaviorState.getState() + "\"");
                // </HACK>

                // <HACK>
                // TODO: Replace this with a queue.
                getUnit().sendMessageTcp(content);
                // </HACK>

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
//        String stateString = eventHolder.getEvent().getAction().getState().getState();
        String stateString = eventHolder.getEvent().getState().get(0).getState();
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
                //getClay().getStore().storeState(eventHolder.getEvent().getAction(), eventHolder.getEvent().getAction().getState());
//                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState());
                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState().get(0));
                getClay().getStore().storeEvent(eventHolder.getEvent());

                // Send updated state to device
                // <HACK>
                String content = "set event " + eventHolder.getEvent().getUuid() + " state \"" + updatedStateString + "\"";
//                getUnit().sendMessage(content);
                // </HACK>

                // <HACK>
                // TODO: Replace this with a queue.
                getUnit().sendMessageTcp(content);
                // </HACK>

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                timelineListView.refreshListViewFromData();

//                // Update the behavior state
//                State behaviorState = new State(eventHolder.getEvent().getAction(), eventHolder.getEvent().getAction().getTag(), updatedStateString);
//                eventHolder.getEvent().getAction().setState(behaviorState);
//
//                // ...then addUnit it to the device.
//                String behaviorUuid = eventHolder.getEvent().getAction().getUuid().toString();
//                unit.sendMessage("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay ().getStore().storeState(behaviorState);
//                eventHolder.getEvent().setAction(eventHolder.getEvent().getAction(), behaviorState);
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
//                unit.sendMessage (tagString);

                // Create the behavior
                eventHolder.getEvent().getAction().setTag(tagString);

                // Store: Store the new behavior state and update the event.
                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState().get(0));
                getClay().getStore().storeBehavior(eventHolder.getEvent().getAction());

                // Send updated state to device
                // <HACK>
                String content = "set event " + eventHolder.getEvent().getUuid() + " state \"" + tagString + "\"";
//                getUnit().sendMessage(content);
                // </HACK>

                // <HACK>
                // TODO: Replace this with a queue.
                getUnit().sendMessageTcp(content);
                // </HACK>

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

        /* Pop-up title */

        // builder.setTitle ("Message");

        // Declare transformation layout
        LinearLayout transformLayout = new LinearLayout (getContext());
        transformLayout.setOrientation(LinearLayout.VERTICAL);

        /* Message content */

        // Title
        final TextView messageLabel = new TextView (getContext());
        messageLabel.setText("Message");
        messageLabel.setPadding(70, 20, 70, 20);
        transformLayout.addView(messageLabel);

        // Content input field
        final EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        transformLayout.addView(input);

        /* Message type */

        // Get list of devices that have been discovered
        final ArrayList<String> destinationAddressStrings = new ArrayList<String>();

        // Title
        final TextView protocolLabel = new TextView (getContext());
        protocolLabel.setText("Type");
        protocolLabel.setPadding(70, 20, 70, 20);
        transformLayout.addView(protocolLabel);

        // List of types (i.e., TCP, UDP, Mesh, etc.)
        final Spinner messageTypeSpinner = new Spinner (getContext());
        ArrayList<String> messageTypeStrings = new ArrayList<String>();
        messageTypeStrings.add("UDP"); // Internet
        messageTypeStrings.add("TCP"); // Internet
        messageTypeStrings.add("Mesh"); // Clay
        ArrayAdapter<String> messageTypeSpinnerArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, messageTypeStrings); //selected item will look like a spinner set from XML
        messageTypeSpinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        messageTypeSpinner.setAdapter(messageTypeSpinnerArrayAdapter);
        transformLayout.addView(messageTypeSpinner);

        /* Message destination address */

        // Destination label
        final TextView destinationLabel = new TextView (getContext());
        destinationLabel.setText("Destination");
        destinationLabel.setPadding(70, 20, 70, 20);
        transformLayout.addView(destinationLabel);

        // Set destination of message
        final Spinner destinationSpinner = new Spinner (getContext());
        final ArrayAdapter<String> destinationAddressArrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, destinationAddressStrings); //selected item will look like a spinner set from XML
        destinationAddressArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        destinationSpinner.setAdapter(destinationAddressArrayAdapter);
        transformLayout.addView(destinationSpinner);

        // "Other" destination address, specified by user
        final TextView otherDestinationTitle = new TextView (getContext());
        otherDestinationTitle.setText("Other");
        otherDestinationTitle.setPadding(70, 20, 70, 20);
        otherDestinationTitle.setVisibility(View.GONE);
        transformLayout.addView(otherDestinationTitle);

        // "Other" destination input field
        final EditText otherDestinationText = new EditText(getContext());
        otherDestinationText.setInputType(InputType.TYPE_CLASS_TEXT);
        otherDestinationText.setVisibility(View.GONE);
        transformLayout.addView(otherDestinationText);

        /* Set the view */

        builder.setView(transformLayout);

        /* Set up interactivity */

        messageTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = messageTypeSpinner.getItemAtPosition(position).toString();
                if (selectedItemText.equals("UDP") || selectedItemText.equals("TCP")) {
                    // Get list of all discovered devices on the mesh network
                    destinationAddressStrings.clear();
                    // TODO: Get list of all discovered smartphones, tablets, and other devices for interacting with Clay
                    destinationAddressStrings.add(getClay().getInternetBroadcastAddress() + ":4445"); // Broadcast address
                    destinationAddressStrings.add(getClay().getInternetAddress() + ":4445"); // This device's address
                    destinationAddressStrings.add("Other");
                    destinationAddressArrayAdapter.notifyDataSetChanged();
                } else if (selectedItemText.equals("Mesh")) {
                    // Get list of all discovered devices on the mesh network
                    destinationAddressStrings.clear();
                    for (Unit unit : getClay().getUnits()) {
                        destinationAddressStrings.add(unit.getUuid().toString());
                    }
                    // Note: There's no "Other" option for mesh.
                    destinationAddressArrayAdapter.notifyDataSetChanged();
                }

                // Select the first item by default
                destinationSpinner.setSelection(0, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                messageTypeSpinner.setSelection(0, true);
            }
        });

        destinationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = destinationSpinner.getItemAtPosition(position).toString();
                if (selectedItemText.equals("Other")) {
                    otherDestinationTitle.setVisibility(View.VISIBLE);
                    otherDestinationText.setVisibility(View.VISIBLE);
                    otherDestinationText.invalidate();
                } else {
                    otherDestinationTitle.setVisibility(View.GONE);
                    otherDestinationText.setVisibility(View.GONE);
                    otherDestinationText.invalidate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                otherDestinationTitle.setVisibility(View.GONE);
                otherDestinationText.setVisibility(View.GONE);
            }
        });

        /* Perform "automated" interactions to initialize pop-up */

        // TODO:

        /* Initialize the state */

        // Extract state from string representation
        String currentStateString = eventHolder.getEvent().getState().get(0).getState();
        int currentDestinationAddressStringIndex = currentStateString.indexOf(" ");
        int currentContentStringIndex = currentStateString.indexOf(" ", currentDestinationAddressStringIndex + 1);

        String currentTypeString = currentStateString.substring(0, currentDestinationAddressStringIndex);
        String currentDestinationAddressString = currentStateString.substring(currentDestinationAddressStringIndex + 1, currentContentStringIndex);
        String currentContentString = currentStateString.substring (currentContentStringIndex + 1);
        currentContentString = currentContentString.substring(1, currentContentString.length() - 1);

        /* Initialize the pop-up with the state */

        // Message content
        input.setText(currentContentString);
        input.setSelection(input.getText().length());

        // Message type
        int messageTypeIndex = messageTypeSpinnerArrayAdapter.getPosition(currentTypeString);
        messageTypeSpinner.setSelection(messageTypeIndex);

        // Message destination
        int messageDestinationIndex = destinationAddressArrayAdapter.getPosition(currentDestinationAddressString);
        destinationSpinner.setSelection(messageDestinationIndex);

        otherDestinationText.setText (currentDestinationAddressString);

        // Set up the buttons
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Update the behavior profile state
                // e.g., "udp 192.168.1.255:4445 \"hello world\""
                // TODO: "udp none 192.168.1.255:4445 \"hello world\""... make same message string format!
                String updatedStateString = messageTypeSpinner.getSelectedItem().toString();
//                updatedStateString += " none";
                if (!destinationSpinner.getSelectedItem().toString().equals("Other")) {
                    updatedStateString += " " + destinationSpinner.getSelectedItem().toString();
                } else {
                    updatedStateString += " " + otherDestinationText.getText().toString();
                }
                updatedStateString += " \'" + input.getText().toString() + "\'";

                // Update the behavior state
                // <HACK>
                eventHolder.getEvent ().setTimeline (unit.getTimeline());
                // </HACK>
                eventHolder.updateState (updatedStateString);

                // Store: Store the new behavior state and update the event.
//                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState());
                getClay().getStore().storeState (eventHolder.getEvent(), eventHolder.getEvent().getState().get(0));
                getClay().getStore().storeEvent (eventHolder.getEvent());

                // Send updated state to device
                // <HACK>
                String stateToSend = updatedStateString.substring (0, updatedStateString.indexOf(" ")).toLowerCase() + " " + updatedStateString.substring(updatedStateString.indexOf(" ") + 1);
                String content = "set event " + eventHolder.getEvent().getUuid() + " state \"" + stateToSend + "\"";
//                getUnit().sendMessage(content);
                // </HACK>

                // <HACK>
                // TODO: Replace this with a queue.
                getUnit().sendMessageTcp(content);
                // </HACK>

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                timelineListView.refreshListViewFromData();

//                // Update the behavior state
//                State behaviorState = new State(eventHolder.getEvent().getAction(), eventHolder.getEvent().getAction().getTag(), stateString);
//                eventHolder.getEvent().setAction(eventHolder.getEvent().getAction(), behaviorState);
//
//                // ...then addUnit it to the device...
//                String behaviorUuid = eventHolder.getEvent().getAction().getUuid().toString();
//                unit.sendMessage("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay().getStore().storeState(behaviorState);
//                eventHolder.getEvent().setAction(eventHolder.getEvent().getAction(), behaviorState);
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

    public void displayUpdateToneOptions(final EventHolder eventHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle ("Tone");
        builder.setMessage("Choose frequency and duration.");

        // Declare transformation layout
        LinearLayout transformLayout = new LinearLayout (getContext());
        transformLayout.setOrientation(LinearLayout.VERTICAL);

        // Set up the frequency
        final TextView frequencyLabel = new TextView (getContext());
        frequencyLabel.setPadding(70, 20, 70, 20);
        transformLayout.addView(frequencyLabel);

        final SeekBar frequencyVal = new SeekBar (getContext());
        frequencyVal.setMax(5000);
        frequencyVal.setHapticFeedbackEnabled(true); // TODO: Emulate this in the custom interface

        // Get the behavior state
        String stateString = eventHolder.getEvent().getState().get(0).getState();
        String frequencyString = stateString.split(" ")[1];
        int frequency = Integer.parseInt(frequencyString);

        // Update the view
        frequencyLabel.setText("Frequency (" + frequency + " Hz)");
        frequencyVal.setProgress(frequency);

        frequencyVal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                frequencyLabel.setText("Frequency (" + progress + " Hz)");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        transformLayout.addView(frequencyVal);

        // Set up the duration
        final TextView durationLabel = new TextView (getContext());
        durationLabel.setPadding(70, 20, 70, 20);
        transformLayout.addView(durationLabel);

        final SeekBar durationVal = new SeekBar (getContext());
        durationVal.setMax(5000);
        durationVal.setHapticFeedbackEnabled(true); // TODO: Emulate this in the custom interface

        // Get the behavior state
        String durationString = stateString.split(" ")[3];
        int duration = Integer.parseInt(durationString);

        // Update the view
        durationLabel.setText("Duration (" + duration + " ms)");
        durationVal.setProgress(duration);

        durationVal.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                durationLabel.setText("Duration (" + progress + " ms)");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        transformLayout.addView(durationVal);

        // Assign the layout to the alert dialog.
        builder.setView(transformLayout);

        // Set up the buttons
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Create transform string
                String updatedStateString = "frequency " + frequencyVal.getProgress() + " hz " + durationVal.getProgress() + " ms";

                // Update the behavior state
                // <HACK>
                eventHolder.getEvent().setTimeline(unit.getTimeline());
                // </HACK>
                eventHolder.updateState(updatedStateString);

                // Store: Store the new behavior state and update the event.
//                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState());
                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState().get(0));
                getClay().getStore().storeEvent(eventHolder.getEvent());

                // Send updated state to device
                // <HACK>
                String content = "set event " + eventHolder.getEvent().getUuid() + " state \"" + updatedStateString + "\"";
//                getUnit().sendMessage(content);
                // </HACK>

                // <HACK>
                // TODO: Replace this with a queue.
                getUnit().sendMessageTcp(content);
                // </HACK>

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                timelineListView.refreshListViewFromData();

//                // Update the behavior state
//                State behaviorState = new State(eventHolder.getEvent().getAction(), eventHolder.getEvent().getAction().getTag(), stateString);
//                eventHolder.getEvent().setAction(eventHolder.getEvent().getAction(), behaviorState);
//
//                // ...then addUnit it to the device...
//                String behaviorUuid = eventHolder.getEvent().getAction().getUuid().toString();
//                unit.sendMessage("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay().getStore().storeState(behaviorState);
//                eventHolder.getEvent().setAction(eventHolder.getEvent().getAction(), behaviorState);
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
        String phrase = eventHolder.getEvent().getState().get(0).getState();

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
//                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState());
                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState().get(0));
                getClay().getStore().storeEvent(eventHolder.getEvent());

                // Send updated state to device
                // <HACK>
                String content = "set event " + eventHolder.getEvent().getUuid() + " state \"" + updatedStateString + "\"";
//                getUnit().sendMessage(content);
                // </HACK>

                // <HACK>
                // TODO: Replace this with a queue.
                getUnit().sendMessageTcp(content);
                // </HACK>

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                timelineListView.refreshListViewFromData();

//                State behaviorState = new State(eventHolder.getEvent().getAction(), eventHolder.getEvent().getAction().getTag(), stateString);
////                eventHolder.getEvent().getAction().setState(behaviorState);
//                eventHolder.getEvent().setAction(eventHolder.getEvent().getAction(), behaviorState);
//
//                // ...then addUnit it to the device...
//                String behaviorUuid = eventHolder.getEvent().getAction().getUuid().toString();
//                unit.sendMessage("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay ().getStore().storeState(behaviorState);
//                eventHolder.getEvent().setAction(eventHolder.getEvent().getAction(), behaviorState);
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
        int time = Integer.parseInt(eventHolder.getEvent().getState().get(0).getState());

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
//                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState());
                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState().get(0));
                getClay().getStore().storeEvent(eventHolder.getEvent());

                // Send updated state to device
                // <HACK>
                String content = "set event " + eventHolder.getEvent().getUuid() + " state \"" + updatedStateString + "\"";
//                getUnit().sendMessage(content);
                // </HACK>

                // <HACK>
                // TODO: Replace this with a queue.
                getUnit().sendMessageTcp(content);
                // </HACK>

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                timelineListView.refreshListViewFromData();

//                // Add wait
//                State behaviorState = new State (eventHolder.getEvent().getAction(), eventHolder.getEvent().getAction().getTag(), "" + waitVal.getProgress());
//                eventHolder.getEvent().setAction(eventHolder.getEvent().getAction(), behaviorState);
//
//                // ...then addUnit it to the device...
//                String behaviorUuid = eventHolder.getEvent().getAction().getUuid().toString();
//                unit.sendMessage("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay ().getStore().storeState(behaviorState);
//                eventHolder.getEvent().setAction(eventHolder.getEvent().getAction(), behaviorState);
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
