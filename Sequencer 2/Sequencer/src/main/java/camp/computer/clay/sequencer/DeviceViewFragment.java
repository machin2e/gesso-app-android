package camp.computer.clay.sequencer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobeta.android.sequencer.R;

import camp.computer.clay.system.Unit;

public class DeviceViewFragment extends Fragment {

    // The Clay unit associated with this fragment.
    private Unit unit;

    // The timeline view used to draw the timeline.
    TimelineListView timelineView;

    // Configure the interface settings
    private boolean DISABLE_SCROLLBAR_FADING = true;
    private boolean DISABLE_SCROLLBARS = true;
    private boolean DISABLE_OVERSCROLL_EFFECT = true;

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
        timelineView = (TimelineListView) rootView.findViewById(R.id.listview_timeline);
        timelineView.setTag(getArguments().getInt(ARG_SECTION_NUMBER));

//        timelineView.setTimeline(this.unit.getTimeline());

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
        timelineView.setTimeline(eventHolders);
        */

//        adapter = new EventHolderAdapter(getContext(), R.layout.list_item_handle_right, eventHolders);
//        timelineView.setAdapter(adapter);

        // Create behavior profiles for the unit's behaviors and assign the data to the ListView
        Log.v("CM_Log", "onCreateView");
//        Log.v ("CM_Log", "\tunit: " + this.unit);
//        Log.v("CM_Log", "\tunit.getTimeline: " + this.unit.getTimeline());
        timelineView.setTimeline(this.unit.getTimeline());

        // <HACK>
        timelineView.setUnit(unit);
        // </HACK>

        if (DISABLE_SCROLLBAR_FADING) {
            timelineView.setScrollbarFadingEnabled(false);
        }

        // Disable the scrollbars.
        if (DISABLE_SCROLLBARS) {
            timelineView.setVerticalScrollBarEnabled(false);
            timelineView.setHorizontalScrollBarEnabled(false);
        }

        // Disable overscroll effect.
        if (DISABLE_OVERSCROLL_EFFECT) {
            timelineView.setOverScrollMode(View.OVER_SCROLL_NEVER);
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
