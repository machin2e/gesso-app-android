package camp.computer.clay.sequencer;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import camp.computer.clay.system.Unit;

/**
 * A fragment representing a section of the app. Each fragment represents a single unit. It
 * shows the timeline of unit behavior and controls for changing the timeline.
 */
public class DeviceViewFragment extends Fragment {

    // The Clay unit associated with this fragment.
    private Unit unit;

//        private ArrayList<EventHolder> eventHolders;

    private TimelineListView listView;

    // Configure the interface settings
    boolean disableScrollbarFading = true;
    boolean disableScrollbars = true;
    boolean disableOverscrollEffect = true;

    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    public static final String ARG_SECTION_NUMBER = "section_number";

//        private ArrayList<String> behaviorEvents = new ArrayList<String>();
//        ArrayAdapter<String> listAdapter;

    // TODO: DeviceViewFragment(Unit unit)
    public DeviceViewFragment() {

//            eventHolders = new ArrayList<EventHolder>();

//            behaviorEvents.add("hello a");
//            behaviorEvents.add("hello b");
//            behaviorEvents.add("hello c");
//            behaviorEvents.add("hello d");
//            behaviorEvents.add("hello e");
//            behaviorEvents.add("hello f");
//            behaviorEvents.add("hello g");
    }

    public void setUnit (Unit unit) {
        this.unit = unit;

        // Create behavior profiles for the timeline
//            createTimelineEvents();
    }

    public Unit getUnit () {
        return this.unit;
    }

//        private void createTimelineEvents() {
//            eventHolders.clear();
//
//            // Create a behavior profile for each of the unit's behaviors
//            for (Behavior behavior : this.unit.getTimeline().restoreBehaviors()) {
//                EventHolder timelineEvent = new EventHolder(behavior);
//                eventHolders.add(timelineEvent);
//            }
//
//            Log.v ("Behavior_Count", "profile count: " + this.eventHolders.size());
//        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_listview, container, false);

        // Define the view (get a reference to it and pass it an adapter)
        listView = (TimelineListView) rootView.findViewById(R.id.listview_timeline);
        listView.setTag(getArguments().getInt(ARG_SECTION_NUMBER));
        // TODO: Create new TimelineDeviceAdapter with the data for this tab's unit! (or reuse and repopulate with new data)

        // Create behavior profiles for the unit's behaviors and assign the data to the ListView
        Log.v ("CM_Log", "onCreateView");
        Log.v ("CM_Log", "\tunit: " + this.unit);
        Log.v ("CM_Log", "\tunit.getTimeline: " + this.unit.getTimeline());
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

    public void refreshView () {
        Log.v("CM_Log", "refreshView");
//        Log.v("CM_Log", "\tlistView = " + this.listView);
//        listView.setEventHolders(this.unit.getTimeline());
//        if (this.listView != null) {
//            this.listView.refreshListViewFromData();
//        }
        this.getView().invalidate();
    }
}
