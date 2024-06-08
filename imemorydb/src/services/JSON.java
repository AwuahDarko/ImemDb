package services;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JSON {
    public static String convertMapToJson(HashMap<String, String> elements) {

        Gson gson = new Gson();
        Type gsonType = new TypeToken<HashMap<String, String>>(){}.getType();
        return gson.toJson(elements,gsonType);
    }

    public static String convertArrayToJson(ArrayList<HashMap<String, String>> elements) {

        Gson gson = new Gson();
        Type gsonType = new TypeToken<ArrayList<HashMap<String, String>>>(){}.getType();
        return gson.toJson(elements,gsonType);
    }

    public static Map<String, String> convertJsonToMap(String json){

        Gson gson = new Gson();
        Type empMapType = new TypeToken<Map<String, String>>() {}.getType();
        return gson.fromJson(json, empMapType);
    }

    public static ArrayList<Map<String, String>> convertJsonToArray(String json){

        Gson gson = new Gson();
        Type empArrayType = new TypeToken<ArrayList<Map<String, String>>>() {}.getType();
        return gson.fromJson(json, empArrayType);
    }
}
