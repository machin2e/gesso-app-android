package camp.computer.clay.platform.graphics.controls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Processor;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Group;
import camp.computer.clay.engine.system.RenderSystem;
import camp.computer.clay.model.Action;
import camp.computer.clay.model.Script;
import camp.computer.clay.model.configuration.Configuration;
import camp.computer.clay.platform.Application;
import camp.computer.clay.platform.communication.HttpRequestTasks;
import camp.computer.clay.platform.util.ViewGroupHelper;
import camp.computer.clay.util.Random;

public class PlatformUi {

    private Context context = null;

    public PlatformUi(Context context) {
        this.context = context;
    }

    // TODO: Replace OnActionListener with Action?
    public interface OnActionListener<T> {
        void onComplete(T result);
    }

    public interface LabelMapper<T> {
        String map(T element);
    }

    public interface SelectEventHandler<T> {
        void execute(T selection);
    }

    public void openActionEditor(Entity extension) {
        createActionEditor_v3(extension);
    }

    private View createSwitchControllerView(Entity port) {
        return createSwitchControllerView_v2(port);
    }

    private View createPulseControllerView(Entity port) {
        return createPulseControllerView_v2(port);
    }

    private View createWaveControllerView(Entity port) {
        return createWaveControllerView_v2(port);
    }

