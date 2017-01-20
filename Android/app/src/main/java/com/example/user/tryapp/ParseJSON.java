package com.example.user.tryapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by user on 12/30/2016.
 */

public class ParseJSON {
    public static String[] ids;
    public static String[] links_sites;
    public static String[] comments;

    public static final String JSON_ARRAY = "result";
    public static final String KEY_ID = "id";
    public static final String KEY_LINK_SITE = "link_site";
    public static final String KEY_COMMENT = "comment";

    private JSONArray users = null;

    private String json;

    public ParseJSON(String json){
        this.json = json;
    }

    protected void parseJSON(){
        JSONObject jsonObject=null;
        try {
            jsonObject = new JSONObject(json);
            users = jsonObject.getJSONArray(JSON_ARRAY);

            ids = new String[users.length()];
            links_sites = new String[users.length()];
            comments = new String[users.length()];

            for(int i=0;i<users.length();i++){
                JSONObject jo = users.getJSONObject(i);
                ids[i] = jo.getString(KEY_ID);
                links_sites[i] = jo.getString(KEY_LINK_SITE);
                comments[i] = jo.getString(KEY_COMMENT);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
