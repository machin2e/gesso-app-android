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
import camp.computer.clay.engine.component.Scriptable;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.util.Signal;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.engine.manager.Group;
import camp.computer.clay.platform.Application;
import camp.computer.clay.platform.tasks.HttpRequestTasks;
import camp.computer.clay.platform.util.ViewGroupHelper;
import camp.computer.clay.structure.Action;
import camp.computer.clay.structure.Script;
import camp.computer.clay.structure.configuration.Configuration;
import camp.computer.clay.util.Random;

public class Widgets {

    private Context context = null;

    public Widgets(Context context) {
        this.context = context;
    }

    // TODO: Replace with EventListener?
    public interface OnActionListener<T> {
        void onComplete(T result);
    }

    public interface LabelMapper<T> {
        String map(T element);
    }

    // TODO: Replace with EventListener?
    public interface SelectEventHandler<T> {
        void execute(T selection);
    }

    abstract static class Parameters {
        static class DefaultContainerView {
            static String CONTAINER_BACKGROUND_COLOR = "#44000000";

            static int CONTAINER_INNER_PADDING_LEFT = ViewGroupHelper.dpToPx(0);
            static int CONTAINER_INNER_PADDING_TOP = ViewGroupHelper.dpToPx(0);
            static int CONTAINER_INNER_PADDING_RIGHT = ViewGroupHelper.dpToPx(0);
            static int CONTAINER_INNER_PADDING_BOTTOM = ViewGroupHelper.dpToPx(5);

            static int CONTAINER_LEFT_MARGIN = ViewGroupHelper.dpToPx(0);
            static int CONTAINER_TOP_MARGIN = ViewGroupHelper.dpToPx(0);
            static int CONTAINER_RIGHT_MARGIN = ViewGroupHelper.dpToPx(0);
            static int CONTAINER_BOTTOM_MARGIN = ViewGroupHelper.dpToPx(5);
        }

        static class ActionContainerView {
            static String CONTAINER_BACKGROUND_COLOR = "#88000000";

            static int CONTAINER_INNER_PADDING_LEFT = ViewGroupHelper.dpToPx(0);
            static int CONTAINER_INNER_PADDING_TOP = ViewGroupHelper.dpToPx(0);
            static int CONTAINER_INNER_PADDING_RIGHT = ViewGroupHelper.dpToPx(0);
            static int CONTAINER_INNER_PADDING_BOTTOM = ViewGroupHelper.dpToPx(5);

            static int CONTAINER_LEFT_MARGIN = ViewGroupHelper.dpToPx(0);
            static int CONTAINER_TOP_MARGIN = ViewGroupHelper.dpToPx(0);
            static int CONTAINER_RIGHT_MARGIN = ViewGroupHelper.dpToPx(0);
            static int CONTAINER_BOTTOM_MARGIN = ViewGroupHelper.dpToPx(5);
        }

        static class DefaultTitleButton {
            static String BUTTON_TEXT_COLOR = "#ffffffff";
            static int BUTTON_TEXT_SIZE = 10;
            static String BUTTON_BACKGROUND_COLOR = "#00000000";

            static int BUTTON_INNER_PADDING_LEFT = ViewGroupHelper.dpToPx(20);
            static int BUTTON_INNER_PADDING_TOP = ViewGroupHelper.dpToPx(12);
            static int BUTTON_INNER_PADDING_RIGHT = ViewGroupHelper.dpToPx(20);
            static int BUTTON_INNER_PADDING_BOTTOM = ViewGroupHelper.dpToPx(12);

            static int BUTTON_OUTER_PADDING_LEFT = 0;
            static int BUTTON_OUTER_PADDING_TOP = 0;
            static int BUTTON_OUTER_PADDING_RIGHT = 0;
            static int BUTTON_OUTER_PADDING_BOTTOM = 0;
        }

        static class ActionTitleButton {
            static String BUTTON_TEXT_COLOR = "#ffffffff";
            static int BUTTON_TEXT_SIZE = 10;
            static String BUTTON_BACKGROUND_COLOR = "#00000000";

            static int BUTTON_INNER_PADDING_LEFT = ViewGroupHelper.dpToPx(20);
            static int BUTTON_INNER_PADDING_TOP = ViewGroupHelper.dpToPx(12);
            static int BUTTON_INNER_PADDING_RIGHT = ViewGroupHelper.dpToPx(20);
            static int BUTTON_INNER_PADDING_BOTTOM = ViewGroupHelper.dpToPx(12);

            static int BUTTON_OUTER_PADDING_LEFT = 0;
            static int BUTTON_OUTER_PADDING_TOP = 0;
            static int BUTTON_OUTER_PADDING_RIGHT = 0;
            static int BUTTON_OUTER_PADDING_BOTTOM = 0;
        }

        static class DefaultTaskButton {
            static String BUTTON_TEXT_COLOR = "#ffffffff";
            static int BUTTON_TEXT_SIZE = 10;
            static String BUTTON_BACKGROUND_COLOR = "#00000000";

