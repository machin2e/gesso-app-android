package camp.computer.clay.designer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobeta.android.sequencer.R;

import java.util.ArrayList;
import java.util.UUID;

import camp.computer.clay.system.Clay;
import camp.computer.clay.system.ContentEntry;

public class EventHolderAdapter extends BaseAdapter {

    // Reference to the event holders
    private ArrayList<EventHolder> eventHolders;

    /**
     * Default constructor. Creates the new Adaptor object to provide a ListView with eventHolders.
     * @param context
     * @param eventHolders
     */
    public EventHolderAdapter(Context context, ArrayList<EventHolder> eventHolders) {
        this.eventHolders = eventHolders;
    }

    public void remove (EventHolder eventHolder) {
        this.eventHolders.remove(eventHolder);
    }

    public void insert (EventHolder eventHolder, int position) {
        this.eventHolders.add(position, eventHolder);
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
        View view = null;

        // Select the layout for the view based on the type of object being displayed in the view
        String type = getItemType (position);

        final EventHolder eventHolder = getItem (position);
        Log.v("Layout", "type: " + type);

//        int layoutResource = getLayoutByType (type); // Default resource
//        Log.v("Layout", "resource: " + layoutResource);

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

            view = getEventView (eventHolder);

//            if (eventHolder.getType().equals("tone")) {
//                view = getToneView (eventHolder);
//            } else if (eventHolder.getType().equals("say")) {
//                view = getSayView (eventHolder);
//            } else if (eventHolder.getType().equals("message")) {
//                view = getMessageView (eventHolder);
//            } else if (eventHolder.getType().equals("pause")) {
//                view = getPauseView(eventHolder);
//            } else if (eventHolder.getType().equals("light")) {
//                view = getLightView(eventHolder);
//            } else if (eventHolder.getType().equals("signal")) {
//                view = getSignalView(eventHolder);
//            } else if (eventHolder.getType().equals ("highlight")) {
//                view = getHighlightView (eventHolder, "", "");
//            } else if (eventHolder.getType().equals ("timeline")) {
//                view = getTimelineSegmentView (eventHolder, "", "");
//            }

//            else if (eventHolder.getType().equals ("create")) {
//                view = getTextView(eventHolder, "", "create");
//            } else if (eventHolder.getType().equals ("list")) {
//                view = getTextView (eventHolder, "", "list");
//            }

            if (view != null) {
                view.setTag(eventHolder);
            }
        } else {
            view = convertView;
        }

        return view;
    }

    private View getEventView (EventHolder eventHolder) {

        // Container
        RelativeLayout eventView = new RelativeLayout (ApplicationView.getContext());

        // Top
        LinearLayout upperLayout = getTopView(eventHolder);
        if (eventHolder.isStateVisible()) {
            upperLayout.setVisibility(View.VISIBLE);
        } else {
            upperLayout.setVisibility(View.GONE);
        }
        eventView.addView(upperLayout);

        // Middle (content)
        LinearLayout middleLayout = getMiddleView(eventHolder);
        eventView.addView(middleLayout);

        // Bottom
        LinearLayout bottomLayout = getBottomView(eventHolder);
        if (eventHolder.isStateVisible()) {
            bottomLayout.setVisibility(View.VISIBLE);
        } else {
            bottomLayout.setVisibility(View.GONE);
        }
        eventView.addView(bottomLayout);

        return eventView;
    }

    /**
     * Tone action view.
     * @param eventHolder View holder with the Tone event.
     * @return View for the Tone event.
     */
    private View getToneView (EventHolder eventHolder) {

        // Extract state
        String stateText = eventHolder.getEvent().getState().get(0).getState();
        String[] tokens = stateText.split(" ");
        String frequencyText = tokens[1] + " Hz";
        String durationText = tokens[3] + " ms";

        // Action
        RelativeLayout eventInnerView = new RelativeLayout (ApplicationView.getContext());

        // Tag
        final TextView actionLabel = getEventTagView(eventHolder.getEvent().getAction().getTag());
        //eventView.addView(actionLabel);
        eventInnerView.addView(actionLabel);

        // Timeline segment
        final ImageView imageView = getEventTimelineView (150, 3.0f, false, actionLabel);
        eventInnerView.addView(imageView);

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
        LinearLayout actionLayout = getActionLayout();
        eventInnerView.addView(actionLayout);

        // Action: Tone frequency
        final TextView frequencyLabel = getTextFieldView(frequencyText);
        actionLayout.addView(frequencyLabel);

        // Action: Duration frequency
        final TextView durationLabel = getTextFieldView(durationText);
        actionLayout.addView(durationLabel);

        return eventInnerView;
    }

    private LinearLayout getMiddleView (EventHolder eventHolder) {

        // Middle (content)
        LinearLayout middleLayout = new LinearLayout(ApplicationView.getContext());
        middleLayout.setId(R.id.event_middle_layout);
        middleLayout.setOrientation(LinearLayout.VERTICAL);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT); // (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        params.addRule(RelativeLayout.BELOW, R.id.event_top_layout);
