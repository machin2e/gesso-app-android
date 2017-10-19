package camp.computer.clay.platform.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

public class ViewGroupHelper {

    /**
     * Configuration
     */

    private static Context context;

    public static void setContext(Context context) {
        ViewGroupHelper.context = context;
    }

    public static Context getContext() {
        return ViewGroupHelper.context;
    }

    /**
     * ViewGroup
     */

    public static ViewGroup getParent(View view) {
        return (ViewGroup) view.getParent();
    }

    public static void removeView(View view) {
        ViewGroup parent = getParent(view);
        if (parent != null) {
            parent.removeView(view);
        }
    }

    public static void replaceView(View currentView, View newView) {
        ViewGroup parent = getParent(currentView);
        if (parent == null) {
            return;
        }
        final int index = parent.indexOfChild(currentView);
        removeView(currentView);
        removeView(newView);
        parent.addView(newView, index);
    }

    /**
     * Display/Screen
     */

    public static int dpToPx(float dp) {

        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                r.getDisplayMetrics()
        );

        return px;
    }

    public static int mmToPx(float mm) {

        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_MM,
                mm,
                r.getDisplayMetrics()
        );

        return px;
    }

    public int inToPx(float in) {

        Resources r = context.getResources();
        int px = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_IN,
                in,
                r.getDisplayMetrics()
        );

        return px;
    }
}
