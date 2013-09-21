package org.ktln2.android.androidhelperlibrary.animation;

import android.view.ViewGroup;

import com.nineoldandroids.animation.IntEvaluator;
import com.nineoldandroids.animation.TypeEvaluator;


/**
 * This evaluator is used in animation of "layouting" a view. Because internally
 * the configuration containing WRAP_CONTENT/MATCH_PARENT are identified with
 * negative numbers they are not allowed.
 *
 * Remember: for now is possible to act only on the width/height values!
 *
 */
public class LayoutParamsEvaluator<T extends ViewGroup.LayoutParams, S extends ViewGroup> implements TypeEvaluator<T> {
    private S mInstance;
    static private IntEvaluator intEvaluator = new IntEvaluator();

    public LayoutParamsEvaluator(S object) {
        super();

        mInstance = object;
    }

    @Override
    public T evaluate(float t, ViewGroup.LayoutParams startLayoutParams, ViewGroup.LayoutParams finalLayoutParams) {
        if (startLayoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT ||
                startLayoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT ||
                finalLayoutParams.height == ViewGroup.LayoutParams.WRAP_CONTENT ||
                finalLayoutParams.height == ViewGroup.LayoutParams.MATCH_PARENT)
            throw new RuntimeException("MATCH_PARENT/WRAP_CONTENT are not allowed in animations");

        T originalLp = (T)mInstance.getLayoutParams();

        originalLp.width = intEvaluator.evaluate(t, startLayoutParams.width, finalLayoutParams.width);
        originalLp.height = intEvaluator.evaluate(t, startLayoutParams.height, finalLayoutParams.height);

        return originalLp;
    }
}
