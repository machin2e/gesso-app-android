package camp.computer.clay.sequencer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
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

import java.util.ArrayList;

import camp.computer.clay.system.Clay;
import camp.computer.clay.system.Device;

public class EventDesignerView {

    private Device device;

    private TimelineView timelineView;

    public EventDesignerView(Device device, TimelineView timelineView) {

        this.device = device;

        this.timelineView = timelineView;
    }

    public Context getContext () {
        return ApplicationView.getApplicationView();
    }

    public Clay getClay () {
        return getDevice().getClay();
    }

    public Device getDevice() {
        return this.device;
    }

    public void displayUpdateLightsOptions(final EventHolder eventHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // Title
        builder.setTitle ("light");

        // Layout
        LinearLayout designerViewLayout = new LinearLayout (getContext());
        designerViewLayout.setOrientation (LinearLayout.VERTICAL);

        // Enable or disable lights
        // final TextView lightLabel = new TextView (getContext());
        // lightLabel.setText("Choose some lights");
        // lightLabel.setPadding(70, 20, 70, 20);
        // designerViewLayout.addView(lightLabel);

        final ColorPickerDialog colorPickerDialog = ColorPickerDialog.createColorPickerDialog (getContext(), ColorPickerDialog.DARK_THEME);

        /*
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
        designerViewLayout.addView(lightLayout);
        */

        // Select light color
        final TextView lightColorLabel = new TextView (getContext());
        lightColorLabel.setText("Choose colors");
        lightColorLabel.setPadding(70, 20, 70, 20);
        designerViewLayout.addView(lightColorLabel);

        LinearLayout lightColorLayout = new LinearLayout (getContext());
        lightColorLayout.setOrientation(LinearLayout.HORIZONTAL);
        final ArrayList<Button> lightColorButtons = new ArrayList<Button> ();
        final ArrayList<Integer> lightColors = new ArrayList<Integer> ();
        final ArrayList<String> lightColorHexStrings = new ArrayList<String> ();
        for (int i = 0; i < 12; i++) {
            final String channelLabel = Integer.toString(i + 1);
            final Button colorButton = new Button (getContext());
            colorButton.setPadding(0, 0, 0, 0);
            colorButton.setText(Integer.toString(i + 1));

            lightColors.add(0);
            lightColorHexStrings.add("#ff000000");

            colorButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                colorPickerDialog.setOnColorPickedListener(new ColorPickerDialog.OnColorPickedListener() {
                    @Override
                    public void onColorPicked(int color, String hexVal) {
                        colorButton.setBackgroundColor(color);
                        int lightColorIndex = Integer.valueOf(String.valueOf(colorButton.getText())) - 1;
                        lightColors.set(lightColorIndex, color);
                        lightColorHexStrings.set(lightColorIndex, hexVal);
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
        designerViewLayout.addView(lightColorLayout);

        // Initialize state
        String stateString = eventHolder.getEvent().getState().get(0).getState();
        Log.v ("Light_State", stateString);
        final String[] splitStateString = stateString.split(" ");

        // Set button background to current color state
        lightColors.clear();
        lightColorHexStrings.clear();
        for (int i = 0; i < lightColorButtons.size(); i++) {
            Button lightColorButton = lightColorButtons.get (i);

            // Format the event's colors in "#FFRRGGBB" format.
            String lightColorHexString = "#FF" + splitStateString[i];
            int color = Color.parseColor(lightColorHexString);

            // Save color string and integer value
            lightColorHexStrings.add(lightColorHexString);
            lightColors.add(color);

            lightColorButton.setBackgroundColor(color);
        }

        // Assign the layout to the alert dialog.
        builder.setView(designerViewLayout);

        // Set up the buttons
        builder.setPositiveButton ("DONE", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {

                String updatedStateString = "";
                Byte[] colorBytesString = new Byte[12 * 3]; // i.e., 12 lights, each with 3 color bytes
                for (int i = 0; i < 12; i++) {

                    /*
                    final ToggleButton lightEnableButton = lightToggleButtons.get (i);
                    */
                    final Button lightColorButton = lightColorButtons.get (i);

                    // LED enable. Is the LED on or off?

                    /*
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
                    */

                    // Get LED color
                    int color = lightColors.get(i);
                    int r = (color & 0x00FF0000) >> 16;
                    int g = (color & 0x0000FF00) >> 8;
                    int b = (color & 0x000000FF) >> 0;

                    colorBytesString[3 * i + 0] = (byte) r;
                    colorBytesString[3 * i + 1] = (byte) g;
                    colorBytesString[3 * i + 2] = (byte) b;

                    // Add space between channel states.
                    if (i < (12 - 1)) {
                        updatedStateString = updatedStateString.concat (" ");
                    }
                }

//                Log.v ("Light_State", "updated: " + updatedStateString);
//
//                // Compute strings corresponding to bytes that represent color channels for each
//                // of Clay's LEDs. Note this is not encoded as binary data, but as an ASCII string.
//                String colorBitString = "";
//                byte[] colorBytes = new byte[12 * 3];
//                for (int i = 0; i < 12 * 3; i++) {
//                    colorBytes[i] = colorBytesString[i];
//                    String s1 = String.format("%8s", Integer.toBinaryString(colorBytesString[i] & 0xFF)).replace(' ', '0');
//                    colorBitString += s1 + " "; // Integer.toString((int) colorHexBytes[i]) + " ";
//                }
//                Log.v("Light_State", "color hex: " + colorBitString);


                String lightHexColorString = "";
                for (int i = 0; i < lightColorHexStrings.size(); i++) {
                    lightHexColorString += " " + lightColorHexStrings.get(i).substring(3).toUpperCase();
                }
                updatedStateString = lightHexColorString.trim();

                Log.v("Light_State", "updated state: " + updatedStateString);

                // Update the behavior state
                // <HACK>
                eventHolder.getEvent().setTimeline(device.getTimeline());
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
//        getDevice().sendMessage ("cache action " + action.getUuid() + " \"" + action.getTag() + " " + event.getState().get(0).getState() + "\"");
//                getDevice().sendMessage ("start event " + eventHolder.getEvent().getUuid());
//                getDevice().sendMessage ("set event " + eventHolder.getEvent().getUuid() + " action " + eventHolder.getEvent().getAction().getUuid());
//                getDevice().sendMessage ("set event " + eventHolder.getEvent().getUuid() + " state \"light " + eventHolder.getEvent().getState().get(0).getState() + "\""); // <HACK />
                String content = "set event " + eventHolder.getEvent().getUuid() + " state \"" + updatedStateString + "\"";
                Log.v ("Color", content);
                //getDevice().sendMessage (content); // <HACK />
                /*
                String packedBytes = new String(colorBytes, Charset.forName("UTF-8")); // "US-ASCII"
                String msg2 = "set event " + eventHolder.getEvent().getUuid() + " state \"color " + packedBytes + "\"";
                getDevice().sendMessage (msg2); // <HACK />
                for (int i = 0; i < msg2.length(); i++) {
                    Log.v ("Color", String.valueOf(Integer.valueOf(msg2.getBytes()[i])));
                }
                */
                // device.sendMessage("update action " + behaviorUuid + " \"" + behaviorState.getState() + "\"");
                // </HACK>

                // <HACK>
                // TODO: Replace this with a queue.
                getDevice().enqueueMessage(content);
                // </HACK>

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                timelineView.refreshTimelineView();
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

    public void displayEventTriggerOptions(final EventHolder eventHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("trigger (message)");

        // Declare transformation layout
        LinearLayout designerViewLayout = new LinearLayout (getContext());
        designerViewLayout.setOrientation(LinearLayout.VERTICAL);

        // Trigger
        final EditText triggerMessageText = new EditText(getContext());
        triggerMessageText.setInputType(InputType.TYPE_CLASS_TEXT);
        triggerMessageText.setVisibility(View.VISIBLE);
        designerViewLayout.addView(triggerMessageText);

        builder.setView(designerViewLayout);

        // Get behavior state
        //String triggerMessageContent = eventHolder.getEvent().getState().get(0).getState();
        String triggerMessageContent = "";

        // Update the view
        triggerMessageText.setText(triggerMessageContent);
        triggerMessageText.setSelection(triggerMessageText.getText().length());

        ApplicationView.getApplicationView().speakPhrase(triggerMessageContent);

        // Set up the buttons
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Save configuration options to object
////                item.phrase = input.getText().toString();
//
//                String updatedStateString = triggerMessageText.getText().toString();
//
//                // Update the behavior state
//                // <HACK>
//                eventHolder.getEvent().setTimeline(device.getTimeline());
//                // </HACK>
//                eventHolder.updateState (updatedStateString);

                // Store: Store the new behavior state and update the event.
//                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState().get(0));
//                getClay().getStore().storeEvent(eventHolder.getEvent());

                // Send updated state to device
                // <HACK>
                String triggerText = triggerMessageText.getText().toString();

                eventHolder.setTriggerMessage(triggerText);

                if (triggerText.length() > 0) {
                    getDevice().enqueueMessage("set event " + eventHolder.getEvent().getUuid() + " trigger \"" + triggerText + "\"");
                }
                // </HACK>

                // Refresh the timeline view
                timelineView.refreshTimelineView();
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

    public void displayUpdateSignalOptions(final EventHolder eventHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        final ArrayList<String> channelStateStrings = new ArrayList<String>();

        /* Pop-up tag */

        // builder.setTitle("Signal");

        // Declare transformation layout
        LinearLayout designerViewLayout = new LinearLayout (getContext());
        designerViewLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout channelConfigurationLayout = new LinearLayout (getContext());
        channelConfigurationLayout.setOrientation(LinearLayout.HORIZONTAL);
        designerViewLayout.addView(channelConfigurationLayout);

        channelConfigurationLayout.setPadding(10, 10, 10, 10);

        /* Message content */

//        // Title
//        final TextView messageContentTitle = new TextView (getContext());
//        messageContentTitle.setText("Channel");
//        messageContentTitle.setPadding(70, 20, 70, 20);
//        designerViewLayout.addView(messageContentTitle);

        // Content input field
        final ArrayList<String> channelNumberData = new ArrayList<String>();
        for (int i = 1; i <= 12; i++) {
            channelNumberData.add(Integer.toString(i));
        }
        final Spinner channelNumberSelector = new Spinner (getContext());
        final ArrayAdapter<String> channelNumberDataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, channelNumberData); //selected item will look like a spinner set from XML
        channelNumberDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        channelNumberSelector.setAdapter(channelNumberDataAdapter);
        //designerViewLayout.addView(channelNumberSelector);
        channelConfigurationLayout.addView(channelNumberSelector);

        /* Direction */

//        // Title
//        final TextView messageTypeTitle = new TextView (getContext());
//        messageTypeTitle.setText("Direction");
//        messageTypeTitle.setPadding(70, 20, 70, 20);
//        designerViewLayout.addView(messageTypeTitle);

        // List of types (i.e., TCP, UDP, Mesh, etc.)
        final Spinner signalDirectionSelector = new Spinner (getContext());
        final ArrayList<String> signalDirectionData = new ArrayList<String>();
        signalDirectionData.add("input");
        signalDirectionData.add("output");
        ArrayAdapter<String> signalDirectionDataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, signalDirectionData); //selected item will look like a spinner set from XML
        signalDirectionDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        signalDirectionSelector.setAdapter(signalDirectionDataAdapter);
        //designerViewLayout.addView(messageTypeSelector);
        channelConfigurationLayout.addView(signalDirectionSelector);

        /* Type */

        // Get list of devices that have been discovered
        final ArrayList<String> signalTypeData = new ArrayList<String>();
        signalTypeData.add("toggle");
        signalTypeData.add("pulse");
        signalTypeData.add("waveform");

//        // Destination label
//        final TextView signalTypeTitle = new TextView (getContext());
//        signalTypeTitle.setText("Type");
//        signalTypeTitle.setPadding(70, 20, 70, 20);
//        designerViewLayout.addView(signalTypeTitle);

        // Set destination of message
        final Spinner signalTypeSelector = new Spinner (getContext());
        final ArrayAdapter<String> signalTypeDataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, signalTypeData); //selected item will look like a spinner set from XML
        signalTypeDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        signalTypeSelector.setAdapter(signalTypeDataAdapter);
        //designerViewLayout.addView(signalTypeSelector);
        channelConfigurationLayout.addView(signalTypeSelector);








        // "Toggle" data editor
        final LinearLayout toggleDataLayout = new LinearLayout (getContext());
        toggleDataLayout.setOrientation(LinearLayout.HORIZONTAL);
        toggleDataLayout.setVisibility(View.GONE);
        designerViewLayout.addView(toggleDataLayout);

        final TextView toggleDataTitle = new TextView (getContext());
        toggleDataTitle.setText("State");
        toggleDataTitle.setPadding(70, 20, 70, 20);
        toggleDataLayout.addView(toggleDataTitle);

        final EditText toggleSignalDataEditor = new EditText(getContext());
        toggleSignalDataEditor.setInputType(InputType.TYPE_CLASS_NUMBER);
        toggleDataLayout.addView(toggleSignalDataEditor);

        // "Pulse" data editor: Frequency
        final LinearLayout pulseDataLayout = new LinearLayout (getContext());
        pulseDataLayout.setOrientation(LinearLayout.HORIZONTAL);
        pulseDataLayout.setVisibility(View.GONE);
        designerViewLayout.addView(pulseDataLayout);

        final TextView pulseFrequencyTitle = new TextView (getContext());
        pulseFrequencyTitle.setText("Period");
        pulseFrequencyTitle.setPadding(70, 20, 70, 20);
        pulseDataLayout.addView(pulseFrequencyTitle);

        final EditText pulseSignalFrequencyDataEditor = new EditText(getContext());
//        pulseSignalFrequencyDataEditor.setInputType(InputType.TYPE_CLASS_NUMBER);
        pulseDataLayout.addView(pulseSignalFrequencyDataEditor);

        // "Pulse" data editor: Duty Cycle
        final TextView pulseDutyCycleTitle = new TextView (getContext());
        pulseDutyCycleTitle.setText("Duty Cycle");
        pulseDutyCycleTitle.setPadding(70, 20, 70, 20);
        pulseDataLayout.addView(pulseDutyCycleTitle);

        final EditText pulseSignalDutyCycleDataEditor = new EditText(getContext());
//        pulseSignalDutyCycleDataEditor.setInputType(InputType.TYPE_CLASS_NUMBER);
        pulseDataLayout.addView(pulseSignalDutyCycleDataEditor);

        // Set up key listeners

        toggleSignalDataEditor.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // <HACK>
                String channelNumberString = channelNumberSelector.getSelectedItem().toString();
                String signalDirectionString = signalDirectionSelector.getSelectedItem().toString();
                String signalTypeString = signalTypeSelector.getSelectedItem().toString();

                String stateString = "";
                if (signalDirectionString.equals("input")) {
                    // TODO: Display the values received from the device
                    stateString += "none";
                } else if (signalDirectionString.equals("output")) {
                    if (signalTypeString.equals("toggle")) {
                        stateString += toggleSignalDataEditor.getText().toString();
                    } else if (signalTypeString.equals("pulse")) {
                        stateString += pulseSignalFrequencyDataEditor.getText().toString() + ",";
                        stateString += pulseSignalDutyCycleDataEditor.getText().toString();
                    } else if (signalTypeString.equals("waveform")) {
                        stateString += "none";
                    }
                }

                String currentState = channelNumberString + " " + signalDirectionString + " " + signalTypeString + ": " + stateString;

                currentState = "T";
                if (signalDirectionString.equals("input")) {
                    currentState += "I";
                } else if (signalDirectionString.equals("output")) {
                    currentState += "O";
                }
                if (signalTypeString.equals("toggle")) {
                    currentState += "T";
                } else if (signalTypeString.equals("pulse")) {
                    currentState += "P";
                } else if (signalTypeString.equals("waveform")) {
                    currentState += "W";
                }
                currentState += ":";
                currentState += stateString;

                int channelIndex = Integer.parseInt(channelNumberSelector.getSelectedItem().toString()) - 1;
                channelStateStrings.set(channelIndex, currentState);

                Log.v("Signal", "" + currentState);
                // </HACK>
                return false;
            }
        });

        pulseSignalFrequencyDataEditor.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // <HACK>
                String channelNumberString = channelNumberSelector.getSelectedItem().toString();
                String signalDirectionString = signalDirectionSelector.getSelectedItem().toString();
                String signalTypeString = signalTypeSelector.getSelectedItem().toString();

                String stateString = "";
                if (signalDirectionString.equals("input")) {
                    // TODO: Display the values received from the device
                    stateString += "none";
                } else if (signalDirectionString.equals("output")) {
                    if (signalTypeString.equals("toggle")) {
                        stateString += toggleSignalDataEditor.getText().toString();
                    } else if (signalTypeString.equals("pulse")) {
                        stateString += pulseSignalFrequencyDataEditor.getText().toString() + ",";
                        stateString += pulseSignalDutyCycleDataEditor.getText().toString();
                    } else if (signalTypeString.equals("waveform")) {
                        stateString += "none";
                    }
                }

                String currentState = channelNumberString + " " + signalDirectionString + " " + signalTypeString + ": " + stateString;

                currentState = "T";
                if (signalDirectionString.equals("input")) {
                    currentState += "I";
                } else if (signalDirectionString.equals("output")) {
                    currentState += "O";
                }
                if (signalTypeString.equals("toggle")) {
                    currentState += "T";
                } else if (signalTypeString.equals("pulse")) {
                    currentState += "P";
                } else if (signalTypeString.equals("waveform")) {
                    currentState += "W";
                }
                currentState += ":";
                currentState += stateString;

                int channelIndex = Integer.parseInt(channelNumberSelector.getSelectedItem().toString()) - 1;
                channelStateStrings.set(channelIndex, currentState);

                Log.v("Signal", "" + currentState);
                // </HACK>
                return false;
            }
        });

