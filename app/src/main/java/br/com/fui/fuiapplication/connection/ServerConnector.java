package br.com.fui.fuiapplication.connection;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.AccessToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

import br.com.fui.fuiapplication.data.Application;

/**
 * Created by guilherme on 13/10/17.
 */

public class ServerConnector {

    public static final int OK_CODE = 200;
    public static final int AUTHORIZATION_CODE = 201;
    public static final int UNAUTHORIZED_CODE = 401;
    public static final int SERVER_ERROR_CODE = 500;
    public static final int SERVICE_UNAVAILABLE_CODE = 503;
    public static final int NO_CONNECTION_CODE = -1;
    //timeout in milliseconds
    private static final int timeout = 5000;
    private static final String c9IP = "";
    private static final String localIP = "http://192.168.25.155:3000/";
    private static final String herokuIP = "https://fuiserver.herokuapp.com/";
    private static final String developmentIP = localIP;
    private static final String productionIP = herokuIP;
    private static String token = "";
    private static boolean validToken = false;
    private static String facebook_identifier = "";
    private static Context appContext;
    private static SharedPreferences settings;
    public static final String PREFS_NAME = "Fui";
    //limit multiple request attempts
    public static final int maxAttempts = 3;


    public static String getServerIP(){
        if(Application.PRODUCTION){
            return productionIP;
        }else{
            return developmentIP;
        }
    }

    /**
     * Sends a request to the server
     * @param action    the server action requested
     * @param info      json data
     * @return the ResponseMessage object for the request. Returns null if connection is unsuccessful.
     */
    public static ResponseMessage sendRequest(String action, JSONObject info, int attempt){
        //new attempt
        attempt++;
        ResponseMessage response = null;

        try {
            URL url = new URL(ServerConnector.getServerIP() + action);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(ServerConnector.timeout);
            connection.setReadTimeout(ServerConnector.timeout);
            if (ServerConnector.validToken) {
                connection.setRequestProperty("Authorization", token);
            }
            connection.connect();

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            if(info!=null){
                wr.writeBytes(info.toString());
            }else{
                wr.writeBytes("");
            }

            wr.flush();
            wr.close();

            BufferedReader br;

            Log.d("request_fui", connection.getResponseCode() + "");
            Log.d("request_fui", connection.getResponseMessage());

            // checks if response code is ok or an error code
            if (200 <= connection.getResponseCode() && connection.getResponseCode() <= 299) {
                br = new BufferedReader(new InputStreamReader((connection.getInputStream())));
            } else {
                br = new BufferedReader(new InputStreamReader((connection.getErrorStream())));

                //unauthorized request, user was previously logged in and max attempts not reached out yet
                if (connection.getResponseCode() == UNAUTHORIZED_CODE && validToken && attempt < maxAttempts) {
                    validToken = false;
                    //tries to refresh token
                    Log.d("request", "Refreshing Token");
                    int code = facebookLogin(attempt);
                    if (code == AUTHORIZATION_CODE) {
                        // try again
                        return sendRequest(action, info, attempt);
                    }
                }

            }

            StringBuilder sb = new StringBuilder();
            String output;

            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            response = new ResponseMessage(connection.getResponseCode(), connection.getResponseMessage(),
                    sb.toString());

            Log.d("request", sb.toString());

        }catch(SocketTimeoutException e){
            response = new ResponseMessage(NO_CONNECTION_CODE, "", "");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    /**
     * Sends an authorization request to the server.
     * @return the request return code
     */
    public static int facebookLogin(int attempts){
        String user_token, facebook_identifier;

        if(AccessToken.getCurrentAccessToken()!=null){
            user_token = AccessToken.getCurrentAccessToken().getToken();
            facebook_identifier = AccessToken.getCurrentAccessToken().getUserId();
        }else{
            return NO_CONNECTION_CODE;
        }

        try{
            JSONObject holder = new JSONObject();
            JSONObject user = new JSONObject();
            user.put("facebook_identifier", facebook_identifier);
            user.put("access_token", user_token);
            holder.put("auth", user);

            ResponseMessage response = ServerConnector.sendRequest("facebook_user_token", holder, attempts);

            if (response != null) {
                if(response.getCode() == AUTHORIZATION_CODE){
                    JSONObject responseBody = new JSONObject(response.getBody());
                    if(responseBody.get("jwt") != null){
                        ServerConnector.updateData(facebook_identifier, responseBody.get("jwt").toString());
                        Log.d("request", "token: " + ServerConnector.token);
                    }
                }
                return response.getCode();
            } else {
                return NO_CONNECTION_CODE;
            }

        }catch(JSONException e){
            e.printStackTrace();
        }

        return NO_CONNECTION_CODE;
    }


    /**
     * Saves account's information on Shared Preferences.
     * @param facebook_identifier   facebook id
     * @param jwt       user's json web token
     */
    public static void updateData(String facebook_identifier,  String jwt){
        ServerConnector.token = jwt;
        ServerConnector.validToken = true;
        ServerConnector.facebook_identifier = facebook_identifier;
        SharedPreferences.Editor editor = ServerConnector.settings.edit();
        editor.putString("facebook_identifier", facebook_identifier);
        editor.putString("token", jwt);
        editor.commit();
    }

    /**
     * Accesses Shared Preferences data and updates Connection's attributes.
     * Returns whether there's an account saved or not
     * @return  whether there's a saved account or not
     */
    public static boolean retrieveData(){
        if(ServerConnector.settings.contains("facebook_identifier")){
            ServerConnector.facebook_identifier = ServerConnector.settings.getString("facebook_identifier", "");
            ServerConnector.token = ServerConnector.settings.getString("token", "");
            validToken = true;
            return true;
        }
        return false;
    }

    /**
     *  Verifies if application has a stored account by retrieving saved information
     *  such as user's facebook id and fui token
     * @param   context the application context
     * @return          whether the application has a stored account or not
     */
    public static boolean hasSavedAccount(Context context){
        ServerConnector.appContext = context;
        ServerConnector.settings = appContext.getSharedPreferences(PREFS_NAME, 0);
        return retrieveData();
    }


}
