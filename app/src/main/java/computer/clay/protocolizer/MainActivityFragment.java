package computer.clay.protocolizer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Random;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayList<String> behaviorSequence = new ArrayList<String>();

//    private Communication communication = null;
    ArrayAdapter<String> listAdapter;

    public MainActivityFragment() {
    }

    public MainActivity getApplication () {
        MainActivity mainActivity = (MainActivity) getActivity();
        return mainActivity;
    }

    @Override
    public void onPause() {
        super.onPause();
//        if (((MainActivity) this.getActivity()).getClay ().getCommunication() != null) {
//            ((MainActivity) this.getActivity()).getClay().getCommunication().stopDatagramServer();
//        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (((MainActivity) activity).getClay ().getCommunication() == null) {
//            communication = new Communication ();
//            communication.startDatagramServer ();
            (((MainActivity) activity)).getClay ().getCommunication().startDatagramServer ();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (communication == null) {
//            communication = new Communication();
//            communication.startDatagramServer();
//        (((MainActivity) activity).getClay ().getCommunication().startDatagramServer ();
//        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void refreshTimeline () {
        // <HACK>
        // Add the current behavior's constructs to the current perspective.
        behaviorSequence.clear();
        behaviorSequence.add("abstract");
        for (int i = 0; i < getApplication().getClay().getPerspective().getBehaviorConstructs().size(); i++) {
            BehaviorConstruct behaviorConstruct = getApplication().getClay().getPerspective().getBehaviorConstructs().get (i);

            String title = behaviorConstruct.getBehavior ().getTitle ();
            behaviorSequence.add (title);
        }
        behaviorSequence.add("create");
        // </HACK>

        listAdapter.notifyDataSetChanged();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);


        if (((MainActivity) this.getActivity()).getClay ().getCommunication() == null) {
            Log.e ("Clay", "Communication subsystem does not exist in memory.");
        }

        // Sequence abstraction behaviors.
        behaviorSequence.add("abstract");

        // Add the current behavior's constructs to the current perspective.
        for (int i = 0; i < getApplication().getClay().getPerspective().getBehaviorConstructs().size(); i++) {
            BehaviorConstruct behaviorConstruct = getApplication().getClay().getPerspective().getBehaviorConstructs().get (i);

            String title = behaviorConstruct.getBehavior().getTitle();
            behaviorSequence.add (title);
        }

        // Sequence behaviors.
        behaviorSequence.add("create");

        // Define the adapter (adapts the data to the actual rendered view)
        listAdapter = new ArrayAdapter<String>( // ArrayAdapter<String> mForecastAdapter = new ArrayAdapter<String>(
                getActivity(), // The current context (this fragment's parent activity).
                R.layout.list_item_http_request, // ID of list item layout
                R.id.list_item_http_request_textview, // ID of textview to populate (using the specified list item layout)
                behaviorSequence // The list of forecast data
        );

//        communication.listAdapter = listAdapter; // TODO: (HACK) This shouldn't be necessary or should be elsewhere!

        // Define the view (get a reference to it and pass it an adapter)
        ListView listView = (ListView) rootView.findViewById(R.id.listview_http_requests);
        listView.setAdapter (listAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                String touchedEntryText = listAdapter.getItem (position);

                if (touchedEntryText.equals("create")) {

                    // Add a new behavior construct to the looping sequence.
//                    behaviorSequence.add(1, "<select>");
//                    listAdapter.notifyDataSetChanged();

                    // TODO: Get Clay
                    MainActivity application = getApplication();
                    Log.v("Clay_Construct", "application: " + application);
                    Clay clay = application.getClay();
                    Log.v ("Clay_Construct", "clay: " + clay);
                    // TODO: Get Perspective
                    Perspective perspective = clay.getPerspective();
                    Log.v("Clay_Construct", "perspective: " + perspective);
//                    BehaviorConstruct behaviorConstruct = new BehaviorConstruct (perspective, 0, 0);
//                    perspective.addBehaviorConstruct(behaviorConstruct);
                    // <HACK>
                    Hack_PromptForBehaviorSelection(perspective);
                    //((AppActivity) getClay ().getPlatformContext()).Hack_PromptForBehaviorSelection(behaviorConstruct);
//                    behaviorSequence.set (position, "Set");
//                    listAdapter.notifyDataSetChanged();

                } else if (touchedEntryText.equals("abstract")) {

                    // TODO: Combine the list of behaviors into a single behavior by adding them to a behavior's loop.

                    // TODO: Display the name of the complex behavior as a single item in the list.
                    // TODO: Behavior newComplexBehavior = new Behavior ("complex");

                    // Create new loop of behaviors
                    Loop loop = new Loop ();
                    for (int i = 0; i < getApplication().getClay().getPerspective().getBehaviorConstructs().size(); i++) {
                        BehaviorConstruct behaviorConstruct = getApplication().getClay().getPerspective().getBehaviorConstructs().get (i);
                        Behavior behavior = behaviorConstruct.getBehavior ();
                        loop.addBehavior (behavior);

                        // Get the selected behavior construct
                        String title = "" + behavior.getTitle();
                        Toast toast = Toast.makeText(getActivity(), title, Toast.LENGTH_SHORT); //Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }

                    // Create new behavior construct to encapsulate the loop (as a complex behavior)

                    refreshTimeline();
//                    listAdapter.notifyDataSetChanged();

                } else if (touchedEntryText.equals ("<select>")) {

//                    // TODO: Get Clay
//                    MainActivity application = getApplication();
//                    Log.v("Clay_Construct", "application: " + application);
//                    Clay clay = application.getClay();
//                    Log.v ("Clay_Construct", "clay: " + clay);
//                    // TODO: Get Perspective
//                    Perspective perspective = clay.getPerspective();
//                    Log.v("Clay_Construct", "perspective: " + perspective);
//                    BehaviorConstruct behaviorConstruct = new BehaviorConstruct (perspective, 0, 0);
//                    perspective.addBehaviorConstruct(behaviorConstruct);
//                    // <HACK>
//                    getApplication().Hack_PromptForBehaviorSelection(behaviorConstruct);
//                    //((AppActivity) getClay ().getPlatformContext()).Hack_PromptForBehaviorSelection(behaviorConstruct);
////                    behaviorSequence.set (position, "Set");
//                    listAdapter.notifyDataSetChanged();

                }

                /*
                // NOTE: This can be deleted. It's been left here for reference.
                HttpRequestTask httpRequestTask = new HttpRequestTask();
                httpRequestTask.execute(httpRequestAdapter.getItem(position)); // httpRequestTask.execute("94110");
                */
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {

                /*
//                Context context = view.getContext();
                String httpRequestText = listAdapter.getItem(position); //CharSequence text = "Hello toast!";
//                int duration = Toast.LENGTH_SHORT;
                Toast toast = Toast.makeText(getActivity(), httpRequestText, Toast.LENGTH_SHORT); //Toast toast = Toast.makeText(context, text, duration);
                toast.show();
                */

                /*
                HttpRequestTask httpRequestTask = new HttpRequestTask();
//                httpRequestTask.execute("94110");
                httpRequestTask.execute (listAdapter.getItem (position));
                */

                // Get the selected behavior construct
                BehaviorConstruct behaviorConstruct = getApplication().getClay().getPerspective().getBehaviorConstructs().get(position - 1);
                String title = behaviorConstruct.getBehavior().getTitle();
                Toast toast = Toast.makeText(getActivity(), title, Toast.LENGTH_SHORT); //Toast toast = Toast.makeText(context, text, duration);
                toast.show();

                getApplication().getClay().getPerspective().getBehaviorConstructs().remove (position - 1);
//                behaviorSequence.remove (position);

                /*
                Intent settingsIntent = new Intent(getActivity(), HttpRequestActivity.class);
                startActivity(settingsIntent);
                */

                return false;
            }
        });

        // Disable the scrollbars.
        listView.setScrollbarFadingEnabled(false);
        listView.setVerticalScrollBarEnabled(false);
        listView.setHorizontalScrollBarEnabled(false);

        // Disable overscroll effect.
        listView.setOverScrollMode(View.OVER_SCROLL_NEVER);

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // TODO: Handle the selected options item.

        return super.onOptionsItemSelected(item);
    }

    String Hack_BehaviorTransformTitle = "";
    public void Hack_PromptForBehaviorTransform (final Perspective perspective) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle ("Change the channel.");
        builder.setMessage ("What do you want to do?");

        // TODO: Populate with the current transform values (if any).