        pulseSignalDutyCycleDataEditor.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // <HACK>
                String channelNumberString = channelNumberSelector.getSelectedItem().toString();
                String signalDirectionString = signalDirectionSelector.getSelectedItem().toString();
                String signalTypeString = signalTypeSelector.getSelectedItem().toString();

                String stateString = "";
                if (signalDirectionString.equals("input")) {
                    // TODO: Display the values received from the device
                    stateString += "none";
                } else if (signalDirectionString.equals("output")) {
                    if (signalTypeString.equals("toggle")) {
                        stateString += toggleSignalDataEditor.getText().toString();
                    } else if (signalTypeString.equals("pulse")) {
                        stateString += pulseSignalFrequencyDataEditor.getText().toString() + ",";
                        stateString += pulseSignalDutyCycleDataEditor.getText().toString();
                    } else if (signalTypeString.equals("waveform")) {
                        stateString += "none";
                    }
                }

                String currentState = channelNumberString + " " + signalDirectionString + " " + signalTypeString + ": " + stateString;

                currentState = "T";
                if (signalDirectionString.equals("input")) {
                    currentState += "I";
                } else if (signalDirectionString.equals("output")) {
                    currentState += "O";
                }
                if (signalTypeString.equals("toggle")) {
                    currentState += "T";
                } else if (signalTypeString.equals("pulse")) {
                    currentState += "P";
                } else if (signalTypeString.equals("waveform")) {
                    currentState += "W";
                }
                currentState += ":";
                currentState += stateString;

