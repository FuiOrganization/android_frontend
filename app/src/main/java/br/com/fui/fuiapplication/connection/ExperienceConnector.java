package br.com.fui.fuiapplication.connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import br.com.fui.fuiapplication.models.Experience;

/**
 * Created by guilherme on 14/10/17.
 */

public class ExperienceConnector {

    public static Experience[] getRecommendations() {
        Experience recommendations[] = {};

        ResponseMessage response = ServerConnector.sendRequest("experiences/get_recommendations", null);
        try {
            if (response != null) {
                JSONArray jsonArray = new JSONArray(response.getBody());
                recommendations = new Experience[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject object = jsonArray.getJSONObject(i);
                    Experience e = new Experience(
                            object.getString("name"),
                            object.getString("description"), object.getString("image_url"),
                            object.getBoolean("sponsored")
                    );
                    recommendations[i] = e;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        return recommendations;
    }
}
