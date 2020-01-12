package com.techcorp.babasaheb.ambedkarquotes.inhindi.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class CustomButtonLight extends androidx.appcompat.widget.AppCompatButton {

    public CustomButtonLight(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomButtonLight(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomButtonLight(Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Regular.ttf");
        setTypeface(tf);
    }

}