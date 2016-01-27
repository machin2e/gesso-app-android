package com.example.android.horizontalpaging;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

public class CustomListView extends ListView {

    //private ArrayAdapter<ListItem> adapter;
    private CustomAdapter adapter;
    private ArrayList<ListItem> data; // The data to display in _this_ ListView.

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

    public void init()
    {
        initData ();

//        adapter = new ArrayAdapter<String>(getContext(),R.layout.list_item_type_001, R.id.label, data);
        // setup the data adaptor
        CustomAdapter adapter = new CustomAdapter(getContext(), R.layout.list_item_type_001, this.data);
        setAdapter(adapter);
        setOnItemClickListener(new ListSelection());
    }

    /**
     * Set up the data source and populate the list of data to show in this ListView.
     */
    public void initData () {
        // setup the data source
        this.data = new ArrayList<ListItem>();

        // create some objects... and add them into the array list
        this.data.add(new ListItem("abstract", "Subtitle", 0));

        this.data.add(new ListItem("behavior 1", "Subtitle", 1));
        this.data.add(new ListItem("behavior 2", "Subtitle", 1));
        this.data.add(new ListItem("behavior 3", "Subtitle", 1));

        this.data.add(new ListItem("create", "Subtitle", 0));
    }

    private class ListSelection implements OnItemClickListener
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id)
        {

            ListItem item = (ListItem) data.get (position);

            // Update layout
            // HACK: Separate the state and view!
            if (item.selected == false) {
                // Update state of data
                item.selected = true;
                // Update state of view
                view.setPadding(view.getPaddingLeft() + 100, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            } else {
                // Update state of data
                item.selected = false;
                // Update state of view
                view.setPadding(20, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            }

            // Update image
            ImageView icon = (ImageView) view.findViewById(R.id.icon);

            //int w = WIDTH_PX, h = HEIGHT_PX;
            int w = icon.getWidth(), h = icon.getHeight();

            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
            Canvas canvas = new Canvas(bmp);

            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.rgb(61, 61, 61));
            canvas.drawRect(0, 0, w, h, paint);

            icon.setImageBitmap(bmp);

            // Show options
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("You pressed item #" + (position+1));
            builder.setPositiveButton("OK", null);
            builder.show();
        }

    }
}