    public void promptAcknowledgment(final OnActionListener onActionListener) {
        Application.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                new AlertDialog.Builder(context)
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

    public void openCreateExtensionView(final OnActionListener onActionListener) {

        /*
        Application.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                Log.v("IASM", "Input Text");

                final AlertDialog.Builder builder = new AlertDialog.Builder(Application.getInstance().getApplication());
                builder.setTitle("Create ExtensionEntity");

                // Set up the input
                final EditText input = new EditText(context);

                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);

                // Add input to view
                builder.setView(input);

//                // Set up the buttons
//                builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        onActionListener.onComplete(input.getText().toString());
//                    }
//                });
//
//                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // TODO: Callback with "Cancel" action
//                        dialog.cancel();
//                    }
//                });

                builder.show();
            }
        });
        */

        Application.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final int containerViewId = generateViewId();

                final RelativeLayout containerView = new RelativeLayout(context);
                containerView.setId(containerViewId);
                containerView.setBackgroundColor(Color.parseColor("#bb000000"));
                // TODO: set layout_margin=20dp

                // Background Event Handler
                containerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        containerView.setVisibility(View.GONE);
                        return true;
                    }
                });

                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                containerView.addView(linearLayout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                // Title: "Save Extension"
                TextView textView = new TextView(context);
                textView.setText("Save Extension");
                textView.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20));
                textView.setTextSize(15);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(textView);

                // Layout (Linear Vertical): Action List
                final LinearLayout linearLayout2 = new LinearLayout(context);
                linearLayout2.setOrientation(LinearLayout.VERTICAL);
                linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                final EditText extensionTitleView = (EditText) createEditText(null, "Type name here");
                linearLayout.addView(extensionTitleView);

                linearLayout.addView(createButtonView("Save", new SelectEventHandler<String>() {
                    @Override
                    public void execute(String selection) {
                        Log.v("createButtonView", "got callback! " + selection);
                        String extensionTitle = extensionTitleView.getText().toString();
                        Log.v("createButtonView", "extension title: " + extensionTitle);
                        onActionListener.onComplete(extensionTitle);

                        // <REMOVE_VIEW>
                        View containerView = Application.getInstance().findViewById(containerViewId);
                        ((ViewManager) containerView.getParent()).removeView(containerView);
                        // </REMOVE_VIEW>
                    }
                }));
                // in callback: onActionListener.onComplete(input.getText().toString());

                // Add to main Application View
                FrameLayout frameLayout = (FrameLayout) Application.getInstance().findViewById(Application.applicationViewId);
                frameLayout.addView(containerView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
        });
    }

    // TODO: public <T> void openInteractiveAssembler(List<T> options, OnActionListener onActionListener) {

    /**
     * Opens Interactive Assembler. Starts by prompting for an Extension. The subsequent steps
     * guide the assembly process.
     */
    public void openInteractiveAssembler(final List<Configuration> options, final OnActionListener onActionListener) {

        // Show Extension Browser
        createListView(
                options,
                new LabelMapper<Configuration>() {
                    @Override
                    public String map(Configuration configuration) {
                        return configuration.getLabel();
                    }
                },
                new SelectEventHandler<Configuration>() {
                    @Override
                    public void execute(Configuration selectedConfiguration) {
                        //Toast.makeText(getBaseContext(), ""+arg2,     Toast.LENGTH_SHORT).show();
                        Log.v("ListView", "selected Configuration: " + selectedConfiguration.getLabel());

                        // Add Action to Process
                        onActionListener.onComplete(selectedConfiguration);

                        // Start Interactive Assembly
                        openInteractiveAssemblyTaskOverview();
                    }
                }
        );
    }

    // Break multi-updateImage tasks up into a sequence of floating interface elements that must be completed to continue (or abandon the sequence)
    // displayFloatingTaskDialog(<task list>, <task updateImage to display>)

    public void openInteractiveAssemblyTaskOverview() { // was "openInteractiveAssemblyTaskOverview"

//        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
//        // builderSingle.setIcon(R.drawable.ic_launcher);
//        dialogBuilder.setTitle("Complete these steps to assemble");
//
//        // TODO: Difficulty
//        // TODO: Average Clock
//
//        // Create data adapter
//        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
//                context,
//                android.R.layout.select_dialog_multichoice
//        );
//
//        // Add data to adapter
//        arrayAdapter.add("Task 1");
//        arrayAdapter.add("Task 2");
//        arrayAdapter.add("Task 3");
//        arrayAdapter.add("Task 4");
//        arrayAdapter.add("Task 5");
//
//        final Context appContext = context;
//
//        /*
//        builderSingle.setNegativeButton(
//                "Cancel",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                });
//        */
//
//        // Positive button
//        dialogBuilder.setPositiveButton(
//                "Start",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        openInteractiveAssemblyTaskView();
//                    }
//                }
//        );
//
//        // Set data adapter
//        dialogBuilder.setAdapter(
//                arrayAdapter,
//                null
//        );
//
//
//        AlertDialog dialog = dialogBuilder.create();
//
//        dialog.getListView().setItemsCanFocus(false);
//        dialog.getListView().setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//        dialog.getListView().setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // Manage selected items here
//                System.out.println("clicked" + position);
//                CheckedTextView textView = (CheckedTextView) view;
//                if (textView.isChecked()) {
//
//                } else {
//
//                }
//            }
//        });
//
//        dialog.show();

        // Show Extension Browser
        ArrayList<String> assemblyInstructions = new ArrayList<>();
        assemblyInstructions.add("Step 1");
        assemblyInstructions.add("Step 2");
        assemblyInstructions.add("Step 3");
        assemblyInstructions.add("Step 4");
        assemblyInstructions.add("Step 5");

        createListView(
                assemblyInstructions,
                new LabelMapper<String>() {
                    @Override
                    public String map(String configuration) {
                        return configuration;
                    }
                },
                new SelectEventHandler<String>() {
                    @Override
                    public void execute(String selectedAssemblyStep) {
                        //Toast.makeText(getBaseContext(), ""+arg2,     Toast.LENGTH_SHORT).show();
                        Log.v("ListView", "selected Configuration: " + selectedAssemblyStep);

                        // Start Interactive Assembly
                        openInteractiveAssemblyTaskView();
                    }
                }
        );

        // TODO: "Start Assembly" Button
        // TODO: Response: openInteractiveAssemblyTaskView(<AssemblyProcess>, <step 1>)
    }

    public void openInteractiveAssemblyTaskView() {

        // TODO: Show single step in the assembly process. Float on bottom of screen.

        // <REPLACE>
        ArrayList<String> assemblyInstructions = new ArrayList<>();
        assemblyInstructions.add("Step N");

        createListView(
                assemblyInstructions,
                new LabelMapper<String>() {
                    @Override
                    public String map(String configuration) {
                        return configuration;
                    }
                },
                new SelectEventHandler<String>() {
                    @Override
                    public void execute(String selectedAssemblyStep) {
                        //Toast.makeText(getBaseContext(), ""+arg2,     Toast.LENGTH_SHORT).show();
                        Log.v("ListView", "selected Configuration: " + selectedAssemblyStep);

                        // Start Interactive Assembly
                        openInteractiveAssemblyTaskView();
                    }
                }
        );
        // </REPLACE>

        // TODO: Add "Next" button

    }

    boolean reqeustedActions = false;

    public void createActionEditor_v3(final Entity extension) {

        // TODO: Hack into the JS engine in V8 to execute this pure JS. Fuck it.

        final String BUTTON_TEXT_COLOR = "#ffffffff";

        // NOTE: This is just a list of edit boxes. Each with a dropdown to save new script or load from the list. MVP, bitches.

        // <REFACTOR>
        // TODO: Relocate so these are stored in Cache.
        // Cache Action and Script in Repository. Retrieve Actions and Scripts from Remote Server.
        if (!reqeustedActions) {
            HttpRequestTasks.HttpRequestTask httpRequestTask = new HttpRequestTasks.HttpRequestTask();
            httpRequestTask.uri = World.ASSET_SERVER_URI + "/repository/actions";
            new HttpRequestTasks.HttpGetRequestTask().execute(httpRequestTask); // TODO: Add GET request to queue in Application startup... in an Engine System
            reqeustedActions = true;
        }
        // </REFACTOR>

        // Get list of Ports connected to Extension
        // String portTypesString = "";
        final Group<Entity> ports = Portable.getPorts(extension);
        /*
        for (int i = 0; i < ports.size(); i++) {
            Entity port = ports.get(i);
            Log.v("PortType", "port type: " + Port.getType(port));
            portTypesString += Port.getType(port) + " ";
        }
        */

        Application.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                final FrameLayout containerView = new FrameLayout(context);
                containerView.setBackgroundColor(Color.parseColor("#bb000000"));
                // TODO: set layout_margin=20dp

                // Background Event Handler
                containerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        containerView.setVisibility(View.GONE);
                        return true;
                    }
                });

                FrameLayout.LayoutParams containerViewLayoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                containerView.setLayoutParams(containerViewLayoutParams);

                final ScrollView scrollView = new ScrollView(context);
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                scrollView.addView(linearLayout, new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                containerView.addView(scrollView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                // Title: "Actions"
//                TextView textView = new TextView(context);
//                textView.setText("Extension Controller");
//                textView.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20));
//                textView.setTextSize(20);
//                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//                textView.setGravity(Gravity.CENTER_HORIZONTAL);
//                linearLayout.addView(textView);

                View titleView = createHeaderView("Extension Controller");
                linearLayout.addView(titleView);

                View findDataView = createDataImportView("Find Data");
                linearLayout.addView(findDataView);

                // Layout (Linear Vertical): Action List
                final LinearLayout actionListLayout = new LinearLayout(context);
                actionListLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.addView(actionListLayout, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                // TODO: Populate Action List from Extension

                // Default Action Controller based on Port Configuration
                /*
                EditText defaultActionBasedOnPortConfiguration = (EditText) createActionView_v1();
                defaultActionBasedOnPortConfiguration.setText("PLACEHOLDER: " + finadlPortTypesString);
                linearLayout.addView(defaultActionBasedOnPortConfiguration);
                */

                /*
                View setProtocolAdapterView = createDataImportView("Add Protocol Adapter");
                linearLayout.addView(setProtocolAdapterView);
                */

                /*
                // TODO: Return View IDs and add listeners after creating view structure.
                createView(
                        new TitleView(new SelectEventHandler<>() {
                            @Override
                            public void execute(Object selection) {
                                // TODO:
                            }
                        }),
                        new TaskView(new SelectEventHandler<>() {
                            @Override
                            public void execute(Object selection) {
                                // TODO:
                            }
                        }),
                        new SelectorManager(
                                new TaskView(new SelectEventHandler<>() {
                                    @Override
                                    public void execute(Object selection) {
                                        // TODO:
                                    }
                                }),
                                new TaskView(new SelectEventHandler<>() {
                                    @Override
                                    public void execute(Object selection) {
                                        // TODO:
                                    }
                                })
                        )
                );
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

                    button6.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));
                    button6.setBackgroundColor(Color.parseColor(camp.computer.clay.util.Color.getColor(Port.getType(ports.get(i)))));

                    LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                    );
                    params4.setMargins(0, ViewGroupHelper.dpToPx(5), 0, 0);
                    button6.setLayoutParams(params4);

                    portableLayout.addView(button6);
                }
                */


                // Button: "Search for Action"
//                LinearLayout searchLayout = new LinearLayout(context);
//                searchLayout.setOrientation(LinearLayout.HORIZONTAL);
//                linearLayout.addView(searchLayout);

                /*
                EditText searchBox = new EditText(context);
                searchBox.setHint("Search for Action");
                searchBox.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));
                searchBox.setBackgroundColor(Color.parseColor("#44000000"));

                LinearLayout.LayoutParams params8 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params8.setMargins(0, ViewGroupHelper.dpToPx(5), 0, 0);
                searchBox.setLayoutParams(params8);

                Button cancelSearchButton = new Button(context);
                cancelSearchButton.setText("X");
                cancelSearchButton.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));
                cancelSearchButton.setBackgroundColor(Color.parseColor("#44000000"));

                searchLayout.addView(searchBox);

                TextView browseActionEventView = (TextView) createTextView("\uD83D\uDD0D", 1);
                searchLayout.addView(browseActionEventView);
                */

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

                // Button: "Add Action"
                Button button2 = new Button(context);
                button2.setText("Add Action");
                button2.setTextColor(Color.parseColor(BUTTON_TEXT_COLOR));
                button2.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));
                button2.setBackgroundColor(Color.parseColor("#44000000"));

                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params3.setMargins(0, ViewGroupHelper.dpToPx(5), 0, 0);
                button2.setLayoutParams(params3);
                linearLayout.addView(button2);

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
                        } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                        } else if (touchActionType == MotionEvent.ACTION_MOVE) {
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
                                            extension.getComponent(Processor.class).process.addAction(selectedAction);

                                            // Replace with new View
//                                    View actionView = createActionView_v1(selectedAction);
//                                    ViewGroupHelper.replaceView(containerView, actionView);

                                            View actionView = createNewActionView_v5(selectedAction);

//                                            ViewGroupHelper.replaceView(containerView, actionView);
                                            actionListLayout.addView(actionView);

//                                    openScriptEditor(selectedAction.getScript());
                                        }
                                    }
                            );

                            /*
                            View actionEditorView = createActionView(extension);
//                            linearLayout2.addView(actionEditorView, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                            actionListLayout.addView(actionEditorView);
                            */

                        } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                        } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                        } else {
                        }

                        return true;
                    }
                });

                // Button: Test Script
                Button button3 = new Button(context);
                button3.setText("Test");
                button3.setTextColor(Color.parseColor(BUTTON_TEXT_COLOR));
                button3.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));
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

                            List<Action> actions = extension.getComponent(Processor.class).process.getActions();
                            for (int i = 0; i < actions.size(); i++) {
                                Log.v("ActionProcess", "" + i + ": " + actions.get(i).getTitle());
                            }

                            // Send complete scripts to Hosts
                            HttpRequestTasks.HttpRequestTask httpRequestTask = new HttpRequestTasks.HttpRequestTask();
                            httpRequestTask.entity = extension;
                            new HttpRequestTasks.HttpPostRequestTask().execute(httpRequestTask); // TODO: Replace with queueHttpTask(httpTask)

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
                params4.setMargins(0, ViewGroupHelper.dpToPx(5), 0, 0);
                button3.setLayoutParams(params4);
                linearLayout.addView(button3);

                // Add to main Application View
                FrameLayout frameLayout = (FrameLayout) Application.getInstance().findViewById(Application.applicationViewId);
                frameLayout.addView(containerView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));


                // <POPULATE_DATA>
                List<Action> actions = extension.getComponent(Processor.class).process.getActions();
                for (int i = 0; i < actions.size(); i++) {
                    Log.v("ActionProcess", "" + i + ": " + actions.get(i).getTitle());
                    View actionView = createNewActionView_v5(actions.get(i));
                    actionListLayout.addView(actionView);
                }
                // </POPULATE_DATA>
            }
        });
    }

    public View createListView(final List listData, final LabelMapper labelMapper, final SelectEventHandler selectEventHandler) {

        Application.getInstance().runOnUiThread(new Runnable() {
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
                textView.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20));
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
                        View containerView = Application.getInstance().findViewById(listViewId);
                        ((ViewManager) containerView.getParent()).removeView(containerView);
                        // </REMOVE_VIEW>
                    }
                });

                ll.addView(listView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

                linearLayout.addView(ll);

                // Add to main Application View
                FrameLayout frameLayout = (FrameLayout) Application.getInstance().findViewById(Application.applicationViewId);
                frameLayout.addView(containerLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
        });

        return null;
    }

    private View createSwitchControllerView_v2(Entity port) {

        String labelButtonText = "Switch";

        // <PARAMETERS>
        int CONTAINER_TOP_MARGIN = ViewGroupHelper.dpToPx(5);
        // ...
        int BUTTON_PADDING_LEFT = ViewGroupHelper.dpToPx(20);
        // ...
        int BUTTON_INNER_PADDING_TOP = ViewGroupHelper.dpToPx(12);
        // ...
        int BUTTON_OUTER_PADDING_TOP = 0; // was ViewGroupHelper.dpToPx(5)
        // ...
        int TASK_BUTTON_WIDTH = 150;

        int BUTTON_TEXT_SIZE = 12;

        String CONTAINER_BACKGROUND_COLOR = camp.computer.clay.util.Color.getColor(Port.Type.SWITCH);

        String BUTTON_BACKGROUND_COLOR = "#00000000";

        String BUTTON_TEXT_COLOR = "#ffffffff";
        // </PARAMETERS>

        // <CONTAINER_VIEW>
        final RelativeLayout containerView = new RelativeLayout(context);
        containerView.setBackgroundColor(Color.parseColor(CONTAINER_BACKGROUND_COLOR));

        LinearLayout.LayoutParams containerLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerLayoutParams.setMargins(0, CONTAINER_TOP_MARGIN, 0, 0);

        containerView.setLayoutParams(containerLayoutParams);
        // </CONTAINER_VIEW>


        int labelButtonId = generateViewId();
        final int taskButton1Id = generateViewId();
        final int taskButton2Id = generateViewId();
        final int taskButton3Id = generateViewId();


        // <LABEL_BUTTON>
        // Button: "Import Data Source"
        Button labelButton = new Button(context);
        labelButton.setId(labelButtonId);
        labelButton.setText(labelButtonText); // i.e., Formerly "Add Data Source". i.e., [Project][Internet][Generator].
        labelButton.setTextColor(Color.parseColor("#88ffffff"));
        labelButton.setPadding(ViewGroupHelper.dpToPx(20), BUTTON_INNER_PADDING_TOP, ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));
        labelButton.setBackgroundColor(Color.parseColor(BUTTON_BACKGROUND_COLOR));
        labelButton.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        RelativeLayout.LayoutParams labelButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        labelButtonParams.setMargins(0, BUTTON_OUTER_PADDING_TOP, 0, 0);
        labelButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        labelButtonParams.addRule(RelativeLayout.LEFT_OF, taskButton3Id); // Set to left of left-most "task button view"
        labelButton.setLayoutParams(labelButtonParams);

        labelButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                } else if (touchActionType == MotionEvent.ACTION_UP) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                } else {
                }

                return true;
            }
        });

        containerView.addView(labelButton);
        // </LABEL_BUTTON>

        // <TASK_BUTTON>
        Button taskButton1 = new Button(context);
        taskButton1.setId(taskButton1Id);
        taskButton1.setText("On"); // i.e., [Project][Internet][Generator]
        taskButton1.setTextColor(Color.parseColor(BUTTON_TEXT_COLOR));
        //button9.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));
        taskButton1.setTextSize(BUTTON_TEXT_SIZE);
        taskButton1.setPadding(0, BUTTON_INNER_PADDING_TOP, 0, ViewGroupHelper.dpToPx(12));
        taskButton1.setBackgroundColor(Color.parseColor(BUTTON_BACKGROUND_COLOR));
