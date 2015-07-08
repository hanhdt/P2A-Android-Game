package com.cse.p2a.aseangame.utils;

import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * Created by rafe on 12/16/13.
 */
public class CartoonAnimationUtils {

    View mStarter;
    ViewGroup mContainer;
    private static final AccelerateInterpolator sAccelerator = new AccelerateInterpolator();
    private static final DecelerateInterpolator sDecelerator = new DecelerateInterpolator();
    private static final LinearInterpolator sLinearInterpolator = new LinearInterpolator();
    static long SHORT_DURATION = 100;
    static long MEDIUM_DURATION = 200;
    static long REGULAR_DURATION = 300;
    static long LONG_DURATION = 500;

    private static float sDurationScale = 1f;

    public CartoonAnimationUtils(View starter, ViewGroup vGroup) {
        this.mStarter = starter;
        this.mContainer = vGroup;
    }
}
