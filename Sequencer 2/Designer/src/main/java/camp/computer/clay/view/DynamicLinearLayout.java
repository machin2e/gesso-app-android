package camp.computer.clay.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mobeta.android.sequencer.R;

import java.util.ArrayList;

import camp.computer.clay.designer.ApplicationView;
import camp.computer.clay.system.ContentEntry;

public class DynamicLinearLayout extends LinearLayout {

    // Choices.
    final ArrayList<Button> optionButtonList = new ArrayList<Button>();
    final Button[] selectedOptionButton = { null };

    // Layout.
    final LinearLayout buttonListView = new LinearLayout(getContext()); // TODO: final DynamicLinearLayout buttonListView = new DynamicLinearLayout(getContext());
//    buttonListView.setOrientation(DynamicLinearLayout.HORIZONTAL);
//    buttonListView.setVerticalGravity(Gravity.CENTER_HORIZONTAL);

    //ArrayList<String> optionList = contentEntry.choice().get(key).getContentRange();
    //ArrayList<String> optionList; // = contentEntry.choice().get("content").getKeys();
//    private ContentEntry contentEntry;

    public DynamicLinearLayout(ContentEntry contentEntry, Context context) {
        super(context);
        generateView(contentEntry);
    }

    public DynamicLinearLayout(Context context) {
        super(context);
    }

    public DynamicLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DynamicLinearLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DynamicLinearLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public interface OnChoiceListener {
        void apply (ContentEntry contentEntry);
    }

    private boolean optionsAreChildren = true; // if not, then they're values in the range, or a text field

