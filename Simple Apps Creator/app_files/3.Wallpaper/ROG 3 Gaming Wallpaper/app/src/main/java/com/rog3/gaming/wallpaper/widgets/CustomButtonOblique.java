package com.rog3.gaming.wallpaper.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

public class CustomButtonOblique extends androidx.appcompat.widget.AppCompatButton {

    public CustomButtonOblique (Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomButtonOblique (Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomButtonOblique (Context context) {
        super(context);
        init();
    }

    private void init() {
        Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "OpenSans-Regular.ttf");
        setTypeface(tf);
    }

}