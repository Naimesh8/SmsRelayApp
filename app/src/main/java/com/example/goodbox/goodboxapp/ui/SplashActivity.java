package com.example.goodbox.goodboxapp.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.example.goodbox.goodboxapp.R;
import com.example.goodbox.goodboxapp.service.SyncUtils;

public class SplashActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
    private SharedPreferences mPreference;
    Intent startIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mPreference = getSharedPreferences(SyncUtils.PREF_NAME,MODE_PRIVATE);
        String url = mPreference.getString(SyncUtils.KEY_SERVER_URL,"");

        if(url != null && !TextUtils.isEmpty(url)) {

            startIntent = new Intent(SplashActivity.this, MainActivity.class);

        } else {

            startIntent = new Intent(SplashActivity.this, BoardingActivity.class);
        }


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                startActivity(startIntent);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }


}
