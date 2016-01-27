package com.example.android.horizontalpaging;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class CustomListView extends ListView {

    private CustomAdapter adapter;
    private ArrayList<ListItem> data; // The data to display in _this_ ListView. This has to be repopulated on initialization.

    public CustomListView(Context context) {
        super(context);
        init ();
    }

    public CustomListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * Initialize the ListView.
     */
    public void init()
    {
        initData();

//        adapter = new ArrayAdapter<String>(getContext(),R.layout.list_item_type_light, R.id.label, data);
        // setup the data adaptor
        this.adapter = new CustomAdapter(getContext(), R.layout.list_item_type_light, this.data);
        setAdapter(adapter);
        setOnItemClickListener(new ListSelection());
    }

    /**
     * Set up the data source and populate the list of data to show in this ListView.
     */
    public void initData () {
        // TODO: Initialize data from cache or from remote source in this function. Do this because the ViewPager will destroy this object when moving between pages.

        // setup the data source
        this.data = new ArrayList<ListItem>();

        // create some objects... and add them into the array list
        this.data.add(new ListItem("abstract", "Subtitle", CustomAdapter.SYSTEM_CONTROL_LAYOUT));

        // Basic behaviors
        this.data.add(new ListItem("lights", "Subtitle", CustomAdapter.LIGHT_CONTROL_LAYOUT));
        this.data.add(new ListItem("io", "Subtitle", CustomAdapter.IO_CONTROL_LAYOUT));
        this.data.add(new ListItem("message", "turn lights on", CustomAdapter.MESSAGE_CONTROL_LAYOUT));
        this.data.add(new ListItem("wait", "500 ms", CustomAdapter.WAIT_CONTROL_LAYOUT));
        this.data.add(new ListItem("say", "oh, that's great", CustomAdapter.SAY_CONTROL_LAYOUT));

        this.data.add(new ListItem("create", "Subtitle", CustomAdapter.SYSTEM_CONTROL_LAYOUT));
    }

    /**
     * Add data to the ListView.
     *
     * @param item
     */
    private void addData (ListItem item) {
        if (adapter != null) {
            data.add(data.size() - 1, item);
            updateViewFromData();
        }
    }

    private void updateViewFromData () {
        // TODO: Perform callbacks into data model to propagate changes based on view state and data item state.
        adapter.notifyDataSetChanged();
    }

    private class ListSelection implements OnItemClickListener
    {

        @Override
        public void onItemClick(AdapterView<?> parent, final View view, final int position, long id)
        {

            final ListItem item = (ListItem) data.get (position);

            // Check if the list item was a constructor
            if (item.type == CustomAdapter.SYSTEM_CONTROL_LAYOUT) {
                if (item.title == "create") {
                    String title = "";
                    String subtitle = "";
                    int type = CustomAdapter.CONTROL_PLACEHOLDER_LAYOUT;

                    addData (new ListItem (title, subtitle, type));
                }
                // TODO: (?)
            }

            if (item.type != CustomAdapter.SYSTEM_CONTROL_LAYOUT && item.type != CustomAdapter.CONTROL_PLACEHOLDER_LAYOUT) {

                // Update state of the object associated with the selected view.
                if (item.selected == false) {
                    // Toggle the item as selected
                    item.selected = true;
                } else {
                    // Toggle the item as not selected
                    item.selected = false;
                }

                /*
                // Show options
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage("You pressed item #" + (position + 1));
                builder.setPositiveButton("OK", null);
                builder.show();
                */

                // Request the ListView to be redrawn so the views in it will be displayed
                // according to their updated state information.
                updateViewFromData ();
            }

            if (item.type == CustomAdapter.CONTROL_PLACEHOLDER_LAYOUT) {

                // Show options
//                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
//                builder.setTitle("Select a behavior");
//                builder.setMessage("You pressed item #" + (position + 1));
//                builder.setPositiveButton("OK", null);
//                builder.show();

                int basicBehaviorCount = 5;
                final String[] basicBehaviors = new String[basicBehaviorCount];
                basicBehaviors[0] = "lights";
                basicBehaviors[1] = "io";
                basicBehaviors[2] = "message";
                basicBehaviors[3] = "wait";
                basicBehaviors[4] = "say";

                // Show the list of behaviors
                AlertDialog.Builder builder = new AlertDialog.Builder (getContext());
                builder.setTitle("Select a behavior");
                builder.setItems(basicBehaviors, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int itemIndex) {

                        if (basicBehaviors[itemIndex].toString().equals("lights")) {
//                            Hack_PromptForBehaviorTransform(perspective);

                            // <HACK>
                            // This removes the specified item from the list and replaces it with an item of a specific type.
                            // TODO: Replace view, not data! (i.e., item.type = CustomAdapter.LIGHT_CONTROL_LAYOUT;)
                            int index = data.indexOf(item);
                            data.remove(index);
                            updateViewFromData();
                            // Add the new item.
                            ListItem replacementItem = new ListItem ("lights", "", CustomAdapter.LIGHT_CONTROL_LAYOUT);
                            data.add(index, replacementItem);
                            // </HACK>

                        } else if (basicBehaviors[itemIndex].toString().equals("io")) {

//                            Hack_PromptForBehaviorTransform(perspective);

                            // <HACK>
                            // This removes the specified item from the list and replaces it with an item of a specific type.
                            // TODO: Replace view, not data! (i.e., item.type = CustomAdapter.CONTROL_PLACEHOLDER_LAYOUT;)
                            int index = data.indexOf(item);
                            data.remove(index);
                            updateViewFromData();
                            // Add the new item.
                            ListItem replacementItem = new ListItem ("io", "", CustomAdapter.IO_CONTROL_LAYOUT);
                            data.add(index, replacementItem);
                            // </HACK>

                        } else if (basicBehaviors[itemIndex].toString().equals("wait")) {

//                            Hack_PromptForTimeTransform(perspective);

                            // <HACK>
                            // This removes the specified item from the list and replaces it with an item of a specific type.
                            // TODO: Replace view, not data! (i.e., item.type = CustomAdapter.WAIT_CONTROL_LAYOUT;)
                            int index = data.indexOf(item);
                            data.remove(index);
                            updateViewFromData();
                            // Add the new item.
                            ListItem replacementItem = new ListItem ("wait", "500 ms", CustomAdapter.WAIT_CONTROL_LAYOUT);
                            data.add(index, replacementItem);
                            // </HACK>

                        } else if (basicBehaviors[itemIndex].toString().equals("message")) {

//                            Hack_PromptForMessage(perspective);

                            // <HACK>
                            // This removes the specified item from the list and replaces it with an item of a specific type.
                            // TODO: Replace view, not data! (i.e., item.type = CustomAdapter.MESSAGE_CONTROL_LAYOUT;)
                            int index = data.indexOf(item);
                            data.remove(index);
                            updateViewFromData();
                            // Add the new item.
                            ListItem replacementItem = new ListItem ("message", "turn lights off", CustomAdapter.MESSAGE_CONTROL_LAYOUT);
                            data.add(index, replacementItem);
                            // </HACK>

                        } else if (basicBehaviors[itemIndex].toString().equals("say")) {

//                            Hack_PromptForSpeech(perspective);

                            // <HACK>
                            // This removes the specified item from the list and replaces it with an item of a specific type.
                            // TODO: Replace view, not data! (i.e., item.type = CustomAdapter.MESSAGE_CONTROL_LAYOUT;)
                            int index = data.indexOf(item);
                            data.remove(index);
                            updateViewFromData();
                            // Add the new item.
                            ListItem replacementItem = new ListItem("say", "what do you think?", CustomAdapter.SAY_CONTROL_LAYOUT);
                            data.add(index, replacementItem);
                            // </HACK>

                        }

                        updateViewFromData();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();

            }
        }

    }
}
