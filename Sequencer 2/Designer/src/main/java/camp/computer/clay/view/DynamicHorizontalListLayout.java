package camp.computer.clay.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.mobeta.android.sequencer.R;

import java.util.ArrayList;

import camp.computer.clay.designer.ApplicationView;

public class DynamicHorizontalListLayout extends LinearLayout {

    // Choices.
    final ArrayList<Button> optionButtonList = new ArrayList<Button>();
    final Button[] selectedOptionButton = { null };

    // Layout.
    LinearLayout buttonListView = new LinearLayout(getContext());

    public DynamicHorizontalListLayout(Context context) {
        super(context);
        generateView();
    }

    public DynamicHorizontalListLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        generateView();
    }

    public DynamicHorizontalListLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        generateView();
    }

    public DynamicHorizontalListLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        generateView();
    }

    private ArrayList<OnChangeOptionListListener> onChangeOptionListListeners = new ArrayList<OnChangeOptionListListener>();

    public interface OnChangeOptionListListener {
        void onOptionListChanged (View dynamicLayout);
    }

    public void addOnChangeOptionListListener(OnChangeOptionListListener onChangeOptionListListener) {
        onChangeOptionListListeners.add(onChangeOptionListListener);
    }

    public void notifyOnChangeOptionListListeners() {
        Log.v ("Horizontal_Selector", "notifyOnChangeOptionListListeners");
        for (OnChangeOptionListListener onChangeOptionListListener : this.onChangeOptionListListeners) {
            onChangeOptionListListener.onOptionListChanged(this);
        }
    }

    private ArrayList<OnSelectOptionListener> onSelectOptionListeners = new ArrayList<OnSelectOptionListener>();

    public interface OnSelectOptionListener {
        void onSelectOption (View dynamicLayout);
    }

    public void addOnSelectOptionListener (OnSelectOptionListener onSelectOptionListener) {
        onSelectOptionListeners.add(onSelectOptionListener);
    }

    public void notifyOnSelectOptionListeners() {
        Log.v("Horizontal_Selector", "notifyOnSelectOptionListeners");
        for (OnSelectOptionListener onSelectOptionListener : this.onSelectOptionListeners) {
            onSelectOptionListener.onSelectOption(this);
        }
    }

    public void resetSelection () {
        setSelectedButton(null);
    }

    public void setOptions (ArrayList<String> optionList) {

        // Reset view. Remove all option buttons from layout
        for (Button optionButton : this.optionButtonList) {
            this.buttonListView.removeView(optionButton);
        }

        // Reset options. Reset option button list
        this.optionButtonList.clear();

        // Reset selected option.
        this.selectedOptionButton[0] = null;

        // Add new options.
        generateButtonList(optionList, null);

        updateView();
    }

    public void setOptions (ArrayList<String> optionList, ArrayList<String> optionTextList) {

        // Reset view. Remove all option buttons from layout
        for (Button optionButton : this.optionButtonList) {
            this.buttonListView.removeView(optionButton);
        }

        // Reset options. Reset option button list
        this.optionButtonList.clear();

        // Reset selected option.
        this.selectedOptionButton[0] = null;

        // Add new options.
        generateButtonList(optionList, optionTextList);

        updateView();
    }

    public ArrayList<String> getOptions() {
        ArrayList<String> optionList = new ArrayList<String>();
        for (Button optionButton : this.optionButtonList) {
            //optionList.add(optionButton.getText().toString());
            optionList.add((String) optionButton.getTag(R.id.UUID_TAG_INDEX));
        }
        return optionList;
    }

    public boolean hasSelection () {
        return this.selectedOptionButton[0] != null;
    }

    public String getSelection() {
        if (this.selectedOptionButton[0] != null) {
            //return this.selectedOptionButton[0].getText().toString();
            return (String) this.selectedOptionButton[0].getTag(R.id.UUID_TAG_INDEX);
        } else {
            return null;
        }
    }

    private void generateButtonList (ArrayList<String> optionList, ArrayList<String> optionTextList) {

        for (int i = 0; i < optionList.size(); i++) {

            // Get text to display
            String optionText = null;
            if (optionTextList != null && i < optionTextList.size() && optionTextList.get(i) != null) {
                optionText = optionTextList.get(i);
            } else {
                optionText = optionList.get(i);
            }

            // Create
            Button optionButton = new Button(getContext());

            // Text
            optionButton.setText(optionText);
            optionButton.setTextSize(12);

            // Style
            optionButton.setPadding(0, 0, 0, 0);
            optionButton.setBackgroundColor(Color.TRANSPARENT);
            optionButton.setTextColor(Color.LTGRAY);

            // Data (Tags)
            optionButton.setTag(R.id.UUID_TAG_INDEX, optionList.get(i));

            // Measure width of view before displaying
//            WindowManager windowManager = (WindowManager) getContext()
//                    .getSystemService(Context.WINDOW_SERVICE);
//            Display display = windowManager.getDefaultDisplay();
//
//            LinearLayout view = (LinearLayout) ((ViewGroup) this.getParent()).getParent();
////            View view = this; // TODO: View view = findViewById(R.id.YOUR_VIEW_ID);
//            view.measure(display.getWidth(), display.getHeight());
//
//            view.getMeasuredWidth(); // view width
//            view.getMeasuredHeight(); //view height
////            Log.v("Measurement", "width: " + view.getMeasuredWidth());
////            Log.v("Measurement", "height: " + view.getMeasuredHeight());

            // Style (LayoutParams)
            // <HACK>
            int parentWidth = 920; // TODO: Get this programmatically from parent view.
            int buttonWidth = parentWidth / optionList.size();
            int buttonHeight = 80; // TODO: Get programmatically based on text height.
            // </HACK>
            LayoutParams params = new LayoutParams(buttonWidth, buttonHeight);
            params.setMargins(0, 0, 0, 0);
            optionButton.setLayoutParams(params);

            // Add to view (column)
            buttonListView.addView(optionButton);

            // Add to button list
            optionButtonList.add(optionButton);
        }

        notifyOnChangeOptionListListeners();

        // Interactivity. Set up event handlers for buttons.
        for (final Button optionButton : this.optionButtonList) {

            // Setup: Set up interactivity.
            optionButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    setSelectedButton(optionButton);
                }
            });
        }
    }

    public void setSelection (String text) {
        Log.v("Horizontal_Selector", "setSelection");
        if (text != null) {
            for (Button optionButton : this.optionButtonList) {
                String buttonOption = (String) optionButton.getTag(R.id.UUID_TAG_INDEX);
                if (buttonOption.equals(text)) {
                //if (optionButton.getText().toString().equals(text)) {
                    setSelectedButton(optionButton);
                    break;
                }
            }
        }
    }

    private void setSelectedButton(Button optionButton) {
        selectedOptionButton[0] = optionButton;
        updateView();
        notifyOnSelectOptionListeners();
    }

    public Button getSelectedButton() {
        return selectedOptionButton[0];
    }

    private void generateView () {

        this.setOrientation(DynamicHorizontalListLayout.VERTICAL);


//        // Reset
//        optionButtonList.clear();
//        selectedOptionButton[0] = null;
//
//
//        this.removeAllViews();

        // Layout.
        //DynamicHorizontalListLayout buttonListView = new DynamicHorizontalListLayout(getContext());
        if (buttonListView == null) {
            buttonListView = new DynamicHorizontalListLayout(getContext());
            buttonListView.setOrientation(DynamicHorizontalListLayout.HORIZONTAL);
            buttonListView.setVerticalGravity(Gravity.CENTER_HORIZONTAL);
        }

        //ArrayList<String> optionList = contentEntry.choice().get(key).getContentRange();
        ArrayList<String> optionList = new ArrayList<String>();
        ArrayList<String> optionTextList = new ArrayList<String>();


        generateButtonList(optionList, optionTextList);

        // Style (LayoutParams)
        LayoutParams LLParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
//        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        // params.setMargins(0, 0, 0, 0);
        buttonListView.setLayoutParams(LLParams);

        this.addView(buttonListView);
    }

    public void updateView () {
        // Reset.
        for (int k = 0; k < optionButtonList.size(); k++) {
            optionButtonList.get(k).setTextColor(Color.LTGRAY);
            optionButtonList.get(k).setTypeface(null, Typeface.NORMAL);
        }

//                    // Select.
//                    for (Button optionButton : optionButtonList) {
//                        //if (optionButton.getText().toString().equals(contentEntry.getContent())) {
////                        Log.v("Compare", "choice.key: " + contentEntry.choice().get(key).getContent());
//                        //if (optionButton.getText().toString().equals(contentEntry.choice().get("content").get(key).getKey())) {
//                        if (optionButton.getText().toString().equals(contentEntry.get(optionText).getKey())) {
//                            selectedOptionButton[0] = optionButton; // Button. Select the button.
//
//                            // <HACK>
////                            selectedObservable = optionButton.getText().toString();
//                            // </HACK>
//                            break;
//                        }
//                    }

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

    // TODO: Add (selection_constraint_in_this_view, View)
    ArrayList<View> dependentViews = new ArrayList<View>();

    public void addDependentView (View view) {
        dependentViews.add (view);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);

        // TODO: Set visibility of dependent views!
        for (View dependentView : this.dependentViews) {
            dependentView.setVisibility(visibility);
        }
    }
}
