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

    public Context getContext() {
        return ApplicationView.getApplicationView();
    }

    public Clay getClay() {
        return getDevice().getClay();
    }

    public Device getDevice() {
        return this.device;
    }

    public void displayUpdateLightsOptions(final EventHolder eventHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        // Title
        builder.setTitle("light");

        // Layout
        LinearLayout designerViewLayout = new LinearLayout(getContext());
        designerViewLayout.setOrientation(LinearLayout.VERTICAL);

        // Enable or disable lights
        // final TextView lightLabel = new TextView (getContext());
        // lightLabel.setText("Choose some lights");
        // lightLabel.setPadding(70, 20, 70, 20);
        // designerViewLayout.addView(lightLabel);

        final ColorPickerDialog colorPickerDialog = ColorPickerDialog.createColorPickerDialog(getContext(), ColorPickerDialog.DARK_THEME);

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
        final TextView lightColorLabel = new TextView(getContext());
        lightColorLabel.setText("Choose colors");
        lightColorLabel.setPadding(70, 20, 70, 20);
        designerViewLayout.addView(lightColorLabel);

        LinearLayout lightColorLayout = new LinearLayout(getContext());
        lightColorLayout.setOrientation(LinearLayout.HORIZONTAL);
        final ArrayList<Button> lightColorButtons = new ArrayList<Button>();
        final ArrayList<Integer> lightColors = new ArrayList<Integer>();
        final ArrayList<String> lightColorHexStrings = new ArrayList<String>();
        for (int i = 0; i < 12; i++) {
            final String channelLabel = Integer.toString(i + 1);
            final Button colorButton = new Button(getContext());
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
        Log.v("Light_State", stateString);
        final String[] splitStateString = stateString.split(" ");

        // Set button background to current color state
        lightColors.clear();
        lightColorHexStrings.clear();
        for (int i = 0; i < lightColorButtons.size(); i++) {
            Button lightColorButton = lightColorButtons.get(i);

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
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String updatedStateString = "";
                Byte[] colorBytesString = new Byte[12 * 3]; // i.e., 12 lights, each with 3 color bytes
                for (int i = 0; i < 12; i++) {

                    /*
                    final ToggleButton lightEnableButton = lightToggleButtons.get (i);
                    */
                    final Button lightColorButton = lightColorButtons.get(i);

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
                        updatedStateString = updatedStateString.concat(" ");
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
                eventHolder.updateState(updatedStateString);

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
                Log.v("Color", content);
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
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    public void displayEventTriggerOptions(final EventHolder eventHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("trigger (message)");

        // Declare transformation layout
        LinearLayout designerViewLayout = new LinearLayout(getContext());
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

        builder.show();
    }

    public void displayUpdateSignalOptions(final ContentEntry contentEntry) {

        // <SETTINGS>
        boolean showConstant = true;
        boolean showDataSources = true;
        // </SETTINGS>

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

        // Layout
        final LinearLayout designerViewLayout = new LinearLayout(getContext());
        designerViewLayout.setOrientation(LinearLayout.VERTICAL);

        // Layout Style (LayoutParams)
        LinearLayout.LayoutParams params5 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params5.setMargins(0, 0, 0, 0);
        designerViewLayout.setLayoutParams(params5);

        // Layouts
        LinearLayout channelChooserView = new LinearLayout(getContext());
        final LinearLayout channelDirectionSelectionButtonsLayout = new LinearLayout(getContext());
        channelDirectionSelectionButtonsLayout.setId(R.id.channel_direction);
        final LinearLayout channelTypeSelectionButtonsLayout = new LinearLayout(getContext());

        // Views
        final Spinner contentTypeSelector = new Spinner(getContext());
        final EditText numberEntryView = new EditText(getContext());
        final TextView contentProviderTitle = new TextView(getContext());
        final LinearLayout channelSelectionButtonsLayout = new LinearLayout(getContext());
        final TextView contentSelectionLabel = new TextView(getContext());
//        final ListView contentProviderListView = new ListView(getContext());

        // <TITLE>
        LinearLayout row = (LinearLayout) getRowView();
        designerViewLayout.addView(row);

        final TextView channelEnabledLabel = getTitleView(contentEntry.getKey());
        row.addView (channelEnabledLabel);
        // </TITLE>

        // <CHANNEL CHOOSER>
        channelChooserView = (LinearLayout) generateChannelChooserView(contentEntry);
        designerViewLayout.addView(channelChooserView);
        // </CHANNEL CHOOSER>

        // <CHANNEL CONFIGURATION>
        // Single channel (chosen above) controller
        LinearLayout channelConfigurationView = (LinearLayout) generateChannelConfigurationView(contentEntry, true, "direction", "type");
        designerViewLayout.addView(channelConfigurationView);
        // </CHANNEL CONFIGURATION>

        // <CHANNEL CONTENT>
//        // The channel's content store
//        final LinearLayout baseView = new LinearLayout(getContext());
//        baseView.setOrientation(LinearLayout.HORIZONTAL);
//        baseView.setVerticalGravity(Gravity.CENTER_HORIZONTAL);
//
//        for (ContentEntry value : contentEntry.choice().get("content").getChildren()) {
//            // TODO: final ArrayList<Button> optionButtonList = new ArrayList<Button>(); ...
//            // TODO: ...add callback to update the selected content key (in the object)
//            LinearLayout exposedChannelView = (LinearLayout) generateContentInputView(value);
//            baseView.addView(exposedChannelView);
//        }
//
//        designerViewLayout.addView(baseView);

        LinearLayout channelContentView = (LinearLayout) generateChannelContentChooserView (contentEntry, null);
        designerViewLayout.addView(channelContentView);

//        LinearLayout contentEditorView = (LinearLayout) generateChannelContentEditorView (contentEntry);
//        designerViewLayout.addView(contentEditorView);

        // </CHANNEL CONTENT>


        // TODO: contentEntry.addOnContentChangeListener (for channel, update data view based on content + selected content key)...
        // TODO: ...add the following into: generateDataEntryView() and place in callback for selected content key...
        // TODO:    ...and update the state of the data view on every call, for the selected combo...

//        // "Enter data..."
//        final TextView dataEntryLabel = new TextView(getContext());
//        dataEntryLabel.setText("Put the data here.");
////        channelEnabledLabel.setTextSize(20);
////        channelEnabledLabel.setPadding(70, 20, 70, 20);
//        dataEntryLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//        designerViewLayout.addView(dataEntryLabel);

        // data editor was here...

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

                Log.v("Content_Editor", "channel." + number + ".content.type: " + contentType);
                Log.v("Content_Editor", "channel." + number + ".content.provider: " + contentProvider);
                Log.v("Content_Editor", "channel." + number + ".content.source: " + contentSource);

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

        builder.show();
    }

    private View generateChannelContentEditorView(final ContentEntry contentEntry) {

        LinearLayout baseLayout = new LinearLayout(getContext());
        baseLayout.setOrientation(LinearLayout.VERTICAL);

        final ListView contentProviderListView = new ListView(getContext());
        final Spinner contentTypeSelector = new Spinner(getContext());
        final EditText numberEntryView = new EditText(getContext());
        final TextView contentProviderTitle = new TextView(getContext());
        final LinearLayout channelSelectionButtonsLayout = new LinearLayout(getContext());
        final TextView contentSelectionLabel = new TextView(getContext());

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
                    ((TextView) parent.getChildAt(i)).setTextColor(Color.LTGRAY);
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
        baseLayout.addView(contentTypeSelector);
        // </SWITCH-TITLE: CONTENT TYPE SELECTOR>

        // <SECTION: CONTENT INPUT TITLE>
        // Number input
        // final EditText numberEntryView = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        numberEntryView.setInputType(InputType.TYPE_CLASS_TEXT);//input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        baseLayout.addView(numberEntryView);
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
        baseLayout.addView(contentProviderTitle);
        // </TITLE: CONTENT PROVIDER TITLE>

        // <SECTION: CONTENT PROVIDER SELECTION BUTTONS>
        final ArrayList<Button> channelSelectionButtonList = new ArrayList<Button>();
        final Button[] selectedButton = {null};
        // final LinearLayout channelSelectionButtonsLayout = new LinearLayout (getContext());
        channelSelectionButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < 12; i++) {

            // Create
            final Button channelNumberButton = new Button(getContext());

            // Text
            final String channelNumberString = Integer.toString(i + 1);
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

            final Button channelSelectionButton = channelSelectionButtonList.get(i);

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
                    contentEntry.choice().get("content").get(selectedObservable).put("provider", selectedButton[0].getText().toString());
                    Log.v("Content5", "choice: " + contentEntry.choice().getKey());
                    Log.v ("Content5", "" + selectedObservable + ": " + contentEntry.choice().get("content").get(selectedObservable).getContent());
                }
            });
        }


        baseLayout.addView(channelSelectionButtonsLayout);
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
        baseLayout.addView(contentSelectionLabel);
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
                contentEntry.choice().get("content").get(selectedObservable).put("source", textView.getText().toString());

                // TODO: if (enableMultipleContentProviders) {
            }
        });

        baseLayout.addView(contentProviderListView);

        return baseLayout;
        // </SECTION: CONTENT SOURCE>
    }

    private TextView getTitleView(String title) {
        return getTextView (title, 20);
    }

    private TextView getTextView(String text, int textSize) {
        final TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setTextSize(textSize);
        textView.setPadding(70, 20, 70, 20);
        textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        return textView;
    }

    private View getRowView() {
        LinearLayout rowLayout = new LinearLayout(getContext());
        rowLayout.setOrientation(LinearLayout.HORIZONTAL);

        // Style (LayoutParams)
        LinearLayout.LayoutParams params6 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params6.setMargins(0, 0, 0, 0);
        rowLayout.setLayoutParams(params6);

        return rowLayout;
    }

    private View generateChannelChooserView(final ContentEntry contentEntry) {

        // Choices.
        final ArrayList<Button> optionButtonList = new ArrayList<Button>();
        final Button[] selectedOptionButton = {null};

        // Layout.
        final LinearLayout baseView = new LinearLayout(getContext());
        baseView.setOrientation(LinearLayout.HORIZONTAL);
        baseView.setVerticalGravity(Gravity.CENTER_HORIZONTAL);

//        TODO: contentEntry.addOnContentChangeListener(/* code to update the graphical state of the column to reflect the ContentEntry */);
//        TODO: eventually, only call contentChangeListeners for entries that have constraints (i.e., that potentially require updates)

        for (int i = 0; i < 12; i++) {

            // Create
            final Button channelNumberButton = new Button(getContext());

            // Text
            final String channelNumberString = Integer.toString(i + 1);
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

            final Button optionButton = optionButtonList.get(i);
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
                    contentEntry.set(optionButton.getText().toString());
                }
            });
        }

        return baseView;
    }

    private View generateChannelConfigurationView(ContentEntry contentEntry, boolean horizontal, String... keys) {

        final LinearLayout baseView = new LinearLayout(getContext());

        if (horizontal) {
            baseView.setOrientation(LinearLayout.HORIZONTAL);
        } else {
            baseView.setOrientation(LinearLayout.VERTICAL);
        }

        // Add to view (row)
        for (int i = 0; i < keys.length; i++) {
            LinearLayout columnView = columnView = (LinearLayout) generateVerticalChooserView(contentEntry, keys[i]);
            baseView.addView(columnView);
        }

        // Style (LayoutParams)
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        // params.setMargins(0, 0, 0, 0);
        baseView.setLayoutParams(params);

        return baseView;
    }

    String selectedObservable = null;

    // TODO: public View generateVerticalChooserView (ContentEntry contentEntry, OnContentChangeListener onContentChangeListener) {
    public View generateChannelContentChooserView (final ContentEntry contentEntry, final String key2) {

        final LinearLayout baseLayout = new LinearLayout(getContext());
        baseLayout.setOrientation(LinearLayout.VERTICAL);

        // Channel content selector
        LinearLayout channelContentView = (LinearLayout) generateChannelContentChooserView2 (contentEntry, null);
        baseLayout.addView(channelContentView);

        // Content editor
        LinearLayout contentEditorView = (LinearLayout) generateChannelContentEditorView(contentEntry);
        contentEditorView.setTag("content_editor_view");
        baseLayout.addView(contentEditorView);

        return baseLayout;

    }
    public View generateChannelContentChooserView2 (final ContentEntry contentEntry, final String key2) {

        final LinearLayout baseLayout = new LinearLayout(getContext());
        baseLayout.setOrientation(LinearLayout.VERTICAL);

        // Choices.
        final ArrayList<Button> optionButtonList = new ArrayList<Button>();
        final Button[] selectedOptionButton = { null };

        // Layout.
        final LinearLayout buttonListView = new LinearLayout(getContext());
        buttonListView.setOrientation(LinearLayout.HORIZONTAL);
        buttonListView.setVerticalGravity(Gravity.CENTER_HORIZONTAL);

        // TODO: Put rendering code in here. Just make the layout scaffolding outside of this...
//        contentEntry.addOnContentChangeListener(new ContentEntry.OnContentChangeListener() {
//            @Override
//            public void notifyContentChanged() {
//
//                // Get content for chosen channel
//                ArrayList<ContentEntry> children = contentEntry.choice().get("content").getChildren();
//            }
//        });

//        TODO: contentEntry.addOnContentChangeListener(/* code to update the graphical state of the column to reflect the ContentEntry */);
//        TODO: eventually, only call contentChangeListeners for entries that have constraints (i.e., that potentially require updates)

        //ArrayList<String> optionList = contentEntry.choice().get(key).getContentRange();
        ArrayList<String> optionList = contentEntry.choice().get("content").getKeys();

        contentEntry.addOnContentChangeListener(new ContentEntry.OnContentChangeListener() {
            @Override
            public void notifyContentChanged() {

                // Reset.
                for (int k = 0; k < optionButtonList.size(); k++) {
                    optionButtonList.get(k).setTextColor(Color.LTGRAY);
                    optionButtonList.get(k).setTypeface(null, Typeface.NORMAL);
                }

//                baseLayout.findViewWithTag("content_editor_view").setVisibility(View.GONE);

            }
        });

        for (int i = 0; i < optionList.size(); i++) {

            final String key = optionList.get(i);

            Log.v ("Hello", "\t" + optionList.get(i));

            // Create
            final Button optionButton = new Button(getContext());

            // Text
            optionButton.setText(optionList.get(i));
            optionButton.setTextSize(12);

            // Style
            optionButton.setPadding(0, 0, 0, 0);
            optionButton.setBackgroundColor(Color.TRANSPARENT);
            optionButton.setTextColor(Color.LTGRAY);

            // Style (LayoutParams)
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 80);
            params.setMargins(0, 0, 0, 0);
            optionButton.setLayoutParams(params);

            // Add to view (column)
            buttonListView.addView(optionButton);

            // Add to button choose
            optionButtonList.add(optionButton);

            final ContentEntry.OnContentChangeListener contentListener = new ContentEntry.OnContentChangeListener() {
                @Override
                public void notifyContentChanged() {

                    // contentEntry2.removeOnContentChangeListener(this);

//                    Log.v("Compare", "\t\t\tnotifyContentChanged: " + key);

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
//                        Log.v("Compare", "choice.key: " + contentEntry.choice().get(key).getContent());
                        if (optionButton.getText().toString().equals(contentEntry.choice().get("content").get(key).getKey())) {
                            selectedOptionButton[0] = optionButton; // Button. Select the button.

                            // <HACK>
                            selectedObservable = optionButton.getText().toString();
                            // </HACK>
                            break;
                        }
                    }

                    // Color.
                    if (selectedOptionButton[0] != null) {

//                        baseLayout.findViewWithTag("content_editor_view").setVisibility(View.VISIBLE);

                        int textColor = ApplicationView.getApplicationView().getResources().getColor(R.color.timeline_segment_color);
                        selectedOptionButton[0].setTextColor(textColor); // Color. Update the color.
                        selectedOptionButton[0].setTypeface(null, Typeface.BOLD);
                    }

//                    // Data.
//                    if (selectedOptionButton[0] != null) {
//                        contentEntry.choice().get(key).set(selectedOptionButton[0].getText().toString(), false);
//                    }
                }
            };

            // Setup: Set up interactivity.
            optionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // List for changes to data state...
