package camp.computer.clay.sequencer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.azeesoft.lib.colorpicker.ColorPickerDialog;
import com.mobeta.android.sequencer.R;

import java.util.ArrayList;
import java.util.UUID;

import camp.computer.clay.system.Clay;
import camp.computer.clay.system.ContentEntry;
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
            lightToggleButtons.add(toggleButton); // Add the button to the choose.
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
            lightColorButtons.add(colorButton); // Add the button to the choose.
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

        // Get choose of devices that have been discovered
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

//        final EditText pulseSignalFrequencyDataEditor = new EditText(getContext());
////        pulseSignalFrequencyDataEditor.setInputType(InputType.TYPE_CLASS_NUMBER);
//        pulseDataLayout.addView(pulseSignalFrequencyDataEditor);

        final int[] channelIndex = { 0 };

//        // <HACK>
//        // TODO: Update this from a choose of the observables received from the boards.
//        final ContentEntry state = new ContentEntry("channel", "");
//        for (int i = 0; i < 12; i++) {
//            ContentEntry channelStructure = state.put(String.valueOf(i + 1), "");
//
//            // channel/n/number
//            channelStructure.put("number", channelNumberSelector.getSelectedItem().toString());
//
//            // channel/n/direction
//            channelStructure.put("direction", signalDirectionSelector.getSelectedItem().toString());
//
//            // channel/n/type
//            channelStructure.put("type", signalTypeSelector.getSelectedItem().toString());
//
//            // channel/n/content
//            ContentEntry channelContentStructure = channelStructure.put("content", "");
//
//            // channel/n/content/<observable>
//            channelContentStructure.put("toggle_value", "off");
//            channelContentStructure.put("waveform_sample_value", "none");
//            channelContentStructure.put("pulse_period_seconds", "0");
//            channelContentStructure.put("pulse_duty_cycle", "0");
//        }
//        // </HACK>

        final Button periodDataSourceButton = new Button (getContext());
        periodDataSourceButton.setText("!");
        periodDataSourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UUID deviceUuid = eventHolder.getEvent().getTimeline().getDevice().getUuid();
                //ContentEntry contentEntry = getClay().getContent().get("devices").get(deviceUuid.toString()).get("channels").get(String.valueOf(channelIndex[0] + 1));
                ContentEntry contentEntry = getClay().getContent().get("devices").get(deviceUuid.toString()).get("channels");
                // TODO: ContentEntry contentEntry = getClay().getContent().get("devices").get(deviceUuid.toString()).get("channels").get(String.valueOf(channelIndex[0] + 1)).get("content");
//                displayListItemSelector (contentEntry);
                Log.v ("Content_View", "contentEntry: " + contentEntry);
                if (contentEntry != null) {
                    Log.v ("Content_View", "contentEntry: " + contentEntry.getKey());
                    Log.v ("Content_View", "contentEntry: " + contentEntry.getContent());
                    displayUpdateData (contentEntry, true, true);
                }
            }
        });

        pulseDataLayout.addView(periodDataSourceButton);

        // "Pulse" data editor: Duty Cycle
        final TextView pulseDutyCycleTitle = new TextView (getContext());
        pulseDutyCycleTitle.setText("Duty Cycle");
        pulseDutyCycleTitle.setPadding(70, 20, 70, 20);
        pulseDataLayout.addView(pulseDutyCycleTitle);

//        final EditText pulseSignalDutyCycleDataEditor = new EditText(getContext());
////        pulseSignalDutyCycleDataEditor.setInputType(InputType.TYPE_CLASS_NUMBER);
//        pulseDataLayout.addView(pulseSignalDutyCycleDataEditor);

//        final int[] channelIndex = {0};

//        // <HACK>
//        // TODO: Update this from a choose of the observables received from the boards.
//        final ContentEntry state = new ContentEntry("channel", "");
//        for (int i = 0; i < 12; i++) {
//            ContentEntry channelState = state.put(String.valueOf(i + 1), "");
//            channelState.put("toggle_value", "off");
//            channelState.put("waveform_sample_value", "none");
//            channelState.put("pulse_period_seconds", "0");
//            channelState.put("pulse_duty_cycle", "0");
//        }
//        // </HACK>

        final Button dutyCycleDataSourceButton = new Button (getContext());
        dutyCycleDataSourceButton.setText("!");
        dutyCycleDataSourceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                ContentEntry contentEntry = state.get(String.valueOf(channelIndex[0] + 1));
                UUID deviceUuid = eventHolder.getEvent().getTimeline().getDevice().getUuid();
                ContentEntry contentEntry = getClay().getContent().get("devices").get(deviceUuid.toString()).get("channels").get(String.valueOf(channelIndex[0] + 1));
