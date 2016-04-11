package camp.computer.clay.sequencer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobeta.android.sequencer.R;

import java.util.ArrayList;

public class EventHolderAdapter extends BaseAdapter {

    // store the context (as an inflated layout)
    LayoutInflater inflater;

    // store the resource (typically list_item.xml)
    private int resource;

    // Reference to the event holders
    private ArrayList<EventHolder> eventHolders;

    /**
     * Default constructor. Creates the new Adaptor object to provide a ListView with eventHolders.
     * @param context
     * @param eventHolders
     */
    public EventHolderAdapter(Context context, ArrayList<EventHolder> eventHolders) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.eventHolders = eventHolders;
    }

    @Override
    public int getCount() {
        return this.eventHolders.size();
    }

    @Override
    public EventHolder getItem(int position) {
        return this.eventHolders.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getItemType(int position){
        // Your if else code and return type ( TYPE_1 to TYPE_5 )
        EventHolder eventHolder = getItem(position);
        return eventHolder.getType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // reuse a given view, or inflate a new one from the xml
        View view;

        // Select the layout for the view based on the type of object being displayed in the view
        String type = getItemType (position);

        final EventHolder eventHolder = getItem (position);
        Log.v("Layout", "type: " + type);

        int layoutResource = getLayoutByType (type); // Default resource
        Log.v("Layout", "resource: " + layoutResource);

//        // Check if the view is reusable. The view is reusable if it is for the same type.
//        // TODO: Only recycle view if it's for the same type (e.g., when updating state)
//        if (convertView != null) {
//            EventHolder viewTag = (EventHolder) convertView.getTag();
//            if (viewTag.type != type) {
//                convertView = null;
//            }
//        }

        // <HACK>
        // This prevents view recycling.
        // TODO: Only do this if the event type (i.e., behavior type) has changed.
        convertView = null;
        // </HACK>

        // Get the eventHolders corresponding to the view
//        final EventHolder eventHolder = eventHolders.get (position);

        // Create new view if none are recyclable
        if (convertView == null) {

            if (eventHolder.getType().equals("tone")) {
                view = getToneView (eventHolder);
            } else if (eventHolder.getType().equals("say")) {
                view = getSayView (eventHolder);
            } else if (eventHolder.getType().equals("message")) {
                view = getMessageView (eventHolder);
            } else if (eventHolder.getType().equals("pause")) {
                view = getPauseView(eventHolder);
            } else if (eventHolder.getType().equals("light")) {
                view = getLightView(eventHolder);
            } else if (eventHolder.getType().equals("signal")) {
                view = getSignalView(eventHolder);
            } else if (eventHolder.getType().equals ("choose")) {
                view = getTextView (eventHolder, "", "choose");
            } else {
                view = this.inflater.inflate(layoutResource, parent, false);
            }
            view.setTag(eventHolder);
        } else {
            view = convertView;
        }

        // Update the list item's view according to the type
        if (!eventHolder.getType().equals("tone") && !eventHolder.getType().equals("say") && !eventHolder.getType().equals("message") && !eventHolder.getType().equals("pause") && !eventHolder.getType().equals("light") && !eventHolder.getType().equals("signal")
                && !eventHolder.getType().equals("choose")) {
            updateViewForType(view, eventHolder);
        }

        // bind the eventHolders to the view object
        return this.bindData(view, position);
    }

    /**
     * Tone action view.
     * @param eventHolder View holder with the Tone event.
     * @return View for the Tone event.
     */
    private View getToneView (EventHolder eventHolder) {

        RelativeLayout actionView = new RelativeLayout (ApplicationView.getContext());
        RelativeLayout.LayoutParams params = null;

        // Extract state
        String stateText = eventHolder.getEvent().getState().get(0).getState();
        String[] tokens = stateText.split(" ");
        String frequencyText = tokens[1] + " Hz";
        String durationText = tokens[3] + " ms";

        // Action label
        final TextView actionLabel = new TextView (ApplicationView.getContext());
        actionLabel.setId(R.id.label);
        actionLabel.setText(eventHolder.getEvent().getAction().getTag());
        actionLabel.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        actionLabel.setAllCaps(true);
        actionLabel.setTextSize(10.0f);
        actionLabel.setWidth(150);
        actionView.addView(actionLabel);

        // Action label layout
        params = (RelativeLayout.LayoutParams) actionLabel.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        actionLabel.setLayoutParams(params);

        // Timeline segment
        final ImageView imageView = new ImageView (ApplicationView.getContext());
        imageView.setId(R.id.icon);
        actionView.addView(imageView);

        drawTimelineSegment(imageView, eventHolder, 150);

        // Timeline segment layout
        params = (RelativeLayout.LayoutParams) imageView.getLayoutParams(); // new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.RIGHT_OF, actionLabel.getId());
        imageView.setLayoutParams(params);

        /*
        // Add ImageView for rendering timeline segment
        final ImageView waveformImageView = new ImageView (ApplicationView.getContext());
        waveformImageView.setId(R.id.waveform);
        waveformImageView.setBackgroundResource(R.drawable.drag);
        actionView.addView(waveformImageView);

        waveformImageView.getLayoutParams().height = 100;

        // Set the width and height of the visualization
        int w = (actionView.getWidth() > 0 ? actionView.getWidth() : 250);
        int h = (actionView.getHeight() > 0 ? actionView.getHeight() : 50);

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
        Canvas canvas = new Canvas(bmp);

        Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint2.setColor(Color.rgb(255, 61, 61));
        canvas.drawRect(0, 0, bmp.getWidth(), bmp.getHeight(), paint2);

        waveformImageView.setImageBitmap(bmp);

        RelativeLayout.LayoutParams lpView = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lpView.addRule(RelativeLayout.RIGHT_OF, imageView.getId());
        waveformImageView.setLayoutParams(lpView);

//        params = (RelativeLayout.LayoutParams) waveformImageView.getLayoutParams(); // new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        params.addRule(RelativeLayout.RIGHT_OF, imageView.getId());
//        waveformImageView.setLayoutParams(params);

        waveformImageView.requestLayout(); // call this if already laid out
        */

        // Linear layout (to the right side of timeline)
        LinearLayout linearLayout = new LinearLayout(ApplicationView.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        actionView.addView (linearLayout);

        params = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        params.addRule(RelativeLayout.RIGHT_OF, imageView.getId());
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        linearLayout.setLayoutParams(params);

        // Action: Tone frequency
        final TextView frequencyLabel = new TextView (ApplicationView.getContext());
        frequencyLabel.setText(frequencyText); // e.g., "F♭ for 3 ms");
        frequencyLabel.setTextSize(12.0f);
        frequencyLabel.setPadding(0, 5, 0, 5);
        linearLayout.addView(frequencyLabel);

        // Action: Duration frequency
        final TextView durationLabel = new TextView (ApplicationView.getContext());
        durationLabel.setText(durationText); // e.g., "F♭ for 3 ms"
        durationLabel.setTextSize(12.0f);
        durationLabel.setPadding(0, 5, 0, 5);
        linearLayout.addView(durationLabel);

        return actionView;
    }

    /**
     * Say action view.
     * @param eventHolder View holder with the Say event.
     * @return View for the Say event.
     */
    private View getSayView (EventHolder eventHolder) {

        RelativeLayout actionView = new RelativeLayout (ApplicationView.getContext());
        RelativeLayout.LayoutParams params = null;

        // Extract state
        String stateText = eventHolder.getEvent().getState().get(0).getState();
        // String[] tokens = stateText.split(" ");
        String sayText = stateText;

        // Action label
        final TextView actionLabel = new TextView (ApplicationView.getContext());
        actionLabel.setId(R.id.label);
        actionLabel.setText(eventHolder.getEvent().getAction().getTag());
        actionLabel.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        actionLabel.setAllCaps(true);
        actionLabel.setTextSize(10.0f);
        actionLabel.setWidth(150);
        actionView.addView(actionLabel);

        // Action label layout
        params = (RelativeLayout.LayoutParams) actionLabel.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        actionLabel.setLayoutParams(params);

        // Timeline segment
        final ImageView imageView = new ImageView (ApplicationView.getContext());
        imageView.setId(R.id.icon);
        actionView.addView(imageView);

        drawTimelineSegment(imageView, eventHolder, 150);

        // Timeline segment layout
        params = (RelativeLayout.LayoutParams) imageView.getLayoutParams(); // new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.RIGHT_OF, actionLabel.getId());
        imageView.setLayoutParams(params);

        // Linear layout (to the right side of timeline)
        LinearLayout linearLayout = new LinearLayout(ApplicationView.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        actionView.addView (linearLayout);

        params = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        params.addRule(RelativeLayout.RIGHT_OF, imageView.getId());
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        linearLayout.setLayoutParams(params);

        // Action: Speech phrase
        final TextView frequencyLabel = new TextView (ApplicationView.getContext());
        frequencyLabel.setText(sayText); // e.g., "oh, that's great";
        frequencyLabel.setTextSize(12.0f);
        frequencyLabel.setPadding(0, 5, 0, 5);
        linearLayout.addView(frequencyLabel);

        return actionView;
    }

    /**
     * Pause action view.
     * @param eventHolder View holder with the Pause event.
     * @return View for the Pause event.
     */
    private View getPauseView (EventHolder eventHolder) {

        RelativeLayout actionView = new RelativeLayout (ApplicationView.getContext());
        RelativeLayout.LayoutParams params = null;

        // Extract state
        String stateText = eventHolder.getEvent().getState().get(0).getState();
        // String[] tokens = stateText.split(" ");
        String durationText = stateText + " ms";

        // Action label
        final TextView actionLabel = new TextView (ApplicationView.getContext());
        actionLabel.setId(R.id.label);
        actionLabel.setText(eventHolder.getEvent().getAction().getTag());
        actionLabel.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        actionLabel.setAllCaps(true);
        actionLabel.setTextSize(10.0f);
        actionLabel.setWidth(150);
        actionView.addView(actionLabel);

        // Action label layout
        params = (RelativeLayout.LayoutParams) actionLabel.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        actionLabel.setLayoutParams(params);

        // Timeline segment
        final ImageView imageView = new ImageView (ApplicationView.getContext());
        imageView.setId(R.id.icon);
        actionView.addView(imageView);

        drawTimelineSegment(imageView, eventHolder, 150);

        // Timeline segment layout
        params = (RelativeLayout.LayoutParams) imageView.getLayoutParams(); // new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.RIGHT_OF, actionLabel.getId());
        imageView.setLayoutParams(params);

        // Linear layout (to the right side of timeline)
        LinearLayout linearLayout = new LinearLayout(ApplicationView.getContext());
        linearLayout.setId(R.id.content);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        actionView.addView (linearLayout);

        params = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        params.addRule(RelativeLayout.RIGHT_OF, imageView.getId());
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        linearLayout.setLayoutParams(params);

        // Action: Pause duration
        final TextView frequencyLabel = new TextView (ApplicationView.getContext());
        frequencyLabel.setText(durationText); // e.g., "oh, that's great";
        frequencyLabel.setTextSize(12.0f);
        frequencyLabel.setPadding(0, 5, 0, 5);
        linearLayout.addView(frequencyLabel);

        return actionView;
    }

    /**
     * Pause action view.
     * @param eventHolder View holder with the Pause event.
     * @return View for the Pause event.
     */
    private View getLightView (EventHolder eventHolder) {

        RelativeLayout eventView = new RelativeLayout (ApplicationView.getContext());
        RelativeLayout.LayoutParams params = null;

        // Extract state
        String stateText = eventHolder.getEvent().getState().get(0).getState();
        // String[] tokens = stateText.split(" ");
        String durationText = stateText + " ms";

        // Action label
        final TextView actionLabel = new TextView (ApplicationView.getContext());
        actionLabel.setId(R.id.label);
        actionLabel.setText(eventHolder.getEvent().getAction().getTag());
        actionLabel.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        actionLabel.setAllCaps(true);
        actionLabel.setTextSize(10.0f);
        actionLabel.setWidth(150);
        eventView.addView(actionLabel);

        // Action label layout
        params = (RelativeLayout.LayoutParams) actionLabel.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        actionLabel.setLayoutParams(params);

        // Timeline segment
        final ImageView imageView = new ImageView (ApplicationView.getContext());
        imageView.setId(R.id.icon);
        eventView.addView(imageView);

        drawTimelineSegment(imageView, eventHolder, 150);

        // Timeline segment layout
        params = (RelativeLayout.LayoutParams) imageView.getLayoutParams(); // new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.RIGHT_OF, actionLabel.getId());
        imageView.setLayoutParams(params);

        // (Top-level vertical) Linear layout (to the right side of timeline)
        LinearLayout linearLayout = new LinearLayout(ApplicationView.getContext());
        linearLayout.setId(R.id.content);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

//        params = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
//        params.addRule(RelativeLayout.RIGHT_OF, imageView.getId());
//        params.addRule(RelativeLayout.CENTER_VERTICAL);
//        linearLayout.setLayoutParams(params);

        // Set layout parameters
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeParams.addRule(RelativeLayout.RIGHT_OF, imageView.getId());
        relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        linearLayout.setLayoutParams(relativeParams);

        // Add to event view
        eventView.addView(linearLayout);

        // (Second-level horizontal) Linear layout (to the right side of timeline)
        LinearLayout horizontalLinearLayout = new LinearLayout(ApplicationView.getContext());
        horizontalLinearLayout.setId(R.id.preview_layout);
        horizontalLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        // relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        linearLayout.addView (horizontalLinearLayout);

        int[] previewResourceIds = new int[] { R.id.preview_1, R.id.preview_2, R.id.preview_3, R.id.preview_4, R.id.preview_5, R.id.preview_6, R.id.preview_7, R.id.preview_8, R.id.preview_9, R.id.preview_10, R.id.preview_11, R.id.preview_12 };
        for (int i = 0; i < 12; i++) {

            // (Third-level vertical) Linear layout (to the right side of timeline)
            LinearLayout lightLinearLayout = new LinearLayout(ApplicationView.getContext());
            lightLinearLayout.setOrientation(LinearLayout.VERTICAL);

//            linearLayout.getLayoutParams().width = 100;

            horizontalLinearLayout.addView(lightLinearLayout);

//            LinearLayout.LayoutParams lightLinearLayoutParams = new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.WRAP_CONTENT);
//            linearLayout.setLayoutParams(lightLinearLayoutParams);

            lightLinearLayout.getLayoutParams().width = 50;

            // Action: Preview 1 label
            final TextView previewLabel = new TextView(ApplicationView.getContext());
//            previewLabel.setId(R.id.preview_1_label); // TODO: Create new resource dynamically
            previewLabel.setText(String.valueOf(i + 1));
            previewLabel.setTextSize(10.0f);
            previewLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            previewLabel.setPadding(0, 5, 0, 5);
            lightLinearLayout.addView(previewLabel);

            // TODO: Add params

            // Action: Preview 1
            final ImageView previewImageView = new ImageView(ApplicationView.getContext());
            previewImageView.setId(previewResourceIds[i]);
            lightLinearLayout.addView(previewImageView);

            /* State: Generate bitmap */

            int w = 20;
            int h = 20;

            Bitmap.Config conf2 = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bmp2 = Bitmap.createBitmap(w, h, conf2); // this creates a MUTABLE bitmap
            Canvas canvas2 = new Canvas(bmp2);

            Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);

            // Get behavior state
            //String lightStateString = item.getEvent().getAction().getState().getState();
            // TODO: ^ The above line doesn't get the most recent or correct state from the behavior... is the reference out of date or not updated somewhere? That's bad, man.
            String lightStateString = eventHolder.getEvent().getState().get(0).getState();
            String[] lightStates = lightStateString.split(" ");

            // Update the view
            if (!lightStates[i].equals("000000")) {
                String colorString = "#" + lightStates[i];
                int color = Color.parseColor(colorString);
                paint2.setColor(color);
            } else {
                paint2.setColor(Color.rgb(100, 100, 100));
            }

            canvas2.drawRect(0, 0, bmp2.getWidth(), bmp2.getHeight(), paint2);

            previewImageView.setImageBitmap(bmp2);
        }

//        drawTimelineSegment(previewImageView, eventHolder, 150);

        /** Layout parameters */

        // (Top-level vertical) Linear layout (to the right side of timeline)
//        params = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
//        params.addRule(RelativeLayout.RIGHT_OF, imageView.getId());
//        params.addRule(RelativeLayout.CENTER_VERTICAL);
//        linearLayout.setLayoutParams(params);

        // (Second-level horizontal) Linear layout (to the right side of timeline)
//        LinearLayout.LayoutParams llParams = (LinearLayout.LayoutParams) horizontalLinearLayout.getLayoutParams();
////        llParams.addRule(RelativeLayout.RIGHT_OF, actionLabel.getId());
//        // params.addRule(RelativeLayout.CENTER_VERTICAL);
//        horizontalLinearLayout.setLayoutParams(llParams);

//        // (Third-level vertical) Linear layout (to the right side of timeline)
//        llParams = (LinearLayout.LayoutParams) lightLinearLayout.getLayoutParams();
////        llParams.addRule(RelativeLayout.RIGHT_OF, actionLabel.getId());
////        llParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
////        llParams.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER);
//        lightLinearLayout.setLayoutParams(llParams);

//        // Preview 1 layout
//        params = (RelativeLayout.LayoutParams) previewImageView.getLayoutParams(); // new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        params.addRule(RelativeLayout.BELOW, previewLabel.getId());
//        params.addRule(RelativeLayout.TEXT_ALIGNMENT_CENTER);
//        params.addRule(RelativeLayout.TEXT_ALIGNMENT_GRAVITY, RelativeLayout.CENTER_HORIZONTAL);
//        previewImageView.setLayoutParams(params);

        /* Set state */

        // Set the width and height of the visualization
//        int w = (preview_layout.getWidth() > 0 ? preview_layout.getWidth() : 20);
//        int h = (preview_layout.getHeight() > 0 ? preview_layout.getHeight() : 20);
//
//        if (preview != null) {



//        }

        return eventView;
    }

    /**
     * Signal action view.
     * @param eventHolder View holder with the Signal event.
     * @return View for the Signal event.
     */
    private View getSignalView (EventHolder eventHolder) {

        RelativeLayout eventView = new RelativeLayout (ApplicationView.getContext());
        RelativeLayout.LayoutParams params = null;

        // Extract state
        String stateText = eventHolder.getEvent().getState().get(0).getState();
        String[] tokens = stateText.split(" ");
//        String durationText = stateText;

        // Action label
        final TextView actionLabel = new TextView (ApplicationView.getContext());
        actionLabel.setId(R.id.label);
        actionLabel.setText(eventHolder.getEvent().getAction().getTag());
        actionLabel.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        actionLabel.setAllCaps(true);
        actionLabel.setTextSize(10.0f);
        actionLabel.setWidth(150);
        eventView.addView(actionLabel);

        // Action label layout
        params = (RelativeLayout.LayoutParams) actionLabel.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        actionLabel.setLayoutParams(params);

        // Timeline segment
        final ImageView imageView = new ImageView (ApplicationView.getContext());
        imageView.setId(R.id.icon);
        eventView.addView(imageView);

        drawTimelineSegment(imageView, eventHolder, 150);

        // Timeline segment layout
        params = (RelativeLayout.LayoutParams) imageView.getLayoutParams(); // new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.RIGHT_OF, actionLabel.getId());
        imageView.setLayoutParams(params);

        // (Top-level vertical) Linear layout (to the right side of timeline)
        LinearLayout linearLayout = new LinearLayout(ApplicationView.getContext());
        linearLayout.setId(R.id.content);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

//        params = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
//        params.addRule(RelativeLayout.RIGHT_OF, imageView.getId());
//        params.addRule(RelativeLayout.CENTER_VERTICAL);
//        linearLayout.setLayoutParams(params);

        // Set layout parameters
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        relativeParams.addRule(RelativeLayout.RIGHT_OF, imageView.getId());
        relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        linearLayout.setLayoutParams(relativeParams);

        // Add to event view
        eventView.addView(linearLayout);

        // (Second-level horizontal) Linear layout (to the right side of timeline)
        LinearLayout horizontalLinearLayout = new LinearLayout(ApplicationView.getContext());
        horizontalLinearLayout.setId(R.id.preview_layout);
        horizontalLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        // relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);

        linearLayout.addView (horizontalLinearLayout);

        int[] previewResourceIds = new int[] { R.id.preview_1, R.id.preview_2, R.id.preview_3, R.id.preview_4, R.id.preview_5, R.id.preview_6, R.id.preview_7, R.id.preview_8, R.id.preview_9, R.id.preview_10, R.id.preview_11, R.id.preview_12 };
        for (int i = 0; i < 12; i++) {

            // (Third-level vertical) Linear layout (to the right side of timeline)
            LinearLayout lightLinearLayout = new LinearLayout(ApplicationView.getContext());
            lightLinearLayout.setOrientation(LinearLayout.VERTICAL);

//            linearLayout.getLayoutParams().width = 100;

            horizontalLinearLayout.addView(lightLinearLayout);

//            LinearLayout.LayoutParams lightLinearLayoutParams = new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.WRAP_CONTENT);
//            linearLayout.setLayoutParams(lightLinearLayoutParams);

            lightLinearLayout.getLayoutParams().width = 50;

            // Action: Preview 1 label
            final TextView previewLabel = new TextView(ApplicationView.getContext());
//            previewLabel.setId(R.id.preview_1_label); // TODO: Create new resource dynamically
            previewLabel.setText(String.valueOf(i + 1));
            previewLabel.setTextSize(10.0f);
            previewLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            previewLabel.setPadding(0, 5, 0, 5);
            lightLinearLayout.addView(previewLabel);

            // TODO: Add params

            // Action: Preview 1
            final ImageView previewImageView = new ImageView(ApplicationView.getContext());
            previewImageView.setId(previewResourceIds[i]);
            lightLinearLayout.addView(previewImageView);

            /* State: Generate bitmap */

            int w = 20;
            int h = 20;

            Bitmap.Config conf2 = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bmp2 = Bitmap.createBitmap(w, h, conf2); // this creates a MUTABLE bitmap
            Canvas canvas2 = new Canvas(bmp2);
            Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);

            // Get behavior state
            String ioStateString = eventHolder.getEvent().getState().get(0).getState();
            String[] ioStates = ioStateString.split(" ");

            char ioState = ioStates[i].charAt(0);
            char ioDirectionState = ioStates[i].charAt(1);
            char ioSignalTypeState = ioStates[i].charAt(2);
            char ioSignalValueState = ioStates[i].charAt(3);

            // Update the view according to the state
            if (ioState == 'T') {
                paint2.setColor(Color.rgb(61, 255, 61));
            } else {
                paint2.setColor(Color.rgb(255, 61, 61));
            }

            canvas2.drawCircle(bmp2.getWidth() / 2, bmp2.getWidth() / 2, convertDpToPixel(3.0f, ApplicationView.getContext()), paint2);

            previewImageView.setImageBitmap(bmp2);
        }

        return eventView;
    }

    /**
     * Message action view.
     * @param eventHolder View holder with the Message event.
     * @return View for the Message event.
     */
    private View getMessageView (EventHolder eventHolder) {

        RelativeLayout actionView = new RelativeLayout (ApplicationView.getContext());
        RelativeLayout.LayoutParams params = null;

        // Extract state
//        String stateText = eventHolder.getEvent().getState().get(0).getState();
//        String[] tokens = stateText.split(" ");
//        String messageTypeText = tokens[0];
//        String messageDestinationText = tokens[1];
//        String messageText = tokens[2];

        // Extract state
        String stateText = eventHolder.getEvent().getState().get(0).getState();
        int currentDestinationAddressStringIndex = stateText.indexOf(" ");
        int currentContentStringIndex = stateText.indexOf(" ", currentDestinationAddressStringIndex + 1);

        String messageTypeString = stateText.substring(0, currentDestinationAddressStringIndex);
        String messageDestinationString = stateText.substring(currentDestinationAddressStringIndex + 1, currentContentStringIndex);
        String messageContentString = stateText.substring (currentContentStringIndex + 1);
        messageContentString = messageContentString.substring(1, messageContentString.length() - 1);

        // Action label
        final TextView actionLabel = new TextView (ApplicationView.getContext());
        actionLabel.setId(R.id.label);
        actionLabel.setText(eventHolder.getEvent().getAction().getTag());
        actionLabel.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        actionLabel.setAllCaps(true);
        actionLabel.setTextSize(10.0f);
        actionLabel.setWidth(150);
        actionView.addView(actionLabel);

        // Action label layout
        params = (RelativeLayout.LayoutParams) actionLabel.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        actionLabel.setLayoutParams(params);

        // Timeline segment
        final ImageView imageView = new ImageView (ApplicationView.getContext());
        imageView.setId(R.id.icon);
        actionView.addView(imageView);

        drawTimelineSegment(imageView, eventHolder, 150);

        // Timeline segment layout
        params = (RelativeLayout.LayoutParams) imageView.getLayoutParams(); // new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.RIGHT_OF, actionLabel.getId());
        imageView.setLayoutParams(params);

        // Linear layout (to the right side of timeline)
        LinearLayout linearLayout = new LinearLayout(ApplicationView.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        actionView.addView (linearLayout);

        params = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        params.addRule(RelativeLayout.RIGHT_OF, imageView.getId());
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        linearLayout.setLayoutParams(params);

        // Action: Message content
        final TextView messageContentLabel = new TextView (ApplicationView.getContext());
        messageContentLabel.setText(messageContentString);
        messageContentLabel.setTextSize(12.0f);
        messageContentLabel.setPadding(0, 5, 0, 5);
        linearLayout.addView(messageContentLabel);

        // Action: Message type
        final TextView messageTypeLabel = new TextView (ApplicationView.getContext());
        messageTypeLabel.setText(messageTypeString);
        messageTypeLabel.setTextSize(11.0f);
        messageTypeLabel.setTextColor(Color.DKGRAY);
        messageTypeLabel.setPadding(0, 0, 0, 0);
        linearLayout.addView(messageTypeLabel);

        // Action: Message destination
        final TextView messageDestinationLabel = new TextView (ApplicationView.getContext());
        messageDestinationLabel.setText(messageDestinationString);
        messageDestinationLabel.setTextSize(11.0f);
        messageDestinationLabel.setTextColor(Color.DKGRAY);
        messageDestinationLabel.setPadding(0, 0, 0, 0);
        linearLayout.addView(messageDestinationLabel);

        return actionView;
    }

    /**
     * Message action view.
     * @param eventHolder View holder with the Message event.
     * @return View for the Message event.
     */
    private View getTextView (EventHolder eventHolder, String tag, String text) {

        RelativeLayout actionView = new RelativeLayout (ApplicationView.getContext());
        RelativeLayout.LayoutParams params = null;

        // Action label
        final TextView actionLabel = new TextView (ApplicationView.getContext());
        actionLabel.setId(R.id.label);
        actionLabel.setText(tag);
        actionLabel.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        actionLabel.setAllCaps(true);
        actionLabel.setTextSize(10.0f);
        actionLabel.setWidth(150);
        actionView.addView(actionLabel);

        // Action label layout
        params = (RelativeLayout.LayoutParams) actionLabel.getLayoutParams();
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        actionLabel.setLayoutParams(params);

        // Timeline segment
        final ImageView imageView = new ImageView (ApplicationView.getContext());
        imageView.setId(R.id.icon);
        actionView.addView(imageView);

        drawTimelineSegment(imageView, eventHolder, 150);

        // Timeline segment layout
        params = (RelativeLayout.LayoutParams) imageView.getLayoutParams(); // new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.RIGHT_OF, actionLabel.getId());
        imageView.setLayoutParams(params);

        // Linear layout (to the right side of timeline)
        LinearLayout linearLayout = new LinearLayout(ApplicationView.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        actionView.addView (linearLayout);

        params = (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        params.addRule(RelativeLayout.RIGHT_OF, imageView.getId());
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        linearLayout.setLayoutParams(params);

        // Action: Text content
        final TextView textView = new TextView (ApplicationView.getContext());
        textView.setText(text);
        textView.setAllCaps(true);
        textView.setTextSize(12.0f);
        textView.setPadding(0, 5, 0, 5);
        linearLayout.addView(textView);

        return actionView;
    }

    public void remove (EventHolder eventHolder) {
        this.eventHolders.remove(eventHolder);
    }

    public void insert (EventHolder eventHolder, int position) {
        this.eventHolders.add(position, eventHolder);
    }

    private int getLayoutByType(String type) {
        Log.v("Layout", "type: " + type);
        int resourceForType;
        if (type.equals("create")) {
            resourceForType = R.layout.list_item_type_system;
        } else if (type.equals("complex")) {
            resourceForType = R.layout.list_item_type_complex;
        } else {
            resourceForType = R.layout.list_item_type_system;
        }
        return resourceForType;
    }

    /**
     * Updates the view layout specifically for the type of eventHolders that is displayed in it.
     *
     * @param view
     * @param eventHolder
     */
    private void updateViewForType (View view, EventHolder eventHolder) {

        Log.v ("Width", "updateViewForType");

        // Update layout of a behavior control
        if (!eventHolder.getType().equals("create")
                && !eventHolder.getType().equals("choose")) {

            // Update layout based on state
            if (!eventHolder.isSelected()) {
                // Update left padding
                view.setPadding(0, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            } else {
                // Update left padding to indent the item
                view.setPadding(120, view.getPaddingTop(), view.getPaddingRight(), view.getPaddingBottom());
            }

        }

        // Draw timeline segment
        int segmentLength = 120;
        drawTimelineSegment (view, eventHolder, segmentLength);

        view.invalidate();
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
        paint.setColor(Color.rgb(255, 255, 255));
        paint.setStyle(Paint.Style.STROKE);

        if (eventHolder.getType().equals("complex")) {
            paint.setStrokeWidth(convertDpToPixel(10.0f, view.getContext()));
        } else {
            paint.setStrokeWidth(3.0f);
        }

        // Set path effect
        // Reference: http://developer.android.com/reference/android/graphics/PathEffect.html
        if (eventHolder.getType().equals("pause")) {
            paint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        } else {
            paint.setPathEffect(null);
        }

        // Draw the line representing the timeline segment
        canvas.drawLine((float) (w / 2.0), (float) 0, (float) (w / 2.0), (float) h, paint);

        // Update the view with the bitmap
        imageView.setImageBitmap(bmp);

    }

    /**
     * Bind the provided eventHolders to the view.
     * This is the only method not required by base adapter.
     */
    public View bindData(View view, int position) {

        Log.v ("Width", "bindData");

        // Make sure it's worth drawing the view
        if (this.eventHolders.get(position) == null) {
            return view;
        }

        // Pull out the eventHolders object represented by the view
        EventHolder eventHolder = this.eventHolders.get(position);

        // Update the event label
        // Extract the view object to update, cast it to the correct type, and update the value.
        View viewElement = view.findViewById(R.id.label);
        TextView tv = (TextView)viewElement;

        if (eventHolder.getType().equals("create") || eventHolder.getType().equals("complex")) {
            // TODO: Consider making a behavior for system controls, too.
            tv.setText(eventHolder.tag);
        }
//        else {
//            tv.setText(eventHolder.getEvent().getAction().getTag());
//        }

        // Update the remainder of the view based on the type of eventHolders it represents.
        if (eventHolder.getType().equals("create")) {

            ImageView icon = (ImageView) view.findViewById(R.id.icon);
            icon.setImageResource(R.drawable.tile);

        } else if (eventHolder.getType().equals("complex")) {

            TextView textView = (TextView) view.findViewById (R.id.text);
            if (textView != null) {
                textView.setText(eventHolder.summary);
            }
        }

        // return the final view object
        return view;
    }
}
