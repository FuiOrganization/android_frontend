package br.com.fui.fuiapplication.data;

import android.os.Bundle;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.LoggingBehavior;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.com.fui.fuiapplication.models.Experience;

/**
 * Created by guilherme on 18/10/17.
 */

public class Data {
    public static ArrayList<Experience> recommendations = null;
    public static String name = "";
    public static String email = "";
    public static String profilePic = "";

    public static void getFacebookData() {
        FacebookSdk.addLoggingBehavior(LoggingBehavior.REQUESTS);
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(
                            JSONObject object,
                            GraphResponse response) {
                        try {
                            if (object != null) {
                                Data.name = object.getString("name");
                                Data.email = object.getString("email");
                                Data.profilePic = object.getJSONObject("picture").getJSONObject("data").getString("url");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "name, email, picture.type(large)");
        request.setParameters(parameters);
        request.executeAsync();
    }
}
