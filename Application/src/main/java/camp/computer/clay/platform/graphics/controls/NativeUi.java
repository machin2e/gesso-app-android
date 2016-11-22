package camp.computer.clay.platform.graphics.controls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.text.Html;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HttpsURLConnection;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.system.RenderSystem;
import camp.computer.clay.model.Action;
import camp.computer.clay.model.Process;
import camp.computer.clay.model.Script;
import camp.computer.clay.model.configuration.Configuration;
import camp.computer.clay.platform.Application;
import camp.computer.clay.platform.R;
import camp.computer.clay.platform.util.ViewGroupHelper;

public class NativeUi {

    private Application application = null;
    private Context context = null;

    public NativeUi(Application application) {
        this.application = application;
        this.context = application.getApplicationContext();
    }

    // TODO: Replace OnActionListener with Action?
    public interface OnActionListener<T> {
        void onComplete(T result);
    }

    public void promptAcknowledgment(final OnActionListener onActionListener) {
        Application.getView().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(application.getView())
                        .setTitle("Notice")
                        .setMessage("The extension already has a profile.")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // continue with delete
                                onActionListener.onComplete(null);
                            }
                        })
//                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener()
//                {
//                    public void onClick(DialogInterface dialog, int which)
//                    {
//                        // do nothing
//                    }
//                })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }

    public void promptInputText(final OnActionListener onActionListener) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(application.getView());
        builder.setTitle("Create ExtensionEntity");

        // Set up the input
        final EditText input = new EditText(application.getView());

        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        // Add input to view
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onActionListener.onComplete(input.getText().toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO: Callback with "Cancel" action
                dialog.cancel();
            }
        });

        Application.getView().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                builder.show();
            }
        });
    }

    // TODO: public <T> void promptSelection(List<T> options, OnActionListener onActionListener) {
    public <T> void promptSelection(final List<Configuration> options, final OnActionListener onActionListener) {

        // Items
//        List<String> options = new ArrayList<>();
//        options.add("Servo");
//        options.add("Servo with Analog Feedback");
//        options.add("IR Rangefinder");
//        options.add("Ultrasonic Rangefinder");
//        options.add("Stepper Motor");

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(application.getView());
        // dialogBuilder.setIcon(R.drawable.ic_launcher);
        dialogBuilder.setTitle("What do you want to connect?");

        // Add data adapter
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                application.getView(),
                android.R.layout.select_dialog_item
        );

//        // Add data to adapter. These are the options.
//        for (int i = 0; i < options.size(); i++) {
//            arrayAdapter.add(options.get(i));
//        }

        // Add Profiles from Repository
        for (int i = 0; i < options.size(); i++) {
//            Configuration extensionProfile = getClay().getConfigurations().get(i);
//            options.add(extensionProfile.getLabel());
            arrayAdapter.add(options.get(i).getLabel());
        }

        // Profiles from Inventory
        arrayAdapter.add("Add from Inventory");

        // Apply the adapter to the dialog
        dialogBuilder.setAdapter(arrayAdapter, null);

        /*
        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        */

        Application.getView().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final AlertDialog dialog = dialogBuilder.create();

                dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String selectionLabel = arrayAdapter.getItem(position);
                        Configuration selection = options.get(position);

                        // Configure based on Configuration
                        // Add Ports based on Configuration
                        onActionListener.onComplete(selection);
