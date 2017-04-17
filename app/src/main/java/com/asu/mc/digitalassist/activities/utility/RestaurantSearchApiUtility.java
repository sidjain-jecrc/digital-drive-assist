package com.asu.mc.digitalassist.activities.utility;

import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

/**
 * Created by Siddharth on 4/17/2017.
 */

public class RestaurantSearchApiUtility {
    private static final int SEARCH_LIMIT = 10;
    // Set search radius as maximum of 4 miles(approx. 6478 meters)
    private static final int SEARCH_RADIUS = 6478;
    // To retrieve results sorted by distance
    private static final int SORT_BY_DISTANCE = 1;
    private static final int SORT_BY_RATING = 2;

    // Store search API Url
    private final String restaurantSearchApiUrl = "https://api.yelp.com/v2/search";
    OAuthService service;
    Token accessToken;

    // Setup the Search API OAuth credentials.
    public RestaurantSearchApiUtility(String consumerKey, String consumerSecret, String token, String tokenSecret) {
        this.service = new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(consumerKey).apiSecret(consumerSecret).build();
        this.accessToken = new Token(token, tokenSecret);
    }

    // Creates and sends a request to the Search API by term and location.
    public String searchForNearbyRestaurantByLocation(String zipcodeOrCityName) {
        OAuthRequest request = new OAuthRequest(Verb.GET, restaurantSearchApiUrl);
        request.addQuerystringParameter("term", "food");  // Change here: Restaurant / Food (currently its Food)
        request.addQuerystringParameter("location", zipcodeOrCityName);
        request.addQuerystringParameter("sort", String.valueOf(SORT_BY_RATING));
        request.addQuerystringParameter("radius_filter", String.valueOf(SEARCH_RADIUS));
        request.addQuerystringParameter("limit", String.valueOf(SEARCH_LIMIT));
        return sendRequestAndGetResponse(request);
    }

    // Sends an OAuthRequest to Search API to fetch Response
    private String sendRequestAndGetResponse(OAuthRequest request) {
        this.service.signRequest(this.accessToken, request);
        Response response = request.send();
        return response.getBody();
    }

}

