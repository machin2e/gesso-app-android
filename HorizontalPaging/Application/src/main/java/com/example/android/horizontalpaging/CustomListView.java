package com.example.android.horizontalpaging;

import android.app.AlertDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class CustomListView extends ListView {

    //private ArrayAdapter<ListItem> adapter;
    private CustomAdapter adapter;
    private ArrayList data;

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
        // setup the data source
        this.data = new ArrayList<ListItem>();

        // create some objects... and add them into the array list
        this.data.add(new ListItem("behavior 1", "Subtitle", 0));
        this.data.add(new ListItem("behavior 2", "Subtitle", 1));
        this.data.add(new ListItem("behavior 3", "Subtitle", 1));
        this.data.add(new ListItem("behavior 4", "Subtitle", 1));
        this.data.add(new ListItem("behavior 5", "Subtitle", 0));

//        adapter = new ArrayAdapter<String>(getContext(),R.layout.row, R.id.label, data);
        // setup the data adaptor
        CustomAdapter adapter = new CustomAdapter(getContext(), R.layout.row, this.data);
        setAdapter (adapter);
        setOnItemClickListener (new ListSelection());
    }

    private class ListSelection implements OnItemClickListener
    {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setMessage("You pressed item #" + (position+1));
            builder.setPositiveButton("OK", null);
            builder.show();
        }

    }
}