//        taskButton1.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        taskButton1.setMinWidth(0);
        taskButton1.setMinHeight(0);
        taskButton1.setIncludeFontPadding(false);

        RelativeLayout.LayoutParams taskButton1Params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        taskButton1Params.setMargins(0, BUTTON_OUTER_PADDING_TOP, 0, 0);
        taskButton1Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        taskButton1Params.addRule(RelativeLayout.RIGHT_OF, labelButtonId);
        taskButton1Params.width = TASK_BUTTON_WIDTH;
        taskButton1.setLayoutParams(taskButton1Params);

        taskButton1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    // Single-Selector Handler Strategy
                    View onView = Application.getInstance().findViewById(taskButton1Id);
                    View offView = Application.getInstance().findViewById(taskButton2Id);

                    // TODO: Update state!

                    // Update style to reflect state
                    onView.setBackgroundColor(Color.parseColor("#44000000"));
                    offView.setBackgroundColor(Color.parseColor("#00000000"));

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                } else {
                }

                return true;
            }
        });

        containerView.addView(taskButton1);
        // </TASK_BUTTON>

        // <TASK_BUTTON>
        Button taskButton2 = new Button(context);
        taskButton2.setId(taskButton2Id);
        taskButton2.setTextColor(Color.parseColor(BUTTON_TEXT_COLOR));
        taskButton2.setText("Off"); // i.e., [Project][Internet][Generator]
        taskButton2.setTextSize(BUTTON_TEXT_SIZE);
        taskButton2.setPadding(0, BUTTON_INNER_PADDING_TOP, 0, ViewGroupHelper.dpToPx(12));
        taskButton2.setBackgroundColor(Color.parseColor(BUTTON_BACKGROUND_COLOR));
//        taskButton2.setMinWidth(0);
//        taskButton2.setMaxWidth(10);
//        taskButton2.setMinHeight(0);
//        taskButton2.setIncludeFontPadding(false);

        RelativeLayout.LayoutParams taskButton2Params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        taskButton2Params.setMargins(0, BUTTON_OUTER_PADDING_TOP, 0, 0);
        taskButton2Params.width = TASK_BUTTON_WIDTH;
        taskButton2Params.addRule(RelativeLayout.LEFT_OF, taskButton1Id);
        taskButton2.setLayoutParams(taskButton2Params);

        containerView.addView(taskButton2);

        taskButton2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    // Single-Selector Handler Strategy
                    View onView = Application.getInstance().findViewById(taskButton1Id);
                    View offView = Application.getInstance().findViewById(taskButton2Id);

                    // TODO: Update state!

                    // Update style to reflect state
                    onView.setBackgroundColor(Color.parseColor("#00000000"));
                    offView.setBackgroundColor(Color.parseColor("#44000000"));

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                } else {
                }

                return true;
            }
        });
        // </TASK_BUTTON>

        // <TASK_BUTTON>
        Button taskButton3 = new Button(context);
        taskButton3.setId(taskButton3Id);
        taskButton3.setTextColor(Color.parseColor(BUTTON_TEXT_COLOR));
        taskButton3.setText("In"); // i.e., [Project][Internet][Generator]
        taskButton3.setTextSize(BUTTON_TEXT_SIZE);
        taskButton3.setPadding(0, BUTTON_INNER_PADDING_TOP, 0, ViewGroupHelper.dpToPx(12));
        taskButton3.setBackgroundColor(Color.parseColor(BUTTON_BACKGROUND_COLOR));

        RelativeLayout.LayoutParams taskButton3Params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        taskButton3Params.setMargins(0, BUTTON_OUTER_PADDING_TOP, 0, 0);
        taskButton3Params.width = TASK_BUTTON_WIDTH;
        taskButton3Params.addRule(RelativeLayout.LEFT_OF, taskButton2Id);
        taskButton3.setLayoutParams(taskButton3Params);

        containerView.addView(taskButton3);

        taskButton3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    // TODO:

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                } else {
                }

                return true;
            }
        });
        // </TASK_BUTTON>

        return containerView;
    }

    // <TODO>
    public enum ViewType {
        BUTTON,
        TEXT // i.e., for labels, read-only text, editable text
    }

    public class Parameters {

    }

    public class EntryView {
        ViewType type;
        Parameters parameters;
    }

    private View createEntryView(String label) {
        return null;
    }
    // </TODO>

    private View createPulseControllerView_v2(Entity port) {

        String labelButtonText = "Pulse";

        // <PARAMETERS>
        abstract class LabelParameters {

        }

        abstract class TaskParameters {

        }

        int CONTAINER_TOP_MARGIN = ViewGroupHelper.dpToPx(5);
        // ...
        int BUTTON_PADDING_LEFT = ViewGroupHelper.dpToPx(20);
        int BUTTON_PADDING_TOP = ViewGroupHelper.dpToPx(20);
        int BUTTON_PADDING_RIGHT = ViewGroupHelper.dpToPx(20);
        int BUTTON_PADDING_BOTTOM = ViewGroupHelper.dpToPx(20);
        // ...

        int BUTTON_TEXT_SIZE = 12;

        int BUTTON_INNER_PADDING_LEFT = ViewGroupHelper.dpToPx(5);
        int BUTTON_INNER_PADDING_TOP = ViewGroupHelper.dpToPx(12);
        int BUTTON_INNER_PADDING_RIGHT = ViewGroupHelper.dpToPx(5);
        int BUTTON_INNER_PADDING_BOTTOM = ViewGroupHelper.dpToPx(12);

        int BUTTON_OUTER_PADDING_LEFT = 0; // was ViewGroupHelper.dpToPx(5)
        int BUTTON_OUTER_PADDING_TOP = 0; // was ViewGroupHelper.dpToPx(5)
        int BUTTON_OUTER_PADDING_RIGHT = 0; // was ViewGroupHelper.dpToPx(5)
        int BUTTON_OUTER_PADDING_BOTTOM = 0; // was ViewGroupHelper.dpToPx(5)
        // ...
        int TASK_BUTTON_WIDTH = 400;

        String CONTAINER_BACKGROUND_COLOR = camp.computer.clay.util.Color.getColor(Port.Type.PULSE);

        String BUTTON_BACKGROUND_COLOR = "#00000000";

        int TEXT_INNER_PADDING_LEFT = ViewGroupHelper.dpToPx(5);
        int TEXT_INNER_PADDING_TOP = ViewGroupHelper.dpToPx(12);
        int TEXT_INNER_PADDING_RIGHT = ViewGroupHelper.dpToPx(5);
        int TEXT_INNER_PADDING_BOTTOM = ViewGroupHelper.dpToPx(12);
        // </PARAMETERS>

        // <CONTAINER_VIEW>
        final RelativeLayout containerView = new RelativeLayout(context);
        containerView.setBackgroundColor(Color.parseColor(CONTAINER_BACKGROUND_COLOR));

        LinearLayout.LayoutParams containerViewLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerViewLayoutParams.setMargins(0, CONTAINER_TOP_MARGIN, 0, 0);

        containerView.setLayoutParams(containerViewLayoutParams);
        // </CONTAINER_VIEW>


        int labelButtonId = generateViewId();
        final int taskButton1Id = generateViewId();
        final int taskButton2Id = generateViewId();
//        final int taskButton3Id = generateViewId();


        // <LABEL_BUTTON>
        // Button: "Import Data Source"
        Button labelButton = new Button(context);
        labelButton.setId(labelButtonId);
        labelButton.setText(labelButtonText); // i.e., Formerly "Add Data Source". i.e., [Project][Internet][Generator].
        labelButton.setTextColor(Color.parseColor("#88ffffff"));
        labelButton.setPadding(ViewGroupHelper.dpToPx(20), BUTTON_INNER_PADDING_TOP, ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));
        labelButton.setBackgroundColor(Color.parseColor(BUTTON_BACKGROUND_COLOR));
        labelButton.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        RelativeLayout.LayoutParams labelButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        labelButtonParams.setMargins(BUTTON_OUTER_PADDING_LEFT, BUTTON_OUTER_PADDING_TOP, BUTTON_OUTER_PADDING_RIGHT, BUTTON_OUTER_PADDING_BOTTOM);
        labelButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        labelButtonParams.addRule(RelativeLayout.LEFT_OF, taskButton2Id); // Set to left of left-most "task button view"
        labelButton.setLayoutParams(labelButtonParams);

        labelButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                } else if (touchActionType == MotionEvent.ACTION_UP) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                } else {
                }

                return true;
            }
        });

        containerView.addView(labelButton);
        // </LABEL_BUTTON>

        // <TASK_BUTTON>
        EditText taskButton1 = new EditText(context);
        taskButton1.setId(taskButton1Id);
        taskButton1.setTextSize(BUTTON_TEXT_SIZE);
        taskButton1.setTypeface(null, Typeface.BOLD);
        // taskButton1.setAllCaps(true); // NOTE: Doesn't work for EditText
        taskButton1.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        // TODO: Set BOLD
        taskButton1.setHint("On Time (DS)".toUpperCase()); // i.e., [Project][Internet][Generator]
        //button9.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));
        taskButton1.setPadding(BUTTON_INNER_PADDING_LEFT, BUTTON_INNER_PADDING_TOP, BUTTON_INNER_PADDING_RIGHT, BUTTON_INNER_PADDING_BOTTOM);
        taskButton1.setBackgroundColor(Color.parseColor(BUTTON_BACKGROUND_COLOR));