//        if (behaviorConstruct.getBehavior().getTransform() != null) {
//            Log.v("Behavior_Transform", behaviorConstruct.getBehavior().getTransform());
//            // TODO: Store the previous values so they can be used to initialize the interface.
//        }

        // TODO: Specify the units to receive the change.

        // Declare transformation layout
        LinearLayout transformLayout = new LinearLayout (getActivity());
        transformLayout.setOrientation (LinearLayout.VERTICAL);

        /*
        // Condition

        // Set up the condition label
        final TextView conditionLabel = new TextView (this);
        conditionLabel.setText("Condition");
        conditionLabel.setPadding(70, 20, 70, 20);
        transformLayout.addView(conditionLabel);
        */

        // TODO: None, Switch, Threshold, Gesture, Message, Data

        // LEDs:

        // Set up the LED label
        final TextView lightLabel = new TextView (getActivity());
        lightLabel.setText("Enable LED feedback");
        lightLabel.setPadding(70, 20, 70, 20);
        transformLayout.addView (lightLabel);

        LinearLayout lightLayout = new LinearLayout (getActivity());
        lightLayout.setOrientation (LinearLayout.HORIZONTAL);
//        channelLayout.setLayoutParams (new LinearLayout.LayoutParams (MATCH_PARENT));
        final ArrayList<ToggleButton> lightToggleButtons = new ArrayList<> ();
        for (int i = 0; i < 12; i++) {
            final String channelLabel = Integer.toString (i + 1);
            final ToggleButton toggleButton = new ToggleButton (getActivity());
            toggleButton.setPadding(0, 0, 0, 0);
            toggleButton.setText(channelLabel);
            toggleButton.setTextOn(channelLabel);
            toggleButton.setTextOff(channelLabel);
            // e.g., LinearLayout.LayoutParams param = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            params.setMargins (0, 0, 0, 0);
            toggleButton.setLayoutParams (params);
            lightToggleButtons.add (toggleButton); // Add the button to the list.
            lightLayout.addView (toggleButton);
        }
        transformLayout.addView (lightLayout);

        // Channels

        final ArrayList<ToggleButton> channelEnableToggleButtons = new ArrayList<> ();
        final ArrayList<Button> channelDirectionButtons = new ArrayList<> ();
        final ArrayList<Button> channelModeButtons = new ArrayList<> ();
        final ArrayList<ToggleButton> channelValueToggleButtons = new ArrayList<> ();

        // Set up the channel label
        final TextView channelEnabledLabel = new TextView (getActivity());
        channelEnabledLabel.setText("Enable channels");
        channelEnabledLabel.setPadding(70, 20, 70, 20);
        transformLayout.addView (channelEnabledLabel);

        LinearLayout channelEnabledLayout = new LinearLayout (getActivity());
        channelEnabledLayout.setOrientation (LinearLayout.HORIZONTAL);
        for (int i = 0; i < 12; i++) {
            final String channelLabel = Integer.toString (i + 1);
            final ToggleButton toggleButton = new ToggleButton (getActivity());
            toggleButton.setPadding(0, 0, 0, 0);
            toggleButton.setText (channelLabel);
            toggleButton.setTextOn (channelLabel);
            toggleButton.setTextOff (channelLabel);
            // e.g., LinearLayout.LayoutParams param = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            params.setMargins (0, 0, 0, 0);
            toggleButton.setLayoutParams (params);
            channelEnableToggleButtons.add (toggleButton); // Add the button to the list.
            channelEnabledLayout.addView (toggleButton);
        }
        transformLayout.addView (channelEnabledLayout);

        // Set up the label
        final TextView signalLabel = new TextView (getActivity());
        signalLabel.setText ("Set channel direction, mode, and value"); // INPUT: Discrete/Digital, Continuous/Analog; OUTPUT: Discrete, Continuous/PWM
        signalLabel.setPadding (70, 20, 70, 20);
        transformLayout.addView (signalLabel);

        // Show I/O options
        final LinearLayout ioLayout = new LinearLayout (getActivity());
        ioLayout.setOrientation (LinearLayout.HORIZONTAL);
        for (int i = 0; i < 12; i++) {
            final String channelLabel = Integer.toString (i + 1);
            final Button toggleButton = new Button (getActivity());
            toggleButton.setPadding (0, 0, 0, 0);
            toggleButton.setText(" ");
            toggleButton.setEnabled (false); // TODO: Initialize with current state at the behavior's location in the loop! That is allow defining _changes to_ an existing state, so always work from grounded state material.
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            toggleButton.setLayoutParams (params);
            channelDirectionButtons.add (toggleButton); // Add the button to the list.
            ioLayout.addView (toggleButton);
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
        LinearLayout channelModeLayout = new LinearLayout (getActivity());
        channelModeLayout.setOrientation (LinearLayout.HORIZONTAL);
        for (int i = 0; i < 12; i++) {
            final Button toggleButton = new Button (getActivity());
            toggleButton.setPadding (0, 0, 0, 0);
            toggleButton.setText(" ");
            toggleButton.setEnabled (false); // TODO: Initialize with current state at the behavior's location in the loop! That is allow defining _changes to_ an existing state, so always work from grounded state material.
            // e.g., LinearLayout.LayoutParams param = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            toggleButton.setLayoutParams (params);
            channelModeButtons.add (toggleButton); // Add the button to the list.
            channelModeLayout.addView (toggleButton);
        }
        transformLayout.addView (channelModeLayout);

        // Value. Show channel value.
        LinearLayout channelValueLayout = new LinearLayout (getActivity());
        channelValueLayout.setOrientation (LinearLayout.HORIZONTAL);
//        channelLayout.setLayoutParams (new LinearLayout.LayoutParams (MATCH_PARENT));
        for (int i = 0; i < 12; i++) {
            // final String buttonLabel = Integer.toString (i + 1);
            final ToggleButton toggleButton = new ToggleButton (getActivity());
            toggleButton.setPadding(0, 0, 0, 0);
            toggleButton.setEnabled (false);
            toggleButton.setText (" ");
            toggleButton.setTextOn ("H");
            toggleButton.setTextOff ("L");
            // e.g., LinearLayout.LayoutParams param = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            params.setMargins (0, 0, 0, 0);
            toggleButton.setLayoutParams (params);
            channelValueToggleButtons.add (toggleButton); // Add the button to the list.
            channelValueLayout.addView (toggleButton);
        }
        transformLayout.addView (channelValueLayout);

        // Set up interactivity for channel enable buttons.
        for (int i = 0; i < 12; i++) {

            final ToggleButton channelEnableButton = channelEnableToggleButtons.get (i);
            final Button channelDirectionButton = channelDirectionButtons.get (i);
            final Button channelModeButton = channelModeButtons.get (i);
            final ToggleButton channelValueToggleButton = channelValueToggleButtons.get (i);

            channelEnableButton.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener () {
                @Override
                public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {

                    if (channelDirectionButton.getText ().toString ().equals (" ")) {
                        channelDirectionButton.setText ("I");
                    }
                    channelDirectionButton.setEnabled (isChecked);

                    if (channelModeButton.getText ().toString ().equals (" ")) {
                        channelModeButton.setText ("T");
                    }
                    channelModeButton.setEnabled (isChecked);

                    if (isChecked == false) {
                        channelValueToggleButton.setEnabled (isChecked);
                    }
                }
            });
        }

        // Setup interactivity for I/O options
        for (int i = 0; i < 12; i++) {

            final Button channelDirectionButton = channelDirectionButtons.get (i);
            final Button channelModeButton = channelModeButtons.get (i);
            final ToggleButton channelValueToggleButton = channelValueToggleButtons.get (i);

            channelDirectionButton.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick (View v) {
                    String currentText = channelDirectionButton.getText ().toString ();
                    if (currentText.equals (" ")) {
                        channelDirectionButton.setText ("I");
                        channelModeButton.setEnabled (true);

                        // Update modes for input channel.
                        if (channelModeButton.getText ().toString ().equals (" ")) {
                            channelModeButton.setText ("T");
                        }
                        channelModeButton.setOnClickListener (new View.OnClickListener () {
                            @Override
                            public void onClick (View v) {
                                String currentText = channelModeButton.getText ().toString ();
                                if (currentText.equals (" ")) { // Do not change. Keep current state.

                                    channelModeButton.setText ("T"); // Toggle.

                                    // Update values for output channel.
                                    channelValueToggleButton.setEnabled (true);
                                    if (channelValueToggleButton.getText ().toString ().equals (" ")) {
                                        channelValueToggleButton.setText ("L");
                                    }

                                } else if (currentText.equals ("T")) {
                                    channelModeButton.setText ("W"); // Waveform.
                                    channelValueToggleButton.setEnabled (false); // Update values for output channel.
                                } else if (currentText.equals ("W")) {
                                    channelModeButton.setText ("P"); // Pulse.
                                    channelValueToggleButton.setEnabled (false); // Update values for output channel.
                                } else if (currentText.equals ("P")) {
                                    channelModeButton.setText ("T"); // Toggle

                                    // Update values for output channel.
                                    channelValueToggleButton.setEnabled (true);
                                    if (channelValueToggleButton.getText ().toString ().equals (" ")) {
                                        channelValueToggleButton.setText ("L");
                                    }
                                }
                            }
                        });

                    } else if (currentText.equals ("I")) {
                        channelDirectionButton.setText ("O");

                        // Update modes for output channel.
                        channelModeButton.setEnabled (true);
                        if (channelModeButton.getText ().toString ().equals (" ")) {
                            channelModeButton.setText ("T");
                        }
                        channelModeButton.setOnClickListener (new View.OnClickListener () {
                            @Override
                            public void onClick (View v) {
                                String currentText = channelModeButton.getText ().toString ();
                                if (currentText.equals (" ")) { // Do not change. Keep current state.

                                    channelModeButton.setText ("T"); // Toggle.

                                    // Update values for output channel.
                                    channelValueToggleButton.setEnabled (true);
                                    if (channelValueToggleButton.getText ().toString ().equals (" ")) {
                                        channelValueToggleButton.setText ("L");
                                    }

                                } else if (currentText.equals ("T")) {
                                    channelModeButton.setText ("W"); // Waveform.
                                    channelValueToggleButton.setEnabled (false); // Update values for output channel.
                                } else if (currentText.equals ("W")) {
                                    channelModeButton.setText ("P"); // Pulse.
                                    channelValueToggleButton.setEnabled (false); // Update values for output channel.
                                } else if (currentText.equals ("P")) {
                                    channelModeButton.setText ("T"); // Toggle

                                    // Update values for output channel.
                                    channelValueToggleButton.setEnabled (true);
                                    if (channelValueToggleButton.getText ().toString ().equals (" ")) {
                                        channelValueToggleButton.setText ("L");
                                    }
                                }
                            }
                        });

                    } else if (currentText.equals ("O")) {
                        channelDirectionButton.setText ("I");
                        channelModeButton.setEnabled (true);

                        // Update modes for input channel.
                        if (channelModeButton.getText ().toString ().equals (" ")) {
                            channelModeButton.setText ("T");
                        }
                        channelModeButton.setOnClickListener (new View.OnClickListener () {
                            @Override
                            public void onClick (View v) {
                                String currentText = channelModeButton.getText ().toString ();
                                if (currentText.equals (" ")) { // Do not change. Keep current state.
                                    channelModeButton.setText ("T"); // Toggle.

                                    // Update values for output channel.
                                    channelValueToggleButton.setEnabled (true);
                                    if (channelValueToggleButton.getText ().toString ().equals (" ")) {
                                        channelValueToggleButton.setText ("L");
                                    }

                                } else if (currentText.equals ("T")) {
                                    channelModeButton.setText ("W"); // Waveform.
                                    channelValueToggleButton.setEnabled (false); // Update values for output channel.
                                } else if (currentText.equals ("W")) {
                                    channelModeButton.setText ("P"); // Pulse.
                                    channelValueToggleButton.setEnabled (false); // Update values for output channel.
                                } else if (currentText.equals ("P")) {
                                    channelModeButton.setText ("T"); // Toggle

                                    // Update values for output channel.
                                    channelValueToggleButton.setEnabled (true);
                                    if (channelValueToggleButton.getText ().toString ().equals (" ")) {
                                        channelValueToggleButton.setText ("L");
                                    }
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
//                Hack_behaviorTitle = input.getText ().toString ();
                String transformString = "apply ";
                // Add the LED state
//                for (int i = 0; i < 12; i++) {
//                    if (lightToggleButtons.get(i).isChecked()) {
//                        transformString = transformString.concat(" 1");
//                    } else {
//                        transformString = transformString.concat(" 0");
//                    }
//                }

                for (int i = 0; i < 12; i++) {

                    final ToggleButton lightEnableButton = lightToggleButtons.get (i);
                    final ToggleButton channelEnableButton = channelEnableToggleButtons.get (i);
                    final Button channelDirectionButton = channelDirectionButtons.get (i);
                    final Button channelModeButton = channelModeButtons.get (i);
                    final ToggleButton channelValueToggleButton = channelValueToggleButtons.get (i);

                    // LED enable. Is the LED on or off?

                    if (lightEnableButton.isChecked ()) {
                        transformString = transformString.concat ("T");
                    } else {
                        transformString = transformString.concat ("F");
                    }
                    // transformString = transformString.concat (","); // Add comma

                    // TODO: Set LED color.

                    // Channel enable. Is the channel enabled?

                    if (channelEnableButton.isChecked ()) {
                        transformString = transformString.concat ("T");
                    } else {
                        transformString = transformString.concat ("F");
                    }
                    // transformString = transformString.concat (","); // Add comma

                    // Channel I/O direction. Is the I/O input or output?

                    if (channelDirectionButton.isEnabled ()) {
                        String channelDirectionString = channelDirectionButton.getText ().toString ();
                        transformString = transformString.concat (channelDirectionString);
                    } else {
                        transformString = transformString.concat ("-");
                    }
                    // transformString = transformString.concat (","); // Add comma

                    // Channel I/O mode. Is the channel toggle switch (discrete), waveform (continuous), or pulse?

                    if (channelModeButton.isEnabled ()) {
                        String channelModeString = channelModeButton.getText ().toString ();
                        transformString = transformString.concat (channelModeString);
                    } else {
                        transformString = transformString.concat ("-");
                    }

                    // Channel value.
                    // TODO: Create behavior transform to apply channel values separately. This transform should only configure the channel operational flow state.

                    if (channelValueToggleButton.isEnabled ()) {
                        String channelValueString = channelValueToggleButton.getText ().toString ();
                        transformString = transformString.concat (channelValueString);
                    } else {
                        transformString = transformString.concat ("-");
                    }

                    // Add space between channel states.
                    if (i < (12 - 1)) {
                        transformString = transformString.concat (" ");
                    }
                }

                // Add wait
                Hack_BehaviorTransformTitle = transformString;
                Behavior behavior = new Behavior ("transform");
                behavior.setTransform(Hack_BehaviorTransformTitle);
                BehaviorConstruct behaviorConstruct = new BehaviorConstruct (perspective);
                behaviorConstruct.setBehavior(behavior);
                perspective.addBehaviorConstruct(behaviorConstruct);

                // Refresh the timeline view
                refreshTimeline();
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

    String Hack_SwitchBehaviorTransformTitle = "";
    public void Hack_PromptForSwitchBehaviorTransform (final Perspective perspective) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle ("Cause and effect.");
        builder.setMessage ("Describe the relationship.");

        // TODO: Specify the units to receive the change.

        // Declare transformation layout
        LinearLayout transformLayout = new LinearLayout (getActivity());
        transformLayout.setOrientation (LinearLayout.VERTICAL);

        /*
        // Condition

        // Set up the condition label
        final TextView conditionLabel = new TextView (this);
        conditionLabel.setText("Condition");
        conditionLabel.setPadding(70, 20, 70, 20);
        transformLayout.addView(conditionLabel);
        */

        // TODO: None, Switch, Threshold, Gesture, Message, Data

        // Source:

        // Set up the switch cause label
        final TextView switchCauseLabel = new TextView (getActivity());
        switchCauseLabel.setText ("Cause");
        switchCauseLabel.setPadding (70, 20, 70, 20);
        transformLayout.addView (switchCauseLabel);

//        final NumberPicker switchCauseNumberPicker = new NumberPicker (this);
//        switchCauseNumberPicker.setEnabled (true);
//        switchCauseNumberPicker.setMinValue (1);
//        switchCauseNumberPicker.setMaxValue (12);

        LinearLayout switchCauseLayout = new LinearLayout (getActivity());
        switchCauseLayout.setOrientation (LinearLayout.HORIZONTAL);
        final ArrayList<ToggleButton> switchCauseToggleButtons = new ArrayList<> ();
        for (int i = 0; i < 12; i++) {
            final String channelLabel = Integer.toString (i + 1);
            final ToggleButton toggleButton = new ToggleButton (getActivity());
            toggleButton.setPadding (0, 0, 0, 0);
            toggleButton.setText (channelLabel);
            toggleButton.setTextOn (channelLabel);
            toggleButton.setTextOff (channelLabel);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            params.setMargins (0, 0, 0, 0);
            toggleButton.setLayoutParams (params);
            switchCauseToggleButtons.add (toggleButton); // Add the button to the list.
            switchCauseLayout.addView (toggleButton);
        }
        transformLayout.addView (switchCauseLayout);

        // Effect:

        // Set up the switch cause label
        final TextView switchEffectLabel = new TextView (getActivity());
        switchEffectLabel.setText ("Effect");
        switchEffectLabel.setPadding (70, 20, 70, 20);
        transformLayout.addView (switchEffectLabel);

        LinearLayout switchEffectLayout = new LinearLayout (getActivity());
        switchEffectLayout.setOrientation(LinearLayout.HORIZONTAL);
        final ArrayList<ToggleButton> switchEffectToggleButtons = new ArrayList<> ();
//        final ArrayList<ArrayList<Boolean>> switchEffectChannels = new ArrayList<> ();
//        for (int i = 0; i < 12; i++) {
//            ArrayList<Boolean> effectChannels = new ArrayList<> ();
//            for (int j = 0; j < 12; j++) {
//                effectChannels.add (false);
//            }
//            switchEffectChannels.add (effectChannels);
//        }
        for (int i = 0; i < 12; i++) {
            final String channelLabel = Integer.toString (i + 1);
            final ToggleButton toggleButton = new ToggleButton (getActivity());
            toggleButton.setPadding(0, 0, 0, 0);
            toggleButton.setText(channelLabel);
            toggleButton.setTextOn(channelLabel);
            toggleButton.setTextOff(channelLabel);
            // e.g., LinearLayout.LayoutParams param = new LinearLayout.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            params.setMargins (0, 0, 0, 0);
            toggleButton.setLayoutParams (params);
            switchEffectToggleButtons.add (toggleButton); // Add the button to the list.
            switchEffectLayout.addView (toggleButton);
        }
        transformLayout.addView (switchEffectLayout);

//        for (int i = 0; i < 12; i++) {
//            final ToggleButton switchCauseToggleButton = switchCauseToggleButtons.get (i);
//            switchCauseToggleButton.setOnCheckedChangeListener (new CompoundButton.OnCheckedChangeListener () {
//                @Override
//                public void onCheckedChanged (CompoundButton buttonView, boolean isChecked) {
//
//                    buttonView.setEnabled (false);
//
//                    // Update states of effect channel toggle buttons.
//                    for (int j = 0; j < 12; j++) {
//
//                        // Update the cause toggle buttons.
//                        ToggleButton otherSwitchCauseToggleButton = switchCauseToggleButtons.get (j);
//                        if (buttonView != otherSwitchCauseToggleButton) {
//                            otherSwitchCauseToggleButton.setChecked (false);
//                        }
//
//                        // Update the effect toggle buttons.
//                        switchEffectToggleButtons.get (j).setEnabled (false);
//                        switchEffectToggleButtons.get (j).setChecked (false);
//                        switchEffectToggleButtons.get (j).setEnabled (true);
//                    }
//
//                    buttonView.setChecked (true);
//                    buttonView.setEnabled (true);
//
//                }
//            });
//        }

        // Assign the layout to the alert dialog.
        builder.setView (transformLayout);

        // Set up the buttons
        builder.setPositiveButton ("DONE", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {
//                Hack_behaviorTitle = input.getText ().toString ();

                for (int i = 0; i < 12; i++) {

                    // Get the cause toggle button.
                    ToggleButton causeToggleButton = switchCauseToggleButtons.get (i);
                    if (causeToggleButton.isChecked () == false) {
                        continue;
                    }

                    // Get the effects of the given cause
                    for (int j = 0; j < 12; j++) {

                        // Get the cause toggle button.
                        ToggleButton effectToggleButton = switchEffectToggleButtons.get (j);
                        if (effectToggleButton.isChecked () == false) {
                            continue;
                        }

                        String transformString = "cause " + (i + 1) + " effect " + (j + 1); // TODO: "switch <cause-unit-uuid> 4 <effect-unit-uuid> 8 when <transition-type>"

                        // Define cause and effect transform.
                        Hack_SwitchBehaviorTransformTitle = transformString;
                        Behavior behavior = new Behavior ("cause/effect");
                        behavior.setTransform(Hack_SwitchBehaviorTransformTitle);
                        BehaviorConstruct behaviorConstruct = new BehaviorConstruct (perspective);
                        behaviorConstruct.setBehavior(behavior);
                        perspective.addBehaviorConstruct(behaviorConstruct);

                        // HACK: Break
                        // TODO: Support adding multiple mappings to try it out. Feel the power!
                        i = 12;
                        j = 12;

                    }

                }

                // Refresh the timeline view
                refreshTimeline();
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
     * Verbalizer.
     */
    String Hack_PromptForSpeechTitle = "";
    public void Hack_PromptForSpeech (final Perspective perspective) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle ("tell me the behavior");

        // Set up the input
        final EditText input = new EditText(getActivity());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);//input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton ("DONE", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {
                Hack_PromptForSpeechTitle = input.getText ().toString ();
                Behavior behavior = new Behavior("say");
                behavior.setTransform(Hack_PromptForSpeechTitle);
                BehaviorConstruct behaviorConstruct = new BehaviorConstruct (perspective);
                behaviorConstruct.setBehavior(behavior);
                perspective.addBehaviorConstruct(behaviorConstruct);

                // Refresh the timeline view
                refreshTimeline();
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
     * Message.
     */
    String Hack_PromptForMessageTitle = "";
    public void Hack_PromptForMessage (final Perspective perspective) {
        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity());
        builder.setTitle ("Describe the message.");

        // Set up the input
        final EditText input = new EditText(getActivity());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);//input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton ("DONE", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {
                Hack_PromptForMessageTitle = input.getText ().toString ();
                Behavior behavior = new Behavior ("message");
                behavior.setTransform(Hack_PromptForMessageTitle);
                BehaviorConstruct behaviorConstruct = new BehaviorConstruct (perspective);
                behaviorConstruct.setBehavior(behavior);
                perspective.addBehaviorConstruct (behaviorConstruct);

                // Refresh the timeline view
                refreshTimeline();
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

    String Hack_TimeTransformTitle = "";
    public void Hack_PromptForTimeTransform (final Perspective perspective) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle ("Time Transform");
        builder.setMessage ("How do you want to change time?");

        // Declare transformation layout
        LinearLayout transformLayout = new LinearLayout (getActivity());
        transformLayout.setOrientation (LinearLayout.VERTICAL);

        // Wait (until next behavior)

        // Set up the label
        final TextView waitLabel = new TextView (getActivity());
        waitLabel.setText ("Wait (0 ms)");
        waitLabel.setPadding (70, 20, 70, 20);
        transformLayout.addView (waitLabel);

        final SeekBar waitVal = new SeekBar (getActivity());
        waitVal.setMax (1000);
        waitVal.setHapticFeedbackEnabled (true); // TODO: Emulate this in the custom interface
        waitVal.setOnSeekBarChangeListener (new SeekBar.OnSeekBarChangeListener () {
            @Override
            public void onProgressChanged (SeekBar seekBar, int progress, boolean fromUser) {
                waitLabel.setText ("Wait (" + progress + " ms)");
            }

            @Override
            public void onStartTrackingTouch (SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch (SeekBar seekBar) {

            }
        });
        transformLayout.addView (waitVal);

        // Assign the layout to the alert dialog.
        builder.setView (transformLayout);

        // Set up the buttons
        builder.setPositiveButton ("DONE", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {

                // Create transform string
                String transformString = "wait ";

                // Add wait
                transformString = transformString.concat (Integer.toString (waitVal.getProgress ()));
                Hack_TimeTransformTitle = transformString;
                Behavior behavior = new Behavior ("time");
                behavior.setTransform(Hack_TimeTransformTitle);
                BehaviorConstruct behaviorConstruct = new BehaviorConstruct (perspective);
                behaviorConstruct.setBehavior(behavior);
                perspective.addBehaviorConstruct(behaviorConstruct);

                // Refresh the timeline view
                refreshTimeline();
            }
        });
        builder.setNegativeButton ("Cancel", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {
                dialog.cancel ();
            }
        });

        builder.show ();
    }/**
     * Show behavior browser and prompt for selection.
     */
    public void Hack_PromptForBehaviorSelection (final Perspective perspective) {

        // Create the list of behaviors
        int basicBehaviorCount = getApplication().getClay ().getBehaviorRepository ().getCachedBehaviors ().size();
        final String[] basicBehaviors = new String[basicBehaviorCount];
        for (int i = 0; i < basicBehaviorCount; i++) {
            Behavior behavior = getApplication().getClay ().getBehaviorRepository ().getCachedBehaviors ().get (i);
            basicBehaviors[i] = behavior.getTitle ();
        }

        // Show the list of behaviors
        AlertDialog.Builder builder = new AlertDialog.Builder (getActivity());
        builder.setTitle ("Select a behavior");
        builder.setItems(basicBehaviors, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {

                if (basicBehaviors[item].toString().equals("control")) {
                    Hack_PromptForBehaviorTransform(perspective);
                } else if (basicBehaviors[item].toString().equals("time")) {
                    Hack_PromptForTimeTransform(perspective);
                } else if (basicBehaviors[item].toString().equals("cause/effect")) {
                    Hack_PromptForSwitchBehaviorTransform(perspective);
                } else if (basicBehaviors[item].toString().equals("message")) {
                    Hack_PromptForMessage(perspective);
                } else if (basicBehaviors[item].toString().equals("say")) {
                    Hack_PromptForSpeech(perspective);
                }
            }
        });
        AlertDialog alert = builder.create();

        // Verbalize creative scaffolding for context
        ArrayList<String> phrases = new ArrayList<String> ();
        phrases.add ("hi. what do you want me to do?");
        phrases.add("choose one of these behaviors.");
        phrases.add("do what?");
        phrases.add("what're you thinking?");
        phrases.add("tell me what to do");
        phrases.add("which one?");
        phrases.add("select a behavior");
        phrases.add("adding a behavior");
        // Choose the phrase to verbalize. Default to random selection algorithm.
        // TODO: Choose the verbalization pattern based on the speed of interaction (metric for experience and comfort level).
        Random random = new Random();
        int phraseChoice = random.nextInt (phrases.size ());
        // Verbalize the phrase
//        Hack_Speak(phrases.get(phraseChoice));
        // TODO: Adapt voice recognition to look for context-specfic speech.

        alert.show();
    }

    public class UdpDatagramTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
                /* Get weather data from an Internet source. */

            if (params.length == 0) {
                return null;
            }

//            communication.sendDatagram(params[0], "connected");

            // This only happens if there was an error getting or parsing the forecast.
            return null;
        }
    }

    public class HttpRequestTask extends AsyncTask<String, Void, String[]> { // Extend AsyncTask and use void generics (for now)

        private final String LOG_TAG = HttpRequestTask.class.getSimpleName();

        @Override
        protected String[] doInBackground(String... params) {
                /* Get weather data from an Internet source. */

            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String forecastJsonStr = null;
            String[] forecastData;

            String format = "json";
            String units = "metric";
            int numDays = 7;

            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast

//                URL url = new URL("http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7");

//                Uri.Builder builder = new Uri.Builder ();
//                builder.scheme("http")
//                        .authority("api.openweathermap.org")
//                        .appendPath("data")
//                        .appendPath("2.5")
//                        .appendPath("forecast")
//                        .appendPath("daily")
//                        .appendQueryParameter("q", postcodes[0])
//                        .appendQueryParameter("mode", "json")
//                        .appendQueryParameter("units", "metric")
//                        .appendQueryParameter("cnt", "7");
//
//                URL url = new URL (builder.build().toString());

                //final String FORECAST_BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/daily?";
                final String CLAY_UNIT_BASE_URL = "http://192.168.43.235/message?";
                final String CONTENT_PARAM = "content";
//                final String FORMAT_PARAM = "mode";
//                final String UNITS_PARAM = "units";
//                final String DAYS_PARAM = "cnt";

                // This approach enables the user to set the zip code from the settings activity.
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()); // Get preferences for this activity
                String remoteHostUri = prefs.getString(getString(R.string.pref_remote_host_key), // If there's a value stored for the location key in preferences, use it...
                        getString(R.string.pref_remote_host_default));

                // TODO: Replace the " " space character with "%20" string.

                //Uri builtUri = Uri.parse(remoteHostUri).buildUpon()
                Uri builtUri = Uri.parse(CLAY_UNIT_BASE_URL).buildUpon()
                        .appendQueryParameter(CONTENT_PARAM, params[0])
//                        .appendQueryParameter(FORMAT_PARAM, format)
//                        .appendQueryParameter(UNITS_PARAM, units)
//                        .appendQueryParameter(DAYS_PARAM, Integer.toString(numDays))
                        .build();

                URL url = new URL(builtUri.toString());




                /*
                communication.sendDatagram (params[0]);
                */

//                // Broadcast UDP packet to the specified address.
//                String messageStr = params[0]; // "turn light 1 on";
//                int local_port = 4445;
//                int server_port = 4445;
//                DatagramSocket s = new DatagramSocket(local_port);
////                InetAddress local = InetAddress.getByName("192.168.43.235");
//                InetAddress local = InetAddress.getByName("255.255.255.255");
//                int msg_length = messageStr.length();
//                byte[] message = messageStr.getBytes();
//                DatagramPacket p = new DatagramPacket(message, msg_length, local, server_port);
//                s.send(p);
//                s.close();





                Log.v(LOG_TAG, "SENDING REQUEST TO: " + url.toString());

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                forecastJsonStr = buffer.toString();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            // All this just for: return getWeatherDataFromJson(forecastJsonStr, numDays);
//            try {
//                return getWeatherDataFromJson(forecastJsonStr, numDays);
//            } catch (JSONException e) {
//                Log.e(LOG_TAG, e.getMessage(), e);
//                e.printStackTrace();
//            }

            // This only happens if there was an error getting or parsing the forecast.
            return null;
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (result != null) {
                listAdapter.clear ();
                for (String dayForecastStr : result) {
                    listAdapter.add (dayForecastStr);
                }
                // New day is back from the server at this point!

                // NOTE: Array adapter internally calls: adapter.notifyDataSetChanged()
            }
        }

        /* The date/time conversion code is going to be moved outside the asynctask later,
         * so for convenience we're breaking it out into its own method now.
         */
        private String getReadableDateString(long time){
            // Because the API returns a unix timestamp (measured in seconds),
            // it must be converted to milliseconds in order to be converted to valid date.
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(time);
        }

        /**
         * Prepare the weather high/lows for presentation.
         */
        private String formatHighLows(double high, double low) {
            // For presentation, assume the user doesn't care about tenths of a degree.
            long roundedHigh = Math.round(high);
            long roundedLow = Math.round(low);

            String highLowStr = roundedHigh + "/" + roundedLow;
            return highLowStr;
        }
    }
}
