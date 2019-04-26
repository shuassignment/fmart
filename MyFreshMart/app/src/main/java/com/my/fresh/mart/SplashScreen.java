package com.my.fresh.mart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SplashScreen extends AppCompatActivity {
    private View mContentView;
    int Count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        getSupportActionBar().hide();
        new AsyncTaskDownloadDB(SplashScreen.this).execute();


        try {
            SharedPreferences UserToken = getSharedPreferences(Config.NUMBER_OF_TIMES_APP_OPEN, 0); // 0 - for private mode
            Count = UserToken.getInt("COUNT", 0);

        } catch (Exception e) {

            SharedPreferences pref = getSharedPreferences(Config.NUMBER_OF_TIMES_APP_OPEN, 0); // 0 - for private mode
            SharedPreferences.Editor editor = pref.edit();
            editor.putInt("COUNT", 0).apply();

        }

        SharedPreferences pref = getSharedPreferences(Config.NUMBER_OF_TIMES_APP_OPEN, 0); // 0 - for private mode
        SharedPreferences.Editor editor = pref.edit();
        if (Count==1){
            editor.putString("First_opended",Config.getCurrentTime()).apply();
        }
        Count++;
        editor.putInt("COUNT", Count).apply();

//        Config.PARIDIZ.child("Installs").child(Config.FIREBASE_USER).

        getWindow().getDecorView().setSystemUiVisibility(

                View.SYSTEM_UI_FLAG_FULLSCREEN

                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, CustomerMainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}
