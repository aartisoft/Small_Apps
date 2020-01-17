package com.happymothersdayquotes.status.wishesand.Images.Activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;

import com.bumptech.glide.Glide;
//import com.facebook.ads.AdSize;
//import com.facebook.ads.AdView;
//import com.facebook.ads.AudienceNetworkAds;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.TransferListener;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.snackbar.Snackbar;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.happymothersdayquotes.status.wishesand.Images.ConnectionDetector;
import com.happymothersdayquotes.status.wishesand.Images.Constant;
import com.happymothersdayquotes.status.wishesand.Images.DbAdapter;
import com.happymothersdayquotes.status.wishesand.Images.PrefManager;
import com.happymothersdayquotes.status.wishesand.Images.R;
import com.happymothersdayquotes.status.wishesand.Images.Utility;
import com.happymothersdayquotes.status.wishesand.Images.gettersetter.ItemUpdate;
import com.happymothersdayquotes.status.wishesand.Images.gettersetter.Item_videos;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import cz.msebera.android.httpclient.Header;

import static com.happymothersdayquotes.status.wishesand.Images.Constant.NOTIFICATION_CHANNEL_ID;
import static com.happymothersdayquotes.status.wishesand.Images.Constant.SHARED_AUTOPLAY_SET;


public class VideoplayerActivity extends AppCompatActivity {

    private LinearLayout detail_rl;
    private PrefManager prf;
    private ConnectionDetector detectorconn;
    Boolean conn;
    Constant constantfile;
    String ActionTypepass = "";
    LinearLayout action_ll;
    AlertDialog alertDialog;
    private Toolbar mTopToolbar;
    Item_videos getvideodata = new Item_videos();
    DbAdapter db;

    private static final String WHATSAPP_ID = "com.whatsapp";
    private static final String FACEBOOK_ID = "com.facebook.katana";
    private static final String INSTAGRAM_ID = "com.instagram.android";
    private static final String SHARE_ID = "com.android.all";
    private static final String DOWNLOAD_ID = "com.android.download";

    //#########################################################################################

    private String type = "video/*";


    private Timeline.Window window;
    private DataSource.Factory mediaDataSourceFactory;
    private ImageView ivHideControllerButton, exo_play;
    private BandwidthMeter bandwidthMeter;
    private DefaultTrackSelector trackSelector;
    private boolean shouldAutoPlay;
    private ImageView image_view_load_video_item;
    private RelativeLayout relative_layout_fragement_video_thum;
    private ImageView image_view_video_fragement_video;
    private ImageView exo_replay;
    private LinearLayout linear_layout_exo_replay;
    private RelativeLayout actiondownload_rl;
    private ImageView image_share, image_face, image_whats, image_insta;
    private RelativeLayout relative_layout_progress_fragement_video;
    private TextView text_view_progress_fragement_video;
    private ProgressBar progress_bar_fragement_video;
    private SimpleExoPlayerView simpleExoPlayerView;
    private Boolean downloading = false;
    private String path;
    ProgressBar Progressbar_player;
    RelativeLayout player_rl;
    TextView videonotfound;
    NestedScrollView scroll_detail;
    private AdView adViewbanner;
    Boolean mExoPlayerFullscreen = false;
    Dialog mFullScreenDialog;
    TextView detail_title, detail_views;

    private SimpleExoPlayer player;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTopToolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        db = new DbAdapter(VideoplayerActivity.this);
        db.open();

