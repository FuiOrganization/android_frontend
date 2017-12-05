package br.com.fui.fuiapplication.connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import br.com.fui.fuiapplication.data.CustomSharedPreferences;
import br.com.fui.fuiapplication.data.Data;
import br.com.fui.fuiapplication.models.Checkin;
import br.com.fui.fuiapplication.models.Experience;

/**
 * Created by guilherme on 14/10/17.
 */

public class ExperienceConnector {

    /**
     * Sends a request to server for user check-in history
     * @return ArrayList of check-ins
     */
    public static ArrayList<Checkin> getHistory(){
        //deal with date
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));

        ArrayList<Checkin> history = new ArrayList<>();

        ResponseMessage response = ServerConnector.sendRequest("checkin/history", null, 0);
        try {
            if (response != null) {
                JSONArray jsonArray = new JSONArray(response.getBody());

                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject checkin = jsonArray.getJSONObject(i);
                    JSONObject experience = checkin.getJSONObject("experience");
                    Checkin c = new Checkin(
                            checkin.getInt("experience_id"),
                            experience.getString("name"),
                            new Date(sdf.parse(checkin.getString("created_at").substring(0, 19)).getTime())
                    );
                    history.add(c);
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //save on shared preferences
        if(history.size() > 0){
            CustomSharedPreferences.updateHistory(history);
        }

        return history;
    }

    /**
     * Sends a check-in request to server
     * @param experience_id experience ID
     * @return whether the check-in was successfully or not
     */
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
     * Sends a request to server for four random recommendations
     * @return ArrayList of recommendations
     */
    public static ArrayList<Experience> getRecommendations() {
        ArrayList<Experience> recommendations = new ArrayList<>();

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
