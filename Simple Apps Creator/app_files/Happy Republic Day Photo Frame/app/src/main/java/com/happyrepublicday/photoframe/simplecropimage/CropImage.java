package com.happyrepublicday.photoframe.simplecropimage;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.media.FaceDetector;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StatFs;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.happyrepublicday.photoframe.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.CountDownLatch;


public class CropImage extends MonitoredActivity {

    // public static final String IMAGE_PATH = "image-path";
    public static final String SCALE = "scale";
    public static final String ORIENTATION_IN_DEGREES = "orientation_in_degrees";
    public static final String ASPECT_X = "aspectX";
    public static final String ASPECT_Y = "aspectY";
    public static final String OUTPUT_X = "outputX";
    public static final String OUTPUT_Y = "outputY";
    public static final String SCALE_UP_IF_NEEDED = "scaleUpIfNeeded";
    public static final String CIRCLE_CROP = "circleCrop";
    public static final String RETURN_DATA = "return-data";
    public static final String RETURN_DATA_AS_BITMAP = "data";
    public static final String ACTION_INLINE_DATA = "inline-data";
    public static final int NO_STORAGE_ERROR = -1;
    public static final int CANNOT_STAT_ERROR = -2;
    private static final String TAG = "CropImage";
    final int IMAGE_MAX_SIZE = 1024;
    private final Handler mHandler = new Handler();
    private final BitmapManager.ThreadSet mDecodingThreads = new BitmapManager.ThreadSet();
    boolean mWaitingToPick;
    boolean mSaving;
    HighlightView mCrop;
    RelativeLayout layout, layout1;
    // These are various options can be specified in the intent.
    private Bitmap.CompressFormat mOutputFormat = Bitmap.CompressFormat.JPEG;
    private Uri mSaveUri = null;
    private boolean mDoFaceDetection = true;
    private boolean mCircleCrop = false;
    private int mAspectX;
    private int mAspectY;
    private int mOutputX;
    private int mOutputY;
    private boolean mScale;
    private CropImageView mImageView;
    private ContentResolver mContentResolver;
    private Bitmap mBitmap;
//	AdRequest adRequest;
//	private InterstitialAd interstitial;
//	AdRequest adRequest1;
    Runnable mRunFaceDetection = new Runnable() {
        @SuppressWarnings("hiding")
        float mScale = 1F;
        Matrix mImageMatrix;
        FaceDetector.Face[] mFaces = new FaceDetector.Face[3];
        int mNumFaces;

        // For each face, we create a HightlightView for it.
        private void handleFace(FaceDetector.Face f) {

            PointF midPoint = new PointF();

            int r = ((int) (f.eyesDistance() * mScale)) * 2;
            f.getMidPoint(midPoint);
            midPoint.x *= mScale;
            midPoint.y *= mScale;

            int midX = (int) midPoint.x;
            int midY = (int) midPoint.y;

            HighlightView hv = new HighlightView(mImageView);

            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();

            Rect imageRect = new Rect(0, 0, width, height);

            RectF faceRect = new RectF(midX, midY, midX, midY);
            faceRect.inset(-r, -r);
            if (faceRect.left < 0) {
                faceRect.inset(-faceRect.left, -faceRect.left);
            }

            if (faceRect.top < 0) {
                faceRect.inset(-faceRect.top, -faceRect.top);
            }

            if (faceRect.right > imageRect.right) {
                faceRect.inset(faceRect.right - imageRect.right, faceRect.right
                        - imageRect.right);
            }

            if (faceRect.bottom > imageRect.bottom) {
                faceRect.inset(faceRect.bottom - imageRect.bottom,
                        faceRect.bottom - imageRect.bottom);
            }

            hv.setup(mImageMatrix, imageRect, faceRect, mCircleCrop,
                    mAspectX != 0 && mAspectY != 0);

            mImageView.add(hv);
        }

        // Create a default HightlightView if we found no face in the picture.
        private void makeDefault() {

            HighlightView hv = new HighlightView(mImageView);

            int width = mBitmap.getWidth();
            int height = mBitmap.getHeight();

            Rect imageRect = new Rect(0, 0, width, height);

            // make the default size about 4/5 of the width or height
            int cropWidth = Math.min(width, height) * 4 / 5;
            int cropHeight = cropWidth;

            if (mAspectX != 0 && mAspectY != 0) {

                if (mAspectX > mAspectY) {

                    cropHeight = cropWidth * mAspectY / mAspectX;
                } else {

                    cropWidth = cropHeight * mAspectX / mAspectY;
                }
            }

            int x = (width - cropWidth) / 2;
            int y = (height - cropHeight) / 2;

            RectF cropRect = new RectF(x, y, x + cropWidth, y + cropHeight);
            hv.setup(mImageMatrix, imageRect, cropRect, mCircleCrop, mAspectX != 0 && mAspectY != 0);

            mImageView.mHighlightViews.clear(); // Thong added for rotate

            mImageView.add(hv);
        }

        // Scale the image down for faster face detection.
        private Bitmap prepareBitmap() {

            if (mBitmap == null) {

                return null;
            }

            // 256 pixels wide is enough.
            if (mBitmap.getWidth() > 256) {

                mScale = 256.0F / mBitmap.getWidth();
            }
            Matrix matrix = new Matrix();
            matrix.setScale(mScale, mScale);
            return Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
                    mBitmap.getHeight(), matrix, true);
        }

