package camp.computer.clay.sprites.utilities;

import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.mobeta.android.sequencer.R;

import camp.computer.clay.designer.ApplicationView;

public class Movement {

    // Based on: http://stackoverflow.com/questions/10276251/how-to-animate-a-view-with-translate-animation-in-android
    public void moveToPoint (final View view, final Point destinationPoint, int translateDuration)
    {
        FrameLayout root = (FrameLayout) ApplicationView.getApplicationView().findViewById(R.id.application_view);
        DisplayMetrics dm = new DisplayMetrics();
        ApplicationView.getApplicationView().getWindowManager().getDefaultDisplay().getMetrics( dm );
        int statusBarOffset = dm.heightPixels - root.getMeasuredHeight();

        int originalPos[] = new int[2];
        view.getLocationOnScreen( originalPos );

        /*
        int xDest = dm.widthPixels/2;
        xDest -= (view.getMeasuredWidth()/2);
        int yDest = dm.heightPixels/2 - (view.getMeasuredHeight()/2) - statusBarOffset;
        */

        int xDest = destinationPoint.x;
        int yDest = destinationPoint.y;


        final int amountToMoveRight = xDest - originalPos[0] - (int) (view.getWidth() / 2.0f);
        final int amountToMoveDown = yDest - originalPos[1] - (int) (view.getHeight() / 2.0f);

        TranslateAnimation animation = new TranslateAnimation(0, amountToMoveRight, 0, amountToMoveDown);
        animation.setDuration(translateDuration);
        // animation.setFillAfter(true);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
//                params.topMargin += amountToMoveDown;
//                params.leftMargin += amountToMoveRight;
//                view.setLayoutParams(params);

                // Get button holder
                RelativeLayout relativeLayout = (RelativeLayout) ApplicationView.getApplicationView().findViewById(R.id.context_button_holder);

                // Get screen width and height of the device
                DisplayMetrics metrics = new DisplayMetrics();
                ApplicationView.getApplicationView().getWindowManager().getDefaultDisplay().getMetrics(metrics);
                int screenWidth = metrics.widthPixels;
                int screenHeight = metrics.heightPixels;

                // Get button width and height
                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) relativeLayout.getLayoutParams();
                int buttonWidth = relativeLayout.getWidth();
                int buttonHeight = relativeLayout.getHeight();

                // Reposition button
//                params.rightMargin = screenWidth - (int) event.getRawX() - (int) (buttonWidth / 2.0f);
//                params.bottomMargin = screenHeight - (int) event.getRawY() - (int) (buttonHeight / 2.0f);

                params.bottomMargin = screenWidth - (int) destinationPoint.x - (int) (buttonWidth / 2.0f); // amountToMoveDown;
                params.rightMargin = screenHeight - (int) destinationPoint.y - (int) (buttonHeight / 2.0f); // amountToMoveRight;
                view.setLayoutParams(params);

//                relativeLayout.requestLayout();
//                relativeLayout.invalidate();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        view.startAnimation (animation);
    }

}
