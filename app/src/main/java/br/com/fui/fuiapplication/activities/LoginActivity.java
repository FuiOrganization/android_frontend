package br.com.fui.fuiapplication.activities;

import android.content.Intent;
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

public class LoginActivity extends AppCompatActivity {
    CallbackManager callbackManager;
    Intent mainIntent;
    private final int REQUEST_EXIT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainIntent = new Intent(this, MainActivity.class);

        //verifies if user is already logged in
        if(AccessToken.getCurrentAccessToken()!= null && !AccessToken.getCurrentAccessToken().isExpired()){
            startActivity(mainIntent);
            finish();
        }

        setContentView(R.layout.activity_login);
        callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) this.findViewById(R.id.login_button);
        loginButton.setReadPermissions(Arrays.asList("user_status", "user_likes", "user_location"));

        loginButton.registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        startActivityForResult(mainIntent, REQUEST_EXIT);
                        LoginActivity.this.finish();
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


}
