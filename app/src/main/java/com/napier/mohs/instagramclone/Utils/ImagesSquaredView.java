package com.napier.mohs.instagramclone.Utils;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;

/**
 * Created by Mohs on 17/03/2018.
 */

public class ImagesSquaredView extends AppCompatImageView {
    public ImagesSquaredView(Context context) {
        super(context);
    }

    public ImagesSquaredView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ImagesSquaredView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec); // important as widths passed make it same as height
    }
}
