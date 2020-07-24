package com.techcorp.teluguvideostatus;

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

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.techcorp.teluguvideostatus.Activity.MainActivity;
import com.techcorp.teluguvideostatus.R;
import com.techcorp.teluguvideostatus.gettersetter.Item_collections;

import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.techcorp.teluguvideostatus.Constant.SHARED_NOTIFICATION_SET;


public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    private String title;
    private String message;
    public static final String NOTIFICATION_CHANNEL_ID = "10001_Telugu";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        if (remoteMessage.getNotification() != null) {
        }

        Log.e("getnotification","calling ," + remoteMessage.getData().toString());
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
            String withfilter = jsonget.replaceAll("=",":");
            JSONObject json = new JSONObject(withfilter);
            JSONObject jsonmessage = json.getJSONObject("message");
            JSONObject jsontitle = json.getJSONObject("title");

            String message = jsonmessage.optString("message");
            title = jsontitle.optString("title");
            String imageurl = "";
            if(json.has("others") && !json.isNull("others") ){
                JSONObject getvideo = json.getJSONObject("others");
                imageurl = getvideo.getString("video_image_thumb");

                Item_collections noti_item = new Item_collections();

                    noti_item.setId(getvideo.getString("id"));
                    noti_item.setVideo_name(getvideo.getString("video_name"));
                    noti_item.setVideo_url(getvideo.getString("video_url"));
                    noti_item.setVideo_image_thumb(getvideo.getString("video_image_thumb"));
                    noti_item.setTotal_views(getvideo.getString("total_views"));
                    noti_item.setIs_type(getvideo.getString("is_layout"));
                    noti_item.setDate(getvideo.getString("date"));
                    noti_item.setCat_id(getvideo.getString("cat_id"));
                    noti_item.setCategory_name(getvideo.getString("category_name"));

                Constant.Passing_item_object = new Item_collections();
                Constant.Passing_item_object = noti_item;
                Constant.Passing_from_notification = true;
            }

            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            resultIntent.putExtra("message", message);
            resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);
            if (!imageurl.equals("")  && imageurl != null){
                showNotificationWithImage(getApplicationContext(),title, message,imageurl, pendingIntent);
            }else{
                showNotification(getApplicationContext(),title, message, pendingIntent);
            }



        } catch (Exception e) {
            Log.e(TAG, "Exception: handle " + e.getMessage());
        }
    }

    @SuppressLint("NewApi")
    private void showNotificationWithImage(Context applicationContext, String title, String message,String image, PendingIntent resultIntent) {

       Bitmap bitmap = getBitmapFromURL(image);

        Notification notif = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notif = new Notification.Builder(applicationContext)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.logo_small)
                    .setColor(Color.BLACK)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_small))
                    .setStyle(new Notification.BigPictureStyle().bigPicture(bitmap))
                    .setContentIntent(resultIntent)
                    .setAutoCancel(true)
                    .setChannelId(NOTIFICATION_CHANNEL_ID)
                    .setContentText(message)
                    .build();
        } else {
            notif = new Notification.Builder(applicationContext)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.logo_small)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_small))
                    .setStyle(new Notification.BigPictureStyle().bigPicture(bitmap))
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Teluguvs_notification", importance);
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
            notif = new Notification.Builder(applicationContext)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.logo_small)
                    .setColor(Color.BLACK)
                    .setContentIntent(resultIntent)
                    .setAutoCancel(true)
                    .setChannelId(NOTIFICATION_CHANNEL_ID)
                    .setContentText(message)
                    .build();
        } else {
            notif = new Notification.Builder(applicationContext)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.logo_small)
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "Teluguvs_notification", importance);
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