            static int TASK_BUTTON_WIDTH = 150;

            static int BUTTON_INNER_PADDING_LEFT = ViewGroupHelper.dpToPx(0);
            static int BUTTON_INNER_PADDING_TOP = ViewGroupHelper.dpToPx(12);
            static int BUTTON_INNER_PADDING_RIGHT = ViewGroupHelper.dpToPx(0);
            static int BUTTON_INNER_PADDING_BOTTOM = ViewGroupHelper.dpToPx(12);

            static int BUTTON_OUTER_PADDING_LEFT = 0;
            static int BUTTON_OUTER_PADDING_TOP = 0;
            static int BUTTON_OUTER_PADDING_RIGHT = 0;
            static int BUTTON_OUTER_PADDING_BOTTOM = 0;
        }

        public enum Alignment {
            LEFT,
            CENTER,
            RIGHT
        }
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

    // <REFACTOR>
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
    // </REFACTOR>

    public void openCreateExtensionView(final OnActionListener onActionListener) {

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

                linearLayout.addView(createButton("Save", new SelectEventHandler<String>() {
                    @Override
                    public void execute(String selection) {
                        Log.v("createButton", "got callback! " + selection);
                        String extensionTitle = extensionTitleView.getText().toString();
                        Log.v("createButton", "extension title: " + extensionTitle);
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

                        // Start Interactive Plan
                        openInteractiveAssemblyTaskOverview();
                    }
                }
        );
    }

    // Break multi-updateImage tasks up into a sequence of floating interface elements that must be completed to continue (or abandon the sequence)
    // displayFloatingTaskDialog(<task list>, <task updateImage to display>)

    public void openInteractiveAssemblyTaskOverview() { // was "openInteractiveAssemblyTaskOverview"

        // TODO: Generate assembly instructions if they don't already exist for the Extension.

        // <REFACTOR>
        // TODO: Put assembly instructions into a separate class.
        // Show Extension Browser
        ArrayList<String> assemblyInstructions = new ArrayList<>();
        assemblyInstructions.add("Step 1");
        assemblyInstructions.add("Step 2");
        assemblyInstructions.add("Step 3");
        assemblyInstructions.add("Step 4");
        assemblyInstructions.add("Step 5");
        // </REFACTOR>

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

                        // Start Interactive Plan
                        openInteractiveAssemblyTaskView();
                    }
                }
        );

        // TODO: "Start Plan" Button
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