//        taskButton1.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
//        taskButton1.setMinWidth(0);
//        taskButton1.setMinHeight(0);
//        taskButton1.setIncludeFontPadding(false);

        RelativeLayout.LayoutParams taskButton1Params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        taskButton1Params.setMargins(0, BUTTON_OUTER_PADDING_TOP, 0, 0);
        taskButton1Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        taskButton1Params.addRule(RelativeLayout.RIGHT_OF, labelButtonId);
//        taskButton1Params.width = TASK_BUTTON_WIDTH;
        taskButton1Params.addRule(RelativeLayout.CENTER_VERTICAL); // for EditText, for vertical centering
        taskButton1.setLayoutParams(taskButton1Params);

        /*
        taskButton1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    // Single-Selector Handler Strategy
                    View onView = Application.getInstance().findViewById(taskButton1Id);
                    View offView = Application.getInstance().findViewById(taskButton2Id);

                    // TODO: Update state!

                    // Update style to reflect state
                    onView.setBackgroundColor(Color.parseColor("#44000000"));
                    offView.setBackgroundColor(Color.parseColor("#00000000"));

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                } else {
                }

                return true;
            }
        });
        */

        containerView.addView(taskButton1);
        // </TASK_BUTTON>

        // <TASK_BUTTON>
        EditText taskButton2 = new EditText(context);
        taskButton2.setId(taskButton2Id);
        taskButton2.setTextSize(BUTTON_TEXT_SIZE);
        taskButton2.setTypeface(null, Typeface.BOLD);
        // taskButton2.setAllCaps(true); // NOTE: Doesn't work for EditText
        taskButton2.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        taskButton2.setHint("Interval (Period)".toUpperCase()); // i.e., [Project][Internet][Generator]
        taskButton2.setPadding(BUTTON_INNER_PADDING_LEFT, BUTTON_INNER_PADDING_TOP, BUTTON_INNER_PADDING_RIGHT, BUTTON_INNER_PADDING_BOTTOM);
        taskButton2.setBackgroundColor(Color.parseColor(BUTTON_BACKGROUND_COLOR));
//        taskButton2.setMinWidth(0);
//        taskButton2.setMaxWidth(10);
//        taskButton2.setMinHeight(0);
//        taskButton2.setIncludeFontPadding(false);

        RelativeLayout.LayoutParams taskButton2Params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        taskButton2Params.setMargins(0, BUTTON_OUTER_PADDING_TOP, 0, 0);
//        taskButton2Params.width = TASK_BUTTON_WIDTH;
        taskButton2Params.addRule(RelativeLayout.LEFT_OF, taskButton1Id);
        taskButton2Params.addRule(RelativeLayout.CENTER_VERTICAL); // for EditText, for vertical centering
        taskButton2.setLayoutParams(taskButton2Params);

        containerView.addView(taskButton2);

        /*
        taskButton2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    // Single-Selector Handler Strategy
                    View onView = Application.getInstance().findViewById(taskButton1Id);
                    View offView = Application.getInstance().findViewById(taskButton2Id);

                    // TODO: Update state!

                    // Update style to reflect state
                    onView.setBackgroundColor(Color.parseColor("#00000000"));
                    offView.setBackgroundColor(Color.parseColor("#44000000"));

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                } else {
                }

                return true;
            }
        });
        */
        // </TASK_BUTTON>

        /*
        // <TASK_BUTTON>
        Button taskButton3 = new Button(context);
        taskButton3.setId(taskButton3Id);
        taskButton3.setText("In"); // i.e., [Project][Internet][Generator]
        taskButton3.setPadding(0, BUTTON_INNER_PADDING_TOP, 0, ViewGroupHelper.dpToPx(12));
        taskButton3.setBackgroundColor(Color.parseColor(BUTTON_BACKGROUND_COLOR));

        RelativeLayout.LayoutParams taskButton3Params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        taskButton3Params.setMargins(0, BUTTON_OUTER_PADDING_TOP, 0, 0);
//        taskButton3Params.width = TASK_BUTTON_WIDTH;
        taskButton3Params.addRule(RelativeLayout.LEFT_OF, taskButton2Id);
        taskButton3.setLayoutParams(taskButton3Params);

        containerView.addView(taskButton3);

        taskButton3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    // TODO:

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                } else {
                }

                return true;
            }
        });
        // </TASK_BUTTON>
        */

        return containerView;
    }

    private View createWaveControllerView_v2(Entity port) {

        String labelButtonText = "Wave";

        // <PARAMETERS>
        abstract class LabelParameters {

        }

        abstract class TaskParameters {

        }

        int CONTAINER_TOP_MARGIN = ViewGroupHelper.dpToPx(5);
        // ...
        int BUTTON_PADDING_LEFT = ViewGroupHelper.dpToPx(20);
        int BUTTON_PADDING_TOP = ViewGroupHelper.dpToPx(20);
        int BUTTON_PADDING_RIGHT = ViewGroupHelper.dpToPx(20);
        int BUTTON_PADDING_BOTTOM = ViewGroupHelper.dpToPx(20);
        // ...

        int BUTTON_TEXT_SIZE = 12;

        int BUTTON_INNER_PADDING_LEFT = ViewGroupHelper.dpToPx(5);
        int BUTTON_INNER_PADDING_TOP = ViewGroupHelper.dpToPx(12);
        int BUTTON_INNER_PADDING_RIGHT = ViewGroupHelper.dpToPx(5);
        int BUTTON_INNER_PADDING_BOTTOM = ViewGroupHelper.dpToPx(12);

        int BUTTON_OUTER_PADDING_LEFT = 0; // was ViewGroupHelper.dpToPx(5)
        int BUTTON_OUTER_PADDING_TOP = 0; // was ViewGroupHelper.dpToPx(5)
        int BUTTON_OUTER_PADDING_RIGHT = 0; // was ViewGroupHelper.dpToPx(5)
        int BUTTON_OUTER_PADDING_BOTTOM = 0; // was ViewGroupHelper.dpToPx(5)
        // ...
        int TASK_BUTTON_WIDTH = 400;

        String CONTAINER_BACKGROUND_COLOR = camp.computer.clay.util.Color.getColor(Port.Type.WAVE);

        String BUTTON_BACKGROUND_COLOR = "#00000000";

        int TEXT_INNER_PADDING_LEFT = ViewGroupHelper.dpToPx(5);
        int TEXT_INNER_PADDING_TOP = ViewGroupHelper.dpToPx(12);
        int TEXT_INNER_PADDING_RIGHT = ViewGroupHelper.dpToPx(5);
        int TEXT_INNER_PADDING_BOTTOM = ViewGroupHelper.dpToPx(12);
        // </PARAMETERS>

        // <CONTAINER_VIEW>
        final RelativeLayout containerView = new RelativeLayout(context);
        containerView.setBackgroundColor(Color.parseColor(CONTAINER_BACKGROUND_COLOR));

        LinearLayout.LayoutParams containerViewLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerViewLayoutParams.setMargins(0, CONTAINER_TOP_MARGIN, 0, 0);

        containerView.setLayoutParams(containerViewLayoutParams);
        // </CONTAINER_VIEW>


        int labelButtonId = generateViewId();
        final int taskButton1Id = generateViewId();
//        final int taskButton2Id = generateViewId();
//        final int taskButton3Id = generateViewId();


        // <LABEL_BUTTON>
        // Button: "Import Data Source"
        Button labelButton = new Button(context);
        labelButton.setId(labelButtonId);
        labelButton.setText(labelButtonText); // i.e., Formerly "Add Data Source". i.e., [Project][Internet][Generator].
        labelButton.setTextColor(Color.parseColor("#88ffffff"));
        labelButton.setPadding(ViewGroupHelper.dpToPx(20), BUTTON_INNER_PADDING_TOP, ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));
        labelButton.setBackgroundColor(Color.parseColor(BUTTON_BACKGROUND_COLOR));
        labelButton.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        RelativeLayout.LayoutParams labelButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        labelButtonParams.setMargins(BUTTON_OUTER_PADDING_LEFT, BUTTON_OUTER_PADDING_TOP, BUTTON_OUTER_PADDING_RIGHT, BUTTON_OUTER_PADDING_BOTTOM);
        labelButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        labelButtonParams.addRule(RelativeLayout.LEFT_OF, taskButton1Id); // Set to left of left-most "task button view"
        labelButton.setLayoutParams(labelButtonParams);

        labelButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                } else if (touchActionType == MotionEvent.ACTION_UP) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                } else {
                }

                return true;
            }
        });

        containerView.addView(labelButton);
        // </LABEL_BUTTON>

        // <TASK_BUTTON>
        EditText taskButton1 = new EditText(context);
        taskButton1.setId(taskButton1Id);
        taskButton1.setTextSize(BUTTON_TEXT_SIZE);
        taskButton1.setTypeface(null, Typeface.BOLD);
        // taskButton1.setAllCaps(true); // NOTE: Doesn't work for EditText
        taskButton1.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        // TODO: Set BOLD
        taskButton1.setHint("Amplitude (Voltage/ADC)".toUpperCase()); // i.e., [Project][Internet][Generator]
        //button9.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));
        taskButton1.setPadding(BUTTON_INNER_PADDING_LEFT, BUTTON_INNER_PADDING_TOP, BUTTON_INNER_PADDING_RIGHT, BUTTON_INNER_PADDING_BOTTOM);
        taskButton1.setBackgroundColor(Color.parseColor(BUTTON_BACKGROUND_COLOR));
//        taskButton1.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
//        taskButton1.setMinWidth(0);
//        taskButton1.setMinHeight(0);
//        taskButton1.setIncludeFontPadding(false);

        RelativeLayout.LayoutParams taskButton1Params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        taskButton1Params.setMargins(0, BUTTON_OUTER_PADDING_TOP, 0, 0);
        taskButton1Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        taskButton1Params.addRule(RelativeLayout.RIGHT_OF, labelButtonId);