//                    contentEntry.addOnContentChangeListener(contentListener);
                    //contentEntry.choice().get("content").get(key).addOnContentChangeListener(contentListener);

                    contentListener.notifyContentChanged();

                    // Data.
                    // Note: Don't update the content here, since this represents a choice of which
                    // observable to edit, not an actual change to its content.
//                    contentEntry.choice().get("content").get(key).set(optionButton.getText().toString());

                }
            });
        }

        // Style (LayoutParams)
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // params.setMargins(0, 0, 0, 0);
        buttonListView.setLayoutParams(params);

        baseLayout.addView(buttonListView);


        return baseLayout;
    }

    private View generateChoiceView() {

        final LinearLayout baseView = new LinearLayout(getContext());

        // <SECTION: CHANNEL SELECTION BUTTONS>
        final ArrayList<String> optionList = new ArrayList<String>();
        optionList.add("pulse_period_seconds");
        optionList.add("pulse_duty_cycle");
        final ArrayList<Button> optionButtonList = new ArrayList<Button>();
        final Button[] selectedOptionButton = {null};
        // final LinearLayout channelSelectionButtonsLayout = new LinearLayout (getContext());
        baseView.setOrientation(LinearLayout.HORIZONTAL);
        for (int i = 0; i < optionList.size(); i++) {

            // Create
            final Button channelNumberButton = new Button(getContext());

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

            final Button channelSelectionButton = optionButtonList.get(i);

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
        final Button[] selectedOptionButton = {null};

        // Layout.
        final LinearLayout columnView = new LinearLayout(getContext());
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

        Log.v("floo", "" + contentEntry);

        // Choices.
        final ArrayList<Button> optionButtonList = new ArrayList<Button>();
        final Button[] selectedOptionButton = {null};

        // Layout.
        final LinearLayout columnView = new LinearLayout(getContext());
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

    /**
     * Update's the tag (or label) of a timeline view.
     *
     * @param eventHolder
     */
    public void displayUpdateTagOptions(final EventHolder eventHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Tag the view.");

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

        builder.show();
    }

    public void displayUpdateMessageOptions(final EventHolder eventHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        /* Pop-up tag */

        // builder.setTitle ("Message");

        // Declare transformation layout
        LinearLayout designerViewLayout = new LinearLayout(getContext());
        designerViewLayout.setOrientation(LinearLayout.VERTICAL);

        /* Message content */

        // Title
        final TextView messageContentTitle = new TextView(getContext());
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
        final TextView messageTypeTitle = new TextView(getContext());
        messageTypeTitle.setText("Type");
        messageTypeTitle.setPadding(70, 20, 70, 20);
        designerViewLayout.addView(messageTypeTitle);

        // List of types (i.e., TCP, UDP, Mesh, etc.)
        final Spinner messageTypeSelector = new Spinner(getContext());
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
        final TextView messageDestinationTitle = new TextView(getContext());
        messageDestinationTitle.setText("Destination");
        messageDestinationTitle.setPadding(70, 20, 70, 20);
        designerViewLayout.addView(messageDestinationTitle);

        // Set destination of message
        final Spinner messageDestinationSelector = new Spinner(getContext());
        final ArrayAdapter<String> messageDestinationDataAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, messageDestinationData); //selected item will look like a spinner set from XML
        messageDestinationDataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        messageDestinationSelector.setAdapter(messageDestinationDataAdapter);
        designerViewLayout.addView(messageDestinationSelector);

        // "Other" destination address, specified by user
        final TextView messageCustomDestinationTitle = new TextView(getContext());
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
        String currentContentString = currentStateString.substring(currentContentStringIndex + 1);
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
                getClay().getStore().storeEvent(eventHolder.getEvent());

                // Send updated state to device
                // <HACK>
                String stateToSend = stateString.substring(0, stateString.indexOf(" ")).toLowerCase() + " " + stateString.substring(stateString.indexOf(" ") + 1);
                String content = "set event " + eventHolder.getEvent().getUuid() + " state \"" + stateToSend + "\"";
//                getDevice().sendMessage(content);
                // </HACK>

                Log.v("Event_Trigger", "state: " + stateToSend);
                Log.v("Event_Trigger", "update: " + content);

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

        builder.show();
    }

    public void displayUpdateToneOptions(final EventHolder eventHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Tone");
        builder.setMessage("Choose frequency and duration.");

        // Declare transformation layout
        LinearLayout designerViewLayout = new LinearLayout(getContext());
        designerViewLayout.setOrientation(LinearLayout.VERTICAL);

        // Set up the frequency
        final TextView frequencyLabel = new TextView(getContext());
        frequencyLabel.setPadding(70, 20, 70, 20);
        designerViewLayout.addView(frequencyLabel);

        final SeekBar frequencyVal = new SeekBar(getContext());
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
                                ApplicationView.getApplicationView().playTone(Double.parseDouble(String.valueOf(progress)), 0.2);
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
        final TextView durationLabel = new TextView(getContext());
        durationLabel.setPadding(70, 20, 70, 20);
        designerViewLayout.addView(durationLabel);

        final SeekBar durationVal = new SeekBar(getContext());
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

        builder.show();
    }

    public void displayUpdateSayOptions(final EventHolder eventHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("tell me the behavior");

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

        builder.show();
    }

    public void displayUpdateWaitOptions(final EventHolder eventHolder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Time Transform");
        builder.setMessage("How do you want to change time?");

        // Declare transformation layout
        LinearLayout designerViewLayout = new LinearLayout(getContext());
        designerViewLayout.setOrientation(LinearLayout.VERTICAL);

        // Set up the label
        final TextView waitLabel = new TextView(getContext());
        waitLabel.setPadding(70, 20, 70, 20);
        designerViewLayout.addView(waitLabel);

        final SeekBar waitVal = new SeekBar(getContext());
        waitVal.setMax(1000);
        waitVal.setHapticFeedbackEnabled(true); // TODO: Emulate this in the custom interface

        // Get the behavior state
        int time = Integer.parseInt(eventHolder.getEvent().getState().get(0).getState());

        // Update the view
        waitLabel.setText("Wait (" + time + " ms)");
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
        builder.setPositiveButton("DONE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

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
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
}