                        // Start Interactive Plan
                        openInteractiveAssemblyTaskView();
                    }
                }
        );
        // </REPLACE>

        // TODO: Add "Next" button

    }

    boolean reqeustedActions = false;

    public void createActionEditor_v3(final Entity extension) {

        // TODO: Return View IDs and add listeners after creating view structure.

        // NOTE: This is just a list of edit boxes. Each with a dropdown to save new script or load from the list. MVP, bitches.

        // <REFACTOR>
        // TODO: Relocate so these are stored in Cache_OLD.
        // Cache_OLD Action and Script in Repository. Retrieve Actions and Scripts from Remote Server.
        if (!reqeustedActions) {
            HttpRequestTasks.HttpRequestTask httpRequestTask = new HttpRequestTasks.HttpRequestTask();
            httpRequestTask.uri = HttpRequestTasks.DEFAULT_HTTP_GET_ACTIONS_URI;
            new HttpRequestTasks.HttpGetRequestTask().execute(httpRequestTask); // TODO: Add GET request to enqueue in Application startup... in an Engine System
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

        final int containerViewId = generateViewId();
        final int addActionButtonId = generateViewId();
        final int testActionsButtonId = generateViewId();

        Application.getInstance().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                // <CONTAINER_VIEW>
                final FrameLayout containerView = new FrameLayout(context);
                containerView.setId(containerViewId);

                containerView.setPadding(
                        Parameters.DefaultContainerView.CONTAINER_INNER_PADDING_LEFT,
                        Parameters.DefaultContainerView.CONTAINER_INNER_PADDING_TOP,
                        Parameters.DefaultContainerView.CONTAINER_INNER_PADDING_RIGHT,
                        Parameters.DefaultContainerView.CONTAINER_INNER_PADDING_BOTTOM
                );

                containerView.setBackgroundColor(Color.parseColor(Parameters.DefaultContainerView.CONTAINER_BACKGROUND_COLOR));

                // <REFACTOR>
                // Background Event Handler
                containerView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        // containerView.setVisibility(View.GONE);
                        return true;
                    }
                });
                // </REFACTOR>

                FrameLayout.LayoutParams containerViewLayoutParams = new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );

                containerViewLayoutParams.setMargins(0, 0, 0, 0);
                containerViewLayoutParams.gravity = Gravity.TOP;

                containerView.setLayoutParams(containerViewLayoutParams);

                // ScrollView
                final ScrollView containerScrollView = new ScrollView(context);
                LinearLayout linearLayout = new LinearLayout(context);
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                containerScrollView.addView(
                        linearLayout,
                        new RelativeLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                );

                containerView.addView(containerScrollView);
                // </CONTAINER_VIEW>

                // <HEADER_VIEW>
                View titleView = createHeaderView("Extension Controller", new SelectEventHandler<Integer>() {
                    @Override
                    public void execute(Integer sourceViewId) {
                        View containerView = Application.getInstance().findViewById(containerViewId);
                        ((ViewManager) containerView.getParent()).removeView(containerView);
                    }
                });
                linearLayout.addView(titleView);
                // </HEADER_VIEW>

                // <DATA_CHOOSER>
                View findDataView = createHeaderView("Find Data", new SelectEventHandler<Integer>() {
                    @Override
                    public void execute(Integer sourceViewId) {

                        // TODO: Handle "Find Data" close button
                        // View containerView = Application.getInstance().findViewById(containerViewId);
                        // ((ViewManager) containerView.getParent()).removeView(containerView);
                    }
                });
                linearLayout.addView(findDataView);
                // </DATA_CHOOSER>

                // <ACTION_LIST>
                final LinearLayout actionListLayout = new LinearLayout(context);
                actionListLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.addView(
                        actionListLayout,
                        new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                );
                // </ACTION_LIST>

                // <PORT_CONTROLLERS>
                LinearLayout portableLayout = new LinearLayout(context);
                portableLayout.setOrientation(LinearLayout.HORIZONTAL);
                linearLayout.addView(portableLayout);

                for (int i = 0; i < ports.size(); i++) {

                    if (Port.getType(ports.get(i)) == Signal.Type.SWITCH) {
                        // <DIGITAL_PORT_CONTROL>
                        View digitalPortLayout = createSwitchControllerView(ports.get(i));
                        linearLayout.addView(digitalPortLayout);
                        // </DIGITAL_PORT_CONTROL>
                    }

                    if (Port.getType(ports.get(i)) == Signal.Type.PULSE) {
                        // <PWM_PORT_CONTROL>
                        View pulsePortLayout = createPulseControllerView(ports.get(i));
                        linearLayout.addView(pulsePortLayout);
                        // </PWM_PORT_CONTROL>
                    }

                    if (Port.getType(ports.get(i)) == Signal.Type.WAVE) {
                        // <ADC_PORT_CONTROL>
                        View wavePortLayout = createWaveControllerView(ports.get(i));
                        linearLayout.addView(wavePortLayout);
                        // </ADC_PORT_CONTROL>
                    }
                }
                // </PORT_CONTROLLERS>

                // <ADD_ACTION_BUTTON>
                // Button: "Add Action"
                Button button2 = new Button(context);
                button2.setId(addActionButtonId);

                button2.setText("Add Action");
                button2.setTextColor(Color.parseColor(Parameters.DefaultTitleButton.BUTTON_TEXT_COLOR));

                button2.setPadding(
                        Parameters.DefaultTitleButton.BUTTON_INNER_PADDING_LEFT,
                        Parameters.DefaultTitleButton.BUTTON_INNER_PADDING_TOP,
                        Parameters.DefaultTitleButton.BUTTON_INNER_PADDING_RIGHT,
                        Parameters.DefaultTitleButton.BUTTON_INNER_PADDING_BOTTOM
                );

                button2.setBackgroundColor(Color.parseColor(Parameters.DefaultTitleButton.BUTTON_BACKGROUND_COLOR));

                LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                params2.setMargins(
                        Parameters.DefaultTitleButton.BUTTON_OUTER_PADDING_LEFT,
                        Parameters.DefaultTitleButton.BUTTON_OUTER_PADDING_TOP, // ViewGroupHelper.dpToPx(5)
                        Parameters.DefaultTitleButton.BUTTON_OUTER_PADDING_RIGHT,
                        Parameters.DefaultTitleButton.BUTTON_OUTER_PADDING_BOTTOM
                );

                button2.setLayoutParams(params2);
                linearLayout.addView(button2);

                // <REFACTOR>
                button2.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent motionEvent) {

                        int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                        // Update the state of the touched object based on the current pointerCoordinates interaction state.
                        if (touchActionType == MotionEvent.ACTION_DOWN) {
                        } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                        } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                        } else if (touchActionType == MotionEvent.ACTION_UP) {

                            final List<Action> actions = (List<Action>) World.getInstance().cache.getObjects(Action.class); // repository.getActions();

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
                                            extension.getComponent(Scriptable.class).process.addAction(selectedAction);

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

                        } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                        } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                        } else {
                        }

                        return true;
                    }
                });
                // </REFACTOR>

                // </ADD_ACTION_BUTTON>

                // <TEST_BUTTON>
                // Button: Test Script
                Button button3 = new Button(context);
                button3.setId(testActionsButtonId);

                button3.setText("Test");
                button3.setTextColor(Color.parseColor(Parameters.DefaultTitleButton.BUTTON_TEXT_COLOR));

                button3.setPadding(
                        Parameters.DefaultTitleButton.BUTTON_INNER_PADDING_LEFT,
                        Parameters.DefaultTitleButton.BUTTON_INNER_PADDING_TOP,
                        Parameters.DefaultTitleButton.BUTTON_INNER_PADDING_RIGHT,
                        Parameters.DefaultTitleButton.BUTTON_INNER_PADDING_BOTTOM
                );

                button3.setBackgroundColor(Color.parseColor(Parameters.DefaultTitleButton.BUTTON_BACKGROUND_COLOR));

                // <REFACTOR>
                button3.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent motionEvent) {

                        int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                        // Update the state of the touched object based on the current pointerCoordinates interaction state.
                        if (touchActionType == MotionEvent.ACTION_DOWN) {
                        } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                        } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                        } else if (touchActionType == MotionEvent.ACTION_UP) {

                            List<Action> actions = extension.getComponent(Scriptable.class).process.getActions();
                            for (int i = 0; i < actions.size(); i++) {
                                Log.v("ActionProcess", "" + i + ": " + actions.get(i).getTitle());
                            }

                            // <REFACTOR>
                            // TODO: Queue HTTP request on event enqueue.
                            // Send complete scripts to Hosts
                            HttpRequestTasks.HttpRequestTask httpRequestTask = new HttpRequestTasks.HttpRequestTask();
                            httpRequestTask.entity = extension;
                            new HttpRequestTasks.HttpPostRequestTask().execute(httpRequestTask); // TODO: Replace with queueHttpTask(httpTask)
                            // </REFACTOR>

                        } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                        } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                        } else {
                        }

                        return true;
                    }
                });
                // </REFACTOR>

                LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params2.setMargins(
                        Parameters.DefaultTitleButton.BUTTON_OUTER_PADDING_LEFT,
                        Parameters.DefaultTitleButton.BUTTON_OUTER_PADDING_TOP, // ViewGroupHelper.dpToPx(5)
                        Parameters.DefaultTitleButton.BUTTON_OUTER_PADDING_RIGHT,
                        Parameters.DefaultTitleButton.BUTTON_OUTER_PADDING_BOTTOM
                );

                button3.setLayoutParams(params3);
                linearLayout.addView(button3);
                // </TEST_BUTTON>

                // Add to main Application View
                FrameLayout frameLayout = (FrameLayout) Application.getInstance().findViewById(Application.applicationViewId);
                frameLayout.addView(containerView, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));


                // <POPULATE_DATA>
                List<Action> actions = extension.getComponent(Scriptable.class).process.getActions();
                for (int i = 0; i < actions.size(); i++) {
                    Log.v("ActionProcess", "" + i + ": " + actions.get(i).getTitle());
                    View actionView = createNewActionView_v5(actions.get(i));
                    actionListLayout.addView(actionView);
                }
                // </POPULATE_DATA>
            }
        });
    }

    private View createHeaderView(String text, final SelectEventHandler taskButtonEventHandler) {

        int containerViewId = generateViewId();
        int labelButtonId = generateViewId();
        final int taskButton1Id = generateViewId();

        // <CONTAINER_VIEW>
        final RelativeLayout containerView = new RelativeLayout(context);
        containerView.setId(containerViewId);
        containerView.setBackgroundColor(Color.parseColor(Parameters.DefaultContainerView.CONTAINER_BACKGROUND_COLOR));

        LinearLayout.LayoutParams containerViewLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        containerViewLayoutParams.setMargins(
                Parameters.DefaultContainerView.CONTAINER_LEFT_MARGIN,
                Parameters.DefaultContainerView.CONTAINER_TOP_MARGIN,
                Parameters.DefaultContainerView.CONTAINER_RIGHT_MARGIN,
                Parameters.DefaultContainerView.CONTAINER_BOTTOM_MARGIN
        );

        containerView.setLayoutParams(containerViewLayoutParams);
        // </CONTAINER_VIEW>

        // <LABEL_BUTTON>
        Button labelButton = new Button(context);
        labelButton.setId(labelButtonId);

        labelButton.setText(text); // i.e., Formerly "Add Data Source". i.e., [Project][Internet2][Generator].
        labelButton.setTextColor(Color.parseColor(Parameters.DefaultTitleButton.BUTTON_TEXT_COLOR));
        labelButton.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        labelButton.setBackgroundColor(Color.parseColor(Parameters.DefaultTitleButton.BUTTON_BACKGROUND_COLOR));

        labelButton.setPadding(
                Parameters.DefaultTitleButton.BUTTON_INNER_PADDING_LEFT,
                Parameters.DefaultTitleButton.BUTTON_INNER_PADDING_TOP,
                Parameters.DefaultTitleButton.BUTTON_INNER_PADDING_RIGHT,
                Parameters.DefaultTitleButton.BUTTON_INNER_PADDING_BOTTOM

        );

        RelativeLayout.LayoutParams labelButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );
        labelButtonParams.setMargins(Parameters.DefaultTitleButton.BUTTON_OUTER_PADDING_LEFT, Parameters.DefaultTitleButton.BUTTON_OUTER_PADDING_TOP, Parameters.DefaultTitleButton.BUTTON_OUTER_PADDING_RIGHT, Parameters.DefaultTitleButton.BUTTON_OUTER_PADDING_BOTTOM);
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

        taskButton1.setText("\u2716"); // i.e., [Project][Internet2][Generator]
        taskButton1.setTextColor(Color.parseColor(Parameters.DefaultTaskButton.BUTTON_TEXT_COLOR));

        taskButton1.setBackgroundColor(Color.parseColor(Parameters.DefaultTaskButton.BUTTON_BACKGROUND_COLOR));

        taskButton1.setPadding(
                Parameters.DefaultTaskButton.BUTTON_INNER_PADDING_LEFT,
                Parameters.DefaultTaskButton.BUTTON_INNER_PADDING_TOP,
                Parameters.DefaultTaskButton.BUTTON_INNER_PADDING_RIGHT,
                Parameters.DefaultTaskButton.BUTTON_INNER_PADDING_BOTTOM
        );

        // taskButton1.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        taskButton1.setMinWidth(0);
        taskButton1.setMinHeight(0);
        taskButton1.setIncludeFontPadding(false);

        RelativeLayout.LayoutParams taskButton1Params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        taskButton1Params.setMargins(
                Parameters.DefaultTaskButton.BUTTON_OUTER_PADDING_LEFT,
                Parameters.DefaultTaskButton.BUTTON_OUTER_PADDING_TOP,
                Parameters.DefaultTaskButton.BUTTON_OUTER_PADDING_RIGHT,
                Parameters.DefaultTaskButton.BUTTON_OUTER_PADDING_BOTTOM
        );

        taskButton1Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        taskButton1Params.addRule(RelativeLayout.RIGHT_OF, labelButtonId);
        taskButton1Params.width = Parameters.DefaultTaskButton.TASK_BUTTON_WIDTH;
        taskButton1.setLayoutParams(taskButton1Params);

        // <REFACTOR>
        taskButton1.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    //View containerView = Application.getInstance().findViewById(imageEditorId);