//        params.addRule(RelativeLayout.CENTER_VERTICAL);
        middleLayout.setLayoutParams(params);



        View view = null;

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
        } else if (eventHolder.getType().equals ("highlight")) {
            view = getHighlightView (eventHolder, "", "");
        } else if (eventHolder.getType().equals ("timeline")) {
            view = getTimelineSegmentView (eventHolder, "", "");
        }

        middleLayout.addView(view);

        return middleLayout;

    }

    private LinearLayout getBottomView (EventHolder eventHolder) {

        // Top
        LinearLayout bottomViewLayout = new LinearLayout(ApplicationView.getContext());
        bottomViewLayout.setId(R.id.event_bottom_layout);
        bottomViewLayout.setOrientation(LinearLayout.VERTICAL);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT); // (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
         params.addRule(RelativeLayout.BELOW, R.id.event_middle_layout);
//        params.addRule(RelativeLayout.CENTER_VERTICAL);
        bottomViewLayout.setLayoutParams(params);

        RelativeLayout upperInnerView = new RelativeLayout (ApplicationView.getContext());

        bottomViewLayout.addView(upperInnerView);


        // Tag
        final TextView actionLabel = getEventTagView("");
        upperInnerView.addView(actionLabel);

        // Timeline segment
        final ImageView imageView = getEventTimelineView(70, 3.0f, false, actionLabel);
        upperInnerView.addView(imageView);

        // Linear layout (to the right side of timeline)
        LinearLayout actionLayout = getActionLayout();
        upperInnerView.addView(actionLayout);

        // Action: Tone frequency
        final TextView frequencyLabel = getTextFieldView("");
        actionLayout.addView(frequencyLabel);





        RelativeLayout bottomViewSeparatorLayout = new RelativeLayout (ApplicationView.getContext());

        bottomViewLayout.addView(bottomViewSeparatorLayout);

        // Tag
        final TextView actionLabel2 = getEventTagView("");
        bottomViewSeparatorLayout.addView(actionLabel2);

        // Timeline segment
        final ImageView imageView2 = getEventTimelineView(250, 3.0f, false, actionLabel2);
        imageView2.setId(R.id.timeline_bottom_segment);
        bottomViewSeparatorLayout.addView(imageView2);

        // Linear layout (to the right side of timeline)
        LinearLayout actionLayout2 = getActionLayout();
        bottomViewSeparatorLayout.addView(actionLayout2);

        // Action: Trigger
        final TextView frequencyLabel2 = getTextButtonView("");
        actionLayout2.addView(frequencyLabel2);


        return bottomViewLayout;
    }

    private LinearLayout getActionLayout() {
        LinearLayout linearLayout = new LinearLayout(ApplicationView.getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT); // (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        params.addRule(RelativeLayout.RIGHT_OF, R.id.timeline_segment);
        params.addRule(RelativeLayout.CENTER_VERTICAL);
        linearLayout.setLayoutParams(params);

//        actionView.addView (linearLayout);

        return linearLayout;
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

        // Tag
        final TextView actionLabel = getEventTagView (eventHolder.getEvent().getAction().getTag());
        actionView.addView(actionLabel);

        // Timeline segment
        final ImageView imageView = getEventTimelineView (110, 3.0f, true, actionLabel);
        actionView.addView(imageView);

        // Linear layout (to the right side of timeline)
        LinearLayout linearLayout = getActionLayout();
        actionView.addView(linearLayout);

        // Action: Pause duration
        final TextView frequencyLabel = getTextFieldView(durationText);
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

        // Tag
        final TextView actionLabel = getEventTagView(eventHolder.getEvent().getAction().getTag());
        eventView.addView(actionLabel);

        // Timeline segment
        final ImageView imageView = getEventTimelineView(120, 3.0f, false, actionLabel);
        eventView.addView(imageView);

        // (Top-level vertical) Linear layout (to the right side of timeline)
        LinearLayout linearLayout = getActionLayout();
        eventView.addView(linearLayout);

        // (Second-level horizontal) Linear layout (to the right side of timeline)
        LinearLayout horizontalLinearLayout = new LinearLayout(ApplicationView.getContext());
        horizontalLinearLayout.setId(R.id.preview_layout);
        horizontalLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.addView (horizontalLinearLayout);

        int[] previewResourceIds = new int[] { R.id.preview_1, R.id.preview_2, R.id.preview_3, R.id.preview_4, R.id.preview_5, R.id.preview_6, R.id.preview_7, R.id.preview_8, R.id.preview_9, R.id.preview_10, R.id.preview_11, R.id.preview_12 };
        for (int i = 0; i < 12; i++) {

            // (Third-level vertical) Linear layout (to the right side of timeline)
            LinearLayout lightLinearLayout = new LinearLayout(ApplicationView.getContext());
            lightLinearLayout.setOrientation(LinearLayout.VERTICAL);
            horizontalLinearLayout.addView(lightLinearLayout);
            lightLinearLayout.getLayoutParams().width = 70;

            // Action: Preview 1 label
            final TextView previewLabel = new TextView(ApplicationView.getContext());
            previewLabel.setText(String.valueOf(i + 1));
            previewLabel.setTextSize(10.0f);
            previewLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            previewLabel.setPadding(0, 5, 0, 5);
            lightLinearLayout.addView(previewLabel);

            // Action: Preview 1
            final ImageView previewImageView = new ImageView(ApplicationView.getContext());
            previewImageView.setId(previewResourceIds[i]);
            lightLinearLayout.addView(previewImageView);

            /* State: Generate bitmap */

            int w = 25;
            int h = 25;

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

//    private View getSignalView (EventHolder eventHolder) {
//        // <SECTION: CONTENT PROVIDER SELECTION BUTTONS>
//        final ArrayList<Button> channelSelectionButtonList = new ArrayList<Button>();
//        final Button[] selectedButton = {null};
//        // final LinearLayout channelSelectionButtonsLayout = new LinearLayout (getContext());
//        channelSelectionButtonsLayout.setOrientation(LinearLayout.HORIZONTAL);
//        for (int i = 0; i < 12; i++) {
//
//            // Create
//            final Button channelNumberButton = new Button (getContext());
//
//            // Text
//            final String channelNumberString = Integer.toString (i + 1);
//            channelNumberButton.setText(channelNumberString);
//            channelNumberButton.setTextSize(12);
//
//            // Style
//            channelNumberButton.setPadding(0, 0, 0, 0);
//            channelNumberButton.setBackgroundColor(Color.TRANSPARENT);
//            channelNumberButton.setTextColor(Color.LTGRAY);
//
//            // Style (LayoutParams)
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(10, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
//            params.setMargins(0, 0, 0, 0);
//            channelNumberButton.setLayoutParams(params);
//
//            // Add to view
//            channelSelectionButtonsLayout.addView(channelNumberButton);
//
//            // Add to button list
//            channelSelectionButtonList.add(channelNumberButton);
//        }
//
//        // Setup: Set up interactivity.
//        for (int i = 0; i < channelSelectionButtonList.size(); i++) {
//
//            final Button channelSelectionButton = channelSelectionButtonList.get (i);
//
//            channelSelectionButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    // Reset.
//                    for (int i = 0; i < channelSelectionButtonList.size(); i++) {
//                        channelSelectionButtonList.get(i).setTextColor(Color.LTGRAY);
//                        channelSelectionButtonList.get(i).setTypeface(null, Typeface.NORMAL);
//                    }
//
//                    // Select.
//                    if (selectedButton[0] != channelSelectionButton) {
//                        selectedButton[0] = channelSelectionButton; // Button. Select the button.
//                    } else {
//                        selectedButton[0] = null; // Deselect the button.
//                    }
//
//                    // Color.
//                    if (selectedButton[0] != null) {
//                        int textColor = ApplicationView.getApplicationView().getResources().getColor(R.color.timeline_segment_color);
//                        selectedButton[0].setTextColor(textColor); // Color. Update the color.
//                        selectedButton[0].setTypeface(null, Typeface.BOLD);
//                    }
//
//                    // Data.
//                    contentEntry.choice().get("content").put("provider", selectedButton[0].getText().toString());
//                }
//            });
//        }
//
//
//        designerViewLayout.addView (channelSelectionButtonsLayout);
//        // </SECTION: CONTENT PROVIDER SELECTION BUTTONS>
//
//
//    }

    /**
     * Signal action view.
     * @param eventHolder View holder with the Signal event.
     * @return View for the Signal event.
     */
    private View getSignalView (final EventHolder eventHolder) {

        RelativeLayout eventView = new RelativeLayout (ApplicationView.getContext());
        RelativeLayout.LayoutParams params = null;

        // Extract state
        String stateText = eventHolder.getEvent().getState().get(0).getState();
        String[] tokens = stateText.split(" ");
//        String durationText = stateText;

        // Tag
        final TextView actionLabel = getEventTagView(eventHolder.getEvent().getAction().getTag());
        eventView.addView(actionLabel);

        // Timeline segment
        final ImageView imageView = getEventTimelineView(150, 3.0f, false, actionLabel);
        eventView.addView(imageView);

        // (Top-level vertical) Linear layout (to the right side of timeline)
        LinearLayout linearLayout = getActionLayout();
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
            lightLinearLayout.setHorizontalGravity(Gravity.CENTER_HORIZONTAL);

//            linearLayout.getLayoutParams().width = 100;

            horizontalLinearLayout.addView(lightLinearLayout);

//            LinearLayout.LayoutParams lightLinearLayoutParams = new LinearLayout.LayoutParams(100, LinearLayout.LayoutParams.WRAP_CONTENT);
//            linearLayout.setLayoutParams(lightLinearLayoutParams);

            lightLinearLayout.getLayoutParams().width = 70;







            // Create
            final Button channelNumberButton = new Button (ApplicationView.getContext());

            // Text
            final String channelNumberString = Integer.toString (i + 1);
            channelNumberButton.setText(channelNumberString);
            channelNumberButton.setTextSize(10);

            // Style
            channelNumberButton.setPadding(0, 0, 0, 0);
            channelNumberButton.setBackgroundColor(Color.TRANSPARENT);
            channelNumberButton.setTextColor(Color.LTGRAY);

            // Style (LayoutParams)
            LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(60, 60, 1.0f);
            //LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(60, ViewGroup.LayoutParams.MATCH_PARENT, 1.0f);
            params2.setMargins(0, 0, 0, 0);
            channelNumberButton.setLayoutParams(params2);

            // Add to view
            lightLinearLayout.addView(channelNumberButton);

            // Add to button list
//            channelSelectionButtonList.add(channelNumberButton);


            channelNumberButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    UUID deviceUuid = eventHolder.getEvent().getTimeline().getDevice().getUuid();
                    //ContentEntry contentEntry = getClay().getContent().get("devices").get(deviceUuid.toString()).get("channels").get(String.valueOf(channelIndex[0] + 1));
                    Clay clay = eventHolder.getEvent().getClay();
                    ContentEntry contentEntry = clay.getContent().get("devices").get(deviceUuid.toString()).get("channels");
                    // TODO: ContentEntry contentEntry = getClay().getContent().get("devices").get(deviceUuid.toString()).get("channels").get(String.valueOf(channelIndex[0] + 1)).get("content");
//                displayListItemSelector (contentEntry);
                    Log.v ("Content_View", "contentEntry: " + contentEntry);
                    if (contentEntry != null) {
                        Log.v ("Content_View", "contentEntry: " + contentEntry.getKey());
                        Log.v("Content_View", "contentEntry: " + contentEntry.getContent());
//                        displayUpdateSignalOptions (contentEntry, true, true);
                        //ApplicationView.getApplicationView().getTimelineView().getEventDesigner().displayEventTriggerOptions(eventHolder);
                        ApplicationView.getApplicationView().getTimelineView().getEventDesigner().displayUpdateSignalOptions(eventHolder);
                    }
                }
            });





//            // Action: Preview 1 label
//            final TextView previewLabel = new TextView(ApplicationView.getContext());
////            previewLabel.setId(R.id.preview_1_label); // TODO: Create new resource dynamically
//            previewLabel.setText(String.valueOf(i + 1));
//            previewLabel.setTextSize(10.0f);
//            previewLabel.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//            previewLabel.setPadding(0, 5, 0, 5);
//            lightLinearLayout.addView(previewLabel);

            // TODO: Add params

            // Action: Preview 1
            final ImageView previewImageView = new ImageView(ApplicationView.getContext());
            previewImageView.setId(previewResourceIds[i]);
            lightLinearLayout.addView(previewImageView);

            /* State: Generate bitmap */

            int w = 25;
            int h = 25;

            Bitmap.Config conf2 = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bmp2 = Bitmap.createBitmap(w, h, conf2); // this creates a MUTABLE bitmap
            Canvas canvas2 = new Canvas(bmp2);
            Paint paint2 = new Paint(Paint.ANTI_ALIAS_FLAG);

            // Get behavior state
            String ioStateString = eventHolder.getEvent().getState().get(0).getState();
            Log.v ("Signal", "ioStateString: " + ioStateString);
            String[] ioStates = ioStateString.split(" ");
            Log.v("Signal", "ioStates.length: " + ioStates.length);

            Log.v("Signal", "ioStates[" + i + "]: " + ioStates[i]);

            char ioState = 'T'; // ioStates[i].charAt(0);
//            char ioDirectionState = ioStates[i].charAt(1);
//            char ioSignalTypeState = ioStates[i].charAt(2);
//            char ioSignalValueState = ioStates[i].charAt(3);

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

        // Extract state
        String stateText = eventHolder.getEvent().getState().get(0).getState();
        int currentDestinationAddressStringIndex = stateText.indexOf(" ");
        int currentContentStringIndex = stateText.indexOf(" ", currentDestinationAddressStringIndex + 1);

        String messageTypeString = stateText.substring(0, currentDestinationAddressStringIndex);
        String messageDestinationString = stateText.substring(currentDestinationAddressStringIndex + 1, currentContentStringIndex);
        String messageContentString = stateText.substring(currentContentStringIndex + 1);
        messageContentString = messageContentString.substring(1, messageContentString.length() - 1);

        // Tag
        final TextView actionLabel = getEventTagView(eventHolder.getEvent().getAction().getTag());
        actionView.addView(actionLabel);

        // Timeline
        final ImageView imageView = getEventTimelineView (180, 3.0f, false, actionLabel);
        actionView.addView(imageView);

        // Linear layout (to the right side of timeline)
        LinearLayout linearLayout = getActionLayout();
        actionView.addView(linearLayout);

        // Action: Message content
        final TextView messageContentLabel = getTextFieldView(messageContentString);
        linearLayout.addView(messageContentLabel);

        // Action: Message type
        final TextView messageTypeLabel = getTextSubFieldView(messageTypeString);
        linearLayout.addView(messageTypeLabel);

        // Action: Message destinationMachine
        final TextView messageDestinationLabel = getTextSubFieldView(messageDestinationString);
        linearLayout.addView(messageDestinationLabel);

        return actionView;
    }

    private LinearLayout getTopView (final EventHolder eventHolder) {

        // Top
        LinearLayout topViewLayout = new LinearLayout(ApplicationView.getContext());
        topViewLayout.setId(R.id.event_top_layout);
        topViewLayout.setOrientation(LinearLayout.VERTICAL);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT); // (RelativeLayout.LayoutParams) linearLayout.getLayoutParams();
        // params.addRule(RelativeLayout.RIGHT_OF, R.id.icon);
