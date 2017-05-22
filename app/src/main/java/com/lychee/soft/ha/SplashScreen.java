package com.lychee.soft.ha;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

/**
 * Created by Eli on 21/03/2017.
 */

public class SplashScreen extends Activity {

    Context context;
    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        context = this;

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(SplashScreen.this, dashboard_activity.class);
                startActivity(i);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }


}
