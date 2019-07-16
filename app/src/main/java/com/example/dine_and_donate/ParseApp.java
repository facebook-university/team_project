package com.example.dine_and_donate;

import android.app.Application;

import com.example.dine_and_donate.Models.Consumer;
import com.example.dine_and_donate.Models.Event;
import com.example.dine_and_donate.Models.Organization;
import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ParseApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

//        ParseUser.registerSubclass(Consumer.class);
//        ParseObject.registerSubclass(Organization.class);
        ParseObject.registerSubclass(Event.class);

        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("dineAndDonate")
                .clientKey("zb7UvDfk5fu95ZQETRTsKqSs")
                .server("http://dine-and-donate.herokuapp.com/parse")
                .build();
        Parse.initialize(configuration);
    }
}
