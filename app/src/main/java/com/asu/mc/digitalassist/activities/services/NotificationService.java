package com.asu.mc.digitalassist.activities.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.asu.mc.digitalassist.R;
import com.asu.mc.digitalassist.activities.RestaurantActivity;
import com.asu.mc.digitalassist.activities.utility.ResultActivity;

public class NotificationService extends IntentService {
    private static final String ACTION_OVERSPEED = "OVERSPEED";
    private static final String ACTION_RECOMMENDATION = "RECOMMENDATION";
    private static final int OVERSPEED_ID = 123;
    private static final int RECOMMENDATION_ID = 345;
    public NotificationService(){
        super(NotificationService.class.getSimpleName());
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public static Intent createIntentOverSpeedNotificationService(Context context){
        Intent intent = new Intent(context,NotificationService.class);
        intent.setAction(ACTION_OVERSPEED);
        return intent;
    }
    public static Intent createIntentRecommendationNotificationService(Context context){
        Intent intent = new Intent(context,NotificationService.class);
        intent.setAction(ACTION_RECOMMENDATION);
        return intent;
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(getClass().getSimpleName(), "onHandleIntent, started handling a notification event");
        String action = intent.getAction();
        if(action.equals(ACTION_OVERSPEED)){
            processSpeedNotification();
        }
        if(action.equals(ACTION_RECOMMENDATION)){
            processRecommendationNotification();
        }
    }
    private void processSpeedNotification(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.speedicon);
        mBuilder.setContentTitle("OverSpeed Alert!!!");
        mBuilder.setContentText("You have crossed the speed limit for this route");
        Intent resultIntent = new Intent(this, ResultActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ResultActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(OVERSPEED_ID,mBuilder.build());
    }
    private void processRecommendationNotification(){
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.drawable.eaticon);
        mBuilder.setContentTitle("Nearby Restaurants");
        mBuilder.setContentText("You have some nearby restaurants");
        Intent resultIntent = new Intent(this, RestaurantActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(ResultActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pendingIntent);
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(RECOMMENDATION_ID,mBuilder.build());
    }
}
