package com.example.android.horizontalpaging;

/*
* Defines a simple object to be displayed in a list view.
*/

public class ListItem {
    public String title;
    public String subTitle;

    public int type; // Used by the custom BaseAdapter to select the layout for the list_item_type_001.
    public static int DEFAULT_TYPE = 0;

    // default constructor
    public ListItem() {
        this("Title", "Subtitle", DEFAULT_TYPE);
    }

    // main constructor
    public ListItem(String title, String subTitle, int type) {
        super();
        this.title = title;
        this.subTitle = subTitle;
        this.type = type;
    }

    // String representation
    public String toString() {
        return this.title + " : " + this.subTitle;
    }
}
