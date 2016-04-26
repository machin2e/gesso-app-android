package camp.computer.clay.sequencer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;

import com.mobeta.android.sequencer.R;

import java.util.Calendar;
import java.util.Date;

import camp.computer.clay.system.Device;

public class DeviceViewPager extends ViewPager {

    private static boolean ENABLE_TOUCH = true;

    private static int BACKGROUND_COLOR = Color.BLACK;

    private boolean disableSelectingEvents = true;

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

    int currentViewTag = 0;
    TimelineView currentListView = null;

    public DeviceViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setBackgroundColor(BACKGROUND_COLOR);

        this.addOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.v("Scroller", "onPageScrolled");
            }

            @Override
            public void onPageSelected(int position) {
                Log.v("Device", "setting position to " + position);
                // Select the page at the specified position
                currentViewTag = position;
                currentListView = (TimelineView) findViewWithTag(currentViewTag);
                // <HACK>
                ApplicationView.getApplicationView().getCursorView().init();
                // </HACK>
                ApplicationView.getApplicationView().getCursorView().updatePosition();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                Log.v("Scroller", "onPageScrollStateChanged state = " + state);
            }
        });
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        DeviceViewPager viewPager = (DeviceViewPager) findViewById(R.id.pager);
//        Drawable backgroundImage = getDrawable(R.drawable.clay_background_image);
//        viewPager.setBackground(backgroundImage);
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), R.drawable.clay_background_image);
        BitmapDrawable bitmapDrawable = new BitmapDrawable(bmp);
        bitmapDrawable.setGravity(Gravity.CENTER);

//        Canvas canvas = new Canvas(bmp.copy(Bitmap.Config.ARGB_8888, true));
        Paint p = new Paint();
        p.setColor(Color.BLACK);
        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap gbmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
        Canvas canvas = new Canvas(gbmp);
        canvas.drawRect(0, 0, w, h, p);
        //canvas.drawBitmap(bmp, viewPager.getWidth() / 2.0f, viewPager.getHeight() / 2.0f, p);
        canvas.drawBitmap(bmp, (w / 2.0f)  - (bmp.getWidth() / 2.0f), (h / 2.0f) - (bmp.getHeight() / 2.0f), p);
        BitmapDrawable gbitmapDrawable = new BitmapDrawable(gbmp);

        viewPager.setBackground(gbitmapDrawable);
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

        if (currentListView == null) {
            currentListView = (TimelineView) findViewWithTag (currentViewTag);
            // <HACK>
            ApplicationView.getApplicationView().getCursorView().init();
            // </HACK>
        }

        Log.v ("Touch_Event", "onTouchEvent");

        final int action = event.getAction() & MotionEventCompat.ACTION_MASK;
        int counter = event.getPointerCount();

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

                // Calculate distance
                int distance = (int) Math.sqrt(Math.pow(currentTouch.x - previousTouch.x, 2) + Math.pow (currentTouch.y - previousTouch.y, 2));
                touchDistance += distance;

                // Get current view
                int index = currentListView.getViewIndexByPosition((int) event.getRawX(), (int) event.getRawY());
                if (index != -1) {
                    currentListView.selectEventHolderByIndex(index);
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

                // Update gesture classification
                currentListView.composeEventHolderSelection();

            }

            return interceptTouches;
        }

//        if (this.ENABLE_TOUCH) {
//            return super.onTouchEvent(event);
//        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent (MotionEvent event) {
        String touchLocation = "" + event.getX () + ", " + event.getY ();

        Log.v ("Touch_Event", "onInterceptTouchEvent");

        // Get the number of touch points
        int touchPointCount = event.getPointerCount();

        // Check if interacting with a list item
        if (currentListView == null) {
            currentListView = (TimelineView) findViewWithTag (currentViewTag);
            // <HACK>
            ApplicationView.getApplicationView().getCursorView().init();
            // </HACK>
        }


        // Handle touch event based on the number of touches detected and the current state
        // of the gesture recognition logic.
        if (touchPointCount == 1) {

            if (event.getAction () == MotionEvent.ACTION_DOWN) {

                Log.v ("Touch_Event", "Touched event tag");

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
                if (!disableSelectingEvents) {
                    if (startTouch.x < 200) {
                        interceptTouches = true;
                    } else {
                        interceptTouches = false;
                    }
                }

            } else if (event.getAction () == MotionEvent.ACTION_UP) {

                Log.v ("Touch_Event", "Released event tag");

                // Save the current touch point...
                currentTouch.set((int) event.getX(), (int) event.getY());
                currentTime = startTime;

                // ...and compute the distance between the touch down and up actions...
                int distance = (int) Math.sqrt(Math.pow(currentTouch.x - previousTouch.x, 2) + Math.pow (currentTouch.y - previousTouch.y, 2));

                if (distance < 50) {
                    Log.v ("Touch_Event", "distance: " + distance);
                    if (startTouch.x < 200) {
                        Log.v ("Touch_Event", "startTouch.x < 200");
                        EventHolder eventHolder = ApplicationView.getApplicationView().getTimelineView().getEventHolderByPosition(currentTouch.x, currentTouch.y);
                        if (eventHolder != null) {
                            ApplicationView.getApplicationView().getTimelineView().expandEventView(eventHolder);
                        }
//                        interceptTouches = true;
                    } else {
//                        interceptTouches = false;
                    }
                }

            }

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

    public void setPagingEnabled (boolean enabled) {
        this.ENABLE_TOUCH = enabled;
    }

    @Override
    protected void onPageScrolled(int position, float offset, int offsetPixels) {
        super.onPageScrolled(position, offset, offsetPixels);

        Log.v("Scroller", "onPageScrolled");
    }

    /**
     * Returns the currently-selected timeline view.
     * @return
     */
    public TimelineView getTimelineView () {
        return currentListView;
    }

    public void setTimelineView (Device device) {
        DeviceViewPagerAdapter adapter = (DeviceViewPagerAdapter) this.getAdapter();
        int deviceCount = adapter.getCount();
        for (int i = 0; i < deviceCount; i++) {
            DeviceViewFragment fragment = (DeviceViewFragment) adapter.getItem(i);
            if (fragment.getDevice().getUuid().equals(device.getUuid())) {
                this.setCurrentItem(i);
                break;
            }
        }
    }
}
