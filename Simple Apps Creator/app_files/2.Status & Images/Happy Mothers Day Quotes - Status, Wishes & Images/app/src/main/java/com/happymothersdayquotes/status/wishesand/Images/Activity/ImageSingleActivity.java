package com.happymothersdayquotes.status.wishesand.Images.Activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.media.MediaScannerConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.happymothersdayquotes.status.wishesand.Images.Adapter.SlidingImage_Adapter;
import com.happymothersdayquotes.status.wishesand.Images.ConnectionDetector;
import com.happymothersdayquotes.status.wishesand.Images.Constant;
import com.happymothersdayquotes.status.wishesand.Images.Float.FloatingActionButton;
import com.happymothersdayquotes.status.wishesand.Images.Float.FloatingActionsMenu;
import com.happymothersdayquotes.status.wishesand.Images.R;
import com.happymothersdayquotes.status.wishesand.Images.Utility;
import com.happymothersdayquotes.status.wishesand.Images.gettersetter.ItemUpdate;
import com.happymothersdayquotes.status.wishesand.Images.gettersetter.Item_images;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

import static com.happymothersdayquotes.status.wishesand.Images.Constant.NOTIFICATION_CHANNEL_ID;

public class ImageSingleActivity extends AppCompatActivity {
    private static ViewPager mPager;
    ArrayList<Item_images> mData;
    int cposition;

    private String path;
    private ConnectionDetector detectorconn;
    Boolean conn;
    RelativeLayout image_relative;
    Constant constantfile;
    FloatingActionButton action_download, action_setas, action_share;
    FloatingActionsMenu floating_action_group;

    private RelativeLayout rl_progress_fragement_image;
    private TextView tv_progress_frag_image;
    private ProgressBar pb_fragement_image;

    private Boolean downloading = false;
    String ActionTypepass = "";
    private static final String SETAS_ID = "com.android.ACTION_ATTACH_DATA";
    private static final String SHARE_ID = "com.android.all";
    private static final String DOWNLOAD_ID = "com.android.download";

    //#########################################################################################

