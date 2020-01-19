package com.independencedayphotoframes.anddpmaker.widgets;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.DisplayMetrics;

import java.io.Serializable;

public abstract class Multi_Touch_Entity implements Serializable {

    protected static final int GRAB_AREA_SIZE = 40;
    private static final int UI_MODE_ANISOTROPIC_SCALE = 2;
    private static final int UI_MODE_ROTATE = 1;
    protected float mAngle;
    protected float mCenterX;
    protected float mCenterY;
    protected int mDisplayHeight;
    protected int mDisplayWidth;
    protected boolean mFirstLoad;
    protected float mGrabAreaX1;
    protected float mGrabAreaX2;
    protected float mGrabAreaY1;
    protected float mGrabAreaY2;
    protected int mHeight;
    protected boolean mIsGrabAreaSelected;
    protected boolean mIsLatestSelected;
    protected float mMaxX;
    protected float mMaxY;
    protected float mMinX;
    protected float mMinY;
    protected transient Paint mPaint;
    protected float mScaleX;
    protected float mScaleY;
    protected float mStartMidX;
    protected float mStartMidY;
    protected int mUIMode;
    protected int mWidth;

    public Multi_Touch_Entity() {
        mFirstLoad = true;
        mPaint = new Paint();
        mIsGrabAreaSelected = false;
        mIsLatestSelected = false;
        mUIMode = 1;
    }

    public Multi_Touch_Entity(Resources resources) {
        mFirstLoad = true;
        mPaint = new Paint();
        mIsGrabAreaSelected = false;
        mIsLatestSelected = false;
        mUIMode = 1;
        getMetrics(resources);
    }

    public boolean containsPoint(float f, float f1) {
        return f >= mMinX && f <= mMaxX && f1 >= mMinY && f1 <= mMaxY;
    }

    public abstract void draw(Canvas canvas);

    public float getAngle() {
        return mAngle;
    }

    public float getCenterX() {
        return mCenterX;
    }

    public float getCenterY() {
        return mCenterY;
    }

    public int getHeight() {
        return mHeight;
    }

    public float getMaxX() {
        return mMaxX;
    }

    public float getMaxY() {
        return mMaxY;
    }

    protected void getMetrics(Resources resources) {
        DisplayMetrics displaymetrics = resources.getDisplayMetrics();
        int i;
        int j;
        if (resources.getConfiguration().orientation == 2) {
            i = Math.max(displaymetrics.widthPixels,
                    displaymetrics.heightPixels);
        } else {
            i = Math.min(displaymetrics.widthPixels,
                    displaymetrics.heightPixels);
        }
        mDisplayWidth = i;
        if (resources.getConfiguration().orientation == 2) {
            j = Math.min(displaymetrics.widthPixels,
                    displaymetrics.heightPixels);
        } else {
            j = Math.max(displaymetrics.widthPixels,
                    displaymetrics.heightPixels);
        }
        mDisplayHeight = j;
    }

    public float getMinX() {
        return mMinX;
    }

    public float getMinY() {
        return mMinY;
    }

    public float getScaleX() {
        return mScaleX;
    }

    public float getScaleY() {
        return mScaleY;
    }

    public int getWidth() {
        return mWidth;
    }

    public boolean grabAreaContainsPoint(float f, float f1) {
        return f >= mGrabAreaX1 && f <= mGrabAreaX2 && f1 >= mGrabAreaY1
                && f1 <= mGrabAreaY2;
    }

    public boolean isGrabAreaSelected() {
        return mIsGrabAreaSelected;
    }

    public abstract void load(Context context, float f, float f1);

    public void reload(Context context) {
        mFirstLoad = false;
        load(context, mCenterX, mCenterY);
    }

    public void setIsGrabAreaSelected(boolean flag) {
        mIsGrabAreaSelected = flag;
    }

    protected boolean setPos(float f, float f1, float f2, float f3, float f4) {
        float f5 = f2 * (float) (mWidth / 2);
        float f6 = f3 * (float) (mHeight / 2);
        mMinX = f - f5;
        mMinY = f1 - f6;
        mMaxX = f + f5;
        mMaxY = f1 + f6;
        mGrabAreaX1 = mMaxX - 40F;
        mGrabAreaY1 = mMaxY - 40F;
        mGrabAreaX2 = mMaxX;
        mGrabAreaY2 = mMaxY;
        mCenterX = f;
        mCenterY = f1;
        mScaleX = f2;
        mScaleY = f3;
        mAngle = f4;
        return true;
    }

    public boolean setPos(Multi_Touch_Controller.PositionAndScale positionandscale) {
        float f;
        float f1;
        if ((2 & mUIMode) != 0) {
            f = positionandscale.getScaleX();
        } else {
            f = positionandscale.getScale();
        }
        if ((2 & mUIMode) != 0) {
            f1 = positionandscale.getScaleY();
        } else {
            f1 = positionandscale.getScale();
        }
        return setPos(positionandscale.getXOff(), positionandscale.getYOff(),
                f, f1, positionandscale.getAngle());
    }

    public abstract void unload();
}
