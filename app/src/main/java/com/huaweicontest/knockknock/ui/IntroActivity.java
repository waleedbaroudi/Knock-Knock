package com.huaweicontest.knockknock.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.huaweicontest.knockknock.R;
import com.huaweicontest.knockknock.ui.fragments.IntroPagerSlide;

public class IntroActivity extends AppCompatActivity {

    private static final int INTRO_PAGE_COUNT = 4;

    ViewPager2 introPager;
    IntroSlideAdapter introPagerAdapter;
    TabLayout introIndicator;

    Button skipButton, nextButton;

    SharedPreferences sharedPrefs;
    public static final String APP_SHARED_PREFS = "M_SHARED_PREFS";
    private static final String FIRST_LAUNCH_BOOL = "FIRST_LAUNCH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        sharedPrefs = getSharedPreferences(APP_SHARED_PREFS, MODE_PRIVATE);
        if (!sharedPrefs.getBoolean(FIRST_LAUNCH_BOOL, true))
            //skip intro if it's not the first launch of the app
            launchMain();
        setupButtons();
        setupIntroPager();
    }

    private void setupButtons() {
        skipButton = findViewById(R.id.skip_intro_button);
        skipButton.setOnClickListener(v -> launchMain());

        nextButton = findViewById(R.id.next_slide_button);
        nextButton.setOnClickListener(v -> {
            int current = introIndicator.getSelectedTabPosition();
            if (current < INTRO_PAGE_COUNT - 1)
                introPager.setCurrentItem(current + 1, true);
        });
    }

    private void launchMain() {
        sharedPrefs.edit().putBoolean(FIRST_LAUNCH_BOOL, false).apply();
        startActivity(new Intent(IntroActivity.this, MainActivity.class));
        finish();
    }

    private void setupIntroPager() {
        introPager = findViewById(R.id.intro_pager);
        introIndicator = findViewById(R.id.intro_indicator);
        introPagerAdapter = new IntroSlideAdapter(this);
        introPager.setAdapter(introPagerAdapter);
        TabLayoutMediator mediator = new TabLayoutMediator(introIndicator, introPager, (tab, position) -> {
        });
        mediator.attach();

        introPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (position == INTRO_PAGE_COUNT - 1) {
                    nextButton.setText("Got it!");
                    nextButton.setOnClickListener(v -> launchMain());
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //hide status bar
        int uiOption = View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOption);
    }

    @Override
    public void onBackPressed() {
        int current = introIndicator.getSelectedTabPosition();
        if (current == 0) //if at first slide, let the system deal with the back press
            super.onBackPressed();
        else //otherwise go to the previous intro slide
            introPager.setCurrentItem(current - 1, true);
    }

    private class IntroSlideAdapter extends FragmentStateAdapter {


        public IntroSlideAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return new IntroPagerSlide(position);
        }

        @Override
        public int getItemCount() {
            return INTRO_PAGE_COUNT;
        }
    }
}