//                displayListItemSelector (contentEntry);
                displayUpdateData(contentEntry, true, true);
            }
        });

        pulseDataLayout.addView(dutyCycleDataSourceButton);

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




                        /*
                        stateString += pulseSignalFrequencyDataEditor.getText().toString() + ",";
                        stateString += pulseSignalDutyCycleDataEditor.getText().toString();
                        */



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

        /*
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




//                        stateString += pulseSignalFrequencyDataEditor.getText().toString() + ",";
//                        stateString += pulseSignalDutyCycleDataEditor.getText().toString();





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
        */

        /*
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
        */

        /* Set the view */

        builder.setView(designerViewLayout);

        /* Set up interactivity */

        channelNumberSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.v ("Signal", "SELECTED CHANNEL");

                // Get selected channel's state
                String channelNumberString = channelNumberSelector.getItemAtPosition(position).toString();
                channelIndex[0] = Integer.parseInt(channelNumberString) - 1;
                String channelStateString = channelStateStrings.get(channelIndex[0]);

                // Get channel's direction
                String channelDirectionString = "" + channelStateString.charAt(1);
                if (channelDirectionString.equals("I")) {
                    signalDirectionSelector.setSelection(0);
                } else if (channelDirectionString.equals("O")) {
                    signalDirectionSelector.setSelection(1);
                }

                // Get channel's type
                String channelTypeString = "" + channelStateString.charAt(2);
                if (channelTypeString.equals("T")) {
                    signalTypeSelector.setSelection(0);
                } else {
                    signalTypeSelector.setSelection(1);
                }

                // Get channel's data
                if (channelDirectionString.equals("O")) {
                    String toggleSignalDataString = channelStateString.split(":")[1];
                    if (channelTypeString.equals("T")) {
                        toggleSignalDataEditor.setText(toggleSignalDataString);
                    } else if (channelTypeString.equals("P")) {
                        String[] toggleSignalDataStrings = toggleSignalDataString.split(",");
//                        pulseSignalFrequencyDataEditor.setText(toggleSignalDataStrings[0]);
//                        pulseSignalDutyCycleDataEditor.setText(toggleSignalDataStrings[1]);
                    }

//                    UPDATE THIS STATE...
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
                    // Get choose of all discovered devices on the mesh network
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
//                        stateString += pulseSignalFrequencyDataEditor.getText().toString() + ",";
//                        stateString += pulseSignalDutyCycleDataEditor.getText().toString();
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
//                    pulseSignalFrequencyDataEditor.setText("0");
//                    pulseSignalDutyCycleDataEditor.setText("0");
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
//                        stateString += pulseSignalFrequencyDataEditor.getText().toString() + ",";
//                        stateString += pulseSignalDutyCycleDataEditor.getText().toString();
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

                Log.v("Signal2", "" + currentState);
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
        // e.g., "TOP:3,\'waveform_sample_value\'|\'pulse_duty_cycle\';F,0.02|\'pulse_period_seconds\'"
        String currentStateString = eventHolder.getEvent().getState().get(0).getState();
        final String[] currentStateStrings = currentStateString.split(" ");
        channelStateStrings.clear();
        for (int i = 0; i < currentStateStrings.length; i++) {
            channelStateStrings.add(currentStateStrings[i]);
            Log.v("Signal3", "state " + i + ": " + channelStateStrings.get(i));
        }

        Log.v ("Signal3", "current state: " + currentStateString);



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
        builder.setPositiveButton("EXCELLENT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String stateString = "";
                for (String state : channelStateStrings) {
                    stateString += " " + state;
                }
                stateString = stateString.trim();
                Log.v("Signal4", "state: " + stateString);

                // Update the behavior state
                eventHolder.getEvent().setTimeline(device.getTimeline());
                eventHolder.updateState(stateString);

                // Store: Store the new behavior state and update the event.
                // getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState());
                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState().get(0));
                getClay().getStore().storeEvent(eventHolder.getEvent());

                // Context
                // i.e., The context is generally encoded as <channel-settings>:<channel-source>,<observable-source-key>|<observable-destination-key>
                // e.g., set event <event-uuid> context "TOT:3,\"waveform-sample-value\"|\"pulse_duty_cycle\""

                // "channel":"4":"pulse_duty_cycle"="channel":"3":"waveform_sample_value"
                // "channels" / "4" / "pulse_duty_cycle" <-- "channels" / "3" / "waveform_sample_value"
                //                                  (OR) <-- "observables" / "<observable id>"
                // "channels" / "4" / "pulse_period_seconds" <-- "numbers" / "0.02f"
                //
                // TOP:3,\'waveform_sample_value\'|\'pulse_duty_cycle\';F,0.02|\'pulse_period_seconds\'

                // String contextString = "TIT:none TIT:none TIW:none TOP:3,'waveform_sample_value'|'pulse_duty_cycle';F,0.02|'pulse_period_seconds' TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none";
                String contextString = "";

//                // source_channel
//                for (int i = 0; i < 12; i++) {
//                    ContentEntry contentEntry = state.get(String.valueOf(i + 1));
//                    if (contentEntry != null) {
//                        if (contentEntry.contains("source_observable")) {
//                            ContentEntry sourceObservableContent = contentEntry.get("source_observable");
//
//                            Log.v("hmap", "source_observable[" + i + "]: " + sourceObservableContent.getContent());
//
//                            // <HACK>
////                            ContentEntry sourceChannelContent = contentEntry.put("source_channel", pulseSignalDutyCycleDataEditor.getText().toString());
//                            // Log.v("hmap", "source_channel[" + i + "]: " + sourceChannelContent.getContent());
//                            // </HACK>
//
//                            // <HACK>
//                            ContentEntry destinationObservableContent = null;
//                            String selectedItemText = signalTypeSelector.getSelectedItem().toString();
//                            if (selectedItemText.equals("toggle")) {
//
//                            } else if (selectedItemText.equals("waveform")) {
//
//                            } else if (selectedItemText.equals("pulse")) {
//                                destinationObservableContent = contentEntry.put("destination_observable", "pulse_duty_cycle");
//                                // Log.v("hmap", "destination_observable[" + i + "]: " + destinationObservableContent.getContent());
//                            }
//                            // </HACK>
//
//
//
//
//                            // TODO: Iterate over all observables for this channel (channel i) and assign values to them
//                            // ;F,0.02|'pulse_period_seconds'
//                            contextString += " " + channelStateStrings.get(i).split(":")[0];
////                            contextString += ":" + sourceChannelContent.getContent() + ",'" + sourceObservableContent.getContent() + "'|'" + destinationObservableContent.getContent() + "'";
////                            Log.v("hmap", "stringToSend: " + contextString);
//                            //TODO: Add... 'pulse_period_seconds': pulseSignalFrequencyDataEditor.getText().toString()
//
//                            // <HACK>
//                            contextString += ";F,0.02|'pulse_period_seconds'";
//                            // </HACK>
//                        } else {
//                            // ":none"
//
//                            contextString += " " + channelStateStrings.get(i).split(":")[0];
//                            contextString += ":none";
////                            Log.v("hmap", "stringToSend: " + contextString);
//                        }
//                    }
//                }

                contextString = contextString.trim();
                Log.v("Signal5", "state: " + contextString);

//                String contextString = "TIT:none TIT:none TIW:none TOP:3,'waveform_sample_value'|'pulse_duty_cycle';F,0.02|'pulse_period_seconds' TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none";
                String contextContent = "set event " + eventHolder.getEvent().getUuid() + " context \"" + contextString + "\"";

                getDevice().enqueueMessage(contextContent);

                // Send updated state to device
                // <HACK>
//                stateString = "none none none 0.02,64450 none none none none none none none none";
//                //stateString = "TIT:none:none TIT:none:none TIW:none TOP:0.02,64450:3,'waveform-sample-value'|'pulse_duty_cycle' TIT:none:none TIT:none:none TIT:none:none TIT:none:none TIT:none:none TIT:none:none TIT:none:none TIT:none:none";
//                String content = "set event " + eventHolder.getEvent().getUuid() + " state \"" + stateString + "\"";
////                getDevice().sendMessage(content);
                // </HACK>

                Log.v("Event_Trigger", "state: " + stateString);
//                Log.v ("Event_Trigger", "update: " + content);

                // <HACK>
                // TODO: Replace this with a queue.
                String stateContent = "set event " + eventHolder.getEvent().getUuid() + " state \"" + eventHolder.getEvent().getState().get(0).getState().toString() + "\"";
                getDevice().enqueueMessage(stateContent);
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

    /**
     * Display the behaviors available for selection, starting with basic, cached, public.
     */
    public void displayListItemSelector(final ContentEntry content) {

        // Get choose of behaviors available for selection
        final String[] exposedObservables = new String[4];
        exposedObservables[0] = "toggle_value";
        exposedObservables[1] = "waveform_sample_value";
        exposedObservables[2] = "pulse_duty_cycle";
        exposedObservables[3] = "pulse_period_seconds";

        // Show the choose of behaviors
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setItems(exposedObservables, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int itemIndex) {

                // Get selected item
                String selectedItem = exposedObservables[itemIndex];

                content.put("source_observable", selectedItem);
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void displayUpdateData (final ContentEntry contentEntry, boolean showConstant, boolean showDataSources) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//        builder.setTitle(content.getKey());

//        final ContentEntry[] content = { null };

//        // <HACK>
//        for (ContentEntry childEntry : contentEntry.getChildren()) {
//            if (childEntry.contains("number")) {
//                content[0] = childEntry;
//                break;
//            }
//        }
//        // </HACK>

        final LinearLayout designerViewLayout = new LinearLayout (getContext());

        LinearLayout channelSelectionButtonsLayout2 = new LinearLayout (getContext());
        final LinearLayout channelDirectionSelectionButtonsLayout = new LinearLayout (getContext());
        channelDirectionSelectionButtonsLayout.setId(R.id.channel_direction);
        final LinearLayout channelTypeSelectionButtonsLayout = new LinearLayout (getContext());

        final Spinner contentTypeSelector = new Spinner (getContext());
        final EditText numberEntryView = new EditText(getContext());
        final TextView contentProviderTitle = new TextView (getContext());
        final LinearLayout channelSelectionButtonsLayout = new LinearLayout (getContext());
        final TextView contentSelectionLabel = new TextView (getContext());
        final ListView contentProviderListView = new ListView(getContext());

        // Declare transformation layout
        // final LinearLayout designerViewLayout = new LinearLayout (getContext());
        designerViewLayout.setOrientation(LinearLayout.VERTICAL);

        // Style (LayoutParams)
        LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params5.setMargins(0, 0, 0, 0);
        designerViewLayout.setLayoutParams(params5);

        // Title
        final TextView channelEnabledLabel = new TextView (getContext());
        channelEnabledLabel.setText(contentEntry.getKey());
        channelEnabledLabel.setTextSize(20);
        channelEnabledLabel.setPadding(70, 20, 70, 20);
        channelEnabledLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        designerViewLayout.addView(channelEnabledLabel);

//        // <SECTION: CHANNEL SELECTION BUTTONS>
//        final ArrayList<Button> channelSelectionButtonList2 = new ArrayList<Button>();
//        final Button[] selectedButton2 = {null};
//        // final LinearLayout channelSelectionButtonsLayout = new LinearLayout (getContext());
//        channelSelectionButtonsLayout2.setOrientation(LinearLayout.HORIZONTAL);
//        for (int i = 0; i < 12; i++) {
//
//            // Create
//            final Button channelNumberButton = new Button (getContext());
//
//            // Text
//            final String channelNumberString = Integer.toString (i + 1);
//            channelNumberButton.setText(channelNumberString);
//            channelNumberButton.setTextSize(12);
//
//            // Style
//            channelNumberButton.setPadding(0, 0, 0, 0);
//            channelNumberButton.setBackgroundColor(Color.TRANSPARENT);
//            channelNumberButton.setTextColor(Color.LTGRAY);
//
//            // Style (LayoutParams)
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
//            params.setMargins(0, 0, 0, 0);
//            channelNumberButton.setLayoutParams(params);
//
//            // Add to view
//            channelSelectionButtonsLayout2.addView(channelNumberButton);
//
//            // Add to button choose
//            channelSelectionButtonList2.add(channelNumberButton);
//        }
//
//        // Setup: Set up interactivity.
//        for (int i = 0; i < channelSelectionButtonList2.size(); i++) {
//
//            final Button channelSelectionButton = channelSelectionButtonList2.get (i);
//
//            final int finalI = i;
//            channelSelectionButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    // Reset.
//                    for (int i = 0; i < channelSelectionButtonList2.size(); i++) {
//                        channelSelectionButtonList2.get(i).setTextColor(Color.LTGRAY);
//                        channelSelectionButtonList2.get(i).setTypeface(null, Typeface.NORMAL);
//                    }
//
//                    // Select.
//                    if (selectedButton2[0] != channelSelectionButton) {
//                        selectedButton2[0] = channelSelectionButton; // Button. Select the button.
//                    } else {
//                        selectedButton2[0] = null; // Deselect the button.
//                    }
//
//                    // Color.
//                    if (selectedButton2[0] != null) {
//                        int textColor = ApplicationView.getApplicationView().getResources().getColor(R.color.timeline_segment_color);
//                        selectedButton2[0].setTextColor(textColor); // Color. Update the color.
//                        selectedButton2[0].setTypeface(null, Typeface.BOLD);
//                    }
//
//                    // Data.
//                    //content.get("content").put("provider", selectedButton2[0].getText().toString());
//                    for (ContentEntry childEntry : contentEntry.getChildren()) {
//                        if (childEntry.contains("number")) {
//                            if (childEntry.get("number").equals(String.valueOf(finalI + 1))) {
//                                content[0] = childEntry;
//                            }
//                        }
//                    }
////                    ContentEntry contentEntry = getClay().getContent().get("devices").get(deviceUuid.toString()).get("channels").get(String.valueOf(channelIndex[0] + 1));
//                }
//            });
//        }


        // Channel chooser
        channelSelectionButtonsLayout2 = (LinearLayout) generateChannelChooserView(contentEntry);
        designerViewLayout.addView (channelSelectionButtonsLayout2);

        // Single channel (chosen above) controller
        LinearLayout channelConfigurationView = (LinearLayout) generateChannelConfigurationView(contentEntry, true, "direction", "type");
        designerViewLayout.addView(channelConfigurationView);

        // TODO: Update this one so it generates appropriately...
//        LinearLayout exposedChannelView = (LinearLayout) generateChoiceView();
        //contentEntry.choice().get("number").getContent()
        //LinearLayout exposedChannelView = (LinearLayout) generateContentInputView(contentEntry.choice().get("pulse_duty_cycle"));

        // The channel's content store
        final LinearLayout baseView = new LinearLayout (getContext());
        baseView.setOrientation(LinearLayout.HORIZONTAL);
        baseView.setVerticalGravity(Gravity.CENTER_HORIZONTAL);

        for (ContentEntry value : contentEntry.choice().get("content").getChildren()) {
            // TODO: final ArrayList<Button> optionButtonList = new ArrayList<Button>(); ...
            // TODO: ...add callback to update the selected content key (in the object)
            LinearLayout exposedChannelView = (LinearLayout) generateContentInputView (value);
            baseView.addView(exposedChannelView);
        }

        designerViewLayout.addView(baseView);






        // TODO: contentEntry.addOnContentChangeListener (for channel, update data view based on content + selected content key)...
        // TODO: ...add the following into: generateDataEntryView() and place in callback for selected content key...
        // TODO:    ...and update the state of the data view on every call, for the selected combo...

        // "Enter data..."
        final TextView dataEntryLabel = new TextView (getContext());
        dataEntryLabel.setText("Put the data here.");
//        channelEnabledLabel.setTextSize(20);
//        channelEnabledLabel.setPadding(70, 20, 70, 20);
        dataEntryLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        designerViewLayout.addView(dataEntryLabel);

        // <SWITCH-TITLE: CONTENT TYPE SELECTOR>
        // Number title
        // final Spinner contentTypeSelector = new Spinner (getContext());
        ArrayList<String> contentTypeData = new ArrayList<String>();
        contentTypeData.add("number");
        contentTypeData.add("data");

        ArrayAdapter<String> contentTypeDataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, contentTypeData); //selected item will look like a spinner set from XML
        contentTypeDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        contentTypeSelector.setAdapter(contentTypeDataAdapter);
        contentTypeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                // Reset.
                for (int i = 0; i < parent.getChildCount(); i++) {
                    ((TextView) parent.getChildAt(i)).setTextColor (Color.LTGRAY);
                }

                // Select.
                int textColor = ApplicationView.getApplicationView().getResources().getColor(R.color.timeline_segment_color);
                ((TextView) parent.getChildAt(0)).setTextColor(textColor);
                ((TextView) parent.getChildAt(0)).setTextSize(12);
                ((TextView) parent.getChildAt(0)).setAllCaps(true);
                ((TextView) parent.getChildAt(0)).setTypeface(null, Typeface.BOLD);

                // Data.
//                contentEntry.choice().get("content").put("type", contentTypeSelector.getSelectedItem().toString());
                contentEntry.choice().put("type", contentTypeSelector.getSelectedItem().toString());

//                // View.
//                if (contentEntry.choice().get("content").get("type").getContent().equals("number")) {
//                    // Reset.
//                    numberEntryView.setVisibility(View.GONE);
//                    contentProviderTitle.setVisibility(View.GONE);
//                    channelSelectionButtonsLayout.setVisibility(View.GONE);
//                    contentSelectionLabel.setVisibility(View.GONE);
//                    contentProviderListView.setVisibility(View.GONE);
//
//                    // Show.
//                    numberEntryView.setVisibility(View.VISIBLE);
//                } else if (contentEntry.choice().get("content").get("type").getContent().equals("data")) {
//                    // Reset.
//                    numberEntryView.setVisibility(View.GONE);
//                    contentProviderTitle.setVisibility(View.GONE);
//                    channelSelectionButtonsLayout.setVisibility(View.GONE);
//                    contentSelectionLabel.setVisibility(View.GONE);
//                    contentProviderListView.setVisibility(View.GONE);
//
//                    // Show.
//                    contentProviderTitle.setVisibility(View.VISIBLE);
//                    channelSelectionButtonsLayout.setVisibility(View.VISIBLE);
//                    contentSelectionLabel.setVisibility(View.VISIBLE);
//                    contentProviderListView.setVisibility(View.VISIBLE);
//                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        designerViewLayout.addView(contentTypeSelector);
        // </SWITCH-TITLE: CONTENT TYPE SELECTOR>

        // <SECTION: CONTENT INPUT TITLE>
        // Number input
        // final EditText numberEntryView = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        numberEntryView.setInputType(InputType.TYPE_CLASS_TEXT);//input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        designerViewLayout.addView(numberEntryView);
        // </SECTION: CONTENT INPUT TITLE>

        // <TITLE: CONTENT PROVIDER TITLE>
        // "Channel"
        // final TextView channelNumbersLabel = new TextView (getContext());
        contentProviderTitle.setText("PROVIDER"); // or "Channel"
        contentProviderTitle.setTextColor(Color.WHITE);
//        channelEnabledLabel.setTextSize(20);
//        channelEnabledLabel.setPadding(70, 20, 70, 20);
        contentProviderTitle.setAllCaps(true);
        contentProviderTitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        designerViewLayout.addView(contentProviderTitle);
        // </TITLE: CONTENT PROVIDER TITLE>

        // <SECTION: CONTENT PROVIDER SELECTION BUTTONS>
        final ArrayList<Button> channelSelectionButtonList = new ArrayList<Button>();
        final Button[] selectedButton = {null};
        // final LinearLayout channelSelectionButtonsLayout = new LinearLayout (getContext());
        channelSelectionButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < 12; i++) {

            // Create
            final Button channelNumberButton = new Button (getContext());

            // Text
            final String channelNumberString = Integer.toString (i + 1);
            channelNumberButton.setText(channelNumberString);
            channelNumberButton.setTextSize(12);

            // Style
            channelNumberButton.setPadding(0, 0, 0, 0);
            channelNumberButton.setBackgroundColor(Color.TRANSPARENT);
            channelNumberButton.setTextColor(Color.LTGRAY);

            // Style (LayoutParams)
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            params.setMargins(0, 0, 0, 0);
            channelNumberButton.setLayoutParams(params);

            // Add to view
            channelSelectionButtonsLayout.addView(channelNumberButton);

            // Add to button choose
            channelSelectionButtonList.add(channelNumberButton);
        }

        // Setup: Set up interactivity.
        for (int i = 0; i < channelSelectionButtonList.size(); i++) {

            final Button channelSelectionButton = channelSelectionButtonList.get (i);

            channelSelectionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Reset.
                    for (int i = 0; i < channelSelectionButtonList.size(); i++) {
                        channelSelectionButtonList.get(i).setTextColor(Color.LTGRAY);
                        channelSelectionButtonList.get(i).setTypeface(null, Typeface.NORMAL);
                    }

                    // Select.
                    if (selectedButton[0] != channelSelectionButton) {
                        selectedButton[0] = channelSelectionButton; // Button. Select the button.
                    } else {
                        selectedButton[0] = null; // Deselect the button.
                    }

                    // Color.
                    if (selectedButton[0] != null) {
                        int textColor = ApplicationView.getApplicationView().getResources().getColor(R.color.timeline_segment_color);
                        selectedButton[0].setTextColor(textColor); // Color. Update the color.
                        selectedButton[0].setTypeface(null, Typeface.BOLD);
                    }

                    // Data.
                    contentEntry.choice().get("content").put("provider", selectedButton[0].getText().toString());
                }
            });
        }


        designerViewLayout.addView (channelSelectionButtonsLayout);
        // </SECTION: CONTENT PROVIDER SELECTION BUTTONS>

        // <TITLE: CONTENT SOURCE>
        // "Channel"
        // final TextView contentSelectionLabel = new TextView (getContext());
        contentSelectionLabel.setText("Data");
        contentSelectionLabel.setTextColor(Color.WHITE);
