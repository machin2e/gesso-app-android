package camp.computer.clay.platform.graphics.controls;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.platform.Application;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.platform.R;

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
    public <T> void promptSelection(final List<Profile> options, final OnActionListener onActionListener) {

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
//            Profile extensionProfile = getClay().getProfiles().get(i);
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
                        Profile selection = options.get(position);

                        // Configure based on Profile
                        // Add Ports based on Profile
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

    public void OLD_openActionEditor(Entity extension) {
        Application.getView().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                final RelativeLayout pathEditor = (RelativeLayout) Application.getView().findViewById(R.id.action_editor_view);
                pathEditor.setVisibility(View.VISIBLE);
            }
        });
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
                textView.setText("Actions");
                textView.setPadding(dpToPx(20), dpToPx(20), dpToPx(20), dpToPx(20));
                textView.setTextSize(20);
                textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                linearLayout.addView(textView);

                // Text: "Data Sources (Imports)"
                TextView textView2 = new TextView(context);
                textView2.setText("data sources (imports)");
                textView2.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
                textView2.setBackgroundColor(Color.parseColor("#44000000"));
                linearLayout.addView(textView2);

                // Button: "Add Source"
                Button button1 = new Button(context);
                button1.setText("Add Source (from Extension/Port)");
                button1.setPadding(dpToPx(20), dpToPx(12), dpToPx(20), dpToPx(12));
                button1.setBackgroundColor(Color.parseColor("#44000000"));

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );
                params.setMargins(0, dpToPx(5), 0, 0);
                button1.setLayoutParams(params);

                linearLayout.addView(button1);

                // Layout (Linear Vertical): Actions
                final LinearLayout linearLayout2 = new LinearLayout(context);
                linearLayout2.setOrientation(LinearLayout.VERTICAL);

                TextView textView3 = new TextView(context);
                textView3.setText("target set period (source:wave)");
                linearLayout2.addView(textView3);

                TextView textView4 = new TextView(context);
                textView4.setText("target set duty cycle (number:500)");
                linearLayout2.addView(textView4);

                linearLayout.addView(linearLayout2, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                // Button: "Add Action"
                Button button2 = new Button(context);
                button2.setText("Add Action");
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

                            addExtensionAction(linearLayout2);

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
                linearLayout.addView(button2);

                // Button: "Browse Actions"
                Button button3 = new Button(context);
                button3.setText("Browse Actions for Ext/Port Config");
                linearLayout.addView(button3);

                // Text: "Main Extension Controller Action/Widgets"
                TextView textView5 = new TextView(context);
                textView5.setText("Main Extension Controller(s) Actions/Widgets");
                linearLayout.addView(textView5);

                Button button4 = new Button(context);
                button4.setText("Add TODO");
                linearLayout.addView(button4);

                Button button5 = new Button(context);
                button5.setText("Add Note");
                linearLayout.addView(button5);

                // Add to main Application View
                FrameLayout frameLayout = (FrameLayout) Application.getView().findViewById(R.id.application_view);
                frameLayout.addView(relativeLayout, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
            }
        });
    }

    private void addExtensionAction(View view) {

        final TextView actionView = new TextView(context);
        actionView.setText("Event (<PortEntity> <PortEntity> ... <PortEntity>)\nExpose: <PortEntity> <PortEntity> ... <PortEntity>");
        int horizontalPadding = (int) Application.getView().convertDipToPx(20);
        int verticalPadding = (int) Application.getView().convertDipToPx(10);
        actionView.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
        actionView.setBackgroundColor(Color.parseColor("#44000000"));

        final LinearLayout actionList = (LinearLayout) view;

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

        actionList.addView(actionView);
    }
}
