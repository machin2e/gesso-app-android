package camp.computer.clay.platform.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;

import camp.computer.clay.platform.Application;

public class DeviceDimensionsHelper {

    // DeviceDimensionsHelper.getDisplayWidth(context) => (display width in pixels)
    public static int getDisplayWidth(Context context) {
//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        return displayMetrics.widthPixels;

        int screenWidth = 0;
        int screenHeight = 0;
        if (Build.VERSION.SDK_INT >= 11) {
            Point size = new Point();
            try {
                Application.getInstance().getWindowManager().getDefaultDisplay().getRealSize(size);
                screenWidth = size.x;
                screenHeight = size.y;
            } catch (NoSuchMethodError e) {
                Log.i("error", "it can't work");
            }

        } else {
            DisplayMetrics metrics = new DisplayMetrics();
            Application.getInstance().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            screenWidth = metrics.widthPixels;
            screenHeight = metrics.heightPixels;
        }
        return screenWidth;
    }

    // DeviceDimensionsHelper.getDisplayHeight(context) => (display height in pixels)
    public static int getDisplayHeight(Context context) {
//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        return displayMetrics.heightPixels;

        int screenWidth = 0;
        int screenHeight = 0;
        if (Build.VERSION.SDK_INT >= 11) {
            Point size = new Point();
            try {
                Application.getInstance().getWindowManager().getDefaultDisplay().getRealSize(size);
                screenWidth = size.x;
                screenHeight = size.y;
            } catch (NoSuchMethodError e) {
                Log.i("error", "it can't work");
            }

        } else {
            DisplayMetrics metrics = new DisplayMetrics();
            Application.getInstance().getWindowManager().getDefaultDisplay().getMetrics(metrics);
            screenWidth = metrics.widthPixels;
            screenHeight = metrics.heightPixels;
        }
        return screenHeight;
    }

    /*
    public static int getDisplayDensity(Context context) {
        // TODO: http://stackoverflow.com/questions/3166501/getting-the-screen-density-programmatically-in-android?noredirect=1&lq=1
    }
    */

    // DeviceDimensionsHelper.convertDpToPixel(25f, context) => (25dp converted to pixels)
    public static float convertDpToPixel(float dp, Context context) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    // DeviceDimensionsHelper.convertPixelsToDp(25f, context) => (25px converted to dp)
    public static float convertPixelsToDp(float px, Context context) {
        Resources r = context.getResources();
        DisplayMetrics metrics = r.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }

    // DeviceDimensionsHelper.convertMmToPixel(25f, context) => (25mm converted to pixels)
    public static float convertMmToPixel(float dp, Context context) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_MM, dp, r.getDisplayMetrics());
    }

//    public int dpToPx(float dp) {
//
//        Resources r = context.getResources();
//        int px = (int) TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_DIP,
//                dp,
//                r.getDisplayMetrics()
//        );
//
//        return px;
//    }
//
//    public int mmToPx(float mm) {
//
//        Resources r = context.getResources();
//        int px = (int) TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_MM,
//                mm,
//                r.getDisplayMetrics()
//        );
//
//        return px;
//    }
//
//    public int inToPx(float in) {
//
//        Resources r = context.getResources();
//        int px = (int) TypedValue.applyDimension(
//                TypedValue.COMPLEX_UNIT_IN,
//                in,
//                r.getDisplayMetrics()
//        );
//
//        return px;
//    }
}