//        contentSelectionLabel.setTextSize(20);
//        contentSelectionLabel.setPadding(70, 20, 70, 20);
        contentSelectionLabel.setAllCaps(true);
        contentSelectionLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        designerViewLayout.addView(contentSelectionLabel);
        // </TITLE: CONTENT SOURCE>

        // <SECTION: CONTENT SOURCE>
        // Content Provider
        // ListView contentProviderListView = new ListView(getContext());
        ArrayList<String> contentProviderData = new ArrayList<String>();
        // <HACK>
        // TODO: Generate a choose based on observables received from the device being configured: constraint to possible value choose or regex
        contentProviderData.add("toggle_value");
        contentProviderData.add("waveform_sample_value");
        contentProviderData.add("pulse_period_seconds");
        contentProviderData.add("pulse_duty_cycle");
        // </HACK>
        // Alternative (with checkbox):  ArrayAdapter<String> contentProviderDataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_checked, contentProviderData);
        ArrayAdapter<String> contentProviderDataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, contentProviderData);
        contentProviderListView.setAdapter(contentProviderDataAdapter);

        final int[] selectedDatumIndex = {-1};

        contentProviderListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Reset selection. Resets text color and typeface.
                for (int i = 0; i < parent.getChildCount(); i++) {
                    TextView textView = (TextView) parent.getChildAt(i);
                    textView.setTextColor(Color.WHITE);
                    textView.setTypeface(null, Typeface.NORMAL);
                }

                // Select.
