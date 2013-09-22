package org.ktln2.android.androidhelperlibrary.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.TextWatcher;
import android.text.Editable;

import org.ktln2.android.androidhelperlibrary.R;


/**
 * Simple widget to encapsule some common behaviors expected from a search box:
 *
 *  - when some text is entered a "clear" button is shown
 *  - when an action is performed on text entered a loading icon is shown
 */
public class SearchBox extends RelativeLayout {
    private Context mContext;

    private AutoCompleteTextView mAutocompleteTextView;
    private ImageButton mCancelSearch;

    private enum SEARCH_MODE {
        EMPTY_TEXT,
        TEXT,
        LOADING,
    };
    private SEARCH_MODE mSearchMode = SEARCH_MODE.EMPTY_TEXT;

    public SearchBox(Context context) {
        super(context);

        mContext = context;

        init();
    }

    public SearchBox(Context context, AttributeSet attrs) {
        super(context, attrs);

        mContext = context;

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ahl__search_box, this, true);

        init();
    }

    public SearchBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        mContext = context;

        init();
    }

    private void init() {
        configureAutocompleteTextView();
        configureActionButton();
    }

    private TextView.OnEditorActionListener searchBoxEditorListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_SEARCH || actionId == EditorInfo.IME_NULL) {

                doUpdate(v.getText());

                onSearchModeChange(SEARCH_MODE.LOADING);
                handled = true;
            }

            return handled;
        }
    };

    protected void doUpdate(CharSequence sequence) {}

    // update the search mode based on empty/not empty text
    TextWatcher searchBoxTextWatcher = new TextWatcher() {
        @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mSearchMode == SEARCH_MODE.EMPTY_TEXT) {
                    onSearchModeChange(SEARCH_MODE.TEXT);
                }

                if (s.toString().equals("")) {
                    onSearchModeChange(SEARCH_MODE.EMPTY_TEXT);
                }
            }

        @Override
            public void afterTextChanged(Editable s) {}
    };

    View.OnClickListener cancelSearchListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mAutocompleteTextView.setText("");
        }
    };

    protected void configureAutocompleteTextView() {
        mAutocompleteTextView = (AutoCompleteTextView)findViewById(R.id.ahl__searchbox_autocomplete);
        mAutocompleteTextView.setOnEditorActionListener(searchBoxEditorListener);
        mAutocompleteTextView.addTextChangedListener(searchBoxTextWatcher);
    }

    protected void configureActionButton() {
        mCancelSearch = (ImageButton)findViewById(R.id.ahl__searchbox_action);
    }

    protected void onSearchModeChange(SEARCH_MODE mode) {
        mSearchMode = mode;

        // remove any previous animation
        mCancelSearch.clearAnimation();

        boolean enabled = true;
        int level = 0;
        View.OnClickListener listener = null;

        switch (mode) {
            case EMPTY_TEXT:
                level = 1;
                break;
            case TEXT:
                level = 2;
                listener = cancelSearchListener;
                break;
            case LOADING:
                level = 3;
                enabled = false;

                Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.ahl_loading);
                mCancelSearch.startAnimation(animation);

                break;
            default:
                throw new RuntimeException("You have generated an impossible situation, you are welcome");
        }

        mCancelSearch.setImageLevel(level);
        mCancelSearch.setOnClickListener(listener);
        mAutocompleteTextView.setEnabled(enabled);
    }

    public <T extends ListAdapter & Filterable> void setAutocompleteAdapter(T adapter) {
        mAutocompleteTextView.setAdapter(adapter);
    }
}
