package app.com.rajneesh.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import app.com.rajneesh.R;

/*
* Display Splash Screen with youtube logo when the APP is launched
* */
public class SplashScreen extends AppCompatActivity {

    private int SPLASH_SCREEN_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // Start your app main activity
                Intent mIntent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(mIntent);

                // close this activity
                finish();
            }
        }, SPLASH_SCREEN_TIME_OUT);

    }
}