//                    ((ViewManager) containerView.getParent()).removeView(containerView);

                    // TODO: Change parameter to something more useful...
                    taskButtonEventHandler.execute(taskButton1Id);

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                } else {
                }

                return true;
            }
        });
        // </REFACTOR>

        containerView.addView(taskButton1);
        // </TASK_BUTTON>

        return containerView;
    }

    private View createNewActionView_v5(final Action action) {

        int containerViewId = generateViewId();
        int labelButtonId = generateViewId();
        int taskButton1Id = generateViewId();
        int taskButton2Id = generateViewId();

        // <CONTAINER_VIEW>
        final RelativeLayout containerView = new RelativeLayout(context);
        containerView.setId(containerViewId);
        containerView.setBackgroundColor(Color.parseColor(Parameters.ActionContainerView.CONTAINER_BACKGROUND_COLOR));

        LinearLayout.LayoutParams containerLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        containerLayoutParams.setMargins(
                Parameters.ActionContainerView.CONTAINER_LEFT_MARGIN,
                Parameters.ActionContainerView.CONTAINER_TOP_MARGIN,
                Parameters.ActionContainerView.CONTAINER_RIGHT_MARGIN,
                Parameters.ActionContainerView.CONTAINER_BOTTOM_MARGIN
        );

        containerView.setLayoutParams(containerLayoutParams);
        // <CONTAINER_VIEW>

        // Button: "Import Data Source"
        Button labelButton = new Button(context);
        labelButton.setId(labelButtonId);

        labelButton.setText(action.getTitle() + "\n (" + action.getScript().getCode().length() + " lines)"); // i.e., Formerly "Add Data Source". i.e., [Project][Internet2][Generator].
        labelButton.setTextColor(Color.parseColor(Parameters.ActionTitleButton.BUTTON_TEXT_COLOR));
        labelButton.setTextSize(Parameters.ActionTitleButton.BUTTON_TEXT_SIZE);
        labelButton.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        labelButton.setIncludeFontPadding(false);
        labelButton.setAllCaps(false);
        labelButton.setTypeface(null, Typeface.NORMAL);

        labelButton.setPadding(
                Parameters.ActionTitleButton.BUTTON_INNER_PADDING_LEFT,
                Parameters.ActionTitleButton.BUTTON_INNER_PADDING_TOP,
                Parameters.ActionTitleButton.BUTTON_INNER_PADDING_RIGHT,
                Parameters.ActionTitleButton.BUTTON_INNER_PADDING_BOTTOM
        );

        labelButton.setBackgroundColor(Color.parseColor(Parameters.ActionTitleButton.BUTTON_BACKGROUND_COLOR));
//        labelButton.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL); // Aligns button text to the left side of the button
        labelButton.setMinHeight(0);

        RelativeLayout.LayoutParams labelButtonParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT
        );

        labelButtonParams.setMargins(
                Parameters.ActionTitleButton.BUTTON_OUTER_PADDING_LEFT,
                Parameters.ActionTitleButton.BUTTON_OUTER_PADDING_TOP,
                Parameters.ActionTitleButton.BUTTON_OUTER_PADDING_RIGHT,
                Parameters.ActionTitleButton.BUTTON_OUTER_PADDING_BOTTOM
        );

        labelButtonParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        labelButtonParams.addRule(RelativeLayout.LEFT_OF, taskButton1Id); // Set to left of left-most "task button view"
        labelButton.setLayoutParams(labelButtonParams);

        // <REFACTOR>
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
        // </REFACTOR>

        containerView.addView(labelButton);

        // <TASK_BUTTON>
        Button taskButton1 = new Button(context);
        taskButton1.setId(taskButton1Id);

        taskButton1.setText("\u2716"); // i.e., [Project][Internet2][Generator]
        taskButton1.setTextColor(Color.parseColor(Parameters.DefaultTaskButton.BUTTON_TEXT_COLOR));
        taskButton1.setTextSize(Parameters.DefaultTaskButton.BUTTON_TEXT_SIZE);
        taskButton1.setIncludeFontPadding(false);
        taskButton1.setAllCaps(false);
        taskButton1.setTypeface(null, Typeface.NORMAL);

        taskButton1.setBackgroundColor(Color.parseColor(Parameters.DefaultTaskButton.BUTTON_BACKGROUND_COLOR));

        taskButton1.setPadding(
                Parameters.DefaultTaskButton.BUTTON_INNER_PADDING_LEFT,
                Parameters.DefaultTaskButton.BUTTON_INNER_PADDING_TOP,
                Parameters.DefaultTaskButton.BUTTON_INNER_PADDING_RIGHT,
                Parameters.DefaultTaskButton.BUTTON_INNER_PADDING_BOTTOM
        );

        taskButton1.setMinWidth(0);
        taskButton1.setMinHeight(0);

        RelativeLayout.LayoutParams taskButton1Params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        taskButton1Params.setMargins(
                Parameters.DefaultTaskButton.BUTTON_OUTER_PADDING_LEFT,
                Parameters.DefaultTaskButton.BUTTON_OUTER_PADDING_TOP,
                Parameters.DefaultTaskButton.BUTTON_OUTER_PADDING_RIGHT,
                Parameters.DefaultTaskButton.BUTTON_OUTER_PADDING_BOTTOM
        );

        taskButton1Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        taskButton1Params.addRule(RelativeLayout.RIGHT_OF, labelButtonId);
        taskButton1Params.width = Parameters.DefaultTaskButton.TASK_BUTTON_WIDTH;
        taskButton1.setLayoutParams(taskButton1Params);

        // <REFACTOR>
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
        // </REFACTOR>

        containerView.addView(taskButton1);
        // </TASK_BUTTON>


        // <TASK_BUTTON>
        final Button taskButton2 = new Button(context);
        taskButton2.setId(taskButton2Id);

        taskButton2.setText("\uD83D\uDD0D"); // i.e., [Project][Internet2][Generator]
        taskButton2.setTextColor(Color.parseColor(Parameters.DefaultTaskButton.BUTTON_TEXT_COLOR));
        taskButton2.setTextSize(Parameters.DefaultTaskButton.BUTTON_TEXT_SIZE);
        taskButton2.setIncludeFontPadding(false);
        taskButton2.setAllCaps(false);
        taskButton2.setTypeface(null, Typeface.NORMAL);

        taskButton2.setPadding(
                Parameters.DefaultTaskButton.BUTTON_INNER_PADDING_LEFT,
                Parameters.DefaultTaskButton.BUTTON_INNER_PADDING_TOP,
                Parameters.DefaultTaskButton.BUTTON_INNER_PADDING_RIGHT,
                Parameters.DefaultTaskButton.BUTTON_INNER_PADDING_BOTTOM
        );

        taskButton2.setMinWidth(0);
        taskButton2.setMinHeight(0);

        taskButton2.setBackgroundColor(Color.parseColor("#44330000"));
        taskButton2.setMinHeight(0);

        RelativeLayout.LayoutParams taskButton2Params = new RelativeLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        taskButton2Params.setMargins(
                Parameters.DefaultTaskButton.BUTTON_OUTER_PADDING_LEFT,
                Parameters.DefaultTaskButton.BUTTON_OUTER_PADDING_TOP,
                Parameters.DefaultTaskButton.BUTTON_OUTER_PADDING_RIGHT,
                Parameters.DefaultTaskButton.BUTTON_OUTER_PADDING_BOTTOM
        );