//                CheckedTextView textView = (CheckedTextView) view;
//                textView.setChecked(!textView.isChecked());
                selectedDatumIndex[0] = position;
                TextView textView = (TextView) view;

                // Color.
                int textColor = ApplicationView.getApplicationView().getResources().getColor(R.color.timeline_segment_color);
                textView.setTextColor(textColor);
                textView.setTypeface(null, Typeface.BOLD);

                // Data.
                contentEntry.choice().get("content").put("source", textView.getText().toString());

                // TODO: if (enableMultipleContentProviders) {
            }
        });

        designerViewLayout.addView(contentProviderListView);
        // </SECTION: CONTENT SOURCE>

        // Set the view
        builder.setView(designerViewLayout);

        // Set up the buttons
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String number = contentEntry.choice().get("number").getContent();
                String contentType = contentEntry.choice().get("content").get("type").getContent();
                String contentProvider = contentEntry.choice().get("content").get("provider").getContent();
                String contentSource = contentEntry.choice().get("content").get("source").getContent();

                Log.v ("Content_Editor", "channel." + number + ".content.type: " + contentType);
                Log.v ("Content_Editor", "channel." + number + ".content.provider: " + contentProvider);
                Log.v ("Content_Editor", "channel." + number + ".content.source: " + contentSource);

                // Save configuration options to object
