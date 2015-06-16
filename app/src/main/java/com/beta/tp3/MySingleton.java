package com.beta.tp3;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jorand on 16/06/15.
 */
public class MySingleton {
    private static MySingleton ourInstance = new MySingleton();

    public static String Test() {
        return "ok";
    }

    public static String trimMessage(String json, String key){
        /*
        * http://stackoverflow.com/a/21868734/1969761
        */

        String trimmedString;

        try{
            JSONObject obj = new JSONObject(json);
            trimmedString = obj.getString(key);
        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }

        return trimmedString;
    }

    /*
    public static MySingleton getInstance() {
        return ourInstance;
    }
    */

    private MySingleton() {
    }
}
