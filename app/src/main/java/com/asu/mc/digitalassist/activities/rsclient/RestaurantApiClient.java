package com.asu.mc.digitalassist.activities.rsclient;

import com.asu.mc.digitalassist.activities.models.Restaurant;
import com.asu.mc.digitalassist.activities.utility.RestaurantSearchApiUtility;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Siddharth on 4/17/2017.
 */

public class RestaurantApiClient {

    private static final String CONSUMER_KEY = "0g_t1_QDCB1I2vm_eNtClg";
    private static final String CONSUMER_SECRET = "JUgGx3X6vNme9jcgxAcSryUdPEQ";
    private static final String TOKEN = "xVgENShX8UlT_Xci_fW8VB_kHchlfV1w";
    private static final String TOKEN_SECRET = "I6z_giUHlzqNqmCEkEVL1QMytXU";

    protected List<Restaurant> getNearbyRestaurantList(String zipCodeorCityName) {

        RestaurantSearchApiUtility utilObject = new RestaurantSearchApiUtility(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
        String responseString = utilObject.searchForNearbyRestaurantByLocation(zipCodeorCityName);
        List<Restaurant> responseList = new ArrayList<>();
        JSONParser parser = new JSONParser();
        try {

            // parsing response string using simple json parser
            JSONObject jsonObject = (JSONObject) parser.parse(responseString);
            long numOfRestaurantsNearby = (long) jsonObject.get("total");

            // Checking if there is at least one store in the vicinity
            if (numOfRestaurantsNearby > 0) {
                JSONArray arrayObject = (JSONArray) jsonObject.get("businesses");

                for (Object obj : arrayObject) {
                    JSONObject innerBusinessObject = (JSONObject) obj;
                    boolean isClosed = (boolean) innerBusinessObject.get("is_closed");
                    if (!isClosed) {

                        String name = (String) innerBusinessObject.get("name");
                        String mobileUrl = (String) (innerBusinessObject.get("mobile_url") != null ? innerBusinessObject.get("mobile_url") : innerBusinessObject.get("url"));
                        String ratings = String.valueOf(innerBusinessObject.get("rating"));
                        String contact = String.valueOf(innerBusinessObject.get("phone"));
                        String category = (String) ((JSONArray) innerBusinessObject.get("categories")).get(0);
                        responseList.add(new Restaurant(name, mobileUrl, ratings, contact, category));
                    }
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return responseList;
    }
}
