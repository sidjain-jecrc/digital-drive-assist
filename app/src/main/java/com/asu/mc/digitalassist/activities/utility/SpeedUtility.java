package com.asu.mc.digitalassist.activities.utility;

import android.util.Log;

/**
 * Created by Siddharth on 4/8/2017.
 */

public class SpeedUtility {

    private static String TAG = SpeedUtility.class.getSimpleName();

    private static long calculateDistanceInMeters(double lat1, double lng1, double lat2, double lng2) {
        Log.d(TAG, "Inside calculateDistanceInMeters");

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        long distanceInMeters = Math.round(6371000 * c);
        return distanceInMeters;
    }

    private static long calculateDistanceInKms(double lat1, double lng1, double lat2, double lng2) {
        Log.d(TAG, "Inside calculateDistanceInKms");

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        long distanceInKms = Math.round(6371 * c);
        return distanceInKms;
    }

    private static long calculateDistanceInMiles(double lat1, double lng1, double lat2, double lng2) {
        Log.d(TAG, "Inside calculateDistanceInMiles");

        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        long distanceInMiles = Math.round(3959 * c);
        return distanceInMiles;
    }

    private static float calculateSpeedInMph(long distance, long hours){
        Log.d(TAG, "Inside calculateSpeedInMph");

        float speedInMph = distance/hours;
        return speedInMph;
    }

}