//        taskButton1Params.width = TASK_BUTTON_WIDTH;
        taskButton1Params.addRule(RelativeLayout.CENTER_VERTICAL); // for EditText, for vertical centering
        taskButton1.setLayoutParams(taskButton1Params);

        /*
        taskButton1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    // Single-Selector Handler Strategy
                    View onView = Application.getInstance().findViewById(taskButton1Id);
                    View offView = Application.getInstance().findViewById(taskButton2Id);

                    // TODO: Update state!

                    // Update style to reflect state
                    onView.setBackgroundColor(Color.parseColor("#44000000"));
                    offView.setBackgroundColor(Color.parseColor("#00000000"));

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                } else {
                }

                return true;
            }
        });
        */

        containerView.addView(taskButton1);
        // </TASK_BUTTON>

        /*
        // <TASK_BUTTON>
        EditText taskButton2 = new EditText(context);
        taskButton2.setId(taskButton2Id);
        taskButton2.setTextSize(BUTTON_TEXT_SIZE);
        taskButton2.setHint("Interval (Period)"); // i.e., [Project][Internet][Generator]
        taskButton2.setPadding(BUTTON_INNER_PADDING_LEFT, BUTTON_INNER_PADDING_TOP, BUTTON_INNER_PADDING_RIGHT, BUTTON_INNER_PADDING_BOTTOM);
        taskButton2.setBackgroundColor(Color.parseColor(BUTTON_BACKGROUND_COLOR));
//        taskButton2.setMinWidth(0);
//        taskButton2.setMaxWidth(10);
//        taskButton2.setMinHeight(0);
//        taskButton2.setIncludeFontPadding(false);

        RelativeLayout.LayoutParams taskButton2Params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        taskButton2Params.setMargins(0, BUTTON_OUTER_PADDING_TOP, 0, 0);
//        taskButton2Params.width = TASK_BUTTON_WIDTH;
        taskButton2Params.addRule(RelativeLayout.LEFT_OF, taskButton1Id);
        taskButton2Params.addRule(RelativeLayout.CENTER_VERTICAL); // for EditText, for vertical centering
        taskButton2.setLayoutParams(taskButton2Params);

        containerView.addView(taskButton2);

        taskButton2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    // Single-Selector Handler Strategy
                    View onView = Application.getInstance().findViewById(taskButton1Id);
                    View offView = Application.getInstance().findViewById(taskButton2Id);

                    // TODO: Update state!

                    // Update style to reflect state
                    onView.setBackgroundColor(Color.parseColor("#00000000"));
                    offView.setBackgroundColor(Color.parseColor("#44000000"));

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                } else {
                }

                return true;
            }
        });
        // </TASK_BUTTON>
        */

        /*
        // <TASK_BUTTON>
        Button taskButton3 = new Button(context);
        taskButton3.setId(taskButton3Id);
        taskButton3.setText("In"); // i.e., [Project][Internet][Generator]
        taskButton3.setPadding(0, BUTTON_INNER_PADDING_TOP, 0, ViewGroupHelper.dpToPx(12));
        taskButton3.setBackgroundColor(Color.parseColor(BUTTON_BACKGROUND_COLOR));

        RelativeLayout.LayoutParams taskButton3Params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        taskButton3Params.setMargins(0, BUTTON_OUTER_PADDING_TOP, 0, 0);
//        taskButton3Params.width = TASK_BUTTON_WIDTH;
        taskButton3Params.addRule(RelativeLayout.LEFT_OF, taskButton2Id);
        taskButton3.setLayoutParams(taskButton3Params);

        containerView.addView(taskButton3);

        taskButton3.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    // TODO:

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                } else {
                }

                return true;
            }
        });
        // </TASK_BUTTON>
        */

        return containerView;
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

    public View createScriptEditorView_v2(final Script script) {

        Application.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // <PARAMETERS>
                int TASK_BUTTON_WIDTH = 150;
                // </PARAMETERS>

                final int containerViewId = generateViewId();
                final int editorViewId = generateViewId();

                final FrameLayout containerView = new FrameLayout(context);
                containerView.setId(containerViewId);
                containerView.setBackgroundColor(Color.parseColor("#bb000000"));
                // TODO: set layout_margin=20dp

                // Background Event Handler
                containerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        containerView.setVisibility(View.GONE);
                        return true;
                    }
                });

                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                containerView.addView(linearLayout, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

                Button taskButton1 = new Button(context);
                //taskButton1.setId(taskButton1Id);
                taskButton1.setText("\u2716"); // i.e., [Project][Internet][Generator]
                //button9.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));
                taskButton1.setPadding(0, ViewGroupHelper.dpToPx(12), 0, ViewGroupHelper.dpToPx(12));
                taskButton1.setBackgroundColor(Color.parseColor("#44003300"));
//        taskButton1.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                taskButton1.setMinWidth(0);
                taskButton1.setMinHeight(0);
                taskButton1.setIncludeFontPadding(false);

                FrameLayout.LayoutParams taskButton1Params = new FrameLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                taskButton1Params.setMargins(0, 0, 0, 0);
                taskButton1Params.width = TASK_BUTTON_WIDTH;
                taskButton1Params.gravity = Gravity.TOP | Gravity.RIGHT; // Button Outer Gravity
                taskButton1.setLayoutParams(taskButton1Params);
                containerView.addView(taskButton1);

                taskButton1.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                        if (touchActionType == MotionEvent.ACTION_DOWN) {
                        } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                        } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                        } else if (touchActionType == MotionEvent.ACTION_UP) {

                            // TODO: Save the script revision!
                            EditText scriptEditorView = (EditText) Application.getInstance().findViewById(editorViewId);
                            script.setCode(scriptEditorView.getText().toString());

                            View containerView = Application.getInstance().findViewById(containerViewId);
                            ((ViewManager) containerView.getParent()).removeView(containerView);

                        } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                        } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                        } else {
                        }

                        return true;
                    }
                });

                // Title: "Actions"
                TextView textView = new TextView(context);
                textView.setText("Script Editor");
                textView.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20));
                textView.setTextSize(20);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(textView);

                // EditText: Script Editor
                Typeface typeface = Typeface.createFromAsset(Application.getInstance().getAssets(), RenderSystem.NOTIFICATION_FONT);
                // Typeface boldTypeface = Typeface.create(typeface, Typeface.NORMAL);

                EditText scriptEditorView = new EditText(context);
                scriptEditorView.setId(editorViewId);
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
                    // formattedScriptText = formattedScriptText.replace("\n", "<br />");
                    // formattedScriptText = formattedScriptText.replace("\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
                }

                /*
                // <SYNTAX_HIGHLIGHTER>
                Formatter htmlFormatter = null;
                try {
                    htmlFormatter = Formatter.getByName("html");
                } catch (ResolutionException x) {
                    Log.v("Jygments", "cannot get HTML formatter");
                }

                //String contents = "var action = function(data) { clay.getPorts(); return data; }"; // submittedFile.getContents();
                String contents = script.getCode();
                String formattedText = null;
                try {
                    Lexer lexer = Lexer.getByName("javascript");
                    CharArrayWriter w = new CharArrayWriter();
                    htmlFormatter.format(lexer.getTokens(contents), w);
                    formattedText = w.toString();

                } catch (ResolutionException x) {
                    Log.v("Pygments", "cannot syntax highlight: " + x);
                    formattedText = contents;
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.v("Jygments", "formatted text: " + formattedText);

//                textPane.setText(formattedText);
//                textPane.setCaretPosition(0);
                formattedScriptText = formattedText;
                // </SYNTAX_HIGHLIGHTER>
                */

                scriptEditorView.setText(Html.fromHtml(formattedScriptText));
                scriptEditorView.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));
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
                params.setMargins(0, ViewGroupHelper.dpToPx(5), 0, 0);
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
                FrameLayout frameLayout = (FrameLayout) Application.getInstance().findViewById(Application.applicationViewId);
                frameLayout.addView(containerView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
        });

        return null;
    }

    public void openSettings() {

        Application.getInstance().runOnUiThread(new Runnable() {
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
                textView.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20));
                textView.setTextSize(20);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(textView);

                // Layout (Linear Vertical): Action List
                final LinearLayout linearLayout2 = new LinearLayout(context);
                linearLayout2.setOrientation(LinearLayout.VERTICAL);
                linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                linearLayout.addView(createButtonView("portable separation distance", null));
                linearLayout.addView(createButtonView("extension separation MIN", null));
                linearLayout.addView(createButtonView("extension separation MAX", null));

                linearLayout.addView(createButtonView("add/remove Host", null));
                linearLayout.addView(createButtonView("enable/disable notifications", null));
                linearLayout.addView(createButtonView("enable/disable vibration", null));
                linearLayout.addView(createButtonView("enable/disable network", null));

                // Title: "Settings"
                TextView debugSubtitle = new TextView(context);
                debugSubtitle.setText("Debug Section Subtitle!");
                debugSubtitle.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20));
                debugSubtitle.setTextSize(12);
                debugSubtitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                debugSubtitle.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(debugSubtitle);

                linearLayout.addView(createButtonView("debug: show monitor", null));
                linearLayout.addView(createButtonView("debug: show boundaries", null));
                linearLayout.addView(createButtonView("debug: target fps", null));
                linearLayout.addView(createButtonView("debug: sleep time", null));

                // Add to main Application View
                FrameLayout frameLayout = (FrameLayout) Application.getInstance().findViewById(Application.applicationViewId);
                frameLayout.addView(relativeLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
        });
    }

    public void openMainMenu() {

        Application.getInstance().runOnUiThread(new Runnable() {
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
                textView.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20));
                textView.setTextSize(15);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(textView);

                // Layout (Linear Vertical): Action List
                final LinearLayout linearLayout2 = new LinearLayout(context);
                linearLayout2.setOrientation(LinearLayout.VERTICAL);
                linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                linearLayout.addView(createButtonView("browse projects", null));
                linearLayout.addView(createButtonView("start from extensions", null));
                linearLayout.addView(createButtonView("free build", null));
                linearLayout.addView(createButtonView("challenge mode", null));

                // Title: "Player"
                TextView debugSubtitle = new TextView(context);
                debugSubtitle.setText("Player");
                debugSubtitle.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20));
                debugSubtitle.setTextSize(15);
                debugSubtitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                debugSubtitle.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(debugSubtitle);

                linearLayout.addView(createButtonView("Projects", null));
                linearLayout.addView(createButtonView("Inventory", null));
                linearLayout.addView(createButtonView("Ideas", null));
                linearLayout.addView(createButtonView("Friends", null));
                linearLayout.addView(createButtonView("Achievements", null));

                // Title: "Store"
                TextView storeSubtitle = new TextView(context);
                storeSubtitle.setText("Store");
                storeSubtitle.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20));
                storeSubtitle.setTextSize(15);
                storeSubtitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                storeSubtitle.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(storeSubtitle);

                linearLayout.addView(createButtonView("Clay", null));
                linearLayout.addView(createButtonView("Kits", null));
                linearLayout.addView(createButtonView("Components", null));
                linearLayout.addView(createButtonView("Accessories", null));

                // Add to main Application View
                FrameLayout frameLayout = (FrameLayout) Application.getInstance().findViewById(Application.applicationViewId);
                frameLayout.addView(relativeLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
        });
    }


    public void generateView(final String title) {

        Application.getInstance().runOnUiThread(new Runnable() {
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
                textView.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20));
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
                debugSubtitle.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20));
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
                storeSubtitle.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20));
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
                FrameLayout frameLayout = (FrameLayout) Application.getInstance().findViewById(Application.applicationViewId);
                frameLayout.addView(relativeLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
        });
    }

    int imageEditorId;

    public void openImageEditor(Entity extension) {

        Application.getInstance().runOnUiThread(new Runnable() {
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
                FrameLayout frameLayout = (FrameLayout) Application.getInstance().findViewById(Application.applicationViewId);
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
        params6.setMargins(0, ViewGroupHelper.dpToPx(5), 0, 0);
        imageView.setLayoutParams(params6);

        // ViewTreeObserver
        ViewTreeObserver vto = imageView.getViewTreeObserver();
        vto.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            public boolean onPreDraw() {

                // Remove callback to onPreDraw
                final ImageView imageView = (ImageView) Application.getInstance().findViewById(imageViewId);
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
                                View containerView = Application.getInstance().findViewById(imageEditorId);
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

                // View view = Application.getInstance().findViewById(Application.applicationViewId);
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
        textView.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));
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
        params.setMargins(0, ViewGroupHelper.dpToPx(5), 0, 0);
        textView.setLayoutParams(params);

        return textView;
    }

    public View createEditText(String text, String hintText) {

        EditText editText = new EditText(context);

        editText.setHint(hintText);

        if (text != null && text.length() > 0) {
            editText.setText(text);
        }

        editText.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, ViewGroupHelper.dpToPx(5), 0, 0);
        editText.setLayoutParams(layoutParams);

        return editText;
    }

    // TODO: Create content, style, layout parameters so they can be passed to a general function.
    private View createButtonView(final String text, final SelectEventHandler selectEventHandler) {

        // <PARAMETERS>
        int textSize = 10;
        String textColor = "#ffffffff";
        String backgroundColor = "#44000000";
        int paddingLeft = ViewGroupHelper.dpToPx(20);
        int paddingTop = ViewGroupHelper.dpToPx(12);
        int paddingRight = ViewGroupHelper.dpToPx(20);
        int paddingBottom = ViewGroupHelper.dpToPx(12);
        int marginLeft = 0;
        int marginTop = ViewGroupHelper.dpToPx(5);
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
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    Log.v("createButtonView", "Pressed Button");
                    if (selectEventHandler != null) {
                        Log.v("createButtonView", "invoking callback");
                        selectEventHandler.execute(text);
                    }

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                } else {
                }

                return true;
            }
        });

        return button;
    }

    private View createHeaderView(String text) {

        // <PARAMETERS>
        int CONTAINER_LEFT_MARGIN = ViewGroupHelper.dpToPx(0);
        int CONTAINER_TOP_MARGIN = ViewGroupHelper.dpToPx(0);
        int CONTAINER_RIGHT_MARGIN = ViewGroupHelper.dpToPx(0);
        int CONTAINER_BOTTOM_MARGIN = ViewGroupHelper.dpToPx(5);
        // ...
        int BUTTON_PADDING_LEFT = ViewGroupHelper.dpToPx(20);
        // ...
        int BUTTON_INNER_PADDING_TOP = ViewGroupHelper.dpToPx(12);
        // ...
        int BUTTON_OUTER_PADDING_TOP = 0; // was ViewGroupHelper.dpToPx(5)
        // ...
        int TASK_BUTTON_WIDTH = 150;

        String CONTAINER_BACKGROUND_COLOR = "#44000000";

        String BUTTON_BACKGROUND_COLOR = "#00000000";

        String BUTTON_TEXT_COLOR = "#ffffffff";
        // </PARAMETERS>

        // <CONTAINER_VIEW>
        final RelativeLayout containerView = new RelativeLayout(context);
        containerView.setBackgroundColor(Color.parseColor(CONTAINER_BACKGROUND_COLOR));

        LinearLayout.LayoutParams containerLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerLayoutParams.setMargins(CONTAINER_LEFT_MARGIN, CONTAINER_TOP_MARGIN, CONTAINER_RIGHT_MARGIN, CONTAINER_BOTTOM_MARGIN);

        containerView.setLayoutParams(containerLayoutParams);
        // </CONTAINER_VIEW>


        int labelButtonId = generateViewId();
        int taskButton1Id = generateViewId();


        // <LABEL_BUTTON>
        // Button: "Import Data Source"
        Button labelButton = new Button(context);
        labelButton.setId(labelButtonId);
        labelButton.setText(text); // i.e., Formerly "Add Data Source". i.e., [Project][Internet][Generator].
        labelButton.setTextColor(Color.parseColor(BUTTON_TEXT_COLOR));
        labelButton.setPadding(ViewGroupHelper.dpToPx(20), BUTTON_INNER_PADDING_TOP, ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));
        labelButton.setBackgroundColor(Color.parseColor(BUTTON_BACKGROUND_COLOR));
        labelButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        RelativeLayout.LayoutParams labelButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        labelButtonParams.setMargins(0, BUTTON_OUTER_PADDING_TOP, 0, 0);
        labelButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        labelButtonParams.addRule(RelativeLayout.LEFT_OF, taskButton1Id); // Set to left of left-most "task button view"
        labelButton.setLayoutParams(labelButtonParams);

        labelButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                } else if (touchActionType == MotionEvent.ACTION_UP) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                } else {
                }

                return true;
            }
        });

        containerView.addView(labelButton);
        // </LABEL_BUTTON>

        // <TASK_BUTTON>
        Button taskButton1 = new Button(context);
        taskButton1.setId(taskButton1Id);
        taskButton1.setText("\u2716"); // i.e., [Project][Internet][Generator]
        taskButton1.setTextColor(Color.parseColor(BUTTON_TEXT_COLOR));
        //button9.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));
        taskButton1.setPadding(0, ViewGroupHelper.dpToPx(12), 0, ViewGroupHelper.dpToPx(12));
        taskButton1.setBackgroundColor(Color.parseColor(BUTTON_BACKGROUND_COLOR));