    private String type = "image/*";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_single);

        this.conn = null;
        constantfile = new Constant();
        this.detectorconn = new ConnectionDetector(ImageSingleActivity.this);
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());

        mData = new ArrayList<>();
        mData = Constant.Passing_item_images;
        cposition = Constant.Pass_img_pos;
        init();
    }

    private void init() {

        ImageView back = (ImageView) findViewById(R.id.back);
        image_relative = (RelativeLayout) findViewById(R.id.image_relative);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        this.pb_fragement_image = (ProgressBar) findViewById(R.id.pb_fragement_image);
        this.tv_progress_frag_image = (TextView) findViewById(R.id.tv_progress_frag_image);
        this.rl_progress_fragement_image = (RelativeLayout) findViewById(R.id.rl_progress_fragement_image);


        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(getResources().getColor(android.R.color.white));

        floating_action_group = (FloatingActionsMenu) findViewById(R.id.floating_action_group);
        action_download = (FloatingActionButton) findViewById(R.id.action_download);
        action_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floating_action_group.toggle();
                setdisableclick();
                ActionTypepass = DOWNLOAD_ID;
                Actionclickworking();
            }
        });
        action_setas = (FloatingActionButton) findViewById(R.id.action_setas);
        action_setas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floating_action_group.toggle();
                setdisableclick();
                ActionTypepass = SETAS_ID;
                Actionclickworking();
            }
        });
        action_share = (FloatingActionButton) findViewById(R.id.action_share);
        action_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                floating_action_group.toggle();
                setdisableclick();
                ActionTypepass = SHARE_ID;
                Actionclickworking();
            }
        });

        mPager = (ViewPager) findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(ImageSingleActivity.this, mData));
        mPager.setCurrentItem(cposition);

        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                cposition = i;
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        CallAddView(mData.get(cposition).getId());

    }

    public void setdisableclick() {
        action_share.setClickable(false);
        action_setas.setClickable(false);
        action_download.setClickable(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                action_share.setClickable(true);
                action_setas.setClickable(true);
                action_download.setClickable(true);
            }
        }, 2000);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Actionclickworking();
                } else {
                    Toast.makeText(ImageSingleActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void Actionclickworking() {

        String getextension = getFileExtension(mData.get(cposition).getWallpaper_image());
        if (!downloading && !getextension.equals("")) {
            this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
            if (this.conn.booleanValue()) {
                boolean result = Utility.checkPermission(ImageSingleActivity.this);
                if (result) {
                    Item_images temp_item = mData.get(cposition);
                    if (!downloading) {
                        String passimagename = temp_item.getWall_name();
                        if (ActionTypepass == DOWNLOAD_ID) {
                            Date c = Calendar.getInstance().getTime();
                            SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmmss");
                            String formattedDate = df.format(c);
                            passimagename = passimagename +"_"+formattedDate;
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                            new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, temp_item.getWallpaper_image(), passimagename, getextension, 0, ActionTypepass);
                        } else {
                            new DownloadFileFromURL().execute(AsyncTask.THREAD_POOL_EXECUTOR, temp_item.getWallpaper_image(), passimagename, getextension, 0, ActionTypepass);

                        }
                    }


                    //showAdsPredialog();
                } else {
                    constantfile.snackbarcommonrelative(ImageSingleActivity.this, image_relative, "Please give the permission to download Image!");
                }
            } else {
                snackbarcommonrelativeLong(ImageSingleActivity.this, image_relative, getResources().getString(R.string.no_internet));

            }
        } else {
            constantfile.snackbarcommonrelative(ImageSingleActivity.this, image_relative, getResources().getString(R.string.not_download));
        }
    }

    public void setDownloading(Boolean downloading) {
        if (downloading) {
            rl_progress_fragement_image.setVisibility(View.VISIBLE);
        } else {
            rl_progress_fragement_image.setVisibility(View.GONE);
        }
        this.downloading = downloading;
    }

    public void setProgressValue(int progress) {
        this.pb_fragement_image.setProgress(progress);
        this.tv_progress_frag_image.setText(getResources().getString(R.string.downloading) + " " + progress + " %");
    }


    public void snackbarcommonrelativeLong(Context mcontext, RelativeLayout coordinatorLayout, String snackmsg) {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, snackmsg + "", Snackbar.LENGTH_INDEFINITE).setAction("Try Again", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Actionclickworking();
            }
        });
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(ContextCompat.getColor(mcontext, R.color.white));
        TextView textView = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_text);
        TextView textaction = (TextView) snackbarView.findViewById(com.google.android.material.R.id.snackbar_action);
        textView.setTextSize(16);
        textaction.setTextSize(18);
        textView.setTextColor(Color.BLACK);
        textaction.setTextColor(Color.BLACK);
        snackbar.show();
    }

    private static String getFileExtension(String fileName) {
        if (fileName.lastIndexOf("/") != -1 && fileName.lastIndexOf("/") != 0) {
            String temfilename = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
            if (temfilename.lastIndexOf(".") != -1 && temfilename.lastIndexOf(".") != 0)
                return fileName.substring(fileName.lastIndexOf(".") + 1);
            else
                return "";
        } else {
            return "";
        }

    }


    class DownloadFileFromURL extends AsyncTask<Object, String, String> {

        private int position;
        private String old = "-100";
        private boolean runing = true;
        private String share_app;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            setDownloading(true);
        }

        public boolean dir_exists(String dir_path) {
            boolean ret = false;
            File dir = new File(dir_path);
            if (dir.exists() && dir.isDirectory())
                ret = true;
            return ret;
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            runing = false;
        }

        @Override
        protected String doInBackground(Object... f_url) {
            int count;
            try {
                URL url = new URL((String) f_url[0]);
                String title = (String) f_url[1];
                String extension = (String) f_url[2];
                this.position = (int) f_url[3];
                this.share_app = (String) f_url[4];
                Log.v("v", (String) f_url[0]);

                URLConnection conection = url.openConnection();
                conection.setRequestProperty("Accept-Encoding", "identity");
                conection.connect();

                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String dir_path = Environment.getExternalStorageDirectory().toString() + "/" + getResources().getString(R.string.download_directory) + "/" + getResources().getString(R.string.download_images) + "/";

                if (!dir_exists(dir_path)) {
                    File directory = new File(dir_path);
                    if (directory.mkdirs()) {
                        Log.v("dir", "is created 1");
                    } else {
                        Log.v("dir", "not created 1");

                    }
                    if (directory.mkdir()) {
                        Log.v("dir", "is created 2");
                    } else {
                        Log.v("dir", "not created 2");

                    }
                } else {
                }
                File file = new File(dir_path + title.toString().replace("/", "_") + "." + extension);
                if (!file.exists()) {
                    OutputStream output = new FileOutputStream(dir_path + title.toString().replace("/", "_") + "." + extension);

                    byte data[] = new byte[1024];

                    long total = 0;


                    while ((count = input.read(data)) != -1) {
                        total += count;
                        publishProgress("" + (int) ((total * 100) / lenghtOfFile));
                        output.write(data, 0, count);
                        if (!runing) {
                            Log.v("v", "not rurning");
                        }
                    }

                    output.flush();

                    output.close();
                    input.close();

                }
                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{dir_path + title.toString().replace("/", "_") + "." + extension},
                        null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {

                            }
                        });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    final Uri contentUri = Uri.fromFile(new File(dir_path + title.toString().replace("/", "_") + "." + extension));
                    scanIntent.setData(contentUri);
                    ImageSingleActivity.this.sendBroadcast(scanIntent);
                } else {
                    final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                    ImageSingleActivity.this.sendBroadcast(intent);
                }
                path = dir_path + title.replace("/", "_") + "." + extension;
            } catch (Exception e) {
            }

            return null;
        }

        protected void onProgressUpdate(String... progress) {
            try {
                if (!progress[0].equals(old)) {
                    old = progress[0];
                    setDownloading(true);
                    setProgressValue(Integer.parseInt(progress[0]));
                }
            } catch (Exception e) {

            }
        }


        @Override
        protected void onPostExecute(String file_url) {

            setDownloading(false);
            if (path == null) {
                try {
                    constantfile.snackbarcommonrelative(ImageSingleActivity.this, image_relative, getResources().getString(R.string.download_failed));
                } catch (Exception e) {

                }
            } else {
                switch (share_app) {
                    case SETAS_ID:
                        setas(path);
                        break;
                    case SHARE_ID:
                        share(path);
                        break;
                    case DOWNLOAD_ID:
                        download(path);
                        break;
                }
            }
        }

    }

    public void share(String path) {
        Uri imageUri = FileProvider.getUriForFile(ImageSingleActivity.this, getApplicationContext().getPackageName() + ".provider", new File(path));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        final String final_text = getResources().getString(R.string.download_moreimage_from_link) + "\n\n" + Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName()).toString();

        shareIntent.putExtra(Intent.EXTRA_TEXT, final_text);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

        shareIntent.setType(type);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_via) + " " + getResources().getString(R.string.app_name)));
        } catch (android.content.ActivityNotFoundException ex) {
            constantfile.snackbarcommonrelative(ImageSingleActivity.this, image_relative, getResources().getString(R.string.app_not_installed));
        }
    }

    public void setas(String path) {

        Uri imageUri = FileProvider.getUriForFile(ImageSingleActivity.this, getApplicationContext().getPackageName() + ".provider", new File(path));
        Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(imageUri, type);
        intent.putExtra("mimeType", type);
        try {
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.share_via) + " " + getResources().getString(R.string.app_name)));
        } catch (android.content.ActivityNotFoundException ex) {
            constantfile.snackbarcommonrelative(ImageSingleActivity.this, image_relative, getResources().getString(R.string.not_download));
        }
    }

    private void download(String getpath) {
        constantfile.snackbarcommonrelative(ImageSingleActivity.this, image_relative, getResources().getString(R.string.images_downloaded));
        CallAddDownload(mData.get(cposition).getId());
        showNotification(ImageSingleActivity.this, getResources().getString(R.string.app_name), "Download Successfully", getpath);
    }

    @SuppressLint("NewApi")
    private void showNotification(Context mContext, String title, String message, String filepath) {

        // Bitmap bitmap = getBitmapFromURL(image);
        Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", new File(filepath));
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(contentUri, "image/*");
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext, 100, intent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notif = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notif = new NotificationCompat.Builder(mContext)
                    .setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setColor(Color.BLACK)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setChannelId(NOTIFICATION_CHANNEL_ID)
                    .setSubText(message)
                    .build();
        } else {
            notif = new NotificationCompat.Builder(mContext)
                    .setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setSubText(message)
                    .build();

        }
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, Constant.notification_name, importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }

        notif.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(1, notif);

    }

    public void CallAddDownload(String PassID) {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(60000);
        params.put("type", "image");
        params.put("view_id", PassID);
        client.get(Constant.GET_ADD_DOWNLOAD, params, new AsynchronouseDownload());
    }


    class AsynchronouseDownload extends JsonHttpResponseHandler {

        AsynchronouseDownload() {
        }

        public void onStart() {
            super.onStart();
        }

        public void onSuccess(int i, Header[] headers, JSONObject bytes) {


            try {
                ItemUpdate statusData = new ItemUpdate();
                statusData.setStatus(bytes.getBoolean("status"));
                statusData.setMessage(bytes.getString("message"));
                if (statusData.isStatus()) {

                } else {

                }
                Log.e("calladdviewApi", "response :: " + bytes.getBoolean("status"));
            } catch (Exception e) {
            }


        }

    }

    public void CallAddView(String PassID) {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(60000);
        params.put("type", "image");
        params.put("view_id", PassID);
        client.get(Constant.GET_ADD_VIEW, params, new AsynchronouseData());
    }


    class AsynchronouseData extends JsonHttpResponseHandler {

        AsynchronouseData() {
        }

        public void onStart() {
            super.onStart();
        }

        public void onSuccess(int i, Header[] headers, JSONObject bytes) {


            try {
                ItemUpdate statusData = new ItemUpdate();
                statusData.setStatus(bytes.getBoolean("status"));
                statusData.setMessage(bytes.getString("message"));
                if (statusData.isStatus()) {

                } else {

                }
                Log.e("calladdviewApi", "response :: " + bytes.getBoolean("status"));
            } catch (Exception e) {
            }


        }

    }


}
