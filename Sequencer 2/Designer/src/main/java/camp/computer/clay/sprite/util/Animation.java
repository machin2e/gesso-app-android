package camp.computer.clay.sprite.util;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.mobeta.android.sequencer.R;

import camp.computer.clay.designer.ApplicationView;

public class Animation {

    // References:
    // - http://stackoverflow.com/questions/10276251/how-to-animate-a-view-with-translate-animation-in-android
    public void moveToPoint (final View view, final Point destinationPoint, int translateDuration)
    {
        FrameLayout root = (FrameLayout) ApplicationView.getApplicationView().findViewById(R.id.application_view);
        DisplayMetrics dm = new DisplayMetrics();
        ApplicationView.getApplicationView().getWindowManager().getDefaultDisplay().getMetrics( dm );
        int statusBarOffset = dm.heightPixels - root.getMeasuredHeight();

        int originalPosition[] = new int[2];
        view.getLocationOnScreen(originalPosition);

        int xDest = destinationPoint.x;
        int yDest = destinationPoint.y;

        final int amountToMoveRight = xDest - originalPosition[0] - (int) (view.getWidth() / 2.0f);
        final int amountToMoveDown = yDest - originalPosition[1] - (int) (view.getHeight() / 2.0f);

        TranslateAnimation animation = new TranslateAnimation(0, amountToMoveRight, 0, amountToMoveDown);
        animation.setDuration(translateDuration);
        // animation.setFillAfter(true);

        animation.setAnimationListener(new android.view.animation.Animation.AnimationListener() {
            @Override
            public void onAnimationStart(android.view.animation.Animation animation) {

            }

            @Override
            public void onAnimationEnd(android.view.animation.Animation animation) {

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

                params.bottomMargin = screenWidth - (int) destinationPoint.x - (int) (buttonWidth / 2.0f); // amountToMoveDown;
                params.rightMargin = screenHeight - (int) destinationPoint.y - (int) (buttonHeight / 2.0f); // amountToMoveRight;
                view.setLayoutParams(params);

//                relativeLayout.requestLayout();
//                relativeLayout.invalidate();
            }

            @Override
            public void onAnimationRepeat(android.view.animation.Animation animation) {

            }
        });


        view.startAnimation (animation);
    }

    public interface OnScaleListener {
        void onScale (float currentScale);
    }

    public static void scaleValue (final float startScale, final float targetScale, int duration, final OnScaleListener onScaleListener) {

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(startScale, targetScale);
        valueAnimator.setDuration(duration);
        // valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());

        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (onScaleListener != null) {
                    onScaleListener.onScale((float) animation.getAnimatedValue());
                }
            }
        });

        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
//                ((MapView) view).scale = targetScale;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        valueAnimator.start();
    }

}
