package camp.computer.clay.sequencer;

import android.content.Context;
import android.graphics.Point;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.Calendar;
import java.util.Date;

public class CustomViewPager extends ViewPager {

    private static boolean ENABLE_TOUCH = true;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * References:
     * - ViewPager.OnPageChangeListener
     *   http://developer.android.com/reference/android/support/v4/view/ViewPager.OnPageChangeListener.html
     * - Android Developers
     *   http://developer.android.com/reference/android/support/v4/view/ViewPager.html#addOnPageChangeListener(android.support.v4.view.ViewPager.OnPageChangeListener)
     *
     * @param listener
     */
    @Override
    public void addOnPageChangeListener(OnPageChangeListener listener) {
        super.addOnPageChangeListener(listener);
        // TODO: http://developer.android.com/reference/android/support/v4/view/ViewPager.OnPageChangeListener.html
    }

    @Override
    public void removeOnPageChangeListener(OnPageChangeListener listener) {
        super.removeOnPageChangeListener(listener);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v("Gesture_Log", "onTouchEvent from CustomViewPager");

        final int action = event.getAction() & MotionEventCompat.ACTION_MASK;
        int counter = event.getPointerCount();

        int currentViewTag = 1; // TODO: Get the tag for the active page in the ViewPager
        CustomListView currentListView = (CustomListView) findViewWithTag(currentViewTag);
        if (currentListView.itemHasFocus) {
            Log.v ("Gesture_Log_2", "itemHasFocus!!");
            return false;
        }

        // If onInterceptTouch event returned true, then touch events will be directed here.
        // Check for that condition.
        if (interceptTouches) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                // Save the start point and time of the gesture...
                startTouch.set((int) event.getX(), (int) event.getY());
                startTime = calendar.getTime();

                // ...and the previous touch...
                previousTouch.set((int) event.getX(), (int) event.getY());
                previousTime = startTime;

                // ...and the current touch.
                currentTouch.set((int) event.getX(), (int) event.getY());
                currentTime = startTime;

                // Reset the touch distance
                touchDistance = 0;

//                // Update the gesture classification
//                if (startTouch.x < 200) {
//                    interceptTouches = true;
//                } else {
//                    interceptTouches = false;
//                }

            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                // Update the previous touch...
                previousTouch.set (currentTouch.x, currentTouch.y);
                previousTime = currentTime;

                // ...and update the current point and time of the gesture.
                currentTouch.set((int) event.getX(), (int) event.getY());
                currentTime = calendar.getTime();

                // TODO: Update the gesture classification
                // TODO: Selected any additional list items covered by the drag

                int distance = (int) Math.sqrt(Math.pow(currentTouch.x - previousTouch.x, 2) + Math.pow (currentTouch.y - previousTouch.y, 2));
                touchDistance += distance;
                Log.v ("Gesture_Log", "distance = " + touchDistance);

                // Get current view
                currentViewTag = 1; // TODO: Get the tag for the active page in the ViewPager
                currentListView = (CustomListView) findViewWithTag(currentViewTag);
                int index = currentListView.getViewIndexByPosition ((int) event.getRawX(), (int) event.getRawY());
                if (index != -1) {
                    currentListView.selectItemByIndex(index);
                }

            } else if (event.getAction() == MotionEvent.ACTION_UP) {

                // Update the previous touch...
                previousTouch.set (currentTouch.x, currentTouch.y);
                previousTime = currentTime;

                // ...and update the current point and time of the gesture.
                currentTouch.set((int) event.getX(), (int) event.getY());
                currentTime = calendar.getTime();

                // Save the stop point and time of the gesture
                stopTouch.set((int) event.getX(), (int) event.getY());
                stopTime = calendar.getTime();

                int distance = (int) Math.sqrt(Math.pow(currentTouch.x - previousTouch.x, 2) + Math.pow (currentTouch.y - previousTouch.y, 2));
                touchDistance += distance;
                Log.v ("Gesture_Log", "distance = " + touchDistance);

                // Update gesture classification
                currentListView = (CustomListView) findViewWithTag(1);
                currentListView.abstractSelectedItems();
                currentListView.updateViewFromData();

                Log.v("Gesture_Log", "ACTION_UP (touch)");

            }