        this.conn = null;
        constantfile = new Constant();
        this.detectorconn = new ConnectionDetector(VideoplayerActivity.this);
        this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);


        initView();
        initAction();
        showData();

        CallAddView(getvideodata.getId());
        viewAdstype(getvideodata.getIs_type().toLowerCase());
    }

    public void setdisableclick() {
        image_whats.setClickable(false);
        image_insta.setClickable(false);
        image_face.setClickable(false);
        image_share.setClickable(false);
        actiondownload_rl.setClickable(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                image_whats.setClickable(true);
                image_insta.setClickable(true);
                image_face.setClickable(true);
                image_share.setClickable(true);
                actiondownload_rl.setClickable(true);
            }
        }, 2000);
    }

    private void initView() {

        detail_rl = (LinearLayout) findViewById(R.id.detail_rl);
        videonotfound = (TextView) findViewById(R.id.videonotfound);
        scroll_detail = (NestedScrollView) findViewById(R.id.scroll_detail);
        action_ll = (LinearLayout) findViewById(R.id.action_ll);
        player_rl = (RelativeLayout) findViewById(R.id.player_rl);
        Progressbar_player = (ProgressBar) findViewById(R.id.Progressbar_player);
        actiondownload_rl = (RelativeLayout) findViewById(R.id.actiondownload_rl);
        image_whats = (ImageView) findViewById(R.id.image_whats);
        image_insta = (ImageView) findViewById(R.id.image_insta);
        image_face = (ImageView) findViewById(R.id.image_face);
        image_share = (ImageView) findViewById(R.id.image_share);
        detail_title = (TextView) findViewById(R.id.detail_title);
        detail_views = (TextView) findViewById(R.id.detail_views);

        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.video_view);

        this.image_view_video_fragement_video = (ImageView) findViewById(R.id.image_view_video_fragement_video);
        this.image_view_load_video_item = (ImageView) findViewById(R.id.image_view_load_video_item);
        this.relative_layout_fragement_video_thum = (RelativeLayout) findViewById(R.id.relative_layout_fragement_video_thum);
        ivHideControllerButton = (ImageView) findViewById(R.id.exo_controller);
        exo_play = (ImageView) findViewById(R.id.exo_play);
        exo_replay = (ImageView) findViewById(R.id.exo_replay);
        linear_layout_exo_replay = (LinearLayout) findViewById(R.id.linear_layout_exo_replay);


        this.progress_bar_fragement_video = (ProgressBar) findViewById(R.id.progress_bar_fragement_video);
        this.text_view_progress_fragement_video = (TextView) findViewById(R.id.text_view_progress_fragement_video);
        this.relative_layout_progress_fragement_video = (RelativeLayout) findViewById(R.id.relative_layout_progress_fragement_video);

        Glide.with(VideoplayerActivity.this)
                .load(getvideodata.getVideo_image_thumb())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(image_view_video_fragement_video);
        prf = new PrefManager(getApplicationContext());
        if (prf.getBoolean(SHARED_AUTOPLAY_SET)) {
            shouldAutoPlay = false;
        } else {
            shouldAutoPlay = true;
        }

        bandwidthMeter = new DefaultBandwidthMeter();
        mediaDataSourceFactory = new DefaultDataSourceFactory(VideoplayerActivity.this.getApplicationContext(), Util.getUserAgent(VideoplayerActivity.this.getApplicationContext(), "mediaPlayerSample"), (TransferListener<? super DataSource>) bandwidthMeter);
        window = new Timeline.Window();

    }


    private void initializePlayer() {
        Progressbar_player.setVisibility(View.VISIBLE);
        relative_layout_fragement_video_thum.setVisibility(View.GONE);

        if (!mExoPlayerFullscreen) {
            simpleExoPlayerView.requestFocus();
        }
        simpleExoPlayerView.setVisibility(View.VISIBLE);
        TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);
        simpleExoPlayerView.setControllerShowTimeoutMs(0);
        player = ExoPlayerFactory.newSimpleInstance(VideoplayerActivity.this, trackSelector);

        simpleExoPlayerView.setPlayer(player);
        simpleExoPlayerView.setControllerAutoShow(false);
        player.setPlayWhenReady(shouldAutoPlay);

        DefaultExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

        MediaSource mediaSource = new ExtractorMediaSource(Uri.parse(getvideodata.getVideo_url()), mediaDataSourceFactory, extractorsFactory, null, null);

        player.prepare(mediaSource);
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {

            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == ExoPlayer.STATE_READY) {
                    Progressbar_player.setVisibility(View.GONE);
                    linear_layout_exo_replay.setVisibility(View.GONE);

                }
                if (playbackState == ExoPlayer.STATE_BUFFERING) {
                    Progressbar_player.setVisibility(View.VISIBLE);
                    linear_layout_exo_replay.setVisibility(View.GONE);

                }
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    linear_layout_exo_replay.setVisibility(View.VISIBLE);
                    Progressbar_player.setVisibility(View.GONE);
                    simpleExoPlayerView.showController();
                    simpleExoPlayerView.setControllerAutoShow(false);
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {

            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }
        });


        ivHideControllerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mExoPlayerFullscreen)
                    openFullscreenDialog();
                else
                    closeFullscreenDialog();
            }
        });

    }

    private void initAction() {

        this.exo_replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exo_play.performClick();
//                shouldAutoPlay = true;
//                initializePlayer();

            }
        });
        this.image_view_load_video_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relative_layout_fragement_video_thum.setVisibility(View.GONE);
                initializePlayer();

            }
        });

        image_whats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setdisableclick();
                ActionTypepass = WHATSAPP_ID;
                Actionclickworking();
            }
        });

        image_insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setdisableclick();
                ActionTypepass = INSTAGRAM_ID;
                Actionclickworking();
            }
        });

        image_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setdisableclick();
                ActionTypepass = FACEBOOK_ID;
                Actionclickworking();
            }
        });

        image_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setdisableclick();
                ActionTypepass = SHARE_ID;
                Actionclickworking();
            }
        });

        actiondownload_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setdisableclick();
                ActionTypepass = DOWNLOAD_ID;
                Actionclickworking();
            }
        });


    }

    public void setDownloading(Boolean downloading) {
        if (downloading) {
            relative_layout_progress_fragement_video.setVisibility(View.VISIBLE);
        } else {
            relative_layout_progress_fragement_video.setVisibility(View.GONE);
        }
        this.downloading = downloading;
    }

    public void setProgressValue(int progress) {
        this.progress_bar_fragement_video.setProgress(progress);
        this.text_view_progress_fragement_video.setText(getResources().getString(R.string.downloading) + " " + progress + " %");
    }

    private void releasePlayer() {
        if (player != null) {
            shouldAutoPlay = player.getPlayWhenReady();
            player.release();
            player = null;
            trackSelector = null;
        }
    }

    private void showData() {
        Constant.Passing_from_notification = false;
        getvideodata = new Item_videos();
        getvideodata = Constant.Passing_item_obj_video;
        if (getvideodata == null) {
            onBackPressed();
        }
        String geturl = getvideodata.getVideo_url();

        if (getFileExtension(geturl).equals("")) {
            videonotfound.setVisibility(View.VISIBLE);
            scroll_detail.setVisibility(View.GONE);
            return;
        }

        mTopToolbar.setTitle(getvideodata.getVideo_name() + "");
        simpleExoPlayerView = (SimpleExoPlayerView) findViewById(R.id.video_view);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        player_rl = (RelativeLayout) findViewById(R.id.player_rl);
        ViewGroup.LayoutParams paramscat = player_rl.getLayoutParams();
        if (getvideodata.getIs_type().toLowerCase().equals("portrait")) {
            paramscat.height = (int) (height / 1.5);
        } else {
            paramscat.height = (int) (height / 2.5);
        }
        player_rl.setLayoutParams(paramscat);

        detail_title.setText(getvideodata.getVideo_name() + "");
        detail_views.setText(getvideodata.getTotal_views() + " Views");

        String uniqueid = getvideodata.getId();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        // checkPermission();
        initializePlayer();
        initFullscreenDialog();
    }

    @Override
    public void onPause() {
        super.onPause();
        releasePlayer();

    }

    @Override
    public void onStop() {
        super.onStop();
        releasePlayer();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Actionclickworking();
                } else {
                    constantfile.snackbarcommonlinear(VideoplayerActivity.this, detail_rl, "Permission denied to read your External storage!");
                }
                return;
            }
        }
    }

    public void Actionclickworking() {
        if (player != null) {
            if (player.getPlayWhenReady()) {
                player.setPlayWhenReady(false);
                player.getPlaybackState();
            }
        }
        String getextension = getFileExtension(getvideodata.getVideo_url());
        if (!downloading && !getextension.equals("")) {
            this.conn = Boolean.valueOf(this.detectorconn.isConnectingToInternet());
            if (this.conn.booleanValue()) {
                boolean result = Utility.checkPermission(VideoplayerActivity.this);
                if (result) {
                    Cursor row = db.fetchsingledata(getvideodata.getId());
                    for (row.moveToFirst(); !row.isAfterLast(); row.moveToNext()) {
                        String video_url = row.getString(row.getColumnIndex("video_url"));
                        if (video_url != null && video_url.length() > 0) {
                            File file = new File(video_url);
                            if (file.exists()) {
                                OpenalreadyDownloaddialog(ActionTypepass, video_url);
                                return;
                            }
                        }
                    }
                    String getextensioninside = getvideodata.getVideo_url().substring(getvideodata.getVideo_url().lastIndexOf("."));
                    if (!downloading) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                            new DownloadFileFromURL().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, getvideodata.getVideo_url(), getvideodata.getVideo_name(), getextensioninside, 0, ActionTypepass);
                        else
                            new DownloadFileFromURL().execute(AsyncTask.THREAD_POOL_EXECUTOR, getvideodata.getVideo_url(), getvideodata.getVideo_name(), getextensioninside, 0, ActionTypepass);
                    }


                    //showAdsPredialog();
                } else {
                    constantfile.snackbarcommonlinear(VideoplayerActivity.this, detail_rl, "Please give the permission to download Image!");
                }
            } else {
                snackbarcommonrelativeLong(VideoplayerActivity.this, detail_rl, getResources().getString(R.string.no_internet));

            }
        } else {
            constantfile.snackbarcommonlinear(VideoplayerActivity.this, detail_rl, getResources().getString(R.string.not_download));
        }
    }


    public void snackbarcommonrelativeLong(Context mcontext, LinearLayout coordinatorLayout, String snackmsg) {
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


    public void CallAddView(String PassID) {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(60000);
        params.put("type", "video");
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

        public void onFailure(int i, Header[] headers, JSONObject bytes, Throwable throwable) {

        }
    }

    public void CallAddDownload(String PassID) {
        RequestParams params = new RequestParams();
        AsyncHttpClient client = new AsyncHttpClient(true, 80, 443);
        client.setTimeout(60000);
        params.put("type","video");
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

        public void onFailure(int i, Header[] headers, JSONObject bytes, Throwable throwable) {

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

                String dir_path = Environment.getExternalStorageDirectory().toString() + "/" + getResources().getString(R.string.download_directory) + "/"+ getResources().getString(R.string.download_videos)+"/";

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
                File file = new File(dir_path + title.toString().replace("/", "_") + "."+ extension);
                if (!file.exists()) {
                    OutputStream output = new FileOutputStream(dir_path + title.toString().replace("/", "_") + "."+ extension);


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
                MediaScannerConnection.scanFile(VideoplayerActivity.this.getApplicationContext(), new String[]{dir_path + title.toString().replace("/", "_") + "."+ extension},
                        null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {

                            }
                        });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    final Uri contentUri = Uri.fromFile(new File(dir_path + title.toString().replace("/", "_") + "."+ extension));
                    scanIntent.setData(contentUri);
                    VideoplayerActivity.this.sendBroadcast(scanIntent);
                } else {
                    final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));
                    VideoplayerActivity.this.sendBroadcast(intent);
                }
                path = dir_path + title.toString().replace("/", "_") +"."+ extension;
            } catch (Exception e) {
            }

            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            try {
                if (!progress[0].equals(old)) {
                    old = progress[0];
                    Log.v("download", progress[0] + "%");
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
                    constantfile.snackbarcommonlinear(VideoplayerActivity.this, detail_rl, getResources().getString(R.string.download_failed));
                } catch (Exception e) {

                }
            } else {
                switch (share_app) {
                    case WHATSAPP_ID:
                        shareWhatsapp(path);
                        break;
                    case FACEBOOK_ID:
                        shareFacebook(path);
                        break;
                    case INSTAGRAM_ID:
                        shareInstagram(path);
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

    public void OpenalreadyDownloaddialog(String share_app, String getpath) {
        if (getpath == null) {
            try {
                constantfile.snackbarcommonlinear(VideoplayerActivity.this, detail_rl, getResources().getString(R.string.download_failed));
            } catch (Exception e) {

            }
        } else {
            switch (share_app) {
                case WHATSAPP_ID:
                    constantfile.snackbarcommonlinear(VideoplayerActivity.this, detail_rl, "Processing");
                    shareWhatsapp(getpath);
                    break;
                case FACEBOOK_ID:
                    constantfile.snackbarcommonlinear(VideoplayerActivity.this, detail_rl, "Processing");
                    shareFacebook(getpath);
                    break;
                case INSTAGRAM_ID:
                    constantfile.snackbarcommonlinear(VideoplayerActivity.this, detail_rl, "Processing");
                    shareInstagram(getpath);
                    break;
                case SHARE_ID:
                    constantfile.snackbarcommonlinear(VideoplayerActivity.this, detail_rl, "Processing");
                    share(getpath);
                    break;
                case DOWNLOAD_ID:
                    constantfile.snackbarcommonlinear(VideoplayerActivity.this, detail_rl, getResources().getString(R.string.already_downloaded));
                    break;
            }
        }
    }

    private void download(String getpath) {
        constantfile.snackbarcommonlinear(VideoplayerActivity.this, detail_rl, getResources().getString(R.string.videos_downloaded));
        Add_todownload(getpath);
        CallAddDownload(getvideodata.getId());
        showNotification(VideoplayerActivity.this, getResources().getString(R.string.app_name), "Download Successfully", getpath);
    }

    public void shareWhatsapp(String path) {

        Uri imageUri = FileProvider.getUriForFile(VideoplayerActivity.this, VideoplayerActivity.this.getApplicationContext().getPackageName() + ".provider", new File(path));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setPackage(WHATSAPP_ID);

        Log.e("shareurllocal", " whatsapp filepath  ::  " + imageUri.toString());
        final String final_text = getResources().getString(R.string.download_more_from_link) + "\n\n" + Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName()).toString();

        shareIntent.putExtra(Intent.EXTRA_TEXT, final_text);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

        shareIntent.setType(type);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(shareIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            constantfile.snackbarcommonlinear(VideoplayerActivity.this, detail_rl, getResources().getString(R.string.whatsapp_not_installed));
        }
    }

    public void shareFacebook(String path) {
        Uri imageUri = FileProvider.getUriForFile(VideoplayerActivity.this, VideoplayerActivity.this.getApplicationContext().getPackageName() + ".provider", new File(path));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setPackage(FACEBOOK_ID);

        Log.e("shareurllocal", " whatsapp filepath  ::  " + imageUri.toString());
        final String final_text = getResources().getString(R.string.download_more_from_link) + "\n\n" + Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName()).toString();

        shareIntent.putExtra(Intent.EXTRA_TEXT, final_text);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

        shareIntent.setType(type);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(shareIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            constantfile.snackbarcommonlinear(VideoplayerActivity.this, detail_rl, getResources().getString(R.string.facebook_not_installed));
        }
    }

    public void shareInstagram(String path) {
        Uri imageUri = FileProvider.getUriForFile(VideoplayerActivity.this, VideoplayerActivity.this.getApplicationContext().getPackageName() + ".provider", new File(path));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setPackage(INSTAGRAM_ID);

        Log.e("shareurllocal", " whatsapp filepath  ::  " + imageUri.toString());
        final String final_text = getResources().getString(R.string.download_more_from_link) + "\n\n" + Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName()).toString();

        shareIntent.putExtra(Intent.EXTRA_TEXT, final_text);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

        shareIntent.setType(type);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(shareIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            constantfile.snackbarcommonlinear(VideoplayerActivity.this, detail_rl, getResources().getString(R.string.instagram_not_installed));
        }
    }

    public void share(String path) {
        Uri imageUri = FileProvider.getUriForFile(VideoplayerActivity.this, VideoplayerActivity.this.getApplicationContext().getPackageName() + ".provider", new File(path));
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);

        final String final_text = getResources().getString(R.string.download_more_from_link) + "\n\n" + Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName()).toString();

        shareIntent.putExtra(Intent.EXTRA_TEXT, final_text);
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);

        shareIntent.setType(type);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_via) + " " + getResources().getString(R.string.app_name)));
        } catch (android.content.ActivityNotFoundException ex) {
            constantfile.snackbarcommonlinear(VideoplayerActivity.this, detail_rl, getResources().getString(R.string.app_not_installed));
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void viewAdstype(String getviewtype) {
        adViewbanner = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adViewbanner.loadAd(adRequest);
    }


    private void initFullscreenDialog() {

        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    private void openFullscreenDialog() {

        ((ViewGroup) simpleExoPlayerView.getParent()).removeView(simpleExoPlayerView);
        mFullScreenDialog.addContentView(simpleExoPlayerView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ivHideControllerButton.setImageDrawable(ContextCompat.getDrawable(VideoplayerActivity.this, R.drawable.ic_fullscreen));//collapse icon
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
    }

    private void closeFullscreenDialog() {

        ((ViewGroup) simpleExoPlayerView.getParent()).removeView(simpleExoPlayerView);
        ((FrameLayout) findViewById(R.id.player_fl)).addView(simpleExoPlayerView);
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        ivHideControllerButton.setImageDrawable(ContextCompat.getDrawable(VideoplayerActivity.this, R.drawable.ic_fullscreen));//expand icon
    }


    @SuppressLint("NewApi")
    private void showNotification(Context mContext, String title, String message, String filepath) {

        // Bitmap bitmap = getBitmapFromURL(image);
        Uri contentUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".provider", new File(filepath));
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(contentUri, "video/*");
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


    private void Add_todownload(String filepath) {

        try {
            Log.e("getstorepath", "getting ::  " + filepath);
            if (db == null) {
                db = new DbAdapter(VideoplayerActivity.this);
                db.open();
            }
            String uniqueid = getvideodata.getId();
            String name = getvideodata.getVideo_name();
            String image_url = getvideodata.getVideo_image_thumb();
            String total_views = getvideodata.getTotal_views();
            String is_type = getvideodata.getIs_type();
            String date = getvideodata.getDate();
            String cat_id = getvideodata.getCat_id();
            String cat_name = getvideodata.getCategory_name();
            db.insertdownload(uniqueid, name, filepath, image_url, total_views, is_type, date, cat_id, cat_name);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

}