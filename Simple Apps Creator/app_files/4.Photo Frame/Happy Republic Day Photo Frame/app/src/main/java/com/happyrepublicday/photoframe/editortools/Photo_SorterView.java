package com.happyrepublicday.photoframe.editortools;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.happyrepublicday.photoframe.widgets.Image_Entity;
import com.happyrepublicday.photoframe.widgets.Multi_Touch_Controller;
import com.happyrepublicday.photoframe.widgets.Multi_Touch_Entity;

import java.util.ArrayList;

public class Photo_SorterView extends View implements Multi_Touch_Controller.MultiTouchObjectCanvas {

    private static final float SCREEN_MARGIN = 100F;
    private static final int UI_MODE_ANISOTROPIC_SCALE = 2;
    private static final int UI_MODE_ROTATE = 1;
    Context cntx;
    private Multi_Touch_Controller.PointInfo currTouchPoint;
    private int displayHeight;
    private int displayWidth;
    private ArrayList mImages;
    private int mUIMode;
    private Multi_Touch_Controller multiTouchController;

    public Photo_SorterView(Context context) {
        this(context, null);
    }

    public Photo_SorterView(Context context, AttributeSet attributeset) {
        this(context, attributeset, 0);
    }

    public Photo_SorterView(Context context, AttributeSet attributeset, int i) {
        super(context, attributeset, i);
        mImages = new ArrayList();
        multiTouchController = new Multi_Touch_Controller(this);
        currTouchPoint = new Multi_Touch_Controller.PointInfo();
        mUIMode = 1;
        init(context);
    }

    private void init(Context context) {
        cntx = context;
        Resources resources = context.getResources();
        setBackgroundColor(0);
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
        displayWidth = i;
        if (resources.getConfiguration().orientation == 2) {
            j = Math.min(displaymetrics.widthPixels,
                    displaymetrics.heightPixels);
        } else {
            j = Math.max(displaymetrics.widthPixels,
                    displaymetrics.heightPixels);
        }
        displayHeight = j;
    }

    public Multi_Touch_Entity getDraggableObjectAtPoint(
            Multi_Touch_Controller.PointInfo pt) {

        float x = pt.getX(), y = pt.getY();
        int n = mImages.size();
        for (int i = n - 1; i >= 0; i--) {
            Image_Entity im = (Image_Entity) mImages.get(i);
            if (im.containsPoint(x, y))
                return im;

        }

        return null;
    }

    public void getPositionAndScale(Object obj,
                                    Multi_Touch_Controller.PositionAndScale positionandscale) {
        getPositionAndScale((Multi_Touch_Entity) obj, positionandscale);
    }

    public void getPositionAndScale(Multi_Touch_Entity multitouchentity,
                                    Multi_Touch_Controller.PositionAndScale positionandscale) {
        float f = multitouchentity.getCenterX();
        float f1 = multitouchentity.getCenterY();
        boolean flag;
        float f2;
        boolean flag1;
        float f3;
        float f4;
        int i;
        boolean flag2;
        if ((2 & mUIMode) == 0) {
            flag = true;
        } else {
            flag = false;
        }
        f2 = (multitouchentity.getScaleX() + multitouchentity.getScaleY()) / 2.0F;
        if ((2 & mUIMode) != 0) {
            flag1 = true;
        } else {
            flag1 = false;
        }
        f3 = multitouchentity.getScaleX();
        f4 = multitouchentity.getScaleY();
        i = 1 & mUIMode;
        flag2 = false;
        if (i != 0) {
            flag2 = true;
        }
        positionandscale.set(f, f1, flag, f2, flag1, f3, f4, flag2,
                multitouchentity.getAngle());
    }

    public void loadImages1(Context context, int i) {
        Resources resources = context.getResources();
        mImages.add(new Image_Entity(i, resources));
        float f = 100F + (float) (Math.random() * (double) ((float) displayWidth - 200F));
        float f1 = 100F + (float) (Math.random() * (double) ((float) displayHeight - 200F));
        ((Multi_Touch_Entity) mImages.get(-1 + mImages.size())).load(context, f, f1);
        invalidate();
    }

    public void loadText(Context context, Drawable drawable) {
        Resources resources = context.getResources();
        mImages.add(new Image_Entity(drawable, resources));
        float f = 100F + (float) (Math.random() * (double) ((float) displayWidth - 200F));
        float f1 = 100F + (float) (Math.random() * (double) ((float) displayHeight - 200F));
        ((Multi_Touch_Entity) mImages.get(-1 + mImages.size())).load(context, f, f1);
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int i = mImages.size();
        int j = 0;
        do {
            if (j >= i) {
                return;
            }
            ((Multi_Touch_Entity) mImages.get(j)).draw(canvas);
            j++;
        } while (true);
    }

    public boolean onTouchEvent(MotionEvent motionevent) {
        return multiTouchController.onTouchEvent(motionevent);
    }

    public boolean pointInObjectGrabArea(
            Multi_Touch_Controller.PointInfo pointinfo, Object obj) {
        return pointInObjectGrabArea(pointinfo, (Multi_Touch_Entity) obj);
    }

    public boolean pointInObjectGrabArea(
            Multi_Touch_Controller.PointInfo pointinfo,
            Multi_Touch_Entity multitouchentity) {
        return false;
    }

    public void removeImages1() {
        try {
            int i = -1 + mImages.size();
            mImages.remove(i);
            invalidate();
            return;
        } catch (Exception exception) {
            return;
        }
    }

    public void selectObject(Object obj,
                             Multi_Touch_Controller.PointInfo pointinfo) {
        selectObject((Multi_Touch_Entity) obj, pointinfo);
    }

    public void selectObject(Multi_Touch_Entity multitouchentity,
                             Multi_Touch_Controller.PointInfo pointinfo) {
        currTouchPoint.set(pointinfo);
        if (multitouchentity != null) {
            mImages.remove(multitouchentity);
            mImages.add(multitouchentity);
        }
        invalidate();
    }

    public boolean setPositionAndScale(Object obj,
                                       Multi_Touch_Controller.PositionAndScale positionandscale,
                                       Multi_Touch_Controller.PointInfo pointinfo) {
        return setPositionAndScale((Multi_Touch_Entity) obj, positionandscale,
                pointinfo);
    }

    public boolean setPositionAndScale(Multi_Touch_Entity multitouchentity,
                                       Multi_Touch_Controller.PositionAndScale positionandscale,
                                       Multi_Touch_Controller.PointInfo pointinfo) {
        currTouchPoint.set(pointinfo);
        boolean flag = ((Image_Entity) multitouchentity)
                .setPos(positionandscale);
        if (flag) {
            invalidate();
        }
        return flag;
    }

    public void trackballClicked() {
        mUIMode = (1 + mUIMode) % 3;
        invalidate();
    }
}
