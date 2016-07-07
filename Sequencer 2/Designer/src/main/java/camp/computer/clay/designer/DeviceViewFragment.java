package camp.computer.clay.designer;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import com.github.clans.fab.FloatingActionButton;
import com.mobeta.android.sequencer.R;

import camp.computer.clay.system.Device;

public class DeviceViewFragment extends Fragment {

    // The Clay device associated with this fragment.
    private Device device;

    // The timeline view used to draw the timeline.
    private TimelineView timelineView;

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

        FrameLayout root = (FrameLayout) Application.getDisplay().findViewById(R.id.application_view);
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
        timelineView = (TimelineView) rootView.findViewById(R.id.listview_timeline);
        timelineView.setTag(getArguments().getInt(ARG_SECTION_NUMBER));

        // Create behavior profiles for the device's behaviors and assign the data to the ListView
        timelineView.setTimeline(this.device.getTimeline());

        // <HACK>
        timelineView.setDevice(device);
        // </HACK>

        if (DISABLE_SCROLLBAR_FADING) {
            timelineView.setScrollbarFadingEnabled(false);
        }

        // <HACK>
        if (!Application.getDisplay().timelineViews.contains(timelineView)) {
            Application.getDisplay().timelineViews.add(timelineView);
        }
        // </HACK>

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
//        final FloatingActionButton fab = (FloatingActionButton) Application.getDisplay().findViewById(R.id.fab_create);
//
//        final ArrayList<FloatingActionButton> fablets = getDevice().getClay().fablets;
//
//        fab.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                boolean returnVal = false;
//                if (event.getAction() == MotionEvent.ACTION_DOWN) {
//
//                    // Reset selection
//                    getDevice().getClay().selectedEventHolderIndex = -1;
//                    getDevice().getClay().selectedEventHolder = null;
//
//                    /*
//                    // Remove generated action buttons
//                    if (fablets.size() > 0) {
//                        for (FloatingActionButton fablet : fablets) {
//                            if (!fablet.isHidden()) {
//                                fablet.hide(true);
//                            }
//
//                            FrameLayout root = (FrameLayout) Application.getDisplay().findViewById(R.id.application_view);
//                            root.removeView(fablet);
//                        }
//                        fablets.erase();
//                    }
//                    */
//
//                    // Save first touchPositions point
//                    getDevice().getClay().fabDownPoint = new Point((int) event.getX(), (int) event.getY());
//                }
//
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//
//                    // If dragging, skip the click event following release
//                    if (getDevice().getClay().fabStatus == Clay.FAB_START_DRAGGING) {
////                        getDevice().getClay().fabDisableClick = true;
//
//                        /*
//                        // <TEST>
//                        // Move to original location
//
//                        // Get screen width and height of the device
//                        DisplayMetrics metrics;
//                        int screenWidth = 0, screenHeight = 0;
//                        metrics = new DisplayMetrics();
//                        Application.getDisplay().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//                        screenHeight = metrics.heightPixels;
//                        screenWidth = metrics.widthPixels;
//
//                        int width = fab.getWidth();
//                        int height = fab.getHeight();
//                        Point dest = new Point((int) screenWidth - (int) (width * 1.1), (int) (screenHeight / 2.0) - (int) (height / 2.0));
//                        Application.getDisplay().moveToPoint(fab, dest, 100);
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
//                    getDevice().getClay().fabStatus = Clay.FAB_STOP_DRAGGING;
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
//                    getDevice().getClay().fabCurrentPoint = new Point((int) event.getX(), (int) event.getY());
//
//                    if (getDevice().getClay().fabStatus == Clay.FAB_STOP_DRAGGING) {
//
//                        double distance = Math.sqrt(Math.pow((getDevice().getClay().fabCurrentPoint.x - getDevice().getClay().fabDownPoint.x), 2) + Math.pow((getDevice().getClay().fabCurrentPoint.y - getDevice().getClay().fabDownPoint.y), 2));
//
//                        // Check if drag threshold is exceeded
//                        if (distance > getDevice().getClay().fabStartDragThresholdDistance) {
//                            getDevice().getClay().fabStatus = Clay.FAB_START_DRAGGING;
//                        }
//                    }
//
//                    if (getDevice().getClay().fabStatus == Clay.FAB_START_DRAGGING) {
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
////                        Application.getDisplay().moveToPoint(fab, dest, 100);
//
////                        if (getDevice().getClay().selectedEventHolder != null) {
////                            timelineView.removeEventHolder(getDevice().getClay().selectedEventHolder);
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
//                                getDevice().getClay().selectedEventHolderIndex = nearestViewIndex;
//
//                                // Highlight the position where Clay will add the event on the timeline
//                                if (getDevice().getClay().selectedEventHolder == null) {
//                                    // timelineView.resetHighlights();
//                                    getDevice().getClay().selectedEventHolder = new EventHolder("highlight", "highlight");
//                                    timelineView.addEventHolder(getDevice().getClay().selectedEventHolderIndex, getDevice().getClay().selectedEventHolder);
//                                } else {
//                                    timelineView.removeEventHolder(getDevice().getClay().selectedEventHolder);
//                                    timelineView.addEventHolder(getDevice().getClay().selectedEventHolderIndex, getDevice().getClay().selectedEventHolder);
//                                }
//
//                            } else if (distanceToBottom < 10) {
//
//                                getDevice().getClay().selectedEventHolderIndex = nearestViewIndex;
//
//                                // Highlight the position where Clay will add the event on the timeline
//                                if (getDevice().getClay().selectedEventHolder == null) {
//                                    // timelineView.resetHighlights();
//                                    getDevice().getClay().selectedEventHolder = new EventHolder("highlight", "highlight");
//                                    timelineView.addEventHolder(getDevice().getClay().selectedEventHolderIndex, getDevice().getClay().selectedEventHolder);
//                                } else {
//                                    timelineView.removeEventHolder(getDevice().getClay().selectedEventHolder);
//                                    timelineView.addEventHolder(getDevice().getClay().selectedEventHolderIndex, getDevice().getClay().selectedEventHolder);
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
////                if (getDevice().getClay().fabDisableClick == false) {
////                if (getDevice().getClay().selectedEventHolderIndex != -1) {
//                    timelineView.displayActionBrowser(new TimelineListView.ActionSelectionListener() {
//                        @Override
//                        public void onSelect(Action action) {
//
//                            if (getDevice().getClay().selectedEventHolderIndex != -1) {
//                                timelineView.replaceEventHolder(getDevice().getClay().selectedEventHolder, action);
////                                timelineView.redrawListViewFromData();
//                            } else {
//                                EventHolder eventHolder = new EventHolder("list", "list");
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
//                                    timelineView.updatePosition(fab);
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
//                getDevice().getClay().fabDisableClick = false;
//            }
//        });
//        // </HACK>

        return rootView;
    }

    public void setDevice(Device device) {
        this.device = device;

        // Create behavior profiles for the timeline
//            createTimelineEvents();
    }

    public Device getDevice() {
        return this.device;
    }

    public TimelineView getTimelineView () {
        return this.timelineView;
    }

}
