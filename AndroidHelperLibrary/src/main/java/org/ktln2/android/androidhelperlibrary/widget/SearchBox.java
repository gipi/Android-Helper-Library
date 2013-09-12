package org.ktln2.android.androidhelperlibrary.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;

import org.ktln2.android.androidhelperlibrary.R;


// http://www.vogella.com/articles/AndroidCustomViews/article.html#tutorial_compoundcontrols
public class SearchBox extends RelativeLayout {
    public SearchBox(Context context) {
        super(context);
    }

    public SearchBox(Context context, AttributeSet attrs) {
        super(context, attrs);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ahl__search_box, this, true);
    }

    public SearchBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
}
