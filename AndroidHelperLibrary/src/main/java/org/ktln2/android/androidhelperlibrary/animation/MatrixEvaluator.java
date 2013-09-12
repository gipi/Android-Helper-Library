package org.ktln2.android.androidhelperlibrary.animation;

import android.graphics.Matrix;

import com.nineoldandroids.animation.FloatEvaluator;
import com.nineoldandroids.animation.TypeEvaluator;


public class MatrixEvaluator implements TypeEvaluator<Matrix> {
    static String TAG = "MatrixEvaluator";

    static float[] valuesStart = new float[9];
    static float[] valuesEnd = new float[9];
    static float[] valuesCalculation = new float[9];

    static private Matrix finalMatrix = new Matrix();

    static private FloatEvaluator floatEvaluator = new FloatEvaluator();

    @Override
    public Matrix evaluate(float t, Matrix startMatrix, Matrix endMatrix) {
        // retrieve the values of the matrix
        startMatrix.getValues(valuesStart);
        endMatrix.getValues(valuesEnd);

        /**
         * Now we calculate the difference value between the matrix entries but not for rotation
         * because is not so simple and for now we want only to translate/scale all the things.
         */
        finalMatrix.reset();// so we have an identity matrix to work with
        finalMatrix.getValues(valuesCalculation);

        valuesCalculation[Matrix.MTRANS_X] = floatEvaluator.evaluate(t, valuesStart[Matrix.MTRANS_X], valuesEnd[Matrix.MTRANS_X]);
        valuesCalculation[Matrix.MTRANS_Y] = floatEvaluator.evaluate(t, valuesStart[Matrix.MTRANS_Y], valuesEnd[Matrix.MTRANS_Y]);
        valuesCalculation[Matrix.MSCALE_X] = floatEvaluator.evaluate(t, valuesStart[Matrix.MSCALE_X], valuesEnd[Matrix.MSCALE_X]);
        valuesCalculation[Matrix.MSCALE_Y] = floatEvaluator.evaluate(t, valuesStart[Matrix.MSCALE_Y], valuesEnd[Matrix.MSCALE_Y]);

        finalMatrix.setValues(valuesCalculation);

        return finalMatrix;
    }
}