//        taskButton1.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        taskButton1.setMinWidth(0);
        taskButton1.setMinHeight(0);
        taskButton1.setIncludeFontPadding(false);

        RelativeLayout.LayoutParams taskButton1Params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        taskButton1Params.setMargins(0, BUTTON_OUTER_PADDING_TOP, 0, 0);
        taskButton1Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        taskButton1Params.addRule(RelativeLayout.RIGHT_OF, labelButtonId);
        taskButton1Params.width = TASK_BUTTON_WIDTH;
        taskButton1.setLayoutParams(taskButton1Params);

        taskButton1.setOnTouchListener(new View.OnTouchListener() {
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

                    //View containerView = Application.getInstance().findViewById(imageEditorId);
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

        containerView.addView(taskButton1);
        // </TASK_BUTTON>

        return containerView;
    }

    private View createCloseButtonView(String text, final SelectEventHandler selectEventHandler) {

        int TOP_MARGIN = ViewGroupHelper.dpToPx(5);
        int BUTTON_PADDING_LEFT = ViewGroupHelper.dpToPx(20);
        // ...
        int TASK_BUTTON_WIDTH = 150;

        RelativeLayout containerLayout = new RelativeLayout(context);
        containerLayout.setBackgroundColor(Color.parseColor("#44000000"));

        LinearLayout.LayoutParams containerLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerLayoutParams.setMargins(0, TOP_MARGIN, 0, 0);

        containerLayout.setLayoutParams(containerLayoutParams);


        final int labelButtonId = generateViewId();
        int taskButton1Id = generateViewId();
        int taskButton2Id = generateViewId();


        // Button: "Import Data Source"
        Button labelButton = new Button(context);
        labelButton.setId(labelButtonId);
        labelButton.setText(text); // i.e., Formerly "Add Data Source". i.e., [Project][Internet][Generator].
        labelButton.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));
        labelButton.setBackgroundColor(Color.parseColor("#44000044"));
        labelButton.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
//        labelButton.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL); // Aligns button text to the left side of the button

        RelativeLayout.LayoutParams labelButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        labelButtonParams.setMargins(0, ViewGroupHelper.dpToPx(5), 0, 0);
//        params6.weight = 1;
//        params6.gravity = Gravity.LEFT;
        labelButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        labelButtonParams.addRule(RelativeLayout.LEFT_OF, taskButton2Id);
