package pack.knowyourdoctor.MainControllers;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import pack.knowyourdoctor.Constants.Numbers;
import pack.knowyourdoctor.R;


public class Controller_SplashScreen extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(Controller_SplashScreen.this, Controller_Home.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, Numbers.SPLASH_TIME_OUT);
    }

}
