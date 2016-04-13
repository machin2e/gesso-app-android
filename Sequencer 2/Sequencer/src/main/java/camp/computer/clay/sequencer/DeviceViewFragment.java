package camp.computer.clay.sequencer;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.github.clans.fab.FloatingActionButton;
import com.mobeta.android.sequencer.R;

import camp.computer.clay.system.Action;
import camp.computer.clay.system.Clay;
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

        /*
        final FloatingActionButton[] fab2 = {null};
        */

        fab.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean returnVal = false;
                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    /*
                    // Remove generated action buttons
                    if (fab2[0] != null) {
                        if (!fab2[0].isHidden()) {
                            fab2[0].hide(true);
                        }

                        FrameLayout root = (FrameLayout) ApplicationView.getApplicationView().findViewById(R.id.application_view);
                        root.removeView(fab2[0]);
                        fab2[0] = null;
                    }
                    */

                    // Save first touch point
                    getUnit().getClay().fabDownPoint = new Point((int) event.getX(), (int) event.getY());
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    // If dragging, skip the click event following release
                    if (getUnit().getClay().fabStatus == Clay.FAB_START_DRAGGING) {
//                        getUnit().getClay().fabDisableClick = true;

                        /*
                        // <TEST>
                        // Move to original location

                        // Get screen width and height of the device
                        DisplayMetrics metrics;
                        int screenWidth = 0, screenHeight = 0;
                        metrics = new DisplayMetrics();
                        ApplicationView.getApplicationView().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                        screenHeight = metrics.heightPixels;
                        screenWidth = metrics.widthPixels;

                        int width = fab.getWidth();
                        int height = fab.getHeight();
                        Point dest = new Point((int) screenWidth - (int) (width * 1.1), (int) (screenHeight / 2.0) - (int) (height / 2.0));
                        ApplicationView.getApplicationView().moveViewToScreenCenter(fab, dest, 100);
                        // </TEST>
                        */

                        /*
                        // Generate action path selectors
                        if (fab2[0] == null) {
                            FloatingActionButton newFab = new FloatingActionButton(getContext());
                            fab2[0] = newFab;

                            newFab.setButtonSize(FloatingActionButton.SIZE_MINI);
                            newFab.setColorNormal(Color.parseColor("#1976D2"));
                            newFab.setX(fab.getX() - 100);
                            newFab.setY(fab.getY() - 200);

                            FrameLayout root = (FrameLayout) ApplicationView.getApplicationView().findViewById(R.id.application_view);
                            root.addView(newFab);
                        }
                        */

                        /*
                        // Remove generated action buttons
                        if (fab2[0] != null) {
                            FrameLayout root = (FrameLayout) ApplicationView.getApplicationView().findViewById(R.id.application_view);
                            root.removeView(fab2[0]);
                            fab2[0] = null;
                        }
                        */
                    }

                    // Reset action button's state
                    getUnit().getClay().fabStatus = Clay.FAB_STOP_DRAGGING;

                    // TODO: If no contextual cue is nearby, start animation to return home (warp? translate?).

                    fab.requestLayout();
                    fab.invalidate();

                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                    getUnit().getClay().fabCurrentPoint = new Point((int) event.getX(), (int) event.getY());

                    if (getUnit().getClay().fabStatus == Clay.FAB_STOP_DRAGGING) {

                        double distance = Math.sqrt(Math.pow((getUnit().getClay().fabCurrentPoint.x - getUnit().getClay().fabDownPoint.x), 2) + Math.pow((getUnit().getClay().fabCurrentPoint.y - getUnit().getClay().fabDownPoint.y), 2));

                        // Check if drag threshold is exceeded
                        if (distance > getUnit().getClay().fabStartDragThresholdDistance) {
                            getUnit().getClay().fabStatus = Clay.FAB_START_DRAGGING;
                        }
                    }

                    if (getUnit().getClay().fabStatus == Clay.FAB_START_DRAGGING) {

                        // Update placement of action button
                        int width = fab.getWidth();
                        int height = fab.getHeight();
                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
                        params.leftMargin = (int) event.getRawX() - (int) (width / 2.0);
                        params.topMargin = (int) event.getRawY() - (int) (height / 2.0);

                        fab.requestLayout();
                        fab.invalidate();

                        // Move Clay
//                        int width = fab.getWidth();
//                        int height = fab.getHeight();
//                        Point dest = new Point((int) event.getRawX() - (int) (width / 2.0), (int) event.getRawY() - (int) (height / 2.0));
//                        ApplicationView.getApplicationView().moveViewToScreenCenter(fab, dest, 100);

//                        if (getUnit().getClay().selectedEventHolder != null) {
//                            timelineView.removeEventHolder(getUnit().getClay().selectedEventHolder);
////                            timelineView.refreshListViewFromData();
//                            timelineView.refreshListViewFromData();
//                        }

                        // TODO: Search for nearest/nearby events, actions, states, etc. (i.e., discovery context)
                        TimelineListView.getTimelineListView().resetViewBackgrounds();
                        TimelineListView.getTimelineListView().findNearbyViews((int) event.getRawX(), (int) event.getRawY());
                        getUnit().getClay().selectedEventHolderIndex = TimelineListView.getTimelineListView().findNearestTimelineIndex((int) event.getRawX(), (int) event.getRawY());
                        // TODO: Highlight nearby elements
                        // TODO: Generate contextual action palette and show semi-translucent (until release, then show opaque)

                        // Highlight the position where Clay will add the event on the timeline
                        if (getUnit().getClay().selectedEventHolder == null) {
                            getUnit().getClay().selectedEventHolder = new EventHolder("highlight", "highlight");
                            timelineView.addEventHolder(getUnit().getClay().selectedEventHolderIndex, getUnit().getClay().selectedEventHolder);
//                            timelineView.refreshListViewFromData();
                            timelineView.refreshListViewFromData();
                        } else {
                            timelineView.removeEventHolder(getUnit().getClay().selectedEventHolder);
                            timelineView.addEventHolder(getUnit().getClay().selectedEventHolderIndex, getUnit().getClay().selectedEventHolder);
//                            timelineView.refreshListViewFromData();
                            timelineView.refreshListViewFromData();
                        }

//                        fab.requestLayout();
//                        fab.invalidate();

                        // Set return value (halts event propagation if true)
                        returnVal = true;
                    }
                }

                return returnVal;
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                if (getUnit().getClay().fabDisableClick == false) {
                if (getUnit().getClay().selectedEventHolderIndex != -1) {
                    timelineView.displayActionBrowser(new TimelineListView.ActionSelectionListener() {
                        @Override
                        public void onSelect(Action action) {
//                            EventHolder eventHolder = new EventHolder("choose", "choose");
                            //timelineView.addEventHolder(eventHolder);
//                            timelineView.addEventHolder(getUnit().getClay().selectedEventHolderIndex, getUnit().getClay().selectedEventHolder);
                            timelineView.replaceEventHolder(getUnit().getClay().selectedEventHolder, action);
                            timelineView.refreshListViewFromData();
//                            timelineView.refreshListViewFromData(); // <HACK />


                            // <TEST>
                            // Move to original location

                            // Get screen width and height of the device
                            DisplayMetrics metrics;
                            int screenWidth = 0, screenHeight = 0;
                            metrics = new DisplayMetrics();
                            ApplicationView.getApplicationView().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                            screenHeight = metrics.heightPixels;
                            screenWidth = metrics.widthPixels;

                            int width = fab.getWidth();
                            int height = fab.getHeight();
                            Point dest = new Point((int) screenWidth - (int) (width * 1.1), (int) (screenHeight / 2.0) - (int) (height / 2.0));
                            ApplicationView.getApplicationView().moveViewToScreenCenter(fab, dest, 400);
                            // </TEST>
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