//                item.phrase = input.getText().toString();

//                String updatedStateString = numberEntryView.getText().toString();
//
//                ApplicationView.getApplicationView().speakPhrase(updatedStateString);
//
//                // Update the behavior state
//                // <HACK>
//                eventHolder.getEvent().setTimeline(device.getTimeline());
//                // </HACK>
//                eventHolder.updateState (updatedStateString);
//
//                // Store: Store the new behavior state and update the event.
////                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState());
//                getClay().getStore().storeState(eventHolder.getEvent(), eventHolder.getEvent().getState().get(0));
//                getClay().getStore().storeEvent(eventHolder.getEvent());
//
//                // Send updated state to device
//                // <HACK>
//                String content = "set event " + eventHolder.getEvent().getUuid() + " state \"" + updatedStateString + "\"";
////                getDevice().sendMessage(content);
//                // </HACK>
//
//                // <HACK>
//                // TODO: Replace this with a queue.
//                getDevice().enqueueMessage(content);
//                // </HACK>
//
//                // Refresh the timeline view
//                // TODO: Move this into a manager that is called by Clay _after_ propagating changes through the data model.
//                timelineView.refreshTimelineView();

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

    private View generateChannelChooserView (final ContentEntry contentEntry) {

        // Choices.
        final ArrayList<Button> optionButtonList = new ArrayList<Button>();
        final Button[] selectedOptionButton = { null };

        // Layout.
        final LinearLayout baseView = new LinearLayout (getContext());
        baseView.setOrientation(LinearLayout.HORIZONTAL);
        baseView.setVerticalGravity(Gravity.CENTER_HORIZONTAL);

//        TODO: contentEntry.addOnContentChangeListener(/* code to update the graphical state of the column to reflect the ContentEntry */);
//        TODO: eventually, only call contentChangeListeners for entries that have constraints (i.e., that potentially require updates)

        for (int i = 0; i < 12; i++) {

            // Create
            final Button channelNumberButton = new Button (getContext());

            // Text
            final String channelNumberString = Integer.toString (i + 1);
            channelNumberButton.setText(channelNumberString);
            channelNumberButton.setTextSize(12);

            // Style
            channelNumberButton.setPadding(0, 0, 0, 0);
            channelNumberButton.setBackgroundColor(Color.TRANSPARENT);
            channelNumberButton.setTextColor(Color.LTGRAY);

            // Style (LayoutParams)
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            params.setMargins(0, 0, 0, 0);
            channelNumberButton.setLayoutParams(params);

            // Add to view
            baseView.addView(channelNumberButton);

            // Add to button choose
            optionButtonList.add(channelNumberButton);
        }

        // Setup: Set up interactivity.
        for (int i = 0; i < optionButtonList.size(); i++) {

            final Button optionButton = optionButtonList.get (i);
            final int finalI = i;

            // Listen for content changes.
            final ContentEntry.OnContentChangeListener contentListener = new ContentEntry.OnContentChangeListener() {
                @Override
                public void notifyContentChanged() {
                    Log.v("Content_Tree", "\t\t\tnotifyContentChanged");

                    // Reset.
                    for (int j = 0; j < optionButtonList.size(); j++) {
                        optionButtonList.get(j).setTextColor(Color.LTGRAY);
                        optionButtonList.get(j).setTypeface(null, Typeface.NORMAL);
                    }

                    // Select.
                    Log.v("Compare", "...");
                    for (Button optionButton : optionButtonList) {
                        //if (optionButton.getText().toString().equals(contentEntry.getContent())) {
                        Log.v("Compare", "button.number: " + optionButton.getText().toString());
                        Log.v("Compare", "choice.number: " + contentEntry.choice().get("number").getContent());
                        if (optionButton.getText().toString().equals(contentEntry.choice().get("number").getContent())) {
                            selectedOptionButton[0] = optionButton; // Button. Select the button.
                            break;
                        }
                    }

                    // Color.
                    if (selectedOptionButton[0] != null) {
                        int textColor = ApplicationView.getApplicationView().getResources().getColor(R.color.timeline_segment_color);
                        selectedOptionButton[0].setTextColor(textColor); // Color. Update the color.
                        selectedOptionButton[0].setTypeface(null, Typeface.BOLD);
                    }

                    // Data.
                    if (selectedOptionButton[0] != null) {
                        contentEntry.set(selectedOptionButton[0].getText().toString(), false);
                    }
                }
            };

            // Interactivity.
            optionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Listener.
                    contentEntry.addOnContentChangeListener(contentListener);

                    // Data.
                    contentEntry.set (optionButton.getText().toString());
                }
            });
        }

        return baseView;
    }

    private View generateChannelConfigurationView (ContentEntry contentEntry, boolean horizontal, String... keys) {

        final LinearLayout baseView = new LinearLayout (getContext());

        if (horizontal) {
            baseView.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            baseView.setOrientation(LinearLayout.VERTICAL);
        }

        // Add to view (row)
        for (int i = 0; i < keys.length; i++) {
            LinearLayout columnView = columnView = (LinearLayout) generateVerticalChooserView (contentEntry, keys[i]);
            baseView.addView(columnView);
        }

        // Style (LayoutParams)
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // params.setMargins(0, 0, 0, 0);
        baseView.setLayoutParams(params);

        return baseView;
    }

    private View generateChoiceView() {

        final LinearLayout baseView = new LinearLayout (getContext());

        // <SECTION: CHANNEL SELECTION BUTTONS>
        final ArrayList<String> optionList = new ArrayList<String>();
        optionList.add("pulse_period_seconds");
        optionList.add("pulse_duty_cycle");
        final ArrayList<Button> optionButtonList = new ArrayList<Button>();
        final Button[] selectedOptionButton = { null };
        // final LinearLayout channelSelectionButtonsLayout = new LinearLayout (getContext());
        baseView.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < optionList.size(); i++) {

            // Create
            final Button channelNumberButton = new Button (getContext());

            // Text
            channelNumberButton.setText(optionList.get(i));
            channelNumberButton.setTextSize(12);

            // Style
            channelNumberButton.setPadding(0, 0, 0, 0);
            channelNumberButton.setBackgroundColor(Color.TRANSPARENT);
            channelNumberButton.setTextColor(Color.LTGRAY);

            // Style (LayoutParams)
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            params.setMargins(0, 0, 0, 0);
            channelNumberButton.setLayoutParams(params);

            // Add to view
            baseView.addView(channelNumberButton);

            // Add to button choose
            optionButtonList.add(channelNumberButton);
        }

        // Setup: Set up interactivity.
        for (int i = 0; i < optionButtonList.size(); i++) {

            final Button channelSelectionButton = optionButtonList.get (i);

            channelSelectionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // Reset.
                    for (int i = 0; i < optionButtonList.size(); i++) {
                        optionButtonList.get(i).setTextColor(Color.LTGRAY);
                        optionButtonList.get(i).setTypeface(null, Typeface.NORMAL);
                    }

                    // Select.
                    if (selectedOptionButton[0] != channelSelectionButton) {
                        selectedOptionButton[0] = channelSelectionButton; // Button. Select the button.
                    } else {
                        selectedOptionButton[0] = null; // Deselect the button.
                    }

                    // Color.
                    if (selectedOptionButton[0] != null) {
                        int textColor = ApplicationView.getApplicationView().getResources().getColor(R.color.timeline_segment_color);
                        selectedOptionButton[0].setTextColor(textColor); // Color. Update the color.
                        selectedOptionButton[0].setTypeface(null, Typeface.BOLD);
                    }

                    // Data.
                    //content.get("content").put("provider", selectedOptionButton[0].getText().toString());
                }
            });
        }

        return baseView;
        //designerViewLayout.addView (channelSelectionButtonsLayout2);
        // </SECTION: CHANNEL SELECTION BUTTONS>
    }

    // TODO: public View generateVerticalChooserView (ContentEntry contentEntry, OnContentChangeListener onContentChangeListener) {
    public View generateVerticalChooserView(final ContentEntry contentEntry, final String key) {

        // Choices.
        final ArrayList<Button> optionButtonList = new ArrayList<Button>();
        final Button[] selectedOptionButton = { null };

        // Layout.
        final LinearLayout columnView = new LinearLayout (getContext());
        columnView.setOrientation(LinearLayout.VERTICAL);
        columnView.setVerticalGravity(Gravity.CENTER_VERTICAL);

//        TODO: contentEntry.addOnContentChangeListener(/* code to update the graphical state of the column to reflect the ContentEntry */);
//        TODO: eventually, only call contentChangeListeners for entries that have constraints (i.e., that potentially require updates)

        ArrayList<String> optionList = contentEntry.choice().get(key).getContentRange();

        for (int j = 0; j < optionList.size(); j++) {

            // Create
            final Button optionButton = new Button(getContext());

            // Text
            optionButton.setText(optionList.get(j));
            optionButton.setTextSize(12);

            // Style
            optionButton.setPadding(0, 0, 0, 0);
            optionButton.setBackgroundColor(Color.TRANSPARENT);
            optionButton.setTextColor(Color.LTGRAY);

            // Style (LayoutParams)
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80);
            params.setMargins(0, 0, 0, 0);
            optionButton.setLayoutParams(params);

            // Add to view (column)
            columnView.addView(optionButton);

            // Add to button choose
            optionButtonList.add(optionButton);

            final ContentEntry.OnContentChangeListener contentListener = new ContentEntry.OnContentChangeListener() {
                @Override
                public void notifyContentChanged() {

                    // contentEntry2.removeOnContentChangeListener(this);

                    Log.v("Compare", "\t\t\tnotifyContentChanged: " + key);

                    Log.v("Content_Tree", "\t\t\tnotifyContentChanged");
                    Log.v("Content_Tree_Notify", "\t\t\tnotifyContentChanged");

                    // Reset.
                    for (int k = 0; k < optionButtonList.size(); k++) {
                        optionButtonList.get(k).setTextColor(Color.LTGRAY);
                        optionButtonList.get(k).setTypeface(null, Typeface.NORMAL);
                    }

                    // Select.
                    Log.v("Compare", "...");
                    for (Button optionButton : optionButtonList) {
                        //if (optionButton.getText().toString().equals(contentEntry.getContent())) {
                        Log.v("Compare", "choice.key: " + contentEntry.choice().get(key).getContent());
                        if (optionButton.getText().toString().equals(contentEntry.choice().get(key).getContent())) {
                            selectedOptionButton[0] = optionButton; // Button. Select the button.
                            break;
                        }
                    }

                    // Color.
                    if (selectedOptionButton[0] != null) {
                        int textColor = ApplicationView.getApplicationView().getResources().getColor(R.color.timeline_segment_color);
                        selectedOptionButton[0].setTextColor(textColor); // Color. Update the color.
                        selectedOptionButton[0].setTypeface(null, Typeface.BOLD);
                    }

                    // Data.
                    if (selectedOptionButton[0] != null) {
                        contentEntry.choice().get(key).set(selectedOptionButton[0].getText().toString(), false);
                    }
                }
            };

            // Setup: Set up interactivity.
            optionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // List for changes to data state...
                    contentEntry.addOnContentChangeListener(contentListener);

                    // Data.
                    contentEntry.choice().get(key).set(optionButton.getText().toString());

                }
            });
        }

        // Style (LayoutParams)
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
        // params.setMargins(0, 0, 0, 0);
        columnView.setLayoutParams(params);

        return columnView;
    }

    public View generateContentInputView(final ContentEntry contentEntry) {

        Log.v ("floo", "" + contentEntry);

        // Choices.
        final ArrayList<Button> optionButtonList = new ArrayList<Button>();
        final Button[] selectedOptionButton = { null };

        // Layout.
        final LinearLayout columnView = new LinearLayout (getContext());
        columnView.setOrientation(LinearLayout.VERTICAL);
        columnView.setVerticalGravity(Gravity.CENTER_VERTICAL);

//        TODO: contentEntry.addOnContentChangeListener(/* code to update the graphical state of the column to reflect the ContentEntry */);
//        TODO: eventually, only call contentChangeListeners for entries that have constraints (i.e., that potentially require updates)

        //ArrayList<String> optionList = contentEntry.get(key).getContentRange();
//        ArrayList<String> optionList = contentEntry.getKeys();
//
//        for (int i = 0; i < optionList.size(); i++) {

            // Create
            final Button optionButton = new Button(getContext());

            // Text
//            optionButton.setText(optionList.get(i));
            optionButton.setText(contentEntry.getKey());
            optionButton.setTextSize(12);

            // Style
            optionButton.setPadding(0, 0, 0, 0);
            optionButton.setBackgroundColor(Color.TRANSPARENT);
            optionButton.setTextColor(Color.LTGRAY);

            // Style (LayoutParams)
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 80);
            params.setMargins(0, 0, 0, 0);
            optionButton.setLayoutParams(params);

            // Add to view (column)
            columnView.addView(optionButton);

            // Add to button choose
            optionButtonList.add(optionButton);

            final ContentEntry.OnContentChangeListener contentListener = new ContentEntry.OnContentChangeListener() {
                @Override
                public void notifyContentChanged() {

                    Log.v("Content_Tree", "\t\t\tnotifyContentChanged");
                    Log.v("Content_Tree_Notify", "\t\t\tnotifyContentChanged");

//                    // Reset.
//                    for (int k = 0; k < optionButtonList.size(); k++) {
//                        optionButtonList.get(k).setTextColor(Color.LTGRAY);
//                        optionButtonList.get(k).setTypeface(null, Typeface.NORMAL);
//                    }
//
//                    // Select.
//                    Log.v("Compare", "...");
//                    for (Button optionButton : optionButtonList) {
//                        //if (optionButton.getText().toString().equals(contentEntry.getContent())) {
////                        Log.v("Compare", "choice.key: " + contentEntry.choice().get(key).getContent());
//                        if (optionButton.getText().toString().equals(contentEntry.choice().get(key).getContent())) {
//                            selectedOptionButton[0] = optionButton; // Button. Select the button.
//                            break;
//                        }
//                    }
//
//                    // Color.
//                    if (selectedOptionButton[0] != null) {
//                        int textColor = ApplicationView.getApplicationView().getResources().getColor(R.color.timeline_segment_color);
//                        selectedOptionButton[0].setTextColor(textColor); // Color. Update the color.
//                        selectedOptionButton[0].setTypeface(null, Typeface.BOLD);
//                    }

                    // Data.
//                    if (selectedOptionButton[0] != null) {
                        //contentEntry.choice().get(key).set(selectedOptionButton[0].getText().toString(), false);
                        contentEntry.set(optionButton.getText().toString(), false);
//                    }
                }
            };

        // Setup: Set up interactivity.
        optionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // List for changes to data state...
                contentEntry.addOnContentChangeListener(contentListener);

                // Data.
                contentEntry.set(optionButton.getText().toString());

            }
        });
