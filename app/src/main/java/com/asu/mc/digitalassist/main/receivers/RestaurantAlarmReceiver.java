package com.asu.mc.digitalassist.main.receivers;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.Toast;

import com.asu.mc.digitalassist.main.services.RestaurantRecommendationService;
import com.asu.mc.digitalassist.main.utility.UserProfileDbHelper;


/**
 * Created by Siddharth on 4/27/2017.
 */

public class RestaurantAlarmReceiver extends WakefulBroadcastReceiver {

    protected static final String TAG = RestaurantAlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Alarm received");

        String homeZipCode = intent.getStringExtra("EXTRA_HOME_ZIP");
        RestaurantRecommendationService.startLocationTrackerAction(context, homeZipCode);
    }

}
