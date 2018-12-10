package com.example.back4app.barbershopapp;

import com.parse.Parse;
import com.parse.ParseInstallation;
import com.parse.facebook.ParseFacebookUtils;
import com.parse.twitter.ParseTwitterUtils;

import android.app.Application;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                // if desired
                .clientKey(getString(R.string.back4app_client_key))
                .server("https://parseapi.back4app.com/")
                .build()
        );

        ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("GCMSenderId", "277480073929");
        installation.saveInBackground();

        ParseFacebookUtils.initialize(this);
        ParseTwitterUtils.initialize("ibtbhbOqvIyEFB1X9Ll2FXJuW", "Ae2RGBNEAHJgX5HhQBRsypQCReYZXaMp9Pn7CaO06zzWoTmZQ1");
    }
}