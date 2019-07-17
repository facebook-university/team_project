package com.example.dine_and_donate;

import android.location.Location;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class YelpService {

    public static final String YELP_API = "UwrFPMfLEi9SPqorMdsGrNcezJDJ7FwB5sZkbovnv3c3lcqCsXXWcxvGIT6j9b37bn-9Rw_C_XJJV2QCK3yl0Si_vwE1r_s4oiXOY_XQQsnI87sG-v3EdbOo1nsvXXYx";
    public static final String YELP_BASE_URL = "https://api.yelp.com/v3/businesses/search?term=restaurants";
    public static final String YELP_LOCATION_QUERY_PARAMETER = "location";

    public static void findRestaurants(String location, Callback callback) {

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(YELP_BASE_URL).newBuilder();
        urlBuilder.addQueryParameter(YELP_LOCATION_QUERY_PARAMETER, location);
        String url = urlBuilder.build().toString();

        Request request= new Request.Builder()
                .url(url)
                .header("Authorization", YELP_API)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}