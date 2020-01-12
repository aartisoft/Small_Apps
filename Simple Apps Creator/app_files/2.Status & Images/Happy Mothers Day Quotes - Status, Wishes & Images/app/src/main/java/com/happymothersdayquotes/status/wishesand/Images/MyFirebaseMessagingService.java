package com.happymothersdayquotes.status.wishesand.Images;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.happymothersdayquotes.status.wishesand.Images.Activity.MainActivity;
import com.happymothersdayquotes.status.wishesand.Images.gettersetter.Item_videos;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.happymothersdayquotes.status.wishesand.Images.Constant.NOTIFICATION_CHANNEL_ID;
import static com.happymothersdayquotes.status.wishesand.Images.Constant.SHARED_NOTIFICATION_SET;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private String title;
    private String message;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null) {
        }

        Log.e("getnotification", "calling ," + remoteMessage.getData().toString());
        PrefManager prf = new PrefManager(getApplicationContext());
        if (prf.getBooleannotification(SHARED_NOTIFICATION_SET)) {
            String msg = remoteMessage.getData().toString().replace("{data=", "");
            try {
                handleDataMessage(msg);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
    }


    @SuppressLint("NewApi")
    private void handleDataMessage(String jsonget) {
        try {
            String withfilter = jsonget.replaceAll("=", ":");
            JSONObject json = new JSONObject(withfilter);
            JSONObject jsonmessage = json.getJSONObject("message");
            JSONObject jsontitle = json.getJSONObject("title");

            String message = jsonmessage.optString("message");
            title = jsontitle.optString("title");
            String imageurl = "";
            String notif_type="";
            if (json.has("others") && !json.isNull("others")) {
                JSONObject getothers = json.getJSONObject("others");
                notif_type = getothers.getString("media_type");
                if (notif_type != null && notif_type.equals("video")){
                    imageurl = getothers.getString("video_image_thumb");

                    Item_videos noti_item = new Item_videos();
                    noti_item.setId(getothers.getString("id"));
                    noti_item.setVideo_name(getothers.getString("video_name"));
                    noti_item.setVideo_url(getothers.getString("video_url"));
                    noti_item.setVideo_image_thumb(getothers.getString("video_image_thumb"));
                    noti_item.setTotal_views(getothers.getString("total_views"));
                    noti_item.setIs_type(getothers.getString("is_type"));
                    noti_item.setDate(getothers.getString("date"));
                    noti_item.setCat_id(getothers.getString("cat_id"));
                    noti_item.setCategory_name(getothers.getString("category_name"));

                    Constant.Passing_from_notifi_type = notif_type;
                    Constant.Passing_item_obj_video = new Item_videos();
                    Constant.Passing_item_obj_video = noti_item;
                    Constant.Passing_from_notification = true;

                }else if (notif_type != null && notif_type.equals("image")){

                    imageurl = getothers.getString("image");
                    Constant.Passing_from_notifi_type = notif_type;
                    Constant.Passing_from_notification = true;
                }

            }

            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            resultIntent.putExtra("message", message);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);
            if (!imageurl.equals("") && imageurl != null) {
                showNotificationWithImage(getApplicationContext(), title, message, imageurl, pendingIntent);
            } else {
                showNotification(getApplicationContext(), title, message, pendingIntent);
            }


        } catch (Exception e) {
            Log.e(TAG, "Exception: handle " + e.getMessage());
        }
    }

    @SuppressLint("NewApi")
    private void showNotificationWithImage(Context applicationContext, String title, String message, String image, PendingIntent resultIntent) {

        Bitmap bitmap = getBitmapFromURL(image);

        Notification notif = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notif = new NotificationCompat.Builder(applicationContext)
                    .setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setColor(Color.BLACK)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                    .setContentIntent(resultIntent)
                    .setAutoCancel(true)
                    .setChannelId(NOTIFICATION_CHANNEL_ID)
                    .setContentText(message)
                    .build();
        } else {
            notif = new NotificationCompat.Builder(applicationContext)
                    .setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap))
                    .setContentIntent(resultIntent)
                    .setAutoCancel(true)
                    .setContentText(message)
                    .build();

        }
//        try {
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//            r.play();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        NotificationManager notificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, Constant.notification_name, importance);
            //notificationChannel.enableVibration(true);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }
        //notif.defaults |= Notification.DEFAULT_VIBRATE;
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Constant.NOTIFICATION_ID_BIG_IMAGE, notif);

    }

    @SuppressLint("NewApi")
    private void showNotification(Context applicationContext, String title, String message, PendingIntent resultIntent) {

        Notification notif = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notif = new NotificationCompat.Builder(applicationContext)
                    .setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setColor(Color.BLACK)
                    .setContentIntent(resultIntent)
                    .setAutoCancel(true)
                    .setChannelId(NOTIFICATION_CHANNEL_ID)
                    .setContentText(message)
                    .build();
        } else {
            notif = new NotificationCompat.Builder(applicationContext)
                    .setContentTitle(title)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(resultIntent)
                    .setAutoCancel(true)
                    .setContentText(message)
                    .build();

        }
//        try {
//            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
//            r.play();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        NotificationManager notificationManager = (NotificationManager) applicationContext.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, Constant.notification_name, importance);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(notificationChannel);
        }
        notif.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(Constant.NOTIFICATION_ID, notif);

    }


    private Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        sendRegistrationToServer(token);
    }


    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }


}