//                while (selection.getPortCount() < position + 1) {
//                    selection.addPort(new PortEntity());
//                }

                        // Response
                /*
                AlertDialog.Builder builderInner = new AlertDialog.Builder(appContext);
                builderInner.setMessage(selectionLabel);
                builderInner.setTitle("Connecting patch");
                builderInner.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                            }
                        });

                dialog.dismiss();
                builderInner.show();
                */

                        dialog.dismiss();

                        promptTasks();
                    }
                });

                dialog.show();
            }
        });
    }

    // Break multi-updateImage tasks up into a sequence of floating interface elements that must be completed to continue (or abandon the sequence)
    // displayFloatingTaskDialog(<task list>, <task updateImage to display>)

    public void promptTasks() {

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(application.getView());
        // builderSingle.setIcon(R.drawable.ic_launcher);
        dialogBuilder.setTitle("Complete these steps to assemble");

        // TODO: Difficulty
        // TODO: Average Clock

        // Create data adapter
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                application.getView(),
                android.R.layout.select_dialog_multichoice
        );

        // Add data to adapter
        arrayAdapter.add("Task 1");
        arrayAdapter.add("Task 2");
        arrayAdapter.add("Task 3");
        arrayAdapter.add("Task 4");
        arrayAdapter.add("Task 5");

        final Context appContext = application.getView();

        /*
        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        */

        // Positive button
        dialogBuilder.setPositiveButton(
                "Start",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

//                        String strName = arrayAdapter.getItem(which);

                        // Response
                        /*
                        AlertDialog.Builder builderInner = new AlertDialog.Builder(appContext);
                        builderInner.setMessage(strName);
                        builderInner.setTitle("Connecting patch");
                        builderInner.setPositiveButton(
                                "Ok",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(
                                            DialogInterface dialog,
                                            int which) {
                                        dialog.dismiss();
                                    }
                                });

                        dialog.dismiss();
                        builderInner.show();
                        */

//                        promptTasks();


                        promptTask();
                    }
                }
        );

        // Set data adapter
        dialogBuilder.setAdapter(
                arrayAdapter,
                null
        );


        AlertDialog dialog = dialogBuilder.create();

        dialog.getListView().setItemsCanFocus(false);
        dialog.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Manage selected items here
                System.out.println("clicked" + position);
                CheckedTextView textView = (CheckedTextView) view;
                if (textView.isChecked()) {

                } else {

                }
            }
        });

        dialog.show();
    }

    public void promptTask() {

        // Items
        List<String> options = new ArrayList<>();
        options.add("Task 1");

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(application.getView());
        // dialogBuilder.setIcon(R.drawable.ic_launcher);
        dialogBuilder.setTitle("Do this task");

        // Add data adapter
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                application.getView(),
                android.R.layout.select_dialog_singlechoice
        );

        // Add data to adapter. These are the options.
        for (int i = 0; i < options.size(); i++) {
            arrayAdapter.add(options.get(i));
        }

        // Apply the adapter to the dialog
        dialogBuilder.setAdapter(arrayAdapter, null);

        /*
        // "Back" Button
        builderSingle.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        */

        final AlertDialog dialog = dialogBuilder.create();

        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String itemLabel = arrayAdapter.getItem(position);

                // Response
                /*
                AlertDialog.Builder builderInner = new AlertDialog.Builder(appContext);
                builderInner.setMessage(strName);
                builderInner.setTitle("Connecting patch");
                builderInner.setPositiveButton(
                        "Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(
                                    DialogInterface dialog,
                                    int which) {
                                dialog.dismiss();
                            }
                        });

                dialog.dismiss();
                builderInner.show();
                */

                dialog.dismiss();

                promptTask();
            }
        });

        dialog.show();
    }

    public int dpToPx(float dp) {

        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );

        return px;
    }

    public int mmToPx(float mm) {

        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_MM,
                mm,
                r.getDisplayMetrics()
        );

        return px;
    }

    public int inToPx(float in) {

        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_IN,
                in,
                r.getDisplayMetrics()
        );

        return px;
    }

    public void openActionEditor(Entity extension) {

        process.clear();

        createActionEditor_v3(extension);
    }

    public void createActionEditor_v3(final Entity extension) {

        // TODO: Hack into the JS engine in V8 to execute this pure JS. Fuck it.

        // NOTE: This is just a list of edit boxes. Each with a dropdown to save new script or load from the list. MVP, bitches.

        // Cache Action and Script in Repository. Retrieve Actions and Scripts from Remote Server.
        new HttpGetRequestTask().execute("http://stackoverflow.com");

        // Get list of Ports connected to Extension
        String portTypesString = "";
        final Group<Entity> ports = Portable.getPorts(extension);
        for (int i = 0; i < ports.size(); i++) {
            Entity port = ports.get(i);
            Log.v("PortType", "port type: " + Port.getType(port));
            portTypesString += Port.getType(port) + " ";
        }

        final String finalPortTypesString = portTypesString;
        Application.getView().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final RelativeLayout relativeLayout = new RelativeLayout(context);
                relativeLayout.setBackgroundColor(Color.parseColor("#bb000000"));
                // TODO: set layout_margin=20dp

                // Background Event Handler
                relativeLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        relativeLayout.setVisibility(View.GONE);
                        return true;
                    }
                });

                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                relativeLayout.addView(linearLayout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                // Title: "Actions"
                TextView textView = new TextView(context);
                textView.setText("Extension Controller");
                textView.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
                textView.setTextSize(20);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(textView);

                LinearLayout ll = new LinearLayout(context);
                ll.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams params7 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params7.setMargins(0, dpToPx(5), 0, 0);

                // Button: "Import Data Source"
                Button button8 = new Button(context);
                button8.setText("Find Data"); // i.e., Formerly "Add Data Source". i.e., [Project][Internet][Generator].
                button8.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
                button8.setBackgroundColor(Color.parseColor("#44000000"));
                button8.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

                LinearLayout.LayoutParams params6 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params6.setMargins(0, dpToPx(5), 0, 0);
                params6.weight = 1;
                button8.setLayoutParams(params6);
                ll.addView(button8);

                Button button9 = new Button(context);
                button9.setText("\u2716"); // i.e., [Project][Internet][Generator]
                button9.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
                button9.setBackgroundColor(Color.parseColor("#44000000"));
                button9.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                button9.setWidth(50);

                LinearLayout.LayoutParams params9 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params9.setMargins(0, dpToPx(5), 0, 0);
//                params6.weight = 0;
                button9.setLayoutParams(params9);
                ll.addView(button9);

                linearLayout.addView(ll, params7);

                // Layout (Linear Vertical): Action List
                final LinearLayout linearLayout2 = new LinearLayout(context);
                linearLayout2.setOrientation(LinearLayout.VERTICAL);
                linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                // Default Action Controller based on Port Configuration
                /*
                EditText defaultActionBasedOnPortConfiguration = (EditText) createActionView_v1();
                defaultActionBasedOnPortConfiguration.setText("PLACEHOLDER: " + finalPortTypesString);
                linearLayout.addView(defaultActionBasedOnPortConfiguration);
                */

                LinearLayout portableLayout = new LinearLayout(context);
                portableLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.addView(portableLayout);

                // Ports
                /*
                for (int i = 0; i < ports.size(); i++) {
                    EditText button6 = new EditText(context);

                    button6.setHint("" + Port.getType(ports.get(i)));
                    // Digital/Switch: on/off [binary selector]
                    // PWM/Pulse: period, duty cycle [analog sliders in bounded range] (alt: sine wave parameters)
                    // ADC/Wave: ADC value [analog slider in ADC range] (alt: amplitude)

                    button6.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
                    button6.setBackgroundColor(Color.parseColor(camp.computer.clay.util.Color.getColor(Port.getType(ports.get(i)))));

                    LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params4.setMargins(0, dpToPx(5), 0, 0);
                    button6.setLayoutParams(params4);

                    portableLayout.addView(button6);
                }
                */


                // Button: "Search for Action"
                LinearLayout searchLayout = new LinearLayout(context);
                searchLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.addView(searchLayout);

                /*
                EditText searchBox = new EditText(context);
                searchBox.setHint("Search for Action");
                searchBox.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
                searchBox.setBackgroundColor(Color.parseColor("#44000000"));

                LinearLayout.LayoutParams params8 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params8.setMargins(0, dpToPx(5), 0, 0);
                searchBox.setLayoutParams(params8);

                Button cancelSearchButton = new Button(context);
                cancelSearchButton.setText("X");
                cancelSearchButton.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
                cancelSearchButton.setBackgroundColor(Color.parseColor("#44000000"));

                searchLayout.addView(searchBox);

                TextView browseActionEventView = (TextView) createTextView("\uD83D\uDD0D", 1);
                searchLayout.addView(browseActionEventView);
                */

                // Button: "Add Action"
                Button button2 = new Button(context);
                button2.setText("Add Action");
                button2.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
                button2.setBackgroundColor(Color.parseColor("#44000000"));

                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params3.setMargins(0, dpToPx(5), 0, 0);
                button2.setLayoutParams(params3);
                searchLayout.addView(button2);

                button2.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent motionEvent) {

                        int pointerIndex = ((motionEvent.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT);
                        int pointerId = motionEvent.getPointerId(pointerIndex);
                        //int touchAction = (motionEvent.getEvent () & MotionEvent.ACTION_MASK);
                        int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);
                        int pointCount = motionEvent.getPointerCount();

                        // Update the state of the touched object based on the current pointerCoordinates interaction state.
                        if (touchActionType == MotionEvent.ACTION_DOWN) {
                            // TODO:
                        } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                            // TODO:
                        } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                            // TODO:
                        } else if (touchActionType == MotionEvent.ACTION_UP) {

                            View actionEditorView = createActionView(extension);
//                            linearLayout2.addView(actionEditorView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                            linearLayout2.addView(actionEditorView);

                        } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                            // TODO:
                        } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                            // TODO:
                        } else {
                            // TODO:
                        }

                        return true;
                    }
                });

                for (int i = 0; i < ports.size(); i++) {

                    if (Port.getType(ports.get(i)) == Port.Type.SWITCH) {
                        // <DIGITAL_PORT_CONTROL>
                        View digitalPortLayout = createSwitchControllerView(ports.get(i));
                        linearLayout.addView(digitalPortLayout);
                        // </DIGITAL_PORT_CONTROL>
                    }

                    if (Port.getType(ports.get(i)) == Port.Type.PULSE) {
                        // <PWM_PORT_CONTROL>
                        View pulsePortLayout = createPulseControllerView(ports.get(i));
                        linearLayout.addView(pulsePortLayout);
                        // </PWM_PORT_CONTROL>
                    }

                    if (Port.getType(ports.get(i)) == Port.Type.WAVE) {
                        // <ADC_PORT_CONTROL>
                        View wavePortLayout = createWaveControllerView(ports.get(i));
                        linearLayout.addView(wavePortLayout);
                        // </ADC_PORT_CONTROL>
                    }
                }

                // Button: Test Script
                Button button3 = new Button(context);
                button3.setText("Test");
                button3.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
                button3.setBackgroundColor(Color.parseColor("#44000000"));

                button3.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent motionEvent) {

                        int pointerIndex = ((motionEvent.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT);
                        int pointerId = motionEvent.getPointerId(pointerIndex);
                        //int touchAction = (motionEvent.getEvent () & MotionEvent.ACTION_MASK);
                        int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);
                        int pointCount = motionEvent.getPointerCount();

                        // Update the state of the touched object based on the current pointerCoordinates interaction state.
                        if (touchActionType == MotionEvent.ACTION_DOWN) {
                            // TODO:
                        } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                            // TODO:
                        } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                            // TODO:
                        } else if (touchActionType == MotionEvent.ACTION_UP) {

                            List<Action> actions = process.getActions();
                            for (int i = 0; i < actions.size(); i++) {
                                Log.v("ActionProcess", "" + i + ": " + actions.get(i).getTitle());
                            }

                            // Send complete scripts to Hosts
                            new HttpPostRequestTask().execute("http://stackoverflow.com");

                        } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                            // TODO:
                        } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                            // TODO:
                        } else {
                            // TODO:
                        }

                        return true;
                    }
                });

                LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params4.setMargins(0, dpToPx(5), 0, 0);
                button3.setLayoutParams(params4);
                linearLayout.addView(button3);

                // Add to main Application View
                FrameLayout frameLayout = (FrameLayout) Application.getView().findViewById(R.id.application_view);
                frameLayout.addView(relativeLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
        });
    }

    public interface SelectEventHandler<T> {
        void execute(T selection);
    }

    public interface LabelMapper<T> {
        String map(T element);
    }

    public View createListView(final List listData, final LabelMapper labelMapper, final SelectEventHandler selectEventHandler) {

        Application.getView().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final int listViewId = generateViewId();

                final RelativeLayout containerLayout = new RelativeLayout(context);
                containerLayout.setId(listViewId);
                containerLayout.setBackgroundColor(Color.parseColor("#bb000000"));
                // TODO: set layout_margin=20dp

                // Background Event Handler
                containerLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        containerLayout.setVisibility(View.GONE);
                        return true;
                    }
                });

                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                containerLayout.addView(linearLayout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                // Title: "Choose"
                TextView textView = new TextView(context);
                textView.setText("Choose");
                textView.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
                textView.setTextSize(20);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(textView);

                // Create labels for data
                ArrayList<String> dataLabels = new ArrayList<>();
                for (int i = 0; i < listData.size(); i++) {
                    String label = labelMapper.map(listData.get(i));
                    dataLabels.add(label);
                }

                LinearLayout ll = new LinearLayout(context);
                ListView listView = new ListView(context);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, dataLabels);
                listView.setAdapter(adapter);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        selectEventHandler.execute(listData.get(i));

                        // <REMOVE_VIEW>
                        View containerView = Application.getView().findViewById(listViewId);
                        ((ViewManager) containerView.getParent()).removeView(containerView);
                        // </REMOVE_VIEW>
                    }
                });

                ll.addView(listView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                linearLayout.addView(ll);

                // Add to main Application View
                FrameLayout frameLayout = (FrameLayout) Application.getView().findViewById(R.id.application_view);
                frameLayout.addView(containerLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
        });

        return null;
    }

    class HttpGetRequestTask extends AsyncTask<String, String, String> {

        String serverUri = "http://192.168.1.2:8001/repository/actions";
        String response = "";

        @Override
        protected String doInBackground(String... uri) {
            Log.v("HTTPResponse", "HttpGetRequestTask");
            String responseString = null;
            try {
                URL url = new URL(serverUri);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                if (httpConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    // Do normal input or output stream reading
                    Log.v("HTTPResponse", "HTTP response: " + httpConnection.getResponseCode());

//                    BufferedReader br;
//                    if (200 <= httpConnection.getResponseCode() && httpConnection.getResponseCode() <= 299) {
//                        br = new BufferedReader(new InputStreamReader((httpConnection.getInputStream())));
//                    } else {
//                        br = new BufferedReader(new InputStreamReader((httpConnection.getErrorStream())));
//                    }
//
//                    int bytesRead = -1;
//                    char[] buffer = new char[1024];
//                    while ((bytesRead = br.read(buffer)) >= 0) {
//                        // process the buffer, "bytesRead" have been read, no more, no less
//                    }
//
//                    Log.v("HTTPResponse", "HTTP GET response: " + buffer.toString());

                    InputStream is = httpConnection.getInputStream();
                    int ch;
                    StringBuffer sb = new StringBuffer();
                    while ((ch = is.read()) != -1) {
                        sb.append((char) ch);
                    }
                    String jsonString = sb.toString();
                    Log.v("HTTPResponse", "HTTP GET response: " + jsonString);


                    // Create JSON object from file contents
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(jsonString);

                        JSONArray actionsArray = jsonObject.getJSONArray("actions");
//                        String hostTitle = actionsArray.getString("title");

                        for (int i = 0; i < actionsArray.length(); i++) {
                            JSONObject actionObject = actionsArray.getJSONObject(i);
                            String type = actionObject.getString("type");
                            String actionTitle = actionObject.getString("title");
                            String actionScript = actionObject.getString("script");

                            Log.v("HTTPResponse", "^^^");
                            Log.v("HTTPResponse", "type: " + type);
                            Log.v("HTTPResponse", "action title: " + actionTitle);
                            Log.v("HTTPResponse", "action script: " + actionScript);
                            Log.v("HTTPResponse", "---");

                            // Cache Action and Script in Repository. Retrieve Actions and Scripts from Remote Server.
                            // TODO: Create event in global event queue to fetch and cache this data when Builder loads! It shouldn't be happenin' in UI codez!!
                            World.getWorld().repository.createTestAction(actionTitle, actionScript);
                        }

                        // HostEntity host = new HostEntity();

//                        Log.v("Configuration", "reading JSON name: " + hostTitle);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    response = "FAILED"; // See documentation for more info on response handling
                }
            } catch (Exception e) {
                //TODO Handle problems..
            }
            Log.v("HTTPResponse", "HTTP response: " + responseString);
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Do anything with response..
        }
    }

    // TODO: 11/21/2016 Add HttpPostRequestTask to global TCP/UDP communications queue to server.
    class HttpPostRequestTask extends AsyncTask<String, String, String> {

        String serverUri = "http://192.168.1.2:8001/jsonPost";
        String response = "";

        @Override
        protected String doInBackground(String... uri) {
            String responseString = null;
            try {
                URL url = new URL(serverUri);
                HttpURLConnection httpConnection = (HttpURLConnection) url.openConnection();
                httpConnection.setRequestMethod("POST");// type of request
                httpConnection.setRequestProperty("Content-Type", "application/json");//some header you want to add
//                httpConnection.setRequestProperty("Authorization", "key=" + AppConfig.API_KEY);//some header you want to add
                httpConnection.setDoOutput(true);

//                ObjectMapper mapper = new ObjectMapper();
//                mapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
                DataOutputStream dataOutputStream = new DataOutputStream(httpConnection.getOutputStream());
                //content is the object you want to send, use instead of NameValuesPair
//                mapper.writeValue(dataOutputStream, content);

                String processActionScripts = "{ \"type\": \"process\", \"actions\": [";
                List<Action> actions = process.getActions();
                for (int i = 0; i < actions.size(); i++) {
                    processActionScripts += ""
                            + "{"
                            + "\"title\": \"" + actions.get(i).getTitle() + "\","
                            + "\"script\": \"" + actions.get(i).getScript().getCode() + "\""
                            + "}";

                    if (i < (actions.size() - 1)) {
                        processActionScripts += ", ";
                    }
                }
                processActionScripts += "] }";

                //httpConnection.getOutputStream().write("{ \"type\": \"Action\", \"script_uuid\": \"08edbf0a-b020-11e6-80f5-76304dec7eb7\" }".getBytes());
                Log.v("HttpPostRequestTask", "HTTP POST: " + processActionScripts);
                httpConnection.getOutputStream().write(processActionScripts.getBytes());
                dataOutputStream.flush();
                dataOutputStream.close();

                if (httpConnection.getResponseCode() == HttpsURLConnection.HTTP_OK) {
                    // Do normal input or output stream reading
                    Log.v("HTTPResponse", "HTTP response: " + httpConnection.getResponseCode());
                } else {
                    response = "FAILED"; // See documentation for more info on response handling
                }
            } catch (Exception e) {
                //TODO Handle problems..
            }
            Log.v("HTTPResponse", "HTTP response: " + responseString);
            return responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //Do anything with response..
        }
    }

    private View createSwitchControllerView(Entity port) {
        return createSwitchControllerView_v1(port);
    }

    private View createPulseControllerView(Entity port) {
        return createPulseControllerView_v1(port);
    }

    private View createWaveControllerView(Entity port) {
        return createWaveControllerView_v1(port);
    }

    private View createSwitchControllerView_v1(Entity port) {

        // TODO: Add dropdown with available data sources to choose from.
        // TODO: Expose manual controls when selected.

        LinearLayout containerLayout = new LinearLayout(context);
        containerLayout.setOrientation(LinearLayout.VERTICAL);

        View selectorView = createSelector("Select Data Controller", new RequestDataTask<String>() {
            @Override
            public List<String> execute() {
                List<Action> actions = process.getActions();

                final List<String> actionTitles = new ArrayList<>();
                for (int i = 0; i < actions.size(); i++) {
                    actionTitles.add(actions.get(i).getTitle());
                }
                return actionTitles;
            }
        });
        containerLayout.addView(selectorView);
        containerLayout.setBackgroundColor(Color.parseColor(camp.computer.clay.util.Color.getColor(Port.Type.SWITCH)));

        LinearLayout digitalPortLayout = new LinearLayout(context);
        digitalPortLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams digitalLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        digitalLayoutParams.setMargins(0, dpToPx(5), 0, 0);
        digitalPortLayout.setLayoutParams(digitalLayoutParams);

        digitalPortLayout.setBackgroundColor(Color.parseColor(camp.computer.clay.util.Color.getColor(Port.Type.SWITCH)));

        // Digital Label
        View digitalText = createTextView("Switch", 2);
        digitalPortLayout.addView(digitalText);

        // Text: "Data Sources (Imports)"
        final EditText digitalValue = new EditText(context);
        digitalValue.setTextSize(11.0f);
        digitalValue.setHint("[on|off]");
        digitalValue.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
        digitalValue.setBackgroundColor(Color.parseColor("#44000000"));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dpToPx(5), 0, 0);
        digitalValue.setLayoutParams(params);
        digitalPortLayout.addView(digitalValue);

        containerLayout.addView(digitalPortLayout);

        return containerLayout;
    }

    private View createPulseControllerView_v1(Entity port) {

        LinearLayout pulsePortLayout = new LinearLayout(context);
        pulsePortLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams pulseLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        pulseLayoutParams.setMargins(0, dpToPx(5), 0, 0);
        pulsePortLayout.setLayoutParams(pulseLayoutParams);

        pulsePortLayout.setBackgroundColor(Color.parseColor(camp.computer.clay.util.Color.getColor(Port.Type.PULSE)));

        // Pulse Label
        View pulseText = createTextView("Pulse", 2);
        pulsePortLayout.addView(pulseText);

        // Pulse Value (Duty Cycle)
        final EditText pulseDutyCycleValue = new EditText(context);
        pulseDutyCycleValue.setTextSize(11.0f);
        pulseDutyCycleValue.setHint("on time (duty cycle)");
        pulseDutyCycleValue.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
        pulseDutyCycleValue.setBackgroundColor(Color.parseColor("#44000000"));

        LinearLayout.LayoutParams pulseParams1 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        pulseParams1.setMargins(0, dpToPx(5), 0, 0);
        pulseDutyCycleValue.setLayoutParams(pulseParams1);
        pulsePortLayout.addView(pulseDutyCycleValue);

        // Pulse Value (Period)
        final EditText pulsePeriodValue = new EditText(context);
        pulsePeriodValue.setTextSize(11.0f);
        pulsePeriodValue.setHint("interval (period)");
        pulsePeriodValue.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
        pulsePeriodValue.setBackgroundColor(Color.parseColor("#44000000"));

        LinearLayout.LayoutParams pulseParams2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        pulseParams2.setMargins(0, dpToPx(5), 0, 0);
        pulsePeriodValue.setLayoutParams(pulseParams2);
        pulsePortLayout.addView(pulsePeriodValue);

        return pulsePortLayout;
    }

    private View createWaveControllerView_v1(Entity port) {

        LinearLayout wavePortLayout = new LinearLayout(context);
        wavePortLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams waveLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        waveLayoutParams.setMargins(0, dpToPx(5), 0, 0);
        wavePortLayout.setLayoutParams(waveLayoutParams);

        wavePortLayout.setBackgroundColor(Color.parseColor(camp.computer.clay.util.Color.getColor(Port.Type.WAVE)));

        // Wave Label
        View waveText = createTextView("Wave", 2);
        wavePortLayout.addView(waveText);

        // Text: "Data Sources (Imports)"
        final EditText waveValue = new EditText(context);
        waveValue.setTextSize(11.0f);
        waveValue.setHint("amplitude (voltage/ADC)");
        waveValue.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
        waveValue.setBackgroundColor(Color.parseColor("#44000000"));

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params2.setMargins(0, dpToPx(5), 0, 0);
        waveValue.setLayoutParams(params2);
        wavePortLayout.addView(waveValue);

        return wavePortLayout;
    }

    private View createActionView_v1() {

//        final TextView actionView = new TextView(context);
//        actionView.setText("Event (<PortEntity> <PortEntity> ... <PortEntity>)\nExpose: <PortEntity> <PortEntity> ... <PortEntity>");
//        int horizontalPadding = (int) Application.getView().convertDipToPx(20);
//        int verticalPadding = (int) Application.getView().convertDipToPx(10);
//        actionView.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
//        actionView.setBackgroundColor(Color.parseColor("#44000000"));

        // Text: "Data Sources (Imports)"
        final EditText actionView = new EditText(context);
        actionView.setTextSize(11.0f);
        actionView.setHint("TODO: <describe to search scripts>");
        actionView.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
        actionView.setBackgroundColor(Color.parseColor("#44000000"));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dpToPx(5), 0, 0);
        actionView.setLayoutParams(params);

        // final LinearLayout actionList = (LinearLayout) view;

        /*
        actionView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    actionList.removeView(actionView);

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                    // TODO:
                } else {
                    // TODO:
                }

                return true;
            }
        });
        */

        // actionList.addView(actionView);

        return actionView;
    }

    private View createActionView_v2() {

        // Text: "Data Sources (Imports)"
        final TextView actionView = new TextView(context);
        actionView.setTextSize(11.0f);
        actionView.setText("TODO: <describe to search scripts>");
        actionView.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
        actionView.setBackgroundColor(Color.parseColor("#44000000"));

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(0, dpToPx(5), 0, 0);
        actionView.setLayoutParams(params);

        // final LinearLayout actionList = (LinearLayout) view;

        actionView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    createScriptEditorView_v1();

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                    // TODO:
                } else {
                    // TODO:
                }

                return true;
            }
        });

        // actionList.addView(actionView);

        return actionView;
    }

    private Process process = new Process();

    private View createActionView(final Entity extension) {
        return createActionView_v3(extension);
    }

    private View createActionView_v3(final Entity extension) {

        final LinearLayout containerLayout = new LinearLayout(context);
        containerLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        containerLayout.setLayoutParams(layoutParams);

        TextView browseActionEventView, scriptEditorEventView, playPauseEventView, moveUpEventView, moveDownEventView, removeEventView;

        browseActionEventView = (TextView) createTextView("\uD83D\uDD0D", 1);
        browseActionEventView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    final List<Action> actions = World.getWorld().repository.getActions();

                    createListView(
                            actions,
                            new LabelMapper<Action>() {
                                @Override
                                public String map(Action action) {
                                    return action.getTitle();
                                }
                            },
                            new SelectEventHandler<Action>() {
                                @Override
                                public void execute(Action selectedAction) {
                                    //Toast.makeText(getBaseContext(), ""+arg2,     Toast.LENGTH_SHORT).show();
                                    Log.v("ListView", "selected: " + selectedAction.getTitle());

                                    // Add Action to Process
                                    process.addAction(selectedAction);

                                    // Replace with new View
                                    View actionView = createActionView_v1(selectedAction);
                                    ViewGroupHelper.replaceView(containerLayout, actionView);
                                }
                            }
                    );


//                    final Spinner selectorView = (Spinner) createSelector("Select Action", new RequestDataTask<String>() {
//                        @Override
//                        public List<String> execute() {
//                            final List<Action> actions = World.getWorld().repository.getActions();
//
//                            final List<String> repositoryActionTitles = new ArrayList<>();
//                            for (int i = 0; i < actions.size(); i++) {
//                                repositoryActionTitles.add(actions.get(i).getTitle());
//                            }
//                            return repositoryActionTitles;
//                        }
//                    });
//                    selectorView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                            if (i > 0) {
//                                //String selectedText = spinnerArray.get(i);
//                                String selectedText = (String) selectorView.getAdapter().getItem(i);
//
//                                /*
//                                if (selectedText.equals("new script")) {
//
//                                }
//                                */
//
//                                final List<Action> actions = World.getWorld().repository.getActions();
//                                Action action = actions.get(i - 1);
//                                process.addAction(action);
//
//                                // Replace with new View
//                                View actionView = createActionView_v1(action);
//
//                                ViewGroupHelper.replaceView(selectorView, actionView);
//                            }
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> adapterView) {
//
//                        }
//                    });
//
////                    showView(selectorView);
//
//                    // Replace with new View
////                    View newActionView = createNewActionView_v3(extension);
//
//                    ViewGroupHelper.replaceView(containerLayout, selectorView);

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                    // TODO:
                } else {
                    // TODO:
                }

                return true;
            }
        });
        containerLayout.addView(browseActionEventView);

        scriptEditorEventView = (TextView) createTextView("NEW/EDIT", 1);
        ((LinearLayout.LayoutParams) scriptEditorEventView.getLayoutParams()).weight = 1.0f;
        containerLayout.addView(scriptEditorEventView);

        /*
        playPauseEventView = (TextView) createTextView("\u25ba", 1); // Play/Pause
        containerLayout.addView(playPauseEventView);

        moveUpEventView = (TextView) createTextView("\u25b2", 1); // Move Up
        containerLayout.addView(moveUpEventView);

        moveDownEventView = (TextView) createTextView("\u25bc", 1); // Move Down
        containerLayout.addView(moveDownEventView);
        */

        removeEventView = (TextView) createTextView("\u2716", 1); // Remove
        containerLayout.addView(removeEventView);

        // final LinearLayout actionList = (LinearLayout) view;

        scriptEditorEventView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    // Remove current View
                    //View containerView = Application.getView().findViewById(imageEditorId);