    private void generateView(final ContentEntry contentEntry) {

        Log.v("Choice", "\tgenerateView()");

        this.setOrientation(DynamicLinearLayout.VERTICAL);


        // Reset
        optionButtonList.clear();
        selectedOptionButton[0] = null;


        this.removeAllViews();






        // Layout.
        final DynamicLinearLayout buttonListView = new DynamicLinearLayout(getContext());
        buttonListView.setOrientation(DynamicLinearLayout.HORIZONTAL);
        buttonListView.setVerticalGravity(Gravity.CENTER_HORIZONTAL);

        //ArrayList<String> optionList = contentEntry.choice().get(key).getContentRange();
        final ArrayList<String> optionList;
        if (optionsAreChildren) {
            optionList = contentEntry.getKeys();
        } else {
            optionList = contentEntry.getContentRange();
        }






        for (int i = 0; i < optionList.size(); i++) {

            final String key = optionList.get(i);

            Log.v ("Hello", "\t" + optionList.get(i));

            // Create
            final Button optionButton = new Button(getContext());

            // Text
            optionButton.setText(optionList.get(i));
            optionButton.setTextSize(12);

            // Style
            optionButton.setPadding(0, 0, 0, 0);
            optionButton.setBackgroundColor(Color.TRANSPARENT);
            optionButton.setTextColor(Color.LTGRAY);

            // Style (LayoutParams)
            DynamicLinearLayout.LayoutParams params = new DynamicLinearLayout.LayoutParams(150, 80);
            params.setMargins(0, 0, 0, 0);
            optionButton.setLayoutParams(params);

            // Add to view (column)
            buttonListView.addView(optionButton);

            // Add to button list
            optionButtonList.add(optionButton);

            final ContentEntry.OnContentChangeListener contentListener = new ContentEntry.OnContentChangeListener() {
                @Override
                public void notifyContentChanged() {

                    // contentEntry2.removeOnContentChangeListener(this);

//                    Log.v("Compare", "\t\t\tnotifyContentChanged: " + key);

                    Log.v("Content_Tree", "\t\t\tnotifyContentChanged");
                    Log.v("Content_Tree_Notify", "\t\t\tnotifyContentChanged");

                    // Reset.
                    for (int k = 0; k < optionButtonList.size(); k++) {
                        optionButtonList.get(k).setTextColor(Color.LTGRAY);
                        optionButtonList.get(k).setTypeface(null, Typeface.NORMAL);
                    }

                    // Select.
                    Log.v("Compare", "...");
                    for (Button optionButton : optionButtonList) {
                        //if (optionButton.getText().toString().equals(contentEntry.getContent())) {
//                        Log.v("Compare", "choice.key: " + contentEntry.choice().get(key).getContent());
                        //if (optionButton.getText().toString().equals(contentEntry.choice().get("content").get(key).getKey())) {
                        if (optionButton.getText().toString().equals(contentEntry.get(key).getKey())) {
                            selectedOptionButton[0] = optionButton; // Button. Select the button.

                            // <HACK>
//                            selectedObservable = optionButton.getText().toString();
                            // </HACK>
                            break;
                        }
                    }

                    // Color.
                    if (selectedOptionButton[0] != null) {

//                        baseLayout.findViewWithTag("content_editor_view").setVisibility(View.VISIBLE);

                        int textColor = ApplicationView.getApplicationView().getResources().getColor(R.color.timeline_segment_color);
                        selectedOptionButton[0].setTextColor(textColor); // Color. Update the color.
                        selectedOptionButton[0].setTypeface(null, Typeface.BOLD);
                    }

//                    // Data.
//                    if (selectedOptionButton[0] != null) {
//                        contentEntry.choice().get(key).set(selectedOptionButton[0].getText().toString(), false);
//                    }
                }
            };

            // Setup: Set up interactivity.
            final int finalI = i;
            optionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // List for changes to data state...
//                    contentEntry.addOnContentChangeListener(contentListener);
                    //contentEntry.choice().get("content").get(key).addOnContentChangeListener(contentListener);

                    selectedOptionButton[0] = optionButton;

//                    contentEntry.get(optionList.get(finalI));
                    contentListener.notifyContentChanged();

                    Log.v ("Choice", "button: " + optionButton.getText().toString());
                    ContentEntry ce = contentEntry.get(optionButton.getText().toString());
                    Log.v ("Choice", "key: " + ce.getKey());
                    notifyOnChoiceListeners(contentEntry, ce);


                    //onChoiceListener.apply(optionButton.getText().toString());
                    //contentListener.notifyContentChanged();

                    // Data.
                    // Note: Don't update the content here, since this represents a choice of which
                    // observable to edit, not an actual change to its content.
//                    contentEntry.choice().get("content").get(key).set(optionButton.getText().toString());

                }
            });

            contentListener.notifyContentChanged();
        }

        // Style (LayoutParams)
        DynamicLinearLayout.LayoutParams params = new DynamicLinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // params.setMargins(0, 0, 0, 0);
        buttonListView.setLayoutParams(params);

        this.addView(buttonListView);
    }

    private OnChoiceListener updateLayout = new OnChoiceListener() {
        @Override
        public void apply(ContentEntry contentEntry) {
            Log.v ("Choice", "TODO: update layout here based on state!");

            generateView(contentEntry);
        }
    };

    private ArrayList<OnChoiceListener> onChoiceListeners = new ArrayList<OnChoiceListener>();
    // private ArrayList<DynamicLinearLayout> listeners = new ArrayList<DynamicLinearLayout>();

    public void addOnChoiceListener(OnChoiceListener channelChooserView) {
        onChoiceListeners.add(channelChooserView);
    }

    public void addListener (DynamicLinearLayout channelChooserView) {
        addOnChoiceListener(channelChooserView.updateLayout);
    }

    public void notifyOnChoiceListeners(ContentEntry contentEntry, ContentEntry selectedContentEntry) {
        for (OnChoiceListener onChoiceListener : this.onChoiceListeners) {
            onChoiceListener.apply(contentEntry);
        }
    }

    public interface OnContentChangeListener {
        void notifyContentChanged ();
    }

