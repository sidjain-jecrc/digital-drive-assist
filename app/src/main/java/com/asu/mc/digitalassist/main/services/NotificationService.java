package com.asu.mc.digitalassist.main.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.asu.mc.digitalassist.R;
import com.asu.mc.digitalassist.main.activities.OverSpeedActivity;
import com.asu.mc.digitalassist.main.activities.RestaurantActivity;

public class NotificationService extends IntentService {

    protected static final String TAG = NotificationService.class.getSimpleName();

    private static final String ACTION_OVERSPEED = "OVERSPEED";
    private static final String ACTION_RECOMMENDATION = "RECOMMENDATION";
    private static final int OVERSPEED_ID = 123;
    private static final int RECOMMENDATION_ID = 345;

    public NotificationService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.d(TAG, "onHandleIntent, started handling a notification event");
        String action = intent.getAction();

        if (action.equals(ACTION_OVERSPEED)) {
            processSpeedNotification();
        }
        if (action.equals(ACTION_RECOMMENDATION)) {
            processRecommendationNotification();
        }
    }

    public static Intent createIntentOverSpeedNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_OVERSPEED);
        return intent;
    }

    public static Intent createIntentRecommendationNotificationService(Context context) {
        Intent intent = new Intent(context, NotificationService.class);
        intent.setAction(ACTION_RECOMMENDATION);
        return intent;
    }

    private void processSpeedNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.speedicon);
        mBuilder.setContentTitle("OverSpeed Alert!!!");
        mBuilder.setContentText("You have crossed the speed limit for this route");
   //     mBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        Intent resultIntent = new Intent(this, OverSpeedActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(OverSpeedActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(OVERSPEED_ID, mBuilder.build());
    }

    private void processRecommendationNotification() {
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.eaticon);
        mBuilder.setContentTitle("Nearby Restaurants");
        mBuilder.setContentText("You have some nearby restaurants");
 //       mBuilder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);
        Intent resultIntent = new Intent(this, RestaurantActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(RestaurantActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(RECOMMENDATION_ID, mBuilder.build());
    }
}
