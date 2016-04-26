package camp.computer.clay.sequencer;

import android.graphics.Point;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

import com.github.clans.fab.FloatingActionButton;
import com.mobeta.android.sequencer.R;

import java.util.ArrayList;

import camp.computer.clay.system.Action;
import camp.computer.clay.system.Device;

public class CursorView /* extends FloatingActionButton */ {

    public FloatingActionButton fab = null;
    public ArrayList<FloatingActionButton> fablets = new ArrayList<FloatingActionButton>();
    public Point fabDownPoint;
    public Point fabCurrentPoint;
    public static int FAB_STOP_DRAGGING = 0;
    public static int FAB_START_DRAGGING = 1;
    public int fabStatus = FAB_STOP_DRAGGING;
    public boolean fabDisableClick = false;
    public int fabStartDragThresholdDistance = 75;
    public int selectedEventHolderIndex = -1;
    public EventHolder selectedEventHolder = null;

    CursorView() {

        // Set up FAB
        fab = (FloatingActionButton) ApplicationView.getApplicationView().findViewById(R.id.fab_create);
    }

    // <FAB>

    public void setUpActionButton() {
//        // Get screen width and height of the device
//        DisplayMetrics metrics;
//        int screenWidth = 0, screenHeight = 0;
//        metrics = new DisplayMetrics();
//        ApplicationView.getApplicationView().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        screenHeight = metrics.heightPixels;
//        screenWidth = metrics.widthPixels;
//
//        Log.v ("Metrics", "width: " + screenWidth);
//        Log.v ("Metrics", "height: " + screenHeight);
//
//        // Update placement of action button (default)
//        int width = fab.getWidth();
//        int height = fab.getHeight();
//        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
//        params.leftMargin = (int) screenWidth - (int) (width * 1.1);
//        params.topMargin = (int) (screenHeight / 2.0) - (int) (height / 2.0);

        ApplicationView.getApplicationView().getTimelineView().addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

                // Update position
                //TimelineListView.getTimelineListView().updatePosition();
                updatePosition();

                // Remove the layout
                ApplicationView.getApplicationView().getTimelineView().removeOnLayoutChangeListener(this);
//                TimelineListView.getTimelineListView().removeOnLayoutChangeListener(this);
                Log.e("Move_Finger", "Updated timeline layout.");
            }
        });

        ApplicationView.getApplicationView().getTimelineView().refreshTimelineView();
    }

