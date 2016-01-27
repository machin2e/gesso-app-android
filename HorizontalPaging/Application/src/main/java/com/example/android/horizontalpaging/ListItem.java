package com.example.android.horizontalpaging;

/*
* Defines a simple object to be displayed in a list view.
*/

public class ListItem {
    public String title;
    public String subTitle;

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
        this.subTitle = subTitle;
        this.type = type;

        // Initialize
        this.selected = false;
    }

    // String representation
    public String toString() {
        return this.title + " : " + this.subTitle;
    }
}
