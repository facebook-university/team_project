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
    public static final String YELP_LATITUDE_QUERY_PARAMETER = "latitude";
    public static final String YELP_LONGITUDE_QUERY_PARAMETER = "longitude";

    public static void findRestaurants(String longitude, String latitude, Callback callback) {

        OkHttpClient client = new OkHttpClient.Builder()
                .build();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(YELP_BASE_URL).newBuilder();
        urlBuilder.addQueryParameter(YELP_LATITUDE_QUERY_PARAMETER, latitude);
        urlBuilder.addQueryParameter(YELP_LONGITUDE_QUERY_PARAMETER, longitude);

        String url = urlBuilder.build().toString();
        System.out.println("URL: " + url);

        Request request= new Request.Builder()
                .url(url)
                .get()
                .header("Authorization", "Bearer " + YELP_API)
                .build();

        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}