//        taskButton1Params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        taskButton2Params.width = Parameters.DefaultTaskButton.TASK_BUTTON_WIDTH;
        taskButton2Params.addRule(RelativeLayout.LEFT_OF, taskButton1Id);
        taskButton2.setLayoutParams(taskButton2Params);

        // <REFACTOR>
        taskButton2.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                int touchActionType = (motionEvent.getAction() & MotionEvent.ACTION_MASK);

                if (touchActionType == MotionEvent.ACTION_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_POINTER_DOWN) {
                } else if (touchActionType == MotionEvent.ACTION_MOVE) {
                } else if (touchActionType == MotionEvent.ACTION_UP) {

                    final List<Action> actions = (List<Action>) World.getInstance().cache.getObjects(Action.class);

                    // TODO: Color.parseColor(camp.computer.clay.util.Color.getColor(Port.getType(ports.get(i))))

                    taskButton2.setBackgroundColor(
                            Color.argb(
                                    255,
                                    Random.generateRandomInteger(0, 255),
                                    Random.generateRandomInteger(0, 255),
                                    Random.generateRandomInteger(0, 255)
                            )
                    );

                } else if (touchActionType == MotionEvent.ACTION_POINTER_UP) {
                } else if (touchActionType == MotionEvent.ACTION_CANCEL) {
                } else {
                }
                return true;
            }
        });

        containerView.addView(taskButton2);
        // </TASK_BUTTON>

        return containerView;
    }

    //----------------------------------------------------------
    //----------------------------------------------------------
    //----------------------------------------------------------
    //----------------------------------------------------------
    //----------------------------------------------------------
    //----------------------------------------------------------
    //----------------------------------------------------------
    //----------------------------------------------------------
    //----------------------------------------------------------


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

        String CONTAINER_BACKGROUND_COLOR = camp.computer.clay.util.Color.getColor(Signal.Type.SWITCH);

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
        labelButton.setText(labelButtonText); // i.e., Formerly "Add Data Source". i.e., [Project][Internet2][Generator].
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
        taskButton1.setText("On"); // i.e., [Project][Internet2][Generator]
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
        taskButton2.setText("Off"); // i.e., [Project][Internet2][Generator]
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
        taskButton3.setText("In"); // i.e., [Project][Internet2][Generator]
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

        String CONTAINER_BACKGROUND_COLOR = camp.computer.clay.util.Color.getColor(Signal.Type.PULSE);

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
        labelButton.setText(labelButtonText); // i.e., Formerly "Add Data Source". i.e., [Project][Internet2][Generator].
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
        taskButton1.setHint("On Time (DS)".toUpperCase()); // i.e., [Project][Internet2][Generator]
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
        taskButton2.setHint("Interval (Period)".toUpperCase()); // i.e., [Project][Internet2][Generator]
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
        taskButton3.setText("In"); // i.e., [Project][Internet2][Generator]
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

        String CONTAINER_BACKGROUND_COLOR = camp.computer.clay.util.Color.getColor(Signal.Type.WAVE);

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
        labelButton.setText(labelButtonText); // i.e., Formerly "Add Data Source". i.e., [Project][Internet2][Generator].
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
        taskButton1.setHint("Amplitude (Voltage/ADC)".toUpperCase()); // i.e., [Project][Internet2][Generator]
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
        taskButton2.setHint("Interval (Period)"); // i.e., [Project][Internet2][Generator]
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
        taskButton3.setText("In"); // i.e., [Project][Internet2][Generator]
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
                taskButton1.setText("\u2716"); // i.e., [Project][Internet2][Generator]
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
                Typeface typeface = Typeface.createFromAsset(Application.getInstance().getAssets(), World.NOTIFICATION_FONT);
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

                linearLayout.addView(createButton("browse projects", null));
                linearLayout.addView(createButton("start from extensions", null));
                linearLayout.addView(createButton("free build", null));
                linearLayout.addView(createButton("challenge mode", null));

                // Title: "Player"
                TextView debugSubtitle = new TextView(context);
                debugSubtitle.setText("Player");
                debugSubtitle.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20));
                debugSubtitle.setTextSize(15);
                debugSubtitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                debugSubtitle.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(debugSubtitle);

                linearLayout.addView(createButton("Projects", null));
                linearLayout.addView(createButton("Inventory", null));
                linearLayout.addView(createButton("Ideas", null));
                linearLayout.addView(createButton("Friends", null));
                linearLayout.addView(createButton("Achievements", null));

                // Title: "Store"
                TextView storeSubtitle = new TextView(context);
                storeSubtitle.setText("Store");
                storeSubtitle.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20));
                storeSubtitle.setTextSize(15);
                storeSubtitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                storeSubtitle.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(storeSubtitle);

                linearLayout.addView(createButton("Clay", null));
                linearLayout.addView(createButton("Kits", null));
                linearLayout.addView(createButton("Components", null));
                linearLayout.addView(createButton("Accessories", null));

                // Add to main Application View
                FrameLayout frameLayout = (FrameLayout) Application.getInstance().findViewById(Application.applicationViewId);
                frameLayout.addView(relativeLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
        });
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

                linearLayout.addView(createButton("portable separation distance", null));
                linearLayout.addView(createButton("extension separation MIN", null));
                linearLayout.addView(createButton("extension separation MAX", null));

                linearLayout.addView(createButton("add/remove Host", null));
                linearLayout.addView(createButton("enable/disable notifications", null));
                linearLayout.addView(createButton("enable/disable vibration", null));
                linearLayout.addView(createButton("enable/disable network", null));

                // Title: "Settings"
                TextView debugSubtitle = new TextView(context);
                debugSubtitle.setText("Debug Section Subtitle!");
                debugSubtitle.setPadding(ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20), ViewGroupHelper.dpToPx(20));
                debugSubtitle.setTextSize(12);
                debugSubtitle.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                debugSubtitle.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(debugSubtitle);

                linearLayout.addView(createButton("debug: show monitor", null));
                linearLayout.addView(createButton("debug: show boundaries", null));
                linearLayout.addView(createButton("debug: target fps", null));
                linearLayout.addView(createButton("debug: sleep time", null));

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
                relativeLayout.setBackgroundColor(Color.parseColor("#00000000"));
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
        imageView.setBackgroundColor(Color.parseColor("#00ffffff"));

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
                canvas.drawColor(Color.parseColor("#bbffffff")); // Color.WHITE

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

                            // Add point to ModelBuilder
                            imagePoints.add(new Transform(motionEvent.getX(), motionEvent.getY()));

                            // Set background color
                            canvas.drawColor(Color.WHITE);

                            // Draw vertex Points in Shape
                            paint.setColor(Color.BLUE);
                            paint.setStyle(Paint.Style.STROKE);
                            paint.setStrokeWidth(3.0f);
                            if (imagePoints.size() > 0) {
                                android.graphics.Path path = new android.graphics.Path();
                                path.setFillType(android.graphics.Path.FillType.EVEN_ODD);
                                path.moveTo((float) imagePoints.get(0).x, (float) imagePoints.get(0).y);
                                for (int i = 1; i < imagePoints.size(); i++) {
                                    path.lineTo((float) imagePoints.get(i).x, (float) imagePoints.get(i).y);
                                }
                                // path.lineTo((float) boundary.get(0).x, (float) boundary.get(0).y);
                                path.close();
                                canvas.drawPath(path, paint);
                            }

                            for (int i = 0; i < imagePoints.size(); i++) {
                                Transform transform = imagePoints.get(i);

                                // Center
                                paint.setColor(Color.BLACK);
                                paint.setStyle(Paint.Style.FILL);
                                canvas.drawCircle((float) transform.x, (float) transform.y, 8, paint);

                                // Outline
                                paint.setColor(Color.LTGRAY);
                                paint.setStyle(Paint.Style.STROKE);
                                paint.setStrokeWidth(1.0f);
                                canvas.drawCircle((float) transform.x, (float) transform.y, 100, paint);

                                // Coordinate
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
    // </NATIVE_UI_UTILS>

    // <BASIC_UI_CONTROLS>
    public View createEditText(String text, String hintText) {

        // <PARAMETERS>
        int ELEMENT_INNER_PADDING_LEFT = ViewGroupHelper.dpToPx(20);
        int ELEMENT_INNER_PADDING_TOP = ViewGroupHelper.dpToPx(12);
        int ELEMENT_INNER_PADDING_RIGHT = ViewGroupHelper.dpToPx(20);
        int ELEMENT_INNER_PADDING_BOTTOM = ViewGroupHelper.dpToPx(12);

        int ELEMENT_OUTER_PADDING_LEFT = ViewGroupHelper.dpToPx(0);
        int ELEMENT_OUTER_PADDING_TOP = ViewGroupHelper.dpToPx(5);
        int ELEMENT_OUTER_PADDING_RIGHT = ViewGroupHelper.dpToPx(0);
        int ELEMENT_OUTER_PADDING_BOTTOM = ViewGroupHelper.dpToPx(0);
        // </PARAMETERS>

        // <EDIT_TEXT>
        EditText editText = new EditText(context);

        editText.setHint(hintText);

        if (text != null && text.length() > 0) {
            editText.setText(text);
        }

        editText.setPadding(ELEMENT_INNER_PADDING_LEFT, ELEMENT_INNER_PADDING_TOP, ELEMENT_INNER_PADDING_RIGHT, ELEMENT_INNER_PADDING_BOTTOM);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(ELEMENT_OUTER_PADDING_LEFT, ELEMENT_OUTER_PADDING_TOP, ELEMENT_OUTER_PADDING_RIGHT, ELEMENT_OUTER_PADDING_BOTTOM);
        editText.setLayoutParams(layoutParams);
        // </EDIT_TEXT>

        return editText;
    }

    // TODO: Create content, style, layout parameters so they can be passed to a general function.
    private View createButton(final String text, final SelectEventHandler selectEventHandler) {

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

                    Log.v("createButton", "Pressed Button");
                    if (selectEventHandler != null) {
                        Log.v("createButton", "invoking callback");
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
    // </BASIC_UI_CONTROLS>
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
//                    if (v.getTagUid() == titleText.getTagUid()) {
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