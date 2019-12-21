package com.makarsankranti.photoframe2020.Activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.makarsankranti.photoframe2020.Constant;
import com.makarsankranti.photoframe2020.R;
import com.makarsankranti.photoframe2020.editortools.Photo_SorterView;
import com.makarsankranti.photoframe2020.editortools.SandboxView;
import com.makarsankranti.photoframe2020.widgets.FilterListener;
import com.makarsankranti.photoframe2020.widgets.FilterViewAdapter;
import com.makarsankranti.photoframe2020.widgets.FrameListener;
import com.makarsankranti.photoframe2020.widgets.FrameViewAdapter;
import com.makarsankranti.photoframe2020.widgets.PhotoFilter;
import com.makarsankranti.photoframe2020.widgets.StickerBSFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class EditActivity extends AppCompatActivity implements FilterListener, FrameListener, StickerBSFragment.StickerListener {

    public static final int REQUEST_GALLERY = 1;
    public static final int REQUEST_CROP_IMAGE = 2;
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    public static String pass_done;
    Animation in, out;
    LinearLayout showbutton, save2;
    RelativeLayout rlmain, savelayout, save1;
    ImageView image;
    SandboxView view;
    Bitmap bitmap;
    Button bcolor;
    private float xCoOrdinate, yCoOrdinate;
    int cl = Color.BLUE;
    private RecyclerView mRvFilters, mFrameView;
    EditText editAddText;
    File mFileTemp;
    RelativeLayout relative_edit;
    Constant constantfile;
    Photo_SorterView photosorter;


    int finalHeight;
    private StickerBSFragment mStickerBSFragment;
    AlertDialog alert;
    private FilterViewAdapter mFilterViewAdapter = new FilterViewAdapter(this);
    private FrameViewAdapter mFrameViewAdapter = new FrameViewAdapter(this);
    RadioButton radio_frame, radio_flip, radio_save;
    //RadioButton  radio_sticker, radio_effect;
    private boolean mIsFilterVisible = false;
    private boolean mIsFrameVisible = false;

    public static void copyStream(InputStream input, OutputStream output)
            throws IOException {

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    @SuppressLint("NewApi")
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity_frame);

        constantfile = new Constant();

        try {
            relative_edit = (RelativeLayout) findViewById(R.id.relative_edit);
            savelayout = (RelativeLayout) findViewById(R.id.savelayout123);

            photosorter = (Photo_SorterView) findViewById(R.id.photosortr123);

            rlmain = (RelativeLayout) findViewById(R.id.rlchange3454);
            image = (ImageView) findViewById(R.id.image);

            mRvFilters = (RecyclerView) findViewById(R.id.rvFilterView);

            LinearLayoutManager llmFilters = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            mRvFilters.setLayoutManager(llmFilters);
            mRvFilters.setAdapter(mFilterViewAdapter);

            mFrameView = (RecyclerView) findViewById(R.id.rvFrameView);
            LinearLayoutManager llmFrame = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            mFrameView.setLayoutManager(llmFrame);
            mFrameView.setAdapter(mFrameViewAdapter);

            LoadFrame(true);

            rlmain.removeAllViews();
            view = new SandboxView(EditActivity.this, Constant.bmp);
            rlmain.addView(view);

            mStickerBSFragment = new StickerBSFragment();
            mStickerBSFragment.setStickerListener(this);
            showFilter(false);
            showFrame(true);

            radio_frame = (RadioButton) findViewById(R.id.radio_frame);
            radio_flip = (RadioButton) findViewById(R.id.radio_flip);
            //radio_sticker = (RadioButton) findViewById(R.id.radio_sticker);
            //radio_effect = (RadioButton) findViewById(R.id.radio_effect);
            radio_save = (RadioButton) findViewById(R.id.radio_save);
            radio_frame.setOnClickListener(radio_listener);
            radio_flip.setOnClickListener(radio_listener);
           // radio_sticker.setOnClickListener(radio_listener);
            //radio_effect.setOnClickListener(radio_listener);
            radio_save.setOnClickListener(radio_listener);

            in = AnimationUtils.loadAnimation(this, R.anim.in);
            out = AnimationUtils.loadAnimation(this, R.anim.out);

        } catch (NullPointerException e) {

        } catch (ArrayIndexOutOfBoundsException e) {

        } catch (ActivityNotFoundException e) {

        } catch (ClassCastException e) {

        } catch (NoClassDefFoundError e) {
            // TODO: handle exception
        } catch (Exception e) {
            // TODO: handle exception
        }
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }

    }

    public void LoadFrame(boolean beginning) {
        Glide.with(EditActivity.this)
                .load(Uri.parse("file:///android_asset/image/" + Constant.arrayofimages.get(Constant.selectedframe)))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        return false; // important to return false so the error placeholder can be placed
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        if (beginning) {
                            finalHeight = resource.getIntrinsicHeight();
                        }
                        savelayout.requestLayout();
                        savelayout.getLayoutParams().height = finalHeight;
                        return false;
                    }
                }).into(image);


    }

    View.OnClickListener radio_listener = new View.OnClickListener() {
        @SuppressLint("NewApi")
        public void onClick(View v) {
            //perform your action here
            showFilter(false);
            showFrame(false);
            if (v == radio_frame) {
                showFrame(true);
            } else if (v == radio_flip) {
                Matrix matrix = new Matrix();
                matrix.preScale(-1.0f, 1.0f);
                Constant.bmp = Bitmap.createBitmap(Constant.bmp, 0, 0, Constant.bmp.getWidth(), Constant.bmp.getHeight(), matrix, true);
                //mPhotoEditorView.getSource().setImageBitmap(Constant.bmp);       *****************************
                rlmain.removeAllViews();
                view = new SandboxView(EditActivity.this, Constant.bmp);
                rlmain.addView(view);

//            } else if (v == radio_sticker) {
//                mStickerBSFragment.show(getSupportFragmentManager(), mStickerBSFragment.getTag());
//            } else if (v == radio_effect) {
//                showFilter(true);
            } else if (v == radio_save) {
                saveImage();
            }
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void showFilter(boolean isVisible) {
        if (isVisible) {
            mRvFilters.setVisibility(View.VISIBLE);
        } else {
            mRvFilters.setVisibility(View.GONE);
        }
        mIsFilterVisible = isVisible;


    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void showFrame(boolean isVisible) {
        if (isVisible) {
            mFrameView.setVisibility(View.VISIBLE);
        } else {
            mFrameView.setVisibility(View.GONE);
        }
        mIsFrameVisible = isVisible;

    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }


    private void saveimg() {

        savelayout.setDrawingCacheEnabled(true);
        try {
            Bitmap bitmap = savelayout.getDrawingCache();
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/" + Constant.downloadDirectory);
            myDir.mkdirs();
            File file = new File(myDir, Constant.downloadDirectory
                    + System.currentTimeMillis() + ".jpg");

            Toast.makeText(getApplicationContext(), "image Downloaded",
                    Toast.LENGTH_SHORT).show();

            if (file.exists())
                file.delete();
            try {
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileOutputStream ostream = new FileOutputStream(file);
                bitmap.compress(CompressFormat.PNG, 10, ostream);
                ostream.close();
                savelayout.setDrawingCacheEnabled(false);
            } catch (Exception e) {

                e.printStackTrace();
            }
        } catch (NullPointerException e) {

        } catch (ArrayIndexOutOfBoundsException e) {

        } catch (ClassCastException e) {

        } catch (ActivityNotFoundException e) {

        } catch (RuntimeException e) {

        } catch (OutOfMemoryError e) {

        }

    }

    @SuppressLint("MissingPermission")
    private void saveImage() {
        savelayout.setDrawingCacheEnabled(true);
//        savelayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
//        savelayout.layout(0, 0, savelayout.getMeasuredWidth(), savelayout.getMeasuredHeight());
        savelayout.buildDrawingCache(true);
        Constant.getfinalimage = null;
        Constant.getfinalimage = Bitmap.createBitmap(savelayout.getDrawingCache(true));
        if (Constant.getfinalimage != null){
            savelayout.setDrawingCacheEnabled(false);
            Intent intent = new Intent(EditActivity.this,Final_Image_Activity.class);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else{
            savelayout.setDrawingCacheEnabled(false);
            constantfile.snackbarcommonrelative(EditActivity.this,relative_edit,"Please Try Again!");
        }

    }


    private void shareimg() {
        String filepath = null;

        savelayout.setDrawingCacheEnabled(true);

        Bitmap bitmap = savelayout.getDrawingCache();
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/" + Constant.downloadDirectory);
        myDir.mkdirs();
        File file = new File(myDir, Constant.downloadDirectory + ".jpg");
        filepath = file.getPath();

        if (file.exists())
            file.delete();
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream ostream = new FileOutputStream(file);
            bitmap.compress(CompressFormat.PNG, 10, ostream);
            ostream.close();
            savelayout.setDrawingCacheEnabled(false);
        } catch (Exception e) {

            e.printStackTrace();
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        Uri screenshotUri = Uri.parse(filepath);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_TEXT, "Profile Photo Frame Create By : "
                + getPackageName() + getApplicationContext().getPackageName());
        intent.putExtra(Intent.EXTRA_STREAM, screenshotUri);

        Intent openInChooser = new Intent(intent);
        openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                "Share image using");
        startActivity(openInChooser);

    }


    public void AddText(String srt, int pos2) {
//        Typeface typ = Typeface.createFromAsset(EditActivity.this.getAssets(),
//                font[pos2]);
//        Paint paint = new Paint();
//        paint.setTextSize(100);
//        paint.setTypeface(typ);
//        paint.setTextAlign(Paint.Align.LEFT);
//        paint.setColor(cl);
//        Log.e("textget",srt);
//        int width = (int) (paint.measureText(srt) + 0.5f); // round
//        float baseline = (int) (-paint.ascent() + 0.5f); // ascent() is negative
//        int height = (int) (baseline + paint.descent() + 50f);
//        Bitmap image = Bitmap.createBitmap(width, height,
//                Bitmap.Config.ARGB_8888);
//        Canvas canvas = new Canvas(image);
//        canvas.drawText(srt, 0, baseline, paint);
//        Drawable d = new BitmapDrawable(getResources(), image);
//        photosorter.loadText(EditActivity.this, d);
//        photosorter.bringToFront();
    }


    @SuppressLint("NewApi")
    @Override
    public void onBackPressed() {
        if (mIsFilterVisible) {
            showFilter(false);
        } else if (mIsFrameVisible) {
            showFrame(false);
        } else {
            AppExit();
        }
    }

    public void AppExit() {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(getResources().getString(R.string.exit_editimage_title));
        alertDialogBuilder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        alert.dismiss();
                        Intent intent = new Intent(EditActivity.this, MainActivity.class);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                    }
                });

        alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });
        alert = alertDialogBuilder.create();
        alert.show();

    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }

    @Override
    public void onFilterSelected(PhotoFilter photoFilter) {
            //view.setFilterEffect(photoFilter);
    }

    @Override
    public void onFrameSelected(int position) {
        Constant.selectedframe = position;
        LoadFrame(true);
    }

    @Override
    public void onStickerClick(Bitmap bitmap, int position) {
//        Drawable d = new BitmapDrawable(getResources(), bitmap);
//        if (photosorter.getVisibility() == View.GONE) {
//            photosorter.setVisibility(View.VISIBLE);
//        }
//        photosorter.loadText(EditActivity.this, d);
//        photosorter.bringToFront();

    }
}