        public void run() {

            mImageMatrix = mImageView.getImageMatrix();
            Bitmap faceBitmap = prepareBitmap();

            mScale = 1.0F / mScale;
            if (faceBitmap != null && mDoFaceDetection) {
                FaceDetector detector = new FaceDetector(faceBitmap.getWidth(),
                        faceBitmap.getHeight(), mFaces.length);
                mNumFaces = detector.findFaces(faceBitmap, mFaces);
            }

            if (faceBitmap != null && faceBitmap != mBitmap) {
                faceBitmap.recycle();
            }

            mHandler.post(new Runnable() {
                public void run() {

                    mWaitingToPick = mNumFaces > 1;
                    if (mNumFaces > 0) {
                        for (int i = 0; i < mNumFaces; i++) {
                            handleFace(mFaces[i]);
                        }
                    } else {
                        makeDefault();
                    }
                    mImageView.invalidate();
                    if (mImageView.mHighlightViews.size() == 1) {
                        mCrop = mImageView.mHighlightViews.get(0);
                        mCrop.setFocus(true);
                    }

                    if (mNumFaces > 1) {
                        Toast.makeText(CropImage.this, "Multi face crop help",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    };
    private String mImagePath;
    private boolean mScaleUp = true;

    public static void showStorageToast(Activity activity) {

        showStorageToast(activity, calculatePicturesRemaining(activity));
    }

    public static void showStorageToast(Activity activity, int remaining) {

        String noStorageText = null;

        if (remaining == NO_STORAGE_ERROR) {

            String state = Environment.getExternalStorageState();
            if (state.equals(Environment.MEDIA_CHECKING)) {

                noStorageText = activity.getString(R.string.preparing_card);
            } else {

                noStorageText = activity.getString(R.string.no_storage_card);
            }
        } else if (remaining < 1) {

            noStorageText = activity.getString(R.string.not_enough_space);
        }

        if (noStorageText != null) {

            Toast.makeText(activity, noStorageText, Toast.LENGTH_SHORT).show();
        }
    }

    public static int calculatePicturesRemaining(Activity activity) {

        try {
			/*
			 * if (!ImageManager.hasStorage()) { return NO_STORAGE_ERROR; } else
			 * {
			 */
            String storageDirectory = "";
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                storageDirectory = Environment.getExternalStorageDirectory().toString();
            } else {
                storageDirectory = activity.getFilesDir().toString();
            }
            StatFs stat = new StatFs(storageDirectory);
            float remaining = ((float) stat.getAvailableBlocks() * (float) stat.getBlockSize()) / 400000F;
            return (int) remaining;
            // }
        } catch (Exception ex) {
            // if we can't stat the filesystem then we don't know how many
            // pictures are remaining. it might be zero but just leave it
            // blank since we really don't know.
            return CANNOT_STAT_ERROR;
        }
    }

    @SuppressLint("NewApi")
    @Override
    public void onCreate(Bundle icicle) {

        super.onCreate(icicle);
        mContentResolver = getContentResolver();

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.cropimage);

//		try {
//
//			layout = (RelativeLayout) findViewById(R.id.adView);
//			adRequest = new AdRequest.Builder().build();
//			adRequest1 = new AdRequest.Builder().build();
//
//			interstitial = new InterstitialAd(this);
//			interstitial.setAdUnitId(Glob.full);
//
//			interstitial.loadAd(adRequest);
//
//			interstitial.setAdListener(new AdListener() {
//				@Override
//				public void onAdLoaded() {
//					if (interstitial.isLoaded()) {
//						interstitial.show();
//					}
//				}
//
//				@Override
//				public void onAdFailedToLoad(int errorCode) {
//				}
//			});
//		} catch (Exception e) {
//		}
//
//		if (isOnline()) {
//
//			layout.setVisibility(View.VISIBLE);
//			try {
//				AdView adView = new AdView(this);
//				adView.setAdSize(AdSize.SMART_BANNER);
//				adView.setAdUnitId(Glob.banner);
//				layout.addView(adView);
//
//				adView.loadAd(adRequest);
//			} catch (Exception e) {
//
//			}
//		} else {
//			layout.setVisibility(View.GONE);
//		}

        mImageView = (CropImageView) findViewById(R.id.image);

        showStorageToast(this);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {

            if (extras.getString(CIRCLE_CROP) != null) {

                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
                    mImageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                }

                mCircleCrop = true;
                mAspectX = 1;
                mAspectY = 1;
            }

            mImagePath = extras.getString("image-path");

            mSaveUri = getImageUri(mImagePath);
            mBitmap = getBitmap(mImagePath);

            if (extras.containsKey(ASPECT_X) && extras.get(ASPECT_X) instanceof Integer) {

                mAspectX = extras.getInt(ASPECT_X);
            } else {

                throw new IllegalArgumentException("aspect_x must be integer");
            }
            if (extras.containsKey(ASPECT_Y)
                    && extras.get(ASPECT_Y) instanceof Integer) {

                mAspectY = extras.getInt(ASPECT_Y);
            } else {

                throw new IllegalArgumentException("aspect_y must be integer");
            }
            mOutputX = extras.getInt(OUTPUT_X);
            mOutputY = extras.getInt(OUTPUT_Y);
            mScale = extras.getBoolean(SCALE, true);
            mScaleUp = extras.getBoolean(SCALE_UP_IF_NEEDED, true);
        }

        if (mBitmap == null) {

            Log.d(TAG, "finish!!!");
            finish();
            return;
        }

        // Make UI fullscreen.
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findViewById(R.id.backcrop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

//        findViewById(R.id.crop).setOnClickListener(
//                new View.OnClickListener() {
//                    public void onClick(View v) {
//
//                        setResult(RESULT_CANCELED);
//                        finish();
//                    }
//                });

        findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                try {
                    onSaveClicked();
                    //overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
                } catch (Exception e) {
                    finish();
                }
            }
        });
        findViewById(R.id.rotate).setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {

                        mBitmap = Util.rotateImage(mBitmap, -90);
                        RotateBitmap rotateBitmap = new RotateBitmap(mBitmap);
                        mImageView.setImageRotateBitmapResetBase(rotateBitmap,
                                true);
                        mRunFaceDetection.run();
                    }
                });

