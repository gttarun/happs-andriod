package ee364e.happs;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by Young on 2017-02-17.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    Intent resultIntent;
    String title;
    String body;
    String id;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        title = remoteMessage.getData().get("title");
        body = remoteMessage.getData().get("body");
        id = remoteMessage.getData().get("eventURL");
        Log.d("firebase", title);
        makeNotification(title, body);
    }

    void makeNotification(String title, String body) {

        resultIntent = new Intent(getApplicationContext() , EventLayoutActivity.class);
        resultIntent.putExtra("event_id", id);
        //EventBus.getDefault().postSticky(id);
        PendingIntent resultPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);


        NotificationCompat.Builder mBuilder = (android.support.v7.app.NotificationCompat.Builder) new android.support.v7.app.NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.happs)
                .setContentTitle(title)
                .setAutoCancel(true)
                .setSound(alarmSound)
                .setContentIntent(resultPendingIntent)
                .setContentText(body);

        int mNotificationId = 1;
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }


}
