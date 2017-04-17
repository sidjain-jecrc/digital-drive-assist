package com.asu.mc.digitalassist.activities.utility;

import android.location.Location;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

/**
 * Created by Siddharth on 4/16/2017.
 */

public class LocationUtility {
    protected static String TAG = LocationUtility.class.getSimpleName();

    protected static Location mLastLocation;

    // return lat,long coordinates of current location
    public static String getCurrentKnownLocation(GoogleApiClient mGoogleApiClient){
        Log.d(TAG, "Inside getCurrentKnownLocation method");
        String latLongString = null;
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                Log.d(TAG, "Setting lat, long coordinates");
                latLongString = String.valueOf(mLastLocation.getLatitude()) + "," + String.valueOf(mLastLocation.getLongitude());
            }

        } catch (SecurityException e) {
            Log.e(TAG, "Error: " + e);
        }

        Log.i(TAG, "Current location being returned: " + latLongString);
        return latLongString;
    }

    protected void createLocationRequest() {
        Log.d(TAG, "Inside createLocationRequest method");

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


}