//        params.addRule(RelativeLayout.CENTER_VERTICAL);
        topViewLayout.setLayoutParams(params);

        RelativeLayout topViewSeparatorLayout = new RelativeLayout (ApplicationView.getContext());

        topViewLayout.addView(topViewSeparatorLayout);

        // Tag
        final TextView actionLabel2 = getEventTagView("");
        topViewSeparatorLayout.addView(actionLabel2);

        // Timeline segment
        final ImageView imageView2 = getEventTimelineView(250, 3.0f, false, actionLabel2);
        imageView2.setId(R.id.timeline_top_segment);
        topViewSeparatorLayout.addView(imageView2);

        // Linear layout (to the right side of timeline)
        LinearLayout actionLayout2 = getActionLayout();
        topViewSeparatorLayout.addView(actionLayout2);

        // Action: Trigger
        final TextView frequencyLabel2 = getTextButtonView("");
        actionLayout2.addView(frequencyLabel2);



        RelativeLayout innerTopViewLayout = new RelativeLayout (ApplicationView.getContext());

        topViewLayout.addView(innerTopViewLayout);

        // Tag
        final TextView actionLabel = getEventTagView("");
        innerTopViewLayout.addView(actionLabel);

        // Timeline segment
        final ImageView imageView = getEventTimelineView(70, 3.0f, false, actionLabel);
        innerTopViewLayout.addView(imageView);

        // Linear layout (to the right side of timeline)
        LinearLayout actionLayout = getActionLayout();
        innerTopViewLayout.addView(actionLayout);

        // Action: Trigger
        String triggerMessage = "none";
        if (!eventHolder.getTriggerMessage().equals("")) {
            triggerMessage = eventHolder.getTriggerMessage();
        }
        final TextView frequencyLabel = getTextButtonView(triggerMessage);
        frequencyLabel.setAllCaps(true);
        frequencyLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("Touch", "Trigger Button");
                ApplicationView.getApplicationView().getTimelineView().getEventDesigner().displayEventTriggerOptions(eventHolder);
            }
        });
        actionLayout.addView(frequencyLabel);

        /*
        // Trigger
        final EditText triggerMessageText = new EditText(ApplicationView.getContext());
        triggerMessageText.setInputType(InputType.TYPE_CLASS_TEXT);
        triggerMessageText.setVisibility(View.VISIBLE);
        actionLayout.addView(triggerMessageText);
        */

        return topViewLayout;

    }

    /**
     * Say action view.
     * @param eventHolder View holder with the Say event.
     * @return View for the Say event.
     */
    private View getSayView (EventHolder eventHolder) {

        RelativeLayout actionView = new RelativeLayout (ApplicationView.getContext());

        // Extract state
        String stateText = eventHolder.getEvent().getState().get(0).getState();
        String sayText = stateText;

        // Tag
        final TextView actionLabel = getEventTagView(eventHolder.getEvent().getAction().getTag());
        actionView.addView(actionLabel);

        // Timeline segment
        final ImageView imageView = getEventTimelineView(150, 3.0f, false, actionLabel);
        actionView.addView(imageView);

        // Linear layout (to the right side of timeline)
        LinearLayout linearLayout = getActionLayout();
        actionView.addView(linearLayout);

        // Action: Speech phrase
        final TextView speechTextView = getTextFieldView(sayText);
        linearLayout.addView(speechTextView);

        return actionView;
    }

    private TextView getTextSubFieldView(String text) {
        final TextView messageTypeLabel = new TextView (ApplicationView.getContext());
        messageTypeLabel.setText(text);
        messageTypeLabel.setTextSize(11.0f);
        messageTypeLabel.setTextColor(Color.DKGRAY);
        messageTypeLabel.setPadding(0, 0, 0, 0);
        return messageTypeLabel;
    }

    private TextView getTextFieldView(String text) {
        final TextView messageContentLabel = new TextView (ApplicationView.getContext());
        messageContentLabel.setText(text);
        messageContentLabel.setTextSize(12.0f);
        messageContentLabel.setPadding(0, 5, 0, 5);
        return messageContentLabel;
    }

    private TextView getTextButtonView(String text) {
        final TextView textButtonView = new TextView (ApplicationView.getContext());
        textButtonView.setText(text);
        textButtonView.setTextSize(10.0f);
        textButtonView.setAllCaps(true);
        textButtonView.setPadding(0, 5, 0, 5);
        return textButtonView;
    }

    private ImageView getEventTimelineView(int height, float thickness, boolean isDashed, View leftView) {
        final ImageView imageView = new ImageView (ApplicationView.getContext());
        imageView.setId(R.id.timeline_segment);

//        if (eventHolder.getType().equals("complex")) {
//            paint.setStrokeWidth(convertDpToPixel(10.0f, view.getContext()));
//        } else {
//            paint.setStrokeWidth(3.0f);
//        }

        drawTimelineSegment(imageView, height, thickness, isDashed);

        // Timeline segment layout
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT); // (RelativeLayout.LayoutParams) imageView.getLayoutParams(); // new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.RIGHT_OF, leftView.getId());
        imageView.setLayoutParams(params);

        return imageView;
    }

    /**
     * Message action view.
     * @param eventHolder View holder with the Message event.
     * @return View for the Message event.
     */
    private View getTextView (EventHolder eventHolder, String tag, String text) {

        RelativeLayout actionView = new RelativeLayout (ApplicationView.getContext());
        RelativeLayout.LayoutParams params = null;

        // Tag
        final TextView actionLabel = getEventTagView (tag);
        actionView.addView(actionLabel);

        // Timeline
        final ImageView imageView = getEventTimelineView (150, 3.0f, false, actionLabel);
        actionView.addView(imageView);

        // Linear layout (to the right side of timeline)
        LinearLayout linearLayout = getActionLayout();
        actionView.addView(linearLayout);

        // Action: Text content
        final TextView textView = new TextView (ApplicationView.getContext());
        textView.setText(text);
        textView.setAllCaps(true);
        textView.setTextSize(10.0f);
        textView.setPadding(0, 5, 0, 5);
        linearLayout.addView(textView);

        return actionView;
    }

    /**
     * Message action view.
     * @param eventHolder View holder with the Message event.
     * @return View for the Message event.
     */
    private View getTimelineSegmentView (EventHolder eventHolder, String tag, String text) {

        RelativeLayout actionView = new RelativeLayout (ApplicationView.getContext());
        RelativeLayout.LayoutParams params = null;

        // Tag
        final TextView actionLabel = getEventTagView (tag);
        actionView.addView(actionLabel);

        // Timeline
        final ImageView imageView = getEventTimelineView (150, 3.0f, false, actionLabel);
        actionView.addView(imageView);

        // Linear layout (to the right side of timeline)
        LinearLayout linearLayout = getActionLayout();
        actionView.addView(linearLayout);

        // Action: Text content
        final TextView textView = new TextView (ApplicationView.getContext());
        textView.setText(text);
        textView.setAllCaps(true);
        textView.setTextSize(10.0f);
        textView.setPadding(0, 5, 0, 5);
        linearLayout.addView(textView);

        return actionView;
    }

    /**
     * Message action view.
     * @param eventHolder View holder with the Message event.
     * @return View for the Message event.
     */
    private View getHighlightView (EventHolder eventHolder, String tag, String text) {

        RelativeLayout eventView = new RelativeLayout (ApplicationView.getContext());
        RelativeLayout.LayoutParams params = null;

        // Tag
        final TextView actionLabel = getEventTagView ("");
        eventView.addView(actionLabel);

        eventView.setBackgroundColor(ApplicationView.getApplicationView().getResources().getColor(R.color.timeline_segment_color));

        // Timeline
        final ImageView imageView = getEventTimelineView (15, 3.0f, false, actionLabel);
        eventView.addView(imageView);

        // Linear layout (to the right side of timeline)
        LinearLayout linearLayout = getActionLayout();
        eventView.addView(linearLayout);

//        // Action: Text content
//        final TextView textView = new TextView (ApplicationView.getContext());
//        textView.setText(text);
//        textView.setAllCaps(true);
//        textView.setTextSize(10.0f);
//        textView.setPadding(0, 5, 0, 5);
//        linearLayout.addView(textView);

        return eventView;
    }

    private TextView getEventTagView(String tag) {

        // Action label
        TextView actionLabel = new TextView (ApplicationView.getContext());
        if (tag != null && tag.length() > 0) {
            actionLabel.setId(R.id.tag);
            actionLabel.setText(tag);
        } else {
            actionLabel.setId(R.id.event_label);
            actionLabel.setText("");
        }
        actionLabel.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        actionLabel.setAllCaps(true);
        actionLabel.setTextSize(10.0f);
        actionLabel.setWidth(150);

        // Action label layout
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT); // (RelativeLayout.LayoutParams) actionLabel.getLayoutParams();
        params1.addRule(RelativeLayout.CENTER_VERTICAL);
        actionLabel.setLayoutParams(params1);

        return actionLabel;
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
     * @param heightInPixels
     */
    private void drawTimelineSegment (View view, int heightInPixels, float strokeWidth, boolean isDashed) {

        ImageView imageView = (ImageView) view.findViewById(R.id.timeline_segment);

        int w = (int) convertDpToPixel((float) 22.0, view.getContext());
        int h = heightInPixels; // (int) convertDpToPixel((float) 22.0, view.getContext());

        Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(w, h, conf); // this creates a MUTABLE bitmap
        Canvas canvas = new Canvas(bmp);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // paint.setColor(Color.rgb(255, 255, 255));
        paint.setColor (ApplicationView.getApplicationView().getResources().getColor(R.color.timeline_segment_color));
        paint.setStyle(Paint.Style.STROKE);

        paint.setStrokeWidth(strokeWidth);

        // Set path effect
        // Reference: http://developer.android.com/reference/android/graphics/PathEffect.html
        if (isDashed) {
            paint.setPathEffect(new DashPathEffect(new float[]{10, 20}, 0));
        } else {
            paint.setPathEffect(null);
        }

        // Draw the line representing the timeline segment
        canvas.drawLine((float) (w / 2.0), (float) 0, (float) (w / 2.0), (float) h, paint);

        // Update the view with the bitmap
        imageView.setImageBitmap(bmp);

    }
}
