package com.example.dine_and_donate.Models;

import com.google.firebase.database.IgnoreExtraProperties;

import org.json.JSONException;
import org.json.JSONObject;


@IgnoreExtraProperties
public class Restaurant {

    public double amountRaised;
    public int pastEvents;

    public static String format(JSONObject object) throws JSONException {
        StringBuilder sb = new StringBuilder();
        sb.append(object.getString("name"));
        sb.append("\n");
        JSONObject location = object.getJSONObject("location");
        sb.append(location.getString("address1"));
        sb.append(", ");
        sb.append(location.getString("city"));
        sb.append(", ");
        sb.append(location.getString("state"));
        return sb.toString();
    }
}