                int channelIndex = Integer.parseInt(channelNumberSelector.getSelectedItem().toString()) - 1;
                channelStateStrings.set(channelIndex, currentState);

                Log.v("Signal", "" + currentState);
                // </HACK>
                return false;
            }
        });

        /* Set the view */

        builder.setView(designerViewLayout);

        /* Set up interactivity */

        channelNumberSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.v ("Signal", "SELECTED CHANNEL");

                // Get selected channel's state
                String channelNumberString = channelNumberSelector.getItemAtPosition(position).toString();
                int channelIndex = Integer.parseInt(channelNumberString) - 1;
                String channelStateString = channelStateStrings.get(channelIndex);

                // Get channel's direction
                String channelDirectionString = "" + channelStateString.charAt(1);
                if (channelDirectionString.equals("I")) {
                    signalDirectionSelector.setSelection(0);
                } else if (channelDirectionString.equals("O")) {
                    signalDirectionSelector.setSelection(1);
                }
//                signalDirectionSelector.setVisibility(View.VISIBLE);

                // Get channel's type
                String channelTypeString = "" + channelStateString.charAt(2);
                if (channelTypeString.equals("T")) {
                    signalTypeSelector.setSelection(0);
                } else {
                    signalTypeSelector.setSelection(1);
                }