//        findViewById(R.id.rotateRight).setOnClickListener(
//                new View.OnClickListener() {
//                    public void onClick(View v) {
//
//                        mBitmap = Util.rotateImage(mBitmap, 90);
//                        RotateBitmap rotateBitmap = new RotateBitmap(mBitmap);
//                        mImageView.setImageRotateBitmapResetBase(rotateBitmap,
//                                true);
//                        mRunFaceDetection.run();
//                    }
//                });

        startFaceDetection();
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private Uri getImageUri(String path) {

        return Uri.fromFile(new File(path));
    }

    private Bitmap getBitmap(String path) {

        Uri uri = getImageUri(path);
        InputStream in = null;
        try {
            in = mContentResolver.openInputStream(uri);

            // Decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;

            BitmapFactory.decodeStream(in, null, o);
            in.close();

            int scale = 1;
            if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_SIZE) {
                scale = (int) Math.pow(
                        2,
                        (int) Math.round(Math.log(IMAGE_MAX_SIZE
                                / (double) Math.max(o.outHeight, o.outWidth))
                                / Math.log(0.5)));
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            in = mContentResolver.openInputStream(uri);
            Bitmap b = BitmapFactory.decodeStream(in, null, o2);
            in.close();

            return b;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "file " + path + " not found");
        } catch (IOException e) {
            Log.e(TAG, "file " + path + " not found");
        }
        return null;
    }

    private void startFaceDetection() {

        if (isFinishing()) {
            return;
        }

        mImageView.setImageBitmapResetBase(mBitmap, true);

        Util.startBackgroundJob(this, null, "Please wait\u2026",
                new Runnable() {
                    public void run() {

                        final CountDownLatch latch = new CountDownLatch(1);
                        final Bitmap b = mBitmap;
                        mHandler.post(new Runnable() {
                            public void run() {

                                if (b != mBitmap && b != null) {
                                    mImageView.setImageBitmapResetBase(b, true);
                                    mBitmap.recycle();
                                    mBitmap = b;
                                }
                                if (mImageView.getScale() == 1F) {
                                    mImageView.center(true, true);
                                }
                                latch.countDown();
                            }
                        });
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                        mRunFaceDetection.run();
                    }
                }, mHandler);
    }

    private void onSaveClicked() throws Exception {
        // TODO this code needs to change to use the decode/crop/encode single
        // step api so that we don't require that the whole (possibly large)
        // bitmap doesn't have to be read into memory
        if (mSaving)
            return;

        if (mCrop == null) {

            return;
        }

        mSaving = true;

        Rect r = mCrop.getCropRect();

        int width = r.width();
        int height = r.height();

        // If we are circle cropping, we want alpha channel, which is the
        // third param here.
        Bitmap croppedImage;
        try {

            croppedImage = Bitmap.createBitmap(width, height,
                    mCircleCrop ? Bitmap.Config.ARGB_8888
                            : Bitmap.Config.RGB_565);
        } catch (Exception e) {
            throw e;
        }
        if (croppedImage == null) {

            return;
        }

        {
            Canvas canvas = new Canvas(croppedImage);
            Rect dstRect = new Rect(0, 0, width, height);
            canvas.drawBitmap(mBitmap, r, dstRect, null);
        }

        if (mCircleCrop) {

            // OK, so what's all this about?
            // Bitmaps are inherently rectangular but we want to return
            // something that's basically a circle. So we fill in the
            // area around the circle with alpha. Note the all important
            // PortDuff.Mode.CLEAR.
            Canvas c = new Canvas(croppedImage);
            Path p = new Path();
            p.addCircle(width / 2F, height / 2F, width / 2F, Path.Direction.CW);
            c.clipPath(p, Region.Op.DIFFERENCE);
            c.drawColor(0x00000000, PorterDuff.Mode.CLEAR);
        }

		/* If the output is required to a specific size then scale or fill */
        if (mOutputX != 0 && mOutputY != 0) {

            if (mScale) {

				/* Scale the image to the required dimensions */
                Bitmap old = croppedImage;
                croppedImage = Util.transform(new Matrix(), croppedImage, mOutputX, mOutputY, mScaleUp);
                if (old != croppedImage) {

                    old.recycle();
                }
            } else {

				/*
                 * Don't scale the image crop it to the size requested. Create
				 * an new image with the cropped image in the center and the
				 * extra space filled.
				 */

                // Don't scale the image but instead fill it so it's the
                // required dimension
                Bitmap b = Bitmap.createBitmap(mOutputX, mOutputY,
                        Bitmap.Config.RGB_565);
                Canvas canvas = new Canvas(b);

                Rect srcRect = mCrop.getCropRect();
                Rect dstRect = new Rect(0, 0, mOutputX, mOutputY);

                int dx = (srcRect.width() - dstRect.width()) / 2;
                int dy = (srcRect.height() - dstRect.height()) / 2;

				/* If the srcRect is too big, use the center part of it. */
                srcRect.inset(Math.max(0, dx), Math.max(0, dy));

				/* If the dstRect is too big, use the center part of it. */
                dstRect.inset(Math.max(0, -dx), Math.max(0, -dy));

				/* Draw the cropped bitmap in the center */
                canvas.drawBitmap(mBitmap, srcRect, dstRect, null);

				/* Set the cropped bitmap as the new bitmap */
                croppedImage.recycle();
                croppedImage = b;
            }
        }

        // Return the cropped image directly or save it to the specified URI.
        Bundle myExtras = getIntent().getExtras();
        if (myExtras != null
                && (myExtras.getParcelable("data") != null || myExtras
                .getBoolean(RETURN_DATA))) {

            Bundle extras = new Bundle();
            extras.putParcelable(RETURN_DATA_AS_BITMAP, croppedImage);
            setResult(RESULT_OK, (new Intent()).setAction(ACTION_INLINE_DATA)
                    .putExtras(extras));
            finish();
        } else {
            final Bitmap b = croppedImage;
            Util.startBackgroundJob(this, null,
                    getString(R.string.saving_image), new Runnable() {
                        public void run() {

                            saveOutput(b);
                        }
                    }, mHandler);
        }
    }

    private void saveOutput(Bitmap croppedImage) {

        if (mSaveUri != null) {
            OutputStream outputStream = null;
            try {
                outputStream = mContentResolver.openOutputStream(mSaveUri);
                if (outputStream != null) {
                    croppedImage.compress(mOutputFormat, 90, outputStream);

                }
            } catch (IOException ex) {

                Log.e(TAG, "Cannot open file: " + mSaveUri, ex);
                setResult(RESULT_CANCELED);
                finish();
                return;
            } finally {

                Util.closeSilently(outputStream);
            }

            Bundle extras = new Bundle();
            Intent intent = new Intent(mSaveUri.toString());
            intent.putExtras(extras);
            intent.putExtra("image-path", mImagePath);
            intent.putExtra(ORIENTATION_IN_DEGREES,
                    Util.getOrientationInDegree(this));
            setResult(RESULT_OK, intent);
        } else {

            Log.e(TAG, "not defined image url");
        }
        croppedImage.recycle();
        finish();
    }

    @Override
    protected void onPause() {

        super.onPause();
        BitmapManager.instance().cancelThreadDecoding(mDecodingThreads);
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();

        if (mBitmap != null) {

            mBitmap.recycle();
        }
    }

}
