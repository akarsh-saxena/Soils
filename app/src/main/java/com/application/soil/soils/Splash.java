package com.application.soil.soils;

import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.VideoView;

public class Splash extends AppCompatActivity {
    private static int SPLASH_TIME_OUT = 5000;
    ImageView splashImageView;
    //String uriPath = "android.resource://com.ecample.lenovo.soils" + R.mipmap.farm2;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        splashImageView= findViewById(R.id.imageView2);
        progressBar=findViewById(R.id.progressBar2);


        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {

                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(Splash.this, MainActivity.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);

    }}
