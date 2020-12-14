package com.huaweicontest.knockknock.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.huaweicontest.knockknock.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class IntroPagerSlide extends Fragment {
    int position;

    TextView introText, introHeading;
    CircleImageView introFigure;

    String[] headings;
    String[] texts;
    int[] imageResources;

    public IntroPagerSlide(int pagerPosition) {
        position = pagerPosition;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup slide = (ViewGroup) inflater.inflate(R.layout.fragment_intro_pager_slide, container, false);
        introHeading = slide.findViewById(R.id.intro_heading);
        introText = slide.findViewById(R.id.intro_text);
        introFigure = slide.findViewById(R.id.intro_figure);
        setViewContents();
        return slide;
    }

    /**
     * gets the contents of the current slide depending on the position and displays them
     */
    private void setViewContents() {
        headings = getResources().getStringArray(R.array.intro_headings);
        texts = getResources().getStringArray(R.array.intro_texts);
        imageResources = new int[]{R.drawable.touch, R.drawable.secure, R.drawable.global, R.drawable.compatible};
        introHeading.setText(headings[position]);
        introText.setText(texts[position]);
        introFigure.setImageResource(imageResources[position]);
    }
}