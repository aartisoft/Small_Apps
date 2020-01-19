package com.independencedayphotoframes.anddpmaker.editortools;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;


public class SandboxView extends View implements OnTouchListener {

    private final Bitmap bitmap;
    private final int width;
    private final int height;
    ColorMatrix newMatrix;
    int progressB;
    int progressC;
    ColorMatrixColorFilter filtr;
    private Matrix transform = new Matrix();
    private Vector2D position = new Vector2D();
    private float scale = 1;
    private float angle = 0;
    private TouchManager touchManager = new TouchManager(2);
    private boolean isInitialized = false;

    // Debug helpers to draw lines between the two touch points
    private Vector2D vca = null;
    private Vector2D vcb = null;
    private Vector2D vpa = null;
    private Vector2D vpb = null;

    public SandboxView(Context context, Bitmap bitmap) {
        super(context);

        this.bitmap = bitmap;
        this.width = bitmap.getWidth();
        this.height = bitmap.getHeight();

        setOnTouchListener(this);
        newMatrix = new ColorMatrix();
        filtr = new ColorMatrixColorFilter(newMatrix);
    }

    private static float getDegreesFromRadians(float angle) {
        return (float) (angle * 180.0 / Math.PI);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isInitialized) {
            int w = getWidth();
            int h = getHeight();
            position.set(w / 2, h / 2);
            isInitialized = true;
        }

        float f = (float) (259 * (255 + progressC))
                / (float) (255 * (259 - progressC));
        float af[] = new float[20];

        af[0] = f;
        af[1] = 0.0F;
        af[2] = 0.0F;
        af[3] = 0.0F;
        af[4] = 128F + -128F * f + (float) progressB;
        af[5] = 0.0F;
        af[6] = f;
        af[7] = 0.0F;
        af[8] = 0.0F;
        af[9] = 128F + -128F * f + (float) progressB;
        af[10] = 0.0F;
        af[11] = 0.0F;
        af[12] = f;
        af[13] = 0.0F;
        af[14] = 128F + -128F * f + (float) progressB;
        af[15] = 0.0F;
        af[16] = 0.0F;
        af[17] = 0.0F;
        af[18] = 1.0F;
        af[19] = 0.0F;
        int w = getWidth();
        newMatrix.set(af);
        filtr = new ColorMatrixColorFilter(newMatrix);

        Paint paint = new Paint();

        transform.reset();
        transform.postTranslate(-width / 2.0f, -height / 2.0f);
        transform.postRotate(getDegreesFromRadians(angle));
        transform.postScale(scale, scale);
        transform.postTranslate(position.getX(), position.getY());
        paint.setColorFilter(filtr);
        //Bitmap bitmap1 = getCroppedBitmap(bitmap,w);
        canvas.drawBitmap(bitmap, transform, paint);

        try {
            paint.setColor(0xFF007F00);
            // canvas.drawCircle(vca.getX(), vca.getY(), 64, paint);
            paint.setColor(0xFF7F0000);
            // canvas.drawCircle(vcb.getX(), vcb.getY(), 64, paint);

            paint.setColor(0xFFFF0000);
            // canvas.drawLine(vpa.getX(), vpa.getY(), vpb.getX(), vpb.getY(),
            // paint);
            paint.setColor(0xFF00FF00);
            // canvas.drawLine(vca.getX(), vca.getY(), vcb.getX(), vcb.getY(),
            // paint);
        } catch (NullPointerException e) {
            // Just being lazy here...
        }
    }

    public boolean onTouch(View v, MotionEvent event) {
        vca = null;
        vcb = null;
        vpa = null;
        vpb = null;

        try {
            touchManager.update(event);

            if (touchManager.getPressCount() == 1) {
                vca = touchManager.getPoint(0);
                vpa = touchManager.getPreviousPoint(0);
                position.add(touchManager.moveDelta(0));
            } else {
                if (touchManager.getPressCount() == 2) {
                    vca = touchManager.getPoint(0);
                    vpa = touchManager.getPreviousPoint(0);
                    vcb = touchManager.getPoint(1);
                    vpb = touchManager.getPreviousPoint(1);

                    Vector2D current = touchManager.getVector(0, 1);
                    Vector2D previous = touchManager.getPreviousVector(0, 1);
                    float currentDistance = current.getLength();
                    float previousDistance = previous.getLength();

                    if (previousDistance != 0) {
                        scale *= currentDistance / previousDistance;
                    }

                    angle -= Vector2D.getSignedAngleBetween(current, previous);
                }
            }

            invalidate();
        } catch (Throwable t) {
            // So lazy...
        }
        return true;
    }

    public void setBrightProgress(int i) {
        progressB = i;
        invalidate();
    }

    public void setContrastProgress(int i) {
        progressC = i;
        invalidate();
    }


    public static Bitmap getCroppedBitmap(Bitmap bmp, int radius) {
        Bitmap sbmp;

        if (bmp.getWidth() != radius || bmp.getHeight() != radius) {
            float smallest = Math.min(bmp.getWidth(), bmp.getHeight());
            float factor = smallest / radius;
            sbmp = Bitmap.createScaledBitmap(bmp,
                    (int) (bmp.getWidth() / factor),
                    (int) (bmp.getHeight() / factor), false);
        } else {
            sbmp = bmp;
        }

        Bitmap output = Bitmap.createBitmap(radius, radius, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final String color = "#BAB399";
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, radius, radius);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(Color.parseColor(color));
        canvas.drawCircle(radius / 2 + 0.7f, radius / 2 + 0.7f,
                radius / 2 + 0.1f, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(sbmp, rect, rect, paint);

        return output;
    }

}
