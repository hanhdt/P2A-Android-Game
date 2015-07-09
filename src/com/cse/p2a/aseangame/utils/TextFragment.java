/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cse.p2a.aseangame.utils;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorListenerAdapter;
import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.cse.p2a.aseangame.R;

public class TextFragment extends Fragment {

    ImageView imgHelp01;
    View.OnClickListener clickListener;
    OnTextFragmentAnimationEndListener mListener;
    Bitmap mCurrentBitmap = null;
    int mCurrentIndex = 0;
    private int screenWidth, screenHeight;

    public TextFragment() {
    }

    public TextFragment(int sWidth, int sHeight) {
        screenWidth = sWidth;
        screenHeight = sHeight;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final int[] helpImageIDs = {R.drawable.help_landscape01, R.drawable.help_landscape02,
                R.drawable.help_landscape03};

        mCurrentBitmap = ImageUtils.decodeSampledBitmapFromResource(getResources(),
                helpImageIDs[mCurrentIndex], screenWidth, screenHeight);
        View view = inflater.inflate(R.layout.text_fragment, container, false);
        imgHelp01 = (ImageView) view.findViewById(R.id.help_txt1);
        imgHelp01.setImageBitmap(mCurrentBitmap);
        imgHelp01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ++mCurrentIndex;
                if (mCurrentIndex == 1) {
                    mCurrentBitmap.recycle();
                    mCurrentBitmap = ImageUtils.decodeSampledBitmapFromResource(getResources(),
                            helpImageIDs[mCurrentIndex], screenWidth, screenHeight);
                    imgHelp01.setImageBitmap(mCurrentBitmap);

                } else if (mCurrentIndex == 2) {
                    mCurrentBitmap.recycle();
                    mCurrentBitmap = ImageUtils.decodeSampledBitmapFromResource(getResources(),
                            helpImageIDs[mCurrentIndex], screenWidth, screenHeight);
                    imgHelp01.setImageBitmap(mCurrentBitmap);
                } else {
                    mCurrentIndex = 0;
                    mCurrentBitmap.recycle();
                    mCurrentBitmap = ImageUtils.decodeSampledBitmapFromResource(getResources(),
                            helpImageIDs[mCurrentIndex], screenWidth, screenHeight);
                    imgHelp01.setImageBitmap(mCurrentBitmap);

                }
            }
        });
        view.setOnClickListener(clickListener);

        return view;
    }

    public void setClickListener(View.OnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public Animator onCreateAnimator(int transit, boolean enter, int nextAnim) {
        int id = enter ? R.anim.slide_fragment_in : R.anim.slide_fragment_out;
        final Animator anim = AnimatorInflater.loadAnimator(getActivity(), id);
        if (enter) {
            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mListener.onAnimationEnd();
                }
            });
        }
        return anim;
    }

    public void setOnTextFragmentAnimationEnd(OnTextFragmentAnimationEndListener listener) {
        mListener = listener;
    }

}