//                signalTypeSelector.setVisibility(View.GONE);

                // Get channel's data
                if (channelDirectionString.equals("O")) {
                    String toggleSignalDataString = channelStateString.split(":")[1];
                    if (channelTypeString.equals("T")) {
                        toggleSignalDataEditor.setText(toggleSignalDataString);
//                        toggleSignalDataEditor.setVisibility(View.GONE);
                    } else if (channelTypeString.equals("P")) {
                        String[] toggleSignalDataStrings = toggleSignalDataString.split(",");
                        pulseSignalFrequencyDataEditor.setText(toggleSignalDataStrings[0]);
//                        pulseSignalFrequencyDataEditor.setVisibility(View.GONE);
                        pulseSignalDutyCycleDataEditor.setText(toggleSignalDataStrings[1]);
//                        pulseSignalDutyCycleDataEditor.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        signalDirectionSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = signalDirectionSelector.getItemAtPosition(position).toString();
                if (selectedItemText.equals("input")) {
//                    signalTypeTitle.setVisibility(View.VISIBLE);
                    signalTypeSelector.setVisibility(View.VISIBLE);
                    // Get list of all discovered devices on the mesh network
                    signalTypeData.clear();
                    signalTypeData.add("toggle");
                    signalTypeData.add("waveform");
                    signalTypeDataAdapter.notifyDataSetChanged();

                    toggleDataLayout.setVisibility(View.GONE);
                    pulseDataLayout.setVisibility(View.GONE);
                } else if (selectedItemText.equals("output")) {
//                    signalTypeTitle.setVisibility(View.VISIBLE);
                    signalTypeSelector.setVisibility(View.VISIBLE);
                    signalTypeData.clear();
                    signalTypeData.add("toggle");
                    signalTypeData.add("pulse");
                    signalTypeDataAdapter.notifyDataSetChanged();

                    toggleDataLayout.setVisibility(View.GONE);
                    pulseDataLayout.setVisibility(View.GONE);
                }

                // <HACK>
                String channelNumberString = channelNumberSelector.getSelectedItem().toString();
                String signalDirectionString = signalDirectionSelector.getSelectedItem().toString();
                String signalTypeString = signalTypeSelector.getSelectedItem().toString();

                String stateString = "";
                if (signalDirectionString.equals("input")) {
                    // TODO: Display the values received from the device
                    stateString += "none";
                } else if (signalDirectionString.equals("output")) {
                    if (signalTypeString.equals("toggle")) {
                        stateString += toggleSignalDataEditor.getText().toString();
                    } else if (signalTypeString.equals("pulse")) {
                        stateString += pulseSignalFrequencyDataEditor.getText().toString() + ",";
                        stateString += pulseSignalDutyCycleDataEditor.getText().toString();
                    } else if (signalTypeString.equals("waveform")) {
                        stateString += "none";
                    }
                }

                String currentState = channelNumberString + " " + signalDirectionString + " " + signalTypeString + ": " + stateString;

                currentState = "T";
                if (signalDirectionString.equals("input")) {
                    currentState += "I";
                } else if (signalDirectionString.equals("output")) {
                    currentState += "O";
                }
                if (signalTypeString.equals("toggle")) {
                    currentState += "T";
                } else if (signalTypeString.equals("pulse")) {
                    currentState += "P";
                } else if (signalTypeString.equals("waveform")) {
                    currentState += "W";
                }
                currentState += ":";
                currentState += stateString;

                int channelIndex = Integer.parseInt(channelNumberSelector.getSelectedItem().toString()) - 1;
                channelStateStrings.set(channelIndex, currentState);

                Log.v("Signal", "" + currentState);
                // </HACK>

//                // Select the first item by default
//                signalTypeSelector.setSelection(0, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                signalTypeSelector.setSelection(0, true);
            }
        });

        signalTypeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = signalTypeSelector.getItemAtPosition(position).toString();
                if (selectedItemText.equals("toggle")) {
                    toggleSignalDataEditor.setText("0");
                    toggleDataLayout.setVisibility(View.VISIBLE);
                    pulseDataLayout.setVisibility(View.GONE);
                } else if (selectedItemText.equals("pulse")) {
                    pulseSignalFrequencyDataEditor.setText("0");
                    pulseSignalDutyCycleDataEditor.setText("0");
                    toggleDataLayout.setVisibility(View.GONE);
                    pulseDataLayout.setVisibility(View.VISIBLE);
                } else if (selectedItemText.equals("waveform")) {
                    toggleDataLayout.setVisibility(View.GONE);
                    pulseDataLayout.setVisibility(View.GONE);
                }

                // <HACK>
                String channelNumberString = channelNumberSelector.getSelectedItem().toString();
                String signalDirectionString = signalDirectionSelector.getSelectedItem().toString();
                String signalTypeString = signalTypeSelector.getSelectedItem().toString();

                String stateString = "";
                if (signalDirectionString.equals("input")) {
                    // TODO: Display the values received from the device
                    stateString += "none";
                } else if (signalDirectionString.equals("output")) {
                    if (signalTypeString.equals("toggle")) {
                        stateString += toggleSignalDataEditor.getText().toString();
                    } else if (signalTypeString.equals("pulse")) {
                        stateString += pulseSignalFrequencyDataEditor.getText().toString() + ",";
                        stateString += pulseSignalDutyCycleDataEditor.getText().toString();
                    } else if (signalTypeString.equals("waveform")) {
                        stateString += "none";
                    }
                }

                String currentState = channelNumberString + " " + signalDirectionString + " " + signalTypeString + ": " + stateString;

                currentState = "T";
                if (signalDirectionString.equals("input")) {
                    currentState += "I";
                } else if (signalDirectionString.equals("output")) {
                    currentState += "O";
                }
                if (signalTypeString.equals("toggle")) {
                    currentState += "T";
                } else if (signalTypeString.equals("pulse")) {
                    currentState += "P";
                } else if (signalTypeString.equals("waveform")) {
                    currentState += "W";
                }
                currentState += ":";
                currentState += stateString;

                int channelIndex = Integer.parseInt(channelNumberSelector.getSelectedItem().toString()) - 1;
                channelStateStrings.set(channelIndex, currentState);

                Log.v("Signal", "" + currentState);
                // </HACK>
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                toggleDataLayout.setVisibility(View.GONE);
                pulseDataLayout.setVisibility(View.GONE);
            }
        });

        /* Perform "automated" interactions to initialize pop-up */

        // TODO:

        /* Initialize the state */

        // Extract state from string representation
        // e.g., "TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none"
        String currentStateString = eventHolder.getEvent().getState().get(0).getState();
        final String[] currentStateStrings = currentStateString.split(" ");
        channelStateStrings.clear();
        for (int i = 0; i < currentStateStrings.length; i++) {
            channelStateStrings.add(currentStateStrings[i]);
            Log.v("Signal", "state " + i + ": " + channelStateStrings.get(i));
        }

        Log.v ("Signal", "current state: " + currentStateString);



        int currentDestinationAddressStringIndex = currentStateString.indexOf(" ");
        int currentContentStringIndex = currentStateString.indexOf(" ", currentDestinationAddressStringIndex + 1);

        String currentTypeString = currentStateString.substring(0, currentDestinationAddressStringIndex);
        String currentDestinationAddressString = currentStateString.substring(currentDestinationAddressStringIndex + 1, currentContentStringIndex);
        String currentContentString = currentStateString.substring (currentContentStringIndex + 1);
        currentContentString = currentContentString.substring(1, currentContentString.length() - 1);

        /* Initialize the pop-up with the state */

        // Message content
