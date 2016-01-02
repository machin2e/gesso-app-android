package computer.clay.protocolizer;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends ActionBarActivity {

    private final int CHECK_CODE = 0x1;
    private final int LONG_DURATION = 5000;
    private final int SHORT_DURATION = 1200;

    private Speaker speaker;

    private static Context context;

    private Clay clay = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Store application context
        MainActivity.context = getApplicationContext();

        getSupportActionBar().hide(); // Hide the application's title bar.

        // Remove notification bar
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // Configure Clay for the Android platform
        Clay.setPlatformContext (this);

        // Create Clay
        this.clay = new Clay ();

        // Start speech synthesis engine.
        checkTTS ();

        // Set content view.
        setContentView (R.layout.activity_main);
    }

    @Override
    protected void onPause() {
        super.onPause();

//        // Pause the communications
//        // HACK: Resume this!
//        communication.stopDatagramServer ();
//        clay.getNetwork ().stopDatagramServer ();
    }

    @Override
    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == CHECK_CODE){
            if(resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                speaker = new Speaker(this);
            }else {
                Intent install = new Intent();
                install.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(install);
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            startActivity(new Intent(this, SettingsActivity.class));

            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public Clay getClay () {
        return this.clay;
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }

    private void checkTTS(){
        Intent check = new Intent();
        check.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(check, CHECK_CODE);
    }

    public void Hack_Speak (String phrase) {
        Log.v("Clay_Verbalizer", "Hack_Speak: " + phrase);
//        if (speaker.isAllowed ())
        if (speaker != null) {
            speaker.allow (true);
            speaker.speak (phrase);
            speaker.allow (false);
        }
    }

    String Hack_TimeTransformTitle = "";
    public void Hack_PromptForTimeTransform (final BehaviorConstruct behaviorConstruct) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle ("Time Transform");
        builder.setMessage ("How do you want to change time?");

        // Declare transformation layout
        LinearLayout transformLayout = new LinearLayout (this);
        transformLayout.setOrientation (LinearLayout.VERTICAL);

        // Wait (until next behavior)

        // Set up the label
        final TextView waitLabel = new TextView (this);
        waitLabel.setText ("Wait (0 ms)");
        waitLabel.setPadding (70, 20, 70, 20);
        transformLayout.addView (waitLabel);

        final SeekBar waitVal = new SeekBar (this);
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
                behaviorConstruct.getBehavior().setTitle ("time");
                behaviorConstruct.getBehavior().setTransform (Hack_TimeTransformTitle);
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

    String Hack_BehaviorTransformTitle = "";
    public void Hack_PromptForBehaviorTransform (final BehaviorConstruct behaviorConstruct) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle ("Change the channel.");
        builder.setMessage ("What do you want to do?");
        // builder.setTitle ("Behavior Transform");

        // Populate with the current transform values (if any).
        if (behaviorConstruct.getBehavior().getTransform() != null) {
            Log.v("Behavior_Transform", behaviorConstruct.getBehavior().getTransform());
            // TODO: Store the previous values so they can be used to initialize the interface.
        }

        // TODO: Specify the units to receive the change.

        // Declare transformation layout
        LinearLayout transformLayout = new LinearLayout (this);
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
        final TextView lightLabel = new TextView (this);
        lightLabel.setText("Enable LED feedback");
        lightLabel.setPadding(70, 20, 70, 20);
        transformLayout.addView (lightLabel);

        LinearLayout lightLayout = new LinearLayout (this);
        lightLayout.setOrientation (LinearLayout.HORIZONTAL);
//        channelLayout.setLayoutParams (new LinearLayout.LayoutParams (MATCH_PARENT));
        final ArrayList<ToggleButton> lightToggleButtons = new ArrayList<> ();
        for (int i = 0; i < 12; i++) {
            final String channelLabel = Integer.toString (i + 1);
            final ToggleButton toggleButton = new ToggleButton (this);
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
        final TextView channelEnabledLabel = new TextView (this);
        channelEnabledLabel.setText("Enable channels");
        channelEnabledLabel.setPadding(70, 20, 70, 20);
        transformLayout.addView (channelEnabledLabel);

        LinearLayout channelEnabledLayout = new LinearLayout (this);
        channelEnabledLayout.setOrientation (LinearLayout.HORIZONTAL);
        for (int i = 0; i < 12; i++) {
            final String channelLabel = Integer.toString (i + 1);
            final ToggleButton toggleButton = new ToggleButton (this);
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
        final TextView signalLabel = new TextView (this);
        signalLabel.setText ("Set channel direction, mode, and value"); // INPUT: Discrete/Digital, Continuous/Analog; OUTPUT: Discrete, Continuous/PWM
        signalLabel.setPadding (70, 20, 70, 20);
        transformLayout.addView (signalLabel);

        // Show I/O options
        final LinearLayout ioLayout = new LinearLayout (this);
        ioLayout.setOrientation (LinearLayout.HORIZONTAL);
        for (int i = 0; i < 12; i++) {
            final String channelLabel = Integer.toString (i + 1);
            final Button toggleButton = new Button (this);
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
        LinearLayout channelModeLayout = new LinearLayout (this);
        channelModeLayout.setOrientation (LinearLayout.HORIZONTAL);
        for (int i = 0; i < 12; i++) {
            final Button toggleButton = new Button (this);
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
        LinearLayout channelValueLayout = new LinearLayout (this);
        channelValueLayout.setOrientation (LinearLayout.HORIZONTAL);
//        channelLayout.setLayoutParams (new LinearLayout.LayoutParams (MATCH_PARENT));
        for (int i = 0; i < 12; i++) {
            // final String buttonLabel = Integer.toString (i + 1);
            final ToggleButton toggleButton = new ToggleButton (this);
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
                behaviorConstruct.getBehavior().setTitle ("transform");
                behaviorConstruct.getBehavior().setTransform (Hack_BehaviorTransformTitle);
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
    public void Hack_PromptForSwitchBehaviorTransform (final BehaviorConstruct behaviorConstruct) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle ("Cause and effect.");
        builder.setMessage ("Describe the relationship.");

        // TODO: Specify the units to receive the change.

        // Declare transformation layout
        LinearLayout transformLayout = new LinearLayout (this);
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
        final TextView switchCauseLabel = new TextView (this);
        switchCauseLabel.setText ("Cause");
        switchCauseLabel.setPadding (70, 20, 70, 20);
        transformLayout.addView (switchCauseLabel);

//        final NumberPicker switchCauseNumberPicker = new NumberPicker (this);
//        switchCauseNumberPicker.setEnabled (true);
//        switchCauseNumberPicker.setMinValue (1);
//        switchCauseNumberPicker.setMaxValue (12);

        LinearLayout switchCauseLayout = new LinearLayout (this);
        switchCauseLayout.setOrientation (LinearLayout.HORIZONTAL);
        final ArrayList<ToggleButton> switchCauseToggleButtons = new ArrayList<> ();
        for (int i = 0; i < 12; i++) {
            final String channelLabel = Integer.toString (i + 1);
            final ToggleButton toggleButton = new ToggleButton (this);
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
        final TextView switchEffectLabel = new TextView (this);
        switchEffectLabel.setText ("Effect");
        switchEffectLabel.setPadding (70, 20, 70, 20);
        transformLayout.addView (switchEffectLabel);

        LinearLayout switchEffectLayout = new LinearLayout (this);
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
            final ToggleButton toggleButton = new ToggleButton (this);
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
                        behaviorConstruct.getBehavior().setTitle ("cause/effect");
                        behaviorConstruct.getBehavior().setTransform (Hack_SwitchBehaviorTransformTitle);

                        // HACK: Break
                        // TODO: Support adding multiple mappings to try it out. Feel the power!
                        i = 12;
                        j = 12;

                    }

                }
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
    public void Hack_PromptForSpeech (final BehaviorConstruct behaviorConstruct) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle ("tell me the behavior");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);//input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton ("DONE", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {
                Hack_PromptForSpeechTitle = input.getText ().toString ();
                behaviorConstruct.getBehavior ().setTitle ("say");
                behaviorConstruct.getBehavior ().setTransform ("say " + Hack_PromptForSpeechTitle);
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
    public void Hack_PromptForMessage (final BehaviorConstruct behaviorConstruct) {
        AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setTitle ("Describe the message.");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);//input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton ("DONE", new DialogInterface.OnClickListener () {
            @Override
            public void onClick (DialogInterface dialog, int which) {
                Hack_PromptForMessageTitle = input.getText ().toString ();
                behaviorConstruct.getBehavior ().setTitle ("message");
                behaviorConstruct.getBehavior ().setTransform ("message " + Hack_PromptForMessageTitle);
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

    public void Hack_PromptForBehaviorSelection (final BehaviorConstruct behaviorConstruct) {

        // Create the list of behaviors
        int basicBehaviorCount = getClay ().getBehaviorRepository ().getCachedBehaviors ().size ();
        final String[] basicBehaviors = new String[basicBehaviorCount];
        for (int i = 0; i < basicBehaviorCount; i++) {
            Behavior behavior = getClay ().getBehaviorRepository ().getCachedBehaviors ().get (i);
            basicBehaviors[i] = behavior.getTitle ();
        }

        // Show the list of behaviors
        AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setTitle ("Select a behavior");
        builder.setItems (basicBehaviors, new DialogInterface.OnClickListener () {
            public void onClick (DialogInterface dialog, int item) {

                if (basicBehaviors[item].toString ().equals ("control")) {
                    Hack_PromptForBehaviorTransform (behaviorConstruct);
                } else if (basicBehaviors[item].toString ().equals ("time")) {
                    Hack_PromptForTimeTransform (behaviorConstruct);
                } else if (basicBehaviors[item].toString ().equals ("cause/effect")) {
                    Hack_PromptForSwitchBehaviorTransform (behaviorConstruct);
                } else if (basicBehaviors[item].toString ().equals ("message")) {
                    Hack_PromptForMessage(behaviorConstruct);
                } else if (basicBehaviors[item].toString ().equals ("say")) {
                    Hack_PromptForSpeech (behaviorConstruct);
                }
            }
        });
        AlertDialog alert = builder.create();

        // Verbalize creative scaffolding for context
        ArrayList<String> phrases = new ArrayList<String> ();
        phrases.add ("hi. what do you want me to do?");
        phrases.add ("choose one of these behaviors.");
        phrases.add ("do what?");
        phrases.add ("what're you thinking?");
        phrases.add ("tell me what to do");
        phrases.add ("which one?");
        phrases.add ("select a behavior");
        phrases.add ("adding a behavior");
        // Choose the phrase to verbalize. Default to random selection algorithm.
        // TODO: Choose the verbalization pattern based on the speed of interaction (metric for experience and comfort level).
        Random random = new Random();
        int phraseChoice = random.nextInt (phrases.size ());
        // Verbalize the phrase
        Hack_Speak (phrases.get (phraseChoice));
        // TODO: Adapt voice recognition to look for context-specfic speech.

        alert.show();

//        AlertDialog.Builder builderSingle = new AlertDialog.Builder(AppActivity.getAppContext ());
////        builderSingle.setIcon(R.drawable.ic_launcher);
//        builderSingle.setTitle("Select One Name:-");
//
//        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
//                AppActivity.getAppContext (),
//                android.R.layout.select_dialog_singlechoice);
//        arrayAdapter.add("Hardik");
//        arrayAdapter.add("Archit");
//        arrayAdapter.add("Jignesh");
//        arrayAdapter.add("Umang");
//        arrayAdapter.add("Gatti");
//
//        builderSingle.setNegativeButton (
//                "cancel",
//                new DialogInterface.OnClickListener () {
//                    @Override
//                    public void onClick (DialogInterface dialog, int which) {
//                        dialog.dismiss ();
//                    }
//                });
//
//        builderSingle.setAdapter (
//                arrayAdapter,
//                new DialogInterface.OnClickListener () {
//                    @Override
//                    public void onClick (DialogInterface dialog, int which) {
//                        String strName = arrayAdapter.getItem (which);
//                        AlertDialog.Builder builderInner = new AlertDialog.Builder (AppActivity.getAppContext ());
//                        builderInner.setMessage (strName);
//                        builderInner.setTitle ("Your Selected Item is");
//                        builderInner.setPositiveButton (
//                                "DONE",
//                                new DialogInterface.OnClickListener () {
//                                    @Override
//                                    public void onClick (
//                                            DialogInterface dialog,
//                                            int which) {
//                                        dialog.dismiss ();
//                                    }
//                                });
////                        builderInner.create ();
//                        builderInner.show ();
//                    }
//                });
////        builderSingle.create();
//        builderSingle.show();
    }



//    public class HttpRequestTask extends AsyncTask<String, Void, String[]> { // Extend AsyncTask and use void generics (for now)
//
//        private final String LOG_TAG = HttpRequestTask.class.getSimpleName();
//
//        @Override
//        protected String[] doInBackground(String... params) {
//            /* Get weather data from an Internet source. */
//
//            if (params.length == 0) {
//                return null;
//            }
//
//            // These two need to be declared outside the try/catch
//            // so that they can be closed in the finally block.
//            HttpURLConnection urlConnection = null;
//            BufferedReader reader = null;
//
//            // Will contain the raw JSON response as a string.
//            String responseJsonString = null;
//            String[] responseData;
//
////            String format = "json";
////            String units = "metric";
////            int numDays = 7;
//            String httpRequestMethod = "GET";
//            String content = params[0];
//
//            try {
//
//                // Construct the URL for the HTTP request
//                // TODO: IP_ADDRESS_PARAM = "ipAddress";
//                final String CLAY_UNIT_BASE_URL = "http://192.168.0.113/message?";
//                final String CONTENT_PARAM = "content";
////                final String FORMAT_PARAM = "mode";
////                final String UNITS_PARAM = "units";
////                final String DAYS_PARAM = "cnt";
//
//                // This approach enables the user to set the zip code from the settings activity.
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity()); // Get preferences for this activity
//                String remoteHostUri = prefs.getString(getString(R.string.pref_remote_host_key), // If there's a value stored for the location key in preferences, use it...
//                        getString(R.string.pref_remote_host_default));
//
//                // Build the URI. In doing so, replace the " " space character with "%20" string. This is done by Uri.parse().
//                Uri builtUri = Uri.parse(CLAY_UNIT_BASE_URL).buildUpon()
//                        .appendQueryParameter(CONTENT_PARAM, content)
//                        .build();
//                URL url = new URL(builtUri.toString());
//                Log.v(LOG_TAG, "SENDING REQUEST TO: " + url.toString());
//
//                // Create the request to OpenWeatherMap, and open the connection
//                urlConnection = (HttpURLConnection) url.openConnection();
//                urlConnection.setRequestMethod(httpRequestMethod);
//                urlConnection.connect();
//
//                // Read the input stream into a String
//                InputStream inputStream = urlConnection.getInputStream();
//                StringBuffer buffer = new StringBuffer();
//                if (inputStream == null) {
//                    // Nothing to do.
//                    return null;
//                }
//                reader = new BufferedReader(new InputStreamReader(inputStream));
//
//                String line;
//                while ((line = reader.readLine()) != null) {
//                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
//                    // But it does make debugging a *lot* easier if you print out the completed
//                    // buffer for debugging.
//                    buffer.append(line + "\n");
//                }
//
//                if (buffer.length() == 0) {
//                    // Stream was empty.  No point in parsing.
//                    return null;
//                }
//                responseJsonString = buffer.toString();
//            } catch (IOException e) {
//                Log.e(LOG_TAG, "Error ", e);
//                // If the code didn't successfully get the weather data, there's no point in attemping
//                // to parse it.
//                return null;
//            } finally{
//                if (urlConnection != null) {
//                    urlConnection.disconnect();
//                }
//                if (reader != null) {
//                    try {
//                        reader.close();
//                    } catch (final IOException e) {
//                        Log.e(LOG_TAG, "Error closing stream", e);
//                    }
//                }
//            }
//
//            // All this just for: return getWeatherDataFromJson(forecastJsonStr, numDays);
//            // TODO: Update this to return a string? or JSON? or Behavior?
//            try {
//                // TODO: return responseJsonString;
//                return getWeatherDataFromJson(responseJsonString, numDays);
//            } catch (JSONException e) {
//                Log.e(LOG_TAG, e.getMessage(), e);
//                e.printStackTrace();
//            }
//
//            // This only happens if there was an error getting or parsing the forecast.
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String[] result) {
//            if (result != null) {
//                // TODO: Add units to the list of units.
//                // TODO: Update loops based on current behavior reported by units.
//                // TODO: Add behaviors to the behavior repository.
//                httpRequestAdapter.clear();
//                for (String dayForecastStr : result) {
//                    httpRequestAdapter.add(dayForecastStr);
//                }
//                // New day is back from the server at this point!
//
//                // NOTE: Array adapter internally calls: adapter.notifyDataSetChanged()
//            }
//        }
//
//        /* The date/time conversion code is going to be moved outside the asynctask later,
//         * so for convenience we're breaking it out into its own method now.
//         */
//        private String getReadableDateString(long time){

    @Override
    protected void onDestroy () {
        super.onDestroy ();
        speaker.destroy();
    }
}
