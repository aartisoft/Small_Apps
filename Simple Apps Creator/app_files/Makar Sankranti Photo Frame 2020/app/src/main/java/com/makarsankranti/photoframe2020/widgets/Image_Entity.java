package com.makarsankranti.photoframe2020.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;

public class Image_Entity extends Multi_Touch_Entity {

    private static final double INITIAL_SCALE_FACTOR = 0.14999999999999999D;
    int chk;
    private Drawable ddd;
    private transient Drawable mDrawable;
    private int mResourceId;

    public Image_Entity(int i, Resources resources) {
        super(resources);
        chk = 1;
        mResourceId = i;
    }

    public Image_Entity(Drawable drawable, Resources resources) {
        super(resources);
        chk = 2;
        ddd = drawable;
    }

    public Image_Entity(Image_Entity imageentity, Resources resources) {
        super(resources);
        mDrawable = imageentity.mDrawable;
        mResourceId = imageentity.mResourceId;
        ddd = imageentity.ddd;
        mScaleX = imageentity.mScaleX;
        mScaleY = imageentity.mScaleY;
        mCenterX = imageentity.mCenterX;
        mCenterY = imageentity.mCenterY;
        mAngle = imageentity.mAngle;
    }

    public void draw(Canvas canvas) {
        canvas.save();
        float f = (mMaxX + mMinX) / 2.0F;
        float f1 = (mMaxY + mMinY) / 2.0F;
        mDrawable.setBounds((int) mMinX, (int) mMinY, (int) mMaxX, (int) mMaxY);
        canvas.translate(f, f1);
        canvas.rotate((180F * mAngle) / 3.141593F);
        canvas.translate(-f, -f1);
        mDrawable.draw(canvas);
        canvas.restore();
    }

    public void load(Context context, float f, float f1) {
        Resources resources = context.getResources();
        getMetrics(resources);
        mStartMidX = f;
        mStartMidY = f1;
        mDrawable = ddd;
        if (chk == 1) {
            mDrawable = resources.getDrawable(mResourceId);
        }
        mWidth = mDrawable.getIntrinsicWidth();
        mHeight = mDrawable.getIntrinsicHeight();
        float f2;
        float f3;
        float f4;
        float f5;
        if (mFirstLoad) {
            f2 = f;
            f3 = f1;
            float f6 = (float) (0.14999999999999999D * (double) ((float) Math
                    .max(mDisplayWidth, mDisplayHeight) / (float) Math.max(
                    mWidth, mHeight)));
            f5 = f6;
            f4 = f6;
            mFirstLoad = false;
        } else {
            f2 = mCenterX;
            f3 = mCenterY;
            f4 = mScaleX;
            f5 = mScaleY;
            float _tmp = mAngle;
        }
        setPos(f2, f3, f4, f5, mAngle);
    }

    public void unload() {
        mDrawable = null;
    }
}