            return interceptTouches;
        }

//        if (this.ENABLE_TOUCH) {
//            return super.onTouchEvent(event);
//        }

        return false;
    }

    Calendar calendar = Calendar.getInstance();

    Point startTouch = new Point ();
    Date startTime;
    Point previousTouch = new Point ();
    Date previousTime;
    Point currentTouch = new Point ();
    Date currentTime;
    Point stopTouch = new Point ();
    Date stopTime;

    int dragThreshold = 40;

    int touchDistance = 0;

    boolean interceptTouches = false;
    // boolean itemHasFocus = false; // NOTE: This is located in CustomListView.java

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        Log.v("Gesture_Log", "onInterceptTouchEvent from CustomViewPager");
        String touchLocation = "" + event.getX() + ", " + event.getY();
        Log.v("Gesture_Log", "\tat " + touchLocation);

        // Get the number of touch points
        int touchPointCount = event.getPointerCount();
        Log.v ("Gesture_Log", "Touch points detected: " + touchPointCount);

        // Check if interacting with a list item
        int currentViewTag = 1; // TODO: Get the tag for the active page in the ViewPager
        CustomListView currentListView = (CustomListView) findViewWithTag(currentViewTag);
        if (currentListView.itemHasFocus) {
            Log.v ("Gesture_Log_2", "itemHasFocus!");
            return false;
        }

        // Handle touch event based on the number of touches detected and the current state
        // of the gesture recognition logic.
        if (touchPointCount == 1) {

            if (event.getAction() == MotionEvent.ACTION_DOWN) {

                // Save the start point and time of the gesture...
                startTouch.set((int) event.getX(), (int) event.getY());
                startTime = calendar.getTime();

                // ...and the previous touch...
                previousTouch.set((int) event.getX(), (int) event.getY());
                previousTime = startTime;

                // ...and the current touch.
                currentTouch.set((int) event.getX(), (int) event.getY());
                currentTime = startTime;

                // Reset the touch distance
                touchDistance = 0;

                // Update the gesture classification
                if (startTouch.x < 200) {
                    interceptTouches = true;
                } else {
                    interceptTouches = false;
                }

            }

            /*
            else if (event.getAction() == MotionEvent.ACTION_MOVE) {

                // Update the previous touch...
                previousTouch.set(currentTouch.x, currentTouch.y);
                previousTime = currentTime;

                // ...and update the current point and time of the gesture.
                currentTouch.set((int) event.getX(), (int) event.getY());
                currentTime = calendar.getTime();

                // Update the gesture classification
                // TODO:

                int distance = (int) Math.sqrt(Math.pow(currentTouch.x - previousTouch.x, 2) + Math.pow(currentTouch.y - previousTouch.y, 2));
                touchDistance += distance;
                Log.v("Gesture_Log", "distance = " + touchDistance);

            } else if (event.getAction() == MotionEvent.ACTION_UP) {

                Log.v("Gesture_Log", "ACTION_UP (intercept)");

                // Save the stop point and time of the gesture
                stopTouch.set((int) event.getX(), (int) event.getY());
                stopTime = calendar.getTime();

            }
            */

        } else if (touchPointCount == 2) {

            // TODO: If there's a single touch, then wait for a second touch for some small amount of time (less than 500 ms) to detect a second touch, to account for timing difference between pointer touches.

            interceptTouches = false;

        }

        if (interceptTouches) {
            return true;
        } else {
            return false;
        }

//        if (this.ENABLE_TOUCH) {
//            return super.onInterceptTouchEvent(event);
//        }
//
//        return false;
    }

    public void setPagingEnabled(boolean enabled) {
        this.ENABLE_TOUCH = enabled;
    }
}
