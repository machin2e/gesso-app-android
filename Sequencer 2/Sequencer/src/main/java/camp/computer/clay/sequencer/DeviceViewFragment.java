package camp.computer.clay.sequencer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobeta.android.sequencer.R;

import java.util.ArrayList;
import java.util.Arrays;

import camp.computer.clay.system.Unit;

public class DeviceViewFragment extends Fragment {

    // The Clay unit associated with this fragment.
    private Unit unit;

    TimelineListView listView;

    // Configure the interface settings
    boolean disableScrollbarFading = true;
    boolean disableScrollbars = true;
    boolean disableOverscrollEffect = true;

//    private EventHolderAdapter adapter;

//    private ArrayList<EventHolder> eventHolders;

    public static final String ARG_SECTION_NUMBER = "section_number";

    public DeviceViewFragment() {
//            eventHolders = new ArrayList<EventHolder>();
//            behaviorEvents.addUnit("hello a");
//            behaviorEvents.addUnit("hello b");
//            behaviorEvents.addUnit("hello c");
//            behaviorEvents.addUnit("hello d");
//            behaviorEvents.addUnit("hello e");
//            behaviorEvents.addUnit("hello f");
//            behaviorEvents.addUnit("hello g");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);

        // Define the view (get a reference to it and pass it an adapter)
        listView = (TimelineListView) rootView.findViewById(R.id.listview_timeline);
        listView.setTag(getArguments().getInt(ARG_SECTION_NUMBER));

//        listView.setEventHolders(this.unit.getTimeline());

        /*
        String[] array = getResources().getStringArray(R.array.countries);
        ArrayList<String> eventTitles = new ArrayList<String>(Arrays.asList(array));
        eventHolders = new ArrayList<EventHolder>();
        for (int i = 0; i < eventTitles.size(); i++) {
            String eventTitle = eventTitles.get (i);
            Event event = new Event(i, eventTitle);
            eventHolders.add(new EventHolder(event));
        }

        Log.v ("Adding", "eventHolders.size: " + eventHolders.size());
        listView.setEventHolders(eventHolders);
        */

//        adapter = new EventHolderAdapter(getContext(), R.layout.list_item_handle_right, eventHolders);
//        listView.setAdapter(adapter);

        // Create behavior profiles for the unit's behaviors and assign the data to the ListView
        Log.v("CM_Log", "onCreateView");
//        Log.v ("CM_Log", "\tunit: " + this.unit);
//        Log.v("CM_Log", "\tunit.getTimeline: " + this.unit.getTimeline());
        listView.setEventHolders(this.unit.getTimeline());

        // <HACK>
        listView.setUnit (unit);
        // </HACK>

        if (disableScrollbarFading) {
            listView.setScrollbarFadingEnabled(false);
        }

        // Disable the scrollbars.
        if (disableScrollbars) {
            listView.setVerticalScrollBarEnabled(false);
            listView.setHorizontalScrollBarEnabled(false);
        }

        // Disable overscroll effect.
        if (disableOverscrollEffect) {
            listView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

        return rootView;
    }

    public void setUnit (Unit unit) {
        this.unit = unit;

        // Create behavior profiles for the timeline
//            createTimelineEvents();
    }

    public Unit getUnit () {
        return this.unit;
    }

}
