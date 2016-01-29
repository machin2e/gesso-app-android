package camp.computer.clay.sequencer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdapter extends BaseAdapter {
    // store the context (as an inflated layout)
    LayoutInflater inflater;
    // store the resource (typically list_item.xml)
    private int resource;
    // store (a reference to) the data
    private ArrayList<ListItem> data;

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
     * provide a ListView with data.
     * @param context
     * @param resource
     * @param data
     */
    public CustomAdapter(Context context, int resource, ArrayList<ListItem> data) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.resource = resource;
        this.data = data;
    }

    /**
     * Return the size of the data set.
     */
    public int getCount() {
        return this.data.size();
    }

    /**
     * Return an object in the data set.
     */
    public Object getItem(int position) {
        return this.data.get(position);
    }

    /**
     * Return the position provided.
     */
    public long getItemId(int position) {
        return position;
    }

    public int getItemType(int position){
        // Your if else code and return type ( TYPE_1 to TYPE_5 )
        ListItem listItem = (ListItem) getItem (position);
        return listItem.type;
    }

    /**
     * Return a generated view for a position.
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // reuse a given view, or inflate a new one from the xml
        View view;

        // Select the layout for the view based on the type of object being displayed in the view
        int type = getItemType (position);
        int resourceForType; // Default resource
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

        // <HACK>
        // This prevents view recycling.
        convertView = null;
        // </HACK>

        if (convertView == null) {
            //view = this.inflater.inflate(resource, parent, false);
            view = this.inflater.inflate(resourceForType, parent, false);
        } else {
            view = convertView;
        }

        // Get the data corresponding to the view
        ListItem listItem = data.get(position);

        // Update the list item's view according to the type
        updateViewForType(view, listItem);

        // bind the data to the view object
        return this.bindData(view, position);
    }

    /**
     * Updates the view layout specifically for the type of data that is displayed in it.
     *
     * @param view
     * @param listItem
     */
    private void updateViewForType (View view, ListItem listItem) {
        // Update layout of a behavior control
        if (listItem.type != SYSTEM_CONTROL_LAYOUT
                && listItem.type != CONTROL_PLACEHOLDER_LAYOUT) {

            // Update layout based on state
            if (listItem.selected == false) {
                // Update left padding
                view.setPadding(20, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            } else {
                // Update left padding to indent the item
                view.setPadding(120, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            }

        }

        // Set the background color
        if (listItem.hasFocus) {
            view.setBackgroundColor(Color.LTGRAY);
        } else {
            view.setBackgroundColor(Color.TRANSPARENT);
        }

        if (listItem.type == LIGHT_CONTROL_LAYOUT) {

            // Update image
//            drawTimelineSegment (view, listItem);

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

                    //int w = WIDTH_PX, h = HEIGHT_PX;
                    int w2 = (preview_layout.getWidth() > 0 ? preview_layout.getWidth() : 20);
                    int h2 = (preview_layout.getHeight() > 0 ? preview_layout.getHeight() : 20);
                    //            if (listItem.selected) {
                    //                w2 = 300;
                    //            }

                    if (preview != null) {

                        Bitmap.Config conf2 = Bitmap.Config.ARGB_8888; // see other conf types
                        Bitmap bmp2 = Bitmap.createBitmap(w2, h2, conf2); // this creates a MUTABLE bitmap
                        Canvas canvas2 = new Canvas(bmp2);

                        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint2.setColor(Color.rgb(255, 61, 61));
                        //canvas2.drawRect(0, 0, w2, h2, paint2);
                        canvas2.drawRect(0, 0, bmp2.getWidth(), bmp2.getHeight(), paint2);

                        preview.setImageBitmap(bmp2);

                    }
                }

                TextView label = (TextView) view.findViewById(R.id.label);
//            preview.setX (label.getX());
//            preview.setX(0);

                preview_layout.setX(label.getX());
            }

        } else if (listItem.type == IO_CONTROL_LAYOUT) {

            // Update image
//            drawTimelineSegment (view, listItem);


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

                    //int w = WIDTH_PX, h = HEIGHT_PX;
                    int w2 = (preview_layout.getWidth() > 0 ? preview_layout.getWidth() : 20);
                    int h2 = (preview_layout.getHeight() > 0 ? preview_layout.getHeight() : 20);
                    //            if (listItem.selected) {
                    //                w2 = 300;
                    //            }

                    if (preview != null) {

                        Bitmap.Config conf2 = Bitmap.Config.ARGB_8888; // see other conf types
                        Bitmap bmp2 = Bitmap.createBitmap(w2, h2, conf2); // this creates a MUTABLE bitmap
                        Canvas canvas2 = new Canvas(bmp2);

                        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
                        paint2.setColor(Color.rgb(255, 61, 61));
                        //canvas2.drawRect(0, 0, w2, h2, paint2);
                        canvas2.drawRect(0, 0, bmp2.getWidth(), bmp2.getHeight(), paint2);

                        preview.setImageBitmap(bmp2);

                    }
                }

                TextView label = (TextView) view.findViewById(R.id.label);

                preview_layout.setX(label.getX());
            }
        } else if (listItem.type == CustomAdapter.MESSAGE_CONTROL_LAYOUT) {

            // Update image
//            drawTimelineSegment (view, listItem);

        } else if (listItem.type == CustomAdapter.WAIT_CONTROL_LAYOUT) {

            // Update image
//            drawTimelineSegment (view, listItem);

        } else if (listItem.type == CustomAdapter.SAY_CONTROL_LAYOUT) {

            // Update image
//            drawTimelineSegment (view, listItem);

        } else if (listItem.type == CustomAdapter.COMPLEX_LAYOUT) {

            // Update image
//            drawTimelineSegment (view, listItem);

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
     * Draws the timeline segment for the specified view
     * @param icon
     */
    private void drawTimelineSegment(View view, ListItem listItem) {

        ImageView imageView = (ImageView) view.findViewById(R.id.icon);

        int w = (int) convertDpToPixel((float) 22.0, view.getContext());
        int h = (int) convertDpToPixel((float) 22.0, view.getContext());

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
        Canvas canvas = new Canvas(bmp);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.rgb(61, 61, 61));
        paint.setStrokeWidth(3.0f);

        canvas.drawLine((float) (w / 2.0), (float) 0, (float) (w / 2.0), (float) h, paint);

        imageView.setImageBitmap(bmp);

    }

    /**
     * Bind the provided data to the view.
     * This is the only method not required by base adapter.
     */
    public View bindData(View view, int position) {
        // make sure it's worth drawing the view
        if (this.data.get(position) == null) {
            return view;
        }

        // pull out the object
        ListItem item = this.data.get(position);

        // extract the view object
        View viewElement = view.findViewById(R.id.label);
//        View viewElement = view.findViewById(R.id.title);
        // cast to the correct type
        TextView tv = (TextView)viewElement;
        // set the value
        tv.setText(item.title);

//        viewElement = view.findViewById(R.id.message);
//        tv = (TextView)viewElement;
//        tv.setText(item.message);

        if (item.type == SYSTEM_CONTROL_LAYOUT) {
            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setImageResource(R.drawable.tile);
//            drawTimelineSegment (view, item);
        }

        // Update the icon in the item's layout
        if (item.type == LIGHT_CONTROL_LAYOUT) {

//            ImageView icon = (ImageView) view.findViewById(R.id.icon);
//            icon.setImageResource(R.drawable.tile);

            drawTimelineSegment (view, item);

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
                        if (item.lightStates.get(i) == true) {
                            paint2.setColor(item.lightColors.get(i));
                        } else {
                            paint2.setColor(Color.rgb(100, 100, 100));
                        }

                        canvas2.drawRect(0, 0, bmp2.getWidth(), bmp2.getHeight(), paint2);

                        preview.setImageBitmap(bmp2);

                    }
                }

                TextView label = (TextView) view.findViewById(R.id.label);

                preview_layout.setX(label.getX());
            }

        } else if (item.type == IO_CONTROL_LAYOUT) {

            drawTimelineSegment (view, item);

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
                        if (item.ioStates.get(i) == true) {
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

        } else if (item.type == MESSAGE_CONTROL_LAYOUT) {

            drawTimelineSegment (view, item);

            TextView textView = (TextView) view.findViewById (R.id.text);
            if (textView != null) {
                textView.setText("\"" + item.message + "\"");
            }
        } else if (item.type == WAIT_CONTROL_LAYOUT) {

            drawTimelineSegment (view, item);

            TextView textView = (TextView) view.findViewById (R.id.text);
            if (textView != null) {
                textView.setText(item.time + " ms");
            }
        } else if (item.type == SAY_CONTROL_LAYOUT) {

            drawTimelineSegment (view, item);

            TextView textView = (TextView) view.findViewById (R.id.text);
            if (textView != null) {
                textView.setText("\"" + item.phrase + "\"");
            }
        } else if (item.type == COMPLEX_LAYOUT) {

            drawTimelineSegment (view, item);

            TextView textView = (TextView) view.findViewById (R.id.text);
            if (textView != null) {
                textView.setText(item.summary);
            }
        }

        // return the final view object
        return view;
    }
}