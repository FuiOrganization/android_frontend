package br.com.fui.fuiapplication.connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import br.com.fui.fuiapplication.data.CustomSharedPreferences;
import br.com.fui.fuiapplication.models.Experience;

/**
 * Created by guilherme on 14/10/17.
 */

public class ExperienceConnector {

    public static boolean checkin(int experience_id){
        JSONObject holder = new JSONObject();
        try {
            holder.put("experience_id", experience_id);
            ResponseMessage response = ServerConnector.sendRequest("checkin/checkin", holder, 0);
            if(response != null){
                if(response.getCode() == ServerConnector.OK_CODE){
                    return true;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Sends a request to server for four recommendations
     * @return ArrayList of recommendations
     */
    public static ArrayList<Experience> getRecommendations() {
        ArrayList<Experience> recommendations = new ArrayList<Experience>();

        ResponseMessage response = ServerConnector.sendRequest("recommendations/recommend", null, 0);
        try {
            if (response != null) {
                JSONArray jsonArray = new JSONArray(response.getBody());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    Experience e = new Experience(
                            object.getInt("id"),
                            object.getString("name"),
                            object.getString("description"),
                            object.getString("image_url"),
                            object.getBoolean("sponsored"),
                            object.getBoolean("visited")
                    );
                    recommendations.add(e);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //save on shared preferences
        if(recommendations.size() > 0){
            CustomSharedPreferences.updateRecommendations(recommendations);
        }
        
        return recommendations;
    }
}
