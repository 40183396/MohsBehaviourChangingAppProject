package com.napier.mohs.instagramclone.Utils;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

/**
 * Created by Mohs on 19/03/2018.
 *
 * handles toggling of star like
 */

public class Star {
    private static final String TAG = "Star";

    private static final DecelerateInterpolator DECELERATE_INTERPOLATOR = new DecelerateInterpolator();
    private static final AccelerateInterpolator ACCELERATE_INTERPOLATOR = new AccelerateInterpolator();

    public ImageView starYellow, starHollow;

    public Star(ImageView starHollow, ImageView starYellow){
        this.starHollow = starHollow;
        this.starYellow = starYellow;
    }

    public void likeToggle(){
        Log.d(TAG, "likeToggle: toggling star");

        AnimatorSet animationSet = new AnimatorSet();

        if(starYellow.getVisibility() == View.VISIBLE){
            Log.d(TAG, "likeToggle: toggling yellow star off");
            starYellow.setScaleX(0.1f);
            starYellow.setScaleY(0.1f);

            ObjectAnimator scaleYDown = ObjectAnimator.ofFloat(starYellow, "scaleY", 1f, 0f);
            scaleYDown.setDuration(300);
            scaleYDown.setInterpolator(ACCELERATE_INTERPOLATOR);

            ObjectAnimator scaleXDown = ObjectAnimator.ofFloat(starYellow, "scaleX", 1f, 0f);
            scaleXDown.setDuration(300);
            scaleXDown.setInterpolator(ACCELERATE_INTERPOLATOR);

            starYellow.setVisibility(View.GONE);
            starHollow.setVisibility(View.VISIBLE);

            animationSet.playTogether(scaleYDown, scaleXDown);
        }

        else if(starYellow.getVisibility() == View.GONE){
            Log.d(TAG, "likeToggle: toggling yellow star off");
            starYellow.setScaleX(0.1f);
            starYellow.setScaleY(0.1f);

            ObjectAnimator scaleYDown = ObjectAnimator.ofFloat(starYellow, "scaleY", 0.1f, 1f);
            scaleYDown.setDuration(300);
            scaleYDown.setInterpolator(ACCELERATE_INTERPOLATOR);

            ObjectAnimator scaleXDown = ObjectAnimator.ofFloat(starYellow, "scaleX", 0.1f, 1f);
            scaleXDown.setDuration(300);
            scaleXDown.setInterpolator(ACCELERATE_INTERPOLATOR);

            starYellow.setVisibility(View.VISIBLE);
            starHollow.setVisibility(View.GONE);

            animationSet.playTogether(scaleYDown, scaleXDown);
        }

        animationSet.start();
    }
}
