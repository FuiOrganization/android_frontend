package br.com.fui.fuiapplication.data;

import android.content.SharedPreferences;
import android.media.FaceDetector;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import br.com.fui.fuiapplication.models.Experience;
import br.com.fui.fuiapplication.models.FacebookProfile;

/**
 * Created by guilherme on 18/11/17.
 */

public class CustomSharedPreferences {
    public static SharedPreferences settings;
    public static final String PREFS_NAME = "Fui";
    public static final String FACEBOOK_PROFILE_KEY = "facebook_profile";
    public static final String RECOMMENDATIONS = "recommendations";

    public static void clear(){
        CustomSharedPreferences.settings.edit().clear().commit();
    }

    public static void updateFacebookProfile(FacebookProfile profile){
        SharedPreferences.Editor editor = CustomSharedPreferences.settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(profile);
        editor.putString(FACEBOOK_PROFILE_KEY, json);
        editor.commit();
    }

    public static boolean loadFacebookProfile(){
        Gson gson = new Gson();
        String json = CustomSharedPreferences.settings.getString(FACEBOOK_PROFILE_KEY,"");
        FacebookProfile obj = gson.fromJson(json, FacebookProfile.class);
        if(obj != null){
            Data.facebookUserId = obj.getId();
            Data.name = obj.getName();
            Data.email = obj.getEmail();
            Data.profilePic = obj.getImageUrl();
            return true;
        }
        return false;
    }

    public static void updateRecommendations(ArrayList<Experience> recommendations){
        SharedPreferences.Editor editor = CustomSharedPreferences.settings.edit();
        Gson gson = new Gson();
        String json = gson.toJson(recommendations);
        editor.putString(RECOMMENDATIONS, json);
        editor.commit();
    }

    public static ArrayList<Experience> getRecommendations(){
        Gson gson = new Gson();
        String json = CustomSharedPreferences.settings.getString(RECOMMENDATIONS,"");
        ArrayList<Experience> obj = gson.fromJson(json, new TypeToken<ArrayList<Experience>>(){}.getType());
        return obj;
    }

}