//        }

        // Style (LayoutParams)
        params = new LinearLayout.LayoutParams(300, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
        // params.setMargins(0, 0, 0, 0);
        columnView.setLayoutParams(params);

        return columnView;
    }

    public void displayUpdateIOOptions2 (final EventHolder eventHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle ("Change the channel.");
        builder.setMessage("What do you want to do?");

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
            channelEnableToggleButtons.add(toggleButton); // Add the button to the choose.
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
            channelDirectionButtons.add(toggleButton); // Add the button to the choose.
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
            channelModeButtons.add(toggleButton); // Add the button to the choose.
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
            channelValueToggleButtons.add(toggleButton); // Add the button to the choose.
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

        // Get choose of devices that have been discovered
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
                    // Get choose of all discovered devices on the mesh network
                    messageDestinationData.clear();
                    // TODO: Get choose of all discovered smartphones, tablets, and other devices for interacting with Clay
                    messageDestinationDataAdapter.notifyDataSetChanged();
                } else if (selectedItemText.equals("UDP") || selectedItemText.equals("TCP")) {
                    messageDestinationTitle.setVisibility(View.VISIBLE);
                    messageDestinationSelector.setVisibility(View.VISIBLE);
                    // Get choose of all discovered devices on the mesh network
                    messageDestinationData.clear();
                    // TODO: Get choose of all discovered smartphones, tablets, and other devices for interacting with Clay
                    messageDestinationData.add(getClay().getInternetBroadcastAddress() + ":4445"); // Broadcast address
                    messageDestinationData.add(getClay().getInternetAddress() + ":4445"); // This device's address
                    messageDestinationData.add("Other");
                    messageDestinationDataAdapter.notifyDataSetChanged();
                } else if (selectedItemText.equals("Mesh")) {
                    messageDestinationTitle.setVisibility(View.VISIBLE);
                    messageDestinationSelector.setVisibility(View.VISIBLE);
                    // Get choose of all discovered devices on the mesh network
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