//        messageContentEditor.setText(currentContentString);
//        messageContentEditor.setSelection(messageContentEditor.getText().length());

        // Message type
        int messageTypeIndex = signalDirectionDataAdapter.getPosition(currentTypeString);
        signalTypeSelector.setSelection(messageTypeIndex);

        // Message destination
        int messageDestinationIndex = signalTypeDataAdapter.getPosition(currentDestinationAddressString);
        signalTypeSelector.setSelection(messageDestinationIndex);

        toggleSignalDataEditor.setText(currentDestinationAddressString);

        // Set up the buttons
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String stateString = "";
                for (String state : channelStateStrings) {
                    stateString += " " + state;
                }
                stateString = stateString.trim();
                Log.v("Signal", "state: " + stateString);



//                String channelNumberString = channelNumberSelector.getSelectedItem().toString();
//                String signalDirectionString = signalDirectionSelector.getSelectedItem().toString();
//                String signalTypeString = signalTypeSelector.getSelectedItem().toString();
//
//                String stateString = "";
//                if (signalDirectionString.equals("input")) {
//                    // TODO: Display the values received from the device
//                    stateString += "none";
//                } else if (signalDirectionString.equals("output")) {
//                    if (signalTypeString.equals("toggle")) {
//                        stateString += toggleSignalDataEditor.getText().toString();
//                    } else if (signalTypeString.equals("pulse")) {
//                        stateString += pulseSignalFrequencyDataEditor.getText().toString() + ",";
//                        stateString += pulseSignalDutyCycleDataEditor.getText().toString();
//                    } else if (signalTypeString.equals("waveform")) {
//                        stateString += "none";
//                    }
//                }
//
//                String currentState = channelNumberString + " " + signalDirectionString + " " + signalTypeString + ": " + stateString;
//
//                // index by: (channelNumberString - 1)
//                currentState = "T";
//                if (signalDirectionString.equals("input")) {
//                    currentState += "I";
//                } else if (signalDirectionString.equals("output")) {
//                    currentState += "O";
//                }
//                if (signalTypeString.equals("toggle")) {
//                    currentState += "T";
//                } else if (signalTypeString.equals("pulse")) {
//                    currentState += "P";
//                } else if (signalTypeString.equals("waveform")) {
//                    currentState += "W";
//                }
//                currentState += ":";
//                currentState += stateString;
//
//                Log.v("Signal", "" + currentState);

//                // Update the behavior profile state
//                // e.g., "udp 192.168.1.255:4445 \"hello world\""
//                // TODO: "udp none 192.168.1.255:4445 \"hello world\""... make same message string format!
//                String messageTypeString = messageTypeSelector.getSelectedItem().toString();
//                String stateString = "";
//                if (messageTypeString.equals("Device")) {
//                    stateString = messageTypeString;
//                    stateString += " " + "none";
//                    stateString += " \'" + messageContentEditor.getText().toString() + "\'";
//                } else {
//                    stateString = messageTypeString;
//                    if (!signalTypeSelector.getSelectedItem().toString().equals("Other")) {
//                        stateString += " " + signalTypeSelector.getSelectedItem().toString();
//                    } else {
//                        stateString += " " + messageCustomDestinationEditor.getText().toString();
//                    }
//                    stateString += " \'" + messageContentEditor.getText().toString() + "\'";
//                }
//

                // Update the behavior state
                // <HACK>
                eventHolder.getEvent().setTimeline(device.getTimeline());
                // </HACK>
                eventHolder.updateState(stateString);

                // Store: Store the new behavior state and update the event.
//                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState());
                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState().get(0));
                getClay().getStore().storeEvent (eventHolder.getEvent());

                // Context
                // i.e., The context is generally encoded as <channel-settings>:<channel-source>,<observable-source-key>|<observable-destination-key>
                // e.g., set event <event-uuid> context "TOT:3,\"waveform-sample-value\"|\"pulse_duty_cycle\""
                String contextString = "TIT:none TIT:none TIW:none TOP:3,'waveform_sample_value'|'pulse_duty_cycle' TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none";
                String contextContent = "set event " + eventHolder.getEvent().getUuid() + " context \"" + contextString + "\"";

                getDevice().enqueueMessage(contextContent);

                // Send updated state to device
                // <HACK>
                stateString = "none none none 0.02,64450 none none none none none none none none";
                //stateString = "TIT:none:none TIT:none:none TIW:none TOP:0.02,64450:3,'waveform-sample-value'|'pulse_duty_cycle' TIT:none:none TIT:none:none TIT:none:none TIT:none:none TIT:none:none TIT:none:none TIT:none:none TIT:none:none";
                String content = "set event " + eventHolder.getEvent().getUuid() + " state \"" + stateString + "\"";
//                getDevice().sendMessage(content);
                // </HACK>

                Log.v ("Event_Trigger", "state: " + stateString);
                Log.v ("Event_Trigger", "update: " + content);

                // <HACK>
                // TODO: Replace this with a queue.
                getDevice().enqueueMessage(content);
                // </HACK>

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                timelineView.refreshTimelineView();