//        params6.addRule(RelativeLayout.LEFT_OF, R.id.id_to_be_left_of);
//        params6.addRule(RelativeLayout.RIGHT_OF, button8.getId());
        labelButton.setLayoutParams(labelButtonParams);
        containerLayout.addView(labelButton);

        labelButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                } else if (touchActionType == MotionEvent.ACTION_UP) {
                    selectEventHandler.execute(labelButtonId);
                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                } else {
                }

                return true;
            }
        });


        Button taskButton1 = new Button(context);
        taskButton1.setId(taskButton1Id);
        taskButton1.setText("\u2716"); // i.e., [Project][Internet][Generator]
        //button9.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));
        taskButton1.setPadding(0, ViewGroupHelper.dpToPx(12), 0, ViewGroupHelper.dpToPx(12));
        taskButton1.setBackgroundColor(Color.parseColor("#44003300"));
//        taskButton1.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        taskButton1.setMinWidth(0);
        taskButton1.setMinHeight(0);
        taskButton1.setIncludeFontPadding(false);

        RelativeLayout.LayoutParams taskButton1Params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        taskButton1Params.setMargins(0, ViewGroupHelper.dpToPx(5), 0, 0);
        taskButton1Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        taskButton1Params.addRule(RelativeLayout.RIGHT_OF, labelButtonId);
        taskButton1Params.width = TASK_BUTTON_WIDTH;
        taskButton1.setLayoutParams(taskButton1Params);
        containerLayout.addView(taskButton1);


        Button taskButton2 = new Button(context);
        taskButton2.setId(taskButton2Id);
        taskButton2.setText("\u2716"); // i.e., [Project][Internet][Generator]
        taskButton2.setPadding(0, ViewGroupHelper.dpToPx(12), 0, ViewGroupHelper.dpToPx(12));
        taskButton2.setBackgroundColor(Color.parseColor("#44330000"));
//        taskButton2.setMinWidth(0);
//        taskButton2.setMaxWidth(10);
//        taskButton2.setMinHeight(0);
//        taskButton2.setIncludeFontPadding(false);

        RelativeLayout.LayoutParams taskButton2Params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        taskButton2Params.setMargins(0, ViewGroupHelper.dpToPx(5), 0, 0);
//        taskButton1Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        taskButton2Params.width = TASK_BUTTON_WIDTH;
        taskButton2Params.addRule(RelativeLayout.LEFT_OF, taskButton1Id);
        taskButton2.setLayoutParams(taskButton2Params);

        containerLayout.addView(taskButton2);

        return containerLayout;
    }

    private View createDataImportView(String text) {

        // <PARAMETERS>
        int CONTAINER_LEFT_MARGIN = ViewGroupHelper.dpToPx(0);
        int CONTAINER_TOP_MARGIN = ViewGroupHelper.dpToPx(0);
        int CONTAINER_RIGHT_MARGIN = ViewGroupHelper.dpToPx(0);
        int CONTAINER_BOTTOM_MARGIN = ViewGroupHelper.dpToPx(5);
        // ...
        int BUTTON_PADDING_LEFT = ViewGroupHelper.dpToPx(20);
        // ...
        int BUTTON_INNER_PADDING_TOP = ViewGroupHelper.dpToPx(12);
        // ...
        int BUTTON_OUTER_PADDING_TOP = 0; // was ViewGroupHelper.dpToPx(5)
        // ...
        int TASK_BUTTON_WIDTH = 150;

        String CONTAINER_BACKGROUND_COLOR = "#44000000";

        String BUTTON_BACKGROUND_COLOR = "#00000000";

        String BUTTON_TEXT_COLOR = "#ffffffff";
        // </PARAMETERS>

        // <CONTAINER_VIEW>
        final RelativeLayout containerView = new RelativeLayout(context);
        containerView.setBackgroundColor(Color.parseColor(CONTAINER_BACKGROUND_COLOR));

        LinearLayout.LayoutParams containerLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerLayoutParams.setMargins(CONTAINER_LEFT_MARGIN, CONTAINER_TOP_MARGIN, CONTAINER_RIGHT_MARGIN, CONTAINER_BOTTOM_MARGIN);

        containerView.setLayoutParams(containerLayoutParams);
        // </CONTAINER_VIEW>


        int labelButtonId = generateViewId();
        int taskButton1Id = generateViewId();


        // <LABEL_BUTTON>
        // Button: "Import Data Source"
        Button labelButton = new Button(context);
        labelButton.setId(labelButtonId);
        labelButton.setText(text); // i.e., Formerly "Add Data Source". i.e., [Project][Internet][Generator].
        labelButton.setTextColor(Color.parseColor(BUTTON_TEXT_COLOR));
        labelButton.setPadding(ViewGroupHelper.dpToPx(20), BUTTON_INNER_PADDING_TOP, ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));
        labelButton.setBackgroundColor(Color.parseColor(BUTTON_BACKGROUND_COLOR));
        labelButton.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);

        RelativeLayout.LayoutParams labelButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        labelButtonParams.setMargins(0, BUTTON_OUTER_PADDING_TOP, 0, 0);
        labelButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        labelButtonParams.addRule(RelativeLayout.LEFT_OF, taskButton1Id); // Set to left of left-most "task button view"
        labelButton.setLayoutParams(labelButtonParams);

        labelButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                } else if (touchActionType == MotionEvent.ACTION_UP) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                } else {
                }

                return true;
            }
        });

        containerView.addView(labelButton);
        // </LABEL_BUTTON>

        // <TASK_BUTTON>
        Button taskButton1 = new Button(context);
        taskButton1.setId(taskButton1Id);
        taskButton1.setText("\u2716"); // i.e., [Project][Internet][Generator]
        taskButton1.setTextColor(Color.parseColor(BUTTON_TEXT_COLOR));
        //button9.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(12));
        taskButton1.setPadding(0, ViewGroupHelper.dpToPx(12), 0, ViewGroupHelper.dpToPx(12));
        taskButton1.setBackgroundColor(Color.parseColor(BUTTON_BACKGROUND_COLOR));
//        taskButton1.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        taskButton1.setMinWidth(0);
        taskButton1.setMinHeight(0);
        taskButton1.setIncludeFontPadding(false);

        RelativeLayout.LayoutParams taskButton1Params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        taskButton1Params.setMargins(0, BUTTON_OUTER_PADDING_TOP, 0, 0);
        taskButton1Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        taskButton1Params.addRule(RelativeLayout.RIGHT_OF, labelButtonId);
        taskButton1Params.width = TASK_BUTTON_WIDTH;
        taskButton1.setLayoutParams(taskButton1Params);

        taskButton1.setOnTouchListener(new View.OnTouchListener() {
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

                    //View containerView = Application.getInstance().findViewById(imageEditorId);
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

        containerView.addView(taskButton1);
        // </TASK_BUTTON>

        return containerView;
    }

    private View createNewActionView_v5(final Action action) {

        int CONTAINER_LEFT_MARGIN = ViewGroupHelper.dpToPx(0);
        int CONTAINER_TOP_MARGIN = ViewGroupHelper.dpToPx(0);
        int CONTAINER_RIGHT_MARGIN = ViewGroupHelper.dpToPx(0);
        int CONTAINER_BOTTOM_MARGIN = ViewGroupHelper.dpToPx(0);

        int BUTTON_INNER_PADDING_LEFT = ViewGroupHelper.dpToPx(20);
        int BUTTON_INNER_PADDING_TOP = ViewGroupHelper.dpToPx(12); // ViewGroupHelper.dpToPx(12)
        int BUTTON_INNER_PADDING_RIGHT = ViewGroupHelper.dpToPx(20);
        int BUTTON_INNER_PADDING_BOTTOM = ViewGroupHelper.dpToPx(12); // ViewGroupHelper.dpToPx(12)

        int BUTTON_OUTER_PADDING_LEFT = 0; // was ViewGroupHelper.dpToPx(5)
        int BUTTON_OUTER_PADDING_TOP = 0; // was ViewGroupHelper.dpToPx(5)
        int BUTTON_OUTER_PADDING_RIGHT = 0; // was ViewGroupHelper.dpToPx(5)
        int BUTTON_OUTER_PADDING_BOTTOM = 0; // was ViewGroupHelper.dpToPx(5)

        int TASK_BUTTON_WIDTH = 150;

        String CONTAINER_BACKGROUND_COLOR = "#88000000";

        String BUTTON_BACKGROUND_COLOR = "#00000000";

        int BUTTON_TEXT_SIZE = 10;
        String BUTTON_TEXT_COLOR = "#ffffffff";

        final RelativeLayout containerView = new RelativeLayout(context);
        containerView.setBackgroundColor(Color.parseColor(CONTAINER_BACKGROUND_COLOR));

        LinearLayout.LayoutParams containerLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        containerLayoutParams.setMargins(CONTAINER_LEFT_MARGIN, CONTAINER_TOP_MARGIN, CONTAINER_RIGHT_MARGIN, CONTAINER_BOTTOM_MARGIN);

        containerView.setLayoutParams(containerLayoutParams);


        int labelButtonId = generateViewId();
        int taskButton1Id = generateViewId();
        int taskButton2Id = generateViewId();


        // Button: "Import Data Source"
        Button labelButton = new Button(context);
        labelButton.setId(labelButtonId);
        labelButton.setText(action.getTitle() + "\n (" + action.getScript().getCode().length() + " lines)"); // i.e., Formerly "Add Data Source". i.e., [Project][Internet][Generator].
        labelButton.setTextColor(Color.parseColor(BUTTON_TEXT_COLOR));
        labelButton.setTextSize(BUTTON_TEXT_SIZE);
        labelButton.setIncludeFontPadding(false);
        labelButton.setAllCaps(false);
        labelButton.setTypeface(null, Typeface.NORMAL);
        labelButton.setPadding(BUTTON_INNER_PADDING_LEFT, BUTTON_INNER_PADDING_TOP, BUTTON_INNER_PADDING_RIGHT, BUTTON_INNER_PADDING_BOTTOM);
        labelButton.setBackgroundColor(Color.parseColor(BUTTON_BACKGROUND_COLOR));
        labelButton.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
//        labelButton.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL); // Aligns button text to the left side of the button
        labelButton.setMinHeight(0);

        RelativeLayout.LayoutParams labelButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        labelButtonParams.setMargins(BUTTON_OUTER_PADDING_LEFT, BUTTON_OUTER_PADDING_TOP, BUTTON_OUTER_PADDING_RIGHT, BUTTON_OUTER_PADDING_BOTTOM);
        labelButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        labelButtonParams.addRule(RelativeLayout.LEFT_OF, taskButton1Id); // Set to left of left-most "task button view"
        labelButton.setLayoutParams(labelButtonParams);

        labelButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    // Open Script Editor
                    openScriptEditor(action.getScript());

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                } else {
                }

                return true;
            }
        });

        containerView.addView(labelButton);


        Button taskButton1 = new Button(context);
        taskButton1.setId(taskButton1Id);
        taskButton1.setText("\u2716"); // i.e., [Project][Internet][Generator]
        taskButton1.setTextColor(Color.parseColor(BUTTON_TEXT_COLOR));
        taskButton1.setTextSize(BUTTON_TEXT_SIZE);
        taskButton1.setIncludeFontPadding(false);
        taskButton1.setAllCaps(false);
        taskButton1.setTypeface(null, Typeface.NORMAL);
        //taskButton1.setPadding(BUTTON_INNER_PADDING_LEFT, BUTTON_INNER_PADDING_TOP, BUTTON_INNER_PADDING_RIGHT, BUTTON_INNER_PADDING_BOTTOM);
        taskButton1.setPadding(0, ViewGroupHelper.dpToPx(12), 0, ViewGroupHelper.dpToPx(12));
        taskButton1.setBackgroundColor(Color.parseColor(BUTTON_BACKGROUND_COLOR));
