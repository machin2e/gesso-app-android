package com.example.android.horizontalpaging;

/*
* Defines a simple object to be displayed in a list view.
*
* This serves as "placeholder" representing the data for the view corresponding to an object in the
* object model.
*/

import android.graphics.Color;

import java.util.ArrayList;

public class ListItem {

    public String title;

    // for Lights behavior
    public ArrayList<Boolean> lightStates;
    public ArrayList<Integer> lightColors;

    // for I/O behavior
    public ArrayList<Boolean> ioStates;

    // for Message
    public String message;

    // for Wait behavior
    public int time;

    // for Say
    public String phrase;

    public static int DEFAULT_TYPE = CustomAdapter.IO_CONTROL_LAYOUT;

    public int type; // Used by the custom BaseAdapter to select the layout for the list_item_type_light.

    public boolean selected = false;

    // default constructor
    public ListItem() {
        this("Title", "Subtitle", DEFAULT_TYPE);
    }

    // main constructor
    public ListItem(String title, String subTitle, int type) {
        super();

        // Set parameters
        this.title = title;
        this.message = subTitle;
        this.type = type;

        // Initialize
        this.selected = false;

        // Initialize type
        initializeType();
    }

    private void initializeType() {
        if (this.type == CustomAdapter.LIGHT_CONTROL_LAYOUT) {

            // Initialize light states to false (off)
            lightStates = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                lightStates.add(false);
            }

            // Initialize light color to blue
            lightColors = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                lightColors.add(Color.rgb(0, 0, 255));
            }

        } else if (this.type == CustomAdapter.IO_CONTROL_LAYOUT) {

            // Initialize I/O states to false (off)
            ioStates = new ArrayList<>();
            for (int i = 0; i < 12; i++) {
                ioStates.add(false);
            }

        } else if (this.type == CustomAdapter.MESSAGE_CONTROL_LAYOUT) {

            message = "hello";

        } else if (this.type == CustomAdapter.WAIT_CONTROL_LAYOUT) {

            this.time = 250;

        } else if (this.type == CustomAdapter.SAY_CONTROL_LAYOUT) {

            phrase = "oh, that's great";

        }
    }

    // String representation
    public String toString() {
        return this.title + " : " + this.message;
    }
}
