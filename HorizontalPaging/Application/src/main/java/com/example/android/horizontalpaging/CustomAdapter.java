package com.example.android.horizontalpaging;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    // store the context (as an inflated layout)
    private LayoutInflater inflater;
    // store the resource (typically list_item.xml)
    private int resource;
    // store (a reference to) the data
    private ArrayList<ListItem> data;

    /**
     * Default constructor. Creates the new Adaptor object to
     * provide a ListView with data.
     * @param context
     * @param resource
     * @param data
     */
    public CustomAdapter(Context context, int resource, ArrayList<ListItem> data) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.data = data;
    }

    /**
     * Return the size of the data set.
     */
    public int getCount() {
        return this.data.size();
    }

    /**
     * Return an object in the data set.
     */
    public Object getItem(int position) {
        return this.data.get(position);
    }

    /**
     * Return the position provided.
     */
    public long getItemId(int position) {
        return position;
    }

    public int getItemType(int position){
        // Your if else code and return type ( TYPE_1 to TYPE_5 )
        ListItem listItem = (ListItem) getItem (position);
        return listItem.type;
    }

    /**
     * Return a generated view for a position.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // reuse a given view, or inflate a new one from the xml
        View view;

        // Select the layout for the view based on the type of object being displayed in the view
        int type = getItemType (position);
        int resourceForType; // Default resource
        if (type == 0) {
            resourceForType = R.layout.list_item_type_001; // Select the layout for the list_item_type_001 type
        } else if (type == 1) {
            resourceForType = R.layout.list_item_type_002;
        } else {
            resourceForType = R.layout.list_item_type_001;
        }

        if (convertView == null) {
            //view = this.inflater.inflate(resource, parent, false);
            view = this.inflater.inflate(resourceForType, parent, false);
        } else {
            view = convertView;
        }

        // bind the data to the view object
        return this.bindData(view, position);
    }

    /**
     * Bind the provided data to the view.
     * This is the only method not required by base adapter.
     */
    public View bindData(View view, int position) {
        // make sure it's worth drawing the view
        if (this.data.get(position) == null) {
            return view;
        }

        // pull out the object
        ListItem item = this.data.get(position);

        // extract the view object
        View viewElement = view.findViewById(R.id.label);
//        View viewElement = view.findViewById(R.id.title);
        // cast to the correct type
        TextView tv = (TextView)viewElement;
        // set the value
        tv.setText(item.title);

//        viewElement = view.findViewById(R.id.subTitle);
//        tv = (TextView)viewElement;
//        tv.setText(item.subTitle);

        // Update the icon in the item's layout
        if (item.type == 1) {
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setImageResource(R.drawable.tile);
        }

        // return the final view object
        return view;
    }
}