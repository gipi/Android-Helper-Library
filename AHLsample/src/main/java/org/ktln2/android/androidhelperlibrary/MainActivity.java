package org.ktln2.android.androidhelperlibrary;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.widget.ArrayAdapter;

import org.ktln2.android.androidhelperlibrary.widget.SearchBox;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.ObjectAnimator;

import org.ktln2.android.androidhelperlibrary.animation.LayoutParamsEvaluator;


public class MainActivity extends Activity {
    private LinearLayout mExpandableLayout;
    private int mInitialWidth;
    private int mInitialHeight;
    private boolean mIsExpanded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        AndroidHelper.logInfo(this);

        SearchBox box = (SearchBox)findViewById(R.id.main_search_box);
        box.setAutocompleteAdapter(new ArrayAdapter<String>(
            this,
            android.R.layout.simple_list_item_1,
            getResources().getStringArray(R.array.names)
        ));

        configureExpandableLayout();
    }

    Animator.AnimatorListener expandAnimatorListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationEnd(Animator animation) {
            mIsExpanded = !mIsExpanded;
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {

        }
    };

    private View.OnClickListener onExpandableClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ViewGroup.LayoutParams originalLp = mExpandableLayout.getLayoutParams();
            ViewGroup.LayoutParams startLp = new ViewGroup.LayoutParams(originalLp);
            ViewGroup.LayoutParams finalLp = new ViewGroup.LayoutParams(originalLp);

            // get the starting dimensions to avoid wrap_content/match_parent
            // misleading values
            startLp.height = mIsExpanded ? 500 : mInitialHeight;
            startLp.width = mIsExpanded ? 500 : mInitialWidth;

            finalLp.width = mIsExpanded ? mInitialWidth : 500;
            finalLp.height = mIsExpanded? mInitialHeight: 500;

            ObjectAnimator animator = ObjectAnimator.ofObject(
                    mExpandableLayout,
                    "layoutParams",
                    new LayoutParamsEvaluator<RelativeLayout.LayoutParams, LinearLayout>(
                            mExpandableLayout),
                    startLp,
                    finalLp
            );
            animator.setInterpolator(new AnticipateOvershootInterpolator());
            animator.addListener(expandAnimatorListener);
            animator.start();
        }
    };

    private void configureExpandableLayout() {
        mExpandableLayout = (LinearLayout)findViewById(R.id.main_expandable_layout);
        // because of the layout system we have to wait in order to know the size of the mExpandableLayout
        mExpandableLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mExpandableLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                mInitialHeight = mExpandableLayout.getHeight();
                mInitialWidth  = mExpandableLayout.getWidth();
            }
        });

        mExpandableLayout.setOnClickListener(onExpandableClicked);
    }
}
