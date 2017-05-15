package com.risid.urp;

import android.app.Activity;
import android.content.Intent;

import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;



/**
 * Created by risid on 2016/3/19.
 */
public class SplashActivity extends Activity {
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getWindow().setFormat(PixelFormat.RGBA_8888);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);

        setContentView(R.layout.splash);

        //Display the current version number

        new Handler().postDelayed(new Runnable() {
            public void run() {
                /* Create an Intent that will start the Main WordPress Activity. */
                Intent intent1 = new Intent();
                intent1.setClass(SplashActivity.this,com.risid.urp.LoginActivity.class);
                startActivity(intent1);
                SplashActivity.this.finish();
            }
        }, 2900); //2900 for release

    }

}
