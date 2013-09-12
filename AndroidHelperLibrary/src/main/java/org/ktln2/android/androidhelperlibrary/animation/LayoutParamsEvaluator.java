package org.ktln2.android.androidhelperlibrary.animation;

import android.view.ViewGroup;

import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.animation.TypeEvaluator;


/**
 * This evaluator is used in animation of "layouting" a view. Because internally
 * the configuration containing WRAP_CONTENT/MATCH_PARENT are identified with
 * negative numbers they are not allowed.
 *
 * Remember: the LayoutParams MUST be the parent one!
 *
 */
public class LayoutParamsEvaluator<T extends ViewGroup.LayoutParams> implements TypeEvaluator<T> {
    private T mLayoutParams;
    static private IntEvaluator intEvaluator = new IntEvaluator();

    public LayoutParamsEvaluator(T value) {
        super();

        mLayoutParams = value;
    }

    @Override
    public T evaluate(float t, T startLayoutParams, T finalLayoutParams) {
        if (startLayoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT ||
                startLayoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT ||
                finalLayoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT ||
                finalLayoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT)
            throw new RuntimeException("MATCH_PARENT/WRAP_CONTENT are not allowed in animations");

        mLayoutParams.width = intEvaluator.evaluate(t, startLayoutParams.width, finalLayoutParams.width);
        mLayoutParams.height = intEvaluator.evaluate(t, startLayoutParams.height, finalLayoutParams.height);

        return mLayoutParams;
    }
}