//    public View generateChannelContentChooserView2 (final ContentEntry contentEntry, final String key2) {
//
//        final LinearLayout baseLayout = new LinearLayout(getContext());
//        baseLayout.setOrientation(LinearLayout.VERTICAL);
//
//
//        // Choices.
//        final ArrayList<Button> optionButtonList = new ArrayList<Button>();
//        final Button[] selectedOptionButton = { null };
//
//        // Layout.
//        final LinearLayout buttonListView = new LinearLayout(getContext());
//        buttonListView.setOrientation(LinearLayout.HORIZONTAL);
//        buttonListView.setVerticalGravity(Gravity.CENTER_HORIZONTAL);
//
//        // TODO: Put rendering code in here. Just make the layout scaffolding outside of this...
////        contentEntry.addOnContentChangeListener(new ContentEntry.OnContentChangeListener() {
////            @Override
////            public void notifyContentChanged() {
////
////                // Get content for chosen channel
////                ArrayList<ContentEntry> children = contentEntry.choice().get("content").getChildren();
////            }
////        });
//
////        TODO: contentEntry.addOnContentChangeListener(/* code to update the graphical state of the column to reflect the ContentEntry */);
////        TODO: eventually, only call contentChangeListeners for entries that have constraints (i.e., that potentially require updates)
//
//        //ArrayList<String> optionList = contentEntry.choice().get(key).getContentRange();
//        ArrayList<String> optionList = contentEntry.choice().get("content").getKeys();
//
//        contentEntry.addOnContentChangeListener(new ContentEntry.OnContentChangeListener() {
//            @Override
//            public void notifyContentChanged() {
//
//                // Reset.
//                for (int k = 0; k < optionButtonList.size(); k++) {
//                    optionButtonList.get(k).setTextColor(Color.LTGRAY);
//                    optionButtonList.get(k).setTypeface(null, Typeface.NORMAL);
//                }
//
////                baseLayout.findViewWithTag("content_editor_view").setVisibility(View.GONE);
//
//            }
//        });
//
//        for (int i = 0; i < optionList.size(); i++) {
//
//            final String key = optionList.get(i);
//
//            Log.v("Hello", "\t" + optionList.get(i));
//
//            // Create
//            final Button optionButton = new Button(getContext());
//
//            // Text
//            optionButton.setText(optionList.get(i));
//            optionButton.setTextSize(12);
//
//            // Style
//            optionButton.setPadding(0, 0, 0, 0);
//            optionButton.setBackgroundColor(Color.TRANSPARENT);
//            optionButton.setTextColor(Color.LTGRAY);
//
//            // Style (LayoutParams)
//            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(150, 80);
//            params.setMargins(0, 0, 0, 0);
//            optionButton.setLayoutParams(params);
//
//            // Add to view (column)
//            buttonListView.addView(optionButton);
//
//            // Add to button list
//            optionButtonList.add(optionButton);
//
//            final ContentEntry.OnContentChangeListener contentListener = new ContentEntry.OnContentChangeListener() {
//                @Override
//                public void notifyContentChanged() {
//
//                    // contentEntry2.removeOnContentChangeListener(this);
//
////                    Log.v("Compare", "\t\t\tnotifyContentChanged: " + key);
//
//                    Log.v("Content_Tree", "\t\t\tnotifyContentChanged");
//                    Log.v("Content_Tree_Notify", "\t\t\tnotifyContentChanged");
//
//                    // Reset.
//                    for (int k = 0; k < optionButtonList.size(); k++) {
//                        optionButtonList.get(k).setTextColor(Color.LTGRAY);
//                        optionButtonList.get(k).setTypeface(null, Typeface.NORMAL);
//                    }
//
//                    // Select.
//                    Log.v("Compare", "...");
//                    for (Button optionButton : optionButtonList) {
//                        //if (optionButton.getText().toString().equals(contentEntry.getContent())) {
////                        Log.v("Compare", "choice.key: " + contentEntry.choice().get(key).getContent());
//                        if (optionButton.getText().toString().equals(contentEntry.choice().get("content").get(key).getKey())) {
//                            selectedOptionButton[0] = optionButton; // Button. Select the button.
//
//                            // <HACK>
//                            selectedObservable = optionButton.getText().toString();
//                            // </HACK>
//                            break;
//                        }
//                    }
//
//                    // Color.
//                    if (selectedOptionButton[0] != null) {
//
////                        baseLayout.findViewWithTag("content_editor_view").setVisibility(View.VISIBLE);
//
//                        int textColor = ApplicationView.getApplicationView().getResources().getColor(R.color.timeline_segment_color);
//                        selectedOptionButton[0].setTextColor(textColor); // Color. Update the color.
//                        selectedOptionButton[0].setTypeface(null, Typeface.BOLD);
//                    }
//
////                    // Data.
////                    if (selectedOptionButton[0] != null) {
////                        contentEntry.choice().get(key).set(selectedOptionButton[0].getText().toString(), false);
////                    }
//                }
//            };
//
//            // Setup: Set up interactivity.
//            optionButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//
//                    // List for changes to data state...
////                    contentEntry.addOnContentChangeListener(contentListener);
//                    //contentEntry.choice().get("content").get(key).addOnContentChangeListener(contentListener);
//
//                    contentListener.notifyContentChanged();
//
//                    // Data.
//                    // Note: Don't update the content here, since this represents a choice of which
//                    // observable to edit, not an actual change to its content.
////                    contentEntry.choice().get("content").get(key).set(optionButton.getText().toString());
//
//                }
//            });
//        }
//
//        // Style (LayoutParams)
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        // params.setMargins(0, 0, 0, 0);
//        buttonListView.setLayoutParams(params);
//
//        baseLayout.addView(buttonListView);
//
//
//        return baseLayout;
//    }
}