//                    View containerView = containerLayout;
//                    ((ViewManager) containerView.getParent()).removeView(containerView);

                    Script newScript = new Script();
                    newScript.setCode("// TODO: write script!");
                    World.getWorld().repository.addScript(newScript); // Add to repository

                    Action newAction = new Action();
                    newAction.setTitle("new action");
                    newAction.setScript(newScript); // Add to repository
                    World.getWorld().repository.addAction(newAction); // Add to repository

                    process.addAction(newAction);

                    // Replace with new View
                    View newActionView = createNewActionView_v3(newAction);

                    ViewGroupHelper.replaceView(containerLayout, newActionView);

                    openScriptEditor(newAction.getScript());

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                    // TODO:
                } else {
                    // TODO:
                }

                return true;
            }
        });

        removeEventView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    //View containerView = Application.getView().findViewById(imageEditorId);
                    View containerView = containerLayout;
                    ((ViewManager) containerView.getParent()).removeView(containerView);

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                    // TODO:
                } else {
                    // TODO:
                }

                return true;
            }
        });

        // actionList.addView(actionView);

        return containerLayout;
    }

    // TODO: Combine with createActionView(...)
    private View createNewActionView_v3(final Action action) {

        final LinearLayout containerLayout = new LinearLayout(context);
        containerLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        containerLayout.setLayoutParams(layoutParams);

        View view;

        view = createTextView("Action Title [Edit/Branch] [Remove]", 0);
        containerLayout.addView(view);

        // final LinearLayout actionList = (LinearLayout) view;

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    openScriptEditor(action.getScript());

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                    // TODO:
                } else {
                    // TODO:
                }

                return true;
            }
        });

        // actionList.addView(actionView);

        return containerLayout;
    }

    private View createActionView_v1(final Action action) {

        final LinearLayout containerLayout = new LinearLayout(context);
        containerLayout.setOrientation(LinearLayout.HORIZONTAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        containerLayout.setLayoutParams(layoutParams);

        View view;

        view = createTextView(action.getTitle(), 0);
        containerLayout.addView(view);

        // final LinearLayout actionList = (LinearLayout) view;

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    openScriptEditor(action.getScript());

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                    // TODO:
                } else {
                    // TODO:
                }

                return true;
            }
        });

        // actionList.addView(actionView);

        return containerLayout;
    }

    /*
    // <HELPERS>
    // ASCII format for Network Interchange (RFC20)
    // References:
    // - https://en.wikipedia.org/wiki/ASCII
    // - https://tools.ietf.org/html/rfc20
    public static final String ASCII_CODE_TAB = "&#09;";

    // HTML Character Entity References
    // Reference: https://www.w3.org/TR/html401/sgml/entities.html
    public static final String HTML_CHARACTER_NON_BREAKABLE_SPACE = "&nbsp;";
    public static final String HTML_CHARACTER_EN_SPACE = "&ensp;";
    public static final String HTML_CHARACTER_EM_SPACE = "&emsp;";
    public static final String HTML_CHARACTER_THIN_SPACE = "&thinsp;";
    // </HELPERS>
    */

    // <SETTINGS>
    public static float SCRIPT_EDITOR_TEXT_SIZE = 15.0f;
    public static float SCRIPT_EDITOR_LINE_SPACING_MULTIPLIER = 1.5f; // e.g., 1 for single spacing, 2 for double spacing, etc.
    public static float SCRIPT_EDITOR_LINE_SPACING = 0.0f;
    // </SETTINGS>

    public void openScriptEditor(Script script) {
        createScriptEditorView_v2(script);
    }

    public View createScriptEditorView_v1() {

        Application.getView().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final RelativeLayout relativeLayout = new RelativeLayout(context);
                relativeLayout.setBackgroundColor(Color.parseColor("#bb000000"));
                // TODO: set layout_margin=20dp

                // Background Event Handler
                relativeLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        relativeLayout.setVisibility(View.GONE);
                        return true;
                    }
                });

                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                relativeLayout.addView(linearLayout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                // Title: "Actions"
                TextView textView = new TextView(context);
                textView.setText("Script Editor");
                textView.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
                textView.setTextSize(20);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(textView);

                // EditText: Script Editor
                Typeface typeface = Typeface.createFromAsset(Application.getView().getAssets(), RenderSystem.NOTIFICATION_FONT);
                // Typeface boldTypeface = Typeface.create(typeface, Typeface.NORMAL);

                final EditText scriptEditorView = new EditText(context);
                scriptEditorView.setTypeface(typeface);
                scriptEditorView.setTextSize(SCRIPT_EDITOR_TEXT_SIZE);
                scriptEditorView.setLineSpacing(SCRIPT_EDITOR_LINE_SPACING, SCRIPT_EDITOR_LINE_SPACING_MULTIPLIER);
                // scriptEditorView.setHint("");
                String formattedScriptText = "" +
                        "<font color=\"#47a842\">var</font> action = <font color=\"#47a842\">function</font>(<font color=\"#3498db\">data</font>) {<br />" +
                        "\u0009<font color=\"#47a842\">print</font>(\"Hello World!\");<br />" +
                        "\u0009// <strong>TODO:</strong> Use Clay API to code action's script.</font><br />" +
                        "}<br /><br />" +
                        "<font color=\"#c5c5c5\">// <strong>NOTE:</strong> Refer to examples/tutorial below to get started.</font><br />" +
                        "<font color=\"#c5c5c5\">// <strong>NOTE:</strong> Cast to larger screen and attach keyboard.</font><br />" +
                        "<font color=\"#c5c5c5\">// <strong>NOTE:</strong> Open script and action editors on two screens.</font>";
                scriptEditorView.setText(Html.fromHtml(formattedScriptText));
                scriptEditorView.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
                scriptEditorView.setBackgroundColor(Color.parseColor("#44000000"));
                scriptEditorView.setGravity(Gravity.TOP | Gravity.LEFT);

                // scriptEditorView.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                scriptEditorView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                scriptEditorView.setVerticalScrollBarEnabled(true);
                scriptEditorView.setMovementMethod(ScrollingMovementMethod.getInstance());
                scriptEditorView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                params.setMargins(0, dpToPx(5), 0, 0);
                scriptEditorView.setLayoutParams(params);

                linearLayout.addView(scriptEditorView);

                // TODO: Add keyboard handler to EditText so a Bluetooth or USB keyboard can be used to write code (say, in combination with casting to a monitor or tablet).

                // Add to main Application View
                FrameLayout frameLayout = (FrameLayout) Application.getView().findViewById(R.id.application_view);
                frameLayout.addView(relativeLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
        });

        return null;
    }

    public View createScriptEditorView_v2(final Script script) {

        Application.getView().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final RelativeLayout relativeLayout = new RelativeLayout(context);
                relativeLayout.setBackgroundColor(Color.parseColor("#bb000000"));
                // TODO: set layout_margin=20dp

                // Background Event Handler
                relativeLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        relativeLayout.setVisibility(View.GONE);
                        return true;
                    }
                });

                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                relativeLayout.addView(linearLayout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                // Title: "Actions"
                TextView textView = new TextView(context);
                textView.setText("Script Editor");
                textView.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
                textView.setTextSize(20);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(textView);

                // EditText: Script Editor
                Typeface typeface = Typeface.createFromAsset(Application.getView().getAssets(), RenderSystem.NOTIFICATION_FONT);
                // Typeface boldTypeface = Typeface.create(typeface, Typeface.NORMAL);

                EditText scriptEditorView = new EditText(context);
                scriptEditorView.setTypeface(typeface);
                scriptEditorView.setTextSize(SCRIPT_EDITOR_TEXT_SIZE);
                scriptEditorView.setLineSpacing(SCRIPT_EDITOR_LINE_SPACING, SCRIPT_EDITOR_LINE_SPACING_MULTIPLIER);
                // scriptEditorView.setHint("");
                String formattedScriptText = "";
                if (script == null) {
                    formattedScriptText = "" +
                            "<font color=\"#47a842\">var</font> action = <font color=\"#47a842\">function</font>(<font color=\"#3498db\">data</font>) {<br />" +
                            "\u0009<font color=\"#47a842\">print</font>(\"Hello World!\");<br />" +
                            "\u0009// <strong>TODO:</strong> Use Clay API to code action's script.</font><br />" +
                            "}<br /><br />" +
                            "<font color=\"#c5c5c5\">// <strong>NOTE:</strong> Refer to examples/tutorial below to get started.</font><br />" +
                            "<font color=\"#c5c5c5\">// <strong>NOTE:</strong> Cast to larger screen and attach keyboard.</font><br />" +
                            "<font color=\"#c5c5c5\">// <strong>NOTE:</strong> Open script and action editors on two screens.</font>";
                } else {
                    formattedScriptText = script.getCode();
                }
                scriptEditorView.setText(Html.fromHtml(formattedScriptText));
                scriptEditorView.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
                scriptEditorView.setBackgroundColor(Color.parseColor("#44000000"));
                scriptEditorView.setGravity(Gravity.TOP | Gravity.LEFT);

                // scriptEditorView.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                scriptEditorView.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                scriptEditorView.setVerticalScrollBarEnabled(true);
                scriptEditorView.setMovementMethod(ScrollingMovementMethod.getInstance());
                scriptEditorView.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                params.setMargins(0, dpToPx(5), 0, 0);
                scriptEditorView.setLayoutParams(params);

                linearLayout.addView(scriptEditorView);

                // TODO: Add keyboard handler to EditText so a Bluetooth or USB keyboard can be used to write code (say, in combination with casting to a monitor or tablet).
                /*
                // <TODO:REPLACE_WITH_GLOBAL_INPUT_SYSTEM>
                scriptEditorView.setKeyListener(new KeyListener() {
                    @Override
                    public int getInputType() {
                        return 0;
                    }

                    @Override
                    public boolean onKeyDown(View view, Editable editable, int i, KeyEvent keyEvent) {
                        return false;
                    }

                    @Override
                    public boolean onKeyUp(View view, Editable editable, int keyCode, KeyEvent keyEvent) {
                        switch (keyCode) {
                            case KeyEvent.KEYCODE_ENTER:
                            case KeyEvent.KEYCODE_NUMPAD_ENTER: {
//                                scriptEditorView.getText().append("\n// TODO:");
//                                nativeUi.openSettings();
                                //your Action code
                                return true;
                            }
                        }
                        return false;
                    }

                    @Override
                    public boolean onKeyOther(View view, Editable editable, KeyEvent keyEvent) {
                        return false;
                    }

                    @Override
                    public void clearMetaKeyState(View view, Editable editable, int i) {

                    }
                });
                // </TODO:REPLACE_WITH_GLOBAL_INPUT_SYSTEM>
                */

                // Add to main Application View
                FrameLayout frameLayout = (FrameLayout) Application.getView().findViewById(R.id.application_view);
                frameLayout.addView(relativeLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
        });

        return null;
    }

    public void openSettings() {

        Application.getView().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final RelativeLayout relativeLayout = new RelativeLayout(context);
                relativeLayout.setBackgroundColor(Color.parseColor("#bb000000"));
                // TODO: set layout_margin=20dp

                // Background Event Handler
                relativeLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        relativeLayout.setVisibility(View.GONE);
                        return true;
                    }
                });

                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                relativeLayout.addView(linearLayout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                // Title: "Settings"
                TextView textView = new TextView(context);
                textView.setText("Settings");
                textView.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
                textView.setTextSize(20);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(textView);

                // Layout (Linear Vertical): Action List
                final LinearLayout linearLayout2 = new LinearLayout(context);
                linearLayout2.setOrientation(LinearLayout.VERTICAL);
                linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                linearLayout.addView(createButtonView("portable separation distance"));
                linearLayout.addView(createButtonView("extension separation MIN"));
                linearLayout.addView(createButtonView("extension separation MAX"));

                linearLayout.addView(createButtonView("add/remove Host"));
                linearLayout.addView(createButtonView("enable/disable notifications"));
                linearLayout.addView(createButtonView("enable/disable vibration"));
                linearLayout.addView(createButtonView("enable/disable network"));

                // Title: "Settings"
                TextView debugSubtitle = new TextView(context);
                debugSubtitle.setText("Debug Section Subtitle!");
                debugSubtitle.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
                debugSubtitle.setTextSize(12);
                debugSubtitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                debugSubtitle.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(debugSubtitle);

                linearLayout.addView(createButtonView("debug: show monitor"));
                linearLayout.addView(createButtonView("debug: show boundaries"));
                linearLayout.addView(createButtonView("debug: target fps"));
                linearLayout.addView(createButtonView("debug: sleep time"));

                // Add to main Application View
                FrameLayout frameLayout = (FrameLayout) Application.getView().findViewById(R.id.application_view);
                frameLayout.addView(relativeLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
        });
    }

    public void openMainMenu() {

        Application.getView().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final RelativeLayout relativeLayout = new RelativeLayout(context);
                relativeLayout.setBackgroundColor(Color.parseColor("#bb000000"));
                // TODO: set layout_margin=20dp

                // Background Event Handler
                relativeLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        relativeLayout.setVisibility(View.GONE);
                        return true;
                    }
                });

                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                relativeLayout.addView(linearLayout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                // Title: "Main Menu"
                TextView textView = new TextView(context);
                textView.setText("Main Menu");
                textView.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
                textView.setTextSize(15);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(textView);

                // Layout (Linear Vertical): Action List
                final LinearLayout linearLayout2 = new LinearLayout(context);
                linearLayout2.setOrientation(LinearLayout.VERTICAL);
                linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                linearLayout.addView(createButtonView("browse projects"));
                linearLayout.addView(createButtonView("start from extensions"));
                linearLayout.addView(createButtonView("free build"));
                linearLayout.addView(createButtonView("challenge mode"));

                // Title: "Player"
                TextView debugSubtitle = new TextView(context);
                debugSubtitle.setText("Player");
                debugSubtitle.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
                debugSubtitle.setTextSize(15);
                debugSubtitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                debugSubtitle.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(debugSubtitle);

                linearLayout.addView(createButtonView("Projects"));
                linearLayout.addView(createButtonView("Inventory"));
                linearLayout.addView(createButtonView("Ideas"));
                linearLayout.addView(createButtonView("Friends"));
                linearLayout.addView(createButtonView("Achievements"));

                // Title: "Store"
                TextView storeSubtitle = new TextView(context);
                storeSubtitle.setText("Store");
                storeSubtitle.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
                storeSubtitle.setTextSize(15);
                storeSubtitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                storeSubtitle.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(storeSubtitle);

                linearLayout.addView(createButtonView("Clay"));
                linearLayout.addView(createButtonView("Kits"));
                linearLayout.addView(createButtonView("Components"));
                linearLayout.addView(createButtonView("Accessories"));

                // Add to main Application View
                FrameLayout frameLayout = (FrameLayout) Application.getView().findViewById(R.id.application_view);
                frameLayout.addView(relativeLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
        });
    }


    public void generateView(final String title) {

        Application.getView().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final RelativeLayout relativeLayout = new RelativeLayout(context);
                relativeLayout.setBackgroundColor(Color.parseColor("#bb000000"));
                // TODO: set layout_margin=20dp

                // Background Event Handler
                relativeLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        relativeLayout.setVisibility(View.GONE);
                        return true;
                    }
                });

                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                relativeLayout.addView(linearLayout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                // Title: "Main Menu"
                TextView textView = new TextView(context);
                textView.setText(title);
                textView.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
                textView.setTextSize(15);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(textView);

                /*
                // Layout (Linear Vertical): Action List
                final LinearLayout linearLayout2 = new LinearLayout(context);
                linearLayout2.setOrientation(LinearLayout.VERTICAL);
                linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                linearLayout.addView(createButtonView("browse projects"));
                linearLayout.addView(createButtonView("start from extensions"));
                linearLayout.addView(createButtonView("free build"));
                linearLayout.addView(createButtonView("challenge mode"));

                // Title: "Player"
                TextView debugSubtitle = new TextView(context);
                debugSubtitle.setText("Player");
                debugSubtitle.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
                debugSubtitle.setTextSize(15);
                debugSubtitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                debugSubtitle.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(debugSubtitle);

                linearLayout.addView(createButtonView("Projects"));
                linearLayout.addView(createButtonView("Inventory"));
                linearLayout.addView(createButtonView("Ideas"));
                linearLayout.addView(createButtonView("Friends"));
                linearLayout.addView(createButtonView("Achievements"));

                // Title: "Store"
                TextView storeSubtitle = new TextView(context);
                storeSubtitle.setText("Store");
                storeSubtitle.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
                storeSubtitle.setTextSize(15);
                storeSubtitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                storeSubtitle.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(storeSubtitle);

                linearLayout.addView(createButtonView("Clay"));
                linearLayout.addView(createButtonView("Kits"));
                linearLayout.addView(createButtonView("Components"));
                linearLayout.addView(createButtonView("Accessories"));
                */

                // Add to main Application View
                FrameLayout frameLayout = (FrameLayout) Application.getView().findViewById(R.id.application_view);
                frameLayout.addView(relativeLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
        });
    }

    int imageEditorId;

    public void openImageEditor(Entity extension) {

        Application.getView().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final RelativeLayout relativeLayout = new RelativeLayout(context);
                imageEditorId = generateViewId();
                relativeLayout.setId(imageEditorId);
                relativeLayout.setBackgroundColor(Color.parseColor("#bb000000"));
                // TODO: set layout_margin=20dp

                // Background Event Handler
                relativeLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        relativeLayout.setVisibility(View.GONE);
                        return true;
                    }
                });

                // Drawing Editor
                View imageView = createImageEditor();
                relativeLayout.addView(imageView);

                // Add to main view
                // TODO: Create in separate fragment!
                FrameLayout frameLayout = (FrameLayout) Application.getView().findViewById(R.id.application_view);
                frameLayout.addView(relativeLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
        });
    }

    private List<Transform> imagePoints = new ArrayList<>();

    public View createImageEditor() { // formerly openBitmapEditor()

        imagePoints.clear();

        // Drawing Canvas
        ImageView imageView = new ImageView(context);

        final int imageViewId = generateViewId();
        imageView.setId(imageViewId);

        LinearLayout.LayoutParams params6 = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );
        params6.setMargins(0, dpToPx(5), 0, 0);
        imageView.setLayoutParams(params6);

        // ViewTreeObserver
        ViewTreeObserver vto = imageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {

                // Remove callback to onPreDraw
                final ImageView imageView = (ImageView) Application.getView().findViewById(imageViewId);
                imageView.getViewTreeObserver().removeOnPreDrawListener(this);

                // Get dimension of ImageView
                int finalHeight = imageView.getMeasuredHeight();
                int finalWidth = imageView.getMeasuredWidth();

                // Create bitmap, canvas, and paint for rendering the drawing
                final Bitmap bitmap = Bitmap.createBitmap(finalWidth, finalHeight, Bitmap.Config.ARGB_8888);
                final Canvas canvas = new Canvas(bitmap);
                final Paint paint = new Paint();
                paint.setAntiAlias(true);

                // Set background color
                canvas.drawColor(Color.WHITE);

                // Configure interaction handler for drawing editor
                imageView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                        if (touchActionType == MotionEvent.ACTION_DOWN) {
                            // TODO:
                        } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                            // TODO:
                        } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                            // TODO:
                        } else if (touchActionType == MotionEvent.ACTION_UP) {

                            // <HACK>
                            if (motionEvent.getX() < 50 && motionEvent.getY() < 50) {
                                View containerView = Application.getView().findViewById(imageEditorId);
                                ((ViewManager) containerView.getParent()).removeView(containerView);
                            }
                            // </HACK>

                            // Add point to Image
                            imagePoints.add(new Transform(motionEvent.getX(), motionEvent.getY()));

                            // Set background color
                            canvas.drawColor(Color.WHITE);

                            for (int i = 0; i < imagePoints.size(); i++) {
                                Transform transform = imagePoints.get(i);

                                paint.setColor(Color.BLACK);
                                paint.setStyle(Paint.Style.FILL);
                                canvas.drawCircle((float) transform.x, (float) transform.y, 4, paint);

                                paint.setColor(Color.BLACK);
                                paint.setStyle(Paint.Style.FILL);
                                paint.setTextSize(15);
                                canvas.drawText("" + (int) transform.x + ", " + (int) transform.y, (float) transform.x, (float) transform.y, paint);
                            }

                            imageView.invalidate();

                        } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                            // TODO:
                        } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                            // TODO:
                        } else {
                            // TODO:
                        }

                        return true;
                    }
                });


                imageView.setImageBitmap(bitmap);

                imageView.invalidate();

                // View view = Application.getView().findViewById(R.id.application_view);
                // view.invalidate();

                return true;
            }
        });

        // return imageViewId;
        return imageView;
    }

    // <NATIVE_UI_UTILS>
    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    /**
     * Generate a value suitable for use in {@link #setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    public static int generateViewId() {
        for (; ; ) {
            final int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) newValue = 1; // Roll over to 1, not 0.
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
    // </NATIVE_UI_UTILS>

    // <BASIC_UI>
    public View createTextView(String text, int layoutStrategy) {

        // Text: "Data Sources (Imports)"
        final TextView textView = new TextView(context);
        textView.setTextSize(10.0f);
        textView.setText(text);
        textView.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
        textView.setBackgroundColor(Color.parseColor("#44000000"));

        LinearLayout.LayoutParams params = null;
        if (layoutStrategy == 0) {
            // NOTE: (Strategy 1) Sets TextView width to MATCH_PARENT
            params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
        } else if (layoutStrategy == 1) {
            // NOTE: (Strategy 2) Sets TextView width to fill horizontal space equally (e.g., use to create multiple horizontal TextViews with equal width)
            params = new LinearLayout.LayoutParams(
                    0, // NOTE: set layout_width to 0dp and set the layout_weight to 1 for filling horizontal space available
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1.0f
            );
        } else if (layoutStrategy == 2) {
            // NOTE: (Strategy 3) Sets TextView to wrap content horizontally and MATCH_PARENT vertically. Use to add multiple variable-width Views of equal height to a horizontal layout.
            params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, // NOTE: set layout_width to 0dp and set the layout_weight to 1 for filling horizontal space available
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
        }
        params.setMargins(0, dpToPx(5), 0, 0);
        textView.setLayoutParams(params);

        return textView;
    }

    public View createEditText(String text, String hintText) {

        EditText editText = new EditText(context);

        editText.setHint(hintText);

        if (text != null && text.length() > 0) {
            editText.setText(text);
        }

        editText.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, dpToPx(5), 0, 0);
        editText.setLayoutParams(layoutParams);

        return editText;
    }

    // TODO: Create content, style, layout parameters so they can be passed to a general function.
    private View createButtonView(String text) {

        // <PARAMETERS>
        int textSize = 10;
        String textColor = "#ffffffff";
        String backgroundColor = "#44000000";
        int paddingLeft = dpToPx(20);
        int paddingTop = dpToPx(12);
        int paddingRight = dpToPx(20);
        int paddingBottom = dpToPx(12);
        int marginLeft = 0;
        int marginTop = dpToPx(5);
        int marginRight = 0;
        int marginBottom = 0;
        // </PARAMETERS>

        Button button = new Button(context);
        button.setPadding(paddingLeft, paddingTop, paddingRight, paddingBottom);
        button.setTextColor(Color.parseColor(textColor));
        button.setTextSize(textSize);
        button.setBackgroundColor(Color.parseColor(backgroundColor));

        button.setText(text);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(marginLeft, marginTop, marginRight, marginBottom);
        button.setLayoutParams(layoutParams);

        // Interaction
        button.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int pointerIndex = ((motionEvent.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT);
                int pointerId = motionEvent.getPointerId(pointerIndex);
                //int touchAction = (motionEvent.getEvent () & MotionEvent.ACTION_MASK);
                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);
                int pointCount = motionEvent.getPointerCount();

                // Update the state of the touched object based on the current pointerCoordinates interaction state.
                if (touchActionType == MotionEvent.ACTION_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                    // TODO:
                } else {
                    // TODO:
                }

                return true;
            }
        });

        return button;
    }

    public interface RequestDataTask<T> {
        List<T> execute();
    }

    private View createSelector(final String title, final RequestDataTask<String> requestDataTask) {

        final List<String> spinnerData = new ArrayList<>(requestDataTask.execute());
        spinnerData.add(title);

        // Spinner: Action Browser
        final Spinner spinner = new Spinner(context);
        spinner.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
        spinner.setBackgroundColor(Color.parseColor("#44000000"));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 0, 0, 0);
        spinner.setLayoutParams(layoutParams);

        // TODO: Load Scripts from Repository
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, spinnerData); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerArrayAdapter);

        spinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int pointerIndex = ((motionEvent.getAction() & MotionEvent.ACTION_POINTER_ID_MASK) >> MotionEvent.ACTION_POINTER_ID_SHIFT);
                int pointerId = motionEvent.getPointerId(pointerIndex);
                //int touchAction = (motionEvent.getEvent () & MotionEvent.ACTION_MASK);
                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);
                int pointCount = motionEvent.getPointerCount();

                // Update the state of the touched object based on the current pointerCoordinates interaction state.
                if (touchActionType == MotionEvent.ACTION_DOWN) {

                    spinnerData.clear();
                    spinnerData.add(title);
                    spinnerData.addAll(requestDataTask.execute());
                    spinnerArrayAdapter.notifyDataSetChanged();
                    spinner.invalidate();
                    Log.v("Spinner", "Item selected");

                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_UP) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                    // TODO:
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                    // TODO:
                } else {
                    // TODO:
                }

                return false;
            }
        });

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if (i > 0) {
                    String selectedText = spinnerData.get(i);

                    if (selectedText.equals("new script")) {

//                        // View actionEditorView = createActionView_v1();
//                        TextView actionEditorView = (TextView) createActionView_v2();
//                        linearLayout2.addView(actionEditorView);

                    } else {
//                        // EditText actionEditorView = (EditText) createActionView_v1();
//                        TextView actionEditorView = (TextView) createActionView_v2();
//                        actionEditorView.setText(selectedText);
//                        linearLayout2.addView(actionEditorView);
                    }

//                    spinner.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        return spinner;
    }

    public View createSlider() {
        return null;
    }

    public void showView(final View view) {

        Application.getView().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Add to main Application View
                FrameLayout frameLayout = (FrameLayout) Application.getView().findViewById(R.id.application_view);
                frameLayout.addView(view, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
        });
    }
    // </BASIC_UI>
}