//                // Update the behavior state
//                State behaviorState = new State(eventHolder.getEvent().getAction(), eventHolder.getEvent().getAction().getTag(), stateString);
//                eventHolder.getEvent().setAction(eventHolder.getEvent().getAction(), behaviorState);
//
//                // ...then addDevice it to the device...
//                String behaviorUuid = eventHolder.getEvent().getAction().getUuid().toString();
//                device.sendMessage("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay().getStore().storeState(behaviorState);
//                eventHolder.getEvent().setAction(eventHolder.getEvent().getAction(), behaviorState);
//                //getClay ().getStore().updateBehaviorState(behaviorState);
//                getClay().getStore().updateTimeline(device.getTimeline());
//
//                // Refresh the timeline view
//                refreshTimelineView();
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

    public void displayUpdateIOOptions2 (final EventHolder eventHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle ("Change the channel.");
        builder.setMessage ("What do you want to do?");

        // TODO: Populate with the current transform values (if any).
        // TODO: Specify the units to receive the change.

        // Declare transformation layout
        LinearLayout designerViewLayout = new LinearLayout (getContext());
        designerViewLayout.setOrientation(LinearLayout.VERTICAL);

        // Channels

        final ArrayList<ToggleButton> channelEnableToggleButtons = new ArrayList<> ();
        final ArrayList<Button> channelDirectionButtons = new ArrayList<> ();
        final ArrayList<Button> channelModeButtons = new ArrayList<> ();
        final ArrayList<ToggleButton> channelValueToggleButtons = new ArrayList<> ();

        // Set up the channel label
        final TextView channelEnabledLabel = new TextView (getContext());
        channelEnabledLabel.setText("Enable channels");
        channelEnabledLabel.setPadding(70, 20, 70, 20);
        designerViewLayout.addView(channelEnabledLabel);

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
        designerViewLayout.addView (channelEnabledLayout);

        // Set up the label
        final TextView signalLabel = new TextView (getContext());
        signalLabel.setText("Set channel direction, mode, and value"); // INPUT: Discrete/Digital, Continuous/Analog; OUTPUT: Discrete, Continuous/PWM
        signalLabel.setPadding(70, 20, 70, 20);
        designerViewLayout.addView(signalLabel);

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
        designerViewLayout.addView (ioLayout);

        /*
        // Set up the I/O mode label
        final TextView ioModeLabel = new TextView (this);
        ioModeLabel.setText ("I/O Mode"); // INPUT: Discrete/Digital, Continuous/Analog; OUTPUT: Discrete, Continuous/PWM
        ioModeLabel.setPadding (70, 20, 70, 20);
        designerViewLayout.addView (ioModeLabel);
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
        designerViewLayout.addView(channelModeLayout);

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
        designerViewLayout.addView(channelValueLayout);

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
        builder.setView (designerViewLayout);

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
                eventHolder.getEvent().setTimeline(device.getTimeline());
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
                Log.v ("Signal", content);
//                getDevice().sendMessage(content);
                // </HACK>

                // <HACK>
                // TODO: Replace this with a queue.
                getDevice().enqueueMessage(content);
                // </HACK>

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                timelineView.refreshTimelineView();

//                // Update the behavior state
//                State behaviorState = new State(eventHolder.getEvent().getAction(), eventHolder.getEvent().getAction().getTag(), updatedStateString);
//                eventHolder.getEvent().getAction().setState(behaviorState);
//
//                // ...then addDevice it to the device.
//                String behaviorUuid = eventHolder.getEvent().getAction().getUuid().toString();
//                device.sendMessage("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay ().getStore().storeState(behaviorState);
//                eventHolder.getEvent().setAction(eventHolder.getEvent().getAction(), behaviorState);
//                //getClay ().getStore().updateBehaviorState(behaviorState);
//                getClay ().getStore().updateTimeline(device.getTimeline());
//
//                // Refresh the timeline view
//                refreshTimelineView();
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
        input.setText(eventHolder.tag);
        input.setSelection(input.getText().length());

        // Set up the buttons
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Update the state of the behavior
                eventHolder.tag = input.getText().toString();

                // TODO: Update the corresponding behavior state... this should propagate back through the object model... and cloud...
//                item.restoreAction().setTag(input.getText().toString())
//                item.restoreAction().setTag(input.getText().toString());

                // Send changes to device
                // TODO: "create behavior (...)"
                String tagString = input.getText().toString();
//                device.sendMessage (tagString);

                // Create the behavior
                eventHolder.getEvent().getAction().setTag(tagString);

                // Store: Store the new behavior state and update the event.
                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState().get(0));
                getClay().getStore().storeAction(eventHolder.getEvent().getAction());

                // Send updated state to device
                // <HACK>
                String content = "set event " + eventHolder.getEvent().getUuid() + " state \"" + tagString + "\"";
//                getDevice().sendMessage(content);
                // </HACK>

                // <HACK>
                // TODO: Replace this with a queue.
                getDevice().enqueueMessage(content);
                // </HACK>

                // Refresh the timeline view
                timelineView.refreshTimelineView();
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

        /* Pop-up tag */

        // builder.setTitle ("Message");

        // Declare transformation layout
        LinearLayout designerViewLayout = new LinearLayout (getContext());
        designerViewLayout.setOrientation(LinearLayout.VERTICAL);

        /* Message content */

        // Title
        final TextView messageContentTitle = new TextView (getContext());
        messageContentTitle.setText("Message");
        messageContentTitle.setPadding(70, 20, 70, 20);
        designerViewLayout.addView(messageContentTitle);

        // Content input field
        final EditText messageContentEditor = new EditText(getContext());
        messageContentEditor.setInputType(InputType.TYPE_CLASS_TEXT);
        designerViewLayout.addView(messageContentEditor);

        /* Message type */

        // Get list of devices that have been discovered
        final ArrayList<String> messageDestinationData = new ArrayList<String>();

        // Title
        final TextView messageTypeTitle = new TextView (getContext());
        messageTypeTitle.setText("Type");
        messageTypeTitle.setPadding(70, 20, 70, 20);
        designerViewLayout.addView(messageTypeTitle);

        // List of types (i.e., TCP, UDP, Mesh, etc.)
        final Spinner messageTypeSelector = new Spinner (getContext());
        ArrayList<String> messageTypeData = new ArrayList<String>();
        messageTypeData.add("Device"); // Same device (pure message passing between events)
        messageTypeData.add("UDP"); // Internet
        messageTypeData.add("TCP"); // Internet
        messageTypeData.add("Mesh"); // Clay
        ArrayAdapter<String> messageTypeDataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, messageTypeData); //selected item will look like a spinner set from XML
        messageTypeDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        messageTypeSelector.setAdapter(messageTypeDataAdapter);
        designerViewLayout.addView(messageTypeSelector);

        /* Message destination address */

        // Destination label
        final TextView messageDestinationTitle = new TextView (getContext());
        messageDestinationTitle.setText("Destination");
        messageDestinationTitle.setPadding(70, 20, 70, 20);
        designerViewLayout.addView(messageDestinationTitle);

        // Set destination of message
        final Spinner messageDestinationSelector = new Spinner (getContext());
        final ArrayAdapter<String> messageDestinationDataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, messageDestinationData); //selected item will look like a spinner set from XML
        messageDestinationDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        messageDestinationSelector.setAdapter(messageDestinationDataAdapter);
        designerViewLayout.addView(messageDestinationSelector);

        // "Other" destination address, specified by user
        final TextView messageCustomDestinationTitle = new TextView (getContext());
        messageCustomDestinationTitle.setText("Other");
        messageCustomDestinationTitle.setPadding(70, 20, 70, 20);
        messageCustomDestinationTitle.setVisibility(View.GONE);
        designerViewLayout.addView(messageCustomDestinationTitle);

        // "Other" destination input field
        final EditText messageCustomDestinationEditor = new EditText(getContext());
        messageCustomDestinationEditor.setInputType(InputType.TYPE_CLASS_TEXT);
        messageCustomDestinationEditor.setVisibility(View.GONE);
        designerViewLayout.addView(messageCustomDestinationEditor);

        /* Set the view */

        builder.setView(designerViewLayout);

        /* Set up interactivity */

        messageTypeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = messageTypeSelector.getItemAtPosition(position).toString();
                if (selectedItemText.equals("Device")) {
                    messageDestinationTitle.setVisibility(View.GONE);
                    messageDestinationSelector.setVisibility(View.GONE);
                    // Get list of all discovered devices on the mesh network
                    messageDestinationData.clear();
                    // TODO: Get list of all discovered smartphones, tablets, and other devices for interacting with Clay
                    messageDestinationDataAdapter.notifyDataSetChanged();
                } else if (selectedItemText.equals("UDP") || selectedItemText.equals("TCP")) {
                    messageDestinationTitle.setVisibility(View.VISIBLE);
                    messageDestinationSelector.setVisibility(View.VISIBLE);
                    // Get list of all discovered devices on the mesh network
                    messageDestinationData.clear();
                    // TODO: Get list of all discovered smartphones, tablets, and other devices for interacting with Clay
                    messageDestinationData.add(getClay().getInternetBroadcastAddress() + ":4445"); // Broadcast address
                    messageDestinationData.add(getClay().getInternetAddress() + ":4445"); // This device's address
                    messageDestinationData.add("Other");
                    messageDestinationDataAdapter.notifyDataSetChanged();
                } else if (selectedItemText.equals("Mesh")) {
                    messageDestinationTitle.setVisibility(View.VISIBLE);
                    messageDestinationSelector.setVisibility(View.VISIBLE);
                    // Get list of all discovered devices on the mesh network
                    messageDestinationData.clear();
                    for (Device device : getClay().getDevices()) {
                        messageDestinationData.add(device.getUuid().toString());
                    }
                    // Note: There's no "Other" option for mesh.
                    messageDestinationDataAdapter.notifyDataSetChanged();
                }

                // Select the first item by default
                messageDestinationSelector.setSelection(0, true);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                messageTypeSelector.setSelection(0, true);
            }
        });

        messageDestinationSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItemText = messageDestinationSelector.getItemAtPosition(position).toString();
                if (selectedItemText.equals("Other")) {
                    messageCustomDestinationTitle.setVisibility(View.VISIBLE);
                    messageCustomDestinationEditor.setVisibility(View.VISIBLE);
                    messageCustomDestinationEditor.invalidate();
                } else {
                    messageCustomDestinationTitle.setVisibility(View.GONE);
                    messageCustomDestinationEditor.setVisibility(View.GONE);
                    messageCustomDestinationEditor.invalidate();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                messageCustomDestinationTitle.setVisibility(View.GONE);
                messageCustomDestinationEditor.setVisibility(View.GONE);
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
        messageContentEditor.setText(currentContentString);
        messageContentEditor.setSelection(messageContentEditor.getText().length());

        // Message type
        int messageTypeIndex = messageTypeDataAdapter.getPosition(currentTypeString);
        messageTypeSelector.setSelection(messageTypeIndex);

        // Message destination
        int messageDestinationIndex = messageDestinationDataAdapter.getPosition(currentDestinationAddressString);
        messageDestinationSelector.setSelection(messageDestinationIndex);

        messageCustomDestinationEditor.setText(currentDestinationAddressString);

        // Set up the buttons
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Update the behavior profile state
                // e.g., "udp 192.168.1.255:4445 \"hello world\""
                // TODO: "udp none 192.168.1.255:4445 \"hello world\""... make same message string format!
                String messageTypeString = messageTypeSelector.getSelectedItem().toString();
                String stateString = "";
                if (messageTypeString.equals("Device")) {
                    stateString = messageTypeString;
                    stateString += " " + "none";
                    stateString += " \'" + messageContentEditor.getText().toString() + "\'";
                } else {
                    stateString = messageTypeString;
                    if (!messageDestinationSelector.getSelectedItem().toString().equals("Other")) {
                        stateString += " " + messageDestinationSelector.getSelectedItem().toString();
                    } else {
                        stateString += " " + messageCustomDestinationEditor.getText().toString();
                    }
                    stateString += " \'" + messageContentEditor.getText().toString() + "\'";
                }

                // Update the behavior state
                // <HACK>
                eventHolder.getEvent().setTimeline(device.getTimeline());
                // </HACK>
                eventHolder.updateState(stateString);

                // Store: Store the new behavior state and update the event.
//                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState());
                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState().get(0));
                getClay().getStore().storeEvent (eventHolder.getEvent());

                // Send updated state to device
                // <HACK>
                String stateToSend = stateString.substring (0, stateString.indexOf(" ")).toLowerCase() + " " + stateString.substring(stateString.indexOf(" ") + 1);
                String content = "set event " + eventHolder.getEvent().getUuid() + " state \"" + stateToSend + "\"";
