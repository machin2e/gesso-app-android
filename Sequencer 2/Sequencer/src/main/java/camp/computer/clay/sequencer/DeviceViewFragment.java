package camp.computer.clay.sequencer;

import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.github.clans.fab.FloatingActionButton;
import com.mobeta.android.sequencer.R;

import java.util.ArrayList;

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

    private FloatingActionButton generateFablet(int x, int y, boolean startHidden) {
        FloatingActionButton newFab = new FloatingActionButton(getContext());

        // Animations
        newFab.setShowAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fab_scale_up));
        newFab.setHideAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.fab_scale_down));

        // Style
        newFab.setButtonSize(FloatingActionButton.SIZE_MINI);
        newFab.setColorNormal(Color.parseColor("#1976D2"));

        // Hide
        if (startHidden) {
            newFab.hide(false);
        }

        FrameLayout root = (FrameLayout) ApplicationView.getApplicationView().findViewById(R.id.application_view);
        root.addView(newFab);

        // Position
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) newFab.getLayoutParams();
        params.leftMargin = x - (int) (newFab.getWidth() / 2.0);
        params.topMargin = y + (int) (newFab.getHeight() / 2.0);

        return newFab;
    }

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

//            timelineView.setFastScrollAlwaysVisible(false);
//            timelineView.
        }

        // Disable overscroll effect.
        if (DISABLE_OVERSCROLL_EFFECT) {
            timelineView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        }

