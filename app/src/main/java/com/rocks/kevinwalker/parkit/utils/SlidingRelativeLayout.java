package com.rocks.kevinwalker.parkit.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.RelativeLayout;

/**
 * Custom implementation of the RelativeLayout class to enable animation on a custom xFraction property
 */
public class SlidingRelativeLayout extends RelativeLayout {

    private float xFraction = 0;

    public SlidingRelativeLayout(Context context) {
        super(context);
    }

    public SlidingRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlidingRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private ViewTreeObserver.OnPreDrawListener preDrawListener = null;

    /**
     * Sets the position of the RelativeLayout
     * @param xFraction The fraction to alter this layout's position to (-1.0 to 1.0)
     */
    private void setXFraction(final float xFraction) {

        this.xFraction = xFraction;

        if(getWidth() == 0) {
            if(preDrawListener == null) {
                preDrawListener = new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        getViewTreeObserver().removeOnPreDrawListener(preDrawListener);
                        setXFraction(xFraction);
                        return true;
                    }
                };

                getViewTreeObserver().addOnPreDrawListener(preDrawListener);
            }

            return;
        }

        float translationX = getWidth() * xFraction;
        setTranslationX(translationX);
    }

    public float getxFraction() {
        return this.xFraction;
    }
}