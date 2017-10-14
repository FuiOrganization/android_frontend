package br.com.fui.fuiapplication.connection;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import br.com.fui.fuiapplication.models.Experience;

/**
 * Created by guilherme on 14/10/17.
 */

public class ExperienceConnector {

    public static Experience[] getRecommendations(){
        Experience recommendations[] = {};
        try {
            ResponseMessage response = Connection.sendRequest("experiences/get_recommendations", new JSONObject(""));
            Log.d("experience", response.getBody());
            Log.d("experience", response.getMessage());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return recommendations;
    }
}
