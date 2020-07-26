package com.rog3.gaming.wallpaper.widgets;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

@SuppressLint("AppCompatCustomView")
public class CustomTextViewOblique extends TextView {

    public CustomTextViewOblique(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomTextViewOblique(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomTextViewOblique(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Regular.ttf");
            setTypeface(tf);
        }
    }

}