package com.example.dine_and_donate.Models;

import org.json.JSONException;
import org.json.JSONObject;

public class Restaurant {

    public static String format(JSONObject object) throws JSONException {
        StringBuilder sb = new StringBuilder();
        sb.append(object.getString("name") + "\n");
        JSONObject location = object.getJSONObject("location");
        sb.append(location.getString("address1") + ", ");
        sb.append(location.getString("city") + ", ");
        sb.append(location.getString("state"));
        return sb.toString();
    }
}
