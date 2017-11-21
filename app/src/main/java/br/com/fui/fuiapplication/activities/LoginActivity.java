package br.com.fui.fuiapplication.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import java.util.Arrays;

import br.com.fui.fuiapplication.R;
import br.com.fui.fuiapplication.connection.ServerConnector;
import br.com.fui.fuiapplication.data.Application;
import br.com.fui.fuiapplication.data.CustomSharedPreferences;
import br.com.fui.fuiapplication.data.Data;
import br.com.fui.fuiapplication.models.FacebookProfile;

public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    Intent mainIntent;
    private final int REQUEST_EXIT = 0;
    private LogInTask logInTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainIntent = new Intent(this, MainActivity.class);

        //TODO debug mode is not working properly after development of fui authentication
        //skip facebook login and fake fui authentication
        if (Application.DEBUG_MODE) {
            //set test access token
            AccessToken fakeAccessToken = new AccessToken(
                    Application.TEST_USER_ACCESS_TOKEN, getResources().getString(R.string.facebook_app_id),
                    Application.TEST_USER_ID, null, null,
                    null, null, null
            );
            AccessToken.setCurrentAccessToken(fakeAccessToken);
            //execute login task
            logInTask = new LogInTask();
            logInTask.execute((Void) null);
        }

        //verifies if user is already logged in
        if (AccessToken.getCurrentAccessToken() != null && !AccessToken.getCurrentAccessToken().isExpired()) {
            //execute login task
            logInTask = new LogInTask();
            logInTask.execute((Void) null);
        }

        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) this.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("user_status", "user_likes", "user_location", "email", "public_profile"));

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //login to fui server
                        logInTask = new LogInTask();
                        logInTask.execute((Void) null);
                    }

                    @Override
                    public void onCancel() {
                        // App code
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                    }
                });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    //AsyncTask to deal with login function
    private class LogInTask extends AsyncTask<Void, Void, Integer>{

        @Override
        protected Integer doInBackground(Void... voids) {

            //verify if has saved account
            if(ServerConnector.hasSavedAccount(LoginActivity.this)){
                return ServerConnector.AUTHORIZATION_CODE;
            }
            //try to login to server
            return ServerConnector.facebookLogin(0);
        }

        @Override
        protected void onPostExecute(final Integer code) {
            //if successfully, go to main activity
            if(code == ServerConnector.AUTHORIZATION_CODE){
                //check if cache fails
                if(!CustomSharedPreferences.loadFacebookProfile()) {
                    //update local facebook data
                    Data.getFacebookData();
                }
                startActivity(mainIntent);
                finish();
            }
        }
    }


}
