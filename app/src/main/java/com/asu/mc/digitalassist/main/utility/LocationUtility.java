package com.asu.mc.digitalassist.main.utility;

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

    // return current location
    public static Location getCurrentKnownLocation(GoogleApiClient mGoogleApiClient) {
        Log.d(TAG, "Inside getCurrentKnownLocation method");
        String latLongString = null;
        try {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } catch (SecurityException e) {
            Log.e(TAG, "Error: " + e);
        }

        Log.i(TAG, "Current location being returned: " + mLastLocation);
        return mLastLocation;
    }

    protected void createLocationRequest(GoogleApiClient mGoogleApiClient) {
        Log.d(TAG, "Inside createLocationRequest method");

        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }


}