//        taskButton1.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        taskButton1.setMinWidth(0);
        taskButton1.setMinHeight(0);

        RelativeLayout.LayoutParams taskButton1Params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        taskButton1Params.setMargins(0, BUTTON_OUTER_PADDING_TOP, 0, 0);
        taskButton1Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        taskButton1Params.addRule(RelativeLayout.RIGHT_OF, labelButtonId);
        taskButton1Params.width = TASK_BUTTON_WIDTH;
        taskButton1.setLayoutParams(taskButton1Params);

        taskButton1.setOnTouchListener(new View.OnTouchListener() {
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

                    //View containerView = Application.getInstance().findViewById(imageEditorId);
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

        containerView.addView(taskButton1);


        final Button taskButton2 = new Button(context);
        taskButton2.setId(taskButton2Id);
        taskButton2.setText("\uD83D\uDD0D"); // i.e., [Project][Internet][Generator]
        taskButton2.setTextColor(Color.parseColor(BUTTON_TEXT_COLOR));
        taskButton2.setTextSize(BUTTON_TEXT_SIZE);
        taskButton2.setIncludeFontPadding(false);
        taskButton2.setAllCaps(false);
        taskButton2.setTypeface(null, Typeface.NORMAL);
        //taskButton1.setPadding(BUTTON_INNER_PADDING_LEFT, BUTTON_INNER_PADDING_TOP, BUTTON_INNER_PADDING_RIGHT, BUTTON_INNER_PADDING_BOTTOM);
        taskButton2.setPadding(0, BUTTON_INNER_PADDING_TOP, 0, ViewGroupHelper.dpToPx(12));
        taskButton2.setBackgroundColor(Color.parseColor("#44330000"));
//        taskButton2.setMinWidth(0);
//        taskButton2.setMaxWidth(10);
        taskButton2.setMinHeight(0);
//        taskButton2.setIncludeFontPadding(false);

        RelativeLayout.LayoutParams taskButton2Params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        taskButton2Params.setMargins(0, BUTTON_OUTER_PADDING_TOP, 0, 0);
//        taskButton1Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        taskButton2Params.width = TASK_BUTTON_WIDTH;
        taskButton2Params.addRule(RelativeLayout.LEFT_OF, taskButton1Id);
        taskButton2.setLayoutParams(taskButton2Params);

        taskButton2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    final List<Action> actions = World.getWorld().repository.getActions();

                    // TODO: Color.parseColor(camp.computer.clay.util.Color.getColor(Port.getType(ports.get(i))))

                    taskButton2.setBackgroundColor(
                            Color.argb(
                                    255,
                                    Random.generateRandomInteger(0, 255),
                                    Random.generateRandomInteger(0, 255),
                                    Random.generateRandomInteger(0, 255)
                            )
                    );

//                    createListView(
//                            actions,
//                            new LabelMapper<Action>() {
//                                @Override
//                                public String map(Action action) {
//                                    return action.getTitle();
//                                }
//                            },
//                            new SelectEventHandler<Action>() {
//                                @Override
//                                public void execute(Action selectedAction) {
//                                    //Toast.makeText(getBaseContext(), ""+arg2,     Toast.LENGTH_SHORT).show();
//                                    Log.v("ListView", "selected: " + selectedAction.getTitle());
//
//                                    taskButton2.setBackgroundColor(Color.RED);
//
////                                    // Add Action to Process
////                                    process.addAction(selectedAction);
////
////                                    // Replace with new View
////                                    View actionView = createActionView_v1(selectedAction);
////                                    ViewGroupHelper.replaceView(containerView, actionView);
//                                }
//                            }
//                    );

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                } else {
                }
                return true;
            }
        });

        containerView.addView(taskButton2);

        return containerView;
    }
    // </BASIC_UI>
}

//----------------- REBUILD THESE!

//----------------------------------------------------------------------------------------------

//    // References:
//    // - http://stackoverflow.com/questions/4165414/how-to-hide-soft-keyboard-on-android-after-clicking-outside-edittext
//    boolean isTitleEditorInitialized = false;
//
//    public void openTitleEditor(String title) {
//        final RelativeLayout titleEditor = (RelativeLayout) findViewById(R.id.title_editor_view);
//
//        // Initialize Text
//        final EditText titleText = (EditText) findViewById(R.id.title_editor_text);
//        titleText.setText(title);
//
//        // Configure Text Editor
//        if (isTitleEditorInitialized == false) {
//
//            /*
//            // Set the font face
//            Typeface type = Typeface.createFromAsset(getAssets(), "fonts/Dosis-Light.ttf");
//            titleText.setTypeface(type);
//            */
//
//            // Configure to hide keyboard when a touch occurs anywhere except the text
//            titleText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//                @Override
//                public void onFocusChange(View v, boolean hasFocus) {
//                    if (!hasFocus) {
//                        hideKeyboard(v);
//                    }
//                }
//            });
//
//            // Configure touch interaction
//            titleText.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (v.getId() == titleText.getId()) {
//
//                        // Move the cursor to the end of the line
//                        titleText.setSelection(titleText.getText().length());
//
//                        // Show the cursor
//                        titleText.setCursorVisible(true);
//                    }
//                }
//            });
//
//            isTitleEditorInitialized = true;
//        }
//
//        titleText.setCursorVisible(false);
//
//        titleEditor.setVisibility(View.VISIBLE);
//
//        /*
//        // Now Set your animation
//        Animation fadeInAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_in_animation);
//        titleText.startAnimation(fadeInAnimation);
//
//        // Callback to hide editor
//        startTitleEditorService();
//        */
//    }

//    public void setTitleEditor(String title) {
//        // Update the Text
//        final EditText titleText = (EditText) findViewById(R.id.title_editor_text);
//        titleText.setText(title);
//
//        /*
//        // Callback to hide editor
//        startTitleEditorService();
//        */
//    }

//    public void closeTitleEditor() {
//        final RelativeLayout titleEditor = (RelativeLayout) findViewById(R.id.title_editor_view);
//
//        final EditText titleText = (EditText) findViewById(R.id.title_editor_text);
//
//        titleEditor.setVisibility(View.INVISIBLE);
//
//        /*
//        // Now Set your animation
//        Animation fadeOutAnimation = AnimationUtils.loadAnimation(this, R.anim.fade_out_animation);
//
//        fadeOutAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                titleEditor.setImageVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//
//        titleText.startAnimation(fadeOutAnimation);
//        */
//    }

//    private Handler titleEditorServiceHandler = new Handler();
//    private Runnable titleEditorServiceRunnable = new Runnable() {
//        @Override
//        public void run() {
//            // Do what you need to do.
//            // e.g., foobar();
//            closeTitleEditor();
//
////            // Uncomment this for periodic callback
////            if (enableFullscreenService) {
////                fullscreenServiceHandler.postDelayed(this, FULLSCREEN_SERVICE_PERIOD);
////            }
//        }
//    };
//
//    private void startTitleEditorService() {
//        titleEditorServiceHandler.postDelayed(titleEditorServiceRunnable, 5000);
//    }
//
//    public void hideKeyboard(View view) {
//        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
//    }


/*
private void addPathExtensionAction() {

    final TextView actionConstruct = new TextView(getContext());
    actionConstruct.setText("Event (<PortEntity> <PortEntity> ... <PortEntity>)\nExpose: <PortEntity> <PortEntity> ... <PortEntity>");
    int horizontalPadding = (int) ViewGroupHelper.dpToPx(20);
    int verticalPadding = (int) ViewGroupHelper.dpToPx(10);
    actionConstruct.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
    actionConstruct.setBackgroundColor(Color.parseColor("#44000000"));

    final LinearLayout pathPatchActionList = (LinearLayout) findViewById(R.id.path_editor_action_list);

    actionConstruct.setOnTouchListener(new View.OnTouchListener() {
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

                pathPatchActionList.removeView(actionConstruct);

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

    pathPatchActionList.addView(actionConstruct);
}
*/

/*
// PathEntity Editor
final RelativeLayout pathEditor = (RelativeLayout) findViewById(R.id.action_editor_view);
pathEditor.setOnTouchListener(new View.OnTouchListener() {
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        pathEditor.setVisibility(View.GONE);
        return true;
    }
});

final Button pathEditorAddActionButton = (Button) findViewById(R.id.path_editor_add_action);
pathEditorAddActionButton.setOnTouchListener(new View.OnTouchListener() {
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

            addPathExtensionAction();

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