package br.com.fui.fuiapplication.connection;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;

/**
 * Created by guilherme on 13/10/17.
 */

public class Connection {

    public static final int AUTHORIZATION_CODE = 201;
    public static final int SERVICE_UNAVAILABLE_CODE = 503;
    public static final int UNAUTHORIZED_CODE = 404;
    public static final int NO_CONNECTION_CODE = -1;
    //timeout in milliseconds
    private static final int timeout = 5000;
    private static final String c9IP = "";
    private static final String localIP = "http://192.168.25.6:3000/";
    private static String serverIP = localIP;
    private static String token = "";
    private static boolean validToken = false;


    /**
     * Sends a request to the server
     * @param action    the server action requested
     * @param info      json data
     * @return the ResponseMessage object for the request. Returns null if connection is unsuccessful.
     */
    public static ResponseMessage sendRequest(String action, JSONObject info){
        ResponseMessage response = null;

        try {
            URL url = new URL(Connection.serverIP + action);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");
            connection.setConnectTimeout(Connection.timeout);
            connection.setReadTimeout(Connection.timeout);
            if (Connection.validToken) {
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

                //unauthorized request and user was previously logged in
                if (connection.getResponseCode() == UNAUTHORIZED_CODE && validToken) {
                    validToken = false;
                    //tries to refresh token
                    Log.d("request", "Refreshing Token");
                    //int code = login(email, password);
                    /*if (code == AUTHORIZATION_CODE) {
                        // try again
                        return sendRequest(action, info);
                    }*/
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


}
