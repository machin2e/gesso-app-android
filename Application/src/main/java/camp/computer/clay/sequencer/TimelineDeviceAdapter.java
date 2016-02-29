package camp.computer.clay.sequencer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class TimelineDeviceAdapter extends BaseAdapter {

    // store the context (as an inflated layout)
    LayoutInflater inflater;

    // store the resource (typically list_item.xml)
    private int resource;

    // Reference to the events
    private ArrayList<EventHolder> events;

    // Layout types
    public static final int SYSTEM_CONTROL_LAYOUT = 0; // This controls the programming interface, not the modules. It's a different category of behavior for managing other behavior controls.
    public static final int CONTROL_PLACEHOLDER_LAYOUT = 1;
    public static final int LIGHT_CONTROL_LAYOUT = 2;
    public static final int IO_CONTROL_LAYOUT = 3;
    public static final int MESSAGE_CONTROL_LAYOUT = 4;
    public static final int WAIT_CONTROL_LAYOUT = 5;
    public static final int SAY_CONTROL_LAYOUT = 6;
    public static final int COMPLEX_LAYOUT = 7;

    /**
     * Default constructor. Creates the new Adaptor object to
     * provide a ListView with events.
     * @param context
     * @param resource
     * @param events
     */
    public TimelineDeviceAdapter(Context context, int resource, ArrayList<EventHolder> events) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.events = events;
    }

    /**
     * Return the size of the events set.
     */
    public int getCount() {
        return this.events.size();
    }

    /**
     * Return an object in the events set.
     */
    public Object getItem(int position) {
        return this.events.get(position);
    }

    /**
     * Return the position provided.
     */
    public long getItemId(int position) {
        return position;
    }

    public int getItemType(int position){
        // Your if else code and return type ( TYPE_1 to TYPE_5 )
        EventHolder eventHolder = (EventHolder) getItem (position);
        return eventHolder.type;
    }

    /**
     * Return a generated view for a position.
     */
    public View getView(final int position, View convertView, final ViewGroup parent) {
        // reuse a given view, or inflate a new one from the xml
        View view;

        // Select the layout for the view based on the type of object being displayed in the view
        int type = getItemType (position);
        int layoutResource = getLayoutByType(type); // Default resource

        // <HACK>
        // This prevents view recycling.
        convertView = null;
        // </HACK>

        // Get the events corresponding to the view
        final EventHolder eventHolder = events.get(position);

        if (convertView == null) {
            view = this.inflater.inflate(layoutResource, parent, false);
        } else {
            view = convertView;
        }

        /*
        // Set up an observer so the geometry of the layout can be accessed.
        final View ref = view;
        view.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                ref.removeOnLayoutChangeListener(this);
                Log.d("Dimensions", "onLayoutChange(" + position + "): height " + (bottom - top));
                //ArrayList<Integer> mHeights = new ArrayList<Integer>();
                //mHeights.set(position, (bottom - top));
                int height = bottom - top;

                drawTimelineSegment(v, events.get(position), height);


                if (eventHolder.type == TimelineDeviceAdapter.WAIT_CONTROL_LAYOUT) {

                    RelativeLayout.LayoutParams layoutParams;

                    // Center the label
                    TextView tv = (TextView) v.findViewById(R.id.label);
                    layoutParams = (RelativeLayout.LayoutParams) tv.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    tv.setLayoutParams(layoutParams);

                    // Center the time in milliseconds
                    TextView time = (TextView) v.findViewById(R.id.text);
                    layoutParams = (RelativeLayout.LayoutParams) time.getLayoutParams();
                    layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
                    time.setLayoutParams(layoutParams);
                }

                //ArrayList<Integer> mDistances = new ArrayList<Integer>();
                int mDistances;
                if(position > 0) {
//                    mDistances.set(position, bottom - top + mDistances.get(position-1) + ((CustomListView)parent).getDividerHeight());
//                    mDistances = bottom - top + mDistances.get(position-1) + ((CustomListView)parent).getDividerHeight();
                }
                //holderRef.distanceFromTop = mDistances.get(position);
//                int distanceFromTop = mDistances.get(position);
//                Log.d("Dimensions", "New height for " + position + " is " + mHeights.get(position) + " Distance: " + mDistances.get(position));
            }
        });
        */

        // Update the list item's view according to the type
        updateViewForType(view, eventHolder);
//        view.invalidate();

        // bind the events to the view object
        return this.bindData(view, position);
    }

    private int getLayoutByType(int type) {
        int resourceForType;
        if (type == SYSTEM_CONTROL_LAYOUT) {
            resourceForType = R.layout.list_item_type_system;
        } else if (type == CONTROL_PLACEHOLDER_LAYOUT) {
            resourceForType = R.layout.list_item_type_placeholder;
        } else if (type == LIGHT_CONTROL_LAYOUT) {
            resourceForType = R.layout.list_item_type_light;
        } else if (type == IO_CONTROL_LAYOUT) {
            resourceForType = R.layout.list_item_type_io;
        } else if (type == MESSAGE_CONTROL_LAYOUT) {
            resourceForType = R.layout.list_item_type_message;
        } else if (type == WAIT_CONTROL_LAYOUT) {
            resourceForType = R.layout.list_item_type_wait;
        } else if (type == SAY_CONTROL_LAYOUT) {
            resourceForType = R.layout.list_item_type_say;
        } else if (type == COMPLEX_LAYOUT) {
            resourceForType = R.layout.list_item_type_complex;
        } else {
            resourceForType = R.layout.list_item_type_light;
        }
        return resourceForType;
    }

    /**
     * Updates the view layout specifically for the type of events that is displayed in it.
     *
     * @param view
     * @param eventHolder
     */
    private void updateViewForType (View view, EventHolder eventHolder) {
        // Update layout of a behavior control
        if (eventHolder.type != SYSTEM_CONTROL_LAYOUT
                && eventHolder.type != CONTROL_PLACEHOLDER_LAYOUT) {

            // Update layout based on state
            if (eventHolder.selected == false) {
                // Update left padding
                view.setPadding(0, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            } else {
                // Update left padding to indent the item
                view.setPadding(120, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            }

        }

        // Set the background color
        if (eventHolder.hasFocus) {
            view.setBackgroundColor(Color.LTGRAY);
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        int segmentLength = 165;
        drawTimelineSegment (view, eventHolder, segmentLength);

        view.invalidate();

        if (eventHolder.type == LIGHT_CONTROL_LAYOUT) {

            // Get layout containing light state visualizations
            LinearLayout preview_layout = (LinearLayout) view.findViewById(R.id.preview_layout);

            int[] previews = new int[12];
            previews[0] = R.id.preview_1;
            previews[1] = R.id.preview_2;
            previews[2] = R.id.preview_3;
            previews[3] = R.id.preview_4;
            previews[4] = R.id.preview_5;
            previews[5] = R.id.preview_6;
            previews[6] = R.id.preview_7;
            previews[7] = R.id.preview_8;
            previews[8] = R.id.preview_9;
            previews[9] = R.id.preview_10;
            previews[10] = R.id.preview_11;
            previews[11] = R.id.preview_12;

            if (preview_layout != null) {

                for (int i = 0; i < previews.length; i++) {

                    // Update image preview
                    ImageView preview = (ImageView) view.findViewById(previews[i]);

                    // Set the width and height of the visualization
                    int w = (preview_layout.getWidth() > 0 ? preview_layout.getWidth() : 20);
                    int h = (preview_layout.getHeight() > 0 ? preview_layout.getHeight() : 20);

                    if (preview != null) {

                        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                        Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
                        Canvas canvas = new Canvas(bmp);

                        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint2.setColor(Color.rgb(255, 61, 61));
                        canvas.drawRect(0, 0, bmp.getWidth(), bmp.getHeight(), paint2);

                        preview.setImageBitmap(bmp);

                    }
                }

                // Update position
                TextView label = (TextView) view.findViewById(R.id.label);
                preview_layout.setX(label.getX());
            }

        } else if (eventHolder.type == IO_CONTROL_LAYOUT) {

            // Get layout containing light state visualizations
            LinearLayout previewLayout = (LinearLayout) view.findViewById(R.id.preview_layout);

            int[] previews = new int[12];
            previews[0] = R.id.preview_1;
            previews[1] = R.id.preview_2;
            previews[2] = R.id.preview_3;
            previews[3] = R.id.preview_4;
            previews[4] = R.id.preview_5;
            previews[5] = R.id.preview_6;
            previews[6] = R.id.preview_7;
            previews[7] = R.id.preview_8;
            previews[8] = R.id.preview_9;
            previews[9] = R.id.preview_10;
            previews[10] = R.id.preview_11;
            previews[11] = R.id.preview_12;

            if (previewLayout != null) {

                for (int i = 0; i < previews.length; i++) {

                    // Update image preview
                    ImageView preview = (ImageView) view.findViewById(previews[i]);

                    int w = (previewLayout.getWidth() > 0 ? previewLayout.getWidth() : 20);
                    int h = (previewLayout.getHeight() > 0 ? previewLayout.getHeight() : 20);

                    if (preview != null) {

                        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
                        Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
                        Canvas canvas = new Canvas(bmp);

                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint.setColor(Color.rgb(255, 61, 61));
                        canvas.drawRect(0, 0, bmp.getWidth(), bmp.getHeight(), paint);

                        preview.setImageBitmap(bmp);

                    }
                }

                // Update the position
                TextView label = (TextView) view.findViewById(R.id.label);
                previewLayout.setX(label.getX());
            }
        } else if (eventHolder.type == TimelineDeviceAdapter.MESSAGE_CONTROL_LAYOUT) {

            // TODO:

        } else if (eventHolder.type == TimelineDeviceAdapter.WAIT_CONTROL_LAYOUT) {

            // TODO:

        } else if (eventHolder.type == TimelineDeviceAdapter.SAY_CONTROL_LAYOUT) {

            // TODO:

        } else if (eventHolder.type == TimelineDeviceAdapter.COMPLEX_LAYOUT) {

            // TODO:

        }
    }

    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    /**
     * Draws the timeline segment of the specified length (in pixels) for specified view and item.
     * @param view
     * @param eventHolder
     * @param heightInPixels
     */
    private void drawTimelineSegment (View view, EventHolder eventHolder, int heightInPixels) {

        ImageView imageView = (ImageView) view.findViewById(R.id.icon);

        int w = (int) convertDpToPixel((float) 22.0, view.getContext());
        int h = heightInPixels; // (int) convertDpToPixel((float) 22.0, view.getContext());

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
        Canvas canvas = new Canvas(bmp);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(61, 61, 61));
        paint.setStyle(Paint.Style.STROKE);

        if (eventHolder.type == COMPLEX_LAYOUT) {
            paint.setStrokeWidth(convertDpToPixel(10.0f, view.getContext()));
        } else {
            paint.setStrokeWidth(3.0f);
        }

        // Set path effect
        // Reference: http://developer.android.com/reference/android/graphics/PathEffect.html
        if (eventHolder.type == TimelineDeviceAdapter.WAIT_CONTROL_LAYOUT) {
            paint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        } else {
            paint.setPathEffect(null);
        }

        // Draw the line representing the timeline segment
        canvas.drawLine((float) (w / 2.0), (float) 0, (float) (w / 2.0), (float) h, paint);

        /*
        // TODO: Draw prompts to person
        if (eventHolder.type == TimelineDeviceAdapter.CONTROL_PLACEHOLDER_LAYOUT) {
            paint.setColor(Color.CYAN);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle((float) (w / 2.0), (float) (h / 2.0), 20, paint);
        }
        */

        // Update the view with the bitmap
        imageView.setImageBitmap(bmp);

    }

    /**
     * Bind the provided events to the view.
     * This is the only method not required by base adapter.
     */
    public View bindData(View view, int position) {

        // Make sure it's worth drawing the view
        if (this.events.get(position) == null) {
            return view;
        }

        // Pull out the events object represented by the view
        EventHolder eventHolder = this.events.get(position);

        // Update the event label
        // Extract the view object to update, cast it to the correct type, and update the value.
        View viewElement = view.findViewById(R.id.label);
        TextView tv = (TextView)viewElement;

        if (eventHolder.type == SYSTEM_CONTROL_LAYOUT || eventHolder.type == CONTROL_PLACEHOLDER_LAYOUT || eventHolder.type == COMPLEX_LAYOUT) {
            // TODO: Consider making a behavior for system controls, too.
            tv.setText(eventHolder.title);
        } else {
            tv.setText(eventHolder.getEvent().getBehavior().getTag());
        }

        // Update the remainder of the view based on the type of events it represents.
        if (eventHolder.type == SYSTEM_CONTROL_LAYOUT) {

            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setImageResource(R.drawable.tile);

        } else if (eventHolder.type == LIGHT_CONTROL_LAYOUT) {

            // Set states of I/O visualization
            // Get layout containing light state visualizations
            LinearLayout previewLayout = (LinearLayout) view.findViewById(R.id.preview_layout);

            int[] previews = new int[12];
            previews[0]  = R.id.preview_1;
            previews[1]  = R.id.preview_2;
            previews[2]  = R.id.preview_3;
            previews[3]  = R.id.preview_4;
            previews[4]  = R.id.preview_5;
            previews[5]  = R.id.preview_6;
            previews[6]  = R.id.preview_7;
            previews[7]  = R.id.preview_8;
            previews[8]  = R.id.preview_9;
            previews[9]  = R.id.preview_10;
            previews[10] = R.id.preview_11;
            previews[11] = R.id.preview_12;

            if (previewLayout != null) {

                for (int i = 0; i < previews.length; i++) {

                    // Update image preview
                    ImageView preview = (ImageView) view.findViewById(previews[i]);

                    int w2 = (previewLayout.getWidth() > 0 ? previewLayout.getWidth() : 20);
                    int h2 = (previewLayout.getHeight() > 0 ? previewLayout.getHeight() : 20);

                    if (preview != null) {

                        Bitmap.Config conf2 = Bitmap.Config.ARGB_8888; // see other conf types
                        Bitmap bmp2 = Bitmap.createBitmap(w2, h2, conf2); // this creates a MUTABLE bitmap
                        Canvas canvas2 = new Canvas(bmp2);

                        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);

                        // Get behavior state
                        //String lightStateString = item.getEvent().getBehavior().getState().getState();
                        // TODO: ^ The above line doesn't get the most recent or correct state from the behavior... is the reference out of date or not updated somewhere? That's bad, man.
                        String lightStateString = eventHolder.getEvent().getBehavior().getState().getState();
                        String[] lightStates = lightStateString.split(" ");

                        // Update the view
                        if (lightStates[i].equals("T")) {
                            paint2.setColor(Color.rgb(0, 0, 255));
                        } else {
                            paint2.setColor(Color.rgb(100, 100, 100));
                        }

                        canvas2.drawRect(0, 0, bmp2.getWidth(), bmp2.getHeight(), paint2);

                        preview.setImageBitmap(bmp2);

                    }
                }

                TextView label = (TextView) view.findViewById(R.id.label);

                previewLayout.setX(label.getX());
            }

        } else if (eventHolder.type == IO_CONTROL_LAYOUT) {

            // Set states of I/O visualization
            // Get layout containing light state visualizations
            LinearLayout preview_layout = (LinearLayout) view.findViewById(R.id.preview_layout);

            int[] previews = new int[12];
            previews[0] = R.id.preview_1;
            previews[1] = R.id.preview_2;
            previews[2] = R.id.preview_3;
            previews[3] = R.id.preview_4;
            previews[4] = R.id.preview_5;
            previews[5] = R.id.preview_6;
            previews[6] = R.id.preview_7;
            previews[7] = R.id.preview_8;
            previews[8] = R.id.preview_9;
            previews[9] = R.id.preview_10;
            previews[10] = R.id.preview_11;
            previews[11] = R.id.preview_12;

            if (preview_layout != null) {

                for (int i = 0; i < previews.length; i++) {

                    // Update image preview
                    ImageView preview = (ImageView) view.findViewById(previews[i]);

                    int w2 = (preview_layout.getWidth() > 0 ? preview_layout.getWidth() : 20);
                    int h2 = (preview_layout.getHeight() > 0 ? preview_layout.getHeight() : 20);

                    if (preview != null) {

                        Bitmap.Config conf2 = Bitmap.Config.ARGB_8888; // see other conf types
                        Bitmap bmp2 = Bitmap.createBitmap(w2, h2, conf2); // this creates a MUTABLE bitmap
                        Canvas canvas2 = new Canvas(bmp2);
                        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);

                        // Get behavior state
                        String ioStateString = eventHolder.getEvent().getBehavior().getState().getState();
                        String[] ioStates = ioStateString.split(" ");

                        char ioState = ioStates[i].charAt(0);
                        char ioDirectionState = ioStates[i].charAt(1);
                        char ioSignalTypeState = ioStates[i].charAt(2);
                        char ioSignalValueState = ioStates[i].charAt(3);

                        // Update the view
                        if (ioState == 'T') {
                            paint2.setColor(Color.rgb(61, 255, 61));
                        } else {
                            paint2.setColor(Color.rgb(255, 61, 61));
                        }

                        canvas2.drawRect(0, 0, bmp2.getWidth(), bmp2.getHeight(), paint2);

                        preview.setImageBitmap(bmp2);

                    }
                }

                TextView label = (TextView) view.findViewById(R.id.label);

                preview_layout.setX(label.getX());
            }

        } else if (eventHolder.type == MESSAGE_CONTROL_LAYOUT) {

            TextView textView = (TextView) view.findViewById (R.id.text);
            if (textView != null) {

                // Get behavior state
                String message = eventHolder.getEvent().getBehavior().getState().getState();

                // Update the view
                textView.setText("\"" + message + "\"");
            }
        } else if (eventHolder.type == WAIT_CONTROL_LAYOUT) {

            TextView textView = (TextView) view.findViewById (R.id.text);
            if (textView != null) {

                // Get behavior state
                int time = Integer.parseInt(eventHolder.getEvent().getBehavior().getState().getState());

                // Update the view
                textView.setText(time + " ms");
            }
        } else if (eventHolder.type == SAY_CONTROL_LAYOUT) {

            TextView textView = (TextView) view.findViewById (R.id.text);
            if (textView != null) {
                textView.setText("\"" + eventHolder.getEvent().getBehavior().getState().getState() + "\"");
            }
        } else if (eventHolder.type == COMPLEX_LAYOUT) {

            TextView textView = (TextView) view.findViewById (R.id.text);
            if (textView != null) {
                textView.setText(eventHolder.summary);
            }
        }

        // return the final view object
        return view;
    }
}