//                getDevice().sendMessage(content);
                // </HACK>

                Log.v ("Event_Trigger", "state: " + stateToSend);
                Log.v ("Event_Trigger", "update: " + content);

                // <HACK>
                // TODO: Replace this with a queue.
                getDevice().enqueueMessage(content);
                // </HACK>

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                timelineView.refreshTimelineView();

//                // Update the behavior state
//                State behaviorState = new State(eventHolder.getEvent().getAction(), eventHolder.getEvent().getAction().getTag(), stateString);
//                eventHolder.getEvent().setAction(eventHolder.getEvent().getAction(), behaviorState);
//
//                // ...then addDevice it to the device...
//                String behaviorUuid = eventHolder.getEvent().getAction().getUuid().toString();
//                device.sendMessage("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay().getStore().storeState(behaviorState);
//                eventHolder.getEvent().setAction(eventHolder.getEvent().getAction(), behaviorState);
//                //getClay ().getStore().updateBehaviorState(behaviorState);
//                getClay().getStore().updateTimeline(device.getTimeline());
//
//                // Refresh the timeline view
//                refreshTimelineView();
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
        LinearLayout designerViewLayout = new LinearLayout (getContext());
        designerViewLayout.setOrientation(LinearLayout.VERTICAL);

        // Set up the frequency
        final TextView frequencyLabel = new TextView (getContext());
        frequencyLabel.setPadding(70, 20, 70, 20);
        designerViewLayout.addView(frequencyLabel);

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
            public void onProgressChanged(SeekBar seekBar, final int progress, boolean fromUser) {
                frequencyLabel.setText("Frequency (" + progress + " Hz)");

                // Preview tone played in background thread
                final Handler handler = new Handler();

                // Use a new tread as this can take a while
                final Thread thread = new Thread(new Runnable() {
                    public void run() {
                        // genTone();
                        handler.post(new Runnable() {

                            public void run() {
                                // playSound();
                                ApplicationView.getApplicationView().playTone (Double.parseDouble(String.valueOf (progress)), 0.2);
                            }
                        });
                    }
                });
                thread.start();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        designerViewLayout.addView(frequencyVal);

        // Set up the duration
        final TextView durationLabel = new TextView (getContext());
        durationLabel.setPadding(70, 20, 70, 20);
        designerViewLayout.addView(durationLabel);

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
        designerViewLayout.addView(durationVal);

        // Assign the layout to the alert dialog.
        builder.setView(designerViewLayout);

        // Set up the buttons
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Create transform string
                String updatedStateString = "frequency " + frequencyVal.getProgress() + " hz " + durationVal.getProgress() + " ms";

                // Update the behavior state
                // <HACK>
                eventHolder.getEvent().setTimeline(device.getTimeline());
                // </HACK>
                eventHolder.updateState(updatedStateString);

                // Store: Store the new behavior state and update the event.
//                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState());
                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState().get(0));
                getClay().getStore().storeEvent(eventHolder.getEvent());

                // Send updated state to device
                // <HACK>
                String content = "set event " + eventHolder.getEvent().getUuid() + " state \"" + updatedStateString + "\"";
