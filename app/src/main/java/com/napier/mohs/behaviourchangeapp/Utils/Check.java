package com.napier.mohs.behaviourchangeapp.Utils;

import android.animation.ValueAnimator;

import com.airbnb.lottie.LottieAnimationView;

/**
 * Created by Mohs on 23/03/2018.
 */

public class Check {
    private static final String TAG = "Check";

    public LottieAnimationView check;

    public Check(LottieAnimationView check){
        this.check = check;
    }

    private void startCheckAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(500);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                check.setProgress((Float) valueAnimator.getAnimatedValue());
            }
        });

        if (check.getProgress() == 0f) {
            animator.start();
        } else {
            check.setProgress(0f);
        }
    }
}
