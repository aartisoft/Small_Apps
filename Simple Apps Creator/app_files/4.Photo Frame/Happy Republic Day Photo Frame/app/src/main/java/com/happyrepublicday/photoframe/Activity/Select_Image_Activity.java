package com.happyrepublicday.photoframe.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import androidx.core.content.FileProvider;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.happyrepublicday.photoframe.ConnectionDetector;
import com.happyrepublicday.photoframe.Constant;
import com.happyrepublicday.photoframe.R;
import com.happyrepublicday.photoframe.Utility;
import com.happyrepublicday.photoframe.simplecropimage.CropImage;
import com.happyrepublicday.photoframe.simplecropimage.InternalStorageContentProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import static com.google.android.gms.common.util.IOUtils.copyStream;

/**
 * Created by Kakadiyas on 11-07-2017.
 */

public class Select_Image_Activity extends AppCompatActivity{


    private ConnectionDetector detectorconn;
    Boolean conn;
    File mFileTemp;
    Constant constantfile;
    LinearLayout camera_ll,gallery_ll;
    private int REQUEST_CAMERA = 2, SELECT_FILE = 1,CROP_IMAGE = 3;

    private String userChoosenTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selectimage);

        constantfile = new Constant();
        this.conn = null;
        this.detectorconn = new ConnectionDetector(getApplicationContext());
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());

        ActionBar action = getSupportActionBar();
        action.setTitle(getResources().getString(R.string.choose_image_title));
        action.setDisplayHomeAsUpEnabled(true);
        action.setHomeButtonEnabled(true);

        camera_ll = (LinearLayout) findViewById(R.id.camera_ll);
        gallery_ll = (LinearLayout) findViewById(R.id.gallery_ll);


        camera_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage("camera");
            }
        });

        gallery_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage("gallery");
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("camera"))
                        cameraIntent();
                    else if(userChoosenTask.equals("gallery"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }

    private void selectImage(String type) {
        boolean result= Utility.checkPermission(Select_Image_Activity.this);
        userChoosenTask = type;
        if (type.equals("camera")) {
            if(result)
                cameraIntent();

        } else if (type.equals("gallery")) {
            if (result)
                galleryIntent();
        }
    }

    private void galleryIntent()
    {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
        } else {
            mFileTemp = new File(getFilesDir(), System.currentTimeMillis() + ".jpg");
        }
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }

    private void cameraIntent()
    {
        Uri mImageCaptureUri = null;
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
            mImageCaptureUri = FileProvider.getUriForFile(Select_Image_Activity.this, getApplicationContext().getPackageName()+".provider" , mFileTemp);
        } else {
            mFileTemp = new File(getFilesDir(), System.currentTimeMillis() + ".jpg");
            mImageCaptureUri = InternalStorageContentProvider.CONTENT_URI;
        }
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(intent, REQUEST_CAMERA);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1){
            if (resultCode == Activity.RESULT_OK) {
                if (requestCode == SELECT_FILE)
                    onSelectFromGalleryResult(data);
                else if (requestCode == REQUEST_CAMERA)
                    onCaptureImageResult(data);
                else if (requestCode == CROP_IMAGE)
                    onCropandRotateResult(data);
            }
        }

    }

    private void onCaptureImageResult(Intent data) {
        try {
            startCropImage();
        } catch (ArrayIndexOutOfBoundsException e) {

        } catch (IndexOutOfBoundsException e) {
        } catch (OutOfMemoryError e) {
        } catch (NullPointerException e) {
        } catch (NumberFormatException e) {
        } catch (Exception e) {

        }
    }

    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {

        try {

            InputStream inputStream = getContentResolver().openInputStream(data.getData());
            FileOutputStream fileOutputStream = new FileOutputStream(mFileTemp);
            copyStream(inputStream, fileOutputStream);
            fileOutputStream.close();
            inputStream.close();
            Log.e("selectedfilepath ",":::   "+mFileTemp);
            startCropImage();

        } catch (ArrayIndexOutOfBoundsException e) {
            Log.e("selectedfileerror ","arrayindex:::   "+e.toString());
        } catch (IndexOutOfBoundsException e) {
            Log.e("selectedfileerror ","indextout:::   "+e.toString());
        } catch (OutOfMemoryError e) {
            Log.e("selectedfileerror ","outofmemory:::   "+e.toString());
        } catch (NullPointerException e) {
            Log.e("selectedfileerror ","nullpointer:::   "+e.toString());
        } catch (NumberFormatException e) {
            Log.e("selectedfileerror ","numberfrmate:::   "+e.toString());
        } catch (Exception e) {
            Log.e("selectedfileerror ","exception:::   "+e.toString());
        }

    }


    @SuppressWarnings("deprecation")
    private void onCropandRotateResult(Intent data) {

        try {
            Intent i1 = new Intent(Select_Image_Activity.this, EditActivity.class);
            String path = data.getStringExtra("image-path");
            if (path == null) {
                return;
            }
            Bitmap bitmap = BitmapFactory.decodeFile(mFileTemp.getPath());

            Constant.bmp = bitmap;

            startActivity(i1);
            finish();
        } catch (ArrayIndexOutOfBoundsException e) {

        } catch (IndexOutOfBoundsException e) {
        } catch (OutOfMemoryError e) {
        } catch (NullPointerException e) {
        } catch (NumberFormatException e) {
        } catch (Exception e) {

        }

    }

    private void startCropImage() {

        Intent intent = new Intent(this, CropImage.class);
        intent.putExtra("image-path", mFileTemp.getPath());
        intent.putExtra(CropImage.SCALE, true);
        intent.putExtra(CropImage.ASPECT_X, 0);
        intent.putExtra(CropImage.ASPECT_Y, 0);
        intent.putExtra(CropImage.SCALE, true);
        intent.putExtra(CropImage.RETURN_DATA, false);
        startActivityForResult(intent, CROP_IMAGE);

    }

}