//        // <HACK>
//        // Set up FAB
//        final FloatingActionButton fab = (FloatingActionButton) ApplicationView.getApplicationView().findViewById(R.id.fab_create);
//
//        final ArrayList<FloatingActionButton> fablets = getUnit().getClay().fablets;
//
//        fab.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                boolean returnVal = false;
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//
//                    // Reset selection
//                    getUnit().getClay().selectedEventHolderIndex = -1;
//                    getUnit().getClay().selectedEventHolder = null;
//
//                    /*
//                    // Remove generated action buttons
//                    if (fablets.size() > 0) {
//                        for (FloatingActionButton fablet : fablets) {
//                            if (!fablet.isHidden()) {
//                                fablet.hide(true);
//                            }
//
//                            FrameLayout root = (FrameLayout) ApplicationView.getApplicationView().findViewById(R.id.application_view);
//                            root.removeView(fablet);
//                        }
//                        fablets.clear();
//                    }
//                    */
//
//                    // Save first touch point
//                    getUnit().getClay().fabDownPoint = new Point((int) event.getX(), (int) event.getY());
//                }
//
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//
//                    // If dragging, skip the click event following release
//                    if (getUnit().getClay().fabStatus == Clay.FAB_START_DRAGGING) {
////                        getUnit().getClay().fabDisableClick = true;
//
//                        /*
//                        // <TEST>
//                        // Move to original location
//
//                        // Get screen width and height of the device
//                        DisplayMetrics metrics;
//                        int screenWidth = 0, screenHeight = 0;
//                        metrics = new DisplayMetrics();
//                        ApplicationView.getApplicationView().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                        screenHeight = metrics.heightPixels;
//                        screenWidth = metrics.widthPixels;
//
//                        int width = fab.getWidth();
//                        int height = fab.getHeight();
//                        Point dest = new Point((int) screenWidth - (int) (width * 1.1), (int) (screenHeight / 2.0) - (int) (height / 2.0));
//                        ApplicationView.getApplicationView().moveViewToScreenCenter(fab, dest, 100);
//                        // </TEST>
//                        */
//
//                        /*
//                        // Generate action path selectors
//                        if (fablets.isEmpty()) {
//
//                            FloatingActionButton newFab1 = generateFablet((int) fab.getX() - 100, (int) fab.getY() - 150, true);
//                            FloatingActionButton newFab2 = generateFablet((int) fab.getX() - 150, (int) fab.getY(), true);
//                            FloatingActionButton newFab3 = generateFablet((int) fab.getX() - 100, (int) fab.getY() + 150, true);
//
//                            fablets.add(newFab1);
//                            fablets.add(newFab2);
//                            fablets.add(newFab3);
//
//                            // Show fablet
//                            newFab1.show(true);
//                            newFab2.show(true);
//                            newFab3.show(true);
//                        }
//                        */
//                    }
////                    Log.v ("Timeline_Point", "x: " + point.x + ", y: " + point.y);
//
//                    // Reset action button's state
//                    getUnit().getClay().fabStatus = Clay.FAB_STOP_DRAGGING;
//
//                    // TODO: If no contextual cue is nearby, start animation to return home (warp? translate?).
//
//                    fab.requestLayout();
//                    fab.invalidate();
//
//                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
//
////                    Point point = TimelineListView.getTimelineListView().getPointUnderTimeline();
//
//                    getUnit().getClay().fabCurrentPoint = new Point((int) event.getX(), (int) event.getY());
//
//                    if (getUnit().getClay().fabStatus == Clay.FAB_STOP_DRAGGING) {
//
//                        double distance = Math.sqrt(Math.pow((getUnit().getClay().fabCurrentPoint.x - getUnit().getClay().fabDownPoint.x), 2) + Math.pow((getUnit().getClay().fabCurrentPoint.y - getUnit().getClay().fabDownPoint.y), 2));
//
//                        // Check if drag threshold is exceeded
//                        if (distance > getUnit().getClay().fabStartDragThresholdDistance) {
//                            getUnit().getClay().fabStatus = Clay.FAB_START_DRAGGING;
//                        }
//                    }
//
//                    if (getUnit().getClay().fabStatus == Clay.FAB_START_DRAGGING) {
//
//                        // Update placement of action button
//                        int width = fab.getWidth();
//                        int height = fab.getHeight();
//                        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
//                        params.leftMargin = (int) event.getRawX() - (int) (width / 2.0);
//                        params.topMargin = (int) event.getRawY() - (int) (height / 2.0);
//
//                        fab.requestLayout();
//                        fab.invalidate();
//
//                        // Move Clay
////                        int width = fab.getWidth();
////                        int height = fab.getHeight();
////                        Point dest = new Point((int) event.getRawX() - (int) (width / 2.0), (int) event.getRawY() - (int) (height / 2.0));
////                        ApplicationView.getApplicationView().moveViewToScreenCenter(fab, dest, 100);
//
////                        if (getUnit().getClay().selectedEventHolder != null) {
////                            timelineView.removeEventHolder(getUnit().getClay().selectedEventHolder);
////                        }
//
//                        // TODO: Search for nearest/nearby events, actions, states, etc. (i.e., discovery context)
//                        TimelineListView.getTimelineListView().resetViewBackgrounds();
////                        TimelineListView.getTimelineListView().findNearbyViews((int) event.getRawX(), (int) event.getRawY());
//                        int nearestViewIndex = TimelineListView.getTimelineListView().findNearestTimelineIndex((int) event.getRawX(), (int) event.getRawY());
//
//                        Log.v("Dist", "---");
//
//                        // Check if distance is within selection threshold
//                        Rect rect = new Rect();
//                        View nearestView = timelineView.getViewByPosition((int) event.getRawX(), (int) event.getRawY()); // ByIndex(nearestViewIndex);
//                        if (nearestViewIndex == -1) {
//                            nearestView = null;
//                        }
//
//                        Log.v ("Dist", "nearestViewIndex: " + nearestViewIndex);
//                        Log.v ("Dist", "nearestView: " + nearestView);
//                        if (nearestView != null) {
//                            Log.v("Dist", "rawY: " + (int) event.getRawY());
//
//                            // Get screen coordinates of the nearest view
//                            int[] listViewCoords = new int[2];
//                            nearestView.getLocationOnScreen(listViewCoords);
//                            // int x = (int) listViewCoords[0];
//                            int y = (int) listViewCoords[1] + (int) (nearestView.getHeight() / 2.0);
//
//                            // Compute distance to the top and bottom of the view
//                            int distanceToTop = Math.abs(y - (int) event.getRawY());
//                            int distanceToBottom = Math.abs((y + nearestView.getHeight()) - (int) event.getRawY());
//
//                            if (distanceToTop < 10) {
//
//                                getUnit().getClay().selectedEventHolderIndex = nearestViewIndex;
//
//                                // Highlight the position where Clay will add the event on the timeline
//                                if (getUnit().getClay().selectedEventHolder == null) {
//                                    // timelineView.resetHighlights();
//                                    getUnit().getClay().selectedEventHolder = new EventHolder("highlight", "highlight");
//                                    timelineView.addEventHolder(getUnit().getClay().selectedEventHolderIndex, getUnit().getClay().selectedEventHolder);
//                                } else {
//                                    timelineView.removeEventHolder(getUnit().getClay().selectedEventHolder);
//                                    timelineView.addEventHolder(getUnit().getClay().selectedEventHolderIndex, getUnit().getClay().selectedEventHolder);
//                                }
//
//                            } else if (distanceToBottom < 10) {
//
//                                getUnit().getClay().selectedEventHolderIndex = nearestViewIndex;
//
//                                // Highlight the position where Clay will add the event on the timeline
//                                if (getUnit().getClay().selectedEventHolder == null) {
//                                    // timelineView.resetHighlights();
//                                    getUnit().getClay().selectedEventHolder = new EventHolder("highlight", "highlight");
//                                    timelineView.addEventHolder(getUnit().getClay().selectedEventHolderIndex, getUnit().getClay().selectedEventHolder);
//                                } else {
//                                    timelineView.removeEventHolder(getUnit().getClay().selectedEventHolder);
//                                    timelineView.addEventHolder(getUnit().getClay().selectedEventHolderIndex, getUnit().getClay().selectedEventHolder);
//                                }
//
//                            }
//                        }
//
////                        fab.requestLayout();
////                        fab.invalidate();
//
//                        // Set return value (halts event propagation if true)
//                        returnVal = true;
//                    }
//                }
//
//                return returnVal;
//            }
//        });
//
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
////                if (getUnit().getClay().fabDisableClick == false) {
////                if (getUnit().getClay().selectedEventHolderIndex != -1) {
//                    timelineView.displayActionBrowser(new TimelineListView.ActionSelectionListener() {
//                        @Override
//                        public void onSelect(Action action) {
//
//                            if (getUnit().getClay().selectedEventHolderIndex != -1) {
//                                timelineView.replaceEventHolder(getUnit().getClay().selectedEventHolder, action);
////                                timelineView.redrawListViewFromData();
//                            } else {
//                                EventHolder eventHolder = new EventHolder("choose", "choose");
//                                timelineView.addEventHolder(eventHolder);
//                                timelineView.replaceEventHolder(eventHolder, action);
//                            }
//
//                            // <FAB>
//                            timelineView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
//                                @Override
//                                public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
//
//                                    // Update position
//                                    timelineView.fabUpdatePosition(fab);
//
//                                    // Remove the layout
//                                    timelineView.removeOnLayoutChangeListener(this);
//                                    Log.e("Move_Finger", "Updated timeline layout.");
//                                }
//                            });
//
//                            timelineView.refreshTimelineView();
//                            // </FAB>
//                        }
//                    });
//
////                }
//
//                getUnit().getClay().fabDisableClick = false;
//            }
//        });
//        // </HACK>

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
