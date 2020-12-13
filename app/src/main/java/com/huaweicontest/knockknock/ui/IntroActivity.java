package com.huaweicontest.knockknock.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;

import com.huaweicontest.knockknock.R;
import com.huaweicontest.knockknock.ui.fragments.IntroPagerSlide;

public class IntroActivity extends AppCompatActivity {

    private static final int INTRO_PAGE_COUNT = 4;
    ViewPager2 introPager;
    IntroSlideAdapter introPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        introPager = findViewById(R.id.intro_pager);
        introPagerAdapter = new IntroSlideAdapter(this);
        introPager.setAdapter(introPagerAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //hide status bar
        int uiOption = View.SYSTEM_UI_FLAG_FULLSCREEN;
        getWindow().getDecorView().setSystemUiVisibility(uiOption);
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