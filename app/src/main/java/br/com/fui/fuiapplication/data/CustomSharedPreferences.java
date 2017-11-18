package br.com.fui.fuiapplication.data;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import br.com.fui.fuiapplication.models.Experience;

/**
 * Created by guilherme on 18/11/17.
 */

public class CustomSharedPreferences {
    public static SharedPreferences settings;
    public static final String PREFS_NAME = "Fui";

    public static void updateRecommendations(ArrayList<Experience> recommendations){
        SharedPreferences.Editor editor = CustomSharedPreferences.settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(recommendations);
        editor.putString("recommendations", json);
        editor.commit();
    }

    public static ArrayList<Experience> getRecommendations(){
        Gson gson = new Gson();
        String json = CustomSharedPreferences.settings.getString("recommendations","");
        ArrayList<Experience> obj = gson.fromJson(json, new TypeToken<ArrayList<Experience>>(){}.getType());
        return obj;
    }

}
