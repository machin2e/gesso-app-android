package camp.computer.clay.sequencer;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.github.clans.fab.FloatingActionButton;
import com.mobeta.android.sequencer.R;

import camp.computer.clay.system.Action;
import camp.computer.clay.system.Clay;
import camp.computer.clay.system.Event;
import camp.computer.clay.system.Timeline;
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

    public DeviceViewFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View rootView = inflater.inflate(R.layout.fragment_listview, container, false);

        // Define the view (get a reference to it and pass it an adapter)
        timelineView = (TimelineListView) rootView.findViewById(R.id.listview_timeline);
        timelineView.setTag(getArguments().getInt(ARG_SECTION_NUMBER));

        // Create behavior profiles for the unit's behaviors and assign the data to the ListView
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

        // <HACK>
        // Set up FAB
        final FloatingActionButton fab = (FloatingActionButton) ApplicationView.getApplicationView().findViewById(R.id.fab_create);

        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean returnVal = false;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    getUnit().getClay().downPoint = new Point((int) event.getX(), (int) event.getY());
//                    getUnit().getClay().fabStatus = Clay.FAB_START_DRAGGING;
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    if (getUnit().getClay().fabStatus == Clay.FAB_START_DRAGGING) {
                        getUnit().getClay().fabDisableClick = true; // Skip the click event following release
                    }

                    getUnit().getClay().fabStatus = Clay.FAB_STOP_DRAGGING;
                    Log.i("Drag", "Stopped Dragging");

                    fab.requestLayout();

                    fab.invalidate();

                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                    if (getUnit().getClay().fabStatus == Clay.FAB_STOP_DRAGGING) {
                        getUnit().getClay().currentPoint = new Point((int) event.getX(), (int) event.getY());

                        double distance = Math.sqrt(Math.pow((getUnit().getClay().currentPoint.x - getUnit().getClay().downPoint.x), 2) + Math.pow((getUnit().getClay().currentPoint.y - getUnit().getClay().downPoint.y), 2));

                        if (distance > 50) {
                            getUnit().getClay().fabStatus = Clay.FAB_START_DRAGGING;
                        }
                    }

                    if (getUnit().getClay().fabStatus == Clay.FAB_START_DRAGGING) {
//                        Log.i("Drag", "Dragging");

//                        Log.i("Drag", "x: " + event.getRawX() + ", y: " + event.getRawY());

                        int width = fab.getWidth();
                        int height = fab.getHeight();

                        Log.i("Drag", "downtime: " + event.getDownTime());
                        Log.i("Drag", "eventtime: " + event.getEventTime());
                        long diff = event.getEventTime() - event.getDownTime();
                        Log.i("Drag", "diff: " + diff);

//                        ViewGroup.LayoutParams layoutParams = fab.getLayoutParams();
//                        layoutParams.
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
                        params.leftMargin = (int) event.getRawX() - (int) (width / 2.0);
                        params.topMargin = (int) event.getRawY() - (int) (height / 2.0);

                        fab.requestLayout();

                        fab.invalidate();

                        returnVal = true;

//                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
//                                50, 50);
//                        layoutParams.setMargins((int) me.getRawX() - 25,
//                                (int) me.getRawY() - 50, 0, 0);
//                        layout.removeView(btn);
//                        layout.addView(btn, layoutParams);
                    }
                }
                return returnVal;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getUnit().getClay().fabDisableClick == false) {
                    timelineView.displayActionBrowser(new TimelineListView.ActionSelectionListener() {
                        @Override
                        public void onSelect(Action action) {
                            EventHolder eventHolder = new EventHolder("choose", "choose");
                            timelineView.addEventHolder(eventHolder);
                            timelineView.replaceEventHolder(eventHolder, action);
                            timelineView.refreshListViewFromData(); // <HACK />
                        }
                    });
                }

                getUnit().getClay().fabDisableClick = false;

//                EventHolder eventHolder = new EventHolder(event);
//                eventHolders.add(eventHolder);
//
//                EventHolder eventHolder = new EventHolder("choose", "choose");
//                timelineView.addEventHolder(eventHolder);
//                timelineView.displayActionBrowser(eventHolder);
//
//
//
//                Timeline timeline = this.getUnit().getTimeline();
//                event = new Event(timeline, action);
//                EventHolder replacementEventHolder = new EventHolder(event);
            }
        });
        // </HACK>

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
