package com.mi9t.k20prowallpapers;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
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
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.snackbar.Snackbar;
import com.mi9t.k20prowallpapers.gettersetter.Item_collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class Constant {
    public static final String BASIC_URL = "http://simpleappscreator.com/mobile_wise_wallpaper/api/";


    public static final String GET_IMAGE_LISTING = BASIC_URL + "api_listing.php";
    public static final String GET_ADD_VIEW = BASIC_URL + "api_add_view.php";
    public static final String GET_ADD_DOWNLOAD = BASIC_URL + "api_add_download.php";
    public static final String GET_ADD_TOKEN = BASIC_URL + "api_add_token.php";


    public static final String downloadshareimage = "MI9TWallpapershare.jpg";
    public static final String LOGIN_DATA = "LOGIN_DATA";
    public static final String actiondownload = "download";
    public static final String actionsetas = "setas";
    public static int Adscount = 2;
    public static ArrayList<Item_collections> passing_array = new ArrayList<>();
    public static Item_collections passing_object = new Item_collections();
    public static int passing_from = 1;


    ProgressDialog pdialog;
    private Context context;
    private String downloadUrl = "", downloadFileName = "";
    String action = "share";
    RelativeLayout relativelayout;

    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    public static final String NOTIFICATION_CHANNEL_ID = "10011";


    public static final String SHARED_PREF = "ah_firebase_K20";
    public static final String TOPIC_GLOBAL = "global";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";


    public void snackbarcommonrelative(Context mcontext, RelativeLayout coordinatorLayout, String snackmsg) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg + "", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.colorPrimaryDark));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(16);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void snackbarcommonView(Context mcontext, View coordinatorLayout, String snackmsg) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg + "", Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.colorPrimaryDark));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        textView.setTextSize(16);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    public void snackbarcommoncoordinatorLayout(Context mcontext, CoordinatorLayout coordinatorLayout, String snackmsg) {
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

    public ArrayList<Item_collections> ConvertJSONtoModel(JSONArray getarray) throws JSONException {
        ArrayList<Item_collections> temp_array = new ArrayList<>();

        for (int i = 0; i < getarray.length(); i++) {
            JSONObject pass = getarray.getJSONObject(i);
            Item_collections temp_item = new Item_collections();

            temp_item.setId(pass.getString("id"));
            temp_item.setWall_name(pass.getString("wall_name"));
            temp_item.setWallpaper_image(pass.getString("wallpaper_image"));
            temp_item.setWallpaper_image_thumb(pass.getString("wallpaper_image_thumb"));
            temp_item.setTotal_views(pass.getString("total_views"));
            temp_item.setTotal_download(pass.getString("total_download"));
            temp_item.setDate(pass.getString("date"));
            temp_item.setWall_tags(pass.getString("wall_tags"));
            temp_array.add(temp_item);
        }

        return temp_array;
    }

    public void showporgressdialog(Context mContext, String Actiontype) {
        String title = "";
        if (Actiontype.equals(actionsetas)) {
            title = "Set As";
        } else if (Actiontype.equals(actiondownload)) {
            title = "Downloding...";
        } else {
            title = "Processing";
        }
        pdialog = new ProgressDialog(mContext, R.style.MyAlertDialogStyle);
        pdialog.setCancelable(false);
        pdialog.setMessage(title);
        pdialog.show();
    }


    public void share_image(Context mContext, String Actiontype, String image_url, RelativeLayout relaivelayout) {
        this.context = mContext;
        this.downloadUrl = image_url;
        this.action = Actiontype;
        this.relativelayout = relaivelayout;

        downloadFileName = context.getResources().getString(R.string.download_directory) + System.currentTimeMillis() + ".jpg";
        new DownloadingTask().execute();
    }

    private class DownloadingTask extends AsyncTask<Void, Void, Void> {

        File outputFile = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(Void result) {
            try {
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }
                if (outputFile != null) {

                    if (action.equals(actionsetas)) {
                        SetAscall(context, outputFile);
                    } else {
                        if (pdialog.isShowing()) {
                            pdialog.dismiss();
                        }
                        snackbarcommonrelative(context, relativelayout, "Download Successfully!");
                        new generatePictureStyleNotification(context, context.getResources().getString(R.string.app_name), "Download Successfully", outputFile.getAbsolutePath()).execute();
                    }
                } else {
                    snackbarcommonrelative(context, relativelayout, "Something Went Wrong! please try again");
                }
            } catch (Exception e) {
                if (pdialog.isShowing()) {
                    pdialog.dismiss();
                }
                e.printStackTrace();
            }

            super.onPostExecute(result);
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);//Create Download URl
                HttpURLConnection c = (HttpURLConnection) url.openConnection();//Open Url Connection
                c.setRequestMethod("GET");//Set Request Method to "GET" since we are grtting data
                c.connect();//connect the URL Connection

                //If Connection response is not OK then show Logs
                if (c.getResponseCode() != HttpURLConnection.HTTP_OK) {

                }

                String root = Environment.getExternalStorageDirectory().toString();
                File myDir = new File(root + "/" + context.getResources().getString(R.string.download_directory) + "/");
                if (!myDir.exists()) {
                    myDir.mkdirs();
                }

                outputFile = new File(myDir, downloadFileName);//Create Output file in Main File
                if (!outputFile.exists()) {
                    outputFile.createNewFile();
                }

                FileOutputStream fos = new FileOutputStream(outputFile);//Get OutputStream for NewFile Location

                InputStream is = c.getInputStream();//Get InputStream for connection

                byte[] buffer = new byte[1024];//Set buffer type
                int len1 = 0;//init length
                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);//Write new file
                }
                fos.close();
                is.close();
                scanFile(context, outputFile.getAbsolutePath());

            } catch (Exception e) {
                e.printStackTrace();
                outputFile = null;
            }

            return null;
        }
    }


    public void download_bitmapimage(Context mContext, Bitmap imageshare, RelativeLayout relaivelayout) {
        context = mContext;
        if (pdialog.isShowing()) {
            pdialog.dismiss();
        }
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/" + mContext.getResources().getString(R.string.download_directory) + "/");
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
    }

    public void setAs_bitmapimage(Context mContext, Bitmap imageshare, RelativeLayout relaivelayout) {
        context = mContext;
        if (pdialog.isShowing()) {
            pdialog.dismiss();
        }
        try {
            String root = Environment.getExternalStorageDirectory().toString();
            File myDir = new File(root + "/" + mContext.getResources().getString(R.string.download_directory) + "/");
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
                    SetAscall(mContext, file);
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

    public void SetAscall(Context mContext, File outputFile) {
        Uri uri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".provider", outputFile);
        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("mimeType", "image/*");
        mContext.startActivity(Intent.createChooser(intent, "Set Image"));
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

        @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.O)
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

            Notification notif = new Notification.Builder(mContext)
                    .setContentIntent(pendingIntent)
                    .setContentTitle(title)
                    .setContentText(message)
                    .setSmallIcon(android.R.drawable.stat_sys_download_done)
                    .setStyle(new Notification.BigPictureStyle().bigPicture(bitmap))
                    .setChannelId(NOTIFICATION_CHANNEL_ID)
                    .build();

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Wallexplore_notification", importance);
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
