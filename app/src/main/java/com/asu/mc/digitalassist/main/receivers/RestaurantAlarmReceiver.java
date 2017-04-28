package com.asu.mc.digitalassist.main.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.asu.mc.digitalassist.main.services.LocationTrackerService;

/**
 * Created by Siddharth on 4/27/2017.
 */

public class RestaurantAlarmReceiver extends BroadcastReceiver {

    protected static final String TAG = RestaurantAlarmReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "onReceive");
        String homeZipCode = intent.getStringExtra("EXTRA_HOME_ZIP");
        LocationTrackerService.startLocationTrackerAction(context, homeZipCode);
    }
}
