package com.huaweicontest.knockknock.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.huaweicontest.knockknock.R;
import com.huaweicontest.knockknock.model.Constant;

public class SplashActivity extends AppCompatActivity {
    ImageView logo;
    TextView slogan;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        sharedPreferences = getSharedPreferences(Constant.APP_SHARED_PREFS, MODE_PRIVATE);
        logo = findViewById(R.id.logo);
        slogan = findViewById(R.id.splash_slogan);
        beginAnimations();

        boolean firstLaunch = sharedPreferences.getBoolean(Constant.FIRST_LAUNCH_BOOL, true);

        new Thread(() -> {
            try {
                Thread.sleep(Constant.SPLASH_DURATION);
                if (firstLaunch)
                    startActivity(new Intent(SplashActivity.this, IntroActivity.class));
                else
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void beginAnimations() {
        logo.animate()
                .translationY(Constant.SPLASH_LOGO_SLIDE_DISTANCE)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(Constant.SPLASH_ANIM_DURATION);

        slogan.animate()
                .translationY(Constant.SPLASH_SLOGAN_SLIDE_DISTANCE)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(Constant.SPLASH_ANIM_DURATION);
    }
}