//                getDevice().sendMessage(content);
                // </HACK>

                // <HACK>
                // TODO: Replace this with a queue.
                getDevice().enqueueMessage(content);
                // </HACK>

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                timelineView.refreshTimelineView();

//                // Update the behavior state
//                State behaviorState = new State(eventHolder.getEvent().getAction(), eventHolder.getEvent().getAction().getTag(), stateString);
//                eventHolder.getEvent().setAction(eventHolder.getEvent().getAction(), behaviorState);
//
//                // ...then addDevice it to the device...
//                String behaviorUuid = eventHolder.getEvent().getAction().getUuid().toString();
//                device.sendMessage("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay().getStore().storeState(behaviorState);
//                eventHolder.getEvent().setAction(eventHolder.getEvent().getAction(), behaviorState);
//                //getClay ().getStore().updateBehaviorState(behaviorState);
//                getClay().getStore().updateTimeline(device.getTimeline());
//
//                // Refresh the timeline view
//                refreshTimelineView();
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

        ApplicationView.getApplicationView().speakPhrase(phrase);

        // Set up the buttons
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Save configuration options to object
//                item.phrase = input.getText().toString();

                String updatedStateString = input.getText().toString();

                ApplicationView.getApplicationView().speakPhrase(updatedStateString);

                // Update the behavior state
                // <HACK>
                eventHolder.getEvent().setTimeline(device.getTimeline());
                // </HACK>
                eventHolder.updateState (updatedStateString);

                // Store: Store the new behavior state and update the event.
//                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState());
                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState().get(0));
                getClay().getStore().storeEvent(eventHolder.getEvent());

                // Send updated state to device
                // <HACK>
                String content = "set event " + eventHolder.getEvent().getUuid() + " state \"" + updatedStateString + "\"";
//                getDevice().sendMessage(content);
                // </HACK>

                // <HACK>
                // TODO: Replace this with a queue.
                getDevice().enqueueMessage(content);
                // </HACK>

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                timelineView.refreshTimelineView();

//                State behaviorState = new State(eventHolder.getEvent().getAction(), eventHolder.getEvent().getAction().getTag(), stateString);
////                eventHolder.getEvent().getAction().setState(behaviorState);
//                eventHolder.getEvent().setAction(eventHolder.getEvent().getAction(), behaviorState);
//
//                // ...then addDevice it to the device...
//                String behaviorUuid = eventHolder.getEvent().getAction().getUuid().toString();
//                device.sendMessage("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay ().getStore().storeState(behaviorState);
//                eventHolder.getEvent().setAction(eventHolder.getEvent().getAction(), behaviorState);
//                //getClay ().getStore().updateBehaviorState(behaviorState);
//                getClay ().getStore().updateTimeline(device.getTimeline());
//
//                // Refresh the timeline view
//                refreshTimelineView();
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
        LinearLayout designerViewLayout = new LinearLayout (getContext());
        designerViewLayout.setOrientation(LinearLayout.VERTICAL);

        // Set up the label
        final TextView waitLabel = new TextView (getContext());
        waitLabel.setPadding(70, 20, 70, 20);
        designerViewLayout.addView(waitLabel);

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
//                getDevice().enqueueMessage("set event " + eventHolder.getEvent().getUuid() + " state \"" + String.valueOf(progress) + "\"");
//                getDevice().enqueueMessage("set event " + eventHolder.getEvent().getUuid() + " duration \"" + String.valueOf(progress) + "\"");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        designerViewLayout.addView(waitVal);

        // Assign the layout to the alert dialog.
        builder.setView(designerViewLayout);

        // Set up the buttons
        builder.setPositiveButton ("DONE", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {

                // Create transform string
                String updatedStateString = "" + waitVal.getProgress();

                // Update the behavior state
                // <HACK>
                eventHolder.getEvent().setTimeline(device.getTimeline());
                // </HACK>
                eventHolder.updateState(updatedStateString);

                // Store: Store the new behavior state and update the event.
//                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState());
                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState().get(0));
                getClay().getStore().storeEvent(eventHolder.getEvent());

                // Send updated state to device
                // <HACK>
//                getDevice().sendMessage(content);
                // </HACK>

                // <HACK>
                // TODO: Replace this with a queue.
                getDevice().enqueueMessage("set event " + eventHolder.getEvent().getUuid() + " state \"" + updatedStateString + "\"");
                getDevice().enqueueMessage("set event " + eventHolder.getEvent().getUuid() + " duration \"" + updatedStateString + "\"");
                // </HACK>

                // Refresh the timeline view
                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
                timelineView.refreshTimelineView();

//                // Add wait
//                State behaviorState = new State (eventHolder.getEvent().getAction(), eventHolder.getEvent().getAction().getTag(), "" + waitVal.getProgress());
//                eventHolder.getEvent().setAction(eventHolder.getEvent().getAction(), behaviorState);
//
//                // ...then addDevice it to the device...
//                String behaviorUuid = eventHolder.getEvent().getAction().getUuid().toString();
//                device.sendMessage("update behavior " + behaviorUuid + " \"" + eventHolder.getEvent().getState().getState() + "\"");
//
//                // ...and finally update the repository.
//                getClay ().getStore().storeState(behaviorState);
//                eventHolder.getEvent().setAction(eventHolder.getEvent().getAction(), behaviorState);
//                getClay ().getStore().updateTimeline(device.getTimeline());
//
//                // Refresh the timeline view
//                refreshTimelineView();
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