//    private void moveUnderTimeline(FloatingActionButton fab) {
//        // Get screen width and height of the device
//        DisplayMetrics metrics;
//        int screenWidth = 0, screenHeight = 0;
//        metrics = new DisplayMetrics();
//        ApplicationView.getApplicationView().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        screenHeight = metrics.heightPixels;
//        screenWidth = metrics.widthPixels;
//
//        Log.v ("Metrics", "width: " + screenWidth);
//        Log.v ("Metrics", "height: " + screenHeight);
//
//        // Update placement of action button (default)
//        int width = fab.getWidth();
//        int height = fab.getHeight();
//        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
//        params.leftMargin = (int) 200; // i.e., width of event label
//
//        int eventCount = (int) TimelineListView.getTimelineListView().getChildCount();
//        params.topMargin = (int) TimelineListView.getTimelineListView().getChildAt(eventCount).getScrollY();
//    }
// </FAB>

    public void updatePosition() {

        if (fabStatus != FAB_START_DRAGGING) {

            // Get screen width and height of the device
            DisplayMetrics metrics;
            int screenWidth = 0, screenHeight = 0;
            metrics = new DisplayMetrics();
            ApplicationView.getApplicationView().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            screenHeight = metrics.heightPixels;
            screenWidth = metrics.widthPixels;

            if (ApplicationView.getApplicationView().getTimelineView().getEventHolders().size() == 0) {
                int width = fab.getWidth();
                int height = fab.getHeight();
                int xOffset = 170; // TODO: Make dynamic
                int yOffset = 55; // TODO: Make dynaimc
                Point dest = new Point((int) (screenWidth / 2.0) - (int) (width / 2.0) + xOffset, (int) (screenHeight / 2.0) - (int) (height / 2.0) + yOffset);
//            ApplicationView.getApplicationView().moveToPoint(fab, dest, 400);

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) fab.getLayoutParams();
                params.leftMargin = (int) dest.x;
                params.topMargin = (int) dest.y;
                return;
            }

            // Get point under last event on timeline
            Point point = ApplicationView.getApplicationView().getTimelineView().getPointUnderTimeline();
            if (point != null) {
                point.x = 90; // TODO: Dynamically get x coordinate of timeline
                point.y = point.y + (int) (0.01 * fab.getHeight());
            }

            if (point != null && point.y < (screenHeight - fab.getHeight())) {
                // Timeline does not fill screen
                moveToPoint(fab, point, 400);
            } else {
                // Timeline fills screen
                int width = fab.getWidth();
                int height = fab.getHeight();
                Point dest = new Point((int) screenWidth - (int) (width * 1.1), (int) (screenHeight / 2.0) - (int) (height / 2.0));

                moveToPoint(fab, dest, 400);

            }

        }
    }



    // Based on: http://stackoverflow.com/questions/10276251/how-to-animate-a-view-with-translate-animation-in-android
    public void moveToPoint (final View view, Point destinationPoint, int translateDuration)
    {
        FrameLayout root = (FrameLayout) ApplicationView.getApplicationView().findViewById(R.id.application_view);
        DisplayMetrics dm = new DisplayMetrics();
        ApplicationView.getApplicationView().getWindowManager().getDefaultDisplay().getMetrics( dm );
        int statusBarOffset = dm.heightPixels - root.getMeasuredHeight();

        int originalPos[] = new int[2];
        view.getLocationOnScreen( originalPos );

        /*
        int xDest = dm.widthPixels/2;
        xDest -= (view.getMeasuredWidth()/2);
        int yDest = dm.heightPixels/2 - (view.getMeasuredHeight()/2) - statusBarOffset;
        */

        int xDest = destinationPoint.x;
        int yDest = destinationPoint.y;


        final int amountToMoveRight = xDest - originalPos[0];
        final int amountToMoveDown = yDest - originalPos[1];
        TranslateAnimation animation = new TranslateAnimation(0, amountToMoveRight, 0, amountToMoveDown);
        animation.setDuration(translateDuration);
        // animation.setFillAfter(true);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                params.topMargin += amountToMoveDown;
                params.leftMargin += amountToMoveRight;
                view.setLayoutParams(params);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        view.startAnimation (animation);
    }

    public void init() {
        // <HACK>

        fab.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean returnVal = false;

                final TimelineView timelineView = ApplicationView.getApplicationView().getTimelineView();

                if (event.getAction() == MotionEvent.ACTION_DOWN) {

                    // Reset selection
                    selectedEventHolderIndex = -1;
                    selectedEventHolder = null;

                    /*
                    // Remove generated action buttons
                    if (fablets.size() > 0) {
                        for (FloatingActionButton fablet : fablets) {
                            if (!fablet.isHidden()) {
                                fablet.hide(true);
                            }

                            FrameLayout root = (FrameLayout) ApplicationView.getApplicationView().findViewById(R.id.application_view);
                            root.removeView(fablet);
                        }
                        fablets.erase();
                    }
                    */

                    // Save first touch point
                    fabDownPoint = new Point((int) event.getX(), (int) event.getY());
                }

                if (event.getAction() == MotionEvent.ACTION_UP) {

                    // If dragging, skip the click event following release
                    if (fabStatus == FAB_START_DRAGGING) {
//                        getDevice().getClay().fabDisableClick = true;

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
                        ApplicationView.getApplicationView().moveToPoint(fab, dest, 100);
                        // </TEST>
                        */

                        /*
                        // Generate action path selectors
                        if (fablets.isEmpty()) {

                            FloatingActionButton newFab1 = generateFablet((int) fab.getX() - 100, (int) fab.getY() - 150, true);
                            FloatingActionButton newFab2 = generateFablet((int) fab.getX() - 150, (int) fab.getY(), true);
                            FloatingActionButton newFab3 = generateFablet((int) fab.getX() - 100, (int) fab.getY() + 150, true);

                            fablets.add(newFab1);
                            fablets.add(newFab2);
                            fablets.add(newFab3);

                            // Show fablet
                            newFab1.show(true);
                            newFab2.show(true);
                            newFab3.show(true);
                        }
                        */
                    }
//                    Log.v ("Timeline_Point", "x: " + point.x + ", y: " + point.y);

                    // Reset action button's state
                    fabStatus = FAB_STOP_DRAGGING;

                    // TODO: If no contextual cue is nearby, start animation to return home (warp? translate?).

                    fab.requestLayout();
                    fab.invalidate();

                } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

//                    Point point = TimelineListView.getTimelineListView().getPointUnderTimeline();

                    fabCurrentPoint = new Point((int) event.getX(), (int) event.getY());

                    if (fabStatus == FAB_STOP_DRAGGING) {

                        double distance = Math.sqrt(Math.pow((fabCurrentPoint.x - fabDownPoint.x), 2) + Math.pow((fabCurrentPoint.y - fabDownPoint.y), 2));

                        // Check if drag threshold is exceeded
                        if (distance > fabStartDragThresholdDistance) {
                            fabStatus = FAB_START_DRAGGING;

                            // Update timeline view
                            timelineView.resetEventViews();
                            timelineView.refreshTimelineView();
                            timelineView.refreshAvatarView();
                        }
                    }

                    if (fabStatus == FAB_START_DRAGGING) {

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
//                        ApplicationView.getApplicationView().moveToPoint(fab, dest, 100);

//                        if (getDevice().getClay().selectedEventHolder != null) {
//                            timelineView.removeEventHolder(getDevice().getClay().selectedEventHolder);
//                        }

                        // TODO: Search for nearest/nearby events, actions, states, etc. (i.e., discovery context)
                        ApplicationView.getApplicationView().getTimelineView().resetViewBackgrounds();
//                        TimelineListView.getTimelineListView().findNearbyViews((int) event.getRawX(), (int) event.getRawY());
                        int nearestViewIndex = ApplicationView.getApplicationView().getTimelineView().findNearestTimelineIndex((int) event.getRawX(), (int) event.getRawY());

                        Log.v("Dist", "---");

                        // Check if distance is within selection threshold
                        Rect rect = new Rect();
                        View nearestView = timelineView.getViewByPosition((int) event.getRawX(), (int) event.getRawY()); // ByIndex(nearestViewIndex);
                        if (nearestViewIndex == -1) {
                            nearestView = null;
                        }

                        Log.v("Dist", "nearestViewIndex: " + nearestViewIndex);
                        Log.v("Dist", "nearestView: " + nearestView);
                        if (nearestView != null) {
                            Log.v("Dist", "rawY: " + (int) event.getRawY());

                            // Get screen coordinates of the nearest view
                            int[] listViewCoords = new int[2];
                            nearestView.getLocationOnScreen(listViewCoords);
                            // int x = (int) listViewCoords[0];
                            int y = (int) listViewCoords[1] + (int) (nearestView.getHeight() / 2.0);

                            // Compute distance to the top and bottom of the view
                            int distanceToTop = Math.abs(y - (int) event.getRawY());
                            int distanceToBottom = Math.abs((y + nearestView.getHeight()) - (int) event.getRawY());

                            if (distanceToTop < 10) {

                                selectedEventHolderIndex = nearestViewIndex;

                                // Highlight the position where Clay will add the event on the timeline
                                if (selectedEventHolder == null) {
                                    // timelineView.resetHighlights();
                                    selectedEventHolder = new EventHolder("highlight", "highlight");
                                    timelineView.addEventHolder(selectedEventHolderIndex, selectedEventHolder);
                                } else {
                                    timelineView.removeEventHolder(selectedEventHolder);
                                    timelineView.addEventHolder(selectedEventHolderIndex, selectedEventHolder);
                                }

                            } else if (distanceToBottom < 10) {

                                selectedEventHolderIndex = nearestViewIndex;

                                // Highlight the position where Clay will add the event on the timeline
                                if (selectedEventHolder == null) {
                                    // timelineView.resetHighlights();
                                    selectedEventHolder = new EventHolder("highlight", "highlight");
                                    timelineView.addEventHolder(selectedEventHolderIndex, selectedEventHolder);
                                } else {
                                    timelineView.removeEventHolder(selectedEventHolder);
                                    timelineView.addEventHolder(selectedEventHolderIndex, selectedEventHolder);
                                }

                            }
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

                final TimelineView timelineView = ApplicationView.getApplicationView().getTimelineView();

                // Update timeline view
                timelineView.resetEventViews();
                timelineView.refreshTimelineView();
                timelineView.refreshAvatarView();

//                if (getDevice().getClay().fabDisableClick == false) {
//                if (getDevice().getClay().selectedEventHolderIndex != -1) {
                timelineView.displayActionBrowser(new TimelineView.ActionSelectionListener() {
                    @Override
                    public void onSelect(Action action) {

                        if (selectedEventHolderIndex != -1) {
                            timelineView.replaceEventHolder(selectedEventHolder, action);
//                                timelineView.redrawListViewFromData();
                        } else {
                            EventHolder eventHolder = new EventHolder("choose", "choose");
                            timelineView.addEventHolder(eventHolder);
                            timelineView.replaceEventHolder(eventHolder, action);
                        }

                        // <FAB>
                        timelineView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                            @Override
                            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {

                                // Update position
                                updatePosition();

                                // Remove the layout
                                timelineView.removeOnLayoutChangeListener(this);
                                Log.e("Move_Finger", "Updated timeline layout.");
                            }
                        });

                        timelineView.refreshTimelineView();
                        // </FAB>
                    }
                });

//                }

                fabDisableClick = false;
            }
        });

        fab.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                final TimelineView timelineView = ApplicationView.getApplicationView().getTimelineView();

                timelineView.displayDeviceBrowser(new TimelineView.DeviceSelectionListener() {
                    @Override
                    public void onSelect(Device device) {
                        ApplicationView.getApplicationView().setTimelineView(device);
                    }
                });

//                }

//                fabDisableClick = false;

                return false;
            }
        });
        // </HACK>
    }

    // <HACK>
    public void hide(boolean b) {
        fab.hide(b);
    }

    public void show(boolean b) {
        fab.show(b);
    }
    // </HACK>
}
