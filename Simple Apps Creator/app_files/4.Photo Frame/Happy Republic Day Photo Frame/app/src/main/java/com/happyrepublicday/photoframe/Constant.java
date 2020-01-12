package com.happyrepublicday.photoframe;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.snackbar.Snackbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Constant {
    public static final String LOGIN_DATA = "LOGIN_DATA";
    public static final String downloadDirectory = "Republic Day Photo Frames";
    public static final String downloadshareimage = "RepublicDay_Photoframeshare.jpg";
    public static int Adscount = 1;
    public static Bitmap bmp;
    public static Bitmap getfinalimage;
    public static ArrayList<String> arrayofimages = new ArrayList<>();
    public static ArrayList<String> arrayofshowimages = new ArrayList<>();
    public static int[] stickerList = new int[]{R.drawable.ic_add, R.drawable.ic_add};
    public static int selectedframe;

    public static final String NOTIFICATION_CHANNEL_ID = "10001";


    public void snackbarcommonrelative(Context mcontext, RelativeLayout coordinatorLayout, String snackmsg) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg + "", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.colorPrimaryDark));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(16);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void snackbarcommonlinear(Context mcontext, LinearLayout coordinatorLayout, String snackmsg) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg + "", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.colorPrimaryDark));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(16);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void snackbarcommonConstraintLayout(Context mcontext, ConstraintLayout coordinatorLayout, String snackmsg) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg + "", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.colorPrimaryDark));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(16);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void snackbarcommondrawerLayout(Context mcontext, DrawerLayout coordinatorLayout, String snackmsg) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg + "", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.colorPrimaryDark));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(16);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    private void scanFile(Context context, String path) {

        MediaScannerConnection.scanFile(context,
                new String[]{path}, null,
                new MediaScannerConnection.OnScanCompletedListener() {

                    public void onScanCompleted(String path, Uri uri) {
                        Log.d("Tag", "Scan finished. You can view the image in the gallery now.");
                    }
                });
    }

    public void download_image(Context mContext, Bitmap imageshare, RelativeLayout relaivelayout) {
        boolean result = Utility.checkPermission(mContext);
        if (result) {
            try {
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/" + downloadDirectory + "/");
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }
                File file = new File(myDir, mContext.getResources().getString(R.string.download_imagename) + System.currentTimeMillis() + ".jpg");

                try {
                    if (file.exists()) {
                        file = new File(myDir, mContext.getResources().getString(R.string.download_imagename) + System.currentTimeMillis() + "12" + ".jpg");
                        file.createNewFile();


                    } else {
                        if (!file.exists()) {
                            file.createNewFile();
                        }
                    }
                    FileOutputStream out = new FileOutputStream(file);
                    imageshare.compress(Bitmap.CompressFormat.JPEG, 40, out);
                    out.flush();
                    out.close();
                    scanFile(mContext, file.getAbsolutePath());

                } catch (Exception e) {
                    e.printStackTrace();
                }
                snackbarcommonrelative(mContext, relaivelayout, "Download Successfully!");
                new generatePictureStyleNotification(mContext, mContext.getResources().getString(R.string.app_name), "Download Successfully", file.getAbsolutePath()).execute();


            } catch (NullPointerException e) {
                snackbarcommonrelative(mContext, relaivelayout, "Something Went Wrong! please try again");
            } catch (ArrayIndexOutOfBoundsException e) {
                snackbarcommonrelative(mContext, relaivelayout, "Something Went Wrong! please try again");
            } catch (ClassCastException e) {
                snackbarcommonrelative(mContext, relaivelayout, "Something Went Wrong! please try again");
            } catch (ActivityNotFoundException e) {
                snackbarcommonrelative(mContext, relaivelayout, "Something Went Wrong! please try again");
            } catch (RuntimeException e) {
                snackbarcommonrelative(mContext, relaivelayout, "Something Went Wrong! please try again");
            } catch (OutOfMemoryError e) {
                snackbarcommonrelative(mContext, relaivelayout, "Something Went Wrong! please try again");
            }
        } else {
            snackbarcommonrelative(mContext, relaivelayout, "Please give the permission to download Image!");
        }
    }


    public void share_image(Context mContext, Bitmap imageshare, RelativeLayout relaivelayout) {
        boolean result = Utility.checkPermission(mContext);
        if (result) {
            try {
                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/" + downloadDirectory + "/");
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }
                File file = new File(myDir, mContext.getResources().getString(R.string.download_imagename) + downloadshareimage);
                snackbarcommonrelative(mContext, relaivelayout, "Sharing Process!");
                if (file.exists())
                    file.delete();
                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    FileOutputStream ostream = new FileOutputStream(file);
                    imageshare.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                    ostream.close();

                    Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", file);

                    if (contentUri != null) {
                        Shareother(mContext, contentUri);
                    }

                    scanFile(mContext, file.getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (NullPointerException e) {
                snackbarcommonrelative(mContext, relaivelayout, "Something Went Wrong! please try again");
            } catch (ArrayIndexOutOfBoundsException e) {
                snackbarcommonrelative(mContext, relaivelayout, "Something Went Wrong! please try again");
            } catch (ClassCastException e) {
                snackbarcommonrelative(mContext, relaivelayout, "Something Went Wrong! please try again");
            } catch (ActivityNotFoundException e) {
                snackbarcommonrelative(mContext, relaivelayout, "Something Went Wrong! please try again");
            } catch (RuntimeException e) {
                snackbarcommonrelative(mContext, relaivelayout, "Something Went Wrong! please try again");
            } catch (OutOfMemoryError e) {
                snackbarcommonrelative(mContext, relaivelayout, "Something Went Wrong! please try again");
            }
        } else {
            snackbarcommonrelative(mContext, relaivelayout, "Please give the permission to download Image!");
        }
    }

    public void Shareother(Context mContext, Uri contentUri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
        shareIntent.setDataAndType(contentUri, mContext.getContentResolver().getType(contentUri));
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, mContext.getResources().getString(R.string.share_image_title));
        shareIntent.putExtra(Intent.EXTRA_TEXT, mContext.getResources().getString(R.string.share_image_title));
        mContext.startActivity(Intent.createChooser(shareIntent, "Share image using"));
    }


    public class generatePictureStyleNotification extends AsyncTask<String, Void, Bitmap> {

        private Context mContext;
        private String title, message, imageUrl;

        public generatePictureStyleNotification(Context context, String title, String message, String imageUrl) {
            super();
            this.mContext = context;
            this.title = title;
            this.message = message;
            this.imageUrl = imageUrl;
        }

        @Override
        protected Bitmap doInBackground(String... params) {

            InputStream in;
            try {
                URL url = new URL(this.imageUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @TargetApi(Build.VERSION_CODES.O)
        @Override
        protected void onPostExecute(Bitmap result) {
            super.onPostExecute(result);

            Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", new File(imageUrl));
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(contentUri, "image/*");
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 100, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            Bitmap bitmap = BitmapFactory.decodeFile((new File(imageUrl)).getAbsolutePath());
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);

            Notification notif = new NotificationCompat.Builder(mContext)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                    .setChannelId(NOTIFICATION_CHANNEL_ID)
                    .build();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, mContext.getResources().getString(R.string.app_name) + "_notification", importance);
//                notificationChannel.enableVibration(true);
//                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                assert notificationManager != null;
                notificationManager.createNotificationChannel(notificationChannel);
            }

            notif.flags |= Notification.FLAG_AUTO_CANCEL;
            notificationManager.notify(1, notif);


          /*  String groupId = "my_group_01";
            CharSequence groupName = mContext.getString(R.string.group_name);
            mNotificationManager.createNotificationChannelGroup(new NotificationChannelGroup(groupId, groupName));*/
        }
    }
}
